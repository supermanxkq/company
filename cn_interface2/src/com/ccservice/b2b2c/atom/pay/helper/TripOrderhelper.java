package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.pay.handle.TripOrderhandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.tripline.Tripline;
import com.ccservice.b2b2c.base.triporder.Triporder;

public class TripOrderhelper extends Payhelperframework implements Payhelper {

    private Triporder ordertrip;

    public TripOrderhelper(long orderid) {
        super(orderid);
        ordertrip = Server.getInstance().getTripService().findTriporder(orderid);
    }

    @Override
    public String getHandleName() {
        return TripOrderhandle.class.getSimpleName();
    }

    private String membername = null;

    @Override
    public String getOrderDescription() {
        // TODO Auto-generated method stub
        Tripline tripline = Server.getInstance().getTripService().findTripline(this.ordertrip.getTriplineid());
        String desciption = null;
        if (desciption == null) {
            StringBuilder sb = new StringBuilder("");

            sb.append("订单号为" + this.ordertrip.getId() + "，线路名称是" + tripline.getName() + "，"
                    + formatTimestamptoMinute(this.ordertrip.getStatetime()) + "日出发，行程为期" + tripline.getTripdays()
                    + "天");
            desciption = sb.toString();
        }
        return desciption;
    }

    @Override
    public String getOrdername() {
        // TODO Auto-generated method stub
        Tripline tripline = Server.getInstance().getTripService().findTripline(this.ordertrip.getTriplineid());
        if (membername == null) {
            StringBuilder sb = new StringBuilder("");
            sb.append("订单号：" + this.ordertrip.getId());
            membername = sb.toString();
        }

        return membername;
    }

    @Override
    public String getOrdernumber() {
        // TODO Auto-generated method stub
        return this.ordertrip.getCode();
    }

    @Override
    public double getOrderprice() {
        // TODO Auto-generated method stub
        return this.ordertrip.getSump();
    }

    @Override
    public String getShwourl() {
        // TODO Auto-generated method stub
        return "http://www.yeebooking.com.cn/cn_home/Orderinfo!orderdetail.jspx?id=" + this.ordertrip.getId();
    }

    @Override
    public int getTradetype() {
        // TODO Auto-generated method stub
        return 0;
    }

    public Triporder getOrdertrip() {
        return ordertrip;
    }

    public void setOrdertrip(Triporder ordertrip) {
        this.ordertrip = ordertrip;
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }
}
