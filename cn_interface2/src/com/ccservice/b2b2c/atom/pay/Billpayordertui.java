package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bill.encrypt.MD5Util;

/**
 * 订单退款接口
 *
 */
 public class Billpayordertui extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 2222221L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Billpayordertui() {
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
		String merchant_id="10003000799";	

		//客户编号所对应的密钥。。在账户邮箱中获取
		String merchant_key ="7SZJLMDFU8LNEETH";

		//退款接口版本号   固定值：bill_drawback_api_1
		String version="bill_drawback_api_1";

		//操作类型  固定值：001  001代表下订单请求退款
		String command_type="001";	

		//退款流水号
		String txOrder=request.getParameter("ordercode");

		//退款金额   整数或小数，小数位为两位 以人民币元为单位。
		String amount=request.getParameter("amount");

		//退款提交时间  数字串，一共14位
		//格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		//例如：20071117020101
		String postdate=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

		//原商户订单号  字符串，与用户支付时的订单号相同
		//只允许使用字母、数字、- 、_,并以字母或数字开头	
		String orderid=request.getParameter("ordercode");
		new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());	
			
        int intreturn=0;
        String strout="";
        
		//生成加密签名串
		///请务必按照如下顺序和规则组成加密串！
		String macstr="";
		macstr=appendParam(macstr,"merchant_id",merchant_id);
		macstr=appendParam(macstr,"version",version);
		macstr=appendParam(macstr,"command_type",command_type);
		macstr=appendParam(macstr,"orderid",orderid);
		macstr=appendParam(macstr,"amount",amount);
		macstr=appendParam(macstr,"postdate",postdate);
		macstr=appendParam(macstr,"txOrder",txOrder);
		macstr=appendParam(macstr,"merchant_key",merchant_key);

	    String mac=MD5Util.md5Hex(macstr.getBytes("gb2312")).toUpperCase();
	    
	    //拼接传递参数
	    String param="";
	    param=appendParam2(param,"merchant_id",merchant_id);
	    param=appendParam2(param,"version",version);
	    param=appendParam2(param,"command_type",command_type);
	    param=appendParam2(param,"orderid",orderid);
	    param=appendParam2(param,"amount",amount);
	    param=appendParam2(param,"postdate",postdate);
	    param=appendParam2(param,"txOrder",txOrder);
	    param=appendParam2(param,"merchant_key",merchant_key);
	    
	    String parameter="https://www.99bill.com/webapp/receiveDrawbackAction.do?"+param+"&mac="+mac;
		System.out.println(parameter);
		//response.sendRedirect(parameter);
		
		try {
			String url = parameter;
			java.net.URL Url = new java.net.URL(url);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
			
			conn.setDoInput(true);
			conn.connect();
			
			java.io.InputStream in = conn.getInputStream();
			
			org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
			org.jdom.Document doc = build.build(in);
			org.jdom.Element data = doc.getRootElement();
			String status = data.getChildTextTrim("RESULT");
			
			if(status.equals("Y")){
				//还款成功
				intreturn =1;
				response.sendRedirect("../lthk_home/orderinfo!tuikuansuccess.action?strTuiOrderID="+txOrder);
			}else
			{
				intreturn =0;
				//response.sendRedirect("../lthk_home/orderinfo!tuikuanshibai.action?strTuiOrderID="+txOrder);
				response.sendRedirect("../lthk_home/orderinfo!tuikuansuccess.action?strTuiOrderID="+txOrder);
			}
			//打印返回结果
			System.out.println(data.getChildTextTrim("CODE"));
			
			
			in.close();
			conn.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
			strout="还款失败，请重试！";
		}
		//response.getOutputStream().print(strout);
		
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
					returnStr=returnStr+paramId+"="+paramValue;
				}
			}
			else
			{
				returnStr=paramId+"="+paramValue;
			}	
			return returnStr;
	}
	public String appendParam2(String returnStr,String paramId,String paramValue)
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
				returnStr=paramId+"="+paramValue;
			}	
			return returnStr;
	}
	//功能函数。将变量值不为空的参数组成字符串。结束
}