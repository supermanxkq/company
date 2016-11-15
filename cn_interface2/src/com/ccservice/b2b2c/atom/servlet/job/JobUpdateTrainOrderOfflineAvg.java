package com.ccservice.b2b2c.atom.servlet.job;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.server.Server;

/**
 * @author 作者 guozhengju:
 * @version 创建时间：2015年8月10日 下午2:18:11 类说明
 **/
public class JobUpdateTrainOrderOfflineAvg implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		System.out
				.println("****************定时去更新线下火车票信息***************AAAAA*************");
		// 出票效率(总数，出票商Id)
		String sql1 = "select distinct ProviderAgentid from TrainOrderOfflineRecord";
		List agentsList = Server.getInstance().getSystemService()
				.findMapResultBySql(sql1, null);
		// type=1为五分钟，2为35分钟

		if (agentsList.size() > 0) {
			for (int type = 1; type < 3; type++) {

				for (int i = 0; i < agentsList.size(); i++) {
					Map map = (Map) agentsList.get(i);
					String result = getAvgEffAndTime(map.get("ProviderAgentid")
							.toString(), type);
					String[] avg = result.split(",");
					String avgEff = avg[0];
					String avgTime = avg[1];

					String agentId = map.get("ProviderAgentid").toString();
					String recordsql = "SELECT * FROM TrainOfflineAvgEffAndTime WHERE recordType="
							+ type + " AND ProviderAgentid=" + agentId;
					List recordList = Server.getInstance().getSystemService()
							.findMapResultBySql(recordsql, null);
					if (recordList.size() > 0) {
						String updatesql1 = "UPDATE TrainOfflineAvgEffAndTime SET avgEffective='"
								+ avgEff
								+ "',avgTime='"
								+ avgTime
								+ "' WHERE ProviderAgentid="
								+ agentId
								+ " AND recordType=" + type;
						Server.getInstance().getSystemService()
								.excuteAdvertisementBySql(updatesql1);
					} else {
						String updatesql1 = "INSERT TrainOfflineAvgEffAndTime VALUES("
								+ agentId
								+ ",'"
								+ avgEff
								+ "','"
								+ avgTime
								+ "'," + type + ")";

						Server.getInstance().getSystemService()
								.excuteAdvertisementBySql(updatesql1);
					}
				}
			}

		}

	}

	/**
	 * 根据agentid获取效率和平均时长
	 * 
	 * @param agentsList
	 * @param type
	 * @return guozhengju 2015/08/10 19:37
	 */
	public static String getAvgEffAndTime(String agentId, int type) {
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time11 = "2015-08-08 08:04:54.000";
		String time22 = "2015-08-08 14:04:54.000";

		/*
		 * if (type == 1) { time11 = sdf.format(d.getTime() - 5 * 60 * 1000);
		 * time22 = sdf.format(d.getTime() - 4 * 60 * 1000); } else if (type ==
		 * 2) { time11 = sdf.format(d.getTime() - 35 * 60 * 1000); time22 =
		 * sdf.format(d.getTime() - 34 * 60 * 1000); }
		 */
		float efficiency = 0;
		long avg = 0;
		// 已出票订单数量
		String sqlChupiao1 = "SELECT * FROM TrainOrderOfflineRecord where ResponseTime>DistributionTime and ProviderAgentid="
				+ agentId
				+ "  and DistributionTime between '"
				+ time11
				+ "' and '" + time22 + "' ";
		List Chupiao1 = Server.getInstance().getSystemService()
				.findMapResultBySql(sqlChupiao1, null);
		// 所有分配订单数量
		String sqlChupiao2 = "SELECT distinct FKTrainOrderOfflineId from TrainOrderOfflineRecord  where ProviderAgentid="
				+ agentId
				+ " and DistributionTime between '"
				+ time11
				+ "' and '" + time22 + "' ";
		List Chupiao2 = Server.getInstance().getSystemService()
				.findMapResultBySql(sqlChupiao2, null);

		if (Chupiao1.size() > 0 && Chupiao2.size() > 0) {
			efficiency = (float) Chupiao1.size() / (float) Chupiao2.size();
		}
		long sum = 0;
		int count = 0;

		String sqlTime = "SELECT DistributionTime,ResponseTime FROM TrainOrderOfflineRecord where ResponseTime>DistributionTime "
				+ " and ProviderAgentid="
				+ agentId
				+ " and DistributionTime between '"
				+ time11
				+ "' and '"
				+ time22 + "'";
		List listTime = Server.getInstance().getSystemService()
				.findMapResultBySql(sqlTime, null);
		if (listTime.size() > 0) {
			count = listTime.size();
			for (int j = 0; j < listTime.size(); j++) {
				Map map1 = (Map) listTime.get(0);
				String time1 = map1.get("DistributionTime").toString();
				String time2 = map1.get("ResponseTime").toString();
				SimpleDateFormat sd = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Date d1 = new Date();
				Date d2 = new Date();
				try {
					d1 = sd.parse(time1);
					d2 = sd.parse(time2);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				long l = (d2.getTime() - d1.getTime());
				sum += l;
			}
			avg = sum / count;
		}
		// efficiency出票率，avg平均出票时间
		return efficiency + "," + avg;
	}

	public static void main(String[] args) {
		System.out
				.println("****************定时去更新线下火车票信息***************AAAAAAAAAAAAA*************");
		// 出票效率(总数，出票商Id)
		String sql1 = "select distinct ProviderAgentid from TrainOrderOfflineRecord";
		List agentsList = Server.getInstance().getSystemService()
				.findMapResultBySql(sql1, null);
		// type=1为五分钟，2为35分钟

		if (agentsList.size() > 0) {
			for (int type = 1; type < 3; type++) {

				for (int i = 0; i < agentsList.size(); i++) {
					Map map = (Map) agentsList.get(i);
					String result = getAvgEffAndTime(map.get("ProviderAgentid")
							.toString(), type);
					String[] avg = result.split(",");
					String avgEff = avg[0];
					String avgTime = avg[1];

					String agentId = map.get("ProviderAgentid").toString();
					String recordsql = "SELECT * FROM TrainOfflineAvgEffAndTime WHERE recordType="
							+ type + " AND ProviderAgentid=" + agentId;
					List recordList = Server.getInstance().getSystemService()
							.findMapResultBySql(recordsql, null);
					if (recordList.size() > 0) {
						String updatesql1 = "UPDATE TrainOfflineAvgEffAndTime SET avgEffective='"
								+ avgEff
								+ "',avgTime='"
								+ avgTime
								+ "' WHERE ProviderAgentid="
								+ agentId
								+ " AND recordType=" + type;
						Server.getInstance().getSystemService()
								.excuteAdvertisementBySql(updatesql1);
					} else {
						String updatesql1 = "INSERT TrainOfflineAvgEffAndTime VALUES("
								+ agentId
								+ ",'"
								+ avgEff
								+ "','"
								+ avgTime
								+ "'," + type + ")";

						Server.getInstance().getSystemService()
								.excuteAdvertisementBySql(updatesql1);
					}
				}
			}

		}
	}

}
