<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>

	<head>
		<meta charset="utf-8" />
		<title></title>
	</head>

	<body>
		<form action="train_cancel.html" method="post">
			<table align="center">
				<tr>
					<td>
						<div align="right">使用方订单号：</div>
					</td>
					<td>

						<input type="text" id="orderid" name="orderid" />
					</td>
				</tr>

				<tr>
					<td>
						<div align="right">交易单号：</div>
					</td>
					<td>

						<input type="text" id="transactionid" name="transactionid"/>
					</td>
				</tr>

				<tr>
					<td colspan="2">
						<div align="center">
							<input type="submit" value="取消订单">
						</div>
					</td>
				</tr>
			</table>
		</form>
	</body>

</html>