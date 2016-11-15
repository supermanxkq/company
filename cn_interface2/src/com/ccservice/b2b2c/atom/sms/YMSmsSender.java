package com.ccservice.b2b2c.atom.sms;

import java.net.MalformedURLException;
import java.sql.Timestamp;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.encoding.XMLType;

import org.apache.axis.client.Call;


import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.ymsend.Ymsend;
import org.apache.axis.client.Service;

public class YMSmsSender {
	/**
	 * 
	 * @param MCode
	 * @return
	 */
	public String getMobileSMS(String MCode){
		String Guid = "";
		String url = "http://external.dqc100.com:8090/InterfaceHost/SookShop.asmx";
		//在浏览器中打开url，可以找到SOAPAction: "http://www.chinsoft.com.cn/SendMQ"
		String namespace = "http://www.dqc100.com/";
		Service service = new Service();
			try {
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(url));
			//call.setUsername("xuan"); // 用户名（如果需要验证）
			//call.setPassword("123456"); // 密码
			call.setUseSOAPAction(true);
			call.setSOAPActionURI(namespace + "GetMobileVerCode"); // action uri
			call.setOperationName(new QName(namespace, "GetMobileVerCode"));// 设置要调用哪个方法
			// 设置参数名称，具体参照从浏览器中看到的
			call.addParameter(new QName(namespace, "accountName"), XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter(new QName(namespace, "accreditCode"), XMLType.XSD_STRING, ParameterMode.IN);
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING); // 要返回的数据类型
			//String sendTime = "2011-07-14 13:05:32";
			Object[] params = new Object[] {MCode, "www.dqcpay.com"};
			Guid= (String) call.invoke(params); //方法执行后的返回值
			System.out.println(Guid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Guid = "";
			System.out.println("发送手机短信验证码并返回GUID串....出现未知错误");
		} 
		if("".equals(Guid)){
			return "";
		}else{
			return Guid;
		}
		
	}

	
	/**
	 * 发送短信
	 * 
	 * @param mobiles
	 *            手机号码组
	 * @param content
	 *            短信内容
	 * 
	 * @return 返回发送结果
	 * @throws MalformedURLException 
	 */
	public int sendSMS(String[] mobiles, String content,long ordercode,String strUserID) {
		try{
			for(int i=0;i<mobiles.length;i++)
			{
				Ymsend ymsend=new Ymsend();
				ymsend.setContent(content);
				ymsend.setCreatetime(new Timestamp(System.currentTimeMillis()));
				ymsend.setOrdercode(ordercode);
				ymsend.setPhone(mobiles[i]);
				ymsend.setType(2);
				ymsend.setState(0);
				Server.getInstance().getMemberService().createYmsend(ymsend);
			}
			return 1;
		}catch(Exception e)
		{
			return -1;
		}
		
	}
}
