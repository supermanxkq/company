<%@ page contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>检测线上的未核验手机号的账号是否都能正常下单的Job</title>
</head>
<body>
	<%
	    String type = request.getParameter("type");
	    try {
	        if ("1".equals(type)) {
	            com.ccservice.b2b2c.atom.servlet.job.air.JobScanAirOpenTicket.startScheduler("* * 8 * * ?");
	        }
	        else if ("0".equals(type)) {
	            // 	            com.ccservice.inter.job.train.account.Job12306AccountCheckMobileNew.pauseJob();
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	%>
	<br /> 操作类型 (type==1开启,type==0关闭) type=<%=type%>
</body>