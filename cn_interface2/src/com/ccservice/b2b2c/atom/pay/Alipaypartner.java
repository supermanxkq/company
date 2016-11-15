package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.base.util.Util;
import com.pay.config.AlipayConfig;

/**
 * @author hanmenghui
 *  支付宝支付商圈签约。
 */
@SuppressWarnings("serial")
public class Alipaypartner extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		this.doPost(request, response);
	}

	@Override
	public void doPost(HttpServletRequest request,HttpServletResponse response){
		String url="https://mapi.alipay.com/gateway.do";
		String service="sign_protocol_with_partner";
		String partner=AlipayConfig.getInstance().getPartnerID();// 合作者身份ID
		String key=AlipayConfig.getInstance().getKey();
		String _input_charset="UTF-8";
		String sign_type="MD5";
		String email="";
		StringBuilder sb=new StringBuilder(100);
		this.assemblyParam(sb, "email", email);
		this.assemblyParam(sb, "_input_charset", _input_charset);
		this.assemblyParam(sb, "partner", partner);
		this.assemblyParam(sb, "service", service);
		sb.append(key);
		String sign="";
		try {
			 sign=Util.MD5(sb.toString());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		StringBuilder param=new StringBuilder(100);
		this.assemblyParam(param, "email", email);
		this.assemblyParam(param, "partner", partner);
		this.assemblyParam(param, "service", service);
		this.assemblyParam(param, "_input_charset", _input_charset);
		this.assemblyParam(param, "sign_type", sign_type);
		this.assemblyParam(param, "sign", sign);
		try {
			url+="?"+param;
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void assemblyParam(StringBuilder str, String pname, String pvlaue) {
		if (pvlaue != null && pvlaue.length() > 0) {
			if (str.length() > 0) {
				str.append("&" + pname + "=" + pvlaue);

			} else {
				str.append(pname + "=" + pvlaue);
			}
		}
	}

}
