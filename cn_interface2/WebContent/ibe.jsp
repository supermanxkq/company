<%@ page contentType="text/html; charset=gbk"%>
<%@page import="com.travelsky.ibe.client.AV"%>
<%@page import="com.travelsky.ibe.client.AvResult"%>
<%@page import="com.travelsky.ibe.client.AvItem"%>
<%@page import="com.travelsky.ibe.client.AvSegment"%>

<%
    AV av = new AV();
    AvResult avresult = new AvResult();
    try {
        //格式化出发时间
        System.out.println("开始查询");
        avresult = av.getAvailability("PEK", "CAN",
                "20111119 00:00:00", "ALL", true, true);
        System.out.println("查询出的航班信息：" + avresult);
    }
    catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("航班查询接口异常：" + ex.getMessage().toString());
    }
    out.print(avresult);
%>
