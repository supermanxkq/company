package com.ccservice.b2b2c.atom.servlet.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.job.thread.DeletePassengerThread;
import com.ccservice.b2b2c.util.FileUtils;
import com.ccservice.elong.inter.PropertyUtil;

public class JobDeletePassenger implements Job {

    private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    //    private long sleeptime = 1000l;
    //
    //    private String enDeleteAblePassengerMin = "25";
    //
    //    private int deleptebegin = 0;
    private static long sleeptime = 2000l;

    private static String enDeleteAblePassengerMin = "10";

    private static int deleptebegin = 0;

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {/*
                                                                                while (true) {
                                                                                System.out.println("begin");
                                                                                if (isNight()) {
                                                                                break;
                                                                                }
                                                                                int delepteend = this.deleptebegin;
                                                                                try {
                                                                                String deleptebeginString = FileUtils.readFileUTF8("D://Delete.txt");
                                                                                deleptebegin = Integer.valueOf(deleptebeginString);
                                                                                delepteend = Integer.valueOf(deleptebeginString);
                                                                                if (deleptebegin == 0) {
                                                                                break;
                                                                                }
                                                                                }
                                                                                catch (Exception e1) {
                                                                                e1.printStackTrace();
                                                                                }
                                                                                String url = PropertyUtil.getValue("delete_rep_url", "Train.properties");
                                                                                String enDeleteAblePassengerMin_Str = PropertyUtil.getValue("enDeleteAblePassengerMin", "Train.properties");
                                                                                if (!"".equals(enDeleteAblePassengerMin_Str)) {
                                                                                this.enDeleteAblePassengerMin = enDeleteAblePassengerMin_Str;
                                                                                }
                                                                                String sleeptimeStr = PropertyUtil.getValue("sleeptimeStr", "Train.properties");
                                                                                if (!"".equals(sleeptimeStr)) {
                                                                                this.sleeptime = Long.valueOf(sleeptimeStr);
                                                                                }
                                                                                String sql = "select top 200 ID, C_LOGINNAME, C_LOGPASSWORD from T_CUSTOMERUSER with(nolock) "
                                                                                + "where C_TYPE = 4 and C_ISENABLE = 1 and C_LOGINNUM >= 90 and ID>" + deleptebegin
                                                                                + " order by ID";
                                                                                List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
                                                                                System.out.println(list.size());
                                                                                if (list.size() == 0) {
                                                                                break;
                                                                                }
                                                                                for (int i = 0; i < list.size(); i++) {
                                                                                if (isNight()) {
                                                                                break;
                                                                                }
                                                                                if (i > 0) {
                                                                                try {
                                                                                System.out.println("sleep:" + sleeptime);
                                                                                Thread.sleep(sleeptime);
                                                                                }
                                                                                catch (InterruptedException e) {
                                                                                e.printStackTrace();
                                                                                }
                                                                                }
                                                                                Map map = (Map) list.get(i);
                                                                                String ID = map.get("ID").toString();
                                                                                String C_LOGINNAME = map.get("C_LOGINNAME").toString();
                                                                                String C_LOGPASSWORD = map.get("C_LOGPASSWORD").toString();
                                                                                System.out.println("线程启动：" + ID);
                                                                                ExecutorService pool = Executors.newFixedThreadPool(1);
                                                                                Thread t1 = new DeletePassengerThread(ID, url, C_LOGINNAME, C_LOGPASSWORD, enDeleteAblePassengerMin);
                                                                                pool.execute(t1);
                                                                                pool.shutdown();
                                                                                delepteend = Integer.valueOf(ID);
                                                                                }
                                                                                try {
                                                                                if (delepteend > 0) {
                                                                                System.out.println("结束：" + delepteend);
                                                                                FileUtils.writeFileUTF8(delepteend + "", "D://Delete.txt");
                                                                                }
                                                                                }
                                                                                catch (Exception e) {
                                                                                e.printStackTrace();
                                                                                }
                                                                                }
                                                                                */
    }

    //判断当前时间23:30至07:00
    private static boolean isNight() {
        boolean isNight = false;
        try {
            //当前
            Date current = sdf.parse(sdf.format(new Date()));
            //23:30 - 07:00
            Date start = sdf.parse("23:00:00");
            Date end = sdf.parse("07:30:00");
            //判断
            if (current.after(start) || current.before(end)) {
                isNight = true;
            }
        }
        catch (Exception e) {

        }
        return isNight;
    }

    @SuppressWarnings("rawtypes")
    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.println("begin");
            if (isNight()) {
                try {
                    Thread.sleep(30000L);
                }
                catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                continue;
            }
            int delepteend = deleptebegin;
            try {
                String deleptebeginString = FileUtils.readFileUTF8("D://Delete.txt");
                deleptebegin = Integer.valueOf(deleptebeginString);
                delepteend = Integer.valueOf(deleptebeginString);
                if (deleptebegin == 0) {
                    break;
                }
            }
            catch (Exception e1) {
                e1.printStackTrace();
            }
            String url = PropertyUtil.getValue("delete_rep_url", "Train.properties");
            String enDeleteAblePassengerMin_Str = PropertyUtil.getValue("enDeleteAblePassengerMin", "Train.properties");
            if (!"".equals(enDeleteAblePassengerMin_Str)) {
                enDeleteAblePassengerMin = enDeleteAblePassengerMin_Str;
            }
            String sleeptimeStr = PropertyUtil.getValue("sleeptimeStr", "Train.properties");
            if (!"".equals(sleeptimeStr)) {
                sleeptime = Long.valueOf(sleeptimeStr);
            }
            String sql = "select top 200 ID, C_LOGINNAME, C_LOGPASSWORD from T_CUSTOMERUSER with(nolock) "
                    + "where C_TYPE = 4 and C_ISENABLE = 1 and C_LOGINNUM >= 90 and ID>" + deleptebegin
                    + " order by ID";
            List list = Server.getInstance().getSystemService().findMapResultBySql(sql, null);
            System.out.println(list.size());
            if (list.size() == 0) {
                break;
            }
            for (int i = 0; i < list.size(); i++) {
                if (isNight()) {
                    break;
                }
                if (i > 0) {
                    try {
                        System.out.println("sleep:" + sleeptime);
                        Thread.sleep(sleeptime);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Map map = (Map) list.get(i);
                String ID = map.get("ID").toString();
                String C_LOGINNAME = map.get("C_LOGINNAME").toString();
                String C_LOGPASSWORD = map.get("C_LOGPASSWORD").toString();
                System.out.println("线程启动：" + ID);
                ExecutorService pool = Executors.newFixedThreadPool(1);
                Thread t1 = new DeletePassengerThread(ID, url, C_LOGINNAME, C_LOGPASSWORD, enDeleteAblePassengerMin);
                pool.execute(t1);
                pool.shutdown();
                delepteend = Integer.valueOf(ID);
            }
            try {
                if (delepteend > 0) {
                    System.out.println("结束：" + delepteend);
                    FileUtils.writeFileUTF8(delepteend + "", "D://Delete.txt");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
