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
 public class PaypalBack extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
   static final long serialVersionUID = 1L;
   
    /* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#HttpServlet()
	 */
	public PaypalBack() {
		super();
	}   	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String url="";
		String order=request.getParameter("order");
		String token=(String)request.getSession().getAttribute(order);
		String payerid="";
		String tolfee=request.getParameter("tolfee");
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
			encoder.add("METHOD", "GetExpressCheckoutDetails");
			encoder.add("TOKEN",token);
			
			NVPDecoder decoder = new NVPDecoder();
			// Execute the API operation and obtain the response.
			String NVPRequest = encoder.encode();
			String NVPResponse = caller.call(NVPRequest);
			decoder.decode(NVPResponse);
			
			
			String strAck = decoder.get("ACK");
			payerid=decoder.get("PAYERID");
			System.out.println(decoder.get("PAYERID"));
			if(strAck !=null && !(strAck.equals("Success") || strAck.equals("SuccessWithWarning")))	{
	              System.out.println("\n########## CreateRecurringPaymentsProfile call failed ##########\n");
			} else {
				 System.out.println("\n########## CreateRecurringPaymentsProfile call passed ##########\n");
				 System.out.println("https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token="+decoder.get("TOKEN"));
			}
			}catch (Exception e) {
				// TODO: handle exception
			}
			
			
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
				encoder.add("METHOD", "DoExpressCheckoutPayment");
				encoder.add("TOKEN",token);
				encoder.add("PAYERID",payerid);
				encoder.add("PAYMENTACTION","Sale");
				encoder.add("AMT",tolfee);
				
				
				
				
				NVPDecoder decoder = new NVPDecoder();
				// Execute the API operation and obtain the response.
				String NVPRequest = encoder.encode();
				String NVPResponse = caller.call(NVPRequest);
				decoder.decode(NVPResponse);
				
				System.out.println(decoder.get("PAYMENTTYPE"));
				System.out.println(decoder.get("ORDERTIME"));
				System.out.println(decoder.get("AMT"));
				System.out.println(decoder.get("PAYMENTSTATUS"));
				}catch (Exception e) {
					// TODO: handle exception
				}
			response.sendRedirect("http://www.chinaebooking.com/sj_interface/show.jsp");
		
	}  	
	
	/* (non-Java-doc)
	 * @see javax.servlet.http.HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request,response);
	}   	  	    
}