package com.ccservice.b2b2c.atom.servlet;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengTrainOrder;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public class QunarTrainOrderCallBack {
    /**
     * 占座成功后回调给qunar(异步接口或者同步超时成功回调)
     * @param trainorder
     * @param merchantCode
     * @param returnmsg
     * @return
     * @time 2015年1月15日 下午9:14:06
     * @author fiend
     * @throws UnsupportedEncodingException 
     */
    public String train_order_callback(Trainorder trainorder, String merchantCode, String returnmsg, String key,
            String zhanzuojieguoBackUrl) throws UnsupportedEncodingException {
        String status = "false";//true:成功，false:失败
        int code = 100;

        String msg = "";
        String msg1 = "";
        String HMAC = "";
        String param = "";
        String result = "";
        String result1 = "";
        String comment = "";
        String orderNo = trainorder.getQunarOrdernumber();
        if (trainorder.getOrderstatus() == 1) {//等待支付说明占座成功
            String ticketNo = trainorder.getExtnumber();
            JSONArray tickets = new JSONArray();
            JSONArray tickets1 = new JSONArray();
            for (int i = 0; i < trainorder.getPassengers().size(); i++) {
                Trainpassenger trainpassenger = trainorder.getPassengers().get(i);
                Trainticket trainticket = trainpassenger.getTraintickets().get(0);
                JSONObject ticketjson = new JSONObject();
                JSONObject ticketjson1 = new JSONObject();
                //ticket_no票号（此票在本订单中的唯一标识，订票成功后才有值）
                ticketjson.put("ticketNo", ticketNo);
                ticketjson1.put("ticketNo", ticketNo);
                String seattypeString = trainticket.getSeattype();
                if (seattypeString.contains("卧")) {
                    seattypeString = trainticket.getSeattype()
                            + ((trainticket.getSeatno().contains("下")) ? "下"
                                    : (trainticket.getSeatno().contains("中")) ? "中" : (trainticket.getSeatno()
                                            .contains("上")) ? "上" : "");
                }
                if (trainticket.getSeatno() != null && "无座".equals(trainticket.getSeatno())) {
                    seattypeString = "无座";
                }
                ticketjson.put("seatType", TongchengTrainOrder.getzwname_Qunar(seattypeString));
                ticketjson1.put("seatType", TongchengTrainOrder.getzwname_Qunar(seattypeString));
                try {
                    //cxin几车厢几座（在订票成功后才会有值）
                    String seatNo = trainticket.getCoach() + "车" + trainticket.getSeatno().replace('座', '号');
                    if (trainticket.getSeatno() != null && "无座".equals(trainticket.getSeatno())) {
                        seatNo = trainticket.getCoach() + "车无座";
                    }
                    ticketjson1.put("seatNo", seatNo);
                    seatNo = geturlencode(seatNo);
                    ticketjson.put("seatNo", seatNo);
                }
                catch (Exception e) {
                }
                //            price   string  票价
                ticketjson.put("price", trainticket.getPrice() + "");
                ticketjson1.put("price", trainticket.getPrice() + "");
                //            passengersename string  乘客姓名
                ticketjson1.put("passengerName", trainpassenger.getName());
                String name = geturlencode(trainpassenger.getName());
                ticketjson.put("passengerName", name);
                //联程值 因为占座没有联程 所以为0
                ticketjson.put("seq", "0");
                ticketjson1.put("seq", "0");
                //            ticketType    string  票种ID。
                //            与票种名称对应关系：
                //            1:成人票，0:儿童票，2:学生票
                ticketjson.put("ticketType", ticketType(trainticket.getTickettype()) + "");
                ticketjson1.put("ticketType", ticketType(trainticket.getTickettype()) + "");
                tickets.add(ticketjson);
                tickets1.add(ticketjson1);
            }

            JSONObject jsoresult = new JSONObject();
            JSONObject jsoresult1 = new JSONObject();
            jsoresult.put("count", trainorder.getPassengers().size());
            jsoresult1.put("count", trainorder.getPassengers().size());
            jsoresult.put("tickets", tickets);
            jsoresult1.put("tickets", tickets1);
            status = "success";
            result = jsoresult.toString();
            result1 = jsoresult1.toString();
        }
        else if (trainorder.getOrderstatus() == 8) {//取消订单交易关闭   说明没有占座成功
            status = "false";
            code = 0;
            String res = returnmsg;
            res = res == null ? "" : geturldecode(res);
            WriteLog.write("QUNAR火车票接口_占座回调", trainorder.getId() + ":res:" + res);
            if (res.indexOf("没有余票") > -1 || res.indexOf("此车次无票") > -1 || res.indexOf("已无余票") > -1) {
                code = 1;
                msg1 = msg = "没有余票";
                msg = geturlencode(msg);
            }
            else if (res.indexOf("没有足够的票") > -1) {
                code = 1;
                msg1 = msg = "没有足够的票";
                msg = geturlencode(msg);
            }
            else if (res.indexOf("已订") > -1) {
                code = 2;
                msg1 = msg = "乘客已办理其他订单";
                msg = geturlencode(msg);
            }
            else if (res.indexOf("身份") > -1) {
                code = 6;
                msg1 = msg = "乘客身份信息未通过验证";
                WriteLog.write("QUNAR火车票接口_占座回调", trainorder.getId() + ":msg:" + msg);
                msg = geturlencode(msg);
            }
            else if (res.indexOf("其他订单行") > -1 || res.indexOf("本次购票行程冲突") > -1) {
                code = 2;
                msg1 = msg = "本次购票与其他订单行程冲突";
                msg = geturlencode(msg);
            }
            else {
                code = 0;
                msg1 = "其他";
                msg = geturlencode("其他");
            }
        }
        try {
            WriteLog.write("QUNAR火车票接口_占座回调", trainorder.getId() + ":backurl:" + zhanzuojieguoBackUrl + ":parm:data="
                    + param);
            WriteLog.write("QUNAR火车票接口_占座回调_keyHMAC", key + merchantCode + orderNo + result1 + status + code + msg1
                    + comment);

            HMAC = ElongHotelInterfaceUtil.MD5(key + merchantCode + orderNo + result1 + status + code + msg1 + comment)
                    .toUpperCase();
            WriteLog.write("QUNAR火车票接口_占座回调_keyHMAC", HMAC);
        }
        catch (Exception e1) {

        }
        param = "merchantCode=" + merchantCode + "&orderNo=" + orderNo + "&result=" + result + "&status=" + status
                + "&code=" + code + "&msg=" + msg + "&comment=" + comment + "&HMAC=" + HMAC;
        WriteLog.write("QUNAR火车票接口_占座回调", trainorder.getId() + ":backurl:" + zhanzuojieguoBackUrl + ":parm:data="
                + param);
        String ret = SendPostandGet.submitPost(zhanzuojieguoBackUrl, param, "utf-8").toString();
        WriteLog.write("QUNAR火车票接口_占座回调", trainorder.getId() + ":qunar返回:" + ret);
        JSONObject jsoret = JSONObject.parseObject(ret);
        int i = 0;
        if ("true".equalsIgnoreCase(jsoret.getString("ret"))) {
            ret = "success";
        }
        else {
            while (i < 5) {
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ret = SendPostandGet.submitPost(zhanzuojieguoBackUrl, param, "utf-8").toString();
                WriteLog.write("QUNAR火车票接口_占座回调", trainorder.getId() + ":第" + i + "次:qunar返回:" + ret);
                if ("true".equalsIgnoreCase(jsoret.getString("ret"))) {
                    ret = "success";
                    i = 5;
                }
                else {
                    i++;
                }
            }
        }
        return ret;
    }

    private String geturlencode(String oldstring) {
        try {
            oldstring = URLEncoder.encode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }

    private String geturldecode(String oldstring) {
        try {
            oldstring = URLDecoder.decode(oldstring, "utf-8");
        }
        catch (Exception e) {
        }
        return oldstring;
    }

    //映射票种类型
    private int ticketType(int type) {

        if (type == 2) {
            type = 0;
        }
        if (type == 3) {
            type = 2;
        }
        return type;
    }
}
