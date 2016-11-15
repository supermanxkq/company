package com.ccservice.b2b2c.util;

import java.util.Properties;

import javax.jms.JMSException;
 
import com.aliyun.openservices.ons.api.Message; 
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult; 

public class ALiMQUtil {
	
	private static Producer producer; 
	private final static Object SYCN_PRODUCER_OBJECT = new Object();
	//测试
	public static void main(String[] args) {
		
           
	}
    
	public static void sendMessage(String sendBody, String topicName,
			String producerId, String accessKey, String secretKey,
			String tagName, String keyOrderId) throws JMSException {

		if (producer == null || producer.isClosed()) {
			synchronized (SYCN_PRODUCER_OBJECT) {
				if (producer == null || producer.isClosed()) {
					Properties properties = new Properties();
					properties.put(PropertyKeyConst.ProducerId, producerId);// 您在控制台创建的Producer
					// ID
					properties.put(PropertyKeyConst.AccessKey, accessKey);// AccessKey
					// 阿里云身份验证，在阿里云服务器管理控制台创建
					properties.put(PropertyKeyConst.SecretKey, secretKey);// SecretKey
					// 阿里云身份验证，在阿里云服务器管理控制台创建
					producer = ONSFactory.createProducer(properties);
					producer.start(); // 在发送消息前，必须调用start方法来启动Producer，只需调用一次即可。
				}
			}
		} 
		Message msg = new Message( //
				// Message Topic
				topicName,
				// Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
				tagName,
				// Message Body 可以是任何二进制形式的数据， MQ不做任何干预，
				// 需要Producer与Consumer协商好一致的序列化和反序列化方式
				sendBody.getBytes());
		// 设置代表消息的业务关键属性，请尽可能全局唯一。
		// 以方便您在无法正常收到消息情况下，可通过MQ控制台查询消息并补发。
		// 注意：不设置也不会影响消息正常收发
		msg.setKey(keyOrderId);
		// 发送消息，只要不抛异常就是成功
		producer.send(msg);
		// 打印发送messageId
		// System.out.println(send);
		// producer.shutdown(); 不关

	}
}
