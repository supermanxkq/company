<meta http-equiv="Content-Type" content="text/html; charset=gbk" />
<%@page import="java.util.Map"%>
<%! 
// �ύǰ����ҵ���߼�
public void logicBeforeSendReq() {
}
// �ύ����ҵ���߼�
public void logicAfterSendReq() {
}
%>
<%
logicBeforeSendReq();
// Map map = ICC.getICCQueryRequestBackMap(request);
Map map = null;

if((Boolean)map.get("httpConnection") == false){
out.println("ͨѶʧ��" + "<br/>");
}else{
Map parameter = (Map)map.get("parameter");
Boolean checkHmac = (Boolean)map.get("checkHmac");
if((Boolean)map.get("checkHmac") ){

out.println("�ӿ�����:" + parameter.get("r0_Cmd") + "<br>");
out.println("��ѯ���:" + parameter.get("r1_Code") + "<br>");
out.println("�̻����:" + parameter.get("p1_MerId") + "<br>");
out.println("������ˮ��:" + parameter.get("r2_TrxId") + "<br>");
out.println("���׽��:" + parameter.get("r3_Amt") + "<br>");
out.println("���ױ���:" + parameter.get("r4_Cur") + "<br>");
out.println("�̻�������:" + parameter.get("r6_Order") + "<br>");
out.println("֧��״̬:" + parameter.get("rb_PayStatus") + "<br>");
out.println("�������ʱ��:" + parameter.get("sd_paySuccessTime") + "<br>");
out.println("�˿����:" + parameter.get("rc_RefundCount") + "<br>");
out.println("���˿���:" + parameter.get("rd_RefundAmt") + "<br>");
out.println("�̻��Զ�����Ϣ1:" + parameter.get("pe_extInfo1") + "<br>");
out.println("�̻��Զ�����Ϣ2:" + parameter.get("pe_extInfo2") + "<br>");
out.println("�̻��Զ�����Ϣ3:" + parameter.get("pe_extInfo3") + "<br>");
out.println("�̻��Զ�����Ϣ4:" + parameter.get("pe_extInfo4") + "<br>");
out.println("������Ϣ:" + parameter.get("errorMsg") + "<br>");

logicAfterSendReq();
}else{
out.println("����ǩ����Ч" + "<br/>");
}
}
%> 