<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<body style="text-align: center;">
	<%if(request.getParameter("message")!=null){ %>		
     <%=new String(request.getParameter("message").getBytes("ISO-8859-1"),"utf-8")%>
	<%}else{%>
		订单已支付失败，具体原因请联系管理员！		
	<% }%>
	
	</body>
</html>
