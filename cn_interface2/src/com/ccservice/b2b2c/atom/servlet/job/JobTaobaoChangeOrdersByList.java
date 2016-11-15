package com.ccservice.b2b2c.atom.servlet.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;

public class JobTaobaoChangeOrdersByList implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        try {
            TaobaoHotelInterfaceUtil tbiu = new TaobaoHotelInterfaceUtil();
            tbiu.taobaoQuerChangOrder();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
