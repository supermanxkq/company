<%@page import="java.util.Map"%>
<%@page import="java.util.List"%>
<%@page import="com.ccservice.b2b2c.atom.server.Server"%>
<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.ccservice.b2b2c.atom.service12306.onlineRefund.util.OnlineRefundUtil"%>
<%
	response.setCharacterEncoding("UTF-8");
	response.setHeader("content-type", "text/html;charset=UTF-8");
	try {
	    //MQ
	    OnlineRefundUtil util = new OnlineRefundUtil();
	    //退票问题
	    String querySql = "select C_ORDERID, ID, C_CHANGEID, C_CHANGETYPE, C_TICKETNO, C_TCTICKETNO, C_DEPARTTIME, C_TTCDEPARTTIME, C_REFUNDREQUESTTIME " 
	    					+ "from T_TRAINTICKET with(nolock) where C_STATUS = 6 and C_ISQUESTIONTICKET in (1, 2) and C_ISAPPLYTICKET = 1 and C_REFUNDTYPE = 0";
		//查询退票
		List list = Server.getInstance().getSystemService().findMapResultBySql(querySql, null);
		//循环退票
		for(int i = 0 ; i < list.size(); i++){
		    try {
		        //MAP
		        Map map = (Map) list.get(i);
		        //改签数据
		        long changeId = map.get("C_CHANGEID") == null ? 0 : Long.parseLong(map.get("C_CHANGEID").toString());
		        int changeType = map.get("C_CHANGETYPE") == null ? 0 : Integer.parseInt(map.get("C_CHANGETYPE").toString());
		        //线上改签
		        boolean reqChange = changeId > 0 && changeType == 1;
		        //车票改签
		        boolean isChange = changeId > 0 && (changeType == 1 || changeType == 2);
		        //车票数据
		        long ticketId = Long.parseLong(map.get("ID").toString());
		        long orderId = Long.parseLong(map.get("C_ORDERID").toString());
		        String requestTime = map.get("C_REFUNDREQUESTTIME").toString();
		        String ticket_no = map.get(isChange ? "C_TCTICKETNO" : "C_TICKETNO").toString().trim();
		        String departTime = map.get(reqChange ? "C_TTCDEPARTTIME" : "C_DEPARTTIME").toString().trim();
		        //重丢MQ
		        if(orderId > 0 && ticketId > 0) {
		            util.retryRefund(orderId, ticketId, ticket_no, departTime, requestTime, true);
		        }
		    }
		    catch(Exception e){
		        
		    }
		}
	    //审核问题
	    String updateSql = "update T_TRAINTICKET set C_REFUNDTYPE = 1, C_ISQUESTIONTICKET = 0 where C_STATUS = 8 and C_ISQUESTIONTICKET = 3 and C_ISAPPLYTICKET = 1 and C_REFUNDTYPE = 0";
	    //直接更新
	    Server.getInstance().getSystemService().excuteAdvertisementBySql(updateSql);
	    //输出提示
	    out.print("<center>操作完成!</center>");
	} catch (Exception e) {
	    out.print("<center>系统异常!</center>");
	}
%>