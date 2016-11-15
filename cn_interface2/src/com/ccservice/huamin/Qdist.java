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
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.country.Country;
import com.ccservice.b2b2c.base.region.Region;

/**
 * 查询区域资料 接口四
 * 
 * @author 2012-6-12下午01:31:36
 */
public class Qdist {
	public static void main(String[] args) {
		getdist();
	}

	public static void getdist() {
		// String totalurl =
		// "http://123.196.113.28:8034/cn_interface/huaminHotel.jsp?test=qdist&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_dist=&p_distname=";
		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qdist&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_dist=&p_distname=";
		URL url = null;
		try {
			url = new URL(totalurl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream inputStream = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						inputStream));
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
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	public static void parsexml(String xmlstr) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xmlstr));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			Element cities = result.getChild("CITIES");
			if (cities != null) {
				String resultcode = result.getChildText("RETURN_CODE");
				String errormessage = result.getChildText("ERROR_MESSAGE");
				if (errormessage != null || !"".equals(errormessage)) {
					WriteLog.write("区域城市新增数据", "错误信息:" + resultcode + ","
							+ errormessage);
				}
				List<Element> cityes = cities.getChildren("CITIES");
				int i = 0;
				if (cityes != null && cityes.size() > 0) {
					for (Element city : cityes) {
						String countrycode = city.getChildText("COUNTRY");
						List<Country> countryfromtable = Server.getInstance()
								.getInterHotelService().findAllCountry(
										" WHERE " + Country.COL_HUAMINCODE
												+ "='" + countrycode.trim()
												+ "'", "", -1, 0);
						String countryname = city.getChildText("COUNTRYNAME");
						if ("CHN".equals(countrycode.trim())) {
							String distcode = city.getChildText("DIST");
							String distname = city.getChildText("DISTNAME");
							String citycode = city.getChildText("CITY");
							System.out.println("华闽城市代码:" + citycode);
							if (distname.equals("")) {

							} else {
								Region region = new Region();
								region.setName(distname);
								List<City> cityfromtable = Server
										.getInstance()
										.getHotelService()
										.findAllCity(
												"WHERE " + City.COL_HUAMINCODE
														+ "='"
														+ citycode.trim() + "'",
												"", -1, 0);
								region.setCityid(cityfromtable.get(0).getId());
								region.setCountryid(countryfromtable.get(0)
										.getId());
								region.setLanguage(0);
								region.setHuamincode(distcode.trim());

								List<Region> regionfromtable = Server
										.getServer().getHotelService()
										.findAllRegion(
												" WHERE " + Region.COL_cityid
														+ "="
														+ region.getCityid()
														+ " AND "
														+ Region.COL_name
														+ "='"
														+ region.getName()
														+ "'", "", -1, 0);
								if (regionfromtable.size() > 0) {
									region
											.setId(regionfromtable.get(0)
													.getId());
									Server.getInstance().getHotelService()
											.updateRegionIgnoreNull(region);
								} else {
									region.setType("1");
									Server.getInstance().getHotelService()
											.createRegion(region);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
