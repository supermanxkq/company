package com.ccservice.b2b2c.atom.servlet;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.MQ.MQMethod;
import com.ccservice.b2b2c.atom.servlet.MQ.TrainpayMqMSGUtil;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public class QunarInformTicket extends Thread {

    Trainorder order;

    QunarOrderMethod qunarordermethod;

    String qunarPayurl;

    String key;

    String merchantCode;

    String fanhui;

    public QunarInformTicket(Trainorder order, QunarOrderMethod qunarordermethod, String qunarPayurl, String key,
            String merchantCode) {
        this.order = order;
        this.qunarordermethod = qunarordermethod;
        this.qunarPayurl = qunarPayurl;
        this.key = key;
        this.merchantCode = merchantCode;

    }

    @Override
    public void run() {
        try {

            String hmccString = qunarTrainPay(order, qunarPayurl, key, this.merchantCode);
            WriteLog.write("QUNAR火车票接口_通知出票", "success:" + hmccString);
            if (hmccString.equals("success")) {
                String sql = "UPDATE T_TRAINORDER SET C_ISQUESTIONORDER=0,C_STATE12306=5,C_ORDERSTATUS=2,C_SUPPLYTRADENO='qunarpaying',C_SUPPLYPAYWAY=14,"
                        + "C_SUPPLYACCOUNT=C_SUPPLYACCOUNT+'/QUNAR',C_SUPPLYPRICE=C_TOTALPRICE WHERE ID ="
                        + qunarordermethod.getId();
                Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                writetrainorderrc("qunar代付发送成功", 2, qunarordermethod);
                WriteLog.write("QUNAR火车票接口_通知出票", "qunar代付发送成功");
            }
            else {
                String sql = "UPDATE T_TRAINORDER SET C_ISQUESTIONORDER=0,C_STATE12306=4,C_ORDERSTATUS=2,C_SUPPLYTRADENO='qunarpayfalse',C_SUPPLYPAYWAY=14 WHERE ID ="
                        + qunarordermethod.getId();
                Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                writetrainorderrc("qunar代付发送失败", 2, qunarordermethod);
                WriteLog.write("QUNAR火车票接口_通知出票", "qunar代付发送失败");
                //TODO
                try {
                    new TrainpayMqMSGUtil(MQMethod.ORDERPAY_NAME).sendPayMQmsg(order, 1, 0);
                    WriteLog.write("QUNAR火车票接口_通知出票_MQ", ":确认出票：" + order.getOrdernumber());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String qunarTrainPay(Trainorder trainorder, String qunarPayurl, String key, String merchantCoden) {
        String status = "false";//        true:成功，false:失败
        int code = 100;
        String msg = "";
        String param = "";
        //qunar订单号
        String orderNo = trainorder.getQunarOrdernumber();
        //订单12306总价
        float price = 0f;
        //订单12306账号
        String un = trainorder.getSupplyaccount().split("/")[0];
        //订单备注
        String comment = "";
        //签名字段
        String HMAC = "";
        for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
            for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                price += trainticket.getPrice();
            }
        }

        try {
            HMAC = ElongHotelInterfaceUtil.MD5(key + this.merchantCode + orderNo + price + un + comment).toUpperCase();
        }
        catch (Exception e1) {
        }

        param = "merchantCode=" + this.merchantCode + "&orderNo=" + orderNo + "&price=" + price + "&un=" + un
                + "&comment=" + comment + "&HMAC=" + HMAC;
        WriteLog.write("QUNAR火车票接口_通知出票", trainorder.getId() + ":backurl:" + qunarPayurl + ":parm:data=" + param);
        String ret = SendPostandGet.submitPost(qunarPayurl, param, "utf-8").toString();
        WriteLog.write("QUNAR火车票接口_通知出票", trainorder.getId() + ":qunar返回:" + ret);
        JSONObject jsoret = JSONObject.parseObject(ret);
        int i = 0;
        if ("true".equalsIgnoreCase(jsoret.getString("ret"))) {
            ret = "success";
        }
        else {
            while (i < 5) {
                try {
                    Thread.sleep(5000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                WriteLog.write("QUNAR火车票接口_通知出票", trainorder.getId() + ":backurl:" + qunarPayurl + ":parm:data="
                        + param);
                ret = SendPostandGet.submitPost(qunarPayurl, param, "utf-8").toString();
                WriteLog.write("QUNAR火车票接口_通知出票", trainorder.getId() + ":qunar返回:" + ret);
                jsoret = JSONObject.parseObject(ret);
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

    /**
     * 此类公用生成操作记录方法 
     * @param content
     * @param status
     * @time 2015年1月31日 下午3:37:17
     * @author fiend
     */
    private void writetrainorderrc(String content, int status, QunarOrderMethod qunarordermethod) {
        createTrainorderrc(qunarordermethod.getId(), content, "qunar拉单接口", status, 1);
    }

    /**
     * 创建火车票操作记录
     * 
     * @param trainorderId 火车票订单id
     * @param content 内容
     * @param createuser 用户
     * @param status 状态
     * @time 2015年1月18日 下午5:02:43
     * @author fiend
     */
    private void createTrainorderrc(Long trainorderId, String content, String createuser, int status, int ywtype) {
        Trainorderrc rc = new Trainorderrc();
        rc.setOrderid(trainorderId);
        rc.setContent(content);
        rc.setStatus(status);
        rc.setCreateuser(createuser);
        rc.setYwtype(ywtype);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

}
