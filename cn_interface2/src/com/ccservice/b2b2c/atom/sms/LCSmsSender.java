package com.ccservice.b2b2c.atom.sms;

import java.net.URLEncoder;

import com.ccservice.b2b2c.ben.Dnsbarends;

public class LCSmsSender implements SmsSender {

    /**
     * 浪弛短信接口
     */
    private String username;

    private String password;

    private String ipAddress;

    private String userid;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // @Override
    // public int sendSMS(String[] mobiles, String content, long ordercode,
    // long sendagentid, Dnsbarends dns) {
    // // TODO Auto-generated method stub
    // return 0;
    // }
    /**
     * 发送短信 发送单条短信
     * http://www.lanz.net.cn/LANZGateway/Login.asp?UserID=1234567&Account=ABCDE&Password=12345678
     * ActiveID：登录时获得的ActiveID ErrorNum：0：成功 其它：失败 ActiveID：（16位字符串）
     */
    @Override
    public boolean sendSMS(String[] mobiles, String content, long ordercode, long sendagent, Dnsbarends dns) {
        try {
            int intReturn = 0;
            java.io.InputStream in = null;
            String totalurl = "";
            // String strparm="SendSMS.asp?ActiveID="+strActiveID+"&SMSType=1";
            // strparm+="&Phone="++"&Content=aaaaaaaaaa";//+URLEncoder.encode(content);
            totalurl = "http://localhost:10091/sms.aspx?cmd=http://www.lanz.net.cn/LANZGateway/SendSMS.asp?SMSType=1&Phone="
                    + mobiles[0] + "&Content=" + URLEncoder.encode(content, "GB2312") + "&ActiveID=";
            try {
                java.net.URL Url = new java.net.URL(totalurl);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                in = conn.getInputStream();
                org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
                org.jdom.Document doc = build.build(in);
                org.jdom.Element data = doc.getRootElement();
                System.out.println("发送短信代码：" + data.getChildTextTrim("ErrorNum"));
                in.close();
                conn.disconnect();
                if (data.getChildTextTrim("ErrorNum").equals("0")) {
                    return true;

                }
                else {
                    return false;
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    @Override
    public String smsBalanceInquiry() {
        // TODO Auto-generated method stub
        return null;
    }

}
