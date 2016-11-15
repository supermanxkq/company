<%@page import="com.test.TomcatStatusMem"%>
<%@page import="com.ccservice.b2b2c.util.ExceptionUtil"%>
<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%
    try {
        String type = request.getParameter("type");
        if (type != null && "1".equals(type)) {
            TomcatStatusMem.init();
            out.print("success");
        }else{
            out.print("false");
        }
    }
    catch (Exception e) {
        ExceptionUtil.writelogByException("error_ocstest", e);
    }
%>