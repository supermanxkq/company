package com.ccservice.b2b2c.atom.pay.test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import com.billpay.pki.Pkipair;
import com.callback.PropertyUtil;

/**
 * @Description: 快钱人民币支付网关接口及分账
 * @Copyright (c) 上海快钱信息服务有限公司
 * @version 2.0
 */
/**
 * Servlet implementation class for Servlet: Billpay
 * 
 */
public class BillpayAndShareProfitTest {
    public static void main(String[] args) throws Exception {
        Random r = new Random();
        new BillpayAndShareProfitTest().pay("YDXA2016060216360931", 0.2d, "机票机票款", "394");
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
        // 字符集.固定选择值。可为空。
        // /只能选择1、2、3.
        // /1代表UTF-8; 2代表GBK; 3代表gb2312
        // /默认值为1
        String inputCharset = "1";
        //网关接口地址
        String subway = "https://www.99bill.com/msgateway/recvMsgatewayMerchantInfoAction.htm?";//分账网关	
        //        String subway = "https://sandbox.99bill.com/msgateway/recvMsgatewayMerchantInfoAction.htm?";//分账网关	
        //服务器接受支付结果的后台地址.与[pageUrl]不能同时为空。必须是绝对地址,快钱通过服务器连接的方式将交易结果发送到[bgUrl]对应的页面地址，在商户处理完成后输出的<result>为1，页面会转向到<redirecturl>对应的地址。
        String bgUrl = "";
        // 网关版本.固定值///快钱会根据版本号来调用对应的接口处理程序。///本代码版本号固定为v2.0
        String version = "v2.0";

        // 语言种类.固定选择值。///只能选择1、2、3///1代表中文；2代表英文///默认值为1
        String language = "1";

        // 签名类型.固定值///1代表MD5签名///当前版本固定为1
        String signType = "4";

        //主收款方联系方式类型，固定值 1代表Email
        String payeeContactType = "1";
        //主收款方联系方式，当payeeContactType=1 时输入Email 地址 
        String payeeContact = "tqhk01@163.com";

        // 商户订单号 由字母、数字、或[-][_]组成
        String orderId = ordernumber;
        // 订单金额 以分为单位，必须是整型数字
        // /比方2，代表0.02元
        String orderAmount = "" + (int) (money * 100);

        //主收款方应收额,以分为单位，必须是整型数字
        String payeeAmount = "" + (int) (money * 100);

        // 订单提交时间
        // /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]如；20080101010101
        String orderTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        // 商品名称 可为中文或英文字符
        String productName = goodsDesc;
        // 商品数量 可为空，非空时必须为数字
        String productNum = "1";

        // 扩展字段1 在支付结束后原样返回给商户
        String ext1 = agentID + "";
        // 扩展字段2 在支付结束后原样返回给商户
        String ext2 = ordernumber + "FgGpAirnofiryHandle";

        // 支付方式.固定选择值 只能选择00、10、11、12、13、14
        // /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
        String payType = "00";//默认银行卡支付
        //提交此分账请求的合作方在快钱的用户编号。
        String pid = "10078915436";
        //        String pid = "10012138843";

        //分账数据，可为空
        String sharingData = "";
        //        String sharingData = "1^2829679462@qq.com^100^0^test";
        //分账标志 ，0 代表异步分账，即不立即把款项分配给相关账户；1 代表支付成功立刻分账
        String sharingPayFlag = "1";
        // 支付人联系方式类型.固定选择值///只能选择1///1代表Email
        bgUrl = PropertyUtil.getValue("pageUrl", "GpAir.properties");

        // 生成加密签名串 请务必按照如下顺序和规则组成加密串！

        StringBuilder signMsgVal = new StringBuilder("");
        signMsgVal = appendParam(signMsgVal, "inputCharset", inputCharset);
        signMsgVal = appendParam(signMsgVal, "bgUrl", bgUrl);
        signMsgVal = appendParam(signMsgVal, "version", version);
        signMsgVal = appendParam(signMsgVal, "language", language);
        signMsgVal = appendParam(signMsgVal, "signType", signType);
        signMsgVal = appendParam(signMsgVal, "payeeContactType", payeeContactType);
        signMsgVal = appendParam(signMsgVal, "payeeContact", payeeContact);
        signMsgVal = appendParam(signMsgVal, "orderId", orderId);
        signMsgVal = appendParam(signMsgVal, "orderAmount", orderAmount);
        signMsgVal = appendParam(signMsgVal, "payeeAmount", payeeAmount);
        signMsgVal = appendParam(signMsgVal, "orderTime", orderTime);
        signMsgVal = appendParam(signMsgVal, "productName", productName);
        signMsgVal = appendParam(signMsgVal, "productNum", productNum);
        signMsgVal = appendParam(signMsgVal, "ext1", ext1);
        signMsgVal = appendParam(signMsgVal, "ext2", ext2);
        signMsgVal = appendParam(signMsgVal, "payType", payType);
        signMsgVal = appendParam(signMsgVal, "pid", pid);
        signMsgVal = appendParam(signMsgVal, "sharingData", sharingData);
        signMsgVal = appendParam(signMsgVal, "sharingPayFlag", sharingPayFlag);

        System.out.println("signMsgVal==" + signMsgVal);
        Pkipair pki = new Pkipair();
        String signMsg = pki.signMsg(signMsgVal.toString().trim(), agentID);
        System.out.println("证书加密后==" + signMsg);
        //陈星修改结束
        // post参数
        String signMsgValstr = "";
        signMsgValstr = appendParam(signMsgValstr, "inputCharset", inputCharset);
        signMsgValstr = appendParam(signMsgValstr, "bgUrl", bgUrl);
        signMsgValstr = appendParam(signMsgValstr, "version", version);
        signMsgValstr = appendParam(signMsgValstr, "language", language);
        signMsgValstr = appendParam(signMsgValstr, "signType", signType);
        signMsgValstr = appendParam(signMsgValstr, "signMsg", java.net.URLEncoder.encode(signMsg, "UTF-8"));
        signMsgValstr = appendParam(signMsgValstr, "payeeContactType", payeeContactType);
        signMsgValstr = appendParam(signMsgValstr, "payeeContact", payeeContact);
        signMsgValstr = appendParam(signMsgValstr, "orderId", orderId);
        signMsgValstr = appendParam(signMsgValstr, "orderAmount", orderAmount);
        signMsgValstr = appendParam(signMsgValstr, "payeeAmount", payeeAmount);
        signMsgValstr = appendParam(signMsgValstr, "orderTime", orderTime);
        signMsgValstr = appendParam(signMsgValstr, "productName", productName);
        signMsgValstr = appendParam(signMsgValstr, "productNum", productNum);
        signMsgValstr = appendParam(signMsgValstr, "ext1", ext1);
        signMsgValstr = appendParam(signMsgValstr, "ext2", ext2);
        signMsgValstr = appendParam(signMsgValstr, "payType", payType);
        signMsgValstr = appendParam(signMsgValstr, "pid", pid);
        signMsgValstr = appendParam(signMsgValstr, "sharingData", sharingData);
        signMsgValstr = appendParam(signMsgValstr, "sharingPayFlag", sharingPayFlag);
        String parameter = subway + signMsgValstr;
        System.out.println(parameter);
        return parameter;
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