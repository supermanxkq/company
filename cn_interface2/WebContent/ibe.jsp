<%@ page contentType="text/html; charset=gbk"%>
<%@page import="com.travelsky.ibe.client.AV"%>
<%@page import="com.travelsky.ibe.client.AvResult"%>
<%@page import="com.travelsky.ibe.client.AvItem"%>
<%@page import="com.travelsky.ibe.client.AvSegment"%>

<%
    AV av = new AV();
    AvResult avresult = new AvResult();
    try {
        //��ʽ������ʱ��
        System.out.println("��ʼ��ѯ");
        avresult = av.getAvailability("PEK", "CAN",
                "20111119 00:00:00", "ALL", true, true);
        System.out.println("��ѯ���ĺ�����Ϣ��" + avresult);
    }
    catch (Exception ex) {
        ex.printStackTrace();
        System.out.println("�����ѯ�ӿ��쳣��" + ex.getMessage().toString());
    }
    out.print(avresult);
%>
