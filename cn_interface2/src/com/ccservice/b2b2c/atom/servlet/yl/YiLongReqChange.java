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

public class YiLongReqChange extends TrainSelectLoginWay {
    /**
     * merchantId   供应商id   是   string  是   分配给艺龙的id
    timeStamp   推送时间戳   是   string  是   发送请求的时间戳
    orderId 订单号 是   string  是   下单订单号
    orderItemId 票号  是   String  是   票号（票唯一id）

    type    操作类型    是   String  是   1：线下退票
    2：线下改签
    note    操作类型    否   String  是   线下退票或线下改签
    sign    签名  是   string  否   签名
    yangtao
     * 
     * */
    public String returnticketYilong(String merchantId, String timeStamp, String orderId, String orderItemId,
            String Sign, String note, String type, int r1) {
        String result = "";
        JSONObject resultJson = new JSONObject();
        InterfaceAccount interfaceAccount = getInterfaceAccount(merchantId);
        String key = interfaceAccount.getKeystr();
        String localSign = "merchantId=" + merchantId + "&timeStamp=" + timeStamp + "&orderId=" + orderId
                + "&orderItemId=" + orderItemId + "&note=" + note + "&type=" + type;
        localSign = getSignMethod(localSign) + key;
        WriteLog.write("Elong_艺龙线下退票请求_排序", r1 + ":localSign:" + localSign + "|key" + key);
        try {
            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
            WriteLog.write("Elong_艺龙线下退票请求_排序", r1 + ":localSign:" + localSign + "|key" + key);
            if (Sign.equals(localSign)) {

                String sql = "SELECT ID,C_ORDERNUMBER,C_EXTNUMBER FROM T_TRAINORDER WITH(NOLOCK) WHERE C_QUNARORDERNUMBER='"
                        + orderId + "'";
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    String trainorderid = map.get("ID").toString();
                    String transactionid = map.get("C_ORDERNUMBER").toString();
                    String ordernumber = map.get("C_EXTNUMBER").toString();

                    JSONObject json = refundTicketResquestData(trainorderid, orderItemId);
                    json.put("orderid", orderId);// 订单号
                    json.put("transactionid", transactionid);// 供应商交易单号
                    json.put("ordernumber", ordernumber);// 12306订单号
                    json.put("partnerid", merchantId);
                    json.put("reqtoken", orderItemId);

                    WriteLog.write("Elong_艺龙线下退票请求_ElongRefundTicketRequestServlet", r1 + ":json:" + json.toString());
                    String results = new TongChengTrainReturnTicket().returnticket(json, r1);
                    if (results.indexOf("退票请求已接收") > -1) {
                        resultJson.put("retcode", "0");
                        resultJson.put("retdesc", "成功");
                        result = resultJson.toJSONString();
                    }
                    else {
                        resultJson = JSONObject.parseObject(results);
                        result = resultJson.toJSONString();
                    }
                }
                else {
                    resultJson.put("retcode", "452");
                    resultJson.put("retdesc", "此订单不存在");
                    result = resultJson.toJSONString();
                }
            }
            else {
                resultJson.put("retcode", "-1");
                resultJson.put("retdesc", "Md5校验失败");
                result = resultJson.toJSONString();
            }
        }
        catch (Exception e) {
        }

        return result;
    }

    /**
     * 转换同程请求退票参数
     * 
     * @param paramJson
     * @param trainorderid
     * @param orderItemId
     * @return
     * @time 2015年12月29日 下午5:25:29
     * @author W.C.L
     */
    private JSONObject refundTicketResquestData(String trainorderid, String orderItemId) {
        JSONObject json = new JSONObject();
        JSONArray tickets = new JSONArray();
        JSONObject ticket = new JSONObject();
        Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(Integer.parseInt(trainorderid));

        List<Trainpassenger> trainpassengers = trainorder.getPassengers();
        for (int i = 0; i < trainpassengers.size(); i++) {
            Trainpassenger trainpassenger = trainpassengers.get(i);
            if (trainpassenger.getPassengerid().equals(orderItemId)) {
                ticket.put("passengername", trainpassenger.getName());// 乘客姓名
                ticket.put("passporttypeseid", trainpassenger.getIdtype());// 证件类型
                ticket.put("passportseno", trainpassenger.getIdnumber());// 证件号
                List<Trainticket> traintickets = trainpassenger.getTraintickets();
                for (int j = 0; j < traintickets.size(); j++) {
                    Trainticket trainticket = traintickets.get(0);
                    ticket.put("remark", "2");
                    if (ElongHotelInterfaceUtil.StringIsNull(trainticket.getTcticketno())) {
                        ticket.put("ticket_no", trainticket.getTicketno());// 车票号
                    }
                    else {
                        ticket.put("ticket_no", trainticket.getTcticketno());// 改签后车票号
                    }
                }
            }
        }

        tickets.add(ticket);
        json.put("tickets", tickets);
        return json;
    }
}