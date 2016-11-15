<%@page pageEncoding="utf-8"%><%@page contentType="text/html; charset=utf-8"%><%@page import="java.net.URLDecoder"%><%@page import="com.ccservice.huamin.WriteLog"%><%@page import="com.ccservice.b2b2c.atom.qunar.PHUtil"%><%@page import="com.ccservice.elong.inter.PropertyUtil"%><%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%><%
	//深捷旅JSON价格中转
	String req = "";
	String retstr = "";
	try{
	    //Request Data
	    req = request.getParameter("req");
	    req = URLDecoder.decode(req, "utf-8");
		req = new String(req.getBytes("iso-8859-1"), "utf-8");
		//JlTour URL
		String url = PropertyUtil.getValue("jlHotelUrl");
		retstr = PHUtil.submitPost(url, req).toString();
	}catch (Exception e) {
		WriteLog.write("深捷旅JSON价格中转", ElongHotelInterfaceUtil.errormsg(e) + "###" + req);
	}
	out.print(retstr);
%>