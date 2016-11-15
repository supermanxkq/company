package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.util.Payment;
import com.alipay.util.UtilDate;
import com.pay.config.AlipayConfig;

/**
 * Servlet implementation class for Servlet: Alipay
 *
 */
 public class Alipayqian extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Alipayqian() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//创建订单接口
		UtilDate date = new UtilDate();//生成客户网站订单号，测试接口使用的系统时间
		//****************************************
		String paygateway = "https://www.alipay.com/cooperate/gateway.do?"; //支付接口（不可修改）
		String service = "sign_protocol_with_partner";    //服务名称--签署电子协议接口（不可修改）
		String sign_type = "MD5"; //签名方式（不可修改）
		String input_charset = AlipayConfig.getInstance().getCharSet();  //（不可修改）
		//*****************************************************
		//partner和key提取方法：登陆签约支付宝账户--->点击“商家服务”就可以看到
		String partner = AlipayConfig.getInstance().getPartnerID(); //partner合作伙伴ID(必填)	
		String key = AlipayConfig.getInstance().getKey(); //partner账户对应的支付宝安全校验码(必填)

		    
		String email = "420189155@qq.com";   //签约的支付宝账户

		String ItemUrl = Payment.CreateUrlqian(paygateway, input_charset,
				service, partner, sign_type, email, key);
		response.sendRedirect(ItemUrl);
		
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}   	  	    
}