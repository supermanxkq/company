package com.ccservice.b2b2c.atom.sms;

import java.net.URLEncoder;

import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.ben.Dnsbarends;
import com.ccservice.b2b2c.util.KeyWordFilter;
import com.tenpay.util.MD5Util;

/**
 * 企信通短信接口
 * 
 * @author 贾建磊
 */
public class QiXinTongSmsSender implements SmsSender {

    private String username;

    private String password;

    private String smsSendAddress;//发送短信地址

    private String getBalanceAddress;//查询短信剩余条数地址

    /**
     * 短信发送接口   
     * 
     * @author 贾建磊
     */
    @SuppressWarnings("deprecation")
    public boolean sendSMS(String[] mobiles, String content, long ordercode, long sendagentid, Dnsbarends dns) {
        String paramContent = "pwd=" + MD5Util.MD5Encode(password, "UTF-8") + "&username=" + username;
        String mobileStr = "";
        if (mobiles != null && mobiles.length > 0) {
            mobileStr += mobiles[0];
            if (mobiles.length >= 1) {
                for (int i = 1; i < mobiles.length; i++) {
                    mobileStr += "," + mobiles[i];
                }
            }
        }
        paramContent += "&p=" + mobileStr;
        content = KeyWordFilter.doFilter(content);
        paramContent += "&msg=" + URLEncoder.encode(content);
        WriteLog.write("SMS2", "企信通发送短信接口传入接口的数据[" + smsSendAddress + "?" + paramContent + "]");
        WriteLog.write("SMS2", "发送短信的内容[" + content + "]");
        String result = SendPostandGet.submitGet(smsSendAddress + "?" + paramContent);
        WriteLog.write("SMS2", "企信通发送短信接口从接口传出的数据[" + result + "]");
        if (result != null && !result.equals("")) {
            JSONObject resultInfo = JSONObject.fromObject(result.toString());
            String status = resultInfo.getString("status");
            if (status != null && status.equals("100")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 短信剩余条数查询接口
     * 
     * @author 贾建磊
     */
    public String smsBalanceInquiry() {
        String paramContent = "pwd=" + MD5Util.MD5Encode(password, "UTF-8") + "&username=" + username;
        WriteLog.write("SMS2", "企信通短信剩余条数查询传入接口的数据[" + getBalanceAddress + "?" + paramContent + "]");
        String result = SendPostandGet.submitGet(getBalanceAddress + "?" + paramContent);
        WriteLog.write("SMS2", "企信通短信剩余条数查询从接口传出的数据[" + result + "]");
        if (result != null && !result.equals("")) {
            JSONObject resultInfo = JSONObject.fromObject(result.toString());
            String status = resultInfo.getString("status");
            if (status != null && status.equals("100")) {
                String balance = resultInfo.getString("balance");
                if (balance != null && !balance.equals("")) {
                    return balance;
                }
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

    public String getSmsSendAddress() {
        return smsSendAddress;
    }

    public void setSmsSendAddress(String smsSendAddress) {
        this.smsSendAddress = smsSendAddress;
    }

    public String getGetBalanceAddress() {
        return getBalanceAddress;
    }

    public void setGetBalanceAddress(String getBalanceAddress) {
        this.getBalanceAddress = getBalanceAddress;
    }
}
