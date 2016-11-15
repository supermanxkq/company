<%@page pageEncoding="utf-8"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.ccservice.b2b2c.util.OcsMethod"%>
<%@page import="com.ccservice.b2b2c.util.ExceptionUtil"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%
	try{
	    //1 set  2 get 3remove 4replce
	    String type = request.getParameter("type");
	    String key = request.getParameter("key");
	    String value = request.getParameter("value");
	    String timeout = request.getParameter("timeout");
	    if(ElongHotelInterfaceUtil.StringIsNull(type)||ElongHotelInterfaceUtil.StringIsNull(key)){
	        System.out.println("请求非法");
	        return;
	    }
	    if("1".equals(type)){
	        if(ElongHotelInterfaceUtil.StringIsNull(value)){
	            System.out.println("set请求非法");
		        return;
	        }else{
                int timeout_int=-1;
	            if(!ElongHotelInterfaceUtil.StringIsNull(timeout)){
	                try{
	                     timeout_int = Integer.valueOf(timeout);
	                }catch(Exception e){
	                    ExceptionUtil.writelogByException("error_ocstest", e);
	                }
	            }
	            if(timeout_int>-1){
	                boolean issuccess=OcsMethod.getInstance().add(key, value,timeout_int);
	                System.out.println("set请求==>"+	issuccess+"===>timeout_int:"+timeout_int);
	            }else{
	                boolean issuccess=OcsMethod.getInstance().add(key, value);
	                System.out.println("set请求==>"+	issuccess);
	            }
	        }
	    }else if ("2".equals(type)){
	        String str=OcsMethod.getInstance().get(key);
	        System.out.println("get请求==>"+str);
	    }else if ("3".equals(type)){
	        boolean issuccess=OcsMethod.getInstance().remove(key);
            System.out.println("remove请求==>"+	issuccess);
	    }else if ("4".equals(type)){
	        int timeout_int=-1;
            if(!ElongHotelInterfaceUtil.StringIsNull(timeout)){
                try{
                     timeout_int = Integer.valueOf(timeout);
                }catch(Exception e){
                    ExceptionUtil.writelogByException("error_ocstest", e);
                }
            }
            if(timeout_int>-1){
                boolean issuccess=OcsMethod.getInstance().replace(key, value,timeout_int);
                System.out.println("replace请求==>"+	issuccess+"===>timeout_int:"+timeout_int);
            }else{
                boolean issuccess=OcsMethod.getInstance().replace(key, value);
                System.out.println("replace请求==>"+	issuccess);
            }
	    }else {
	        System.out.println("未知请求==>"+	type);
	    }
	    //OcsMethod.getInstance()
	}catch (Exception e) {
	    ExceptionUtil.writelogByException("error_ocstest", e);
	}
%>