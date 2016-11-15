package com.ccservice.b2b2c.atom.train.idmongo.mem;

import com.callback.PropertyUtil;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.train.idmongo.Thread.MyThreadMongoInsertPassengerTomas;
import com.ccservice.b2b2c.util.ExceptionUtil;

public class MongoInsertPassengerTomasMem {
    //日期差异
    public static int daychange = 0;

    //是否启动
    public static boolean isStart = false;

    static {
        try {
            daychange = Integer.valueOf(PropertyUtil.getValue("daychange", "Train.properties"));
        }
        catch (Exception e) {
           ExceptionUtil.writelogByException("MongoInsertPassengerTomasMem_Exception", e);
        }
        new MyThreadMongoInsertPassengerTomas().start();
    }

    public static void addDaychange() {
        daychange += 1;
        WriteLog.write("MongoInsertPassengerTomasMem", daychange + "");
    }

    public static void open() {
        if (!isStart) {
            isStart = true;
            System.out.println("启动成功！");
        }
        else {
            System.out.println("正在执行，无需启动！");
        }
    }

    public static void close() {
        if (isStart) {
            isStart = false;
            System.out.println("关闭成功！");
        }
        else {
            System.out.println("关闭状态，无需关闭！");
        }
    }
}
