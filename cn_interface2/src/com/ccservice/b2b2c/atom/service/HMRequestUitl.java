package com.ccservice.b2b2c.atom.service;

public class HMRequestUitl {

    public static Long type = 1l;//供应商模式

    //航天华有访问地址
    public static String huayou = "http://121.197.13.153:8080/cn_interface/HMHotel.jsp?p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI";

    //航天华有访问地址
    public static String huayouorder = "http://121.197.13.153:8080/cn_interface/HMHotel.jsp?";

    //公司
    public static String huayoucompany = "CN00839";

    //id
    public static String huayouid = "HTHYAPI";

    //pass
    public static String huayoupass = "HTHYAPI";

    //海游访问地址
    public static String haiyouorder = "http://112.124.32.203:8032/cn_interface/HMHotel.jsp?";

    //海游访问地址
    public static String haiyou = "http://112.124.32.203:8032/cn_interface/HMHotel.jsp?p_company=CN00834&p_id=API&p_pass=123456";

    //公司
    public static String haiyoucompany = "CN00834";

    //id
    public static String haiyouid = "API";

    //pass
    public static String haiyoupass = "123456";

    //风行访问地址
    public static String fengxingorder = "http://www.hoowind.cn/cn_interface/HMHotel.jsp?";

    //风行访问地址
    public static String fengxing = "http://www.hoowind.cn/cn_interface/HMHotel.jsp?p_company=CN00852&p_id=FXAPI&p_pass=FXAPI";

    //公司
    public static String fengxingcompany = "CN00852";

    //id
    public static String fengxingid = "FXAPI";

    //pass
    public static String fengxingpass = "FXAPI";

    /**
     * 华闽请求地址 
     */
    public static String getHMRequestUrlHeader() {
        if (type == 1) {
            return huayou;
        }
        else if (type == 2) {
            return haiyou;
        }
        else if (type == 3) {
            return fengxing;
        }
        return "";
    }

    /**
     * 华闵地址
     */
    public static String getHMOrderurl() {
        if (type == 1) {
            return huayouorder;
        }
        else if (type == 2) {
            return huayouorder;
        }
        else if (type == 3) {
            return fengxingorder;
        }
        return "";
    }

    /**
     * 返回公司账户信息
     */
    public static String getCompany() {
        if (type == 1) {
            return huayoucompany;
        }
        else if (type == 2) {
            return haiyoucompany;
        }
        else if (type == 3) {
            return fengxingcompany;
        }
        return "";
    }

    /**
     * 返回id信息
     */
    public static String getId() {
        if (type == 1) {
            return huayouid;
        }
        else if (type == 2) {
            return haiyouid;
        }
        else if (type == 3) {
            return fengxingid;
        }
        return "";
    }

    /**
     * 返回密码信息
     */
    public static String getPass() {
        if (type == 1) {
            return huayoupass;
        }
        else if (type == 2) {
            return haiyoupass;
        }
        else if (type == 3) {
            return fengxingpass;
        }
        return "";
    }
}
