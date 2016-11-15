package com.ccservice.b2b2c.atom.refund.helper;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.refund.handle.TrainSpreadRefundHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;

public class TrainSpreadHelper extends Refundframework implements Refundhelper {

    AirticketPaymentrecord record;

    Trainorder trainorder;

    public TrainSpreadHelper(long orderid) {
        super(orderid);
        this.record = Server.getInstance().getTrainService().findTrainSpreadrecord(orderid);
        this.trainorder = Server.getInstance().getTrainService().findTrainorderPayinfo(orderid);
    }

    @Override
    public long getOrderid() {
        return record.getId();
    }

    @Override
    public Class getProfitHandle() {
        return TrainSpreadRefundHandle.class;
    }

    @Override
    public List<Refundinfo> getRefundinfos() {
        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
        Refundinfo refundinfo = new Refundinfo();
        refundinfo.setRoyalty_parameters(null);

        refundinfo.setTradeno(trainorder.getTradeno());
        refundinfo.setRefundprice((float) record.getTradeprice());
        refundinfos.add(refundinfo);
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
        // TODO Auto-generated method stub
        return 0;
    }

}
