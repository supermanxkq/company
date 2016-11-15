package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.tenpay.PayRequestHandler;
import com.tenpay.util.TenpayUtil;

/**
 * Servlet implementation class for Servlet: Tenpay
 *
 */
 public class Tenpay extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Tenpay() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("GBK");
		response.setCharacterEncoding("GBK");
		//�̻���
		String bargainor_id = "1900000109";

		//��Կ
		String key = "8934e7d15453e97507ef794cf7b0519d";

		//�ص�֪ͨURL
		String return_url = "http://www.chinaebooking.com/sj_interface/ten_return_url.jsp";

		//��ǰʱ�� yyyyMMddHHmmss
		String currTime = TenpayUtil.getCurrTime();

		//8λ����
		String strDate = currTime.substring(0, 8);

		//6λʱ��
		String strTime = currTime.substring(8, currTime.length());

		//��λ�����
		String strRandom = TenpayUtil.buildRandom(4) + "";

		//10λ���к�,�������е���
		String strReq = strTime + strRandom;

		//�̼Ҷ�����,����������32λ��ȡǰ32λ���Ƹ�ֻͨ��¼�̼Ҷ����ţ�����֤Ψһ��
		String sp_billno =  request.getParameter("bargainor_id");//strReq;

		//�Ƹ�ͨ���׵��ţ�����Ϊ��10λ�̻���+8λʱ�䣨YYYYmmdd)+10λ��ˮ��
		String transaction_id = bargainor_id + strDate + strReq;

		//����PayRequestHandlerʵ��
		PayRequestHandler reqHandler = new PayRequestHandler(request, response);

		//������Կ
		reqHandler.setKey(key);

		//��ʼ��
		reqHandler.init();

		//-----------------------------
		//����֧������
		//-----------------------------
		reqHandler.setParameter("bargainor_id", bargainor_id);			//�̻���
		reqHandler.setParameter("sp_billno", sp_billno);				//�̼Ҷ�����
		reqHandler.setParameter("transaction_id", transaction_id);		//�Ƹ�ͨ���׵���
		
		reqHandler.setParameter("return_url", return_url);				//֧��֪ͨurl
		reqHandler.setParameter("desc", new String(request.getParameter("desc").getBytes("ISO8859-1"),"UTF-8"));	//��Ʒ���
		reqHandler.setParameter("total_fee", request.getParameter("total_fee"));						//��Ʒ���,�Է�Ϊ��λ

		//�û�ip,���Ի���ʱ��Ҫ�����ip������ʽ�����ټӴ˲���
		//reqHandler.setParameter("spbill_create_ip",request.getRemoteAddr());

		//��ȡ���������url
		String requestUrl = reqHandler.getRequestURL();

		//��ȡdebug��Ϣ
		String debuginfo = reqHandler.getDebugInfo();

		//System.out.println("requestUrl:" + requestUrl);
		//System.out.println("debuginfo:" + debuginfo);
		
		response.sendRedirect(requestUrl);
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}   	  	    
}