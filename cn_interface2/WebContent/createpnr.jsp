<%@page import="com.ccservice.b2b2c.atom.interticket.HttpClient"%>
<%@ page contentType="text/html; charset=gbk"%>
<%
String url = "http://service2.travel-data.cn/ToolsService.asmx/GetPNR?"+request.getQueryString();		
String strReturn=HttpClient.httpget(url,"utf-8").toString();
out.print(strReturn);
%>