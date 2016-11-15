package com.ccservice.huamin;

import java.io.StringReader;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.region.Region;

/**
 * 查询酒店资料 接口5
 * 
 * @author 2012-6-12下午07:32:01
 */
public class Qhotel {
	public static void main(String[] args) {
		getHotel();
	}

	public static void getHotel() {
//		//中国 CHN
//		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qhotel&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_hotel=&p_hotelname=&p_grade=&p_country=CHN&p_area=&p_city=";
//		//中国香港 HKG
//		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qhotel&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_hotel=&p_hotelname=&p_grade=&p_country=HKG&p_area=&p_city=";
//		//台湾 TWN
//		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qhotel&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_hotel=&p_hotelname=&p_grade=&p_country=TWN&p_area=&p_city=";
		//中国澳门 MFM
		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qhotel&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_hotel=&p_hotelname=&p_grade=&p_country=MFM&p_area=&p_city=";
		String str = Util.getStr(totalurl);
		parsexml(str);
//		System.out.println(cities.size());
	}

	public static void parsexml(String xmlstr) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xmlstr));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			List<Element> hotels = result.getChildren("HOTELS");
			if (hotels != null && hotels.size() > 0) {
				for (Element hotel : hotels) {

					Hotel hoteltemp = new Hotel();
//					hoteltemp.setSourcetype(3l);
//					hoteltemp.setCountryid(countryid);
//					hoteltemp.setState(3);
//					hoteltemp.setStatedesc("华闽预付酒店");
//					hoteltemp.setType(5);

					String huamincode = hotel.getChildText("HOTEL");
					System.out.println("华闽酒店标识：" + huamincode);
					hoteltemp.setHotelcode(huamincode.trim());
					String hotelname = hotel.getChildText("HOTELNAME");
					System.out.println("开始导入：" + hotelname);
					hoteltemp.setName(hotelname);
//					String grade = hotel.getChildText("GRADE");
//					hoteltemp.setStar(Integer.parseInt(grade.trim()));
//					String address = hotel.getChildText("ADDRESS");
//					hoteltemp.setAddress(address);
//					hoteltemp.setCityid(cityid);
//					hoteltemp.setProvinceid(provinceid);
//					String regioncode = hotel.getChildText("DIST");
//					List<Region> regionfromtable = Server.getInstance().getHotelService().findAllRegion(
//							" WHERE " + Region.COL_HUAMINCODE + "='" + regioncode.trim() + "'", "", -1, 0);
//					if (regionfromtable.size() > 0) {
//						hoteltemp.setRegionid1(regionfromtable.get(0).getId());
//					}
//					String lat = hotel.getChildText("LATITUDE");
//					String lon = hotel.getChildText("LONGITUDE");
//					try {
//						if (lat != null && !"".equals(lat)) {
//							hoteltemp.setLat(Double.parseDouble(lat));
//						}
//						if (lon != null && !"".equals(lat)) {
//							hoteltemp.setLng(Double.parseDouble(lon));
//						}
//					} catch (Exception ex) {
//						WriteLog.write("酒店新增数据", "经纬度错误信息:" + hotelname + "," + lat + "," + lon + ":" + ex.getMessage());
//					}
//					String tel = hotel.getChildText("TEL");
//					hoteltemp.setTortell(tel);
//					String fax = hotel.getChildText("FAX");
//					hoteltemp.setFax1(fax);
//					String hoteldesc = hotel.getChildText("HOTELDESC");
//					hoteltemp.setDescription(hoteldesc);

					List<Hotel> hotelfromtable = Server.getInstance().getHotelService().findAllHotel(
							" WHERE " + Hotel.COL_hotelcode + "='" + hoteltemp.getHotelcode() + "'", "", -1, 0);
					if (hotelfromtable.size() > 0) {
						hoteltemp.setId(hotelfromtable.get(0).getId());
						Server.getInstance().getHotelService().updateHotelIgnoreNull(hoteltemp);
					} else {
						hoteltemp = Server.getInstance().getHotelService().createHotel(hoteltemp);
					}
					System.out.println("酒店录入成功!~~~~~~~~~~~~~~~~");
				}
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				//WriteLog.write("酒店新增数据", "错误信息:" + resultco + "," + errormessage);
			}
		} catch (Exception e) {
			//WriteLog.write("酒店新增数据", "错误信息:" + "城市id" + cityid + "\n" + e.getMessage());
		}
	}
}
