package com.ccservice.huamin;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.HMRequestUitl;
import com.ccservice.b2b2c.base.hmhotelprice.Hmhotelprice;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.roomtype.Roomtype;

/**
 * 
 * @author 2012-6-11上午11:47:29 查询酒店价格
 */
public class Qrate {

	public static void main(String[] args) throws Exception {
		//List<Hotel> hotelfromtable = Server.getInstance().getHotelService().findAllHotel(" WHERE C_HUAMINCODE IS NOT NULL", "", -1, 0);
		//for (Hotel hotel : hotelfromtable) {
			getQrate(1382,"2013-01-04","2012-01-09");
		//}
	}

	/**
	 * 酒店id
	 * 
	 * @param hotelid
	 * @return
	 * @throws 
	 */
	public static List<Hmhotelprice> getQrate(long hotelid,String checkin,String checkout) throws Exception {
		// 获取华敏酒店代码
		String hmhotelid = "";
		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.US);
		SimpleDateFormat sd=new SimpleDateFormat("yyyy-MM-dd");
		String totalurl = HMRequestUitl.getHMRequestUrlHeader() + "&api=qrate&p_lang=SIM&p_checkin="
			+ sdf.format(sd.parse(checkin)) + "&p_checkout=" + sdf.format(sd.parse(checkout)) + "&p_hotel=" + hmhotelid;
		System.out.println("访问路径:" + totalurl);
		if (hotelid > 0) {
			Hotel hotel = Server.getInstance().getHotelService().findHotel(hotelid);
			if (hotel != null) {
				hmhotelid = hotel.getHotelcode();
			}
		}
		// 日期类型: 30-OCT-11
		String xmlstr = Util.getStr(totalurl);
		// System.out.println("~~~~~~~~~" + xmlstr);
		List<Hmhotelprice> hmhotelprices = parsexml(hotelid, xmlstr);
		return hmhotelprices;
	}

	public static List<Hmhotelprice> parsexml(long hotelid, String xmlstr) {
		List<Hmhotelprice> hmhotelprice = new ArrayList<Hmhotelprice>();
		SAXBuilder sb = new SAXBuilder();
		try {
			Document doc = sb.build(new StringReader(xmlstr));
			Element root = doc.getRootElement();
			Element result = root.getChild("XML_RESULT");
			Element contracts = result.getChild("CONTRACTS");
			if (contracts != null) {
				String hmcontract = contracts.getChildText("CONTRACT");
				System.out.println("酒店合同代码:"+hmcontract);
				String hmcontractver = contracts.getChildText("VER");
				// String hmhotelid = contracts.getChildText("HOTEL");
				// String hmhotelname = contracts.getChildText("HOTELNAME");
				String cur = contracts.getChildText("CUR");
				Element product = contracts.getChild("PRODUCT");
				String prod = product.getChildText("PROD");
				String nation = product.getChildText("NATION");
				System.out.println("国家代码:"+nation);
				// String nationname = product.getChildText("NATIONNAME");
				String min = product.getChildText("MIN");
				String max = product.getChildText("MAX");
				String advance = product.getChildText("ADVANCE");
				String ticket = product.getChildText("TICKET");
				List<Element> rooms = product.getChildren("ROOM");
				if (nation.equals("PRC")||nation.equals("ALL")) {
					for (Element room : rooms) {
						String hmroomtype = room.getChildText("CAT");
						// 房型代码
						List<Roomtype> roomtypefromtable = Server.getInstance().getHotelService()
								.findAllRoomtype(
										" WHERE " + Roomtype.COL_roomcode + "='" + hmroomtype + "' AND " + Roomtype.COL_hotelid + "='" + hotelid + "'",
										"", -1, 0);
						String type = room.getChildText("TYPE");
						// 汇率
						String serv = room.getChildText("SERV");
						// 服务包
						String bf = room.getChildText("BF");
						// 以包括早餐数量
						List<Element> stays = room.getChildren("STAY");
						for (Element stay : stays) {
							Hmhotelprice hotelprice = new Hmhotelprice();
							// 房型代码
							if (roomtypefromtable.size() > 0) {
								hotelprice.setRoomtypeid(roomtypefromtable.get(0).getId());
							}
							// 汇率
							hotelprice.setType(type);
							// 服务包
							hotelprice.setServ(serv);
							// 已包含早餐数量
							hotelprice.setBf(Long.parseLong(bf));
							hotelprice.setContractid(hmcontract);
							// 酒店合同版本号
							hotelprice.setContractver(hmcontractver);
							// 酒店代码
							hotelprice.setHotelid(hotelid);
							// 货币
							hotelprice.setCur(cur);
							// 提醒代码
							hotelprice.setProd(prod);
							// 国籍代码
							// hotelprice.setCountryid(168l);
							// // 国家名称
							// if (nation.equals("ALL")) {
							// hotelprice.setCountryname("中国");
							// }
							// if (nation.equals("PRC")) {
							// hotelprice.setCountryname("中国大陆市场");
							// }
							// if (nation.equals("NPRC")) {
							// hotelprice.setCountryname("非中国大陆市场");
							// }
							// 最少停留晚数
							// System.out.println("最少停留晚数:" + min);
							hotelprice.setMinday(Long.parseLong(min.trim()));
							// 最多停留晚数
							// System.out.println("最多停留晚数:" + max);
							hotelprice.setMaxday(Long.parseLong(max.trim()));
							// 提前预定天数
							hotelprice.setAdvancedday(Long.parseLong(advance));
							// 有无机票 1有机票 0 无机票
							hotelprice.setTicket(ticket);
							if (Long.parseLong(ticket.trim()) == 0) {
								System.out.println("不需要机票号码");
							}
							if (Long.parseLong(ticket.trim()) == 1) {
								System.out.println("需要机票号码");
							}

							SimpleDateFormat sd = new SimpleDateFormat("dd-M-yy");
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							// System.out.println(sd.format(LastDay()));
							String deadline = room.getChildText("DEADLINE");
							// 最后期限
							hotelprice.setDeadline(sdf.format(sd.parse(deadline)));
							String statedate = stay.getChildText("STAYDATE");
							// 日期
							hotelprice.setStatedate(sdf.format(sd.parse(statedate)));
							System.out.println("日期:" + sdf.format(sd.parse(statedate)));
							String price = stay.getChildText("PRICE");
							try {
								// 价格
								hotelprice.setPrice(Double.parseDouble(price));
								// 可维护的酒店价格
								hotelprice.setPriceoffer(Double.parseDouble(price));
							} catch (Exception e) {
								// TODO: handle exception
								WriteLog.write("录入酒店价格出错", "酒店id:" + hotelprice.getHotelid());
							}
							String allot = stay.getChildText("ALLOT");
							// 获得分配代码
							hotelprice.setAllot(allot);
							// Y-房型已獲得分配, 能夠即時確認 N-房型未獲得分配, 等待回覆 C-酒店關閉
							String isallot = stay.getChildText("IS_ALLOT");
							// if (isallot.equals("Y")) {
							// hotelprice.setIsallot("房型已获得分配,能够及时确认");
							// }
							// if (isallot.equals("N")) {
							// hotelprice.setIsallot("房型未获得分配,等待回复");
							// }
							hotelprice.setIsallot(isallot);
							hmhotelprice.add(hotelprice);
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hmhotelprice;
	}
}
