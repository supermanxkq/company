package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.Timestamp;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.recharge.Recharge;

public class MobilRechargeHandle implements PayHandle {

    Log log = LogFactory.getLog(MobilRechargeHandle.class);

    private static final long serialVersionUID = 1L;

    int step = 0;

    @Override
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail) {
        String sql = "UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='" + new Timestamp(System.currentTimeMillis())
                + "' WHERE C_ORDERCODE='" + ordernumber + "'";
        Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        List<Recharge> rechargeList = Server.getInstance().getMemberService()
                .findAllRecharge(" WHERE 1=1 and " + Recharge.COL_ordernumber + "='" + ordernumber + "'", "", -1, 0);
        if (rechargeList != null && rechargeList.size() > 0) {
            Recharge recharge = rechargeList.get(0);
            recharge.setPayordermethod(1);//支付方式  1网银
            recharge.setPaystate(1);//支付成功
            recharge.setTradeno(tradeno);//交易号
            Server.getInstance().getMemberService().updateRecharge(recharge);

            recharge = Server.getInstance().getMemberService().findRecharge(recharge.getId());
            if (recharge.getState() == 11) {
                if (recharge.getOrdernumber().contains("P")) {//对手机充值处理
                    String result = Server
                            .getInstance()
                            .getAtomService()
                            .directFill(recharge.getProductid().trim(), recharge.getOrdernumber().trim(),
                                    recharge.getPhonenumber());
                    if (result != null && !result.equals("FAIL")) {
                        JSONObject resultJoson = JSONObject.fromObject(result.trim());
                        String resultnoValue = resultJoson.getString("resultnoValue");
                        if (resultnoValue != null && resultnoValue.equals("0000")) {
                            String tranidValue = resultJoson.getString("tranidValue");
                            recharge.setRefordernumber(tranidValue);
                            recharge.setState(Recharge.INRECHARGE);
                        }
                        else {
                            recharge.setState(Recharge.RECHARGEFAILURE);
                        }
                    }
                    else {
                        recharge.setState(Recharge.RECHARGEFAILURE);
                    }
                }
                else if (recharge.getOrdernumber().contains("Q")) {
                    String result = Server
                            .getInstance()
                            .getAtomService()
                            .directRecharge(recharge.getProductid(), recharge.getCardnum(),
                                    recharge.getOrdernumber().trim(), recharge.getPhonenumber(), 1,
                                    recharge.getClientip());
                    if (result != null && !result.equals("FAIL")) {
                        recharge.setRefordernumber(result);
                        recharge.setState(Recharge.INRECHARGE);
                    }
                    else {
                        recharge.setState(Recharge.RECHARGEFAILURE);
                    }
                }
                Server.getInstance().getMemberService().updateRecharge(recharge);
            }
            else {
                WriteLog.write("手机充值", "非可充值状态：" + recharge.getState() + ",订单号：" + ordernumber + ",交易号：" + tradeno);
                //recharge.setState(Recharge.RECHARGEFAILURE);
                //Server.getInstance().getMemberService().updateRecharge(recharge);
            }
        }
    }

    public static void main(String[] args) {
        new MobilRechargeHandle().orderHandle("P15010111084036741", "2015010100001000630047910406", 197f, 0, "");
    }
}
