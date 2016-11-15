package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.ccservice.b2b2c.atom.pay.helper.Payhelper;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.service.Profitshare;
import com.ccservice.b2b2c.base.traderecord.Traderecord;
import com.pay.config.Chinapnrconfig;

/**
 * Servlet implementation class for Servlet: Alipay 汇付天下支付类 请把key文件放到calss文件下。
 */
public class Chinapnrpay extends PaySupport implements Pay {

    public Chinapnrpay(HttpServletRequest request, HttpServletResponse response, Payhelper payhelper) {
        super(request, response, payhelper);
        format.applyPattern("###0.00");
    }

    static final long serialVersionUID = 1L;

    private final String gateurl = "http://mas.chinapnr.com/gar/RecvMerchant.do";

    private final String Version = "10";// 版本号

    private final String MerId = Chinapnrconfig.getInstance().getPartnerID();// 商户号

    private final String CmdId = "Buy";// 消息类型。

    DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();

    ///static long ticketid;
    Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    /*
     * (non-Java-doc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    @Override
    public void pay(float factorage) throws Exception {
        System.out.println(Chinapnrconfig.getInstance().selfaccount);
        logger.error("汇付天下订单支付开始");
        String OrdId = payhelper.getOrdernumber();// 订单号(int)payhelper.getOrderprice()+
        String OrdAmt = format.format(payhelper.getOrderprice() + factorage);// 订单金额
        // 订单的总金额，应大于或等于各分账金额总和
        String Pid = payhelper.getOrdernumber();// 商品编号
        String RetUrl = "http://" + request.getServerName() + ":" + request.getServerPort()
                + "/cn_interface/paysuccess.jsp";// 页面返回地址
                                                 // 交易完成后,钱管家系统把交易结果通过页面方式，发送到该地址上
        String BgRetUrl = "http://" + request.getServerName() + ":" + request.getServerPort()
                + "/cn_interface/ChinapnrNofity"; // 后台返回地址
                                                  // 订单支付时，商户后台应答地址
        String MerPriv = payhelper.getOrdernumber() + "Fg" + payhelper.getHandleName();// 商户私有数据项
                                                                                       // 为商户的自定义字段，该字段在交易完成后由钱管家系统原样返回
        String GateId = "";// 网管号 银行ID 。强烈建议商户置该域为空
        String paymethod = request.getParameter("paymethod");
        if (!"chinapnr".equals(paymethod)) {
            String bankid = request.getParameter("pay_bank");
            if (bankid != null && !"".equals(bankid)) {
                GateId = bankid;
            }
        }
        String IsBalance = "Y";// 是否自动结算。 可选
        String UsrMp = "";// 用户手机号。
        String DivDetails = "";// getRoyalty_parameters(payhelper.getRoyalty_parameters());// 分账明细
        String PayUsrId = "";// 付款人用户号 可选
        String CurCode = "RMB";// 币种
        String ChkValue = "";// 签名
        // 签名
        String MerKeyFile = "";
        try {
            MerKeyFile = URLDecoder.decode(Chinapnrpay.class.getClassLoader().getResource("/").getPath(), "UTF-8")
                    + "MerPrK" + MerId + ".key";
        }
        catch (UnsupportedEncodingException e1) {
        } // 商户私钥文件路径 请将MerPrK510010.key改为你的私钥文件名称
        String MerData = Version + CmdId + MerId + OrdId + OrdAmt + CurCode + Pid + RetUrl + MerPriv + GateId + UsrMp
                + DivDetails + PayUsrId + BgRetUrl + IsBalance;
        System.out.println(MerData);
        SecureLink sl = new SecureLink();
        int ret = sl.SignMsg(MerId, MerKeyFile, MerData);
        if (ret != 0) {
            System.out.println("签名错误 ret=" + ret);
            return;
        }
        try {
            Traderecord traderecord = new Traderecord();
            traderecord.setCreateuser("chinapnrpay");
            traderecord.setGoodsname(payhelper.getOrdername());
            traderecord.setCode("");// 外部订单号。
            traderecord.setCreatetime(new Timestamp(System.currentTimeMillis()));
            traderecord.setOrdercode(payhelper.getOrdernumber());
            traderecord.setPayname("汇付天下");
            traderecord.setPaytype(0);// 0支付宝 1财付通
            traderecord.setRetcode(DivDetails);
            traderecord.setState(0);// 0等待支付1支付成功2支付失败
            traderecord.setTotalfee((int) payhelper.getOrderprice());// 支付金额分为单位
            traderecord.setType(payhelper.getTradetype());// 订单类型
            traderecord.setPaymothed(paymethod);// 支付方式
            traderecord.setBankcode(GateId);// 支付银行

            traderecord = Server.getInstance().getMemberService().createTraderecord(traderecord);
        }
        catch (Exception e) {
            logger.info("交易记录失败", e.fillInStackTrace());

            // return;
        }

        ChkValue = sl.getChkValue();
        Map<String, String> map = new HashMap<String, String>();
        map.put("Version", Version);
        map.put("CmdId", CmdId);
        map.put("MerId", MerId);
        map.put("OrdId", OrdId);
        map.put("OrdAmt", OrdAmt);
        map.put("CurCode", CurCode);
        map.put("Pid", Pid);
        map.put("RetUrl", RetUrl);
        map.put("BgRetUrl", BgRetUrl);
        map.put("MerPriv", MerPriv);
        map.put("GateId", GateId);
        map.put("UsrMp", UsrMp);
        map.put("DivDetails", DivDetails);
        map.put("PayUsrId", PayUsrId);
        map.put("IsBalance", IsBalance);
        map.put("ChkValue", ChkValue);
        String param = createLinkString(map);
        try {
            response.sendRedirect(gateurl + "?" + param);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static String createLinkString(Map<String, String> params) {

        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);

        String prestr = "";

        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (value == null || value.length() == 0) {
                continue;
            }
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            }
            else {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        System.out.print("拼接后的字符串：" + prestr);
        return prestr;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.ccservice.b2b2c.atom.pay.helper.Payhelper#getRoyalty_parameters()
     *      Agent:user1:50.00;Agent:user2:800.00
     * 
     * 
     */
    public String getRoyalty_parameters(Map<Customeragent, Float> agentroya) {
        if (agentroya != null && agentroya.size() > 0) {
            List<Profitshare> rebates = new ArrayList<Profitshare>();
            Iterator<Map.Entry<Customeragent, Float>> agentiterator = agentroya.entrySet().iterator();
            System.out.println(agentroya.size());
            String royaltysb = "";
            for (; agentiterator.hasNext();) {
                Map.Entry<Customeragent, Float> entery = agentiterator.next();
                Customeragent agent = entery.getKey();
                if (agent.getId() == 46 && Chinapnrconfig.getInstance().selfaccount) {
                    continue;
                }
                double money = entery.getValue();
                boolean hascount = (agent.getChinapnrcount() != null && !"".equals(agent.getChinapnrcount())) ? true
                        : false;
                if (hascount) {
                    if (agentiterator.hasNext()) {
                        royaltysb = ";" + "Agent:" + agent.getChinapnrcount() + ":" + format.format(money) + royaltysb;
                    }
                    else {
                        royaltysb = "Agent:" + agent.getChinapnrcount() + ":" + format.format(money) + royaltysb;
                    }
                    Profitshare rebate = new Profitshare();
                    rebate.setAccount(agent.getAlipayaccount());
                    rebate.setBtype(payhelper.getTradetype());
                    rebate.setPagentid(agent.getId());
                    //rebate.setPmethod(Paymentmethod.EBANKPAY);
                    rebate.setProfit(money);
                    //rebate.setPtime(new Timestamp(System.currentTimeMillis()));
                    ///	rebate.setTicketid(ticketid);
                    rebates.add(rebate);

                }
            }
            if (rebates.size() > 0) {
                ///	Server.getInstance().getB2BSystemService().createBatchProfitshare(rebates);
            }
            return royaltysb.toString();
        }
        else {
            return "";
        }
        // return null;getre
    }

    public static boolean isNotnullorEpt(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void pay(long agentid) throws Exception {
        // TODO Auto-generated method stub

    }

}