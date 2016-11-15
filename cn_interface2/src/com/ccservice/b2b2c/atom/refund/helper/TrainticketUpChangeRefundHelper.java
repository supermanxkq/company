package com.ccservice.b2b2c.atom.refund.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.refund.handle.TrainticketUpChangeRefundHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;

public class TrainticketUpChangeRefundHelper extends Refundframework implements Refundhelper {

    Trainticket trainticket;

    Trainorder trainorder;

    List<AirticketPaymentrecord> records;

    public TrainticketUpChangeRefundHelper(long orderid) {
        super(orderid);
        this.trainticket = Server.getInstance().getTrainService().findTrainticket(orderid);
        this.trainorder = Server.getInstance().getTrainService()
                .findTrainorderPayinfo(trainticket.getTrainpassenger().getOrderid());
    }

    @Override
    public long getOrderid() {
        return trainticket.getId();
    }

    @Override
    public Class getProfitHandle() {
        return TrainticketUpChangeRefundHandle.class;
    }

    @Override
    public List<Refundinfo> getRefundinfos() {
        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
        List list = getTrainRefundAlipayMethod();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                Refundinfo refundinfo = new Refundinfo();
                refundinfo.setTradeno(map.get("TradeNo").toString());
                refundinfo.setRefundprice(Float.valueOf(map.get("RefundPrice").toString()));//退得钱数
                refundinfo.setRoyalty_parameters(null);
                refundinfos.add(refundinfo);
            }
        }
        return refundinfos;
    }

    @Override
    public String getOldOrdId() {
        return this.trainorder.getOrdernumber();
    }

    @Override
    public String getOrdernumber() {
        return this.trainorder.getOrdernumber();
    }

    @Override
    public int getTradetype() {
        return 3;
    }

    @Override
    public long getAgentId() {
        return this.trainorder.getAgentid();
    }

    /**
     * 获取交易记录
     * 
     * @return
     * @time 2016年7月25日 下午2:48:28
     * @author fiend
     */
    private List getTrainRefundAlipayMethod() {
        String sql = " [sp_TrainUpChangeAlipayRefundMethod_select] @Orderid=" + this.trainorder.getId()
                + " ,@Ticketid=" + this.trainticket.getId();
        return Server.getInstance().getSystemService().findMapResultByProcedure(sql);
    }

}
