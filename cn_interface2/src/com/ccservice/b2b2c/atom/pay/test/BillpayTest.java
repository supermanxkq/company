package com.ccservice.b2b2c.atom.pay.test;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.billpay.pki.Pkipair;
import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.server.Server;

/**
 * @Description: 快钱人民币支付网关接口
 * @Copyright (c) 上海快钱信息服务有限公司
 * @version 2.0
 */
/**
 * Servlet implementation class for Servlet: Billpay
 * 
 */
public class BillpayTest {
    public static void main(String[] args) throws Exception {
        Random r = new Random();
        new BillpayTest().pay("YDX912165423123234123481", 0.02d, "机票机票款", "46");
    }

    /**
     * 查询块钱银行code
     * @param code
     */
    public String findBankCode(String code) {
        String sql = "SELECT PayCode FROM dbo.GpBankCode WITH(NOLOCK) WHERE code='" + code + "'";
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        if (list.size() == 1) {
            Map map = (Map) list.get(0);
            String PayCode = map.get("PayCode") == null ? "" : map.get("PayCode").toString();
            return PayCode;
        }
        return "";
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
        String merchantId = "10021636212";
        //网关接口地址
        String subway = "https://www.99bill.com/gateway/recvMerchantInfoAction.htm?";//人民币网关	
        //        String subway = "https://sandbox.99bill.com/gateway/recvMerchantInfoAction.htm?";//人民币网关	
        // 人民币网关账户号
        // /请登录快钱系统获取用户编号，用户编号后加01即为人民币网关账户号。
        String merchantAcctId = merchantId + "" + "01";
        // 人民币网关密钥
        // /区分大小写.请与快钱联系索取
        String key = "";
        // 字符集.固定选择值。可为空。
        // /只能选择1、2、3.
        // /1代表UTF-8; 2代表GBK; 3代表gb2312
        // /默认值为1
        String inputCharset = "1";
        // 接受支付结果的页面地址.与[bgUrl]不能同时为空。必须是绝对地址。
        // /如果[bgUrl]为空，快钱将支付结果Post到[pageUrl]对应的地址。
        // /如果[bgUrl]不为空，并且[bgUrl]页面指定的<redirecturl>地址不为空，则转向到<redirecturl>对应的地址
        String pageUrl = "";
        // 服务器接受支付结果的后台地址.与[pageUrl]不能同时为空。必须是绝对地址。
        // /快钱通过服务器连接的方式将交易结果发送到[bgUrl]对应的页面地址，在商户处理完成后输出的<result>如果为1，页面会转向到<redirecturl>对应的地址。
        // /如果快钱未接收到<redirecturl>对应的地址，快钱将把支付结果post到[pageUrl]对应的页面。
        String bgUrl = "";
        // 网关版本.固定值///快钱会根据版本号来调用对应的接口处理程序。///本代码版本号固定为v2.0
        String version = "v2.0";
        // 语言种类.固定选择值。///只能选择1、2、3///1代表中文；2代表英文///默认值为1
        String language = "1";
        // 签名类型.固定值///1代表MD5签名///当前版本固定为1
        String signType = "4";
        // 支付人姓名///可为中文或英文字符
        String payerName = "consumer";
        // 支付人联系方式类型.固定选择值///只能选择1///1代表Email
        String payerContactType = "";
        // 支付人联系方式///只能选择Email或手机号
        String payerContact = "";
        pageUrl = PropertyUtil.getValue("pageUrl", "GpAir.properties");
        bgUrl = pageUrl;
        // 商户订单号 由字母、数字、或[-][_]组成
        String orderId = ordernumber;

        // 订单金额 以分为单位，必须是整型数字
        // /比方2，代表0.02元
        String orderAmount = "" + (int) (money * 100);
        // String orderAmount="2";
        // 订单提交时间
        // /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]如；20080101010101
        String orderTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

        // 商品名称 可为中文或英文字符
        String productName = ordernumber + "pay";
        // 商品数量 可为空，非空时必须为数字
        String productNum = "1";
        // 商品代码 可为字符或者数字
        String productId = "TicketNumber";
        // 商品描述
        String productDesc = "商品描述";
        // 扩展字段1 在支付结束后原样返回给商户
        String ext1 = agentID + "";
        // 扩展字段2 在支付结束后原样返回给商户
        String ext2 = ordernumber + "FgGpAirnofiryHandle";
        // 支付方式.固定选择值 只能选择00、10、11、12、13、14
        // /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
        String payType = "00";//默认银行卡支付
        //if (request.getParameter("paymethod") != null) {
        //    payType = new String(request.getParameter("paymethod").getBytes("ISO8859-1"), "UTF-8");
        //}
        // 银行代码
        // /实现直接跳转到银行页面去支付,只在payType=10时才需设置参数
        // /具体代码参见 接口文档银行代码列表
        String bankId = "";//findBankCode(bankCode);
        //if ("".equals(bankId)) {
        //bankId = "UPOP";//没有就是银联
        //}
        //if (request.getParameter("billbank") != null && payType.equals("10")) {
        //    bankId = new String(request.getParameter("billbank").getBytes("ISO8859-1"), "UTF-8");
        //}

        // 同一订单禁止重复提交标志 固定选择值： 1、0
        // /1代表同一订单号只允许提交1次；0表示同一订单号在没有支付成功的前提下可重复提交多次。默认为0建议实物购物车结算类商户采用0；虚拟产品类商户采用1
        String redoFlag = "";

        // 快钱的合作伙伴的账户号 如未和快钱签订代理合作协议，不需要填写本参数
        String pid = "";
        // 生成加密签名串 请务必按照如下顺序和规则组成加密串！

        StringBuilder signMsgVal = new StringBuilder("");
        signMsgVal = appendParam(signMsgVal, "inputCharset", inputCharset);
        signMsgVal = appendParam(signMsgVal, "pageUrl", pageUrl);
        signMsgVal = appendParam(signMsgVal, "bgUrl", bgUrl);
        signMsgVal = appendParam(signMsgVal, "version", version);
        signMsgVal = appendParam(signMsgVal, "language", language);
        signMsgVal = appendParam(signMsgVal, "signType", signType);
        signMsgVal = appendParam(signMsgVal, "merchantAcctId", merchantAcctId);
        signMsgVal = appendParam(signMsgVal, "payerName", payerName);
        signMsgVal = appendParam(signMsgVal, "payerContactType", payerContactType);
        signMsgVal = appendParam(signMsgVal, "payerContact", payerContact);
        signMsgVal = appendParam(signMsgVal, "orderId", orderId);
        signMsgVal = appendParam(signMsgVal, "orderAmount", orderAmount);
        signMsgVal = appendParam(signMsgVal, "orderTime", orderTime);
        signMsgVal = appendParam(signMsgVal, "productName", productName);
        signMsgVal = appendParam(signMsgVal, "productNum", productNum);
        signMsgVal = appendParam(signMsgVal, "productId", productId);
        signMsgVal = appendParam(signMsgVal, "productDesc", productDesc);
        signMsgVal = appendParam(signMsgVal, "ext1", ext1);
        signMsgVal = appendParam(signMsgVal, "ext2", ext2);
        signMsgVal = appendParam(signMsgVal, "payType", payType);
        signMsgVal = appendParam(signMsgVal, "bankId", bankId);
        signMsgVal = appendParam(signMsgVal, "redoFlag", redoFlag);
        signMsgVal = appendParam(signMsgVal, "pid", pid);

        System.out.println("signMsgVal==" + signMsgVal);
        Pkipair pki = new Pkipair();
        String signMsg = pki.signMsg(signMsgVal.toString().trim(), agentID);
        System.out.println("证书加密后==" + signMsg);
        //陈星修改结束
        // post参数
        String signMsgValstr = "";
        signMsgValstr = appendParam(signMsgValstr, "inputCharset", inputCharset);
        signMsgValstr = appendParam(signMsgValstr, "pageUrl", pageUrl);
        signMsgValstr = appendParam(signMsgValstr, "bgUrl", bgUrl);
        signMsgValstr = appendParam(signMsgValstr, "version", version);
        signMsgValstr = appendParam(signMsgValstr, "language", language);
        signMsgValstr = appendParam(signMsgValstr, "signType", signType);
        signMsgValstr = appendParam(signMsgValstr, "signMsg", java.net.URLEncoder.encode(signMsg, "UTF-8"));
        signMsgValstr = appendParam(signMsgValstr, "merchantAcctId", merchantAcctId);
        signMsgValstr = appendParam(signMsgValstr, "payerName", payerName);
        signMsgValstr = appendParam(signMsgValstr, "payerContactType", payerContactType);
        signMsgValstr = appendParam(signMsgValstr, "payerContact", payerContact);
        signMsgValstr = appendParam(signMsgValstr, "orderId", orderId);
        signMsgValstr = appendParam(signMsgValstr, "orderAmount", orderAmount);
        signMsgValstr = appendParam(signMsgValstr, "orderTime", orderTime);
        signMsgValstr = appendParam(signMsgValstr, "productName", productName);
        signMsgValstr = appendParam(signMsgValstr, "productNum", productNum);
        signMsgValstr = appendParam(signMsgValstr, "productId", productId);
        signMsgValstr = appendParam(signMsgValstr, "productDesc", productDesc);
        signMsgValstr = appendParam(signMsgValstr, "ext1", ext1);
        signMsgValstr = appendParam(signMsgValstr, "ext2", ext2);
        signMsgValstr = appendParam(signMsgValstr, "payType", payType);
        signMsgValstr = appendParam(signMsgValstr, "bankId", bankId);
        signMsgValstr = appendParam(signMsgValstr, "redoFlag", redoFlag);
        signMsgValstr = appendParam(signMsgValstr, "pid", pid);
        signMsgValstr = appendParam(signMsgValstr, "submit", "");
        //System.out.println("signMsgValstr=="+signMsgValstr);

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