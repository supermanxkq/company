package com.ccservice.b2b2c.atom.service12306.onlineRefund.mq.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Comparator;
import java.util.Collections;
import com.alibaba.fastjson.JSONObject;

/**
 * 退票数据工具
 * @author WH
 * @time 2016年11月3日 上午11:16:33
 * @version 1.0
 */

class ReturnTicketDataUtil {

    /**
     * 排序取下一个车票
     */
    public static JSONObject nextTicket(Hashtable<Long, JSONObject> tickets) {
        //转换
        List<JSONObject> list = new ArrayList<JSONObject>(tickets.values());
        //排序
        try {
            Collections.sort(list, new Comparator<JSONObject>() {
                public int compare(JSONObject a, JSONObject b) {
                    //结果
                    int result = 0;
                    //发车时间
                    long departTimeA = a.getLongValue("departTime");
                    long departTimeB = b.getLongValue("departTime");
                    //时间一样
                    if (departTimeA == departTimeB) {
                        //请求时间
                        long requestTimeA = a.getLongValue("requestTime");
                        long requestTimeB = b.getLongValue("requestTime");
                        //小的放前
                        result = requestTimeA <= requestTimeB ? -1 : 1;
                    }
                    //小的在前，暂不跟当前时间比较
                    else {
                        result = departTimeA < departTimeB ? -1 : 1;
                    }
                    //返回
                    return result;
                }
            });
        }
        catch (Exception e) {

        }
        //返回
        return list.get(0);
    }
}