package com.ccservice.b2b2c.atom.service12306.offlineRefund.util;

import java.util.Hashtable;

/**
 * 退票数据
 * @author WH
 * @time 2016年5月3日 上午10:00:58
 * @version 1.0
 */

public class RefundTicketDataUtil {

    /**
     * 锁
     */
    private static Object refundDataLocker = new Object();

    /**
     * 退票数据
     */
    public static Hashtable<String, String> refundData = new Hashtable<String, String>();

    /**
     * 添加数据
     */
    public static void refundDataAdd(String dataKey, String dataValue) {
        synchronized (refundDataLocker) {
            refundData.put(dataKey, dataValue);
        }
    }

    /**
     * 移除数据
     */
    public static void refundDataRemove(String dataKey) {
        synchronized (refundDataLocker) {
            refundData.remove(dataKey);
        }
    }

    /**
     * 包含数据
     */
    public static boolean refundDataContains(String dataKey) {
        synchronized (refundDataLocker) {
            return refundData.containsKey(dataKey);
        }
    }

    /**
     * 获取数据
     */
    public static String refundDataGet(String dataKey) {
        synchronized (refundDataLocker) {
            return refundData.get(dataKey);
        }
    }

    /**
     * 只读数据
     */
    public static Hashtable<String, String> refundDataReadOnly() {
        synchronized (refundDataLocker) {
            Hashtable<String, String> temp = new Hashtable<String, String>();
            temp.putAll(refundData);
            return temp;
        }
    }

}