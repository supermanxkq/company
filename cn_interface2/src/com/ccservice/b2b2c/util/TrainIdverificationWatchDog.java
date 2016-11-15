package com.ccservice.b2b2c.util;

import org.apache.log4j.helpers.FileWatchdog;

/**
 * 
 * @author wzc
 * 文件监控
 *
 */
public class TrainIdverificationWatchDog extends FileWatchdog {

    public TrainIdverificationWatchDog(String filename) {
        super(filename);
    }

    @Override
    protected void doOnChange() {
        try {
            TrainIdverificationPropetyUtil.initData(filename);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("当前是否开通虚假身份验证:" + TrainIdverificationPropetyUtil.getValue("isfalsitylverification"));
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
