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
	<form action="get_train_info.html" method="post">
		<table align="center">
			<tr>
				<td>
					<div align="right">乘车日期：</div>
				</td>
				<td><input type="text" id="train_date" name="train_date" />2015-01-01</td>
			</tr>
			<tr>
				<td>
					<div align="right">出发站简码：</div>
				</td>
				<td><input type="text" id="from_station" name="from_station" /><a
					href="https://kyfw.12306.cn/otn/resources/js/framework/station_name.js "
					target="view_window">各站简码查询(ctrl+f快捷查找)</a></td>
			</tr>
			<tr>
				<td>
					<div align="right">到达站简码：</div>
				</td>
				<td><input type="text" id="to_station" name="to_station" /></td>
			</tr>
			<tr>
				<td>
					<div align="right">车次内部编码：</div>
				</td>
				<td><input type="text" id="train_no" name="train_no" /></td>
			</tr>
			<tr>
				<td>
					<div align="right">车次号：</div>
				</td>
				<td><input type="text" id="train_code" name="train_code" /></td>
			</tr>
			<tr>
				<td colspan="2">
					<div align="center">
						<input type="submit" value="有价格查询">
					</div>
				</td>
			</tr>
		</table>
	</form>
</body>