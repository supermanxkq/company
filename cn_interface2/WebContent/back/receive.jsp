<%@page contentType="text/html; charset=gb2312" language="java"%>
<%@ page import="com.bill.encrypt.MD5Util"%>
<%
/**
 * @Description: ��Ǯ�����֧�����ؽӿڷ���
 * @Copyright (c) �Ϻ���Ǯ��Ϣ�������޹�˾
 * @version 2.0
 */

//��ȡ����������˻���
String merchantAcctId=(String)request.getParameter("merchantAcctId").trim();

//���������������Կ
///���ִ�Сд
String key="1234567897654321";

//��ȡ���ذ汾.�̶�ֵ
///��Ǯ����ݰ汾�������ö�Ӧ�Ľӿڴ������
///������汾�Ź̶�Ϊv2.0
String version=(String)request.getParameter("version").trim();

//��ȡ��������.�̶�ѡ��ֵ��
///ֻ��ѡ��1��2��3
///1�������ģ�2����Ӣ��
///Ĭ��ֵΪ1
String language=(String)request.getParameter("language").trim();

//ǩ������.�̶�ֵ
///1����MD5ǩ��
///��ǰ�汾�̶�Ϊ1
String signType=(String)request.getParameter("signType").trim();

//��ȡ֧����ʽ
///ֵΪ��10��11��12��13��14
///00�����֧��������֧��ҳ����ʾ��Ǯ֧�ֵĸ���֧����ʽ���Ƽ�ʹ�ã�10�����п�֧��������֧��ҳ��ֻ��ʾ���п�֧����.11���绰����֧��������֧��ҳ��ֻ��ʾ�绰֧����.12����Ǯ�˻�֧��������֧��ҳ��ֻ��ʾ��Ǯ�˻�֧����.13������֧��������֧��ҳ��ֻ��ʾ����֧����ʽ��.14��B2B֧��������֧��ҳ��ֻ��ʾB2B֧��������Ҫ���Ǯ���뿪ͨ����ʹ�ã�
String payType=(String)request.getParameter("payType").trim();

//��ȡ���д���
///�μ����д����б�
String bankId=(String)request.getParameter("bankId").trim();

//��ȡ�̻�������
String orderId=(String)request.getParameter("orderId").trim();

//��ȡ�����ύʱ��
///��ȡ�̻��ύ����ʱ��ʱ��.14λ���֡���[4λ]��[2λ]��[2λ]ʱ[2λ]��[2λ]��[2λ]
///�磺20080101010101
String orderTime=(String)request.getParameter("orderTime").trim();

//��ȡԭʼ�������
///�����ύ����Ǯʱ�Ľ���λΪ�֡�
///�ȷ�2 ������0.02Ԫ
String orderAmount=(String)request.getParameter("orderAmount").trim();

//��ȡ��Ǯ���׺�
///��ȡ�ý����ڿ�Ǯ�Ľ��׺�
String dealId=(String)request.getParameter("dealId").trim();

//��ȡ���н��׺�
///���ʹ�����п�֧��ʱ�������еĽ��׺š��粻��ͨ������֧������Ϊ��
String bankDealId=(String)request.getParameter("bankDealId").trim();

//��ȡ�ڿ�Ǯ����ʱ��
///14λ���֡���[4λ]��[2λ]��[2λ]ʱ[2λ]��[2λ]��[2λ]
///�磻20080101010101
String dealTime=(String)request.getParameter("dealTime").trim();

//��ȡʵ��֧�����
///��λΪ��
///�ȷ� 2 ������0.02Ԫ
String payAmount=(String)request.getParameter("payAmount").trim();

//��ȡ����������
///��λΪ��
///�ȷ� 2 ������0.02Ԫ
String fee=(String)request.getParameter("fee").trim();

//��ȡ��չ�ֶ�1
String ext1=(String)request.getParameter("ext1").trim();

//��ȡ��չ�ֶ�2
String ext2=(String)request.getParameter("ext2").trim();

//��ȡ������
///10���� �ɹ�11���� ʧ��
String payResult=(String)request.getParameter("payResult").trim();

//��ȡ�������
///��ϸ���ĵ���������б�
String errCode=(String)request.getParameter("errCode").trim();

//��ȡ����ǩ����
String signMsg=(String)request.getParameter("signMsg").trim();



//���ɼ��ܴ������뱣������˳��
	String merchantSignMsgVal="";
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"merchantAcctId",merchantAcctId);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"version",version);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"language",language);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"signType",signType);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"payType",payType);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"bankId",bankId);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"orderId",orderId);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"orderTime",orderTime);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"orderAmount",orderAmount);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"dealId",dealId);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"bankDealId",bankDealId);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"dealTime",dealTime);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"payAmount",payAmount);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"fee",fee);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"ext1",ext1);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"ext2",ext2);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"payResult",payResult);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"errCode",errCode);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"key",key);

String merchantSignMsg=MD5Util.md5Hex(merchantSignMsgVal.getBytes("gb2312")).toUpperCase();


//��ʼ���������ַ
int rtnOk=0;
String rtnUrl="";

//�̼ҽ������ݴ�������ת���̼���ʾ֧�������ҳ��
///���Ƚ���ǩ���ַ�����֤
if(signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())){

	///���Ž���֧������ж�
	switch(Integer.parseInt(payResult)){
	
		  case 10:
			
			//*  
			// �̻���վ�߼������ȷ����¶���֧��״̬Ϊ�ɹ�
			// �ر�ע�⣺ֻ��signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())����payResult=10���ű�ʾ֧���ɹ���ͬʱ������������ύ����ǰ�Ķ��������жԱ�У�顣
			//*
			
			//�������Ǯ�����������ṩ��Ҫ�ض���ĵ�ַ��
			rtnOk=1;
			rtnUrl="http://localhost:8080/lthk_interface/show.jsp?msg=success!";
			break;
		  
		 default:

			rtnOk=1;
			rtnUrl="http://localhost:8080/lthk_interface/show.jsp?msg=false!";
			break;

	}

}else{

	rtnOk=1;
	rtnUrl="http://localhost:8080/lthk_interface/show.jsp?msg=error!";

}

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


//���±������Ǯ�����������ṩ��Ҫ�ض���ĵ�ַ

%>
<result><%=rtnOk %></result><redirecturl><%=rtnUrl %></redirecturl>