package com.ccservice.b2b2c.atom.service12306.offlineRefund.util;

import java.util.Map;

/**
 * 退票工具类
 * @author WH
 * @time 2016年4月1日 上午11:31:39
 * @version 1.0
 */

public class RefundTicketUtil {

    @SuppressWarnings("rawtypes")
    public static String getStringMapValue(Map map, String key) {
        return map.get(key) == null ? "" : map.get(key).toString();
    }

    @SuppressWarnings("rawtypes")
    public static long getLongMapValue(Map map, String key) {
        return map.get(key) == null ? 0 : Long.parseLong(map.get(key).toString());
    }

    @SuppressWarnings("rawtypes")
    public static int getIntMapValue(Map map, String key) {
        return map.get(key) == null ? 0 : Integer.parseInt(map.get(key).toString());
    }

    @SuppressWarnings("rawtypes")
    public static float getFloatMapValue(Map map, String key) {
        return map.get(key) == null ? 0 : Float.parseFloat(map.get(key).toString());
    }

    /**
     * 不再进行线上退票的12306状态 
     */
    public static boolean dontRefundStatus(int status12306) {
        //已取票、退票完成、账号被封、手机待核验、旅游旺季
        return status12306 == 8 || status12306 == 17 || status12306 == 18 || status12306 == 19 || status12306 == 20
                || status12306 == -18 || status12306 == -19 || status12306 == -20;
    }

}