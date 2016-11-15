package com.ccservice.b2b2c.atom.servlet.job;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.TrainUtil;

public class JobTrainStationArrival extends Thread {

	Map map;

	int j;

	public JobTrainStationArrival(Map map, int j) {
		this.map = map;
		this.j = j;
	}

	@Override
	public void run() {
		name(this.map, this.j);
	}

	public void name(Map map, int j) {
		String train_station_name = map.get("City").toString();
		String train_station_code = Train12306StationInfo
				.GetValue(train_station_name);
		String json = TrainUtil.getliecheshike(train_station_name,
				"2015-09-10", train_station_code, 5);
		if (json == null || "-1".equals(json)) {
			WriteLog.write("errorstation", "" + j + "--->" + train_station_name
					+ "--->" + train_station_code);
			return;
		}
		JSONObject jsonObject = JSON.parseObject(json);
		JSONObject jsonObject2 = jsonObject.getJSONObject("data");
		JSONArray array = jsonObject2.getJSONArray("data");
		if (array == null) {
			WriteLog.write("errorstation", "" + j + "--->" + train_station_name
					+ "--->" + train_station_code);
			return;
		}
		for (int i = 0; i < array.size(); i++) {
			JSONObject object = array.getJSONObject(i);
			String start_station_name = object.get("start_station_name")
					.toString();
			String end_station_name = object.get("end_station_name").toString();
			String DepartureStationCity = Return(start_station_name);
			String FinalStationCity = Return(end_station_name);
			try {
				Server.getInstance()
						.getSystemService()
						.findMapResultByProcedure(
								"[dbo].[sp_TrainStationToStation_Select_Insert] "
										+ " @DepartureStationCity="
										+ DepartureStationCity
										+ "  ,@FinalStationCity ="
										+ FinalStationCity);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public String Return(String name) {
		String N = name.substring(name.length() - 1, name.length());
		if (N.contains("东")) {
			name = name.substring(0, name.length() - 1);
		} else if (N.contains("南")) {
			name = name.substring(0, name.length() - 1);
		} else if (N.contains("西")) {
			name = name.substring(0, name.length() - 1);
		} else if (N.contains("北")) {
			name = name.substring(0, name.length() - 1);
		}
		return name;
	}

}
