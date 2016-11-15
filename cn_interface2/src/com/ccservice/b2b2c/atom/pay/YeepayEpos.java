package com.ccservice.b2b2c.atom.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.helper.Payhelper;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.traderecord.Traderecord;
import com.ccservice.b2b2c.util.HttpUtils;
import com.pay.config.YeepayConfig;
import com.yeepay.interFace.DigestUtilInterFace;
import com.yeepay.util.ProcessUtil;
import com.yeepay.util.UpgradeMap;

/**
 * @author hanmh
 * 易宝无卡支付
 *
 */
public class YeepayEpos extends PaySupport implements Pay {
    public YeepayEpos(HttpServletRequest request, HttpServletResponse response, Payhelper payhelper) {
        super(request, response, payhelper);
    }

    private static final String requesturl = "https://www.yeepay.com/app-merchant-proxy/command";

    public void pay(float factorage) throws Exception {
        WriteLog.write("无卡支付支付", "无卡支付开始，支付金额：" + (payhelper.getOrderprice() + factorage));
        Map<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("p0_Cmd", "EposSale");// 固定值“EposSale”。
        parameterMap.put("p1_MerId", YeepayConfig.getInstance().getPartnerID());// 商户编号
        String selfOrderid = payhelper.getOrdernumber() + "Fg" + payhelper.getHandleName();// 商户订单号
        WriteLog.write("无卡支付支付", "商户订单号:" + selfOrderid);
        parameterMap.put("p2_Order", selfOrderid);// 商户订单号
        String paymoney = payhelper.getOrderprice() + factorage + "";
        parameterMap.put("p3_Amt", paymoney);// 消费金额
        parameterMap.put("p4_Cur", "CNY");// 交易币种 是 - 固定值 “CNY”。
        // p5_Pid 商品名称 否 // Max(50) // 此参数如用到中文,请注意转码。
        parameterMap.put("p5_Pid", payhelper.getOrdername());
        //		String notify_url = "http://211.103.207.133:8080/cn_interface/YeepayNotifyHandle";
        String notify_url = "http://" + request.getServerName() + ":" + request.getServerPort()
                + "/cn_interface/YeepayNotifyHandle";
        // 接收支付结果地址 Max(200) 交易完成后会将交易结果以HTTP协议的形式请求到该地址上，用request接收结果。
        parameterMap.put("p8_Url", notify_url);
        parameterMap.put("pa_CredType", request.getParameter("pa_CredType"));// 证件类型
        parameterMap.put("pb_CredCode", request.getParameter("pb_CredCode"));// 证件号码。此参数如用到中文,请注意转码。
        parameterMap.put("pd_FrpId", request.getParameter("pd_FrpId"));// 银行编码
        parameterMap.put("pe_BuyerTel", request.getParameter("pe_BuyerTel"));// 消费者手机号。
        String name = new String(request.getParameter("pf_BuyerName").getBytes("ISO8859-1"), "UTF-8");
        parameterMap.put("pf_BuyerName", name);// 消费者姓名		

        parameterMap.put("pt_ActId", request.getParameter("pt_ActId"));// 信用卡卡号。
        parameterMap.put("pa2_ExpireYear", request.getParameter("pa2_ExpireYear"));// /信用卡有效期（年）必须在2007-2099
        // 之间,如：2009。
        parameterMap.put("pa3_ExpireMonth", request.getParameter("pa3_ExpireMonth"));// 信用卡有效期（月）, 如：1。
        parameterMap.put("pa4_CVV", request.getParameter("pa4_CVV"));// 信用卡背面的3或4位cvv2码。
        parameterMap.put("prisk_TerminalCode", "");//终端号
        parameterMap.put("prisk_Param", "");//终端号
        parameterMap.put("pr_NeedResponse", "1");// /需要应答 是 固定值“1”。
        // // parameterMap.put("hmac","1");///产生hmac需要两个参数，并调用相关API.参数1:
        // STR，列表中的参数值按照签名顺序拼接所产生的字符串，注意null要转换为“”，并确保无乱码.参数2: 商户密钥.
        DigestUtilInterFace.addHmac(YeepayConfig.getEposReqHmacOrder(), parameterMap, YeepayConfig.getInstance()
                .getKey());
        Map map = requestPayback(parameterMap);
        Boolean checkHmac = (Boolean) map.get("checkHmac");
        Map parameter = (Map) map.get("parameter");
        String returnstr = "";
        Map result = new HashMap();
        WriteLog.write("无卡支付支付", "checkHmac:" + checkHmac);
        if (Boolean.TRUE.equals(checkHmac)) {
            if ("1".equals(parameter.get("r1_Code"))) {
                WriteLog.write("无卡支付支付", "支付成功");
                createOrderrc(parameter.get("r2_TrxId").toString());
                result.put("orderid", selfOrderid);
                result.put("paymoney", paymoney);
                result.put("msg", "创建消费订单成功");
            }
            else if (getSMSFlag(parameter.get("r1_Code") + "")) {
                WriteLog.write("无卡支付支付", "支付成功");
                createOrderrc(parameter.get("r2_TrxId").toString());
                result.put("orderid", selfOrderid);
                result.put("paymoney", paymoney);
                result.put("msg", "创建消费订单成功");
            }
            else {
                WriteLog.write("无卡支付支付", "支付失败");
                result.put("orderid", "");
                result.put("paymoney", "");
                result.put("msg", "" + parameter.get("errorMsg"));
            }
            WriteLog.write("无卡支付支付", "业务类型=" + parameter.get("r0_Cmd"));
            WriteLog.write("无卡支付支付", "提交结果=" + parameter.get("r1_Code"));
            WriteLog.write("无卡支付支付", "交易流水号=" + parameter.get("r2_TrxId"));
            WriteLog.write("无卡支付支付", "商户订单号=" + parameter.get("r6_Order"));
            WriteLog.write("无卡支付支付", "商户编号=" + parameter.get("p1_MerId"));
            WriteLog.write("无卡支付支付", "交易金额=" + parameter.get("r3_Amt"));
            WriteLog.write("无卡支付支付", "交易币种=" + parameter.get("r4_Cur"));
            WriteLog.write("无卡支付支付", "商品名称=" + parameter.get("r5_Pid"));
            WriteLog.write("无卡支付支付", "备注信息=" + parameter.get("r8_MP"));
            WriteLog.write("无卡支付支付", "交易结果返回类型=" + parameter.get("r9_BType"));
            WriteLog.write("无卡支付支付", "卡号对应的银行=" + parameter.get("rb_BankId"));
            WriteLog.write("无卡支付支付", "授权号=" + parameter.get("rp_authno"));
            WriteLog.write("无卡支付支付", "下单时间=" + parameter.get("rp_PayDate"));
            WriteLog.write("无卡支付支付", "交易时间=" + parameter.get("ru_Trxtime"));
            WriteLog.write("无卡支付支付", "错误信息=" + parameter.get("errorMsg"));
        }
        else {
            result.put("orderid", "");
            result.put("paymoney", "");
            result.put("msg", "");
            WriteLog.write("无卡支付支付", "交易信息被篡改");
        }
        returnstr = JSONObject.fromObject(result).toString();
        response.setContentType("text/plain; charset=utf-8");
        PrintWriter out = response.getWriter();
        out.print(returnstr);
        System.out.println("---------------" + returnstr + "---------------");
        out.flush();
        out.close();

    }

    public boolean getSMSFlag(String code) {
        boolean flag = false;
        if (code != null) {
            if ("81100".equals(code) || "81201".equals(code) || "81202".equals(code) || "81203".equals(code)) {
                flag = true;
            }
        }
        return flag;
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
        String[] backHmacOrder = YeepayConfig.getEposReqBackHmacOrder();
        Map returnMap = new UpgradeMap();
        Map parameterMap = ProcessUtil.formatReqReturnString(reqResult, "\n", "=");
        parameterMap = ProcessUtil.urlDecodeMap(parameterMap);
        returnMap.put("checkHmac", Boolean.valueOf(DigestUtilInterFace.checkHmac(backHmacOrder, parameterMap,
                YeepayConfig.getInstance().getKey())));
        returnMap.put("parameter", parameterMap);
        return returnMap;
    }

    /**
     * @param serialnumber
     *            日志记录
     */
    public void createOrderrc(String serialnumber) {
        Traderecord traderecord = new Traderecord();
        try {
            traderecord.setCreateuser("Yeepay");
            traderecord.setGoodsname(payhelper.getOrdername());
            traderecord.setCode(serialnumber);// 外部订单号。
            traderecord.setOrdercode(payhelper.getOrdernumber());
            traderecord.setPayname("易宝");
            traderecord.setPaytype(0);// 0支付宝 1财付通
            traderecord.setState(0);// 0等待支付1支付成功2支付失败
            traderecord.setTotalfee((int) payhelper.getOrderprice());// 支付金额分为单位
            traderecord.setType(payhelper.getTradetype());// 订单类型
            traderecord.setPaymothed("Epos");// 支付方式
            traderecord.setBankcode(request.getParameter("pd_FrpId"));// 支付银行
            traderecord = Server.getInstance().getMemberService().createTraderecord(traderecord);
        }
        catch (Exception e) {
            WriteLog.write("无卡支付支付", "交易记录失败" + e.fillInStackTrace());
        }
        WriteLog.write("无卡支付支付", "提交无卡支付.银行编码：" + request.getParameter("pd_FrpId") + ";交易流水号：" + serialnumber);
    }

    @Override
    public void pay(long agentid) throws Exception {
        // TODO Auto-generated method stub

    }
}
