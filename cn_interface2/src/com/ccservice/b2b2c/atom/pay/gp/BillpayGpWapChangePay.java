package com.ccservice.b2b2c.atom.pay.gp;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.billpay.pki.Pkipair;
import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.PayEntryInfo;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.ben.Orderchange;

/**
 * 快钱手机端支付 改签支付
 * @author wzc
 *
 */
public class BillpayGpWapChangePay extends AirSupper {
    public static void main(String[] args) throws Exception {
        JSONObject obj = new JSONObject();
        String msg = new BillpayGpWapChangePay(191, "").pay(obj, 100, "改签支付", 0);
        System.out.println(msg);
    }

    private Orderchange orderchange;

    private Orderinfo orderinfo = null;

    /**
     * 
     * @param orderid
     * @param OrderNumber
     */
    public BillpayGpWapChangePay(long orderid, String OrderNumber) {
        if (orderid <= 0) {
            String sql = "SELECT ID FROM dbo.T_ORDERCHANGE WITH(nolock) WHERE C_CHANGENUMBER='" + OrderNumber + "'";
            List<Map> list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                orderid = Long.parseLong(map.get("ID").toString());
            }
        }
        orderchange = Server.getInstance().getB2BAirticketService().findOrderchange(orderid);
        orderinfo = Server.getInstance().getAirService().findOrderinfo(orderchange.getOrderid());
    }

    /**
     * 
     * @param ordernumber
     * @param money 保留两位小数
     * @param goodsDesc
     * @return
     * @throws Exception
     */
    public String pay(JSONObject msgobj, double money, String goodsDesc, int ind) throws Exception {
        String bankCode = "";
        bankCode = msgobj.containsKey("bankCode") ? msgobj.getString("bankCode") : "";
        bankCode = findBankCode(bankCode);
        if (orderchange == null) {
            return "未查询到改签订单";
        }
        if (orderinfo.getPolicyagentid() == null || orderinfo.getPolicyagentid() <= 0) {
            return "无效订单供应商";
        }
        String ordernumber = orderchange.getId() + "";
        PayEntryInfo info = findAgentInfo(orderinfo.getPolicyagentid().longValue() + "", 1);
        String merchantAcctId = "";
        if (info == null) {
            return "快钱账户缺失";
        }
        else {
            merchantAcctId = info.getPid();
        }
        merchantAcctId += "01";
        // 字符集.固定选择值。可为空。
        // /只能选择1、2、3.
        // /1代表UTF-8; 2代表GBK; 3代表gb2312
        // /默认值为1
        String inputCharset = "1";
        //网关接口地址
        String subway = "https://www.99bill.com/mobilegateway/recvMerchantInfoAction.htm?";
        //服务器接受支付结果的后台地址.与[pageUrl]不能同时为空。必须是绝对地址,快钱通过服务器连接的方式将交易结果发送到[bgUrl]对应的页面地址，在商户处理完成后输出的<result>为1，页面会转向到<redirecturl>对应的地址。
        String bgUrl = "";

        String pageUrl = "";
        // 网关版本.固定值///快钱会根据版本号来调用对应的接口处理程序。///本代码版本号固定为v2.0
        String version = "mobile1.0";

        // 语言种类.固定选择值。///只能选择1、2、3///1代表中文；2代表英文///默认值为1
        String language = "1";

        String payerId = "";
        // 签名类型.固定值///1代表MD5签名///当前版本固定为1
        String signType = "4";

        //英文或者中文字符    (32)
        String payerName = "";
        //  支付人联系类型  固定值： 1  代表电子邮件方式 (2)
        String payerContactType = "";
        //支付人联系方式 (50)
        String payerContact = "";

        // 商户订单号 由字母、数字、或[-][_]组成
        String orderId = ordernumber;
        String moneyShare = getRoyalty_parameters(orderchange, money, orderinfo);
        WriteLog.write("GpWaoPay改签获取支付链接", ind + ":ordernumber:" + orderinfo.getOrdernumber() + ":分润参数：" + moneyShare);

        String[] aryinfo = moneyShare.split(",");

        if (aryinfo[0].equals("0")) {
            return "支付参数不合法";
        }
        //主收款方应收额,以分为单位，必须是整型数字
        // /比方2，代表0.02元
        String orderAmount = "" + (int) (money * 100);
        // 订单金额 以分为单位，必须是整型数字
        // 订单提交时间
        // /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]如；20080101010101
        String orderTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
        // 商品名称 可为中文或英文字符
        String productName = goodsDesc;
        // 商品数量 可为空，非空时必须为数字
        String productNum = "1";
        //      商品代码，可以是 字母,数字,-,_   (20) 
        String productId = "";

        //      商品描述， 英文或者中文字符串  (400)
        String productDesc = "";

        // 扩展字段1 在支付结束后原样返回给商户
        String ext1 = orderinfo.getPolicyagentid().longValue() + "";
        // 扩展字段2 在支付结束后原样返回给商户
        String ext2 = ordernumber + "FgOrderchangehandle";

        //*   支付方式 固定值: 00, 10, 11, 12, 13, 14, 15, 16, 17  (2)
        // 00: 其他支付
        // 10: 银行卡支付
        // 11: 电话支付
        // 12: 快钱账户支付
        // 13: 线下支付
        // 14: 企业网银在线支付
        // 15: 信用卡在线支付
        // 17: 预付卡支付
        // *B2B 支付需要单独申请，默认不开通        String payType = "00";//默认银行卡支付
        String payType = "15";

        // 银行代码 银行代码 要在开通银行时 使用， 默认不开通 (8)
        String bankId = bankCode;
        // 支付人联系方式类型.固定选择值///只能选择1///1代表Email
        bgUrl = PropertyUtil.getValue("pageUrl", "GpAir.properties");
        //同一订单禁止重复提交标志，实物购物车填1，虚拟产品用0。1代表只能提交一次，0代表在支付不成功情况下可以再提交。可为空。
        String redoFlag = "";
        // 生成加密签名串 请务必按照如下顺序和规则组成加密串！
        String pid = "";

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
        signMsgVal = appendParam(signMsgVal, "payerId", payerId);
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
        String signMsg = pki.signMsg(signMsgVal.toString().trim(), orderinfo.getPolicyagentid().longValue() + "");
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
        signMsgValstr = appendParam(signMsgValstr, "merchantAcctId", merchantAcctId);
        signMsgValstr = appendParam(signMsgValstr, "payerName", payerName);
        signMsgValstr = appendParam(signMsgValstr, "payerContactType", payerContactType);
        signMsgValstr = appendParam(signMsgValstr, "payerContact", payerContact);
        signMsgValstr = appendParam(signMsgValstr, "payerId", payerId);
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
        signMsgValstr = appendParam(signMsgValstr, "signMsg", java.net.URLEncoder.encode(signMsg, "UTF-8"));

        String parameter = subway + signMsgValstr;
        System.out.println(parameter);
        return parameter;
    }

    /**
     * 创建分润记录
     * @param orderid
     * @return
     */
    public String getRoyalty_parameters(Orderchange orderchange, double Summoney, Orderinfo orderinfo) {
        if ((orderchange.getRefundprice() != null) && (orderchange.getRefundprice() <= Summoney)) {
            String msg = "1^" + findSupplyAcount(orderinfo.getPolicyagentid()) + "^" + formatMoneyInt(Summoney * 100)
                    + "^0^" + "change" + orderchange.getChangenumber();
            return "1," + msg;
        }
        else {
            return "0,金额错误";
        }
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
}
