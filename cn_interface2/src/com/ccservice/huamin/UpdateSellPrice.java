package com.ccservice.huamin;

import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hmhotelprice.Hmhotelprice;

//设置返点控制
public class UpdateSellPrice {
	public static void main(String[] args) {
		UpdateSellPrices(10);// 10个点
	}

	public static void UpdateSellPrices(int fan) {
		String where = "where C_HOTELID in (select ID from T_HOTEL where C_PAYTYPE=2 and C_STATE=3)";
		List<Hmhotelprice> hmList = Server.getInstance().getHotelService()
				.findAllHmhotelprice("where C_PRICEOFFER=c_price ", "order by id desc", -1, 0);
		System.out.println("size:"+hmList.size());
		for (Hmhotelprice hmhotelprice : hmList) {
			System.out.println("size:"+hmList.size());
			int d = (int) (hmhotelprice.getPrice() * (1 + fan / 100.0));
			System.out.println("id:"+hmhotelprice.getId());
			hmhotelprice.setPriceoffer(new Double(d));
			System.out.println(hmhotelprice.getPrice() + ":"
					+ hmhotelprice.getPriceoffer());
			Server.getInstance().getHotelService().updateHmhotelprice(
					hmhotelprice);
		}
	}
}
