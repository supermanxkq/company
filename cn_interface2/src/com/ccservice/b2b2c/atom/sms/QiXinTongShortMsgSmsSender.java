package com.ccservice.b2b2c.atom.sms;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.ben.Dnsbarends;
import com.ccservice.b2b2c.util.KeyWordFilter;

/**
 * 企信通短信接口
 * 
 * @author Administrator
 *
 */
public class QiXinTongShortMsgSmsSender implements SmsSender {

    private String username;//用户名

    private String password;//密码

    private String interfaceAddress;//接口地址

    /**
     * 发送信息
     */
    @Override
    public boolean sendSMS(String[] mobiles, String content, long ordercode, long sendagentid, Dnsbarends dns) {
        try {
            String companyName = "";
            if (dns.getAgentid() > 0) {
                Customeragent customeragent = Server.getInstance().getMemberService()
                        .findCustomeragent(dns.getAgentid());
                if (customeragent != null) {
                    companyName = customeragent.getAgentcompanyname();
                }
            }
            String paramContent = "username=" + username + "&password=" + password + "&Ext=" + dns.getAgentid();
            String mobileStr = "";
            if (mobiles != null && mobiles.length > 0) {
                mobileStr += mobiles[0];
                if (mobiles.length >= 1) {
                    for (int i = 1; i < mobiles.length; i++) {
                        mobileStr += "," + mobiles[i];
                    }
                }
            }
            paramContent += "&mobile=" + mobileStr;
            content = KeyWordFilter.doFilter(content);
            content = "【" + companyName + "】" + content;
            paramContent += "&message=" + URLEncoder.encode(content, "GB2312");
            WriteLog.write("SMS2", interfaceAddress + "sendsms.asp?" + paramContent);
            WriteLog.write("SMS2", "企信通内容:" + content);
            String result = SendPostandGet.submitGet(interfaceAddress + "sendsms.asp?" + paramContent);
            WriteLog.write("SMS2", result);
            if (result != null && !result.trim().equals("")) {
                if (!result.trim().contains("-")) {
                    return true;
                }
            }
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 查询余额
     * 
     * @return
     */
    @Override
    public String smsBalanceInquiry() {
        String paramContent = "username=" + username + "&password=" + password;
        String result = SendPostandGet.submitGet(interfaceAddress + "getBalance.asp?" + paramContent);
        if (result != null && !result.trim().equals("")) {
            if (!result.trim().contains("-")) {
                return result.trim();
            }
        }
        return "FAIL";
    }

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

    public String getInterfaceAddress() {
        return interfaceAddress;
    }

    public void setInterfaceAddress(String interfaceAddress) {
        this.interfaceAddress = interfaceAddress;
    }

    public static void main(String[] args) {
        try {
            System.out.println(URLEncoder.encode("【航天商旅】测试的", "GB2312"));
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
