package com.ccservice.b2b2c.atom.pay.handle;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.pay.config.YeepayConfig;
import com.yeepay.interFace.YeepayInterFace;

/**
 * @author Administrator 支付支付通知接口 可优化，待优化：小寒
 * 
 */
@SuppressWarnings("serial")
public class YeepayNotifyHandle extends NotifyHandleSupport {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        WriteLog.write("易宝Epos支付通知", "易宝Epos支付结果通知");

        String returnstr = "faild";
        try {
            Map map = getEposCallbackMap(request);
            if (Boolean.TRUE.equals(map.get("checkHmac"))) {
                if (map.get("r1_Code").equals("1")) {
                    WriteLog.write("易宝Epos支付通知", "支付成功");
                    returnstr = "success";
                    String ordernumber = request.getParameter("r6_Order");
                    String trade_no = request.getParameter("r2_TrxId");
                    String get_total_fee = request.getParameter("r3_Amt");
                    super.orderHandle(ordernumber, trade_no, Float.valueOf(get_total_fee), Paymentmethod.YEEPAY, "");
                }
                else {
                    WriteLog.write("易宝Epos支付通知", "支付失败");
                }
                WriteLog.write("易宝Epos支付通知", "业务类型=" + request.getParameter("r0_Cmd"));
                WriteLog.write("易宝Epos支付通知", "提交结果=" + request.getParameter("r1_Code"));
                WriteLog.write("易宝Epos支付通知", "交易流水号=" + request.getParameter("r2_TrxId"));
                WriteLog.write("易宝Epos支付通知", "商户订单号=" + request.getParameter("r6_Order"));
                WriteLog.write("易宝Epos支付通知", "商户编号=" + request.getParameter("p1_MerId"));
                WriteLog.write("易宝Epos支付通知", "交易金额=" + request.getParameter("r3_Amt"));
                WriteLog.write("易宝Epos支付通知", "交易币种=" + request.getParameter("r4_Cur"));
                WriteLog.write("易宝Epos支付通知", "商品名称="
                        + new String(request.getParameter("r5_Pid").getBytes("iso-8859-1"), "gbk"));
                WriteLog.write("易宝Epos支付通知", "备注信息="
                        + new String(request.getParameter("r8_MP").getBytes("iso-8859-1"), "gbk"));
                WriteLog.write("易宝Epos支付通知", "通知类型=" + request.getParameter("r9_BType"));
                WriteLog.write("易宝Epos支付通知", "卡号对应的银行=" + request.getParameter("rb_BankId"));
                WriteLog.write("易宝Epos支付通知", "授权号=" + request.getParameter("rp_authno"));
                WriteLog.write("易宝Epos支付通知", "下单时间=" + request.getParameter("rp_PayDate"));
                WriteLog.write("易宝Epos支付通知", "交易时间=" + request.getParameter("ru_Trxtime"));
                WriteLog.write("易宝Epos支付通知",
                        "错误信息=" + new String(request.getParameter("errorMsg").getBytes("iso-8859-1"), "gbk"));
            }
            else {
                WriteLog.write("易宝Epos支付通知", "交易信息被篡改");
            }
        }
        catch (Exception ex) {
        }

        try {
            PrintWriter out = response.getWriter();
            out.print(returnstr);
            out.flush();
            out.close();
        }
        catch (IOException i) {
            WriteLog.write("易宝Epos支付通知", "××支付宝支付成功IO异常" + i.fillInStackTrace());
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        this.doGet(request, response);
    }

    public Map getEposCallbackMap(HttpServletRequest request) {
        String keyValue = YeepayConfig.getInstance().getKey();
        String[] callbackHmacOrder = YeepayConfig.getEposCallbackHmacOrder();
        return YeepayInterFace.getCallbackMap(request, keyValue, callbackHmacOrder);
    }

}
