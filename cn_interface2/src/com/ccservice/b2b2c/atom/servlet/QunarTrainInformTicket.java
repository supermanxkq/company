package com.ccservice.b2b2c.atom.servlet;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.MQ.MQMethod;
import com.ccservice.b2b2c.atom.servlet.MQ.TrainpayMqMSGUtil;
import com.ccservice.b2b2c.atom.train.data.thread.AddMemcachedThread;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;

public class QunarTrainInformTicket {
    /**
     * @time 2015年7月13日10:38:55
     * @param orderNo
     * @param Type
     * @param reqFrom
     * @param reqTime
     **/
    public String InformTicket(String orderNo, String Type, String reqFrom, String reqTime, String key,
            String merchantCode, String qunarurl) {
        JSONObject jsonStr = new JSONObject();
        int r1 = new Random().nextInt(10000000);
        QunarOrderMethod qunarordermethod = new QunarOrderMethod();
        String qunarPayurl = qunarurl + "ProcessGoPay.do";
        if (ElongHotelInterfaceUtil.StringIsNull(orderNo)) {
            jsonStr.put("success", false);
            jsonStr.put("code", "107");
            jsonStr.put("msg", "业务参数缺失");
            return jsonStr.toJSONString();
        }
        WriteLog.write("QUNAR火车票接口_通知出票", "进入支付:orderNo" + orderNo + ":Type:" + Type + ":reqFrom:" + reqFrom
                + ":reqTime:" + reqTime + ":key:" + key + ":merchantCode:" + merchantCode);
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderNo);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        //订单不存在
        if (orders == null || orders.size() == 0) {
            jsonStr.put("success", false);
            jsonStr.put("code", "402");
            jsonStr.put("msg", "订单不存在");
            return jsonStr.toJSONString();
        }
        Trainorder order = new Trainorder();
        if (orders.size() == 1) {
            order = orders.get(0);
        }

        int status = order.getOrderstatus();//状态
        int status12306 = order.getState12306();
        String extnumber = order.getExtnumber();//12306订单号
        WriteLog.write("QUNAR火车票接口_通知出票", ":status状态：" + status + "：12306状态：" + status12306 + ":12306订单号：" + extnumber);
        WriteLog.write("QUNAR火车票接口_通知出票", ":Trainorder状态：" + status12306 + "：12306订单号：" + extnumber);

        //加载其他字段、乘客
        order = Server.getInstance().getTrainService().findTrainorder(order.getId());
        order.setPaymethod(4);

        //更新订单
        order.setOrderstatus(Trainorder.WAITISSUE);
        Server.getInstance().getTrainService().updateTrainorder(order);
        WriteLog.write("QUNAR火车票接口_通知出票", "：订单号：" + order.getId());
        //更新车票
        for (Trainpassenger trainpassenger : order.getPassengers()) {
            for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                trainticket.setStatus(Trainticket.WAITISSUE);
                Server.getInstance().getTrainService().updateTrainticket(trainticket);
            }
        }
        try {//日志
            Trainorderrc rz = new Trainorderrc();
            rz.setYwtype(1);
            rz.setCreateuser("系统接口");
            rz.setContent("接口确认出票");
            rz.setOrderid(order.getId());
            rz.setStatus(Trainorder.WAITPAY);
            Server.getInstance().getTrainService().createTrainorderrc(rz);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        qunarordermethod.setQunarordernumber(order.getQunarOrdernumber());//去哪儿订单号
        qunarordermethod.setId(order.getId());
        qunarordermethod.setOrderstatus(order.getOrderstatus());//订单状态
        qunarordermethod.setCreatetime("" + order.getCreatetime());//创建时间
        qunarordermethod.setIsquestionorder(order.getIsquestionorder());
        qunarordermethod.setState12306(order.getState12306());//12306状态
        qunarordermethod.setInterfaceType(order.getInterfacetype());
        AddQunarInformTicket(order, qunarordermethod, qunarPayurl, key, merchantCode);
        //返回
        jsonStr.put("ret", true);
        return jsonStr.toJSONString();

    }

    public void AddQunarInformTicket(Trainorder order, QunarOrderMethod qunarordermethod, String qunarPayurl,
            String key, String merchantCode) {// 创建一个可重用固定线程数的线程池
        ExecutorService pool = Executors.newFixedThreadPool(1);
        // 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
        Thread t1 = null;
        // 将线程放入池中进行执行
        t1 = new QunarInformTicket(order, qunarordermethod, qunarPayurl, key, merchantCode);
        pool.execute(t1);
        pool.shutdown();

    }
}
