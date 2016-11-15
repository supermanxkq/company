package com.ccservice.b2b2c.atom.servlet;


import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;

/**
 * 
 * 下单线程
 *
 */
public class MyThreadDownOrderNewReal extends Thread {

	static Logger log = Logger.getLogger(MyThreadDownOrderNewReal.class);

	private long sid;
	private long orderid;
	private int orderstatus;

	public MyThreadDownOrderNewReal(long sid, long orderid, int orderstatus) {
		this.sid=sid;
		this.orderid=orderid;
		this.orderstatus=orderstatus;
	}
	
	public void run(){
		DownOrderNew( sid,  orderid, orderstatus);
		
	}

	public void DownOrderNew(long sid, long orderid, int orderstatus) {
		try {
		    WriteLog.write("t4.6收单下单", sid + ":" + sid + "订单id：" + orderid);
            String sqlqq="update T_TRAINORDER set C_CREATETIME='"+TimeUtil.getCurrentTime()+"',C_ORDERSTATUS="+orderstatus+"  where ID = "+orderid;
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sqlqq);
            new TongchengSupplyMethod().activeMQroordering(orderid);// 放单发mq
            String sql = "delete  from T_TRAINORDERACQUIRING  where ID = " + sid;
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args) {
		System.out.println(TimeUtil.getCurrentTime());
		String sql = "update T_TRAINORDER set C_CREATETIME='"+TimeUtil.getCurrentTime()+"',C_ORDERSTATUS=8  where ID = 47146";
		Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
	}
}
