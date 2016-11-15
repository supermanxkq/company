package com.alipay.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.traderecord.Traderecord;

public class UtilDate {

	public UtilDate() {
	}

	public static String getOrderNum(String ordernumber) {

		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
		return ordernumber + df.format(date);
	}

	public static String getDateFormatter() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(date);
	}

	public static String getDate() {
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		return df.format(date);
	}

	public static String getThree() {
		Random rad = new Random();
		return (new StringBuilder(String.valueOf(rad.nextInt(1000))))
				.toString();
	}
}
