<%@ page import="com.ccservice.b2b2c.atom.interticket.HttpClient"%>
<%@ page contentType="text/html; charset=gbk"%>

<%
//String test=request.getParameter("test");
String test=request.getQueryString();
if(test.indexOf("test=")!=-1){
	
test=test.replaceFirst("test=","");
}
	String url ="http://service2.travel-data.cn/service.asmx/"+test;	
	String strReturn=HttpClient.httpget(url,"utf-8").toString();
	out.print(strReturn);
%>
