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
// Map map = ICC.getICCRefundRequestBackMap(request);
Map map =null;
if((Boolean)map.get("httpConnection") == false){
out.println("ͨѶʧ��" + "<br/>");
}else{
Map parameter = (Map)map.get("parameter");
Boolean checkHmac = (Boolean)map.get("checkHmac");
if((Boolean)map.get("checkHmac") ){

out.println("�ӿ�����:" + parameter.get("r0_Cmd") + "<br>");
out.println("�˿���:" + parameter.get("r1_Code") + "<br>");
out.println("�̻����:" + parameter.get("p1_MerId") + "<br>");
out.println("������ˮ��:" + parameter.get("r2_TrxId") + "<br>");
out.println("�˿���:" + parameter.get("r3_Amt") + "<br>");
out.println("�˿����:" + parameter.get("r4_Cur") + "<br>");
out.println("�̻��Զ�����Ϣ1:" + parameter.get("pe_extInfo1") + "<br>");
out.println("�̻��Զ�����Ϣ2:" + parameter.get("pe_extInfo2") + "<br>");
out.println("������Ϣ˵��:" + parameter.get("errorMsg") + "<br>");

logicAfterSendReq();
}else{
out.println("����ǩ����Ч" + "<br/>");
}
}
%> 