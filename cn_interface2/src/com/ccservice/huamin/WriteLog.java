package com.ccservice.huamin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Calendar;

public class WriteLog {

	/**д��־<br>

	* дlogString�ַ�./logĿ¼�µ��ļ���

	* @param logString ��־�ַ�

	* @author tower

	*/

	public static void write(String fileNameHead,String logString) {

	try {

	String logFilePathName=null;

	Calendar cd = Calendar.getInstance();//��־�ļ�ʱ��

	int year=cd.get(Calendar.YEAR);

	String month=addZero(cd.get(Calendar.MONTH)+1);

	String day=addZero(cd.get(Calendar.DAY_OF_MONTH));

	String hour=addZero(cd.get(Calendar.HOUR_OF_DAY));

	String min=addZero(cd.get(Calendar.MINUTE));

	String sec=addZero(cd.get(Calendar.SECOND));

	 

	 

	File fileParentDir=new File("D:/hotellog");//�ж�logĿ¼�Ƿ����

	if (!fileParentDir.exists()) {

	fileParentDir.mkdir();

	}

	if (fileNameHead==null||fileNameHead.equals("")) {

	logFilePathName="D:/hotellog/"+year+month+day+".log";//��־�ļ���

	}else {

	logFilePathName="D:/hotellog/"+fileNameHead+year+month+day+".log";//��־�ļ���

	}

	 

	PrintWriter printWriter=new PrintWriter(new FileOutputStream(logFilePathName, true));//�����ļ�βд����־�ַ�

	String time="["+year+"-"+month+"-"+day+" "+hour+":"+min+":"+sec+"] ";

	printWriter.println(time+logString);

	printWriter.flush();

	 

	} catch (FileNotFoundException e) {

	// TODO Auto-generated catch block

	e.getMessage();

	}

	}

	 

	/**����iС��10��ǰ�油0

	* @param i

	* @return

	* @author tower

	*/

	public static String addZero(int i) {

	if (i<10) {

	String tmpString="0"+i;

	return tmpString;

	}

	else {

	return String.valueOf(i);

	}  

	}

	public static void main(String[] args) {

	 

	//ǰ�����ļ�����,����������

	write("121212", "4444");
	write("121212", "5555");
	write("ok", "111");

	}

	}

