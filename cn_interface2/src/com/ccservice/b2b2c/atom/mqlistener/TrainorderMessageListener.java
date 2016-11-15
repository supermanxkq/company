package com.ccservice.b2b2c.atom.mqlistener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;

/**
 * 
 * @author Administrator
 *
 */
public class TrainorderMessageListener implements MessageListener {
    private String orderNoticeResult;

    private long orderid;

    @Override
    public void onMessage(Message message) {
        try {
            orderNoticeResult = ((TextMessage) message).getText();
            this.orderid = Long.parseLong(orderNoticeResult);
            Trainorder trainOrder = Server.getInstance().getTrainService().findTrainorder(orderid);

        }
        catch (JMSException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
