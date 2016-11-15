package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.dnsmaintenance.Dnsmaintenance;
import com.ccservice.b2b2c.base.rebaterecord.Rebaterecord;
import com.ccservice.b2b2c.base.service.ISystemService;
import com.ccservice.component.sms.SMSTemplet;
import com.ccservice.component.sms.SMSType;

/**
 * @author Administrator
 * 虚拟账户充值
 *
 */
public class VmoneyrechargenotifyHandle implements PayHandle {

    @Override
    public void orderHandle(String ordernumberstr, String tradeno, double payprice, int paytype, String selleremail) {
        String ordernumber = ordernumberstr.substring(6);
        ISystemService service = Server.getInstance().getSystemService();
        Rebaterecord record = Server.getInstance().getMemberService().findRebaterecord(Long.valueOf(ordernumber));
        //查询出需要为代理帐号充值金额   杨荣强
        //        String sqlsString = "select C_AMOUNTOFCHARGE from t_rebaterecord where ID=" + record.getId();
        String sqlsString = "select * from CHARGEAMOUNT where REBATERECORDID=" + record.getId();
        List list = Server.getInstance().getSystemService().findMapResultBySql(sqlsString, null);
        Double amountOfCharge = 0.00;
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            amountOfCharge = map.get("AMOUNTOFCHARGE") == null ? 0.00 : Double.parseDouble(map.get("AMOUNTOFCHARGE")
                    .toString());
        }
        else {
            amountOfCharge = record.getRebatemoney();
        }
        if (record.getPaystate() != 1) {
            //                sql = "UPDATE T_CUSTOMERAGENT SET C_VMONEY=C_VMONEY+" + record.getRebatemoney() + " WHERE ID="
            //                        + record.getRebateagentid() + ";";
            String sql = "UPDATE T_CUSTOMERAGENT SET C_VMONEY=C_VMONEY+" + amountOfCharge + " WHERE ID="
                    + record.getRebateagentid() + ";";
            sql += ";UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='" + new Timestamp(System.currentTimeMillis())
                    + "' WHERE C_ORDERCODE='" + ordernumberstr + "' AND C_TYPE=6";
            service.findMapResultBySql(sql, null);
            String rsql = "UPDATE T_REBATERECORD SET C_PAYSTATE=1,C_REBATETIME='"
                    + new Timestamp(System.currentTimeMillis()) + "',C_PAYMETHOD=9,C_TRADENO='" + tradeno
                    + "',C_VMBALANCE=(SELECT C_VMONEY FROM T_CUSTOMERAGENT  WHERE ID=" + record.getRebateagentid()
                    + " ) WHERE ID=" + ordernumber;
            service.findMapResultBySql(rsql, null);
            //发送短信
            Customeragent customeragent = Server.getInstance().getMemberService()
                    .findCustomeragent(record.getRebateagentid());
            if (customeragent.getSmsmovemoney() != null && customeragent.getSmsmovemoney() == 1) {
                String agentstr = customeragent.getId() + "," + customeragent.getParentstr();
                List<Dnsmaintenance> dnses = Server
                        .getInstance()
                        .getSystemService()
                        .findAllDnsmaintenance("where c_agentid in (" + agentstr + ")", "order by c_agentid desc", -1,
                                0);
                if (dnses != null && dnses.size() > 0) {
                    Dnsmaintenance dns = dnses.get(0);
                    String smstemple = "";
                    smstemple = new SMSTemplet().getSMSTemplett(SMSType.VMONEYCHARGESUCCESS, dns);
                    //您好，财务充值获得[变动金额]充值成功.
                    // smstemple = smstemple.replace("[变动金额]", record.getRebatemoney() + "");
                    //代理帐号充值金额为应付金额扣除手续费金额
                    smstemple = smstemple.replace("[变动金额]", amountOfCharge + "");
                    Server.getInstance()
                            .getAtomService()
                            .sendSms(new String[] { "" + customeragent.getAgentphone() + "" }, smstemple,
                                    record.getId(), customeragent.getId(), dns, 0);
                }
            }

        }
        else {
            WriteLog.write("虚拟账户支付", ordernumberstr + ":" + tradeno + ":" + payprice);
        }
    }
}
