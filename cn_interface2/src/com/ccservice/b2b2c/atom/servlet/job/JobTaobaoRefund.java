package com.ccservice.b2b2c.atom.servlet.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.component.PublicComponent;
import com.ccservice.b2b2c.atom.hotel.TaobaoHotelInterfaceUtil;

/**
 * 定时抓取淘宝退票
 * 
 * @time 2015年5月22日 下午3:34:06
 * @author fiend
 */
public class JobTaobaoRefund extends PublicComponent implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        execute();
    }

    public void execute() {
        new TaobaoHotelInterfaceUtil().returnOrdersByList(0);
        new TaobaoHotelInterfaceUtil().returnOrdersByList(1);
    }

}
