package com.ccservice.huamin;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;

public class Qratechange {
	public static void main(String[] args) {
	}

	// http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qratechange&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_checkin=20-JUL-12&p_checkout=08-AUG-12&p_country=CHN&p_city=SHA&p_modify=25-JUN-12
	public static void qratechange(Date checkin, Date checkOut, Date modify) {
		SimpleDateFormat sd = new SimpleDateFormat("dd-M-yy");
		String sql = " where C_HUAMINCODE is not null ";
		List<City> cityes = Server.getInstance().getHotelService().findAllCity(
				sql, "order by id asc ", -1, 0);
		for (City city : cityes) {
			String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qratechange&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_checkin="
					+ sd.format(checkin)
					+ "&p_checkout="
					+ sd.format(checkOut)
					+ "&p_country=CHN&p_city="
					+ city.getHuamincode()
					+ "&p_modify=" + sd.format(modify);
			String xmlstr = Util.getStr(totalurl);
		}

	}
}
