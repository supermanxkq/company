package com.ccservice.bussinessmancode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.region.Region;
import com.ccservice.bussinessentry.GetHotelListPara;
import com.ccservice.bussinessman.DDS2Stub;
import com.ccservice.bussinessman.DDS2Stub.GetHotelList;
import com.ccservice.bussinessman.DDS2Stub.GetHotelListResponse;
import com.ccservice.huamin.WriteLog;

/**
 * @author wzc 酒店价格查询接口
 */
public class BussGetHotelList {
	public static void main(String[] args) throws Exception {
		List<City> city = Server.getInstance().getHotelService().findAllCity(
				"where c_busscode is not null", "order by id asc", -1, 0);
		for (int i = 0; i < city.size(); i++) {
			System.out.println("城市：" + city.get(i).getName());
			GetHotelListPara para = new GetHotelListPara();
			para.setFCityId(Integer.parseInt(city.get(i).getBusscode()));
			int pagesize = 10;
			para.setPageIndex(1);
			para.setPageSize(pagesize);
			Calendar cal1 = Calendar.getInstance();
			Calendar cal2 = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date date = sdf.parse(sdf.format(cal1.getTime()));
			cal1.setTime(date);
			cal1.add(Calendar.DAY_OF_MONTH, 1);
			para.setFInDate(cal1);
			System.out.println(cal1.getTime());
			Date date1 = sdf.parse(sdf.format(cal2.getTime()));
			cal2.setTime(date1);
			cal2.add(Calendar.DAY_OF_MONTH, 2);
			System.out.println(cal2.getTime());
			para.setFOutDate(cal2);
			int total = GetHotelList(para);
			System.out.println(total);
			// 总页数
			int pageNum = total % pagesize == 0 ? total / pagesize
					: ((total / pagesize) + 1);
			if (pageNum > 0) {
				System.out.println("总页数：" + pageNum);
				for (int j = 1; j <= pageNum; j++) {
					GetHotelListPara parat = new GetHotelListPara();
					parat.setFCityId(Integer
							.parseInt(city.get(i).getBusscode()));
					parat.setPageIndex(j);
					parat.setPageSize(pagesize);
					Calendar calt = Calendar.getInstance();
					Date datet = sdf.parse(sdf.format(calt.getTime()));
					calt.setTime(datet);
					calt.add(Calendar.DAY_OF_MONTH, 1);
					parat.setFInDate(calt);
					calt.add(Calendar.DAY_OF_MONTH, 1);
					parat.setFOutDate(calt);
					GetHotelList(parat);
					Thread.sleep(5000);
				}
			}
		}
	}

	public static int GetHotelList(GetHotelListPara para) throws Exception {
		DDS2Stub stub = new DDS2Stub();
		GetHotelListResponse response = new GetHotelListResponse();
		GetHotelList getHotelList = new GetHotelList();

		getHotelList.setLoginToken(BussinessLogin.getLoginToken());
		getHotelList.setFCityId(para.getFCityId());
		if (para.getFDHI01RegionId() > 0) {
			getHotelList.setFDHI01DHP02Id(para.getFDHI01RegionId());
		}
		if (para.getFDHI01Name() != null) {
			getHotelList.setFDHI01Name(para.getFDHI01Name());
		}
		if (para.getFDHI01DHP02Id() > 0) {
			getHotelList.setFDHI01DHP02Id(para.getFDHI01DHP02Id());
		}
		if (para.getFDHI01Address() != null) {
			getHotelList.setFDHI01Address(para.getFDHI01Address());
		}
		if (para.getFRoomName() != null) {
			getHotelList.setFRoomName(para.getFRoomName());
		}
		getHotelList.setFOrderByStar(para.getFOrderByStar());
		getHotelList.setFOrderByRecLevels(para.getFOrderByRecLevels());
		getHotelList.setFOrderByPrice(para.getFOrderByPrice());
		getHotelList.setFInDate(para.getFInDate());
		getHotelList.setFOutDate(para.getFOutDate());
		getHotelList.setFPaymentMode(para.getFPaymentMode());
		getHotelList.setFUserLevel(para.getFUserLevel());
		getHotelList.setFPromotionsRat(para.getFPromotionsRat());
		getHotelList.setFSmaillPrice(para.getFSmaillPrice());
		getHotelList.setFBigPrice(para.getFBigPrice());
		if (para.getFLandmark() != null) {
			getHotelList.setFLandmark(para.getFLandmark());
			if (para.getFPonitX() != null) {
				getHotelList.setFPonitX(para.getFPonitX());
			}
			if (para.getFPonitY() != null) {
				getHotelList.setFPonitY(para.getFPonitY());
			}
		}
		getHotelList.setFHotelDistance(para.getFHotelDistance());
		getHotelList.setFShowRoomCount(para.getFShowRoomCount());
		getHotelList.setPageIndex(para.getPageIndex());
		getHotelList.setPageSize(para.getPageSize());
		response = stub.getHotelList(getHotelList);
		int totalpage = response.getTotalPage();
		String jsonresult = response.getGetHotelListResult();
		System.out.println(jsonresult);
		JSONObject obj=null;
		try {
			obj = JSONObject.fromObject(jsonresult.replaceAll("\n", ""));
		JSONArray Table1s = obj.getJSONArray("Table1");
		for (int i = 0; i < Table1s.size(); i++) {
			JSONObject Table1 = Table1s.getJSONObject(i);
		}
		JSONArray Tables = obj.getJSONArray("Table");
		for (int i = 0; i < Tables.size(); i++) {
			JSONObject Table = Tables.getJSONObject(i);
			Hotel hotel = new Hotel();
			int hotelid = Table.getInt("DHI01Id");// 酒店Id Int
			hotel.setHotelcode(hotelid + "");
			String hotelname = Table.getString("DHI01Name");// 酒店名称 String
			hotel.setName(hotelname);
			String englishname = Table.getString("DHI01EnglishName");// 酒店英文名称
			hotel.setEnname(englishname);
			String DHI01PinyinCode = Table.getString("DHI01PinyinCode");// 酒店拼音简写
			hotel.setJpname(DHI01PinyinCode);
			String DHI01Nation = Table.getString("DHI01Nation");// 国家Id Int
			hotel.setCountryid(168l);
			int DHI01ProvinceId = Table.getInt("DHI01ProvinceId");// 省份Id Int
			int DHI01CityId = Table.getInt("DHI01CityId");// 城市Id Int
			List<City> cityes = Server.getInstance().getHotelService()
					.findAllCityBySql(
							"select * from T_CITY where C_BUSSCODE="
									+ DHI01CityId, -1, 0);
			if (cityes.size() > 0) {
				hotel.setCityid(cityes.get(0).getId());
				hotel.setProvinceid(cityes.get(0).getProvinceid());
			}
			int DHI01RegionId = Table.getInt("DHI01RegionId");// 区域Id Int
			List<Region> regions = Server.getInstance().getHotelService()
					.findAllRegionBySql(
							"select * from T_REGION where C_BUSSCODE="
									+ DHI01RegionId, -1, 0);
			if (regions.size() > 0) {
				hotel.setRegionid1(regions.get(0).getId());
			}
			/**
			 * 1 五星级 2 四星级 3 三星级 4 二星级 5 经济型 6 准五星 7 准四星 8 准三星
			 */
			int DHI01DHP02Id = Table.getInt("DHI01DHP02Id");// 星级Id Int
			if (DHI01DHP02Id == 1) {
				hotel.setStar(5);
			} else if (DHI01DHP02Id == 2) {
				hotel.setStar(4);
			} else if (DHI01DHP02Id == 3) {
				hotel.setStar(3);
			} else if (DHI01DHP02Id == 4) {
				hotel.setStar(2);
			} else if (DHI01DHP02Id == 5) {
				hotel.setStar(1);
			} else if (DHI01DHP02Id == 6) {
				hotel.setStar(16);
			} else if (DHI01DHP02Id == 7) {
				hotel.setStar(13);
			} else if (DHI01DHP02Id == 8) {
				hotel.setStar(10);
			}
			String DHI01Traffic = Table.getString("DHI01Traffic");// 交通 String
			hotel.setTrafficinfo(DHI01Traffic);
			String DHI01Around = Table.getString("DHI01Around");// 周围环境 String
			String DHI01Server = Table.getString("DHI01Server");// 宾馆服务项目 String
			hotel.setServiceitem(DHI01Server);
			String DHI01Entertainments = Table.getString("DHI01Entertainments");// 宾馆餐饮娱乐与健身设施
			hotel.setFootitem(DHI01Entertainments);
			String DHI01Introduce = Table.getString("DHI01Introduce");// 酒店介绍
			hotel.setDescription(DHI01Introduce);
			String DHI01Explains = Table.getString("DHI01Explains");// 酒店特别提示
			hotel.setAvailPolicy(DHI01Explains);
			String DHI01StartDatetemp = Table.getString("DHI01StartDate");
			String DHI01TrimDatetemp = Table.getString("DHI01TrimDate");//
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String DHI01StartDate = "";
			String DHI01TrimDate = "";
			try {
				if (!"".equals(DHI01StartDatetemp)) {
					DHI01StartDate = sdf.format(sdf.parse(DHI01StartDatetemp));// 开业时间
					Date date = sdf.parse(DHI01StartDate);
					hotel.setOpendate(new java.sql.Date(date.getTime()));
				}

				if (!"".equals(DHI01TrimDatetemp)) {
					DHI01TrimDate = sdf.format(sdf.parse(DHI01TrimDatetemp));// 装修时间
					hotel.setRepaildate(DHI01TrimDate);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}

			String DHI01SiteUrl = Table.getString("DHI01SiteUrl");// 酒店网址
			String DHI01Telephone = Table.getString("DHI01Telephone");// 总机电话号码
			hotel.setTortell(DHI01Telephone);
			String DHI01Address = Table.getString("DHI01Address");// 酒店地址
			hotel.setAddress(DHI01Address);
			hotel.setSourcetype(5l);
			int DHI01IsAirportServer = Table.getInt("DHI01IsAirportServer");// 是否有接机服务
			hotel.setAirportservice(DHI01IsAirportServer + "");
			// Int
			// 0 否
			// / 1
			// 是
			int DHI01DHP04Id = Table.getInt("DHI01DHP04Id");// 推荐等级
			// Int
			// 0 否
			// / 1
			// 是
			int DHI01IsShow = Table.getInt("DHI01IsShow");// 前台是否显示 Int 0 否 /

			String DHI10WarrantyInfo = Table.getString("DHI10WarrantyInfo");// 担保信息
			hotel.setWarrantyInfo(DHI10WarrantyInfo);
			int DHI01IsAcceptForeign = Table.getInt("DHI01IsAcceptForeign");// 是否接受外宾
			hotel.setAcceptForeign(DHI01IsAcceptForeign);
			// 1 是
			String DHI01PointX = Table.getString("DHI01PointX");// 纬度 Float 百度坐标
			String DHI01PointY = Table.getString("DHI01PointY");// 经度 Float 百度坐标
			if (DHI01PointX != null && !"".equals(DHI01PointX)) {
				hotel.setLat(Double.parseDouble(DHI01PointX));
			}
			if (DHI01PointY != null && !"".equals(DHI01PointY)) {
				hotel.setLng(Double.parseDouble(DHI01PointY));
			}

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
			if(!DHI01LowestPrices.equals("")){
				hotel.setStartprice(Double.parseDouble(DHI01LowestPrices));
			}
			List<Hotel> hotels = Server.getInstance().getHotelService()
					.findAllHotel("where C_HOTELCODE='" + hotel.getHotelcode(),
							"'", -1, 0);
			if (hotels.size() > 0) {
				hotel.setId(hotels.get(0).getId());
				System.out.println("更新：" + hotel.getName());
				Server.getInstance().getHotelService().updateHotelIgnoreNull(
						hotel);
			} else {
				System.out.println("添加：" + hotel.getName());
				hotel = Server.getInstance().getHotelService().createHotel(
						hotel);
			}
		}
		} catch (Exception e1) {
			e1.printStackTrace();
			WriteLog.write("json解析问题", "json字符串："+jsonresult+"\n"+e1.getMessage());
		}
		return totalpage;
	}
}
