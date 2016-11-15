package com.ccservice.elong.inter;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 工具类 用于与date类型相关的类型转换
 * 
 * @author 师卫林
 * 
 */
public class DateSwitch {
	public static void main(String[] args) {
		// String arrivalatetime = "1900-01-01 00:00:00";
		// SwitchCalendar(arrivalatetime);
		// System.out.println(SwitchCalendar(arrivalatetime));
		// System.out.println(SwitchString(SwitchCalendar(arrivalatetime)));
		// SwitchSqlDate(SwitchCalendar(arrivalatetime));
		// System.out.println(CatchBeforeDay());
		// CatchLastDay();
		System.out.println(new Date());
	}

	/**
	 * 将Calendar类型转成String类型 需要时分秒
	 * 
	 * @param switchCalendar
	 */
	public static String SwitchString(Calendar cal) {
		// TODO Auto-generated method stub
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateStr = df.format(cal.getTime());
		return dateStr;
	}

	/**
	 * 将String 类型转换成Calendar类型 需要时分秒
	 * 
	 * @param str
	 * @return
	 */
	public static Calendar SwitchCalendar(String str) {
		// TODO Auto-generated method stub
		Calendar cal = new GregorianCalendar();
		Date data = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			data = sdf.parse(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cal.setTime(data);
		return cal;
	}

	/**
	 * 将String 类型转换成Calendar类型 不需要时分秒
	 * 
	 * @param str
	 * @return
	 */
	public static Calendar SwitchCalendar2(String str) {
		// TODO Auto-generated method stub
		Calendar cal = new GregorianCalendar();
		Date data = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			data = sdf.parse(str);
			// System.out.println(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
		cal.setTime(data);
		// System.out.println(cal);
		return cal;
	}

	/**
	 * 将Calendar类型转换成java.sql.Date类型
	 * 
	 * @param cal
	 * @return
	 */
	public static java.sql.Date SwitchSqlDate(Calendar cal) {
		Date date = cal.getTime();
		Timestamp datetime = new Timestamp(date.getTime());
		java.sql.Date time = new java.sql.Date(datetime.getTime());
		System.out.println(time);
		return time;
	}

	/**
	 * 获取当前日期的前一天
	 * 
	 * @return
	 */
	public static String CatchBeforeDay() {
		// 获取当前日期的前一天 System.currentTimeMillis()-24*60*60*1000(一天的毫秒数)
		Date date = new Date(System.currentTimeMillis() - 86400000);
		// System.out.println("当前天:"+System.currentTimeMillis());
		// System.out.println("前一天:"+(System.currentTimeMillis()-86400000));
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		String time = sdf.format(date);
		return time;
	}

	/**
	 * 当天日期
	 * 
	 * @return
	 */
	public static String CatchDay() {
		java.util.Date date = new java.util.Date();
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		String time = sdf.format(date);
		System.out.println(time);
		return time;
	}

	/**
	 * 获取当前日期的后一天
	 * 
	 * @return
	 */
	public static String CatchLaterDay() {
		// 获取当前日期的后一天 System.currentTimeMillis()-24*60*60*1000(一天的毫秒数)
		java.util.Date date = new java.util.Date(
				System.currentTimeMillis() + 86400000);
		// System.out.println("当前天:"+System.currentTimeMillis());
		// System.out.println("后一天:"+(System.currentTimeMillis()-86400000));
		java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(
				"yyyy-MM-dd");
		String time = sdf.format(date);
		System.out.println(time);
		return time;
	}

	/**
	 * 获取当前月的最后一天
	 * 
	 * @return
	 */
	public static String CatchLastDay() {
		Calendar cal = Calendar.getInstance();
		// 把日期设置为当月第一天
		cal.set(Calendar.DATE, 1);
		// 日期回滚一天，也就是最后一天
		cal.roll(Calendar.DATE, -1);
		// 获取当前月的天数
		int MaxDay = cal.get(Calendar.DATE);
		// 获取当前月份
		int nowMonth = cal.get(Calendar.MONTH) + 1;
		String NowMonth;
		if (nowMonth < 10) {
			NowMonth = "0" + nowMonth;
		} else {
			NowMonth = nowMonth + "";
		}
		// 获取当前年份
		int NowYear = cal.get(Calendar.YEAR);
		StringBuilder sb = new StringBuilder();
		// 进行拼接
		sb.append(NowYear).append("-").append(NowMonth).append("-").append(
				MaxDay);
		System.out.println(sb.toString());
		return sb.toString();
	}
	/**
	 * 显示时间
	 * @param time
	 * @return
	 */
	public static String showTime(long time) {
		long hour = time / (60 * 60 * 1000);
		long minute = (time - hour * 60 * 60 * 1000) / (60 * 1000);
		long second = (time - hour * 60 * 60 * 1000 - minute * 60 * 1000) / 1000;
		if (second >= 60) {
			second = second % 60;
			minute += second / 60;
		}
		if (minute >= 60) {
			minute = minute % 60;
			hour += minute / 60;
		}
		String ok = hour + "小时" + minute + "分" + second + "秒";
		return ok;
	}
	/**
	 * String 类型转换成java.util.Date类型
	 * @param string
	 * @return
	 * @throws Exception
	 */
	public static Date StringSwitchUDate(String string) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date time = sdf.parse(string);
		return time;
	}
}
