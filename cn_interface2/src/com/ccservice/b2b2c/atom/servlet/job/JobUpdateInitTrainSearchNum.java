package com.ccservice.b2b2c.atom.servlet.job;

import java.util.ArrayList;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.component.SendPostandGet;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.interfaceaccount.InterfaceAccount;
import com.ccservice.elong.inter.PropertyUtil;

/**
 * 初始化用户的查询次数及最大查询次数/每天
 * 
 * @time 2015年5月21日 上午10:02:29
 * @author baiyushan
 */

public class JobUpdateInitTrainSearchNum implements Job {

    //请求地址的类型
    public static int inittype = 1;

    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        doinit(1);
    }

    /**
     * 初始化次数
     * @time 2015年5月21日 上午10:05:30
     * @author baiyushan
     */
    public static String initdata(int inittype, String partnerid) {
        String paramContent = "initdata=" + inittype + "&partnerid=" + partnerid;
        String trainSearchInitData_url = PropertyUtil.getValue("jobUpdateInitTrainSearchNum_trainSearchInitData_url",
                "Train.properties");
        String resultString = SendPostandGet.submitPost(trainSearchInitData_url, paramContent, "utf-8").toString();
        System.out.println(resultString);
        return resultString;
    }

    @SuppressWarnings("unchecked")
    public static void doinit(int inittype) {
        try {
            InterfaceAccount interfaceAccount = new InterfaceAccount();
            List<InterfaceAccount> list_interfaceAccount = new ArrayList<InterfaceAccount>();
            String sql = "select * from T_INTERFACEACCOUNT";
            list_interfaceAccount = Server.getInstance().getMemberService().findAllInterfaceAccountBySql(sql, -1, 0);
            if (list_interfaceAccount.size() > 0) {
                for (int i = 0; i < list_interfaceAccount.size(); i++) {
                    interfaceAccount = list_interfaceAccount.get(i);
                    initdata(1, interfaceAccount.getUsername());
                    System.out.println(interfaceAccount.getUsername() + "夜间12：00初始化完成！");
                }
            }
        }
        catch (Exception e) {
        }
    }

    /**
     * 初始化的时候讲数据库中的trianNum初始化为0
     * @param args
     * @time 2015年5月21日 下午6:48:30
     * @author baiyushan
     */
    //    public static void main(String[] args) {
    //        doinit(1);
    //
    //    }

}
