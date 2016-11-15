package com.ccservice.b2b2c.atom.service12306;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.tenpay.util.MD5Util;

/**
 * 
 * @author ffh
 *
 * @date2016年4月13日
 */
public class TrainSearch {

	/**
	 * 查询余票最低坐席
	 * 
	 * @param fromDate
	 * @param fromStation
	 * @param toStation
	 * @param trainNum
	 * @param ticketPrice
	 * @return {"success":true;"zwtype":"硬座"}
	 */
	@SuppressWarnings("static-access")
	public JSONObject querySurplusTrainticket(String fromDate,
			String fromStation, String toStation, String trainNum,
			float noSeatTicketPrice) {
		WriteLog.write("开始__querySurplusTrainticket()__查询最低坐席", "fromDate:"
				+ fromDate + ";fromStation:" + fromStation + ";toStation:"
				+ toStation + ";trainNum:" + trainNum + ";ticketPrice:"
				+ noSeatTicketPrice);
		JSONObject jsonObject = new JSONObject();
		TrainSearch search = new TrainSearch();
		if (fromDate != null && !"".equals(fromDate) && fromStation != null
				&& !"".equals(fromStation) && toStation != null
				&& !"".equals(toStation) && trainNum != null
				&& !"".equals(trainNum)) {

			Train12306StationInfoUtil infoUtil = new Train12306StationInfoUtil();
			String fromStationThreeCode = infoUtil.getSZMByName(fromStation);
			String toStationThreeCode = infoUtil.getSZMByName(toStation);

			try {
				jsonObject = search.queryLowestSeattype(fromDate,
						fromStationThreeCode, toStationThreeCode, trainNum,
						noSeatTicketPrice);
			} catch (Exception e) {
				WriteLog.write("ERROR___查询最低坐席",
						"querySurplusTrainticket --> fromDate:" + fromDate
								+ ";fromStation:" + fromStation + ";toStation:"
								+ toStation + ";trainNum:" + trainNum
								+ ";ticketPrice:" + noSeatTicketPrice);
			}
		}
		return jsonObject;
	}

	/**
	 * 查询最低坐席
	 * 
	 * @param fromDate
	 * @param fromStation
	 * @param toStation
	 * @param trainNum
	 * @param noSeatTicketPrice
	 * @return 最低坐席json {"success",boolean;"zwtype",String}
	 */
	private JSONObject queryLowestSeattype(String fromDate, String fromStation,
			String toStation, String trainNum, float noSeatTicketPrice) {
		WriteLog.write("开始___查询最低坐席", "queryLowestSeattype()--> fromDate:"
				+ fromDate + ";fromStation:" + fromStation + ";toStation:"
				+ toStation + ";trainNum:" + trainNum + ";noSeatTicketPrice:"
				+ noSeatTicketPrice);
		JSONObject lowestTrainTicketSeattypeJson = new JSONObject();

		if (fromDate != null && fromStation != null && toStation != null
				&& trainNum != null) {

			String allSurplusTrainTicket = queryTrainTicket(fromDate,
					fromStation, toStation, trainNum, noSeatTicketPrice);
			JSONObject trainoJson = getTrainTicketJson(allSurplusTrainTicket,
					trainNum);

			boolean flag = trainoJson.getBoolean("success");

			String zwtype = getSurplusTrainTicket(trainoJson);

			lowestTrainTicketSeattypeJson.put("success", flag);
			lowestTrainTicketSeattypeJson.put("zwtype", zwtype);

		} else {
			WriteLog.write("error__查询最低坐席",
					"queryLowestSeattype()--> fromDate:" + fromDate
							+ ";fromStation:" + fromStation + ";toStation:"
							+ toStation + ";trainNum:" + trainNum
							+ ";noSeatTicketPrice:" + noSeatTicketPrice);
		}
		return lowestTrainTicketSeattypeJson;
	}

	/**
	 * 查询余票
	 * 
	 * @param fromDate
	 * @param fromStation
	 * @param toStation
	 * @param trainNum
	 * @param noSeatTicketPrice
	 * @return String
	 * @author ffh
	 */
	private String queryTrainTicket(String fromDate, String fromStation,
			String toStation, String trainNum, float noSeatTicketPrice) {
		WriteLog.write("start__queryTrainTicket()__Class(TrainSearch)",
				"queryLowestSeattype()--> fromDate:" + fromDate
						+ ";fromStation:" + fromStation + ";toStation:"
						+ toStation + ";trainNum:" + trainNum
						+ ";noSeatTicketPrice:" + noSeatTicketPrice);

		String method = "train_query_remain";
		String partnerid = "meituan";
		String key = "92l2w9s745is8djyh0hbpyfg8v812pvw";
		String url = "http://searchtrain.hangtian123.net/trainSearch";// 正式查询地址
		String allTrainTicket = null;

		try {
			allTrainTicket = visitQuerySurplusTrainTicketInterface(fromDate,
					fromStation, toStation, partnerid, key, url, method);
		} catch (Exception e) {
			WriteLog.write("error__queryTrainTicket()__TrainSearch",
					"fromDate:" + fromDate + ";fromStation:" + fromStation
							+ ";toStation:" + toStation + ";partnerid:"
							+ partnerid + ";key:" + key + ";url:" + url
							+ ";method:" + method);
		}

		return allTrainTicket;

	}

	/**
	 * 解析查询到的所有余票的String(json),得到确定车次的json
	 * 
	 * @param fromDate
	 * @param fromStation
	 * @param toStation
	 * @return
	 * @author ffh
	 */
	private JSONObject getTrainTicketJson(String allTrainTicketJson,
			String trainNum) {
		WriteLog.write("start__getTrainTicketJson()__Class(TrainSearch)",
				"getTrainTicketJson--> allTrainTicketJson:"
						+ allTrainTicketJson + ";trainNum:" + trainNum);
		if (allTrainTicketJson != null && !"".equals(allTrainTicketJson)) {
			// -1？
			if ("-1".equals(allTrainTicketJson)) {
				WriteLog.write("trainticket=-1 ", "trainTicket="
						+ allTrainTicketJson);
			}

			JSONObject TrainTicketJson = new JSONObject();

			try {
				TrainTicketJson = JSONObject.parseObject(allTrainTicketJson);
			} catch (Exception e) {
				WriteLog.write("error_解析json失败!", "jsonObject:"
						+ allTrainTicketJson);
			}

			if (TrainTicketJson != null && TrainTicketJson.containsKey("data")) {
				JSONArray allTrainTicketJsonArray = TrainTicketJson
						.getJSONArray("data");
				if (allTrainTicketJsonArray != null) {
					for (int i = 0; i < allTrainTicketJsonArray.size(); i++) {
						JSONObject trainTicketJson = allTrainTicketJsonArray
								.getJSONObject(i);
						String trainCode = trainTicketJson
								.getString("train_code"); // 车次
						if (trainCode.equals(trainNum)) {
							trainTicketJson.put("success", true);
							return trainTicketJson;
						}
					}
				}
			}
		}
		JSONObject trainTicketJson = new JSONObject();
		trainTicketJson.put("success", false);
		return trainTicketJson;
	}

	/**
	 * 解析具体余票,得到最低坐席
	 * 
	 * @param trainJson
	 * @return String
	 * @author ffh
	 */
	private String getSurplusTrainTicket(JSONObject trainJson) {
		WriteLog.write("start__getSurplusTrainTicket()_Class(TrainSearch)",
				"trainJson" + trainJson);
		String zwtype = null;
		String train_type = null;

		if (trainJson != null) {
			train_type = trainJson.getString("train_type");
			String gjrw_num = trainJson.getString("gjrw_num");
			String tdz_num = trainJson.getString("tdz_num");
			String ydz_num = trainJson.getString("ydz_num");
			String rw_num = trainJson.getString("rw_num");
			String yw_num = trainJson.getString("yw_num");
			String qtxb_num = trainJson.getString("qtxb_num");
			String edz_num = trainJson.getString("edz_num");
			String wz_num = trainJson.getString("wz_num");
			String rz_num = trainJson.getString("rz_num");
			String yz_num = trainJson.getString("yz_num");
			String swz_num = trainJson.getString("swz_num");

			if (!"--".equals(qtxb_num)) {
				zwtype = "其他席别";
			} else if (!"--".equals(wz_num)) {
				zwtype = "无座";
			} else if (!"--".equals(yz_num)) {
				zwtype = "硬座";
			} else if (!"--".equals(rz_num)) {
				zwtype = "软座";
			} else if (!"--".equals(yw_num)) {
				zwtype = "硬卧";
			} else if (!"--".equals(rw_num)) {
				zwtype = "软卧";
			} else if (!"--".equals(gjrw_num)) {
				zwtype = "高级软卧";
			} else if (!"--".equals(edz_num)) {
				zwtype = "二等座";
			} else if (!"--".equals(ydz_num)) {
				zwtype = "一等座";
			} else if (!"--".equals(tdz_num)) {
				zwtype = "特等座";
			} else if (!"--".equals(swz_num)) {
				zwtype = "商务座";
			} else {
				zwtype = getSeat(train_type);
			}

		} else {
			WriteLog.write("fail__getSurplusTrainTicket()_Class(TrainSearch)",
					"trainJson" + trainJson);
		}

		return zwtype;
	}

	/**
	 * 如果没有余票，按旧的无座转换
	 * 
	 * @param traino
	 * @param zwtype
	 * @return seatType
	 * @author ffh
	 */
	private String getSeat(String traino) {
		String i_st = "";
		if (traino.startsWith("D")) {
			i_st = "二等座";
		} else if (traino.startsWith("C")) {
			i_st = "软座";
		} else {
			i_st = "硬座";
		}
		return i_st;
	}

	/**
	 * 访问接口(查询余票)
	 * 
	 * @time 2015年4月30日 下午2:30:51
	 * @author chendong
	 * @throws Exception
	 */
	private static String visitQuerySurplusTrainTicketInterface(
			String train_date, String from_station, String to_station,
			String partnerid, String key, String url, String method)
			throws Exception {
		WriteLog.write("start__visit__TrainSearch()",
				"visitInterface--> train_date:" + train_date + ";from_station:"
						+ from_station + ";to_station:" + to_station
						+ ";partnerid:" + partnerid + ";key:" + key + ";url:"
						+ url + ";method:" + method);
		String reqtime = getreqtime();
		String sign = getsign(partnerid, method, reqtime, key);
		JSONObject json1 = new JSONObject();
		json1.put("partnerid", partnerid);
		json1.put("method", method);
		json1.put("reqtime", reqtime);
		json1.put("sign", sign);
		json1.put("train_date", train_date);
		json1.put("from_station",
				Train12306StationInfoUtil.getThreeByName(from_station));
		json1.put("to_station",
				Train12306StationInfoUtil.getThreeByName(to_station));
		json1.put("purpose_codes", "ADULT");
		// json1.put("ischeck", "no");
		String paramContent = "jsonStr=" + json1.toJSONString();
		System.out
				.println("===========================================================url:"
						+ url);
		System.out.println(paramContent);
		String resultString = null;
		try {
			resultString = SendPostandGet
					.submitPost(url, paramContent, "utf-8").toString();
		} catch (Exception e) {
			WriteLog.write("ERROR_调用失败", "url" + url + ";paramContent:"
					+ paramContent);
		}
		return resultString;
	}

	public static String getreqtime() {
		SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat(
				"yyyyMMddHHmmssSSS");
		return yyyyMMddHHmmssSSS.format(new Date());
	}

	/**
	 * md5(partnerid+method+reqtime+md5(key))，
	 * 
	 * @time 2014年12月12日 下午2:44:31
	 * @author chendong
	 */
	public static String getsign(String partnerid, String method,
			String reqtime, String key) {
		// String keyString = partnerid + method + reqtime +
		// MD5Util.MD5Encode(key, "UTF-8");
		// keyString = MD5Util.MD5Encode(keyString, "UTF-8");

		System.out.print("=========key加密前=========");
		System.out.print(key);
		System.out.print("=========key加密后=========");
		key = MD5Util.MD5Encode(key, "UTF-8");
		System.out.print(key);
		String jiamiqian = partnerid + method + reqtime + key;
		System.out.print("=========sign加密前=========");
		System.out.println(jiamiqian);
		String sign = MD5Util.MD5Encode(jiamiqian, "UTF-8");
		System.out.print("=========sign加密后=========");
		System.out.println(sign);

		return sign;
	}

}
