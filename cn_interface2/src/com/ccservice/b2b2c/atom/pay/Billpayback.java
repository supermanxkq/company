package com.ccservice.b2b2c.atom.pay;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.bill.encrypt.MD5Util;
import com.pay.config.BillpayConfig;

/**
 * Servlet implementation class for Servlet: Billpay
 *
 */
 public class Billpayback extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 2222221L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Billpayback() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		/**
		 * @Description: 快钱人民币支付网关接口范例
		 * @Copyright (c) 上海快钱信息服务有限公司
		 * @version 2.0
		 */
		//商户编号
		//对于快钱新系统注册商户，该值为商户在快钱的会员号；对于快钱老系统注册商户，该值为商户的商户编号。
		String merchant_id="";//BillpayConfig.getInstance().getPartnerIDback();

		//客户编号所对应的密钥。。在账户邮箱中获取
		String merchant_key ="";//BillpayConfig.getInstance().getKeyback();

		//退款接口版本号   固定值：bill_drawback_api_1
		String version="bill_drawback_api_1";

		//操作类型  固定值：001  001代表下订单请求退款
		String command_type="001";	

		//退款流水号
		String txOrder=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

		//退款金额   整数或小数，小数位为两位 以人民币元为单位。
		String amount=new String(request.getParameter("backmoney").getBytes(),"UTF-8");

		//退款提交时间  数字串，一共14位
		//格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		//例如：20071117020101
		String postdate=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

		//原商户订单号  字符串，与用户支付时的订单号相同
		//只允许使用字母、数字、- 、_,并以字母或数字开头	
		String orderid=new String(request.getParameter("oldordercode").getBytes(),"UTF-8");

			//生成加密签名串
			///请务必按照如下顺序和规则组成加密串！
			String macstr="";
			macstr=appendParam2(macstr,"merchant_id",merchant_id);
			macstr=appendParam2(macstr,"version",version);
			macstr=appendParam2(macstr,"command_type",command_type);
			macstr=appendParam2(macstr,"orderid",orderid);
			macstr=appendParam2(macstr,"amount",amount);
			macstr=appendParam2(macstr,"postdate",postdate);
			macstr=appendParam2(macstr,"txOrder",txOrder);
			macstr=appendParam2(macstr,"merchant_key",merchant_key);
			
			String signMsgValstr="";
			signMsgValstr=appendParam(signMsgValstr,"merchant_id",merchant_id);
			signMsgValstr=appendParam(signMsgValstr,"version",version);
			signMsgValstr=appendParam(signMsgValstr,"command_type",command_type);
			signMsgValstr=appendParam(signMsgValstr,"orderid",orderid);
			signMsgValstr=appendParam(signMsgValstr,"amount",amount);
			signMsgValstr=appendParam(signMsgValstr,"postdate",postdate);
			signMsgValstr=appendParam(signMsgValstr,"txOrder",txOrder);

			String mac=MD5Util.md5Hex(macstr.getBytes("utf-8")).toUpperCase();
			String parameter="https://sandbox.99bill.com/webapp/receiveDrawbackAction.do?"+signMsgValstr+"&mac="+mac;
			System.out.println(parameter);
			String redirectuser="";//BillpayConfig.getInstance().getRedirecturl();
			String returnstr=httpget(parameter,"utf-8");
			SAXBuilder build = new SAXBuilder();
			Document document;
			try {
				document = build.build(new StringReader(returnstr));
				Element root = document.getRootElement();
				String merchant=root.getChildText("MERCHANT");
				String backorderid=root.getChildText("ORDERID");
				String txorder=root.getChildText("TXORDER");
				String backamount=root.getChildText("AMOUNT");
				String result=root.getChildText("RESULT");
				String code=root.getChildText("CODE");
				redirectuser+="?resule="+result+"&code="+java.net.URLEncoder.encode(code,"UTF-8");
			} catch (JDOMException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
			response.sendRedirect(redirectuser);
	        
	        
		
	}  	
	public static String httpget(String url,String encode){
		try {
			
			URL Url = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
			
			conn.setDoInput(true);
			conn.connect();
			
			InputStream in = conn.getInputStream();
			
			byte[] buf = new byte[2046];
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int len=0;
			int size=0;
			while((len=in.read(buf))>0){
				bout.write(buf,0,len);
				size+=len;
			}
			
			//System.out.println(new String(content,0,size));
			in.close();
			conn.disconnect();
			
			return new String(bout.toByteArray(),encode);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return null;
	}
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}   
	//功能函数。将变量值不为空的参数组成字符串
	public String appendParam(String returnStr,String paramId,String paramValue)
	{
			if(!returnStr.equals(""))
			{
				if(!paramValue.equals(""))
				{
					returnStr=returnStr+"&"+paramId+"="+paramValue;
				}
			}
			else
			{
				if(!paramValue.equals(""))
				{
				returnStr=paramId+"="+paramValue;
				}
			}	
			return returnStr;
	}
	
	//功能函数。将变量值不为空的参数组成字符串
	public String appendParam2(String returnStr,String paramId,String paramValue)
	{
			if(!returnStr.equals(""))
			{
				if(!paramValue.equals(""))
				{
					returnStr=returnStr+paramId+"="+paramValue;
				}
			}
			else
			{
				returnStr=paramId+"="+paramValue;
			}	
			return returnStr;
	}
	//功能函数。将变量值不为空的参数组成字符串。结束
}