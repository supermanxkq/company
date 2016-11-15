package com.ccservice.b2b2c.util;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.ccservice.b2b2c.atom.component.WriteLog;

public class ActiveMQUtil {

    /**
     * 用于发送消息
     * 
     * @param url
     * @param QUEUE_NAME
     * @param expectedBody
     * @throws JMSException
     * @time 2015年1月4日 下午12:07:08
     * @author chendong
     */
    public static void sendMessage(String url, String QUEUE_NAME, String expectedBody) {// ConnectionFactory ：连接工厂，JMS 用它创建连接
        // Connection ：JMS 客户端到JMS Provider 的连接
        Connection connection = null;
        // Session： 一个发送或接收消息的线程
        Session session;
        // Destination ：消息的目的地;消息发送给谁.
        Destination destination;
        // MessageProducer：消息发送者
        MessageProducer producer;
        // TextMessage message;
        // 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", url);
        try {
            // 构造从工厂得到连接对象
            connection = connectionFactory.createConnection();
            // 启动
            connection.start();
            // 获取操作连接
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            destination = session.createQueue(QUEUE_NAME);
            // 得到消息生成者【发送者】
            producer = session.createProducer(destination);
            // 设置不持久化，此处学习，实际根据项目决定
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            // 构造消息，此处写死，项目就是参数，或者方法获取
            TextMessage message = session.createTextMessage(expectedBody);
            // 发送消息到目的地方
            //            System.out.println("发送消息：" + ":" + expectedBody);
            producer.send(message);
            session.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                WriteLog.write("MQ_ERROR", url + "@" + QUEUE_NAME + "@" + expectedBody);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        finally {
            try {
                if (null != connection)
                    connection.close();
            }
            catch (Throwable ignore) {
            }
        }
    }

    /**
     * 用于发送消息
     * 
     * @param url
     * @param QUEUE_NAME
     * @param expectedBody
     * @param level 消息的优先级。0-4为正常的优先级，5-9为高优先级。可以通过下面方式设置：
     * @throws JMSException
     */
    public static void sendMessage(String url, String QUEUE_NAME, String expectedBody, int level) {// ConnectionFactory ：连接工厂，JMS 用它创建连接
        // Connection ：JMS 客户端到JMS Provider 的连接
        Connection connection = null;
        // Session： 一个发送或接收消息的线程
        Session session;
        // Destination ：消息的目的地;消息发送给谁.
        Destination destination;
        // MessageProducer：消息发送者
        MessageProducer producer;
        // TextMessage message;
        // 构造ConnectionFactory实例对象，此处采用ActiveMq的实现jar
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("admin", "admin", url);
        try {
            // 构造从工厂得到连接对象
            connection = connectionFactory.createConnection();
            // 启动
            connection.start();
            // 获取操作连接
            session = connection.createSession(Boolean.TRUE, Session.AUTO_ACKNOWLEDGE);
            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            destination = session.createQueue(QUEUE_NAME);
            // 得到消息生成者【发送者】
            producer = session.createProducer(destination);
            // 设置不持久化，此处学习，实际根据项目决定
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            // 构造消息，此处写死，项目就是参数，或者方法获取
            TextMessage message = session.createTextMessage(expectedBody);
            // 发送消息到目的地方
            producer.setPriority(level);
            producer.send(message);
            session.commit();
        }
        catch (Exception e) {
            e.printStackTrace();
            try {
                WriteLog.write("MQ_ERROR", url + "@" + QUEUE_NAME + "@" + expectedBody + "@" + level);
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        finally {
            try {
                if (null != connection)
                    connection.close();
            }
            catch (Throwable ignore) {
            }
        }
    }

    public static String receiveMessage(String url, String QUEUE_NAME) { // ConnectionFactory ：连接工厂，JMS 用它创建连接
        String message_text = "";
        ConnectionFactory connectionFactory;
        // Connection ：JMS 客户端到JMS Provider 的连接
        Connection connection = null;
        // Session： 一个发送或接收消息的线程
        Session session;
        // Destination ：消息的目的地;消息发送给谁.
        Destination destination;
        // 消费者，消息接收者
        MessageConsumer consumer;
        connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_USER,
                ActiveMQConnection.DEFAULT_PASSWORD, url);
        try {
            // 构造从工厂得到连接对象
            connection = connectionFactory.createConnection();
            // 启动
            connection.start();
            // 获取操作连接
            session = connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
            // 获取session注意参数值xingbo.xu-queue是一个服务器的queue，须在在ActiveMq的console配置
            destination = session.createQueue(QUEUE_NAME);
            consumer = session.createConsumer(destination);
            while (true) {
                //设置接收者接收消息的时间，为了便于测试，这里谁定为100s
                TextMessage message = (TextMessage) consumer.receive(100000);
                if (null != message) {
                    message_text = message.getText();
                    System.out.println("收到消息:" + message_text);
                }
                else {
                    break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                if (null != connection)
                    connection.close();
            }
            catch (Throwable ignore) {
            }
        }
        return message_text;
    }

}
