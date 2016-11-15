package com.pay.config;

import java.util.HashMap;
import java.util.Map;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.PayInfo;

public class YeepayConfig extends PayConfig {

    private static YeepayConfig yeepayconfig;

    public static YeepayConfig getInstance() {
        if (yeepayconfig == null) {
            yeepayconfig = new YeepayConfig();
            PayInfo payinfo = Server.getInstance().getB2BSystemService().findPayInfoBytypeId(4);
            yeepayconfig.setKey(payinfo.getKey());
            yeepayconfig.setPartnerID(payinfo.getPartnerId());
            yeepayconfig.setSellerEmail(payinfo.getSellerEmail());
            yeepayconfig.setSelfaccount(payinfo.getAccounttype() == 0);
        }
        return yeepayconfig;
    }

    //	private static String merchantID = Configuration.getInstance().getValue("merchantID");
    //	private static String keyValue = Configuration.getInstance().getValue("keyValue");
    private static String eposReqUrl = "https://www.yeepay.com/app-merchant-proxy/command";

    private static String byteCharsetName = "gbk";

    public static String getByteCharsetName() {
        return byteCharsetName;
    }

    // 支付请求参数
    public static String[] getEposReqHmacOrder() {
        return new String[] { "p0_Cmd", "p1_MerId", "p2_Order", "p3_Amt", "p4_Cur", "p5_Pid", "p8_Url", "pa_CredType",
                "pb_CredCode", "pd_FrpId", "pe_BuyerTel", "pf_BuyerName", "pt_ActId", "pa0_Mode", "pa2_ExpireYear",
                "pa3_ExpireMonth", "pa4_CVV", "pa5_BankBranch", "pa6_CardType", "prisk_TerminalCode", "prisk_Param" };
    }

    // 正常支付
    public static String[] getYeepayReqHmacOrder() {
        return new String[] { "p0_Cmd", "p1_MerId", "p2_Order", "p3_Amt", "p4_Cur", "p5_Pid", "p6_Pcat", "p7_Pdesc",
                "p8_Url", "p9_SAF", "pa_MP", "pd_FrpId", "pr_NeedResponse", "keyValue" };
    }

    //信用卡验证码消费参数
    public static String[] getYeeposVerifySalePar() {
        return new String[] { "p0_Cmd", "p1_MerId", "p2_Order", "pb_VerifyCode" };
    }

    //二次获取验证码
    public static String[] getYeeposCode() {
        return new String[] { "p0_Cmd", "p1_MerId", "p2_Order" };
    }

    public static Map getEposReqFixParameter() {
        Map returnMap = new HashMap();
        returnMap.put("p0_Cmd", "EposTransaction");
        returnMap.put("p1_MerId", YeepayConfig.getInstance().getPartnerID());
        returnMap.put("p4_Cur", "CNY");
        returnMap.put("pr_NeedResponse", "1");
        return returnMap;
    }

    //----------------------------------------------------------------------------------------//

    // 查询请求参数
    public static String[] getEposQueryHmacOrder() {
        return new String[] { "p0_Cmd", "p1_MerId", "p2_Order", "pv_Ver", "p3_ServiceType" };
    }

    public static String[] getEposQueryBackHmacOrder() {
        return new String[] { "r0_Cmd", "r1_Code", "r2_TrxId", "r3_Amt", "r4_Cur", "r5_Pid", "r6_Order", "r8_MP",
                "rw_RefundRequestID", "rx_CreateTime", "ry_FinshTime", "rz_RefundAmount", "rb_PayStatus",
                "rc_RefundCount", "rd_RefundAmt" };
    }

    public static Map getEposQueryFixParameter() {
        Map returnMap = new HashMap();
        returnMap.put("p0_Cmd", "QueryOrdDetail");
        returnMap.put("p1_MerId", YeepayConfig.getInstance().getPartnerID());
        returnMap.put("pv_Ver", "3.0");
        return returnMap;
    }

    //----------------------------------------------------------------------------------------//

    // 退款请求及返回参数
    public static String[] getEposRefundHmacOrder() {
        return new String[] { "p0_Cmd", "p1_MerId", "p2_Order", "pb_TrxId", "p3_Amt", "p4_Cur", "p5_Desc" };
    }

    public static String[] getEposRefundBackHmacOrder() {
        return new String[] { "r0_Cmd", "r1_Code", "r2_TrxId", "r3_Amt", "r4_Cur" };

    }

    public static Map getEposRefundFixParameter() {
        Map returnMap = new HashMap();
        returnMap.put("p0_Cmd", "RefundOrd");
        returnMap.put("p1_MerId", YeepayConfig.getInstance().getPartnerID());
        returnMap.put("p4_Cur", "CNY");
        return returnMap;
    }

    //----------------------------------------------------------------------------------------//

    // 支付返回参数
    public static String[] getEposReqBackHmacOrder() {
        return new String[] { "r0_Cmd", "r1_Code", "r2_TrxId", "r6_Order", "p1_MerId", "r3_Amt", "r4_Cur", "r5_Pid",
                "r8_MP", "r9_BType", "rb_BankId", "rp_authno", "rp_PayDate", "ru_Trxtime" };

    }

    //二次支付返回参数
    public static String[] getReturnEposCode() {
        return new String[] { "r0_Cmd", "r1_Code", "p1_MerId", "r6_Order" };
    }

    /**
     * 信用卡验证码消费接口返回参数
     */
    public static String[] getYeeEposVerifySaleParReturn() {
        return new String[] { "r0_Cmd", "r1_Code", "r2_TrxId", "r6_Order", "ro_BankOrderId" };
    }

    /**
     * 支付通知返回参数
     * @return
     */
    public static String[] getEposCallbackHmacOrder() {
        return new String[] { "p1_MerId", "r0_Cmd", "r1_Code", "r2_TrxId", "r3_Amt", "r4_Cur", "r5_Pid", "r6_Order",
                "r7_Uid", "r8_MP", "r9_BType" };

    }

    public static String getEposReqUrl() {
        return eposReqUrl;
    }

}
