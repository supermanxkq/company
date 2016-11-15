package com.ccservice.b2b2c.atom.taobao;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.FuzzyRefund;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class TaobaoReceiveRefundPriceInfo {

    /**
     * 
    * @Title: operate
    * @Description: TODO   接收tmc回传信息处理类
    * @param @param json    
    * @return void   
    * @author RRRRRR
    * @throws
    *   数据样式              {
                    description: "2016101821001004690298216961-12500-W2016101883812920", 
                    extra: "199046211001", 
                    main_biz_order_id: 2256317380620140, 
                    msg_type: "7", 
                    sub_biz_order_id: 8250, 
                    time_stamp: "Oct 20, 2016 10:00:15 AM", 
                    user_id: 2374174001
                  }
     */
    public void operate(JSONObject json) {
        Integer r1 = new Random().nextInt(10000000);
        try {
            String description = json.containsKey("description") ? json.getString("description") : "";
            String qunarordernumber = json.containsKey("main_biz_order_id") ? json.getString("main_biz_order_id") : "";
            String ticketinterfaceno = json.containsKey("sub_biz_order_id") ? json.getString("sub_biz_order_id") : "";//锁定当前车票
            if (ElongHotelInterfaceUtil.StringIsNull(description)
                    || ElongHotelInterfaceUtil.StringIsNull(qunarordernumber)
                    || ElongHotelInterfaceUtil.StringIsNull(ticketinterfaceno)) {
                WriteLog.write("107_TAOBAO_ReceiveRefundPriceInfo", r1+" 字段信息不全-->description" + description
                        + "  qunarordernumber: " + qunarordernumber + "ticketinterfaceno :" + ticketinterfaceno);
                return;
            }
            else {
                float taobaorefundprice = 0;
                String des[] = description.split("-");
                String alipayno = des[0];//流水号
                String pricestring = des[1];//退款金额
                try {
                    taobaorefundprice = (Float.valueOf(pricestring) / 100);
                }
                catch (Exception e) {
                    WriteLog.write("107_TAOBAO_ReceiveRefundPriceInfo",r1+" 金额转换出现异常 " + pricestring + " 前后对比 "
                            + taobaorefundprice);
                    e.printStackTrace();
                }
                String orderid = orderIDByInterfaceNumber(qunarordernumber,r1);
                Trainorder trainorder = null;
                if (ElongHotelInterfaceUtil.StringIsNull(orderid)) {
                    WriteLog.write("107_TAOBAO_ReceiveRefundPriceInfo", r1+" 查询出错 没能正确取出订单id  orderid：" + orderid);
                }
                else {
                    trainorder = Server.getInstance().getTrainService().findTrainorder(Long.valueOf(orderid));
                    Trainorderchange change = new Trainorderchange();
                    Long changeId = 0l;
                    for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
                        for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                            if (!ElongHotelInterfaceUtil.StringIsNull(trainticket.getInterfaceticketno())
                                    && ticketinterfaceno.equals(trainticket.getInterfaceticketno())) {
                                changeId = trainticket.getChangeid();
                                FuzzyRefund frf = null;
                                if (changeId > 0) {//可能改签
                                    change = Server.getInstance().getTrainService().findTrainOrderChangeById(changeId);
                                    if (alipayno.equals(change.getSupplytradeno())) {
                                        frf = saveFuzzy(trainorder, change, change.getSupplyprice(), taobaorefundprice,
                                                false);
                                    }
                                    else if (alipayno.equals(trainorder.getSupplytradeno())) {
                                        frf = saveFuzzy(trainorder, null, trainorder.getOrderprice(),
                                                taobaorefundprice, false);
                                    }
                                    else {
                                        WriteLog.write("107_TAOBAO_ReceiveRefundPriceInfo",
                                                r1+" 流水号 未在 改签表  和 订单表  ,流水号 alipayno :" + alipayno);
                                        return;
                                    }
                                }
                                else {//该票一定没改签
                                    if (alipayno.equals(trainorder.getSupplytradeno())) {
                                        frf = saveFuzzy(trainorder, null, trainorder.getOrderprice(),
                                                taobaorefundprice, false);
                                    }
                                    else {
                                        WriteLog.write("107_TAOBAO_ReceiveRefundPriceInfo",
                                                r1+" 流水号 未在 改签表  和 订单表  ,流水号 alipayno :" + alipayno);
                                        return;
                                    }
                                }
                                if (frf != null && frf.getId() > 0) {
                                    WriteLog.write("107_TAOBAO_ReceiveRefundPriceInfo", r1+" 模糊退记录插入成功  FuzzyRefund . id :"
                                            + frf.getId());
                                }
                                else {
                                    WriteLog.write("107_TAOBAO_ReceiveRefundPriceInfo", r1+" 模糊退记录插入  异常 :"
                                            + ticketinterfaceno);
                                }
                            }
                            else {
                                WriteLog.write("107_TAOBAO_ReceiveRefundPriceInfo", r1+" 匹配票出现错误未找到  ticketinterfaceno :"
                                        + ticketinterfaceno);
                                return;
                            }
                        }
                    }

                }
            }
        }
        catch (Exception e) {
            WriteLog.write("107_TAOBAO_ReceiveRefundPriceInfo", r1+" 处理信息异常" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 通过接口订单号 获取订单号
     * @param interfaceNumber
     * @return
     */
    @SuppressWarnings("rawtypes")
    private static String orderIDByInterfaceNumber(String interfaceNumber,Integer r1) {
        WriteLog.write("107TaobaoRefund_orderNumberByInterfaceNumber", r1+"接口订单号--->" + interfaceNumber);
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
            WriteLog.write("107ERROR_TaobaoRefund_orderNumberByInterfaceNumber", r1+"接口订单号--->" + interfaceNumber);
            ExceptionUtil.writelogByException("ERROR_TaobaoRefund_orderNumberByInterfaceNumber", e);
        }
        WriteLog.write("107TaobaoRefund_orderNumberByInterfaceNumber", r1+"接口订单号--->" + interfaceNumber + "--->订单号--->"
                + ordernumber);
        return ordernumber;
    }

    /**
     * 新增模糊退记录
     */
    private FuzzyRefund saveFuzzy(Trainorder order, Trainorderchange change, float totalMoney,
            float taobao_refundprice, boolean highChange) {
        //创建记录
        FuzzyRefund fuzzy = new FuzzyRefund();
        //记录赋值
        fuzzy.setRemark("淘宝");
        fuzzy.setTimeStamp("");
        fuzzy.setOrderId(order.getId());
        fuzzy.setTotalMoney(totalMoney);
        fuzzy.setMoney(taobao_refundprice);
        fuzzy.setStatus(FuzzyRefund.REFUNDED);
        fuzzy.setOrderNumber(order.getOrdernumber());
        fuzzy.setOrderPayMethod(order.getPaymethod());
        fuzzy.setOrderTradeNum(order.getSupplytradeno());
        fuzzy.setOrderInterfaceType(order.getInterfacetype());
        fuzzy.setRefundPriceQuestion(FuzzyRefund.REFUNDNORMAL);
        if (change == null) {
            fuzzy.setChangeId(0);
            //改签交易号
            fuzzy.setChangeTradeNum("");
            //改签特征值
            fuzzy.setChangeRequestReqtoken("");
        }
        else {
            fuzzy.setChangeId(change.getId());
            //改签交易号
            fuzzy.setChangeTradeNum(ElongHotelInterfaceUtil.StringIsNull(change.getSupplytradeno()) ? "" : change
                    .getSupplytradeno());
            //改签特征值
            fuzzy.setChangeRequestReqtoken(!ElongHotelInterfaceUtil.StringIsNull(change.getRequestReqtoken()) ? change
                    .getRequestReqtoken() : "");
        }
        //保存记录
        return Server.getInstance().getTrainService().createFuzzyRefund(fuzzy);
    }
}
