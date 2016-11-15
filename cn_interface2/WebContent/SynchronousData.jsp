<%@page
	import="com.ccservice.b2b2c.atom.servlet.account.TrainOrderAccountMethod"%>
<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.ccservice.b2b2c.util.ExceptionUtil"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%
    try {
        String type = request.getParameter("type");
        if (ElongHotelInterfaceUtil.StringIsNull(type) || ElongHotelInterfaceUtil.StringIsNull(type)) {
            out.print("jsonStr is null");
            return;
        }
        if("1".equals(type)){
            TrainOrderAccountMethod.getInstance();
            out.print("success");
        }else{
            out.print("false");
        }

    }
    catch (Exception e) {
        out.print("Exception");
        ExceptionUtil.writelogByException("error_taobaoReLogin", e);
    }
%>