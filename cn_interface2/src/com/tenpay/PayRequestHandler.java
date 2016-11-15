package com.tenpay;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenpay.util.MD5Util;
import com.tenpay.util.TenpayUtil;

/**
 * ��ʱ����������
 * ============================================================================
 * api˵����
 * init(),��ʼ��������Ĭ�ϸ�һЩ������ֵ����cmdno,date�ȡ�
 * getGateURL()/setGateURL(),��ȡ/������ڵ�ַ,����������ֵ
 * getKey()/setKey(),��ȡ/������Կ
 * getParameter()/setParameter(),��ȡ/���ò���ֵ
 * getAllParameters(),��ȡ���в���
 * getRequestURL(),��ȡ������������URL
 * doSend(),�ض��򵽲Ƹ�֧ͨ��
 * getDebugInfo(),��ȡdebug��Ϣ
 * 
 * ============================================================================
 *
 */
public class PayRequestHandler extends RequestHandler {

	public PayRequestHandler(HttpServletRequest request,
			HttpServletResponse response) {
		
		super(request, response); 

		//֧�����ص�ַ
		this.setGateUrl("http://service.tenpay.com/cgi-bin/v3.0/payservice.cgi");
		
	}

	/**
	 * @Override
	 * ��ʼ��������Ĭ�ϸ�һЩ������ֵ����cmdno,date�ȡ�
	 */
	public void init() {

		Date now = new Date();
		SimpleDateFormat dfDay = new SimpleDateFormat("yyyyMMdd");
		String strDay = dfDay.format(now);
		
		//�������
		this.setParameter("cmdno", "1");
		
		//����
		this.setParameter("date",  strDay);
		
		//�̻���
		this.setParameter("bargainor_id", "");
		
		//�Ƹ�ͨ���׵���
		this.setParameter("transaction_id", "");
		
		//�̼Ҷ�����
		this.setParameter("sp_billno", "");
		
		//��Ʒ�۸��Է�Ϊ��λ
		this.setParameter("total_fee", "");
		
		//��������
		this.setParameter("fee_type",  "1");
		
		//����url
		this.setParameter("return_url",  "");
		
		//�Զ������
		this.setParameter("attach",  "");
		
		//�û�ip
		this.setParameter("spbill_create_ip",  "");
		
		//��Ʒ����
		this.setParameter("desc",  "");
		
		//���б���
		this.setParameter("bank_type",  "0");
		
		//�ַ�������
		this.setParameter("cs", "gbk");
		
		//ժҪ
		this.setParameter("sign", "");
	}

	/**
	 * @Override
	 * ����ǩ��
	 */
	protected void createSign() {
		
		//��ȡ����
		String cmdno = this.getParameter("cmdno");
		String date = this.getParameter("date");
		String bargainor_id = this.getParameter("bargainor_id");
		String transaction_id = this.getParameter("transaction_id");
		String sp_billno = this.getParameter("sp_billno");
		String total_fee = this.getParameter("total_fee");
		String fee_type = this.getParameter("fee_type");
		String return_url = this.getParameter("return_url");
		String attach = this.getParameter("attach");
		String spbill_create_ip = this.getParameter("spbill_create_ip");
		String key = this.getKey();
		
		//��֯ǩ��
		StringBuffer sb = new StringBuffer();
		sb.append("cmdno=" + cmdno + "&");
		sb.append("date=" + date + "&");
		sb.append("bargainor_id=" + bargainor_id + "&");
		sb.append("transaction_id=" + transaction_id + "&");
		sb.append("sp_billno=" + sp_billno + "&");
		sb.append("total_fee=" + total_fee + "&");
		sb.append("fee_type=" + fee_type + "&");
		sb.append("return_url=" + return_url + "&");
		sb.append("attach=" + attach + "&");
		if(!"".equals(spbill_create_ip)) {
			sb.append("spbill_create_ip=" + spbill_create_ip + "&");
		}
		sb.append("key=" + key);
		
		String enc = TenpayUtil.getCharacterEncoding(
				this.getHttpServletRequest(), this.getHttpServletResponse());
		//���ժҪ
		String sign = MD5Util.MD5Encode(sb.toString(), enc).toLowerCase();
				
		this.setParameter("sign", sign);
		
		//debug��Ϣ
		this.setDebugInfo(sb.toString() + " => sign:"  + sign);
		
	}
	
	
	
	
	

}
