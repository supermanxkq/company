package com.ccservice.b2b2c.atom.pay.helper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

public abstract class Payhelperframework implements Payhelper {	
	
	
	
	public Payhelperframework(long orderid){
		
	}
//	public String getNotifyurl(){
//		
//		if(paytype==1){//支付宝notify
//		return servername+"AlipayNotifyHandle";//此处与web.xml中配置请一致
//		}
//		if(paytype==2){//快钱pageurl
//			return servername+"BillpayNotifyHandle";
//		}
//		if(paytype==3){//汇付天下
//			return servername+"ChinapnrNofity";
//		}
//		return "";
//	}
	
	public String getReturnurl(){
//		if(type==1){//支付宝returnurl
//		return notifyurl+"AlipayReturnHandle";
//		}
//		if(type==2){//快钱bgurl
//			return "";
//		}
		return "";
	}
	
	protected <T> T convertNull(T val, T retval) {
		if (val == null) {
			return retval;
		} else {
			return val;
		}
	}

	/**
	 * 从map转换为对象
	 * 
	 * @param <T>
	 * @param t
	 * @param map
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws NoSuchFieldException
	 */
	public <T> T setFiledfrommap(Class t, Map map) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException, NoSuchFieldException {
		Iterator<Map.Entry<String, String>> iterator = map.entrySet()
				.iterator();
		T tt = (T) t.newInstance();
		System.out.println(map.size());
		for (Map.Entry<String, String> entry = null; iterator.hasNext();) {
			entry = iterator.next();
			String paraname = entry.getKey();
			Object val = entry.getValue();
			paraname = paraname.substring(0, 1).toUpperCase()
					+ paraname.substring(1);
			Method getm = t.getMethod("get" + paraname, null);
			String type = getm.getReturnType().getSimpleName();
			if (type.equals("Integer") || type.equals("int")) {
				val = Integer.valueOf(val.toString());
			}
			if (type.equals("Long") || type.equals("long")) {
				val = Long.valueOf(converNull(val,'0').toString());
			}
			if (type.equals("Float") || type.equals("float")) {
				val = Float.valueOf(val.toString());
			}
			Method method = t.getMethod("set" + paraname, getm.getReturnType());

			method.invoke(tt, val);
		}
		return tt;
	}

	/**
	 * 转换null
	 * 
	 * @param <T>
	 * @param t
	 * @param v
	 * @return
	 */
	public <T> T converNull(T t, T v) {
		if (t != null) {
			return t;
		}
		return v;
	}

	private SimpleDateFormat minuteformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	public String formatTimestamptoMinute(Timestamp date) {
		try {
			return (minuteformat.format(date));

		} catch (Exception e) {
			return "";
		}

	}

}
