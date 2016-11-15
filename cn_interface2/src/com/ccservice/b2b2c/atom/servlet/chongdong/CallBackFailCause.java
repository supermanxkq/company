package com.ccservice.b2b2c.atom.servlet.chongdong;

import java.io.IOException;
import java.util.Random;

import org.apache.commons.httpclient.HttpException;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;

public class CallBackFailCause {
    public static void main(String[] args) {
        String result = "{\"code\": \"SYSTEM_ERROR\",\"message\": \"Auth approved.\",\"approvalCode\": \"b8377570-6223-41b4-b72c-fa594d4f0989\"}";
        CallBackFailCause callBackFailCause = new CallBackFailCause();
        callBackFailCause.dealreturndata("https://stage.wfinance.com.cn:10443/cdi/v2/transactions/submit", result, "");
    }

    /**
     * 分析返回结果，并处理
     * 杨荣强
     */
    public String dealreturndata(String congDongAuthUrl, String result, String jsonString) {
        int r1 = new Random().nextInt(10000000);
        String str = "";
        JSONObject jsonObject_1 = JSONObject.parseObject(result);
        String code = jsonObject_1.getString("code");
        String message = jsonObject_1.getString("message");
        if ("SUCCESS".equals(code)) {
            str = "SUCCESS";
        }
        else if ("SYSTEM_ERROR".equals(code)) {
            for (int i = 0; i < 3; i++) {
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                WormholeTradeCallBack wormholeTradeCallBack = new WormholeTradeCallBack();
                String resultString = "";
                try {
                    resultString = WormholeTradeCallBack.submitHttpclient(congDongAuthUrl, jsonString);
                }
                catch (HttpException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                if (resultString != null && !"".equals(resultString)) {
                    JSONObject jsonObjects = JSONObject.parseObject(resultString);
                    String code1 = jsonObjects.getString("code");
                    if ("SUCCESS".equals(code1)) {
                        str = "SUCCESS";
                        break;

                    }
                }
            }
            str = "系统异常";

        }
        else if ("PARTIAL_FAIL".equals(code)) {
            str = "部分数据提交失败";
        }
        else if ("INVALID_ACCOUNT_NUMBER".equals(code)) {
            str = "数据提交或请求授权的卡号有误";
        }
        else if ("LIMIT_EXCEED".equals(code)) {
            str = "请求的账号授信余额不足";
        }
        else if ("VALIDATION_ERROR".equals(code) && "一次上传交易数大于1000，请分批次上传！".equals(message)) {
            str = "数据提交数量过大";
        }
        else if ("VALIDATION_ERROR".equals(code) && "一次上传交易数小于1，请至少上传一条交易！".equals(message)) {
            str = "数据提交列表为空";
        }
        else if ("VALIDATION_ERROR".equals(code) && "具体的验证失败的信息。".equals(message)) {
            str = "各类数据验证错误";
        }
        WriteLog.write("回调虫洞交易接口_反馈结果", r1 + "------>反馈结果：" + result + "------>str" + str);
        return str;
    }
}
