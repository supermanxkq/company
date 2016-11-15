/**
 * 
 */
package com.ccservice.b2b2c.atom.servlet.TongChengTrain;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 
 * @time 2015年10月22日 下午2:29:02
 * @author chendong
 */
public class TrainServerUtil {

    /**
     * 4.23.获取供应商12306 host IP地址
     * @time 2015年10月22日 下午2:29:13
     * @author chendong
     */
    public static String get_train_12306hostip() {
        String ip = "";
        InetAddress host;
        try {
            host = InetAddress.getByName("kyfw.12306.cn");
            ip = host.toString().substring(host.toString().lastIndexOf("/") + 1, host.toString().length());
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ip;
    }

}
