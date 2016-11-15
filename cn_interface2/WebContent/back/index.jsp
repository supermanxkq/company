<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="GBK"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>支付宝即时支付</title>
<style type="text/css">
<!--
.style1 {
	color: #FF0000
}
-->
</style>
</head>
<body>
	<form name="alipaysubmit" method="get" action="Alipay">
		<table width="30%" border="0" align="center">
			<tr>
				<th colspan="2" align="left"
					style="FONT-SIZE: 14px; COLOR: #CC000; FONT-FAMILY: Verdana">
					支付宝测试</th>
			<tr>
				<th colspan="2" scope="col"
					style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					订单确认</th>
			</tr>
			<tr>
				<td colspan="2" height="2" bgcolor="#ff7300"
					style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana"></td>
			</tr>

			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					订单号： <%
					com.alipay.util.UtilDate date = new com.alipay.util.UtilDate(); //调取支付宝工具类生成订单号
					String get_order = com.alipay.util.UtilDate.getOrderNum("");
				%>

				</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="get_order" value="<%=get_order%>" />
				</td>
			</tr>

			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					产品名称：</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="subject" value="机票支付-订单号：334446666" />
				</td>
			</tr>
			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					产品描述：</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="body" value="机票服务" />
				</td>
			</tr>

			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					付款总金额：</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="total_fee" value="0.01" />
				</td>
				<td></td>
			</tr>



			<tr>
				<td></td>
				<td><img src="img/alipay.gif"
					onClick='document.alipaysubmit.submit()' /></td>
			</tr>

		</table>
	</form>



	<hr>

	<form name="tenpaysubmit" method="get" action="Tenpay">
		<table width="30%" border="0" align="center">
			<tr>
				<th colspan="2" align="left"
					style="FONT-SIZE: 14px; COLOR: #CC000; FONT-FAMILY: Verdana">
					财付通测试</th>
			<tr>
				<th colspan="2" scope="col"
					style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					订单确认</th>
			</tr>
			<tr>
				<td colspan="2" height="2" bgcolor="#ff7300"
					style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana"></td>
			</tr>

			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					订单号： <%
					//当前时间 yyyyMMddHHmmss
					String currTime = com.tenpay.util.TenpayUtil.getCurrTime();

					//8位日期
					String strDate = currTime.substring(0, 8);

					//6位时间
					String strTime = currTime.substring(8, currTime.length());

					//四位随机数
					String strRandom = com.tenpay.util.TenpayUtil.buildRandom(4) + "";

					//10位序列号,可以自行调整。
					String strReq = strTime + strRandom;
				%>

				</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="bargainor_id" value="<%=strReq%>" />
				</td>
			</tr>

			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					产品名称：</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="desc" value="机票支付-订单号：334446666" />
				</td>
			</tr>


			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					付款总金额：</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="total_fee" value="1" />
				</td>
				<td></td>
			</tr>



			<tr>
				<td></td>
				<td><a onclick="tenpaysubmit.submit()">财付通支付</a></td>
			</tr>

		</table>
	</form>

</body>
</html>
