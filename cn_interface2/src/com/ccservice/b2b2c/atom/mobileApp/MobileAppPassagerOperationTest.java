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

public class MobileAppPassagerOperationTest {
    //通用参数
    public static String partnerid;

    public static String method;

    public static String reqtime;

    public static String sign;//md5(partnerid+method+reqtime+md5(key))

    public static String curphone;

    public static String curpwd;//当前用户密码

    //增加的参数
    public static String username;

    public static String useridentitytype;

    public static String useridentity;

    public static String usertype;

    //删除的参数
    public static String ID;

    //修改参数
    public static String updateID;

    public static String updateusername;

    public static String updateuseridentity;

    public static String updateuseridentitytype;

    public static String updateusertype;

    public static String status;

    //    final static String url = "http://121.40.125.114:9116/cn_interface/MobileAppPassagerOperation";

    final static String url = "http://localhost:8080/ccs_interface/MobileAppPassagerOperation";

    //    final static String url = "";

    public static String getreqtime() {
        SimpleDateFormat yyyyMMddHHmmssSSS = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return yyyyMMddHHmmssSSS.format(new Date());
    }

    public static String getsign(String partnerid, String reqtime, String key, String method) {
        String keyString = partnerid + method + reqtime + MD5Util.MD5Encode(key, "UTF-8");
        keyString = MD5Util.MD5Encode(keyString, "UTF-8");
        return keyString;
    }

    public static void main(String[] args) {
        //给通用参数赋值
        partnerid = "tongcheng_train_test";
        String key = "lmh46c63ubh1h8oj6680wbtgfi40btqh";
        reqtime = getreqtime();
        curphone = "15811073432";
        curpwd = "111111";
        //        method = "add_method";
        //        method = "select_method";
        method = "delete_method";
        //        method = "update_method";
        sign = getsign(partnerid, reqtime, key, method);
        JSONObject jsonStr = new JSONObject();
        jsonStr.put("partnerid", partnerid);
        jsonStr.put("method", method);
        jsonStr.put("reqtime", reqtime);
        jsonStr.put("sign", sign);
        jsonStr.put("curphone", curphone);
        jsonStr.put("curpwd", curpwd);
        /**
         * 增加测试
         */
        //        username = "王战朝";
        //        useridentitytype = "二代身份证";
        //        useridentity = "410883199006281010";
        //        usertype = "成人";
        //        jsonStr.put("username", username);
        //        jsonStr.put("useridentitytype", useridentitytype);
        //        jsonStr.put("useridentity", useridentity);
        //        jsonStr.put("usertype", usertype);
        //        jsonStr.toString();
        //        System.out.println(jsonStr);
        //        String resultString = SendPostandGet.submitPost(url, "jsonStr=" + jsonStr, "UTF-8").toString();
        //        System.out.println(resultString);

        /**
         * 删除测试
         */
        ID = "3909";
        jsonStr.put("ID", ID);
        jsonStr.toString();
        String resultString = SendPostandGet.submitPost(url, "jsonStr=" + jsonStr, "utf-8").toString();
        System.out.println(resultString);

        /**
         * 查看测试
         */

        //        jsonStr.toString();
        //        String resultString = SendPostandGet.submitPost(url, "jsonStr=" + jsonStr, "utf-8").toString();
        //        System.out.println(resultString);

        /**
         * 修改测试
         */

        //        ID = "3909";
        //        username = "王战朝";
        //        useridentitytype = "二代身份证";
        //        useridentity = "4108831990062810110";
        //        usertype = "成人";
        //        jsonStr.put("ID", ID);
        //        jsonStr.put("username", username);
        //        jsonStr.put("useridentitytype", useridentitytype);
        //        jsonStr.put("useridentity", useridentity);
        //        jsonStr.put("usertype", usertype);
        //        jsonStr.toString();
        //        String resultString = SendPostandGet.submitPost(url, "jsonStr=" + jsonStr, "utf-8").toString();
        //        System.out.println(resultString);

    }
}
