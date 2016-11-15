package com.ccservice.b2b2c.atom.refund.helper;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.refund.handle.OrderchangerefuseRefundHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Orderchange;

public class Orderchangerefusehelper extends Refundframework implements Refundhelper {
    private Orderchange change = null;

    public Orderchangerefusehelper(long orderid) {
        super(orderid);
        this.change = Server.getInstance().getB2BAirticketService().findOrderchange(orderid);
    }

    @Override
    public long getOrderid() {
        return this.change.getId();
    }

    @Override
    public String getOldOrdId() {
        return this.change.getChangenumber();
    }

    @Override
    public List<Refundinfo> getRefundinfos() {
        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
        List<AirticketPaymentrecord> records = Server.getInstance().getB2BSystemService()
                .findAllPaymentrecordByBtype(change.getId(), 7);
        for (AirticketPaymentrecord airticketPaymentrecord : records) {
            Refundinfo refundinfo = new Refundinfo();
            refundinfo.setRoyalty_parameters(null);
            refundinfo.setTradeno(airticketPaymentrecord.getTradeno());
            refundinfo.setRefundprice((float) airticketPaymentrecord.getTradeprice());
            refundinfos.add(refundinfo);
        }
        return refundinfos;
    }

    @Override
    public Class getProfitHandle() {
        // TODO Auto-generated method stub
        return OrderchangerefuseRefundHandle.class;
    }

    @Override
    public String getOrdernumber() {
        return change.getChangenumber();
    }

    @Override
    public int getTradetype() {
        // TODO Auto-generated method stub
        return 7;
    }

    @Override
    public long getAgentId() {
        // TODO Auto-generated method stub
        return 0;
    }

}
