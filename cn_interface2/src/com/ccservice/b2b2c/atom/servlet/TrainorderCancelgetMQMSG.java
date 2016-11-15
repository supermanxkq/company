package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;

import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.listener.TaoBaoTrainOrderChangeGetMQMSGListener;
import com.ccservice.b2b2c.atom.servlet.listener.TrainOrderCancelGetMQMSGListener;

/**
 * Servlet implementation class TrainorderCancelMQGET
 */
@WebServlet("/TrainorderCancelMQGET")
public class TrainorderCancelgetMQMSG extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void trainOrderCancel(String mqaddress, String mqusername) {
        int mqnum = 1;
        WriteLog.write("取消订单MQ", "进入构建消费者~~");
        ConnectionFactory cf = new ActiveMQConnectionFactory(mqaddress);
        Connection conn = null;
        Session session = null;
        try {
            conn = cf.createConnection();
            for (int i = 0; i < mqnum; i++) {
                WriteLog.write("取消订单MQ", "构建消费者:" + i);
                session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = new ActiveMQQueue(mqusername);
                MessageConsumer consumer = session.createConsumer(destination);
                consumer.setMessageListener(new TrainOrderCancelGetMQMSGListener());
            }
            conn.start();
        }
        catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
