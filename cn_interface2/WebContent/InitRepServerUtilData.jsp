<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.ccservice.b2b2c.atom.service12306.RepServerUtil"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%
	response.setCharacterEncoding("UTF-8");
	response.setHeader("content-type", "text/html;charset=UTF-8");
	//请求数据
	String key = request.getParameter("key");
	String value = request.getParameter("value");
	//移除所有
  	boolean removeAll = "removeAll".equalsIgnoreCase(key) && "true".equalsIgnoreCase(value);
	//MAP赋值
    if(!removeAll && !ElongHotelInterfaceUtil.StringIsNull(key) && !ElongHotelInterfaceUtil.StringIsNull(value)){
        key = key.trim();
        value = value.trim();
        if("@Remove".equalsIgnoreCase(value)){
            RepServerUtil.repData.remove(key);
        }
        else{
            RepServerUtil.repData.put(key, Integer.parseInt(value));
        }
    }
    StringBuffer buf = new StringBuffer();
    //只读
    Map<String, Integer> readOnly = new HashMap<String, Integer>();
    readOnly.putAll(RepServerUtil.repData);
    //循环
    for(String temp:readOnly.keySet()){
        if(removeAll){
            RepServerUtil.repData.remove(temp);
        }
        else{
            buf.append(temp + " ---> " + readOnly.get(temp) + "<br/>");            
        }
    }
    out.print("<center>" + buf.toString() + "</center>");
%>