package com.ccservice.b2b2c.util;

import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import com.ccservice.b2b2c.atom.component.WriteLog;

/**
 * 处理异常类
 * @author fiend
 *
 */
public class ExceptionUtil {
    /**
     * 书写对应异常的操作记录
     * @param logname
     * @param e
     * @author fiend
     */
    public static void writelogByException(String logname, Exception e) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            e.printStackTrace(ps);
            WriteLog.write(logname, baos.toString());
        }
        catch (Exception ex) {

        }
    }

    /**
     * 升级版 cd 2015年12月9日12:44:11
     * 书写对应异常的操作记录
     * @param logname
     * @param e
     * @param errorInfo 关联信息
     * @author fiend
     */
    public static void writelogByException(String logname, Exception e, String errorInfo) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            e.printStackTrace(ps);
            WriteLog.write(logname, "[" + errorInfo + "]" + baos.toString());
        }
        catch (Exception ex) {

        }
    }
}
