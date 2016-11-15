package com.ccservice.b2b2c.atom.refund.handle;

import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hotelorderrc.Hotelorderrc;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;

public class HotelrefundMoreHandle implements RefundHandle {
	
	Log logger=LogFactory.getLog(HotelrefundNotifyHandle.class);
	@Override
	public void refundedHandle(boolean success, long orderid, String batchno) {
	    if(success){
	        AirticketPaymentrecord record=Server.getInstance().getB2BAirticketService().findAirticketPaymentrecord(orderid);
	        if(record!=null&&record.getStatus()==1){
	            record.setTradeno(batchno);
	            record.setTradetime(new Timestamp(System.currentTimeMillis()));
	            record.setTradetype(4);
	            if(success){
	                record.setStatus(3);
	            }else{
	                record.setStatus(4);
	            }
	            record.setYwtype(2);
	            Server.getInstance().getB2BAirticketService().updateAirticketPaymentrecord(record);
	            Hotelorderrc rc=new Hotelorderrc();
	            rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
	            rc.setContent("系统退款完成(补款退款)，标识："+batchno);
	            rc.setLanguage(0);
	            Hotelorder order=Server.getInstance().getHotelService().findHotelorder(record.getOrderid());
	            rc.setOrderid(order.getOrderid());
	            rc.setHandleuser("-1");
	            rc.setState(8);
	            try {
	                Server.getInstance().getHotelService().createHotelorderrc(rc);
	            } catch (SQLException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	}
	public Log getLogger() {
		return logger;
	}
	public void setLogger(Log logger) {
		this.logger = logger;
	}
	
}
