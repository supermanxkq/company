package com.ccservice.b2b2c.atom.taobao.thread;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.taobao.TaobaoRefundMethod;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class MyThreadRefund extends Thread {
    private AsyncContext ctx;

    private JSONObject jsonString;

    public MyThreadRefund(AsyncContext ctx, JSONObject jsonString) {
        this.ctx = ctx;
        this.jsonString = jsonString;
    }

    public void run() {
        TaobaoRefundMethod.getInstance();
        String main_biz_order_id = jsonString.containsKey("main_biz_order_id") ? jsonString
                .getString("main_biz_order_id") : "";
        String ttp_id = jsonString.containsKey("ttp_id") ? jsonString.getString("ttp_id") : "";
        int refund_type = jsonString.containsKey("refund_type") ? jsonString.getIntValue("refund_type") : 0;
        String seller_id = jsonString.containsKey("seller_id") ? jsonString.getString("seller_id") : "";
        String trade_no = jsonString.containsKey("trade_no") ? jsonString.getString("trade_no") : "";
        String user_id = jsonString.containsKey("user_id") ? jsonString.getString("user_id") : "";
        String refund_fee = jsonString.containsKey("refund_fee") ? jsonString.getString("refund_fee") : "";
        String apply_id = jsonString.containsKey("apply_id") ? jsonString.getString("apply_id") : "";
        int agree_refund = jsonString.containsKey("agree_refund") ? (jsonString.getBooleanValue("agree_refund") ? 1 : 2)
                : 0;
        int refuse_reason = jsonString.containsKey("refuse_reason") ? jsonString.getIntValue("refuse_reason") : 0;
        JSONArray tickets = jsonString.getJSONArray("tickets");
        if (ElongHotelInterfaceUtil.StringIsNull(main_biz_order_id) || ElongHotelInterfaceUtil.StringIsNull(ttp_id)
                || ElongHotelInterfaceUtil.StringIsNull(seller_id) || ElongHotelInterfaceUtil.StringIsNull(trade_no)
                || ElongHotelInterfaceUtil.StringIsNull(user_id) || ElongHotelInterfaceUtil.StringIsNull(refund_fee)
                || ElongHotelInterfaceUtil.StringIsNull(apply_id) || agree_refund == 0) {
            getResponeOut(ctx, "参数缺失");
            return;
        }
        boolean isSuccess = TaobaoRefundMethod.getInstance().delete(apply_id, refund_fee, agree_refund, refuse_reason,
                refund_type, "");
        if (!isSuccess) {
            getResponeOut(ctx, "修改DB失败_可能原因_已经超时或已经回调taobao");
            return;
        }
        String callbackResult = null;
        callbackResult = new TaobaoHotelInterfaceUtil().refundfee(main_biz_order_id, ttp_id, refund_type, "",
                seller_id, trade_no, user_id, refund_fee, apply_id, agree_refund, refuse_reason);
        if (callbackResult == null) {
            TaobaoRefundMethod.getInstance().callbackResultIntoDB(apply_id, false);
            getResponeOut(ctx, "回调失败_回调结果为null");
            return;
        }
        else if ("SUCCESS".equalsIgnoreCase(callbackResult)) {
            TaobaoRefundMethod.getInstance().callbackResultIntoDB(apply_id, true);
            getResponeOut(ctx, callbackResult);
            return;
        }
        else {
            TaobaoRefundMethod.getInstance().callbackResultIntoDB(apply_id, false);
            getResponeOut(ctx, "回调失败_回调结果为:" + callbackResult);
            return;
        }
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
            ExceptionUtil.writelogByException("ERROR_TaobaoRefundPriceServlet", e);
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
