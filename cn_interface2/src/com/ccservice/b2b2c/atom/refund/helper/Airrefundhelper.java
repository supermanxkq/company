package com.ccservice.b2b2c.atom.refund.helper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.refund.handle.AirTicketRefundHandle;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.service.Profitshare;
import com.ccservice.b2b2c.base.service.Profitshare.Profitstate;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Orderchange;
import com.pay.config.AlipayConfig;

public class Airrefundhelper extends Refundframework implements Refundhelper {
    Log log = LogFactory.getLog(Airrefundhelper.class);

    private Orderchange change = null;

    private Orderinfo orderinfo;

    int pnum = 1;

    public Airrefundhelper(long orderid) {
        super(orderid);
        this.change = Server.getInstance().getB2BAirticketService().findOrderchange(orderid);
        String psql = " SELECT COUNT(C_PID) PCOUNT FROM (SELECT C_PID FROM T_PCHANGEREF WHERE C_CHANGEID="
                + change.getId() + " GROUP BY C_PID) as Temp";
        List list = Server.getInstance().getSystemService().findMapResultBySql(psql, null);
        Map m = (Map) list.get(0);
        pnum = Integer.valueOf(m.get("PCOUNT").toString());
        orderinfo = (Orderinfo) Server
                .getInstance()
                .getAirService()
                .findAllOrderinfo(
                        " WHERE ID =(SELECT C_ORDERID FROM T_PASSENGER WHERE ID=(SELECT TOP 1 C_PID FROM T_PCHANGEREF WHERE C_CHANGEID="
                                + change.getId() + "))", "", -1, 0).get(0);

    }

    /**
     * (non-Javadoc) *
     * 
     * @see com.ccservice.b2b2c.atom.pay.helper.Payhelper#getRoyalty_parameters()
     *      * 代码格式： 收款方Email_1^金额1^备注1|收款方Email_1^收款方Email_2^金额2^备注2 功能效果：
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
            Map<Long, float[]> agentroya = Server.getInstance().getB2BAirticketService()
                    .getAgentlevelrebate(orderinfo.getId(), pnum, 0);
            for (Profitshare rebate : rebates) {
                if (rebate.getStatus() != Profitstate.SHARED)
                    continue;//未分润则不退回				
                if (rebate.getPagentid() == 46 && AlipayConfig.getInstance().selfaccount) {
                    continue;//运营自己无需退回。
                }
                try {//如果运营平台购票不牵扯退回分润情况
                    if (orderinfo.getBuyagentid() == 46) {
                        continue;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (rebate.getPagentid() == 0) {
                    continue;
                }
                Iterator<Map.Entry<Long, float[]>> iterator = agentroya.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Long, float[]> entry = iterator.next();
                    if (entry.getKey() == rebate.getPagentid()) {
                        if (entry.getValue()[0] > 0) {
                            royalty.put(rebate.getAccount(), entry.getValue()[0]);
                            WriteLog.write("分润应退回记录", rebate.getAccount() + ":" + entry.getValue()[0]);
                        }
                    }
                }
            }
        }
        return royalty;
    }

    public float formatfloatMoney(Number money) {
        DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();
        format.applyPattern("###0.00");
        try {
            String result = format.format(money);
            return Float.valueOf(result);
        }
        catch (Exception e) {
            if (money != null) {
                return Float.valueOf(money.toString());
            }
            else {
                return 0;
            }
        }
    }

    @Override
    public long getOrderid() {
        return this.change.getId();
    }

    @Override
    public Class getProfitHandle() {
        return AirTicketRefundHandle.class;
    }

    @Override
    public List<Refundinfo> getRefundinfos() {
        List<Refundinfo> refundinfos = new ArrayList<Refundinfo>();
        float refundprice = this.change.getRefundprice();
        float totalsubprice = 0;
        List<AirticketPaymentrecord> subsidys = Server.getInstance().getB2BAirticketService()
                .findAllSubidyedByOid(this.orderinfo.getId());
        if (subsidys != null && subsidys.size() > 0) {// 支付记录
            float subprice = 0;
            for (AirticketPaymentrecord subsidy : subsidys) {
                if (subsidy.getTradetype() == AirticketPaymentrecord.USUAL) {
                    continue;
                }
                float tui = refundprice - subprice;
                subprice += subsidy.getTradeprice();
                Refundinfo refundinfo = new Refundinfo();
                refundinfo.setRoyalty_parameters(null);
                refundinfo.setTradeno(subsidy.getTradeno());
                if (subprice <= refundprice) {
                    refundinfo.setRefundprice((float) subsidy.getTradeprice());
                }
                else {
                    refundinfo.setRefundprice(tui);
                }
                refundinfos.add(refundinfo);
                totalsubprice += refundinfo.getRefundprice();
                if (subprice >= refundprice) {
                    break;
                }
            }
        }

        Refundinfo refundinfo = new Refundinfo();
        refundinfo.setRoyalty_parameters(this.getRoyalty_parameters());
        refundinfo.setTradeno(this.orderinfo.getTradeno());
        refundinfo.setRefundprice(refundprice - totalsubprice);
        if (refundinfo.getRefundprice() > 0 || refundinfo.getRoyalty_parameters() != null)
            refundinfos.add(refundinfo);
        return refundinfos;
    }

    @Override
    public String getOldOrdId() {
        return this.orderinfo.getOrdernumber();
    }

    @Override
    public String getOrdernumber() {

        return orderinfo.getOrdernumber();
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
