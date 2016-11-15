package com.ccservice.b2b2c.atom.servlet.TongChengTrain.paiduiChange;

import javax.jms.Session;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.ConnectionFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.ActiveMQConnectionFactory;

/**
 * 改签订单排队MQ
 */

@SuppressWarnings("serial")
public class QueueMQ_TrainChangeOrderPaidui extends HttpServlet {

    private String mqaddress = "";//MQ地址

    private String mqusername = "";//MQ用户名

    private String isstart = "";//是否开启

    private int changeOrderPaiDuiNum = 0;

    public void init() throws ServletException {
        super.init();
        isstart = getInitParameter("isstart");
        mqaddress = getInitParameter("mqaddress");
        mqusername = getInitParameter("mqusername");
        changeOrderPaiDuiNum = Integer.parseInt(getInitParameter("changeOrderPaiDuiNum"));
        if ("1".equals(isstart)) {
            System.out.println("改签订单排队-----开启");
            changeNotice();
        }
        else {
        }
    }

    private void changeNotice() {
        ConnectionFactory cf = new ActiveMQConnectionFactory(mqaddress);
        Connection conn = null;
        Session session = null;
        try {
            conn = cf.createConnection();
            Destination destination = new ActiveMQQueue(mqusername);
            for (int i = 0; i < this.changeOrderPaiDuiNum; i++) {
                session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(new TrainChangeOrderPaiduiMessgeListener());
            }
            conn.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}