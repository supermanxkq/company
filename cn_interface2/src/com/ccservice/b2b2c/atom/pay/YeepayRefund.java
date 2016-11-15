package com.ccservice.b2b2c.atom.pay;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.refund.handle.RefundHandle;
import com.ccservice.b2b2c.atom.refund.helper.Refundhelper;
import com.ccservice.b2b2c.atom.refund.helper.Refundinfo;
import com.ccservice.b2b2c.ben.Refundtrade;
import com.pay.config.YeepayConfig;
import com.yeepay.interFace.YeepayInterFace;

public class YeepayRefund extends RefundSupport implements Refund {
    public YeepayRefund(HttpServletRequest request, HttpServletResponse response, Refundhelper refundhelper) {
        super(request, response, refundhelper);
    }

    @Override
    public void refund() {
        WriteLog.write("易宝退款", refundhelper.getOrdernumber() + "通过YeepayRefund执行退款操作");
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("p2_Order", this.refundhelper.getOrdernumber());
        Refundinfo rf = refundhelper.getRefundinfos().get(0);
        parameterMap.put("pb_TrxId", rf.getTradeno());//易宝支付平台产生的交易流水号，每笔订单唯一
        parameterMap.put("p3_Amt", rf.getRefundprice() + "");
        parameterMap.put("p5_Desc", "");//退款说明
        String keyValue = YeepayConfig.getInstance().getKey();
        String url = YeepayConfig.getEposReqUrl();
        Map fixParameter = YeepayConfig.getEposRefundFixParameter();
        String[] hmacOrder = YeepayConfig.getEposRefundHmacOrder();
        String[] backHmacOrder = YeepayConfig.getEposRefundBackHmacOrder();
        Refundtrade refundtrade = this.createRefundtrade(
                "tradeno:" + rf.getTradeno() + ";refund:" + rf.getRefundprice(), refundhelper.getRefundinfos());
        try {
            Map map = YeepayInterFace.getRequestBackMap(parameterMap, keyValue, url, fixParameter, hmacOrder,
                    backHmacOrder);
            Boolean checkHmac = (Boolean) map.get("checkHmac");
            Map parameter = (Map) map.get("parameter");
            if (Boolean.TRUE.equals(checkHmac)) {
                boolean success = false;
                String code = parameter.get("r1_Code").toString();
                String batch_no = parameter.get("r2_TrxId").toString();
                if ("1".equals(code)) {
                    success = true;
                    WriteLog.write("易宝退款", "退款成功");
                }
                try {
                    RefundHandle refundhandle = (RefundHandle) Class.forName(
                            RefundHandle.class.getPackage().getName() + "." + refundtrade.getHandleclass())
                            .newInstance();
                    refundhandle.refundedHandle(success, refundhelper.getOrderid(), batch_no);
                }
                catch (Exception e) {
                    WriteLog.write("易宝退款", batch_no + "退款成功 订单状态更改异常" + e.fillInStackTrace());
                }
                WriteLog.write("易宝退款", "业务类型=" + parameter.get("r0_Cmd") + "");
                WriteLog.write("易宝退款", "提交结果=" + parameter.get("r1_Code") + "");
                WriteLog.write("易宝退款", "退款交易流水号=" + parameter.get("r2_TrxId") + "");
                WriteLog.write("易宝退款", "退款金额=" + parameter.get("r3_Amt") + "");
                WriteLog.write("易宝退款", "交易币种=" + parameter.get("r4_Cur") + "");
            }
            else {
                WriteLog.write("易宝退款", "交易信息被篡改");
            }
        }
        catch (Exception e) {
            WriteLog.write("易宝退款", "Refund fail:很抱歉，未支付成功的交易流水号无法退款" + e.getMessage());
        }

    }

}
