package com.ccservice.b2b2c.atom.servlet;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.PhoneChangeWebThread;
import com.ccservice.b2b2c.atom.service12306.bean.RepServerBean;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.job.OperateApplyOfflineTicketJob;
import com.ccservice.b2b2c.atom.service12306.offlineRefund.job.RefuseApplyOfflineTicketJob;
import com.ccservice.b2b2c.atom.servlet.job.JobDeletePassenger;
import com.ccservice.b2b2c.atom.servlet.job.JobTaobaoChangeOrdersByList;
import com.ccservice.b2b2c.atom.servlet.job.JobTaobaoRefund;
import com.ccservice.b2b2c.atom.servlet.job.JobTaobaoTrainOrdersByList;
import com.ccservice.b2b2c.atom.servlet.job.JobTaobaoTrainPrice;
import com.ccservice.b2b2c.atom.servlet.job.JobUpdate12306TrainDetail;
import com.ccservice.b2b2c.atom.servlet.job.JobUpdate12306TrainLicence;
import com.ccservice.b2b2c.atom.servlet.job.JobUpdateInitTrainSearchNum;
import com.ccservice.b2b2c.atom.servlet.job.JobUpdateTrainOrderOfflineAvg;
import com.ccservice.b2b2c.atom.servlet.job.Jobupdate12306price;
import com.ccservice.b2b2c.atom.servlet.job.Jobupdate12306station_name;
import com.ccservice.b2b2c.atom.servlet.job.Jobupdate12306trainnodata;
import com.ccservice.b2b2c.base.eaccount.Eaccount;
import com.ccservice.elong.inter.PropertyUtil;
import com.qunarprice.QunarCookieJob;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.paiduiChange.TrainChangeOrderPaiDuiJob;
import com.ccservice.b2b2c.atom.servlet.account.method.deletePassenger.DeletPassengers;

/**
 * Servlet implementation class for Servlet: InitServlet
 */

@SuppressWarnings("serial")
public class InitServlet extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {
    Log log = LogFactory.getLog(InitServlet.class);

    private Scheduler scheduler;

    public InitServlet() {
        super();
    }

    public void init() throws ServletException {
        super.init();
        System.out.println("商旅中心接口服务初始化开始...");
        Server.getInstance().setUrl(this.getInitParameter("service_url"));
        Server.getInstance().setUrlAtom(this.getInitParameter("atom_service_url"));
        Server.getInstance().setInter(this.getInitParameter("inter_service_url"));
        Server.getInstance().setInter_yeeurl(this.getInitParameter("inter_yeeurl"));
        Server.getInstance().setCustomercd(this.getInitParameter("customercd"));
        Server.getInstance().setAuthno(this.getInitParameter("authno"));
        Server.getInstance().setElongMiddleUrl(this.getInitParameter("ElongMiddleUrl"));
        Server.getInstance().setElongUserName(this.getInitParameter("ElongUserName"));
        Server.getInstance().setElongAppKey(this.getInitParameter("ElongAppKey"));
        Server.getInstance().setElongSecretKey(this.getInitParameter("ElongSecretKey"));
        Server.getInstance().setTaoBaoServerUrl(this.getInitParameter("TaoBaoServerUrl"));
        Server.getInstance().setTaoBaoAppKey(this.getInitParameter("TaoBaoAppKey"));
        Server.getInstance().setTaoBaoAppSecret(this.getInitParameter("TaoBaoAppSecret"));
        Server.getInstance().setTaoBaoCallBackUrl(this.getInitParameter("TaoBaoCallBackUrl"));
        Server.getInstance().setSuccessRedirectUrl(this.getInitParameter("SuccessRedirectUrl"));
        Server.getInstance().setJLUrl(this.getInitParameter("jl_url"));
        Server.getInstance().setTravelsky_Address(this.getInitParameter("Travelsky_Address"));
        Server.getInstance().setTravelsky_OfficeID(this.getInitParameter("Travelsky_OfficeID"));
        Server.getInstance().setTravelsky_UserID(this.getInitParameter("Travelsky_UserID"));
        Server.getInstance().setTravelsky_Password(this.getInitParameter("Travelsky_Password"));
        Server.getInstance().setDateHashMap(new HashMap<String, String>());

        // REP
        Server.getInstance().setDamaRepIdx(0);
        Server.getInstance().setTrainRepIdx(0);
        Server.getInstance().setRepServers(new ArrayList<RepServerBean>());
        try {
            Eaccount sunshineInsuranceEaccount = new Eaccount();
            sunshineInsuranceEaccount.setUsername(this.getInitParameter("sunshineInsurance_userId"));
            sunshineInsuranceEaccount.setKeystr(this.getInitParameter("sunshineInsurance_sign"));
            sunshineInsuranceEaccount.setEdesc(this.getInitParameter("sunshineInsurance_clientType"));
            sunshineInsuranceEaccount.setNourl(this.getInitParameter("sunshineInsurance_callbackurl"));

            Server.getInstance().setSunshineInsuranceEaccount(sunshineInsuranceEaccount);
        }
        catch (Exception e) {
            System.out.println("商旅中心接口服务初始化阳光保险异常");
            this.log.error("商旅中心接口服务初始化阳光保险异常:", e.fillInStackTrace());
        }
        System.out.println("商旅中心接口服务初始化结束.");

        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
        }
        catch (SchedulerException e) {
            e.printStackTrace();
        }

        /**** 定时关闭未支付的火车票订单 ***/
        try {
            if ("1".equals(this.getInitParameter("iscloseTrainOrder"))) {
                JobDetail itneraryjob = new JobDetail("updRechstatejob", Scheduler.DEFAULT_GROUP, Closeddeal.class);
                scheduler.scheduleJob(itneraryjob, new CronTrigger("closeddeal_trigger", "closeddeal_grop",
                        "0 0 0 * * ?"));// 每天0点触发一次
                // scheduler.start();
            }
            String update12306station_name = PropertyUtil.getValue("update12306station_name", "Train.properties");
            if ("1".equals(update12306station_name)) {
                System.out.println("=====cn_interface,定时更新12306车站简码数据=====启用");
                JobDetail Jobupdate12306station_namejob = new JobDetail("update12306station_namejob",
                        Scheduler.DEFAULT_GROUP, Jobupdate12306station_name.class);
                scheduler.scheduleJob(Jobupdate12306station_namejob, new CronTrigger(
                        "Jobupdate12306station_name_trigger", "Jobupdate12306station_name_grop", "0 0 0 * * ?"));// 每天0点触发一次
                // scheduler.start();
            }
            else {
                //System.out.println("=====cn_interface,定时更新12306车站简码数据=====禁用");
            }
            /**
             * 定时初始化查询火车的次数 每天晚上12：00
             */

            String jobUpdateInitTrainSearchNum_trainSearchInitData_data = PropertyUtil.getValue(
                    "jobUpdateInitTrainSearchNum_trainSearchInitData_data", "Train.properties");
            if ("1".equals(jobUpdateInitTrainSearchNum_trainSearchInitData_data)) {
                System.out.println("=====cn_interface,定时初始化查询火车的次数=========启用");
                JobDetail JobUpdateInitTrainSearchNum_nameJob = new JobDetail("JobUpdateInitTrainSearchNum",
                        Scheduler.DEFAULT_GROUP, JobUpdateInitTrainSearchNum.class);
                scheduler.scheduleJob(JobUpdateInitTrainSearchNum_nameJob, new CronTrigger(
                        "JobUpdateInitTrainSearchNum_name_trigger", "JobUpdateInitTrainSearchNum_name_grop",
                        "0 0 0 * * ?"));// 每天0点触发一次
                // scheduler.start();
            }
            else {
                //System.out.println("=====cn_interface,定时初始化查询火车的次数=========禁用");
            }

            String jobupdate12306trainnodata = PropertyUtil.getValue("jobupdate12306trainnodata", "Train.properties");
            String jobupdate12306trainnodata_cronExpression = PropertyUtil.getValue(
                    "jobupdate12306trainnodata_cronExpression", "Train.properties");
            if ("1".equals(jobupdate12306trainnodata)) {
                System.out.println("=====cn_interface,定时更新12306站站数据=========启用");
                JobDetail Jobupdate12306trainnodatajob = new JobDetail("Jobupdate12306trainnodatajob",
                        Scheduler.DEFAULT_GROUP, Jobupdate12306trainnodata.class);
                scheduler.scheduleJob(Jobupdate12306trainnodatajob, new CronTrigger(
                        "Jobupdate12306trainnodata_trigger", "Jobupdate12306trainnodata_grop",
                        jobupdate12306trainnodata_cronExpression));
                // scheduler.start();
            }
            else {
                //System.out.println("=====cn_interface,定时更新12306站站数据=========禁用");
            }
            String jobupdate12306price = PropertyUtil.getValue("jobupdate12306price", "Train.properties");
            String jobupdate12306price_cronExpression = PropertyUtil.getValue("jobupdate12306price_cronExpression",
                    "Train.properties");
            if ("1".equals(jobupdate12306price)) {
                System.out.println("=====cn_interface,定时更新12306价格数据=========启用");
                JobDetail jobupdate12306pricejob = new JobDetail("jobupdate12306pricejob", Scheduler.DEFAULT_GROUP,
                        Jobupdate12306price.class);
                scheduler.scheduleJob(jobupdate12306pricejob, new CronTrigger("jobupdate12306price_trigger",
                        "jobupdate12306price_grop", jobupdate12306price_cronExpression));
            }
            else {
                //System.out.println("=====cn_interface,定时更新12306价格数据=========禁用");
            }
        }
        catch (Exception e) {
            this.log.error("关闭为未支付订单定时任务异常：", e.fillInStackTrace());
        }
        /**** 定时拉取淘宝退票池中的订单 ***/
        try {
            if ("1".equals(this.getInitParameter("isgettaobaotrainorderbypool"))) {
                System.out.println("=====cn_interface,定时拉取淘宝退票池中的订单=====启用");
                JobDetail JobTaobaoRefundjob = new JobDetail("JobTaobaoRefundjob", Scheduler.DEFAULT_GROUP,
                        JobTaobaoRefund.class);
                scheduler.scheduleJob(JobTaobaoRefundjob, new CronTrigger("JobTaobaoRefund_trigger",
                        "JobTaobaoRefund_grop", "0 0/10 * * * ?"));// 每10分钟触发一次
                // scheduler.start();
            }
            else {
                //System.out.println("=====cn_interface,定时拉取淘宝退票池中的订单=====禁用");
            }
        }
        catch (Exception e) {
            this.log.error("定时拉取淘宝退票池中的订单定时任务异常：", e.fillInStackTrace());
        }
        /**** 定时给淘宝同步卧铺价格的订单 ***/
        try {
            if ("1".equals(this.getInitParameter("istaobaotrainprice"))) {
                String taobaotrainprice_begintime = PropertyUtil.getValue("taobaotrainprice_begintime",
                        "Train.properties");
                System.out.println("=====cn_interface,定时给淘宝同步卧铺价格=====启用");
                JobDetail JobTaobaoTrainPricejob = new JobDetail("JobTaobaoTrainPricejob", Scheduler.DEFAULT_GROUP,
                        JobTaobaoTrainPrice.class);
                scheduler.scheduleJob(JobTaobaoTrainPricejob, new CronTrigger("JobTaobaoTrainPrice_trigger",
                        "JobTaobaoTrainPrice_grop", taobaotrainprice_begintime));// 每天触发一次
                // scheduler.start();
            }
            else {
                //System.out.println("=====cn_interface,定时给淘宝同步卧铺价格=====禁用");
            }
        }
        catch (Exception e) {
            this.log.error("定时给淘宝同步卧铺价格任务异常：", e.fillInStackTrace());
        }
        /**** 定时拉取淘宝订单池中的订单 ***/
        try {
            if ("1".equals(this.getInitParameter("taobaotrainorderbylist"))) {
                String jobtaobaotrainordersbylist_begintime = PropertyUtil.getValue(
                        "jobtaobaotrainordersbylist_begintime", "Train.properties");
                System.out.println("=====cn_interface,定时拉取淘宝订单池中的订单=====启用");
                JobDetail JobTaobaoTrainOrdersByListjob = new JobDetail("JobTaobaoTrainOrdersByListjob",
                        Scheduler.DEFAULT_GROUP, JobTaobaoTrainOrdersByList.class);
                scheduler.scheduleJob(JobTaobaoTrainOrdersByListjob, new CronTrigger(
                        "JobTaobaoTrainOrdersByList_trigger", "JobTaobaoTrainOrdersByList_grop",
                        jobtaobaotrainordersbylist_begintime));
            }
            else {
                //System.out.println("=====cn_interface,定时拉取淘宝订单池中的订单=====禁用");
            }
        }
        catch (Exception e) {
            this.log.error("定时拉取淘宝订单池中的订单异常：", e.fillInStackTrace());
        }
        /**** 定时拉取淘宝改签池中的订单 ***/
        try {
            if ("1".equals(this.getInitParameter("taobaochangeordersbylist"))) {
                String jobtaobaochangeordersbylist_begintime = PropertyUtil.getValue(
                        "jobtaobaochangeordersbylist_begintime", "Train.properties");
                System.out.println("=====cn_interface,定时拉取淘宝改签池中的订单=====启用");
                JobDetail JobTaobaoChangeOrdersByListjob = new JobDetail("JobTaobaoChangeOrdersByListjob",
                        Scheduler.DEFAULT_GROUP, JobTaobaoChangeOrdersByList.class);
                scheduler.scheduleJob(JobTaobaoChangeOrdersByListjob, new CronTrigger(
                        "JobTaobaoTrainOrdersByList_trigger", "JobTaobaoChangeOrdersByList_grop",
                        jobtaobaochangeordersbylist_begintime));
            }
            else {
                //System.out.println("=====cn_interface,定时拉取淘宝改签池中的订单=====禁用");
            }
        }
        catch (Exception e) {
            this.log.error("定时拉取淘宝改签池中的订单异常：", e.fillInStackTrace());
        }
        /** 定时走改签排队**/
        try {
            //开启
            if ("1".equals(PropertyUtil.getValue("ChangeOrderPaiDuiJobOpen", "Train.ChangeOrder.properties"))) {
                //时间
                String time = PropertyUtil.getValue("ChangeOrderPaiDuiJobTime", "Train.ChangeOrder.properties");
                //JOB
                scheduler
                        .scheduleJob(new JobDetail("TrainChangeOrderPaiDuiJobDetail", TrainChangeOrderPaiDuiJob.class),
                                new CronTrigger("TrainChangeOrderPaiDuiCronTrigger",
                                        "TrainChangeOrderPaiDuiCronTrigger", time));
                //打印
                System.out.println("=====cn_interface，改签排队JOB=====启用");
            }
        }
        catch (Exception e) {
            this.log.error("cn_interface定时走改签排队异常：", e.fillInStackTrace());
        }

        /** 定时处理接口申请线下退票 **/
        try {
            //开启
            if ("1".equals(PropertyUtil.getValue("OperateApplyOfflineTicketJobOpen"))) {
                //时间
                String time = PropertyUtil.getValue("OperateApplyOfflineTicketJobTime");
                //JOB
                scheduler.scheduleJob(new JobDetail("OperateApplyOfflineTicketJobDetail",
                        OperateApplyOfflineTicketJob.class), new CronTrigger("OperateApplyOfflineTicketCronTrigger",
                        "OperateApplyOfflineTicketCronTrigger", time));
                //打印
                System.out.println("=====cn_interface，定时处理接口申请线下退票JOB=====启用");
            }
        }
        catch (Exception e) {
            this.log.error("cn_interface定时处理接口申请线下退票异常：", e.fillInStackTrace());
        }

        /** 定时拒绝接口申请线下退票 **/
        try {
            //开启
            if ("1".equals(PropertyUtil.getValue("RefuseApplyOfflineTicketJobOpen"))) {
                //时间
                String time = PropertyUtil.getValue("RefuseApplyOfflineTicketJobTime");
                //JOB
                scheduler.scheduleJob(new JobDetail("RefuseApplyOfflineTicketJobDetail",
                        RefuseApplyOfflineTicketJob.class), new CronTrigger("RefuseApplyOfflineTicketCronTrigger",
                        "RefuseApplyOfflineTicketCronTrigger", time));
                //打印
                System.out.println("=====cn_interface，定时拒绝接口申请线下退票JOB=====启用");
            }
        }
        catch (Exception e) {
            this.log.error("cn_interface定时拒绝接口申请线下退票异常：", e.fillInStackTrace());
        }

        /**** 定时去12306获取车次详情 ***/
        try {
            if ("1".equals(this.getInitParameter("update12306traindetail"))) {
                String jobupdate12306traindetail_begintime = PropertyUtil.getValue(
                        "jobupdate12306traindetail_begintime", "Train.properties");
                System.out.println("=====cn_interface,定时去12306获取车次详情=====启用");
                JobDetail JobUpdate12306TrainDetailjob = new JobDetail("JobUpdate12306TrainDetailjob",
                        Scheduler.DEFAULT_GROUP, JobUpdate12306TrainDetail.class);
                scheduler.scheduleJob(JobUpdate12306TrainDetailjob, new CronTrigger(
                        "JobUpdate12306TrainDetail_trigger", "JobUpdate12306TrainDetail_grop",
                        jobupdate12306traindetail_begintime));
            }
            else {
                //System.out.println("=====cn_interface,定时去12306获取车次详情=====禁用");
            }
        }
        catch (Exception e) {
            this.log.error("定时去12306获取车次详情异常：", e.fillInStackTrace());
        }
        /**** 定时去12306获取车次车号 ***/
        try {
            if ("1".equals(this.getInitParameter("update12306trainlicence"))) {
                String jobupdate12306trainlicence_begintime = PropertyUtil.getValue(
                        "jobupdate12306trainlicence_begintime", "Train.properties");
                System.out.println("=====cn_interface,定时去12306获取车次车号=====启用");
                JobDetail JobUpdate12306TrainLicencejob = new JobDetail("JobUpdate12306TrainLicencejob",
                        Scheduler.DEFAULT_GROUP, JobUpdate12306TrainLicence.class);
                scheduler.scheduleJob(JobUpdate12306TrainLicencejob, new CronTrigger(
                        "JobUpdate12306TrainLicence_trigger", "JobUpdate12306TrainLicence_grop",
                        jobupdate12306trainlicence_begintime));
            }
            else {
                //System.out.println("=====cn_interface,定时去12306获取车次车号=====禁用");
            }
        }
        catch (Exception e) {
            this.log.error("定时去12306获取车次车号异常：", e.fillInStackTrace());
        }

        /**** 定时删除12306中乘客 ***/
        try {
            if ("1".equals(this.getInitParameter("deletepassegners"))) {
                String JobDeletePassenger_begintime = PropertyUtil.getValue("JobDeletePassenger_begintime",
                        "Train.properties");
                System.out.println("=====cn_interface,定时删除12306中乘客=====启用");
                JobDetail JobDeletePassengerjob = new JobDetail("JobDeletePassengerjob", Scheduler.DEFAULT_GROUP,
                        JobDeletePassenger.class);
                scheduler.scheduleJob(JobDeletePassengerjob, new CronTrigger("JobDeletePassenger_trigger",
                        "JobDeletePassenger_grop", JobDeletePassenger_begintime));
            }
            else {
                //System.out.println("=====cn_interface,定时删除12306中乘客=====禁用");
            }
        }
        catch (Exception e) {
            this.log.error("定时去12306获取车次车号异常：", e.fillInStackTrace());
        }

        /** 定时获取去哪儿酒店价格Cookie */
        try {
            if ("1".equals(PropertyUtil.getValue("QunarVcodeJobOpen"))) {
                System.out.println("=====cn_interface，定时更新去哪儿酒店Cookie=====启用");
                String time = PropertyUtil.getValue("QunarVcodeTime");
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                CronTrigger QunarCookieValidCronTrigger = new CronTrigger("QunarCookieValidCronTrigger",
                        "QunarCookieValidCronTrigger", time);
                JobDetail QunarCookieValidJobDetail = new JobDetail("QunarCookieValidJobDetail", QunarCookieJob.class);
                scheduler.scheduleJob(QunarCookieValidJobDetail, QunarCookieValidCronTrigger);
            }
        }
        catch (Exception e) {
            System.out.println("定时更新去哪儿酒店Cookie，" + ElongHotelInterfaceUtil.errormsg(e));
        }
        /** 定时更新线下出票点出票时长效率 */
        try {
            if ("1".equals(PropertyUtil.getValue("UpdateTrainOrderOfflineAvgJobOpen"))) {
                System.out.println("=====cn_interface,定时更新线下火车票出票时长效率=====启用");
                String time = PropertyUtil.getValue("UpdateTrainOrderOfflineAvgTime");
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                CronTrigger UpdateTrainOrderOfflineAvgCronTrigger = new CronTrigger(
                        "UpdateTrainOrderOfflineAvgCronTrigger", "UpdateTrainOrderOfflineAvgCronTrigger", time);
                JobDetail TrainOrderOfflineAvgJobDetail = new JobDetail("TrainOrderOfflineAvgJobDetail",
                        JobUpdateTrainOrderOfflineAvg.class);
                scheduler.scheduleJob(TrainOrderOfflineAvgJobDetail, UpdateTrainOrderOfflineAvgCronTrigger);
            }
        }
        catch (Exception e) {
            System.out.println("定时更新线下火车票出票时长效率，" + ElongHotelInterfaceUtil.errormsg(e));
        }

        /**定时删除常旅*/
        try {
            if ("1".equals(PropertyUtil.getValue("UpdateDeletePassengerJobOpen"))) {
                System.out.println("=====cn_interface,定时删除常旅=====启用");
                String time = PropertyUtil.getValue("UpdateDeletePassengerJAvgTime");
                Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
                CronTrigger UpdateTrainOrderOfflineAvgCronTrigger = new CronTrigger("UpdateDeletePassengerJobOpen",
                        "UpdateDeletePassengerJobOpenTrigger", time);
                JobDetail TrainOrderOfflineAvgJobDetail = new JobDetail("UpdateDeletePassengerJobOpen",
                        DeletPassengers.class);
                scheduler.scheduleJob(TrainOrderOfflineAvgJobDetail, UpdateTrainOrderOfflineAvgCronTrigger);
            }
        }
        catch (Exception e) {
            System.out.println("定时删除常旅，" + ElongHotelInterfaceUtil.errormsg(e));
        }

        try {
            scheduler.start();
        }
        catch (SchedulerException e) {
            e.printStackTrace();
        }

        Server.getInstance().setMQurl(this.getInitParameter("MQurl"));
        Server.getInstance().setMQusername(this.getInitParameter("MQusername"));
        getSZMTask();//三字码刷新定时任务
    }

    public void destroy() {
        super.destroy();
        try {
            scheduler.shutdown();
        }
        catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public String getstaus(String staus, String infoString) {
        if ("0".equals(staus)) {
            return "禁用";
        }
        else if ("1".equals(staus)) {
            System.out.println(infoString + ":启用");
            return "启用";
        }
        else {
            return "空";
        }
    }

    //三字码定时任务
    public void getSZMTask() {
        try {
            Jobupdate12306station_name.startScheduler("00 00 00 ? * *");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}