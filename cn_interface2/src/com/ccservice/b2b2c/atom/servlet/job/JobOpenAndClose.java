package com.ccservice.b2b2c.atom.servlet.job;

import org.quartz.Job;
import com.weixin.util.RequestUtil;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import com.ccservice.b2b2c.atom.service12306.AccountSystem;
import com.ccservice.b2b2c.atom.servlet.TongChengTrain.TongchengSupplyMethod;

public class JobOpenAndClose implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        cleanAccount();
    }

    /**
     * 清空账号
     * @param types 2:可用账号池；3:使用中账号池
     * @param isenable 状态 4:当天不可用
     */
    private void cleanAccount() {
        //账号系统地址
        String url = new TongchengSupplyMethod().GetAccountSystemUrl();
        url = url.substring(0, url.lastIndexOf("/") + 1) + "UpdateAccount.jsp";
        //清理可用账号池
        RequestUtil.post(url + "?types=2", "", "UTF-8", AccountSystem.NullMap, 0);
        //清理使用中账号池
        RequestUtil.post(url + "?types=3&isenable=4", "", "UTF-8", AccountSystem.NullMap, 0);
    }

}
