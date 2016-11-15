package com.ccservice.b2b2c.atom.pay;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.bill.encrypt.MD5Util;
import com.ccservice.b2b2c.atom.refund.handle.RefundHandle;
import com.ccservice.b2b2c.atom.refund.helper.Refundhelper;
import com.ccservice.b2b2c.atom.refund.helper.Refundinfo;
import com.ccservice.b2b2c.ben.Refundtrade;
import com.pay.config.BillpayConfig;

/**
 * Servlet implementation class for Servlet: Billpayrefund 快钱订单退款控制。
 * 
 * @author:zouyuanchao
 * @date:2013-07-15
 * 
 */
@SuppressWarnings("serial")
public class Billpayrefund extends RefundSupport implements Refund {

	public Billpayrefund(HttpServletRequest request,
			HttpServletResponse response, Refundhelper refundhelper) {
		super(request, response, refundhelper);
	}

	Log logger = LogFactory.getLog(Alipayrefund.class);
	@Override
	public void refund(){
		response.setContentType("text/plain; charset=utf-8");
		// 人民币网关密钥
		// /区分大小写.请与快钱联系索取
		String key = BillpayConfig.getInstance().getRefundkey();
		// 字符集.固定选择值。可为空。
		// /只能选择1、2、3.
		// /1代表UTF-8; 2代表GBK; 3代表gb2312
		// /默认值为1
		String inputCharset = "3";
		// 网关版本.固定值
		// /固定值：v2.0
		// /注意为小写字母
		String version = "v2.0";
		// 签名类型.固定值
		// /1代表MD5签名
		// /当前版本固定为1
		String signType = "1";
		// 商户订单号
		// /用户支付的原提交的订单号
		String orderId = refundhelper.getOldOrdId();
		// 快钱的合作伙伴的账户号
		// /如未和快钱签订代理合作协议，不需要填写本参数
		String pid = BillpayConfig.getInstance().getPartnerID();
		float refundprice = 0;
		List<Refundinfo> ris = refundhelper.getRefundinfos();
		for (Refundinfo r : ris) {
			refundprice += r.getRefundprice();
		}
		// 退款总金额
		// /整形数值
		// /单位是“分”new
		// String(request.getParameter("backmoney").getBytes(),"UTF-8");
		String returnAllAmount = (int)(refundprice * 100) + "";
		// 退款请求提交时间
		// /格式：yyyymmddHIMMSS
		// /例如：20061013230103
		String returnTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
				.format(new java.util.Date());

		// 退款明细
		// /字符串
		// /详细的退款明细数据，见表后格式说明
		// returnContactType^returnContact^returnAmount^returnDesc
		// returnContactType：固定选择值：1。1 代表Email 地址；
		// returnContact：对应returnContactType 的选择填写email。
		// returnAmount：填写应该退款的金额。单位为分。如100 代表1 元
		// returnDesc：退款备注说明。中文或英文字符串。
		// 加密前，多条退款明细信息之间采用符号 | 进行分隔
		String returnDetail="";
		try {
			returnDetail = new String(getReturnDetail(ris,refundprice).getBytes(), "gb2312");
		} catch (UnsupportedEncodingException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		Refundtrade refundtrade = this.createRefundtrade(returnDetail,
				refundhelper.getRefundinfos());
		// 退款流水号/只允许使用字母、数字、- 、_,并以字母或数字开头每次提交的退款流水号，必须唯一
		String seqId = new java.text.SimpleDateFormat("yyyyMMddHHmmss")
				.format(new java.util.Date())
				+ refundtrade.getId();
		// 扩展字段1
		// /在支付结束后原样返回给商户
		String ext1 = refundtrade.getId() + "";

		// 扩展字段2
		// /在支付结束后原样返回给商户
		String ext2 = "";

		String returnFee = "";
		String result = "";
		String errCode = "";
		String returnData = "";
		// 生成加密签名串
		// /请务必按照如下顺序和规则组成加密串！
		String signMsgVal = "";
		signMsgVal = appendParam(signMsgVal, "inputCharset", inputCharset);
		signMsgVal = appendParam(signMsgVal, "version", version);
		signMsgVal = appendParam(signMsgVal, "signType", signType);
		signMsgVal = appendParam(signMsgVal, "orderId", orderId);
		signMsgVal = appendParam(signMsgVal, "pid", pid);
		signMsgVal = appendParam(signMsgVal, "seqId", seqId);
		signMsgVal = appendParam(signMsgVal, "returnAllAmount", returnAllAmount);
		signMsgVal = appendParam(signMsgVal, "returnTime", returnTime);
		signMsgVal = appendParam(signMsgVal, "ext1", ext1);
		signMsgVal = appendParam(signMsgVal, "ext2", ext2);
		signMsgVal = appendParam(signMsgVal, "returnDetail", returnDetail);
		signMsgVal = appendParam(signMsgVal, "key", key);
		String signMsg="";
		try {
			signMsg = MD5Util.md5Hex(signMsgVal.getBytes("gb2312"))
					.toUpperCase();
		} catch (UnsupportedEncodingException e1) {
		}
		signMsgVal = appendParam(signMsgVal, "signMsg", signMsg);
		String parameter = "https://www.99bill.com/msgateway/recvMerchantRefundAction.htm?"
				+ signMsgVal;
		logger.error("退款请求：" + parameter);
		String returnstr = httpget(parameter, "UTF-8");
		logger.error("请求返回：" + returnstr);
		String handleclass=refundhelper.getProfitHandle().getSimpleName();
		SAXBuilder build = new SAXBuilder();
		Document document;
		boolean refundr=false;
		try {
			document = build.build(new StringReader(returnstr));
			Element root = document.getRootElement();
			orderId = root.getChildText("orderId");
			pid = root.getChildText("pid");
			seqId = root.getChildText("seqId");
			returnAllAmount = root.getChildText("returnAllAmount");
			returnFee = root.getChildText("returnFee");
			result = root.getChildText("result");
			errCode = root.getChildText("errCode");
			returnData = root.getChildText("returnData");
			signMsg = root.getChildText("signMsg");		
			if (result.equals("10")){
				logger.error(orderId + "退款成功");
				refundr=true;			
			}else if (result.equals("11")){
				logger.error(orderId + "退款失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("退款异常");
		}
		try {
			RefundHandle refundhandle=	(RefundHandle) Class.forName(
					RefundHandle.class.getPackage().getName() + "." + handleclass)
					.newInstance();
			refundhandle.refundedHandle(refundr,refundhelper.getOrderid(),seqId);
		} catch(Exception e){
		}
	}
	
	public static String getReturnDetail(List<Refundinfo> refundinfos,float totalrefunprice) {	
		// /1^yihanzhiming@sina.com^20^refundprice
		String royaltysb="";
		float rebate=0;
	
			Refundinfo refundinfo=refundinfos.get(0);
			 Map<String,Float>royalty=refundinfo.getRoyalty_parameters();
			if (royalty != null && royalty.size() > 0) {
				Iterator<Map.Entry<String, Float>> agentiterator = royalty
						.entrySet().iterator();
				for (; agentiterator.hasNext();) {
					Map.Entry<String, Float> entery = agentiterator
							.next();
					String account = entery.getKey();
					float money = entery.getValue();
						royaltysb +="|1^" +account+ "^"+(int)(money*100)
								+ "^退废票返还分润";
						rebate+=money;
				}
			}
			
		
	if(totalrefunprice>rebate){
			royaltysb="1^"+BillpayConfig.getInstance().getSellerEmail()+"^"+(int)((totalrefunprice-rebate)*100)+"^退废票返还分润"+royaltysb;
	}else{
		royaltysb=royaltysb.substring(1);
	}
		return royaltysb;
	}

	/**
	 * 功能函数。将变量值不为空的参数组成字符串
	 * 
	 * @param returnStr
	 * @param paramId
	 * @param paramValue
	 * @return
	 */
	public static String appendParam(String returnStr, String paramId,
			String paramValue) {
		if (!returnStr.equals("")) {
			if (!paramValue.equals("")) {
				returnStr = returnStr + "&" + paramId + "=" + paramValue;
			}
		} else {
			returnStr = paramId + "=" + paramValue;
		}
		return returnStr;
	}

	public static String httpget(String url, String encode) {
		try {
			URL Url = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream in = conn.getInputStream();
			byte[] buf = new byte[2046];
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			int len = 0;
			int size = 0;
			while ((len = in.read(buf)) > 0) {
				bout.write(buf, 0, len);
				size += len;
			}

			in.close();
			conn.disconnect();

			return new String(bout.toByteArray(), encode);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}