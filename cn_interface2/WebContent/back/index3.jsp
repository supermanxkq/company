<%@ page contentType="text/html; charset=gb2312" language="java"%>
<%
/**
 * @Description: ��Ǯ����ҷ��������̻��տ�ӿڷ���
 * @Copyright (c) �Ϻ���Ǯ��Ϣ�������޹�˾
 * @version 2.0
 */


//����������Կ
///���ִ�Сд.�����Ǯ��ϵ��ȡ
String key="9I67EG8EHN7CBQ3X";

//�ַ���.�̶�ѡ��ֵ(��Ϊ��)
///�̶�ѡ��ֵ��1��2��31 ����UTF-8�� 2 ����GBK��3 ����gb2312Ĭ��ֵΪ1
String inputCharset="3";

//���ܷ��˽����ҳ���ַ(��Ϊ��)
///��Ҫ�Ǿ��Ե�ַ����bgurl ����ͬʱΪ�յ�bgUrl Ϊ��ʱ����Ǯֱ�ӽ����˽��post ��pageUrl��bgUrl ��Ϊ��ʱ������bgUrl �ķ�ʽ����
String pageUrl="";

//���������ܷ��˽���ĺ�̨��ַ(��Ϊ��)
///��Ҫ�Ǿ��Ե�ַ����bgurl ����ͬʱΪ�տ�Ǯ�����˽�����͵�bgUrl ��Ӧ�ĵ�ַ�����һ�ȡ�̼Ұ���Լ����ʽ����ĵ�ַ����ʾҳ����û�
String bgUrl="http://www.yoursite.com/receive.jsp";

//���ذ汾(��Ϊ��)
///�̶�ֵ��v2.0
//ע��ΪСд��ĸ
String version="v2.0";

//��������.�̶�ѡ��ֵ(��Ϊ��)��
///�̶�ֵ��1
///1 ��������
String language="1";

//ǩ������.�̶�ֵ(��Ϊ��)
///�̶�ֵ��1
///1����MD5����ǩ����ʽ
String signType="1";

//���տ��ϵ��ʽ����(��Ϊ��)
///�̶�ѡ��ֵ��1
///1����Email��ַ
String payeeContactType="1";

//���տ��ϵ��ʽ(��Ϊ��)
///��payeeContactType=1 ʱ����Email ��ַ
String payeeContact ="kqkqklqcom@126.com";
  
//֧��������(��Ϊ��)
///�ַ���
///��Ϊ���Ļ�Ӣ���ַ�
String payerName="payerName";

//֧������ϵ��ʽ����.�̶�ѡ��ֵ(��Ϊ��)
///�̶�ѡ��ֵ��1
///1����Email��ַ
String payerContactType="1";

//֧������ϵ��ʽ(��Ϊ��)
///�ַ���
///����payerContactTypeֵ��Ӧ��д
String payerContact="kquser@126.com";

//�̻�������(��Ϊ��)
///�ַ���
///ֻ����ʹ����ĸ�����֡�- ��_,������ĸ�����ֿ�ͷÿ�̼��ύ�Ķ����ţ������������˻�������Ψһ
String orderId=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

//�̻��������(��Ϊ��)
///��������
///�Է�Ϊ��λ���ȷ�10 Ԫ���ύʱ���ӦΪ1000
String orderAmount="3";

//���տӦ�ն�(��Ϊ��)
///��������
///�Է�Ϊ��λ���ȷ�10 Ԫ���ύʱ���ӦΪ1000
String payeeAmount="2";
	
//�����ύʱ��(��Ϊ��)
///���ִ���һ��14λ
///��ʽΪ����[4λ]��[2λ]��[2λ]ʱ[2λ]��[2λ]��[2λ]
///���磺20071117020101
String orderTime=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

//��Ʒ����(��Ϊ��)
///��Ϊ���Ļ�Ӣ���ַ�
String productName="productName";

//��Ʒ����(��Ϊ��)
///��Ϊ�գ��ǿ�ʱ����Ϊ����
String productNum="1";

//��Ʒ����(��Ϊ��)
///Ӣ�Ļ������ַ���
String productDesc="";
	
//��չ�ֶ�1(��Ϊ��)
///��֧��������ԭ�����ظ��̻�
String ext1="";

//��չ�ֶ�2(��Ϊ��)
///��֧��������ԭ�����ظ��̻�
String ext2="";
	
//֧����ʽ.�̶�ѡ��ֵ(��Ϊ��)
///00�����֧��������֧��ҳ����ʾ���п�֧���Ϳ�Ǯ�˻���
///10�����п�֧��
///12����Ǯ�˻�֧��
String payType="00";

//���д���(��Ϊ��)
///�ַ���
///payType=10ʱ���ֶ���д���д���ͱ�ʾ��Ӧ����ֱ������ο����д�����ձ�ֻ�п�ͨ����ֱ�����̼Ҳſ���ʹ�ô˹��ܡ���ͨ�ӿڲ�����д��
String bankId="";

//��������û����(��Ϊ��)
///���ִ�
///�ύ�˷�������ĺ������ڿ�Ǯ���û���š�
String pid="10003000799";

//��������(��Ϊ��)
///�ַ���
///��ϸ�ķ�����ϸ���ݣ�������ʽ˵��
String sharingData="1^kquser@126.com^1^0^test";//����˵�����ĵ�

//���˱�־(��Ϊ��)
///�̶�ֵ��1��0
///1����֧���ɹ����̷���
///0�����첽���ˣ����������ѿ������������Ա
String sharingPayFlag="0";

	//���ɼ���ǩ����
	///����ذ�������˳��͹�����ɼ��ܴ���
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
	//MD5 md5=new MD5();
//String signMsg=md5.getMD5ofStr(signMsgVal);
String signMsg=MD5Util.md5Hex(signMsgVal.getBytes("gb2312")).toUpperCase();

%>
<%!
	//���ܺ�����������ֵ��Ϊ�յĲ�������ַ���
	public String appendParam(String returnStr,String paramId,String paramValue)
	{
			if(!returnStr.equals(""))
			{
				if(!paramValue.equals(""))
				{
					returnStr=returnStr+"&"+paramId+"="+paramValue;
				}
			}
			else
			{
				returnStr=paramId+"="+paramValue;
			}	
			return returnStr;
	}
	//���ܺ�����������ֵ��Ϊ�յĲ�������ַ���������

%>


<!doctype html public "-//w3c//dtd html 4.0 transitional//en" >
<%@page import="com.bill.encrypt.MD5Util"%>
<html>
	<head>
		<title>ʹ�ÿ�Ǯ֧��</title>
		<meta http-equiv="content-type" content="text/html; charset=gb2312" >
	</head>
	
<BODY>
	
	<div align="center">
		<table width="600" border="0" cellpadding="1" cellspacing="1" bgcolor="#CCCCCC" >
			<tr bgcolor="#FFFFFF">
				<td width="80">֧����ʽ:</td>
				<td >��Ǯ[99bill]</td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td >�������:</td>
				<td ><%=orderId %></td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>�������:</td>
				<td><%=orderAmount %></td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>���տ�ս��:</td>
				<td><%=payeeAmount %></td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>��������:</td>
				<td><%=sharingData %></td>
			</tr>
			<tr>
				<td></td>
				<td></td>
			</tr>
	  </table>
	</div>

	<div align="center" style="font-size=12px;font-weight: bold;color=red;">
		<form name="kqPay" action="https://www.99bill.com/msgateway/recvMsgatewayMerchantInfoAction.htm" method="post">
			<input type="hidden" name="inputCharset" value="<%=inputCharset %>"/>
			<input type="hidden" name="bgUrl" value="<%=bgUrl %>"/>
			<input type="hidden" name="pageUrl" value="<%=pageUrl %>"/>
			<input type="hidden" name="version" value="<%=version %>"/>
			<input type="hidden" name="language" value="<%=language %>"/>
			<input type="hidden" name="signType" value="<%=signType %>"/>
			<input type="hidden" name="payeeContactType" value="<%=payeeContactType %>"/>
			<input type="hidden" name="payeeContact" value="<%=payeeContact %>"/>			
			<input type="hidden" name="payerName" value="<%=payerName %>"/>
			<input type="hidden" name="payerContactType" value="<%=payerContactType %>"/>
			<input type="hidden" name="payerContact" value="<%=payerContact %>"/>
			<input type="hidden" name="orderId" value="<%=orderId %>"/>
			<input type="hidden" name="orderAmount" value="<%=orderAmount %>"/>
			<input type="hidden" name="payeeAmount" value="<%=payeeAmount %>"/>
			<input type="hidden" name="orderTime" value="<%=orderTime %>"/>
			<input type="hidden" name="productName" value="<%=productName %>"/>
			<input type="hidden" name="productNum" value="<%=productNum %>"/>
			<input type="hidden" name="productDesc" value="<%=productDesc %>"/>
			<input type="hidden" name="ext1" value="<%=ext1 %>"/>
			<input type="hidden" name="ext2" value="<%=ext2 %>"/>
			<input type="hidden" name="payType" value="<%=payType %>"/>
			<input type="hidden" name="bankId" value="<%=bankId %>"/>
			<input type="hidden" name="pid" value="<%=pid %>"/>
			<input type="hidden" name="sharingData" value="<%=sharingData %>"/>
			<input type="hidden" name="sharingPayFlag" value="<%=sharingPayFlag %>"/>
			<input type="hidden" name="signMsg" value="<%=signMsg %>"/>	
			<input type="submit" name="submit" value="�ύ����Ǯ">
			
		</form>		
	</div>
	
</BODY>
</HTML>