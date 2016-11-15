package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.text.DecimalFormat;
//import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import TrainInterfaceMethod.TrainInterfaceMethod;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.Thread.SendPayMQmsgThread;
import com.ccservice.b2b2c.atom.servlet.chongdong.WormholeShouquan;
import com.ccservice.b2b2c.atom.servlet.chongdong.WormholeUtil;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.b2b2c.base.train.Trainorder;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainticket;
import com.ccservice.b2b2c.base.trainpassenger.Trainpassenger;
import com.ccservice.b2b2c.ben.Trainform;
import com.ccservice.b2b2c.util.TimeUtil;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 火车票确认出票
 * method --> train_confirm
 * @author WH
 */

public class TongChengConfirmTrain extends TongchengSupplyMethod {

    //时间格式化
    //    private static final SimpleDateFormat totalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //时间格式化
    //    private static final SimpleDateFormat totalFormat_ymdHm = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    //异步支付12306
    @SuppressWarnings("rawtypes")
    public String opeate(JSONObject json, InterfaceAccount interfaceAccount) {
        Long l1 = System.currentTimeMillis();
        JSONObject retobj = new JSONObject();
        //同程订单号
        String orderid = json.containsKey("orderid") ? json.getString("orderid") : "";
        //交易单号
        String transactionid = json.containsKey("transactionid") ? json.getString("transactionid") : "";
        int insureNum = json.containsKey("insureNum") ? json.getIntValue("insureNum") : 0;
        //代理商
        String partnerid = json.containsKey("partnerid") ? json.getString("partnerid") : "";
        String username12306 = getUsername(json);
        String userpassword12306 = getUserPassword(json);
        String cookie12306 = json.containsKey("cookie") ? json.getString("cookie") : null;//cookie
        //存在空值
        if (ElongHotelInterfaceUtil.StringIsNull(orderid) || ElongHotelInterfaceUtil.StringIsNull(transactionid)
                || ElongHotelInterfaceUtil.StringIsNull(partnerid)) {
            retobj.put("success", false);
            retobj.put("code", "107");
            retobj.put("msg", "业务参数缺失");
            return retobj.toJSONString();
        }
        //        testtuniuMethod();//途牛备用流程

        Trainform trainform = new Trainform();
        trainform.setQunarordernumber(orderid);
        trainform.setOrdernumber(transactionid);
        List<Trainorder> orders = Server.getInstance().getTrainService().findAllTrainorder(trainform, null);
        WriteLog.write("TongChengConfirmTrain",
                orderid + ":orders:" + orders.size() + ":" + JSONObject.toJSONString(orders));
        //订单不存在
        if (orders == null || orders.size() == 0) {
            retobj.put("success", false);
            retobj.put("code", "402");
            retobj.put("msg", "订单不存在");
            return retobj.toJSONString();
        }
        Trainorder order = new Trainorder();
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
        //将登录用的帐号名密码或Cookie存入数据库
        WriteLog.write("TongChengConfirmTrain", orderid + ":order:" + order.getQunarOrdernumber());
        diaoyongTrainAccountSrcUtildeinsertData(order.getId(), username12306, userpassword12306, partnerid, cookie12306);
        WriteLog.write("TongChengConfirmTrain", orderid + ":order2:" + order.getQunarOrdernumber());
        int status = order.getOrderstatus();//状态
        int status12306 = order.getState12306();
        String extnumber = order.getExtnumber();//12306订单号
        int interfacetype = interfaceAccount.getInterfacetype();//判断
        if (status12306 != Trainorder.ORDEREDWAITPAY || status != Trainorder.WAITPAY
                || ElongHotelInterfaceUtil.StringIsNull(extnumber)) {
            WriteLog.write("TongChengConfirmTrain", orderid + ":order:" + order.getQunarOrdernumber() + "status12306:"
                    + status12306 + ":status:" + status + ":extnumber:" + extnumber);
            try {
                if (status == Trainorder.WAITISSUE
                        && (TrainInterfaceMethod.TONGCHENG == interfacetype || TrainInterfaceMethod.MEITUAN == interfacetype)
                /* && MemCached.getInstance().get("comfirmtrain=" + orderid) != null
                 && transactionid.equalsIgnoreCase(MemCached.getInstance().get("comfirmtrain=" + orderid)
                         .toString())*/) {
                    WriteLog.write("TongChengConfirmTrain", orderid + ":order:" + order.getQunarOrdernumber()
                            + ":status:" + status + ":interfacetype:" + interfacetype);
                    retobj.put("success", true);
                    retobj.put("code", "100");
                    retobj.put("msg", "出票请求已接收");
                    retobj.put("ordernumber", order.getExtnumber());
                    retobj.put("orderid", orderid);
                    return retobj.toJSONString();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            retobj.put("success", false);
            retobj.put("code", "112");
            retobj.put("msg", "该订单状态下，不能确认出票");
            return retobj.toJSONString();
        }
        /* else {
             try {
                 Date date = new Date(1000 * 1 * 60 * 3);//3分鐘缓存
                 MemCached.getInstance().add("comfirmtrain=" + orderid, transactionid, date);
             }
             catch (Exception e) {
                 e.printStackTrace();
             }
         }*/
        //加载其他字段、乘客
        order = Server.getInstance().getTrainService().findTrainorder(order.getId());
        WriteLog.write("TongChengConfirmTrain", orderid + ":加载其他字段、乘客order:" + order.getQunarOrdernumber());
        //订单创建时间
        long createTime = order.getCreatetime().getTime();
        //订单创建成功实践
        long dercreatetime = order.getCreatetime().getTime();
        try {
            WriteLog.write("TongChengConfirmTrain", orderid + ":<=orderid:updateConfirmTime:");
            updateConfirmTime(order.getId());//更新确认出票时间
            String sql = "SELECT C_EXTORDERCREATETIME exttime FROM T_TRAINORDER WITH (NOLOCK) WHERE ID ="
                    + order.getId();
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            Map map = (Map) list.get(0);
            String dercreatetimeString = map.get("exttime") == null ? "" : map.get("exttime").toString();
            WriteLog.write("TongChengConfirmTrain", orderid + ":" + dercreatetimeString);
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
        //        try {
        //            //发车时间
        //            String deptimestr = order.getPassengers().get(0).getTraintickets().get(0).getDeparttime();
        //            long deptime = totalFormat_ymdHm.parse(deptimestr).getTime();
        //            //购票时间TO发车时间的时间差
        //            long subTimeDep2Create = (deptime - createTime) / 1000;//秒
        //            String currentDate = ElongHotelInterfaceUtil.getCurrentDate();
        //            long temp_before = totalFormat.parse(currentDate + " 22:45:00").getTime();
        //            long temp = totalFormat.parse(currentDate + " 23:22:00").getTime();
        //            if (subTimeDep2Create <= 2 * 60 * 60) {//购票时间距发车时间2个小时以内
        //                if (subTime > 8 * 60 && subTimeReal > 8 * 60) {//给接口方8分钟的确认出票时间
        //                    timeout = true;
        //                }
        //            }
        //            else if (currentTime > temp) {//确认出票时间在23:22:00之后的全部超时
        //                timeout = true;
        //            }
        //            else if (createTime < temp_before) {//创建订单时间在22:45:00之后     确认出票时间在23:22:00之前  不算超时
        //                if (subTime > 25 * 60) {//2015年5月12日 以后 给接口方25分钟的确认出票时间
        //                    timeout = true;
        //                }
        //            }
        //        }
        //        catch (Exception e) {
        //        }
        if (timeout) { //支付超时
            retobj.put("success", false);
            retobj.put("code", "401");
            retobj.put("msg", "确认出票的请求时间已超过规定的时间");
            return retobj.toJSONString();
        }
        //        boolean iskoukuan = false;
        //getSysconfigString("train_confirm_pay");
        //        
        //        int interfacetype = order.getInterfacetype() != null && order.getInterfacetype() > 0 ? order.getInterfacetype()
        //                : new TrainInterfaceType().getTrainInterfaceType(order.getId());
        //2015年4月11日19:45:24 chendong 

        //        if (TrainInterfaceMethod.TONGCHENG == interfacetype) {
        //            iskoukuan = true;
        //        }
        /**
         * 测试虫洞授权接口
         * 杨荣强
         */
        if (WormholeUtil.checkTrainOrderIsWormhole(order.getId())) {
            WormholeShouquan wormholeShouquan = new WormholeShouquan();
            String result = wormholeShouquan.sendjson(orderid, insureNum);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String code = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            if (!"SUCCESS".equals(code)) {
                retobj.put("success", false);
                retobj.put("code", "999");
                retobj.put("msg", message);
                return retobj.toJSONString();
            }
        }
        String changeserial = "";
        if (TrainInterfaceMethod.TONGCHENG == interfacetype || TrainInterfaceMethod.MEITUAN == interfacetype) {
            //虚拟支付
            //            Payresult payresult = vmonyPay(order.getId(), order.getOrdernumber(), order.getOrderprice(), 3,
            //                    order.getQunarOrdernumber(), order.getAgentid());
            //            //支付成功了才往下执行
            //            if (!payresult.isPaysuccess()) {
            //                retobj.put("success", false);
            //                retobj.put("code", "403");
            //                retobj.put("msg", payresult.getResultmessage());
            //                return retobj.toJSONString();
            //            }
            //            changeserial = payresult.getResultmessage();
            order.setPaymethod(4);
        }
        else if (TrainInterfaceMethod.WITHHOLDING_BEFORE == interfacetype) {//代扣-->出票流程,先扣钱在出票
            TrainWithholeResult trainWithholeResult = new TrainWithholding().withHoldingBefore(order.getId(), 2, 1);
            if ("S".equalsIgnoreCase(trainWithholeResult.getStatuscode())) {
                changeserial = trainWithholeResult.getRemark();
                train_change_write(order.getId(), 1);
            }
            else if ("F".equalsIgnoreCase(trainWithholeResult.getStatuscode())) {
                retobj.put("success", false);
                retobj.put("code", "403");
                retobj.put("msg", trainWithholeResult.getRemark());
                train_change_write(order.getId(), 2);
                return retobj.toJSONString();
            }
            else {
                retobj.put("success", false);
                retobj.put("code", "403");
                retobj.put("msg", trainWithholeResult.getRemark());
                train_change_write(order.getId(), 3);
                return retobj.toJSONString();
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
        //更新车票
        for (Trainpassenger trainpassenger : order.getPassengers()) {
            for (Trainticket trainticket : trainpassenger.getTraintickets()) {
                trainticket.setStatus(Trainticket.WAITISSUE);
                Server.getInstance().getTrainService().updateTrainticket(trainticket);
            }
        }
        WriteLog.write("TongChengConfirmTrain", orderid + ":trainorderrc");
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
        new SendPayMQmsgThread(order, orderid).start();
        //返回
        retobj.put("success", true);
        retobj.put("code", "100");
        retobj.put("msg", "出票请求已接收");
        retobj.put("ordernumber", order.getExtnumber());
        retobj.put("orderid", orderid);
        retobj.put("changeserial", changeserial);
        WriteLog.write("TongChengConfirmTrain", orderid + ":确认出票[总耗时:" + (System.currentTimeMillis() - l1) + "]:"
                + order.getOrdernumber());
        return retobj.toJSONString();
    }

    //    private void testtuniuMethod() {
    //        //若是途牛,走此线程
    //        if (partneridIsTuniu(partnerid)) {
    //            boolean isTrue = false;
    //            try {
    //                String sql = "SELECT TOP 1 C_EXTNUMBER FROM T_TRAINORDER WHERE C_ORDERNUMBER='" + transactionid
    //                        + "' ORDER BY ID DESC";
    //                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
    //                if (list.size() > 0) {
    //                    Map map = (Map) list.get(0);
    //                    String extnumber = map.get("C_EXTNUMBER").toString();
    //                    // 创建一个可重用固定线程数的线程池
    //                    ExecutorService pool = Executors.newFixedThreadPool(1);
    //                    TuniuConfirmTrainThread confirm = null;
    //                    confirm = new TuniuConfirmTrainThread(orderid, transactionid, interfaceAccount);
    //                    Thread tuniu = new Thread(confirm);
    //                    pool.execute(tuniu);
    //                    // 关闭线程池
    //                    pool.shutdown();
    //                    WriteLog.write("途牛确认出票Thread", "tongc订单号:" + orderid + "线程关闭");
    //                    retobj.put("success", true);
    //                    retobj.put("code", "100");
    //                    retobj.put("msg", "出票请求已接收");
    //                    retobj.put("ordernumber", extnumber);
    //                    retobj.put("orderid", orderid);
    //                    retobj.put("changeserial", "");
    //                    isTrue = true;
    //                }
    //            }
    //            catch (Exception e) {
    //                WriteLog.write("err_TongChengConfirmTrain", orderid);
    //                ExceptionUtil.writelogByException("err_TongChengConfirmTrain", e);
    //            }
    //            if (!isTrue) {
    //                retobj.put("success", false);
    //                retobj.put("code", "402");
    //                retobj.put("msg", "订单不存在");
    //            }
    //            return retobj.toJSONString();
    //        }
    //
    //        
    //    }

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
     * 是否是途牛的代理商，是:true,不是:false
     * 
     * @param partnerid
     * @return
     * @time 2015年8月19日 下午2:29:02
     * @author Administrator
     */
    public boolean partneridIsTuniu(String partnerid) {
        String Checkpartnerid = PropertyUtil.getValue("ChecktrainCreateTimeOutpartnerid", "Train.properties");
        if (Checkpartnerid != null && !"".equals(Checkpartnerid)) {
            String[] parterids = Checkpartnerid.split("_");
            for (int i = 0; i < parterids.length; i++) {
                if (partnerid.contains(parterids[i])) {
                    return true;
                }
            }
        }
        return false;
    }
}