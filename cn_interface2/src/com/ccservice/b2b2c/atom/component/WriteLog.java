package com.ccservice.b2b2c.atom.component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Calendar;

public class WriteLog {

    /**
     * 写日志<br>
     * 
     * 写logString字符串到./log目录下的文件中
     * 
     * @param logString
     *            日志字符串
     * 
     * @author tower
     * 
     */

    public static void write(String fileNameHead, String logString) {
        PrintWriter printWriter = null;
        try {

            String logFilePathName = null;

            Calendar cd = Calendar.getInstance();// 日志文件时间

            int year = cd.get(Calendar.YEAR);

            String month = addZero(cd.get(Calendar.MONTH) + 1);

            String day = addZero(cd.get(Calendar.DAY_OF_MONTH));

            String hour = addZero(cd.get(Calendar.HOUR_OF_DAY));

            String min = addZero(cd.get(Calendar.MINUTE));

            String sec = addZero(cd.get(Calendar.SECOND));
            String mill = addZero(cd.get(Calendar.MILLISECOND));
            String path = "D:/userlog/" + year + month + "/" + day + "/" + hour;
            File fileParentDir = new File(path);// 判断log目录是否存在

            if (!fileParentDir.exists()) {

                fileParentDir.mkdirs();

            }

            if (fileNameHead == null || fileNameHead.equals("")) {

                logFilePathName = path + "/" + year + month + day + ".log";// 日志文件名

            }
            else {

                logFilePathName = path + "/" + fileNameHead + ".log";// 日志文件名

            }

            printWriter = new PrintWriter(new FileOutputStream(logFilePathName, true));// 紧接文件尾写入日志字符串

            String time = "[" + year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec + "." + mill + "] ";
            printWriter.println(time + logString);
        }
        catch (FileNotFoundException e) {
            e.getMessage();
        }
        finally {
            if (printWriter != null) {
                printWriter.close();
                printWriter.flush();
            }
        }

    }

    /**
     * 整数i小于10则前面补0
     * 
     * @param i
     * 
     * @return
     * 
     * @author tower
     * 
     */

    public static String addZero(int i) {

        if (i < 10) {

            String tmpString = "0" + i;

            return tmpString;

        }

        else {

            return String.valueOf(i);

        }

    }

    public static void main(String[] args) {
        // 前面是文件名字,后面是内容
        write("test", "陈栋");
    }

}
