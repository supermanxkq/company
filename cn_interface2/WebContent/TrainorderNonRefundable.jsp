<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%@page import="com.ccservice.b2b2c.atom.train.TrainorderNonRefundable"%>
<%
    try {
        request.setCharacterEncoding("UTF-8");
        //车票ID
        Long ticketid = Long.valueOf(request.getParameter("ticketid"));
        //订单ID
        Long trainorderid = Long.valueOf(request.getParameter("trainorderid"));
        //接口类型
        int interfacetype = Integer.valueOf(request.getParameter("interfacetype"));
        //无法退票原因 
        int reason = Integer.valueOf(request.getParameter("reason"));
        //12306无法退票原因
        String errMsg = request.getParameter("errMsg");
        //乱码处理
        if (ElongHotelInterfaceUtil.StringIsNull(errMsg)) {
            errMsg = "";
        }else{
            errMsg = new String(errMsg.getBytes("ISO8859-1"), "UTF-8");
        }
        if (reason == 41) {
            if (ElongHotelInterfaceUtil.StringIsNull(errMsg)) {
                errMsg = "尊敬的旅客，为防止网上囤票倒票，给广大旅客创造一个公平的购票环境，凡通过互联网或手机购买的本次列车车票，如需办理退票、改签和变更到站等变更业务，请持乘车人身份证件原件到就近车站办理，代办时还需持代办人的身份证件原件。";
            }
            errMsg = "无法在线退票_" + errMsg;
        }else if (reason == 51){
            if (ElongHotelInterfaceUtil.StringIsNull(errMsg)) {
                errMsg = "用户12306账号登录失败";
            }
        }
        //数据库配置的请求interface地址
        String responseurl = request.getParameter("responseurl");
        //调用无法退票接口
        new TrainorderNonRefundable().nonRefundableJsp(ticketid, trainorderid, interfacetype, reason,
                responseurl, errMsg);
    }
    catch (Exception e) {
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