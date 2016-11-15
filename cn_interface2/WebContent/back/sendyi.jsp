<%@ page contentType="text/html; charset=gb2312" language="java"%>
<%@ page import="com.bill.encrypt.MD5Util"%>
<%
 /**
 * @Description: 快钱分帐网关接口范例
 * @Copyright (c) 上海快钱信息服务有限公司
 * @version 2.0
 */
///请登录快钱系统获取用户编号.
//String merchantAcctId="1000300079901";
//20080115162542
//网关密钥
///区分大小写.请与快钱联系索取
String key="9I67EG8EHN7CBQ3X";

//字符集.固定选择值。可为空。
///固定选择值：1、2、3
///1代表UTF-8； 2代表GBK；3代表gb2312
///默认值为1
String inputCharset="1";

//网关版本.固定值
///固定值：v2.0
///注意为小写字母
String version="v2.0";

//签名类型.固定值
///字符串
///只允许使用字母、数字、- 、_,并以字母或数字开头每商户提交的订单号，必须在自身账户交易中唯一
String signType="1";

//商户订单号
///由字母、数字、或[-][_]组成
String orderId="20100927115136";
//new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

//异步分账的提交时间
///数字串，一共14位
///格式为：年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
///例如：20071117020101
String sharingTime=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

//合作伙伴用户编号
///提交此分账请求的合作方的快钱用户编号
String pid="10003000799";

//分账数据
///字符串
///接收方的快钱账户Email。只允许填写一个。每个Email只能分一次，一旦分了就把原来提交的分账明细中要分给此Email的金额一次性分账到位。
String sharingInfo="bianzhifu@gmail.com";

	//生成加密签名串
	///请务必按照如下顺序和规则组成加密串！
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


<html>
  <head>
      		<title>使用快钱支付</title>
		<meta http-equiv="content-type" content="text/html; charset=gb2312" >
  </head>
  
  <body>
        	<div align="center">
		<table width="259" border="0" cellpadding="1" cellspacing="1" bgcolor="#CCCCCC" >
			<tr bgcolor="#FFFFFF">
				<td width="80">支付方式:</td>
				<td >快钱[99bill]</td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td >订单号:</td>
				<td ><%=orderId %></td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>分账时间:</td>
				<td><%=sharingTime %></td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td>分账数据:</td>
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
			<input type="submit" name="submit" value="提交到快钱">
		</form>		
	</div>
  </body>
</html>
