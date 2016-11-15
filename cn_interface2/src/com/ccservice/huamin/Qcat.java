package com.ccservice.huamin;

import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.roomtype.Roomtype;

/**
 * 房型数据插入 接口六
 * 
 * @author wzc-ccs
 * 
 */
public class Qcat {
	public static void main(String[] args) {
		getCat();
	}

	public static void getCat() {
		// String url =
		// "http://123.196.113.28:8034/cn_interface/huaminHotel.jsp?test=qcat&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_cat=&p_catname=";
		String url = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qcat&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_cat=&p_catname=";
		String str = Util.getStr(url);
		// System.out.println(str);
		parse(str);
	}

	public static void parse(String xml) {
		SAXBuilder sb = new SAXBuilder();
		try {
			Document document = sb.build(new StringReader(xml));
			Element root = document.getRootElement();
			System.out.println("....."+root.getName());
			root.getName();
			Element result = root.getChild("XML_RESULT");
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			Element errormessage = result.getChild("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("房型数据", "错误信息:" + resultco + "," + errormessage);
			}
			List<Element> categories = result.getChildren("CATEGORIES");
			if (categories != null && categories.size() > 0) {
				for (Element category : categories) {
					Roomtype roomtype = new Roomtype();
					String catcode = category.getChildText("CAT");
					String catname = category.getChildText("CATNAME");
					roomtype.setRoomcode(catcode.trim());
					System.out.println("华闽房型code"+roomtype.getRoomcode());
					List<Hotel> hotelfromtable = Server.getInstance().getHotelService().findAllHotel(
							" WHERE " + Hotel.COL_hotelcode + "='" + roomtype.getRoomcode() + "'", "", -1, 0);
					if (hotelfromtable.size() > 0) {
						roomtype.setHotelid(hotelfromtable.get(0).getId());
						System.out.println("酒店id:"+roomtype.getHotelid());
					}
					roomtype.setName(catname);
					roomtype.setLanguage(0);
					System.out.println("插入房型：" + catname);
					List<Roomtype> roomtypefromtable = Server.getInstance().getHotelService().findAllRoomtype(
							" WHERE " + Roomtype.COL_roomcode + "='" + roomtype.getRoomcode() + "'", "", -1, 0);
					if (roomtypefromtable.size() > 0) {
						roomtype.setId(roomtypefromtable.get(0).getId());
						Server.getInstance().getHotelService().updateRoomtypeIgnoreNull(roomtype);
					} else {
						Server.getInstance().getHotelService().createRoomtype(roomtype);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
