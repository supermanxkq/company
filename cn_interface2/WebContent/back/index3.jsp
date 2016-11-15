<%@ page contentType="text/html; charset=gb2312" language="java"%>
<%
/**
 * @Description: 快钱人民币分账网关商户收款接口范例
 * @Copyright (c) 上海快钱信息服务有限公司
 * @version 2.0
 */


//分账网关密钥
///区分大小写.请与快钱联系索取
String key="9I67EG8EHN7CBQ3X";

//字符集.固定选择值(可为空)
///固定选择值：1、2、31 代表UTF-8； 2 代表GBK；3 代表gb2312默认值为1
String inputCharset="3";

//接受分账结果的页面地址(可为空)
///需要是绝对地址，与bgurl 不能同时为空当bgUrl 为空时，快钱直接将分账结果post 到pageUrl当bgUrl 不为空时，按照bgUrl 的方式返回
String pageUrl="";

//服务器接受分账结果的后台地址(可为空)
///需要是绝对地址，与bgurl 不能同时为空快钱将分账结果发送到bgUrl 对应的地址，并且获取商家按照约定格式输出的地址，显示页面给用户
String bgUrl="http://www.yoursite.com/receive.jsp";

//网关版本(不为空)
///固定值：v2.0
//注意为小写字母
String version="v2.0";

//语言种类.固定选择值(可为空)。
///固定值：1
///1 代表中文
String language="1";

//签名类型.固定值(不为空)
///固定值：1
///1代表MD5加密签名方式
String signType="1";

//主收款方联系方式类型(不为空)
///固定选择值：1
///1代表Email地址
String payeeContactType="1";

//主收款方联系方式(不为空)
///当payeeContactType=1 时输入Email 地址
String payeeContact ="kqkqklqcom@126.com";
  
//支付人姓名(可为空)
///字符串
///可为中文或英文字符
String payerName="payerName";

//支付人联系方式类型.固定选择值(可为空)
///固定选择值：1
///1代表Email地址
String payerContactType="1";

//支付人联系方式(可为空)
///字符串
///根据payerContactType值对应填写
String payerContact="kquser@126.com";

//商户订单号(不为空)
///字符串
///只允许使用字母、数字、- 、_,并以字母或数字开头每商家提交的订单号，必须在自身账户交易中唯一
String orderId=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

//商户订单金额(不为空)
///整型数字
///以分为单位。比方10 元，提交时金额应为1000
String orderAmount="3";

//主收款方应收额(不为空)
///整型数字
///以分为单位。比方10 元，提交时金额应为1000
String payeeAmount="2";
	
//订单提交时间(不为空)
///数字串，一共14位
///格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
///例如：20071117020101
String orderTime=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

//商品名称(可为空)
///可为中文或英文字符
String productName="productName";

//商品数量(可为空)
///可为空，非空时必须为数字
String productNum="1";

//商品描述(可为空)
///英文或中文字符串
String productDesc="";
	
//扩展字段1(可为空)
///在支付结束后原样返回给商户
String ext1="";

//扩展字段2(可为空)
///在支付结束后原样返回给商户
String ext2="";
	
//支付方式.固定选择值(不为空)
///00：组合支付（网关支付页面显示银行卡支付和快钱账户）
///10：银行卡支付
///12：快钱账户支付
String payType="00";

//银行代码(可为空)
///字符串
///payType=10时此字段填写银行代码就表示相应银行直连。请参考银行代码对照表。只有开通银行直联的商家才可以使用此功能。普通接口不需填写。
String bankId="";

//合作伙伴用户编号(不为空)
///数字串
///提交此分账请求的合作方在快钱的用户编号。
String pid="10003000799";

//分账数据(不为空)
///字符串
///详细的分账明细数据，见表后格式说明
String sharingData="1^kquser@126.com^1^0^test";//数据说明见文档

//分账标志(不为空)
///固定值：1、0
///1代表支付成功立刻分账
///0代表异步分账，即不立即把款项分配给相关人员
String sharingPayFlag="0";

	//生成加密签名串
	///请务必按照如下顺序和规则组成加密串！
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
	//功能函数。将变量值不为空的参数组成字符串
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
	//功能函数。将变量值不为空的参数组成字符串。结束

%>


<!doctype html public "-//w3c//dtd html 4.0 transitional//en" >
<%@page import="com.bill.encrypt.MD5Util"%>
<html>
	<head>
		<title>使用快钱支付</title>
		<meta http-equiv="content-type" content="text/html; charset=gb2312" >
	</head>
	
<BODY>
	
	<div align="center">
		<table width="600" border="0" cellpadding="1" cellspacing="1" bgcolor="#CCCCCC" >
			<tr bgcolor="#FFFFFF">
				<td width="80">支付方式:</td>
				<td >快钱[99bill]</td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td >订单编号:</td>
				<td ><%=orderId %></td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>订单金额:</td>
				<td><%=orderAmount %></td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>主收款方收金额:</td>
				<td><%=payeeAmount %></td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>分账数据:</td>
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
			<input type="submit" name="submit" value="提交到快钱">
			
		</form>		
	</div>
	
</BODY>
</HTML>