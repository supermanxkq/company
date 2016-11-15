<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.ccservice.b2b2c.atom.train.TrainorderRefundprice"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%
    try {
        //车票ID
        Long ticketid = Long.valueOf(request.getParameter("ticketid"));
        //订单ID
        Long trainorderid = Long.valueOf(request.getParameter("trainorderid"));
        //接口类型
        int interfacetype = Integer.valueOf(request.getParameter("interfacetype"));
        //退票手续费
        float procedure = Float.valueOf(request.getParameter("procedure"));
        //数据库配置的请求interface地址
        String responseurl = request.getParameter("responseurl");
        //退票回调通知类型 2：线下改签退款；3：线上改签退款，退票暂不用
        String returnType = request.getParameter("returnType");
        if (ElongHotelInterfaceUtil.StringIsNull(returnType)) {
            returnType = "0";//returnType为空设为0，用于设置退票时间戳，无其他用
        }
        //调用退票退款接口
        new TrainorderRefundprice().refundpriceJsp(ticketid, trainorderid, interfacetype, responseurl,
                procedure, returnType);
    }
    catch (Exception e) {
        e.printStackTrace();
        String msg = e.getMessage();
        String ret = "出错了，错误信息为：" + (msg == null ? "空" : msg.trim());
        StackTraceElement stack = e.getStackTrace()[0];
        if (stack != null) {
            ret += "；异常类：" + stack.getFileName() + " ；方法： " + stack.getMethodName() + " ；行数： "
                    + stack.getLineNumber();
        }
        System.out.println(ret);
    }
%>