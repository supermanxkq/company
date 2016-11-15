package com.pay.config;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.PayInfo;

/**
 * 支付宝 供货商文件信息。
 * @author Administrator
 *
 */
public class AlipayConfig extends PayConfig {
    private AlipayConfig() {
    };

    private static AlipayConfig alipayconfig;

    public static AlipayConfig getInstance() {
        if (alipayconfig == null) {
            alipayconfig = new AlipayConfig();
            PayInfo payinfo = Server.getInstance().getB2BSystemService().findPayInfoBytypeId(1);
            alipayconfig.setKey(payinfo.getKey());
            alipayconfig.setPartnerID(payinfo.getPartnerId());
            alipayconfig.setSellerEmail(payinfo.getSellerEmail());
            alipayconfig.setSelfaccount(payinfo.getAccounttype() == 0);
        }
        return alipayconfig;
    }

    // 商户的私钥
    // 如果签名方式设置为“0001”时，请设置该参数
    //public static String private_key = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKcsrdL/Rje+ITLZco8J41kC5+B4SeSUMZndjokl+1KzWYNAb/UAbq0n6yDIoonZGRoflGXYis8o9epi1MNwu6CA4q5nOqP22VWA7s3rPDjaWHWIRRtQkMDn9rvXD03Z/PvOpcKJhkFMPImg9QrEEcGK4oPadaIoulP1xOfQSiCvAgMBAAECgYAGfzsGLzuYYQBDTKYiHv1B8VX3qFUe2A+RbkXi2KIK1cON0UxIlkq22g1OSd3fQ5uxIzcOOjsyz+G2yT6BlfkEtMjkGpbjvoYXq5fCrHqmCz5xj8ceUG5BYsA2ifGjLO/voT4Lb16RmUmsyG8t30EU6N6bovmchakdQiiFOO520QJBANEsTofeSoB6+sAeMv7NMDrgsO+0MDXtHLfZE8JEo3hHve3V3LdxsMEf18gyvbI+NYU9fCHk4AkAVUweGe1JMdcCQQDMmXBO09Ki6T2JSjIq6zZD9FHkLpwl8IzecPNZBUBJElucf2vcIYieFL9N23+VMra+bmbyO8brzJjrNONAAdzpAkBY4PGxa7POafL4BRz4BanTqruj7rV5hHnqaJOrvUyUhC9gyrmRPP604NnoWB8giKxnJBwFruG/EWQtBrFPkQVbAkAtpfZWwA/45Q0jungi90OuOa6juHqCRH9Jie3haLiFFSF3cz5/aMPwcTSVjGmUwjHfnwY8+XVSWt2rbKtfNosxAkBzHW4I1ixLAnFaK5WGL1e1pvytwL78tyfNk7QT0+kpD6ggB9qjn5lXVIrcDIYtJa3ACkziqcfEuUvVTgGZQvuj";

    // 支付宝的公钥
    // 如果签名方式设置为“0001”时，请设置该参数
    //public static String ali_public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQClWJ/IK7vxiN0BmG3hGp/6xxlliiHMhMxtZgrKVPWGihecROHvC5WQc158g2qln7diZtD7/QmECQA0DYgFeix5MH0J8wm3UpKnJZtNCMpSKSGGKKHXYYe/nuspFWdCAX+C1lJO5bOGJ+2Ghl5yriQaZ/BZZis/3bPH5LUNjs7s9wIDAQAB";

    public String charSet = "UTF-8";

    public String sign_type = "MD5";

    public String antiphishing = "0";

    public static String transport = "https";

    /**
     * 技术合作运营方email
     */
    public String splitEmail = "hyccservice@126.com";

    /**
     * 分成金额
     */
    public int split = 100;

    // 调试用，创建TXT日志文件夹路径
    public static String log_path = "D:\\";

    public String getCharSet() {
        return charSet;
    }

    public String getSign_type() {
        return sign_type;
    }

    public String getAntiphishing() {
        return antiphishing;
    }

    public String getSplitEmail() {
        return splitEmail;
    }

    public int getSplit() {
        return split;
    }
}