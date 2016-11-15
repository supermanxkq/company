package com.ccservice.b2b2c.atom.servlet.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.component.SendPostandGet;

/**
 * 初始化用户的查询次数及最大查询次数/每天
 * 
 * @time 2015年5月21日 上午10:02:29
 * @author baiyushan
 */

public class UpdateInitTrainnum implements Job {
    //请求地址
    public static String TRAINSEARCH_URL;

    //请求地址的类型
    public static int inittype = 1;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {

    }

    /**
     * 初始化次数
     * @time 2015年5月21日 上午10:05:30
     * @author baiyushan
     */

    private static void initdata(int inittype) {
        String resultString = SendPostandGet.submitPost(TRAINSEARCH_URL, "initdata=" + inittype, "utf-8").toString();
        System.out.println(resultString);
    }

}
