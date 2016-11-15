package com.ccservice.b2b2c.atom.service12306.offlineRefund.local;

import java.util.Map;
import java.util.List;
import org.jsoup.Jsoup;
import java.util.HashMap;
import java.sql.Timestamp;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;
import com.ccservice.b2b2c.base.uniontrade.Uniontrade;
import com.ccservice.b2b2c.atom.servlet.MQ.HttpsClientUtils;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class AliPayRefundUtil {

    public static final Timestamp NullDepartTime = null;//发车时间，空

    /**
     * 虚拟退款记录
     */
    public static Uniontrade getRefundTrade(float aliTotalRefund, Timestamp aliRefundTime) {
        Uniontrade temp = new Uniontrade();
        //虚拟标示
        temp.setId(-1L);
        temp.setAmount(aliTotalRefund);
        //时间非空
        if (aliRefundTime != NullDepartTime) {
            temp.setOrdertime(aliRefundTime);
        }
        else {
            temp.setOrdertime(new Timestamp(System.currentTimeMillis()));
        }
        //返回
        return temp;
    }

    /**
     * 支付服务器对应地址
     */
    @SuppressWarnings({ "unchecked" })
    public static Map<String, String> payUrlMap() {
        Map<String, String> map = new HashMap<String, String>();
        //SQL
        String sql = "where C_NAME like 'alipayurl%'";
        //查询
        List<Sysconfig> list = Server.getInstance().getSystemService().findAllSysconfig(sql, "", -1, 0);
        //循环
        for (Sysconfig config : list) {
            map.put(config.getName(), config.getValue());
        }
        return map;
    }

    /**
     * 获取支付宝退款
     * @param aliPayPath 支付服务器
     * @param aliPayAcount 支付账号
     * @param aliTradeNo 支付流水号
     * @param payUrlMap key:支付服务器，value:对应支付地址
     * @return 退款总额
     */
    public static float refundPrice(String aliPayPath, String aliPayAcount, String aliTradeNo,
            Map<String, String> payUrlMap) {
        //数据非空
        if (!ElongHotelInterfaceUtil.StringIsNull(aliPayPath) && !ElongHotelInterfaceUtil.StringIsNull(aliPayAcount)
                && !ElongHotelInterfaceUtil.StringIsNull(aliTradeNo) && payUrlMap != null && payUrlMap.size() > 0) {
            try {
                //重拿
                if (aliPayPath.contains("/alipayurl")) {
                    aliPayPath = aliPayPath.substring(aliPayPath.indexOf("alipayurl"));
                }
                //地址
                String url = payUrlMap.get(aliPayPath);
                //非空
                if (!ElongHotelInterfaceUtil.StringIsNull(url)) {
                    return catchPrice(url, aliTradeNo, aliPayAcount);
                }
            }
            catch (Exception e) {

            }
        }
        return -1;
    }

    /**
     * 公共抓退款方法
     * @return 0：无退款；－1：数据变化或异常；大于0：具体退款
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static float catchPrice(String aliPayUrl, String aliTradeNo, String aliPayAcount) {
        //数据非空
        if (!ElongHotelInterfaceUtil.StringIsNull(aliPayUrl) && !ElongHotelInterfaceUtil.StringIsNull(aliTradeNo)
                && !ElongHotelInterfaceUtil.StringIsNull(aliPayAcount)) {
            try {
                //参数
                JSONObject json = new JSONObject();
                json.put("cmd", "payorderdetail");
                json.put("AliTradeNo", aliTradeNo);
                json.put("alipayaccount", aliPayAcount);
                //数据
                Map map = new HashMap();
                map.put("data", json.toString());
                //结果
                String result = HttpsClientUtils.posthttpclientdata(aliPayUrl, map, 10 * 1000L);
                //获取退款总额
                if (result != null && result.contains(aliTradeNo)) {
                    Document doc = Jsoup.parse(result);
                    //存在退款
                    if (result.contains("refund amount-pay-in")) {
                        Elements list = doc.select("p[class=refund amount-pay-in]");
                        for (Element element : list) {
                            return Float.parseFloat(element.html().trim().replace("+", "").trim());
                        }
                    }
                    //不存在退款
                    else if (result.contains("amount-pay-out")) {
                        return 0;
                    }
                }
            }
            catch (Exception e) {

            }
        }
        return -1;
    }
}
