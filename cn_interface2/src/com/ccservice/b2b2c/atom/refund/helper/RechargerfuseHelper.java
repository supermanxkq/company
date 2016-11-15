package com.ccservice.b2b2c.atom.refund.helper;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.ccservice.b2b2c.atom.refund.handle.RechargeorderfuseHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.recharge.Recharge;

/**
 * 充值业务 退款辅助类
 * @author Administrator
 *
 */
public class RechargerfuseHelper extends Refundframework implements Refundhelper {

    Recharge recharge;

    private int orderstate;

    public RechargerfuseHelper(long orderid) {
        super(orderid);
        recharge = Server.getInstance().getMemberService().findRecharge(orderid);
        orderstate = recharge.getState();
    }

    @Override
    public String getOldOrdId() {
        return this.recharge.getOrdernumber();
    }

    @Override
    public long getOrderid() {
        return this.recharge.getId();
    }

    @Override
    public String getOrdernumber() {
        return this.recharge.getOrdernumber();
    }

    @Override
    public Class getProfitHandle() {
        return RechargeorderfuseHandle.class;
    }

    @Override
    public List<Refundinfo> getRefundinfos() {
        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
        Refundinfo refundinfo = new Refundinfo();
        refundinfo.setRoyalty_parameters(null);
        refundinfo.setTradeno(recharge.getTradeno());
        if (orderstate == 9) {//全额退款
            refundinfo.setRefundprice(recharge.getRechmoney());
        }
        else if (orderstate == 15) {//部分退款
            DecimalFormat df = new DecimalFormat("#.00");
            refundinfo.setRefundprice(Float.valueOf(df.format(recharge.getRechmoney()
                    - recharge.getActualrechargeamount())));
        }
        else {
            refundinfo.setRefundprice(0);
        }
        refundinfos.add(refundinfo);
        return refundinfos;
    }

    @Override
    public int getTradetype() {
        return 4;
    }

    @Override
    public long getAgentId() {
        // TODO Auto-generated method stub
        return 0;
    }

}
