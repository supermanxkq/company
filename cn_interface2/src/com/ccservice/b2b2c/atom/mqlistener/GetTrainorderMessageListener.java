package com.ccservice.b2b2c.atom.mqlistener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.tq.TrainInsure;
import com.ccservice.b2b2c.base.train.Trainorder;

/**
 * 
 * 天衢114保险投保
 * @author wzc
 *
 */
public class GetTrainorderMessageListener implements MessageListener {
    private String orderNoticeResult;

    private long orderid;

    @Override
    public void onMessage(Message message) {
        try {
            orderNoticeResult = ((TextMessage) message).getText();
            JSONObject insurobj = JSONObject.parseObject(orderNoticeResult);
            this.orderid = Long.parseLong(insurobj.getString("orderid"));
            String type = insurobj.getString("type");
            if ("1".equalsIgnoreCase(type)) {//1 投保
                Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(orderid);
                TrainInsure.getTongChengTrainInsure().insuranceSynchronous(trainorder);
            }
            else if ("2".equalsIgnoreCase(type)) {// 2 退保
                String policyno = insurobj.getString("policyno");
                TrainInsure.getTongChengTrainInsure().tuibao(orderid, "", policyno);
            }
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
