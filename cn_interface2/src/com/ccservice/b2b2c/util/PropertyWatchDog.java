package com.ccservice.b2b2c.util;

import org.apache.log4j.helpers.FileWatchdog;

/**
 * 
 * @author wzc
 * 文件监控
 *
 */
public class PropertyWatchDog extends FileWatchdog {

    public PropertyWatchDog(String filename) {
        super(filename);
    }

    @Override
    protected void doOnChange() {
        try {
            SystemPropetyUtil.initData(filename);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
