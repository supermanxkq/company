package com.ccservice.b2b2c.atom.component.ticket.api;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.zip.GZIPInputStream;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * 若快打码
 * @author WH
 */

public class RuoKuaiDaMaApi {

    private static final String username = "hyccservicecom";//用户名

    private static final String password = "HANGtian126";//密码

    //    private static String typeid = "3040";//验证码类型，3040：4位英文+数字；5000：任意长度中英数三混；参考网址http://www.ruokuai.com/home/pricetype
    private static String typeid = "6113";//验证码类型，12306验证码

    private static String timeout = "90";//超时，1分钟，typeid为5000时，设置为90

    private static final String softid = "32501";//打码软件ID

    private static final String softkey = "99b5b0f67d5a4abdbcbce11b021206cd";//打码软件密码

    private static final int TIMEOUT = 90000;//打码请求超时时间 毫秒

    //本地打码，imgPath格式如： D:\\image\\vcode.jpg
    public static DaMaResult localPrint(String imgPath, String typeid_) {
        //打码结果XML
        String xml = createByPost(username, password, typeid_, timeout, softid, softkey, imgPath);
        //解析XML
        DaMaResult result = displayXmlResult(xml);
        //返回
        return result;
    }

    //本地打码，imgPath格式如： D:\\image\\vcode.jpg
    public static DaMaResult localPrint(String imgPath) {
        //打码结果XML
        String xml = createByPost(username, password, typeid, timeout, softid, softkey, imgPath);
        //解析XML
        DaMaResult result = displayXmlResult(xml);
        //返回
        return result;
    }

    //远程打码，imgUrl格式如：http://www.yeebooking.com/images/vcode.jpg
    public static DaMaResult remotePrint(String imgUrl) {
        //打码结果XML
        String xml = createByUrl(username, password, typeid, timeout, softid, softkey, imgUrl);
        //解析XML
        DaMaResult result = displayXmlResult(xml);
        //返回
        return result;
    }

    //提交错误，print方法DaMaResult的id
    public static void error(String id) {
        report(username, password, softid, softkey, id);
    }

    /**
     * 解析xml结果
     */
    private static DaMaResult displayXmlResult(String xml) {
        DaMaResult result = new DaMaResult();
        if (xml == null || "".equals(xml.trim())) {
            result.setId("-1");
            result.setResult("校验失败");
            return result;
        }
        //<?xml version="1.0"?><Root><Result>dnes</Result><Id>a212b279-1dd1-4ea8-905b-23b744ad2a0c</Id></Root>
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document dm = db.parse(new ByteArrayInputStream(xml.getBytes("utf-8")));
            //成功
            NodeList IdNode = dm.getElementsByTagName("Id");
            NodeList ResultNode = dm.getElementsByTagName("Result");
            //错误
            NodeList ErrorNode = dm.getElementsByTagName("Error");
            if (ErrorNode.getLength() > 0) {
                throw new Exception(ErrorNode.item(0).getFirstChild().getNodeValue());
            }
            else if (ResultNode.getLength() > 0) {
                String Result = ResultNode.item(0).getFirstChild().getNodeValue();
                String Id = IdNode.item(0).getFirstChild().getNodeValue();
                result.setId(Id);
                result.setResult(Result);
            }
        }
        catch (Exception e) {
            result.setId("-1");
            result.setResult("出错了：" + e.getMessage());
        }
        return result;
    }

    private static String MD5(String s) {
        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(s.getBytes());
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        }
        catch (Exception e) {
            return "";
        }
    }

    private static String httpRequestData(String url, String param) throws IOException {
        StringBuffer res = new StringBuffer();
        InputStream in = null;
        HttpURLConnection con = null;
        BufferedReader reader = null;
        DataOutputStream out = null;
        try {
            con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            out = new DataOutputStream(con.getOutputStream());
            out.write(param.getBytes("UTF-8"));
            in = con.getInputStream();
            //判断是否压缩
            String ContentEncoding = con.getHeaderField("Content-Encoding");
            if ("gzip".equalsIgnoreCase(ContentEncoding)) {
                reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(in), "UTF-8"));
            }
            else {
                reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            }
            String lineTxt = null;
            while ((lineTxt = reader.readLine()) != null) {
                res.append(lineTxt);
            }
        }
        catch (Exception e) {
            res = new StringBuffer();
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (Exception e) {
            }
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e) {
            }
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            }
            catch (Exception e) {
            }
        }
        return res.toString();
    }

    private static String httpPostImage(String url, String param, byte[] data) throws IOException {
        long time = System.currentTimeMillis();
        String boundary = "----------" + MD5(String.valueOf(time));
        String boundarybytesString = "\r\n--" + boundary + "\r\n";
        StringBuffer res = new StringBuffer();
        InputStream in = null;
        OutputStream out = null;
        HttpURLConnection con = null;
        BufferedReader reader = null;
        try {
            con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setRequestMethod("POST");
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setUseCaches(false);
            con.setConnectTimeout(TIMEOUT);
            con.setReadTimeout(TIMEOUT);
            con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            out = con.getOutputStream();
            for (String paramValue : param.split("[&]")) {
                out.write(boundarybytesString.getBytes("UTF-8"));
                String paramString = "Content-Disposition: form-data; name=\"" + paramValue.split("[=]")[0]
                        + "\"\r\n\r\n" + paramValue.split("[=]")[1];
                out.write(paramString.getBytes("UTF-8"));
            }
            out.write(boundarybytesString.getBytes("UTF-8"));
            String paramString = "Content-Disposition: form-data; name=\"image\"; filename=\"" + "sample.gif"
                    + "\"\r\nContent-Type: image/gif\r\n\r\n";
            out.write(paramString.getBytes("UTF-8"));
            out.write(data);
            String tailer = "\r\n--" + boundary + "--\r\n";
            out.write(tailer.getBytes("UTF-8"));
            in = con.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = reader.readLine()) != null) {
                res.append(lineTxt);
            }
        }
        catch (Exception e) {
            res = new StringBuffer();
        }
        finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            }
            catch (Exception e) {
            }
            try {
                if (in != null) {
                    in.close();
                }
            }
            catch (Exception e) {
            }
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            }
            catch (Exception e) {
            }
        }
        return res.toString();
    }

    /**
     * 答题(URL) 
     * @param username  用户名
     * @param password  用户密码(支持32位MD5)
     * @param typeid    题目类型
     * @param timeout   任务超时时间，默认与最小值为60秒
     * @param softid    软件ID，开发者可自行申请
     * @param softkey   软件KEY，开发者可自行申请
     * @param imageurl  远程图片URL
     * @return          平台返回结果XML样式
     */
    private static String createByUrl(String username, String password, String typeid, String timeout, String softid,
            String softkey, String imageurl) {
        String param = String.format("username=%s&password=%s&typeid=%s&timeout=%s&softid=%s&softkey=%s", username,
                password, typeid, timeout, softid, softkey);
        String result;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            ImageIO.write(ImageIO.read(new URL(imageurl)), "jpg", baos);
            result = httpPostImage("http://api.ruokuai.com/create.xml", param, baos.toByteArray());
        }
        catch (Exception e) {
            result = "出错了：" + e.getMessage();
        }
        finally {
            if (baos != null) {
                try {
                    baos.flush();
                    baos.close();
                }
                catch (Exception e) {
                }
            }
        }
        return result;
    }

    /**
     * 上传题目图片返回结果   
     * @param username      用户名
     * @param password      密码
     * @param typeid        题目类型
     * @param timeout       任务超时时间
     * @param softid        软件ID
     * @param softkey       软件KEY
     * @param filePath      题目截图或原始图二进制数据路径
     */
    private static String createByPost(String username, String password, String typeid, String timeout, String softid,
            String softkey, String filePath) {
        String result = "";
        String param = String.format("username=%s&password=%s&typeid=%s&timeout=%s&softid=%s&softkey=%s", username,
                password, typeid, timeout, softid, softkey);
        try {
            File f = new File(filePath);
            if (f.exists()) {
                int size = (int) f.length();
                byte[] data = new byte[size];
                FileInputStream fis = new FileInputStream(f);
                fis.read(data, 0, size);
                fis.close();
                if (data.length > 0) {
                    result = httpPostImage("http://api.ruokuai.com/create.xml", param, data);
                }
            }
        }
        catch (Exception e) {
            result = "出错了：" + e.getMessage();
        }
        return result;
    }

    /**
     * 上报错题
     * @param username  用户名
     * @param password  用户密码
     * @param softId    软件ID
     * @param softkey   软件KEY
     * @param id        报错题目的ID
     */
    private static String report(String username, String password, String softid, String softkey, String id) {
        String param = String.format("username=%s&password=%s&softid=%s&softkey=%s&id=%s", username, password, softid,
                softkey, id);
        String result;
        try {
            result = httpRequestData("http://api.ruokuai.com/reporterror.json", param);
        }
        catch (IOException e) {
            result = "出错了：" + e.getMessage();
        }
        return result;
    }
}