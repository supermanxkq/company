package com.ccservice.b2b2c.atom.pay.gp.certificate;

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

/**
 * Gp快钱支付凭证生成
 * @author wzc
 *
 */
public class QueueMQ_GpcertificateCreate extends HttpServlet {

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
            this.querymqnum = Integer.parseInt(this.getInitParameter("Gpcertificatenum"));
        }
        catch (Exception e) {
        }
        if ("1".equals(isstart)) {
            System.out.println("GP生成支付凭证队列:开启");
            orderNotice(this.mqaddress, this.mqusername);
        }
        else {
            System.out.println("GP生成支付凭证队列:关闭");
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
                consumer.setMessageListener(new GpcertificateCreateMessageListener());

            }
            conn.start();
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
