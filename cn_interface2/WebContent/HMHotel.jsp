<%@page import="com.ccservice.b2b2c.atom.interticket.HttpClient"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.*" %>
<%@ page contentType="text/html; charset=gbk"%>
<%@page pageEncoding="utf-8"%>

<%
	String str = request.getParameter("api");
	String test = request.getQueryString();
	String strindex = "api=" + str + "&";
	if (test.indexOf(strindex) != -1) {
		test = test.replaceFirst(strindex, "");
	}
	if(str.equals("qbooking")||str.equals("qbookingstatus")||str.equals("qbookingcancel")){
		String xml=request.getParameter("xml");
		String ReturnMsg = HttpUtil.postSend(xml,str);
		out.print(ReturnMsg);
	}else{
		String url = "http://api.huamin.com.hk/api/xml_" + str + ".php?"
				+ test;
		String strReturn = HttpClient.httpget(url, "utf-8").toString();
		out.print(strReturn);
	}
%>
