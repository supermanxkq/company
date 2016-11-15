package com.ccservice.b2b2c.atom.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.util.HttpUtils;
import com.pay.config.AlipayConfig;

/**
 * 支付宝查询交易记录
 * @author wzc
 * @time   2016年1月25日 下午7:27:47
 */
public class AlipayRecordSearch extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Map<String, PayEntryInfo> accountInfo = null;

    @Override
    public void init() throws ServletException {
        accountInfo = new HashMap<String, PayEntryInfo>();
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        int rand = new Random().nextInt(10000000);
        response.setCharacterEncoding("UTF-8");
        request.setCharacterEncoding("UTF-8");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        String line = "";
        StringBuffer buf = new StringBuffer(1024);
        while ((line = br.readLine()) != null) {
            buf.append(line);
        }
        String JsonStr = buf.toString();
        String responseStr = "-1";
        PrintWriter out = response.getWriter();
        WriteLog.write("d支付宝", rand + ":请求：" + JsonStr);
        if (JsonStr != null && JsonStr.length() > 0) {
            JSONObject getMsg = JSONObject.parseObject(JsonStr);
            String cmd = getMsg.getString("cmd");
            if ("PayDetail".equals(cmd)) {
                responseStr = refundMsg(getMsg, rand, "1");
            }
            else if ("RefundDetail".equals(cmd)) {
                responseStr = refundMsg(getMsg, rand, "2");
            }
        }
        WriteLog.write("d支付宝", rand + ":响应：" + responseStr);
        out.print(responseStr);
        out.flush();
        out.close();
    }

    /**
     * 
     * @author wzc
     * @date 2016年3月8日 下午7:07:43
     */
    public PayEntryInfo findAlipayInfo(String account) {
        PayEntryInfo info = null;
        if (accountInfo == null) {
            accountInfo = new HashMap<String, PayEntryInfo>();
        }
        if (accountInfo.containsKey(account)) {
            info = accountInfo.get(account);
        }
        else {
            String sql = "select  PID,[Key],C_ACCOUNT from T_PAYACCOUNTINFO with(nolock) where C_ACCOUNT='" + account
                    + "'";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            info = new PayEntryInfo();
            if (list.size() == 1) {
                Map map = (Map) list.get(0);
                String pid = map.get("PID") == null ? "" : map.get("PID").toString();
                String keystr = map.get("Key") == null ? "" : map.get("Key").toString();
                String sellemail = map.get("C_ACCOUNT") == null ? "" : map.get("C_ACCOUNT").toString();
                info.setPid(pid);
                info.setKey(keystr);
                info.setSellemail(sellemail);
                accountInfo.put(account, info);
            }
        }
        return info;
    }

    public static void main(String[] args) {
        JSONObject obj = new JSONObject();
        obj.put("payAccount", "qcyg066@51kongtie.com");
        obj.put("TradeNumber", "");
        //2016032121001004920213337062
        //        obj.put("Wnumber", "qua-p-102043882298");
        obj.put("Wnumber", "qua-p-96179454097");
        String msg = new AlipayRecordSearch().refundMsg(obj, 0, "2");
        System.out.println(msg);
    }

    /**
     * 
     * @author wzc
     * @date 2016年3月24日 下午3:57:41
     * @param busstype 1 支付  2 退款
     */
    public String refundMsg(JSONObject getMsg, int rand, String busstype) {
        PayEntryInfo info = null;
        String payAccount = getMsg.getString("payAccount");
        String TradeNumber = getMsg.containsKey("TradeNumber") ? getMsg.getString("TradeNumber") : "";
        String Wnumber = getMsg.containsKey("Wnumber") ? getMsg.getString("Wnumber") : "";
        //        if (payAccount != null) {
        //            info = findAlipayInfo(payAccount);
        //        }
        if (info != null || true) {
            String account = "qcyg066@51kongtie.com";
            String pid = "2088911183863084";
            String key = "kifa5xcy11h213vsrq46ngrn7xi1ze3w";
            //            String pid = info.getPid();
            //            String key = info.getKey();
            // GET方式提交支付请求
            Map<String, String> params = new HashMap<String, String>();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 0);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startTime = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
            String endDate = sdf.format(cal.getTime());
            System.out.println("startTime:" + startTime + ",endDate:" + endDate);
            //增加基本配置
            params.put("paygateway", "https://mapi.alipay.com/gateway.do?");
            params.put("service", "account.page.query");
            params.put("partner", pid);
            params.put("_input_charset", AlipayConfig.getInstance().getCharSet());
            params.put("page_no", 1 + "");
            params.put("gmt_start_time", "");
            params.put("gmt_end_time", "");
            params.put("gmt_start_time", "");
            params.put("gmt_end_time", "");
            params.put("logon_id", payAccount);//交易收款账户
            params.put("iw_account_log_id", "");//账务流水号340005462320
            params.put("trade_no", TradeNumber);//业务流水号2012050726014177
            params.put("merchant_out_order_no", Wnumber);//商户订单号 201205131708360139
            params.put("deposit_bank_no", "");//充值网银流水号
            params.put("page_size", "100");//小于等于 5000 的正整数。为空或者大于 5000 时，默认为 5000。
            params.put("trans_code", "");//交易类型代码
            String ItemUrl_Get = com.alipay.util.Payment.CreateUrl(params, key);
            System.out.println(ItemUrl_Get);
            WriteLog.write("alipay_pay_Train", ItemUrl_Get);
            String msg = HttpUtils.Get_https(ItemUrl_Get);
            WriteLog.write("alipay_pay_Train", msg);
            return msg;
        }
        else {
            return "无账户信息";
        }

    }
}
