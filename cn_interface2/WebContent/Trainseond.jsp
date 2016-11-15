<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.ccservice.b2b2c.atom.servlet.MQ.TrainpayMqMSGUtil"%>
<%@page import="com.ccservice.b2b2c.base.train.Trainorder"%>
<%
    try {
        //订单ID
        Long trainorderid = Long.valueOf(request
                .getParameter("payorderid"));
        Trainorder o = new Trainorder();
        o.setId(trainorderid);
        new TrainpayMqMSGUtil("PayMQ_TrainPay").sendPayMQmsg(o, 1, 1);
    }
    catch (Exception e) {
        String msg = e.getMessage();
        String ret = "出错了，错误信息为：" + (msg == null ? "空" : msg.trim());
        StackTraceElement stack = e.getStackTrace()[0];
        if (stack != null) {
            ret += "；异常类：" + stack.getFileName() + " ；方法： "
                    + stack.getMethodName() + " ；行数： "
                    + stack.getLineNumber();
        }
        System.out.println(ret);
    }
%>