package com.ccservice.b2b2c.atom.pay.gp;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.orderinforc.Orderinforc;
import com.ccservice.b2b2c.ben.Orderchange;

/**
 * 退票
 * @author wzc
 *
 */
public class BillPayTicketRefundOrder extends AirSupper {
    private Orderchange change = null;

    int pnum = 1;

    private Orderinfo orderinfo;

    /**
     * 机票退票
     * @param obj
     * @param rand
     */
    public String TicketRefund(JSONObject obj, int rand) {
        long changeId = obj.getLongValue("changeId");
        this.change = Server.getInstance().getB2BAirticketService().findOrderchange(changeId);
        orderinfo = (Orderinfo) Server.getInstance().getAirService()
                .findAllOrderinfo(
                        " WHERE ID =(SELECT C_ORDERID FROM T_PASSENGER WHERE ID=(SELECT TOP 1 C_PID FROM T_PCHANGEREF WHERE C_CHANGEID="
                                + change.getId() + "))",
                        "", -1, 0)
                .get(0);
        String refundParam = getRefund_parameters(change, orderinfo);
        String result = "false";
        try {
            String msg = new BillRefundInter().refund(orderinfo.getId(), refundParam, orderinfo.getTradeno(),
                    orderinfo.getPolicyagentid());
            JSONObject resultmsg = JSONObject.parseObject(msg);
            boolean successflag = resultmsg.getBooleanValue("success");
            result = resultmsg.getString("success");
            String batchno = resultmsg.getString("seqId");
            Orderchange change = Server.getInstance().getB2BAirticketService().findOrderchange(changeId);
            if (change.getChangestate() == 6) {
                int changestue = 4;
                String memo = "退款成功.退款批次号:" + batchno;
                if (!successflag) {
                    changestue = 12;
                    memo = "退款失败.退款批次号:" + batchno + ",请联系财务线下退款";
                }
                String sql = "UPDATE T_ORDERCHANGE SET C_CHANGESTATE=" + changestue + " WHERE ID=" + change.getId();
                WriteLog.write("Billpay退款", "退款成功：" + sql);
                Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                Orderinforc rc = new Orderinforc();
                rc.setChangeid(change.getId());
                rc.setContent(memo);
                rc.setCustomeruserid(0l);
                rc.setCreatetime(new Timestamp(System.currentTimeMillis()));
                try {
                    Server.getInstance().getAirService().createOrderinforc(rc);
                }
                catch (SQLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 创建分润记录
     * @param orderid
     * @return
     */
    public String getRefund_parameters(Orderchange change, Orderinfo orderinfo) {
        double refundprice = this.change.getRefundprice();//退款金额
        String returnMsg = "";
        double Summoney = 0;//票面价+机建费+燃油费+保险费+服务费
        String sql = "select OId,AgentId,Account,Money,status,Btype from GpShareProfit with(nolock) where Oid="
                + orderinfo.getId();
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        List<GpShareProfit> gplist = new ArrayList<GpShareProfit>();
        if (list.size() > 0) {//已经创建不在创建
            for (int i = 0; i < list.size(); i++) {
                Map map = (Map) list.get(i);
                GpShareProfit profit = new GpShareProfit();
                profit.setMoney(map.get("Money") == null ? 0d : Double.valueOf(map.get("Money").toString()));
                profit.setOId(orderinfo.getId());
                profit.setBtype(map.get("Btype") == null ? 0 : Integer.valueOf(map.get("Btype").toString()));
                profit.setAccount(map.get("Account") == null ? "" : map.get("Account").toString());
                gplist.add(profit);
            }
            //机票支付价格
            String supplyAccount = "";
            for (GpShareProfit sp : gplist) {
                Summoney += sp.getMoney();
                if (sp.getBtype() == 1) {//供应
                    supplyAccount = sp.getAccount();
                }
            }
            refundprice = refundprice * 100;
            returnMsg = formatMoneyInt(Summoney * 100) + ",";
            if (Double.parseDouble(formatMoneyInt(refundprice)) > 0) {
                returnMsg += "" + "1^" + supplyAccount + "^" + formatMoneyInt(refundprice) + "^refund-"
                        + orderinfo.getOrdernumber();
            }
        }
        return returnMsg;
    }
}
