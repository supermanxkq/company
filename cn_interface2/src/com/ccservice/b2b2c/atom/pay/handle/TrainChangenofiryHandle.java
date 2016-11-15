package com.ccservice.b2b2c.atom.pay.handle;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TrainchangeRefundMethod;
import com.ccservice.b2b2c.atom.train.TrainOrderInterfaceInfoMethod;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.TrusteeshipUtil;
import com.ccservice.elong.inter.PropertyUtil;
import com.tenpay.util.MD5Util;

public class TrainChangenofiryHandle implements PayHandle {

    Log logger = LogFactory.getLog(TrainChangenofiryHandle.class.getSimpleName());

    @Override
    public void orderHandle(String orderidstr, String tradeno, double payprice, int paytype, String selleremail) {

        WriteLog.write("火车票改签收款", orderidstr + "==>" + tradeno + "==>" + payprice + "==>" + paytype + "==>"
                + selleremail);
        String sql1 = "SELECT tc.ID,C_TCSTATUS,C_ORDERID,C_ORDERNUMBER,C_AGENTID,C_QUNARORDERNUMBER FROM T_TRAINORDERCHANGE tc with (nolock) "
                + " join T_TRAINORDER o with (nolock) on o.ID=tc.C_ORDERID WHERE  C_TCNUMBER= '" + orderidstr + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql1, null);
        if (list.size() > 0) {
            Map map = (Map) list.get(0);
            long id = Long.valueOf(map.get("ID").toString());
            int orderstatus = Integer.valueOf(map.get("C_TCSTATUS").toString());
            long orderid = Long.valueOf(map.get("C_ORDERID").toString());
            String ordernumber = map.get("C_ORDERNUMBER").toString();
            long agentid = Long.valueOf(map.get("C_AGENTID").toString());
            String interfacenumber = map.get("C_QUNARORDERNUMBER").toString();
            if (Trainorderchange.THOUGHCHANGE == orderstatus) {//状态拦截
                String sql2 = "UPDATE T_TRAINORDERCHANGE SET C_SUPPLYTRADENO='" + tradeno
                        + "',C_SUPPLYPAYMETHOD=1,C_ISPLACEING=1 where ID=" + id;
                try {
                    Server.getInstance().getSystemService().findMapResultBySql(sql2, null);
                    WriteLog.write("火车票改签收款", orderidstr + ":" + sql2);
                }
                catch (Exception e2) {
                    WriteLog.write("火车票改签收款_Exception", orderidstr + ":" + sql2);
                    ExceptionUtil.writelogByException("火车票改签收款_Exception", e2);
                }
                Trainorderrc rc = new Trainorderrc();
                rc.setOrderid(orderid);
                rc.setCreateuser("");
                int tempintisupdateywtype = 0;
                try {
                    rc.setYwtype(1);
                }
                catch (Exception e) {
                    tempintisupdateywtype = 1;
                }
                rc.setContent("支付完成，支付方式:" + Paymentmethod.getPaymethod(paytype) + ",金额：" + payprice + ",交易号："
                        + tradeno);
                rc = Server.getInstance().getTrainService().createTrainorderrc(rc);
                if (tempintisupdateywtype == 1) {
                    String sqlstring = "UPDATE T_TRAINORDERRC SET C_YWTYPE=1 where C_ORDERID=" + rc.getOrderid()
                            + " and C_CONTENT='" + rc.getContent() + "'";
                    WriteLog.write("火车票改签收款", orderidstr + ":" + sqlstring);
                    Server.getInstance().getSystemService().excuteSysconfigBySql(sqlstring);
                }
                AirticketPaymentrecord record = new AirticketPaymentrecord();
                record.setYwtype(4);
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
                    WriteLog.write("火车票改签收款_Exception", orderidstr);
                    ExceptionUtil.writelogByException("火车票改签收款_Exception", e);
                }
                String sharesql = "[dbo].[sp_rebaterecord_MoneyControl]@orderid = "
                        + orderid
                        + ",@ordernumber = N'"
                        + ordernumber
                        + "',@yewutye = 33,@rebateagentid = "
                        + agentid
                        + ",@rebatemoney = "
                        + -payprice
                        + ",@vmenble = 0,@rebatetype = 2,@rebatememo = N'Pos改签收款添加',@customerid = 0,@refordernumber = N'"
                        + interfacenumber + "',@paymethod = 1,@paystate = 1";
                WriteLog.write("火车票改签收款", orderid + "==>" + sharesql);
                try {
                    Server.getInstance().getSystemService().findMapResultByProcedure(sharesql);
                }
                catch (Exception e1) {
                    ExceptionUtil.writelogByException("火车票改签收款添加_Exception", e1);
                }
                try {
                    String sql = "UPDATE T_TRADERECORD SET C_STATE=1,C_MODIFYTIME='"
                            + new Timestamp(System.currentTimeMillis()) + "' WHERE C_ORDERCODE='" + orderidstr + "'";
                    Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                confirmationChange(orderid, id);
            }
        }
        else {
            WriteLog.write("火车票改签收款_err", orderidstr + ":" + tradeno + ":" + payprice + ":" + paytype + ":"
                    + selleremail);
            WriteLog.write("火车票改签收款_err", orderidstr + ":" + sql1);
        }
    }

    /*
     * 确认改签
     * */
    public void confirmationChange(long orderid, long id) {
        try {
            Trainorderchange change = Server.getInstance().getTrainService().findTrainOrderChangeById(id);
            Trainorder trainorder = Server.getInstance().getTrainService().findTrainorder(orderid);
            JSONObject jb = new JSONObject();
            String reqtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()); //请求时间  格式：yyyyMMddHHmmss（非空）
            JSONObject jsonObject = TrainOrderInterfaceInfoMethod.getInterfaceUser(trainorder.getAgentid());
            String partnerid = jsonObject.getString("partnerid");
            String key = jsonObject.getString("partnerid_key");
            String sign = ""; //数字签名
            String method = "train_confirm_change"; // 操作功能名  投保insure;退保cancel_insurance 
            sign = partnerid + method + reqtime + MD5Util.MD5Encode(key, "utf-8");
            sign = MD5Util.MD5Encode(sign, "utf-8");
            jb.put("reqtime", reqtime);
            jb.put("partnerid", partnerid);
            jb.put("sign", sign);
            jb.put("method", method);
            String callbackurl = PropertyUtil.getValue("requestconfimcallbackurl", "Train.properties");//确认改签回调地址 
            if (trainorder.getOrdertype() == 3) {//当为托管时  放入帐号
                String sqlString = "exec [sp_YDXOrderAndLonggin_selectNP] @OrderId =" + trainorder.getId();
                List list1 = Server.getInstance().getSystemService().findMapResultBySql(sqlString, null);
                if (list1.size() > 0) {
                    Map map3 = (Map) list1.get(0);
                    String password = "";
                    try {
                        password = TrusteeshipUtil.decrypt(map3.get("Login12306Password").toString());
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    jb.put("LoginUserName", map3.get("Login12306Name").toString());//12306 用户名密码
                    jb.put("LoginUserPassword", password);
                }
            }
            else {
                jb.put("LoginUserName", "");//12306 用户名密码
                jb.put("LoginUserPassword", "");
            }
            jb.put("reqtoken", change.getRequestReqtoken());// 1~50    string  请求特征值[与请求改签reqtoken对应]
            jb.put("orderid", trainorder.getOrdernumber());
            jb.put("transactionid", trainorder.getQunarOrdernumber());
            jb.put("isasync", "Y");
            jb.put("callbackurl", callbackurl);
            WriteLog.write("ydx确认改签请求参数", trainorder.getId() + " json=" + jb.toString());
            String s = "";
            try {
                s = URLEncoder.encode(jb.toString(), "utf-8");
            }
            catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            String reqUrl = PropertyUtil.getValue("trainorderurl", "Train.properties");
            String result = SendPostandGet.submitPost(reqUrl, "jsonStr=" + s, "UTF-8").toString();
            WriteLog.write("ydx确认改签返回结果", "result=" + result);
            boolean success = false;
            try {
                if (result != null && result != "") {
                    JSONObject jbJsonObject = JSONObject.parseObject(result);
                    success = jbJsonObject.containsKey("success") ? jbJsonObject.getBoolean("success") : false;
                    String msg = jbJsonObject.containsKey("msg") ? jbJsonObject.getString("msg") : "提交确认改签";
                    if (success) {
                        createTrainorderrc(1, trainorder.getId(), "确认改签接口：" + msg, "系统", 12, 0);
                    }
                    else {
                        createTrainorderrc(1, trainorder.getId(), "确认改签接口：" + msg, "系统", 12, 0);
                        if (change.getTcislowchange() == 0) {
                            for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
                                for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                                    if (trainticket.getChangeid() == change.getId()) {
                                        String serverinfo = PropertyUtil.getValue("Serverinfo", "Train.properties");//退款地址配置
                                        new TrainchangeRefundMethod().refundChange(change, trainticket, trainorder,
                                                serverinfo);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
            }
            if (!success) {

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 书写操作记录
     * 
     * @param trainorderid
     * @param content
     * @param createurser
     * @time 2015年1月21日 下午7:05:04
     * @author fiend
     */
    public void createTrainorderrc(int yewutype, long trainorderid, String content, String createurser, int status,
            long ticketid) {
        try {
            Trainorderrc rc = new Trainorderrc();
            rc.setOrderid(trainorderid);
            rc.setContent(content);
            rc.setStatus(status);// Trainticket.ISSUED
            rc.setCreateuser(createurser);// "12306"
            rc.setTicketid(ticketid);
            rc.setYwtype(yewutype);
            Server.getInstance().getTrainService().createTrainorderrc(rc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
