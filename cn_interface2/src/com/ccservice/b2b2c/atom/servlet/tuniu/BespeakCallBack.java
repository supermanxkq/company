package com.ccservice.b2b2c.atom.servlet.tuniu;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.util.DesUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class BespeakCallBack extends Thread {

    public String orderid = "";

    public String url = "";

    public String flag = "";

    public String refuseMsg = "";

    public BespeakCallBack(String dataStr, String flag, String refuseMsg) {
        this.orderid = dataStr;
        this.flag = flag;
        this.refuseMsg = refuseMsg;
    }

    public void run() {
        callbacktotuniu(orderid, flag, refuseMsg);
    }

    //解析回调数据，回调给途牛
    public void callbacktotuniu(String orderid, String flag, String refuseMsg) {
        if (flag.equals("true")) {
            for (int i = 1; i <= 5; i++) {
                String param = getJsonStr(orderid, flag).toJSONString();
                WriteLog.write("途牛抢票_出票回调", "orderid=" + orderid + ";Url:" + this.url + "?" + param);
                String result = SendPostandGet.submitPost(this.url, param, "utf-8").toString();
                WriteLog.write("途牛抢票_出票回调", "orderid=" + orderid + ";result=" + result);
                Trainorder trainorder = new Trainorder();
                if (result.equalsIgnoreCase("SUCCESS")) {
                    trainorder.setId(Long.parseLong(orderid));
                    trainorder.setOrderstatus(Trainorder.ISSUED);
                    trainorder.setIsquestionorder(Trainorder.NOQUESTION);
                    Server.getInstance().getTrainService().updateTrainorder(trainorder);
                    Server.getInstance()
                            .getSystemService()
                            .findMapResultByProcedure(
                                    " sp_T_TRAINTICKET_Update_STATUS @ORDERID=" + Long.parseLong(orderid));
                    createTrainorderrc(Long.parseLong(orderid), "出票回调:回调成功", "抢票系统", 1);
                    break;
                }
                else if (i == 5 && !result.equalsIgnoreCase("SUCCESS")) {
                    trainorder.setId(Long.parseLong(orderid));
                    trainorder.setIsquestionorder(Trainorder.CAIGOUQUESTION);
                    Server.getInstance().getTrainService().updateTrainorder(trainorder);
                    createTrainorderrc(Long.parseLong(orderid), "出票回调:回调失败", "抢票系统", 1);
                }
            }
        }
        else {
            for (int i = 0; i < 5; i++) {
                String param = getJsonStr(orderid, flag).toJSONString();
                WriteLog.write("途牛抢票_出票回调", "orderid=" + orderid + ";Url:" + this.url + "?" + param);
                String result = SendPostandGet.submitPost(this.url, param, "utf-8").toString();
                WriteLog.write("途牛抢票_出票回调", "orderid=" + orderid + ";result=" + result);
                Trainorder trainorder = new Trainorder();
                if (result.equalsIgnoreCase("SUCCESS")) {
                    trainorder.setId(Long.parseLong(orderid));
                    trainorder.setOrderstatus(Trainorder.CANCLED);
                    trainorder.setIsquestionorder(Trainorder.NOQUESTION);
                    Server.getInstance().getTrainService().updateTrainorder(trainorder);
                    createTrainorderrc(Long.parseLong(orderid), "抢票失败:回调成功@" + this.refuseMsg, "抢票系统", 1);
                    break;
                }
                else if (i == 5 && !result.equalsIgnoreCase("SUCCESS")) {
                    trainorder.setId(Long.parseLong(orderid));
                    trainorder.setIsquestionorder(Trainorder.CAIGOUQUESTION);
                    Server.getInstance().getTrainService().updateTrainorder(trainorder);
                    createTrainorderrc(Long.parseLong(orderid), "抢票失败:回调失败@" + this.refuseMsg, "抢票系统", 1);
                }
            }
        }
    }

    //查询回调所需数据
    public JSONObject getJsonStr(String orderid, String flag) {
        JSONObject json = new JSONObject();
        try {
            String sign = "";
            String timestamp = getCurrTime();
            String data = "";
            Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(Long.valueOf(orderid));
            String sql = "select *from T_INTERFACEACCOUNT where C_AGENTID=" + String.valueOf(trainorder.getAgentid());
            List<Map> list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            this.url = list.get(0).get("C_PAYCALLBACKURL").toString();
            String key = list.get(0).get("C_KEY").toString();
            String account = list.get(0).get("C_USERNAME").toString();
            String vendorOrderId = trainorder.getOrdernumber();
            String orderId = trainorder.getQunarOrdernumber();
            String orderSuccess = flag;
            String orderAmount = String.valueOf(trainorder.getOrderprice());
            String cheCi = trainorder.getPassengers().get(0).getTraintickets().get(0).getTrainno();
            String fromStationName = trainorder.getPassengers().get(0).getTraintickets().get(0).getDeparture();
            String toStationName = trainorder.getPassengers().get(0).getTraintickets().get(0).getArrival();
            String trainDate = trainorder.getPassengers().get(0).getTraintickets().get(0).getDeparttime();
            String startTime = trainorder.getPassengers().get(0).getTraintickets().get(0).getDeparttime();
            String arriveTime = trainorder.getPassengers().get(0).getTraintickets().get(0).getArrivaltime();
            String orderNumber = trainorder.getExtnumber();
            JSONArray passengers = new JSONArray();
            for (int i = 0; i < trainorder.getPassengers().size(); i++) {
                String reason = "0";
                String price = String.valueOf(trainorder.getPassengers().get(i).getTraintickets().get(0).getPrice());
                String passengerId = String.valueOf(trainorder.getPassengers().get(i).getPassengerid());
                String ticketNo = trainorder.getPassengers().get(i).getTraintickets().get(0).getTicketno();
                String zwName = trainorder.getPassengers().get(i).getTraintickets().get(0).getSeattype();
                String zwCode = code(zwName);
                String cxin = trainorder.getPassengers().get(i).getTraintickets().get(0).getCoach() + "车厢 "
                        + trainorder.getPassengers().get(i).getTraintickets().get(0).getSeatno();
                String passportTypeName = String.valueOf(trainorder.getPassengers().get(0).getIdtypestr());
                String passportNo = trainorder.getPassengers().get(i).getIdnumber();
                String piaoType = String.valueOf(trainorder.getPassengers().get(i).getTraintickets().get(0)
                        .getTickettype());
                String passengerName = trainorder.getPassengers().get(i).getName();
                String passportTypeId = String.valueOf(trainorder.getPassengers().get(i).getIdtype());
                String piaoTypeName = String.valueOf(trainorder.getPassengers().get(i).getTraintickets().get(0)
                        .getTickettypestr());
                JSONObject jsonnew = new JSONObject();
                jsonnew.put("reason", reason);
                jsonnew.put("price", price);
                jsonnew.put("passengerId", passengerId);
                jsonnew.put("ticketNo", ticketNo);
                jsonnew.put("zwCode", zwCode);
                jsonnew.put("cxin", cxin);
                jsonnew.put("passportTypeName", passportTypeName);
                jsonnew.put("passportNo", passportNo);
                jsonnew.put("zwName", zwName);
                jsonnew.put("piaoType", piaoType);
                jsonnew.put("passengerName", passengerName);
                jsonnew.put("passportTypeId", passportTypeId);
                jsonnew.put("piaoTypeName", piaoTypeName);
                passengers.add(jsonnew);
            }
            JSONObject json1 = new JSONObject();
            json1.put("vendorOrderId", vendorOrderId);
            json1.put("orderId", orderId);
            json1.put("orderSuccess", orderSuccess);
            json1.put("orderAmount", orderAmount);
            json1.put("cheCi", cheCi);
            try {
                json1.put("fromStationCode", Train12306StationInfoUtil.getThreeByName(fromStationName));
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            json1.put("fromStationName", fromStationName);
            try {
                json1.put("toStationCode", Train12306StationInfoUtil.getThreeByName(toStationName));
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            json1.put("toStationName", toStationName);
            json1.put("trainDate", trainDate);
            json1.put("startTime", startTime);
            json1.put("arriveTime", arriveTime);
            json1.put("orderNumber", orderNumber);
            json1.put("passengers", passengers);
            try {
                //                data = new String(Base64.encodeBase64URLSafeString(DesUtil.encrypt(json1.toString().getBytes("UTF-8"),
                //                        key.getBytes("UTF-8"))));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sign = SignUtil.generateSign(json1.toString(), key);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            json.put("account", account);
            json.put("sign", sign);
            json.put("timestamp", timestamp);
            json.put("returnCode", errMsgCode().split(",")[0]);
            json.put("errorMsg", URLEncoder.encode(errMsgCode().split(",")[1], "utf-8"));
            json.put("data", data);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_途牛抢票_出票回调", e);
        }
        return json;
    }

    public String errMsgCode() {
        String errMsgCode = "";
        if (this.refuseMsg.indexOf("无余票") > -1 || this.refuseMsg.indexOf("没有足够的票") > -1) {
            errMsgCode = "301," + this.refuseMsg;
        }
        else if (this.refuseMsg.indexOf("已订") > -1) {
            errMsgCode = "305," + this.refuseMsg;
        }
        else if (this.refuseMsg.indexOf("身份信息涉嫌被他人冒用") > -1 || this.refuseMsg.indexOf("身份") > -1) {
            errMsgCode = "308," + this.refuseMsg;
        }
        else if (this.refuseMsg.indexOf("其他订单行") > -1 || this.refuseMsg.indexOf("本次购票行程冲突") > -1) {
            errMsgCode = "310," + this.refuseMsg;
        }
        else if (this.refuseMsg.indexOf("限制高消费") > -1) {
            errMsgCode = "312," + this.refuseMsg;
        }
        else if (this.refuseMsg.equals("支付成功")) {
            errMsgCode = "231000,出票成功";
        }
        else {
            errMsgCode = "999," + this.refuseMsg;
        }
        return errMsgCode;
    }

    public String code(String Name) {
        String code = "";
        if (Name.contains("棚车")) {
            code = "0";
        }
        else if (Name.contains("硬座")) {
            code = "1";
        }
        else if (Name.contains("软座")) {
            code = "2";
        }
        else if (Name.contains("硬卧")) {
            code = "3";
        }
        else if (Name.contains("软卧")) {
            code = "4";
        }
        else if (Name.contains("包厢硬卧")) {
            code = "5";
        }
        else if (Name.contains("高级软卧")) {
            code = "6";
        }
        else if (Name.contains("一等软座")) {
            code = "7";
        }
        else if (Name.contains("二等软座")) {
            code = "8";
        }
        else if (Name.contains("商务座")) {
            code = "9";
        }
        return code;
    }

    /**
     * 书写操作记录
     **/
    protected void createTrainorderrc(Long trainorderId, String content, String createuser, int status) {
        try {
            Trainorderrc rc = new Trainorderrc();
            rc.setOrderid(trainorderId);
            rc.setContent(content);
            rc.setStatus(status);
            rc.setCreateuser(createuser);
            rc.setYwtype(1);
            Server.getInstance().getTrainService().createTrainorderrc(rc);
        }
        catch (Exception e) {
            WriteLog.write("操作记录失败", trainorderId + ":content:" + content);
        }
    }

    //获取当前请求时间
    public String getCurrTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

}