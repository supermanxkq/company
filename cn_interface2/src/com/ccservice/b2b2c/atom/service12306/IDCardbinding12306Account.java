package com.ccservice.b2b2c.atom.service12306;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.ccservice.b2b2c.atom.component.SendPostandGet;

/**
 * @time 2014年12月22日 下午2:49:53
 * @author wzc
 */
public class IDCardbinding12306Account {
    /**
     * 
     * 根据传入旅客绑定对应的12306账号
     * @param listpassenger
     * @return
     * @time 2014年12月22日 下午2:55:56
     * @author wzc
     */
    public static String IDCardbinding12306Account(String url, String passengerName, String id_no, String id_type,
            String cookieString) {
        String msg = "";
        String par = "";
        try {
            par = "datatypeflag=16&name=" + URLEncoder.encode(passengerName, "UTF-8") + "&id_no=" + id_no + "&id_type="
                    + id_type + "&cookie=" + cookieString;
            msg = SendPostandGet.submitPost(url, par, "UTF-8").toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }
}