package com.ccservice.b2b2c.atom.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.util.HttpUtils;
import com.pay.config.YeepayConfig;
import com.yeepay.interFace.DigestUtilInterFace;
import com.yeepay.util.ProcessUtil;
import com.yeepay.util.UpgradeMap;

/**
 * 
 * @author wzc
 * 易宝二次获取短信验证码
 */
public class YeeEposVerifyCodeReceive extends HttpServlet {

    private static final String requesturl = "https://www.yeepay.com/app-merchant-proxy/command";

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        // TODO Auto-generated method stub
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("p0_Cmd", "EposVerifyCodeReceive");// 固定值“EposVerifySale”。
        parameterMap.put("p1_MerId", YeepayConfig.getInstance().getPartnerID());// 商户编号
        parameterMap.put("p2_Order", request.getParameter("selfOrderid"));// 商户订单号
        WriteLog.write("无卡支付支付", "p0_Cmd:EposVerifyCodeReceive");
        WriteLog.write("无卡支付支付", "p1_MerId:" + YeepayConfig.getInstance().getPartnerID());
        WriteLog.write("无卡支付支付", "p2_Order:" + request.getParameter("selfOrderid"));
        WriteLog.write("无卡支付支付", "key:" + YeepayConfig.getInstance().getKey());
        DigestUtilInterFace.addHmac(YeepayConfig.getYeeposCode(), parameterMap, YeepayConfig.getInstance().getKey());//验证码
        Map map = requestPayback(parameterMap);
        Boolean checkHmac = (Boolean) map.get("checkHmac");
        Map parameter = (Map) map.get("parameter");
        String returnstr = "";
        Map result = new HashMap();
        WriteLog.write("无卡支付支付", "checkHmac:" + checkHmac);
        if (Boolean.TRUE.equals(checkHmac)) {
            if ("1".equals(parameter.get("r1_Code"))) {
                result.put("msg", "获取短信验证码成功");
            }
            else {
                result.put("msg", "获取短信验证码失败");
            }
            WriteLog.write("无卡支付支付", "业务类型=" + parameter.get("r0_Cmd"));
            WriteLog.write("无卡支付支付", "响应结果=" + parameter.get("r1_Code"));
            WriteLog.write("无卡支付支付", "商户编号=" + parameter.get("p1_MerId"));
            WriteLog.write("无卡支付支付", "商户订单号=" + parameter.get("r6_Order"));
            WriteLog.write("无卡支付支付", "响应信息=" + parameter.get("errorMsg"));
        }
        else {
            result.put("msg", "获取短信验证码失败");
            WriteLog.write("无卡支付支付", "交易信息被篡改");
        }
        returnstr = JSONObject.fromObject(result).toString();
        response.setContentType("text/plain; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.print(returnstr);
        out.flush();
        out.close();
    }

    private static Map requestPayback(Map<String, String> parameterMap) {
        try {
            InputStream stream = HttpUtils.URLPost(requesturl, parameterMap);
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));
            StringBuffer s = HttpUtils.getStringBufferFormBufferedReader(in);
            return getRequestBackMap(s.toString());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return new HashMap();
    }

    public static Map getRequestBackMap(String reqResult) {
        String[] backHmacOrder = YeepayConfig.getReturnEposCode();
        Map returnMap = new UpgradeMap();
        Map parameterMap = ProcessUtil.formatReqReturnString(reqResult, "\n", "=");
        parameterMap = ProcessUtil.urlDecodeMap(parameterMap);
        returnMap.put("checkHmac", Boolean.valueOf(DigestUtilInterFace.checkHmac(backHmacOrder, parameterMap,
                YeepayConfig.getInstance().getKey())));
        returnMap.put("parameter", parameterMap);
        return returnMap;
    }
}
