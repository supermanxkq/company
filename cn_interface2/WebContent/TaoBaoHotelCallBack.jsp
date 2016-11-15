<%@page pageEncoding="utf-8"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.TaobaoUtil"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<body id="BodyEle"></body>
<%
    try {
		//Data
		String code = request.getParameter("code");
		if (!ElongHotelInterfaceUtil.StringIsNull(code)) {
			out.print(TaobaoUtil.saveSessionKey(code));
		}else{
		    throw new Exception("获取Code失败。");
		}
	} catch (Exception e) {
		out.print("<script type=\"text/javascript\">alert('抱歉，授权失败，请重新尝试。');history.go(-1);</script>");
	}
%>
