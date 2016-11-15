package com.ccservice.b2b2c.atom.servlet;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import com.ccservice.b2b2c.atom.mqlistener.GetTrainorderMessageListener;

/**
 * 保险投保队列
 * @time 2015年1月24日 下午6:02:07
 * @author chendong
 */
@SuppressWarnings("serial")
public class QueueMQ_TrainInsure extends HttpServlet {

    private String mqaddress = "";// MQ地址

    private String mqusername = "";// MQ 用户名

    private String isstart = "";// 是否开启

    private int querymqnum = 1;

    @Override
    public void init() throws ServletException {
        super.init();
        this.mqaddress = this.getInitParameter("mqaddress");
        this.mqusername = this.getInitParameter("mqusername");
        this.isstart = this.getInitParameter("isstart");
        try {
            this.querymqnum = Integer.parseInt(this.getInitParameter("insureordernum"));
        }
        catch (Exception e) {
        }
        if ("1".equals(isstart)) {
            System.out.println("投保队列:开启");
            orderNotice(this.mqaddress, this.mqusername);
        }
        else {
            System.out.println("投保队列:关闭");
        }
    }

    public void orderNotice(String mqaddress, String mqusername) {
        ConnectionFactory cf = new ActiveMQConnectionFactory(mqaddress);
        Connection conn = null;
        Session session = null;
        try {
            conn = cf.createConnection();
            for (int i = 0; i < querymqnum; i++) {
                session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = new ActiveMQQueue(mqusername);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(new GetTrainorderMessageListener());

            }
            conn.start();
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
