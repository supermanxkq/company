<%@page pageEncoding="utf-8"%>
<%@page contentType="text/html; charset=utf-8"%>
<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page import="com.ccservice.b2b2c.util.ExceptionUtil"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%
    try {
        //请求数据
        String param = request.getParameter("jsonStr");
        //数据为空
        if (ElongHotelInterfaceUtil.StringIsNull(param)) {
            out.print("参数缺失");
        }
        //刷新Cookie
        else{
            //JSON
            JSONObject json = JSONObject.parseObject(param);
            //请求淘宝
            String fresh = new TaobaoHotelInterfaceUtil().freshCookie(json);
            //判断等待
            try {
                //&& json.getBooleanValue("nowait")
                if(fresh != null && fresh.contains("error_msg")){
                    JSONObject error_obj = JSONObject.parseObject(fresh);
                    error_obj = error_obj.getJSONObject("train_agent_session_get_response");
                   	String error_msg = error_obj.getJSONObject("session_info").getString("error_msg");
                   	//判断错误信息
                   	if(error_msg != null && (error_msg.contains("频繁") || error_msg.contains("稍后重试") || 
                   	        error_msg.contains("稍候重试") || error_msg.contains("稍后再试"))){
                   	    Thread.sleep(60 * 1000);//等待1分钟
                   	}
                }
            }catch (Exception e) {
                
            }
            //输出结果
            finally{
                out.print(fresh);
            }
        }
    }
    catch (Exception e) {
        out.print("Exception");
        ExceptionUtil.writelogByException("error_taobaoReLogin", e);
    }
%>