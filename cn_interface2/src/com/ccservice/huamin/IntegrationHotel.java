package com.ccservice.huamin;

import java.sql.SQLException;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelimage.Hotelimage;

/**
 * 将艺龙的酒店基本数据导入到华闽可以匹配到的酒店
 * 
 * @author 师卫林 2012-6-29上午09:28:56
 */
public class IntegrationHotel {
	public static void main(String[] args) {
		System.out.println("开始执行~~~~");
		List<Hotel> hotelfromhm = Server.getInstance().getHotelService().findAllHotel(" WHERE C_SOURCETYPE=3 ", "", -1, 0);
		int i = 0;
		for (Hotel hotel : hotelfromhm) {
			List<Hotel> hotelfromyl = Server.getInstance().getHotelService().findAllHotel(
					" WHERE " + Hotel.COL_name + "='" + hotel.getName() + "' AND C_SOURCETYPE=1 AND C_STATE=3", "", -1, 0);
			Hotel hotelyl = new Hotel();
			if (hotelfromyl.size() > 0) {
				hotelyl = hotelfromyl.get(0);
				updateHotel(hotel.getId(), hotelyl.getId());
				i++;
			}
			List<Hotel> hotelfromyl2 = Server.getInstance().getHotelService().findAllHotel(" WHERE C_NAME LIKE '%" + hotel.getName() + "（原%'", "",
					-1, 0);
			if (hotelfromyl2.size() > 0) {
				for (Hotel hotel2 : hotelfromyl2) {
					Hotel hotelyl2 = new Hotel();
					hotelyl2 = hotel2;
					if (hotelyl2.getSourcetype() == 1 && hotelyl2.getState() == 3) {
						updateHotel(hotel.getId(), hotelyl2.getId());
						i++;
					}
				}
			}
		}
		List<Hotel> nanjing = Server.getInstance().getHotelService().findAllHotel(" WHERE C_NAME ='南京御庭精品酒店'", "", -1, 0);
		List<Hotel> nanjing2 = Server.getInstance().getHotelService().findAllHotel(" WHERE C_NAME ='南京御庭精品酒店（秦淮河店）'", "", -1, 0);
		updateHotel(nanjing.get(0).getId(), nanjing2.get(0).getId());
		List<Hotel> beijing = Server.getInstance().getHotelService().findAllHotel(" WHERE C_NAME ='北京北京大饭店'", "", -1, 0);
		List<Hotel> beijing2 = Server.getInstance().getHotelService().findAllHotel(" WHERE C_NAME ='北京大饭店'", "", -1, 0);
		updateHotel(beijing.get(0).getId(), beijing2.get(0).getId());
		List<Hotel> beijing3 = Server.getInstance().getHotelService().findAllHotel(" WHERE C_NAME ='北京北京饭店莱佛士'", "", -1, 0);
		List<Hotel> beijing4 = Server.getInstance().getHotelService().findAllHotel(" WHERE C_NAME ='北京饭店莱佛士'", "", -1, 0);
		updateHotel(beijing3.get(0).getId(), beijing4.get(0).getId());
		i += 3;
		System.out.println("华闽在艺龙可匹配到的酒店有:" + i + "家");
		System.out.println("执行结束~~");
	}

	public static void updateHotel(long hotelidhm, long hotelidyl) {
		Hotel hotelyl = Server.getInstance().getHotelService().findHotel(hotelidyl);
		Hotel hotelhm = Server.getInstance().getHotelService().findHotel(hotelidhm);
		if (hotelyl.getAddress() != null) {
			hotelhm.setAddress(hotelyl.getAddress());
		}

		if (hotelyl.getDescription() != null) {
			hotelhm.setDescription(hotelyl.getDescription());
		}

		if (hotelyl.getFootitem() != null) {
			hotelhm.setFootitem(hotelyl.getFootitem());
		}

		if (hotelyl.getServiceitem() != null) {
			hotelhm.setServiceitem(hotelyl.getServiceitem());
		}

		if (hotelyl.getMeetingitem() != null) {
			hotelhm.setMeetingitem(hotelyl.getMeetingitem());
		}

		if (hotelyl.getPlayitem() != null) {
			hotelhm.setPlayitem(hotelyl.getPlayitem());
		}

		if (hotelyl.getCarttype() != null) {
			hotelhm.setCarttype(hotelyl.getCarttype());
		}

		if (hotelyl.getRepaildate() != null) {
			hotelhm.setRepaildate(hotelyl.getRepaildate());
		}

		if (hotelyl.getPostcode() != null) {
			hotelhm.setPostcode(hotelyl.getPostcode());
		}

		if (hotelyl.getOpendate() != null) {
			hotelhm.setOpendate(hotelyl.getOpendate());
		}

		if (hotelyl.getSellpoint() != null) {
			hotelhm.setSellpoint(hotelyl.getSellpoint());
		}

		if (hotelyl.getAirportservice() != null) {
			hotelhm.setAirportservice(hotelyl.getAirportservice());
		}

		if (hotelyl.getTrafficinfo() != null) {
			hotelhm.setTrafficinfo(hotelyl.getTrafficinfo());
		}

		if (hotelyl.getRoomAmenities() != null) {
			hotelhm.setRoomAmenities(hotelyl.getRoomAmenities());
		}
		Server.getInstance().getHotelService().updateHotelIgnoreNull(hotelhm);
		System.out.println("艺龙酒店名字:" + hotelyl.getName());
		System.out.println("华闽酒店名字:" + hotelhm.getName());
		List<Hotelimage> hotelimagefromtable = Server.getInstance().getHotelService().findAllHotelimage(
				" WHERE " + Hotelimage.COL_hotelid + "='" + hotelyl.getId() + "'", "", -1, 0);
		for (Hotelimage hotelimage : hotelimagefromtable) {
			Hotelimage hmhotelimage = new Hotelimage();
			hmhotelimage.setDescription(hotelimage.getDescription());
			hmhotelimage.setHotelid(hotelhm.getId());
			hmhotelimage.setLanguage(hotelimage.getLanguage());
			hmhotelimage.setPath(hotelimage.getPath());
			hmhotelimage.setType(hotelimage.getType());
			List<Hotelimage> hmhimage = Server.getInstance().getHotelService().findAllHotelimage(
					" WHERE " + Hotelimage.COL_hotelid + "='" + hmhotelimage.getHotelid() + "' AND " + Hotelimage.COL_path + "='"
							+ hmhotelimage.getPath() + "'", "", -1, 0);
			if (hmhimage.size() > 0) {
				hmhotelimage.setId(hmhimage.get(0).getId());
				Server.getInstance().getHotelService().updateHotelimageIgnoreNull(hmhotelimage);
			} else {
				try {
					Server.getInstance().getHotelService().createHotelimage(hmhotelimage);
					System.out.println("新增一条图片记录~~~");
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
