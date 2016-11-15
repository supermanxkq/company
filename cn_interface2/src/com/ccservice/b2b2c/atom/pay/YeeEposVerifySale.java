package com.ccservice.b2b2c.atom.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.handle.NotifyHandleSupport;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.ccservice.b2b2c.util.HttpUtils;
import com.pay.config.YeepayConfig;
import com.yeepay.interFace.DigestUtilInterFace;
import com.yeepay.util.ProcessUtil;
import com.yeepay.util.UpgradeMap;

/**
 * 
 * @author wzc
 * 易宝信用卡验证码消费接口
 *
 */
public class YeeEposVerifySale extends NotifyHandleSupport {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String requesturl = "https://www.yeepay.com/app-merchant-proxy/command";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        WriteLog.write("无卡支付支付", "无卡支付验证码验证开始");
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("p0_Cmd", "EposVerifySale");// 固定值“EposVerifySale”。
        parameterMap.put("p1_MerId", YeepayConfig.getInstance().getPartnerID());// 商户编号
        parameterMap.put("p2_Order", request.getParameter("selfOrderid"));// 商户订单号
        parameterMap.put("pb_VerifyCode", request.getParameter("pa5_validecode"));// 验证码
        DigestUtilInterFace.addHmac(YeepayConfig.getYeeposVerifySalePar(), parameterMap, YeepayConfig.getInstance()
                .getKey());//验证码
        Map map = requestPayback(parameterMap);
        Boolean checkHmac = (Boolean) map.get("checkHmac");
        Map parameter = (Map) map.get("parameter");
        String returnstr = "";
        if (Boolean.TRUE.equals(checkHmac)) {
            if ("1".equals(parameter.get("r1_Code"))) {
                WriteLog.write("无卡支付支付", "支付成功");
                returnstr = "success";
                String ordernumber = parameter.get("r6_Order") + "";
                String trade_no = parameter.get("r2_TrxId") + "";
                String get_total_fee = request.getParameter("paymoney");
                if (get_total_fee == null) {
                    get_total_fee = "0";
                }
                else if (get_total_fee != null && "".equals(get_total_fee.trim())) {
                    get_total_fee = "0";
                }
                super.orderHandle(ordernumber, trade_no, Float.valueOf(get_total_fee), Paymentmethod.YEEPAY, "");
            }
            else {
                returnstr = "交易失败";
            }
            WriteLog.write("无卡支付支付", "业务类型=" + parameter.get("r0_Cmd"));
            WriteLog.write("无卡支付支付", "响应结果=" + parameter.get("r1_Code"));
            WriteLog.write("无卡支付支付", "易宝交易流水号=" + parameter.get("r2_TrxId"));
            WriteLog.write("无卡支付支付", "商户订单号=" + parameter.get("r6_Order"));
            WriteLog.write("无卡支付支付", "响应信息=" + parameter.get("errorMsg"));
            WriteLog.write("无卡支付支付", "银行订单号=" + parameter.get("ro_BankOrderId"));
        }
        else {
            returnstr = "交易失败";
            WriteLog.write("无卡支付支付", "验证码交易信息被篡改");
        }
        response.setContentType("text/plain; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.print(returnstr);
        out.flush();
        out.close();
    }

    private static Map requestPayback(Map<String, String> parameterMap) {
        try {
            WriteLog.write("无卡支付支付", "requestPayback");
            InputStream stream = HttpUtils.URLPost(requesturl, parameterMap);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            StringBuffer s = HttpUtils.getStringBufferFormBufferedReader(in);
            return getRequestBackMap(s.toString());
        }
        catch (IOException e) {
            WriteLog.write("无卡支付支付", "requestPayback异常：" + e.fillInStackTrace());
            e.printStackTrace();
        }
        return new HashMap();
    }

    public static Map getRequestBackMap(String reqResult) {
        WriteLog.write("无卡支付支付", "请求地址：" + reqResult);
        String[] backHmacOrder = YeepayConfig.getYeeEposVerifySaleParReturn();
        Map returnMap = new UpgradeMap();
        Map parameterMap = ProcessUtil.formatReqReturnString(reqResult, "\n", "=");
        parameterMap = ProcessUtil.urlDecodeMap(parameterMap);
        returnMap.put("checkHmac", Boolean.valueOf(DigestUtilInterFace.checkHmac(backHmacOrder, parameterMap,
                YeepayConfig.getInstance().getKey())));
        returnMap.put("parameter", parameterMap);
        return returnMap;
    }
}
