<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%@page import="com.ccservice.b2b2c.atom.servlet.TaobaoTrainTest"%>
<%
	try{
	   out.print(TaobaoTrainTest.test());
	}catch (Exception e) {
	    String msg = e.getMessage();
        String ret = "出错了，错误信息为：" + (msg == null ? "空" : msg.trim());
        StackTraceElement stack = e.getStackTrace()[0];
        if (stack != null) {
            ret += "；异常类：" + stack.getFileName() + " ；方法： " + stack.getMethodName() + " ；行数： " + stack.getLineNumber();
        }
        System.out.println(ret);
	}
%>