package com.ccservice.b2b2c.atom.servlet.TongChengTrain.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CallBackPassengerUtil;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public class CallBackDataDemo {

    public static int log = 76;

    public static void main(String[] args) {
        //        for (int i = 0; i < 20; i++) {
        //            System.out.println(i % 5 == 0);
        //        }

        cancelBespeakOrderMethod();
    }

    public static void cancelBespeakOrderMethod() {

        for (int i = 76; i < 82; i++) {
            if (i % 5 == 0) {
                log = i;
            }
            System.out.println("===================" + i + "=======================");
            String data2 = "C:/Users/Administrator/Desktop/约票取消订单log/Q_实时取消 -" + i + ".log";
            try {
                File file = new File(data2);
                FileInputStream in = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                String line = null;

                while ((line = br.readLine()) != null) {
                    if (line != null && !"".equals(line)) {
                        disposeDateMethod(line);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void disposeDateMethod(String data) {
        int index = data.indexOf("{");
        if (index > -1) {
            data = data.substring(index, data.length());
            SimpleDateFormat fromFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            JSONObject json = new JSONObject();
            JSONObject jsonData = JSONObject.parseObject(data);
            String qorderid = jsonData.containsKey("qorderid") == true ? jsonData.getString("qorderid") : "";
            if (!"".equals(qorderid)) {
                try {
                    Thread.sleep(100L);
                    String key = "uyf95yzxbnse2ytg3y60yhz026260eu9";
                    String partnerid = "tc_bespeak";
                    String reqtime = fromFormat.format(new Date());
                    String sign = ElongHotelInterfaceUtil.MD5(partnerid + reqtime + ElongHotelInterfaceUtil.MD5(key));
                    String bespeakUrl = "http://bespeak.hangtian123.net/trainorder_bespeak/CancelTicket";

                    json.put("qorderid", qorderid);
                    json.put("partnerid", partnerid);
                    json.put("reqtime", reqtime);
                    json.put("sign", sign);
                    System.out.println(json.toString());
                    String result = SendPostandGet.submitPost(bespeakUrl, "jsonStr=" + json.toString(), "UTF-8")
                            .toString();
                    System.out.println(result);
                    if (result.indexOf("订单取消成功") > 0 && result.indexOf("success") > 0) {
                        WriteLog.write("取消订单成功_" + log, "请求参数:" + json.toString());
                        WriteLog.write("取消订单成功_" + log, "回调参数:" + result);
                    }
                    else {
                        WriteLog.write("取消订单失败_" + log, "请求参数:" + json.toString());
                        WriteLog.write("取消订单失败_" + log, "回调参数:" + result);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 
     * 
     * @time 2015年12月18日 下午6:09:12
     * @author Administrator
     */
    public static void callBackTongchengDisabledAccountMehtod() {

        Customeruser customeruser = new Customeruser();
        customeruser.setLoginname("loginname1");
        customeruser.setIsenable(1);
        List<Trainpassenger> trainPassengers = new ArrayList<Trainpassenger>();
        Trainpassenger p1 = new Trainpassenger();
        p1.setName("许道玉");
        p1.setIdnumber("362323196811086218");
        p1.setIdtype(1);
        Trainticket trainticket = new Trainticket();
        trainticket.setTickettype(1);
        List<Trainticket> traintickets = new ArrayList<Trainticket>();
        traintickets.add(trainticket);
        p1.setTraintickets(traintickets);
        //放入乘客
        trainPassengers.add(p1);
        int operationtypeid = 1;//操作类型 ID 1:新增，2:删除，3:修改
        CallBackPassengerUtil.callBackTongcheng(customeruser, trainPassengers, operationtypeid);
    }
}
