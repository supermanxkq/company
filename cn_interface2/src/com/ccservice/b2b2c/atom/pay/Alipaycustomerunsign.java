package com.ccservice.b2b2c.atom.pay;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.pay.config.AlipayConfig;

/**
 * @author hanmenghui
 *  支付宝支付商圈解约。
 *  说明：签入的协议解约参数为：biz_type、user_email，两个参数均为必填参数。其中biz_type需赋值为10004。
 */
@SuppressWarnings("serial")
public class Alipaycustomerunsign  {

	static Logger logger = Logger.getLogger(Alipaycustomerunsign.class.getSimpleName());
	public static  boolean customerunsign(String user_email){
		String paygateway="https://mapi.alipay.com/gateway.do?";
		String service="customer_unsign";
		String partner=AlipayConfig.getInstance().getPartnerID();// 合作者身份ID
		String key=AlipayConfig.getInstance().getKey();
		String biz_type="10004";
		String input_charset = AlipayConfig.getInstance().getCharSet();
		Map<String,String> params=new HashMap<String,String>();
		params.put("paygateway", paygateway);
		params.put("user_email", user_email);
		params.put("biz_type", biz_type);
		params.put("partner", partner);
		params.put("service", service);
		params.put("_input_charset", input_charset);
		String ItemUrl_Get = com.alipay.util.Payment.CreateUrl(params, key);
		try {
			URL neturl = new URL(ItemUrl_Get);
			HttpURLConnection connection = (HttpURLConnection) neturl
					.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			SAXReader reader = new SAXReader();
			Document document = reader.read(connection.getInputStream());
			Element root = document.getRootElement();
			String is_success = root.elementTextTrim("is_success");
			logger.error(user_email+"解约"+is_success);
			if (is_success.equals("T")) {
					return true;
				
			} else {
				String error=root.elementTextTrim("error");
				logger.error(user_email+"解约"+is_success+error);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public  static void assemblyParam(StringBuilder str, String pname, String pvlaue) {
		if (pvlaue != null && pvlaue.length() > 0) {
			if (str.length() > 0) {
				str.append("&" + pname + "=" + pvlaue);

			} else {
				str.append(pname + "=" + pvlaue);
			}
		}
	}

}
