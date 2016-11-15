<%@page import="com.ccservice.b2b2c.atom.interticket.HttpClient"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.*" %>
<%@ page contentType="text/html; charset=gbk"%>

<%
	String str = request.getParameter("api");
	String test = request.getQueryString();
	String strindex = "api=" + str + "&";
	if (test.indexOf(strindex) != -1) {
		test = test.replaceFirst(strindex, "");
	}
	if(str.equals("otaopt")){
		String url = "http://y2dx.trade.test.qunar.com/api/ota/otaOpt?"+ test;
		String strReturn = HttpClient.httpget(url, "utf-8").toString();
		out.print(strReturn);
	}else{
		String url = "http://y2dx.trade.test.qunar.com/api/ota/otaQueryOrder?"+ test;
		String strReturn = HttpClient.httpget(url, "utf-8").toString();
		out.print(strReturn);
	}
%>
