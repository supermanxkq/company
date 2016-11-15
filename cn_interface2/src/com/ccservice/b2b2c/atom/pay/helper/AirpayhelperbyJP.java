package com.ccservice.b2b2c.atom.pay.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.pay.handle.AirnofiryHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.segmentinfo.Segmentinfo;
import com.ccservice.b2b2c.base.service.IAirService;

public class AirpayhelperbyJP extends Payhelperframework implements Payhelper {

    private int pcount;

    public AirpayhelperbyJP(long orderid) throws Exception {
        super(orderid);
        orderinfo = this.getAirservice().findOrderinfo(orderid);
        pcount = Server.getInstance().getAirService()
                .countPassengerBySql("SELECT COUNT(ID) FROM T_PASSENGER WHERE C_ORDERID=" + this.orderinfo.getId());
    }

    private IAirService service;

    private Orderinfo orderinfo;

    private List<Segmentinfo> segmentinfos;

    private String ordername = null;

    @Override
    public String getOrderDescription() {
        return "机票信息：" + this.getOrdername();
    }

    @Override
    public String getOrdername() {
        if (ordername == null) {
            StringBuilder sb = new StringBuilder("");
            List<Segmentinfo> segments = this.getSegmentinfos();
            for (Segmentinfo sinfo : segments) {
                sb.append(formatTimestamptoMinute(sinfo.getDeparttime()) + " " + sinfo.getAirname()
                        + sinfo.getFlightnumber() + " " + sinfo.getCabincode() + "舱 " + sinfo.getStartairportname()
                        + "--" + sinfo.getEndairportname() + " ");
            }

            int count = Server.getInstance().getAirService()
                    .countPassengerBySql("SELECT COUNT(ID) FROM T_PASSENGER WHERE C_ORDERID=" + this.orderinfo.getId());
            sb.append(count + "张");
            return ordername = sb.toString();
        }
        else {
            return ordername;
        }
    }

    @Override
    public String getOrdernumber() {
        return this.orderinfo.getOrdernumber();
    }

    @Override
    public double getOrderprice() {
        float money = 0f;
        if (orderinfo.getTotalinsurprice() == null) {
            orderinfo.setTotalinsurprice(0f);
        }

        money += orderinfo.getTotalticketprice() + orderinfo.getTotalairportfee() + orderinfo.getTotalfuelfee()
                + orderinfo.getTotalinsurprice()
                + (orderinfo.getTotalgeneralprice() == 0 ? 0 : orderinfo.getTotalgeneralprice())
                + (orderinfo.getTotalbusinessprice() == 0 ? 0 : orderinfo.getTotalbusinessprice());
        String alipayrate = "0";
        try {
            //支付宝手续费
            alipayrate = Server.getInstance().getMemberService().findSysconfig(26).getValue();
        }
        catch (Exception e) {
            alipayrate = "0";
        }
        money += (int) (money * Float.parseFloat(alipayrate)) + 1;
        Float platfee = orderinfo.getCurrplatfee();
        if (platfee != null) {
            money += platfee;
        }
        return money;
    }

    private IAirService getAirservice() {
        if (this.service != null) {
            return this.service;
        }
        else {
            return service = Server.getInstance().getAirService();
        }
    }

    public List<Segmentinfo> getSegmentinfos() {
        if (segmentinfos == null) {
            String id = "-1";
            String sql = "SELECT  AIRNAME AS airname,STARTAIRPOR as startairportname,ENDAIRPORT as endairportname, C_DEPARTTIME as departtime,"
                    + " C_CABINCODE as cabincode,C_FLIGHTNUMBER as flightnumber "
                    + "FROM view_segmentinfo WHERE C_ORDERID = " + orderinfo.getId() + "";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            segmentinfos = new ArrayList<Segmentinfo>();
            if (list.size() > 0) {
                for (Object obj : list) {
                    Map m = (Map) obj;
                    try {
                        Segmentinfo segment = this.setFiledfrommap(Segmentinfo.class, m);
                        segmentinfos.add(segment);
                    }
                    catch (Exception e) {
                        System.out.println("从Map向Segmentinfo赋值出错：");
                        e.printStackTrace();
                    }
                }
            }
        }
        return segmentinfos;

    }

    @Override
    public int getTradetype() {

        return 1;
    }

    @Override
    public String getShwourl() {
        return "";
    }

    @Override
    public String getHandleName() {
        return AirnofiryHandle.class.getSimpleName();
    }

    @Override
    public String getTradeno() {
        return this.orderinfo.getTradeno();
    }

}
