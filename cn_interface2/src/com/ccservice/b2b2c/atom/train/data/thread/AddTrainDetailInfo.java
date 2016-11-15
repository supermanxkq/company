package com.ccservice.b2b2c.atom.train.data.thread;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;

public class AddTrainDetailInfo extends Thread {

	String train_code;

	String TrainParticulars;

	String from_station;

	String to_station;

	String train_date;

	long l2;

	public AddTrainDetailInfo(String train_code, String TrainParticulars,
			String from_station, String to_station, String train_date, long l2) {
		this.train_code = train_code;
		this.TrainParticulars = TrainParticulars;
		this.from_station = from_station;
		this.to_station = to_station;
		this.train_date = train_date;
		this.l2 = l2;
	}

	@Override
	public void run() {
		try {
			try {
				int count_trainprice = Server
						.getInstance()
						.getSystemService()
						.countAdvertisementBySql(
								"select count(*) from TrainDetailInfo with(nolock) where TrainCode='"
										+ this.train_code.toUpperCase()
										+ "' AND StartDate='" + this.train_date
										+ "'");
				String sqlinsert = "";
				if (count_trainprice == 0) {
					sqlinsert = "delete from TrainDetailInfo where TrainCode='"
							+ this.train_code.toUpperCase()
							+ "'AND StartDate='"
							+ this.train_date
							+ "';insert into TrainDetailInfo(TrainCode,TrainParticulars,StartStationName,EndStationName,StartDate) values ('"
							+ this.train_code.toUpperCase() + "','"
							+ TrainParticulars + "','" + this.from_station
							+ "','" + this.to_station + "','" + this.train_date
							+ "')";
				} else {
					sqlinsert = "update TrainDetailInfo set TrainCode='"
							+ this.train_code.toUpperCase()
							+ "',TrainParticulars='" + TrainParticulars
							+ "',StartStationName='" + this.from_station
							+ "',EndStationName='" + this.to_station
							+ "',StartDate='" + this.train_date
							+ "' where TrainCode='"
							+ this.train_code.toUpperCase()
							+ "' AND StartDate='" + this.train_date + "'";
				}
				Server.getInstance().getSystemService()
						.excuteEaccountBySql(sqlinsert);
				long l1 = System.currentTimeMillis();
				long l = l1 - l2;
				System.out.println("扔AddTrainDetailInfo耗时:" + l + "毫秒");
			} catch (Exception e) {
				WriteLog.write("Update12306TrainDetail", "插入数据库失败");
				e.printStackTrace();
			}
		} catch (Exception e) {

		}
	}
}
