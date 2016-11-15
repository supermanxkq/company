package com.ccservice.b2b2c.atom.sms;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.ben.Dnsbarends;

public class TianXunTongSmsSender implements SmsSender {
    Log logger = LogFactory.getLog(TianXunTongSmsSender.class);

    public static void main(String[] args) throws UnsupportedEncodingException {
        String url = "http://203.81.21.34/send/gsend.asp";
        String msg = "您好,袁浩,袁诗亮07月06号南航CZ3558,上海浦东T2至深圳--,17:25起飞20:15到达,已出票.客服4006100861;南航服务热.线电话:95539";
        msg = URLEncoder.encode(msg, "gb2312");

        String msg1 = URLDecoder.decode(msg, "gb2312");
        System.out.println(msg1);
        String paramContent = "name=ydhk001&pwd=ydhk001123&dst=15010692979&msg=" + msg + "&txt=ccdx";
        //        15010692979
        String strReturn = SendPostandGet.submitPost(url, paramContent, "gb2312").toString();
        System.out.println(strReturn);
    }

    /**
     * 现在改为九诺短信接口
     */
    private String username;

    private String password;

    private String ipAddress;

    private String userid;

    private Customeragent customeragent;

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

    /**
     * 现在改为九诺短信接口
     * 
     * @param strMobiles
     * @param Flightdate
     * @param Fno
     * @param Dep
     * @param Arr
     * @param Pname
     * @param Type  
     * @return
     */
    public boolean sendSMS(String[] mobiles, String content, long ordercode, long sendagentid, Dnsbarends dns) {

        String strMobiles = "";
        try {
            strMobiles = mobiles[0];
            for (int i = 1; i < mobiles.length; i++) {
                strMobiles += "," + mobiles[i];
            }
            if (strMobiles.length() > 0) {
                String totalurl = ipAddress + "/gsend.asp";
                String paramContent = "name=" + dns.getSmscounter() + "&pwd=" + dns.getSmspwd() + "&dst=" + strMobiles
                        + "&msg=" + URLEncoder.encode(content, "gb2312") + "&txt=ccdx";
                // / logger.error("发送短信："+totalurl);
                WriteLog.write("SMS", totalurl + "?" + paramContent);
                WriteLog.write("SMS", content);
                try {
                    String strReturn = SendPostandGet.submitPost(totalurl, paramContent, "gb2312").toString();
                    WriteLog.write("SMS", strReturn);
                    if (Integer.parseInt(strReturn.substring(4, 5)) > 0) {
                        return true;
                    }
                    else {
                        return false;

                    }
                }
                catch (Exception e) {
                }

            }

        }
        catch (Exception e) {
            logger.error("短信发送异常", e.fillInStackTrace());
        }
        return false;
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

    public Customeragent getCustomeragent() {
        return customeragent;
    }

    public void setCustomeragent(Customeragent customeragent) {
        this.customeragent = customeragent;
    }

    @Override
    public String smsBalanceInquiry() {
        // TODO Auto-generated method stub
        return null;
    }

}
