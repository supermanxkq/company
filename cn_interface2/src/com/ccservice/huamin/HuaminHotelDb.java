package com.ccservice.huamin;

import java.io.IOException;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.HMRequestUitl;
import com.ccservice.b2b2c.base.bedtype.Bedtype;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.country.Country;
import com.ccservice.b2b2c.base.hmhotelprice.Hmhotelprice;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelbene.hotelbene;
import com.ccservice.b2b2c.base.hotelcancel.hotelcancel;
import com.ccservice.b2b2c.base.hotelproduct.hotelproduct;
import com.ccservice.b2b2c.base.hotelserv.hotelserv;
import com.ccservice.b2b2c.base.region.Region;
import com.ccservice.b2b2c.base.roomtype.Roomtype;

/**
 * 录入华闽酒店数据
 * 
 * @author 师卫林 2012-6-14下午01:51:10
 */
public class HuaminHotelDb {
	public static void main(String[] args) {
		// // 录入区域城市
		// Qdist.getdist();
		// // 录入酒店床型数据
		// Qcat.getCat();
		// // 录入服务代码
		// Qservcode.getQservcode();
		// 根据国家代码 录入酒店数据

		// // 中国香港 HKG
		// getHotel("HKG", "SIM");
	     // 中国 CHN
		 //getHotel("CHN", "SIM");
		// // 中国台湾 TWN
		// getHotel("TWN", "SIM");
		// // 中国澳门 MFM
		// getHotel("MFM", "SIM");

		List<Hotel> hotelfromtable = Server.getInstance().getHotelService()
				.findAllHotel(" WHERE C_SOURCETYPE=3 and id=1493",
						"order by id desc", -1, 0);
		for (Hotel hotel : hotelfromtable) {
			System.out.println("酒店名字:" + hotel.getName());
			try {
				// 和房型表进行关联
				//getcontract(hotel.getId(), hotel.getHotelcode());
				getNewRate(hotel.getId(), Today(new Date()),
						CatchNext31Day(new Date()),hotel.getCityid().toString());
			} catch (Exception e) {
				WriteLog.write("房型错误", "错误信息:" + hotel.getName() + "错误了!");
			}
		}

	}

	// 酒店
	private static void getHotel(String countrycode, String languagetype) {
		String totalurl = HMRequestUitl.getHMRequestUrlHeader() + "&api=qhotel&p_lang="
				+ languagetype.trim()
				+ "&p_hotel=&p_hotelname=&p_grade=&p_country="
				+ countrycode.trim() + "&p_area=&p_city=";
		System.out.println("URL地址:" + totalurl);
		String str = Util.getStr(totalurl);
		parsexml_hotel(countrycode, str);
	}

	// 录入酒店
	public static void parsexml_hotel(String countryhuamincode, String xmlstr) {
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
					List<Country> countryfromtable = Server.getInstance()
							.getInterHotelService().findAllCountry(
									" WHERE " + Country.COL_HUAMINCODE + "='"
											+ countrycode.trim() + "'", "", -1,
									0);
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
						WriteLog.write("星级转换出错了", "错误信息:" + hotelname + ","
								+ e.getMessage());
					}
					String address = hotel.getChildText("ADDRESS");
					hoteltemp.setAddress(address);
					String cityid = hotel.getChildText("CITY");
					List<City> cityfromtable = Server.getInstance()
							.getHotelService().findAllCity(
									" WHERE " + City.COL_HUAMINCODE + "='"
											+ cityid.trim() + "'", "", -1, 0);
					if (cityfromtable.size() > 0) {
						hoteltemp.setCityid(cityfromtable.get(0).getId());
						hoteltemp.setProvinceid(cityfromtable.get(0)
								.getProvinceid());
						String regioncode = hotel.getChildText("DIST");
						List<Region> regionfromtable = Server.getInstance()
								.getHotelService().findAllRegion(
										" WHERE " + Region.COL_HUAMINCODE
												+ "='" + regioncode.trim()
												+ "' and C_CITYID="
												+ cityfromtable.get(0).getId(),
										"", -1, 0);
						if (regionfromtable.size() > 0) {
							hoteltemp.setRegionid1(regionfromtable.get(0)
									.getId());
						}
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
						WriteLog.write("酒店新增数据", "经纬度错误信息:" + hotelname + ","
								+ lat + "," + lon + ":" + ex.getMessage());
					}
					String tel = hotel.getChildText("TEL");
					hoteltemp.setTortell(tel);
					String fax = hotel.getChildText("FAX");
					hoteltemp.setFax1(fax);
					String hoteldesc = hotel.getChildText("HOTELDESC");
					hoteltemp.setDescription(hoteldesc);

					List<Hotel> hotelfromtable = Server.getInstance()
							.getHotelService().findAllHotel(
									" WHERE " + Hotel.COL_hotelcode + "='"
											+ hoteltemp.getHotelcode()
											+ "' AND C_SOURCETYPE = 3", "", -1,
									0);
					if (hotelfromtable.size() > 0) {
						hoteltemp.setId(hotelfromtable.get(0).getId());
						Server.getInstance().getHotelService()
								.updateHotelIgnoreNull(hoteltemp);
						System.out.println("酒店更新成功!~~~~~~~~~~~~~~~~");
					} else {
						hoteltemp = Server.getInstance().getHotelService()
								.createHotel(hoteltemp);
						System.out.println("酒店录入成功!~~~~~~~~~~~~~~~~");
					}
					if (hoteltemp.getName().equals("")) {
						getHotel(countryhuamincode, "ENG");
					}
					try {
						 //和房型表进行关联
						getcontract(hoteltemp.getId(), hoteltemp.getHotelcode());
					} catch (Exception e) {
						WriteLog.write("房型错误", "错误信息:" + hoteltemp.getName()
								+ "错误了!");
					}
				}
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("酒店新增数据", "错误信息:" + resultco + ","
						+ errormessage);
			}
		} catch (Exception e) {
		}
	}

	// 录入酒店价格
	public static void getQrate(long hotelid, long roomtypeid, long bedid,
			String roomtypecode, String bedcode, Date checkin, Date checkout) {
		// 日期类型: 30-OCT-11
		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.US);
		String totalurl = HMRequestUitl.getHMRequestUrlHeader() + "&api=qrate&p_lang=SIM&p_checkin="
				+ sdf.format(checkin)
				+ "&p_checkout="
				+ sdf.format(checkout)
				+ "&p_hotel="
				+ Server.getInstance().getHotelService().findHotel(hotelid)
						.getHotelcode().trim()
				+ "&p_rcat="
				+ roomtypecode
				+ "&p_type=" + bedcode;
		System.out.println("访问路径:" + totalurl);
		String xmlstr = Util.getStr(totalurl);
		// System.out.println("~~~~~~~~~" + xmlstr);
		parsexml(hotelid, roomtypeid, bedid, xmlstr);
	}

	// 录入酒店价格
	public static void getNewRate(long hotelid, Date checkin, Date checkout,String cityid) {
		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.US);
		String totalurl = HMRequestUitl.getHMRequestUrlHeader() + "&api=qrate&p_lang=SIM&p_checkin="
				+ sdf.format(checkin)
				+ "&p_checkout="
				+ sdf.format(checkout)
				+ "&p_hotel="
				+ Server.getInstance().getHotelService().findHotel(hotelid)
						.getHotelcode().trim();
		System.out.println("新访问获取价格路径:" + totalurl);
		String xmlstr = Util.getStr(totalurl);
		if(getXmlCount(xmlstr)>2){
			parseNewxml(hotelid, xmlstr,cityid);
		}else{
			Calendar cal=Calendar.getInstance();
			cal.setTime(checkin);
			cal.add(Calendar.DAY_OF_MONTH, 3);
			String totalurlm = HMRequestUitl.getHMRequestUrlHeader() + "&api=qrate&p_lang=SIM&p_checkin="
					+ sdf.format(cal.getTime())
					+ "&p_checkout="
					+ sdf.format(checkout)
					+ "&p_hotel="
					+ Server.getInstance().getHotelService().findHotel(hotelid)
							.getHotelcode().trim();
			System.out.println("新访问获取价格路径:" + totalurlm);
			String xmlstrm = Util.getStr(totalurlm);
			parseNewxml(hotelid, xmlstrm,cityid);
		}
	}

	public static int getXmlCount(String xmlstr) {
		SAXBuilder sb = new SAXBuilder();
		Document doc;
		int count = 0;
		try {
			doc = sb.build(new StringReader(xmlstr));
			Element root = doc.getRootElement();
			Element result = root.getChild("XML_RESULT");
			count = result.getChildren().size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	// 更新后获取价格代码
	public static void parseNewxml(long hotelid, String xmlStr,String cityid) {
		Server.getInstance().getSystemService().findMapResultBySql(
				"delete from t_hmhotelprice where c_hotelid=" + hotelid, null);
		System.out.println("删除成功……");
		SAXBuilder sb = new SAXBuilder();
		try {
			Document doc = sb.build(new StringReader(xmlStr));
			Element root = doc.getRootElement();
			Element result = root.getChild("XML_RESULT");
			List<Element> contractslist = result.getChildren("CONTRACTS");
			for (Element contracts : contractslist) {
				if (contracts != null) {
					String hmcontract = contracts.getChildText("CONTRACT");
					System.out.println("酒店合同代码:" + hmcontract);
					String hmcontractver = contracts.getChildText("VER");
					String cur = contracts.getChildText("CUR");
					List<Element> products = contracts.getChildren("PRODUCT");
					for (Element product : products) {
						String prod = product.getChildText("PROD");
						String nation = product.getChildText("NATION");
						String nationname = product.getChildText("NATIONNAME");
						String min = product.getChildText("MIN");
						String max = product.getChildText("MAX");
						String advance = product.getChildText("ADVANCE");
						String ticket = product.getChildText("TICKET");
						List<Element> rooms = product.getChildren("ROOM");
						for (Element room : rooms) {
							String hmroomtype = room.getChildText("CAT");
							System.out.println("房型:" + hmroomtype);
							// 床型
							String type = room.getChildText("TYPE");
							System.out.println("TYPE:" + type);
							// 服务包
							String serv = room.getChildText("SERV");
							System.out.println("服务包:" + serv);
							// 以包括早餐数量
							String bf = room.getChildText("BF");
							List<Element> stays = room.getChildren("STAY");
							for (Element stay : stays) {
								Hmhotelprice hotelprice = new Hmhotelprice();
								// 床型
								List<Bedtype> bedtypefromtable = Server
										.getInstance().getHotelService()
										.findAllBedtype(" WHERE " + Bedtype.COL_type
														+ "='" + type.trim()
														+ "'", "", -1, 0);
								if (bedtypefromtable.size() > 0) {
									hotelprice.setType(bedtypefromtable.get(0).getId()+ "");
								}
								hotelprice.setCountryid(nation);
								hotelprice.setCountryname(nationname);
								// 房型代码
								List<Roomtype> roomtypefromtable = Server
										.getInstance().getHotelService().findAllRoomtype(" WHERE "+ Roomtype.COL_roomcode
														+ "='"+ hmroomtype+ "' AND "+ Roomtype.COL_hotelid
														+ "='"+ hotelid+ "' AND "+ Roomtype.COL_bed
														+ "='"+ hotelprice.getType().trim() + "'",
												"", -1, 0);
								if (roomtypefromtable.size() > 0) {
									hotelprice.setRoomtypeid(roomtypefromtable
											.get(0).getId());
								}
								// 服务包
								hotelprice.setServ(serv);
								// 已包含早餐数量
								hotelprice.setBf(Long.parseLong(bf));
								hotelprice.setContractid(hmcontract);
								// 酒店合同版本号
								hotelprice.setContractver(hmcontractver);
								// 酒店代码
								hotelprice.setCityid(cityid);
								hotelprice.setHotelid(hotelid);
								// 货币
								hotelprice.setCur(cur);
								// 提醒代码
								hotelprice.setProd(prod);
								hotelprice.setMinday(Long.parseLong(min.trim()));
								// 最多停留晚数
								hotelprice.setMaxday(Long.parseLong(max.trim()));
								// 提前预定天数
								hotelprice.setAdvancedday(Long.parseLong(advance));
								// 有无机票 1有机票 0 无机票
								hotelprice.setTicket(ticket);
								SimpleDateFormat sdfriqi = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								hotelprice.setUpdatetime(sdfriqi.format(new Date(System.currentTimeMillis())));
								SimpleDateFormat sd = new SimpleDateFormat(
										"dd-M-yy");
								SimpleDateFormat sdf = new SimpleDateFormat(
										"yyyy-MM-dd");
								String deadline = room.getChildText("DEADLINE");
								// 最后期限
								hotelprice.setDeadline(sdf.format(sd.parse(deadline)));
								String statedate = stay.getChildText("STAYDATE");
								// 日期
								hotelprice.setStatedate(sdf.format(sd.parse(statedate)));
								System.out.println("日期:"+ sdf.format(sd.parse(statedate)));
								String price = stay.getChildText("PRICE");
								try {
									// 价格
									hotelprice.setPrice(Double.parseDouble(price));
									// 可维护的酒店价格
									hotelprice.setPriceoffer(Double.parseDouble(price)+ 20);
									// 去哪的价格
									hotelprice.setQunarprice(Double.parseDouble(price));
								} catch (Exception e) {
									WriteLog.write("录入酒店价格出错", "酒店id:"
											+ hotelprice.getHotelid());
								}
								String allot = stay.getChildText("ALLOT");
								hotelprice.setAllot(allot);
								// Y-房型已獲得分配, 能夠即時確認 N-房型未獲得分配, 等待回覆 C-酒店關閉
								String isallot = stay.getChildText("IS_ALLOT");
								if("Y".equals(isallot)){
									hotelprice.setRoomstatus(0l);
									hotelprice.setYuliuNum(1l);
								}else if("N".equals(isallot)){
									hotelprice.setRoomstatus(0l);
									hotelprice.setYuliuNum(0l);
								}else if("C".equals(isallot)){
									hotelprice.setRoomstatus(1l);
									hotelprice.setYuliuNum(0l);
								}
								hotelprice.setIsallot(isallot);
								hotelprice.setAbleornot(0);
								Server.getInstance().getHotelService().createHmhotelprice(hotelprice);
								System.out.println("插入一条价格~~~~");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 录入酒店价格
	 * 
	 * @param hotelid
	 * @param xmlstr
	 *            getQrate(hotelid,roomtype.getId(),roomtype.getBed(), new
	 *            Date(), CatchNext31Day(new Date()));
	 */
	public static void parsexml(long hotelid, long roomtyid, long bedid,
			String xmlstr) {
		SAXBuilder sb = new SAXBuilder();
		try {
			Document doc = sb.build(new StringReader(xmlstr));
			Element root = doc.getRootElement();
			Element result = root.getChild("XML_RESULT");
			Element contracts = result.getChild("CONTRACTS");
			if (contracts != null) {
				String hmcontract = contracts.getChildText("CONTRACT");
				System.out.println("酒店合同代码:" + hmcontract);
				String hmcontractver = contracts.getChildText("VER");
				// String hmhotelid = contracts.getChildText("HOTEL");
				// String hmhotelname = contracts.getChildText("HOTELNAME");
				String cur = contracts.getChildText("CUR");
				Element product = contracts.getChild("PRODUCT");
				String prod = product.getChildText("PROD");
				String nation = product.getChildText("NATION");
				System.out.println("国家代码:" + nation);
				String nationname = product.getChildText("NATIONNAME");
				String min = product.getChildText("MIN");
				String max = product.getChildText("MAX");
				String advance = product.getChildText("ADVANCE");
				String ticket = product.getChildText("TICKET");
				List<Element> rooms = product.getChildren("ROOM");
				for (Element room : rooms) {
					// String hmroomtype = room.getChildText("CAT");
					// System.out.println("房型:" + hmroomtype);
					// 床型
					// String type = room.getChildText("TYPE");
					// System.out.println("TYPE:" + type);
					// 服务包
					String serv = room.getChildText("SERV");
					// System.out.println("服务包:" + serv);
					// 以包括早餐数量
					String bf = room.getChildText("BF");
					List<Element> stays = room.getChildren("STAY");
					for (Element stay : stays) {
						Hmhotelprice hotelprice = new Hmhotelprice();
						// 床型
						// List<Bedtype> bedtypefromtable =
						// Server.getInstance().getHotelService().findAllBedtype(
						// " WHERE " + Bedtype.COL_type + "='" + type.trim() +
						// "'", "", -1, 0);
						// if (bedtypefromtable.size() > 0) {
						// hotelprice.setType(bedtypefromtable.get(0).getId() +
						// "");
						// }
						hotelprice.setType(bedid + "");
						// 房型代码
						// List<Roomtype> roomtypefromtable =
						// Server.getInstance().getHotelService().findAllRoomtype(
						// " WHERE " + Roomtype.COL_roomcode + "='" + hmroomtype
						// + "' AND " + Roomtype.COL_hotelid + "='" + hotelid +
						// "' AND "
						// + Roomtype.COL_bed + "='" +
						// hotelprice.getType().trim() + "'", "", -1, 0);
						// if (roomtypefromtable.size() > 0) {
						// hotelprice.setRoomtypeid(roomtypefromtable.get(0).getId());
						// }
						hotelprice.setRoomtypeid(roomtyid);
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
						// if (Long.parseLong(ticket.trim()) == 0) {
						// System.out.println("不需要机票号码");
						// }
						// if (Long.parseLong(ticket.trim()) == 1) {
						// System.out.println("需要机票号码");
						// }

						SimpleDateFormat sd = new SimpleDateFormat("dd-M-yy");
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd");
						// System.out.println(sd.format(LastDay()));
						String deadline = room.getChildText("DEADLINE");
						// 最后期限
						hotelprice.setDeadline(sdf.format(sd.parse(deadline)));
						String statedate = stay.getChildText("STAYDATE");
						// 日期
						hotelprice
								.setStatedate(sdf.format(sd.parse(statedate)));
						System.out.println("日期:"
								+ sdf.format(sd.parse(statedate)));
						String price = stay.getChildText("PRICE");
						try {
							// 价格
							hotelprice.setPrice(Double.parseDouble(price));
							// 可维护的酒店价格
							hotelprice.setPriceoffer(Double.parseDouble(price));
						} catch (Exception e) {
							// TODO: handle exception
							WriteLog.write("录入酒店价格出错", "酒店id:"
									+ hotelprice.getHotelid());
						}
						String allot = stay.getChildText("ALLOT");
						// 获得分配代码
						hotelprice.setAllot(allot);
						// Y-房型已獲得分配, 能夠即時確認 N-房型未獲得分配, 等待回覆 C-酒店關閉
						String isallot = stay.getChildText("IS_ALLOT");
						hotelprice.setIsallot(isallot);
						if (isallot.equals("C")) {
							Roomtype roomtype = Server.getInstance()
									.getHotelService().findRoomtype(
											hotelprice.getRoomtypeid());
							roomtype.setState(0);
							Server.getInstance().getHotelService()
									.updateRoomtypeIgnoreNull(roomtype);
						} else {
							List<Hmhotelprice> hmhotelpricefromtable = Server
									.getInstance()
									.getHotelService()
									.findAllHmhotelprice(
											" WHERE "
													+ Hmhotelprice.COL_hotelid
													+ "='"
													+ hotelprice.getHotelid()
													+ "' AND "
													+ Hmhotelprice.COL_roomtypeid
													+ "='"
													+ hotelprice
															.getRoomtypeid()
													+ "' AND "
													+ Hmhotelprice.COL_statedate
													+ "='"
													+ hotelprice.getStatedate()
													+ "' AND "
													+ Hmhotelprice.COL_serv
													+ "='"
													+ hotelprice.getServ()
													+ "' AND "
													+ Hmhotelprice.COL_type
													+ "='"
													+ hotelprice.getType()
													+ "'", "", -1, 0);
							if (hmhotelpricefromtable.size() > 0) {
								hotelprice.setId(hmhotelpricefromtable.get(0)
										.getId());
								Server.getInstance().getHotelService()
										.updateHmhotelpriceIgnoreNull(hotelprice.getId(),hotelprice.getHotelid(),"",hotelprice);
								System.out.println("更新一条价格~~~~");
							} else {
								Server.getInstance().getHotelService()
										.createHmhotelprice(hotelprice);
								System.out.println("插入一条价格~~~~");
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

	/**
	 * 将hotel表和roomtype表进行关联
	 */
	public static void getcontract(long hotelid, String huamincode) {
		String totalurl = HMRequestUitl.getHMRequestUrlHeader() + "&api=qcontract&p_lang=SIM&p_hotel="
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
				List<Element> contractlist = result.getChildren("CONTRACTS");
				for (Element contracts : contractlist) {
					String contractid = contracts.getChildText("CONTRACT");
					System.out.println("合同id：" + contractid);
					// String contractver = contracts.getChildText("VER");
					// String hmhotelid = contracts.getChildText("HOTEL");
					// String hotelname = contracts.getChildText("HOTELNAME");
					List<Element> rooms = contracts.getChildren("ROOMS");
					for (Element room : rooms) {
						String cat = room.getChildText("CAT");
						String type = room.getChildText("TYPE");
						String sercode = room.getChildText("SERV");
						// System.out.println("服务代码:" + sercode);
						String bfcountstr = room.getChildText("BF");
						Roomtype roomtype = new Roomtype();
						roomtype.setRoomcode(cat.trim());
						// 房型名称
						String url = HMRequestUitl.getHMRequestUrlHeader() + "&api=qcat&p_lang=SIM&p_cat="
								+ cat.trim() + "&p_catname=";
						System.out.println(url);
						String str = Util.getStr(url);
						roomtype.setName(parse_cat(str));
						roomtype.setHotelid(hotelid);
						roomtype.setLanguage(0);
						roomtype.setState(1);

						List<Bedtype> bedtypefromtable = Server.getInstance()
								.getHotelService().findAllBedtype(
										" WHERE " + Bedtype.COL_type + "='"
												+ type.trim() + "'", "", -1, 0);
						if (bedtypefromtable.size() > 0) {
							roomtype.setBed(Integer.parseInt(bedtypefromtable
									.get(0).getId()
									+ ""));
						}
						List<Roomtype> roomtypelist = Server.getInstance()
								.getHotelService().findAllRoomtype(
										" WHERE " + Roomtype.COL_roomcode
												+ "='" + roomtype.getRoomcode()
												+ "' AND "
												+ Roomtype.COL_hotelid + "="
												+ roomtype.getHotelid()
												+ " AND " + Roomtype.COL_bed
												+ "='" + roomtype.getBed()
												+ "'", "", -1, 0);
						// 服务
						String totalurl = HMRequestUitl.getHMRequestUrlHeader() + "&api=qserv&p_lang=SIM&p_contract="
								+ contractid.trim()
								+ "&p_serv="
								+ sercode.trim() + "&p_country=&p_city=";
						// System.out.println("服务访问地址:"+totalurl);
						String serv = Util.getStr(totalurl);
						if (roomtypelist.size() > 0) {
							roomtype.setId(roomtypelist.get(0).getId());
							Server.getInstance().getHotelService()
									.updateRoomtypeIgnoreNull(roomtype);
						} else {
							roomtype = Server.getInstance().getHotelService()
									.createRoomtype(roomtype);
						}
						parsexml_qserv(hotelid, contractid, serv, roomtype
								.getId(), bfcountstr);

					}
					// // 产品
					getProd(hotelid, contractid);
					// // 取消规则
					//getCancel(hotelid, contractid);
					// // 优点
					getBenefit(hotelid, contractid);
				}
			} else {
				WriteLog.write("更新酒店合同", "酒店id：" + hotelid + "没有房型数据");
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("合同新增数据", "错误信息:" + resultco + ","
						+ errormessage);
			}
			System.out.println("合同数据插入一条……");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 获取房型名称
	 * 
	 * @param xml
	 */
	public static String parse_cat(String xml) {
		SAXBuilder sb = new SAXBuilder();
		Document document;
		String catname = "";
		try {
			document = sb.build(new StringReader(xml));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			Element errormessage = result.getChild("ERROR_MESSAGE");
			List<Element> categories = result.getChildren("CATEGORIES");
			if (categories != null && categories.size() > 0) {
				for (Element category : categories) {
					String catcode = category.getChildText("CAT");
					catname = category.getChildText("CATNAME");
				}
			}
			System.out.println("房型名称:" + catname);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return catname;
	}

	/**
	 * 将hotel表和hotelcancel表进行关联
	 * 
	 * @param hotelid
	 * @param contractid
	 */
	private static void getCancel(long hotelid, String contractid) {
		// TODO Auto-generated method stub
		String totalurl = HMRequestUitl.getHMRequestUrlHeader() + "&api=qcancel&p_lang=SIM&p_contract="
				+ contractid + "&p_cancel=&p_country=&p_city=";
		String str = Util.getStr(totalurl);
		parsexml_cancel(hotelid, contractid, str);
	}

	/**
	 * 取消规则
	 * 
	 * @param hotelid
	 * @param contractid
	 * @param str
	 */
	private static void parsexml_cancel(long hotelid, String contractid,
			String xmlstr) {
		// TODO Auto-generated method stub
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xmlstr));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			if (result.getChildren().size() > 2) {
				Element contracts = result.getChild("CONTRACTS");
				String hmcontractid = contracts.getChildText("CONTRACT");
				String hmcontractver = contracts.getChildText("VER");
				// String hmholtelid = contracts.getChildText("HOTEL");
				// String hmhotelname = contracts.getChildText("HOTELNAME");
				List<Element> cancellation = contracts
						.getChildren("CANCELLATION");
				for (Element cancel : cancellation) {
					hotelcancel hotelcancel = new hotelcancel();
					String calcelcode = cancel.getChildText("CANCEL");
					String canceldesc = cancel.getChildText("CANCELDESC");
					hotelcancel.setConractid(hmcontractid);
					hotelcancel.setConractver(hmcontractver);
					hotelcancel.setHotelid(hotelid);
					hotelcancel.setCancelid(calcelcode);
					hotelcancel.setCanceldesc(canceldesc);
					List<hotelcancel> hotelcancelfromtable = Server
							.getInstance().getHotelService()
							.findAllhotelcancel(
									"WHERE " + hotelcancel.COL_hotelid + "='"
											+ hotelcancel.getHotelid()
											+ "' AND "
											+ hotelcancel.COL_conractid + "='"
											+ hotelcancel.getConractid()
											+ "' AND "
											+ hotelcancel.COL_cancelid + "='"
											+ hotelcancel.getCancelid() + "'",
									"", -1, 0);
					if (hotelcancelfromtable.size() > 0) {
						hotelcancel.setId(hotelcancelfromtable.get(0).getId());
						Server.getInstance().getHotelService()
								.updatehotelcancelIgnoreNull(hotelcancel);
					} else {
						Server.getInstance().getHotelService()
								.createhotelcancel(hotelcancel);
					}
				}
			} else {
				WriteLog.write("酒店取消陈述数据", "暂无信息:合同id" + contractid);
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("酒店取消陈述数据", "错误信息:" + resultco + ","
						+ errormessage);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 录入与酒店关联的服务
	 * 
	 * @param hotelid
	 * @param contractid
	 * @param xml
	 */
	public static void parsexml_qserv(long hotelid, String contractid,
			String xml, long roomtypeid, String breakfast) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xml));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			if (result.getChildren().size() > 2) {
				Element contracts = result.getChild("CONTRACTS");
				String hmcontractid = contracts.getChildText("CONTRACT");
				String hmcontractver = contracts.getChildText("VER");
				// String hmholtelid = contracts.getChildText("HOTEL");
				// String hmhotelname = contracts.getChildText("HOTELNAME");
				String cur = contracts.getChildText("CUR");
				List<Element> services = contracts.getChildren("SERVICE");
				for (Element service : services) {
					String serverid = service.getChildText("SERV");
					// System.out.println("~~~"+serverid);
					List<Element> items = service.getChildren("ITEM");
					for (Element item : items) {
						String servercode = item.getChildText("SERVCODE");
						// System.out.println("!!!"+servercode);
						String servername = item.getChildText("SERVNAME");
						String price = item.getChildText("PRICE");
						String min = item.getChildText("MIN");
						String max = item.getChildText("MAX");
						hotelserv hotelserv = new hotelserv();
						hotelserv.setHotelid(hotelid);
						hotelserv.setContractid(contractid);
						hotelserv.setCur(cur);
						hotelserv.setServ(serverid);
						hotelserv.setServcode(servercode);
						hotelserv.setServname(servername);
						hotelserv.setPrice(price);
						hotelserv.setContractver(hmcontractver);
						hotelserv.setRoomtypeid(roomtypeid);
						hotelserv.setBreakfast(Long.parseLong(breakfast));
						try {
							Long maxcount = Long.parseLong(max.trim());
							Long mincount = Long.parseLong(min.trim());
							hotelserv.setMaxcount(maxcount);
							hotelserv.setMincount(mincount);
						} catch (Exception e) {
							WriteLog.write("酒店服务包新增数据", "最大数量或最小数量转换异常：酒店合同id："
									+ contractid + "：max" + max + ":min" + min);
						}
						List<hotelserv> hotelservfromtable = Server
								.getInstance().getHotelService()
								.findAllhotelserv(
										"WHERE " + hotelserv.COL_hotelid + "='"
												+ hotelserv.getHotelid()
												+ "' AND " + hotelserv.COL_serv
												+ "='" + hotelserv.getServ()
												+ "' AND "
												+ hotelserv.COL_servcode + "='"
												+ hotelserv.getServcode()
												+ "'AND "
												+ hotelserv.COL_ROOMTYPEID
												+ "="
												+ hotelserv.getRoomtypeid()
												+ " and c_contractid='"
												+ contractid + "'", "", -1, 0);
						if (hotelservfromtable.size() > 0) {
							hotelserv.setId(hotelservfromtable.get(0).getId());
							Server.getInstance().getHotelService()
									.updatehotelservIgnoreNull(hotelserv);
							System.out.println("更新服务:" + servername);
						} else {
							Server.getInstance().getHotelService()
									.createhotelserv(hotelserv);
							System.out.println("添加服务:" + servername);
						}
					}
				}
			} else {
				WriteLog
						.write("酒店服务包新增数据", "酒店合同id：" + contractid + "：酒店产品没数据");
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("酒店服务包新增数据", "错误信息:" + resultco + ","
						+ errormessage);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 优点资料
	public static void getBenefit(long hotelid, String contractid) {
		String totalurl = HMRequestUitl.getHMRequestUrlHeader() + "&api=qbene&p_lang=SIM&p_contract="
				+ contractid + "&p_bene=&p_country=&p_city=";
		String str = Util.getStr(totalurl);
		parsexml_benefit(hotelid, contractid, str);
	}

	// 录入优点资料
	private static void parsexml_benefit(long hotelid, String contractid,
			String xml) {
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
					List<hotelbene> hotelbenefromtable = Server.getInstance()
							.getHotelService().findAllhotelbene(
									"WHERE " + hotelbene.COL_hotelid + "='"
											+ hotelbene.getHotelid() + "' AND "
											+ hotelbene.COL_contracid + "='"
											+ hotelbene.getContracid()
											+ "' AND " + hotelbene.COL_bene
											+ "='" + hotelbene.getBene() + "'",
									"", -1, 0);
					if (hotelbenefromtable.size() > 0) {
						hotelbene.setId(hotelbenefromtable.get(0).getId());
						Server.getInstance().getHotelService().updatehotelbeneIgnoreNull(hotelbene);
					} else {
						Server.getInstance().getHotelService().createhotelbene(
								hotelbene);
					}
					System.out.println("成功创建酒店优点……" + hotelbene.getBenedesc());
				}
			} else {
				WriteLog.write("酒店优点资料新增数据", "该酒店合同优点没数据" + ",合同id："
						+ contractid);
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("酒店优点资料新增数据", "错误信息:" + resultco + ","
						+ errormessage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 产品代码
	public static void getProd(long hotelid, String contractid) {
		String totalurl = HMRequestUitl.getHMRequestUrlHeader() + "&api=qprod&p_lang=SIM&p_contract="
				+ contractid + "&p_prod=&p_country=&p_city=";
		String str = Util.getStr(totalurl);
		parsexml_prod(hotelid, contractid, str);
	}

	// 进行关联
	public static void parsexml_prod(long hotelid, String contractid, String xml) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xml));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			if (result.getChildren().size() > 2) {
				Element contracts = result.getChild("CONTRACTS");
				// String contid = contracts.getChildText("CONTRACT");
				String ver = contracts.getChildText("VER");
				// String hmhotelid = contracts.getChildText("HOTEL");
				// String hotelname = contracts.getChildText("HOTELNAME");
				List<Element> products = contracts.getChildren("PRODUCT");
				for (Element product : products) {
					String prod = product.getChildText("PROD");
					String ratetype = product.getChildText("RATETYPE");
					String nation = product.getChildText("NATION");
					String nationname = product.getChildText("NATIONNAME");
					String min = product.getChildText("MIN");
					String max = product.getChildText("MAX");
					String advance = product.getChildText("ADVANCE");
					String ticket = product.getChildText("TICKET");
					String prombuy = product.getChildText("PROMBUY");
					String promget = product.getChildText("PROMGET");
					String promdisc = product.getChildText("PROMDISC");
					String promqty = product.getChildText("PROMQTY");
					String benefit = product.getChildText("BENEFIT");
					String benefitdesc = product.getChildText("BENEFITDESC");
					String cancel = product.getChildText("CANCEL");
					String calceldesc = product.getChildText("CANCELDESC");

					hotelproduct hotelproduct = new hotelproduct();
					hotelproduct.setHmcontractid(contractid);
					hotelproduct.setHmcontractver(ver);
					hotelproduct.setHotelid(hotelid);
					hotelproduct.setProd(prod);
					hotelproduct.setRatetype(ratetype);
					// hotelproduct.setNationid();// 暂时先入库国内数据
					hotelproduct.setNationname(nationname);
					try {
						hotelproduct.setMinday(Long.parseLong(min.trim()));
						hotelproduct.setMaxday(Long.parseLong(max.trim()));
						hotelproduct.setAdvance(Long.parseLong(advance.trim()));
						hotelproduct.setPrombuy(Long.parseLong(prombuy.trim()));
						hotelproduct.setBenefitid(benefit.trim());
						hotelproduct.setPromqty(Long.parseLong(promqty.trim()));
						hotelproduct.setTicket(Long.parseLong(ticket.trim()));
						hotelproduct.setPromget(Long.parseLong(promget.trim()));
					} catch (Exception e) {
						WriteLog.write("酒店产品数据", "转型失败：min" + min + "：合同id："
								+ contractid + ":max：" + max + ":advance"
								+ advance + ":prombuy：" + prombuy + ":benefit："
								+ benefit + ":promqty" + promqty + ":ticket"
								+ ticket + ":" + promget);
					}
					hotelproduct.setPromdisc(promdisc);
					hotelproduct.setBenifitdesc(benefitdesc);
					hotelproduct.setCancel(cancel.trim());
					hotelproduct.setCanceldesc(calceldesc);
					List<hotelproduct> hotelproductfromtable = Server
							.getInstance().getHotelService()
							.findAllhotelproduct(
									"WHERE " + hotelproduct.COL_hotelid + "='"
											+ hotelproduct.getHotelid()
											+ "' AND "
											+ hotelproduct.COL_contractid
											+ "='"
											+ hotelproduct.getHmcontractid()
											+ "' AND " + hotelproduct.COL_prod
											+ "='" + hotelproduct.getProd()
											+ "'", "", -1, 0);
					if (hotelproductfromtable.size() > 0) {
						hotelproduct
								.setId(hotelproductfromtable.get(0).getId());
						Server.getInstance().getHotelService()
								.updatehotelproductIgnoreNull(hotelproduct);
					} else {
						Server.getInstance().getHotelService()
								.createhotelproduct(hotelproduct);
					}
				}
			} else {
				WriteLog.write("酒店产品prod新增数据", "酒店合同id：" + contractid
						+ "：酒店产品没数据");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取当前日期31天后的日期
	 * 
	 * @return
	 */
	public static Date CatchNext31Day(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, 1);
		cal.add(Calendar.DAY_OF_MONTH, -2);
		Date newdate = cal.getTime();
		return newdate;
	}

	public static Date Tomorowday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		Date newdate = cal.getTime();
		return newdate;
	}

	
	public static Date Today(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, 0);
		Date newdate = cal.getTime();
		return newdate;
	}
	/**
	 * 获取指定日期的前一天
	 * 
	 * @return
	 */
	public static Date CatchYesterday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.roll(Calendar.DATE, -1);
		Date newdate = cal.getTime();
		return newdate;
	}

}
