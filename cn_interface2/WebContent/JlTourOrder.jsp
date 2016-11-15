<%@page pageEncoding="utf-8"%><%@page contentType="text/html; charset=utf-8"%><%@page import="java.net.URLDecoder"%><%@page import="java.io.BufferedReader"%><%@page import="java.io.InputStreamReader"%><%@page import="com.ccservice.huamin.WriteLog"%><%@page import="com.ccservice.b2b2c.atom.hotel.JLHotel"%><%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%><%
	//深捷旅XML下单、取消订单接口
	String xml = "";
	String ret = "";
	try{
	    //Request Data
	    BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
        	buf.append(line);
        }
        xml = buf.toString();
        xml = URLDecoder.decode(xml, "utf-8");
	    //Request Jl
	    ret = JLHotel.postSendJL(xml);
	}catch (Exception e) {
		WriteLog.write("深捷旅XML接口订单", ElongHotelInterfaceUtil.errormsg(e) + "###" + xml + "###" + ret);
	}
	out.print(ret);
%>