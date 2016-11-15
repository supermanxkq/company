package com.travelGuide;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.lang.ArrayUtils;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.atom.component.util.MySSLProtocolSocketFactory;
import com.ccservice.elong.inter.PropertyUtil;
import com.weixin.util.RequestUtil;

/**
 * 
 * 12306出行向导请求接口
 * @time 2015年5月8日 上午9:05:48
 */
public class TravelGuideRequest extends HttpServlet {

    private static String verificationUrl_other = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=other&rand=sjrand&";

    private static String DAMAURL_DLL = "";//用dll打码的地址

    final private static String DEFAULT__PICTURE_DIRPATH = "D:/12306img/";

    final private static String SystemDAMAURL_DLL = "http://hthyservice.dll.hangtian123.net:8286/";//用Systemdama

    public String getzwdinfo(JSONObject json, int r1) {
        JSONObject obj = new JSONObject();
        String resultOut = "";
        String backdate = "";
        try {
            //写入日志
            WriteLog.write("t12306travelGuide端接口请求", r1 + ":JsonStr:" + json.toJSONString());
            //进行解析JsonStr字符串
            if (!json.containsKey("cxlx") || !json.containsKey("cz") || !json.containsKey("cc")) {
                obj.put("success", false);
                obj.put("code", "102");
                obj.put("msg", "请求数据格式有误");
                resultOut = obj.toString();
            }
            else {
                String cxlx = json.getString("cxlx");
                String cz = json.getString("cz");
                String cc = json.getString("cc");
                if ("".equals(cxlx)) {
                    obj.put("success", false);
                    obj.put("code", "202");
                    obj.put("msg", "查询类型为空");
                    resultOut = obj.toString();
                }
                else if ("".equals(cz)) {
                    obj.put("success", false);
                    obj.put("code", "202");
                    obj.put("msg", "车站为空");
                    resultOut = obj.toString();
                }
                else if ("".equals(cc)) {
                    obj.put("success", false);
                    obj.put("code", "202");
                    obj.put("msg", "车次为空");
                    resultOut = obj.toString();
                }
                else {
                    if ("0".equals(cxlx)) {
                        backdate = getUserfulDate(cxlx, cz, cc);
                        obj.put("success", true);
                        obj.put("code", "200");
                        obj.put("zwdinfo", backdate);
                        obj.put("msg", "查询成功");
                        resultOut = obj.toString();
                    }
                    else if ("1".equals(cxlx)) {
                        backdate = getUserfulDate(cxlx, cz, cc);
                        if (backdate.contains("没有")) {
                            obj.put("success", false);
                            obj.put("code", "202");
                            obj.put("msg", backdate);
                            obj.put("zwdinfo", "");
                            resultOut = obj.toString();
                        }
                        else {
                            obj.put("success", true);
                            obj.put("code", "200");
                            obj.put("zwdinfo", backdate);
                            obj.put("msg", "查询成功");
                        }
                        resultOut = obj.toString();
                    }
                    else {
                        backdate = "输入的数据值有误";
                        obj.put("success", false);
                        obj.put("code", "202");
                        obj.put("msg", backdate);
                        resultOut = obj.toString();
                    }
                }
            }
        }

        catch (JSONException e) {
            obj.put("success", false);
            obj.put("code", "202");
            obj.put("msg", "请求数据格式有误");
            resultOut = obj.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            obj.put("success", false);
            obj.put("code", "202");
            obj.put("msg", "接口调用异常，操作失败");
            resultOut = obj.toString();
        }
        WriteLog.write("t12306travelGuide端接口请求", r1 + ":jsonStr:" + resultOut);
        return resultOut;
    }

    public String getUserfulDate(String cxlx, String cz, String cc) {
        String date = "没有数据";
        String usefulDate = getdata(cxlx, cz, cc, 0);
        JSONObject json = JSONObject.parseObject(usefulDate);
        String data1 = json.getString("data");
        JSONObject json1 = JSONObject.parseObject(data1);
        if (json1 != null && json1.containsKey("data")) {
            date = json1.getString("data");
            return date;
        }
        return date;

    }

    private static String getdata(String cxlx, String cz, String cc, int damacount) {
        String czEn = URLEncoder.encode(cz).replace("%", "-");
        String cookiestring = get12306cookie();
        int n = 1;
        boolean bo = false;
        String rand_code = "";
        do {
            String picturepath = downloadimgbyhttpclient(cookiestring);
            rand_code = getcheckcodebydama(picturepath, 10);
            //打码时间需要停顿一下
            try {
                Thread.sleep(1500);
            }
            catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            System.out.println("验证码：" + rand_code);
            bo = codeIsRight(rand_code, cookiestring);
            n++;
        }
        while (bo == false && n < 10);

        String url = "https://kyfw.12306.cn/otn/zwdch/query";
        String json = "-1";
        try {
            CCSPostMethod post = null;
            CCSHttpClientnew httpClient = new CCSHttpClientnew(false, 10000L);
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
            post = new CCSPostMethod(url.toString());
            NameValuePair cxlxNameValuePair = new NameValuePair("cxlx", cxlx);
            NameValuePair czNameValuePair = new NameValuePair("cz", cz);
            NameValuePair ccNameValuePair = new NameValuePair("cc", cc);
            NameValuePair czEnNameValuePair = new NameValuePair("czEn", czEn);
            NameValuePair randCode = new NameValuePair("randCode", rand_code);
            NameValuePair[] names = { cxlxNameValuePair, czNameValuePair, ccNameValuePair, czEnNameValuePair, randCode };

            post.setRequestBody(names);
            post.setFollowRedirects(false);
            post.addRequestHeader("Cookie", cookiestring);
            Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
            Protocol.registerProtocol("https", myhttps);
            httpClient.executeMethod(post);
            json = post.getResponseBodyAsString();
        }
        catch (HttpException e) {
        }
        catch (IOException e) {
        }
        if (damacount < 10 && (json.indexOf("验证码错误") >= 0 || json.indexOf("请稍后重试") >= 0 || "-1".equals(json))) {
            try {
                json = getdata(cxlx, cz, cc, damacount + 1);
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
        return json;
    }

    /** 
     * 对文件进行编码 
     * @param file 需要编码的问家 
     * @return 对文件进行base64编码后的字符串 
     * @throws Exception 
     */
    public static byte[] file2byte(File file) throws Exception {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    private static String getcheckcodebydama(String picturepath, int totalcount) {
        String results = "";
        try {
            File oldFile = new File(picturepath);
            byte[] bytes = file2byte(oldFile);
            if (bytes.length > 0) {
                int send_count = 0;
                do {
                    send_count++;
                    //动态寻找打码地址
                    String DAMAURL_DLL_NEW = PropertyUtil.getValue("dll_Path", "Train.properties");
                    if (DAMAURL_DLL_NEW != null && !"".equals(DAMAURL_DLL_NEW)) {
                        DAMAURL_DLL = DAMAURL_DLL_NEW;
                    }
                    else {
                        DAMAURL_DLL = SystemDAMAURL_DLL;
                    }
                    String url_ = DAMAURL_DLL + "hthycodeservletsys";
                    results = SendPostandGet.submitPostDama(url_, bytes);
                }
                while ((results == null || "".equals(results)) && send_count < totalcount);
                JSONObject jsonobject = JSONObject.parseObject(results);
                results = jsonobject.getString("coderesult");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return results;
    }

    private static String downloadimgbyhttpclient(String cookiestring) {
        String picturepath = "-1";
        try {
            CCSHttpClientnew httpClient = new CCSHttpClientnew(false, 10000L);
            CCSGetMethod get = new CCSGetMethod(verificationUrl_other);
            get.addRequestHeader("Cookie", cookiestring);
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
            Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
            Protocol.registerProtocol("https", myhttps);
            get.setFollowRedirects(false);
            String filePath = System.currentTimeMillis() + ".jpg";
            String pictureDirPath = DEFAULT__PICTURE_DIRPATH;
            try {
                String pictureDirPathPro = PropertyUtil.getValue("pictureDirPath", "Train.properties");
                if (pictureDirPathPro != null && !"".equals(pictureDirPathPro)) {
                    pictureDirPath = pictureDirPathPro;
                    File demo = new File(pictureDirPathPro);
                    if (!demo.exists()) {
                        demo.mkdir();
                    }

                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            picturepath = pictureDirPath + filePath;
            File storeFile = new File(picturepath);
            FileOutputStream output = new FileOutputStream(storeFile);
            httpClient.executeMethod(get);
            //得到网络资源的字节数组,并写入文件  
            output.write(get.getResponseBody());
            output.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return picturepath;
    }

    private static String get12306cookie() {
        String cookie = "-1";
        try {
            CCSGetMethod get = null;
            CCSHttpClientnew httpClient = new CCSHttpClientnew(false, 10000L);
            httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
            httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
            get = new CCSGetMethod("https://kyfw.12306.cn/otn/queryTrainInfo/init");
            Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
            Protocol.registerProtocol("https", myhttps);
            get.setFollowRedirects(false);
            httpClient.executeMethod(get);
            Cookie[] cookies = httpClient.getState().getCookies();
            cookie = ArrayUtils.toString(cookies).replace("{", "").replace("}", "").replace(",", ";")
                    + ";current_captcha_type=C";
        }
        catch (HttpException e) {
        }
        catch (IOException e) {
        }
        return cookie;
    }

    /**
     * 根据验证码进行判断12306的验证码是否正确
     */
    public static boolean codeIsRight(String rand_code, String cookies) {
        String param = "randCode=" + rand_code + "&rand=sjrand";
        boolean result = false;
        Map<String, String> header = new HashMap<String, String>();
        header.put("Cookie", cookies);
        header.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        header.put("Content-Length", String.valueOf(param.length()));
        header.put("Referer", "https://kyfw.12306.cn/otn/zwdch/init");
        header.put("Host", "kyfw.12306.cn");
        header.put("Origin", "https://kyfw.12306.cn");
        header.put("User-Agent",
                "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.118 Safari/537.36");
        header.put("X-Requested-With", "keep-alive");
        header.put("Accept-Language", "zh-CN,zh;q=0.8");
        header.put("Connection", "XMLHttpRequest");
        header.put("Accept-Encoding", "gzip, deflate");
        header.put("Accept", "*/*");
        String resultStr = RequestUtil.post("https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn?randCode="
                + rand_code + "&rand=sjrand", "", "", header, 0);
        System.out.println("验证码的结果：" + resultStr);
        if (resultStr.contains("1")) {
            result = true;
        }
        return result;

    }

}
