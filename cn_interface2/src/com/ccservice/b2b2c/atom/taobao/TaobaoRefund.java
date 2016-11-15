package com.ccservice.b2b2c.atom.taobao;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

public class TaobaoRefund {

    /**
     * Cause: java.sql.SQLException: 违反了 UNIQUE KEY 约束 'TrainOrderRefund_RefundOrderNo'。
     * 不能在对象 'dbo.TrainOrderRefund' 中插入重复键。, next=null, stackTrace=[Ljava.lang.StackTraceElement;@2282cbd}) cannot be assigned to java.lang.Throwable
     */

    @SuppressWarnings("rawtypes")
    public void operate(JSONObject jsonObject) {
        try {
            TaobaoRefundMethod.getInstance();
            // 主站id
            String main_biz_order_id = jsonObject.containsKey("main_biz_order_id") ? jsonObject
                    .getString("main_biz_order_id") : "";
            //买家id 
            String user_id = jsonObject.containsKey("user_id") ? jsonObject.getString("user_id") : "";
            //退款单号
            String sub_biz_order_id = jsonObject.containsKey("sub_biz_order_id") ? jsonObject
                    .getString("sub_biz_order_id") : "";
            //ttp_id
            String extra = jsonObject.containsKey("extra") ? jsonObject.getString("extra") : "";
            //卖家ID
            String SellerId = jsonObject.containsKey("SellerId") ? jsonObject.getString("SellerId") : "";
            //描述内容填写的是：autorefund-支付宝流水号-退款金额-merchant_out_order_no 
            //autorefund-2015082921001004820033108685-6900-W2015082974636639 
            String description = jsonObject.containsKey("description") ? jsonObject.getString("description") : "";
            if (ElongHotelInterfaceUtil.StringIsNull(main_biz_order_id)
                    || ElongHotelInterfaceUtil.StringIsNull(user_id)
                    || ElongHotelInterfaceUtil.StringIsNull(sub_biz_order_id)
                    || ElongHotelInterfaceUtil.StringIsNull(extra) || ElongHotelInterfaceUtil.StringIsNull(SellerId)
                    || ElongHotelInterfaceUtil.StringIsNull(description)) {
                WriteLog.write("105_TAOBAO_RefundPrice_false", "基础数据为空" + main_biz_order_id + ":" + user_id + ":"
                        + sub_biz_order_id + ":" + extra + ":" + SellerId + ":" + description);
                return;

            }
            String alipaytradenoString = "";
            int taoBaoRefundPrice = 0;
            try {
                //description>>autorefund-2015110921001004950044010432-2350-  W2015110971089317
                alipaytradenoString = description.split("-")[1];
                String taobaorefundpriceString = description.split("-")[2];
                taoBaoRefundPrice = Integer.valueOf(taobaorefundpriceString);
            }
            catch (Exception e1) {
                WriteLog.write("ERROR_TaobaoRefund", main_biz_order_id);
                ExceptionUtil.writelogByException("ERROR_TaobaoRefund", e1);
            }
            if (ElongHotelInterfaceUtil.StringIsNull(alipaytradenoString) || taoBaoRefundPrice <= 0) {
                WriteLog.write("105_TAOBAO_RefundPrice_false", "淘宝交易号&金额有误:" + description);
                new TaobaoHotelInterfaceUtil().refundfee(main_biz_order_id, extra, 1, "", SellerId,
                        alipaytradenoString, user_id, "", sub_biz_order_id, 0, 1);
                return;
            }
            //获取订单号
            String orderid = orderIDByInterfaceNumber(main_biz_order_id);
            if (ElongHotelInterfaceUtil.StringIsNull(orderid)) {
                WriteLog.write("105_TAOBAO_RefundPrice_false", "对应订单不存在:" + main_biz_order_id);
                new TaobaoHotelInterfaceUtil().refundfee(main_biz_order_id, extra, 1, "", SellerId,
                        alipaytradenoString, user_id, "", sub_biz_order_id, 0, 1);
                return;
            }
            String sql = "  [sp_TrainOrderRefund_Insert] @TaoBaoOrderNo='" + main_biz_order_id + "',@OrderId="
                    + orderid + "    ,@UserId= '" + user_id + "'  ,@SellerId='" + SellerId + "' ,@RefundOrderNo= '"
                    + sub_biz_order_id + "'   ,@TTPId= '" + extra + "'   ,@AlipayTradeNo='" + alipaytradenoString
                    + "'    ,@TaoBaoRefundPrice= " + taoBaoRefundPrice;
            WriteLog.write("105_TAOBAO_RefundPrice", "退款信息入库--->" + sql);
            try {
                List list = Server.getInstance().getSystemService().findMapResultByProcedure(sql);
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    WriteLog.write("105_TAOBAO_RefundPrice", "退款信息BD中PKId--->" + map.get("PKId").toString());
                    String taoBaoPushRefundPrice_Url = PropertyUtil.getValue("taoBaoPushRefundPrice_Url",
                            "Train.properties");
                    JSONObject jObject = new JSONObject();
                    jObject.put("orderId", orderid);
                    jObject.put("taobao_refundnumber", sub_biz_order_id);
                    jObject.put("taobao_alipaytraindeno", alipaytradenoString);
                    jObject.put("taobao_refundprice", taoBaoRefundPrice);
                    jObject.put("taobao_ordernumber", main_biz_order_id);
                    jObject.put("taobao_user_id", user_id);
                    jObject.put("taobao_SellerId", SellerId);
                    jObject.put("taobao_ttp_id", extra);
                    jObject.put("PKId", map.get("PKId").toString());
                    WriteLog.write("105_TAOBAO_RefundPrice", "退款信息推送内部退款接口--->" + taoBaoPushRefundPrice_Url + "===>"
                            + jObject.toString());
                    try {
                        String result = SendPostandGet.submitPost(taoBaoPushRefundPrice_Url, jObject.toString(),
                                "utf-8").toString();
                        WriteLog.write("105_TAOBAO_RefundPrice", "退款信息推送内部退款接口--->第一次--->" + main_biz_order_id + result);
                        if (!"success".equalsIgnoreCase(result)) {
                            for (int i = 0; i < 10; i++) {
                                result = SendPostandGet.submitPost(taoBaoPushRefundPrice_Url, jObject.toString(),
                                        "utf-8").toString();
                                WriteLog.write("105_TAOBAO_RefundPrice", "退款信息推送内部退款接口--->第一次---" + main_biz_order_id
                                        + result);
                                if ("success".equalsIgnoreCase(result)) {
                                    break;
                                }
                                else {
                                    try {
                                        Thread.sleep(1000L);
                                    }
                                    catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                        WriteLog.write("105_TAOBAO_RefundPrice", "退款信息推送内部退款接口--->" + main_biz_order_id + result);
                    }
                    catch (Exception e) {
                        WriteLog.write("ERROR_105_TAOBAO_RefundPrice", "异常--->" + taoBaoPushRefundPrice_Url + "===>"
                                + jObject.toString());
                        ExceptionUtil.writelogByException("ERROR_105_TAOBAO_RefundPrice", e);
                        new TaobaoHotelInterfaceUtil().refundfee(main_biz_order_id, extra, 1, "", SellerId,
                                alipaytradenoString, user_id, "", sub_biz_order_id, 0, 1);
                        return;
                    }
                }
                else {
                    WriteLog.write("105_TAOBAO_RefundPrice", sql + "--->退款信息入库--->入库失败");
                    new TaobaoHotelInterfaceUtil().refundfee(main_biz_order_id, extra, 1, "", SellerId,
                            alipaytradenoString, user_id, "", sub_biz_order_id, 0, 1);
                    return;
                }
            }
            catch (Exception e) {
                WriteLog.write("ERROR_105_TAOBAO_RefundPrice", jsonObject.toString());
                ExceptionUtil.writelogByException("ERROR_105_TAOBAO_RefundPrice", e);
                new TaobaoHotelInterfaceUtil().refundfee(main_biz_order_id, extra, 1, "", SellerId,
                        alipaytradenoString, user_id, "", sub_biz_order_id, 0, 1);
                return;
            }
        }
        catch (Exception e) {
            WriteLog.write("ERROR_105_TAOBAO_RefundPrice", jsonObject.toString());
            ExceptionUtil.writelogByException("ERROR_105_TAOBAO_RefundPrice", e);
        }
    }

    /**
     * 通过接口订单号 获取订单号
     * @param interfaceNumber
     * @return
     */
    @SuppressWarnings("rawtypes")
    private String orderIDByInterfaceNumber(String interfaceNumber) {
        WriteLog.write("TaobaoRefund_orderNumberByInterfaceNumber", "接口订单号--->" + interfaceNumber);
        String ordernumber = "";
        String ordernumbersql = "SELECT TOP 1 ID FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER='"
                + interfaceNumber + "'";
        try {
            List orderlist = Server.getInstance().getSystemService().findMapResultBySql(ordernumbersql, null);
            if (orderlist.size() > 0) {
                Map map1 = (Map) orderlist.get(0);
                ordernumber = map1.get("ID").toString();
            }
        }
        catch (Exception e) {
            WriteLog.write("ERROR_TaobaoRefund_orderNumberByInterfaceNumber", "接口订单号--->" + interfaceNumber);
            ExceptionUtil.writelogByException("ERROR_TaobaoRefund_orderNumberByInterfaceNumber", e);
        }
        WriteLog.write("TaobaoRefund_orderNumberByInterfaceNumber", "接口订单号--->" + interfaceNumber + "--->订单号--->"
                + ordernumber);
        return ordernumber;
    }
}
