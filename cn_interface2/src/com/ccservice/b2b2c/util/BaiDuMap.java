package com.ccservice.b2b2c.util;

public class BaiDuMap {
	static double DEF_PI = 3.14159265359; // PI
	static double DEF_2PI = 6.28318530712; // 2*PI
	static double DEF_PI180 = 0.01745329252; // PI/180.0
	static double DEF_R = 6370693.5; // radius of earth

	// 适用于近距离
	public static double GetShortDistance(double lon1, double lat1,
			double lon2, double lat2) {
		double ew1, ns1, ew2, ns2;
		double dx, dy, dew;
		double distance;
		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 经度差
		dew = ew1 - ew2;
		// 若跨东经和西经180 度，进行调整
		if (dew > DEF_PI)
			dew = DEF_2PI - dew;
		else if (dew < -DEF_PI)
			dew = DEF_2PI + dew;
		dx = DEF_R * Math.cos(ns1) * dew; // 东西方向长度(在纬度圈上的投影长度)
		dy = DEF_R * (ns1 - ns2); // 南北方向长度(在经度圈上的投影长度)
		// 勾股定理求斜边长
		distance = Math.sqrt(dx * dx + dy * dy);
		return distance;
	}

	// 适用于远距离
	public static double GetLongDistance(double lon1, double lat1, double lon2,
			double lat2) {
		double ew1, ns1, ew2, ns2;
		double distance;
		// 角度转换为弧度
		ew1 = lon1 * DEF_PI180;
		ns1 = lat1 * DEF_PI180;
		ew2 = lon2 * DEF_PI180;
		ns2 = lat2 * DEF_PI180;
		// 求大圆劣弧与球心所夹的角(弧度)
		distance = Math.sin(ns1) * Math.sin(ns2) + Math.cos(ns1)
				* Math.cos(ns2) * Math.cos(ew1 - ew2);
		// 调整到[-1..1]范围内，避免溢出
		if (distance > 1.0)
			distance = 1.0;
		else if (distance < -1.0)
			distance = -1.0;
		// 求大圆劣弧长度
		distance = DEF_R * Math.acos(distance);
		return distance;
	}

	/**
	 * 通过地址获取坐标值
	 * 
	 * @param shop
	 * @return
	 */
	public static String getPoint(String shop) {
		String result = "";
		try {
			String sCurrentLine;
			String sTotalString;
			sCurrentLine = "";
			sTotalString = "";
			java.io.InputStream l_urlStream;

			java.net.URL l_url = new java.net.URL(
					"http://api.map.baidu.com/geocoder/v2/?address="
							+ shop
							+ "&output=json&ak=702632E1add3d4953d0f105f27c294b9&callback=showLocation");
			java.net.HttpURLConnection l_connection = (java.net.HttpURLConnection) l_url
					.openConnection();
			l_connection.connect();
			l_urlStream = l_connection.getInputStream();
			java.io.BufferedReader l_reader = new java.io.BufferedReader(
					new java.io.InputStreamReader(l_urlStream));
			String str = l_reader.readLine();
			// 用经度分割返回的网页代码
			String s = "," + "\"" + "lat" + "\"" + ":";
			String strs[] = str.split(s, 2);
			String s1 = "\"" + "lng" + "\"" + ":";
			String a[] = strs[0].split(s1, 2);
			s1 = "}" + "," + "\"";
			String a1[] = strs[1].split(s1, 2);
			result = a[1] + "," + a1[0];
			// System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) {

		String point = BaiDuMap.getPoint("福建省宁德市福安市溪潭镇");
		String point1 = BaiDuMap.getPoint("广东省珠海市金湾区");
		String[] s = point.split(",");
		String[] s1 = point1.split(",");
		double distance = BaiDuMap.GetLongDistance(Double.parseDouble(s[0]),
				Double.parseDouble(s[1]), Double.parseDouble(s1[0]),
				Double.parseDouble(s1[1]));
		System.out.println(distance);
	}

	/**
	 * 
	 * @param destination
	 *            订单邮寄地址
	 * @param agentAddress
	 *            采购商地址
	 * @return
	 */
	public double getDistance(String destination, String agentAddress) {
		String point = BaiDuMap.getPoint(destination);
		String point1 = BaiDuMap.getPoint(agentAddress);
		String[] s = point.split(",");
		String[] s1 = point1.split(",");
		double distance = BaiDuMap.GetLongDistance(Double.parseDouble(s[0]),
				Double.parseDouble(s[1]), Double.parseDouble(s1[0]),
				Double.parseDouble(s1[1]));
		// System.out.println("distance=" + distance);
		return distance;
	}

}