package com.ccservice.b2b2c.atom.service12306.offlineRefund.changeRefund.util;

import java.util.Hashtable;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;

public class ChangeRefundUtil {

    private static Object changeRefundLocker = new Object();

    private static Hashtable<Long, String> changeRefundData = new Hashtable<Long, String>();

    /**
     * 添加至内存
     * @return true:添加成功；false:添加失败，即内存已存在
     */
    public static boolean changeRefundAdd(long changeId) {
        boolean result = false;
        synchronized (changeRefundLocker) {
            if (!changeRefundData.containsKey(changeId)) {
                result = true;
                changeRefundData.put(changeId, ElongHotelInterfaceUtil.getCurrentTime());
            }
        }
        return result;
    }

    /**
     * 移除内存数据
     */
    public static void changeRefundRemove(long changeId) {
        synchronized (changeRefundLocker) {
            if (changeRefundData.containsKey(changeId)) {
                changeRefundData.remove(changeId);
            }
        }
    }

    /**
     * 清空内存数据，每晚定时执行，防止因BUG有未移除的
     */
    public static void changeRefundClean() {
        synchronized (changeRefundLocker) {
            changeRefundData = new Hashtable<Long, String>();
        }
    }

}