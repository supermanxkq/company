package com.tenpay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenpay.util.MD5Util;
import com.tenpay.util.TenpayUtil;

/**
 * ��ʱ����Ӧ����
 * ============================================================================
 * api˵����
 * getKey()/setKey(),��ȡ/������Կ
 * getParameter()/setParameter(),��ȡ/���ò���ֵ
 * getAllParameters(),��ȡ���в���
 * isTenpaySign(),�Ƿ�Ƹ�ͨǩ��,true:�� false:��
 * doShow(),��ʾ������
 * getDebugInfo(),��ȡdebug��Ϣ
 * 
 * ============================================================================
 *
 */
public class PayResponseHandler extends ResponseHandler {

	public PayResponseHandler(HttpServletRequest request,
			HttpServletResponse response) {
		
		super(request, response);
		
	}

	/**
	 * �Ƿ�Ƹ�ͨǩ��
	 * @Override
	 * @return boolean
	 */
	public boolean isTenpaySign() {
		
		//��ȡ����
		String cmdno = this.getParameter("cmdno");
		String pay_result = this.getParameter("pay_result");
		String date = this.getParameter("date");
		String transaction_id = this.getParameter("transaction_id");
		String sp_billno = this.getParameter("sp_billno");
		String total_fee = this.getParameter("total_fee");		
		String fee_type = this.getParameter("fee_type");
		String attach = this.getParameter("attach");
		String key = this.getKey();
		String tenpaySign = this.getParameter("sign").toLowerCase();
		
		//��֯ǩ����
		StringBuffer sb = new StringBuffer();
		sb.append("cmdno=" + cmdno + "&");
		sb.append("pay_result=" + pay_result + "&");
		sb.append("date=" + date + "&");
		sb.append("transaction_id=" + transaction_id + "&");
		sb.append("sp_billno=" + sp_billno + "&");
		sb.append("total_fee=" + total_fee + "&");
		sb.append("fee_type=" + fee_type + "&");
		sb.append("attach=" + attach + "&");
		sb.append("key=" + key);
		
		String enc = TenpayUtil.getCharacterEncoding(
				this.getHttpServletRequest(), this.getHttpServletResponse());
		//���ժҪ
		String sign = MD5Util.MD5Encode(sb.toString(), enc).toLowerCase();
		
		//debug��Ϣ
		this.setDebugInfo(sb.toString() + " => sign:" + sign +
				" tenpaySign:" + tenpaySign);
		
		return tenpaySign.equals(sign);
	} 
	
}
