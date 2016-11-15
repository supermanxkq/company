<%@page import="java.util.Hashtable"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%@page import="com.ccservice.b2b2c.atom.service12306.offlineRefund.util.RefundTicketDataUtil"%>
<%
	response.setCharacterEncoding("UTF-8");
	response.setHeader("content-type", "text/html;charset=UTF-8");
	//请求数据
	String key = request.getParameter("key");
	String value = request.getParameter("value");
    if(!ElongHotelInterfaceUtil.StringIsNull(key) && !ElongHotelInterfaceUtil.StringIsNull(value)){
        key = key.trim();
        value = value.trim();
        if("@Remove".equalsIgnoreCase(value)){
            RefundTicketDataUtil.refundDataRemove(key);
        }
        else{
            //RefundTicketDataUtil.refundDataAdd(key, value);
        }
    }
    StringBuffer buf = new StringBuffer();
    Hashtable<String, String> datas = RefundTicketDataUtil.refundDataReadOnly();
    for(String temp:datas.keySet()){
        buf.append(temp + " ---> " + datas.get(temp) + "<br/>");
    }
    out.print("<center>" + buf.toString() + "</center>");
%>