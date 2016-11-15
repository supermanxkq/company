package com.callback;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * 
 * @author wzc
 * 读取属�?�文件工具类
 *
 */
public class PropertyUtil {
    /**
     * 
     * @param key 根据key返回对应的value�?
     * @return 返回的字符串
     */
    public static String getValue(String key) {
        Properties p = new Properties();
        String value = "";
        InputStream in = null;
        try {
            in = PropertyUtil.class.getResourceAsStream("/Constants.properties");
            p.load(in);
            value = p.getProperty(key);
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
        return value;
    }

    /**
     * 
     * @param key 根据key返回对应的value�?
     * @param filename 文件�?
     * @return 返回的字符串
     */
    public static String getValue(String key, String filename) {
        Properties p = new Properties();
        String value = "";
        InputStream in = null;
        try {
            in = PropertyUtil.class.getResourceAsStream("/" + filename);
            p.load(in);
            value = p.getProperty(key);
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
        return value;
    }
}
