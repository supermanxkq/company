package com.ccservice.huamin;

import java.io.StringReader;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.bedtype.Bedtype;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelcontract.Hotelcontract;
import com.ccservice.b2b2c.base.roomtype.Roomtype;

/**
 * 查询酒店合同--接口十
 * 
 * @author 2012-6-13上午09:05:07
 */
public class Qcontract {
	public static void main(String[] args) {
		getcontract();
	}

	public static void getcontract() {
		List<Hotel> hotelfromtable = Server.getInstance().getHotelService().findAllHotel(" WHERE " + Hotel.COL_sourcetype + "=3", "", -1, 0);
		for (int i = 0; i < hotelfromtable.size(); i++) {
			// String totalurl =
			// "http://123.196.113.28:8034/cn_interface/huaminHotel.jsp?test=qcontract&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_hotel="
			String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qcontract&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_hotel="
					+ hotelfromtable.get(i).getHotelcode()+ "&p_country=&p_city=";
			// System.out.println("totalurl:" + totalurl);
			WriteLog.write("更新酒店合同", hotelfromtable.get(i).getId() + ":" + hotelfromtable.get(i).getName() + ":"
					+ hotelfromtable.get(i).getHotelcode());
			String str = Util.getStr(totalurl);
			parsexml(hotelfromtable.get(i).getId(), str);
		}
		System.out.println("合同数据插入完成~~~~~~~");
	}

	public static void parsexml(long hotelid, String xmlstr) {
		SAXBuilder sb = new SAXBuilder();
		try {
			Document doc = sb.build(new StringReader(xmlstr));
			Element root = doc.getRootElement();
			Element result = root.getChild("XML_RESULT");
			if (result.getChildren().size() > 2) {
				Element contracts = result.getChild("CONTRACTS");
				String contractid = contracts.getChildText("CONTRACT");
				String contractver = contracts.getChildText("VER");
				// String hmhotelid = contracts.getChildText("HOTEL");
				String hotelname = contracts.getChildText("HOTELNAME");
				List<Element> rooms = contracts.getChildren("ROOMS");
				for (Element room : rooms) {
					Hotelcontract hotelcontract = new Hotelcontract();
					String cat = room.getChildText("CAT");
					String type = room.getChildText("TYPE");
					String sercode = room.getChildText("SERV");
					String bfcountstr = room.getChildText("BF");
					List<Roomtype> roomtypefromtable = Server.getInstance().getHotelService().findAllRoomtype(
							" WHERE " + Roomtype.COL_roomcode + "='" + cat.trim() + "'", "", -1, 0);
					Roomtype roomtype = roomtypefromtable.get(0);
					if (roomtypefromtable.size() > 0) {
						roomtype.setHotelid(hotelid);
					}
					List<Bedtype> bedtypefromtable = Server.getInstance().getHotelService().findAllBedtype(
							" WHERE " + Bedtype.COL_type + "='" + type.trim() + "'", "", -1, 0);
					if(bedtypefromtable.size()>0){
						roomtype.setBed(Integer.parseInt(bedtypefromtable.get(0).getId()+""));
					}
					//早餐数量
					roomtype.setBreakfast(Integer.parseInt(bfcountstr));
					Server.getInstance().getHotelService().updateRoomtypeIgnoreNull(roomtype);
					// hotelcontract.setHmcontractid(contractid);
					// hotelcontract.setHotelid(hotelid);
				//	hotelcontract.setHmcontractver(contractver);
					// hotelcontract.setHmroomtype(roomtype);
					// hotelcontract.setLanguage(0);
					//hotelcontract.setHmbedtype(bedtype);
					//hotelcontract.setHmserv(sercode);
//					try {
//						long bfcount = Long.parseLong(bfcountstr);
//						hotelcontract.setHmbreakfastnum(bfcount);
//					} catch (Exception e) {
//						WriteLog.write("合同数据", "早餐数量转型失败：早餐数" + bfcountstr + "：合同id：" + contractid + ":合同版本：" + contractver + ":酒店华敏id" + hotelid
//								+ ":酒店名字：" + hotelname + ":房型代码：" + roomtype);
//					}
					// List<Hotelcontract> hotelcontractfromtable =
					// Server.getInstance().getHotelService().findAllHotelcontract(
					// " WHERE " + Hotelcontract.COL_hotelid + "='" +
					// hotelcontract.getHotelid() + "' AND " +
					// Hotelcontract.COL_HMROOMTYPE
					// + "='" + hotelcontract.getHmroomtype() + "' AND " +
					// Hotelcontract.COL_HMBEDTYPE + "='"
					// + hotelcontract.getHmbedtype() + "'", "", -1, 0);
					// if (hotelcontractfromtable.size() > 0) {
					// hotelcontract.setId(hotelcontractfromtable.get(0).getId());
					// Server.getInstance().getHotelService().updateHotelcontractIgnoreNull(hotelcontract);
					// } else {
					// Server.getInstance().getHotelService().createHotelcontract(hotelcontract);
					//					}
				}
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
}
