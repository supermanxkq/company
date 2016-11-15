package com.ccservice.bussinessmancode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.bussinessentry.PriceEntry;
import com.ccservice.bussinessman.DDS2Stub;
import com.ccservice.bussinessman.DDS2Stub.GetSingleHotel;
import com.ccservice.bussinessman.DDS2Stub.GetSingleHotelResponse;

public class BusInstancePrice {
	public static void main(String[] args) throws Exception {
		List<Hotel> hotels = Server.getInstance().getHotelService()
				.findAllHotel("where 1=1 ",
						"order by id asc", -1, 0);
		for (Hotel hotel : hotels) {
			int FHotelId = Integer.parseInt(hotel.getHotelcode());
			int FRoomId = 0;
			Calendar FInDate = Calendar.getInstance();
			FInDate.add(Calendar.DAY_OF_MONTH, 1);
			Calendar FOutDate = Calendar.getInstance();
			FOutDate.add(Calendar.DAY_OF_MONTH, 7);
			int FPaymentMode = 1;
			int FUserLevel = 2;
			//List<PriceEntry> entrys = getInstancePrice(FHotelId, FRoomId,
			//		FInDate, FOutDate, FPaymentMode, FUserLevel);
			//for (PriceEntry priceEntry : entrys) {
			//	System.out.println(priceEntry);
			//}
		}
	}

	public  List<PriceEntry> getInstancePrice(int FHotelId, int FRoomId,
			Calendar FInDate, Calendar FOutDate, int FPaymentMode,
			int FUserLevel) throws Exception {
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
		JSONArray Table2s = obj.getJSONArray("Table2");// 价格信息
		List<PriceEntry> prices = new ArrayList<PriceEntry>();
		Hotel hotel = null;
		for (int i = 0; i < Table2s.size(); i++) {
			PriceEntry priceentry = new PriceEntry();
			JSONObject Table2 = Table2s.getJSONObject(i);
			String DHI09Date = "";// 日期
			try {
				DHI09Date = sdf
						.format(sdf.parse(Table2.getString("DHI09Date")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			priceentry.setDatestr(DHI09Date);
			int DHI03DHI01Id = Table2.getInt("DHI03DHI01Id");// 酒店Id
			if (DHI03DHI01Id > 0) {
				List<Hotel> hotels = Server.getInstance().getHotelService()
						.findAllHotel("where C_HOTELCODE=" + DHI03DHI01Id, "",
								-1, 0);
				if (hotels.size() > 0) {
					hotel = hotels.get(0);
				}
			}
			if (hotel != null) {
				priceentry.setHotelid((int) hotel.getId());
			}
			int DHI10Id = Table2.getInt("DHI10Id");// 销售房型Id
			int DHI03Id = Table2.getInt("DHI03Id");// 房型信息Id
			Roomtype roomtype = null;
			if (DHI10Id > 0) {
				List<Roomtype> rooms = Server.getInstance().getHotelService()
						.findAllRoomtype("where c_roomcode=" + DHI03Id, "", -1,
								0);
				if (rooms.size() > 0) {
					roomtype = rooms.get(0);
				}
			}
			if (roomtype != null) {
				priceentry.setRoomtypeid((int) roomtype.getId());
			}
			String DHI10Pay = Table2.getString("DHI10Pay");// 销售价格
			priceentry.setPriceoffer(DHI10Pay);
			String DHI18InventoryTotal = Table2
					.getString("DHI18InventoryTotal");// 房型总库存
			String DHI18RoomState = Table2.getString("DHI18RoomState");// 房态Id
			if (!"".equals(DHI18RoomState)) {
				priceentry.setRoomstatus(Integer.parseInt(DHI18RoomState));
			} else {
				priceentry.setRoomstatus(5);
			}
			// 考下面介绍的参数表
			String DHP13Name = Table2.getString("DHP13Name");// 房态名称
			String DHI18SurplusInventory = Table2
					.getString("DHI18SurplusInventory");// 房型剩余库存
			priceentry.setRoomleave(DHI18SurplusInventory);
			String DHI10PayReturn = Table2.getString("DHI10PayReturn");// 返佣额
			int DHI08DOP02Id = Table2.getInt("DHI08DOP02Id");// 支付方式Id Int 1
			// 转账 / 2 现付 / 3
			// 全额预付
			String DHI10DUP02Id = Table2.getString("DHI10DUP02Id");// 客户等级Id
			// Int 1
			// 散客 / 2 同行
			int DHI10BreakfastDesc = Table2.getInt("DHI10BreakfastDesc");// 早餐类型Id
			// Int
			// 参考下面介绍的参数表
			String DHP01Name = Table2.getString("DHP01Name");// 早餐类型名称
			priceentry.setBreakfastDescId(DHI10BreakfastDesc + "");
			priceentry.setBreakfastName(DHP01Name);
			int DHI10DHP07Id = Table2.getInt("DHI10DHP07Id");// 促销等级Id'
			String DHP07Name = Table2.getString("DHP07Name");// 促销等级名称

			int DHI10DHP08Id = Table2.getInt("DHI10DHP08Id");// 最低预定量Id
			int DHP08Amount = Table2.getInt("DHP08Amount");// 最低预定量
			priceentry.setMinAmount(DHP08Amount);
			int DHI10DHP09Id = Table2.getInt("DHI10DHP09Id");// 提前预定天数Id
			int DHP09Days = Table2.getInt("DHP09Days");// 提前预定天数 Int
			priceentry.setBeforeDay(DHP09Days);
			int DHI10DHP10Id = Table2.getInt("DHI10DHP10Id");// 最低消费额Id
			String DHP10Payed = Table2.getString("DHP10Payed");// 最低消费额
			priceentry.setMinPayed(DHP10Payed);
			int DHI10DHP11Id = Table2.getInt("DHI10DHP11Id");// 取消条款Id
			String DHP11Name = Table2.getString("DHP11Name");// 取消条款名称
			priceentry.setCancelname(DHP11Name);
			prices.add(priceentry);
		}
		return prices;
	}
}
