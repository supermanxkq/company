<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="GBK"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=GBK">
<title>֧������ʱ֧��</title>
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
					֧��������</th>
			<tr>
				<th colspan="2" scope="col"
					style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					����ȷ��</th>
			</tr>
			<tr>
				<td colspan="2" height="2" bgcolor="#ff7300"
					style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana"></td>
			</tr>

			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					�����ţ� <%
					com.alipay.util.UtilDate date = new com.alipay.util.UtilDate(); //��ȡ֧�������������ɶ�����
					String get_order = com.alipay.util.UtilDate.getOrderNum("");
				%>

				</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="get_order" value="<%=get_order%>" />
				</td>
			</tr>

			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					��Ʒ���ƣ�</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="subject" value="��Ʊ֧��-�����ţ�334446666" />
				</td>
			</tr>
			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					��Ʒ������</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="body" value="��Ʊ����" />
				</td>
			</tr>

			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					�����ܽ�</td>
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
					�Ƹ�ͨ����</th>
			<tr>
				<th colspan="2" scope="col"
					style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					����ȷ��</th>
			</tr>
			<tr>
				<td colspan="2" height="2" bgcolor="#ff7300"
					style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana"></td>
			</tr>

			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					�����ţ� <%
					//��ǰʱ�� yyyyMMddHHmmss
					String currTime = com.tenpay.util.TenpayUtil.getCurrTime();

					//8λ����
					String strDate = currTime.substring(0, 8);

					//6λʱ��
					String strTime = currTime.substring(8, currTime.length());

					//��λ�����
					String strRandom = com.tenpay.util.TenpayUtil.buildRandom(4) + "";

					//10λ���к�,�������е�����
					String strReq = strTime + strRandom;
				%>

				</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="bargainor_id" value="<%=strReq%>" />
				</td>
			</tr>

			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					��Ʒ���ƣ�</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="desc" value="��Ʊ֧��-�����ţ�334446666" />
				</td>
			</tr>


			<tr>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					�����ܽ�</td>
				<td style="FONT-SIZE: 14px; COLOR: #FF6600; FONT-FAMILY: Verdana">
					<input type="text" name="total_fee" value="1" />
				</td>
				<td></td>
			</tr>



			<tr>
				<td></td>
				<td><a onclick="tenpaysubmit.submit()">�Ƹ�֧ͨ��</a></td>
			</tr>

		</table>
	</form>

</body>
</html>
