package com.ccservice.b2b2c.atom.pay.gp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;

/**
 * Gp确认出票
 * @author wzc
 *
 */
public class GpConfimOrder extends AirSupper {
    public static void main(String[] args) {
        JSONObject msg = new JSONObject();
        msg.put("orderid", 1411);
        System.out.println(new GpConfimOrder().ConfirmOrder(msg, 1));
    }

    /**虚拟支付确认出票
     * @return
     */
    public String ConfirmOrder(JSONObject msg, int ind) {
        long orderid = msg.getLongValue("orderid");
        String OrderNumber = msg.getString("OrderNumber");//订单号
        if (orderid <= 0) {
            String sql = "select Id from T_ORDERINFO with(nolock) WHERE C_ORDERNUMBER='" + OrderNumber + "'";
            List<Map> list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                orderid = Long.parseLong(map.get("Id").toString());
            }
        }
        JSONObject returnObj = new JSONObject();
        String returnnote = "";
        String returnMsg = "1,";
        String sql = "select OId,AgentId,Account,Money,status,Btype from GpShareProfit with(nolock) where Oid="
                + orderid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        List<GpShareProfit> gplist = new ArrayList<GpShareProfit>();
        if (list.size() <= 0) {
            Orderinfo orderinfo = Server.getInstance().getAirService().findOrderinfo(orderid);
            if (orderinfo != null) {
                //机票支付价格
                Double supplymoney = orderinfo.getTotalticketprice() + orderinfo.getTotalairportfee()
                        + orderinfo.getTotalfuelfee() + orderinfo.getGpSupplyServMoney()
                        + orderinfo.getGpSupplyInsurServMoney();//+当时分给供应商的服务费+当时分给供应商的保险费
                String supplynote = "供应服务费:" + orderinfo.getGpSupplyServMoney() + ",保险服务费:"
                        + orderinfo.getGpSupplyInsurServMoney();
                WriteLog.write("GpPay虚拟确认出票", ind + ":ordernumber:" + orderinfo.getOrdernumber() + ":" + supplynote);
                GpShareProfit supplyprofit = new GpShareProfit();
                supplyprofit.setMoney(supplymoney);
                supplyprofit.setAgentId(orderinfo.getPolicyagentid());
                supplyprofit.setOId(orderid);
                supplyprofit.setStatus(0);
                supplyprofit.setPayMethod(4l);
                supplyprofit.setAccount(findSupplyAcount(orderinfo.getPolicyagentid()));
                supplyprofit.setBtype(1);
                supplyprofit.setNote(supplynote);
                gplist.add(supplyprofit);
                try {
                    String insertsql = "select 1;";
                    for (GpShareProfit sp : gplist) {
                        if (sp.getMoney() > 0) {
                            insertsql += "INSERT INTO dbo.GpShareProfit(OId,AgentId,Account,Money,Note,status,Btype) select "
                                    + sp.getOId()
                                    + ","
                                    + sp.getAgentId()
                                    + ",'"
                                    + sp.getAccount()
                                    + "',"
                                    + sp.getMoney()
                                    + ",'"
                                    + sp.getNote()
                                    + "',0,"
                                    + sp.getBtype()
                                    + " WHERE NOT EXISTS (SELECT 1 FROM dbo.GpShareProfit WHERE OId="
                                    + sp.getOId()
                                    + " AND AgentId=" + sp.getAgentId() + ");";
                            WriteLog.write("GpPay虚拟确认出票", ind + ":ordernumber:" + orderinfo.getOrdernumber() + ":"
                                    + insertsql);
                        }
                    }
                    if ("1,".equals(returnMsg)) {
                        try {
                            Server.getInstance().getSystemService().findMapResultBySql(insertsql, null);
                            returnObj.put("success", true);
                            returnObj.put("msg", "新增记录");
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            returnObj.put("success", false);
                            returnObj.put("msg", "插入gp分润记录异常");
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                returnObj.put("success", false);
                returnObj.put("msg", "未查询到订单");
            }
        }
        else {
            returnObj.put("success", true);
            returnObj.put("msg", "已经存在分润记录");
        }
        return returnObj.toJSONString();
    }
}
