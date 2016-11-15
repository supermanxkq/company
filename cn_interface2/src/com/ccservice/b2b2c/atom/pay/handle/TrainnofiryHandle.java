package com.ccservice.b2b2c.atom.pay.handle;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.robticket.RobticketSupportMethod;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;

public class TrainnofiryHandle implements PayHandle {

    Log logger = LogFactory.getLog(TrainnofiryHandle.class.getSimpleName());

    @Override
    public void orderHandle(String orderidstr, String tradeno, double payprice, int paytype, String selleremail) {
        String sql1 = "SELECT ID,C_ORDERSTATUS,C_AGENTID,C_QUNARORDERNUMBER FROM T_TRAINORDER WITH(NOLOCK) WHERE  C_ORDERNUMBER = '"
                + orderidstr + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql1, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            long id = Long.valueOf(map.get("ID").toString());
            long orderstatus = Long.valueOf(map.get("C_ORDERSTATUS").toString());
            long agentid = Long.valueOf(map.get("C_AGENTID").toString());
            String interfacenumber = map.get("C_QUNARORDERNUMBER").toString();
            if (Trainorder.WAITPAY == orderstatus) {//状态拦截
                Trainorder order = new Trainorder();
                order.setId(id);
                order.setPaymethod(Paymentmethod.EBANKPAY);
                order.setTradeno(tradeno);
                if (Paymentmethod.ACEpay == paytype) {//代扣暂时改订单状态
                }
                else {
                    order.setOrderstatus(Trainorder.WAITISSUE);
                }
                //            order.setOrderprice((float) payprice);
                Server.getInstance().getTrainService().updateTrainorder(order);
                Trainorderrc rc = new Trainorderrc();
                rc.setOrderid(id);
                rc.setCreateuser("");
                int tempintisupdateywtype = 0;
                try {
                    rc.setYwtype(1);
                }
                catch (Exception e) {
                    logger.error(e);
                    tempintisupdateywtype = 1;
                }
                rc.setContent("支付完成，支付方式:" + Paymentmethod.getPaymethod(order.getPaymethod()) + ",金额：" + payprice
                        + ",交易号：" + tradeno);
                rc = Server.getInstance().getTrainService().createTrainorderrc(rc);
                train_confirm(orderidstr);//提交到接口确认出票
                if (tempintisupdateywtype == 1) {
                    String sqlstring = "UPDATE T_TRAINORDERRC SET C_YWTYPE=1 where C_ORDERID=" + rc.getOrderid()
                            + " and C_CONTENT='" + rc.getContent() + "'";
                    WriteLog.write("TrainnofiryHandle", sqlstring);
                    Server.getInstance().getSystemService().excuteSysconfigBySql(sqlstring);
                }
                String sharesql = "[dbo].[sp_rebaterecord_MoneyControl]@orderid = "
                        + id
                        + ",@ordernumber = N'"
                        + orderidstr
                        + "',@yewutye = 3,@rebateagentid = "
                        + agentid
                        + ",@rebatemoney = "
                        + -payprice
                        + ",@vmenble = 0,@rebatetype = 2,@rebatememo = N'Pos出票收款添加',@customerid = 0,@refordernumber = N'"
                        + interfacenumber + "',@paymethod = 1,@paystate = 1";
                WriteLog.write("火车票出票收款", id + "==>" + sharesql);
                try {
                    Server.getInstance().getSystemService().findMapResultByProcedure(sharesql);
                }
                catch (Exception e1) {
                    ExceptionUtil.writelogByException("火车票出票收款_Exception", e1);
                }

                AirticketPaymentrecord record = new AirticketPaymentrecord();
                record.setYwtype(3);
                record.setTradeno(tradeno);
                record.setTradeprice(payprice);
                record.setOrderid(id);
                record.setPaymethod(paytype);
                record.setStatus(1);
                record.setSelleremail(selleremail);
                record.setTradetype(AirticketPaymentrecord.USUAL);
                try {
                    Server.getInstance().getB2BAirticketService().createAirticketPaymentrecord(record);
                    Server.getInstance().getAtomService().sendTrainorderalarmsms();
                }
                catch (Exception e) {
                    logger.error(e.fillInStackTrace());
                    e.printStackTrace();
                }
                try {
                    String sql = "UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='"
                            + new Timestamp(System.currentTimeMillis()) + "' WHERE C_ORDERCODE='" + orderidstr + "'";
                    Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        else {
            WriteLog.write("TrainnofiryHandle_err", orderidstr + ":" + tradeno + ":" + payprice + ":" + paytype + ":"
                    + selleremail);
            WriteLog.write("TrainnofiryHandle_err", orderidstr + ":" + sql1);
        }
    }

    /**
     * 提交到接口确认出票
     * @param orderidstr
     * @time 2016年4月4日 上午10:46:55
     * @author chendong
     */
    private void train_confirm(String orderidstr) {

        RobticketSupportMethod rsm = new RobticketSupportMethod();
        String sql = "SELECT ID FROM T_TRAINORDER WHERE c_ordernumber='" + orderidstr + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        String ID = "0";
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            ID = map.get("ID").toString();
        }
        long id = Long.valueOf(ID);

        String isCreateInterfaceOrder = PropertyUtil.getValue("isCreateInterfaceOrder", "Train.b2b.properties");// "tianqutest";
        if ("1".equals(isCreateInterfaceOrder)) {
            boolean isRobticket = rsm.SendRobticket(id); //提交到约票接口确认出票
            if (!isRobticket) {
                String orderid = orderidstr;
                String transactionid = getQunarOrdernumberByorderNumber(orderidstr);
                String confirm_result = TrainOrderInterfaceMethod.train_confirm(orderid, transactionid);//提交到接口确认出票
                refund_Train(confirm_result);
            }
        }
    }

    /**
     * 提交接口确认出票失败 拒单
     * @param confirm_result
     * @author zyx
     */
    private void refund_Train(String confirm_result) {
        WriteLog.write("TrainnofiryHandle_refund_Train", "confirm_result" + confirm_result);
        JSONObject confirm = new JSONObject();
        boolean isRefund_Train = false;
        try {
            confirm = JSONObject.parseObject(confirm_result);

            //请求是否成功
            boolean success = confirm.containsKey("success") ? confirm.getBooleanValue("success") : true;
            String msg = confirm.containsKey("msg") ? confirm.getString("msg") : "提交确认出票失败。";
            if (!success) {
                isRefund_Train = true;
            }
            String ordernumber = confirm.containsKey("ordernumber") ? confirm.getString("ordernumber") : "";
            String orderid = confirm.containsKey("orderid") ? confirm.getString("orderid") : "";
            if (ordernumber == null || "".equals(ordernumber) || orderid == null || "".equals(orderid)) {
                isRefund_Train = false;
            }
            if (isRefund_Train) {
                String sql1 = "SELECT ID,C_ORDERSTATUS FROM T_TRAINORDER WHERE  C_ORDERNUMBER = '" + orderid + "'";
                List list = Server.getInstance().getSystemService().findMapResultBySql(sql1, null);
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    long id = Long.valueOf(map.get("ID").toString());
                    Trainorderrc rc = new Trainorderrc();
                    rc.setOrderid(id);
                    rc.setCreateuser("接口");
                    try {
                        rc.setYwtype(1);
                    }
                    catch (Exception e) {
                        WriteLog.write("TrainnofiryHandle_refund_Train", "e" + e.getMessage());
                    }
                    rc.setContent(msg);
                    rc = Server.getInstance().getTrainService().createTrainorderrc(rc);
                    String sqlstring = "UPDATE T_TRAINORDER SET C_ORDERSTATUS = " + Trainorder.REFUSENOREFUND
                            + " where C_ORDERNUMBER ='" + orderid + "'" + ";update T_TRAINTICKET set C_STATUS="
                            + Trainticket.NONISSUEDABLE
                            + " where C_TRAINPID in (select id from T_TRAINPASSENGER where C_ORDERID='" + id + "')";
                    ;
                    Server.getInstance().getSystemService().excuteSysconfigBySql(sqlstring);
                }

            }

        }
        catch (Exception e) {
            WriteLog.write("TrainnofiryHandle_refund_Train", "e" + e.getMessage());
        }

    }

    /**
     * 
     * @param orderidstr
     * @return
     * @time 2016年4月4日 上午10:50:40
     * @author chendong
     */
    private String getQunarOrdernumberByorderNumber(String orderidstr) {
        String sql = "SELECT C_QUNARORDERNUMBER FROM T_TRAINORDER WHERE c_ordernumber='" + orderidstr + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        String C_QUNARORDERNUMBER = "";
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            C_QUNARORDERNUMBER = map.get("C_QUNARORDERNUMBER").toString();
        }
        return C_QUNARORDERNUMBER;
    }

    public static void main(String[] args) {
        //        String orderidstr = "T14122716413511352";
        //        String tradeno = "2014122700001000650041193884";
        //        float payprice = 155.5f;
        //        int paytype = 1;
        //        new TrainnofiryHandle().orderHandle(orderidstr, tradeno, payprice, paytype, "");
        //        String confirm_result ="{\"changeserial\":\"\",\"ordernumber\":\"E684192621\",\"orderid\":\"T16060676A8C095084C204F6D08581010CA2319F10B\",\"code\":\"100\",\"msg\":\"出票请求失败\",\"success\":false}";
        //       new TrainnofiryHandle().refund_Train(confirm_result);
        new TrainnofiryHandle().train_confirm("T16062236DA833A06C92049F009D360BBFD4264A3F4");
    }

}
