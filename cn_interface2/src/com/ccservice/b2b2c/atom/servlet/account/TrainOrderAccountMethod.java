package com.ccservice.b2b2c.atom.servlet.account;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
 

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.servlet.account.util.TuNiuDesUtil;
import com.ccservice.b2b2c.atom.servlet.tuniu.SignUtil;
import com.ccservice.b2b2c.atom.train.idmongo.MongoLogic;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.ccservice.elong.inter.PropertyUtil;
import com.mongodb.DBObject;
import com.tenpay.util.MD5Util;

public class TrainOrderAccountMethod {
	private static TrainOrderAccountMethod trainOrderAccountMethod = null;

	private static final Object syn = new Object();

	private static int counti = 0;

	public static void getInstance() {
		if (trainOrderAccountMethod == null) {
			synchronized (syn) {
				if (trainOrderAccountMethod == null) {
					trainOrderAccountMethod = new TrainOrderAccountMethod();
				}
			}
		}
	}

	public TrainOrderAccountMethod() {
		// operationMeiTuan();
		// operationQunar();
		operationTuniu();
	}

	public static void main(String[] args) {
		//new TrainOrderAccountMethod().operationTongChengAndLvTu();
		new TrainOrderAccountMethod().operationTuniu(); 
	}
 
	private void operationQunar(){
		int minid = 0;
		while (true) {
			System.out.println(minid);
			WriteLog.write("Qunar接口最后的一个minid", minid + "");
			JSONArray jsonArray = getAccountByDb(minid);
			for (int i = 0; i < jsonArray.size(); i++) {
				if (jsonArray.getJSONObject(i).containsKey("accountId")
						&& jsonArray.getJSONObject(i).getIntValue("accountId") > minid) {
					minid = jsonArray.getJSONObject(i).getIntValue("accountId");
					WriteLog.write("Qunar接口最后的一个minid", minid + "");
				}
				int logId = new Random().nextInt();
				// TODO 调去哪
				getQunar(jsonArray.getJSONObject(i), logId);
			}
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
			}
		}
	}
	
	

	/**
	 * 调用美团同步接口
	 */
	public void operationMeiTuan() {
		while (true) {
			counti = 0;
			int minid = 0;
			while (true) {
				System.out.println("最小的ID" + minid);
				WriteLog.write("zzz接口最后的一个minid", minid + "");
				JSONArray jsonArray = getAccountByDb(minid);
				for (int i = 0; i < jsonArray.size(); i++) {
					if (jsonArray.getJSONObject(i).containsKey("accountId")
							&& jsonArray.getJSONObject(i).getIntValue(
									"accountId") > minid) {
						minid = jsonArray.getJSONObject(i).getIntValue(
								"accountId");
						WriteLog.write("sss接口最后的一个minid", minid + "");
					}
					int logId = new Random().nextInt();
					// TODO 调美团接口
					getMeituan(jsonArray.getJSONObject(i), logId, minid);
				}
				try {
					Thread.sleep(100L);
				} catch (InterruptedException e) {
				}
				WriteLog.write("美团回调个数", "总数--->" + counti);
			}

		}
	}

	/**
	 * 调用同程和旅途接口
	 */
	public void operationTongChengAndLvTu() {
		int minid = 0;
		while (true) {
			System.out.println(minid);
			WriteLog.write("zzz接口最后的一个minid", minid + "");
			JSONArray jsonArray = getAccountByDb(minid);
			for (int i = 0; i < jsonArray.size(); i++) {
				if (jsonArray.getJSONObject(i).containsKey("accountId")
						&& jsonArray.getJSONObject(i).getIntValue("accountId") > minid) {
					minid = jsonArray.getJSONObject(i).getIntValue("accountId");
					WriteLog.write("sss接口最后的一个minid", minid + "");
				}
				int logId = new Random().nextInt();
				// TODO 调同城接口
				getTongChenResult(jsonArray.getJSONObject(i), logId, minid);
				// TODD 调旅途100接口
				// getLvTu(jsonArray.getJSONObject(i),logId, minid);

			}
			try {
				Thread.sleep(100L);
			} catch (InterruptedException e) {
			}
		}
	}

	
	
	/**
	 * 链接 美团接口
	 * 
	 * @param Currentaccount
	 */
	private void getMeituan(JSONObject Currentaccount, int id, int minid) {
		WriteLog.write("美团的最后的一个minid", minid + "");
		List<DBObject> list = getAccountByMongo(Currentaccount, id);
		if (list == null || list.isEmpty()) {
			return;
		}
		WriteLog.write("美团接口", "id" + id + "获取list返回的数据：" + list);
		JSONArray passengers = new JSONArray();
		JSONObject passenger = new JSONObject();
		for (DBObject dbObject : list) {
			passenger = getMeituanJson(dbObject,
					Currentaccount.getString("accountId"), id);
			passengers.add(passenger);
		}
		for (int i = 0; i < passengers.size(); i++) {
			JSONObject passenger2 = passengers.getJSONObject(i);
			send2MeiTuan(minid, passenger2);
		}
	}

	private void getLvTu(JSONObject Currentaccount, int id, int minid) {
		WriteLog.write("旅途的最后的一个minid", minid + "");
		List<DBObject> list = getAccountByMongo(Currentaccount, id);
		if (list == null || list.isEmpty()) {
			return;
		}
		WriteLog.write("旅途接口", "id" + id + "获取list返回的数据：" + list);
		JSONArray passengers = new JSONArray();
		JSONObject passenger = new JSONObject();
		for (DBObject dbObject : list) {
			passenger = getMeituanJson(dbObject,
					Currentaccount.getString("accountId"), id);
			passengers.add(passenger);
		}
		for (int i = 0; i < passengers.size(); i++) {
			JSONObject passenger2 = passengers.getJSONObject(i);
			// 旅途100
			send2LvTu100(minid, passenger2);

		}
	}
	
	
	

	// 美团
	private void send2MeiTuan(int id, JSONObject passenger2) {
		String result = new String();
		try {
			WriteLog.write("美团接口", "id" + id + "passenger参数" + passenger2);
			// 返回标识
			result = submitPost(
					"http://i.meituan.com/uts/train/agentpassenger/updaterealtime/104/HANGTIANHUAYOU",
					passenger2.toString(), "UTF-8").toString();
			if (result.contains("ok")) {
				counti++;
			}
			System.out.println("美团成功回调------->" + counti + "个。");
			WriteLog.write("美团回调个数", "美团回调个数--->" + counti);
			WriteLog.write("美团接口", "id" + id + "最终返回的数据" + result);
		} catch (Exception e) {
			WriteLog.write("美团接口_ERROR", "id" + id);
			ExceptionUtil.writelogByException("美团接口_ERROR", e);
		}
	}

	// 旅途100
	private void send2LvTu100(int id, JSONObject passenger2) {
		String result = new String();
		try {
			String url = PropertyUtil.getValue("Lvtu100Account.url",
					"Train.properties");
			WriteLog.write("旅途100", "id" + id + "passenger参数" + passenger2);
			result = submitPost(url, passenger2.toString(), "UTF-8").toString();
			WriteLog.write("旅途100", "id" + id + "最终返回的数据" + result);
		} catch (Exception e) {
			WriteLog.write("旅途100_ERROR", "id" + id);
			ExceptionUtil.writelogByException("旅途100_ERROR", e);
		}
	}

	/**
	 * 转换身份证号
	 * 
	 * @param id
	 * @return
	 */
	public static String getPassengertypeid(long id) {
		String tyId = String.valueOf(id);
		if (tyId.length() == 19) {
			tyId = tyId.substring(0, tyId.length() - 2) + "X";
		}
		return tyId;

	}

	
	
	/**
	 * 获取美团参数
	 * 
	 * @param dbObject
	 * @param string
	 * @return
	 */
	private JSONObject getMeituanJson(DBObject dbObject, String string, int id) {
		JSONObject jsonObject = new JSONObject();
		JSONObject dbjson = new JSONObject();
		try {
			dbjson = JSONObject.parseObject(dbObject.toString());
		} catch (Exception e) {
			WriteLog.write("美团接口_ERROR", "id" + id);
			ExceptionUtil.writelogByException("美团接口_ERROR_dbObject异常", e);
		}
		long passportseno = dbjson.getLongValue("IDNumber");
		String passengersename = dbjson.getString("RealName");
		String passengertypeid = dbjson.getString("IDType");
		jsonObject.put("operationtypename", "%E6%96%B0%E5%A2%9E");// 新增%E6%96%B0%E5%A2%9E
		jsonObject.put("operationtime", getRepTime());
		jsonObject.put("passporttypeseidname",
				"%E4%BA%8C%E4%BB%A3%E8%BA%AB%E4%BB%BD%E8%AF%81");// 二代身份证%E4%BA%8C%E4%BB%A3%E8%BA%AB%E4%BB%BD%E8%AF%81
		jsonObject.put("passengertypename",
				getPassengerTypeName(Integer.parseInt(passengertypeid)));// 成人票
		jsonObject.put("passportseno", getPassengertypeid(passportseno));// 身份证号
		try {
			jsonObject.put("passengersename",
					URLEncoder.encode(passengersename, "UTF-8")); // 用户名
		} catch (UnsupportedEncodingException e) {
			WriteLog.write("美团接口用户名encode_ERROR", "id" + id);
			ExceptionUtil.writelogByException("美团接口用户名encode_ERROR", e);
		}
		jsonObject.put("passengertypeid", passengertypeid);
		jsonObject.put("operationtypeid", "0");
		jsonObject.put("passporttypeseid", "1");
		WriteLog.write("美团接口", "id" + id + "返回的json数据：" + jsonObject);
		return jsonObject;
	}

	/**
	 * 链接同程接口
	 * 
	 * @param Currentaccount
	 *            当前账号
	 * 
	 */
	private void getTongChenResult(JSONObject Currentaccount, int id, int minid) {
		WriteLog.write("同程的最后的一个minid", minid + "");
		// 获取乘客
		List<DBObject> list = getAccountByMongo(Currentaccount, id);
		WriteLog.write("同程接口", "id" + id + "获取list返回的数据：" + list);
		if (list == null || list.isEmpty()) {
			return;
		}
		JSONArray passengers = new JSONArray();
		JSONArray passenger = new JSONArray();
		for (DBObject dbObject : list) {
			passenger = getBackJson(dbObject,
					Currentaccount.getString("accountId"), getRepTime(), id);
			passengers.add(passenger);
		}
		for (int j = 0; j < passengers.size(); j++) {
			JSONArray account = new JSONArray();
			try {
				account = passengers.getJSONArray(j);
			} catch (Exception e) {
				WriteLog.write("同程接口encode_ERROR", "id" + id);
				ExceptionUtil.writelogByException("同程接口passengers_ERROR", e);
			}
			String result = new String();
			if (!account.isEmpty()
					&& !account.getJSONObject(0).getJSONArray("passengers")
							.isEmpty()) {
				result = getTongchengJsonbyObj("tongcheng_train",
						"x3z5nj8mnvl14nirtwlvhvuialo0akyt", account);
				String parameters = "backjson=" + result;
				WriteLog.write("同程接口", "id" + id + "parameters参数" + parameters);
				String get = new String();
				String callbackurl = "http://train.17usoft.com/trainOrder/services/accountInfoChange";
				try {
					WriteLog.write("同程接口", id + ":parameters:" + parameters
							+ ":callbackurl:" + callbackurl);
					get = SendPostandGet.submitPost(callbackurl, parameters,
							"utf-8").toString();
					System.out.println("res-->" + get);
					if (get.equals("SUCCESS")) {
						counti++;
					}
					System.out.println("同程接口---->" + counti + "个。");
					WriteLog.write("同程接口", id + ":get:" + get);
				} catch (Exception e) {
					WriteLog.write("同程接口encode_ERROR", "id" + id);
					ExceptionUtil.writelogByException("同程接口_最终返回_ERROR", e);
				}
				WriteLog.write("同程接口", "id" + id + "最终返回结果数据:" + get);
				WriteLog.write("同程结果数据", getRepTime() + "" + get);
			}
		}

	}

	/**
	 * 获取ID
	 * 
	 * @param minid
	 *            当前同步的最后一个ID
	 * @return
	 * @time 2016年3月29日 下午2:12:21
	 * @author fiend
	 */
	private JSONArray getAccountByDb(int minid) {
		JSONArray jsonArray = new JSONArray();
		DataTable dataTable = DBHelperAccount.GetDataTable(
				"[T_CUSTOMERUSER_select] @minId=" + minid, null);
		if (dataTable != null) {
			for (DataRow dataRow : dataTable.GetRow()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("accountId", dataRow.GetColumnInt("ID"));
				jsonObject.put("accountName",
						dataRow.GetColumnString("C_LOGINNAME"));
				jsonArray.add(jsonObject);
			}
		}
		WriteLog.write("同步的数据-同程", minid + "--->" + jsonArray.toString());
		return jsonArray;
	}

	/**
	 * 这里通过DB的账号，获取mongo中的所有乘客
	 * 
	 * @param jsonObjectAccount
	 * @return JSONObject
	 *         backjson={"sign":"0373c96c61f694f81b8607db084802bb","partnerid"
	 *         :"tongcheng_train"
	 *         ,"accounts":[{"accountstatusid":"2","accountstatusname"
	 *         :"%E5%8F%AF%E7%94%A8"
	 *         ,"passengers":[{"operationtypename":"%E5%88%A0%E9%99%A4"
	 *         ,"operationtime":"20160322160017","passengersename":
	 *         "%E6%9C%B1%E5%85%86%E5%A5%87"
	 *         ,"identitystatusid":"315","identitystatusmsg":
	 *         "%E8%BA%AB%E4%BB%BD%E4%BF%A1%E6%81%AF%E6%B6%89%E5%AB%8C%E8%A2%AB%E4%BB%96%E4%BA%BA%E5%86%92%E7%94%A8"
	 *         ,"passporttypeseidname":
	 *         "%E4%BA%8C%E4%BB%A3%E8%BA%AB%E4%BB%BD%E8%AF%81"
	 *         ,"passportseno":"21900719741110161x"
	 *         ,"passengertypename":"%E6%88%90%E4%BA%BA%E7%A5%A8"
	 *         ,"passporttypeseid"
	 *         :"1","operationtypeid":"2","passengertypeid":"1"
	 *         }],"accountname":"16211604"
	 *         }],"reqtime":"20160322160017"}-->http:/
	 *         /train.17usoft.com/trainOrder/services/accountInfoChange
	 * @time 2016年3月29日 下午2:15:37
	 * @author fiend
	 */
	private List<DBObject> getAccountByMongo(JSONObject jsonObjectAccount,
			int id) {

		try {
			List<DBObject> list = new MongoLogic()
					.FindMongoByCustomerUser(DesUtil.decrypt(
							jsonObjectAccount.getString("accountName"),
							"A1B2C3D4E5F60708"));
			WriteLog.write("接口-同程", "id" + id + "获取list返回的数据：" + list);

			return list;
		} catch (Exception e) {
			WriteLog.write("接口encode_ERROR", "id" + id);
			ExceptionUtil.writelogByException("接口_getAccountByMongo_ERROR", e);
		}
		return null;
	}

	/**
	 * 获取同程accounts的信息
	 * 
	 * @param list
	 * @param operationtime
	 * @return
	 */
	private JSONArray getBackJson(DBObject dbObject, String id,
			String operationtime, int ID) {
		JSONArray account = new JSONArray();
		JSONObject json = new JSONObject();
		JSONArray passengers = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		JSONObject dbjson = new JSONObject();
		try {
			dbjson = JSONObject.parseObject(dbObject.toString());
		} catch (Exception e) {
			WriteLog.write("同程接口encode_ERROR", "id" + id);
			ExceptionUtil.writelogByException("同程接口_dbObject_ERROR", e);
		}
		long passportseno = dbjson.getLongValue("IDNumber");
		String passengersename = dbjson.getString("RealName");
		String passengertypeid = dbjson.getString("IDType");
		jsonObject.put("operationtypename", "%E6%96%B0%E5%A2%9E");// 新增%E6%96%B0%E5%A2%9E
		jsonObject.put("operationtime", getRepTime());
		jsonObject.put("identitystatusid", "100");
		jsonObject.put("identitystatusmsg", "%E6%AD%A3%E5%B8%B8");// 正常%E6%AD%A3%E5%B8%B8
		jsonObject.put("passporttypeseidname",
				"%E4%BA%8C%E4%BB%A3%E8%BA%AB%E4%BB%BD%E8%AF%81");// 二代身份证%E4%BA%8C%E4%BB%A3%E8%BA%AB%E4%BB%BD%E8%AF%81
		jsonObject.put("passengertypename",
				getPassengerTypeName(Integer.parseInt(passengertypeid)));// 成人票
		jsonObject.put("passportseno", getPassengertypeid(passportseno));// 身份证号
		try {
			jsonObject.put("passengersename",
					URLEncoder.encode(passengersename, "UTF-8")); // 用户名
		} catch (UnsupportedEncodingException e) {
		}
		jsonObject.put("passengertypeid", passengertypeid);
		jsonObject.put("operationtypeid", "1");
		jsonObject.put("passporttypeseid", "1");
		passengers.add(jsonObject);
		if (passengers.isEmpty()) {
			return new JSONArray();
		}
		json.put("passengers", passengers);
		json.put("accountstatusid", "2");
		json.put("accountstatusname", "%E5%8F%AF%E7%94%A8");// 可用%E5%8F%AF%E7%94%A8
		json.put("accountname", id);
		account.add(json);
		WriteLog.write("同程接口", "id" + id + "返回的account数据：" + account);
		WriteLog.write("account返回数据", "id" + id + "返回的数据" + account);
		return account;

	}

	/**
	 * 同程加密
	 * 
	 * @param partnerid
	 * @param key
	 * @param accountsjsonArray
	 * @return
	 */
	private String getTongchengJsonbyObj(String partnerid, String key,
			JSONArray accountsjsonArray) {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("partnerid", partnerid);
		String reqtime = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date()) + "";
		jsonObject.put("reqtime", reqtime);
		String sign = partnerid + reqtime + MD5Util.MD5Encode(key, "utf-8");
		sign = MD5Util.MD5Encode(sign, "utf-8");
		jsonObject.put("sign", sign);
		jsonObject.put("accounts", accountsjsonArray);
		return jsonObject.toJSONString();
	}

	private String getRepTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}

	// 1:成人票，2:儿童票，3:学生票，4:残军票
	private String getPassengerTypeName(int id) {
		String typename = "成人票";
		switch (id) {
		case 1:
			typename = "成人票";
			break;
		case 2:
			typename = "儿童票";
			break;
		case 3:
			typename = "学生票";
			break;
		case 4:
			typename = "残军票";
			break;
		}
		String result = new String();
		try {
			result = URLEncoder.encode(typename, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			WriteLog.write("接口encode_ERROR", "id" + id);
			ExceptionUtil.writelogByException("接口_getPassengerTypeName_ERROR",
					e);
		}
		return result;
	}

	/**
	 * java.net实现 HTTP POST方法提交
	 * 
	 * @param url
	 * @param paramContent
	 * @return
	 */
	public static StringBuffer submitPost(String url, String paramContent,
			String codetype) {
		StringBuffer responseMessage = null;
		java.net.URLConnection connection = null;
		java.net.URL reqUrl = null;
		OutputStream reqOut = null;
		InputStream in = null;
		BufferedReader br = null;
		try {
			String param = paramContent;
			responseMessage = new StringBuffer();
			reqUrl = new java.net.URL(url);
			connection = reqUrl.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			reqOut = connection.getOutputStream();
			reqOut.write(param.getBytes("UTF-8"));
			reqOut.flush();
			int charCount = -1;
			in = connection.getInputStream();

			br = new BufferedReader(new InputStreamReader(in, codetype));
			while ((charCount = br.read()) != -1) {
				responseMessage.append((char) charCount);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (br != null) {
					br.close();
				}
				if (reqOut != null) {
					reqOut.close();
				}
			} catch (Exception e) {
			}
		}
		return responseMessage;
	}

	/**
	 * 链接去哪同步数据
	 * 
	 * @param Currentaccount
	 */
	@SuppressWarnings("unused")
	private void getQunar(JSONObject Currentaccount, int id) {
		List<DBObject> list = getAccountByMongo(Currentaccount, id);
		JSONArray passengers = new JSONArray();
		JSONArray passengersMD5 = new JSONArray();
		passengers = getQunarJsonArray(list,
				Currentaccount.getString("accountId"));
		passengersMD5 = getQunarJsonArrayMD5(list,
				Currentaccount.getString("accountId"));
		if (passengers.getJSONObject(0).getString("passengers").length() <= 2) {
			return;
		}
		String merchantCode = "hcpzs";// 代理商id
		String key = "A4490303F7114720B6596B7568B69E51";// 商户密匙
		String HMAC = key + merchantCode + passengersMD5.toString();
		HMAC = MD5Util.MD5Encode(HMAC, "utf-8").toUpperCase();
		String parameters = "merchantCode=" + merchantCode + "&mappings="
				+ passengers.toString() + "&HMAC=" + HMAC;
		String result = new String();
		try {
			WriteLog.write("去哪参数parameters",
					"id-->" + id + parameters.toString());

			result = submitPostQunar(
					"http://api.pub.train.qunar.com/api/pub/AccountMapping.do",
					parameters, "UTF-8").toString();
			Thread.sleep(6000l);
			counti++;
			System.out.println("调用--->" + counti);
			WriteLog.write("去哪新增返回的结果parameters", "id-->" + id + result);

		} catch (Exception e) {
			WriteLog.write("去哪接口encode_ERROR", "id" + id);
			ExceptionUtil.writelogByException("去哪接口最终异常", e);
		}

	}

	public static StringBuffer submitPostQunar(String url, String paramContent,
			String codetype) {
		StringBuffer responseMessage = null;
		java.net.URLConnection connection = null;
		java.net.URL reqUrl = null;
		OutputStreamWriter reqOut = null;
		InputStream in = null;
		BufferedReader br = null;
		try {
			String param = paramContent;
			// System.out.println("url=" + url + "?" + paramContent + "\n");
			// System.out.println("===========post method start=========");
			responseMessage = new StringBuffer();
			reqUrl = new java.net.URL(url);
			connection = reqUrl.openConnection();
			connection.setDoOutput(true);
			reqOut = new OutputStreamWriter(connection.getOutputStream());
			reqOut.write(param);
			reqOut.flush();
			int charCount = -1;
			in = connection.getInputStream();

			br = new BufferedReader(new InputStreamReader(in, codetype));
			while ((charCount = br.read()) != -1) {
				responseMessage.append((char) charCount);
			}
			// System.out.println(responseMessage);
			// System.out.println("===========post method end=========");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out
					.println("url=" + url + "?" + paramContent + "\n e=" + ex);
		} finally {
			try {
				br.close();
				in.close();
				reqOut.close();
			} catch (Exception e) {
				System.out
						.println("paramContent=" + paramContent + "|err=" + e);
			}
		}
		return responseMessage;
	}

	/**
	 * 返回去哪所需的 passengers jsonArray
	 * 
	 * @param list
	 * @param id
	 * @return
	 */
	private JSONArray getQunarJsonArray(List<DBObject> list, String id) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		JSONArray passengers = new JSONArray();

		for (DBObject dbObject : list) {
			JSONObject passenger = new JSONObject();
			JSONObject dbjson = new JSONObject();
			try {
				dbjson = JSONObject.parseObject(dbObject.toString());
			} catch (Exception e) {
			}
			long cardNo = dbjson.getLongValue("IDNumber");
			String name = dbjson.getString("RealName");
			String certType = dbjson.getString("IDType");
			passenger.put("cardNo", cardNo);
			try {
				passenger.put("name", URLEncoder.encode(name, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
			passenger.put("certType", certType);
			passengers.add(passenger);

		}
		jsonObject.put("account", id);
		jsonObject.put("flag", "2");
		jsonObject.put("passengers", passengers);
		jsonArray.add(jsonObject);
		return jsonArray;
	}

	private JSONArray getQunarJsonArrayMD5(List<DBObject> list, String id) {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		JSONArray passengers = new JSONArray();
		for (DBObject dbObject : list) {
			JSONObject passenger = new JSONObject();
			JSONObject dbjson = new JSONObject();
			try {
				dbjson = JSONObject.parseObject(dbObject.toString());
			} catch (Exception e) {
			}
			long cardNo = dbjson.getLongValue("IDNumber");
			String name = dbjson.getString("RealName");
			String certType = dbjson.getString("IDType");
			passenger.put("cardNo", cardNo);
			passenger.put("name", name);
			passenger.put("certType", certType);
			passengers.add(passenger);

		}
		jsonObject.put("account", id);
		jsonObject.put("flag", "2");
		jsonObject.put("passengers", passengers);
		jsonArray.add(jsonObject);
		return jsonArray;
	}

	
	//----------------------------------------------途牛-开始---------------------------------------
		/**
		 * 调用途牛同步接口Tuniu
		 * @author zhangqifei
		 * @time 2016年8月9日 下午3:20:10
		 */
		private void operationTuniu() {
			while (true) {
				counti = 0;
				int minid = 0;
				while (true) {
					System.out.println("最小的ID" + minid);
					WriteLog.write("途牛接口最后的一个minid", minid + "");
					JSONArray jsonArray = getAccountByDb(minid);  //读取二百条
					
					JSONArray passengers = new JSONArray(); //乘客数据
					for (int i = 0; i < jsonArray.size(); i++) {
						if (jsonArray.getJSONObject(i).containsKey("accountId")
								&& jsonArray.getJSONObject(i).getIntValue(
										"accountId") > minid) {
							minid = jsonArray.getJSONObject(i).getIntValue(
									"accountId");
							WriteLog.write("途牛接口最后的一个minid", minid + "");
						}
						int logId = new Random().nextInt();
						// TODO 途牛接口
						JSONArray array = getTuniu(jsonArray.getJSONObject(i), logId, minid);
						if (array!= null) {
							for (Object object : array) {
								passengers.add(object);
							}
						}
						if(passengers.size() > 200){
							performTuniu(passengers,minid);
							passengers = new JSONArray(); //乘客数据
						}
					}
					if(passengers.size() > 0){
						performTuniu(passengers,minid);
						passengers = new JSONArray(); //乘客数据
					}
					try {
						Thread.sleep(10L);//每次200条账号 2000条数据 处理暂停0.0s
					} catch (InterruptedException e) {
					}
					WriteLog.write("途牛回调个数", "总数--->" + counti);
				}

			}
		}
		
		/**
		 * 链接 途牛接口
		 * @param Currentaccount
		 * @param id
		 * @param minid
		 * @author zhangqifei
		 * @time 2016年8月9日 下午3:25:13
		 */
		private JSONArray getTuniu(JSONObject Currentaccount, int id, int minid){
			WriteLog.write("途牛的最后的一个minid", minid + "");
			List<DBObject> list = getAccountByMongo(Currentaccount, id);  //获取所有乘客
			if (list == null || list.isEmpty()) {
				return null;
			}
			WriteLog.write("途牛接口", "id" + id + "获取list返回的数据：" + list);
			JSONArray passengers = new JSONArray();
			JSONObject passenger = new JSONObject();
			for (DBObject dbObject : list) {
				passenger = getTuniuJson(dbObject,Currentaccount.getString("accountId"), id);
				passengers.add(passenger);
			}
			return passengers;
		}
		
		/**
		 * 执行操作
		 * @param array
		 * @param minid
		 * @author lzd
		 * @time 2016年8月11日 上午11:08:32
		 */
		private void performTuniu(JSONArray array,int minid){
			JSONArray jsonArray = new JSONArray();
			int number = 0;
			for (int i = 0; i < array.size(); i++) {
				number++;
				jsonArray.add(array.getJSONObject(i)); 
				if(number == 10){
					send2Tuniu(number,minid+"", jsonArray);
					jsonArray = new JSONArray();
					number=0;
				}
			} 
			if(array.size() != 0){
				send2Tuniu(number,minid+"", jsonArray); 
			} 
		}
		
		/**
		 * 获取途牛参数
		 * @param dbObject
		 * @param string
		 * @param id
		 * @return
		 * @author lzd
		 * @time 2016年8月9日 下午3:52:01
		 */
		private JSONObject getTuniuJson(DBObject dbObject, String string, int id){
			JSONObject jsonObject = new JSONObject();
			JSONObject dbjson = new JSONObject();
			try {
				dbjson = JSONObject.parseObject(dbObject.toString());
			} catch (Exception e) {
				WriteLog.write("途牛接口_ERROR", "id" + id);
				ExceptionUtil.writelogByException("途牛接口_ERROR_dbObject异常", e);
			}
			long passportseno = dbjson.getLongValue("IDNumber");
			String passengersename = dbjson.getString("RealName");
			String passengertypeid = dbjson.getString("IDType");
			jsonObject.put("idCardName","身份证");// 身份证%e8%ba%ab%e4%bb%bd%e8%af%81
			jsonObject.put("idCard", getPassengertypeid(passportseno));// 身份证号  OK
			jsonObject.put("userName",passengersename); // 用户名 OK
			jsonObject.put("type", passengertypeid);
			jsonObject.put("status", "1");
			jsonObject.put("msg", "更新"); 
			WriteLog.write("途牛接口", "id" + id + "返回的json数据：" + jsonObject);
			return jsonObject;
		}
		
		/**
		 * 途牛send
		 * @param id
		 * @param passenger2
		 * @author zhangqifei
		 * @time 2016年8月9日 下午3:27:57
		 */
		private void send2Tuniu(int number,String vendorOrderId,JSONArray array) {
			String account = PropertyUtil.getValue("tuniu.account", "Train.GuestAccount.properties");
			String key = PropertyUtil.getValue("tuniu.desKey", "Train.GuestAccount.properties");
			JSONObject json = new JSONObject();
			json.put("number", number+"");
			json.put("vendorOrderId", vendorOrderId);
			json.put("idCardList", array); 
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("account", account);  
			jsonObject.put("returnCode", 231000);  
			jsonObject.put("timestamp",formatTime());  
			try {
				jsonObject.put("data", TuNiuDesUtil.encrypt(json.toString()));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			jsonObject.put("sign", SignUtil.generateSign(jsonObject.toString(), key)); 
			String result = new String();
			try {
				WriteLog.write("途牛接口", "vendorOrderId" + vendorOrderId + "passenger参数" + json);
				// 返回标识  http://api.tuniu.org/aln/common/identityValidationUpload   线上地址
				result = submitPost("http://api.tuniu.org/aln/common/identityValidationUpload",jsonObject.toString(), "UTF-8").toString();
				if (result.contains("true")) {
					counti++;
				}
				System.out.println("途牛成功回调------->" + counti + "个。这次发送了------>"+number+"个乘客。"); 
				WriteLog.write("途牛回调个数", "途牛回调个数--->" + counti);
				WriteLog.write("途牛接口", "vendorOrderId" + vendorOrderId + "最终返回的数据" + result);
				Thread.sleep(5L);
			} catch (Exception e) {
				WriteLog.write("途牛接口_ERROR", "vendorOrderId" + vendorOrderId);
				ExceptionUtil.writelogByException("途牛接口_ERROR", e);
			}
		}
		
		/**
	     * 格式化当前时间
	     **/
	    public String formatTime() {
	        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        return sdf.format(new Date());
	    }
		//----------------------------------------------途牛-结束---------------------------------------
	
}
