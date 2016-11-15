<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%
    try {
        String type = request.getParameter("type");
        if (type != null && "1".equals(type)) {
            out.print("success");
        }else{
            out.print("false");
        }
    }
    catch (Exception e) {
        out.print("exception");
    }
%>