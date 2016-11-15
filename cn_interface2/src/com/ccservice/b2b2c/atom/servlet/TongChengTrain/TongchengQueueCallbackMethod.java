package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class TongchengQueueCallbackMethod {
    /**
     * 
     * 
     * @param json  传过来的JSON
     * @param partnerid 接口商户名
     * @param key   接口商户密钥
     * @param reqtime   当前时间yyyyMMddHHmmssfff
     * @return
     * @time 2016年4月19日 下午1:38:32
     * @author fiend
     */
    public String operate(JSONObject json, String partnerid, String key, String reqtime, String callbackUrl) {
        String result = "";
        try {
            //数字签名
            String sign = ElongHotelInterfaceUtil.MD5(partnerid + reqtime + ElongHotelInterfaceUtil.MD5(key));
            //同程订单号
            String orderid = json.containsKey("interfaceOrderNumber") ? json.getString("interfaceOrderNumber") : "";
            //交易单号
            String transactionid = json.containsKey("localOrderNumber") ? json.getString("localOrderNumber") : "";
            //普通占座或改签请求时传入的reqtoken
            String reqtoken = json.containsKey("reqtoken") ? json.getString("reqtoken") : "";
            //排队查询特征值   主动回调时为空
            String queuetoken = "";
            //排队状态：1-排队中，2-未排队
            String queue_status = "1";
            //排队类型：1-普通占座排队  2-改签占座排队
            String queue_type = json.containsKey("queue_type") ? json.getString("queue_type") : "";
            //排队开始时间： yyyyMMddHHmmss（格式）
            String queue_start_time = json.containsKey("queue_start_time") ? json.getString("queue_start_time") : "";
            //排队信息采集时间：yyyyMMddHHmmss（格式）
            String queue_collect_time = json.containsKey("queue_collect_time") ? json.getString("queue_collect_time")
                    : "";
            //排队等待时间    单位：秒    没有返回“-1”
            String queue_wait_time = json.containsKey("queue_wait_time") ? json.getString("queue_wait_time") : "-1";
            //排队等待人数    没有排队返回“-1”
            String queue_wait_nums = json.containsKey("queue_wait_nums") ? json.getString("queue_wait_nums") : "-1";
            JSONObject callbackJsonObject = new JSONObject();
            callbackJsonObject.put("reqtime", reqtime);
            callbackJsonObject.put("sign", sign);
            callbackJsonObject.put("orderid", orderid);
            callbackJsonObject.put("transactionid", transactionid);
            callbackJsonObject.put("reqtoken", reqtoken);
            callbackJsonObject.put("queuetoken", queuetoken);
            callbackJsonObject.put("queue_status", queue_status);
            callbackJsonObject.put("queue_type", queue_type);
            callbackJsonObject.put("queue_start_time", queue_start_time);
            callbackJsonObject.put("queue_collect_time", queue_collect_time);
            callbackJsonObject.put("queue_wait_time", queue_wait_time);
            callbackJsonObject.put("queue_wait_nums", queue_wait_nums);
            WriteLog.write("tc同城排队信息回调接口",
                    orderid + "--->" + callbackUrl + "?queueinfo=" + callbackJsonObject.toString());
            result = SendPostandGet.submitPost(callbackUrl, "queueinfo=" + callbackJsonObject.toString(), "UTF-8")
                    .toString();
            WriteLog.write("tc同城排队信息回调接口", orderid + "--->" + result);
        }
        catch (Exception e) {
            WriteLog.write("tc同城排队信息回调接口_ERROR", json.toString());
            ExceptionUtil.writelogByException("tc同城排队信息回调接口_ERROR", e);
        }
        return result;
    }

}
