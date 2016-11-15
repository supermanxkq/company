package com.ccservice.b2b2c.atom.service12306.mem;

import java.util.Date;

import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class TrainAutoCreateOrderMethod extends TongchengSupplyMethod {
    //內存中是否使用新版WEB自动下单   1:使用  0:不使用
    private static int isAutoCreateOrder = 0;

    //上次修改的最后时间
    private static Date lastChangeDate = new Date();

    //时间点阀值
    private final static long TIME_OUT = 5 * 60 * 1000;

    //配置表c_name
    private final static String SYSTEM_CONFIG_KEY = "IsNewWEB";

    private final static int AUTO = 1;

    public static int getIsAutoCreateOrder() {
        //拿内存中数据，1：如果是使用新版，查DB，查完改内存   2：拿到内存中数据最后修改时间， 如果当前时间-最后修改时间>=时间点阀值，查DB，查完修改内存
        if (System.currentTimeMillis() - lastChangeDate.getTime() >= TIME_OUT || isAutoCreateOrder == AUTO) {
            try {
                String result = getSystemConfig(SYSTEM_CONFIG_KEY);
                int usAutoCreateOrderDB = Integer.parseInt(result);
                isAutoCreateOrder = usAutoCreateOrderDB;
            }
            catch (Exception e) {
                ExceptionUtil.writelogByException("新版web自动下单", e);
            }
        }
        return isAutoCreateOrder;
    }
}
