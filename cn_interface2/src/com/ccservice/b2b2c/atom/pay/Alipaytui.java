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
 public class Alipaytui extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Alipaytui() {
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
		String service = "refund_fastpay_by_platform_nopwd";//服务名称--退款服务（不可修改）
		String sign_type = "MD5"; //签名方式（不可修改）
		String input_charset = AlipayConfig.getInstance().getCharSet();  //（不可修改）
		//*****************************************************
		//partner和key提取方法：登陆签约支付宝账户--->点击“商家服务”就可以看到
		String partner = AlipayConfig.getInstance().getPartnerID(); //partner合作伙伴ID(必填)	
		String key = AlipayConfig.getInstance().getKey(); //partner账户对应的支付宝安全校验码(必填)

		String path = request.getScheme() + "://" + request.getServerName()
				+ ":" + request.getServerPort();// "http://190.10.2.33:7001/"; 

		String notify_url = path + "/alipay_return_pay/Alipay_Notify.asp";//服务器通知url（Alipay_Notify.asp自己的服务所在绝对路经） 
		//======业务参数====
		String batch_no = date.getOrderNum("") + date.getThree(); //批次号规则 “日期”+“12位随机码”20060702001123456789
		String refund_date = "2010-09-27 18:00:00"; //退款日期，可以选择当天  yyyy-MM-dd hh:mm:ss
		String batch_num = "1";//退款总笔数		
/*
		退款详细数据:  交易退款信息$收费退款信息|分润退款信息|分润退款信息 
		例子(单笔数据集)：2009042770320839^0.03^交易退款|gwl25@163.com^^alipay-test01@alipay.com^^0.01^退分润|gwl06@163.com^^alipay-test01@alipay.com^^0.01^退分润 
		“退款详细数据”可以见开发文档
		*/      
		String detail_data = "2008011801009807^0.02^SUCCESS$alipay-test01@alipay.com^^0.01^SUCCESS|bianzhifu@gmail.com^^alipay-test01@alipay.com^^0.01^SUCCESS"; 
		

		String ItemUrl = Payment.CreateUrltui(paygateway, input_charset,
				service, partner, sign_type, batch_no, refund_date,
				batch_num, detail_data, notify_url, key);
		response.sendRedirect(ItemUrl);
		
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}   	  	    
}