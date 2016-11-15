package com.ccservice.huamin;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.bedtype.Bedtype;
import com.ccservice.b2b2c.base.hmhotelprice.AllotResult;
import com.ccservice.b2b2c.base.hmhotelprice.Allotment;
import com.ccservice.b2b2c.base.hmhotelprice.Hmhotelprice;
import com.ccservice.b2b2c.base.hmhotelprice.StayDate;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.roomtype.Roomtype;

public class UpdateHuaminModifyPrice {
	public static void main(String[] args) {
		updatePrice();
		// text();
		// chongfu();
	}

	public static void chongfu() {
		List<Hotel> hotels = Server
				.getInstance()
				.getHotelService()
				.findAllHotel(
						"where id not in (select distinct c_hotelid from t_hmhotelprice) and c_paytype=2",
						"order by id asc ", -1, 0);
		for (Hotel hotel : hotels) {
			System.out.println(hotel.getName());
			try {
				getNewRate(hotel.getId(), new Date(),
						CatchNext31Day(new Date()));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// int[] ids = { 1274 };// 2485,1490,1501,1557
		// for (int i = 0; i < ids.length; i++) {
		// List<Hmhotelprice> hmhotelprices = Server.getInstance()
		// .getHotelService().findAllHmhotelprice(
		// "where id=" + ids[i], "", -1, 0);
		// for (Hmhotelprice hmhotelprice : hmhotelprices) {
		// String contract = hmhotelprice.getContractid();
		// long hotelid = hmhotelprice.getHotelid();
		// getNewRate(ids[i], new Date(), CatchNext31Day(new Date()));
		// System.out.println("更新一条.......");
		// }
		// }
	}

	public static void updatePrice() {
		File files = new File("D:\\酒店价格");
		if (files.exists()) {
			File[] file = files.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					String filename = pathname.getName();
					if (filename.indexOf("HMC_ALLOTUPDATE") == 0
							|| filename.indexOf("HMC_HC") == 0
							|| filename.indexOf("Copy") == 0) {
						return true;
					}
					return false;
				}

			});
			Set<String> hotelcodes = new HashSet<String>();
			label: for (File f : file) {
				SAXBuilder sb = new SAXBuilder();
				String contract = "";
				String hotelid = "";
				try {
					Document doc = sb.build(f);
					Element root = doc.getRootElement();
					if (f.getName().indexOf("HMC_HC") == 0) {
						System.out.println("更新价格……");
						List<Element> contracts = root.getChildren("CONTRACTS");
						for (Element element : contracts) {
							contract = element.getChildText("CONTRACT");
							hotelid = element.getChildText("HOTEL");
							hotelcodes.add(hotelid);// 添加id
							List<Hotel> hotels = Server.getInstance()
									.getHotelService().findAllHotel(
											"where C_HOTELCODE='" + hotelid
													+ "' and c_paytype=2", "",
											-1, 0);
							if (hotels.size() == 1) {
								System.out.println("文件名称：" + f.getName());
								getNewRate(hotels.get(0).getId(), new Date(),
										CatchNext31Day(new Date()));
							} else if (hotels.size() > 1) {
								WriteLog.write("酒店价格更细问题", "找到多个酒店Id："
										+ hotelid);
							} else {
								WriteLog.write("酒店价格更细问题", "未找到对应的酒店Id："
										+ hotelid);
							}
						}
						System.out.println("删除一个文件:" + f.getName());
						f.delete();
					} else if (f.getName().indexOf("HMC_ALLOTUPDATE") == 0
							|| f.getName().indexOf("CopyHMC_ALLOTUPDATE") == 0
							|| f.getName().indexOf("CopyHMC_HC") == 0) {
						System.out.println("更新房态……");
						long time1 = System.currentTimeMillis();
						Element CONTRACTS = root.getChild("CONTRACTS");
						List<Element> contracts = CONTRACTS
								.getChildren("CONTRACT_LIST");
						for (Element element : contracts) {
							contract = element.getChildText("CONTRACT");
							hotelid = element.getChildText("HT_CODE");
							hotelcodes.add(hotelid);// 添加id
							List<Hotel> hotels = Server.getInstance()
									.getHotelService().findAllHotel(
											"where C_HOTELCODE='" + hotelid
													+ "' and c_paytype=2", "",
											-1, 0);
							// if (hotels.size() == 1) {
							// getNewRate(hotels.get(0).getId(), new Date(),
							// CatchNext31Day(new Date()));
							// } else if (hotels.size() > 1) {
							// WriteLog.write("酒店价格更细问题", "找到多个酒店Id："
							// + hotelid);
							// } else {
							// WriteLog.write("酒店价格更细问题", "未找到对应的酒店Id："
							// + hotelid);
							// }
							Calendar cal = Calendar.getInstance();
							Date checkin = cal.getTime();
							cal.add(Calendar.DAY_OF_MONTH, 28);
							Date checkout = cal.getTime();
							AllotResult result = null;
							try {
								result = Server.getInstance()
										.getIHMHotelService().getQallot(
												contract, "", "", "", checkin,
												checkout);
							} catch (Exception e) {
								System.out.println("加3天试试……");
								Calendar calt = Calendar.getInstance();
								calt.add(Calendar.DAY_OF_MONTH, 3);
								Date check = calt.getTime();
								calt.add(Calendar.DAY_OF_MONTH, 28);
								Date check0 = calt.getTime();
								result = Server.getInstance()
										.getIHMHotelService().getQallot(
												contract, "", "", "", check,
												check0);
							}
							System.out.println("获取房态："+result.getHotelname());
							List<Allotment> alloments = result.getAllotments();
							if (alloments != null && alloments.size() > 0) {
								for (Allotment allotment : alloments) {
									String sql = "where C_CONTRACTID='"
											+ result.getContract()
											+ "' and C_CONTRACTVER='"
											+ result.getHotelvar()
											+ "' and C_ALLOT='"
											+ allotment.getAllot() + "' ";
									List<Hmhotelprice> hmhotelprices = Server
											.getInstance().getHotelService()
											.findAllHmhotelprice(sql,
													"order by C_STATEDATE asc",
													-1, 0);
									List<StayDate> stads = allotment
											.getStaydates();
									if (stads.size() > 0
											&& hmhotelprices.size() > 0) {
										for (StayDate stayDate : stads) {
											for (Hmhotelprice hmprice : hmhotelprices) {
												if (stayDate
														.getDatestr()
														.equals(
																hmprice
																		.getStatedate())) {
													if (hmprice
															.getIsallot()
															.equals(
																	stayDate
																			.getAllot())) {
														System.out
																.println("不用更新:"
																		+ f
																				.getName());
													} else {
														hmprice
																.setIsallot(stayDate
																		.getAllot());
														Server
																.getInstance()
																.getHotelService()
																.updateHmhotelpriceIgnoreNull(hmprice.getId(),hmprice.getHotelid(),"",
																		hmprice);
														System.out
																.println("更新一条房态:"
																		+ f
																				.getName());
													}
												}
											}
										}
									} else {
										getNewRate(hotels.get(0).getId(),
												new Date(),
												CatchNext31Day(new Date()));
										f.delete();
										continue label;
									}
								}
							}
						}
						Label: System.out.println("删除一个文件:" + f.getName());
						f.delete();
						long time2 = System.currentTimeMillis();
						System.out.println("用时：" + (time2 - time1) / 1000 / 60
								+ "分钟");
					}
				} catch (Exception e) {
					f.delete();
					StringBuilder sbuf = new StringBuilder();
					File temp = new File("d:\\酒店价格\\CopyHMC_HC" + hotelid
							+ ".xml");
					if (!temp.exists()) {
						try {
							temp.createNewFile();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
					sbuf
							.append("<?xml version='1.0' encoding='UTF-8'?><QPUSHALLOTUPDATE><CONTRACTS>");
					e.printStackTrace();
					sbuf.append("<CONTRACT_LIST>");
					sbuf.append("<CONTRACT>" + contract + "</CONTRACT>");
					sbuf.append("<HT_CODE>" + hotelid + "</HT_CODE>");
					sbuf.append("</CONTRACT_LIST>");
					sbuf.append("</CONTRACTS></QPUSHALLOTUPDATE>");
					write(temp, sbuf.toString());
				}
			}
			// System.out.println("总共的ID数："+hotelcodes.size());
			// StringBuffer sb=new StringBuffer();
			// for (String hotelcode : hotelcodes) {
			// WriteLog.write("酒店所有Id", hotelcode+",");
			// System.out.println("当前酒店code："+hotelcode);
			// try {
			// List<Hotel> hotels = Server.getInstance()
			// .getHotelService().findAllHotel(
			// "where C_HOTELCODE='" + hotelcode
			// + "' and c_paytype=2", "",
			// -1, 0);
			// if (hotels.size() == 1) {
			// getNewRate(hotels.get(0).getId(), new Date(),
			// CatchNext31Day(new Date()));
			// } else if (hotels.size() > 1) {
			// WriteLog.write("酒店价格更细问题", "找到多个酒店Id："
			// + hotelcode);
			// } else {
			// WriteLog.write("酒店价格更细问题", "未找到对应的酒店Id："
			// + hotelcode);
			// }
			// } catch (Exception e) {
			// sb.append(hotelcode+",");
			// WriteLog.write("酒店价格问题id", sb.toString());
			// }
			// }
		}
	}

	public static void copyFile(File sourceFile, File targetFile)
			throws IOException {
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} finally {
			// 关闭流
			if (inBuff != null)
				inBuff.close();
			if (outBuff != null)
				outBuff.close();
		}
	}

	// 录入酒店价格
	public static void getNewRate(long hotelid, Date checkin, Date checkout)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.US);
		String totalurl = "http://www.yeebooking.com.cn:8034/cn_interface/HMHotel.jsp?api=qrate&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_checkin="
				+ sdf.format(checkin)
				+ "&p_checkout="
				+ sdf.format(checkout)
				+ "&p_hotel="
				+ Server.getInstance().getHotelService().findHotel(hotelid)
						.getHotelcode().trim();
		System.out.println("新访问获取价格路径:" + totalurl);
		String xmlstr = Util.getStr(totalurl);
		if (getXmlCount(xmlstr) > 2) {
			parseNewxml(hotelid, xmlstr);
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(checkin);
			cal.add(Calendar.DAY_OF_MONTH, 3);
			String totalurlm = "http://www.yeebooking.com.cn:8034/cn_interface/HMHotel.jsp?api=qrate&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_checkin="
					+ sdf.format(cal.getTime())
					+ "&p_checkout="
					+ sdf.format(checkout)
					+ "&p_hotel="
					+ Server.getInstance().getHotelService().findHotel(hotelid)
							.getHotelcode().trim();
			System.out.println("新访问获取价格路径:" + totalurlm);
			String xmlstrm = Util.getStr(totalurlm);
			parseNewxml(hotelid, xmlstrm);
		}
	}

	public static int getXmlCount(String xmlstr) throws Exception {
		SAXBuilder sb = new SAXBuilder();
		Document doc;
		int count = 0;
		doc = sb.build(new StringReader(xmlstr));
		Element root = doc.getRootElement();
		Element result = root.getChild("XML_RESULT");
		count = result.getChildren().size();
		return count;
	}

	// 更新后获取价格代码
	public static void parseNewxml(long hotelid, String xmlStr) {
		Server.getInstance().getSystemService().findMapResultBySql(
				"delete from t_hmhotelprice where c_hotelid=" + hotelid, null);
		System.out.println("删除成功……");
		SAXBuilder sb = new SAXBuilder();
		try {
			Document doc = sb.build(new StringReader(xmlStr));
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
				// String nation = product.getChildText("NATION");
				// System.out.println("国家代码:" + nation);
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
						List<Bedtype> bedtypefromtable = Server.getInstance()
								.getHotelService().findAllBedtype(
										" WHERE " + Bedtype.COL_type + "='"
												+ type.trim() + "'", "", -1, 0);
						if (bedtypefromtable.size() > 0) {
							hotelprice.setType(bedtypefromtable.get(0).getId()
									+ "");
						}
						hotelprice.setCountryname(nationname);
						// 房型代码
						List<Roomtype> roomtypefromtable = Server.getInstance()
								.getHotelService().findAllRoomtype(
										" WHERE " + Roomtype.COL_roomcode
												+ "='" + hmroomtype + "' AND "
												+ Roomtype.COL_hotelid + "='"
												+ hotelid + "' AND "
												+ Roomtype.COL_bed + "='"
												+ hotelprice.getType().trim()
												+ "'", "", -1, 0);
						if (roomtypefromtable.size() > 0) {
							hotelprice.setRoomtypeid(roomtypefromtable.get(0)
									.getId());
						}
						// hotelprice.setRoomtypeid(roomtyid);
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
						hotelprice.setMinday(Long.parseLong(min.trim()));
						// 最多停留晚数
						// System.out.println("最多停留晚数:" + max);
						hotelprice.setMaxday(Long.parseLong(max.trim()));
						// 提前预定天数
						hotelprice.setAdvancedday(Long.parseLong(advance));
						// 有无机票 1有机票 0 无机票
						hotelprice.setTicket(ticket);
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
						// List<Hmhotelprice> hmhotelpricefromtable = Server
						// .getInstance().getHotelService()
						// .findAllHmhotelprice(
						// " WHERE " + Hmhotelprice.COL_hotelid
						// + "='"
						// + hotelprice.getHotelid()
						// + "' AND "
						// + Hmhotelprice.COL_roomtypeid
						// + "='"
						// + hotelprice.getRoomtypeid()
						// + "' AND "
						// + Hmhotelprice.COL_statedate
						// + "='"
						// + hotelprice.getStatedate()
						// + "' AND "
						// + Hmhotelprice.COL_serv + "='"
						// + hotelprice.getServ()
						// + "' AND "
						// + Hmhotelprice.COL_type + "='"
						// + hotelprice.getType() + "' and "
						// + Hmhotelprice.COL_prod+"='"+hotelprice.getProd()+"'
						// and "+Hmhotelprice.COL_bf +"= "+hotelprice.getBf(),
						// "", -1, 0);
						String price = stay.getChildText("PRICE");
						try {
							double chaprice = 0;
							// if (hmhotelpricefromtable.size() > 0) {
							// chaprice = hmhotelpricefromtable.get(0)
							// .getPriceoffer()
							// - hmhotelpricefromtable.get(0)
							// .getPrice();
							// }
							// if (chaprice <= 0) {
							// chaprice = (int) (Double.parseDouble(price) *
							// 0.1);
							// }
							// System.out.println("差价：" + chaprice);
							// 价格
							hotelprice.setPrice(Double.parseDouble(price));
							// 可维护的酒店价格
							hotelprice
									.setPriceoffer(Double.parseDouble(price) + 20);
							// 去哪的价格
							hotelprice.setQunarprice(Double.parseDouble(price));
						} catch (Exception e) {
							WriteLog.write("录入酒店价格出错", "酒店id:"
									+ hotelprice.getHotelid());
						}
						String allot = stay.getChildText("ALLOT");
						// 获得分配代码
						hotelprice.setAllot(allot);
						// Y-房型已獲得分配, 能夠即時確認 N-房型未獲得分配, 等待回覆 C-酒店關閉
						String isallot = stay.getChildText("IS_ALLOT");
						hotelprice.setIsallot(isallot);
						// if (hmhotelpricefromtable.size() > 0) {
						// System.out.println("找到了……");
						// hotelprice.setId(hmhotelpricefromtable.get(0)
						// .getId());
						// if(hotelprice.getPrice()!=hmhotelpricefromtable.get(0).getPrice()||
						// !hotelprice.getIsallot().equals(hmhotelpricefromtable.get(0).getIsallot())){
						// Server.getInstance().getHotelService().updateHmhotelpriceIgnoreNull(hotelprice);
						// System.out.println("更新一条价格~~~~");
						// }else{
						// System.out.println("价格房态没变化");
						// }
						// } else {
						Server.getInstance().getHotelService()
								.createHmhotelprice(hotelprice);
						System.out.println("插入一条价格~~~~");
						// }
					}
				}
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
		cal.roll(Calendar.MONTH, 1);
		cal.roll(Calendar.DATE, -1);
		Date newdate = cal.getTime();
		return newdate;
	}

	public static void write(File fileName, String logString) {

		try {
			PrintWriter printWriter = new PrintWriter(new FileOutputStream(
					fileName, true));
			printWriter.println(logString);
			printWriter.flush();
		} catch (FileNotFoundException e) {
			e.getMessage();

		}

	}
}
