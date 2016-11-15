package com.ccservice.b2b2c.atom.refund.handle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.util.Alipay_fuction;
import com.pay.config.AlipayConfig;

/**
 * @author Administrator
 * 停用。
 *
 */
@SuppressWarnings("serial")
public class AlipayrefundNotifyHandle extends HttpServlet{
	
	@Override
	public void doGet(HttpServletRequest request,HttpServletResponse response){
		
		this.doPost(request, response);
		
	}
	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response){
		PrintWriter out=null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 获取支付宝POST过来反馈信息
		Map<String, String> params = new HashMap<String, String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			// 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			// valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
			params.put(name, valueStr);
		}
		//notify_time,notify_type,notify_id,sign_type,sign,batch_no,success_num,
		// result_details,unfreezed_details
		String batch_no =request.getParameter("batch_no");// 批次号
		String result_details = request.getParameter("result_details");// 处理结果详情
		String status = "";
		String remark = "";
		String key = AlipayConfig.getInstance().getKey();
		String partner=AlipayConfig.getInstance().getPartnerID();
		String charset = "";
		String alipayNotifyURL = "http://notify.alipay.com/trade/notify_query.do?"
			+ "partner="+ partner+ "&notify_id="+ request.getParameter("notify_id");
	    String responseTxt = Alipay_fuction.checkurl(alipayNotifyURL);
		String sign=request.getParameter("sign");
		String mysign=Alipay_fuction.sign(params, key);
		if (sign.equals(mysign)&&responseTxt.equals("true")) {// 验证成功
			// 原付款交易号^退交易金额^处理结果码^是否充退^充退处理结果。SUCCESS
			// 其中是否充退的可选值：true/false；充退处理结果的可选值：S（成功）、F（失败）、P（处理中）
			if (result_details != null
					&& !result_details.trim().equalsIgnoreCase("")) {
				String[] batch_Data = result_details.split("#");
				for (int loop = 0; loop < batch_Data.length; loop++) {
					String[] layDetails = batch_Data[loop].split("[$|$$]");
					for (int dLoop = 0; dLoop < layDetails.length; dLoop++) {
						if (dLoop == 0) {
							String[] details = layDetails[0].split("\\^");
							if (details.length >= 3) {
								if (details[2].equalsIgnoreCase("SUCCESS")) {
									System.out.println("我来处理");
									//成功,加入业务逻辑
								}else{
									remark = details[2];
									if (details[2].equalsIgnoreCase("TRADE_HAS_CLOSED")) {
										out.write("success");
									}
								}
							}
						} else {}
					}
				}
			} else {
				remark = "退款处理详情为空";
			}
			
			 out.write("success");
			 
			// 更新退款状态
		} else {
			 out.write("fail");
		}
		out.flush();
		out.close();
	}
}
