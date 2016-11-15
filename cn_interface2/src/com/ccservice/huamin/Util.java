package com.ccservice.huamin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.region.Region;

public class Util {
	public static String getStr(String urlstr) {
		String totalurl = urlstr;
		URL url = null;
		try {
			url = new URL(totalurl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				//conn.setConnectTimeout(7000);
				//conn.setReadTimeout(60000);
				conn.setDoInput(true);
				conn.connect();
				InputStream inputStream = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(
						inputStream));
				StringBuilder sb = new StringBuilder();
				String str = "";
				while ((str = br.readLine()) != null) {
					sb.append(str);
				}
				String strReturn = sb.toString().trim();
				conn.disconnect();
				if(strReturn!=null && strReturn.contains("?<?xml version=\"1.0\" encoding=\"UTF-8\"?>")){
					strReturn = strReturn.replace("?<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				}
				return strReturn;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return "";
	}

	// 根据区域id查找本地区域id
	public static Long getregionidbycode(String code, String regionname) {
		List<Region> regions = Server.getInstance().getHotelService()
				.findAllRegion(
						" WHERE " + Region.COL_HUAMINCODE + "='" + code + "'",
						"", -1, 0);
		if (regions.size() > 0) {
			if (regions.size() > 1) {
				for (Region region : regions) {
					if (regionname.equals(region.getName())) {
						return region.getId();
					}
				}
			} else {
				return regions.get(0).getId();
			}
		}
		return 0l;
	}

	// 根据华敏城市code查找本地城市id
	public static long getcityidbycode(String huamincode) {
		List<City> cityes = Server.getInstance().getHotelService().findAllCity(
				" where c_type=1 and " + City.COL_HUAMINCODE + "='"
						+ huamincode + "'", "", -1, 0);
		if (cityes.size() > 0) {
			return cityes.get(0).getId();
		}
		return 0;
	}

}
