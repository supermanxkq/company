package com.ccservice.huamin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.country.Country;

/**
 * 接口二
 * 
 * @author wzc-ccs 华闽--查询国家资料接口
 * 
 */
public class Qcountry {
	public static void main(String[] args) {
		getCountry();
	}

	public static void getCountry() {
		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qcountry&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_country=&p_countryname=";
		System.out.println("URL地址:" + totalurl);
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
				// System.out.println(sb.toString());
				String strReturn = sb.toString().trim();
				conn.disconnect();
				if (strReturn.contains("?")) {
					strReturn = strReturn.replaceFirst("\\?", "");
				}
				parsexml(strReturn);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
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
				// 查询出数据库中所有的国家进行比对
				List<Country> countrys = Server.getInstance().getInterHotelService().findAllCountry(" WHERE C_HUAMINCODE is null ", "", -1, 0);
				for (Element element : countries) {
					Element country = element.getChild("COUNTRY");
					Element countryname = element.getChild("COUNTRYNAME");
					String countrycode = country.getText();
					String countrynames = countryname.getText();
					if (countrynames.equals("") || countrynames.equals("其它")) {
					} else {
						Country cou = new Country();
						cou.setHuamincode(countrycode);
						cou.setZhname(countrynames);
						System.out.println("插入了：" + countrynames);
//						List<Country> countryfromtable = Server.getInstance().getInterHotelService().findAllCountry(
//								" WHERE " + Country.COL_HUAMINCODE + "='" + cou.getHuamincode() + "'", "", -1, 0);
						List<Country> countryfromtable = Server.getInstance().getInterHotelService().findAllCountry(
								" WHERE " + Country.COL_zhname + "='" + cou.getZhname() + "'", "", -1, 0);
						if (countryfromtable.size() > 0) {
							cou.setId(countryfromtable.get(0).getId());
							Server.getInstance().getInterHotelService().updateCountryIgnoreNull(cou);
						} else {
							try {
								Server.getInstance().getInterHotelService().createCountry(cou);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			Element errormessage = result.getChild("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("国家未插入数据", "错误信息:" + resultco + "," + errormessage);
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
