<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="com.ccservice.b2b2c.atom.server.Server"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
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
            Server.getInstance().getDateHashMap().remove(key);
        }
		else if("@Clear".equalsIgnoreCase(value)){
        	Server.getInstance().getDateHashMap().clear();
    	}
        else{
            Server.getInstance().getDateHashMap().put(key, value);
        }
    }
    StringBuffer buf = new StringBuffer();
    Map<String, String> dateHashMap = Server.getInstance().getReadOnlyDateHashMap();
    for(String temp:dateHashMap.keySet()){
        buf.append(temp + " ---> " + dateHashMap.get(temp) + "<br/>");
    }
    out.print("<center>" + buf.toString() + "</center>");
%>