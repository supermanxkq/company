package com.ccservice.b2b2c.atom.pay.gp;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;

import com.ccservice.b2b2c.atom.pay.PayEntryInfo;
import com.ccservice.b2b2c.atom.server.Server;

public class AirSupper {

    public double formatMoneyDoublePos(Double money) {
        format.applyPattern("###0.0");
        try {
            String result = format.format(money);
            return Double.parseDouble(result);
        }
        catch (Exception e) {
            if (money != null) {
                return money;
            }
            else {
                return 0d;
            }
        }
    }

    public double formatMoneyDouble(Double money) {
        format.applyPattern("###0.00");
        try {
            String result = format.format(money);
            return Double.parseDouble(result);
        }
        catch (Exception e) {
            if (money != null) {
                return money;
            }
            else {
                return 0d;
            }
        }
    }

    /**
     * 获取支付信息
     * @return
     */
    public PayEntryInfo findAgentInfo(String agentid, int type) {
        PayEntryInfo info = null;
        if (!"".equals(agentid)) {
            String sql = "SELECT  PId,KeyStr,SellEmail,AgentId,PrivateKey,PublicKey,ISNULL(CompayName,'') CompayName FROM  GPPayInfo  with(nolock) where 1=1 ";
            if (type != 2) {
                sql += "and AccountType=" + type + " and AgentId=" + agentid;
            }
            else {
                sql += "and AccountType=" + type + " and AgentId=46";
            }
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() == 1) {
                info = new PayEntryInfo();
                Map map = (Map) list.get(0);
                String pid = map.get("PId") == null ? "" : map.get("PId").toString();
                String KeyStr = map.get("KeyStr") == null ? "" : map.get("KeyStr").toString();
                String AgentId = map.get("AgentId") == null ? "0" : map.get("AgentId").toString();
                String PrivateKey = map.get("PrivateKey") == null ? "" : map.get("PrivateKey").toString();
                String PublicKey = map.get("PublicKey") == null ? "" : map.get("PublicKey").toString();
                info.setSellemail(map.get("SellEmail") == null ? "" : map.get("SellEmail").toString());
                info.setCompayName(map.get("CompayName") == null ? "" : map.get("CompayName").toString());
                info.setPid(pid);
                info.setKey(KeyStr);
                info.setPrivateKey(PrivateKey);
                info.setPublicKey(PublicKey);
                info.setAgentid(Long.parseLong(AgentId));
            }
        }
        return info;
    }

    /**
     * 查询银行组织代码
     * @param code
     * @return
     */
    public String findBankCode(String code) {
        String paycode = "";
        if (code != null && code.length() > 0) {
            String sql = "SELECT PayCode FROM dbo.GpBankCode with(nolock) WHERE code='" + code
                    + "' AND PayCode IS NOT NULL";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            String account = "";
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                paycode = map.get("PayCode") == null ? "" : map.get("PayCode").toString();
            }
        }
        return paycode;
    }

    /**
     * 查询供应商块钱帐号
     * @param agentid
     * @return
     */
    public String findPlatAcount(long agentid) {
        String sql = "select SellEmail from GPPayInfo with(nolock) where AgentId=" + agentid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        String account = "";
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            account = map.get("SellEmail") == null ? "" : map.get("SellEmail").toString();
        }
        return account;
    }

    /**
     * 查询供应商块钱帐号
     * @param agentid
     * @return
     */
    public String findSupplyAcount(long agentid) {
        String sql = "select BillAccount from GpAgentInfo with(nolock) where AgentId=" + agentid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        String account = "";
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            account = map.get("BillAccount") == null ? "" : map.get("BillAccount").toString();
        }
        return account;
    }

    DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();

    public String formatMoney(Double money) {
        format.applyPattern("###0.00");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Double.toString(money);
            }
            else {
                return "0";
            }
        }
    }

    public String formatMoneyInt(Double money) {
        format.applyPattern("###0");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Double.toString(money);
            }
            else {
                return "0";
            }
        }
    }
}
