package com.ccservice.b2b2c.atom.component;

import java.net.MalformedURLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.service.ITicketSearchService;
import com.ccservice.b2b2c.base.flightinfo.FlightInfo;
import com.ccservice.b2b2c.base.flightinfo.FlightSearch;


public class DoTimeTask {

	private final static Timer timer = new Timer();
    private static Date firstTime;//任务首次执行时间
    private final static long PERIO_TIME=1*60*1000;//任务运行周期: 单位毫秒
    public final static Timer getTimer(){
        return timer;//获取timer对象的实例
    }
    public void start() throws Exception{
       SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       firstTime=sdf.parse("2010-11-04 16:03:40");
       timer.scheduleAtFixedRate(new SendMsgTask(),firstTime,PERIO_TIME);//设置定时任务
    }
    public static void main(String[] args) throws Exception{
      
    	new DoTimeTask().start(); //开启任务
       
    }
}

class SendMsgTask extends TimerTask{
    private static int i=0;
    public void run() {
    	try
    	{
    	   //判断是否是否是设定时间，如果是执行查询热门航班方法
    		Timestamp tmnowtime=new Timestamp(System.currentTimeMillis());
    		Timestamp tmSETTime=dateToTimestamp(formatTimestamp2(tmnowtime)+" 02:10:00");
    		Timestamp tmNowTime=dateToTimestamp(tmnowtime.toString());
    		System.out.println(tmNowTime.getTime()-tmSETTime.getTime());
    		if( (tmNowTime.getTime()-tmSETTime.getTime())>0 && (tmNowTime.getTime()-tmSETTime.getTime())<1000*60*60)
    		{
                 sendMsg();
    		}
    	}
    	catch(Exception ex)
    	{
    		
    	}
    }
    private void sendMsg() throws MalformedURLException {
        //调用航班查询接口
		String url = "http://localhost:8080/lthk_interface/service/" ;

		HessianProxyFactory factory = new HessianProxyFactory();
		ITicketSearchService servier = (ITicketSearchService) factory.create(ITicketSearchService.class,
				url + ITicketSearchService.class.getSimpleName());
		//查询航班
		//热门城市
		String[] strCityFrom={"PEK","SHA","NKJ","WUH","SYX","CAN","DLC","PVG","INC","CKG","LHW","KHN","TAO","YNT","XIY","HGH","NDG","SWA"};
		String[] strCityTo={"PEK","SHA","NKJ","WUH","SYX","CAN","DLC","PVG","INC","CKG","LHW","KHN","TAO","YNT","XIY","HGH","NDG","SWA"};
		//出发时间 --查询最近一周的数据
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd") ;
		Calendar calendar = Calendar.getInstance();
		String startDate = sdf.format(calendar.getTime());
		System.out.println("开始查询");
		for(int w=1;w<8;w++)
		{
			
			//热门城市去程信息
			for(int i=0;i<strCityFrom.length;i++)
			{
				for(int j=0;j<strCityTo.length;j++)
				{
					FlightSearch flightSearch=new FlightSearch();
					flightSearch.setFromDate(startDate);
					flightSearch.setStartAirportCode(strCityFrom[i].toString());
					flightSearch.setEndAirportCode(strCityTo[j].toString());
					List<FlightInfo> listFlightInfo=servier.findAllFlightinfo(flightSearch);
					System.out.println("正在查询："+strCityFrom[i].toString()+"-"+strCityTo[j].toString()+","+startDate+"的数据");
				}
			}
			//热门城市返程信息
			for(int i=0;i<strCityTo.length;i++)
			{
				for(int j=0;j<strCityFrom.length;j++)
				{
					FlightSearch flightSearch=new FlightSearch();
					flightSearch.setFromDate(startDate);
					flightSearch.setStartAirportCode(strCityTo[i].toString());
					flightSearch.setEndAirportCode(strCityFrom[j].toString());
					List<FlightInfo> listFlightInfo=servier.findAllFlightinfo(flightSearch);
					System.out.println("正在查询："+strCityTo[i].toString()+"-"+strCityFrom[j].toString()+","+startDate+"的数据");
				}
			}
			calendar.add(Calendar.DATE , 1);
			startDate= sdf.format(calendar.getTime());
		
		}
		System.out.println("查询结束");
		
    }

    public String formatTimestamp2(Timestamp date){
		try {
			return (new SimpleDateFormat("yyyy-MM-dd").format(date));
			
		} catch (Exception e) {
			return "";
		}
		
	}
public static Timestamp dateToTimestamp(String date){
	try {
		SimpleDateFormat dateFormat=new SimpleDateFormat();
		if(date.length()==10)
		{
		dateFormat=new SimpleDateFormat("yyyy-MM-dd");
		}else
		{
		dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
		return (new Timestamp(dateFormat.parse(date).getTime()));
		
	} catch (Exception e) {
		return null;
	}
	
}
}
