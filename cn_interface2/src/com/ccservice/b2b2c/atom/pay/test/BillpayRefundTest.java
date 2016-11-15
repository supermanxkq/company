package com.ccservice.b2b2c.atom.pay.test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import com.billpay.pki.Pkipair;

/**
 * @Description: 快钱人民币支付网关接口及分账退款接口
 * @Copyright (c) 上海快钱信息服务有限公司
 * @version 2.0
 */
public class BillpayRefundTest {
    public static void main(String[] args) throws Exception {
        Random r = new Random();
        new BillpayRefundTest().pay("YDXA2016060216360931", 0.2d, "机票机票款", "394");
    }

    /**
     * 
     * @param ordernumber
     * @param money 保留两位小数
     * @param goodsDesc
     * @return
     * @throws Exception
     */
    public String pay(String ordernumber, double money, String goodsDesc, String agentID) throws Exception {
        //网关接口地址
        String subway = "https://www.99bill.com/msgateway/recvMerchantRefundAction.htm?";//退款
        //String subway = "https://sandbox.99bill.com/msgateway/recvMerchantRefundAction.htm?";//退款
        //字符集,固定选择值,默认值为1。
        String inputCharset = "1";
        //网关版本，固定值v2.0
        String version = "v2.0";
        //签名类型，4代表证书签名
        String signType = "4";
        //商户订单号
        String orderId = ordernumber;
        //快钱的合作伙伴的账户号
        // String pid = "10012138843";
        String pid = "10078915436";
        //退款流水号
        String seqId = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        //退款总金额，单位分
        String returnAllAmount = "" + (int) (money * 100);
        //退款请求提交时间
        String returnTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        //扩展字段1
        String ext1 = "ext1";
        //扩展字段1
        String ext2 = "ext2";
        //退款明细
        String returnDetail = "1^tqhk01@163.com^20^lei";
        //生成加密签名串，请务必按照如下顺序和规则组成加密串
        String signMsgVal = "";
        signMsgVal = appendParam(signMsgVal, "inputCharset", inputCharset);
        signMsgVal = appendParam(signMsgVal, "version", version);
        signMsgVal = appendParam(signMsgVal, "signType", signType);
        signMsgVal = appendParam(signMsgVal, "orderId", orderId);
        signMsgVal = appendParam(signMsgVal, "pid", pid);
        signMsgVal = appendParam(signMsgVal, "seqId", seqId);
        signMsgVal = appendParam(signMsgVal, "returnAllAmount", returnAllAmount);
        signMsgVal = appendParam(signMsgVal, "returnTime", returnTime);
        signMsgVal = appendParam(signMsgVal, "ext1", ext1);
        signMsgVal = appendParam(signMsgVal, "ext2", ext2);
        signMsgVal = appendParam(signMsgVal, "returnDetail", returnDetail);
        System.out.println("signMsgVal==" + signMsgVal);
        Pkipair Pkipair = new Pkipair();
        String signMsg = Pkipair.signMsg(signMsgVal, agentID);
        System.out.println("证书加密后==" + signMsg);
        // post参数
        String signMsgValstr = "";
        signMsgValstr = appendParam(signMsgValstr, "inputCharset", inputCharset);
        signMsgValstr = appendParam(signMsgValstr, "version", version);
        signMsgValstr = appendParam(signMsgValstr, "signType", signType);
        signMsgValstr = appendParam(signMsgValstr, "orderId", orderId);
        signMsgValstr = appendParam(signMsgValstr, "pid", pid);
        signMsgValstr = appendParam(signMsgValstr, "seqId", seqId);
        signMsgValstr = appendParam(signMsgValstr, "returnAllAmount", returnAllAmount);
        signMsgValstr = appendParam(signMsgValstr, "returnTime", returnTime);
        signMsgValstr = appendParam(signMsgValstr, "ext1", ext1);
        signMsgValstr = appendParam(signMsgValstr, "ext2", ext2);
        signMsgValstr = appendParam(signMsgValstr, "returnDetail", returnDetail);
        signMsgValstr = appendParam(signMsgValstr, "signMsg", java.net.URLEncoder.encode(signMsg, "UTF-8"));
        String parameter = subway + signMsgValstr;
        System.out.println(parameter);
        String result = httpget(parameter, "UTF-8");
        System.out.println(result);
        return parameter;
    }

    public static String httpget(String url, String encode) {
        try {
            URL Url = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream in = conn.getInputStream();
            byte[] buf = new byte[2046];
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            int len = 0;
            int size = 0;
            while ((len = in.read(buf)) > 0) {
                bout.write(buf, 0, len);
                size += len;
            }

            in.close();
            conn.disconnect();

            return new String(bout.toByteArray(), encode);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //功能函数。将变量值不为空的参数组成字符串
    public static String appendParam(String returnStr, String paramId, String paramValue) {
        if (!returnStr.equals("")) {
            if (!paramValue.equals("")) {
                returnStr = returnStr + "&" + paramId + "=" + paramValue;
            }
        }
        else {
            if (!paramValue.equals("")) {
                returnStr = paramId + "=" + paramValue;
            }
        }
        return returnStr;
    }

    //功能函数。将变量值不为空的参数组成字符串。结束
    // 功能函数。将变量值不为空的参数组成字符串
    public static StringBuilder appendParam(StringBuilder returnStr, String paramId, String paramValue) {
        if (returnStr.length() > 0) {
            if (!paramValue.equals("")) {
                returnStr = returnStr.append("&" + paramId + "=" + paramValue);
            }
        }
        else {
            if (!paramValue.equals("")) {
                returnStr.append(paramId + "=" + paramValue);
            }
        }
        return returnStr;
    }

    DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();

    public String formatMoney(Float money) {
        format.applyPattern("#,##0.00");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Float.toString(money);
            }
            else {
                return "0";
            }
        }
    }

}