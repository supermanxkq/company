<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
<%@page import="java.util.Map"%>
<%
// Map map = ICC.getICCBuyCallbackMap(request);
Map map=null;

if(((String)request.getParameter("r9_BType")).equals("2")){
out.println("success</br>");
//�ڽ��յ�֧�����֪ͨ���ж��Ƿ���й�ҵ���߼�������Ҫ�ظ�����ҵ���߼�����
}
	if((Boolean)map.get("checkHmac")){
	out.println("�ӿ�����:" + request.getParameter("r0_Cmd") + "</br>");
	out.println("������:" + request.getParameter("r1_Code") + "</br>");
	out.println("������ˮ��:" + request.getParameter("r2_TrxId") + "</br>");
	out.println("�̻����:" + request.getParameter("p1_MerId") + "</br>");
	out.println("�̻�ƽ̨������:" + request.getParameter("p2_Order") + "</br>");
	out.println("���׽��:" + request.getParameter("p3_Amt") + "</br>");
	out.println("���ױ���:" + request.getParameter("p4_cur") + "</br>");
	out.println("�������ʱ��:" + request.getParameter("rp_PayDate") + "</br>");
	out.println("���׽����������:" + request.getParameter("r9_BType") + "</br>");
	out.println("�̻��Զ�����Ϣ1:" + request.getParameter("pe_extInfo1") + "</br>");
	out.println("�̻��Զ�����Ϣ2:" + request.getParameter("pe_extInfo2") + "</br>");
	out.println("�̻��Զ�����Ϣ3:" + request.getParameter("pe_extInfo3") + "</br>");
	out.println("�̻��Զ�����Ϣ4:" + request.getParameter("pe_extInfo4") + "</br>");
	out.println("������Ϣ:" + request.getParameter("errorMsg") + "</br>");

	logicAtCallback();
	
	}else{
	out.println("����ǩ����Ч");
	}
%>
<%!
public void logicAtCallback(){

}
%>