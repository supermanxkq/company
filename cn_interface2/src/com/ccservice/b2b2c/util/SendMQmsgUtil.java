package com.ccservice.b2b2c.util;

import javax.jms.JMSException;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * <p>发送MQ消息</P>
 * @author zhangqifei
 * @time 2016年8月11日 下午2:38:54
 */
public class SendMQmsgUtil {

    /**
    * 根据配置文件判断使用什么技术类型
    * @param sendBody
    * @param name
    * @throws JMSException
    */
    public static void sendGetUrlMQmsg(String sendBody) throws JMSException {
        String QUEUE_NAME = PropertyUtil.getValue("QueueMQ_trainorder_waitorder_orderid", "Train.properties");
        String type = PropertyUtil.getValue("type", "Train.properties");
        int temp = Integer.parseInt(type);
        if (temp == 0) {
            sendAvtiveMQmsg(sendBody, QUEUE_NAME);
        }
        else if (temp == 1) {
            QUEUE_NAME = PropertyUtil.getValue("topicName", "Train.properties");
            sendALiMQmsg(sendBody, QUEUE_NAME);
        }
        else {
            return;
        }
    }

    /**
     * 发送avtivemq
     * @param sendBody
     * @param mqName
     * @throws JMSException
     */
    public static void sendAvtiveMQmsg(String sendBody, String mqName) throws JMSException {
        String url = PropertyUtil.getValue("activeMQ_url", "Train.properties");
        sendAvtiveMQmsg(sendBody, url, mqName);
    }

    /**
     * 发送avtivemq
     * @param sendBody
     * @param mqUrl
     * @param mqName
     * @throws JMSException
     */
    public static void sendAvtiveMQmsg(String sendBody, String mqUrl, String mqName) throws JMSException {
        WriteLog.write("TongchengSupplyMethodMqMSGUtil_sendActiveMQmsg", "mqname--->" + mqName + ":mqUrl--->" + mqUrl
                + ":sendBody--->" + sendBody);
        ActiveMQUtil.sendMessage(mqUrl, mqName, sendBody);
    }

    /**
     * 发送阿里云mq
     * @param sendBody
     * @param topicName
     * @throws JMSException 
     */
    public static void sendALiMQmsg(String sendBody, String topicName) throws JMSException {
        try {
            ALiMQMethodPool.getinstance().sendMQ(sendBody, topicName);
        }
        catch (Exception e) {
            WriteLog.write("TongchengSupplyMethodMqMSGUtil_sendALiMQmsg_Exception_1", sendBody);
            ExceptionUtil.writelogByException("TongchengSupplyMethodMqMSGUtil_sendALiMQmsg_Exception_1", e);
            try {
                ALiMQMethodPool.getinstance().sendMQ(sendBody, topicName);
            }
            catch (Exception e1) {
                WriteLog.write("TongchengSupplyMethodMqMSGUtil_sendALiMQmsg_Exception_2", sendBody);
                ExceptionUtil.writelogByException("TongchengSupplyMethodMqMSGUtil_sendALiMQmsg_Exception_2", e);
            }
        }
    }

}
