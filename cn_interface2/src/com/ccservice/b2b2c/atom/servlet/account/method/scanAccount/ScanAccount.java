package com.ccservice.b2b2c.atom.servlet.account.method.scanAccount;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.callback.WriteLog;
import com.ccservice.b2b2c.atom.servlet.account.DesUtil;
import com.ccservice.b2b2c.atom.servlet.account.method.deletePassenger.NewDeletePassenger;
import com.ccservice.b2b2c.util.db.DBHelperAccount;
import com.ccservice.b2b2c.util.db.DataRow;
import com.ccservice.b2b2c.util.db.DataTable;
import com.ccservice.elong.inter.PropertyUtil;

public class ScanAccount implements Job{
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    public void execute() {
        int minid = Integer.valueOf(PropertyUtil.getValue("minid"));
        while (true) {
            try {
                if (isNight()) {
                    continue;
                }
                JSONArray NeedScanAccount = new JSONArray();
                try {
                    NeedScanAccount = SelectAccount(minid);
                }
                catch (Exception e) {

                    WriteLog.write("数据库连接异常", e.getMessage());
                }

                int NeedScanAccountSize = NeedScanAccount.size();
                //等待时间，单位：毫秒
                long EveryWait = Long.parseLong(PropertyUtil.getValue("DeletPassengerEveryWait"));
                //超时时间，单位：秒
                long NeedScanAccountTimeOut = Long.parseLong(PropertyUtil.getValue("DeletPassengerTimeOut"));
                //一圈时间，单位：秒
                //                long NeedDeletPassengersTotalTime = Long.parseLong(PropertyUtil.getValue("DeletPassengerTotalTime"));
                //线程数
                int NeedScanAccountNum = Integer.parseInt(PropertyUtil.getValue("DeletPassengerThreadNum"));
                //---------------重新计算---------------
                //                //等待时间
                //                if (NeedDeletPassengersTotalTime > 0) {
                //                    //平均等待
                //                    long tempWait = NeedDeletPassengersTotalTime * 1000 / NeedDeletPassengersSize;
                //                    //等待时间
                //                    EveryWait = tempWait < EveryWait ? tempWait : EveryWait;
                //                }
                //线程数
                NeedScanAccountNum = NeedScanAccountSize < NeedScanAccountNum ? NeedScanAccountSize
                        : NeedScanAccountNum;
                //总任务
                List<Future<String>> FutureList = new ArrayList<Future<String>>();
                //线程池
                ExecutorService ThreadPool = Executors.newFixedThreadPool(NeedScanAccountNum);
                try {
                    CompletionService<String> CompletionService = new ExecutorCompletionService<String>(ThreadPool);
                    //循环删除
                    for (int i = 0; i < NeedScanAccountSize; i++) {
                        //取账号
                        String accountName = DesUtil.decrypt(
                                NeedScanAccount.getJSONObject(i).getString("accountName"), "A1B2C3D4E5F60708");
                        int id = NeedScanAccount.getJSONObject(i).getIntValue("accountId");
                        if (NeedScanAccount.getJSONObject(i).containsKey("accountId")
                                && NeedScanAccount.getJSONObject(i).getIntValue("accountId") > minid) {
                            minid = NeedScanAccount.getJSONObject(i).getIntValue("accountId");
                        }
                        //要等待
                        try {
                            if (EveryWait > 0) {
                                Thread.sleep(EveryWait);
                            }
                        }
                        catch (Exception e) {

                        }
                        for(int j=1;j>0;j++){
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date=new Date();
                            String nowdate=dateFormat.format(date);
                            int nowtime=date.getHours();
                            if(nowtime<8 || nowtime>22){
                                Thread.sleep(10000);
                                WriteLog.write("扫描账号----->线程睡眠时间", "当前时间----->"+nowdate+"睡眠时间---->10s,每天22点以后到第二天8点以前睡眠线程");
                            }
                            else{
                                //线程处理
                                WriteLog.write("扫描账号----->线程启动时间", "当前时间----->"+nowdate+"每天8点到22点启动线程");
                                FutureList.add(CompletionService.submit(new NewScanAccount(accountName, id)));
                                break;
                            }
                        }
                        
                    }
                    //判断超时
                    for (int out = 0; out < FutureList.size(); out++) {
                        //任务超时，单位：秒
                        if (CompletionService.poll(NeedScanAccountTimeOut, TimeUnit.SECONDS) == null) {
                            //取消操作
                            for (int i = 0; i < FutureList.size(); i++) {
                                try {
                                    Future<String> future = FutureList.get(i);
                                    //超时取消
                                    if (!future.isDone()) {
                                        future.cancel(true);
                                    }
                                }
                                catch (Exception e) {

                                }
                            }
                            break;
                        }
                    }
                }
                catch (Exception e) {

                }
                finally {
                    for (int i = 0; i < FutureList.size(); i++) {
                        try {
                            FutureList.get(i).cancel(true);
                        }
                        catch (Exception e) {

                        }
                    }
                    ThreadPool.shutdown();
                }
            }
            catch (Exception e) {
            }
            finally {
                try {
                    //线程等待
                    if (isNight()) {
                        Thread.sleep(Long.parseLong(PropertyUtil.getValue("KeepOnlineThreadNightWait")) * 1000);
                    }
                    else {
                        Thread.sleep(Long.parseLong(PropertyUtil.getValue("KeepOnlineThreadOverWait")) * 1000);
                    }
                }
                catch (Exception e) {
                }
            }
        }
    }

    //判断当前时间23:30至07:00
    private boolean isNight() {
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

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        System.out.println("----------开启----------->");
        execute();
    }

    public static JSONArray SelectAccount(int minid) {
        String selectSql = "[T_CUSTOMERUSER_select] @minId=" + minid;
        JSONArray jsonArray = new JSONArray();
        DataTable dataTable = DBHelperAccount.GetDataTable(selectSql);
        if (dataTable != null) {
            for (DataRow dataRow : dataTable.GetRow()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("accountId", dataRow.GetColumnInt("ID"));
                jsonObject.put("accountName", dataRow.GetColumnString("C_LOGINNAME"));
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }
   
   
}
