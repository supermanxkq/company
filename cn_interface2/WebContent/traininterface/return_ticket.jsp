<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html>
<html>

<head>
<script src="../js/jquery.min.js"></script>
<script>
	function a(id1){
		var id=$("#id").val();
		id = parseInt(id);
		id += id1;
		$("#id").val(id);
		if(id<=5){
		var html='<tr><td colspan="2"><div align="center">第'+id+'位乘客</div></td></tr>'
				+'<tr><td><div align="right">乘客姓名：</div></td><td><input type="text" id="name'+id+'" name="name'+id+'" /></td></tr>'
				+'<tr><td><div align="right">证件类型：</div></td><td><select id="idtype'+id+'" name="idtype'+id+'"><option value="1">二代身份证</option>'
				+'<option value="3">护照</option><option value="4">港澳通行证</option><option value="5">台湾通行证</option></select></td></tr>'
				+'<tr><td><div align="right">证件号：</div></td><td><input type="text" id="idnumber'+id+'" name="idnumber'+id+'" /></td></tr>'
				+'<tr><td><div align="right">座位：</div></td><td><select id="seattype'+id+'" name="seattype'+id+'"><option value="9">商务座</option>'
				+'<option value="P">特等座</option><option value="M">一等座</option><option value="O">二等座</option><option value="6">高级软卧</option>'
				+'<option value="4">软卧</option><option value="3">硬卧</option><option value="2">软座</option><option value="1">硬座</option>'
				+'</select></td></tr><tr><td><div align="right">价格：</div></td><td><input type="text" id="price'+id+'" name="price'+id+'" /></td></tr>'
				+'<tr><td><div align="right">票号：</div></td><td><input type="text" id="ticketno'+id+'" name="ticketno'+id+'" /></td></tr><tr><td>'
				+'<div align="right">乘客类型：</div></td><td><select id="tickettype'+id+'" name="tickettype'+id+'"><option value="1">成人票</option><option value="2">儿童票</option>'
				+'<option value="3">学生票</option><option value="4">残军票</option></select></td></tr>';
		b(html);
		}
		function b(html){
			var html1 = $("#Passenger").html();
			html1 += html;
			var html1=$("#Passenger").html(html1)
		}
	}
</script>
<meta charset="utf-8" />
<title></title>
</head>

<body>
	<form action="return_ticket.html" method="post">
	<input type="hidden" id="id" value="0" name="id">
		<table align="center" id="Passenger">
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
				<td><input type="text" id="transactionid" name="transactionid" /></td>
			</tr>
			<tr>
				<td>
					<div align="right">取票单号：</div>
				</td>
				<td><input type="text" id="ordernumber"
					name="ordernumber" /></td>
			</tr>
			<tr>
				<td>
					<div align="right">请求特征(唯一)：</div>
				</td>
				<td><input type="text" id="reqtoken"
					name="reqtoken" /></td>
			</tr>
			<tr>
				<td>
					<div align="right">异步通知接口回调地址：</div>
				</td>
				<td><input type="text" id="callbackurl"
					name="callbackurl" /></td>
			</tr>
			<tr>
				<td colspan="2">
					<div align="center">
						<input onclick="parent.onclick()" id="addPassenger" type="button" value="添加乘客">
					</div>
				</td>
			</tr>
			</table>
			<table align="center">
			<tr>
				<td colspan="2">
					<div align="center">
						<input type="submit" value="确认退票">
					</div>
				</td>
			</tr>
		</table>
	</form>
</body>

</html>