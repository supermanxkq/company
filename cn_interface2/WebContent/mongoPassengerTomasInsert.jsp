<%@page
	import="com.ccservice.b2b2c.atom.train.idmongo.mem.MongoInsertPassengerTomasMem"%>
<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%
    try {
        String type = request.getParameter("type");
        if ("1".equals(type)) {
            MongoInsertPassengerTomasMem.open();
        }
        else if ("0".equals(type)) {
            MongoInsertPassengerTomasMem.close();
        }
        else {
            out.print("错误请求！");
        }
    }
    catch (Exception e) {
        e.printStackTrace();
    }
%>