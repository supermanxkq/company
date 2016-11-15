package com.qunarprice;

import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 定时更新去哪儿酒店Cookie
 * @author WH
 */

public class QunarCookieJob implements Job {

    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<String> allip = AgentIpUtil.getAllIp();
        for (String ip : allip) {
            ReptileGetQunarHotelPrice.GetCookie("", ip);
        }
    }

}