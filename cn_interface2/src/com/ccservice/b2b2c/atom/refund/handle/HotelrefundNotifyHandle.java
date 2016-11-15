package com.ccservice.b2b2c.atom.refund.handle;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.hotelorderrc.Hotelorderrc;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;

/**
 * 
 * @author wzc
 *	酒店订单退款成功通知处理类
 */
public class HotelrefundNotifyHandle implements RefundHandle{
	
	/**
	 * 判断是否需要支付供应
	 * @param hotelorder
	 * @return
	 */
	public boolean getISpayoffer(Hotelorder hotelorder){
		if(hotelorder.getPayoffer()!=null){
			if(hotelorder.getPayoffer()==3){
				return false;
			}
		}
		return true;
	}
	//判断是否是易订行
	public String getIsYdx(){
		List<Sysconfig> isydxs=Server.getInstance().getSystemService().findAllSysconfig("where c_name='isydx'", "", -1, 0);
		if(isydxs!=null&&isydxs.size()==1){
			Sysconfig isydx=isydxs.get(0);
			return isydx.getValue();
		}
		return "";
	}
	Log logger=LogFactory.getLog(HotelrefundNotifyHandle.class);
	@Override
	public void refundedHandle(boolean success,long orderid, String batchno) {
	    if(success){
	        Hotelorderrc rc=new Hotelorderrc();
	        rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
	        rc.setContent("系统退款完成，标识："+batchno);
	        rc.setLanguage(0);
	        Hotelorder order=Server.getInstance().getHotelService().findHotelorder(orderid);
	        rc.setOrderid(order.getOrderid());
	        rc.setHandleuser("-1");
	        rc.setState(8);
	        try {
	            Server.getInstance().getHotelService().createHotelorderrc(rc);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        if(order!=null){
	            String sql="select * from T_AIRTICKETPAYMENTRECORD where C_YWTYPE=2 and C_ORDERID="+order.getId()+" and C_STATUS=2 order by id desc";
	            List<AirticketPaymentrecord> records=Server.getInstance().getB2BSystemService().findAllPaymentrecordBySql(sql);
	            if(records.size()>0){
	                AirticketPaymentrecord change=records.get(0);
	                
	                if(change.getTradetype()==3){//退换差价
	                    
	                }else if(change.getTradetype()==4){//退款
	                    if(order.getState() == 10 // 退款申请中
	                            || order.getState() == 5// 支付成功待安排房间
	                            || order.getState() == 15){
	                        order.setState(12);//退订退款完成
	                    }else{
	                        order.setState(11);//预定失败退款完成
	                    }
	                    order.setPaystate(2l);// 已退款
	                    Server.getInstance().getHotelService().updateHotelorderIgnoreNull(order);
	                    if(getIsYdx().equals("1")){
	                        if(order.getOrdertype().equals("1")&&getISpayoffer(order)){
	                            if(order.getPaystate()==2){
	                                order.setOutorderstate(4l);
	                                if(order.getCustomerconfig()==1){
	                                    
	                                }else if(order.getCustomerconfig()==2){
	                                    //order.setState(11);//预定失败退款完成
	                                }
	                                Server.getInstance().getHotelOrderInterface().synchotelorder(order, null, null, 2l);
	                            }
	                        }
	                        Server.getInstance().getHotelService().updateHotelorderIgnoreNull(order);
	                    }
	                }
	                
	                change.setStatus(3);
	                change.setTradeno(batchno);
	                Server.getInstance().getB2BAirticketService().updateAirticketPaymentrecord(change);
	            }
	        }
	    }else{
	        Hotelorderrc rc=new Hotelorderrc();
            rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
            rc.setContent("系统退款失败，标识："+batchno);
            rc.setLanguage(0);
            Hotelorder order=Server.getInstance().getHotelService().findHotelorder(orderid);
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
