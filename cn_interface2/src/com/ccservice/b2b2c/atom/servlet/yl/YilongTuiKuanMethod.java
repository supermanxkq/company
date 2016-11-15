package com.ccservice.b2b2c.atom.servlet.yl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengTrainReturnTicket;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public class YilongTuiKuanMethod {
    /**
     * 代理商code  merchantCode    必填  由艺龙分配   
    订单号 orderId 必填  订单号 
    订单项号    orderItemId 必填  订单项号    
    退款流水号   tradeNo 必填  由供应商提供的唯一的流水编号。不允许重复    退款流水号
    退款金额    amount  必填  退款金额    大于0
    退款说明    comment 必填  备注  
    签名  sign    必填  数据签名    参照签名机制

     * */
    Map<String, InterfaceAccount> interfaceAccountMap;

    public JSONObject tuikuan(String orderId, String amount, String comment, String orderItemId, String merchantCode,
            String tradeNo, String sign) {
        String result = "";
        int r1 = new Random().nextInt(10000000);
        JSONObject resultJson = new JSONObject();
        InterfaceAccount interfaceAccount = getInterfaceAccount(merchantCode);
        String key = interfaceAccount.getKeystr();

        String localSign = "comment=" + comment + "&amount=" + amount + "&orderId=" + orderId + "&orderItemId="
                + orderItemId + "&merchantCode=" + merchantCode + "&tradeNo=" + tradeNo;
        localSign = getSignMethod(localSign) + key;
        WriteLog.write("Elong_艺龙退款_ElongRefundTicketRequestServlet", "key" + key + ":localSign:" + localSign);

        try {

            localSign = ElongHotelInterfaceUtil.MD5(localSign).toUpperCase();
        }
        catch (Exception e) {

            e.printStackTrace();
        }
        if (sign.equals(localSign)) {
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
                json.put("partnerid", merchantCode);
                json.put("reqtoken", orderItemId);

                String results = new TongChengTrainReturnTicket().returnticket(json, r1);
            }
            resultJson.put("retdesc", "第三方退款成功");
        }
        else {
            resultJson.put("retcode", "403");
            resultJson.put("retdesc", "签名校验失败");
            result = resultJson.toJSONString();
        }
        return resultJson;
    }

    /**
     * 
     * 
     * @param json
     * @return
     * @time 2015年12月10日 上午11:47:17
     * @author Mr.Wang
     */
    public String getSignMethod(String sign) {
        if (!ElongHotelInterfaceUtil.StringIsNull(sign)) {
            String[] signParam = sign.split("&");
            sign = ElongHotelInterfaceUtil.sort(signParam);
            return sign;
        }
        return "";
    }

    /**
     * 
     * 
     * @param merchantId
     * @return
     * @time 2015年12月24日 下午8:23:55
     * @author yangtao
     */
    public InterfaceAccount getInterfaceAccount(String merchantCode) {
        if (interfaceAccountMap == null) {
            interfaceAccountMap = new HashMap<String, InterfaceAccount>();
        }
        //-----加缓存机制不用每次都去数据库查-----S
        //chendong 2015年4月11日19:18:11
        InterfaceAccount interfaceAccount = interfaceAccountMap.get(merchantCode);
        if (interfaceAccount == null) {
            interfaceAccount = getInterfaceAccountByLoginname(merchantCode);
            if (interfaceAccount != null && interfaceAccount.getKeystr() != null
                    && interfaceAccount.getInterfacetype() != null) {
                interfaceAccountMap.put(merchantCode, interfaceAccount);
            }
        }

        return interfaceAccount;
    }

    /**
     * 根据用户名获取到这个用户的key
     * 
     * @param loginname
     * @return
     * @time 2015年1月14日 下午7:01:07
     * @author fiend
     */
    @SuppressWarnings("unchecked")
    public InterfaceAccount getInterfaceAccountByLoginname(String loginname) {
        WriteLog.write("艺龙_ElongPayMessageServlet_payMsgDisposeMethod", "loginname:" + loginname);
        List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
        InterfaceAccount interfaceAccount = new InterfaceAccount();
        try {
            list_interfaceAccount = Server.getInstance().getMemberService()
                    .findAllInterfaceAccount("where C_USERNAME = '" + loginname + "'", null, -1, 0);
        }
        catch (Exception e) {
        }
        WriteLog.write("艺龙tuiKuan通知_ElongPayMessageServlet_payMsgDisposeMethod", "list_interfaceAccount:"
                + list_interfaceAccount.size());
        if (list_interfaceAccount.size() > 0) {
            interfaceAccount = list_interfaceAccount.get(0);
        }
        return interfaceAccount;
    }

    /* *//**
             * 退款
             */
    /*
    @SuppressWarnings("unused")
    private static void tuikuan(Long trainorderid, Long ticketid, Long agentid) {
     Customeruser customeruser = getcustomeruserbyagentid(agentid);
     Server.getInstance().getTrainService().ticketRefund(trainorderid, ticketid, customeruser, "");
    }

    *//**
      * 根据agentid找到Customeruser
      */
    /*
    @SuppressWarnings("rawtypes")
    public static Customeruser getcustomeruserbyagentid(Long agentid) {
     List list = Server.getInstance().getMemberService()
             .findAllCustomeruser("where C_AGENTID=" + agentid + " and C_ISADMIN=1", "order by id", -1, 0);
     Customeruser customeruser = new Customeruser();
     if (list.size() > 0) {
         customeruser = (Customeruser) list.get(0);
     }
     return customeruser;
    }*/

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
                    //  ticket.put("remark", "2");
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
