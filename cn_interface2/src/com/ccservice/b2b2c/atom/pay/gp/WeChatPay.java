package com.ccservice.b2b2c.atom.pay.gp;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONObject;
import com.alipay.util.WeChatPayment;
import com.callback.PropertyUtil;
import com.callback.SendPostandGet;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.atom.pay.PayEntryInfo;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;

/**
 * 微信支付
 * @author wzc
 *
 */
public class WeChatPay extends AirSupper {
    public static void main(String[] args) {
        JSONObject msgobj = new JSONObject();
        String msg = new WeChatPay(1739, "YDXA201606211531131").pay(msgobj, 0.01, "订单支付啊", 1);
        System.out.println(msg);
    }

    public static final String paygateway = "https://api.mch.weixin.qq.com/pay/unifiedorder"; // 支付接口（不可以修改）

    private Orderinfo orderinfo = null;

    /**
     * 
     * @param orderid
     * @param OrderNumber
     */
    public WeChatPay(long orderid, String OrderNumber) {
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

    /**
     * 微信支付
     * @throws Exception
     */
    public String pay(JSONObject msgobj, double money, String goodsDesc, int ind) {
        if (orderinfo == null) {
            return "未查询到订单";
        }
        if (orderinfo.getPolicyagentid() == null || orderinfo.getPolicyagentid() <= 0) {
            return "无效订单供应商";
        }
        String ordernumber = orderinfo.getOrdernumber();
        long orderid = orderinfo.getId();
        PayEntryInfo info = findAgentInfo(orderinfo.getPolicyagentid().longValue() + "", 3);
        String appid = "";
        if (info == null) {
            return "微信账户缺失";
        }
        else {
            appid = info.getPid();
        }
        //附加数据，在查询API和支付通知中原样返回，该字段主要用于商户携带订单的自定义数据
        String attach = orderinfo.getOrdernumber() + "FgAirnofiryHandle";
        String body = goodsDesc;
        String mch_id = info.getSellemail();//微信支付分配的商户号
        String detail = "";//商品详细列表
        String nonce_str = UUID.randomUUID().toString().replaceAll("-", "");//随机字符串，不长于32位
        String notify_url = PropertyUtil.getValue("weChatnotyfyUrl", "GpAir.properties");
        String openid = "";//trade_type=JSAPI，此参数必传，用户在商户appid下的唯一标识。openid如何获取，可参考【获取openid】。企业号请使用【企业号OAuth2.0接口】获取企业号内成员userid，再调用【企业号userid转openid接口】进行转换
        com.alipay.util.UtilDate utilDate = new com.alipay.util.UtilDate();
        String out_trade_no = utilDate.getOrderNum(orderinfo.getOrdernumber()).substring(0, 32);//商户系统内部的订单号,32个字符内、可包含字母, 其他说明见
        String spbill_create_ip = "";//APP和网页支付提交用户端ip，Native支付填调用微信支付API的机器IP。
        String total_fee = String.valueOf((int) (money * 100));//订单总金额，单位为分
        String trade_type = "NATIVE";//JSAPI--公众号支付、NATIVE--原生扫码支付、APP--app支付，统一下单接口trade_type的传参可参考这里MICROPAY--刷卡支付，刷卡支付有单独的支付接口，不调用统一下单接口
        String sign = "";
        String resultStr = SendPostandGet.submitGet("http://ws.jinri.cn/getip.aspx");
        if (resultStr.contains("DOCTYPE")) {
            spbill_create_ip = resultStr.split("<!DOCTYPE")[0];
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("appid", appid);
        params.put("attach", attach);
        params.put("body", body);
        params.put("mch_id", mch_id);
        params.put("detail", detail);
        params.put("nonce_str", nonce_str);
        params.put("notify_url", notify_url);
        params.put("openid", openid);
        params.put("out_trade_no", out_trade_no);
        params.put("spbill_create_ip", spbill_create_ip);
        params.put("total_fee", total_fee);
        params.put("trade_type", trade_type);
        sign = WeChatPayment.Sign(params, info.getKey());
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        sb.append("<appid>" + appid + "</appid>");
        sb.append("<attach>" + attach + "</attach>");
        sb.append("<body>" + body + "</body>");
        sb.append("<mch_id>" + mch_id + "</mch_id>");
        sb.append("<detail>" + detail + "</detail>");
        sb.append("<nonce_str>" + nonce_str + "</nonce_str>");
        sb.append("<notify_url>" + notify_url + "</notify_url>");
        sb.append("<openid>" + openid + "</openid>");
        sb.append("<out_trade_no>" + out_trade_no + "</out_trade_no>");
        sb.append("<spbill_create_ip>" + spbill_create_ip + "</spbill_create_ip>");
        sb.append("<total_fee>" + total_fee + "</total_fee>");
        sb.append("<trade_type>" + trade_type + "</trade_type>");
        sb.append("<sign>" + sign + "</sign>");
        sb.append("</xml>");
        CCSHttpClient client = new CCSHttpClient(false, 60000l);
        CCSPostMethod post = new CCSPostMethod(paygateway);
        String msg = "";
        post.setRequestBody(sb.toString());
        try {
            client.executeMethod(post);
            msg = post.getResponseBodyAsString();
        }
        catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //        String msg = SendPostandGet.submitPost(paygateway, sb.toString(), "UTF-8").toString();
        String code_url = "";
        SAXReader reader = new SAXReader();
        try {
            System.out.println(msg);
            Document doc = reader.read(new StringReader(msg));
            Element root = doc.getRootElement();
            code_url = root.elementText("code_url");
            //            String prepay_id = root.elementText("prepay_id");
            System.out.println(code_url);
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
        return code_url;
    }
}
