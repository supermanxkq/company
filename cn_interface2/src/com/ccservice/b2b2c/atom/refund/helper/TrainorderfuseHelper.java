package com.ccservice.b2b2c.atom.refund.helper;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.refund.handle.TrainorderfuseHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;

/**
 * 火车票拒单退款辅助类
 * @author Administrator
 *
 */
public class TrainorderfuseHelper extends Refundframework implements Refundhelper {

    Trainorder trainorder;

    public TrainorderfuseHelper(long orderid) {
        super(orderid);
        this.trainorder = Server.getInstance().getTrainService().findTrainorder(orderid);
    }

    @Override
    public long getOrderid() {
        return trainorder.getId();
    }

    @Override
    public Class getProfitHandle() {
        return TrainorderfuseHandle.class;
    }

    @Override
    public List<Refundinfo> getRefundinfos() {
        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
        Refundinfo refundinfo = new Refundinfo();
        refundinfo.setRoyalty_parameters(null);
        refundinfo.setTradeno(trainorder.getTradeno());
        refundinfo.setRefundprice(trainorder.getOrderprice());
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
        return this.trainorder.getAgentid();
    }

}
