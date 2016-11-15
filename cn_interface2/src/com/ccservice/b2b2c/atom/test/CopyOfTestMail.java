package com.ccservice.b2b2c.atom.test;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.service.IMemberService;
import com.ccservice.b2b2c.base.util.Util;

public class CopyOfTestMail {

	/**
	 * @param args
	 * @throws MalformedURLException 
	 */
	public static void main(String[] args) throws MalformedURLException {
		// TODO Auto-generated method stub
		String url = "http://localhost:8080/sj_service/service/" ;

		HessianProxyFactory factory = new HessianProxyFactory();
		IMemberService servier = (IMemberService) factory.create(IMemberService.class,
				url + IMemberService.class.getSimpleName());

		List<Customeruser> list=servier.findAllCustomeruser("", "", -1, 0);
		for(Customeruser customeruser:list)
		{
			try {
				customeruser.setLogpassword(Util.MD5(customeruser.getLogpassword()));
				servier.updateCustomeruserIgnoreNull(customeruser);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("发送成功！");
	}

}
