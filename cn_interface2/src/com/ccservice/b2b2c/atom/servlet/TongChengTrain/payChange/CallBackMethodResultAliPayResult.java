package com.ccservice.b2b2c.atom.servlet.TongChengTrain.payChange;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.train.Trainorderrc;
import com.ccservice.b2b2c.base.train.Trainorderchange;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;

/**
 * 改签支付宝支付结果处理
 * @time 2015年12月30日 下午7:59:04
 * @author 彩娜
 */
public class CallBackMethodResultAliPayResult extends TongchengSupplyMethod {

    private String logName = "TongChengChangePayResultCallBack";

    //支付标识
    private boolean getpaycontrol(String info) {
        return !"支付失败:CASHIER_ACCESS_GAP_CONTROL_TIP".equals(info);
    }

    /**
     * 记录日志
     */
    private void trainRC(long orderId, String msg) {
        try {
            Trainorderrc rc = new Trainorderrc();
            rc.setYwtype(1);
            rc.setContent(msg);
            rc.setOrderid(orderId);
            rc.setCreateuser("自动支付");
            Server.getInstance().getTrainService().createTrainorderrc(rc);
        }
        catch (Exception e) {

        }
    }

    /**
     * 变成问题订单
     */
    private void changeQuestionOrder(Trainorderchange change, String msg, int random) {
        //日志
        trainRC(change.getOrderid(), msg);
        //支付问题
        try {
            int question = Trainorderchange.PAYINGQUESTION;
            String sql = "update T_TRAINORDERCHANGE set C_ISQUESTIONCHANGE = " + question + " where ID = "
                    + change.getId();
            Server.getInstance().getSystemService().excuteAdvertisementBySql(sql);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException(logName, e, random + "-->" + change.getId() + "-->变支付问题异常");
        }
    }

    /**
     *订单超时处理 
     */
    public String payTimeOut(String result, long changeId, int random) {
        //记录日志
        WriteLog.write(logName, random + "-->" + changeId + "-->支付超时");
        //查询改签
        Trainorderchange change = Server.getInstance().getTrainService().findTrainOrderChangeById(changeId);
        //订单标识
        String changeFlag = change.getChangeArrivalFlag() == 1 ? "变更到站" : "改签";
        //问题改签
        changeQuestionOrder(change, "[确认 - " + changeId + "]支付" + changeFlag + "订单，支付超时", random);
        //返回数据
        return "success";
    }

    /**
     * 支付链接处理
     */
    public String parsePayUrlResult(String result, long changeId, int random) {
        //查询改签
        Trainorderchange change = Server.getInstance().getTrainService().findTrainOrderChangeById(changeId);
        //订单标识
        String changeFlag = change.getChangeArrivalFlag() == 1 ? "变更到站" : "改签";
        //获取成功
        if ("success".equals(result)) {
            trainRC(change.getOrderid(), "[确认 - " + changeId + "]获取" + changeFlag + "支付链接成功");
        }
        else {
            //问题改签
            changeQuestionOrder(change, "[确认 - " + changeId + "]获取" + changeFlag + "支付链接失败", random);
            //记录日志
            WriteLog.write(logName, random + "-->" + changeId + "-->获取" + changeFlag + "支付链接失败");
        }
        return "success";
    }

    /**
     * 支付宝支付结果
     */
    public String parsePayResult(String result, long changeId, int random, String payset, String paymethodtype) {
        int flag = 0;
        //记录日志
        WriteLog.write(logName, random + "-->请求返回:" + result + "-->改签ID:" + changeId + "-->payset:" + payset);
        //查询改签
        Trainorderchange change = Server.getInstance().getTrainService().findTrainOrderChangeById(changeId);
        change.setPayflag(payset);
        //订单标识
        String changeFlag = change.getChangeArrivalFlag() == 1 ? "变更到站" : "改签";
        //更新改签
        String payFlagSql = "update T_TRAINORDERCHANGE set C_PAYFLAG = '" + payset + "' where ID = " + changeId;
        Server.getInstance().getSystemService().excuteAdvertisementBySql(payFlagSql);
        //支付异常
        if (result == null || !result.contains("status")) {
            changeQuestionOrder(change, "[确认 - " + changeId + "]支付" + changeFlag + "订单返回数据异常", random);
        }
        else {
            JSONObject obj = JSONObject.parseObject(result);
            //true
            if (obj.getBoolean("status")) {
                //查询失败成功信息
                String msg = obj.getString("info");
                String state = obj.getString("orderstatus");
                String liushuihao = obj.getString("AliTradeNo");
                //当前支付宝账号
                String AliPayUserName = obj.getString("AliPayUserName");
                //支付成功
                if (!ElongHotelInterfaceUtil.StringIsNull(liushuihao) && "支付成功".equals(state)) {
                    flag = 1;
                    //日志
                    WriteLog.write(logName, random + "-->" + changeId + "-->" + liushuihao);
                    //设值
                    change.setSupplytradeno(liushuihao);
                    change.setPayaccount(AliPayUserName);
                    change.setSupplypaymethod(Paymentmethod.ALIPAY);
                    //日志
                    trainRC(change.getOrderid(), "[确认 - " + changeId + "]支付" + changeFlag + "订单成功");
                    //更新
                    String paySuccessSql = "update T_TRAINORDERCHANGE set C_SUPPLYTRADENO = '" + liushuihao
                            + "', C_PAYACCOUNT = '" + AliPayUserName + "', C_SUPPLYPAYMETHOD = "
                            + change.getSupplypaymethod() + " where ID = " + changeId;
                    Server.getInstance().getSystemService().excuteAdvertisementBySql(paySuccessSql);
                    //日志
                    WriteLog.write(logName, random + "-->" + changeId + "-->更新结束-->" + msg);
                }
                else if ("支付失败".equals(state) && getpaycontrol(msg)) {//运行错误
                    changeQuestionOrder(change, "[确认 - " + changeId + "]支付" + changeFlag + "订单失败", random);
                }
                else {
                    flag = 2;
                    //日志
                    WriteLog.write(logName, random + "-->" + changeId + "-->该状态下不用处理");
                }
            }
            else {
                flag = 2;
                //日志
                WriteLog.write(logName, random + "-->" + changeId + "-->该状态下不用处理");
            }
        }
        //1：成功
        //2：结果未知
        if (flag > 0) {
            //改签审核
            activeMQChangeOrder(changeId, 3);
            //记录日志
            WriteLog.write(logName, random + "-->" + changeId + "-->支付" + (flag == 1 ? "成功" : "结果未知") + "，"
                    + changeFlag + "订单正在审核中……");
        }
        return "success";
    }
}