<%@page contentType="text/html; charset=gb2312" language="java"%>
<%
/**
 * @Description: ��Ǯ����ҷ��������̻��տ�ӿڷ���
 * @Copyright (c) �Ϻ���Ǯ��Ϣ�������޹�˾
 * @version 2.0
 */


//����������Կ
///���ִ�Сд.�����Ǯ��ϵ��ȡ
String key="9I67EG8EHN7CBQ3X";

//��ȡ���ذ汾.�̶�ֵ
///�̶�ֵ��v2.0
///���ύ����ʱ�����ذ汾�ű���һ��
String version=(String)request.getParameter("version").trim();

//��ȡ��������.�̶�ѡ��ֵ��
///�̶�ѡ��ֵ��1
///1��������
String language=(String)request.getParameter("language").trim();

//ǩ������.�̶�ֵ
///�̶�ֵ��1
///���ύ����ʱ��ǩ�����ͱ���һ��
String signType=(String)request.getParameter("signType").trim();

//��ȡ֧����ʽ
///�̶�ѡ��ֵ��10 ��12
///10���п�֧����12�����Ǯ�˻����֧��
String payType=(String)request.getParameter("payType").trim();

//��ȡ���д���
///��Ϊ���п�֧��ʱbankId�ֶα�ʾ�û�֧��������
String bankId=(String)request.getParameter("bankId").trim();

//��������û���
///�ύ�˷�������ĺ������ڿ�Ǯ���û���š�
String pid=(String)request.getParameter("pid").trim();

//��ȡ�̻�������
///��ĸ�����֡�-��_ �������
///���ύ����ʱ���̻������ű���һ��
String orderId=(String)request.getParameter("orderId").trim();

//��ȡ�����ύʱ��
///���ִ�
///���ύ����ʱ���̻������ύʱ�䱣��һ��
String orderTime=(String)request.getParameter("orderTime").trim();

//��ȡԭʼ�������
///��������
///�Է�Ϊ��λ���ȷ�10Ԫ���ύʱ���ӦΪ1000���ύ����ʱ���̻���������һ��
String orderAmount=(String)request.getParameter("orderAmount").trim();

//��ȡ��Ǯ���׺�
///���ִ�
///�ý����ڿ�Ǯϵͳ�ж�Ӧ�Ľ��׺�
String dealId=(String)request.getParameter("dealId").trim();

//��ȡ���н��׺�
///���п�֧��ʱ,�������д���˶����Ľ��׺�
String bankDealId=(String)request.getParameter("bankDealId").trim();

//��ȡ�ڿ�Ǯ����ʱ��
///���ִ�
///��Ǯ�Խ��׽��д����ʱ��
String dealTime=(String)request.getParameter("dealTime").trim();

//��ȡʵ��֧�����
///��������
///������ʹ���Ż�ȯ������⣬�û�ʵ��֧���Ľ���Է�Ϊ��λ���ȷ�10Ԫ���ύʱ���ӦΪ1000
String payAmount=(String)request.getParameter("payAmount").trim();

//��ȡ����������
///��������
///����ʱ����������
String fee=(String)request.getParameter("fee").trim();

//���տ��ϵ��ʽ����
///�̶�ѡ��ֵ��1
///1����Email��ַ
String payeeContactType="1";

//���տ��ϵ��ʽ
///�ַ���
///����payerContactType ֵ��Ӧ��дEmail
String payeeContact="";

//���տ�յ����
///��������
///���տ����õ��Ŀ�����
String payeeAmount="";

//��ȡ��չ�ֶ�1
///���ύ����ʱ����չ�ֶ�1����һ��
String ext1=(String)request.getParameter("ext1").trim();

//��ȡ��չ�ֶ�2
///���ύ����ʱ����չ�ֶ�2����һ��
String ext2=(String)request.getParameter("ext2").trim();

//��ȡ������
///10��֧���ɹ�
///11��֧��ʧ��
String payResult=(String)request.getParameter("payResult").trim();

//���˽��
///�ַ���
///��ϸ�ķ��˽����ϸ���ݣ����������ʽ˵����
String sharingResult="";

//��ȡ�������
///���ִ�
///ʧ��ʱ���صĴ�����룬����Ϊ�ա�
///�ο���������
String errCode=(String)request.getParameter("errCode").trim();

//��ȡ����ǩ����
String signMsg=(String)request.getParameter("signMsg").trim();



//���ɼ��ܴ������뱣������˳��
	String merchantSignMsgVal="";
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"version",version);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"language",language);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"signType",signType);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"payType",payType);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"bankId",bankId);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"pid",pid);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"orderId",orderId);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"orderTime",orderTime);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"orderAmount",orderAmount);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"dealId",dealId);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"bankDealId",bankDealId);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"dealTime",dealTime);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"payAmount",payAmount);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"fee",fee);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"payeeContactType",payeeContactType);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"payeeContact",payeeContact);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"payeeAmount",payeeAmount);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"ext1",ext1);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"ext2",ext2);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"payResult",payResult);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"sharingResult",sharingResult);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"errCode",errCode);
	merchantSignMsgVal=appendParam(merchantSignMsgVal,"key",key);
	MD5 md5=new MD5();
//String merchantSignMsg=md5.getMD5ofStr(merchantSignMsgVal);
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
			// �ر�ע�⣺ֻ��signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())����payResult=10���ű�ʾ֧���ɹ���
			//*
			
			//�������Ǯ�����������ṩ��Ҫ�ض���ĵ�ַ��
			rtnOk=1;
			rtnUrl="http://www.yoursite.com/show.jsp?msg=success!";
			break;
		  
		 default:

			rtnOk=1;
			rtnUrl="http://www.yoursite.com/show.jsp?msg=false!";
			break;

	}

}else{

	rtnOk=1;
	rtnUrl="http://www.yoursite.com/show.jsp?msg=error!";

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
				returnStr=paramId+"="+paramValue;
			}	
			return returnStr;
	}
	//���ܺ�����������ֵ��Ϊ�յĲ�������ַ���������


//���±������Ǯ�����������ṩ��Ҫ�ض���ĵ�ַ

%>
<%@page import="com.bill.encrypt.MD5Util"%>
<%@page import="com.bill.encrypt.MD5"%>
<result><%=rtnOk %></result><redirecturl><%=rtnUrl %></redirecturl>