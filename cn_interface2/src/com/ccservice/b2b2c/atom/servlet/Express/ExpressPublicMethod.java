package com.ccservice.b2b2c.atom.servlet.Express;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.alibaba.fastjson.JSONObject;
import com.callback.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.elong.inter.PropertyUtil;

public class ExpressPublicMethod {

	/**
	 * 获取出票点ID
	 * 
	 * @param agengId
	 * @param address
	 * @return
	 * @throws ParseException
	 */
	public static String getDelieveStr(String address, String sendprovince,
			String sendcity, int random) throws ParseException {
		String results = "";
		String fromcode = "010";
		String tocode = getExpressCodes(address, random);
		String time1 = "10:00:00";
		String time2 = "18:00:00";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar now=Calendar.getInstance();
		now.add(Calendar.MINUTE,30);
		String realTime = sdf.format(now.getTimeInMillis());
		fromcode = getExpressCodes(sendprovince + sendcity, random);
		String urlString = PropertyUtil.getValue("expressDeliverUrl",
				"Train.properties");
		String param = "times=" + realTime + "&fromcode=" + fromcode
				+ "&tocode=" + tocode;
		WriteLog.write("线下快递时效接口", random + "--(SF)--sendaddress="
				+ sendprovince + sendcity + "------->" + "address=" + address
				+ "---------->" + urlString + "?" + param);
		String result = SendPostandGet.submitPost(urlString, param, "UTF-8")
				.toString();
		WriteLog.write("线下快递时效接口", random + "--(SF)--fromcode=" + fromcode
				+ "------->" + "address=" + address + "---------->result:"
				+ result);
		try {
			Document document = DocumentHelper.parseText(result);
			Element root = document.getRootElement();
			Element body = root.element("Body");
			if ("OK".equals(root.elementText("Head"))
					&& result.contains("deliver_time")) {
				Element deliverTmResponse = body.element("DeliverTmResponse");
				Element deliverTm = deliverTmResponse.element("DeliverTm");
				String deliver_time = deliverTm.attributeValue("deliver_time");
				results = realTime + "前完成支付," + deliver_time + "前送到！";
			} else if ("OK".equals(root.elementText("Head"))
					&& !result.contains("deliver_time")) {
				if (Integer.parseInt(realTime.substring(11, 12)) > 12) {
					results = realTime + "前完成支付,"
							+ getNextDay(realTime.substring(0, 10), -2)
							+ " 18:00:00前送到！";
				} else {
					results = realTime + "前完成支付,"
							+ getNextDay(realTime.substring(0, 10), -2)
							+ " 12:00:00前送到！";
				}
			} else {
				results = "获取快递时间失败！请上官网核验快递送达时间。";
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		WriteLog.write("线下快递时效接口", random + "--(SF)--results=" + results);
		return results;
	}

	/**
	 * 获取某一天
	 * 
	 * @param dateString
	 * @param i
	 * @return
	 * @throws ParseException
	 */
	public static String getNextDay(String dateString, int i)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = sdf.parse(dateString);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, -i);
		date = calendar.getTime();
		return sdf.format(date);
	}

	/**
	 * 通过存储过程获取乘客地址的citycode
	 * 
	 * @param address
	 * @return
	 */
	public static String getExpressCodes(String address, int random) {
		String procedure = "sp_TrainOfflineExpress_getCode @address='"
				+ address + "'";
		List list = Server.getInstance().getSystemService()
				.findMapResultByProcedure(procedure);
		String cityCode = "010";
		if (list.size() > 0) {
			Map map = (Map) list.get(0);
			cityCode = map.get("CityCode").toString();
		}
		WriteLog.write("线下快递时效接口", random + "--(SF)--cityCode:" + cityCode
				+ "--address:" + address);
		return cityCode;
	}

	public static String getJDDelieveStr(String sendProvince, String sendCity,
			String address, int random) {
		String result = "";
		String sendCode = getProvinceCode(sendProvince, sendCity, random);
		if (sendCode != null && !"".equals(sendCode)) {
			String senderprovinceid = sendCode.split("_")[0];
			String sendercityid = sendCode.split("_")[1];
			result = getJDResultStr(address, senderprovinceid, sendercityid,
					random);
		}
		return result;
	}

	/**
	 * 获取JD省市code
	 * 
	 * @param province
	 * @param city
	 * @return
	 */
	public static String getProvinceCode(String province, String city,
			int random) {
		String provinces = province.replace("省", "").replace("市", "");
		String privinceCode = "";
		String cityCode = "";
		String parnetid = "";
		String sendCode = "";
		String psql = "SELECT areaid FROM JDprovince WITH (NOLOCK) WHERE areaname = '"
				+ provinces + "'";
		List plist = Server.getInstance().getSystemService()
				.findMapResultBySql(psql, null);
		if (plist.size() > 0) {
			Map map = (Map) plist.get(0);
			privinceCode = map.get("areaid").toString();
		}

		String csql = "SELECT areaid,parnetid FROM JDprovince WITH (NOLOCK) WHERE areaname = '"
				+ city + "'";
		List clist = Server.getInstance().getSystemService()
				.findMapResultBySql(csql, null);
		if (clist.size() > 0) {
			Map map = (Map) clist.get(0);
			cityCode = map.get("areaid").toString();
			parnetid = map.get("parnetid").toString();
		}
		if (!"".equals(privinceCode) && privinceCode.equals(parnetid)) {
			sendCode = privinceCode + "_" + cityCode;
		}
		WriteLog.write("线下快递时效接口", random + "--(JD)--province:" + province
				+ "--->city:" + city + "--->sendCode:" + sendCode);
		return sendCode;
	}

	/**
	 * 获取京东返回结果
	 * 
	 * @param address
	 * @param orderid
	 * @param senderprovinceid
	 * @param sendercityid
	 * @return
	 */
	public static String getJDResultStr(String address,
			String senderprovinceid, String sendercityid, int random) {
		if (address != null && !"".equals(address) && senderprovinceid != null
				&& !"".equals(senderprovinceid) && sendercityid != null
				&& !"".equals(sendercityid)) {
			try {
				String result = "";
				String orderid = "";
				String sql = "select top 1 * from JDEXPRESSNUM where status=0 order by createtime asc";
				List list = Server.getInstance().getSystemService()
						.findMapResultBySql(sql, null);
				if (list.size() > 0) {
					Map map = (Map) list.get(0);
					orderid = map.get("EXPRESSNUM").toString();
					WriteLog.write("线下快递时效接口", random + "--(JD)--EXPRESSNUM:"
							+ orderid);
				} else {
					result = "Jd库存运单号已用完";
					return result;
				}
				address = java.net.URLEncoder.encode(address, "utf-8");
				String param = "address=" + address + "&orderid=" + orderid
						+ "&senderprovinceid=" + senderprovinceid
						+ "&sendercityid=" + sendercityid;
				// String url = "http://120.26.100.206:12345/JD/jdisacept";
				String url = PropertyUtil.getValue("jdshixiao",
						"Express.properties");
				String resultjson = SendPostandGet.submitPost(url, param,
						"UTF-8").toString();
				WriteLog.write("线下快递时效接口", random + "--(JD)--resultjson:"
						+ resultjson + "--url:" + url + "--param:" + param);
				if (resultjson != null && !"".equals(resultjson)) {
					JSONObject json = JSONObject.parseObject(resultjson);
					if (json != null && !json.isEmpty()) {
						String shixiao = json
								.getJSONObject(
										"jingdong_etms_range_check_responce")
								.getJSONObject("resultInfo").getString("rcode");
						if (shixiao != null && !"".equals(shixiao)) {
							if ("100".equals(shixiao)) {
								String agingName = json
										.getJSONObject(
												"jingdong_etms_range_check_responce")
										.getJSONObject("resultInfo")
										.getString("agingName");
								String rmessage = json
										.getJSONObject(
												"jingdong_etms_range_check_responce")
										.getJSONObject("resultInfo")
										.getString("rmessage");
								result = rmessage + "," + agingName;
							} else if ("150".equals(shixiao)) {
								result = "进入人工预分拣，请稍后！";
							} else {
								result = "不能京配";
							}
							return result;
						}
					}
				}
			} catch (Exception e) {
			}
		}
		return "";
	}

}
