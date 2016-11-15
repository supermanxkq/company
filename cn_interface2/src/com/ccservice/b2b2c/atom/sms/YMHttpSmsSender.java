package com.ccservice.b2b2c.atom.sms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.sql.Timestamp;

import client.SMSServiceStub;
import client.SMSServiceStub.SendSMSSvr;
import client.SMSServiceStub.SendSMSSvrResponse;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.ymsend.Ymsend;
import com.ccservice.b2b2c.ben.Dnsbarends;

public class YMHttpSmsSender implements SmsSender {

    private String username = "";

    private String password = "";

    private String ipAddress = "";

    private String userid = "";

    //	/**
    //	 * 发送短信
    //	 * 
    //	 * @param mobiles
    //	 *            手机号码组
    //	 * @param content
    //	 *            短信内容
    //	 * 
    //	 * @return 返回发送结果
    //	 * @throws MalformedURLException
    //	 */
    //	public int sendSMS(String[] mobiles, String content, long ordercode,
    //			String strUserID) {
    //		try {
    //			for (int i = 0; i < mobiles.length; i++) {
    //				Ymsend ymsend = new Ymsend();
    //				ymsend.setContent(content);
    //				ymsend.setCreatetime(new Timestamp(System.currentTimeMillis()));
    //				ymsend.setOrdercode(ordercode);
    //				ymsend.setPhone(mobiles[i]);
    //				ymsend.setType(2);
    //				ymsend.setState(0);
    //				Server.getInstance().getMemberService().createYmsend(ymsend);
    //			}
    //			return 1;
    //		} catch (Exception e) {
    //			return -1;
    //		}
    //		
    //	}
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
        try {
            SMSServiceStub stub = new SMSServiceStub();
            SendSMSSvrResponse response = new SendSMSSvrResponse();
            //for (int i = 0; i < mobiles.length; i++) {
            SendSMSSvr sendSMSSvr = new SendSMSSvr();
            sendSMSSvr.setAuthorization("ds!@%dg23");
            sendSMSSvr.setContent(content);
            sendSMSSvr.setMobile(mobiles[0]);
            sendSMSSvr.setPriority("5");
            response = stub.sendSMSSvr(sendSMSSvr);
            System.out.println(response.getSendSMSSvrResult());
            if (response.equals("1")) {
                return true;
                //发送成功
            }

            //}
            return true;
        }
        catch (Exception e) {
        }
        return false;

    }

    //	PUBLIC STATIC VOID MAIN(STRING[] ARGS) THROWS REMOTEEXCEPTION {
    //		STRING[] STR = {"13146595083","18810285453"};
    //		SYSTEM.OUT.PRINTLN(SENDDQCSMS(STR, "你中奖了，贾建磊", 1L, ""));
    //	}

    /**
     * 发送即时短信
     * 
     * @param phone电话号码组
     * @param message短信内容
     * @param addserial附加码
     * @return
     * @throws UnsupportedEncodingException
     */
    public int sendLuckySms(String[] phone, String message, String addserial) {
        int yreturn = 1;
        if (phone != null && !phone.equals("")) {
            java.io.InputStream input = null;
            String totalurl = "";
            totalurl = ipAddress + "sendsms.action?cdkey=" + username + "&password=" + password;
            totalurl += "&phone=" + phone[0];
            try {
                totalurl += "&message=" + URLEncoder.encode(message, "utf-8");
            }
            catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            totalurl += "&addserial=" + addserial;
            System.out.println(totalurl);
            java.net.URL Url;
            try {
                Url = new java.net.URL(totalurl);
                try {
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = null;

                    // use default characterset
                    isr = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(isr);
                    StringWriter out = new StringWriter();
                    int c = -1;

                    while ((c = in.read()) != -1) {
                        out.write(c);
                    }
                    String strReturn = out.toString();
                    String strXml = "";
                    String[] strArr = strReturn.split("[\r]");
                    String strTemp = "";
                    for (int i = 0; i < strArr.length; i++) {
                        strTemp = strArr[i].replaceAll("[\n]", "");
                        if (strTemp.indexOf("error") != -1) {
                            StringBuffer tempStr = new StringBuffer("");
                            char[] tempchar = strTemp.toCharArray();
                            for (int j = 0; j < tempchar.length; j++) {
                                if ('0' <= tempchar[j] && '9' >= tempchar[j]) {
                                    //									yreturn = tempchar[j];
                                    //									System.out.print(yreturn);
                                    tempStr.append(tempchar[j]);
                                }
                            }
                            String str = tempStr.toString();
                            yreturn = Integer.parseInt(str);
                        }
                    }
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return yreturn;
    }

    /**
     * 发送定时短信
     * 
     * @param phone电话号码组
     * @param message短信内容
     * @param addserial附加码
     * @param sendtime发送时间
     * @return
     */
    public int sendTimeingSms(String[] phone, String message, String addserial, String sendtime) {
        int yreturn = 1;
        if (phone != null && !phone.equals("")) {
            java.io.InputStream input = null;
            String totalurl = "";
            totalurl = ipAddress + "/sendtimesms.action?cdkey=" + username + "&password=" + password;
            totalurl += "&phone=" + phone[0];
            try {
                totalurl += "&message=" + URLEncoder.encode(message, "utf-8");
            }
            catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            totalurl += "&addserial=" + addserial;
            totalurl += "&sendtime=" + sendtime;
            java.net.URL Url;
            try {
                Url = new java.net.URL(totalurl);
                try {
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    InputStreamReader isr = null;

                    // use default characterset
                    isr = new InputStreamReader(is);
                    BufferedReader in = new BufferedReader(isr);
                    StringWriter out = new StringWriter();
                    int c = -1;

                    while ((c = in.read()) != -1) {
                        out.write(c);
                    }
                    String strReturn = out.toString();
                    String strXml = "";
                    String[] strArr = strReturn.split("[\r]");
                    String strTemp = "";
                    for (int i = 0; i < strArr.length; i++) {
                        strTemp = strArr[i].replaceAll("[\n]", "");
                        if (strTemp.indexOf("error") != -1) {
                            StringBuffer tempStr = new StringBuffer("");
                            char[] tempchar = strTemp.toCharArray();
                            for (int j = 0; j < tempchar.length; j++) {
                                if ('0' <= tempchar[j] && '9' >= tempchar[j]) {
                                    //									yreturn = tempchar[j];
                                    //									System.out.print(yreturn);
                                    tempStr.append(tempchar[j]);
                                }
                            }
                            String str = tempStr.toString();
                            yreturn = Integer.parseInt(str);
                        }
                        try {
                            System.out.println(strXml);
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
            catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return yreturn;
    }

    /**
     *序列号注册 
     * @return
     */
    public int Ymsregister() {
        int yreturn = 1;
        java.io.InputStream input = null;
        String totalurl = "";
        totalurl = ipAddress + "/regist.action?cdkey=" + username + "&password=" + password;
        java.net.URL Url;
        try {
            Url = new java.net.URL(totalurl);
            try {
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                InputStreamReader isr = null;

                // use default characterset
                isr = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(isr);
                StringWriter out = new StringWriter();
                int c = -1;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                String strReturn = out.toString();
                String strXml = "";
                String[] strArr = strReturn.split("[\r]");
                String strTemp = "";
                for (int i = 0; i < strArr.length; i++) {
                    strTemp = strArr[i].replaceAll("[\n]", "");
                    if (strTemp.indexOf("error") != -1) {
                        StringBuffer tempStr = new StringBuffer("");
                        char[] tempchar = strTemp.toCharArray();
                        for (int j = 0; j < tempchar.length; j++) {
                            if ('0' <= tempchar[j] && '9' >= tempchar[j]) {
                                //								yreturn = tempchar[j];
                                //								System.out.print(yreturn);
                                tempStr.append(tempchar[j]);
                            }
                        }
                        String str = tempStr.toString();
                        yreturn = Integer.parseInt(str);
                    }

                    try {
                        System.out.println(strXml);
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return yreturn;
    }

    /**
     *接收上行短信 
     * @return
     */
    public int getmo() {
        int yreturn = 1;
        java.io.InputStream input = null;
        String totalurl = "";
        totalurl = ipAddress + "/getmo.action?cdkey=" + username + "&password=" + password;
        java.net.URL Url;
        try {
            Url = new java.net.URL(totalurl);
            try {
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                InputStreamReader isr = null;

                // use default characterset
                isr = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(isr);
                StringWriter out = new StringWriter();
                int c = -1;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                String strReturn = out.toString();
                String strXml = "";
                String[] strArr = strReturn.split("[\r]");
                String strTemp = "";
                for (int i = 0; i < strArr.length; i++) {
                    strTemp = strArr[i].replaceAll("[\n]", "");
                    if (strTemp.indexOf("error") != -1) {
                        StringBuffer tempStr = new StringBuffer("");
                        char[] tempchar = strTemp.toCharArray();
                        for (int j = 0; j < tempchar.length; j++) {
                            if ('0' <= tempchar[j] && '9' >= tempchar[j]) {
                                //								yreturn = tempchar[j];
                                //								System.out.print(yreturn);
                                tempStr.append(tempchar[j]);
                            }
                        }
                        String str = tempStr.toString();
                        yreturn = Integer.parseInt(str);
                    }

                    try {
                        System.out.println(strXml);
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return yreturn;
    }

    /**
     *查询余额
     * @return
     */
    public int querybalance() {
        int yreturn = 1;
        java.io.InputStream input = null;
        String totalurl = "";
        totalurl = ipAddress + "/querybalance.action?cdkey=" + username + "&password=" + password;
        java.net.URL Url;
        try {
            Url = new java.net.URL(totalurl);
            try {
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                InputStreamReader isr = null;

                // use default characterset
                isr = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(isr);
                StringWriter out = new StringWriter();
                int c = -1;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                String strReturn = out.toString();
                String strXml = "";
                String[] strArr = strReturn.split("[\r]");
                String strTemp = "";
                for (int i = 0; i < strArr.length; i++) {
                    strTemp = strArr[i].replaceAll("[\n]", "");
                    if (strTemp.indexOf("error") != -1) {
                        StringBuffer tempStr = new StringBuffer("");
                        char[] tempchar = strTemp.toCharArray();
                        for (int j = 0; j < tempchar.length; j++) {
                            if ('0' <= tempchar[j] && '9' >= tempchar[j]) {
                                //								yreturn = tempchar[j];
                                //								System.out.print(yreturn);
                                tempStr.append(tempchar[j]);
                            }
                        }
                        String str = tempStr.toString();
                        yreturn = Integer.parseInt(str);
                    }

                    try {
                        System.out.println(strXml);
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return yreturn;
    }

    /**
     *充值
     * @return
     */
    public int chargeup(String cardno, String cardpass) {
        int yreturn = 1;
        java.io.InputStream input = null;
        String totalurl = "";
        totalurl = ipAddress + "/chargeup.action?cdkey=" + username + "&password=" + password;
        totalurl += "&cardno=" + cardno;
        totalurl += "&cardpass=" + cardpass;

        java.net.URL Url;
        try {
            Url = new java.net.URL(totalurl);
            try {
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url.openConnection();
                conn.setDoInput(true);
                conn.connect();
                InputStream is = conn.getInputStream();
                InputStreamReader isr = null;

                // use default characterset
                isr = new InputStreamReader(is);
                BufferedReader in = new BufferedReader(isr);
                StringWriter out = new StringWriter();
                int c = -1;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
                String strReturn = out.toString();
                String strXml = "";
                String[] strArr = strReturn.split("[\r]");
                String strTemp = "";
                for (int i = 0; i < strArr.length; i++) {
                    strTemp = strArr[i].replaceAll("[\n]", "");
                    if (strTemp.indexOf("error") != -1) {
                        StringBuffer tempStr = new StringBuffer("");
                        char[] tempchar = strTemp.toCharArray();
                        for (int j = 0; j < tempchar.length; j++) {
                            if ('0' <= tempchar[j] && '9' >= tempchar[j]) {
                                //								yreturn = tempchar[j];
                                //								System.out.print(yreturn);
                                tempStr.append(tempchar[j]);
                            }
                        }
                        String str = tempStr.toString();
                        yreturn = Integer.parseInt(str);
                    }

                    try {
                        System.out.println(strXml);
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return yreturn;
    }

    //	public static void main(String[] args) {
    ////		YMHttpSmsSender yms = new YMHttpSmsSender();
    ////		String[] phone = { "15110280546" };
    ////		int nu = yms.sendLuckySms(phone, "测试", "");
    ////		System.out.print(nu);
    //		String stime="2011-11-29 10:38:46";
    //		stime=stime.trim();
    //		stime=stime.replace("-", "");
    //		stime=stime.replace(" ", "");
    //		stime=stime.replace(":", "");
    //		System.out.println(stime);
    //	}

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
