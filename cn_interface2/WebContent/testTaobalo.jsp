<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.ccservice.b2b2c.util.ExceptionUtil"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%
    try {
        String jsonStr = request.getParameter("jsonStr");
        if (ElongHotelInterfaceUtil.StringIsNull(jsonStr) || ElongHotelInterfaceUtil.StringIsNull(jsonStr)) {
            out.print("jsonStr is null");
            return;
        }
        if (jsonStr.equals("1")) {
            new TaobaoHotelInterfaceUtil().returnOrdersByList(1);
        }
        else if (jsonStr.equals("0")) {
            new TaobaoHotelInterfaceUtil().returnOrdersByList(0);
        }
        else {
            JSONObject jsonObject = JSONObject.parseObject(jsonStr);
            out.print(new TaobaoHotelInterfaceUtil().taobaoDrawerNotice(jsonObject));
        }
    }
    catch (Exception e) {
        out.print("Exception");
        ExceptionUtil.writelogByException("error_taobaoReLogin", e);
    }
%>