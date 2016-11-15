<%@page contentType="text/html; charset=gb2312" language="java"%>
<%@taglib uri="webwork" prefix="ww" %>

<!doctype html public "-//w3c//dtd html 4.0 transitional//en" >
<html>
	<head>
		<title>${dns.companyname}商旅中心 支付结果</title>
		<meta http-equiv="content-type" content="text/html; charset=gb2312" >

		<script type="text/javascript">
		function colsewinow(){
		this.window.opener=null;
		window.close();
		}
		</script>
	</head>
	
<BODY>
	
	<div align="center">
	<%
	String payresultstr=request.getParameter("payresult");
	int payresult=Integer.valueOf(payresultstr);
	%>
	
	<%if(payresult==1){ %>
	<img src="../images/icon_04.jpg" style="vertical-align:middle; "><b>&nbsp;&nbsp;订单支付成功!</b>	
	<%} %>
	<%if(payresult==2){ %>
	<img src="../images/png-0495.png" style="vertical-align:middle; "><b>&nbsp;&nbsp;订单支付失败!</b>	
	<%} %>
	<%if(payresult==3){ %>
	<img src="../images/png-0495.png" style="vertical-align:middle; "><b>&nbsp;&nbsp;订单支付异常!</b>	
	<%}%>
  <a onclick="colsewinow()" href="#">[关闭页面]</a>
	</div>
</BODY>
</HTML>