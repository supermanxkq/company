package com.ccservice.b2b2c.atom.component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

/**
 * 
 * 
 * @time 2014年9月21日 下午8:09:06
 * @author wzc
 */
public class FileReadUtil {
    /**
     * 
     * 读取本地文件工具类
     * @param path
     * @return
     * @throws Exception
     * @time 2014年9月21日 下午8:11:00
     * @author wzc
     */
    public static String readFile(String path) throws Exception {
        StringBuffer bf = new StringBuffer();
        FileInputStream in = new FileInputStream(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String data = null;
        while ((data = reader.readLine()) != null) {
            bf.append(data);
        }
        return bf.toString();
    }
}
