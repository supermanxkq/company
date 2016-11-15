package com.ccservice.b2b2c.atom.servlet.QunarTrain.Thread;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;

public class QunarCancelFalseThread extends Thread {
	
	private final String logname = "qunar去哪_取消订单接口新表";
	
	private final int random = new Random().nextInt();
	
	@Override
	public void run() {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (true) {
			Map map = null;
			String interfaceOrderNumber = "";
			String sql = "";
			String deleteSql = "";
			String qunarcancelSql = "SELECT INTERFACEORDERNUMBER FROM T_QUNARCANCEL WITH(NOLOCK)";
			List list = Server.getInstance().getSystemService()
					.findMapResultBySql(qunarcancelSql, null);
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					map = (Map) list.get(i);
					interfaceOrderNumber = map.get("INTERFACEORDERNUMBER")
							.toString();
					sql = "exec [sp_TrainDemandBespeak_Select_Update] @InterfaceOrderNumber='"
							+ interfaceOrderNumber + "'";
					List isHaveList = Server.getInstance().getSystemService()
							.findMapResultBySql(sql, null);
					if (isHaveList.size() > 0) {
						map = (Map) isHaveList.get(0);
						String result = map.get("result").toString();
						if (!result.contains("104") && !result.contains("105")) {
							WriteLog.write(logname, random + "-->result:" + result + "-->interfaceOrderNumber:" + interfaceOrderNumber);
							deleteSql = "DELETE FROM T_QUNARCANCEL WHERE INTERFACEORDERNUMBER='"
									+ interfaceOrderNumber + "'";
							Server.getInstance().getSystemService()
									.findMapResultBySql(deleteSql, null);
						}
					}
				}
			}
			try {
				Thread.sleep(5000); // 跑完一圈睡5s
			} catch (InterruptedException e) {
			}
		}
	}
}
