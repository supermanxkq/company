package com.ccservice.b2b2c.atom.servlet.TongChengTrain.paiduiChange;

import java.util.Map;
import java.util.List;
import org.quartz.Job;
import java.text.SimpleDateFormat;
import org.quartz.JobExecutionContext;
import com.alibaba.fastjson.JSONObject;
import org.quartz.JobExecutionException;
import com.ccservice.b2b2c.util.ActiveMQUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;
import com.ccservice.elong.inter.PropertyUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 改签订单走排队机制Job
 * @author WH
 * @time 2016年2月17日 下午7:04:39
 * @version 1.0
 */

public class TrainChangeOrderPaiDuiJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        execute();
    }

    @SuppressWarnings("rawtypes")
    private void execute() {
        //打印
        System.out.println(ElongHotelInterfaceUtil.getCurrentTime() + "-->开始处理排队改签或变更到站");
        //当天订单
        String todayOrderLimit = PropertyUtil.getValue("ChangeOrderPaiDuiJobTimeStringTodayOrderLimit",
                "Train.ChangeOrder.properties");
        //排队时间
        String[] times = PropertyUtil.getValue("ChangeOrderPaiDuiJobTimeString", "Train.ChangeOrder.properties").split(
                "[|]");
        //循环时间
        for (int m = 0; m < times.length; m++) {
            //最后一次排队
            boolean isLast = m == times.length - 1;
            //开始时间、结束时间
            String stime = times[m].split(",")[0];
            String etime = times[m].split(",")[1];
            //SQL
            String procedureSql = " [sp_TrainOrderChangePaiduiData_SelectByJob] @stime=" + stime + ", @etime=" + etime
                    + " ";
            //查询
            List paiduiList = Server.getInstance().getSystemService().findMapResultByProcedure(procedureSql);
            //记录日志
            WriteLog.write("TrainChangeOrderPaiDuiJob", "改签排队订单:" + stime + ">" + etime + ":" + paiduiList.size()
                    + ":isLast:" + isLast + ":" + JSONObject.toJSONString(paiduiList));
            //打印
            System.out.println("改签排队订单:" + stime + ">" + etime + ":" + paiduiList.size() + ":isLast:" + isLast);
            //循环
            for (int n = 0; n < paiduiList.size(); n++) {
                try {
                    Map map = (Map) paiduiList.get(n);
                    sendMq(map, todayOrderLimit, isLast, m);
                }
                catch (Exception e) {
                    ExceptionUtil.writelogByException("TrainChangeOrderPaiDuiJob_Exception", e, times[m] + "," + n);
                }
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private void sendMq(Map map, String todayOrderLimit, boolean isLast, int m) {
        //发MQ
        boolean isSendMq = true;
        //发车日期
        String departDate = map.get("C_DepartDate").toString();
        //改签ID
        String orderId = map.get("C_TrainOrderId").toString();
        long paiDuiId = Long.parseLong(map.get("C_ID").toString());
        long changeId = Long.parseLong(map.get("C_ChangeId").toString());
        //状态>>0等待检测,1检测中,2检测结束
        int checkStatus = Integer.parseInt(map.get("C_CheckStatus").toString());
        int refundOnline = Integer.parseInt(map.get("C_RefundOnline").toString());
        //超时时间
        String timeout = map.get("C_TimeOut") == null ? "" : map.get("C_TimeOut").toString();
        //存在超时
        if (!ElongHotelInterfaceUtil.StringIsNull(timeout) && timeout.contains(":")) {
            try {
                //改签
                long changeTimeOut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(timeout).getTime();
                //未超时
                isSendMq = System.currentTimeMillis() < changeTimeOut;
                //非最后>>距下一次该改签排队3分钟执行
                if (!isLast) {
                    isLast = System.currentTimeMillis() + 3 * 60 * 1000 >= changeTimeOut;
                }
            }
            catch (Exception e) {
            }
        }
        //当天发车
        if (departDate.equals(ElongHotelInterfaceUtil.getCurrentDate())) {
            //当天限制
            if (Integer.parseInt(todayOrderLimit) == m) {
                isLast = true;
            }
            //如果当前下标超过了当天排队的下标，就不扔MQ了
            if (m > Integer.parseInt(todayOrderLimit)) {
                isSendMq = false;
            }
        }
        //状态正确
        if (checkStatus == 0) {
            //发消息
            if (isSendMq) {
                //参数
                JSONObject json = new JSONObject();
                json.put("idx", m);
                json.put("changeId", changeId);
                json.put("paiDuiId", paiDuiId);
                json.put("isLastPaidui", isLast);
                json.put("refundOnline", refundOnline);
                //地址
                String MQ_URL = PropertyUtil.getValue("activeMQ_url", "Train.properties");
                //名称
                String QueuesName = PropertyUtil.getValue("QueueMQ_ChangeOrder_PaiDui", "Train.ChangeOrder.properties");
                //日志
                WriteLog.write("TrainChangeOrderPaiDuiJob", orderId + ":丢MQ:" + json);
                //发送
                ActiveMQUtil.sendMessage(MQ_URL, QueuesName, json.toString());
            }
            //拒单操作
            else {
                new TrainChangeOrderPaiDuiUtil().refuseChange(changeId, paiDuiId, m);
            }
        }
    }
}