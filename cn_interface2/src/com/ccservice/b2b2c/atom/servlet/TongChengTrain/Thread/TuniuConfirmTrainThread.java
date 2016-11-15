package com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.MQ.MQMethod;
import com.ccservice.b2b2c.atom.servlet.MQ.TrainpayMqMSGUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainWithholding;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TrainWithholeResult;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.util.GuidUtil;
import com.ccservice.b2b2c.util.OcsMethod;
import com.ccservice.b2b2c.util.TimeUtil;
import com.ccservice.elong.inter.PropertyUtil;

public class TuniuConfirmTrainThread extends TongchengSupplyMethod implements Runnable {

    String orderid;

    String transactionid;

    InterfaceAccount interfaceAccount;

    public TuniuConfirmTrainThread(String orderid, String transactionid, InterfaceAccount interfaceAccount) {
        this.orderid = orderid;
        this.transactionid = transactionid;
        this.interfaceAccount = interfaceAccount;

    }

    @Override
    @SuppressWarnings("rawtypes")
    public void run() {
        WriteLog.write("途牛确认出票进程计算", orderid + transactionid + interfaceAccount.getInterfacetype());
        boolean McCached = false;
        String guid = GuidUtil.getUuid();
        McCached = setMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        int logi = 0;
        logi += 1;
        WriteLog.write("途牛确认出票进程计算", "" + logi);
        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        //TODO Thread 2   
        logi += 1;
        WriteLog.write("途牛确认出票进程计算", "" + logi);
        McCached = getMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        logi += 1;
        WriteLog.write("途牛确认出票进程计算", "" + logi);
        Trainorder order = new Trainorder();
        //订单不存在
        if (orders == null || orders.size() == 0) {
            WriteLog.write("途牛确认出票Thread", "订单不存在,orderid" + orderid);
            TuniuConfirmTrainCallBack(orderid, false);
            return;
        }
        WriteLog.write("途牛确认出票Thread", "订单size:" + orders.size() + "&订单状态:" + orders.get(0).getOrderstatus());
        if (orders.size() == 1) {
            order = orders.get(0);
        }
        else if (orders.size() == 2) {
            if (orders.get(0).getOrderstatus() == Trainorder.WAITPAY) {
                order = orders.get(0);
            }
            else {
                order = orders.get(1);
            }
        }
        logi += 1;
        WriteLog.write("途牛确认出票进程计算", "" + logi);
        int status = order.getOrderstatus();//状态
        int status12306 = order.getState12306();
        String extnumber = order.getExtnumber();//12306订单号
        //判断
        int interfacetype = interfaceAccount.getInterfacetype();
        logi += 1;
        WriteLog.write("途牛确认出票进程计算", "" + logi);
        if (status12306 != Trainorder.ORDEREDWAITPAY || status != Trainorder.WAITPAY
                || ElongHotelInterfaceUtil.StringIsNull(extnumber)) {
            try {
                if (status == Trainorder.WAITISSUE && TrainInterfaceMethod.TONGCHENG == interfacetype) {
                    WriteLog.write("途牛确认出票Thread", "出票请求已接收,status:" + status);
                    //                    TuniuConfirmTrainCallBack(orderid, true);
                    return;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            WriteLog.write("途牛确认出票Thread", "該状态下无法确认出票,status12306:" + status12306 + "&status:" + status);
            logi += 1;
            WriteLog.write("途牛确认出票进程计算", "" + logi);
            //            TuniuConfirmTrainCallBack(orderid, true);
            return;
        }
        //加载其他字段、乘客
        order = Server.getInstance().getTrainService().findTrainorder(order.getId());
        //TODO Thread 5  
        logi += 1;
        WriteLog.write("途牛确认出票进程计算", "" + logi);
        McCached = getMemCachedTrainorder(orderid, guid);
        logi += 1;
        WriteLog.write("途牛确认出票进程计算", "" + logi + McCached);
        if (!McCached) {
            return;
        }
        //订单创建时间
        long createTime = order.getCreatetime().getTime();
        //订单创建成功实践
        long dercreatetime = order.getCreatetime().getTime();
        try {
            updateConfirmTime(order.getId());//更新确认出票时间
            logi += 1;
            WriteLog.write("途牛确认出票进程计算", "" + logi);
            McCached = getMemCachedTrainorder(orderid, guid);
            if (!McCached) {
                return;
            }
            logi += 1;
            WriteLog.write("途牛确认出票进程计算", "" + logi);
            String sql = "SELECT C_EXTORDERCREATETIME exttime FROM T_TRAINORDER WITH (NOLOCK) WHERE ID ="
                    + order.getId();
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            logi += 1;
            WriteLog.write("途牛确认出票进程计算", "" + logi);
            McCached = getMemCachedTrainorder(orderid, guid);
            if (!McCached) {
                return;
            }
            logi += 1;
            WriteLog.write("途牛确认出票进程计算", "" + logi);
            Map map = (Map) list.get(0);
            String dercreatetimeString = map.get("exttime").toString();
            dercreatetime = TimeUtil.stringToLong(dercreatetimeString, TimeUtil.yyyyMMddHHmmssSSS);
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
        //当前时间
        long currentTime = System.currentTimeMillis();
        //时间差
        long subTime = (currentTime - createTime) / 1000;//秒
        long subTimeReal = (currentTime - dercreatetime) / 1000;//秒
        //12306在晚上11点半后不可支付了
        //判断是否超时，22:34:59前成功申请的单子，供应商应该保留40分钟的付款时间；22:35:00-23:00:00下 的单子供应商应该给20分钟的付款时间
        boolean timeout = false;
        String changeserial = "";
        if (timeout) { //支付超时
            WriteLog.write("途牛确认出票Thread", "支付超时,orderid" + orderid);
            TuniuConfirmTrainCallBack(orderid, true);
            return;
        }
        if (TrainInterfaceMethod.TONGCHENG == interfacetype) {
            order.setPaymethod(4);
        }
        else if (TrainInterfaceMethod.WITHHOLDING_BEFORE == interfacetype) {//代扣-->出票流程,先扣钱在出票
            TrainWithholeResult trainWithholeResult = new TrainWithholding().withHoldingBefore(order.getId(), 2, 1);
            if ("S".equalsIgnoreCase(trainWithholeResult.getStatuscode())) {
                changeserial = trainWithholeResult.getRemark();
                train_change_write(order.getId(), 1);
            }
            else if ("F".equalsIgnoreCase(trainWithholeResult.getStatuscode())) {
                WriteLog.write("途牛确认出票Thread", "F,msg:" + trainWithholeResult.getRemark());
                TuniuConfirmTrainCallBack(orderid, true);
                return;
            }
            else {
                WriteLog.write("途牛确认出票Thread", "msg:" + trainWithholeResult.getRemark());
                TuniuConfirmTrainCallBack(orderid, true);
                return;
            }
            order.setPaymethod(1);
        }
        else if (TrainInterfaceMethod.WITHHOLDING_AFTER == interfacetype) {
            order.setPaymethod(1);
        }
        else {
            order.setPaymethod(4);
        }
        //更新订单
        order.setOrderstatus(Trainorder.WAITISSUE);
        order.setTradeno(changeserial);
        Server.getInstance().getTrainService().updateTrainorder(order);
        McCached = getMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        //更新车票
        for (Trainpassenger trainpassenger : order.getPassengers()) {
            for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                trainticket.setStatus(Trainticket.WAITISSUE);
                Server.getInstance().getTrainService().updateTrainticket(trainticket);
            }
        }
        // TODO Thread 7  
        McCached = getMemCachedTrainorder(orderid, guid);
        if (!McCached) {
            return;
        }
        try {//日志
            Trainorderrc rz = new Trainorderrc();
            rz.setYwtype(1);
            rz.setCreateuser("系统接口");
            rz.setOrderid(order.getId());
            rz.setContent("接口确认出票[" + transactionid + "]，等待支付12306.");
            rz.setStatus(Trainorder.WAITPAY);
            Server.getInstance().getTrainService().createTrainorderrc(rz);
        }
        catch (Exception e) {
        }
        try {
            WriteLog.write("途牛确认出票Thread", "进入MQ,orderid:" + orderid);
            new TrainpayMqMSGUtil(MQMethod.ORDERPAY_NAME).sendPayMQmsg(order, 1, 0);
            WriteLog.write("12306_TongChengConfirmTrain_MQ", ":确认出票：" + order.getOrdernumber());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 订单确认出票时间添加
     */
    public void updateConfirmTime(long orderid) {
        try {
            String sql = "UPDATE T_TRAINORDER SET C_CONFIRMTIME='"
                    + com.ccservice.b2b2c.atom.service12306.TimeUtil.gettodaydate(4) + "' WHERE ID=" + orderid;
            int count = Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询剩余金额
     * @param json
     * @return
     * @time 2014年12月25日 下午9:13:33
     * @author wzc
     */
    public String query_money(JSONObject json) {
        String partnerid = json.getString("partnerid");//传过来的partnerid
        JSONObject result = new JSONObject();
        String agentids = gettongchengagentid(partnerid);
        Long agentid = 0L;
        Double money = 0D;
        String moneyString = "0";
        try {
            agentid = Long.parseLong(agentids);
            money = getTotalVmoney(agentid).doubleValue();
            DecimalFormat df = new DecimalFormat("0.00");
            moneyString = df.format(money);
        }
        catch (Exception e) {
        }
        result.put("success", true);
        result.put("code", 100);
        result.put("money", moneyString);
        result.put("msg", "");
        return result.toJSONString();
    }

    /**
     * 4.2. 查询授权的未完成订单使用情况
     * 待开发
     * @param json
     * @return
     * @time 2014年12月18日 下午1:59:47
     * @author chendong
     */
    @SuppressWarnings("rawtypes")
    public String train_query_unfinished_order_count(JSONObject json) {
        String sql = "select COUNT(ID) IDCOUNT FROM T_TRAINORDER WHERE C_ORDERSTATUS=2 and C_AGENTID="
                + gettongchengagentid();
        Map map = (Map) Server.getInstance().getSystemService().findMapResultBySql(sql, null).get(0);
        String countid = "0";
        String authcount = getSysconfigString("tongcheng_auth_count");
        int remain = 0;
        try {
            countid = map.get("IDCOUNT").toString();
            remain = (int) (Integer.valueOf(authcount) - Integer.valueOf(countid));
            if (remain <= 0) {
                remain = 0;
            }
        }
        catch (Exception e) {
        }
        JSONObject result = new JSONObject();
        result.put("success", true);
        result.put("code", 100);
        result.put("msg", "");
        result.put("remain_count", remain + "");
        result.put("auth_count", getSysconfigString("tongcheng_auth_count"));
        return result.toJSONString();
    }

    /**
     * 修改订单和书写操作记录
     * @param orderid
     * @param i_koukuantype
     * @time 2015年3月26日 下午4:34:57
     * @author fiend
     */
    public void train_change_write(long orderid, int i_koukuantype) {
        try {
            if (1 == i_koukuantype) {
                //扣款成功
                Server.getInstance()
                        .getSystemService()
                        .findMapResultBySql("UPDATE T_TRAINORDER SET C_ISPLACEING=" + 1 + " WHERE ID =" + orderid, null);
            }
            if (2 == i_koukuantype) {
                //扣款失败
                Server.getInstance()
                        .getSystemService()
                        .findMapResultBySql("UPDATE T_TRAINORDER SET C_ISPLACEING=" + 4 + " WHERE ID =" + orderid, null);
            }
            if (3 == i_koukuantype) {
                //扣款异常
                Server.getInstance()
                        .getSystemService()
                        .findMapResultBySql("UPDATE T_TRAINORDER SET C_ISPLACEING=" + 6 + " WHERE ID =" + orderid, null);
            }
            train_write(orderid, i_koukuantype);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 书写操作记录 
     * @param orderid
     * @param i_koukuantype
     * @time 2015年3月26日 下午4:58:09
     * @author fiend
     */
    public void train_write(long orderid, int i_koukuantype) {
        Trainorderrc rc = new Trainorderrc();
        rc.setOrderid(orderid);
        rc.setContent("扣款失败");
        if (1 == i_koukuantype) {
            rc.setContent("扣款成功");
        }
        if (3 == i_koukuantype) {
            rc.setContent("扣款异常，请客服操作拒单退款");
        }
        rc.setStatus(2);
        rc.setCreateuser("自动扣款");//"12306"
        rc.setTicketid(0);
        rc.setYwtype(1);
        Server.getInstance().getTrainService().createTrainorderrc(rc);
    }

    /**
     * 判断MemCached中有没有数据且数据是否一致, 有:true,没有false
     * 
     * @param orderid
     * @return
     * @time 2015年8月18日 下午6:52:17
     * @author wcl
     */
    public boolean getMemCachedTrainorder(String orderid, String guid) {
        WriteLog.write("途牛确认出票Thread", "orderid" + orderid + "-" + "guid:" + guid);
        String mccguid = OcsMethod.getInstance().get("confirm=" + orderid);
        if (mccguid != null || "".equals(mccguid) && OcsMethod.getInstance().get("confirm=" + orderid).equals(guid)) {
            WriteLog.write("途牛确认出票Thread", "orderid" + orderid + "-" + "guid:" + guid);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 判断McCached有没有值,有就返回false,没有,添加值并返回true   
     * 
     * @param orderid
     * @return
     * @time 2015年8月22日 下午1:47:54
     * @author wcl
     */
    public boolean setMemCachedTrainorder(String orderid, String guid) {
        String mccguid = OcsMethod.getInstance().get("confirm=" + orderid);
        WriteLog.write("途牛确认出票Thread", "orderid:" + orderid + "--mccguid:" + mccguid);
        if (mccguid == null || "".equals(mccguid)) {
            OcsMethod.getInstance().add("confirm=" + orderid, guid, 210);
            WriteLog.write("途牛确认出票Thread", "orderid:" + orderid + "--guid:" + guid + "--mccguid" + mccguid);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 回调
     * 
     * @param retobj
     * @time 2015年8月22日 下午1:51:22
     * @author wcl
     */
    public void TuniuConfirmTrainCallBack(String trainorderid, boolean exist) {
        boolean bool = false;
        String url = PropertyUtil.getValue("TuNiu_CallBack_Url", "Train.properties");
        JSONObject jso = new JSONObject();
        jso.put("trainorderid", trainorderid);
        jso.put("method", "train_pay_callback");
        jso.put("orderid", orderid);
        jso.put("transactionid", transactionid);
        jso.put("isSuccess", "N");
        jso.put("iskefu", "1");
        for (int i = 0; i < 6; i++) {
            String resultUrlString = SendPostandGet.submitPost(url, jso.toString(), "UTF-8").toString();
            if (resultUrlString.equals("success")) {
                OcsMethod.getInstance().remove("confirm=" + orderid);
                WriteLog.write("途牛确认出票Thread", "第" + i + "回调:" + resultUrlString);
                bool = true;
                break;
            }
            WriteLog.write("途牛确认出票Thread", "第" + i + "回调" + resultUrlString);
            try {
                Thread.sleep(5000L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //回调失败且订单存在
        if (!bool && exist) {
            try {
                String update = "update T_TRAINORDER set C_ISQUESTIONORDER=3 where C_ORDERNUMBER='" + trainorderid
                        + "'";
                Server.getInstance().getSystemService().excuteAdvertisementBySql(update);
            }
            catch (Exception e) {
                WriteLog.write("err_TuniuConfirmTrainThread", trainorderid);
                ExceptionUtil.writelogByException("err_TuniuConfirmTrainThread", e);
            }
        }

    }
}
