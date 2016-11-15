<%@page import="java.util.*"%>
<%@page import="java.util.Map.Entry"%>
<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page import="com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil"%>
<%@page import="com.ccservice.b2b2c.atom.service12306.onlineRefund.mq.util.ReturnTicketDataManage"%>
<%
	//结果
	StringBuffer buf = new StringBuffer();
	//提示
	int setTime = 5 * 60 * 1000;//5分钟，红字显示 
	//时间
	String time = ElongHotelInterfaceUtil.getCurrentTime();
	//表格
	buf.append("<table style='cursor:default;border-collapse:collapse;' border='1' cellspacing='0' cellpadding='0' bordercolor='#BEBEBE'>");
	buf.append("<tr>");
	buf.append("<td colspan='6' style='height:30px;padding-left:10px;'>");
	buf.append("服务器时间：" + time);
	buf.append("</td>");
	buf.append("</tr>");
	//表头
	buf.append("<tr style='background:#EEE8CD;'>");
	buf.append("<td style='width:125px;height:30px;' align='center'>订单ID</td>");
	buf.append("<td style='width:125px;' align='center'>车票ID</td>");
	buf.append("<td style='width:125px;' align='center'>退票状态</td>");
	buf.append("<td style='width:225px;' align='center'>申请时间</td>");
	buf.append("<td style='width:225px;' align='center'>发车时间</td>");
	buf.append("<td style='width:225px;' align='center'>处理时间</td>");
	buf.append("</tr>");
	//状态显示
	Map<Integer, String> statusMap = new HashMap<Integer, String>();
	statusMap.put(ReturnTicketDataManage.WAIT, "等待退票");
	statusMap.put(ReturnTicketDataManage.PROCESSING, "正在退票");
	//退票数据
	Hashtable<Long, Hashtable<Long, JSONObject>> orderMap = ReturnTicketDataManage.getReadOnly();
	//遍历订单
	for(Entry<Long, Hashtable<Long, JSONObject>> orderEntry : orderMap.entrySet()) {
	  	int idx = 0;
	    long orderId = orderEntry.getKey();
	    Hashtable<Long, JSONObject> ticketMap = orderEntry.getValue();
	    //遍历车票
	    for(Entry<Long, JSONObject> ticketEntry : ticketMap.entrySet()) {
	        idx++;
	        //信息
	        JSONObject info = ticketEntry.getValue();
	        //时间
	        boolean LongTime = System.currentTimeMillis() - info.getLongValue("requestTime") > setTime;
	      	//拼行
	      	buf.append(LongTime ? "<tr style='color:red'>" : "<tr>");
	    	//第一个
	    	if(idx == 1){
				buf.append("<td align='center' rowspan='" + ticketMap.size() + "'>" + orderId + "</td>");
	    	}
	      	buf.append("<td align='center'>" + ticketEntry.getKey() + "</td>");
	        buf.append("<td align='center'>" + statusMap.get(info.getIntValue("status")) + "</td>");
	        buf.append("<td align='center'>" + info.getTimestamp("requestTime") + "</td>");
	        buf.append("<td align='center'>" + info.getTimestamp("departTime") + "</td>");
	        buf.append("<td align='center'>" + info.getTimestamp("operateTime") + "</td>");
	        buf.append("</tr>");
	    }
	}
	buf.append("</table>");
	//输出结果
	out.print("<center>" + buf.toString() + "</center>");
%>