/**
 * 
 */
package com.ccservice.b2b2c.atom.servlet.job.air;

import java.util.List;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.impl.StdSchedulerFactory;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service12306.TimeUtil;
import com.ccservice.b2b2c.util.db.DBHelper;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 扫描未使用客票的客票
 * @time 2015年11月24日 下午1:14:29
 * @author chendong
 */
public class JobScanAirOpenTicket implements Job {
    public static void main(String[] args) {
        try {
            //            JobScanAirOpenTicket.startScheduler("0/1 0/1 7-23 * * ?");
            JobScanAirOpenTicket jobScanAirOpenTicket = new JobScanAirOpenTicket();
            jobScanAirOpenTicket.execute();
            //            int results = Server.getInstance().getAtomService()
            //                    .sendSimpleMails(new String[] { "249016428@qq.com" }, TimeUtil.gettodaydate(1) + ":退票的票号", "我好啊!");
            //            System.out.println(results);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
     */
    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
//        System.out.println(System.currentTimeMillis());
        execute();
    }

    /**
     * 
     * @time 2015年11月24日 下午2:16:52
     * @author chendong
     */
    private void execute() {
        StringBuffer stringBuffer_result = new StringBuffer();
        DataTable dataTable = DBHelper.GetDataTable("EXEC sp_T_PASSENGER_getAllTicket");
        String officeID = PropertyUtil.getValue("JobScanAirOpenTicketOfficeID", "air.properties");
        List<DataRow> row = dataTable.GetRow();
        if (row.size() > 0) {
            for (int i = 0; i < row.size(); i++) {
                DataRow dataRow = row.get(i);
                String ticket_NO = dataRow.GetColumnString("C_TICKETNUM");
                String C_NAME = dataRow.GetColumnString("C_NAME");
                String C_IDNUMBER = dataRow.GetColumnString("C_IDNUMBER");
                JobScanAirOpenTicketMethod jobScanAirOpenTicketMethod = new JobScanAirOpenTicketMethod(i, ticket_NO,
                        officeID);
                //                0000,BUDEQIANZHUANTUIPIAO不得签转退票/BIANGENGSHOUFEI变更收费,聂文忠,HV3TK3|,CGQ,HAK,|,CZ,6652,Z,25NOV,1625,Z,25NOV,25NOV,20,USED/FLOWN,,,,,784-2148531615
                String result = jobScanAirOpenTicketMethod.check_open_ticket();
                System.out.println(i + "-[" + C_NAME + "(" + C_IDNUMBER + ")" + "]>" + ticket_NO + "-" + result);
                if (result.contains("USED")) {
                }
                else if (result.contains("票号或乘机人不存在")) {
                }
                else {
                    stringBuffer_result.append(ticket_NO + ":" + result + "\r\n");
                    System.out.println(ticket_NO + ":" + result);
                }
            }
        }
        else {
            System.out.println("没有找到票号");
        }
        System.out.println("=================");
        //        System.out.println(stringBuffer_result.toString());
        WriteLog.write("JobScanAirOpenTicket", stringBuffer_result.toString());
        String JobScanAirOpenTicketOfficeID_email = PropertyUtil.getValue("JobScanAirOpenTicketOfficeID_email",
                "air.properties");
        String[] mails = JobScanAirOpenTicketOfficeID_email.split(",");
        Server.getInstance().getAtomService()
                .sendSimpleMails(mails, TimeUtil.gettodaydate(1) + ":需要处理的机票退票的票号", stringBuffer_result.toString());
    }

    // 设置调度任务及触发器，任务名及触发器名可用来停止任务或修改任务
    public static void startScheduler(String expr) throws Exception {
        JobDetail jobDetail = new JobDetail("JobScanAirOpenTicket", "JobScanAirOpenTicketGroup",
                JobScanAirOpenTicket.class);// 任务名，任务组名，任务执行类
        CronTrigger trigger = new CronTrigger("JobScanAirOpenTicket", "JobScanAirOpenTicketGroup", expr);// 触发器名，触发器组名
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();
    }
}
