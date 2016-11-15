package com.ccservice.b2b2c.atom.test;

import java.net.MalformedURLException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.service.IAtomService;

public class TestMail {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		// TODO Auto-generated method stub
		String url = "http://localhost:8080/cn_interface/service/" ;
		HessianProxyFactory factory = new HessianProxyFactory();
		IAtomService servier = (IAtomService) factory.create(IAtomService.class,
				url + IAtomService.class.getSimpleName());
		//servier.sendSimpleMails(new String[]{"2230567110@qq.com"}, "你好", "你好");
		servier.sendHTMLMails(new String[]{"2230567110@qq.com"}, "test mail", "<table border='1'><tr><td>贾建磊贾建磊贾建磊贾建磊贾建磊</td></tr></table>");
		//servier.sendAttachmentMails(new String[]{"sendHTMLMails"}, "你好", "<h1>你好</h1>",new String[]{"D:\\airsearch.txt"});
		System.out.println("发送成功！");
	}

}
