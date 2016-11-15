package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paypal.sdk.core.nvp.NVPDecoder;
import com.paypal.sdk.core.nvp.NVPEncoder;
import com.paypal.sdk.profiles.APIProfile;
import com.paypal.sdk.profiles.ProfileFactory;
import com.paypal.sdk.services.NVPCallerServices;

/**
 * Servlet implementation class for Servlet: Alipay
 *
 */
 public class Paypal extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public Paypal() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url="";
		try{
			NVPCallerServices caller=new NVPCallerServices();
			APIProfile profile = null;
			profile = ProfileFactory.createSignatureAPIProfile();
			profile.setAPIUsername("bianzh_1280304780_biz_api1.gmail.com");
			profile.setAPIPassword("1280304785");
			profile.setSignature("ALABGdYflDm2CCxmzuVe-gZj1Dv0AvCR6UTF0PpwnhgWxJMqv8seCbKp");
			profile.setEnvironment("sandbox");
	        profile.setSubject("");
			caller.setAPIProfile(profile);

			
			NVPEncoder encoder = new NVPEncoder();
			encoder.add("VERSION", "51.0");
			encoder.add("METHOD", "SetExpressCheckout");
			encoder.add("RETURNURL","http://www.chinaebooking.com/sj_interface/PaypalBack?order="+request.getParameter("ordercode")+"&tolfee="+request.getParameter("paypal_talfee"));
			encoder.add("CANCELURL","http://www.chinaebooking.com");	
			
			encoder.add("AMT",request.getParameter("paypal_talfee"));
			encoder.add("DESC",request.getParameter("ordercode"));
			encoder.add("INVNUM",request.getParameter("ordercode"));
			
			
			
			
			
			NVPDecoder decoder = new NVPDecoder();
			// Execute the API operation and obtain the response.
			String NVPRequest = encoder.encode();
			String NVPResponse = caller.call(NVPRequest);
			decoder.decode(NVPResponse);
			
			
			String strAck = decoder.get("ACK");
			System.out.println(decoder.get("TOKEN"));
			request.getSession().setAttribute(request.getParameter("ordercode"), decoder.get("TOKEN"));
			if(strAck !=null && !(strAck.equals("Success") || strAck.equals("SuccessWithWarning")))	{
	              System.out.println("\n########## CreateRecurringPaymentsProfile call failed ##########\n");
	          	
			} else {
				 System.out.println("\n########## CreateRecurringPaymentsProfile call passed ##########\n");
//				 System.out.println("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+decoder.get("TOKEN"));
//				 System.out.println(URLEncoder.encode("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+decoder.get("TOKEN")));
//				// response.sendRedirect(URLEncoder.encode("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+decoder.get("TOKEN")));
				 url="https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+decoder.get("TOKEN");
				}
			}catch (Exception e) {
				// TODO: handle exception
			}
			if(url.equals(""))
			{
				url="/sj_website/wrong.jsp";
			}
			response.sendRedirect(url);
		
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}   	  	    
}