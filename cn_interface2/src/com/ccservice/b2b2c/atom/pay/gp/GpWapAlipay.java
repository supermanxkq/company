package com.ccservice.b2b2c.atom.pay.gp;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.alipay.util.UtilDate;
import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.PayEntryInfo;
import com.ccservice.b2b2c.atom.pay.mobile.AlipaySubmit;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.pay.config.AlipayConfig;

/**
 * Gpwap手机支付
 * @author wzc
 * @version 创建时间：2015年7月20日 下午2:17:53
 */
public class GpWapAlipay extends AirSupper {
    public static void main(String[] args) {
        JSONObject msgobj = new JSONObject();
        String msg = new GpWapAlipay(1739, "YDXA201606211531131").pay(msgobj, 0.1, "", 1);
        System.out.println(msg);
    }

    private Orderinfo orderinfo = null;

    /**
     * 
     * @param orderid
     * @param OrderNumber
     */
    public GpWapAlipay(long orderid, String OrderNumber) {
        if (orderid <= 0) {
            String sql = "select Id from T_ORDERINFO with(nolock) WHERE C_ORDERNUMBER='" + OrderNumber + "'";
            List<Map> list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            if (list.size() > 0) {
                Map map = (Map) list.get(0);
                orderid = Long.parseLong(map.get("Id").toString());
            }
        }
        orderinfo = Server.getInstance().getAirService().findOrderinfo(orderid);
    }

    public String pay(JSONObject msgobj, double money, String goodsDesc, int ind) {
        try {
            if (orderinfo.getPolicyagentid() == null || orderinfo.getPolicyagentid() <= 0) {
                return "无效订单供应商";
            }
            PayEntryInfo info = findAgentInfo(orderinfo.getPolicyagentid().longValue() + "", 2);
            if (info == null) {
                return "无支付信息";
            }
            //支付宝网关地址
            String ALIPAY_GATEWAY_NEW = "http://wappaygw.alipay.com/service/rest.htm?";
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
            String notify_url = PropertyUtil.getValue("notifyurlwap", "GpAir.properties");
            WriteLog.write("GPalipay_pay_web", "localhost:" + notify_url);
            //需http://格式的完整路径，不能加?id=123这类自定义参数
            //页面跳转同步通知页面路径
            String call_back_url = "";
            //操作中断返回地址
            String merchant_url = "";
            //用户付款中途退出返回商户的地址。需http://格式的完整路径，不允许加?id=123这类自定义参数
            //卖家支付宝帐户
            String seller_email = info.getSellemail();
            //必填
            //商户订单号
            //new String(request.getParameter("WIDout_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            // 调取支付宝工具类生成订单号
            com.alipay.util.UtilDate utilDate = new com.alipay.util.UtilDate();
            String out_trade_no = utilDate.getOrderNum(orderinfo.getOrdernumber());//"wadfasdfasdfasd1f11111111";//
            //商户网站订单系统中唯一订单号，必填
            //订单名称
            String subject = orderinfo.getOrdernumber() + "FgGpAirnofiryHandle";
            //必填
            //付款金额
            String total_fee = String.valueOf(money);
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
            WriteLog.write("GPalipay_pay_web", orderinfo.getId() + ":req_data:" + req_dataToken);
            //建立请求
            String sHtmlTextToken = AlipaySubmit.buildRequest(ALIPAY_GATEWAY_NEW, "", "", sParaTempToken, info.getKey(),
                    info.getPrivateKey());
            //URLDECODE返回的信息
            sHtmlTextToken = URLDecoder.decode(sHtmlTextToken, "UTF-8");
            //获取token
            String request_token = AlipaySubmit.getRequestToken(sHtmlTextToken, info.getPrivateKey());
            WriteLog.write("GPalipay_pay_web", orderinfo.getId() + ":request_token:" + request_token);
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
            sParaTemp.put("paygateway", ALIPAY_GATEWAY_NEW);
            //建立请求
            String ItemUrl_Get = com.alipay.util.Payment.CreateUrl(sParaTemp, info.getKey());
            //            String sHtmlText = AlipaySubmit.buildRequest(ALIPAY_GATEWAY_NEW, sParaTemp, "get", "确认", info.getKey(),
            //                    info.getPrivateKey());
            return ItemUrl_Get;
        }
        catch (Exception e) {
            WriteLog.write("GPalipay_pay_web", orderinfo.getId() + ":接口异常" + e.getMessage());
            return "接口异常";
        }
    }
}