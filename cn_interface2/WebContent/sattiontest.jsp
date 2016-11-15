<%@page
	import="com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtil"%>
<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%
    try {
        out.print(Train12306StationInfoUtil.getThreeByName("北京"));
    }
    catch (Exception e) {
        out.print("error");
    }
%>