package com.ccservice.b2b2c.atom.servlet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.service.ITicketSearchService;
import com.ccservice.b2b2c.base.passenger.Passenger;
import com.ccservice.b2b2c.base.service.IAirService;

/**
 * @author Administrator
 * 
 * 提取行程单
 * 
 */
public class PickupItineraryJob implements Job {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		String strRet = "0";
		// 每月1号、8号、15号、22号
		try{
		String url = "http://localhost:8080/lthk_service/service/";
		HessianProxyFactory factory = new HessianProxyFactory();
		IAirService service = (IAirService) factory.create(IAirService.class,
				url + IAirService.class.getSimpleName());
		ITicketSearchService ticketservice = (ITicketSearchService) factory
				.create(ITicketSearchService.class, url
						+ ITicketSearchService.class.getSimpleName());

		// 开始执行自动提取行程单功能
		System.out.println("开始执行自动提取行程单功能-"
				+ new SimpleDateFormat("yyyy-MM-dd").format(new Date(System
						.currentTimeMillis())));
		// 时间范围
		String strwhere = " WHERE " + Passenger.COL_rttime + " between '"
				+ getDatestr(-14) + "' and '" + getDatestr(0) + "'";
		// 有票号并且没有行程单号
		strwhere += " and " + Passenger.COL_ticketnum + " is not null and "
				+ Passenger.COL_ticketnum + "<>''";
		strwhere += " and (" + Passenger.COL_fet + " is null OR "
				+ Passenger.COL_fet + "='')";
		List<Passenger> listpassenger = service.findAllPassenger(strwhere,
				"ORDER BY ID", -1, 0);
		for (Passenger passenger : listpassenger) {
			if (passenger.getFet().equals("")) {
				
				service.updatePassengerIgnoreNull(passenger);
			}
		}
		strRet = "1";
		}catch(Exception e){
			System.out.println("****提取行程单出错：");
			e.printStackTrace();
		}
		System.out.println("提取行程单over");
	}

	/**
	 * 获取当前时间传入0 昨天-1 明天1
	 * 
	 * @param date
	 * @return
	 */
	public String getDatestr(int date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.DAY_OF_MONTH, date);
		System.out.println(cd.toString());
		return sdf.format(cd.getTime()).toString();
	}

	/**
	 * 根据票号提取行程单号
	 * 
	 * @ticketnumber 票号
	 * @return 行程单号
	 */
	public String getReptNumber(String ticketnumber,
			ITicketSearchService servcie) {
		String strRet = "";
		if (ticketnumber.indexOf("781-") >= 0
				|| ticketnumber.indexOf("774-") >= 0) {
			strRet = servcie.commandFunction("DETR:tn/" + ticketnumber + ",f",
					"");
			if (strRet.length() > 0) {
				String strreg = "[R][P][0-9]{10}";
				Pattern pattFlight = Pattern.compile(strreg);
				Matcher mFlight = pattFlight.matcher(strRet);
				if (mFlight.find()) {
					strRet = mFlight.group().toString().replace("RP", "")
							.trim();
				} else {
					strRet = "";
				}
			}
		} else {
			strRet = servcie.getRpNumber(ticketnumber);
		}
		return strRet;
	}

}
