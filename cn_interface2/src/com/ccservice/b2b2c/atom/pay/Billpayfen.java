package com.ccservice.b2b2c.atom.pay;

import java.net.URLEncoder;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.billpay.pki.Pkipair;
import com.ccservice.b2b2c.atom.pay.helper.Payhelper;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.traderecord.Traderecord;
import com.pay.config.BillpayConfig;

/**
 * @Description: 快钱人民币分账网关
 * @Copyright (c) 上海快钱信息服务有限公司
 * @version 2.0
 */
/**
 * Servlet implementation class for Servlet: Billpay
 * 
 */
@SuppressWarnings("serial")
public class Billpayfen extends PaySupport implements Pay {

    public Billpayfen(HttpServletRequest request, HttpServletResponse response, Payhelper payhelper) {
        super(request, response, payhelper);
    }

    //网关接口地址
    String subway = "https://www.99bill.com/msgateway/recvMsgatewayMerchantInfoAction.htm?";//人民币分账网关

    // 字符集.固定选择值。可为空。// /只能选择1、2、3.
    // /1代表UTF-8; 2代表GBK; 3代表gb2312/默认值为1
    String inputCharset = "2";

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

    // 签名类型.固定值///1代表MD5签名///当前版本固定为1 4.PKI
    String signType = "4";

    // 支付人联系方式类型.固定选择值///只能选择1///1代表Email
    String payeeContactType = "1";

    //支付人姓名 可为空
    String payerName = "";

    //支付人联系类型，固定值1 为emile；
    String payerContactType = "";

    //支付人联系类型。
    String payerContact = "";

    //主收款方应收额 不可为空
    String payeeAmount = "";

    @Override
    public void pay(float factorage) throws Exception {
        // 支付人联系方式///只能选择Email或手机号
        String payeeContact = BillpayConfig.getInstance().getSellerEmail();
        response.setContentType("text/plain; charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        this.pageUrl = "http://" + request.getServerName() + ":" + request.getServerPort()
                + "/cn_interface/BillpayNotifyHandle";
        this.bgUrl = pageUrl;
        // 商户订单号 由字母、数字、或[-][_]组成
        String orderId = payhelper.getOrdernumber();
        Map map = null;//payhelper.getRoyalty_parameters();
        // 订单金额 以分为单位，必须是整型数字// /比方2，代表0.02元
        String orderAmount = (int) ((payhelper.getOrderprice() + factorage) * 100) + "";
        // 生成加密签名串 请务必按照如下顺序和规则组成加密串！1^feinaqu@126.com^500^0^miaoshu
        String sharingData = this.getRoyalty_parameters(map, payhelper.getOrderprice());//分账数据sharingContactType^ sharingContact^ sharingApplyAmount^ sharingFeeRate^ sharingDesc

        // 订单提交时间 不可为空
        // /14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]如；20080101010101
        String orderTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

        // 商品名称 可为中文或英文字符
        String productName = "ticket";// payhelper.getOrdername();
        // 商品数量 可为空，非空时必须为数字
        String productNum = "1";
        // 商品代码 可为字符或者数字
        String productId = payhelper.getOrdernumber();
        // 商品描述
        String productDesc = "websiteorder";//payhelper.getOrderDescription();
        // 扩展字段1 在支付结束后原样返回给商户
        String ext1 = payhelper.getOrdernumber();
        // 扩展字段2 在支付结束后原样返回给商户
        String ext2 = payhelper.getOrdernumber() + "Fg" + payhelper.getHandleName();

        // 支付方式.固定选择值 只能选择00、10、11、12、13、14
        // /00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
        String payType = "00";
        if (request.getParameter("paymethod") != null) {
            payType = new String(request.getParameter("paymethod").getBytes("ISO8859-1"), "UTF-8");
        }
        // 银行代码
        // /实现直接跳转到银行页面去支付,只在payType=10时才需设置参数
        // /具体代码参见 接口文档银行代码列表
        String bankId = "";
        if (request.getParameter("billbank") != null && payType.equals("10")) {
            bankId = new String(request.getParameter("billbank").getBytes("ISO8859-1"), "UTF-8");
        }

        // 同一订单禁止重复提交标志 固定选择值： 1、0
        // /1代表同一订单号只允许提交1次；0表示同一订单号在没有支付成功的前提下可重复提交多次。默认为0建议实物购物车结算类商户采用0；虚拟产品类商户采用1
        String redoFlag = "";

        // 快钱的合作伙伴的账户号 如未和快钱签订代理合作协议，不需要填写本参数
        String pid = BillpayConfig.getInstance().getPartnerID();
        /*固定值：1、0 1 代表支付成功立刻分账 0 代表异步分账，即不立即把款项分配给相关账户*/
        String sharingPayFlag = "0";
        String signMsgVal = "";
        signMsgVal = appendParam(signMsgVal, "inputCharset", inputCharset);
        signMsgVal = appendParam(signMsgVal, "pageUrl", pageUrl);
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
        signMsgVal = appendParam(signMsgVal, "productDesc", productDesc);
        signMsgVal = appendParam(signMsgVal, "ext1", ext1);
        signMsgVal = appendParam(signMsgVal, "ext2", ext2);
        signMsgVal = appendParam(signMsgVal, "payType", payType);
        signMsgVal = appendParam(signMsgVal, "pid", pid);
        signMsgVal = appendParam(signMsgVal, "sharingData", sharingData);
        signMsgVal = appendParam(signMsgVal, "sharingPayFlag", sharingPayFlag);
        Pkipair Pkipair = new Pkipair();
        String signMsg = Pkipair.signMsg(signMsgVal, "");

        signMsgVal = appendParam(signMsgVal, "signMsg", URLEncoder.encode(signMsg));
        System.out.println(signMsgVal);

        String parameter = subway + signMsgVal;
        //System.out.println("parameter=="+parameter);
        /*String parameter = "https://www.99bill.com/gateway/recvMerchantInfoAction.htm?"
        		+ signMsgVal + "&signMsg=" + signMsg;*/

        // 写入支付记录
        Traderecord traderecord = new Traderecord();
        traderecord.setCode(orderTime);
        traderecord.setCreatetime(new Timestamp(System.currentTimeMillis()));
        traderecord.setCreateuser("Billpay");
        traderecord.setDescription(productDesc);
        traderecord.setGoodsdesc(productDesc);
        traderecord.setGoodsname(productName);
        traderecord.setModifytime(new Timestamp(System.currentTimeMillis()));
        traderecord.setModifyuser("");
        traderecord.setOrdercode(payhelper.getOrdernumber());
        traderecord.setPayname("服务名称");
        traderecord.setPaytype(2);// 0支付宝 1财付通 2 快钱
        traderecord.setRetcode("");
        traderecord.setState(0);// 0等待支付1支付成功2支付失败
        traderecord.setTotalfee((int) Double.parseDouble(orderAmount));// 支付金额分为单位
        traderecord.setType(1);// 订单类型
        traderecord.setPaymothed("99bill");// 支付方式
        traderecord.setBankcode("");// 支付银行
        try {
            traderecord = Server.getInstance().getMemberService().createTraderecord(traderecord);
        }
        catch (SQLException e) {
            // TODO Auto-generated catch block
            System.out.println("交易失败！");
            e.printStackTrace();
            return;
        }
        System.out.println(parameter);
        response.sendRedirect(parameter);
    }

    //功能函数。将变量值不为空的参数组成字符串
    public String appendParam(String returnStr, String paramId, String paramValue) {
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
    public StringBuilder appendParam(StringBuilder returnStr, String paramId, String paramValue) {
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

    public String getRoyalty_parameters(Map<Customeragent, Float> agentroya, double orderprice) {
        float totallirun = 0f;
        StringBuilder royaltysb = new StringBuilder("");
        if (agentroya != null && agentroya.size() > 0) {
            Iterator<Map.Entry<Customeragent, Float>> agentiterator = agentroya.entrySet().iterator();
            System.out.println(agentroya.size());

            for (; agentiterator.hasNext();) {
                Map.Entry<Customeragent, Float> entery = agentiterator.next();

                Customeragent agent = entery.getKey();
                float money = entery.getValue();
                boolean hascount = (agent.getKuaibillaccount() != null && !agent.getKuaibillaccount().equals("")) ? true
                        : false;

                if (agent.getId() != 46 && hascount) {
                    if (agentiterator.hasNext()) {
                        royaltysb.append("1^" + agent.getKuaibillaccount() + "^" + (int) (money * 100) + "^0^"
                                + agent.getCode() + "getmoney|");
                    }
                    else {
                        royaltysb.append("1^" + agent.getKuaibillaccount() + "^" + (int) (money * 100) + "^0^"
                                + agent.getCode() + "getmoney");

                    }
                    totallirun += money;

                }
            }

        }
        this.payeeAmount = (int) ((orderprice - totallirun) * 100) + "";
        return royaltysb.toString();
        // return null;getre
    }

    @Override
    public void pay(long agentid) throws Exception {
        // TODO Auto-generated method stub

    }

}