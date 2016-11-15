package com.ccservice.b2b2c.atom.pay.helper;

import java.util.List;

import com.ccservice.b2b2c.atom.pay.handle.HotelorderreturnmoreHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelorder.Hotelorder;
import com.ccservice.b2b2c.base.service.IHotelService;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;

/**
 * 
 * @author wzc
 * 酒店补款支付工具类
 *
 */
public class Hotelorderpaymorehelper extends Payhelperframework implements Payhelper {

    private Hotelorder hotelOrder;

    private IHotelService service;

    private List<AirticketPaymentrecord> records;

    public Hotelorderpaymorehelper(long orderid) {
        super(orderid);
        this.hotelOrder = this.getHotelService().findHotelorder(orderid);
        String sql = "select * from T_AIRTICKETPAYMENTRECORD where C_YWTYPE=2 and C_STATUS=0 and C_TRADETYPE=2 and C_ORDERID="
                + orderid + " order by ID asc";
        records = Server.getInstance().getB2BSystemService().findAllPaymentrecordBySql(sql);
    }

    @Override
    public String getHandleName() {
        // TODO Auto-generated method stub
        return HotelorderreturnmoreHandle.class.getSimpleName();
    }

    @Override
    public String getOrdernumber() {
        String orderid = "";
        if (records != null && records.size() > 0) {
            orderid = records.get(0).getId() + "";
        }
        return orderid + "";
    }

    @Override
    public double getOrderprice() {
        double money = 0.0f;
        if (records != null && records.size() > 0) {
            money = records.get(0).getTradeprice();
        }
        return money;
    }

    @Override
    public String getOrderDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getHotelorder().getOrderid() + "补款");
        return sb.toString();
    }

    @Override
    public String getOrdername() {
        StringBuilder sb = new StringBuilder();
        sb.append("订单号：" + hotelOrder.getOrderid());
        return sb.toString();
    }

    @Override
    public String getShwourl() {
        // TODO Auto-generated method stub
        return "";
    }

    @Override
    public int getTradetype() {
        // TODO Auto-generated method stub
        return 2;
    }

    @Override
    public String getTradeno() {
        if (records != null && records.size() > 0) {
            return records.get(0).getTradeno();
        }
        return null;
    }

    private IHotelService getHotelService() {
        if (this.service != null) {
            return this.service;
        }
        else {
            return service = Server.getInstance().getHotelService();
        }
    }

    public Hotelorder getHotelOrder() {
        return hotelOrder;
    }

    public void setHotelOrder(Hotelorder hotelOrder) {
        this.hotelOrder = hotelOrder;
    }

    public IHotelService getService() {
        return service;
    }

    public void setService(IHotelService service) {
        this.service = service;
    }

    // 获取关联订单对象
    private Hotelorder getHotelorder() {
        return this.hotelOrder;
    }

    public List<AirticketPaymentrecord> getRecords() {
        return records;
    }

    public void setRecords(List<AirticketPaymentrecord> records) {
        this.records = records;
    }

}
