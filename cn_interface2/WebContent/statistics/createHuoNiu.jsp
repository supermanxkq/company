<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="webwork" prefix="ww"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>火牛火车票统计</title>
</head>
<body>
	<table>
		<tr>
			<td><iframe style="width:160%;height:200%" src="http://localhost:19007/cn_interface/statistics/createBespeakOrder_linegraph.jsp" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="no" allowtransparency="yes"></iframe></td>
			<td><iframe style="width:160%;height:200%" src="http://localhost:19007/cn_interface/statistics/createBespeakOrder_linegraphHuoNiuTow.jsp" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="no" allowtransparency="yes"></iframe></td>
		</tr>
		<tr>
			<td><iframe style="width:180%;height:200%" src="http://localhost:19007/cn_interface/statistics/createBespeakOrder_linegraphHuoNiuThree.jsp" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="no" allowtransparency="yes"></iframe></td>
			<td><iframe style="width:150%;height:200%" src="http://localhost:19007/cn_interface/statistics/createBespeakOrder_linegraphHuoNiuFour.jsp" frameborder="no" border="0" marginwidth="0" marginheight="0" scrolling="no" allowtransparency="yes"></iframe></td>
		</tr>
	</table>
	
</body>
</html>