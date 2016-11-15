<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.ccservice.b2b2c.atom.server.Server"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%
	response.setCharacterEncoding("UTF-8");
	response.setHeader("content-type", "text/html;charset=UTF-8");
	//标识
	String OpenChange = request.getParameter("OpenChange");
	//判断
    if("true".equals(OpenChange) || "false".equals(OpenChange)){
        //结果
        String result = "";
        //标识
        String key = "changeRefundFlag";
        //开关
        String value = "true".equals(OpenChange) ? "1" : "0";
        //赋值
        Server.getInstance().getDateHashMap().put(key, value);
        //实际
        String real = Server.getInstance().getDateHashMap().get(key);
        //判断
        if(value.equals(real)){
            result = "操作成功，";    
        }
        else{
            result = "操作失败，";
        }
        result += "当前状态：" + ("1".equals(real) ? "开" : "关") + "！";
        //输出
        out.print("<center>"+result+"</center>");
    }
    else{
        out.print("<center>非法请求!</center>");
    }
%>