package com.ccservice.b2b2c.atom.servlet.yl;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainReturnTicket;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainSelectLoginWay;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

/**
 * 艺龙退票请求接口
 * 
 * @time 2015年12月29日 下午5:31:27
 * @author W.C.L
 */
public class ElongRefundTicketRequestDisposeMethod extends TrainSelectLoginWay {

    /**
     * 艺龙退票
     * 
     * @param merchantId 分配给艺龙的id
     * @param timeStamp 发送请求的时间戳
     * @param orderId 下单订单号
     * @param orderItemId 票号（票唯一id）
     * @param Sign 签名
     * @param paramJson 退票json串
     * @param r1 
     * @return  返回响应码
     * @time 2015年12月30日 下午2:34:09
     * @author W.C.l
     */
    public JSONObject refundTicketRequsetDispose(String merchantId, String timeStamp, String orderId,
            String orderItemId, String Sign, String paramJson, int r1) {

        JSONObject resultJson = new JSONObject();
        InterfaceAccount interfaceAccount = getInterfaceAccount(merchantId);
        String key = interfaceAccount.getKeystr();

        String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId
                + "&orderItemId=" + orderItemId + "&paramJson=" + paramJson;
        localSign = getSignMethod(localSign) + key;
        WriteLog.write("Elong_艺龙退票请求_ElongRefundTicketRequestServlet", r1 + ":localSign:" + localSign);

        try {
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
            if (Sign.equals(localSign)) {

                String sql = "SELECT ID,C_ORDERNUMBER,C_EXTNUMBER FROM T_TRAINORDER WITH(NOLOCK) WHERE C_QUNARORDERNUMBER='"
                        + orderId + "'";
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    String trainorderid = map.get("ID").toString();
                    String transactionid = map.get("C_ORDERNUMBER").toString();
                    String ordernumber = map.get("C_EXTNUMBER").toString();

                    JSONObject json = refundTicketResquestData(paramJson, trainorderid, orderItemId, merchantId, key,
                            orderId);
                    if (json.containsValue("432")) {
                        resultJson = json;
                    }
                    else {
                        json.put("orderid", orderId);// 订单号
                        json.put("transactionid", transactionid);// 供应商交易单号
                        json.put("ordernumber", ordernumber);// 12306订单号
                        json.put("partnerid", merchantId);
                        json.put("reqtoken", orderItemId);
                        WriteLog.write("Elong_艺龙退票请求_ElongRefundTicketRequestServlet", r1 + ":json:" + json.toString());
                        String result = new TongChengTrainReturnTicket().returnticket(json, r1);
                        if (result.indexOf("退票请求已接收") > -1) {
                            resultJson.put("retcode", "0");
                            resultJson.put("retdesc", "成功");
                        }
                        else if (result.indexOf("该订单状态下，不能退票") > -1 || result.indexOf("112") > -1) {
                            resultJson.put("retcode", "434");
                            resultJson.put("retdesc", "退票订单号order不是出票成功或者出票成功待差价");
                        }
                        else {
                            resultJson = JSONObject.parseObject(result);
                        }
                    }
                }
                else {
                    resultJson.put("retcode", "452");
                    resultJson.put("retdesc", "此订单不存在");
                }
            }
            else {
                resultJson.put("retcode", "-1");
                resultJson.put("retdesc", "Md5校验失败");
            }
        }
        catch (Exception e) {
        }

        return resultJson;
    }

    /**
     * 转换同程请求退票参数
     * 
     * @param paramJson
     * @param trainorderid 订单ID
     * @param orderItemId  票号（票唯一id）
     * @return
     * @time 2015年12月29日 下午5:25:29
     * @author W.C.L
     */
    private JSONObject refundTicketResquestData(String paramJson, String trainorderid, String orderItemId,
            String merchantCode, String key, String orderid) {
        JSONObject json = new JSONObject();
        JSONArray tickets = new JSONArray();
        JSONObject ticket = new JSONObject();
        JSONObject jsonParam = JSONObject.parseObject(paramJson);
        String payCallbackUrl_temp = "http://trainapi.elong.com/open_api/process_return_result";
        boolean isExist = false;
        Boolean result = true;
        int status = -1;
        String msg = "线下退";
        String amount = "0";
        String returnmsg = "";
        String returntype = "3";
        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(Integer.parseInt(trainorderid));
        List<Trainpassenger> trainpassengers = trainorder.getPassengers();
        for (int i = 0; i < trainpassengers.size(); i++) {
            Trainpassenger trainpassenger = trainpassengers.get(i);
            //匹配要退的orderItemId
            if (trainpassenger.getPassengerid().equals(orderItemId)) {
                ticket.put("passengername", trainpassenger.getName());// 乘客姓名
                ticket.put("passporttypeseid", trainpassenger.getIdtype());// 证件类型
                ticket.put("passportseno", trainpassenger.getIdnumber());// 证件号
                List<Trainticket> traintickets = trainpassenger.getTraintickets();
                for (int j = 0; j < traintickets.size(); j++) {
                    Trainticket trainticket = traintickets.get(0);
                    status = trainticket.getStatus();
                    WriteLog.write("艺龙退票回调通知吉吉_yilong", orderid + ":merchantCode:" + merchantCode + ":orderId:"
                            + orderid + ":orderItemId:" + orderItemId + ":result:" + result + ":payCallbackUrl_temp:"
                            + payCallbackUrl_temp + ":key:" + key + ":amount:" + amount + ":returnmsg:" + returnmsg
                            + ":returntype:" + returntype + ":ticket_no:" + msg + status);
                    if (status == 10) {
                        WriteLog.write("艺龙退票回调通知吉吉_yilong", "进没进来");
                        YiLongCallBackMethod.refunCallBackYl(merchantCode, orderid, orderItemId, result,
                                payCallbackUrl_temp, key, amount, returnmsg, returntype, msg);

                    }
                    else if (ElongHotelInterfaceUtil.StringIsNull(trainticket.getTcticketno())) {
                        ticket.put("ticket_no", trainticket.getTicketno());// 车票号
                    }
                    else {
                        ticket.put("ticket_no", trainticket.getTcticketno());// 改签后车票号
                    }

                }
                isExist = true;
            }
        }
        if (isExist) {//匹配到orderItemId
            tickets.add(ticket);
            json.put("tickets", tickets);
        }
        else {
            json.put("retcode", "432");
            json.put("retdesc", "退票订单号orderitemid不存在");
        }
        return json;
    }
}
