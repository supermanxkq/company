package com.qunarprice;

import java.io.*;
import com.ccservice.elong.inter.PropertyUtil;

/**去哪儿密钥*/
public class QunarMixKeyUtil {

    private static String path = PropertyUtil.getValue("QunarCookiePath").replace("/", File.separator) + File.separator
            + "RandomSecretKey.txt";

    /**写KEY*/
    public static void setKey(String key) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write(key);
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

    /**读KEY*/
    public static String getKey() {
        StringBuffer buf = new StringBuffer();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                fis = new FileInputStream(path);
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

}
