package com.ccservice.b2b2c.atom.servlet.TongChengTrain.paiduiChange;

import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.MessageListener;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;

/**
 * 改签排队监听
 * @author WH
 * @time 2016年2月18日 上午11:54:50
 * @version 1.0
 */

public class TrainChangeOrderPaiduiMessgeListener implements MessageListener {

    public void onMessage(Message message) {
        try {
            //获取字符串
            String sendMsg = ((TextMessage) message).getText();
            //日志
            WriteLog.write("TrainChangeOrderPaiduiMessgeListener", "sendMsg:" + sendMsg);
            //字符串转为JSONObject
            JSONObject json = JSONObject.parseObject(sendMsg);
            //解析数据
            int idx = json.getIntValue("idx");
            long changeId = json.getLongValue("changeId");
            long paiDuiId = json.getLongValue("paiDuiId");
            int refundOnline = json.getIntValue("refundOnline");
            boolean isLastPaidui = json.getBooleanValue("isLastPaidui");
            //核心处理
            new TrainChangeOrderPaiDuiUtil().paiDuiOperate(changeId, paiDuiId, idx, isLastPaidui, refundOnline);
        }
        catch (Exception e) {
        }
    }
}