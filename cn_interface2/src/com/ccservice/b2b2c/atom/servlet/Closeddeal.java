package com.ccservice.b2b2c.atom.servlet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.job.Jobupdate12306station_name;
import com.ccservice.b2b2c.base.train.Trainorder;

public class Closeddeal implements Job {
    Log log = LogFactory.getLog(Closeddeal.class);

    @SuppressWarnings("deprecation")
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        log.error("定时关闭交易任务开始执行");
        Date preyesterday = new Date(System.currentTimeMillis());
        preyesterday.setDate(preyesterday.getDate() - 2);
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(preyesterday);
        String start = date + " 00:00:00";
        String end = date + " 23:59:59";
        // /机票
        // ///String sql="UPDATE T_ORDERINFO SET C_ORDERSTATUS=6 WHERE
        // C_ORDERSTATUS=1 AND C_CREATETIME BETWEEN '"+start+"' AND '"+end+"'";
        // //火车票
        String sql = "UPDATE T_TRAINORDER SET C_ORDERSTATUS=" + Trainorder.CANCLED + "  WHERE C_CREATETIME BETWEEN '"
                + start + "' AND  '" + end + "' AND C_ORDERSTATUS=" + Trainorder.WAITPAY;
        Server.getInstance().getSystemService().findMapResultBySql(sql, null);
        this.log.error(sql);
        //删除申请但未支付的行程单
        String singltripsql = "UPDATE T_PASSENGER SET C_FET=0  WHERE C_FET IN (SELECT ID  FROM T_SINGLEAPPLY  WHERE C_APPLYSTATE=1);";
        singltripsql += "DELETE  T_SINGLEAPPLY  WHERE C_APPLYSTATE=1";
        Server.getInstance().getSystemService().findMapResultBySql(singltripsql, null);
        update12306station();
    }

    /**
     * 更新12306车站
     * 
     * @time 2015年3月10日 下午12:28:28
     * @author chendong
     */
    public void update12306station() {
        Jobupdate12306station_name jobupdate12306station_name = new Jobupdate12306station_name();
        jobupdate12306station_name.execute();
    }

}
