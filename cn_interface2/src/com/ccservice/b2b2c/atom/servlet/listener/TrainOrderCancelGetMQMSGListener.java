package com.ccservice.b2b2c.atom.servlet.listener;

import java.util.Random;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengCancelTrain;

public class TrainOrderCancelGetMQMSGListener implements MessageListener {
    @Override
    public void onMessage(Message message) {
        WriteLog.write("取消订单MQ", "进入消费者取消~~");
        int r1 = new Random().nextInt(10000000);
        try {
            String orderNoticeResult = ((TextMessage) message).getText();
            WriteLog.write("取消订单MQ", "MQ传送值：" + orderNoticeResult);
            JSONObject jsonObject = new JSONObject();
            jsonObject = JSONObject.parseObject(orderNoticeResult);
            TongChengCancelTrain cancelTrain = new TongChengCancelTrain();
            String result = "";
            WriteLog.write("取消订单MQ", "取消失败,返回:" + result);
            int i = 0;
            do {
                result = cancelTrain.operate(jsonObject, r1);
                i++;
                WriteLog.write("取消订单MQ", "取消失败，消费者取消循环次数：" + i);
            }
            while (result.contains("取消订单失败") && i < 5);
        }
        catch (JMSException e) {
            e.printStackTrace();
            WriteLog.write("取消订单MQ", "异常：" + e);
        }
    }

}
