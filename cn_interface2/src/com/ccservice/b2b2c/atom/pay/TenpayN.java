package com.ccservice.b2b2c.atom.pay;

import java.net.InetAddress;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alipay.util.UtilDate;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.helper.Payhelper;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.traderecord.Traderecord;
import com.ccservice.b2b2c.util.AirUtil;
import com.pay.config.TenpayConfig;
import com.tenpay.util.TenpayUtil;

/**
 * 
 * @author wzc
 * 财付通支付
 *
 */
public class TenpayN extends PaySupport implements Pay {

    public static final String paygetway = "https://gw.tenpay.com/gateway/pay.htm";//支付接口地址

    public TenpayN(HttpServletRequest request, HttpServletResponse response, Payhelper payhelper) {
        super(request, response, payhelper);
    }

    @Override
    public void pay(float factorage) throws Exception {
        factorage = 0;
        WriteLog.write("Tenpay_pay", payhelper.getOrdernumber() + "财付通支付交易记录:交易开始");
        String cmdno = "1";//业务代码, 财付通支付支付接口填  1 

        /* 商户号，上线时务必将测试商户号替换为正式商户号 */
        String partner = TenpayConfig.getInstance().getPartnerID();
        //密钥
        String key = TenpayConfig.getInstance().getKey();
        String ServerName = request.getServerName();
        WriteLog.write("Tenpay_pay", "localhost:" + ServerName);
        String currTime = TenpayUtil.getCurrTime();
        //交易完成后跳转的URL
        String return_url = "http://127.0.0.1/";
        String host = "";
        try {
            String localhost = InetAddress.getLocalHost().toString().split("/")[1];
            host = localhost;
            WriteLog.write("Tenpay_pay", "serverhost:" + host);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //接收财付通通知的URL
        String notify_url = "http://" + host + ":" + request.getServerPort() + "/cn_interface/TenpaynotifyHandle";
        //String notify_url = "http://211.103.207.134:8080/cn_interface/TenpaynotifyHandle";
        WriteLog.write("Tenpay_pay", "localhost:" + notify_url);
        String out_trade_no = UtilDate.getOrderNum(payhelper.getOrdernumber());
        String sp_billno = payhelper.getOrdernumber();
        String body = payhelper.getOrderDescription();
        //总金额，以分为单位,不允许包含任何字符
        //String total_fee = String.valueOf((int) ((payhelper.getOrderprice() + factorage) * 100));
        String total_fee = (int) (payhelper.getOrderprice() * 100) + "";
        String fee_type = "1";//RMB人民币
        //商家数据包，原样返回
        String attach = payhelper.getOrdernumber() + "Fg" + payhelper.getHandleName();
        //用户IP（非商户服务器IP），为了防止欺诈，支付时财付通会校验此IP
        String spbill_create_ip = AirUtil.getBrowserIp(request);
        Traderecord traderecord = Server.getInstance().getB2BSystemService()
                .findTradeByOrderunmber(payhelper.getOrdernumber());

        String bank_type = "0";//银行类型:财付通支付填0
        if (request.getParameter("pay_bank") != null) {
            bank_type = new String(request.getParameter("pay_bank").getBytes(), "UTF-8");
        }
        if (traderecord == null) {
            traderecord = new Traderecord();
            try {
                traderecord.setCreateuser("TenpayN");
                traderecord.setGoodsname(payhelper.getOrdername());
                traderecord.setCode(out_trade_no);// 外部订单号。
                traderecord.setOrdercode(payhelper.getOrdernumber());
                traderecord.setPayname("财付通");
                traderecord.setPaytype(0);// 0支付宝 1财付通
                traderecord.setState(0);// 0等待支付1支付成功2支付失败
                traderecord.setTotalfee((int) payhelper.getOrderprice());// 支付金额分为单位
                traderecord.setType(payhelper.getTradetype());// 订单类型
                traderecord.setPaymothed(cmdno);// 支付方式
                traderecord.setBankcode(bank_type);// 支付银行
                traderecord = Server.getInstance().getMemberService().createTraderecord(traderecord);
            }
            catch (Exception e) {
                WriteLog.write("Tenpay_pay", "支付异常：" + e.fillInStackTrace() + "");
            }
        }

        SortedMap<String, String> params = new TreeMap<String, String>();
        params.put("paygateway", paygetway);
        params.put("partner", partner); //商户号
        params.put("out_trade_no", out_trade_no); //商家订单号
        params.put("total_fee", total_fee); //商品金额,以分为单位
        params.put("return_url", return_url); //交易完成后跳转的URL
        params.put("notify_url", notify_url); //接收财付通通知的URL
        params.put("body", body); //商品描述
        params.put("bank_type", bank_type); //银行类型
        params.put("spbill_create_ip", spbill_create_ip); //用户的公网ip
        params.put("fee_type", fee_type);
        //系统可选参数
        params.put("sign_type", "MD5");
        params.put("service_version", "1.0");
        params.put("input_charset", TenpayConfig.getCharSet());
        params.put("sign_key_index", "1");
        //业务可选参数
        params.put("attach", attach);
        params.put("product_fee", "");
        params.put("transport_fee", "");
        params.put("time_start", currTime);
        params.put("time_expire", "");
        params.put("buyer_id", "");
        params.put("goods_tag", "");
        String ItemUrl_Get = TenpayUtil.getRequestURL(params, key);
        WriteLog.write("Tenpay_pay", ItemUrl_Get);
        response.sendRedirect(ItemUrl_Get);
    }

    @Override
    public void pay(long agentid) throws Exception {
        // TODO Auto-generated method stub

    }
}
