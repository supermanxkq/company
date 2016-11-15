package com.ccservice.b2b2c.atom.taobao.thread;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class MyThreadRefundTomas extends Thread {
    private AsyncContext ctx;

    private JSONObject jsonString;

    public MyThreadRefundTomas(AsyncContext ctx, JSONObject jsonString) {
        this.ctx = ctx;
        this.jsonString = jsonString;
    }

    public void run() {
        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put("success", false);
        resultJsonObject.put("taobaoMsg", "");
        String result = "";
        try {
            result = new TaobaoHotelInterfaceUtil().taobaoDrawerNotice(jsonString);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (result == null) {
            resultJsonObject.put("taobaoMsg", "null");
        }
        else {
            try {
                if (JSONObject.parseObject(result).getJSONObject("train_agent_returnticket_confirm_response")
                        .getBooleanValue("is_success")) {
                    resultJsonObject.put("success", true);
                    resultJsonObject.put("taobaoMsg", result);
                }
                else {
                    resultJsonObject.put("success", false);
                    resultJsonObject.put("taobaoMsg", result);
                }
            }
            catch (Exception e) {
                resultJsonObject.put("success", false);
                resultJsonObject.put("taobaoMsg", result);
                resultJsonObject.put("Exception", e.getMessage());
                ExceptionUtil.writelogByException("ERROR_MyThreadRefundTomas", e);
            }
        }
        getResponeOut(ctx, resultJsonObject.toString());
    }

    //生成返回信息
    private void getResponeOut(AsyncContext ctx, String result) {
        try {
            ServletResponse response = ctx.getResponse();
            //编码
            response.setCharacterEncoding("UTF-8");
            //输出
            response.getWriter().write(result);
        }
        catch (Exception e) {
            ExceptionUtil.writelogByException("ERROR_MyThreadRefundTomas", e);
        }
        finally {
            try {
                ctx.complete();
            }
            catch (Exception e) {
            }
        }
    }
}
