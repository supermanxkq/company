package com.pay.config;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.PayInfo;

public class TenpayConfig extends PayConfig {
    private TenpayConfig() {
    };

    private static TenpayConfig tenpayconfig;

    private static String charSet = "GBK";

    public static TenpayConfig getInstance() {
        if (tenpayconfig == null) {
            tenpayconfig = new TenpayConfig();
            PayInfo payinfo = Server.getInstance().getB2BSystemService().findPayInfoBytypeId(5);
            tenpayconfig.setKey(payinfo.getKey());
            tenpayconfig.setPartnerID(payinfo.getPartnerId());
            tenpayconfig.setSellerEmail(payinfo.getSellerEmail());
            tenpayconfig.setSelfaccount(payinfo.getAccounttype() == 0);
        }
        return tenpayconfig;
    }

    public static String getCharSet() {
        return charSet;
    }

    public static void setCharSet(String charSet) {
        TenpayConfig.charSet = charSet;
    }

}
