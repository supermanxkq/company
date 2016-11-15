package com.ccservice.huamin;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.country.Country;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelbene.hotelbene;
import com.ccservice.b2b2c.base.hotelserv.hotelserv;
import com.ccservice.b2b2c.base.region.Region;

/**
 * 查询优点资料--接口十三
 * 
 * @author 2012-6-13上午11:07:08
 */
public class Qbenefit {
	public static void main(String[] args) {
		List<Hotel> hotelfromtable=Server.getInstance().getHotelService().findAllHotel(" WHERE C_SOURCETYPE=3 AND ID>1916", "", -1, 0);
		for (Hotel hotel : hotelfromtable) {
			String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qhotel&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_hotel="+hotel.getHotelcode().trim()+"&p_hotelname=&p_grade=&p_country=CHN&p_area=&p_city=";
			System.out.println("URL地址:" + totalurl);
			String str = Util.getStr(totalurl);
			parsexml_hotel(str);
		}
	}

	public static void getBenefit() {

	}

	// 录入酒店
	public static void parsexml_hotel(String xmlstr) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xmlstr));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			List<Element> hotels = result.getChildren("HOTELS");
			if (hotels != null && hotels.size() > 0) {
				for (Element hotel : hotels) {
					String countrycode = hotel.getChildText("COUNTRY");
					System.out.println("国家代码:" + countrycode);
					List<Country> countryfromtable = Server.getInstance().getInterHotelService().findAllCountry(
							" WHERE " + Country.COL_HUAMINCODE + "='" + countrycode.trim() + "'", "", -1, 0);
					Hotel hoteltemp = new Hotel();
					// 1--表示来自于艺龙,3--表示来自于华闽
					hoteltemp.setSourcetype(3l);
					hoteltemp.setCountryid(countryfromtable.get(0).getId());
					// 0--表示暂时不可用
					// 2--表示艺龙那边没有提供给我们房型
					// 3--表示可用
					hoteltemp.setState(3);
					hoteltemp.setStatedesc("华闽预付酒店");
					// 1--表示国内,2--表示国外
					hoteltemp.setType(1);
					// 预付-2 现付-1
					hoteltemp.setPaytype(2l);

					String huamincode = hotel.getChildText("HOTEL");
					System.out.println("华闽酒店标识：" + huamincode);
					hoteltemp.setHotelcode(huamincode.trim());
					String hotelname = hotel.getChildText("HOTELNAME");
					System.out.println("开始导入：" + hotelname);
					hoteltemp.setName(hotelname);
					String grade = hotel.getChildText("GRADE");
					try {
						if (grade.equals("N") || grade.equals("B")) {
							hoteltemp.setStar(1);
						} else {
							hoteltemp.setStar(Integer.parseInt(grade.trim()));
						}
					} catch (Exception e) {
						WriteLog.write("星级转换出错了", "错误信息:" + hotelname + "," + e.getMessage());
					}
					String address = hotel.getChildText("ADDRESS");
					hoteltemp.setAddress(address);
					String cityid = hotel.getChildText("CITY");
					List<City> cityfromtable = Server.getInstance().getHotelService().findAllCity(
							" WHERE " + City.COL_HUAMINCODE + "='" + cityid.trim() + "'", "", -1, 0);
					if (cityfromtable.size() > 0) {
						hoteltemp.setCityid(cityfromtable.get(0).getId());
						hoteltemp.setProvinceid(cityfromtable.get(0).getProvinceid());
					}
					String regioncode = hotel.getChildText("DIST");
					List<Region> regionfromtable = Server.getInstance().getHotelService().findAllRegion(
							" WHERE " + Region.COL_HUAMINCODE + "='" + regioncode.trim() + "'", "", -1, 0);
					if (regionfromtable.size() > 0) {
						hoteltemp.setRegionid1(regionfromtable.get(0).getId());
					}
					String lat = hotel.getChildText("LATITUDE");
					System.out.println("lat:" + lat);
					String lon = hotel.getChildText("LONGITUDE");
					System.out.println("lon:" + lon);
					try {
						if (lat != null && !"".equals(lat)) {
							hoteltemp.setLat(Double.parseDouble(lat.trim()));
						}
						if (lon != null && !"".equals(lat)) {
							hoteltemp.setLng(Double.parseDouble(lon.trim()));
						}
					} catch (Exception ex) {
						WriteLog.write("酒店新增数据", "经纬度错误信息:" + hotelname + "," + lat + "," + lon + ":" + ex.getMessage());
					}
					String tel = hotel.getChildText("TEL");
					hoteltemp.setTortell(tel);
					String fax = hotel.getChildText("FAX");
					hoteltemp.setFax1(fax);
					String hoteldesc = hotel.getChildText("HOTELDESC");
					hoteltemp.setDescription(hoteldesc);

					List<Hotel> hotelfromtable = Server.getInstance().getHotelService().findAllHotel(
							" WHERE " + Hotel.COL_hotelcode + "='" + hoteltemp.getHotelcode() + "' AND C_SOURCETYPE = 3", "", -1, 0);
					if (hotelfromtable.size() > 0) {
						hoteltemp.setId(hotelfromtable.get(0).getId());
						Server.getInstance().getHotelService().updateHotelIgnoreNull(hoteltemp);
					} else {
						hoteltemp = Server.getInstance().getHotelService().createHotel(hoteltemp);
					}
					System.out.println("酒店录入成功!~~~~~~~~~~~~~~~~");
					try {
						// 和房型表进行关联
						getcontract(hoteltemp.getId(), hoteltemp.getHotelcode());
					} catch (Exception e) {
						WriteLog.write("房型错误", "错误信息:" + hoteltemp.getName() + "错误了!");
					}
				}
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("酒店新增数据", "错误信息:" + resultco + "," + errormessage);
			}
		} catch (Exception e) {
		}
	}

	public static void parsexml(String hotelid, String contractid, String xml) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xml));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			if (result.getChildren().size() > 2) {
				Element contracts = result.getChild("CONTRACTS");
				String hmcontractver = contracts.getChildText("VER");
				List<Element> benefit = contracts.getChildren("BENEFIT");
				for (Element bft : benefit) {
					String benefitcode = bft.getChildText("BENE");
					String benefitdesc = bft.getChildText("BENEDESC");
					hotelbene hotelbene = new hotelbene();
					hotelbene.setContracid(contractid);
					hotelbene.setContracver(hmcontractver);
					// Long hotelid = Util.gethotelid(hmhotelid);
					if (hotelid.equals("0")) {
						WriteLog.write("酒店优点资料新增数据", "没有找到对应的酒店id，华敏酒店id代码：" + hotelid);
					}
					hotelbene.setHotelid(Long.parseLong(hotelid));
					hotelbene.setBene(benefitcode);
					hotelbene.setBenedesc(benefitdesc);
					List<hotelbene> hotelbenefromtable = Server.getInstance().getHotelService().findAllhotelbene(
							"WHERE " + hotelbene.COL_hotelid + "='" + hotelbene.getHotelid() + "' AND " + hotelbene.COL_contracid + "='"
									+ hotelbene.getContracid() + "'", "", -1, 0);
					if (hotelbenefromtable.size() > 0) {
						hotelbene.setId(hotelbenefromtable.get(0).getId());
						Server.getInstance().getHotelService().updatehotelbeneIgnoreNull(hotelbene);
					} else {
						Server.getInstance().getHotelService().createhotelbene(hotelbene);
					}
					System.out.println("成功创建酒店优点……" + hotelbene.getBenedesc());
				}
			} else {
				WriteLog.write("酒店优点资料新增数据", "该酒店合同优点没数据" + ",合同id：" + contractid);
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("酒店优点资料新增数据", "错误信息:" + resultco + "," + errormessage);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 将hotel表和roomtype表进行关联
	 */
	public static void getcontract(long hotelid, String huamincode) {
		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qcontract&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_hotel="
				+ huamincode.trim() + "&p_country=&p_city=";
		String str = Util.getStr(totalurl);
		parsexml_contract(hotelid, str);
		System.out.println("合同数据插入完成~~~~~~~");
	}
	/**
	 * 录入
	 * 
	 * @param hotelid
	 * @param xmlstr
	 */
	public static void parsexml_contract(long hotelid, String xmlstr) {
		SAXBuilder sb = new SAXBuilder();
		try {
			Document doc = sb.build(new StringReader(xmlstr));
			Element root = doc.getRootElement();
			Element result = root.getChild("XML_RESULT");
			if (result.getChildren().size() > 2) {
				Element contracts = result.getChild("CONTRACTS");
				String contractid = contracts.getChildText("CONTRACT");
				List<Element> rooms = contracts.getChildren("ROOMS");
				// 优点
				getBenefit(hotelid, contractid);
			} else {
				WriteLog.write("更新酒店合同", "酒店id：" + hotelid + "没有房型数据");
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("合同新增数据", "错误信息:" + resultco + "," + errormessage);
			}
			System.out.println("合同数据插入一条……");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 优点资料
	public static void getBenefit(long hotelid, String contractid) {
		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qbene&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_contract="
				+ contractid + "&p_bene=&p_country=&p_city=";
		String str = Util.getStr(totalurl);
		parsexml_benefit(hotelid, contractid, str);
	}

	// 录入优点资料
	private static void parsexml_benefit(long hotelid, String contractid, String xml) {
		// TODO Auto-generated method stub
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xml));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			if (result.getChildren().size() > 2) {
				Element contracts = result.getChild("CONTRACTS");
				// String hmcontractid = contracts.getChildText("CONTRACT");
				String hmcontractver = contracts.getChildText("VER");
				// String hmhotelid = contracts.getChildText("HOTEL");
				// String hmhotelname = contracts.getChildText("HOTELNAME");
				List<Element> benefit = contracts.getChildren("BENEFIT");
				for (Element bft : benefit) {
					String benefitcode = bft.getChildText("BENE");
					String benefitdesc = bft.getChildText("BENEDESC");
					hotelbene hotelbene = new hotelbene();
					hotelbene.setContracid(contractid);
					hotelbene.setContracver(hmcontractver);
					hotelbene.setHotelid(hotelid);
					hotelbene.setBene(benefitcode);
					hotelbene.setBenedesc(benefitdesc);
					List<hotelbene> hotelbenefromtable = Server.getInstance().getHotelService().findAllhotelbene(
							"WHERE " + hotelbene.COL_hotelid + "='" + hotelbene.getHotelid() + "' AND " + hotelbene.COL_contracid + "='"
									+ hotelbene.getContracid() + "' AND "+hotelbene.COL_bene+"='"+hotelbene.getBene()+"'", "", -1, 0);
					if (hotelbenefromtable.size() > 0) {
						hotelbene.setId(hotelbenefromtable.get(0).getId());
						Server.getInstance().getHotelService().updatehotelbeneIgnoreNull(hotelbene);
					} else {
						Server.getInstance().getHotelService().createhotelbene(hotelbene);
					}
					System.out.println("成功创建酒店优点……" + hotelbene.getBenedesc());
				}
			} else {
				WriteLog.write("酒店优点资料新增数据", "该酒店合同优点没数据" + ",合同id：" + contractid);
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("酒店优点资料新增数据", "错误信息:" + resultco + "," + errormessage);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
