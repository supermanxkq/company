/**
 * 
 */
package com.callback;

import java.net.URLEncoder;

/**
 * 发送短信的工具类
 * @time 2015年11月3日 下午3:20:06
 * @author chendong
 */
public class SendSms {
    public static void main(String[] args) {
        SendSms sendSms = SendSms.getInstance();
        String content = "just test";
        sendSms.sendSMS(content, "15313151703");
    }

    /**
     * 获取对象
     * @time 2015年11月3日 下午3:23:24
     * @author chendong
     */
    public static SendSms getInstance() {
        String username = PropertyUtil.getValue("zhanghao", "Train.properties");
        String password = PropertyUtil.getValue("mima", "Train.properties");
        SendSms sendSms = new SendSms(username, password);
        return sendSms;
    }

    public SendSms(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    private String username;

    private String password;

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
     * 
     * 
     * @param mobiles 手机号数组
     * @param content 发送的内容
     * @return
     * @time 2015年11月3日 下午3:21:51
     * @author chendong
     */
    public boolean sendSMS(String content, String mobilesString) {
        //String mobilesString = "18363852969";
        String[] mobiles = mobilesString.split("[|]");
        String strMobiles = "";
        try {
            strMobiles = mobiles[0];
            for (int i = 1; i < mobiles.length; i++) {
                strMobiles += "," + mobiles[i];
            }
            if (strMobiles.length() > 0) {
                String totalurl = "http://203.81.21.34/send/gsend.asp";
                String paramContent = "name=" + this.username + "&pwd=" + this.password + "&dst=" + strMobiles
                        + "&msg=" + URLEncoder.encode(content, "gb2312") + "&txt=ccdx";
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
        }
        return false;
    }
}
