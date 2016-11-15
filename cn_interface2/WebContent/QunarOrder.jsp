<%@ page import="com.ccservice.b2b2c.atom.interticket.HttpClient"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.*" %>
<%@ page contentType="text/html; charset=utf-8"%>
<%@ page language="java" import="java.util.*" pageEncoding="gbk"%>

<%
	
	String str = request.getParameter("api");
	String test = request.getQueryString();

	test=new String(test.getBytes("ISO-8859-1"),"gbk");
	String strindex = "api=" + str + "&";
	if (test.indexOf(strindex) != -1) {
		test = test.replaceFirst(strindex, "");
	}
//	if(str.equals("otaQueryOrder")){
			String url = "http://y2dw.trade.test.qunar.com/api/ota/" + str + "?"
				+ test;
		String strReturn = HttpClient.httpget(url, "utf-8").toString();
		System.out.println("****************************START**************************************");
		System.out.println("请求URL:"+url);
		out.print(strReturn);
		System.out.println("返回结果:"+strReturn);
		System.out.println("****************************END**************************************");
//	}else if(str.equals("otaOpt")){
//		String url = "http://y2dw.trade.test.qunar.com/api/ota/" + str + "?"
//				+ test;
//		String strReturn = HttpClient.httpget(url, "utf-8").toString();
//		out.print(strReturn);
//	}
%>
