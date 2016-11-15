package com.ccservice.b2b2c.atom.refund.helper;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.refund.handle.TrainTicketsInsurrefundhandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;
import com.ccservice.b2b2c.base.train.Trainorder;

/**
 * 火车票保险退款助手
 * 
 * @author zqf
 * @time 2016年7月19日 下午2:02:41
 */
public class TrainTicketsInsurrefundhelper extends Refundframework implements
		Refundhelper {
	private Insurorder insurorder = null;

	private Trainorder trainorder = null;
	
	public TrainTicketsInsurrefundhelper(long insurid) {
		super(insurid);
		this.insurorder = Server.getInstance().getAirService()
				.findInsurorder(insurid);
		String orderS = insurorder.getInsuruserid();
        long orderid = 0;
        if (orderS != null && !"".equals(orderS)){
            orderid = Long.valueOf(orderS);
        }
        trainorder =Server.getInstance().getTrainService().findTrainorder(orderid);
	}

	@Override
	public long getOrderid() {
		// TODO Auto-generated method stub
		return this.insurorder.getId();
	}
 
	@Override
	public String getOldOrdId() {
		// TODO Auto-generated method stub
		return this.insurorder.getOrderno();
	}

	@Override
	public List<Refundinfo> getRefundinfos() {
		List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
		float chajia = 0.0f;
		if (insurorder.getStatus() == 1 || insurorder.getStatus() == 2
				|| insurorder.getStatus() == 3) {
			if (insurorder.getPaystatus() == 1) {
				String where = " where c_orderid=" + insurorder.getId();
				List<Insuruser> users = Server.getInstance().getAirService().findAllInsuruser(where, "", -1, 0);
				int insurcount = users.size();// 保险数量
				if (insurorder.getPaymethod() != null) {
					if (insurcount > 0) {
						chajia = (float) (insurorder.getInsurmoney() * insurcount);
					}
					synchronized (this) {
						Refundinfo refundinfo = new Refundinfo();
						refundinfo.setRoyalty_parameters(null);
						refundinfo.setTradeno(trainorder.getTradeno());
						refundinfo.setRefundprice(chajia);
						refundinfos.add(refundinfo);
					}
					
				}
			}
		}
		return refundinfos;
	}

	@Override
	public Class getProfitHandle() {
		return TrainTicketsInsurrefundhandle.class;
	}

	@Override
	public String getOrdernumber() {
		// TODO Auto-generated method stub
		return this.insurorder.getOrderno();
	}

	@Override
	public int getTradetype() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public long getAgentId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
