package com.ccservice.huamin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * 查询城市 接口三
 * 
 * @author 2012-6-12下午01:33:20
 */
public class Qcity {
	public static void main(String[] args) {

	}

	public static void getCity() {
		String totalurl = "http://123.196.113.28:8034/cn_interface/huaminHotel.jsp?test=qcity&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_city=BJS&p_cityname=";
		URL url = null;
		try {
			url = new URL(totalurl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream inputStream = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				StringBuilder sb = new StringBuilder();
				String str = "";
				while ((str = br.readLine()) != null) {
					sb.append(str);
				}
				String strReturn = sb.toString().trim();
				conn.disconnect();
				if (strReturn.contains("?")) {
					strReturn = strReturn.replaceFirst("\\?", "");
				}
				parsexml(strReturn);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void parsexml(String xmlstr) {
		SAXBuilder build = new SAXBuilder();
		try {
			Document document = build.build(new StringReader(xmlstr));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			List<Element> countries = result.getChildren("COUNTRIES");
			if (countries != null && countries.size() > 0) {
				for (Element element : countries) {
					Element country = element.getChild("COUNTRY");
					String countrycode = country.getText();
					Element countryname = element.getChild("COUNTRYNAME");
					String countrynames = countryname.getText();
					System.out.println(countrycode + ":" + countrynames);
				}
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			Element errormessage = result.getChild("ERROR_MESSAGE");
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
