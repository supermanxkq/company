package com.ccservice.b2b2c.atom.component.ticket;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.ArrayUtils;

import com.ccservice.b2b2c.atom.component.ticket.api.DaMaCommon;
import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 国航的工具类
 * 
 * @time 2015年6月11日 下午5:12:50
 * @author chendong
 */
public class Wrapper_CAUtil {
    public static void main1(String[] args) throws IOException {
        HttpClient client = new HttpClient();
        GetMethod get = new GetMethod(
                "http://et.airchina.com.cn/www/servlet/com.ace.um.common.verify.VerifyCodeServlet?t=14295099?timestamp=4604521748?timestamp=14346057");
        client.executeMethod(get);
        File storeFile = new File("D:/2008sohu.jpg");
        FileOutputStream output = new FileOutputStream(storeFile);
        //得到网络资源的字节数组,并写入文件  
        output.write(get.getResponseBody());
        output.close();
    }

    public static void main(String[] args) {
        //        testAllliucheng();
        String mboxCookie = "check#true#"
                + ((Double) Math.ceil((System.currentTimeMillis() / 1000 + 60))).toString().replace(".", "")
                        .replace("E", "")
                + "|session#" + System.currentTimeMillis() + "-" + (int) (new Random().nextFloat() * 999999) + "#"
                + ((Double) Math.ceil((System.currentTimeMillis() / 1000 + 60))).toString().replace(".", "")
                        .replace("E", "");
        System.out.println(mboxCookie);

    }

    private static void testAllliucheng() {
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        String cookiestring = getcookie(httpClient);
        System.out.println("====cookiestring==============");
        System.out.println(cookiestring);
        String url_tupian_path = "http://et.airchina.com.cn/www/servlet/com.ace.um.common.verify.VerifyCodeServlet?"
                + "t=" + System.currentTimeMillis() + "?timestamp=" + System.currentTimeMillis() + "?timestamp="
                + System.currentTimeMillis();
        String dirPath = "D:/CA_img/";
        String picturepath = downloadimgbyhttpclient(url_tupian_path, cookiestring, dirPath);
        System.out.println("====picturepath==============");
        System.out.println(picturepath);
        DaMaCommon dmc = WrapperUtil.getcheckcodebydama(picturepath, 0);//验证码打码
        String rand_code = dmc.getResult().toUpperCase();
        System.out.println("====rand_code==============");
        System.out.println(rand_code);
    }

    /**
     * 获取登录之前初始化的cookie
     * 
     * @return
     * @time 2015年6月18日 下午12:57:30
     * @author chendong
     * @param httpClient2 
     */
    public static String getcookie(HttpClient httpClient) {
        StringBuffer cookieString = new StringBuffer("");
        //        CCSGetMethod httpget = null;
        GetMethod httpget = null;
        //        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        String initUrl = "";
        initUrl = "http://www.airchina.com.cn/";
        httpget = new CCSGetMethod(initUrl);
        httpget.setFollowRedirects(false);
        try {
            int status = 302;
            int executeCount = 0;
            do {
                executeCount++;
                status = httpClient.executeMethod(httpget);
                System.out.println("======================status=============");
                System.out.println(status);
            }
            while (status == 302 && executeCount < 5);
            String responseBodyString = httpget.getResponseBodyAsString();
            //            cookieString.append(getCookieString(httpClient));
            System.out.println("======================responseBodyString=============");
            //            System.out.println(responseBodyString);
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        initUrl = "http://www.airchina.com.cn/www/jsp/userManager/login.jsp";
        //        httpget = new CCSGetMethod(initUrl);
        httpget = new GetMethod(initUrl);
        httpget.setFollowRedirects(false);
        try {
            int status = 302;
            int executeCount = 0;
            do {
                executeCount++;
                status = httpClient.executeMethod(httpget);
                System.out.println("======================status=============");
                System.out.println(status);
            }
            while (status == 302 && executeCount < 5);
            String responseBodyString = httpget.getResponseBodyAsString();
            cookieString.append(getCookieString(httpClient));
            System.out.println("======================responseBodyString=============");
            //            System.out.println(responseBodyString);
        }
        catch (HttpException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return cookieString.toString();
    }

    public static String getCookieString(HttpClient httpClient) {
        StringBuffer cookieString = new StringBuffer("");
        Cookie[] cookies = httpClient.getState().getCookies();
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie1 = cookies[i];
            if (i > 0) {
                cookieString.append(" ");
            }
            cookieString.append(cookie1.getName() + "=" + cookie1.getValue());
            if (i < cookies.length - 1) {
                cookieString.append(";");

            }
        }
        cookieString.append("; s_sess= s_cc=true; s_sq=;");
        return cookieString.toString();
    }

    /**
     * 
     * 
     * @param imgUrl 图片的url地址
     * @param fileURL 存放地址  
     * 
     * @time 2015年6月18日 下午2:27:35
     * @author chendong
     * @param dirPath 
     */
    public static String makeImg(String imgUrl, String cookiestring, String fileURL) {
        try {
            // 创建流  
            BufferedInputStream in = new BufferedInputStream(new URL(imgUrl).openStream());
            // 生成图片名  
            int index = imgUrl.lastIndexOf("/");
            String sName = imgUrl.substring(index + 1, imgUrl.length());
            System.out.println(sName);
            // 存放地址  
            File img = new File(fileURL + sName);
            // 生成图片  
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(img));
            byte[] buf = new byte[2048];
            int length = in.read(buf);
            while (length != -1) {
                out.write(buf, 0, length);
                length = in.read(buf);
            }
            in.close();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return fileURL;
    }

    /**
     * 
     * 
     * @param url_path 下载图片的url地址
     * @param cookiestring cookie字符串
     * @param dirPath 存放图片的路径  "X:/12306img/";
     * @return
     * @time 2015年6月18日 下午1:10:57
     * @author chendong
     */
    public static String downloadimgbyhttpclient(String url_path, String cookiestring, String dirPath) {
        String picturepath = "-1";
        try {
            HttpClient client = new HttpClient();
            GetMethod get = new GetMethod(url_path);
            get.addRequestHeader("Cookie", cookiestring);
            int status = client.executeMethod(get);
            String filePath = System.currentTimeMillis() + new Random().nextInt(1000) + ".jpg";
            picturepath = dirPath + filePath;
            if (!new File(dirPath).exists()) {
                new File(dirPath).mkdirs();
            }
            File storeFile = new File(picturepath);
            FileOutputStream output = new FileOutputStream(storeFile);
            //得到网络资源的字节数组,并写入文件  
            output.write(get.getResponseBody());
            output.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return picturepath;
    }

    /**
     * 根据 namevaluePairs 获取 ContentLength
     * 
     * @param nameValuePairs
     * @return
     * @time 2015年6月24日 上午11:20:16
     * @author chendong
     */
    public static String getContentLength(NameValuePair[] nameValuePairs) {
        Integer tempI = 0;
        for (int i = 0; i < nameValuePairs.length; i++) {
            NameValuePair namevaluepair = nameValuePairs[i];
            tempI += namevaluepair.getName().length();
            tempI += namevaluepair.getValue().length();
            tempI += 1;

        }
        return tempI + "";
    }

    public static String get302Location(Header[] responseHears) {
        String Location = "";
        System.out.println("---responseHears-----------------------");
        for (int i = 0; i < responseHears.length; i++) {
            Header header = responseHears[i];
            if (header.getName().equals("Location")) {
                Location = header.getValue();
                break;
            }
            System.out.println(header);
        }
        return Location;
    }
}
