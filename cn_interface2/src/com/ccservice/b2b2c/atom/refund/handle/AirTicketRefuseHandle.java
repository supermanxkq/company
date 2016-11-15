package com.ccservice.b2b2c.atom.refund.handle;

import java.sql.SQLException;
import java.sql.Timestamp;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinforc.Orderinforc;
import com.ccservice.b2b2c.framework.Treadenotify;

public class AirTicketRefuseHandle implements RefundHandle{

	@Override
	public void refundedHandle(boolean success,long orderid, String batchno) {
		String sql="UPDATE T_ORDERINFO SET  C_PAYSTATUS=2 WHERE ID="+orderid;
		WriteLog.write("拒单退票","退款成功。处理退款："+sql);
		Server.getInstance().getSystemService().findMapResultBySql(sql, null);
		WriteLog.write("拒单退票","退款成功");
		Orderinforc rc=new Orderinforc();
		rc.setOrderinfoid(orderid);
		rc.setContent("拒单退款成功。退款批次号："+batchno);
		rc.setCustomeruserid(0l);
		rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
		try {
			Server.getInstance().getAirService().createOrderinforc(rc);
			//退款成功通知到接口平台。
			Treadenotify.refuseRefundnotify(orderid);
		} catch (SQLException e) {
		}
		
	}
	

}
