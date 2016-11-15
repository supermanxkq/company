package com.ccservice.huamin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.country.Country;
import com.ccservice.b2b2c.base.province.Province;

/**
 * 接口一 华闽--获取国家城市数据 暂时只接入国内酒店城市信息接口
 * 
 * @author wzc-ccs
 */
public class Qcountryareacity {
	public static void main(String[] args) {
		//中国
		getCountryareacity("CHN", "");
		//香港
		getCountryareacity("HKG", "");
		//台湾
		getCountryareacity("TWN", "");
		//澳门
		getCountryareacity("MFM", "");
	}

	public static void getCountryareacity(String countrycode, String countryname) {
		// String totalurl =
		// "http://123.196.113.28:8034/cn_interface/huaminHotel.jsp?test=qcountryareacity&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM";
		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qcountryareacity&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM";
		if (countrycode != null) {
			totalurl += "&p_country=" + countrycode;
		}
		if (countryname != null) {
			totalurl += "&p_countryname=" + countryname;
		}
		System.out.println("URL地址:" + totalurl);
		URL url = null;
		try {
			url = new URL(totalurl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				InputStreamReader isr = null;
				isr = new InputStreamReader(is);
				BufferedReader in = new BufferedReader(isr);
				StringWriter out = new StringWriter();
				int c = -1;

				while ((c = in.read()) != -1) {
					out.write(c);
				}
				String strReturn = out.toString();
				conn.disconnect();
				if (strReturn.indexOf("?") > 0) {
					strReturn = strReturn.replaceFirst("\\?", "");
				}
				parsexml(countrycode, strReturn);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void parsexml(String countrycode, String xmlstr) {
		// 解析器
		SAXBuilder build = new SAXBuilder();
		try {
			Document doc = build.build(new StringReader(xmlstr.trim()));
			Element lists = doc.getRootElement();
			Element result = lists.getChild("XML_RESULT");
			Element countries = result.getChild("COUNTRIES");
			List<Country> countryfromtable = Server.getInstance().getInterHotelService().findAllCountry(
					" WHERE " + Country.COL_HUAMINCODE + "='" + countrycode.trim() + "'", "", -1, 0);
			if (countries != null) {
				// Element country = countries.getChild("COUNTRY");
				// Element countryname = countries.getChild("COUNTRYNAME");
				List<Element> areas = countries.getChildren("AREAS");
				if (areas != null && areas.size() > 0)
					for (Element element : areas) {
						Element area = element.getChild("AREA");
						Element areaname = element.getChild("AREANAME");
						String areacode = area.getText();
						String areanamestr = areaname.getText();

						System.out.println("需要插入的省份名称:" + areanamestr);
						Province province = new Province();
						if(areanamestr.equals("新疆维吾尔自治区")){
							province.setName("新疆");
						}else if(areanamestr.equals("内蒙古自治区")){
							province.setName("内蒙古");
						}else if(areanamestr.equals("宁夏回族自治区")){
							province.setName("宁夏");
						}else{
							province.setName(areanamestr);
						}
						province.setHuamincode(areacode.trim());
						System.out.println("~~~" + province.getHuamincode());
						province.setType(1);//酒店
						province.setLanguage(0);
//						List<Province> provincefromtable = Server.getInstance().getHotelService().findAllProvince(
//								" WHERE " + Province.COL_HUAMINCODE + "='" + province.getHuamincode() + "'", "", -1, 0);
						List<Province> provincefromtable = Server.getInstance().getHotelService().findAllProvince(
								" WHERE " + Province.COL_name + "='" + province.getName() + "'", "", -1, 0);
						if (provincefromtable.size() > 0) {
							province.setId(provincefromtable.get(0).getId());
							Server.getInstance().getHotelService().updateProvinceIgnoreNull(province);
						} else {
							try {
								province = Server.getInstance().getHotelService().createProvince(province);
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						System.out.println("插入了" + areanamestr);
						List<Element> cities = element.getChildren("CITIES");
						if (cities != null && cities.size() > 0) {
							for (Element element2 : cities) {
								Element city = element2.getChild("CITY");
								String citycode = city.getText();
								Element cityname = element2.getChild("CITYNAME");
								String citynamestr = cityname.getText();
								if(citynamestr.equals("")){
									
								}else{
									City city2 = new City();
									city2.setName(citynamestr);
									city2.setHuamincode(citycode.trim());
									city2.setType(1l);//1--酒店
									city2.setLanguage(0);
									city2.setProvinceid(province.getId());
									city2.setCountryid(countryfromtable.get(0).getId());

//									List<City> cityfromtable = Server.getInstance().getHotelService().findAllCity(
//											" WHERE " + City.COL_HUAMINCODE + "='" + city2.getHuamincode() + "'", "", -1, 0);
									List<City> cityfromtable = Server.getInstance().getHotelService().findAllCity(
											" WHERE " + City.COL_name + "='" + city2.getName() + "'", "", -1, 0);
									if (cityfromtable.size() > 0) {
										city2.setId(cityfromtable.get(0).getId());
									} else {
										city2=Server.getInstance().getHotelService().createCity(city2);
									}
									city2.setSort(Integer.parseInt(city2.getId()+""));
									Server.getInstance().getHotelService().updateCityIgnoreNull(city2);
								}
							}
						}
					}
			}
			Element returncode = result.getChild("RETURN_CODE");
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("城市数据未插入", "错误信息:" + returncode + "," + errormessage);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
