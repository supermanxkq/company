package com.ccservice.b2b2c.atom.pay.gp.certificate;

import com.callback.PropertyUtil;
import com.ccservice.b2b2c.util.ActiveMQUtil;

/**
 * @author wzc
 *
 */
public class GpCertificationMqUtil {
    /**
     * 创建支付凭证
     * @param orderid
     */
    public static void sendMq(long orderid) {
        String url = PropertyUtil.getValue("CertificationMq", "GpAir.properties");
        ActiveMQUtil.sendMessage(url, "GpcertificateCreate", orderid + "");
    }
}
