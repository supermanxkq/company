package com.ccservice.b2b2c.atom.servlet.job;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.servlet.job.thread.Jobupdate12306trainnodata_update_distance_thread;
import com.ccservice.b2b2c.base.trainno.TrainNo;
import com.ccservice.b2b2c.util.FileUtils;
import com.ccservice.b2b2c.util.TrainUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 更新12306的车次的车站信息
 * 
 * @time 2015年3月19日 下午2:20:30
 * @author chendong
 */
public class Jobupdate12306trainnodata implements Job {
	public static void main(String[] args) {
		Long l1 = System.currentTimeMillis();
		// execute();
		// get12306cookie();
		// String jsonString = getliecheshike("55000K837870");//获取json数据
		// System.out.println(jsonString);
		// updatetrainnodata();
		updatecheci_distance("G79", 1);
		System.out.println("耗时:" + (System.currentTimeMillis() - l1));
	}

	// final private static String DAMAURL =
	// "http://hthyservice.hangtian123.net/";

	// final private static String DAMAURL =
	// "http://localhost:9019/hthyservice/";

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Long l1 = System.currentTimeMillis();
		String jobupdate12306trainnodata_excute_start = PropertyUtil.getValue(
				"jobupdate12306trainnodata_excute_start", "Train.properties");
		if ("1".equals(jobupdate12306trainnodata_excute_start)) {
			execute();// 更新所有数据开始
		}
		excute_update_distance();// 更新里程信息
		System.out.println("耗时:" + (System.currentTimeMillis() - l1));
	}

	/**
	 * 只更新里程信息
	 * 
	 * @time 2015年5月4日 下午12:48:28
	 * @author chendong
	 */
	private static void excute_update_distance() {
		int threadcount = 5;
		String sql_s = "select top 1 C_STATION_TRAIN_CODE from T_TRAINNO with(nolock) where C_STATION_NO='01' and C_DISTANCE is null order by id desc";
		List<TrainNo> trains = Server.getInstance().getSystemService()
				.findMapResultBySql(sql_s, null);
		for (int i = 0; i < trains.size(); i++) {
			Map map = (Map) trains.get(i);
			String station_train_code = map.get("C_STATION_TRAIN_CODE")
					.toString().trim();
			// System.out.println(station_train_code);
			updatecheci_distance(station_train_code, threadcount);
		}
	}

	private void bianlichexingupdate_distance() {
		int threadcount = 5;
		String[] lieche_lists = new String[] {
				"http://lieche.58.com/checi/kuai.html",
				"http://lieche.58.com/checi/pukuai.html" };
		for (int n = 1; n < lieche_lists.length; n++) {
			String lieche_list = lieche_lists[n];
			String lieche_list_html = SendPostandGet.submitGet(lieche_list);
			String[] lieche_list_htmls = lieche_list_html.split("<li>");
			for (int i = 1; i < lieche_list_htmls.length; i++) {
				String checi_String = lieche_list_htmls[i];
				checi_String = checi_String.substring(
						checi_String.indexOf("_blank") + 8).split("</a></li>")[0]
						.trim();
				updatecheci_distance(checi_String, threadcount);
			}
		}
	}

	/**
	 * 根据车次获取每个车次的公里数
	 * 
	 * @param checi_String
	 * @time 2015年5月4日 下午3:30:57
	 * @author chendong
	 */
	private static void updatecheci_distance(String checi_String,
			int threadcount) {
		// 创建一个可重用固定线程数的线程池
		ExecutorService pool = Executors.newFixedThreadPool(threadcount);
		// 创建实现了Runnable接口对象，Thread对象当然也实现了Runnable接口
		Thread t1 = null;
		// 将线程放入池中进行执行
		t1 = new Jobupdate12306trainnodata_update_distance_thread(checi_String);
		pool.execute(t1);
		pool.shutdown();
	}

	private static void execute() {
		getallcheciandshike(2);
	}

	private static void updatetrainnodata() {
		List<TrainNo> trains = Server
				.getInstance()
				.getTrainService()
				.findAllTrainNo(
						"where C_STATION_NO='01' and c_runtime is null",
						"order by id desc", 100, 0);
		for (int i = 0; i < trains.size(); i++) {
			TrainNo trainno = trains.get(i);
			try {
				Thread.sleep(1000L);
				System.out
						.println("=========================================S");
				System.out.println(trainno.getTrain_no().trim() + ":"
						+ trainno.getStation_train_code().trim());
				updatetrainnodata(trainno.getTrain_no().trim(), 0);
				System.out
						.println("=========================================E");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新trainno数据
	 * 
	 * @time 2015年3月17日 下午8:34:31
	 * @author chendong
	 */
	private static void updatetrainnodata(String train_no, int count) {
		String train_date = TimeUtil.gettodaydatebyfrontandback(1, 10);
		String jsonString = "";
		jsonString = TrainUtil.getliecheshike(train_no, train_date, jsonString,
				0);
		if (jsonString.indexOf("\"data\":null") >= 0) {
			Server.getInstance()
					.getAirService()
					.excuteAirbasepriceBySql(
							"update T_TRAINNO set c_runtime='null' where C_STATION_NO='01' and C_TRAIN_NO='"
									+ train_no + "'");
			return;
		}
		System.out.println(jsonString);
		List<TrainNo> trainnos = new ArrayList<TrainNo>();
		try {
			trainnos = TrainUtil.jiexi12306_checichaxun_shuju(jsonString,
					train_no);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		if (trainnos.size() > 0) {
			TrainNo trainno = trainnos.get(0);
			String station_train_code = trainno.getStation_train_code();
			Server.getInstance()
					.getSystemService()
					.excuteAdvertisementBySql(
							"DELETE FROM T_TRAINNO WHERE C_STATION_TRAIN_CODE='"
									+ station_train_code + "'");
			for (int j = 0; j < trainnos.size(); j++) {
				try {
					Server.getInstance().getTrainService()
							.createTrainNo(trainnos.get(j));
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 获取所有车次和这个车次的时刻信息
	 * 
	 * @param type
	 *            1全取2更新
	 * @time 2015年3月2日 下午4:58:30
	 * @author Administrator
	 */
	public static void getallcheciandshike(int type) {
		// https://kyfw.12306.cn/otn/resources/js/query/train_list.js?scriptVersion=1.2887|2015-3-19
		// 14:27:11
		String TrainStationNames_path = Jobupdate12306trainnodata.class
				.getClassLoader().getResource("").toString().substring(6)
				+ "train_list.js";
		System.out.println(TrainStationNames_path);
		String filedata = "-1";
		try {
			filedata = FileUtils.readFile(TrainStationNames_path, "UTF-8");
			if (filedata.length() > 0) {
				filedata = filedata.replace("var train_list =", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (filedata.length() > 0) {
			// JSONArray jsonArray = JSONObject.parseArray(filedata);
			JSONObject jsonobject = JSONObject.parseObject(filedata);
			Set<String> sets = jsonobject.keySet();
			Iterator iterator = sets.iterator();
			String data_key = iterator.next().toString();
			// while (iterator.hasNext()) {
			// System.out.println(iterator.next());
			// }
			jsonobject = jsonobject.getJSONObject(data_key);// "2015-03-11"
			sets = jsonobject.keySet();
			iterator = sets.iterator();
			// String data_key = iterator.next().toString();
			while (iterator.hasNext()) {
				String array_key = iterator.next().toString();
				System.out.println(array_key);
				JSONArray JSONArray = jsonobject.getJSONArray(array_key);// D|T|G||C|O|Z|K
				for (int i = 0; i < JSONArray.size(); i++) {
					JSONObject jsonobject_checi = JSONArray.getJSONObject(i);
					chulistation_train_code(jsonobject_checi, data_key, type);
				}
			}
		}
	}

	/**
	 * 
	 * 
	 * @param jsonobject_checi
	 * @param type
	 *            1全取2更新
	 * @time 2015年3月19日 下午3:52:53
	 * @author chendong
	 */
	private static void chulistation_train_code(JSONObject jsonobject_checi,
			String data_key, int type) {
		String train_no = jsonobject_checi.getString("train_no");
		String station_train_code = jsonobject_checi
				.getString("station_train_code");
		station_train_code = station_train_code.split("[(]")[0];
		int trainNo_count = gettrainnocount(station_train_code);
		System.out.println(station_train_code + ":" + trainNo_count);
		try {
			if (type == 1 || trainNo_count < 2) {
				String jsonString = TrainUtil.getliecheshike(train_no,
						data_key, "", 0);
				List<TrainNo> trainnos = TrainUtil
						.jiexi12306_checichaxun_shuju(jsonString, train_no);
				if (trainnos == null || trainnos.size() == 0) {
					jsonString = TrainUtil.getliecheshike(train_no, data_key,
							jsonString, 0);
					trainnos = TrainUtil.jiexi12306_checichaxun_shuju(
							jsonString, train_no);
				} else {
					String delete_sql = "DELETE FROM T_TRAINNO WHERE C_STATION_TRAIN_CODE like '%"
							+ station_train_code + "%'";
					Server.getInstance().getSystemService()
							.excuteAdvertisementBySql(delete_sql);
					for (int j = 0; j < trainnos.size(); j++) {
						try {
							Server.getInstance().getTrainService()
									.createTrainNo(trainnos.get(j));
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	private static int gettrainnocount(String station_train_code) {
		int trainNo_count = 0;
		try {
			trainNo_count = Server
					.getInstance()
					.getTrainService()
					.countTrainNoBySql(
							" SELECT COUNT(*) FROM T_TRAINNO WHERE C_STATION_TRAIN_CODE='"
									+ station_train_code + "' ");
			if (trainNo_count == 0) {
				trainNo_count = Server
						.getInstance()
						.getTrainService()
						.countTrainNoBySql(
								" SELECT COUNT(*) FROM T_TRAINNO WHERE C_STATION_TRAIN_CODE like '%"
										+ station_train_code + "%' ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return trainNo_count;
	}

}
