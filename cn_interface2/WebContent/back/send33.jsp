<%@ page language="java" import="java.util.*" pageEncoding="gb2312"%>
<%@ page import="com.bill.encrypt.MD5Util"%>
<jsp:directive.page import="java.text.SimpleDateFormat"/>
<%
//����ҷ������أ������տ�ӿ�
//���뷽ʽ
String inputCharset="3";
//���ܷ��˽����ҳ���ַ
String pageUrl="";
//���������ܽ����ҳ���ַ
String bgUrl="http://211.148.7.253:8801/sunzz/Test3/receive3.jsp";
//���ذ汾��
String version="v2.0";
//��������
String language="1";
//ǩ������
String signType="1";
//�տ��ϵ��ʽ����
String payeeContactType="1";
//�տ��ϵ��ʽ
String payeeContact="kqkqklqcom@126.com";
//֧��������
String payerName="Kady";
//֧������ϵ��ʽ����
String payerContactType="1";
//֧������ϵ��ʽ
String payerContact="kquser02@sina.com";
//�̻�������
String orderId=new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
//�������
String orderAmount="2";
//�տӦ�ն�
String payeeAmount="1";
//�����ύʱ��
String orderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
//��Ʒ����
String productName="sony";
//��Ʒ����
String productNum="1";
//��Ʒ����
String productDesc="";
//��չ�ֶ�1
String ext1="";
//��չ�ֶ�2
String ext2="";
//֧����ʽ
String payType="00";
//���д���
String bankId="";
//���������
String pid="10003000799";
//��������
String sharingData="1^kady.sunzhen@gmail.com^1^0^test";
//���˱�־
String sharingPayFlag="0";
//��Կ
String key="52Q4FY6HLDN7ZRFB";
String signMsgVal="";
signMsgVal=appendParam(signMsgVal,"inputCharset",inputCharset);
signMsgVal=appendParam(signMsgVal,"pageUrl",pageUrl);
signMsgVal=appendParam(signMsgVal,"bgUrl",bgUrl);
signMsgVal=appendParam(signMsgVal,"version",version);
signMsgVal=appendParam(signMsgVal,"language",language);
signMsgVal=appendParam(signMsgVal,"signType",signType);
signMsgVal=appendParam(signMsgVal,"payeeContactType",payeeContactType);
signMsgVal=appendParam(signMsgVal,"payeeContact",payeeContact);
signMsgVal=appendParam(signMsgVal,"payerName",payerName);
signMsgVal=appendParam(signMsgVal,"payerContactType",payerContactType);
signMsgVal=appendParam(signMsgVal,"payerContact",payerContact);
signMsgVal=appendParam(signMsgVal,"orderId",orderId);
signMsgVal=appendParam(signMsgVal,"orderAmount",orderAmount);
signMsgVal=appendParam(signMsgVal,"payeeAmount",payeeAmount);
signMsgVal=appendParam(signMsgVal,"orderTime",orderTime);
signMsgVal=appendParam(signMsgVal,"productName",productName);
signMsgVal=appendParam(signMsgVal,"productNum",productNum);
signMsgVal=appendParam(signMsgVal,"productDesc",productDesc);
signMsgVal=appendParam(signMsgVal,"ext1",ext1);
signMsgVal=appendParam(signMsgVal,"ext2",ext2);
signMsgVal=appendParam(signMsgVal,"payType",payType);
signMsgVal=appendParam(signMsgVal,"bankId",bankId);
signMsgVal=appendParam(signMsgVal,"pid",pid);
signMsgVal=appendParam(signMsgVal,"sharingData",sharingData);
signMsgVal=appendParam(signMsgVal,"sharingPayFlag",sharingPayFlag);
signMsgVal=appendParam(signMsgVal,"key",key);
String signMsg = MD5Util.md5Hex(signMsgVal.getBytes("gb2312")).toUpperCase();
 %>
 <%!
 public String appendParam(String returnStr,String paramId,String paramValue){
 	if(!returnStr.equals(""))
 	{
 		if(!paramValue.equals(""))
 		{
 		 	returnStr = returnStr+"&"+paramId+"="+paramValue;
 		}
 	}
 	else
 	{
 		if(!paramValue.equals(""))
 		{
 			returnStr=paramId+"="+paramValue;
 		}
 	}
 	return returnStr;
 	
 }
  %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   
    <title>My JSP 'send3.jsp' starting page</title>
    
	

  </head>
  
  <body>
    <div align="center">
    	<table border="1">
    	<tr>
    	<td>֧����ʽ</td>
    	<td>��Ǯ</td>
    	</tr>
    	<tr>
    	<td>�������</td>
    	<td><%=orderId %></td>
    	</tr>
    	<tr>
    	<td>�������</td>
    	<td><%=orderAmount %></td>
    	</tr>
    	<tr>
    	<td>���տ���</td>
    	<td><%=payeeAmount %></td>
    	</tr>
    	<tr>
    	<td>��������</td>
    	<td><%=sharingData %></td>
    	</tr>
    	
    	</table>
    </div>
    <div align="center">
    <form name="kqPay" action="https://www.99bill.com/msgateway/recvMsgatewayMerchantInfoAction.htm" method="get" >
    <input type="hidden" name="inputCharset"value="<%=inputCharset %>">
    <input type="hidden" name="signMsg"value="<%=signMsg %>">
    <input type="hidden" name="sharingPayFlag"value="<%=sharingPayFlag %>">
    <input type="hidden" name="sharingData"value="<%=sharingData %>">
    <input type="hidden" name="pid"value="<%=pid %>">
    <input type="hidden" name="bankId"value="<%=bankId %>">
    <input type="hidden" name="payType"value="<%=payType %>">
    <input type="hidden" name="ext2"value="<%=ext2 %>">
    <input type="hidden" name="ext1"value="<%=ext1 %>">
    <input type="hidden" name="productDesc"value="<%=productDesc %>">
    <input type="hidden" name="productNum"value="<%=productNum %>">
    <input type="hidden" name="productName"value="<%=productName %>">
    <input type="hidden" name="orderTime"value="<%=orderTime %>">
    <input type="hidden" name="payeeAmount"value="<%=payeeAmount %>">
    <input type="hidden" name="orderAmount"value="<%=orderAmount %>">
    <input type="hidden" name="orderId"value="<%=orderId %>">
    <input type="hidden" name="payerContact"value="<%=payerContact %>">
    <input type="hidden" name="payerContactType"value="<%=payerContactType %>">
    <input type="hidden" name="payerName"value="<%=payerName %>">
    <input type="hidden" name="payeeContactType"value="<%=payeeContactType %>">
    <input type="hidden" name="payeeContact"value="<%=payeeContact %>">
    <input type="hidden" name="signType"value="<%=signType %>">
    <input type="hidden" name="language"value="<%=language %>">
    <input type="hidden" name="version"value="<%=version %>">
    <input type="hidden" name="bgUrl"value="<%=bgUrl %>">
    <input type="hidden" name="pageUrl"value="<%=pageUrl %>">
    <input type="submit" name="submit"value="�ύ����Ǯ2">
    </form>
    </div>
  </body>
</html>
