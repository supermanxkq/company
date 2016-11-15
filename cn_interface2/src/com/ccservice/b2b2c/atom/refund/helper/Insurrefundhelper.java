package com.ccservice.b2b2c.atom.refund.helper;

import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.refund.handle.Insurrefundhandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.insurorder.Insurorder;
import com.ccservice.b2b2c.base.insuruser.Insuruser;

public class Insurrefundhelper extends Refundframework implements Refundhelper {
    private Insurorder insurorder = null;

    public Insurrefundhelper(long orderid) {
        super(orderid);
        this.insurorder = Server.getInstance().getAirService().findInsurorder(orderid);
    }

    @Override
    public long getOrderid() {
        // TODO Auto-generated method stub
        return this.insurorder.getId();
    }

    @Override
    public String getOldOrdId() {
        // TODO Auto-generated method stub
        return this.insurorder.getOrderno();
    }

    @Override
    public List<Refundinfo> getRefundinfos() {
        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
        float chajia = 0.0f;
        if (insurorder.getStatus() == 1 || insurorder.getStatus() == 2 || insurorder.getStatus() == 3) {
            if (insurorder.getPaystatus() == 1) {
                String where = " where c_orderid=" + insurorder.getId();
                List<Insuruser> users = Server.getInstance().getAirService().findAllInsuruser(where, "", -1, 0);
                int insurcount = users.size();//保险数量
                int failcount = 0;
                for (Insuruser insuruser : users) {
                    if (insuruser.getPolicyno() == null) {
                        failcount++;
                    }
                }
                if (insurorder.getPaymethod() != null && insurcount > 0 && failcount > 0) {
                    synchronized (this) {
                        if (failcount > 0) {
                            chajia = (float) (insurorder.getTotalmoney() / insurcount) * failcount;
                        }
                        Refundinfo refundinfo = new Refundinfo();
                        refundinfo.setRoyalty_parameters(null);
                        refundinfo.setTradeno(insurorder.getLiushuino());
                        refundinfo.setRefundprice(chajia);
                        refundinfos.add(refundinfo);
                    }
                }
            }
        }
        return refundinfos;
    }

    @Override
    public Class getProfitHandle() {
        return Insurrefundhandle.class;
    }

    @Override
    public String getOrdernumber() {
        // TODO Auto-generated method stub
        return this.insurorder.getOrderno();
    }

    @Override
    public int getTradetype() {
        // TODO Auto-generated method stub
        return 6;
    }

    @Override
    public long getAgentId() {
        // TODO Auto-generated method stub
        return 0;
    }

}
