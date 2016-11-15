package com.ccservice.b2b2c.atom.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.ccservice.b2b2c.ben.Dnsbarends;

public class XJSmsSender implements SmsSender {

    private String username;

    private String password;

    private String ipAddress;

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
     * 发送短信
     * 
     * @param mobiles
     *            手机号码组
     * @param content
     *            短信内容
     * 
     * @return 返回发送结果
     * @throws MalformedURLException 
     */

    public boolean sendSMS(String[] mobiles, String content, long ordercode, long sendagentid, Dnsbarends dns) {

        String mobilestr = "";
        for (int i = 0; i < mobiles.length; i++) {
            if (i == 0) {
                mobilestr += mobiles[i];
            }
            else {
                mobilestr += "," + mobiles[i];
            }
        }
        String urltemp = ipAddress + "?loginname=" + username + "&password=" + password + "&tele=" + mobilestr
                + "&msg=" + content;
        URL url;
        try {
            url = new URL(urltemp);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "8859_1");
            out.flush();
            out.close();
            String sCurrentLine;
            String sTotalString;
            sCurrentLine = "";
            sTotalString = "";
            InputStream l_urlStream;
            l_urlStream = connection.getInputStream();
            BufferedReader l_reader = new BufferedReader(new InputStreamReader(l_urlStream));
            while ((sCurrentLine = l_reader.readLine()) != null) {
                sTotalString += sCurrentLine + "\r\n";
            }
            if (sTotalString.indexOf("success") > 0) {
                return true;
            }
            System.out.println(urltemp);
        }
        catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public String smsBalanceInquiry() {
        // TODO Auto-generated method stub
        return null;
    }
}
