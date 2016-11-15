<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="GBK"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<body style="text-align: center;">
	<%if(request.getParameter("msg").equals("success")){ %>
	您的订单已支付成功！
	<%}else{%>
		您的订单已支付失败！
	<% } %>
	</body>
</html>
