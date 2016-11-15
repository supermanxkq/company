<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="webwork" prefix="ww"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>火车票统计</title>
<style type="text/css">
* {
	margin: 0;
	padding: 0;
}

.action {
	font-size: 12px;
	font-family: "微软雅黑";
	width: 96%;
	border: 1px solid #bcbbb8;
	margin: 20px auto;
	border-collapse: collapse;
}

.action table tr {
	border: 1px solid #00C;
	border-collapse: collapse;
}

.action tr td {
	padding: 4px 8px;
	border: 1px solid #c9c9c9;
	border-collapse: collapse;
}

.action .first-td td {
	border-top: none;
}

.action .first-td td:nth-child(1), .action .first-td td:nth-child(3),
	.action .first-td td:nth-child(5), .action .first-td td:nth-child(7)
	, .action .first-td td:nth-child(9) {
	background-color: #e8bd60;
	color: #333;
}

.action .second-td td:nth-child(1), .action .second-td td:nth-child(3),
	.action .second-td td:nth-child(5), .action .second-td td:nth-child(7),
	.action .second-td td:nth-child(9) {
	background-color: #acc2e1;
	color: #333;
}

.action .second-td td:nth-child(1), .action .second-td td:nth-child(3),
	.action .second-td td:nth-child(5), .action .second-td td:nth-child(7)
	, .action .second-td td:nth-child(9) {
	border-top: 1px solid #FFF;
}

.action .next-td td:nth-child(1), .action .third-td td:nth-child(3),
	.action .third-td td:nth-child(5), .action .third-td td:nth-child(7),
	.action .third-td td:nth-child(9) {
	background-color: #f4f4f4;
	color: #333;
	border-top: 1px solid #FFF;
}


.action tr td:nth-child(2), .action tr td:nth-child(4), .action tr td:nth-child(6),
	.action tr td:nth-child(8), .action tr td:nth-child(10) {
	color: #333;
}
</style>
</head>
<body>
	<div id="divTemplate" style="display:none;">
		<table width="100%" border="0" cellspacing="0" cellpadding="0">
			<tr class="first-td">
				<td>平均耗时</td>
				<td>{avgtime}</td>
			</tr>
			<tr id="trCancelType"></tr>
		</table>
	</div>
	<div id="divRealTimeStatus" class="action">
		
	</div>
	<div id="container_day" style="width: 100%; height: 292px;"></div>
	<script type="text/javascript" src="../js/jquery-1.6.min.js"></script>
	<script type="text/javascript" src="../js/statistics/highcharts.js"></script>
	<script type="text/javascript"
		src="../js/trainindex_frame_findHuoNiuTow.js?v=20160408"></script>
</body>
</html>