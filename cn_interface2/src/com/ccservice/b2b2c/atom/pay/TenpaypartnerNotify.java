package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.pay.config.TenpayConfig;
import com.tenpay.util.TenpayUtil;

/**
 * 
 * @author wzc
 * 财付通支付商圈签约通知
 *
 */
public class TenpaypartnerNotify extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        WriteLog.write("Tenpay_支付商圈签约", "**************************");
        String partner = TenpayConfig.getInstance().getPartnerID(); // 支付宝合作伙伴id
        String key = TenpayConfig.getInstance().getKey(); //
        SortedMap params = new TreeMap();
        // 获得POST 过来参数设置到新的params中
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            params.put(name, valueStr);
            WriteLog.write("Tenpay_支付商圈签约", name + ":" + valueStr);
        }
        String mysign = TenpayUtil.createSign(params, key);
        try {
            if (mysign.equals(params.get("cftsign"))) {
                String tenpayacount = params.get("uin").toString();
                if (tenpayacount.contains("DELETE") || tenpayacount.contains("delete")) {
                    WriteLog.write("Tenpay_支付商圈签约", tenpayacount + "非法攻击");
                    tenpayacount = "";
                }
                if (!"".equals(tenpayacount) && tenpayacount.length() > 0) {
                    if ("1".equals(params.get("status"))) {//增加支付商圈
                        String sql = "UPDATE T_CUSTOMERAGENT SET C_ISTENPAYPARTNER=1 where C_TENPAYACCOUNT='"
                                + tenpayacount + "'";
                        WriteLog.write("Tenpay_支付商圈签约", tenpayacount + "签约");
                        Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                    }
                    else if ("2".equals(params.get("status"))) {//用户接触支付商圈
                        String sql = "UPDATE T_CUSTOMERAGENT SET C_ISTENPAYPARTNER=0 where C_TENPAYACCOUNT='"
                                + tenpayacount + "'";
                        WriteLog.write("Tenpay_支付商圈签约", tenpayacount + "解约");
                        Server.getInstance().getSystemService().findMapResultBySql(sql, null);

                    }
                    else {
                        WriteLog.write("Tenpay_支付商圈签约", "财付通账户支付商圈签约状态不合法：" + params.get("status") + ":::::::"
                                + tenpayacount);
                    }
                }
            }
            else {
                WriteLog.write("Tenpay_支付商圈签约", "签名不正确");
            }
        }
        catch (Exception e) {
            WriteLog.write("Tenpay_支付商圈签约", "财付通账户签约异常:" + e.fillInStackTrace() + "");
            e.printStackTrace();
        }
    }
}
