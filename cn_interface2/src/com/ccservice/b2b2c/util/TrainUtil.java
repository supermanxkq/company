package com.ccservice.b2b2c.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.protocol.Protocol;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSPostMethod;
import com.ccservice.b2b2c.atom.component.util.MySSLProtocolSocketFactory;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.trainno.TrainNo;
import com.ccservice.elong.inter.PropertyUtil;

public class TrainUtil {
    private static String verificationUrl_other = "https://kyfw.12306.cn/otn/passcodeNew/getPassCodeNew?module=other&rand=sjrand&";

    public static void main(String[] args) {
        String a = getliecheshike("北京", "2015-08-30", "BJP", 1);
        System.out.println(a);
    }

    /**
     * 获取IP
     * @return
     */
    public static String getBrowserIp(HttpServletRequest request) {
        String ipString = "";
        if (request.getHeader("X-real-ip") == null) {
            ipString = request.getRemoteAddr();
        }
        else {
            ipString = request.getHeader("X-real-ip");
        }
        return ipString;
    }

    /**
     * 根据列车号获取列车时刻
     * 
     * @param train_no
     *            列车号
     * @param train_date
     *            时间
     * @param count
     *            获取失败后重试几次
     * @return
     * @time 2015年3月2日 下午4:03:25
     * @author Administrator
     */
    public static String getliecheshike(String train_station_name, String train_date, String train_station_code,
            int count) {
        long a = System.currentTimeMillis();
        String json = "-1";
        int tempcount = 0;
        do {
            HttpClient httpClient = new HttpClient();
            try {
                httpClient = get12306cookie(train_date, httpClient);
                String picturepath = downloadimgbyhttpclient(httpClient);
                String rand_code = "";
                boolean checkcodePass = false;
                rand_code = getcheckcodebydama(picturepath, 0);
                if (rand_code.trim().length() != 4) {
                    picturepath = downloadimgbyhttpclient(httpClient);
                    rand_code = getcheckcodebydama(picturepath, 0);
                }
                try {
                    Thread.sleep(2500L);
                }
                catch (Exception e) {
                }
                checkcodePass = checkcode(httpClient, rand_code);
                try {
                    train_station_name = URLEncoder.encode(train_station_name, "UTF-8");
                }
                catch (Exception e) {
                }
                String url = "https://kyfw.12306.cn/otn/czxx/query?train_start_date=" + train_date
                        + "&train_station_name=" + train_station_name + "&train_station_code=" + train_station_code
                        + "&randCode=" + rand_code;
                if (tempcount > 0) {
                    Thread.sleep(1000L);
                }
                tempcount++;
                CCSGetMethod get = null;
                get = new CCSGetMethod(url);
                Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
                Protocol.registerProtocol("https", myhttps);
                get.setFollowRedirects(false);
                get.addRequestHeader("Accept", "*/*");
                get.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                get.addRequestHeader("Accept-Encoding", "gzip, deflate");
                get.addRequestHeader("Accept-Language", "zh-cn");
                get.addRequestHeader("Cache-Control", "no-cache");
                get.addRequestHeader("Connection", "keep-alive");
                get.addRequestHeader("Host", "kyfw.12306.cn");
                get.addRequestHeader("If-Modified-Since", "0");
                get.addRequestHeader("Referer", "https://kyfw.12306.cn/otn/queryTrainInfo/init");
                get.addRequestHeader("User-Agent",
                        "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
                get.addRequestHeader("X-Requested-With", "XMLHttpRequest");
                String Cookie = getCookieByHttpClient(httpClient);
                get.addRequestHeader("Cookie", Cookie);
                httpClient.executeMethod(get);
                json = get.getResponseBodyAsString();
            }
            catch (HttpException e) {
                WriteLog.write("Update12306TrainDetail", "请求12306超时");
                e.printStackTrace();
            }
            catch (IOException e) {
                WriteLog.write("Update12306TrainDetail", "请求12306超时");
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                WriteLog.write("Update12306TrainDetail", "请求12306超时");
                e.printStackTrace();
            }
            catch (Exception e) {
                WriteLog.write("Update12306TrainDetail", "在12306审核图片是否正确失败");
                e.printStackTrace();
            }
        }
        while ((json.indexOf("验证码错误") >= 0 || json.indexOf("请稍后重试") >= 0 || "-1".equals(json)) && tempcount < count);
        WriteLog.write("Update12306TrainDetail", "打码耗时 : " + (System.currentTimeMillis() - a) / 1000f + " 秒 ");
        return json;
    }

    private static boolean checkcode(HttpClient httpClient, String randCode) {
        try {
            String url = "https://kyfw.12306.cn/otn/passcodeNew/checkRandCodeAnsyn";
            CCSPostMethod post = null;
            post = new CCSPostMethod(url);
            Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
            Protocol.registerProtocol("https", myhttps);
            post.setFollowRedirects(false);
            post.addRequestHeader("Accept", "*/*");
            post.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            post.addRequestHeader("Accept-Encoding", "gzip, deflate");
            post.addRequestHeader("Accept-Language", "zh-cn");
            post.addRequestHeader("Cache-Control", "no-cache");
            post.addRequestHeader("Connection", "keep-alive");
            post.addRequestHeader("Host", "kyfw.12306.cn");
            post.addRequestHeader("If-Modified-Since", "0");
            post.addRequestHeader("Content-Length", "25");
            post.addRequestHeader("Referer", "https://kyfw.12306.cn/otn/queryTrainInfo/init");
            post.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; rv:11.0) like Gecko");
            post.addRequestHeader("X-Requested-With", "XMLHttpRequest");
            String Cookie = getCookieByHttpClient(httpClient);
            post.addRequestHeader("Cookie", Cookie);
            NameValuePair NameValuePair_rand = new NameValuePair("rand", "sjrand");
            NameValuePair NameValuePair_randCode = new NameValuePair("randCode", randCode);
            NameValuePair[] nameValuePairs = { NameValuePair_rand, NameValuePair_randCode };
            post.setRequestBody(nameValuePairs);
            post.addRequestHeader("Content-Length", getContentLength(nameValuePairs));

            httpClient.executeMethod(post);
            String checkjson = post.getResponseBodyAsString();
            JSONObject jsonobject = JSONObject.parseObject(checkjson);
            String result = jsonobject.getJSONObject("data").getString("result");
            if ("1".equals(result)) {
                return true;
            }
        }
        catch (HttpException e) {
            WriteLog.write("Update12306TrainDetail", "在12306审核图片是否正确失败");
            e.printStackTrace();
        }
        catch (IOException e) {
            WriteLog.write("Update12306TrainDetail", "在12306审核图片是否正确失败");
            e.printStackTrace();
        }
        catch (Exception e) {
            WriteLog.write("Update12306TrainDetail", "在12306审核图片是否正确失败");
            e.printStackTrace();
        }
        return false;
    }

    private static HttpClient get12306cookie(String _jc_save_fromDate, HttpClient httpClient) {
        String cookie = "-1";
        try {
            GetMethod get = null;
            get = new GetMethod("https://kyfw.12306.cn/otn/czxx/init");
            Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
            Protocol.registerProtocol("https", myhttps);
            get.setFollowRedirects(false);
            httpClient.executeMethod(get);
            get.releaseConnection();
        }
        catch (HttpException e) {
            WriteLog.write("Update12306TrainDetail", "请求12306cookie失败");
        }
        catch (IOException e) {
            WriteLog.write("Update12306TrainDetail", "请求12306cookie失败");
        }
        return httpClient;
    }

    /**
     * 获取验证码图片保存并且解析获取内容
     * 
     * @return
     * @time 2015年3月2日 下午4:55:40
     * @author Administrator
     */
    public static String getcheckcode(String cookiestring) {
        String DAMAURL = PropertyUtil.getValue("jobupdate12306trainnodata_DAMAURL", "Train.properties");
        String dirPath = PropertyUtil.getValue("jobupdate12306trainnodata_dirPath", "Train.properties");// "Z:/temp/12306img/";
        String filePath = System.currentTimeMillis() + ".jpg";
        String picturepath = dirPath + filePath;
        Get_picture(verificationUrl_other, dirPath, filePath, cookiestring);
        String results = "";
        try {
            File oldFile = new File(picturepath);
            byte[] bytes = file2byte(oldFile);
            if (bytes.length > 0) {
                results = SendPostandGet.submitPostDama(DAMAURL + "hthycodeservlet", bytes);
                JSONObject jsonobject = JSONObject.parseObject(results);
                results = jsonobject.getString("coderesult");
            }
            if ("".equals(results.trim())) {
                results = "出错了";
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    /**
     * java.net实现 HTTP或HTTPs GET 指定下载路径和文件 如果验证码大小小于1KB 返回-1
     * 
     * @param strUrl
     * @param dirPath
     * @param filePath
     * @param cookiestring
     * @return
     * @time 2014年8月30日 下午3:00:22
     * @author yinshubin
     */
    public static String Get_picture(String strUrl, String dirPath, String filePath, String cookiestring) {
        URLConnection connection = null;
        String Encryption = "";
        String Encryptionstr = "";
        String before = cookiestring;
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
            URL url = new URL(strUrl);
            connection = url.openConnection();
            connection.addRequestProperty("Cookie", cookiestring);
            connection.setDoInput(true);
            connection.setDoOutput(false);
            HttpsURLConnection conn = (HttpsURLConnection) connection;
            conn.setSSLSocketFactory(sc.getSocketFactory());
            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.connect();
            InputStream in = conn.getInputStream();
            if (!savePicToDiskBool(in, dirPath, filePath)) {
                return "-1";
            }
            if ("".equals(before) || "-1".equals(before) || "1".equals(before)) {
                List<String> s = conn.getHeaderFields().get("Set-Cookie");
                for (String str : s) {
                    cookiestring += "; " + getCookie(str.toString().replace("; Path=/", ""));
                }
                cookiestring += Encryptionstr;
                return cookiestring;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
        }
        return cookiestring;
    }

    private static String downloadimgbyhttpclient(HttpClient httpClient) {
        String picturepath = "-1";
        try {
            GetMethod get = new GetMethod(verificationUrl_other);
            Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
            Protocol.registerProtocol("https", myhttps);
            get.setFollowRedirects(false);
            get.addRequestHeader("Accept", "image/png, image/svg+xml, image/*;q=0.8, */*;q=0.5");
            get.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            get.addRequestHeader("Accept-Encoding", "gzip, deflate");
            get.addRequestHeader("Accept-Language", "zh-cn");
            get.addRequestHeader("Cache-Control", "no-cache");
            String cookie = getCookieByHttpClient(httpClient);
            get.addRequestHeader("Cookie", cookie);
            get.addRequestHeader("Referer", "https://kyfw.12306.cn/otn/queryTrainInfo/init");
            get.addRequestHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0)");
            String dirPath = PropertyUtil.getValue("jobupdate12306trainnodata_dirPath", "Train.properties");// "Z:/temp/12306img/";
            String filePath = System.currentTimeMillis() + ".jpg";
            picturepath = dirPath + filePath;
            File storeFile = new File(picturepath);
            FileOutputStream output = new FileOutputStream(storeFile);
            httpClient.executeMethod(get);
            // 得到网络资源的字节数组,并写入文件
            output.write(get.getResponseBody());
            output.close();
        }
        catch (Exception e) {
            WriteLog.write("Update12306TrainDetail", "从12306获取图片保存到D:/12306img/失败");
        }
        return picturepath;
    }

    /**
     * 对文件进行编码
     * 
     * @param file
     *            需要编码的问家
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

    private static String getcheckcodebydama(String picturepath, int count) {
        String results = "";
        String DAMAURL = PropertyUtil.getValue("jobupdate12306trainnodata_DAMAURL", "Train.properties");
        try {
            File oldFile = new File(picturepath);
            byte[] bytes = file2byte(oldFile);
            if (bytes.length > 0) {
                results = SendPostandGet.submitPostDama(DAMAURL, bytes);
                JSONObject jsonobject = JSONObject.parseObject(results);
                results = jsonobject.getString("coderesult");
            }
            if ("".equals(results.trim()) && count <= 10) {
                Thread.sleep(1000L);
                count++;
                results = getcheckcodebydama(picturepath, count);
            }
            else if (count > 10) {
                results = "aaaa";
            }
        }
        catch (Exception e) {
            WriteLog.write("Update12306TrainDetail", "打码地址请求超时");
            e.printStackTrace();
        }
        return results;
    }

    private static class TrustAnyTrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[] {};
        }
    }

    /**
     * java.net实现 HTTP或HTTPs GET方法提交
     * 
     * @param strUrl
     * @return
     * @time 2014年8月30日 下午2:59:27
     * @author yinshubin
     */
    public static String Get_Cookie(String strUrl) {
        URLConnection connection = null;
        BufferedReader reader = null;
        String str = null;
        String cookiestring = "";
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());

            URL url = new URL(strUrl);
            connection = url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(false);
            HttpsURLConnection conn = (HttpsURLConnection) connection;
            conn.setSSLSocketFactory(sc.getSocketFactory());

            conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
            conn.setDoOutput(true);
            List<String> s = conn.getHeaderFields().get("Set-Cookie");
            cookiestring = getCookielogin(s.toString());
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String lines;
            StringBuffer linebuff = new StringBuffer("");
            try {
                while ((lines = reader.readLine()) != null) {
                    linebuff.append(lines);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            str = linebuff.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return str + "cookiestring" + cookiestring;
    }

    /**
     * 说明：拼接COOKIE
     * 
     * @param str
     * @return
     * @time 2014年8月30日 下午2:57:08
     * @author yinshubin
     */
    public static String getCookielogin(String str) {
        if (str.contains("BIGipServerotn")) {
            String[] strs = str.split(";");
            return strs[1].split(",")[1].trim() + "; " + strs[0].substring(1, strs[0].length());
        }
        return str;
    }

    private static String getCookieByHttpClient(HttpClient httpClient) {
        String cookie = "";
        try {
            Cookie[] cookies = httpClient.getState().getCookies();
            for (int i = 0; i < cookies.length; i++) {
                if (i > 0) {
                    cookie += " ";
                }
                cookie += cookies[i].getName() + "=" + cookies[i].getValue();
                if (i < cookies.length - 1) {
                    cookie += ";";
                }
            }
        }
        catch (Exception e) {
        }
        if (cookie.indexOf("current_captcha_type") < 0) {
            cookie += "; current_captcha_type=C";
        }
        return cookie;
    }

    /**
     * 说明：拼接COOKIE
     * 
     * @param str
     * @return
     * @time 2014年8月30日 下午2:57:08
     * @author yinshubin
     */
    public static String getCookie(String str) {
        if (str.contains("BIGipServerotn")) {
            String[] strs = str.split("; path=/,");
            strs[0] = strs[0].substring(1, strs[0].length());
            if (strs[1].contains("current_captcha_type")) {
                String[] strs2 = strs[1].split("; Path=/,");
                String[] strs3 = strs2[1].split(";");
                str = strs3[0] + "; " + strs[0] + "; " + strs2[0];
            }
            else {
                String[] strs2 = strs[1].split(";");
                strs2[0] = strs2[0].substring(1, strs2[0].length());
                str = strs2[0] + "; " + strs[0];
            }
        }
        else if (str.contains("JSESSIONID")) {
            String[] strs = str.split(";");
            str = strs[0].substring(1, strs[0].length());
        }
        return str;
    }

    /**
     * 将图片写到 硬盘指定目录下 如果文件大小小于1KB 返回false
     * 
     * @param in
     * @param dirPath
     * @param filePath
     */
    public static boolean savePicToDiskBool(InputStream in, String dirPath, String filePath) {

        int buflenth = 0;
        try {
            File dir = new File(dirPath);
            if (dir == null || !dir.exists()) {
                dir.mkdirs();
            }

            // 文件真实路径
            String realPath = dirPath.concat(filePath);
            File file = new File(realPath);
            if (file == null || !file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) != -1) {
                fos.write(buf, 0, len);
                buflenth += len;
            }
            fos.flush();
            fos.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        Integer picturesizemin = 1024;
        if (buflenth > picturesizemin) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 解析12306的【车次查询】 的数据
     * 
     * @time 2015年3月2日 下午4:20:01
     * @author chendong
     * @param train_no
     */
    public static List<TrainNo> jiexi12306_checichaxun_shuju(String jsonString, String train_no) {
        JSONObject jsonobject = JSONObject.parseObject(jsonString);
        JSONArray JSONArray_data = jsonobject.getJSONObject("data").getJSONArray("data");
        List<TrainNo> trainnos = new ArrayList<TrainNo>();
        for (int i = 0; i < JSONArray_data.size(); i++) {
            TrainNo trainno = new TrainNo();
            JSONObject json_shike = JSONArray_data.getJSONObject(i);
            String start_station_name = json_shike.getString("start_station_name");// 始发站
            String station_name = json_shike.getString("station_name");// 站名
            String arrive_time = json_shike.getString("arrive_time");// 到站时间
            String station_train_code = json_shike.getString("station_train_code");// 车次
            // if (i == 0) {
            // station_train_code_temp = station_train_code;
            // }
            String train_class_name = json_shike.getString("train_class_name");// 列车类型
            String service_type = json_shike.getString("service_type");// service_type
            String start_time = json_shike.getString("start_time");// 开车时间
            String stopover_time = json_shike.getString("stopover_time");// 停留时间
            String end_station_name = json_shike.getString("end_station_name"); // 终点站名
            String station_no = json_shike.getString("station_no"); // 车站序号
            String arrive_day_str = json_shike.getString("arrive_day_str"); // 当日到达
            String running_time = json_shike.getString("running_time"); // 运行时间
            trainno.setArrive_time(arrive_time == null ? arrive_time : arrive_time.trim());
            trainno.setEnd_station_name(end_station_name == null ? end_station_name : end_station_name.trim());
            trainno.setService_type(service_type == null ? service_type : service_type.trim());
            trainno.setStart_station_name(start_station_name == null ? start_station_name : start_station_name.trim());
            trainno.setStart_time(start_time == null ? start_time : start_time.trim());
            trainno.setStation_name(station_name == null ? station_name : station_name.trim());
            trainno.setStation_no(station_no == null ? station_no : station_no.trim());
            trainno.setStation_train_code(station_train_code);
            trainno.setStopover_time(stopover_time == null ? stopover_time : stopover_time.trim());
            trainno.setTrain_class_name(train_class_name == null ? train_class_name : train_class_name.trim());
            trainno.setArriveday(arrive_day_str == null ? arrive_day_str : arrive_day_str.trim());
            trainno.setRuntime(running_time == null ? running_time : running_time.trim());
            trainno.setTrain_no(train_no);
            trainnos.add(trainno);
        }
        trainnos = getnewstation_train_code(trainnos);
        return trainnos;
    }

    public static List<TrainNo> getnewstation_train_code(List<TrainNo> trainnos) {
        String temp_station_train_code = "";
        for (int i = 0; i < trainnos.size(); i++) {
            String station_train_code = trainnos.get(i).getStation_train_code();
            if (temp_station_train_code.indexOf(station_train_code) >= 0) {
            }
            else {
                if (temp_station_train_code.length() > 0) {
                    temp_station_train_code += "/";
                }
                temp_station_train_code += station_train_code;
            }
        }
        // System.out.println(temp_station_train_code);
        for (int i = 0; i < trainnos.size(); i++) {
            trainnos.get(i).setStation_train_code(temp_station_train_code);
        }
        return trainnos;
    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    public static String getContentLength(NameValuePair[] nameValuePairs) {
        Integer tempI = 0;
        for (int i = 0; i < nameValuePairs.length; i++) {
            NameValuePair namevaluepair = nameValuePairs[i];
            tempI += namevaluepair.getName().length();
            tempI += namevaluepair.getValue().length();
        }
        return tempI + "";
    }

    /**
     * 获取省份CODE
     * 
     * @param str
     * @return
     * @author fiend
     */
    public static String getProvince2Code(String str) {
        if (str.contains("北京")) {
            return "11";
        }
        if (str.contains("天津")) {
            return "12";
        }
        if (str.contains("河北")) {
            return "13";
        }
        if (str.contains("山西")) {
            return "14";
        }
        if (str.contains("内蒙古")) {
            return "15";
        }
        if (str.contains("辽宁")) {
            return "21";
        }
        if (str.contains("吉林")) {
            return "22";
        }
        if (str.contains("黑龙江")) {
            return "23";
        }
        if (str.contains("上海")) {
            return "31";
        }
        if (str.contains("江苏")) {
            return "32";
        }
        if (str.contains("浙江")) {
            return "33";
        }
        if (str.contains("安徽")) {
            return "34";
        }
        if (str.contains("福建")) {
            return "35";
        }
        if (str.contains("江西")) {
            return "36";
        }
        if (str.contains("山东")) {
            return "37";
        }
        if (str.contains("河南")) {
            return "41";
        }
        if (str.contains("湖北")) {
            return "42";
        }
        if (str.contains("湖南")) {
            return "43";
        }
        if (str.contains("广东")) {
            return "44";
        }
        if (str.contains("广西")) {
            return "45";
        }
        if (str.contains("海南")) {
            return "46";
        }
        if (str.contains("重庆")) {
            return "50";
        }
        if (str.contains("四川")) {
            return "51";
        }
        if (str.contains("贵州")) {
            return "52";
        }
        if (str.contains("云南")) {
            return "53";
        }
        if (str.contains("西藏")) {
            return "54";
        }
        if (str.contains("陕西")) {
            return "61";
        }
        if (str.contains("甘肃")) {
            return "62";
        }
        if (str.contains("青海")) {
            return "63";
        }
        if (str.contains("宁夏")) {
            return "64";
        }
        if (str.contains("新疆")) {
            return "65";
        }
        return "";
    }

    /**
     * 根据学校名字获取到学校的 code 保存12306常旅客类型是学生的时候用到
     * 
     * @return
     * @time 2015年4月29日 下午7:11:27
     * @author chendong
     */
    public static String getstationTelecodebychineseName_schoolname(String schoolname, String provinceCode) {
        String stationTelecode = "";
        String mapkey = "otn_userCommon_schoolNames" + provinceCode;
        String allcitys_url = "https://kyfw.12306.cn/otn/userCommon/schoolNames";
        String otn_userCommon_allCitys = Server.getInstance().getDateHashMap().get(mapkey);
        if (otn_userCommon_allCitys == null) {
            otn_userCommon_allCitys = HttpUtils.Get_https(allcitys_url + "?provinceCode=" + provinceCode);
            if (otn_userCommon_allCitys.length() > 100) {
                Server.getInstance().getDateHashMap().put(mapkey, otn_userCommon_allCitys);
            }
        }
        // System.out.println(otn_userCommon_allCitys);
        if (otn_userCommon_allCitys.length() > 100) {
            JSONObject jsonobject = JSONObject.parseObject(otn_userCommon_allCitys);
            JSONArray jsonarray_data = jsonobject.getJSONArray("data");
            for (int i = 0; i < jsonarray_data.size(); i++) {
                JSONObject jsonobject_temp = jsonarray_data.getJSONObject(i);
                if (jsonobject_temp.getString("chineseName").indexOf(schoolname) >= 0) {
                    stationTelecode = jsonobject_temp.getString("stationTelecode");
                    break;
                }
            }
        }
        return stationTelecode;
    }
}
