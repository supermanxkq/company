package com.ccservice.b2b2c.atom.refund.handle;

import java.sql.Timestamp;
import java.util.List;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;

/**
 * <p>
 * 火车票保险退款回调
 * </P>
 * 
 * @author zqf
 * @time 2016年7月19日 下午2:04:38
 */
public class TrainTicketsInsurrefundhandle implements RefundHandle {

	@Override
	public void refundedHandle(boolean success, long orderid, String batchno) {
		Insurorder insurorder = Server.getInstance().getAirService()
				.findInsurorder(orderid);
		WriteLog.write("保险退款", insurorder.getOrderno() + "退款通知：" + batchno); 
		if (insurorder.getPaystatus() == 1) {
			float chajia = 0.0f;
			String where = " where c_orderid=" + insurorder.getId();
			List<Insuruser> users = Server.getInstance().getAirService()
					.findAllInsuruser(where, "", -1, 0);
			int insurcount = users.size();// 保险数量
			if (insurorder.getPaymethod() != null) {
				if (insurcount > 0) {
					chajia = (float) (insurorder.getInsurmoney() * insurcount);
				}
				
				AirticketPaymentrecord payment = new AirticketPaymentrecord();
                payment.setOrderid(insurorder.getId());
                payment.setYwtype(6);
                payment.setTradeprice(chajia);
                payment.setPaymethod(Paymentmethod.VMONEYPAY);
                payment.setStatus(2);
                payment.setTradetype(AirticketPaymentrecord.REFUNDSPREAD);
                payment.setTradetime(new Timestamp(System.currentTimeMillis()));
                payment = Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(payment);
                payment.setStatus(3);
                Server.getInstance().getB2BAirticketService().updateAirticketPaymentrecord(payment);
                insurorder.setPaystatus(2l);
                Server.getInstance().getAirService().updateInsurorderIgnoreNull(insurorder);
                
                
				String orderS = insurorder.getInsuruserid();
		        long trainorderorderid = 0;
		        if (orderS != null && !"".equals(orderS)){
		        	trainorderorderid = Long.valueOf(orderS);
		        	 createTrainorderrc(1, trainorderorderid, "网银退保险钱：" + chajia, "保险退款", 0, 0);
		        }
		       
			}
		}
	}
	
	 /**
     * 书写操作记录
     * @param yewutype
     * @param trainorderid
     * @param content
     * @param createurser
     * @param status
     * @param ticketid
     */
    public static void createTrainorderrc(int yewutype, long trainorderid, String content, String createurser,
            int status, long ticketid) { 
        try {
            Trainorderrc rc = new Trainorderrc();
            rc.setOrderid(trainorderid);
            rc.setContent(content);
            rc.setStatus(status);// Trainticket.ISSUED
            rc.setCreateuser(createurser);// "12306"
            rc.setTicketid(ticketid);
            rc.setYwtype(yewutype);
            Server.getInstance().getTrainService().createTrainorderrc(rc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
