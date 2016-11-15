package com.ccservice.b2b2c.atom.servlet;

import java.util.Map;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.MemCached;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;

/**
 * 火车票查询后需要线程处理的内容
 * 
 * @time 2015年5月16日 下午3:10:50
 * @author chendong
 */
public class TrainSearchThread implements Runnable {
    public static void main(String[] args) {
        System.out.println(MemCached.getInstance().get("trainSearch_trainnum_tuniulvyou"));
    }

    String mem_key;

    Long trainnum;

    int type;//1:放缓存,2:清空数据

    Map<String, InterfaceAccount> interfaceAccountMap;

    String interfaceAccountmmckey;

    InterfaceAccount interfaceAccount;

    public TrainSearchThread() {

    }

    public TrainSearchThread(Map<String, InterfaceAccount> interfaceAccountMap, int type) {
        this.interfaceAccountMap = interfaceAccountMap;
        this.type = type;
    }

    public TrainSearchThread(String mem_key, Long trainnum, int type) {
        this.mem_key = mem_key;
        this.trainnum = trainnum;
        this.type = type;
    }

    public TrainSearchThread(String interfaceAccountmmckey, InterfaceAccount interfaceAccount, int type) {
        this.interfaceAccountmmckey = interfaceAccountmmckey;
        this.interfaceAccount = interfaceAccount;
        this.type = type;
    }

    @Override
    public void run() {
        if (this.type == 1) {//1:放缓存,
            Object object = MemCached.getInstance().get(this.mem_key);//先获取到
            if (object == null) {
                this.trainnum = 0L;
            }
            else {
                this.trainnum = Long.parseLong(object.toString()) + 1;
            }

            boolean issuccess = MemCached.getInstance().add(this.mem_key, this.trainnum + "");
            if (!issuccess) {
                issuccess = MemCached.getInstance().replace(this.mem_key, this.trainnum + "");
            }

            WriteLog.write("查询接口次数", issuccess + "=" + this.mem_key + "=" + this.trainnum);
        }
        else if (this.type == 2) {//2:清空数据
            for (Map.Entry<String, InterfaceAccount> entry : interfaceAccountMap.entrySet()) {
                MemCached.getInstance().delete("trainSearch_trainnum_" + entry.getKey());
            }
        }
        else if (this.type == 3) {
            boolean issuccess = MemCached.getInstance().add(this.interfaceAccountmmckey, interfaceAccount);
            if (!issuccess) {
                issuccess = MemCached.getInstance().replace(this.interfaceAccountmmckey, interfaceAccount);
            }
        }
    }

    public static void main1(String[] args) {
        long l1 = System.currentTimeMillis();
        new Thread(new TrainSearchThread()).start();
        System.out.println(System.currentTimeMillis() - l1);

    }
}
