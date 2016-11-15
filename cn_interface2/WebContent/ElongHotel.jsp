<%@page pageEncoding="utf-8"%>
<%@page import="java.net.URLDecoder"%>
<%@ page contentType="text/html; charset=utf-8"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil" %>
<%
	String retstr = "";
	try{
		//Data
		String api = request.getParameter("api");
		String req = request.getParameter("req");
		req = URLDecoder.decode(req,"utf-8");
		req = new String(req.getBytes("iso-8859-1"),"utf-8");
		String httpType = request.getParameter("httpType");
		//Post
		retstr = ElongHotelInterfaceUtil.postEl(api , req , httpType);
	}catch (Exception e) {
		retstr = "酒店-请求数据异常.";
	}
	out.print(retstr);
%>
