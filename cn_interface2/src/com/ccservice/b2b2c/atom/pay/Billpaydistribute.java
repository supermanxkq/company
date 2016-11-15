package com.ccservice.b2b2c.atom.pay;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.billpay.pki.Pkipair;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.service.Profitshare;
import com.ccservice.b2b2c.base.service.Profitshare.Profitstate;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.pay.config.AlipayConfig;
import com.pay.config.BillpayConfig;

/**
 * @author hanmenghui
 * 块钱异步分润确认接口。块钱异步分润不同于支付宝。快钱异步分润通过在BillPayfen类中sharingPayFlag字段的值来区分，
 * 0代表异步分润，1，代表同步分润，也就是说不管同步分润，还是异步分润，分润信息都要在支付时传递给快钱。如果为1.则快钱在支付成功后立即实现分润。
 * 为0时，需在接收到块钱支付成功通知后，调用此确认接口确认分润信息。
 *
 */
public class Billpaydistribute {
    static final long serialVersionUID = 1L;

    static List<Profitshare> prifitshares;

    static Logger logger = Logger.getLogger(Billpaydistribute.class.getSimpleName());

    public static String distribute(long orderid, int ywtype) {
        logger.info(orderid + "快钱交易异步分润开始");
        /**
         *获取交易号
         */
        List<AirticketPaymentrecord> records = Server.getInstance().getB2BSystemService()
                .findAllPaymentrecordByBtype(orderid, ywtype);
        String tradeno = "";
        for (AirticketPaymentrecord pr : records) {
            if (pr.getTradetype() == AirticketPaymentrecord.USUAL) {
                tradeno = pr.getTradeno();
                break;
            }
        }
        String inputCharset = "1";
        String version = "v2.0";
        String signType = "4";//固定值。代表PKI加密
        String sharingTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String orderId = orderid + "";
        //提交此分账请求的合作方在快钱的用户编号
        String pid = BillpayConfig.getInstance().getPartnerID(); /////"10022777253";
        String dealTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//快钱对交易进行处理的时间
        String payResult = ""; //10：分账成功、11分账失败
        String errCode = "";
        // 生成加密签名串 请务必按照如下顺序和规则组成加密串！	
        String sharingInfo = getRoyalty_parameters(orderid, ywtype, tradeno);
        String key = BillpayConfig.getInstance().getKey();
        //生成加密签名串
        ///请务必按照如下顺序和规则组成加密串！
        String signMsgVal = "";
        signMsgVal = appendParam(signMsgVal, "inputCharset", inputCharset);
        signMsgVal = appendParam(signMsgVal, "version", version);
        signMsgVal = appendParam(signMsgVal, "signType", signType);
        signMsgVal = appendParam(signMsgVal, "orderId", orderId);
        signMsgVal = appendParam(signMsgVal, "sharingTime", sharingTime);
        signMsgVal = appendParam(signMsgVal, "pid", pid);
        signMsgVal = appendParam(signMsgVal, "sharingInfo", sharingInfo);
        signMsgVal = appendParam(signMsgVal, "key", key);
        Pkipair Pkipair = new Pkipair();
        String signMsg = Pkipair.signMsg(signMsgVal, "");
        signMsgVal = appendParam(signMsgVal, "signMsg", URLEncoder.encode(signMsg));
        String parameter = "https://www.99bill.com/msgateway/recvMerchantSharingAction.htm?" + signMsgVal;

        //String redirectuser=BillpayConfig.redirecturl;
        String returnstr = httpget(parameter, "utf-8");
        SAXBuilder build = new SAXBuilder();
        Document document;
        try {
            document = build.build(new StringReader(returnstr));
            Element root = document.getRootElement();
            version = root.getChildText("version");
            orderId = root.getChildText("orderId");
            sharingTime = root.getChildText("sharingTime");
            pid = root.getChildText("pid");
            dealTime = root.getChildText("dealTime");
            sharingInfo = root.getChildText("sharingInfo");
            payResult = root.getChildText("payResult");
            errCode = root.getChildText("errCode");
            signType = root.getChildText("signType");
            signMsg = root.getChildText("signMsg");
            if (payResult.equals("10")) {
                logger.error(orderid + "");
            }
            else if (payResult.equals("11")) {
                logger.error(orderid + "errCode:" + errCode + "signMsg:" + errCode);
            }
        }
        catch (Exception e) {
            logger.error("分润失败");
            e.printStackTrace();
        }

        return "";

    }

    /*
     * (non-Javadoc)
     * 
    	接收方的忚钱账户Email。允许填写多个.每个email之间用|隔开每个Email 只能分一次，一旦分了就把原
    	来提交的分账明细中要分给此Email 的金额一次性分账
    	到位。
     * 
     * 
     */
    public static String getRoyalty_parameters(long orderid, int ywtype, String tradeno) {
        String royaltysb = "";
        prifitshares = new ArrayList<Profitshare>();
        Map<String, String> map = new HashMap<String, String>();
        List<Profitshare> rebates = Server.getInstance().getB2BSystemService().findAllProfitByOid(orderid, ywtype);
        for (Profitshare share : rebates) {
            if ((share.getStatus() != Profitstate.NOSHARE) || share.getPagentid() == 0) {//接口平台不在易订行分润。
                continue;
            }
            boolean shareenable = true;
            String note = "";
            Customeragent agent = (Customeragent) Server.getInstance().getMemberService()
                    .findCustomeragent(share.getPagentid());
            if ((share.getPagentid() == 46 && AlipayConfig.getInstance().selfaccount)) {// 运营商，且不分润,未签约不分润
                shareenable = false;
                Server.getInstance().getB2BSystemService()
                        .profitSharesuccess(share.getId(), agent.getKuaibillaccount(), tradeno, Paymentmethod.EBANKPAY);
                continue;
            }
            if (agent == null) {
                shareenable = false;
                share.setNote("未找到此代理");
            }
            else if (agent.getKuaibillaccount() == null || agent.getKuaibillaccount() == "") {
                shareenable = false;
                share.setNote("账户未维护");
            }
            else if (map.containsKey(agent.getKuaibillaccount())) {
                shareenable = false;
                note = "分润账户不可重复";
            }
            share.setAccount(agent.getKuaibillaccount());
            if (shareenable) {
                double money = share.getProfit();
                map.put(share.getAccount(), "");
                if (royaltysb.length() > 0) {
                    royaltysb += "|";
                }
                royaltysb += agent.getKuaibillaccount();
                prifitshares.add(share);
            }
            if (!shareenable) {
                Server.getInstance().getB2BSystemService()
                        .unableProfitShare(share.getId(), agent.getKuaibillaccount(), note, Paymentmethod.EBANKPAY);
            }

        }
        return royaltysb;

    }

    public static boolean isNotnullorEpt(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        else {
            return true;
        }
    }

    //功能函数。将变量值不为空的参数组成字符串。结束
    // 功能函数。将变量值不为空的参数组成字符串
    public static String appendParam(String returnStr, String paramId, String paramValue) {
        if (!returnStr.equals("")) {
            if (!paramValue.equals("")) {
                returnStr = returnStr + "&" + paramId + "=" + paramValue;
            }
        }
        else {
            returnStr = paramId + "=" + paramValue;
        }
        return returnStr;
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

}