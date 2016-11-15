package com.ccservice.b2b2c.atom.pay.helper;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.carorder.Carorder;

public class CarpayHelper extends Payhelperframework implements Payhelper {

    private Carorder carorder;

    public CarpayHelper(long orderid) {
        super(orderid);
        carorder = Server.getInstance().getCarService().findCarorder(orderid);
    }

    private String ordername = null;

    public void setOrdername(String ordername) {
        this.ordername = ordername;
        System.out.println("订单名称:" + ordername);
    }

    @Override
    public String getHandleName() {
        // TODO Auto-generated method stub
        return CarpayHelper.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {
        // TODO Auto-generated method stub
        return "租车订单信息:" + this.getOrdername();
    }

    @Override
    public String getOrdername() {
        // TODO Auto-generated method stub
        if (ordername == null) {

            StringBuilder sb = new StringBuilder();
            sb.append("订单CODE:" + carorder.getCode() + ",");
            sb.append("汽车名称:" + carorder.getCarname() + ",");
            //			sb.append("取车城市:"+getCityNameByCarcode(carorder.getScityid()+"")+",");
            //			sb.append("取车门店:"+getcarstoreAddresByStorecode(carorder.getScarstoreid()+"")+",");
            //			sb.append("还车城市:"+getCityNameByCarcode(carorder.getEndcityid()+"")+",");
            //			sb.append("还车门店:"+getcarstoreAddresByStorecode(carorder.getEcarstoreid()+"")+",");
            sb.append("租车天数:" + carorder.getManyday() + ",");
            sb.append("支付总金额:" + carorder.getPrice() + "!");
            return ordername = sb.toString();
        }
        else {
            return ordername;
        }
    }

    @Override
    public String getOrdernumber() {
        // TODO Auto-generated method stub
        return carorder.getCode();
    }

    @Override
    public double getOrderprice() {
        // TODO Auto-generated method stub
        ///System.out.println("价格:"+Server.getInstance().getCarService().findCarorder(this.orderid).getPrice());
        return (float) Double.parseDouble(carorder.getPrice());
    }

    /**
     * 点击的时候,可以显示产品的详细信息
     */
    @Override
    public String getShwourl() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTradetype() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * 租车根据code找的城市名称
     */
    public String getCityNameByCarcode(String Carcode) {
        return Server.getInstance().getHotelService().findCitybyCarCode(Carcode).getName();
    }

    /**
     * 租车 找门店
     * @param storecode
     * @return
     */
    public String getcarstoreAddresByStorecode(String storecode) {
        return Server.getInstance().getCarService().findcarStoreByStorecode(storecode).getAbbrname();
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

}
