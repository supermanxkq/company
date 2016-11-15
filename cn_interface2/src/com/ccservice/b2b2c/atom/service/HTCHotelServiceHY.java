package com.ccservice.b2b2c.atom.service;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.kuxun.FindHotelInfo;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hmhotelprice.HTCPriceResult;
import com.ccservice.b2b2c.base.hmhotelprice.ResultStay;
import com.ccservice.b2b2c.base.hotel.BookedRates;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.province.Province;
import com.ccservice.b2b2c.base.region.Region;
import com.ccservice.b2b2c.base.roomtype.Roomtype;


public class HTCHotelServiceHY implements IHTCHotelServiceHY {


	// 获取酒店列表
	public List<Hotel> getproplistHY(String date) {
		// TODO Auto-generated method stub
		HTCHotelService hcs = new HTCHotelService();
		String output = null;
		List<Hotel> ht=new ArrayList<Hotel>();
		try {
			output = hcs.getproplist(date);
			SAXBuilder sxb = new SAXBuilder();
			Document document;
			document = sxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			Element props = root.getChild("props");
			List<Element> prop = props.getChildren("prop");
			for (Element propp : prop) {
				
				// 酒店id
				String id = propp.getChildText("id");
				
				// 酒店名称
				String name = propp.getChildText("name");
				// 酒店状态
				String status = propp.getChildText("status");
				// 酒店状态最后修改时间
				String statusdate = propp.getChildText("date");
				if (Integer.parseInt(id) >= 1050 && status.equals("A")) {
					Hotel hotel=new Hotel();
					hotel.setHotelcode(id);
					ht.add(hotel);
					System.out.println(name);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ht;
		
	}

	private void sleep(int i) {
		// TODO Auto-generated method stub
		
	}

	// 获取酒店基本信息
	public void getPropertyHY(long hotelid) {
		HTCHotelService hcs = new HTCHotelService();
		Hotel hotel=new Hotel();
		String output = hcs.getProperty(hotelid);
		try {
			SAXBuilder sab = new SAXBuilder();
			Document document;
			document = sab.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			Element result = root.getChild("prop");
			System.out.println("酒店id"+hotelid);
			System.out.println("result:"+result);
			if(result==null){
				System.out.println(output);
			}
			// 电话
			if(result!=null){
			String phone = result.getChildText("phone");
			hotel.setTortell(phone);
			// 酒店最晚保留时间
			String holdtime = result.getChildText("holdtime");
			// 地址信息
			String address = result.getChildText("address1");
			hotel.setAddress(address);
			// 传真
			String fax = result.getChildText("fax");
			hotel.setFax1(fax);
			// 星级
			String starrating = result.getChildText("starrating");
			if("".equals(starrating)||starrating==null){
				// 准星级
				String diamondrating = result.getChildText("diamondrating");
				if("2".equals(diamondrating)){
					diamondrating="7";
				}
				if("3".equals(diamondrating)){
					diamondrating="10";
				}
				if("4".equals(diamondrating)){
					diamondrating="13";
				}
				if("5".equals(diamondrating)){
					diamondrating="16";
				}
				hotel.setStar(Integer.parseInt(diamondrating));
			}else{
				hotel.setStar(Integer.parseInt(starrating));
			}
			
			
			
			// 开业日期
			String dateopened = result.getChildText("dateopened");
			if(!"".equals(dateopened)&&dateopened!=null){
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
				Date opendate=sdf.parse(dateopened);
				java.sql.Date odate=new java.sql.Date(opendate.getTime());
				//System.out.println("开业日期："+odate);
				hotel.setOpendate(odate);
			}
			// 邮编
			String zip = result.getChildText("zip");
			
			hotel.setPostcode(zip);
			// 支付方式
			String fop = result.getChildText("fop");
			
			//处理下
			String[] fops=fop.split("\\|");
			StringBuffer sb=new StringBuffer();
			for (int i = 0; i < fops.length; i++) {
				if("AX".equals(fops[i])){
					sb.append("美国运通卡,");
				}
				if("MC".equals(fops[i])){
					sb.append("万事达卡,");
				}
				if("DC".equals(fops[i])){
					sb.append("大莱卡,");
				}
				if("CA".equals(fops[i])){
					sb.append("现金,");
				}
				if("CUP".equals(fops[i])){
					sb.append("国内银联卡,");
				}
				if("VI".equals(fops[i])){
					sb.append("维萨卡");
				}
			}
			//System.out.println(sb);
			hotel.setCarttype(sb.toString());
			// 结账时间
			//String checkout = result.getChildText("checkout");
			// 加床价格
			String extraadult = result.getChildText("extraadult");
			// 时区
			//String timezone = result.getChildText("timezone");
			// 所在省市
			String state = result.getChildText("state");
			String wherex="where C_HTCODE='"+state+"'";
			List<Province> prov = Server.getInstance().getHotelService().findAllProvince(wherex, "", -1, 0);
			hotel.setProvinceid(prov.get(0).getId());
			// 纬度
			String latitude = result.getChildText("latitude");
			if(!"".equals(latitude)&&latitude!=null){
				hotel.setLng(Double.valueOf(latitude));
			}
			
			// 经度
			String longitude = result.getChildText("longitude");
			if(!"".equals(longitude)&&longitude!=null){
				hotel.setLat(Double.valueOf(longitude));
			}
			
			// 机场距离
			String airportdistance = result.getChildText("airportdistance");
			// 酒店类型
			String proptype = result.getChildText("proptype");
			//System.out.println("酒店类型："+proptype);
			// 服务设施信息
			String amenities = result.getChildText("amenities");
			//System.out.println("服务设置："+amenities);
			hotel.setRoomAmenities(amenities);
			// 装修日期
			String daterenovated = result.getChildText("daterenovated");
			hotel.setRepaildate(daterenovated);
			// 酒店名称
			String name = result.getChildText("name");
			System.out.println("酒店名称："+name);
			hotel.setName(name);
			
			hotel.setHotelcode(String.valueOf(hotelid));
			
			// 城市名称
			String cityName = result.getChildText("cityname");
			
			String citywhere="where C_NAME='"+cityName+"'";
			List<City> city=Server.getInstance().getHotelService().findAllCity(citywhere, "", -1, 0);
		//	if(city.size()>0){
				hotel.setCityid(city.get(0).getId());
		//	}
			
			
			// 商圈<tag id="15320">人民广场</tag>,id 是什么，怎么拿
			Element tradearea = result.getChild("tradearea");
			if(!"".equals(tradearea)&&tradearea!=null){
				String tag = tradearea.getChildText("tag");
				String where="where C_NAME='"+tag+"' and C_CITYID="+city.get(0).getId();
				List<Region> region = Server.getInstance().getHotelService().findAllRegion(where, "", -1, 0);
				//System.out.println("商圈："+tag+"    "+region.get(0).getId());
				if(region.size()>0){
					hotel.setRegionid2(region.get(0).getId());
				}
				
			}
			
			
			// 行政区代码
			String districtid = result.getChildText("districtid");
			String wheres="where C_HTCODE='"+districtid+"'";
			//List<Region> reg=Server.getInstance().getHotelService().findAllRegion(wheres, "", -1, 0);
			//hotel.setRegionid1(reg.get(0).getId());
			// 状态
			String status = result.getChildText("statu");
			// 国家168
			//String country = result.getChildText("country");
			hotel.setCountryid(168l);
			// 机场
			String airports = result.getChildText("airports");
			
			//取消金制度
			List<Element> cxlrules = result.getChildren("cxlrule");
			for (Element cxlrule : cxlrules) {
				// rule
				String rule = cxlrule.getChildText("rule");
				// 取消制度描述
				String cxdescription = cxlrule.getChildText("description");
				//System.out.println(cxdescription);
				// penaltyvalue？
				String penaltyvalue = cxlrule.getChildText("penaltyvalue");
				// cancelinpenalty
				String cancelinpenalty = cxlrule
						.getChildText("cancelinpenalty");
				// period
				String period = cxlrule.getChildText("period");
				// notimeframe
				String notimeframe = cxlrule.getChildText("notimeframe");
				// nonrefundable
				String nonrefundable = cxlrule.getChildText("nonrefundable");
				// number
				String number = cxlrule.getChildText("number");
			}
			Element descriptions = result.getChild("descriptions");
			// 地标信息
			String corporate = descriptions.getChildText("corporate");
			// 餐厅设施
			String dining = descriptions.getChildText("dining");
			//System.out.println("餐厅设施："+dining);
			hotel.setFootitem(dining);
			// 酒店交通
			String directions = descriptions.getChildText("directions");
			//System.out.println("酒店交通："+directions);
			hotel.setTrafficinfo(directions);
			// 地理位置
			String location = descriptions.getChildText("location");
			//System.out.println("地理位置："+location);
			hotel.setDescription(location);
			// 会议设施
			String meetings = descriptions.getChildText("meetings");
			//System.out.println("会议设施："+meetings);
			hotel.setMeetingitem(meetings);
			// policies规则，入住时间，加床价，宠物等
			String policies = descriptions.getChildText("policies");
			// 卖点
			String property = descriptions.getChildText("property");
			//System.out.println("卖点"+property);
			hotel.setSellpoint(property);
			// 娱乐健身
			String recreation = descriptions.getChildText("recreation");
			//System.out.println("娱乐健身："+recreation);
			hotel.setPlayitem(recreation);
			// rooms
			String rooms = descriptions.getChildText("rooms");
			//System.out.println(rooms);
			//hotel.setRoomAmenities(rooms);
			// 安全设置
			String safety = descriptions.getChildText("safety");
			
			// 临近景点，方向，距离
			String attractions = descriptions.getChildText("attractions");
			
			//酒店信息来源
			hotel.setSourcetype(4l);
			//酒店类型
			hotel.setType(1);
			
				Hotel pr = Server.getInstance().getHotelService().createHotel(hotel);
				if(pr!=null){
					System.out.println("入库成功");
				}else{
					System.out.println("入库失败");
				}
			System.out.println("");
			}
			// 行政区名字
			//String districtname = result.getChildText("districtname");
			
			// airporttrans？
			//String airporttrans = result.getChildText("airporttrans");
			// 全部电梯数量
			//String totalelevators = result.getChildText("totalelevators");
			
			// 大床房数量（双人间）
			//String doublebeds = result.getChildText("doublebeds");
			// 双床房
			//String twinbeds = result.getChildText("twinbeds");
			// 入住时间
			//String checkin = result.getChildText("checkin");
			// 残疾人客房
			//String handicappedrooms = result.getChildText("handicappedrooms");
			// 大床房数量----------------------------------------------
			//String queenbeds = result.getChildText("queenbeds");
			// 设置？
			//String roomamenities = descriptions.getChildText("amenities");
			
			// 大床房（King）数量
			//String kingbeds = result.getChildText("kingbeds");
			// 楼层
			//String totalfloors = result.getChildText("totalfloors");
			//Element tax = result.getChild("tax");
			// 服务金额比例%
			//String amount = tax.getChildText("amount");
			// 服务费简称
			//String code = tax.getChildText("code");
			// code说明
			//String description = tax.getChildText("description");
			// method?
			//String method = tax.getChildText("method");
			// application?
			//String application = tax.getChildText("application");
			// stackorder?
			//String stackorder = tax.getChildText("stackorder");
			// typecode
			//String typecode = tax.getChildText("typecode");
			//Element group = result.getChild("group");
			// 集团拼音
			//String groupcode = group.getChildText("code");
			// 集团名称描述
			//String groupdescription = group.getChildText("description");
			// 集团名称
		//	String groupname = group.getChildText("name");
			// startdate
			//String startdate = group.getChildText("startdate");
			// 状态
			//String groupstatus = group.getChildText("status");
			// stopdate
			//String stopdate = group.getChildText("stopdate");
			// usedate
			//String usedate = group.getChildText("usedate");
			// 机场方向
			//String airportdirection = result.getChildText("airportdirection");
			
			// 最晚入住时间
			//String latecheckin = result.getChildText("latecheckin");
			// 客房数量
			//String totalrooms = result.getChildText("totalrooms");
			// pricerangehigh？
			// 无烟房数量
			//String nonsmokingrooms = result.getChildText("nonsmokingrooms");
			// 上线时间
			//String dateactivated = result.getChildText("dateactivated");
			// 酒店最大入住人数
			//String maxpropertyoccupancy = result
			//		.getChildText("maxpropertyoccupancy");
			
			// 会议室最大人数
			//String meetingroomcapacity = result
			//		.getChildText("meetingroomcapacity");
			// 会议室总面积
			//String meetingsquarefeet = result.getChildText("meetingsquarefeet");
			// pricerangelow？
			//String pricerangelow = result.getChildText("pricerangelow");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 获取酒店详细信息
	/*public void getDescHY(int id) {
		HTCHotelService hcs = new HTCHotelService();

		String output = hcs.getDesc(id);
		try {
			SAXBuilder saxb = new SAXBuilder();
			Document document;
			document = saxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			Element dm = root.getChild("descriptionmap");
			List<Element> descriptions = dm.getChildren("description");
			for (Element description : descriptions) {
				// 详细信息类型
				String type = description.getChildText("type");
				// 详细信息描述
				String content = description.getChildText("content");
				System.out.println(content);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 获取酒店指定类型的详细信息
	public String getDescByTypeHY(String type, int id) {
		HTCHotelService hcs = new HTCHotelService();

		String output = hcs.getDescByType(type, id);
		String content = "";
		try {
			SAXBuilder axb = new SAXBuilder();
			Document document;
			document = axb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			Element dm = root.getChild("descriptionmap");
			Element desc = dm.getChild("description");
			// 详细信息
			content = desc.getChildText("content");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return content;
	}
*/
	// 获取酒店所有房间代码与详细信息
	@Override
	public List<Roomtype> getRoomObjHY(long id) {
		// TODO Auto-generated method stub
		HTCHotelService hcs = new HTCHotelService();
		String output = hcs.getRoomObj(id);
		SAXBuilder saxb = new SAXBuilder();
		Document document;
		try {
			document = saxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			Element rom = root.getChild("roomobjmap");
			if(rom!=null){
			Element rod = rom.getChild("roomobjdata");
			List<Element> roomdetails = rod.getChildren("roomobjdetail");
//			/System.out.println("酒店id："+id);
			
			List<Roomtype> rtp=new ArrayList<Roomtype>();
			for (Element roomdetail : roomdetails) {
				
				
				Roomtype rt=new Roomtype();
			
				String where = "where C_HOTELCODE='"+id+"'";
				List<Hotel> hotel = Server.getInstance().getHotelService().findAllHotel(where, "", -1, 0);
				if(hotel.size()>0){
					
				
				
				System.out.println("酒店id："+hotel.get(0).getId());
				rt.setHotelid(hotel.get(0).getId());
				// 房型描述
				String description = roomdetail.getChildText("description");
			//	System.out.println("房型描述："+description);
				String[] kuan=description.split("\\，");
				/*for (int i = 0; i < kuan.length; i++) {
					//System.out.println(kuan[i]);
					if("免费宽带上网".equals(kuan[i])){
						rt.setWideband(1);
					}
				}*/
				rt.setRoomdesc(description);
				// 房间名称
				String roomname = roomdetail.getChildText("roomname");
			//	System.out.println("房间名称："+roomname);
				rt.setName(roomname);
				// 房间代码
				String roomtype = roomdetail.getChildText("roomtype");
			//	System.out.println("房间代码："+roomtype);
				rt.setRoomcode(roomtype);
				// 状态
				String status = roomdetail.getChildText("status");
				
				// 房间面积
				String area = roomdetail.getChildText("area");
			//	System.out.println("房间面积："+area);
				rt.setAreadesc(area);
				// 楼层
				String floor = roomdetail.getChildText("floor");
				//System.out.println("楼层："+floor);
				rt.setLayer(floor);
				// 其他信息
				String service = roomdetail.getChildText("service");
			//	System.out.println("其他信息："+service);
				//String[] kd=service.split("\\|");
				
				// 床型
				String bedtype = roomdetail.getChildText("bedtype");
			//	System.out.println("床型："+bedtype);
				String note=bedtype+"|"+service;
				rt.setNote(note);
				
				//rt.setHotelid(id);
				
				if("A".equals(status)){
					status="1";
					rt.setState(Integer.parseInt(status));
					Roomtype ss = Server.getInstance().getHotelService().createRoomtype(rt);
				}
				if("I".equals(status)){
					status="0";
					rt.setState(Integer.parseInt(status));
				}
					//Roomtype ss = Server.getInstance().getHotelService().createRoomtype(rt);
					//if(ss!=null){
					//	System.out.println("床型入库成功");
				//	}
			
				
				// 成人标准入住总数
				String numadults = roomdetail.getChildText("numadults");
				// 小孩标准入住总数
				String numchildren = roomdetail.getChildText("numchildren");
				// 加床总量
				String rollaway = roomdetail.getChildText("rollaway");
				// 最大入住量
				String maxoccupancy = roomdetail.getChildText("maxoccupancy");
				// 成人加床量
				String totadults = roomdetail.getChildText("totadults");
				// 小孩加床量
				String totchildren = roomdetail.getChildText("totchildren");
				// 房型类型
				String category = roomdetail.getChildText("category");
				
				rtp.add(rt);
				
			}
			//System.out.println("");
		//	System.out.println("");
			return rtp;
			}
		}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}

	// 根据房间类型代码查询房间详细信息
	@Override
	public void getRoomObjByTypeHY(String type, int id) {
		// TODO Auto-generated method stub
		HTCHotelService hcs = new HTCHotelService();
		String output = hcs.getRoomObjByType(type, id);
		SAXBuilder saxb = new SAXBuilder();
		Document document;

		try {
			document = saxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			Element rom = root.getChild("roomobjmap");
			Element rod = rom.getChild("roomobjdata");
			Element roomdetail = rod.getChild("roomobjdetail");
			// 房型描述
			String description = roomdetail.getChildText("description");
			// 成人标准入住总数
			String numadults = roomdetail.getChildText("numadults");
			// 小孩标准入住总数
			String numchildren = roomdetail.getChildText("numchildren");
			// 加床总量
			String rollaway = roomdetail.getChildText("rollaway");
			// 房间名称
			String roomname = roomdetail.getChildText("roomname");
			// 房间代码
			String roomtype = roomdetail.getChildText("roomtype");
			// 状态
			String status = roomdetail.getChildText("status");
			// 最大入住量
			String maxoccupancy = roomdetail.getChildText("maxoccupancy");
			// 成人加床量
			String totadults = roomdetail.getChildText("totadults");
			// 小孩加床量
			String totchildren = roomdetail.getChildText("totchildren");
			// 房间面积
			String area = roomdetail.getChildText("area");
			// 楼层
			String floor = roomdetail.getChildText("floor");
			// 床型
			String bedtype = roomdetail.getChildText("bedtype");
			// 房型类型
			String category = roomdetail.getChildText("category");
			// 其他信息
			String service = roomdetail.getChildText("service");
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 获取指定酒店所有价格代码的详细信息
	@Override
	public void getRateObjHY(int id,String date) {
		// TODO Auto-generated method stub
		HTCHotelService hcs = new HTCHotelService();
		HTCHotelServiceHY hcshy=new HTCHotelServiceHY();
		String output = hcs.getRateObj(id,date);
		SAXBuilder saxb = new SAXBuilder();
		Document document;
		try {
			document = saxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			Element rateobjmap = root.getChild("rateobjmap");
			Element rateobjdata = rateobjmap.getChild("rateobjdata");
			List<Element> rateobjdetails = rateobjdata
					.getChildren("rateobjdetail");
			for (Element rateobjdetail : rateobjdetails) {
				// 酒店价格代码
				String rateclass = rateobjdetail.getChildText("rateclass");
				System.out.println("酒店价格代码"+rateclass);
				// 价格名称
				String ratename = rateobjdetail.getChildText("ratename");
				System.out.println("价格名称："+ratename);
				//获取酒店对应的优惠的价格
				//String rate = hcshy.getOnlineRateMapByType(id, date, "", rateclass,night);
				// 描述
				String description = rateobjdetail.getChildText("description");
				// 保障金制度
				String guarrule = rateobjdetail.getChildText("guarrule");
				// 取消制度
				String cxlrule = rateobjdetail.getChildText("cxlrule");
				// RPI
				// String rpi=rateobjdetail.getChildText("RPI");
				// 价格类型
				// String ratecat=rateobjdetail.getChildText("ratecat");
				// 状态
				String status = rateobjdetail.getChildText("status");
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 获取指定酒店指定价格代码详细信息
	@Override
	public void getRateObjByTypeHY(String type, int id) {
		// TODO Auto-generated method stub
		HTCHotelService hcs = new HTCHotelService();
		String output = hcs.getRateObjByType(type, id);
		SAXBuilder saxb = new SAXBuilder();
		Document document;

		try {
			document = saxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			Element rateobjmap = root.getChild("rateobjmap");
			Element rateobjdata = rateobjmap.getChild("rateobjdata");
			Element rateobjdetail = rateobjdata.getChild("rateobjdetail");
			// 酒店价格代码
			String rateclass = rateobjdetail.getChildText("rateclass");
			// 价格名称
			String ratename = rateobjdetail.getChildText("ratename");
			// 描述
			String description = rateobjdetail.getChildText("description");
			// 保障金制度
			String guarrule = rateobjdetail.getChildText("guarrule");
			// 取消制度
			String cxlrule = rateobjdetail.getChildText("cxlrule");
			// RPI
			// String rpi=rateobjdetail.getChildText("RPI");
			// 价格类型
			// String ratecat=rateobjdetail.getChildText("ratecat");
			// 状态
			String status = rateobjdetail.getChildText("status");
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 获取酒店所有计划代码的详细信息
	@Override
	public String getPlanObj(long id) {
		// TODO Auto-generated method stub
		HTCHotelService hcs=new HTCHotelService();
		String output=hcs.getPlanObj(id);
		SAXBuilder saxb=new SAXBuilder();
		Document document;
		try {
			document=saxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			
			Element pomap = root.getChild("planobjmap");
			
			Element pod = pomap.getChild("planobjdata");
			
			List<Element> podetails=pod.getChildren("planobjdetail");
			System.out.println("酒店id:"+id);
			for (Element podetail : podetails) {
				//房型代码
				String roomtype=podetail.getChildText("roomtype");
				System.out.println("房型代码："+roomtype);
				//价格代码（bar）
				String rateclass=podetail.getChildText("rateclass");
				System.out.println("价格代码："+rateclass);
				//计划id，planid
				String planid=podetail.getChildText("planid");
				System.out.println("计划id、："+planid);
				//计划名称
				String planname=podetail.getChildText("planname");
				System.out.println("计划名称："+planname);
				//保证金制度
				String guarrule=podetail.getChildText("guarrule");
				System.out.println("保证金制度："+guarrule);
				//取消金制度
				String cxlrule=podetail.getChildText("cxlrule");
				System.out.println("取消金制度"+cxlrule);
				//种类RPI
				String rpi=podetail.getChildText("RPI");
				System.out.println("种类"+rpi);
				//价格类别
				String ratecat=podetail.getChildText("ratecat");
				System.out.println("价格类别："+ratecat);
				//状态，A，I
				String status=podetail.getChildText("status");
				System.out.println("状态"+status);
				System.out.println("");
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 获取酒店指定计划代码的详细信息
	@Override
	public String getPlanObjByPlanid(String planid, long id) {
		// TODO Auto-generated method stub
		HTCHotelService hcs=new HTCHotelService();
		String output=hcs.getPlanObj(id);
		SAXBuilder saxb=new SAXBuilder();
		Document document;
		try {
			document=saxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			
			Element pomap = root.getChild("planobjmap");
			
			Element pod = pomap.getChild("planobjdata");
			
			Element podetail=pod.getChild("planobjdetail");
			
				//房型代码
				String roomtype=podetail.getChildText("roomtype");
				//价格代码
				String rateclass=podetail.getChildText("rateclass");
				//计划id，planid
				//String planid=podetail.getChildText("planid");
				//计划名称
				String planname=podetail.getChildText("planname");
				//保证金制度
				String guarrule=podetail.getChildText("guarrule");
				//取消金制度
				String cxlrule=podetail.getChildText("cxlrule");
				//种类RPI
				String rpi=podetail.getChildText("RPI");
				//价格类别
				String ratecat=podetail.getChildText("ratecat");
				//状态，A，I
				String status=podetail.getChildText("status");
				
		
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 获取指定酒店图片
	@Override
	public String getImage(long id) {
		// TODO Auto-generated method stub
		HTCHotelService hcs=new HTCHotelService();
		String output=hcs.getImage(id);
		SAXBuilder saxb=new SAXBuilder();
		Document document;
		try {
			document=saxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			//酒店logo
			String logo=root.getChildText("picture");
			System.out.println("logo:"+logo);
			List<Element> galleryes = root.getChildren("gallerys");
			for (Element gallerys : galleryes) {
				List<Element> gallery=gallerys.getChildren("gallery");
				Attribute width = gallerys.getAttribute("width");
				int with = width.getIntValue();
				System.out.println("图片大小："+with);
				for (Element galleryx : gallery) {
					//图片标题
					String caption=galleryx.getChildText("caption");
					System.out.println("图片标题："+caption);
					//图片类型
					String imageCate=galleryx.getChildText("imageCate");
					System.out.println("图片标题："+imageCate);
					//图片地址
					String imageUrl=galleryx.getChildText("imageUrl");
					System.out.println("图片地址："+imageUrl);
				}
			}
			
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// 获取指定酒店订单服务
	@Override
	public String getPropresv(String channel, String confnum, String iata) {
		// TODO Auto-generated method stub
		
		return null;
	}

	// 查询订单审核状态
	@Override
	public String getResvaudit(String cnfnum, String iata) {
		// TODO Auto-generated method stub
		return null;
	}

	// 搜索酒店列表的可用性信息
	@Override
/*	public String hotelSearch(String date, String nights, String ratestyle,
			String prop, String proplv, String city, String district,
			String tradearea, String guestposition, String keywords,
			String pageindex, String pagesize) {
		// TODO Auto-generated method stub
		return null;
	}

	// 搜索单个酒店计划价格的可用性信息
	@Override
	public String hotelSerchById(String date, String nights, String ratestyle,
			String prop, String proplv, String city, String district,
			String tradearea, String guestposition, String keywords,
			String pageindex, String pagesize) {
		// TODO Auto-generated method stub
		return null;
	}

	// 批量搜索酒店信息及计划价格的可用性信息
	@Override
	public String hotelSerchAll(String date, String nights, String ratestyle,
			String prop, String proplv, String city, String district,
			String tradearea, String guestposition, String keywords,
			String pageindex, String pagesize) {
		// TODO Auto-generated method stub
		return null;
	}
*/
	
	// 缓存取价

	public List<HTCPriceResult> getCrateMap(int id,String date,int night) {
		// TODO Auto-generated method stub
		HTCHotelService hcs = new HTCHotelService();
		String output=hcs.getCrateMap(id,date,night);
		SAXBuilder saxb = new SAXBuilder();
		List<HTCPriceResult> hprs=new ArrayList<HTCPriceResult>();
		Document document;
		try {
			document = saxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			Element ratemap = root.getChild("ratemap");
			List<Element> ratedatas = ratemap.getChildren("ratedata");
			for (Element ratedata : ratedatas) {
				List<Element> plandetails = ratedata.getChildren("plandetail");
				HTCPriceResult hpr=new HTCPriceResult();
				for (Element plandetail : plandetails) {
					// 设施信息."|"分隔
					//String amenities = plandetail.getChildText("Amenities");
					// 取消金制度代码
					String cancelRule = plandetail.getChildText("CancelRule");
					// 佣金（暂不用）
					//String commission = plandetail.getChildText("Commission");
					// 计划描述
					//String description = plandetail.getChildText("Description");
					// 保障金制度代码
					String guaranteeRule = plandetail
							.getChildText("GuaranteeRule");
					// 价格代码 现付等
					String rate = plandetail.getChildText("Rate");
					hpr.setRate(rate);
					// 房型
					String room = plandetail.getChildText("Room");
					hpr.setRoomcode(room);
					// 状态
					String status = plandetail.getChildText("Status");
					hpr.setStatus(status);
					// 计划名称
					String planName = plandetail.getChildText("name");
					hpr.setPlanName(planName);
					// 计划代码
					String plan = plandetail.getChildText("plan");
					hpr.setPlan(plan);
					// 酒店id
					String hotelid = plandetail.getChildText("prop");
					hpr.setHotelid(hotelid);
					// 房型信息
					List<Element> roomtypes = plandetail
							.getChildren("roomtype");
					for (Element roomtype : roomtypes) {
						// 房型描述
						String typeDesc = roomtype.getChildText("Desc1");
						// 房型名称
						String typeName = roomtype.getChildText("name");
						hpr.setRoomtype(typeName);
						// AddBed??
						String addbed = roomtype.getChildText("AddBed");

					}
					// 价格信息
					List<Element> rateclasses = plandetail
							.getChildren("rateclass");
					for (Element rateclass : rateclasses) {
						// 取消金制度代码
						// String
						// rateCancelRule=rateclass.getChildText("CancelRule");
						// 保证金制度代码
						// String
						// rateGuaranteeRule=rateclass.getChildText("GuaranteeRule");
						// 价格描述
						//String rateDesc = rateclass.getChildText("Desc1");
						// 价格名称(例：现付价)
						String rateName = rateclass.getChildText("name");
						hpr.setRateName(rateName);
						/*
						 * 价格代码分类 RBT1：现付价 RBT2：限时促销 RBT3：预付优惠 RBT4：连住促销
						 * RBT5：提前预订优惠
						 */
						String ratebtype = rateclass.getChildText("ratebtype");
					}
					// 服务费信息
					List<Element> aces = plandetail
							.getChildren("AdditionalCharges");
					for (Element acs : aces) {
						// 服务费细则
						List<Element> ac = acs.getChildren("AdditionalCharge");
						for (Element acx : ac) {
							// 服务费代码
							String chargeCode = acx.getChildText("ChargeCode");
							hpr.setChargeCode(chargeCode);
							// 服务费状态 A有服务费且价格已包含 B有服务费且价格不包含 R无服务费

							String chargeState = acx
									.getChildText("ChargeState");
							hpr.setChargeState(chargeState);
							
							// 服务费价格
							String chargeAmount = acx
									.getChildText("ChargeAmount");
							hpr.setChargeAmount(chargeAmount);
							
							// 服务费频率 1每间/每次住宿 2每间/每晚 3每人/每次住宿 4每人/每晚
							String chargePlan = acx.getChildText("ChargePlan");
							hpr.setChargePlan(chargePlan);
							
							// 服务费描述
							String chargeDesc = acx.getChildText("ChargeDesc");
							hpr.setChargeDesc(chargeDesc);
							
						}
					}
					// 税金信息
					List<Element> taxes = plandetail.getChildren("Taxes");
					for (Element tax : taxes) {
						List<Element> taxx = tax.getChildren("Tax");
						for (Element tasx : taxx) {
							// 税金代码
							String taxCode = tasx.getChildText("TaxCode");
							// 税金状态
							String taxState = tasx.getChildText("TaxState");
							// 税金价格
							String taxAmount = tasx.getChildText("TaxAmount");
							// 税金频率
							String taxPlan = tasx.getChildText("TaxPlan");
							// 税金描述
							String taxDesc = tasx.getChildText("TaxDesc");
						}
					}
				}
				// 价格详细信息元素
				List<Element> ratedetails = ratedata.getChildren("ratedetail");
				for (Element ratedetail : ratedetails) {
					// 房量
					String allotment = ratedetail.getChildText("Allotment");
					// 房态
					String avStat = ratedetail.getChildText("AvStat");
					hpr.setAvStat(avStat);
					// 双人价
					//String doubleRate = ratedetail.getChildText("Double");
					// 加床价
					//String extraPerson = ratedetail.getChildText("ExtraPerson");
					// 最大入住天数
					//String maxLOS = ratedetail.getChildText("MaxLOS");
					// 最小入住天数
					String minLOS = ratedetail.getChildText("MinLOS");
					hpr.setMinLOS(minLOS);
					// 四人价
					//String quad = ratedetail.getChildText("Quad");
					// 单人价
					String ppsingle = ratedetail.getChildText("ppsingle");
					hpr.setPpsingle(ppsingle);
					// 三人价
					//String triple = ratedetail.getChildText("Triple");
					// 计划级房态描述
					// 0 非计划级价格停售
					// 1 计划级价格停售或满房
					String pr = ratedetail.getChildText("pr");
					// 提前预定期
					String leadTime = ratedetail.getChildText("LeadTime");
					hpr.setLeadTime(leadTime);
					// 早餐信息
					// 早餐数量|早餐价格
					String breakfastDesc = ratedetail
							.getChildText("BreakfastDesc");
					String[] bd=breakfastDesc.split("\\|");
					String breakfastNum=bd[0];
					String breakfastRate=bd[1];
					if(Integer.parseInt(breakfastNum)<0){
						breakfastNum="1";
					}
				//	System.out.println("早餐数量："+breakfastNum);
				//	System.out.println("早餐价格："+breakfastRate);
					hpr.setBreakfastNum(breakfastNum);
					hpr.setBreakfastRate(breakfastRate);
					// 宽带信息
					String netDesc = ratedetail.getChildText("NetDesc");
					hpr.setNetDesc(netDesc);
					// 床型信息
					String bedDesc = ratedetail.getChildText("BedDesc");
					hpr.setBedDesc(bedDesc);
					// 货币单位，CNY:人民币HKD:港元USD:美元
					String currenyCode = ratedetail.getChildText("CurrenyCode");
					hpr.setCurrenyCode(currenyCode);
				}
				Element resv = ratedata.getChild("resv");
				// 预定条件时保证金制度
				String resvGua = resv.getChildText("ResvGua");
				// 预定条件时保证金标志，0无、1担保、2预付
				String ResvGuaFlag = resv.getChildText("ResvGuaFlag");
				// 担保措施
				/*
				 * P、酒店处理信用卡预付 G、酒店信用卡担保 H、超时担保 D、hubs1信用卡担保 B、hubs1处理信用卡预付 W、无
				 */
				String resvGuaType = resv.getChildText("ResvGuaType");
				// 保证金种类
				/*
				 * 0 无 1 首日预付 2 全额预付 3 峰时预付 11 首日担保 12 全额担保 13 超时担保 14 峰时担保 15
				 * 无需担保
				 */
				String resvGuaClass = resv.getChildText("ResvGuaClass");
				// 酒店最晚保留时间
				String resvGuaHoldTime = resv.getChildText("ResvGuaHoldTime");
				// 担保金额和单位,D夜,W周，M月，P百分比，A定量
				String resvGuaAmount = resv.getChildText("ResvGuaAmount");
				// 保证金制度描述
				String resvGuaDesc = resv.getChildText("ResvGuaDesc");
				// 预定条件时取消金制度
				String resvCxl = resv.getChildText("ResvCxl");
				// 罚金,夜Nights|1,百分数Perc|xx,定量FlatRate|xx
				String resvCxlPenalty = resv.getChildText("ResvCxlPenalty");
				// 提前取消时间
				String resvCxlHour = resv.getChildText("ResvCxlHour");
				// 能否取消
				String resvCanCxl = resv.getChildText("ResvCanCxl");
				// 取消金描述
				String resvCxlDesc = resv.getChildText("ResvCxlDesc");
				// 预定条件时房态
				String resvStatus = resv.getChildText("ResvStatus");
				hpr.setResvStatus(resvStatus);
				// 不可订原因
				String noresvReason = resv.getChildText("noresvReason");
				hprs.add(hpr);
			}
			//System.out.println(hprs.size());
			return hprs;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hprs;
	}

	// 缓存根据房型取价
	@Override
	public String getCrateMapByType(int id,String date, String roomtype,String rateclass,int night) {
		// TODO Auto-generated method stub
		return null;
	}

	// 实时取价
	@Override
	public List<HTCPriceResult> getOnlineRateMap(long id, String date,int night) {
		// TODO Auto-generated method stub
		//System.out.println("汇通客实时取价....");
	
		HTCHotelService hcs = new HTCHotelService();
		List<HTCPriceResult> hprs=new ArrayList<HTCPriceResult>();
		String output = hcs.getOnlineRateMap(id, date,night);
		SAXBuilder saxb = new SAXBuilder();
		String hotelid="";
		String room="";
		String status="";
		String typeName="";
		Document document;
		try {
			document = saxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			Element ratemap = root.getChild("ratemap");
			List<Element> ratedatas = ratemap.getChildren("ratedata");
			for (Element ratedata : ratedatas) {
				List<Element> plandetails = ratedata.getChildren("plandetail");
				
				for (Element plandetail : plandetails) {
					// 设施信息."|"分隔
					//String amenities = plandetail.getChildText("Amenities");
					// 取消金制度代码
					String cancelRule = plandetail.getChildText("CancelRule");
					// 佣金（暂不用）
					//String commission = plandetail.getChildText("Commission");
					// 计划描述
					//String description = plandetail.getChildText("Description");
					// 保障金制度代码
					String guaranteeRule = plandetail
							.getChildText("GuaranteeRule");
					// 价格代码 现付等
					String rate = plandetail.getChildText("Rate");
				//	hpr.setRate(rate);
					// 房型
					room = plandetail.getChildText("Room");
					
					// 状态
					status = plandetail.getChildText("Status");
					
					// 计划名称
					String planName = plandetail.getChildText("name");
					//hpr.setPlanName(planName);
					// 计划代码
					String plan = plandetail.getChildText("plan");
					//hpr.setPlan(plan);
					// 酒店id
					hotelid = plandetail.getChildText("prop");
					
					// 房型信息
					List<Element> roomtypes = plandetail
							.getChildren("roomtype");
					for (Element roomtype : roomtypes) {
						// 房型描述
						String typeDesc = roomtype.getChildText("Desc1");
						// 房型名称
						typeName = roomtype.getChildText("name");
					//	hpr.setRoomtype(typeName);
						// AddBed??
						String addbed = roomtype.getChildText("AddBed");

					}
					// 价格信息
					List<Element> rateclasses = plandetail
							.getChildren("rateclass");
					for (Element rateclass : rateclasses) {
						// 取消金制度代码
						// String
						// rateCancelRule=rateclass.getChildText("CancelRule");
						// 保证金制度代码
						// String
						// rateGuaranteeRule=rateclass.getChildText("GuaranteeRule");
						// 价格描述
						//String rateDesc = rateclass.getChildText("Desc1");
						// 价格名称(例：现付价)
						String rateName = rateclass.getChildText("name");
					//	hpr.setRateName(rateName);
						/*
						 * 价格代码分类 RBT1：现付价 RBT2：限时促销 RBT3：预付优惠 RBT4：连住促销
						 * RBT5：提前预订优惠
						 */
						String ratebtype = rateclass.getChildText("ratebtype");
					}
					// 服务费信息
					List<Element> aces = plandetail
							.getChildren("AdditionalCharges");
					for (Element acs : aces) {
						// 服务费细则
						List<Element> ac = acs.getChildren("AdditionalCharge");
						for (Element acx : ac) {
							// 服务费代码
							String chargeCode = acx.getChildText("ChargeCode");
						//	hpr.setChargeCode(chargeCode);
							// 服务费状态 A有服务费且价格已包含 B有服务费且价格不包含 R无服务费

							String chargeState = acx
									.getChildText("ChargeState");
						//	hpr.setChargeState(chargeState);
							
							// 服务费价格
							String chargeAmount = acx
									.getChildText("ChargeAmount");
							//hpr.setChargeAmount(chargeAmount);
							
							// 服务费频率 1每间/每次住宿 2每间/每晚 3每人/每次住宿 4每人/每晚
							String chargePlan = acx.getChildText("ChargePlan");
							//hpr.setChargePlan(chargePlan);
							
							// 服务费描述
							String chargeDesc = acx.getChildText("ChargeDesc");
							//hpr.setChargeDesc(chargeDesc);
							
						}
					}
					// 税金信息
					List<Element> taxes = plandetail.getChildren("Taxes");
					for (Element tax : taxes) {
						List<Element> taxx = tax.getChildren("Tax");
						for (Element tasx : taxx) {
							// 税金代码
							String taxCode = tasx.getChildText("TaxCode");
							// 税金状态
							String taxState = tasx.getChildText("TaxState");
							// 税金价格
							String taxAmount = tasx.getChildText("TaxAmount");
							// 税金频率
							String taxPlan = tasx.getChildText("TaxPlan");
							// 税金描述
							String taxDesc = tasx.getChildText("TaxDesc");
						}
					}
				}
				Element resv = ratedata.getChild("resv");
				// 预定条件时保证金制度
				String resvGua = resv.getChildText("ResvGua");
				// 预定条件时保证金标志，0无、1担保、2预付
				String ResvGuaFlag = resv.getChildText("ResvGuaFlag");
				// 担保措施
				/*
				 * P、酒店处理信用卡预付 G、酒店信用卡担保 H、超时担保 D、hubs1信用卡担保 B、hubs1处理信用卡预付 W、无
				 */
				String resvGuaType = resv.getChildText("ResvGuaType");
				// 保证金种类
				/*
				 * 0 无 1 首日预付 2 全额预付 3 峰时预付 11 首日担保 12 全额担保 13 超时担保 14 峰时担保 15
				 * 无需担保
				 */
				String resvGuaClass = resv.getChildText("ResvGuaClass");
				// 酒店最晚保留时间
				String resvGuaHoldTime = resv.getChildText("ResvGuaHoldTime");
				// 担保金额和单位,D夜,W周，M月，P百分比，A定量
				String resvGuaAmount = resv.getChildText("ResvGuaAmount");
				// 保证金制度描述
				String resvGuaDesc = resv.getChildText("ResvGuaDesc");
				// 预定条件时取消金制度
				String resvCxl = resv.getChildText("ResvCxl");
				// 罚金,夜Nights|1,百分数Perc|xx,定量FlatRate|xx
				String resvCxlPenalty = resv.getChildText("ResvCxlPenalty");
				// 提前取消时间
				String resvCxlHour = resv.getChildText("ResvCxlHour");
				// 能否取消
				String resvCanCxl = resv.getChildText("ResvCanCxl");
				// 取消金描述
				String resvCxlDesc = resv.getChildText("ResvCxlDesc");
				// 预定条件时房态
				String resvStatus = resv.getChildText("ResvStatus");
				
				// 不可订原因
				String noresvReason = resv.getChildText("noresvReason");
				// 价格详细信息元素
				List<Element> ratedetails = ratedata.getChildren("ratedetail");
				for (Element ratedetail : ratedetails) {
					HTCPriceResult hpr=new HTCPriceResult();
					hpr.setHotelid(hotelid);
					hpr.setRoomcode(room);
					hpr.setStatus(status);
					hpr.setRoomtype(typeName);
					//即时订单判断
					hpr.setResvStatus(resvStatus);
					List<ResultStay> rs=new ArrayList<ResultStay>();
					
					ResultStay stay=new ResultStay();
					
					// 房量
					String allotment = ratedetail.getChildText("Allotment");
					// 房态
					String avStat = ratedetail.getChildText("AvStat");
					stay.setAllot(avStat);
					hpr.setAvStat(avStat);
					// 双人价
					//String doubleRate = ratedetail.getChildText("Double");
					// 加床价
					//String extraPerson = ratedetail.getChildText("ExtraPerson");
					// 最大入住天数
					//String maxLOS = ratedetail.getChildText("MaxLOS");
					//入住日期
					String datetime=ratedetail.getChildText("date");
					stay.setStaydate(datetime);
					//hpr.setDatetime(datetime);
				//	System.out.println(datetime);
					// 最小入住天数
					String minLOS = ratedetail.getChildText("MinLOS");
					hpr.setMinLOS(minLOS);
					// 四人价
					//String quad = ratedetail.getChildText("Quad");
					// 单人价
					String ppsingle = ratedetail.getChildText("ppsingle");
					stay.setPrice(ppsingle);
					hpr.setPpsingle(ppsingle);
					// 三人价
					//String triple = ratedetail.getChildText("Triple");
					// 计划级房态描述
					// 0 非计划级价格停售
					// 1 计划级价格停售或满房
					String pr = ratedetail.getChildText("pr");
					// 提前预定期
					String leadTime = ratedetail.getChildText("LeadTime");
					hpr.setLeadTime(leadTime);
					// 早餐信息
					// 早餐数量|早餐价格
					String breakfastDesc = ratedetail
							.getChildText("BreakfastDesc");
					String[] bd=breakfastDesc.split("\\|");
					String breakfastNum=bd[0];
					String breakfastRate=bd[1];
					if(Integer.parseInt(breakfastNum)<0){
						breakfastNum="1";
					}
				//	System.out.println("早餐数量："+breakfastNum);
				//	System.out.println("早餐价格："+breakfastRate);
					stay.setBfNum(breakfastNum);
					stay.setBfRate(breakfastRate);
				//	hpr.setBreakfastNum(breakfastNum);
				//	hpr.setBreakfastRate(breakfastRate);
					// 宽带信息
					String netDesc = ratedetail.getChildText("NetDesc");
					hpr.setNetDesc(netDesc);
					// 床型信息
					String bedDesc = ratedetail.getChildText("BedDesc");
					hpr.setBedDesc(bedDesc);
					// 货币单位，CNY:人民币HKD:港元USD:美元
					String currenyCode = ratedetail.getChildText("CurrenyCode");
					hpr.setCurrenyCode(currenyCode);
					rs.add(stay);
					hpr.setRs(rs);
					hprs.add(hpr);
				}
				
				
			}
			//System.out.println(hprs.size());
			
		//	return hprs;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*for (int i = 0; i < hprs.size(); i++) {
			HTCPriceResult rr = hprs.get(i);
			System.out.println(rr.getDatetime());
			System.out.println(rr.getRoomtype());
		}*/
		return hprs;
	}

	public static void main(String[] args) {
		HTCHotelServiceHY hcs = new HTCHotelServiceHY();
		//hcs.getproplistHY("2010-01-01");
		// hcs.getPropertyHY(1733);
//		 /hcs.getDescHY(6438);
		// System.out.println(hcs.getDescByTypeHY("dining", 1050));
		 //hcs.getRoomObjHY(15702);
		// hcs.getRoomObjByTypeHY("DK",1050);
		 //hcs.getRateObjHY(1050);
		//hcs.getOnlineRateMap(10760, "2012-11-02");
		//hcs.getImage(1176);
		//hcs.getPlanObj(1176);
		
		 List<Hotel> list = hcs.getproplistHY("");
		int x=0;
		label:for (Hotel hotel2 : list) {
			List<HTCPriceResult> vv = hcs.getOnlineRateMap(Long.valueOf(hotel2.getHotelcode()), "2012-12-18",1);
			if(vv.size()>0){
				for (HTCPriceResult priceResult : vv) {
				
					if(Double.parseDouble(priceResult.getPpsingle())>0){
						x++;
						continue label;
					}
				}
			}
			System.out.println(vv.size());
		}
		System.out.println("预付数量："+x);
		System.out.println("酒店总数量："+list.size()); 
		//hcs.getOnlineRateMapByType(1176, "2012-11-02", "", "");
		/* List<BookedRates> list=new ArrayList<BookedRates>();
		 for (int i = 0; i < 3; i++) {
			BookedRates br=new BookedRates();
			br.setDate("2012-10-24");
			br.setRate("1024");
			 list.add(br);
		}
		 hcs.newResvHY(1050, "", "", "", list, "","", "", "", "", "", "", "", "", "", "", "", "", "", "", "");*/
	
	}

	// 实时根据房型或付款类型取价
	@Override
	public String getOnlineRateMapByType(int id,String date, String roomtype,String rateclass,int night) {
		// TODO Auto-generated method stub
		HTCHotelService hcs = new HTCHotelService();
		String output = hcs.getOnlineRateMapByType(id, date, roomtype, rateclass,night);
		SAXBuilder saxb = new SAXBuilder();
		Document document;
		try {
			document = saxb.build(new StringReader(output.trim()));
			Element root = document.getRootElement();
			Element ratemap = root.getChild("ratemap");
			Element ratedata = ratemap.getChild("ratedata");
			
				List<Element> plandetails = ratedata.getChildren("plandetail");
				for (Element plandetail : plandetails) {
					// 设施信息."|"分隔
					String amenities = plandetail.getChildText("Amenities");
					// 取消金制度代码
					String cancelRule = plandetail.getChildText("CancelRule");
					// 佣金（暂不用）
					String commission = plandetail.getChildText("Commission");
					// 计划描述
					String description = plandetail.getChildText("Description");
					// 保障金制度代码
					String guaranteeRule = plandetail
							.getChildText("GuaranteeRule");
					// 价格代码 现付等
					String rate = plandetail.getChildText("Rate");
					// 房型
					String room = plandetail.getChildText("Room");
					// 状态
					String status = plandetail.getChildText("Status");
					// 计划名称
					String name = plandetail.getChildText("name");
					// 计划代码
					String plan = plandetail.getChildText("plan");
					// 酒店id
					String prop = plandetail.getChildText("prop");
					// 房型信息
					List<Element> roomtypes = plandetail
							.getChildren("roomtype");
					for (Element roomtyped : roomtypes) {
						// 房型描述
						String typeDesc = roomtyped.getChildText("Desc1");
						// 房型名称
						String typeName = roomtyped.getChildText("name");
						// AddBed??
						String addbed = roomtyped.getChildText("AddBed");

					}
					// 价格信息
					List<Element> rateclasses = plandetail
							.getChildren("rateclass");
					for (Element rateclassx : rateclasses) {
						// 取消金制度代码
						// String
						// rateCancelRule=rateclass.getChildText("CancelRule");
						// 保证金制度代码
						// String
						// rateGuaranteeRule=rateclass.getChildText("GuaranteeRule");
						// 价格描述
						String rateDesc = rateclassx.getChildText("Desc1");
						// 价格名称
						String rateName = rateclassx.getChildText("name");
						/*
						 * 价格代码分类 RBT1：现付价 RBT2：限时促销 RBT3：预付优惠 RBT4：连住促销
						 * RBT5：提前预订优惠
						 */
						String ratebtype = rateclassx.getChildText("ratebtype");
					}
					// 服务费信息
					List<Element> aces = plandetail
							.getChildren("AdditionalCharges");
					for (Element acs : aces) {
						// 服务费细则
						List<Element> ac = acs.getChildren("AdditionalCharge");
						for (Element acx : ac) {
							// 服务费代码
							String chargeCode = acx.getChildText("ChargeCode");
							// 服务费状态
							String chargeState = acx
									.getChildText("ChargeState");
							// 服务费价格
							String chargeAmount = acx
									.getChildText("ChargeAmount");
							// 服务费频率
							String chargePlan = acx.getChildText("ChargePlan");
							// 服务费描述
							String chargeDesc = acx.getChildText("ChargeDesc");
						
						}
					}
					// 税金信息
					List<Element> taxes = plandetail.getChildren("Taxes");
					for (Element tax : taxes) {
						List<Element> taxx = tax.getChildren("Tax");
						for (Element tasx : taxx) {
							// 税金代码
							String taxCode = tasx.getChildText("TaxCode");
							// 税金状态
							String taxState = tasx.getChildText("TaxState");
							// 税金价格
							String taxAmount = tasx.getChildText("TaxAmount");
							// 税金频率
							String taxPlan = tasx.getChildText("TaxPlan");
							// 税金描述
							String taxDesc = tasx.getChildText("TaxDesc");
						}
					}
				}
				// 价格详细信息元素
				List<Element> ratedetails = ratedata.getChildren("ratedetail");
				for (Element ratedetail : ratedetails) {
					// 房量
					String allotment = ratedetail.getChildText("Allotment");
					// 房态
					String avStat = ratedetail.getChildText("AvStat");
					// 双人价
					String doubleRate = ratedetail.getChildText("Double");
					// 加床价
					String extraPerson = ratedetail.getChildText("ExtraPerson");
					// 最大入住天数
					String maxLOS = ratedetail.getChildText("MaxLOS");
					// 最小入住天数
					String MinLOS = ratedetail.getChildText("MinLOS");
					// 四人价
					String quad = ratedetail.getChildText("Quad");
					// 单人价
					String single = ratedetail.getChildText("Single");
					
					// 三人价
					String triple = ratedetail.getChildText("Triple");
					// 计划级房态描述
					// 0 非计划级价格停售
					// 1 计划级价格停售或满房
					String pr = ratedetail.getChildText("pr");
					// 提前预定期
					String leadTime = ratedetail.getChildText("LeadTime");
					// 早餐信息
					// 早餐数量|早餐价格
					String breakfastDesc = ratedetail
							.getChildText("BreakfastDesc");
					// 宽带信息
					String netDesc = ratedetail.getChildText("NetDesc");
					// 床型信息
					String bedDesc = ratedetail.getChildText("BedDesc");
					// 货币单位，CNY:人民币HKD:港元USD:美元
					String currenyCode = ratedetail.getChildText("CurrenyCode");
				}
				Element resv = ratedata.getChild("resv");
				// 预定条件时保证金制度
				String resvGua = resv.getChildText("ResvGua");
				// 预定条件时保证金标志，0无、1担保、2预付
				String ResvGuaFlag = resv.getChildText("ResvGuaFlag");
				// 担保措施
				/*
				 * P、酒店处理信用卡预付 G、酒店信用卡担保 H、超时担保 D、hubs1信用卡担保 B、hubs1处理信用卡预付 W、无
				 */
				String resvGuaType = resv.getChildText("ResvGuaType");
				// 保证金种类
				/*
				 * 0 无 1 首日预付 2 全额预付 3 峰时预付 11 首日担保 12 全额担保 13 超时担保 14 峰时担保 15
				 * 无需担保
				 */
				String resvGuaClass = resv.getChildText("ResvGuaClass");
				// 酒店最晚保留时间
				String resvGuaHoldTime = resv.getChildText("ResvGuaHoldTime");
				// 担保金额和单位,D夜,W周，M月，P百分比，A定量
				String resvGuaAmount = resv.getChildText("ResvGuaAmount");
				// 保证金制度描述
				String resvGuaDesc = resv.getChildText("ResvGuaDesc");
				// 预定条件时取消金制度
				String resvCxl = resv.getChildText("ResvCxl");
				// 罚金,夜Nights|1,百分数Perc|xx,定量FlatRate|xx
				String resvCxlPenalty = resv.getChildText("ResvCxlPenalty");
				// 提前取消时间
				String resvCxlHour = resv.getChildText("ResvCxlHour");
				// 能否取消
				String resvCanCxl = resv.getChildText("ResvCanCxl");
				// 取消金描述
				String resvCxlDesc = resv.getChildText("ResvCxlDesc");
				// 预定条件时房态
				String resvStatus = resv.getChildText("ResvStatus");
				// 不可订原因
				String noresvReason = resv.getChildText("noresvReason");
			
		}catch (Exception e) {
				// TODO: handle exception
			
		}
		return null;
	}

	@Override
	public String newResvHY(int id, String isassure, String deliverymode,
			String outconfnum, List<BookedRates> bookedrate, String date,
			String nights, String roomtype, String rateclass, String rooms,
			String adults, String children, String firstname, String lastname,
			String street1, String holdTime, String phone, String mobile,
			String email, String remark, String IATA) {
		// TODO Auto-generated method stub
		HTCHotelService hcs=new HTCHotelService();
		hcs.newResvHY(id, isassure, deliverymode, outconfnum, bookedrate, 
				date, nights, roomtype, rateclass, rooms, adults, children, firstname, 
				lastname, street1, holdTime, phone, mobile, email, remark, IATA);
		return null;
	}

	
	
	

}
