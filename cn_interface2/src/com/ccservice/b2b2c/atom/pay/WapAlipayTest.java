package com.ccservice.b2b2c.atom.pay;

import java.net.InetAddress;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alipay.util.UtilDate;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.mobile.AlipaySubmit;
import com.ccservice.b2b2c.atom.server.Server;
import com.pay.config.AlipayConfig;

/**
 * wap手机支付
 * @author wzc
 * @version 创建时间：2015年7月20日 下午2:17:53
 */
public class WapAlipayTest implements Pay {

    /**
     * 获取支付信息
     * @return
     */
    public PayEntryInfo findAgentInfo(long agentid) {
        PayEntryInfo info = null;
        String sql = "select  PARTNERID, KEYSTR, SELLEREMAIL, REFUNDKEY, ACCOUNTTYPE, AGENTID,PrivateKey, PublicKey from payinfo with(nolock) where agentid="
                + agentid;
        List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        info = new PayEntryInfo();
        if (list.size() == 1) {
            Map map = (Map) list.get(0);
            String pid = map.get("PARTNERID") == null ? "" : map.get("PARTNERID").toString();
            String keystr = map.get("KEYSTR") == null ? "" : map.get("KEYSTR").toString();
            String sellemail = map.get("SELLEREMAIL") == null ? "" : map.get("SELLEREMAIL").toString();
            String agentidstr = map.get("AGENTID") == null ? "" : map.get("AGENTID").toString();
            String privateKey = map.get("PrivateKey") == null ? "" : map.get("PrivateKey").toString();
            String publicKey = map.get("PublicKey") == null ? "" : map.get("PublicKey").toString();
            info.setPid(pid);
            info.setKey(keystr);
            info.setSellemail(sellemail);
            info.setAgentid(Long.valueOf(agentidstr));
            info.setPrivateKey(privateKey);
            info.setPublicKey(publicKey);
        }
        else {
            info = findAgentInfo(46l);
        }
        return info;
    }

    public static void main(String[] args) {
        try {
            new WapAlipayTest().pay(309);
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void pay(long agentid) throws Exception {
        PayEntryInfo info = findAgentInfo(agentid);
        //支付宝网关地址
        String ALIPAY_GATEWAY_NEW = "http://wappaygw.alipay.com/service/rest.htm?";
        ////////////////////////////////////调用授权接口alipay.wap.trade.create.direct获取授权码token//////////////////////////////////////
        //返回格式
        String format = "xml";
        //必填，不需要修改
        //返回格式
        String v = "2.0";
        //必填，不需要修改
        //请求号
        String req_id = UtilDate.getOrderNum("sdf");
        //必填，须保证每次请求都是唯一
        //req_data详细信息
        //服务器异步通知页面路径
        String ServerName = "127.0.0.1";
        try {
            String localhost = InetAddress.getLocalHost().toString().split("/")[1];
            ServerName = localhost;
            ServerName = "121.40.226.72";
            WriteLog.write("alipay_pay", "serverhost:" + ServerName);
        }
        catch (Exception e) {
        }
        WriteLog.write("alipay_pay_web", "localhost:" + ServerName);
        String notify_url = "http://127.0.0.1:80/cn_interface/wapAlipayNotify";
        //        String notify_url = "http://124.254.60.66:9004/cn_interface/wapAlipayNotify";
        WriteLog.write("alipay_pay_web", "localhost:" + notify_url);
        //需http://格式的完整路径，不能加?id=123这类自定义参数
        //页面跳转同步通知页面路径
        String call_back_url = "http://58.67.193.171:8080/114train/trainreslist";
        //需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/
        //操作中断返回地址
        String merchant_url = "http://58.67.193.171:8080/114train/trainreslist";
        //用户付款中途退出返回商户的地址。需http://格式的完整路径，不允许加?id=123这类自定义参数
        //卖家支付宝帐户
        String seller_email = info.getSellemail();
        //必填
        //商户订单号
        //new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"), "UTF-8");
        // 调取支付宝工具类生成订单号
        com.alipay.util.UtilDate utilDate = new com.alipay.util.UtilDate();
        String out_trade_no = utilDate.getOrderNum("123123");//"wadfasdfasdfasd1f11111111";//
        //商户网站订单系统中唯一订单号，必填
        //订单名称
        String subject = "123Fg1231";
        //必填
        //付款金额
        String total_fee = String.valueOf(0.1);
        //必填
        //请求业务参数详细
        String req_dataToken = "<direct_trade_create_req><notify_url>" + notify_url + "</notify_url><call_back_url>"
                + call_back_url + "</call_back_url><seller_account_name>" + seller_email
                + "</seller_account_name><out_trade_no>" + out_trade_no + "</out_trade_no><subject>" + subject
                + "</subject><total_fee>" + total_fee + "</total_fee><merchant_url>" + merchant_url
                + "</merchant_url></direct_trade_create_req>";
        //必填
        //把请求参数打包成数组
        Map<String, String> sParaTempToken = new HashMap<String, String>();
        sParaTempToken.put("service", "alipay.wap.trade.create.direct");
        sParaTempToken.put("partner", info.getPid());
        sParaTempToken.put("_input_charset", AlipayConfig.getInstance().getCharSet());
        sParaTempToken.put("sec_id", AlipayConfig.getInstance().getSign_type());
        sParaTempToken.put("format", format);
        sParaTempToken.put("v", v);
        sParaTempToken.put("req_id", req_id);
        sParaTempToken.put("req_data", req_dataToken);
        WriteLog.write("alipay_pay_web", "req_data:" + req_dataToken);
        //建立请求
        String sHtmlTextToken = AlipaySubmit.buildRequest(ALIPAY_GATEWAY_NEW, "", "", sParaTempToken, info.getKey(),
                info.getPrivateKey());
        //URLDECODE返回的信息
        sHtmlTextToken = URLDecoder.decode(sHtmlTextToken, "UTF-8");
        //获取token
        String request_token = AlipaySubmit.getRequestToken(sHtmlTextToken, info.getPrivateKey());
        WriteLog.write("alipay_pay_web", "request_token:" + request_token);
        //业务详细
        String req_data = "<auth_and_execute_req><request_token>" + request_token
                + "</request_token></auth_and_execute_req>";
        //必填
        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "alipay.wap.auth.authAndExecute");
        sParaTemp.put("partner", info.getPid());
        sParaTemp.put("_input_charset", AlipayConfig.getInstance().getCharSet());
        sParaTemp.put("sec_id", AlipayConfig.getInstance().getSign_type());
        sParaTemp.put("format", format);
        sParaTemp.put("v", v);
        sParaTemp.put("req_data", req_data);
        //建立请求
        String sHtmlText = AlipaySubmit.buildRequest(ALIPAY_GATEWAY_NEW, sParaTemp, "get", "确认", info.getKey(),
                info.getPrivateKey());
        System.out.println(sHtmlText);
        WriteLog.write("alipay_pay_web", "paydata:" + sHtmlText);
    }

    @Override
    public void pay(float factorage) throws Exception {

    }

}
