package com.ccservice.b2b2c.atom.service12306.onlineRefund.mq.util;

import java.util.List;
import javax.jms.Session;
import java.util.ArrayList;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.ConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.ActiveMQConnectionFactory;
import com.ccservice.b2b2c.atom.service12306.onlineRefund.mq.listener.ChangeReturnTicketMessageListener;

/**
 * 退票MQ工具类
 * @author WH
 * @time 2016年11月1日 下午5:14:34
 * @version 1.0
 */

public class ChangeReturnTicketMQUtil {

    /**
     * 锁
     */
    private static Object MqLocker = new Object();

    /**
     * 消费者的List
     */
    private static List<MessageConsumer> MqList = new ArrayList<MessageConsumer>();

    /**
     * 关闭消费者
     * @param closeCosumerCount 关闭的消费者数量
     */
    public static int closeMessageConsumer(int closeCosumerCount) throws Exception {
        //循环
        for (int i = 0; i < closeCosumerCount; i++) {
            //加锁
            synchronized (MqLocker) {
                try {
                    //遍历消费者
                    for (int j = 0; j < MqList.size(); j++) {
                        //取值
                        MessageConsumer consumer = MqList.get(j);
                        //退票监听
                        ChangeReturnTicketMessageListener listener = (ChangeReturnTicketMessageListener) consumer
                                .getMessageListener();
                        //非使用中
                        if (!listener.isUse()) {
                            //关闭
                            consumer.close();
                            //移除
                            MqList.remove(consumer);
                            //中断
                            break;
                        }
                    }
                }
                catch (Exception e) {

                }
            }
        }
        //返回
        return MqList.size();
    }

    /**
     * 增加消费者
     * @param MqAddress MQ地址
     * @param MqName MQ名称
     * @param addCosumerCount 消费者个数
     */
    public static int addMessageConsumer(String MqAddress, String MqName, int addCosumerCount) throws Exception {
        //正确
        if (addCosumerCount > 0) {
            //连接
            ConnectionFactory cf = new ActiveMQConnectionFactory(MqAddress);
            Connection conn = cf.createConnection();
            Destination destination = new ActiveMQQueue(MqName);
            //循环
            for (int i = 0; i < addCosumerCount; i++) {
                //加锁
                synchronized (MqLocker) {
                    //SESSION
                    Session session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
                    //CREATE
                    MessageConsumer consumer = session.createConsumer(destination);
                    //SET
                    consumer.setMessageListener(new ChangeReturnTicketMessageListener());
                    //ADD
                    MqList.add(consumer);
                }
            }
            //启动
            conn.start();
        }
        //返回
        return MqList.size();
    }
}