package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bill.encrypt.MD5Util;
import com.pay.config.BillpayConfig;

/**
 * Servlet implementation class for Servlet: Billpay
 *
 */
 public class Billpaytui extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 2222221L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Billpaytui() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		



		//人民币网关密钥
		///区分大小写.请与快钱联系索取
		String key=BillpayConfig.getInstance().getKey();

		//字符集.固定选择值。可为空。
		///只能选择1、2、3.
		///1代表UTF-8; 2代表GBK; 3代表gb2312
		///默认值为1
		String inputCharset="3";

		//网关版本.固定值
		///固定值：v2.0
		///注意为小写字母
		String version="v2.0";

		//签名类型.固定值
		///1代表MD5签名
		///当前版本固定为1
		String signType="1";

		//商户订单号
		///用户支付的原提交的订单号
		String orderId="20080311214626";

		//快钱的合作伙伴的账户号
		///如未和快钱签订代理合作协议，不需要填写本参数
		String pid=BillpayConfig.getInstance().getPartnerID();

		//退款流水号
		///字符串
		///只允许使用字母、数字、- 、_,并以字母或数字开头每次提交的退款流水号，必须唯一
		String seqId=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

		//退款总金额
		///整形数值
		///单位是“分”
		String returnAllAmount="1";

		//退款请求提交时间
		///格式：yyyymmddHIMMSS
		///例如：20061013230103
		String returnTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
			
		//扩展字段1
		///在支付结束后原样返回给商户
		String ext1="";

		//扩展字段2
		///在支付结束后原样返回给商户
		String ext2="";

		//退款明细
		///字符串
		///详细的退款明细数据，见表后格式说明
		String returnDetail="1^"+BillpayConfig.getInstance().getSellerEmail()+"^1^";

		//生成加密签名串
		///请务必按照如下顺序和规则组成加密串！
		String signMsgVal="";
		signMsgVal=appendParam(signMsgVal,"inputCharset",inputCharset);
		signMsgVal=appendParam(signMsgVal,"version",version);
		signMsgVal=appendParam(signMsgVal,"signType",signType);
		signMsgVal=appendParam(signMsgVal,"orderId",orderId);
		signMsgVal=appendParam(signMsgVal,"pid",pid);
		signMsgVal=appendParam(signMsgVal,"seqId",seqId);
		signMsgVal=appendParam(signMsgVal,"returnAllAmount",returnAllAmount);
		signMsgVal=appendParam(signMsgVal,"returnTime",returnTime);
		signMsgVal=appendParam(signMsgVal,"ext1",ext1);
		signMsgVal=appendParam(signMsgVal,"ext2",ext2);
		signMsgVal=appendParam(signMsgVal,"returnDetail",returnDetail);
		signMsgVal=appendParam(signMsgVal,"key",key);
		
		String signMsg=MD5Util.md5Hex(signMsgVal.getBytes("gb2312")).toUpperCase();
		
		//post参数
		String signMsgValstr="";
		signMsgValstr=appendParam(signMsgValstr,"inputCharset",inputCharset);
		signMsgValstr=appendParam(signMsgValstr,"version",version);
		signMsgValstr=appendParam(signMsgValstr,"signType",signType);
		signMsgValstr=appendParam(signMsgValstr,"orderId",orderId);
		signMsgValstr=appendParam(signMsgValstr,"pid",pid);
		signMsgValstr=appendParam(signMsgValstr,"seqId",seqId);
		signMsgValstr=appendParam(signMsgValstr,"returnAllAmount",returnAllAmount);
		signMsgValstr=appendParam(signMsgValstr,"returnTime",returnTime);
		signMsgValstr=appendParam(signMsgValstr,"ext1",ext1);
		signMsgValstr=appendParam(signMsgValstr,"ext2",ext2);
		signMsgValstr=appendParam(signMsgValstr,"returnDetail",returnDetail);
		
		String parameter="https://www.99bill.com/msgateway/recvMerchantRefundAction.htm?"+signMsgValstr+"&signMsg="+signMsg;
		System.out.println(parameter);
		response.sendRedirect(parameter);
	        
	        
		
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}   
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
				if(!paramValue.equals(""))
				{
				returnStr=paramId+"="+paramValue;
				}
			}	
			return returnStr;
	}
	//功能函数。将变量值不为空的参数组成字符串。结束
}