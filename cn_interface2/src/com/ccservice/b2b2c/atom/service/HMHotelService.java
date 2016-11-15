package com.ccservice.b2b2c.atom.service;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.interticket.HttpClient;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.bedtype.Bedtype;
import com.ccservice.b2b2c.base.guest.Guest;
import com.ccservice.b2b2c.base.hmhotelprice.AllotResult;
import com.ccservice.b2b2c.base.hmhotelprice.Allotment;
import com.ccservice.b2b2c.base.hmhotelprice.PriceResult;
import com.ccservice.b2b2c.base.hmhotelprice.ResultProduct;
import com.ccservice.b2b2c.base.hmhotelprice.ResultRoom;
import com.ccservice.b2b2c.base.hmhotelprice.ResultStay;
import com.ccservice.b2b2c.base.hmhotelprice.StayDate;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelgooddata.HotelGoodData;
import com.ccservice.b2b2c.base.hotelorder.HMOrderResult;
import com.ccservice.b2b2c.base.hotelorder.HMRooms;
import com.ccservice.b2b2c.base.hotelorder.HMService;
import com.ccservice.b2b2c.base.hotelorder.OrderStatusResult;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.huamin.CancelResultBean;
import com.ccservice.huamin.Util;
import com.ccservice.huamin.WriteLog;

/**
 * 华闽酒店接口实现类
 * 
 */
public class HMHotelService implements IHMHotelService {

	@Override
	public CancelResultBean cancelorder(String waiorderid) throws  IOException {
		String postmsg=getcancelstr(waiorderid);
		String totalurl = HMRequestUitl.getHMOrderurl()+"api=qbookingcancel&xml="+ postmsg;
		String str = HttpClient.httpget(totalurl, "utf-8").trim();
		WriteLog.write("华闵订单", "取消订单信息：\n"+postmsg);
		if(str!=null && str.contains("?<?xml version=\"1.0\"")){
			str = str.replace("?<?xml version=\"1.0\" ", "<?xml version=\"1.0\" ");
		}
		WriteLog.write("华闵订单", "取消订单状态返回信息：\n"+str);
		CancelResultBean bea=parsecancelorderstr(str);
		return bea;
	}
	
	public CancelResultBean parsecancelorderstr(String str) throws IOException{
		CancelResultBean cal=new CancelResultBean();
		try {
			SAXBuilder sax=new SAXBuilder();
			Document document;
			document=sax.build(new StringReader(str.trim()));
			Element root=document.getRootElement();
			Element xmlresult = root.getChild("XML_RESULT");
			if(xmlresult!=null){
				Element booking=xmlresult.getChild("BOOKING");
				if(booking!=null){
					String hmcref=booking.getChildText("HMCREF");
					String statuscode=booking.getChildText("BOOKSTATUS");
					String errocode=booking.getChildText("ERROR_CODE");
					String errormsg=booking.getChildText("ERROR_MSG");
					
					cal.setWaicode(hmcref);
					cal.setBookstatus(statuscode);
					cal.setErrorcode(errocode);
					cal.setErrormsg(errormsg);
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		return cal;
	}
	
	public String getcancelstr(String waiorderid){
		StringBuilder params = new StringBuilder();
		params.append("<request>");  
		params.append("<company>"+HMRequestUitl.getCompany()+"</company>");
		params.append("<id>"+HMRequestUitl.getId()+"</id>");
		params.append("<pass>"+HMRequestUitl.getPass()+"</pass>");
		params.append("<lang>SIM</lang>"); 
		params.append("<hmcref>"+waiorderid+"</hmcref>");   
		params.append("</request>"); 
		return params.toString();
	}
	
	@Override
	public OrderStatusResult qbookingstatus(String ordercode) throws Exception {
		OrderStatusResult result=new OrderStatusResult();
		try {
			String postmsg=getorderstate(ordercode);
			WriteLog.write("华闵订单", "查询订单状态：\n"+postmsg);
			String totalurl = HMRequestUitl.getHMOrderurl()+"api=qbookingstatus&xml="+postmsg;
			String str = HttpClient.httpget(totalurl, "utf-8").trim();
			if(str!=null && str.contains("?<?xml version=\"1.0\"")){
				str = str.replace("?<?xml version=\"1.0\" ", "<?xml version=\"1.0\" ");
			}
			WriteLog.write("华闵订单", "查询订单状态返回信息：\n"+str);
			result=parseOrderStatus(str);
			return result;
		} catch (RuntimeException e) {
			WriteLog.write("华闵订单异常",e.getMessage());
			return result;
		}
	}

	/**
	 * 查询捷旅华闵订单状态
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public OrderStatusResult parseOrderStatus(String xml) throws JDOMException, IOException{
		OrderStatusResult res=new OrderStatusResult();
		SAXBuilder sax=new SAXBuilder();
		Document document;
		document=sax.build(new StringReader(xml.trim()));
		Element root=document.getRootElement();
		Element xmlresult = root.getChild("XML_RESULT");
		if(xmlresult!=null){
			Element hmcreflist=xmlresult.getChild("HMCREFLIST");
			if(hmcreflist!=null){
				String hmcrefno=hmcreflist.getChildText("HMCREF");
				String refno=hmcreflist.getChildText("REFNO");
				String statuscode=hmcreflist.getChildText("STATUSCODE");
				String statusdesc=hmcreflist.getChildText("STATUSDESC");
				String linkname=hmcreflist.getChildText("TCNAME");
				String linkmail=hmcreflist.getChildText("TCEMAIL");
				String linkfax=hmcreflist.getChildText("TCFAX");
				
				res.setOrdernum(hmcrefno);
				res.setRefno(refno);
				res.setStatuscode(statuscode);
				res.setStatedes(statusdesc);
				res.setLinkname(linkname);
				res.setLinkmail(linkmail);
				res.setLinkfax(linkfax);
			}
		}
		return res;
	}
	/**
	 * 订单状态内容 ordercode可以为字符串 a1,a2
	 */
	public String getorderstate(String ordercode) {
		StringBuilder params = new StringBuilder();
		params.append("<request>");
		params.append("<company>"+HMRequestUitl.getCompany()+"</company>");
		params.append("<id>"+HMRequestUitl.getId()+"</id>");
		params.append("<pass>"+HMRequestUitl.getPass()+"</pass>");
		params.append("<hmcreflist>");
		String[] apis = ordercode.split(",");
		for (int i = 0; i < apis.length; i++) {
			params.append("<hmcref>" + apis[i] + "</hmcref>");
		}
		params.append("</hmcreflist>");
		params.append("</request>");
		return params.toString();
	}

	/**
	 * 提交订单接口
	 */
	@Override
	public HMOrderResult qbooking(String contract, String ver, String checkin,
			String checkout, int preroom, long prod, String cat, String type,
			String serv, long bf, String flightinfo, List<Guest> guest,
			String servcode, int qty, int night, String sr) throws Exception {
		String postxml=getContent(contract, ver, checkin, checkout, preroom, prod,
				cat, type, serv, bf, flightinfo, guest, servcode, qty,night, sr);
		String totalurl = HMRequestUitl.getHMOrderurl()+"api=qbooking&xml="+URLEncoder.encode(postxml, "utf-8");
		WriteLog.write("华闵订单", "下订单：\n"+postxml);
		String str = HttpClient.httpget(totalurl, "utf-8").trim();
		if(str!=null && str.contains("?<?xml version=\"1.0\" ")){
			str = str.replace("?<?xml version=\"1.0\" ", "<?xml version=\"1.0\" ");
		}
		WriteLog.write("华闵订单", "下订单返回信息：\n"+str);
		HMOrderResult result = parsexml(str);
		return result;

	}

	public String getContent(String contract, String ver, String checkin,
			String checkout, int preroom, long prod, String cat, String type,
			String serv, long bf, String flightinfo, List<Guest> guest,
			String servcode, int qty, int night, String sr)
			throws ParseException, UnsupportedEncodingException {
		StringBuffer params = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.US);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		// 表单参数与get形式一样
		params.append("<request>");
		params.append("<company>"+HMRequestUitl.getCompany()+"</company>");
		params.append("<id>"+HMRequestUitl.getId()+"</id>");
		params.append("<pass>"+HMRequestUitl.getPass()+"</pass>");
		params.append("<contract>" + contract.trim() + "</contract>");
		params.append("<ver>" + ver.trim() + "</ver>");
		params.append("<refno></refno>");
		params.append("<guarantee>1</guarantee>");
		params.append("<rooms>");
		for (int i = 0; i < preroom; i++) {
			params.append("<room>");
			params.append("<checkin>" + sdf.format(sd.parse(checkin)).trim()
					+ "</checkin>");
			params.append("<checkout>" + sdf.format(sd.parse(checkout)).trim()
					+ "</checkout>");
			params.append("<prod>" + prod + "</prod>");
			params.append("<cat>" + cat.trim() + "</cat>");
			params.append("<type>" + type.trim() + "</type>");
			params.append("<serv>" + serv.trim() + "</serv>");
			params.append("<bf>" + bf + "</bf>");
			params.append("<flightinfo>" + flightinfo.trim() + "</flightinfo>");
			params.append("<guests>");
			params.append("<guest>"+ guest.get(i).getName()+ "</guest>");
			params.append("</guests>");
			// params.append("<services>");
			// params.append("<service> ");
			// params.append("<servcode>BE</servcode> ");
			// params.append("<qty>2</qty> ");
			// params.append("<night>2</night> ");
			// params.append("</service> ");
			// params.append("</services> ");
			String[] srs = sr.split(",");
			if (srs.length > 0) {
				params.append("<specials>");
				for (int j = 0; j < srs.length; j++) {
					params.append("<sr>" + srs[j].trim() + "</sr>");
				}
				params.append("</specials>");
			}
			params.append("</room>");
		}
		params.append("</rooms>");
		params.append("</request>");
		System.out.println(params.toString().trim());
		return params.toString().trim();
	}

	public HMOrderResult parsexml(String xmlstr) {
		HMOrderResult hmOrderResult = new HMOrderResult();
		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.US);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xmlstr.trim()));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			if (result != null) {
				Element booking = result.getChild("BOOKING");
				if (booking != null) {
					// 华闽订单参考号码(订单号)
					Element hmcref = booking.getChild("HMCREF");
					if (hmcref != null&&!"".equals(hmcref)) {
						String hmcrefstr = hmcref.getText();
						hmOrderResult.setOrderid(hmcrefstr);
						// 公司名称
						String company = booking.getChildText("COMPANY");
						// 公司用户名称
						String id = booking.getChildText("ID");
						// 酒店合同号码
						String contract = booking.getChildText("CONTRACT");
						// 酒店合同版本
						String ver = booking.getChildText("VER");
						// 酒店代码
						String hotel = booking.getChildText("HOTEL");
						hmOrderResult.setHotelid(hotel);
						// N--没有获得分配,Y--获得分配代码
						String cur = booking.getChildText("CUR");
						hmOrderResult.setStatus(cur);
						// 酒店合计（订单总价）
						String amount = booking.getChildText("AMOUNT");
						hmOrderResult.setAmount(Float.parseFloat(amount));
						// 订单日期
						String bookdate = booking.getChildText("BOOKDATE");
						hmOrderResult.setBookdate(sd
								.format(sdf.parse(bookdate)));
						// 订单状态 IC-及时确认,OR-要求中,CFM-已确认,CA-取消中,CAN-已取消,ACK-保证入住
						String bookstatus = booking.getChildText("BOOKSTATUS");
						hmOrderResult.setBookstatus(bookstatus);
						// 保证入住状态
						String gaurantee = booking.getChildText("GAURANTEE");
						hmOrderResult.setGuarantee(gaurantee);
						List<HMRooms> hmrooms = new ArrayList<HMRooms>();
						List<Element> rooms = booking.getChildren("ROOMS");
						for (Element room : rooms) {
							HMRooms roomtemp = new HMRooms();
							// 房型数量
							String roomno = room.getChildText("ROOMNO");
							roomtemp.setRoomno(roomno);
							// 入住日期
							String checkid = room.getChildText("CHECKIN");
							roomtemp.setCheckin(sd.format(sdf.parse(checkid)));
							// 退房日期
							String checkout = room.getChildText("CHECKOUT");
							roomtemp
									.setCheckout(sd.format(sdf.parse(checkout)));
							// 产品代码
							String prod = room.getChildText("PROD");
							roomtemp.setProd(prod);
							// 房型代码
							String cat = room.getChildText("CAT");
							roomtemp.setCat(cat);
							// 床型代码
							String type = room.getChildText("TYPE");
							roomtemp.setType(type);
							// 服务组合
							String serv = room.getChildText("SERV");
							roomtemp.setServ(serv);
							// 早餐数量
							String bf = room.getChildText("BF");
							roomtemp.setBf(Integer.parseInt(bf));
							// 航班资料
							String flight_info = room
									.getChildText("FLIGHT_INFO");
							roomtemp.setFlightinfo(flight_info);
							// 房间状态 IA-内部分配,即时确认 FS-自由销售,即时确认 OR-要求中
							String roomstatus = room.getChildText("ROOMSTATUS");
							roomtemp.setRoomstatus(roomstatus);
							List<Element> stays = room.getChildren("STAYS");
							Map<String, String> staystemp = new HashMap<String, String>();
							for (Element stay : stays) {
								// 停留日数
								String staydate = stay.getChildText("STAYDATE");

								// 停留日数价格
								String price = stay.getChildText("PRICE");
								staystemp.put(sd.format(sdf.parse(staydate)),
										price);
							}
							roomtemp.setPrices(staystemp);

							List<Element> guests = room.getChildren("GUESTS");
							List<String> names = new ArrayList<String>();
							for (Element guest : guests) {
								// 客人名称
								String guestname = guest
										.getChildText("GUESTNAME");
								names.add(guestname);
							}
							roomtemp.setGuestname(names);
							List<Element> specials = room
									.getChildren("SPECIALS");
							List<String> srs = new ArrayList<String>();
							for (Element special : specials) {
								// 特别要求代码
								String sr = special.getChildText("SR");
								srs.add(sr);
							}
							roomtemp.setSrs(srs);
							List<Element> services = room
									.getChildren("SERVICES");
							List<HMService> servicetemp = new ArrayList<HMService>();
							for (Element service : services) {
								HMService servicetem = new HMService();
								// 服务代码
								String servcode = service
										.getChildText("SERVCODE");
								servicetem.setServicecode(servcode);
								// 服务数量
								String qty = service.getChildText("QTY");
								// 服务晚数
								String nonight = service
										.getChildText("NONIGHT");
								servicetem.setNight(Integer.parseInt(nonight));
								// 服务价格
								String price = service.getChildText("PRICE");
								servicetem.setPrice(Float.parseFloat(price));
								// 服务合计
								String serviceAmount = service
										.getChildText("AMOUNT");
								servicetem.setAmount(Integer
										.parseInt(serviceAmount));
							}
							roomtemp.setService(servicetemp);
							hmrooms.add(roomtemp);
						}
						hmOrderResult.setRooms(hmrooms);
					}
				}
				Element resultcode = result.getChild("RETURN_CODE");
				String resultco = resultcode.getText();
				String errormessage = result.getChildText("ERROR_MESSAGE");
				hmOrderResult.setResultmsg(errormessage);
				hmOrderResult.setResultcode(resultco);
			}
		} catch (Exception e) {
			WriteLog.write("华闽下单", "异常：" + e.getMessage() + "\n 返回信息："
					+ xmlstr);
		}
		return hmOrderResult;

	}

	@Override
	public AllotResult getQallot(String contractid, String productid,
			String roomtype, String bedtype, Date checkin, Date checkout)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.US);
		String totalurl = "http://123.196.114.122:8034/cn_interface/HMHotel.jsp?api=qallot&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_checkin="
				+ sdf.format(checkin)
				+ "&p_checkout="
				+ sdf.format(checkout)
				+ "&p_contract="
				+ contractid
				+ "&p_cat="
				+ roomtype
				+ "&p_type=" + bedtype + "&p_prod=" + productid;
		System.out.println(totalurl);
		String xmlstr = Util.getStr(totalurl);
		AllotResult allot = parsexmlallot(xmlstr);
		return allot;
	}

	@Override
	public PriceResult getQrate(String hotelid, Date checkin, Date checkout,
			String hotelname, String country, String citycode,
			String roomtypecode, String bedcode, String grade, String instance)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.US);
		String totalurl = "http://123.196.114.122:8034/cn_interface/HMHotel.jsp?api=qrate&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_checkin="
				+ sdf.format(checkin)
				+ "&p_checkout="
				+ sdf.format(checkout)
				+ "&p_hotel="
				+ hotelid
				+ "&p_rcat="
				+ roomtypecode
				+ "&p_type="
				+ bedcode
				+ "&p_grade="
				+ grade
				+ "&p_country="
				+ country
				+ "&p_hotelname="
				+ hotelname
				+ "&p_city="
				+ citycode
				+ "&p_instant=" + instance;
		System.out.println("访问路径:" + totalurl);
		String xmlstr = Util.getStr(totalurl);
		System.out.println(xmlstr);
		PriceResult priceresult = parsexmlrate(xmlstr);
		return priceresult;

	}

	public AllotResult parsexmlallot(String xmlstr) throws Exception {
		SAXBuilder sb = new SAXBuilder();
		SimpleDateFormat sd = new SimpleDateFormat("dd-MMM-yy", Locale.US);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		AllotResult allotresult = new AllotResult();
		Document doc = null;
		doc = sb.build(new StringReader(xmlstr));
		Element root = doc.getRootElement();
		Element result = root.getChild("XML_RESULT");
		Element contracts = result.getChild("CONTRACTS");
		if (contracts != null) {
			String contract = contracts.getChildText("CONTRACT");
			String ver = contracts.getChildText("VER");
			String hotelid = contracts.getChildText("HOTEL");
			String hotelname = contracts.getChildText("HOTELNAME");
			allotresult.setContract(contract);
			allotresult.setHotelvar(ver);
			allotresult.setHotelid(hotelid);
			allotresult.setHotelname(hotelname);
			List<Allotment> allotments = new ArrayList<Allotment>();
			List<Element> allotment = contracts.getChildren("ALLOTMENT");
			for (Element allotElement : allotment) {
				Allotment allment = new Allotment();
				String allott = allotElement.getChildText("ALLOT");
				allment.setAllot(allott);
				List<StayDate> stays = new ArrayList<StayDate>();
				List<Element> stay = allotElement.getChildren("STAYDATE");
				for (Element element : stay) {
					StayDate sdate = new StayDate();
					String staydate = element.getChildText("STAYDATE");
					String stdate = sdf.format(sd.parse(staydate));
					sdate.setDatestr(stdate);
					String isallot = element.getChildText("IS_ALLOT");
					sdate.setAllot(isallot);
					String roomclosed = element.getChildText("ROOMCLOSED");
					stays.add(sdate);
				}
				allment.setStaydates(stays);
				allotments.add(allment);
			}
			allotresult.setAllotments(allotments);
		}
		return allotresult;
	}

	public PriceResult parsexmlrate(String xmlstr) {
		PriceResult priceresult = new PriceResult();
		try {
			SAXBuilder sb = new SAXBuilder();
			SimpleDateFormat sd = new SimpleDateFormat("dd-M-yy");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Document doc;
			doc = sb.build(new StringReader(xmlstr));
			Element root = doc.getRootElement();
			Element result = root.getChild("XML_RESULT");
			List<Element> contractslist = result.getChildren("CONTRACTS");
			for (Element contracts : contractslist) {
				if (contracts != null) {
					String hmcontract = contracts.getChildText("CONTRACT");
					priceresult.setContract(hmcontract);
					System.out.println("酒店合同代码:" + hmcontract);
					String hmcontractver = contracts.getChildText("VER");
					priceresult.setVer(hmcontractver);
					String hmhotelid = contracts.getChildText("HOTEL");
					priceresult.setHotelid(hmhotelid);
					String hmhotelname = contracts.getChildText("HOTELNAME");
					priceresult.setHotelname(hmhotelname);
					String cur = contracts.getChildText("CUR");
					priceresult.setCur(cur);
					List<Element> products = contracts.getChildren("PRODUCT");
					String block=contracts.getChildText("BLOCK");
					System.out.println("当前是否可用:"+(block.equals("Y")?"no":"yes"));
					if(!block.equals("Y")){
						List<ResultProduct> prods = new ArrayList<ResultProduct>();
						for (Element product : products) {
							ResultProduct repro = new ResultProduct();
							String prod = product.getChildText("PROD");
							repro.setProd(prod);
							String nation = product.getChildText("NATION");
							repro.setNation(nation);
							System.out.println("国家代码:" + nation);
							String nationame = product.getChildText("NATIONNAME");
							repro.setNationname(nationame);
							String min = product.getChildText("MIN");
							String max = product.getChildText("MAX");
							repro.setMin(Integer.parseInt(min));
							repro.setMax(Integer.parseInt(max));
							String advance = product.getChildText("ADVANCE");
							repro.setAdvance(Integer.parseInt(advance));
							String ticket = product.getChildText("TICKET");
							repro.setTicket(Integer.parseInt(ticket));
							List<ResultRoom> roomtemp = new ArrayList<ResultRoom>();
							List<Element> rooms = product.getChildren("ROOM");
							for (Element room : rooms) {
								ResultRoom resultroom = new ResultRoom();
								String hmroomtype = room.getChildText("CAT");
								resultroom.setCat(hmroomtype);
								String type = room.getChildText("TYPE");
								resultroom.setType(type);
								// 服务包
								String serv = room.getChildText("SERV");
								resultroom.setServ(serv);
								// System.out.println("服务包:" + serv);
								// 以包括早餐数量
								String bf = room.getChildText("BF");
								resultroom.setBf(bf);
								String deadline = room.getChildText("DEADLINE");
								resultroom.setDeadline(sdf.format(sd.parse(deadline)));
								List<Element> stays = room.getChildren("STAY");
								List<ResultStay> stayslist = new ArrayList<ResultStay>();
								Map<String, ResultStay> staysdatetemp = new HashMap<String, ResultStay>();
								for (Element stay : stays) {
									ResultStay staytemp = new ResultStay();
									String statedate = stay.getChildText("STAYDATE");
									String datenum = sdf.format(sd.parse(statedate));
									staytemp.setStaydate(datenum);
									String price = stay.getChildText("PRICE");
									staytemp.setPrice(price);
									String allot = stay.getChildText("ALLOT");
									// Y-房型已獲得分配, 能夠即時確認 N-房型未獲得分配, 等待回覆 C-酒店關閉表示满房
									staytemp.setAllot(allot);
									String isallot = stay.getChildText("IS_ALLOT");
									staytemp.setIsallot(isallot);
									stayslist.add(staytemp);
									staysdatetemp.put(datenum, staytemp);
								}
								resultroom.setStays(stayslist);
								resultroom.setStaystemp(staysdatetemp);
								roomtemp.add(resultroom);
							}
							repro.setRooms(roomtemp);
							prods.add(repro);
							
						}
						priceresult.setProduct(prods);
					}else{
						System.out.println(hmhotelid+":不可预订");
						WriteLog.write("不可预订", hmhotelid+":不可预订");
						Server.getInstance().getSystemService().findMapResultBySql(
								"delete from t_hmhotelprice where c_hotelid in (select distinct id from t_hotel where c_hotelcode='"+hmhotelid+"')" , null);
						System.out.println("删除成功……");
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("解析异常：" + xmlstr);
		}
		return priceresult;
	}

	/**
	 * 实时查询价格接口 huc
	 */
	@Override
	public List<HotelGoodData> getQrateHotelData(String hotelid, Date checkin,
			Date checkout, String hotelname, String country, String citycode,
			String roomtypecode, String bedcode, String grade, String instance)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.US);
		String totalurl = "http://123.196.114.122:8034/cn_interface/HMHotel.jsp?api=qrate&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_checkin="
				+ sdf.format(checkin)
				+ "&p_checkout="
				+ sdf.format(checkout)
				+ "&p_hotel="
				+ hotelid
				+ "&p_rcat="
				+ roomtypecode
				+ "&p_type="
				+ bedcode
				+ "&p_grade="
				+ grade
				+ "&p_country="
				+ country
				+ "&p_hotelname="
				+ hotelname
				+ "&p_city="
				+ citycode
				+ "&p_instant=" + instance;
		String xmlstr = Util.getStr(totalurl);
		List<HotelGoodData> hotelGoodDataList = parsexmlQrateHotelData(xmlstr);
		return hotelGoodDataList;

	}

	/**
	 * 解析实时查询价格接口返回数据 huc
	 * 
	 * @param xmlstr
	 * @return
	 */
	public List<HotelGoodData> parsexmlQrateHotelData(String xmlstr) {
		List<HotelGoodData> hotelGoodDataList = new ArrayList<HotelGoodData>();
		try {
			SAXBuilder sb = new SAXBuilder();
			Document doc;
			doc = sb.build(new StringReader(xmlstr));
			Element root = doc.getRootElement();
			Element result = root.getChild("XML_RESULT");
			Element contracts = result.getChild("CONTRACTS");
			if (contracts != null) {
				String hotelcode = contracts.getChildText("HOTEL");
				List<Hotel> hotelList = Server.getInstance().getHotelService()
						.findAllHotel("where C_HOTELCODE='" + hotelcode + "'",
								"", -1, 0);
				if (hotelList != null && hotelList.size() > 0) {
					Hotel hotel = hotelList.get(0);

					String hmcontract = contracts.getChildText("CONTRACT");// 酒店合同號碼
					String hmcontractver = contracts.getChildText("VER");// 酒店合同版本
					String hmhotelname = contracts.getChildText("HOTELNAME");// 酒店名稱

					List<Element> products = contracts.getChildren("PRODUCT");
					for (Element product : products) {
						String prod = product.getChildText("PROD");// 提醒代碼
						String min = product.getChildText("MIN");// 最少停留晚數
						String advance = product.getChildText("ADVANCE");// 提早預訂天數
						List<Element> rooms = product.getChildren("ROOM");
						for (Element room : rooms) {
							String hmroomtype = room.getChildText("CAT");// 房型代碼
							String type = room.getChildText("TYPE");// 床型

							// 只有一条数据
							List<Bedtype> bedtypeList = Server.getInstance()
									.getHotelService().findAllBedtype(
											"where C_TYPE='" + type + "'", "",
											-1, 0);
							if (bedtypeList == null || bedtypeList.size() == 0) {
								WriteLog.write("实时查询价格接口日志", "没有查到床型数据:床型代码["
										+ type + "],房型代码[" + hmroomtype
										+ "],酒店id[" + hotel.getId() + "]--"
										+ xmlstr);
							} else {

								// 只有一条数据
								List<Roomtype> roomtypeList = Server
										.getInstance().getHotelService()
										.findAllRoomtype(
												"where C_ROOMCODE='"
														+ hmroomtype
														+ "' and C_HOTELID="
														+ hotel.getId()
														+ " and C_BED="
														+ bedtypeList.get(0)
																.getId(), "",
												-1, 0);
								if (roomtypeList == null
										|| roomtypeList.size() == 0) {
									WriteLog.write("实时查询价格接口日志",
											"没有查到房型数据:房型代码[" + hmroomtype
													+ "],酒店id[" + hotel.getId()
													+ "]--" + xmlstr);
								}

								String bf = room.getChildText("BF");// 已包括早餐數量
								List<Element> stays = room.getChildren("STAY");
								for (Element stay : stays) {
									String statedate = stay
											.getChildText("STAYDATE");// 停留日期
																		// dd-MM-yy
									String price = stay.getChildText("PRICE");// 價格
									String isallot = stay
											.getChildText("IS_ALLOT");// Y-房型已獲得分配,
																		// 能夠即時確認
																		// N-房型未獲得分配,
																		// 等待回覆
																		// C-酒店關閉表示满房
									String allot = stay.getChildText("ALLOT");
									for (Roomtype roomtype : roomtypeList) {
										HotelGoodData hotelGoodData = new HotelGoodData();
										hotelGoodData.setAllot(allot);
										hotelGoodData.setHotelid(hotel.getId());// 酒店ID
										hotelGoodData.setHotelname(hmhotelname);// 酒店名称
										hotelGoodData.setBaseprice(Long
												.valueOf(price));// 本地底价
										if ("Y".equals(isallot)) {
											hotelGoodData.setYuliunum(1L);// IS_ALLOT
																			// 为Y时
																			// 预留房数目设置1
										} else if ("N".equals(isallot)) {
											hotelGoodData.setYuliunum(0L);// IS_ALLOT
																			// 为Y时
																			// 预留房数目设置1
										} else {// 满房
											hotelGoodData.setYuliunum(-1l);
										}
										String[] statedateArr = statedate
												.split("-");
										StringBuffer statedatesb = new StringBuffer();
										statedatesb.append("20").append(
												statedateArr[2]).append("-")
												.append(statedateArr[1])
												.append("-").append(
														statedateArr[0]);
										hotelGoodData.setDatenum(statedatesb
												.toString());// TODO 日期
																// yyyy-MM-dd
										hotelGoodData.setMinday(Long
												.valueOf(min));// 最少多少天（连住）
										hotelGoodData.setBeforeDay(Long
												.valueOf(advance));// 提前几天
										hotelGoodData.setContractID(hmcontract);// 合同id
										hotelGoodData
												.setContractver(hmcontractver);// 合同版本
										hotelGoodData.setProdid(prod);// 产品id
										hotelGoodData.setBfcount(Long
												.valueOf(bf));// 早餐数量
										hotelGoodData.setSorucetype("3");// 数据源
										hotelGoodData.setCityid(hotel
												.getCityid().toString()); // 城市id

										hotelGoodData.setRoomtypeid(roomtype
												.getId());// 房型ID
										hotelGoodData.setBedtypeid(Long
												.valueOf(roomtype.getBed()));// 床型id
										hotelGoodData.setQunarName(roomtype
												.getQunarname()); // 去哪name
										if (roomtype.getName() != null
												&& !"".equals(roomtype
														.getName())) {
											hotelGoodData
													.setRoomtypename(roomtype
															.getName());// 房型名称
											hotelGoodDataList
													.add(hotelGoodData);
										} else {
											WriteLog.write("实时查询价格接口日志",
													"Qunarname不存在:房型代码["
															+ hmroomtype
															+ "],酒店id["
															+ hotel.getId()
															+ "]--" + xmlstr);

										}
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			WriteLog.write("实时查询价格接口日志", "解析异常：" + xmlstr);
			e.printStackTrace();
		}
		return hotelGoodDataList;
	}


}
