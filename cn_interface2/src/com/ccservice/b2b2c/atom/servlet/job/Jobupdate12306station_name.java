package com.ccservice.b2b2c.atom.servlet.job;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import com.ccservice.b2b2c.atom.component.PublicComponent;
import com.ccservice.b2b2c.atom.component.util.CCSGetMethod;
import com.ccservice.b2b2c.atom.component.util.CCSHttpClient;
import com.ccservice.b2b2c.atom.component.util.MySSLProtocolSocketFactory;
import com.ccservice.b2b2c.atom.hotel.WriteLog;
import com.ccservice.b2b2c.atom.service12306.Train12306StationInfoUtilDBHelper;
import com.ccservice.b2b2c.util.FileUtils;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;

/**
 * 定时更新12306的站点数据
 * 
 * @time 2015年3月1日 下午4:37:06
 * @author chendong
 */
public class Jobupdate12306station_name extends PublicComponent implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        execute();
    }

    //job  为12点执行一次
    public static void main(String[] args) {
        try {
            startScheduler("00 21 * ? * *");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @time 2016年1月6日 下午7:22:14
     * @author chendong
     */
    private void updateStationName(String allStation) {
        String nameString = allStation;
        if (allStation != null && (!"".equals(allStation))) {
            String[] nameStrings = nameString.split("@");
            for (int i = 0; i < nameStrings.length; i++) {
                String name = nameStrings[i];
                String[] names = name.split("[|]");
                String sql = "SELECT top 1 * FROM StationName with(nolock) where Scode='" + names[2] + "'";
                DataTable dataTable = Train12306StationInfoUtilDBHelper.GetDataTable(sql);
                List<DataRow> row = dataTable.GetRow();
                String updateSql = "";
                if (row.size() > 0) {
                    updateSql = "update StationName set Jp='" + names[0] + "',SName='" + names[1] + "',Qp='" + names[3]
                            + "',Sindex='" + names[5] + "',Jp1='" + names[4] + "' where Scode='" + names[2] + "'";
                }
                else {
                    updateSql = "INSERT INTO StationName ([Jp],[SName],[Qp],[Scode],[Jp1],[Sindex]) VALUES ('"
                            + names[0] + "','" + names[1] + "','" + names[3] + "','" + names[2] + "','" + names[4]
                            + "','" + names[5] + "')";
                }
                //System.out.println(updateSql);
                boolean falg = Train12306StationInfoUtilDBHelper.executeSql(updateSql);
                WriteLog.write("Jobupdate12306station_name", falg + " : " + updateSql);
                //System.out.println(names[0] + ":" + names[1] + ":" + names[2] + ":" + names[3] + ":" + names[4] + ":"
                //        + names[5] + "-->" + row.size());
            }
        }
    }

    public void execute() {
        updateStationName(get12306station_namedata()); //更新update
    }

    /**
     *
     * @param data 12306最新车站数据
     * @param TrainStationNames_path 准备更新的文件地址
     * @throws Exception
     * @time 2015年3月10日 上午11:31:05
     * @author chendong
     */
    private void updatestation_name(String data, String TrainStationNames_path) throws Exception {
        String filedata = FileUtils.readFile(TrainStationNames_path, "UTF-8");
        System.out.println(data);
        System.out.println(TrainStationNames_path);
        System.out.println(data.length() + ":" + filedata.length() + ":" + (data.length() > filedata.length()));
        if (data.length() > filedata.length()) {
            FileUtils.writeFileUTF8(data, TrainStationNames_path);
        }
    }

    /**
     * 获取到12306城市的js数据
     * @time 2015年3月1日 下午3:30:40
     * @author chendong
     */
    private static String get12306station_namedata() {
        String htmlString = "-1";
        CCSGetMethod get = null;
        CCSHttpClient httpClient = new CCSHttpClient(false, 60000L);
        get = new CCSGetMethod("https://kyfw.12306.cn/otn/leftTicket/init");
        Protocol myhttps = new Protocol("https", new MySSLProtocolSocketFactory(), 443);
        Protocol.registerProtocol("https", myhttps);
        get.setFollowRedirects(false);
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        try {
            httpClient.executeMethod(get);
            htmlString = get.getResponseBodyAsString();
        }
        catch (HttpException e) {
            //            e.printStackTrace();
        }
        catch (IOException e) {
            //            e.printStackTrace();
        }
        String tempString = "station_name.js?station_version=";
        int s = htmlString.indexOf(tempString) + tempString.length();
        int e = s + 10;
        htmlString = htmlString.substring(s, e);
        htmlString = htmlString.split(" ")[0].replace("\"", "");
        String jsurl = "https://kyfw.12306.cn/otn/resources/js/framework/station_name.js?station_version=" + htmlString;
        get = new CCSGetMethod(jsurl);
        get.setFollowRedirects(false);
        httpClient.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
        try {
            httpClient.executeMethod(get);
            htmlString = get.getResponseBodyAsString();
        }
        catch (HttpException e1) {
        }
        catch (IOException e1) {
        }
        htmlString = htmlString.substring(htmlString.indexOf("='@") + 3).replace("';", "");
        return htmlString;
    }

    /**
     * 读取文件并分析
     * @time 2015年3月10日 上午10:57:01
     * @author chendong
     */
    private static void readtongbulog() {
        //        execute();
        for (int i = 1; i < 19; i++) {
            try {
                File file = new File("X:/TT/t同程火车票接口_4.5申请分配座位席别 (" + i + ").log");
                FileInputStream fis = new FileInputStream(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis, "GBK"));
                String fileContent = "";
                String temp = "";
                while ((temp = br.readLine()) != null) {
                    if (temp.indexOf(":时间:") > 0 && temp.indexOf("TC_") > 0) {
                        System.out.println(temp);
                    }
                }
                br.close();
                fis.close();
            }
            catch (Exception e) {
            }
        }

    }

    // 设置调度任务及触发器，任务名及触发器名可用来停止任务或修改任务
    public static void startScheduler(String expr) throws Exception {
        JobDetail jobDetail = new JobDetail("Jobupdate12306station_name", "Jobupdate12306station_nameGroup",
                Jobupdate12306station_name.class);// 任务名，任务组名，任务执行类
        CronTrigger trigger = new CronTrigger("Jobupdate12306station_name", "Jobupdate12306station_nameGroup", expr);// 触发器名,触发器组名
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
    }

}
