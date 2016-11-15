package com.qunarprice;

import java.io.File;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

/**
 * 随机获取代理IP工具 
 * @author WH
 */

public class AgentIpUtil {

    private static String path = AgentIpUtil.class.getResource("AgentIp.txt").getPath();

    /**
     * 获取IP
     */
    public static String getIp() throws Exception {
        List<String> allip = getAllIp();
        if (allip == null || allip.size() == 0) {
            throw new Exception("No Available Ip.");
        }
        return allip.get(new Random().nextInt(allip.size()));
    }

    /**
     * 获取IP文本内容
     */
    public static List<String> getAllIp() {
        //使用当前主机IP
        boolean current = true;
        List<String> iplist = new ArrayList<String>();
        //IO
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            File file = new File(path);
            if (file.exists()) {
                fis = new FileInputStream(path);
                isr = new InputStreamReader(fis, "utf-8");
                br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (ElongHotelInterfaceUtil.StringIsNull(line)) {
                        continue;
                    }
                    line = line.trim();
                    if ("//localhost".equals(line)) {
                        current = false;
                        continue;
                    }
                    if (line.contains("/") || line.split("\\.").length < 4 || !line.contains(":")) {
                        continue;
                    }
                    iplist.add(line);
                }
            }
        }
        catch (Exception e) {
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
        //加入本地
        if (current) {
            iplist.add("localhost");
        }
        //RETURN
        return iplist;
    }
}