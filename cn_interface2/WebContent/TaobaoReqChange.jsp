<%@page import="java.net.URLDecoder"%>
<%@page import="com.ccservice.b2b2c.atom.component.WriteLog"%>
<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongChengReqChange"%>
<%
	try{
	    String  jsonStr= request.getParameter("jsonStr");
	    jsonStr=URLDecoder.decode(jsonStr, "UTF-8");
	    WriteLog.write("淘宝改签_JSP", jsonStr);
	    JSONObject jsonObject= JSONObject.parseObject(jsonStr);
	  	String result= new TongChengReqChange().operate(jsonObject);
	  	out.print(result);
	}catch (Exception e) {
	    System.out.println("我错了");
	    JSONObject retobj= new JSONObject();
	    retobj.put("success", false);
        retobj.put("code", 999);
        retobj.put("msg", "请求改签失败");
        out.print(retobj);
	}
%>