package com.ccservice.b2b2c.atom.servlet.QunarTrain.Thread;

import java.util.List;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.MQ.MQMethod;
import com.ccservice.b2b2c.atom.servlet.MQ.TrainpayMqMSGUtil;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;

public class MyThreadQunarPayResult extends Thread {
    private long orderid;

    private String payStatus;

    private Trainorder trainorder;

    public MyThreadQunarPayResult(long orderid, String payStatus) {
        this.orderid = orderid;
        this.payStatus = payStatus;
    }

    @Override
    public void run() {
        this.trainorder = Server.getInstance().getTrainService().findTrainorder(orderid);
        WriteLog.write("代付返回结果", this.trainorder.getOrdernumber() + "======>" + this.payStatus);
        if ("1".equals(this.payStatus)) {
            payTrue();
        }
        if ("2".equals(this.payStatus)) {
            payFalse();
        }
        if ("3".equals(this.payStatus)) {
            payException();
        }
        createTrainordertimeout();
    }

    /**
     * qunar支付成功 
     * @time 2015年1月21日 下午3:52:29
     * @author fiend
     */
    public void payTrue() {
        this.trainorder.setState12306(Trainorder.ORDEREDPAYED);
        this.trainorder.setSupplypayway(14);
        this.trainorder.setSupplytradeno("qunarpaytrue");
        float differenceprice = getdirrerenceprice(trainorder);
        writeRc(this.orderid, Trainticket.ISSUED, "支付成功", "qunar支付接口接口", 1);
        trainorder.setOrderstatus(Trainorder.ISSUED);
        try {
            Server.getInstance().getTrainService().refusequnarTrain(trainorder, Trainticket.ISSUED);
            writeRc(this.orderid, Trainticket.ISSUED, "出票完成。退还差价:" + differenceprice + "元", "qunar出票接口", 1);
            trainorder.setIsquestionorder(Trainorder.NOQUESTION);
            Server.getInstance().getTrainService().updateTrainorder(trainorder);
            String sql = "UPDATE T_TRAINORDER SET C_ORDERSTATUS = 3,C_ISQUESTIONORDER = 0 WHERE ID = " + 
                    this.orderid;
                  Server.getInstance().getSystemService().excuteEaccountBySql(sql);
        }
        catch (Exception e) {
            WriteLog.write("代付死锁", "代付成功:" + this.trainorder.getOrdernumber());
            writeRc(this.orderid, Trainticket.ISSUED, "出票完成。退还差价:" + differenceprice + "元--->死锁", "qunar出票接口", 1);
            String deadlockUrl = getSysconfigString("deadlocktrainorder");
            try {
                SendPostandGet.submitGet(deadlockUrl + this.orderid);
                WriteLog.write("代付死锁_转队列", "代付成功转队列成功:" + this.trainorder.getOrdernumber());
            }
            catch (Exception e1) {
                WriteLog.write("代付死锁_转队列", "代付成功转队列失败:" + this.trainorder.getOrdernumber());
            }
        }
    }

    /**
     * qunar支付失败
     * @time 2015年1月21日 下午3:52:29
     * @author fiend
     */
    public void payFalse() {
        this.trainorder.setIsquestionorder(Trainorder.NOQUESTION);
        this.trainorder.setState12306(Trainorder.ORDEREDWAITPAY);
        this.trainorder.setSupplypayway(14);
        this.trainorder.setSupplytradeno("qunarpayfalse");
        try {
            this.trainorder.setSupplyaccount(this.trainorder.getSupplyaccount().replace("/QUNAR", ""));
        }
        catch (Exception e) {
        }
        try {
            writeRc(this.orderid, Trainticket.ISSUED, "支付失败", "qunar支付接口接口", 1);
            Server.getInstance().getTrainService().updateTrainorder(trainorder);
        }
        catch (Exception e) {
            WriteLog.write("代付死锁", "代付失败:" + this.trainorder.getOrdernumber());
            writeRc(this.orderid, Trainticket.ISSUED, "支付失败--->死锁", "qunar出票接口", 1);
        }
        try {
            new TrainpayMqMSGUtil(MQMethod.ORDERPAY_NAME).sendPayMQmsg(this.trainorder, 1, 0);
            WriteLog.write("12306_MyThreadQunarOrder_MQ_Pay", ": qunar确认出票 ： " + this.trainorder.getOrdernumber());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * qunar支付异常
     * @time 2015年1月21日 下午3:52:29
     * @author fiend
     */
    public void payException() {
        this.trainorder.setState12306(Trainorder.ORDEREDPAYFALSE);
        this.trainorder.setSupplypayway(14);
        this.trainorder.setSupplytradeno("qunarpayException");
        this.trainorder.setIsquestionorder(Trainorder.PAYINGQUESTION);
        try {
            writeRc(this.orderid, Trainticket.ISSUED, "支付异常，请客服去12306核对", "qunar支付接口接口", 1);
            Server.getInstance().getTrainService().updateTrainorder(trainorder);
        }
        catch (Exception e) {
            WriteLog.write("代付死锁", "代付异常:" + this.trainorder.getOrdernumber());
            writeRc(this.orderid, Trainticket.ISSUED, "支付异常，请客服去12306核对--->死锁", "qunar出票接口", 1);
        }
    }

    /**
     * 创建qunar支付标识   
     * paystatus 1-支付成功 2-支付失败 3-支付异常
     * @time 2015年1月22日 下午4:35:36
     * @author fiend
     */
    public void createTrainordertimeout() {
        String sql = "DELETE FROM T_TRAINORDERTIMEOUT WHERE C_ORDERID IN (" + this.orderid + ")";
        Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        sql = "INSERT INTO T_TRAINORDERTIMEOUT(C_ORDERID,C_STATE,C_QUNARPAYSTATUS) VALUES(" + this.orderid + ",5,"
                + this.payStatus + ")";
        Server.getInstance().getSystemService().findMapResultBySql(sql, null);
    }

    /**
     * 说明:得到订单差价 
     * @param trainorder
     * @return
     * @time 2014年9月1日 下午3:29:14
     * @author yinshubin
     */
    public float getdirrerenceprice(Trainorder trainorder) {
        float differenceprice = 0f;
        for (Trainpassenger trainpassenger : trainorder.getPassengers()) {
            for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                differenceprice += trainticket.getPayprice() - trainticket.getPrice();
            }
        }
        return differenceprice;
    }

    /**
     * 书写操作记录 
     * @param orderid
     * @param status
     * @param content
     * @param createuser
     * @param ywtype
     * @time 2015年1月22日 下午4:45:07
     * @author fiend
     */
    public void writeRc(long orderid, int status, String content, String createuser, int ywtype) {
        Trainorderrc rc = new Trainorderrc();
        rc.setOrderid(orderid);
        rc.setStatus(status);
        rc.setContent(content);
        rc.setCreateuser(createuser);
        rc.setYwtype(ywtype);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

    /**
     * 根据sysconfig的name获得value
     * 内存
     * @param name
     * @return
     */
    public String getSysconfigString(String name) {
        String result = "-1";
        try {
            if (Server.getInstance().getDateHashMap().get(name) == null) {
                List<Sysconfig> sysoconfigs = Server.getInstance().getSystemService()
                        .findAllSysconfig("WHERE C_NAME='" + name + "'", "", -1, 0);
                if (sysoconfigs.size() > 0) {
                    result = sysoconfigs.get(0).getValue();
                    Server.getInstance().getDateHashMap().put(name, result);
                }
            }
            else {
                result = Server.getInstance().getDateHashMap().get(name);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
