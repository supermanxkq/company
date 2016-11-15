package com.ccservice.bussinessmancode;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.bedtype.Bedtype;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelproduct.hotelproduct;
import com.ccservice.b2b2c.base.hotelserv.hotelserv;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.bussinessman.DDS2Stub;
import com.ccservice.bussinessman.DDS2Stub.GetSingleHotel;
import com.ccservice.bussinessman.DDS2Stub.GetSingleHotelResponse;

public class BussGetSingleHotel {
	public static void main(String[] args) throws Exception {
		List<Hotel> hotels = Server.getInstance().getHotelService()
				.findAllHotel("where 1=1 and c_sourcetype=5 and id<=33415", "order by id desc", -1, 0);
		for (Hotel hotel : hotels) {
			int FHotelId = Integer.parseInt(hotel.getHotelcode());
			int FRoomId = 0;
			Calendar FInDate = Calendar.getInstance();
			FInDate.add(Calendar.DAY_OF_MONTH, 1);
			Calendar FOutDate = Calendar.getInstance();
			FOutDate.add(Calendar.DAY_OF_MONTH, 2);
			int FPaymentMode = 1;
			int FUserLevel = 2;
			GetSingleHotel(FHotelId, FRoomId, FInDate, FOutDate, FPaymentMode,
					FUserLevel);
		}
	}

	/**
	 * 
	 * @param FHotelId
	 *            酒店Id Int 是
	 * @param FRoomId
	 *            房型Id Int 否 0 0 表示查询该酒店所有房型
	 * @param FInDate
	 *            入住日期 DateTime 是
	 * @param FOutDate
	 *            离店日期 DateTime 是
	 * @param FPaymentMode
	 *            支付方式 Int 否 0 0 全部 / 1 转账 2 现付 / 3 全额预付
	 * 
	 * @param FUserLevel
	 *            客户等级 Int 否 0 0 全部 / 1 散客 / 2 同行
	 * @throws RemoteException
	 * @throws Exception 
	 */
	public static void GetSingleHotel(int FHotelId, int FRoomId,
			Calendar FInDate, Calendar FOutDate, int FPaymentMode,
			int FUserLevel) throws RemoteException, Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		DDS2Stub stub = new DDS2Stub();
		GetSingleHotelResponse response = new GetSingleHotelResponse();
		GetSingleHotel getSingleHotel = new GetSingleHotel();
		getSingleHotel.setFHotelId(FHotelId);
		getSingleHotel.setFRoomId(FRoomId);
		getSingleHotel.setFInDate(FInDate);
		getSingleHotel.setFOutDate(FOutDate);
		getSingleHotel.setFPaymentMode(FPaymentMode);
		getSingleHotel.setFUserLevel(FUserLevel);
		getSingleHotel.setLoginToken(BussinessLogin.getLoginToken());
		response = stub.getSingleHotel(getSingleHotel);
		String jsonresult = response.getGetSingleHotelResult();
		JSONObject obj = JSONObject.fromObject(jsonresult.replaceAll("\n", ""));
		// Table
		JSONArray Tables = obj.getJSONArray("Table");// 酒店信息
		for (int i = 0; i < Tables.size(); i++) {
			JSONObject Table = Tables.getJSONObject(i);
			int hotelid = Table.getInt("DHI01Id");// 酒店Id Int
			String hotelname = Table.getString("DHI01Name");// 酒店名称 String
			String englishname = Table.getString("DHI01EnglishName");// 酒店英文名称
			String DHI01PinyinCode = Table.getString("DHI01PinyinCode");// 酒店拼音简写
			String DHI01Nation = Table.getString("DHI01Nation");// 国家Id Int
			int DHI01ProvinceId = Table.getInt("DHI01ProvinceId");// 省份Id Int
			int DHI01CityId = Table.getInt("DHI01CityId");// 城市Id Int
			int DHI01RegionId = Table.getInt("DHI01RegionId");// 区域Id Int
			int DHI01DHP02Id = Table.getInt("DHI01DHP02Id");// 星级Id Int
			String DHI01Traffic = Table.getString("DHI01Traffic");// 交通 String
			String DHI01Around = Table.getString("DHI01Around");// 周围环境 String
			String DHI01Server = Table.getString("DHI01Server");// 宾馆服务项目 String
			String DHI01Entertainments = Table.getString("DHI01Entertainments");// 宾馆餐饮娱乐与健身设施
			String DHI01Introduce = Table.getString("DHI01Introduce");// 酒店介绍
			String DHI01Explains = Table.getString("DHI01Explains");// 酒店特别提示
			String DHI01StartDatetemp = Table.getString("DHI01StartDate");
			String DHI01TrimDatetemp = Table.getString("DHI01TrimDate");//
			String DHI01StartDate = "";
			String DHI01TrimDate = "";
			try {
				DHI01StartDate = sdf.format(sdf.parse(DHI01StartDatetemp));// 开业时间
				DHI01TrimDate = sdf.format(sdf.parse(DHI01TrimDatetemp));// 装修时间
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String DHI01SiteUrl = Table.getString("DHI01SiteUrl");// 酒店网址
			String DHI01Telephone = Table.getString("DHI01Telephone");// 总机电话号码
			String DHI01Address = Table.getString("DHI01Address");// 酒店地址
			int DHI01IsAirportServer = Table.getInt("DHI01IsAirportServer");// 是否有接机服务
			// Int 0
			// 否 / 1
			// 是
			int DHI01DHP04Id = Table.getInt("DHI01DHP04Id");// 推荐等级
			int DHI01IsAcceptForeign = Table.getInt("DHI01IsAcceptForeign");// 是否接受外宾
			// Int 0
			// 否 / 1
			// 是
			int DHI01IsShow = Table.getInt("DHI01IsShow");// 前台是否显示 Int 0 否 /
															// 1 是
			String DHI01PointX = Table.getString("DHI01PointX");// 纬度 Float 百度坐标
			String DHI01PointY = Table.getString("DHI01PointY");// 经度 Float 百度坐标
			String DHI10WarrantyInfo = Table.getString("DHI10WarrantyInfo");// 担保信息
			String DHU02Name = Table.getString("DHU02Name");// 国家名称
			String PDHU01Name = Table.getString("PDHU01Name");// PDHU01Name省份名称
			String PDHU01EnglishName = Table.getString("PDHU01EnglishName");// 省份英文名称
			String PDHU01Pinyin = Table.getString("PDHU01Pinyin");// PDHU01Pinyin省份拼音
			String CDHU01Name = Table.getString("CDHU01Name");// 城市名称
			String CDHU01EnglishName = Table.getString("CDHU01EnglishName");// 城市英文名称
			String CDHU01Pinyin = Table.getString("CDHU01Pinyin");// 城市拼音
			String RDHU01Name = Table.getString("RDHU01Name");// 区域名称
			String RDHU01EnglishName = Table.getString("RDHU01EnglishName");// 区域英文名称
			String RDHU01Pinyin = Table.getString("RDHU01Pinyin");// 区域拼音
			String DHP02Name = Table.getString("DHP02Name");// 星级名称
			String DHP04Name = Table.getString("DHP04Name");// 推荐等级名称
			String DHI01Distances = Table.getString("DHI01Distances");// 公里数
			String DHI01LowestPrices = Table.getString("DHI01LowestPrices");// 最低价格
		}
		// Table1
		JSONArray Table1s = obj.getJSONArray("Table1");// 房型信息
		for (int i = 0; i < Table1s.size(); i++) {
			JSONObject Table1 = Table1s.getJSONObject(i);
			System.out.println("房型："+Table1s.getString(i));
			Roomtype roomtype=new Roomtype();
			roomtype.setLanguage(0);
			int DHI03DHI01Id = Table1.getInt("DHI03DHI01Id");// 酒店Id
			List<Hotel> hotels=Server.getInstance().getHotelService().findAllHotel("where "+Hotel.COL_hotelcode+"='"+DHI03DHI01Id+"'", "", -1, 0);
			Hotel hotel=new Hotel();
			if(hotels.size()>0){
				hotel=hotels.get(0);
				roomtype.setHotelid(hotel.getId());
			}
			String DHI03Id = Table1.getString("DHI03Id");// 房型信息Id
			roomtype.setRoomcode(DHI03Id);
			String DHI11Name = Table1.getString("DHI11Name");// 房型名称
			roomtype.setName(DHI11Name);
			String DHI03DHP05Id = Table1.getString("DHI03DHP05Id");// 床型Id
			String DHP05Name = Table1.getString("DHP05Name");// 床型名称
			List<Bedtype> bedtypes=Server.getInstance().getHotelService().findAllBedtype("where "+Bedtype.COL_type+"='"+DHI03DHP05Id+"'", "", -1, 0);
			Bedtype bedtype=new Bedtype();
			bedtype.setType(DHI03DHP05Id+"");
			bedtype.setTypename(DHP05Name);
			bedtype.setMaxguest(new Long(1));
			if(bedtypes.size()>0){
				bedtype.setId(bedtypes.get(0).getId());
				Server.getInstance().getHotelService().updateBedtypeIgnoreNull(bedtype);
				bedtype=bedtypes.get(0);
				roomtype.setBed((int)bedtype.getId());
			}else{
				bedtype=Server.getInstance().getHotelService().createBedtype(bedtype);
				roomtype.setBed((int)bedtype.getId());
			}
			String DHI03NoSmoking = Table1.getString("DHI03NoSmoking");// 是否无烟
			String DHI03Price = Table1.getString("DHI03Price");// 门市价格
			roomtype.setStartprice(Double.parseDouble(DHI03Price));
			String DHI03Dimension = Table1.getString("DHI03Dimension");// 房间面积
			roomtype.setAreadesc(DHI03Dimension);
			String DHI03Storey = Table1.getString("DHI03Storey");// 楼层
			roomtype.setLayer(DHI03Storey);
			String DHI03Info = Table1.getString("DHI03Info");// 房型介绍
			roomtype.setRoomdesc(DHI03Info);
			String DHI03Computer = Table1.getString("DHI03Computer");// 是否配置电脑
			// Int 0
			// 否 / 1
			// 是
			String DHI03IsOnLine = Table1.getString("DHI03IsOnLine");// 是否配置上网
			// Int 0
			// 否 / 1
			// 是
			String DHI03OnLineRemark = Table1.getString("DHI03OnLineRemark");// 上网备注
			roomtype.setWidedesc(DHI03OnLineRemark);
			String DHI03SpecialRoomType = Table1
					.getString("DHI03SpecialRoomType");// 特殊房型
			String DHI03Image = Table1.getString("DHI03Image");// 房型图片 String
			// 该字段暂时无数据
			String DHI03Remark = Table1.getString("DHI03Remark");// 房型备注
			roomtype.setNote(DHI03Remark);
			String DHI03IsInvalid = Table1.getString("DHI03IsInvalid");// 是否作废
			// Int 0
			// 否 / 1
			// 是
			String DHI03AddCarpet = Table1.getString("DHI03AddCarpet");// 是否加地毯
			// Int 0
			// 否 / 1
			// 是
			int DHI08DOP02Id = Table1.getInt("DHI08DOP02Id");// 支付方式Id Int 1
			// 转账 / 2 现付 / 3
			// 全额预付
			String DOP02Name = Table1.getString("DOP02Name");// 支付方式名称
			String DHI10DUP02Id = Table1.getString("DHI10DUP02Id");// 客户等级Id Int 1
			// 散客 / 2同行
			String DUP02Name = Table1.getString("DUP02Name");// 客户等级名称
			int DHI10BreakfastDesc = Table1.getInt("DHI10BreakfastDesc");// 早餐类型Id
			// Int
			// 参考下面介绍的参数表
			String DHI10BreakfastDescription = Table1
					.getString("DHI10BreakfastDescription");// 早餐类型名称
			roomtype.setBreakfast(DHI10BreakfastDesc);
			int DHI10DHP07Id = Table1.getInt("DHI10DHP07Id");// 促销等级Id
			String DHP07Name = Table1.getString("DHP07Name");// 促销等级名称
			hotelproduct product=new hotelproduct();
			product.setRatetype(DHP07Name);
			int DHI10DHP08Id = Table1.getInt("DHI10DHP08Id");// 最低预定量Id
			int DHP08Amount = Table1.getInt("DHP08Amount");// 最低预定量
			product.setMinday((long)DHP08Amount);
			int DHI10DHP09Id = Table1.getInt("DHI10DHP09Id");// 提前预定天数Id Int
			int DHP09Days = Table1.getInt("DHP09Days");// DHP09Days
			product.setAdvance((long)DHP09Days);
			int DHI10DHP10Id = Table1.getInt("DHI10DHP10Id");// 最低消费额Id
			String DHP10Payed = Table1.getString("DHP10Payed");// 最低消费额
			product.setMinprice(DHP10Payed);
			String DHI10DHP11Id = Table1.getString("DHI10DHP11Id");// 取消条款Id
			product.setCancel(DHI10DHP11Id);
			String DHP11Name = Table1.getString("DHP11Name");// 取消条款名称
			product.setCanceldesc(DHP11Name);
			product.setHotelid(hotel.getId());
			//String DHI10FrontDesc=Table1.getString("DHI10FrontDesc");//销售房型备注
			// json字符串缺少该字段，需和生意人确认
			List<Roomtype> roomtypes=Server.getInstance().getHotelService().findAllRoomtype("where "+Roomtype.COL_roomcode+"='"+DHI03Id+"'", "", -1, 0);
			if(roomtypes.size()>0){
				roomtype.setId(roomtypes.get(0).getId());
				Server.getInstance().getHotelService().updateRoomtypeIgnoreNull(roomtype);
				roomtype=roomtypes.get(0);
			}else{
				roomtype=Server.getInstance().getHotelService().createRoomtype(roomtype);
			}
			product.setRoomtypeid(roomtype.getId());
			List<hotelproduct> products=Server.getInstance().getHotelService().findAllhotelproduct("where "+hotelproduct.COL_ROOMTYPEID+"="+roomtype.getId(), "", -1, 0);
			if(products.size()>0){
				product.setId(products.get(0).getId());
				Server.getInstance().getHotelService().updatehotelproductIgnoreNull(product);
				product=products.get(0);
			}else{
				product=Server.getInstance().getHotelService().createhotelproduct(product);
			}
		}
		Server.getInstance().getSystemService().findMapResultBySql("delete from T_HOTELPRODUCT where C_ROOMTYPEID  not in (select ID from T_ROOMTYPE)", null);
		JSONArray Table2s = obj.getJSONArray("Table2");// 价格信息
		for (int i = 0; i < Table2s.size(); i++) {
			JSONObject Table2 = Table2s.getJSONObject(i);
			System.out.println("pp:"+Table2s.getString(i));
			String DHI09Date = "";// 日期
			try {
				DHI09Date = sdf
						.format(sdf.parse(Table2.getString("DHI09Date")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			int DHI10Id = Table2.getInt("DHI10Id");// 销售房型Id
			String DHI10Pay = Table2.getString("DHI10Pay");// 销售价格
			String DHI18InventoryTotal = Table2
					.getString("DHI18InventoryTotal");// 房型总库存
			String DHI18RoomState = Table2.getString("DHI18RoomState");// 房态Id
			// 考下面介绍的参数表
			String DHP13Name = Table2.getString("DHP13Name");// 房态名称
			String DHI18SurplusInventory = Table2
					.getString("DHI18SurplusInventory");// 房型剩余库存
			String DHI10PayReturn = Table2.getString("DHI10PayReturn");// 返佣额
			int DHI03DHI01Id = Table2.getInt("DHI03DHI01Id");// 酒店Id
			int DHI03Id = Table2.getInt("DHI03Id");// 房型信息Id
			int DHI08DOP02Id = Table2.getInt("DHI08DOP02Id");// 支付方式Id Int 1
			// 转账 / 2 现付 / 3
			// 全额预付
			String DHI10DUP02Id = Table2.getString("DHI10DUP02Id");// 客户等级Id Int 1
			// 散客 / 2 同行
			int DHI10BreakfastDesc = Table2.getInt("DHI10BreakfastDesc");// 早餐类型Id
			// Int
			// 参考下面介绍的参数表
			String DHP01Name = Table2.getString("DHP01Name");// 早餐类型名称
			int DHI10DHP07Id = Table2.getInt("DHI10DHP07Id");// 促销等级Id
			String DHP07Name = Table2.getString("DHP07Name");// 促销等级名称
			int DHI10DHP08Id = Table2.getInt("DHI10DHP08Id");// 最低预定量Id
			int DHP08Amount = Table2.getInt("DHP08Amount");// 最低预定量
			int DHI10DHP09Id = Table2.getInt("DHI10DHP09Id");// 提前预定天数Id
			int DHP09Days = Table2.getInt("DHP09Days");// 提前预定天数 Int
			int DHI10DHP10Id = Table2.getInt("DHI10DHP10Id");// 最低消费额Id
			String DHP10Payed = Table2.getString("DHP10Payed");// 最低消费额
			int DHI10DHP11Id = Table2.getInt("DHI10DHP11Id");// 取消条款Id
			String DHP11Name = Table2.getString("DHP11Name");// 取消条款名称
		}
		JSONArray Table3s = obj.getJSONArray("Table3");// 加收信息
		for (int i = 0; i < Table3s.size(); i++) {
			JSONObject Table3 = Table3s.getJSONObject(i);
			System.out.println("加收："+Table3s.getString(i));
			String DHI09Date = "";// 日期
			try {
				if (!"".equals(Table3.getString("DHI09Date"))) {
					DHI09Date = sdf.format(sdf.parse(Table3
							.getString("DHI09Date")));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			String DHI02DHI03Id = Table3.getString("DHI02DHI03Id");// 房型信息Id
			hotelserv serv=new hotelserv();
			Roomtype roomtype=null;
			int DHI02DHI01Id = Table3.getInt("DHI02DHI01Id");// 酒店id
			Hotel hotel=null;
			List<Hotel> hotels=Server.getInstance().getHotelService().findAllHotel("where c_hotelcode='"+DHI02DHI01Id+"'", "", -1, 0);
			if(hotels.size()>0){
				hotel=hotels.get(0);
			}
			List<Roomtype> rooms=null;
			if(Integer.parseInt(DHI02DHI03Id)==0){
				if(hotel!=null){
					rooms=Server.getInstance().getHotelService().findAllRoomtype(" where c_hotelid="+hotel.getId(), "", -1, 0);
					serv.setHotelid(hotel.getId());
					serv.setRoomtypeid(0l);
				}
			}else{
				rooms=Server.getInstance().getHotelService().findAllRoomtype(" where c_roomcode='"+DHI02DHI03Id+"'", "", -1, 0);
				if(rooms.size()>0){
					roomtype=rooms.get(0);
				}
				if(roomtype!=null){
					serv.setRoomtypeid(roomtype.getId());
					serv.setHotelid(roomtype.getHotelid());
				}
			}
			int DHI02SellType = Table3.getInt("DHI02SellType");// 加收类型Id Int
			if(DHI02SellType==1){
				
			}else if(DHI02SellType==2){
				
			}else if(DHI02SellType==3){
				
			}else if(DHI02SellType==4){
				
			}
			// 参考下面介绍的参数表
			String DHI02Id = Table3.getString("DHI02Id");// 加收id
			serv.setServcode(DHI02Id);
			String DHI02Name = Table3.getString("DHI02Name");// 加收名称
			serv.setServname(DHI02Name);
			String DHI02Week = Table3.getString("DHI02Week");// 星期类型Id
			String DHI02DUP02Id = Table3.getString("DHI02DUP02Id");// 客户等级Id
																	// Int 1
			// 散客 / 2 同行
			String DHI02PriceType = Table3.getString("DHI02PriceType");// 支付方式Id
			// Int 1 转账
			// / 2 现付 /
			// 3 全额预付
			String DHI02SellPrice = Table3.getString("DHI02SellPrice");// 销售价格
			String DHI02Remark = Table3.getString("DHI02Remark");// 加收备注
			serv.setPrice(DHI02SellPrice);
			if(roomtype!=null&&!"".equals(DHI02Name)){
				List<hotelserv> servs=Server.getInstance().getHotelService().findAllhotelserv("where c_roomtypeid="+roomtype.getId()+" and c_servname='"+DHI02Name+"'", "", -1, 0);
				if(servs.size()>0){
					serv.setId(servs.get(0).getId());
					Server.getInstance().getHotelService().updatehotelservIgnoreNull(serv);
					serv=servs.get(0);
				}else{
					serv=Server.getInstance().getHotelService().createhotelserv(serv);
				}
			}else{
				List<hotelserv> servs=Server.getInstance().getHotelService().findAllhotelserv("where c_hotelid="+hotel.getId()+" and c_servname='"+DHI02Name+"'", "", -1, 0);
				if(servs.size()>0){
					serv.setId(servs.get(0).getId());
					Server.getInstance().getHotelService().updatehotelservIgnoreNull(serv);
					serv=servs.get(0);
				}else{
					serv=Server.getInstance().getHotelService().createhotelserv(serv);
				}
			}
		}
		JSONArray Table4s = obj.getJSONArray("Table4");// 取消规则信息
		for (int i = 0; i < Table4s.size(); i++) {
			JSONObject Table4 = Table4s.getJSONObject(i);
			System.out.println("取消："+Table4s.getString(i));
			String DHP11Date = "";// 日期
			try {
				DHP11Date = sdf
						.format(sdf.parse(Table4.getString("DHP11Date")));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String DHP11Name = Table4.getString("DHP11Name");// 取消条款名称
			int DHP12Id = Table4.getInt("DHP12Id");// 取消条款明细Id
			int DHP12DHP11Id = Table4.getInt("DHP12DHP11Id");// 取消条款Id
			int DHP12DayCount = Table4.getInt("DHP12DayCount");// 提前取消天数
			int DHP12Fee = Table4.getInt("DHP12Fee");// 扣款类型
			String DHP12Remark = Table4.getString("DHP12Remark");// 取消条款明细备注
			int DHP12HotelId = Table4.getInt("DHP12HotelId");// 酒店Id
			int DHP12RoomId = Table4.getInt("DHP12RoomId");// 房型Id
			int DHP12DOP02Id = Table4.getInt("DHP12DOP02Id");// 支付方式Id Int 1
			// 转账 / 2 现付 / 3
			// 全额预付
			int DHP12DUP02Id = Table4.getInt("DHP12DUP02Id");// 客户等级Id Int 1
			// 散客 / 2 同行
			int DHP12DHP07Id = Table4.getInt("DHP12DHP07Id");// 促销等级Id
			int DHP12DHP08Id = Table4.getInt("DHP12DHP08Id");// 最低预定量Id
			int DHP12DHP09Id = Table4.getInt("DHP12DHP09Id");// 提前预定天数Id;
			int DHP12DHP10Id = Table4.getInt("DHP12DHP10Id");// 最低消费额Id
			//String DHP11NameDesc = Table4.getString("DHP11NameDesc");// 取消条款明细
			String DHP11Key = Table4.getString("DHP11Key");// 匹配取消条款Key
		}
	}
}
