package com.ccservice.b2b2c.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ccservice.b2b2c.atom.component.WriteLog;

/**
 * 为了保证同城更新不受影响，添加新的时间转换工具类
 * @author fiend
 *
 */
public class InterfaceTimeRuleUtil {

    /**
     * 说明:下单可用时间:早6晚11
     * 
     * @param date
     * @return
     * @time 2015年5月15日 下午3:46:47
     * @author fiend
     */
    public static boolean getNowTimeCreateOrder() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        try {
            Date dateBefor = df.parse("06:02:00");
            Date dateAfter = df.parse("23:00:00");
            Date time = df.parse(df.format(date));
            if (time.after(dateBefor) && time.before(dateAfter)) {
                return true;
            }
        }
        catch (Exception e) {
            WriteLog.write("InterfaceTimeRuleUtil_error", "时间异常");
            ExceptionUtil.writelogByException("InterfaceTimeRuleUtil_error", e);
        }
        return false;//现在24小时,以后有需要再改为FALSE
    }

    /**
     * 是否符合条件取消夜间单
     * @return
     */
    public static boolean isNight() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        try {
            Date dateBefor = df.parse("05:50:00");
            Date dateAfter = df.parse("23:10:00");
            Date time = df.parse(df.format(date));
            if (time.after(dateBefor) && time.before(dateAfter)) {
                return true;
            }
        }
        catch (ParseException e) {
            WriteLog.write("InterfaceTimeRuleUtil_error", "时间异常");
            ExceptionUtil.writelogByException("InterfaceTimeRuleUtil_error", e);
        }
        return true;//现在24小时,以后有需要再改为FALSE
    }

    /**
     * 是否符合条件,放入夜间单
     * @return
     */
    public static boolean isNightCreateOrder() {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        try {
            Date dateBefor = df.parse("06:00:00");
            Date dateAfter = df.parse("23:00:00");
            Date time = df.parse(df.format(date));
            if (time.after(dateBefor) && time.before(dateAfter)) {
                return false;
            }
        }
        catch (ParseException e) {
            WriteLog.write("InterfaceTimeRuleUtil_error", "时间异常");
            ExceptionUtil.writelogByException("InterfaceTimeRuleUtil_error", e);
        }
        return true;//现在24小时,以后有需要再改为FALSE
    }
}
