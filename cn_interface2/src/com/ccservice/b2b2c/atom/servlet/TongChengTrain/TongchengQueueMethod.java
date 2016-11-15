package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;

public class TongchengQueueMethod {
    @SuppressWarnings("rawtypes")
    public String operate(JSONObject jsonObject, int r1) {
        JSONObject resultJsonObject = new JSONObject();
        //同程订单号
        String interfaceOrderNumber = jsonObject.containsKey("orderid") ? jsonObject.getString("orderid") : "";
        //交易单号
        String localOrderNumber = jsonObject.containsKey("transactionid") ? jsonObject.getString("transactionid") : "";
        //排队查询特征值
        String queuetoken = jsonObject.containsKey("queuetoken") ? jsonObject.getString("queuetoken") : "";
        //普通占座或改签请求时传入的特征值
        String reqtoken = jsonObject.containsKey("reqtoken") ? jsonObject.getString("reqtoken") : "";
        //排队类型：1-普通占座排队 2-改签占座排队
        String queue_type = jsonObject.containsKey("queue_type") ? jsonObject.getString("queue_type") : "";

        if (ElongHotelInterfaceUtil.StringIsNull(interfaceOrderNumber)
                || ElongHotelInterfaceUtil.StringIsNull(queuetoken) || ElongHotelInterfaceUtil.StringIsNull(queue_type)) {
            resultJsonObject.put("success", false);
            resultJsonObject.put("code", 107);
            resultJsonObject.put("msg", "业务参数缺失");
            return resultJsonObject.toString();
        }
        // 这里需要判断是否处于排队状态
        if (isQueuing(interfaceOrderNumber, localOrderNumber, queue_type, reqtoken)) {
            try {
                //这里去查排队情况
                String sql = " [sp_TrainOrderQueue_Select] @InterfaceOrderNumber ='" + interfaceOrderNumber
                        + "' , @OrderNumber = '" + localOrderNumber + "' , @Type = " + queue_type + ",@ReqToken = '"
                        + reqtoken + "'";
                WriteLog.write("t同程排队详情", sql);
                List list = Server.getInstance().getSystemService().findMapResultByProcedure(sql);
                if (list.size() > 0) {
                    Map map = (Map) list.get(0);
                    String QueueCollectTime = map.containsKey("QueueCollectTime") ? map.get("QueueCollectTime")
                            .toString() : "";
                    String QueueStartTime = map.containsKey("QueueStartTime") ? map.get("QueueStartTime").toString()
                            : "";
                    String WaitCount = map.containsKey("WaitCount") ? map.get("WaitCount").toString() : "-1";
                    String WaitTime = map.containsKey("WaitTime") ? map.get("WaitTime").toString() : "-1";

                    long QueueCollectLongTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(QueueCollectTime)
                            .getTime();
                    long nowLongTime = System.currentTimeMillis();
                    //如果当前时间nowLongTime-1分钟 在查看时间QueueCollectLongTime之后
                    //并且排队的时间WaitTime不为-1
                    //并且查看时间QueueCollectLongTime+排队的时间WaitTime大于当前时间nowLongTime+1分钟
                    //我们需要把查看时间QueueCollectLongTime改成当前时间nowLongTime
                    //排队的时间WaitTime减去查看时间QueueCollectLongTime所修改的时间（当前时间-QueueCollectLongTime）
                    if ((nowLongTime - 1000 * 60 > QueueCollectLongTime) && !"-1".equals(WaitTime)
                            && (QueueCollectLongTime + Integer.valueOf(WaitTime) * 1000 > nowLongTime + 1000 * 60)) {
                        QueueCollectLongTime = System.currentTimeMillis();
                        WaitTime = String.valueOf(Integer.valueOf(WaitTime)
                                - ((nowLongTime - QueueCollectLongTime) / 1000));
                    }
                    QueueCollectTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(QueueCollectLongTime));
                    QueueStartTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss.SSS").parse(QueueStartTime));
                    resultJsonObject.put("orderid", interfaceOrderNumber);
                    resultJsonObject.put("transactionid", localOrderNumber);
                    resultJsonObject.put("queuetoken", queuetoken);
                    resultJsonObject.put("reqtoken", reqtoken);
                    resultJsonObject.put("queue_status", 1);
                    resultJsonObject.put("queue_type", queue_type);
                    resultJsonObject.put("queue_start_time", QueueStartTime);//yyyyMMddHHmmss
                    resultJsonObject.put("queue_collect_time", QueueCollectTime);//yyyyMMddHHmmss
                    resultJsonObject.put("queue_wait_time", WaitTime);
                    resultJsonObject.put("queue_wait_nums", WaitCount);
                }
                else {
                    resultJsonObject = getErrorJson(interfaceOrderNumber, localOrderNumber, queuetoken, reqtoken,
                            queue_type);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                resultJsonObject = getErrorJson(interfaceOrderNumber, localOrderNumber, queuetoken, reqtoken,
                        queue_type);
            }
        }
        else {
            resultJsonObject = getErrorJson(interfaceOrderNumber, localOrderNumber, queuetoken, reqtoken, queue_type);
        }
        resultJsonObject.put("success", true);
        resultJsonObject.put("code", 100);
        resultJsonObject.put("msg", "处理或操作成功");
        return resultJsonObject.toString();
    }

    /**
     * 是否在排队中
     * 
     * @return
     * @time 2016年4月18日 下午8:11:37
     * @author fiend
     */
    @SuppressWarnings("rawtypes")
    private boolean isQueuing(String interfaceOrderNumber, String localOrderNumber, String queue_type, String reqtoken) {
        try {
            if ("1".equals(queue_type)) {
                String isPaiduiSql = "SELECT C_STATE12306  FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER='"
                        + interfaceOrderNumber + "' AND  ('" + localOrderNumber + "'='' or C_ORDERNUMBER='"
                        + localOrderNumber + "') AND (C_CONTACTUSER = '" + reqtoken + "' or '" + reqtoken + "'='')";
                List isPaiduiList = Server.getInstance().getSystemService().findMapResultBySql(isPaiduiSql, null);
                if (isPaiduiList.size() > 0) {
                    Map map = (Map) isPaiduiList.get(0);
                    int state12306 = Integer.valueOf(map.get("C_STATE12306").toString());
                    if (state12306 == 18) {
                        return true;
                    }
                }
            }
            else if ("2".equals(queue_type)) {
                String isPaiduiSql = "SELECT C_STATUS12306  FROM T_TRAINORDERCHANGE WITH (NOLOCK) WHERE C_ORDERID IN (SELECT ID FROM T_TRAINORDER WITH (NOLOCK) WHERE C_QUNARORDERNUMBER='"
                        + interfaceOrderNumber
                        + "' AND ('"
                        + localOrderNumber
                        + "'='' or C_ORDERNUMBER='"
                        + localOrderNumber + "')) AND C_REQUESTREQTOKEN='" + reqtoken + "'";
                List isPaiduiList = Server.getInstance().getSystemService().findMapResultBySql(isPaiduiSql, null);
                if (isPaiduiList.size() > 0) {
                    Map map = (Map) isPaiduiList.get(0);
                    int state12306 = Integer.valueOf(map.get("C_STATUS12306").toString());
                    if (state12306 == 9) {
                        return true;
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 有问题的时候返回这个JSON
     * 
     * @param interfaceOrderNumber
     * @param localOrderNumber
     * @param queuetoken
     * @param reqtoken
     * @param queue_type
     * @return
     * @time 2016年4月18日 下午8:05:54
     * @author fiend
     */
    private JSONObject getErrorJson(String interfaceOrderNumber, String localOrderNumber, String queuetoken,
            String reqtoken, String queue_type) {
        JSONObject errJsonObject = new JSONObject();
        errJsonObject.put("orderid", interfaceOrderNumber);
        errJsonObject.put("transactionid", localOrderNumber);
        errJsonObject.put("queuetoken", queuetoken);
        errJsonObject.put("reqtoken", reqtoken);
        errJsonObject.put("queue_status", 2);
        errJsonObject.put("queue_type", queue_type);
        errJsonObject.put("queue_start_time", "");//yyyyMMddHHmmss
        errJsonObject.put("queue_collect_time", "");//yyyyMMddHHmmss
        errJsonObject.put("queue_wait_time", "-1");
        errJsonObject.put("queue_wait_nums", "-1");
        return errJsonObject;
    }
}
