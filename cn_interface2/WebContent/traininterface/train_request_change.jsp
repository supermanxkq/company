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
	<form action="train_request_change.html" method="post">
		<table align="center">
			<tr>
				<td>
					<div align="right">订单号：</div>
				</td>
				<td><input type="text" id="orderid" name="orderid" /></td>
			</tr>
			<tr>
				<td>
					<div align="right">交易单号：</div>
				</td>
				<td><input type="text" id="transactionid" name="transactionid" />
				</td>
			</tr>
			<tr>
				<td>
					<div align="right">取票单号：</div>
				</td>
				<td><input type="text" id="ordernumber" name="ordernumber" />
				</td>
			</tr>
			<tr>
				<td>
					<div align="right">改签新车票的车次：</div>
				</td>
				<td><input type="text" id="change_checi" name="change_checi" />
				</td>
			</tr>
			<tr>
				<td>
					<div align="right">改签新车票出发时间：</div>
				</td>
				<td><input type="text" id="change_datetime"
					name="change_datetime" />2014-05-30 17:32:00</td>
			</tr>
			<tr>
				<td>
					<div align="right">改签新车票的座位席别编码：</div>
				</td>
				<td>
				<select id="change_zwcode" name="change_zwcode">
						<option value="9" name="9">商务座</option>
						<option value="P" name="P">特等座</option>
						<option value="M" name="M">一等座</option>
						<option value="O" name="O">二等座</option>
						<option value="6" name="6">高级软卧</option>
						<option value="4" name="4">软卧</option>
						<option value="3" name="3">硬卧</option>
						<option value="2" name="2">软座</option>
						<option value="1" name="1">硬座</option>
				</select></td>
			</tr>
			<tr>
				<td>
					<div align="right">原票的座位席别编码：</div>
				</td>
				<td><select id="old_zwcode" name="old_zwcode">
						<option value="9" name="9">商务座</option>
						<option value="P" name="P">特等座</option>
						<option value="M" name="M">一等座</option>
						<option value="O" name="O">二等座</option>
						<option value="6" name="6">高级软卧</option>
						<option value="4" name="4">软卧</option>
						<option value="3" name="3">硬卧</option>
						<option value="2" name="2">软座</option>
						<option value="1" name="1">硬座</option>
				</select></td>
			</tr>
			<tr>
				<td>
					<div align="right">乘客姓名：</div>
				</td>
				<td><input type="text" id="passengersename"
					name="passengersename" /></td>
			</tr>
			<tr>
				<td>
					<div align="right">证件类型：</div>
				</td>
				<td><select id="passporttypeseid" name="passporttypeseid">
						<option value="1" name="1">二代身份证</option>
						<option value="3" name="3">护照</option>
						<option value="4" name="4">港澳通行证</option>
						<option value="5" name="5">台湾通行证</option>
				</select></td>
			</tr>
			<tr>
				<td>
					<div align="right">证件号：</div>
				</td>
				<td><input type="text" id="passportseno" name="passportseno" />
				</td>
			</tr>
			<tr>
				<td>
					<div align="right">票种类别：</div>
				</td>
				<td>
				<select id="piaotype" name="piaotype">
						<option value="1">成人票</option>
						<option value="2">儿童票</option>
						<option value="3">学生票</option>
						<option value="4">残军票</option>
				</select></td>
			</tr>
			<tr>
				<td>
					<div align="right">原车票票号：</div>
				</td>
				<td><input type="text" id="old_ticket_no" name="old_ticket_no" />
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<div align="center">
						<input type="submit" value="请求改签">
					</div>
				</td>
			</tr>

		</table>
	</form>
</body>

</html>