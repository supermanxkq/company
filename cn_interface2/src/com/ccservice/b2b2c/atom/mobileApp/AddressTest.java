package com.ccservice.b2b2c.atom.mobileApp;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.tenpay.util.MD5Util;

/**
 * 
 * 常用旅客的操作的测试类
 * @time 2015年5月30日 下午4:17:08
 * @author baiyushan
 */

public class AddressTest {
    //通用参数
    public static String partnerid;

    public static String method;

    public static String reqtime;

    public static String sign;//md5(partnerid+method+reqtime+md5(key))

    public static String curphone;

    //增加的参数
    public static String username;

    public static String userphone;

    public static String postcode;

    public static String address;

    //删除的参数
    public static String ID;

    //修改参数
    public static String updateID;

    public static String updateusername;

    public static String updateuserphone;

    public static String updatepostcode;

    public static String updateaddress;

    public static String url;

    public static String getreqtime() {
        SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    public static String getsign(String partnerid, String reqtime, String key) {
        String keyString = partnerid + reqtime + MD5Util.MD5Encode(key, "UTF-8");
        keyString = MD5Util.MD5Encode(keyString, "UTF-8");
        return keyString;
    }

    public static void main(String[] args) {

        //给通用参数赋值
        url = "http://localhost:18080/cn_interface/AddressAppRequest";
        partnerid = "matangtest";
        String key = "b9jmkn4sl87q5q4h7taruvxt7q63pym7";
        reqtime = getreqtime();
        sign = getsign(partnerid, reqtime, key);
        curphone = "13810896056";
        //        method = "add_method";
        //        method = "delete_method";
        //        method = "update_method";
        method = "select_method";
        JSONObject jsonStr = new JSONObject();
        jsonStr.put("partnerid", partnerid);
        jsonStr.put("method", method);
        jsonStr.put("reqtime", reqtime);
        jsonStr.put("sign", sign);
        jsonStr.put("curphone", curphone);
        /**
         * 增加测试
         */

        //        username = "1白玉山";
        //        userphone = "1000000000";
        //        postcode = "036400";
        //        address = "北京";
        //        jsonStr.put("username", username);
        //        jsonStr.put("userphone", userphone);
        //        jsonStr.put("postcode", postcode);
        //        jsonStr.put("address", address);
        //        jsonStr.toString();
        //        String resultString = SendPostandGet.submitPost(url, "jsonStr=" + jsonStr, "UTF-8").toString();
        //        System.out.println(resultString);

        /**
         * 删除测试
         */
        //        ID = "521";
        //        jsonStr.put("ID", ID);
        //        jsonStr.toString();
        //        String resultString = SendPostandGet.submitPost(url, "jsonStr=" + jsonStr, "utf-8").toString();
        //        System.out.println(resultString);

        /**
         * 查看测试
         */

        jsonStr.toString();
        String resultString = SendPostandGet.submitPost(url, "jsonStr=" + jsonStr, "utf-8").toString();
        System.out.println(resultString);

        /**
         * 修改测试
         */
        //        ID = "520";
        //        username = "白山1";
        //        userphone = "47000000";
        //        postcode = "11111111";
        //        address = "儿童";
        //        jsonStr.put("ID", ID);
        //        jsonStr.put("username", username);
        //        jsonStr.put("userphone", userphone);
        //        jsonStr.put("postcode", postcode);
        //        jsonStr.put("address", address);
        //        jsonStr.toString();
        //        String resultString = SendPostandGet.submitPost(url, "jsonStr=" + jsonStr, "utf-8").toString();
        //        System.out.println(resultString);

    }
}
