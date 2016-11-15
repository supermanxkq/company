package com.ccservice.b2b2c.atom.component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONObject;

import com.ccservice.b2b2c.base.flightinfo.CarbinInfo;

public class QunarMethod {

	public static void main(String[] args) {
		Map<String, CarbinInfo> flightCabinInfo = getQunarFlightinfo("北京", "长沙", "2013-04-02",
				"2013-04-02");
//		int f = 542*100/690;
//		System.out.println(f);
	}

	/**
	 * 根据出发到达城市名称查询出去哪的价格
	 * @param searchArrivalAirport
	 * @param searchDepartureAirport
	 * @param searchDepartureTime
	 * @param searchReturnTime
	 * @return
	 */
	public static Map<String, CarbinInfo> getQunarFlightinfo(String searchArrivalAirport,
			String searchDepartureAirport, String searchDepartureTime,
			String searchReturnTime) {
		System.out.println("qunar"+System.currentTimeMillis());
		Map<String, CarbinInfo> specilCarbinMap = new HashMap<String, CarbinInfo>();
		Random random = new Random();
		int _token = random.nextInt(100000);
		String urlString="-1";
		try {
			urlString = "http://flight.qunar.com/twell/longwell?&http%3A%2F%2Fwww.travelco.com%2FsearchArrivalAirport="
					+ URLEncoder.encode(searchArrivalAirport, "utf-8")
					+ "&http%3A%2F%2Fwww.travelco.com%2FsearchDepartureAirport="
					+ URLEncoder.encode(searchDepartureAirport, "utf-8")
					+ "&http%3A%2F%2Fwww.travelco.com%2FsearchDepartureTime="
					+ searchDepartureTime
					+ "&http%3A%2F%2Fwww.travelco.com%2FsearchReturnTime="
					+ searchReturnTime
					+ "&locale=zh&nextNDays=0&searchLangs=zh&searchType=OneWayFlight&tags=1&from=fi_ont_search&_token="
					+ _token;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		specilCarbinMap = parseObject(SendPostandGet.submitPost(urlString,"","UTF-8").toString());
		System.out.println("qunar"+System.currentTimeMillis());
		return specilCarbinMap;
		
	}

	public static Map<String, CarbinInfo> parseObject(String json) {
		Map<String, CarbinInfo> specilCarbinMap = new HashMap<String, CarbinInfo>();
		if (json.startsWith("(")) {
			json = json.substring(1, json.length() - 1);
			// json = json.replace(" ", "");
			json = json.replaceAll("[\\t\\n\\r]", "");
		}
		JSONObject jsonObject = JSONObject.fromObject(json);
		JSONObject oneway_data = jsonObject.getJSONObject("oneway_data");
//		JSONObject recommendInfo = oneway_data.getJSONObject("recommendInfo");
//		JSONObject planeInfo = oneway_data.getJSONObject("planeInfo");
		JSONObject priceInfo = oneway_data.getJSONObject("priceInfo");
		for (Iterator iter = priceInfo.keys(); iter.hasNext();) { //先遍历整个 people 对象  
		    String key = (String)iter.next();
		    if(key.indexOf('/')>=0){
		    	continue;
		    }else {
		    	try {
		    		//{"cabin":"H","op":690,"tc":"经济舱","slowpr":100000,"bfhipr":0,"hipr":0,"wrlen":78,"shipr":0,"bfwrlen":0,"swrlen":0,"bflowpr":100000,"lowpr":407}
					String cabinInfoString = priceInfo.getString(key);
					JSONObject cabinJsonObject = JSONObject
							.fromObject(cabinInfoString);
					CarbinInfo lowCarbin = new CarbinInfo();
					lowCarbin.setCabintypename(cabinJsonObject.getString("tc"));
					lowCarbin.setCabin(cabinJsonObject.getString("cabin"));
					lowCarbin.setPrice(parseIntToFloat(Float.parseFloat(cabinJsonObject
							.getString("lowpr"))));
					lowCarbin.setFuelprice(Float.parseFloat(cabinJsonObject
							.getString("op")));
					lowCarbin.setDiscount(formatfloatMoney(Float.parseFloat(cabinJsonObject
							.getString("lowpr"))*10/Float.parseFloat(cabinJsonObject
									.getString("op"))));
					specilCarbinMap.put(key, lowCarbin);
				} catch (Exception e) {
					e.printStackTrace();
				}
		    }
		}
		return specilCarbinMap;
	}
	
	private static float parseIntToFloat(float price){
		int tempi = (int) (price/10+1)*10;
		return (float)(tempi);
	}
	
	

	/**
	 * 
	 * @return
	 */
	public static float formatfloatMoney(Number discount) {
		DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();
		format.applyPattern("###0.0");
		try {
			String result = format.format(discount);
			return Float.valueOf(result);
		} catch (Exception e) {
			if (discount != null) {
				return Float.valueOf(discount.toString());
			} else {
				return 0;
			}
		}
	}
}
