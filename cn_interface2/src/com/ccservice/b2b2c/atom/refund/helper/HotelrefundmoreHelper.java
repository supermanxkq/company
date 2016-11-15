package com.ccservice.b2b2c.atom.refund.helper;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.refund.handle.HotelrefundMoreHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;

public class HotelrefundmoreHelper extends Refundframework implements Refundhelper {

    private AirticketPaymentrecord pricechange;

    public HotelrefundmoreHelper(long orderid) {
        super(orderid);
        pricechange = Server.getInstance().getB2BAirticketService().findAirticketPaymentrecord(orderid);
    }

    @Override
    public long getOrderid() {
        // TODO Auto-generated method stub
        return pricechange.getId();
    }

    @Override
    public String getOldOrdId() {
        // TODO Auto-generated method stub
        return pricechange.getId() + "";
    }

    @Override
    public List<Refundinfo> getRefundinfos() {
        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
        Refundinfo refundinfo = new Refundinfo();
        refundinfo.setRefundprice((float) pricechange.getTradeprice());
        refundinfo.setRoyalty_parameters(null);
        refundinfo.setTradeno(pricechange.getTradeno());
        refundinfos.add(refundinfo);
        return refundinfos;
    }

    @Override
    public Class getProfitHandle() {
        // TODO Auto-generated method stub
        return HotelrefundMoreHandle.class;
    }

    @Override
    public String getOrdernumber() {
        // TODO Auto-generated method stub
        return pricechange.getId() + "";
    }

    @Override
    public int getTradetype() {
        // TODO Auto-generated method stub
        return 2;
    }

    public AirticketPaymentrecord getPricechange() {
        return pricechange;
    }

    public void setPricechange(AirticketPaymentrecord pricechange) {
        this.pricechange = pricechange;
    }

    @Override
    public long getAgentId() {
        // TODO Auto-generated method stub
        return 0;
    }

}
