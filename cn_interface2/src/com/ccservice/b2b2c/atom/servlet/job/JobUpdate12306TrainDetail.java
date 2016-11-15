package com.ccservice.b2b2c.atom.servlet.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.tenpay.util.MD5Util;

public class JobUpdate12306TrainDetail implements Job {
	// 接口账号
	public static String partnerid = "tongcheng_train_test";

	public static String key = "lmh46c63ubh1h8oj6680wbtgfi40btqh";

	public static String traincheciurl = "http://localhost:9004/cn_interface/TrainEnquiriesServlet";

	public final static SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat(
			"yyyyMMddHHmmssSSS");

	public final static SimpleDateFormat yyyyMMdd = new SimpleDateFormat(
			"yyyy-MM-dd");

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		String method = "get_train_info";
		String reqtime = getreqtime();
		String sign = getsign(partnerid, method, reqtime, key);
		String from_station = "";
		String to_station = "";
		List list = data();
		for (int i = 0; i < list.size(); i++) {
			long a = System.currentTimeMillis();
			Map map = (Map) list.get(i);
			String TrainCode = map.get("TrainCode").toString();
			String TrainDetailsCode = map.get("TrainDetailsCode").toString();
			String jsonStr = "{\"partnerid\":\"" + partnerid
					+ "\",\"method\":\"" + method + "\",\"reqtime\":\""
					+ reqtime + "\",\"sign\":\"" + sign
					+ "\",\"train_date\":\"" + getreqdate()
					+ "\",\"from_station\":\"" + from_station
					+ "\",\"to_station\":\"" + to_station
					+ "\",\"train_no\":\"" + TrainDetailsCode
					+ "\",\"train_code\":\"" + TrainCode + "\"}";
			SendPostandGet.submitPost(traincheciurl, "jsonStr=" + jsonStr,
					"UTF-8").toString();
			System.out.println("执行耗时 : " + (System.currentTimeMillis() - a)
					/ 1000f + " 秒 ");
			WriteLog.write("Update12306TrainDetail",
					"执行插入一次耗时 : " + (System.currentTimeMillis() - a) / 1000f
							+ " 秒 ");
		}
	}

	public static List data() {
		String a = getreqdate() + " 00:00:00.000";
		String sql = "SELECT * FROM TrainDetailCode WITH(NOLOCK) WHERE TrainDepartTime='"
				+ a + "'";
		String get = "";
		List list = Server.getInstance().getSystemService()
				.findMapResultBySql(sql, null);
		return list;
	}

	public static String getreqtime() {
		return yyyyMMddHHmmssSSS.format(new Date());
	}

	public static String getreqdate() {
		return yyyyMMdd.format(new Date());
	}

	/**
	 * md5(partnerid+method+reqtime+md5(key))
	 **/
	public static String getsign(String partnerid, String method,
			String reqtime, String key) {
		return MD5Util.MD5Encode(
				partnerid + method + reqtime + MD5Util.MD5Encode(key, "UTF-8"),
				"UTF-8");
	}

}
