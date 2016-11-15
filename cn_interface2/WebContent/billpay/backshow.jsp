<%@page contentType="text/html; charset=utf-8" language="java"%>
<%
String resule=(String)request.getParameter("resule").trim();
String code=new String((request.getParameter("code")).getBytes("ISO-8859-1"),"UTF-8");


%>
<!doctype html public "-//w3c//dtd html 4.0 transitional//en" >
<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<html>
	<head>
		<title>快钱支付结果</title>
		<meta http-equiv="content-type" content="text/html; charset=utf-8" >
	</head>
	
<BODY>
	
	<div align="center">
	<%
		if(resule.equals("Y"))
		{
			out.print("退款成功");
		}else if(resule.equals("N"))
		{
			out.print("退款失败!错误原因："+code.replace("null",""));
		}
	%>
	</div>

</BODY>
</HTML>