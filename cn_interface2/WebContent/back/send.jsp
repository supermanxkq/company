<%@ page contentType="text/html; charset=gb2312" language="java"%>
<%@ page import="com.bill.encrypt.MD5Util"%>
<%
/**
 * @Description: ��Ǯ�����֧�����ؽӿڷ���
 * @Copyright (c) �Ϻ���Ǯ��Ϣ�������޹�˾
 * @version 2.0
 */

//����������˻���
///���¼��Ǯϵͳ��ȡ�û���ţ��û���ź��01��Ϊ����������˻��š�
String merchantAcctId="1000300079901";

//�����������Կ
///���ִ�Сд.�����Ǯ��ϵ��ȡ
String key="GQWCL37T2IYEBYGF";

//�ַ���.�̶�ѡ��ֵ����Ϊ�ա�
///ֻ��ѡ��1��2��3.
///1����UTF-8; 2����GBK; 3����gb2312
///Ĭ��ֵΪ1
String inputCharset="3";

//����֧�������ҳ���ַ.��[bgUrl]����ͬʱΪ�ա������Ǿ��Ե�ַ��
///���[bgUrl]Ϊ�գ���Ǯ��֧�����Post��[pageUrl]��Ӧ�ĵ�ַ��
///���[bgUrl]��Ϊ�գ�����[bgUrl]ҳ��ָ����<redirecturl>��ַ��Ϊ�գ���ת��<redirecturl>��Ӧ�ĵ�ַ
String pageUrl="";

//����������֧������ĺ�̨��ַ.��[pageUrl]����ͬʱΪ�ա������Ǿ��Ե�ַ��
///��Ǯͨ�����������ӵķ�ʽ�����׽�����͵�[bgUrl]��Ӧ��ҳ���ַ�����̻�������ɺ������<result>���Ϊ1��ҳ���ת��<redirecturl>��Ӧ�ĵ�ַ��
///�����Ǯδ���յ�<redirecturl>��Ӧ�ĵ�ַ����Ǯ����֧�����post��[pageUrl]��Ӧ��ҳ�档
String bgUrl="http://localhost:8080/lthk_interface/receive.jsp";
	
//���ذ汾.�̶�ֵ
///��Ǯ����ݰ汾�������ö�Ӧ�Ľӿڴ������
///������汾�Ź̶�Ϊv2.0
String version="v2.0";

//��������.�̶�ѡ��ֵ��
///ֻ��ѡ��1��2��3
///1�������ģ�2����Ӣ��
///Ĭ��ֵΪ1
String language="1";

//ǩ������.�̶�ֵ
///1����MD5ǩ��
///��ǰ�汾�̶�Ϊ1
String signType="1";
   
//֧��������
///��Ϊ���Ļ�Ӣ���ַ�
String payerName="payerName";

//֧������ϵ��ʽ����.�̶�ѡ��ֵ
///ֻ��ѡ��1
///1����Email
String payerContactType="1";

//֧������ϵ��ʽ
///ֻ��ѡ��Email���ֻ���
String payerContact="";

//�̻�������
///����ĸ�����֡���[-][_]���
String orderId=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

//�������
///�Է�Ϊ��λ����������������
///�ȷ�2������0.02Ԫ
String orderAmount="2";
	
//�����ύʱ��
///14λ���֡���[4λ]��[2λ]��[2λ]ʱ[2λ]��[2λ]��[2λ]
///�磻20080101010101
String orderTime=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

//��Ʒ����
///��Ϊ���Ļ�Ӣ���ַ�
String productName="productName";

//��Ʒ����
///��Ϊ�գ��ǿ�ʱ����Ϊ����
String productNum="1";

//��Ʒ����
///��Ϊ�ַ���������
String productId="";

//��Ʒ����
String productDesc="";
	
//��չ�ֶ�1
///��֧��������ԭ�����ظ��̻�
String ext1="";

//��չ�ֶ�2
///��֧��������ԭ�����ظ��̻�
String ext2="";
	
//֧����ʽ.�̶�ѡ��ֵ
///ֻ��ѡ��00��10��11��12��13��14
///00�����֧��������֧��ҳ����ʾ��Ǯ֧�ֵĸ���֧����ʽ���Ƽ�ʹ�ã�10�����п�֧��������֧��ҳ��ֻ��ʾ���п�֧����.11���绰����֧��������֧��ҳ��ֻ��ʾ�绰֧����.12����Ǯ�˻�֧��������֧��ҳ��ֻ��ʾ��Ǯ�˻�֧����.13������֧��������֧��ҳ��ֻ��ʾ����֧����ʽ��.14��B2B֧��������֧��ҳ��ֻ��ʾB2B֧��������Ҫ���Ǯ���뿪ͨ����ʹ�ã�
String payType="00";

//���д���
///ʵ��ֱ����ת������ҳ��ȥ֧��,ֻ��payType=10ʱ�������ò���
///�������μ� �ӿ��ĵ����д����б�
String bankId="";

//ͬһ������ֹ�ظ��ύ��־
///�̶�ѡ��ֵ�� 1��0
///1����ͬһ������ֻ�����ύ1�Σ�0��ʾͬһ��������û��֧���ɹ���ǰ���¿��ظ��ύ��Ρ�Ĭ��Ϊ0����ʵ�ﹺ�ﳵ�������̻�����0�������Ʒ���̻�����1
String redoFlag="0";

//��Ǯ�ĺ��������˻���
///��δ�Ϳ�Ǯǩ���������Э�飬����Ҫ��д������
String pid="";


	//���ɼ���ǩ����
	///����ذ�������˳��͹�����ɼ��ܴ���
	String signMsgVal="";
	signMsgVal=appendParam(signMsgVal,"inputCharset",inputCharset);
	signMsgVal=appendParam(signMsgVal,"pageUrl",pageUrl);
	signMsgVal=appendParam(signMsgVal,"bgUrl",bgUrl);
	signMsgVal=appendParam(signMsgVal,"version",version);
	signMsgVal=appendParam(signMsgVal,"language",language);
	signMsgVal=appendParam(signMsgVal,"signType",signType);
	signMsgVal=appendParam(signMsgVal,"merchantAcctId",merchantAcctId);
	signMsgVal=appendParam(signMsgVal,"payerName",payerName);
	signMsgVal=appendParam(signMsgVal,"payerContactType",payerContactType);
	signMsgVal=appendParam(signMsgVal,"payerContact",payerContact);
	signMsgVal=appendParam(signMsgVal,"orderId",orderId);
	signMsgVal=appendParam(signMsgVal,"orderAmount",orderAmount);
	signMsgVal=appendParam(signMsgVal,"orderTime",orderTime);
	signMsgVal=appendParam(signMsgVal,"productName",productName);
	signMsgVal=appendParam(signMsgVal,"productNum",productNum);
	signMsgVal=appendParam(signMsgVal,"productId",productId);
	signMsgVal=appendParam(signMsgVal,"productDesc",productDesc);
	signMsgVal=appendParam(signMsgVal,"ext1",ext1);
	signMsgVal=appendParam(signMsgVal,"ext2",ext2);
	signMsgVal=appendParam(signMsgVal,"payType",payType);
	signMsgVal=appendParam(signMsgVal,"bankId",bankId);
	signMsgVal=appendParam(signMsgVal,"redoFlag",redoFlag);
	signMsgVal=appendParam(signMsgVal,"pid",pid);
	signMsgVal=appendParam(signMsgVal,"key",key);

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
				if(!paramValue.equals(""))
				{
				returnStr=paramId+"="+paramValue;
				}
			}	
			return returnStr;
	}
	//���ܺ�����������ֵ��Ϊ�յĲ�������ַ���������

%>


<!doctype html public "-//w3c//dtd html 4.0 transitional//en" >
<html>
	<head>
		<title>ʹ�ÿ�Ǯ֧��</title>
		<meta http-equiv="content-type" content="text/html; charset=gb2312" >
	</head>
	
<BODY>
	
	<div align="center">
		<table width="259" border="0" cellpadding="1" cellspacing="1" bgcolor="#CCCCCC" >
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
				<td>֧����:</td>
				<td><%=payerName %></td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>��Ʒ����:</td>
				<td><%=productName %></td>
			</tr>
			<tr>
				<td></td>
				<td></td>
			</tr>
	  </table>
	</div>

	<div align="center" style="font-size=12px;font-weight: bold;color=red;">
		<form name="kqPay" action="https://www.99bill.com/gateway/recvMerchantInfoAction.htm" method="post">
			<input type="hidden" name="inputCharset" value="<%=inputCharset %>"/>
			<input type="hidden" name="bgUrl" value="<%=bgUrl %>"/>
			<input type="hidden" name="pageUrl" value="<%=pageUrl %>"/>
			<input type="hidden" name="version" value="<%=version %>"/>
			<input type="hidden" name="language" value="<%=language %>"/>
			<input type="hidden" name="signType" value="<%=signType %>"/>
			<input type="hidden" name="signMsg" value="<%=signMsg %>"/>
			<input type="hidden" name="merchantAcctId" value="<%=merchantAcctId %>"/>
			<input type="hidden" name="payerName" value="<%=payerName %>"/>
			<input type="hidden" name="payerContactType" value="<%=payerContactType %>"/>
			<input type="hidden" name="payerContact" value="<%=payerContact %>"/>
			<input type="hidden" name="orderId" value="<%=orderId %>"/>
			<input type="hidden" name="orderAmount" value="<%=orderAmount %>"/>
			<input type="hidden" name="orderTime" value="<%=orderTime %>"/>
			<input type="hidden" name="productName" value="<%=productName %>"/>
			<input type="hidden" name="productNum" value="<%=productNum %>"/>
			<input type="hidden" name="productId" value="<%=productId %>"/>
			<input type="hidden" name="productDesc" value="<%=productDesc %>"/>
			<input type="hidden" name="ext1" value="<%=ext1 %>"/>
			<input type="hidden" name="ext2" value="<%=ext2 %>"/>
			<input type="hidden" name="payType" value="<%=payType %>"/>
			<input type="hidden" name="bankId" value="<%=bankId %>"/>
			<input type="hidden" name="redoFlag" value="<%=redoFlag %>"/>
			<input type="hidden" name="pid" value="<%=pid %>"/>
				
			<input type="submit" name="submit" value="�ύ����Ǯ">
			
		</form>		
	</div>
	
</BODY>
</HTML>