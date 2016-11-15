<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.ccservice.b2b2c.util.ExceptionUtil"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil"%>
<%
	try{
	    String jsonStr = request.getParameter("jsonStr");
	   	out.print(new TaobaoHotelInterfaceUtil().taobaoHandleOrderByJsp(jsonStr).toString());
	}catch (Exception e) {
	    out.print("Exception");
	    ExceptionUtil.writelogByException("error_TaobaoHandle", e);
	}
%>