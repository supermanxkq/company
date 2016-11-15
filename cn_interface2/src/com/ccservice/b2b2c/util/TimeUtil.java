package com.ccservice.b2b2c.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 为了保证同城更新不受影响，添加新的时间转换工具类
 * @author fiend
 *
 */
public class TimeUtil {
    public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";

    public static final String yyyyMMdd = "yyyy-MM-dd";

    public static final String yyyyMMddHHmmssSSS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * string类型转换为long类型
     * strTime要转换的String类型的时间
     * formatType时间格式
     * strTime的时间格式和formatType的时间格式必须相同
     * @param strTime
     * @param formatType
     * @return
     * @throws Exception
     * @author fiend
     */
    public static long stringToLong(String strTime, String formatType) throws Exception {
        Date date = stringToDate(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        }
        else {
            long currentTime = dateToLong(date); // date类型转成long类型
            return currentTime;
        }
    }

    /**
     * string类型转换为date类型
     * strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
     * HH时mm分ss秒，
     * strTime的时间格式必须要与formatType的时间格式相同
     * @param strTime
     * @param formatType
     * @return
     * @throws Exception
     * @author fiend
     */
    public static Date stringToDate(String strTime, String formatType) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        Date date = null;
        date = formatter.parse(strTime);
        return date;
    }

    /**
     * date类型转换为long类型
     * date要转换的date类型的时间
     * @param date
     * @return
     * @author fiend
     */
    public static long dateToLong(Date date) {
        return date.getTime();
    }

    /**
     * long类型转换为String类型
     * currentTime要转换的long类型的时间
     * formatType要转换的string类型的时间格式
     * @param currentTime
     * @param formatType
     * @return
     * @throws Exception
     * @author fiend
     */
    public static String longToString(long currentTime, String formatType) throws Exception {
        Date date = longToDate(currentTime, formatType); // long类型转成Date类型
        String strTime = dateToString(date, formatType); // date类型转成String
        return strTime;
    }

    /**
     * date类型转换为String类型
     * formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     * data Date类型的时间
     * @param data
     * @param formatType
     * @return
     * @author fiend
     */
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    /**
     * long转换为Date类型
     * currentTime要转换的long类型的时间
     * formatType要转换的时间格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     * @param currentTime
     * @param formatType
     * @return
     * @throws Exception
     * @author fiend
     */
    public static Date longToDate(long currentTime, String formatType) throws Exception {
        Date dateOld = new Date(currentTime); // 根据long类型的毫秒数生命一个date类型的时间
        String sDateTime = dateToString(dateOld, formatType); // 把date类型的时间转换为string
        Date date = stringToDate(sDateTime, formatType); // 把String类型转换为Date类型
        return date;
    }
}
