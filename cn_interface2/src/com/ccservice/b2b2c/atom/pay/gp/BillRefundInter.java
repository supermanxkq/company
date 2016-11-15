package com.ccservice.b2b2c.atom.pay.gp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.alibaba.fastjson.JSONObject;
import com.billpay.pki.Pkipair;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.PayEntryInfo;

/**
 * 接口退款工具类
 * @author wzc
 *
 */
public class BillRefundInter extends AirSupper {

    /**
     * @param refundParam 以，分割 【0】退款总额  【1】退款明细
     * @param tradeNum 退款商户单号
     * @param plicyAgentId 退款代理商ID 
     * @return
     * @throws Exception
     */
    public String refund(long orderid, String refundParam, String tradeNum, long plicyAgentId) throws Exception {
        JSONObject msg = new JSONObject();
        PayEntryInfo info = findAgentInfo(plicyAgentId + "", 1);
        String merchantId = "";
        if (info == null) {
            msg.put("success", false);
            msg.put("msg", "快钱账户缺失");
            msg.put("errCode", "");
            return msg.toJSONString();
        }
        else {
            merchantId = info.getPid();
        }
        //网关接口地址
        String subway = "https://www.99bill.com/msgateway/recvMerchantRefundAction.htm?";//退款
        //字符集,固定选择值,默认值为1。
        String inputCharset = "1";
        //网关版本，固定值v2.0
        String version = "v2.0";
        //签名类型，4代表证书签名
        String signType = "4";
        //商户订单号
        String orderId = tradeNum;
        //快钱的合作伙伴的账户号
        String pid = merchantId;
        //退款流水号
        String seqId = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

        WriteLog.write("billRefundOrder", "orderid:" + orderid + ",refundParam:" + refundParam);
        String[] ary = refundParam.split(",");
        //退款总金额，单位分
        String returnAllAmount = ary[0];
        //退款请求提交时间
        String returnTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        //扩展字段1
        String ext1 = "";
        //扩展字段1
        String ext2 = "";
        //退款明细
        String returnDetail = ary[1];
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
        Pkipair Pkipair = new Pkipair();
        String signMsg = Pkipair.signMsg(signMsgVal, info.getAgentid() + "");
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
        WriteLog.write("billRefundOrder", "orderid:" + orderid + ",request:" + parameter);
        String returnstr = httpget(parameter, "UTF-8");
        WriteLog.write("billRefundOrder", "orderid:" + orderid + ",returnstr:" + returnstr);
        SAXBuilder build = new SAXBuilder();
        Document document;
        boolean refundr = false;
        String result = "";
        String errCode = "";
        String returnData = "";
        String msginfo = "";
        try {
            document = build.build(new StringReader(returnstr));
            Element root = document.getRootElement();
            orderId = root.getChildText("orderId");
            pid = root.getChildText("pid");
            seqId = root.getChildText("seqId");
            returnAllAmount = root.getChildText("returnAllAmount");
            result = root.getChildText("result");
            errCode = root.getChildText("errCode");
            returnData = root.getChildText("returnData");
            signMsg = root.getChildText("signMsg");
            if (result.equals("10")) {
                refundr = true;//退款成功
                msginfo = "退款成功";
            }
            else if (result.equals("11")) {
                refundr = false;//退款失败
                msginfo = "退款失败";
            }
        }
        catch (Exception e) {
            msginfo = "退款状态未知";
            e.printStackTrace();
        }
        msg.put("success", refundr);
        msg.put("msg", msginfo);
        msg.put("errCode", errCode);
        msg.put("seqId", seqId);
        return msg.toJSONString();
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
        format.applyPattern("###0");
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

    public String formatMoney(Double money) {
        format.applyPattern("###0.00");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Double.toString(money);
            }
            else {
                return "0";
            }
        }
    }

    public String formatMoneyInt(Double money) {
        format.applyPattern("###0");
        try {
            String result = format.format(money);
            return result;
        }
        catch (Exception e) {
            if (money != null) {
                return Double.toString(money);
            }
            else {
                return "0";
            }
        }
    }
}
