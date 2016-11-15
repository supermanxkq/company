<%@ page contentType="text/html; charset=utf-8"%>
<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page import="com.ccservice.b2b2c.atom.service12306.onlineRefund.mq.util.ChangeReturnTicketMQUtil"%>
<%
	//MQ个数
	int MqSize = 0;
	//操作类型
	String type = request.getParameter("type");
	//MQ地址
	String activeMQ_url = "failover:(tcp://localhost:61616/)";
	//捕捉异常
    try {
        //减
        if ("0".equals(type)) {
            //关闭个数
            int closeCount = 10;
            //关闭处理
            MqSize = ChangeReturnTicketMQUtil.closeMessageConsumer(closeCount);
        }
        //加、查看
        else {
            //增加个数
            int addCount = "1".equals(type) ? 1 : 0;//1:加；其他:查看，个数必须为0
            //MQ名称
            String QUEUE_NAME = "QueueMQ_TrainTicket_RefundTicket?consumer.prefetchSize=1";
            //增加处理
            MqSize = ChangeReturnTicketMQUtil.addMessageConsumer(activeMQ_url, QUEUE_NAME , addCount);
        }
    }
    catch (Exception e) {

    }
  	//输出结果
  	JSONObject outobj = new JSONObject();
  	//结果赋值
  	outobj.put("type", type);
  	outobj.put("url", activeMQ_url);
    outobj.put("consumerSize", MqSize);
    //outobj.put("url", activeMQ_url.replaceAll("localhost", "121.199.25.199"));
%>
<%=	outobj %>