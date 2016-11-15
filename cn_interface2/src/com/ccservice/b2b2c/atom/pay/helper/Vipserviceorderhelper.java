package com.ccservice.b2b2c.atom.pay.helper;

import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.pay.handle.Vipserviceorderhandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.AirservicecardBean;
import com.ccservice.b2b2c.ben.Vipserviceorder;

public class Vipserviceorderhelper extends Payhelperframework implements Payhelper {

    private List<Vipserviceorder> vipserviceorder;

    private List<AirservicecardBean> airservicecard;

    private String ordernumber = "";

    private String totalprice = "";

    public Vipserviceorderhelper(long orderid) {
        super(orderid);
        String sql = "select c_ordernumber,c_cardid from t_vipserviceorder where id = " + orderid;
        vipserviceorder = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        Map map = (Map) vipserviceorder.get(0);
        Integer cardid = (Integer) map.get("c_cardid");
        ordernumber = (String) map.get("c_ordernumber");
        String sql1 = "select C_SERVICEPRICE from T_AIRSERVICECARD where id = " + cardid;
        airservicecard = Server.getInstance().getSystemService().findMapResultBySql(sql1, null);
        Map map1 = (Map) airservicecard.get(0);
        totalprice = (String) map1.get("C_SERVICEPRICE");
    }

    @Override
    public String getHandleName() {
        // TODO Auto-generated method stub
        return Vipserviceorderhandle.class.getSimpleName();
    }

    @Override
    public String getOrderDescription() {
        // TODO Auto-generated method stub
        return "贵宾服务支付";
    }

    @Override
    public String getOrdername() {
        // TODO Auto-generated method stub
        return "贵宾服务支付";
    }

    @Override
    public String getOrdernumber() {
        // TODO Auto-generated method stub
        return ordernumber;
    }

    @Override
    public double getOrderprice() {
        return Float.parseFloat(totalprice);
    }

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

    public List<Vipserviceorder> getVipserviceorder() {
        return vipserviceorder;
    }

    public void setVipserviceorder(List<Vipserviceorder> vipserviceorder) {
        this.vipserviceorder = vipserviceorder;
    }

    public void setOrdernumber(String ordernumber) {
        this.ordernumber = ordernumber;
    }

    @Override
    public String getTradeno() {
        // TODO Auto-generated method stub
        return null;
    }

}
