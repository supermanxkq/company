<%@ page contentType="text/html; charset=gb2312" language="java"%>
<%@ page import="com.bill.encrypt.MD5Util"%>
<%
 /**
 * @Description: ��Ǯ�������ؽӿڷ���
 * @Copyright (c) �Ϻ���Ǯ��Ϣ�������޹�˾
 * @version 2.0
 */
///���¼��Ǯϵͳ��ȡ�û����.
//String merchantAcctId="1000300079901";
//20080115162542
//������Կ
///���ִ�Сд.�����Ǯ��ϵ��ȡ
String key="9I67EG8EHN7CBQ3X";

//�ַ���.�̶�ѡ��ֵ����Ϊ�ա�
///�̶�ѡ��ֵ��1��2��3
///1����UTF-8�� 2����GBK��3����gb2312
///Ĭ��ֵΪ1
String inputCharset="1";

//���ذ汾.�̶�ֵ
///�̶�ֵ��v2.0
///ע��ΪСд��ĸ
String version="v2.0";

//ǩ������.�̶�ֵ
///�ַ���
///ֻ����ʹ����ĸ�����֡�- ��_,������ĸ�����ֿ�ͷÿ�̻��ύ�Ķ����ţ������������˻�������Ψһ
String signType="1";

//�̻�������
///����ĸ�����֡���[-][_]���
String orderId="20100927115136";
//new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

//�첽���˵��ύʱ��
///���ִ���һ��14λ
///��ʽΪ����[4λ]��[2λ]��[2λ]ʱ[2λ]��[2λ]��[2λ]
///���磺20071117020101
String sharingTime=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

//��������û����
///�ύ�˷�������ĺ������Ŀ�Ǯ�û����
String pid="10003000799";

//��������
///�ַ���
///���շ��Ŀ�Ǯ�˻�Email��ֻ������дһ����ÿ��Emailֻ�ܷ�һ�Σ�һ�����˾Ͱ�ԭ���ύ�ķ�����ϸ��Ҫ�ָ���Email�Ľ��һ���Է��˵�λ��
String sharingInfo="bianzhifu@gmail.com";

	//���ɼ���ǩ����
	///����ذ�������˳��͹�����ɼ��ܴ���
	String signMsgVal="";
	signMsgVal=appendParam(signMsgVal,"inputCharset",inputCharset);
	signMsgVal=appendParam(signMsgVal,"version",version);
	signMsgVal=appendParam(signMsgVal,"signType",signType);
	signMsgVal=appendParam(signMsgVal,"orderId",orderId);
	signMsgVal=appendParam(signMsgVal,"sharingTime",sharingTime);
	signMsgVal=appendParam(signMsgVal,"pid",pid);
	signMsgVal=appendParam(signMsgVal,"sharingInfo",sharingInfo);
	signMsgVal=appendParam(signMsgVal,"key",key);

   String signMsg=MD5Util.md5Hex(signMsgVal.getBytes("gb2312")).toUpperCase();
   System.out.println(signMsg);
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


<html>
  <head>
      		<title>ʹ�ÿ�Ǯ֧��</title>
		<meta http-equiv="content-type" content="text/html; charset=gb2312" >
  </head>
  
  <body>
        	<div align="center">
		<table width="259" border="0" cellpadding="1" cellspacing="1" bgcolor="#CCCCCC" >
			<tr bgcolor="#FFFFFF">
				<td width="80">֧����ʽ:</td>
				<td >��Ǯ[99bill]</td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td >������:</td>
				<td ><%=orderId %></td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>����ʱ��:</td>
				<td><%=sharingTime %></td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>��������:</td>
				<td><%=sharingInfo %></td>
			</tr>
			<tr>
				<td></td>
				<td></td>
			</tr>
	  </table>
	</div>
	
	<div align="center" style="font-size=12px;font-weight: bold;color=red;">
		<form name="kqPay" action="https://www.99bill.com/msgateway/recvMerchantSharingAction.htm" method="get">
			<input type="hidden" name="inputCharset" value="<%=inputCharset %>"/>
			<input type="hidden" name="version" value="<%=version %>"/>
			<input type="hidden" name="signType" value="<%=signType %>"/>
			<input type="hidden" name="orderId" value="<%=orderId %>"/>
     		<input type="hidden" name="sharingTime" value="<%=sharingTime %>"/>			
			<input type="hidden" name="pid" value="<%=pid %>"/>
			<input type="hidden" name="sharingInfo" value="<%=sharingInfo %>"/>
			<input type="hidden" name="signMsg" value="<%=signMsg %>"/>
			<input type="submit" name="submit" value="�ύ����Ǯ">
		</form>		
	</div>
  </body>
</html>
