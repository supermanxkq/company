package com.ccservice.b2b2c.atom.pay;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.service.Profitshare;
import com.ccservice.b2b2c.base.service.Profitshare.Profitstate;
import com.ccservice.b2b2c.ben.AirticketPaymentrecord;
import com.ccservice.b2b2c.ben.Paymentmethod;
import com.pay.config.AlipayConfig;
import com.pay.config.Chinapnrconfig;

/**
 * @author Administrator
 * 汇付天下分账 变更。
 *
 */
public class Chinapnrdistribute {
    static final long serialVersionUID = 1L;

    private static final String gateurl = "http://mas.chinapnr.com/gao/entry.do?";

    private static final String Version = "10";//版本号

    private static final String MerId = Chinapnrconfig.getInstance().getPartnerID();//商户号

    private static final String CmdId = "PaymentConfirm";//消息类型。

    static DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();

    static Logger logger = Logger.getLogger(Chinapnrdistribute.class.getSimpleName());

    static List<Profitshare> prifitshares;

    /*
     * (non-Java-doc)
     * 
     * @see javax.servlet.http.HttpServlet#doGet(HttpServletRequest request,
     *      HttpServletResponse response)
     */
    public static String distribute(long orderid, int ywtype) {
        logger.error("汇付天下异步分账开始开始噢噢噢噢");

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
        String IsBalance = "Y";
        String OrdId = orderid + "";
        String DivDetails = getRoyalty_parameters(orderid, ywtype, tradeno);
        ;//分账明细
        String ChkValue = "";//签名

        //签名
        String MerKeyFile = "";
        try {
            MerKeyFile = URLDecoder.decode(Chinapnrdistribute.class.getClassLoader().getResource("/").getPath(),
                    "UTF-8") + "MerPrK" + MerId + ".key";
        }
        catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } //商户私钥文件路径  请将MerPrK510010.key改为你的私钥文件名称
        System.out.println("MerKeyFile:" + MerKeyFile);
        String MerData = Version + CmdId + MerId + OrdId;
        System.out.println(MerData);
        SecureLink sl = new SecureLink();
        int ret = sl.SignMsg(MerId, MerKeyFile, MerData);
        System.out.println("ret：" + ret);

        //		if (ret != 0) 
        //		{
        //			System.out.println("签名错误 ret=" + ret );
        //			return "签名错误";
        //		}
        ChkValue = sl.getChkValue();
        System.out.println("ChkValue:" + ChkValue);
        Map<String, String> map = new HashMap<String, String>();
        map.put("Version", Version);
        map.put("CmdId", CmdId);
        map.put("MerId", MerId);
        map.put("IsBalance", IsBalance);
        map.put("OrdId", OrdId);
        map.put("DivDetails", DivDetails);
        map.put("ChkValue", ChkValue);
        String param = createLinkString(map);
        System.out.println("请求参数");
        logger.error("请求参数×××××××××××××××××××××××：");
        logger.error("请求参数:" + param);

        try {
            URL neturl = new URL(gateurl + param);
            HttpURLConnection connection = (HttpURLConnection) neturl.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.connect();
            BufferedInputStream buffer = new BufferedInputStream(connection.getInputStream());
            byte[] bytes = new byte[2048];
            ByteArrayOutputStream byteout = new ByteArrayOutputStream();
            int i = 0;
            while ((i = buffer.read(bytes)) > 0) {
                byteout.write(bytes, 0, i);
            }
            buffer.close();
            connection.disconnect();
            String str = new String(bytes, "GBK");
            logger.error("返回结果：" + str);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
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
     *Agent:user1:50.00;Agent:user2:800.00
     *多笔分帐时使用分号分割，其中角色固定填写Agent例如订单金额为1000元，分账明细为： 
     *Agent:user1:50.00;Agent:user2:800.00 表示分给user1，50元，分给user2，800元，剩余的150
     *元分给商户本身
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
                        .profitSharesuccess(share.getId(), agent.getChinapnrcount(), tradeno, Paymentmethod.EBANKPAY);
                continue;
            }
            if (agent == null) {
                shareenable = false;
                share.setNote("未找到此代理");
            }
            else if (agent.getChinapnrcount() == null || agent.getChinapnrcount() == "") {
                shareenable = false;
                share.setNote("账户未维护");
            }
            else if (map.containsKey(agent.getChinapnrcount())) {
                shareenable = false;
                note = "分润账户不可重复";
            }
            share.setAccount(agent.getChinapnrcount());
            if (shareenable) {
                double money = share.getProfit();
                map.put(share.getAccount(), "");
                if (royaltysb.length() > 0) {
                    royaltysb = ";Agent:" + share.getAccount() + ":" + format.format(money) + royaltysb;
                }
                else {
                    royaltysb = "Agent:" + share.getAccount() + ":" + format.format(money) + royaltysb;
                }
                prifitshares.add(share);
            }
            if (!shareenable) {
                Server.getInstance().getB2BSystemService()
                        .unableProfitShare(share.getId(), agent.getChinapnrcount(), note, Paymentmethod.EBANKPAY);
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
}