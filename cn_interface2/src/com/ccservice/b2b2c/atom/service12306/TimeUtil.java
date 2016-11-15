package com.ccservice.b2b2c.atom.service12306;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 此类为时间公共类
 * 所有处理时间方法写到这里
 * @author cd
 *
 */
public class TimeUtil {


    /**
     * 获取当天的时间，默认返回 yyyy-MM-dd
     * 
     * @param type 什么格式的    1:yyyy-MM-dd,2:yyyy-MM-dd HH,3:yyyy-MM-dd HH:mm,4:yyyy-MM-dd HH:mm:ss
     * @return 时间的字符串
     * @time 2014年8月30日 上午11:08:14
     * @author chendong
     */
    public static String gettodaydate(int type) {
        if (type == 0) {
            return new SimpleDateFormat("yyyyMMdd").format(new Date());
        }
        if (type == 1) {
            return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
        if (type == 2) {
            return new SimpleDateFormat("yyyy-MM-dd HH").format(new Date());
        }
        if (type == 3) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());
        }
        if (type == 4) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        }
        if (type == 5) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        }
        if (type == 15) {
            return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        }
        else {
            return new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
    }

    /**
     * 获取当天的时间，默认返回HH:mm:ss
     * 
     * @param type 什么格式的    
     * @return 时间的字符串
     * @time 2014年8月30日 上午11:08:14
     * @author chendong
     */
    public static String gettodayTime(int type) {
        if (type == 0) {
            return new SimpleDateFormat("HH").format(new Date());
        }
        if (type == 1) {
            return new SimpleDateFormat("HH:mm").format(new Date());
        }
        if (type == 2) {
            return new SimpleDateFormat("HH:mm:ss").format(new Date());
        }
        if (type == 3) {
            return new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        }
        else {
            return new SimpleDateFormat("HH:mm:ss").format(new Date());
        }
    }

    public static Timestamp parseTimestampbyStringReal(String timeString) {
        try {
            return new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(timeString).getTime());
        }
        catch (ParseException e) {
        }
        return null;
    }

    public static Timestamp parseTimestampbyString(String timeString) {
        try {
            return new Timestamp(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(timeString).getTime());
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return getCurrentTime();
    }

    /**
     * 获取当前时间
     * @return
     */
    public static Timestamp getCurrentTime() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 获取当前时间的前后多少天
     * 
     * @param type 什么格式的    1:yyyy-MM-dd,2:yyyy-MM-dd HH,3:yyyy-MM-dd HH:mm,4:yyyy-MM-dd HH:mm:ss
     * @param day 当前天数的前后天数
     * @return
     * @time 2014年9月1日 下午5:46:01
     * @author chendong
     */
    public static String gettodaydatebyfrontandback(int type, int day) {
        Date nowDate = new Date();
        Calendar now = Calendar.getInstance();
        now.setTime(nowDate);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        nowDate = now.getTime();
        if (type == 1) {
            return new SimpleDateFormat("yyyy-MM-dd").format(nowDate);
        }
        if (type == 2) {
            return new SimpleDateFormat("yyyy-MM-dd HH").format(nowDate);
        }
        if (type == 3) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(nowDate);
        }
        if (type == 4) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowDate);
        }
        else {
            return new SimpleDateFormat("yyyy-MM-dd").format(nowDate);
        }
    }

    /**
     * 把字符串的日期转换为Date类型
     * 
     * @param date
     * @param type
     * @return
     * @time 2015年1月9日 下午3:39:44
     * @author chendong
     */
    public static Date parseStringtimeToDate(String date, int type) {
        Date sdate = new Date();
        try {
            if (type == 1) {
                return new SimpleDateFormat("yyyy-MM-dd").parse(date);
            }
            if (type == 2) {
                return new SimpleDateFormat("yyyy-MM-dd HH").parse(date);
            }
            if (type == 3) {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date);
            }
            if (type == 4) {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
            }
            else {
                return new SimpleDateFormat("yyyy-MM-dd").parse(date);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
        return sdate;
    }

    /**
     * 
     * @param nowDate 几号为参考值
     * @param day 当前天数的前后天数
     * @param type 什么格式的    1:yyyy-MM-dd,2:yyyy-MM-dd HH,3:yyyy-MM-dd HH:mm,4:yyyy-MM-dd HH:mm:ss
     * @return
     * @time 2014年9月1日 下午5:46:01
     * @author chendong
     */
    public static String parseDateToStringtime(Date nowDate, int day, int type) {
        Calendar now = Calendar.getInstance();
        now.setTime(nowDate);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        nowDate = now.getTime();
        if (type == 1) {
            return new SimpleDateFormat("yyyy-MM-dd").format(nowDate);
        }
        if (type == 2) {
            return new SimpleDateFormat("yyyy-MM-dd HH").format(nowDate);
        }
        if (type == 3) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(nowDate);
        }
        if (type == 4) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(nowDate);
        }
        else {
            return new SimpleDateFormat("yyyy-MM-dd").format(nowDate);
        }
    }

    public static void main(String[] args) {
        //        System.out.println(gettodaydatebyfrontandback(1, -30));
        System.out.println(TimeUtil.gettodaydate(4));
    }
}
