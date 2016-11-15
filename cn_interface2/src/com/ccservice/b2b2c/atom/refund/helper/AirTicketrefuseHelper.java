package com.ccservice.b2b2c.atom.refund.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.refund.handle.AirTicketRefuseHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.service.Profitshare;
import com.ccservice.b2b2c.base.service.Profitshare.Profitstate;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.pay.config.AlipayConfig;

public class AirTicketrefuseHelper extends Refundframework implements Refundhelper {

    Orderinfo orderinfo;

    public AirTicketrefuseHelper(long orderid) {
        super(orderid);
        this.orderinfo = Server.getInstance().getAirService().findOrderinfo(orderid);
    }

    @Override
    public long getOrderid() {
        return orderinfo.getId();
    }

    @Override
    public Class getProfitHandle() {
        return AirTicketRefuseHandle.class;
    }

    @Override
    public List<Refundinfo> getRefundinfos() {
        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
        List<AirticketPaymentrecord> subsidys = Server.getInstance().getB2BAirticketService()
                .findAllSubidyedByOid(this.orderinfo.getId());
        if (subsidys != null && subsidys.size() > 0) {// 所有支付金额
            for (AirticketPaymentrecord subsidy : subsidys) {
                Refundinfo refundinfo = new Refundinfo();
                refundinfo.setRoyalty_parameters(null);
                if (subsidy.getTradetype() == AirticketPaymentrecord.USUAL) {
                    refundinfo.setRoyalty_parameters(getRoyalty_parameters());
                }
                refundinfo.setTradeno(subsidy.getTradeno());
                refundinfo.setRefundprice((float) subsidy.getTradeprice());
                refundinfos.add(refundinfo);
            }
        }
        return refundinfos;
    }

    /**
     * (non-Javadoc) *
     * 
     * @see com.ccservice.b2b2c.atom.pay.helper.Payhelper#getRoyalty_parameters() *
     *      代码格式： 收款方Email_1^金额1^备注1|收款方Email_1^收款方Email_2^金额2^备注2 功能效果：
     *      买家付出了交易金额100元，同时刻，金额1给了收款方Email_1，收款方Email_1把获得的金额中的金额2给了收款方Email_2，
     *      seller_email获得剩下的金额（剩下的金额可以为0块钱）。 金额计算规则： 买家交易金额=金额1
     *      +seller_email收款金额+支付宝手续费 收款方Email_1实际获得金额=金额1-金额2（金额1必须大于等于金额2）
     *      收款方Email_2实际获得金额=金额2 seller_email收款金额实际获得金额=买家交易金额-金额1-支付宝手续费
     */
    public Map<String, Float> getRoyalty_parameters() {
        List<Profitshare> rebates = Server.getInstance().getB2BSystemService()
                .findProfitShareByOid(orderinfo.getId(), 1);

        Map<String, Float> royalty = new HashMap<String, Float>();
        if (rebates != null && rebates.size() > 0) {
            String psql = "SELECT COUNT(*) PCOUNT  FROM T_PASSENGER  WHERE C_ORDERID=" + orderinfo.getId();
            List list = Server.getInstance().getSystemService().findMapResultBySql(psql, null);
            Map m = (Map) list.get(0);
            int pnum = Integer.valueOf(m.get("PCOUNT").toString());
            Map<Long, float[]> agentroya = Server.getInstance().getB2BAirticketService()
                    .getAgentlevelrebate(orderinfo.getId(), pnum, 0);
            for (Profitshare rebate : rebates) {
                if (rebate.getStatus() == Profitstate.NOSHARE)
                    continue;

                if (rebate.getPagentid() == 46 && AlipayConfig.getInstance().selfaccount) {
                    continue;//运营自己无需退回。
                }
                Iterator<Map.Entry<Long, float[]>> iterator = agentroya.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Long, float[]> entry = iterator.next();
                    if (entry.getKey() == rebate.getPagentid()) {
                        royalty.put(rebate.getAccount(), entry.getValue()[0]);
                    }
                }
            }
        }
        return royalty;
    }

    @Override
    public String getOldOrdId() {
        return this.orderinfo.getOrdernumber();
    }

    @Override
    public String getOrdernumber() {
        return this.orderinfo.getOrdernumber();
    }

    @Override
    public int getTradetype() {
        return 1;
    }

    @Override
    public long getAgentId() {
        // TODO Auto-generated method stub
        return 0;
    }

}
