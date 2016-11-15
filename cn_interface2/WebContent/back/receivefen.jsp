<%@page contentType="text/html; charset=gb2312" language="java"%>
<%
/**
 * @Description: 快钱人民币分账网关商户收款接口范例
 * @Copyright (c) 上海快钱信息服务有限公司
 * @version 2.0
 */


//分账网关密钥
///区分大小写.请与快钱联系索取
String key="9I67EG8EHN7CBQ3X";

//获取网关版本.固定值
///固定值：v2.0
///与提交订单时的网关版本号保持一致
String version=(String)request.getParameter("version").trim();

//获取语言种类.固定选择值。
///固定选择值：1
///1代表中文
String language=(String)request.getParameter("language").trim();

//签名类型.固定值
///固定值：1
///与提交订单时的签名类型保持一致
String signType=(String)request.getParameter("signType").trim();

//获取支付方式
///固定选择值：10 、12
///10银行卡支付；12代表快钱账户余额支付
String payType=(String)request.getParameter("payType").trim();

//获取银行代码
///当为银行卡支付时bankId字段表示用户支付的银行
String bankId=(String)request.getParameter("bankId").trim();

//合作伙伴用户号
///提交此分账请求的合作方在快钱的用户编号。
String pid=(String)request.getParameter("pid").trim();

//获取商户订单号
///字母、数字、-、_ 及其组合
///与提交订单时的商户订单号保持一致
String orderId=(String)request.getParameter("orderId").trim();

//获取订单提交时间
///数字串
///与提交订单时的商户订单提交时间保持一致
String orderTime=(String)request.getParameter("orderTime").trim();

//获取原始订单金额
///整型数字
///以分为单位。比方10元，提交时金额应为1000与提交订单时的商户订单金额保持一致
String orderAmount=(String)request.getParameter("orderAmount").trim();

//获取快钱交易号
///数字串
///该交易在快钱系统中对应的交易号
String dealId=(String)request.getParameter("dealId").trim();

//获取银行交易号
///银行卡支付时,返回银行处理此订单的交易号
String bankDealId=(String)request.getParameter("bankDealId").trim();

//获取在快钱交易时间
///数字串
///快钱对交易进行处理的时间
String dealTime=(String)request.getParameter("dealTime").trim();

//获取实际支付金额
///整型数字
///返回在使用优惠券等情况外，用户实际支付的金额以分为单位。比方10元，提交时金额应为1000
String payAmount=(String)request.getParameter("payAmount").trim();

//获取交易手续费
///整型数字
///交易时所产生费用
String fee=(String)request.getParameter("fee").trim();

//主收款方联系方式类型
///固定选择值：1
///1代表Email地址
String payeeContactType="1";

//主收款方联系方式
///字符串
///根据payerContactType 值对应填写Email
String payeeContact="";

//主收款方收到金额
///整型数字
///主收款所得到的款项金额
String payeeAmount="";

//获取扩展字段1
///与提交订单时的扩展字段1保持一致
String ext1=(String)request.getParameter("ext1").trim();

//获取扩展字段2
///与提交订单时的扩展字段2保持一致
String ext2=(String)request.getParameter("ext2").trim();

//获取处理结果
///10：支付成功
///11：支付失败
String payResult=(String)request.getParameter("payResult").trim();

//分账结果
///字符串
///详细的分账结果明细数据，规则见表后格式说明：
String sharingResult="";

//获取错误代码
///数字串
///失败时返回的错误代码，可以为空。
///参考错误代码表
String errCode=(String)request.getParameter("errCode").trim();

//获取加密签名串
String signMsg=(String)request.getParameter("signMsg").trim();



//生成加密串。必须保持如下顺序。
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


//初始化结果及地址
int rtnOk=0;
String rtnUrl="";

//商家进行数据处理，并跳转会商家显示支付结果的页面
///首先进行签名字符串验证
if(signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())){

	///接着进行支付结果判断
	switch(Integer.parseInt(payResult)){
	
		  case 10:
			
			//*  
			// 商户网站逻辑处理，比方更新订单支付状态为成功
			// 特别注意：只有signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())，且payResult=10，才表示支付成功！
			//*
			
			//报告给快钱处理结果，并提供将要重定向的地址。
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


//以下报告给快钱处理结果，并提供将要重定向的地址

%>
<%@page import="com.bill.encrypt.MD5Util"%>
<%@page import="com.bill.encrypt.MD5"%>
<result><%=rtnOk %></result><redirecturl><%=rtnUrl %></redirecturl>