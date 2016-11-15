package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.Util_Insurance.henghao.SinosigHengHaoMethod;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.Insure.TrainInsureMethod;
import com.ccservice.b2b2c.atom.servlet.chongdong.WormholeTradeCallBack;
import com.ccservice.b2b2c.atom.servlet.chongdong.WormholeUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

public class TongChengTrainInsureCancel extends TrainInsureMethod {

    private final String logName = "订单退保接口_TongChengTrainInsureCancel";

    private final String logNameException = "订单退保接口_TongChengTrainInsureCancel_Exception";

    public String insureCancel(JSONObject json) {
        int r1 = (int) (Math.random() * 100000);
        WriteLog.write(logName, r1 + "--->" + json.toString());
        JSONObject resultObject = new JSONObject();
        String orderNumber = json.containsKey("OrderNumber") ? json.getString("OrderNumber") : "";
        String interfaceOrderNumber = json.containsKey("InterfaceOrderNumber") ? json.getString("InterfaceOrderNumber")
                : "";
        JSONArray ticketArray = json.containsKey("Tickets") ? json.getJSONArray("Tickets") : new JSONArray();
        if ("".equals(orderNumber) || "".equals(interfaceOrderNumber) || ticketArray.size() == 0) {
            resultObject.put("success", false);
            resultObject.put("msg", "业务参数缺失");
        }
        else {
            Trainorder trainorder = findTrainorderByOrderNumberAndInterfaceOrderNumber(orderNumber,
                    interfaceOrderNumber);
            if (trainorder.getId() == 0) {
                resultObject.put("success", false);
                resultObject.put("msg", "无此订单");
            }
            else {
                JSONArray resultArray = new JSONArray();
                List<Trainpassenger> trainpassengers = trainorder.getPassengers();
                for (int i = 0; i < trainpassengers.size(); i++) {
                    Trainpassenger trainpassenger = trainpassengers.get(i);
                    Trainticket ticket = trainpassenger.getTraintickets().get(0);
                    for (int j = 0; j < ticketArray.size(); j++) {
                        JSONObject ticketObject = ticketArray.getJSONObject(j);
                        ticketObject.put("success", false);
                        ticketObject.put("msg", "未知原因，请联系客服");
                        if (ticketObject.getString("TicketNo").equals(ticket.getTicketno())) {
                            try {
                                String VisitorName = PropertyUtil.getValue("InsureVisitorName",
                                        "Train.insure.properties");
                                String Password = PropertyUtil.getValue("InsurePassword", "Train.insure.properties");
                                String realInsureno = getRealinsureno(ticket.getTicketno(), r1);
                                WriteLog.write(logName, r1 + "---->" + realInsureno);
                                if (realInsureno != null && !"".equals(realInsureno) && !"null".equals(realInsureno)) {
                                    JSONObject res = new SinosigHengHaoMethod(VisitorName, Password)
                                            .cancelPolicyOrder(realInsureno);
                                    int resultId = res.getIntValue("resultId");//为0时，表示成功
                                    String resultErrDesc = res.getString("resultErrDesc");
                                    WriteLog.write(logName, r1 + "---->取消投保返回res:---->" + res);
                                    createTrainorderrc(1, trainorder.getId(), "退保接口:" + ticket.getTicketno()
                                            + "<span style='color:red;'>" + resultErrDesc + "</span>", "投保接口", 1, 0);
                                    if (resultId == 0) {
                                        refund(trainorder, 20f);
                                        ticketObject.put("success", true);
                                        if (WormholeUtil.checkTrainOrderIsWormhole(interfaceOrderNumber)) {
                                            JSONArray jsonArray = new JSONArray();
                                            JSONObject jsonObject = new JSONObject();
                                            jsonObject.put("ticket_no", ticket.getTicketno());
                                            jsonArray.add(jsonObject);
                                            String resultString = new WormholeTradeCallBack().trade(
                                                    interfaceOrderNumber, 3, jsonArray);
                                            if ("SUCCESS".equals(resultString)) {
                                                ticketObject.put("refundWormholeSuccess", true);
                                                String sql_1 = "EXEC [dbo].[sp_T_TRAINTICKET_updateWormholeReturnCallBackIsSuccessByTicketno] @Ticketno='"
                                                        + ticket.getTicketno() + "',@WormholeReturnCallBack=" + 1;
                                                Server.getInstance().getSystemService().findMapResultBySql(sql_1, null);
                                            }
                                            else {
                                                ticketObject.put("refundWormholeSuccess", false);
                                                String sql_2 = "EXEC [dbo].[sp_T_TRAINTICKET_updateWormholeReturnCallBackIsSuccessByTicketno] @Ticketno='"
                                                        + ticket.getTicketno() + "',@WormholeReturnCallBack=" + 2;
                                                Server.getInstance().getSystemService().findMapResultBySql(sql_2, null);
                                            }
                                        }
                                    }
                                    ticketObject.put("msg", resultErrDesc);
                                }
                                else {
                                    ticketObject.put("msg", "无保单可退");
                                }
                            }
                            catch (Exception e) {
                                ticketObject.put("msg", "退保异常");
                                WriteLog.write(logNameException, r1 + "");
                                ExceptionUtil.writelogByException(logNameException, e);
                            }
                            break;
                        }
                        resultArray.add(ticketObject);
                    }
                }
                resultObject.put("success", true);
                resultObject.put("Tickets", resultArray);
            }
        }
        WriteLog.write(logName, r1 + "--->" + resultObject.toString());
        return resultObject.toString();
    }

    /**
     * 获取保单号
     * 
     * @param ticketid
     * @return
     * @time 2016年7月19日 下午6:51:54
     * @author fiend
     */
    public String getRealinsureno(String ticketNo, int r1) {
        String sql = "select C_REALINSURENO from T_TRAINTICKET WITH(NOLOCK) where C_TICKETNO = '" + ticketNo + "'";
        String Realinsureno = "";
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list != null && list.size() > 0) {
                Map<String, String> map = list.get(0);
                Realinsureno = String.valueOf(map.get("C_REALINSURENO"));
            }
        }
        catch (Exception e) {
            WriteLog.write(logNameException, r1 + "");
            ExceptionUtil.writelogByException(logNameException, e);
        }

        return Realinsureno;
    }
}
