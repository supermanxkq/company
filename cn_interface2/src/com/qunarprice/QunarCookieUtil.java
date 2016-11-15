package com.qunarprice;

import java.io.*;
import com.ccservice.elong.inter.PropertyUtil;

/**去哪儿Cookie*/
public class QunarCookieUtil {

    private static String dir = PropertyUtil.getValue("QunarCookiePath").replace("/", File.separator);

    /**下载验证码*/
    public static void downVcode(InputStream in) throws Exception {
        String imgpath = dir + File.separator + "vcode";
        if (!new File(imgpath).exists()) {
            new File(imgpath).mkdirs();
        }
        byte[] bs = new byte[1024];
        int len;
        OutputStream os = new FileOutputStream(imgpath + File.separator + "image.jpg");
        while ((len = in.read(bs)) != -1) {
            os.write(bs, 0, len);
        }
        os.close();
        in.close();
    }

    /**读Cookie*/
    public static String getCookie(String ip) {
        StringBuffer buf = new StringBuffer();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            String txt = dir + File.separator + "HotelCookie" + File.separator + ip + ".txt";
            File file = new File(txt);
            if (file.exists()) {
                fis = new FileInputStream(txt);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    buf.append(lineTxt);
                }
            }
        }
        catch (Exception e) {
            buf = new StringBuffer();
        }
        finally {
            try {
                if (br != null)
                    br.close();
            }
            catch (Exception e) {
            }
            try {
                if (isr != null)
                    isr.close();
            }
            catch (Exception e) {
            }
            try {
                if (fis != null)
                    fis.close();
            }
            catch (Exception e) {
            }
        }
        return buf.toString();
    }

    /**写Cookie*/
    public static void setCookie(String cookie, String ip) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            String txt = dir + File.separator + "HotelCookie";
            File file = new File(txt);
            if (!file.exists()) {
                file.mkdirs();
            }
            txt = txt + File.separator + ip + ".txt";
            file = new File(txt);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(cookie);
        }
        catch (Exception e) {

        }
        finally {
            try {
                if (bw != null)
                    bw.flush();
                bw.close();
            }
            catch (Exception e) {
            }
            try {
                if (fw != null)
                    fw.flush();
                fw.close();
            }
            catch (Exception e) {
            }
        }
    }

}