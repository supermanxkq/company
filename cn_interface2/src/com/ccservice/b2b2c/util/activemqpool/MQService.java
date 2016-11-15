package com.ccservice.b2b2c.util.activemqpool;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

/** 
 * 数据发送类，用于发送数据 
 * 如果获得链接，请查看被注释的代码 
 */
public class MQService {

    private static final MQService mqService = new MQService();

    // 发送消息  
    @SuppressWarnings("static-access")
    public void sendMessage(String queueName, String sendMsg) throws Exception {
        long startTime = System.currentTimeMillis();
        //        ActiveMQConnectionFactory connectionFactory = null;
        Connection connection = null;
        Session session = null;
        Queue queue = null;
        MessageProducer producer = null;
        TextMessage message = null;
        // ====================================  
        try {
            // 获得我们自己初始化的链接工厂然后创建链接  
            //            connectionFactory = MQPooledConnectionFactory.getMyActiveMQConnectionFactory();  
            //            connection = connectionFactory.createConnection();  

            // 链接直接从链接池工厂进行获得  
            connection = MQPooledConnectionFactory.getPooledConnectionFactory().createConnection();

            session = connection.createSession(false, session.AUTO_ACKNOWLEDGE);
            queue = session.createQueue(queueName);
            producer = session.createProducer(queue);
            // 链接开始，如果我们使用的是连接池，那么即使你不开始，也是没有问题的  
            //            connection.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // ====================================  
        message = session.createTextMessage(sendMsg);
        producer.send(message);
        // ===================================  
        // 通过打印会话的内存地址和链接的客户端编号就可以知道我们使用的是不是同一个会话和链接  
        //        System.out.println(session.toString());
        //        System.out.println(connection.getClientID());
        // 无论使用的自己的工厂还是连接池的，都要将会话关闭  
        // 如果不关闭，在使用连接池的时可以看到效果，发送两次时只能发送一次，造成堵塞  
        session.close();
        // 使用自己的工厂和连接池的区别是，运行后自己工厂链接调用关闭程序结束  
        // 而调用连接池链接进行关闭实际上没有关闭，因为连接池要维护这个链接  
        connection.close();
        message = null;
        System.out.println("耗时--->" + (System.currentTimeMillis() - startTime));
    }

    private MQService() {
    }

    // 发送对象每次创建一个，用以区别我们使用的对象  
    public static MQService getInstance() {
        return mqService;
    }

}