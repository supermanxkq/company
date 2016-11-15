package com.ccservice.b2b2c.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TrainorderTimeUtil {
    /**
     * 输入类车发车时间和运行时间，得到到达时间
     * @param departime  yyyy-MM-dd HH:mm     2013-11-09 07:00:00
     * @param costtime  HH:mm          05:36
     * @return   yyyy-MM-dd HH:mm:00        2013-11-09 12:36:00
     * @throws ParseException 2
     * @time 2015年1月6日 下午1:35:12
     * @author fiend
     */
    public static String getArrivalTime(String departime, String costtime) {
        String timeFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        long arrtime = System.currentTimeMillis();
        try {
            Date date = sdf.parse(departime);
            long deptime = date.getTime();
            long hours = 0;
            long mins = 0;
            if (!costtime.split(":")[0].equals("0") && !costtime.split(":")[0].equals("00")) {
                hours = Long.valueOf(costtime.split(":")[0]) * 60 * 60 * 1000;
            }
            if (!costtime.split(":")[1].equals("0") && !costtime.split(":")[1].equals("00")) {
                mins = Long.valueOf(costtime.split(":")[1]) * 60 * 1000;
            }
            arrtime = deptime + hours + mins;
        }
        catch (Exception e) {
        }
        return sdf.format(new Date(arrtime));
    }
}
