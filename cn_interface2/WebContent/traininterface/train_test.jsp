<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<script type="text/javascript" src="../js/jquery.easyui.min.js"></script>
<script>
	function onclick(){
		var myFrameId = document.getElementById('myFrameId');
		var win = myFrameId.window || myFrameId.contentWindow;
		var id=1;
		win.a(id);
	}
</script>
<base href="<%=basePath%>">

<title>接口测试</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!-- 默认主题CSS -->
<link rel="stylesheet" type="text/css" href="js/themes/pepper-grinder/easyui.css" id="linkTheme" />
<!-- 图标CSS -->
<link rel="stylesheet" type="text/css" href="js/themes/icon.css" />
<!-- jQuery主脚本 -->
<script src="js/jquery.min.js"></script>
<!-- jQuery Easy UI主脚本 -->
<script src="js/jquery.easyui.min.js"></script>
<!-- 中文语言支持 -->
<script src="js/locale/easyui-lang-zh_CN.js"></script>
</head>
<body class="easyui-layout" data-options="fit:true">
	<!-- 左边栏 -->
	<div data-options="region:'west',width:200,title:'测试方法'">
		<form id="frmSearch">
			<table width="100%" style="text-align: center;" height="50%">
				<tr>
					<td><a href="traininterface/train_order.jsp" target="myFrameName">火车票订单提交</a></td>
				</tr>
				<tr>
					<td><a href="traininterface/train_confirm.jsp" target="myFrameName">火车票确认出票</a></td>
				</tr>
				<!--<tr>
					<td><a href="traininterface/train_query_info.jsp" target="myFrameName">查询订单详情</a></td>
				</tr> -->
				<tr>
					<td><a href="traininterface/train_cancel.jsp" target="myFrameName">取消火车票订单</a></td>

				</tr>
				<tr>
					<td><a href="traininterface/return_ticket.jsp" target="myFrameName">线上退票</a></td>
				</tr>
				<tr>
					<td><a href="traininterface/train_request_change.jsp" target="myFrameName">请求改签</a></td>
				</tr>
				<tr>
					<td><a href="traininterface/train_cancel_change.jsp" target="myFrameName">取消改签</a></td>
				</tr>
				<tr>
					<td><a href="traininterface/train_confirm_change.jsp" target="myFrameName">确定改签</a></td>
				</tr>
				<tr>
					<td><a href="traininterface/train_query.jsp" target="myFrameName">余票查询(有价格)</a></td>
				</tr>
				<tr>
					<td><a href="traininterface/train_query_remain.jsp" target="myFrameName">余票查询(无价格)</a></td>
				</tr>
				<tr>
					<td><a href="traininterface/get_train_info.jsp" target="myFrameName">车次查询</a></td>
				</tr>
			</table>
		</form>
	</div>
	<!-- 中间区域，主体内容 -->
	<div  data-options="region:'center',title:'测试结果'">
		<iframe id="myFrameId" name="myFrameName"  frameborder="0" height="100%" width="100%" style="overflow:scroll;">
		
		</iframe>
	</div>
</body>
</html>



