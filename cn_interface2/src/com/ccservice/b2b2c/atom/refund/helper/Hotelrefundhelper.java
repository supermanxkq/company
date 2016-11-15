package com.ccservice.b2b2c.atom.refund.helper;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.refund.handle.HotelrefundNotifyHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;

/**
 * 酒店退款
 * 
 * @author wzc
 * 
 */
public class Hotelrefundhelper extends Refundframework implements Refundhelper {

    private AirticketPaymentrecord pricechange;

    private Hotelorder hotelorder;

    public Hotelrefundhelper(long orderid) {
        super(orderid);
        hotelorder = (Hotelorder) Server.getInstance().getHotelService()
                .findAllHotelorder("where ID=" + orderid, "", -1, 0).get(0);
        String sql = "select * from T_AIRTICKETPAYMENTRECORD where C_YWTYPE=2 and C_ORDERID=" + hotelorder.getId()
                + " and C_STATUS=2 order by id desc";
        List<AirticketPaymentrecord> records = Server.getInstance().getB2BSystemService()
                .findAllPaymentrecordBySql(sql);
        if (records.size() > 0) {
            pricechange = records.get(0);
        }
        else {
            pricechange = new AirticketPaymentrecord();
            pricechange.setTradeprice(0.0f);
        }
    }

    @Override
    public long getOrderid() {
        // TODO Auto-generated method stub
        return hotelorder.getId();
    }

    /**
     * 退款成功处理类
     */
    @Override
    public Class getProfitHandle() {
        // TODO Auto-generated method stub
        return HotelrefundNotifyHandle.class;
    }

    public Hotelorder getHotelorder() {
        return hotelorder;
    }

    public void setHotelorder(Hotelorder hotelorder) {
        this.hotelorder = hotelorder;
    }

    public AirticketPaymentrecord getPricechange() {
        return pricechange;
    }

    public void setPricechange(AirticketPaymentrecord pricechange) {
        this.pricechange = pricechange;
    }

    @Override
    public List<Refundinfo> getRefundinfos() {
        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
        Refundinfo refundinfo = new Refundinfo();
        refundinfo.setRefundprice((float) pricechange.getTradeprice());
        refundinfo.setRoyalty_parameters(null);
        refundinfo.setTradeno(hotelorder.getTradenum());
        refundinfos.add(refundinfo);
        return refundinfos;
    }

    @Override
    public String getOldOrdId() {
        return hotelorder.getOrderid();
    }

    @Override
    public String getOrdernumber() {
        return hotelorder.getOrderid();
    }

    @Override
    public int getTradetype() {
        return 2;
    }

    @Override
    public long getAgentId() {
        // TODO Auto-generated method stub
        return 0;
    }

}
