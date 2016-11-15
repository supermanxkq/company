package com.ccservice.b2b2c.atom.servlet.job;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ccservice.b2b2c.atom.server.Server;

public class TrainStationArrival {
	public static void main(String[] args) {
		String sql = "SELECT City FROM TrainStationSaleTime WITH(NOLOCK)";
		List list = Server.getInstance().getSystemService()
				.findMapResultBySql(sql, null);
		int i = 0;
		for (int j = 62; j < list.size(); j++) {
			Map map = (Map) list.get(j);
			creatAddTrainDetailInfo(map, j);
			if (i > 30) {
				try {
					Thread.sleep(5000);
					i = 0;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			i++;
		}
	}

	public static void creatAddTrainDetailInfo(Map map, int j) {
		ExecutorService pool = Executors.newFixedThreadPool(1);
		Thread t2 = null;
		t2 = new JobTrainStationArrival(map, j);
		pool.execute(t2);
		pool.shutdown();
	}
}
