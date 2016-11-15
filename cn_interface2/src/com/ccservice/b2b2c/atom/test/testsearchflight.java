package com.ccservice.b2b2c.atom.test;

import java.net.MalformedURLException;
import java.util.List;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.atom.service.ISearchFlightService;
import com.ccservice.b2b2c.base.flightinfo.FlightInfo;

public class testsearchflight {
	public static void main(String[] args) {

	/*	List<FlightInfo> list=Server.getInstance().getSearchFiveoneFlightService().SeachFiveoneFlight("PEK", "SHA", "", "2011-12-28","");
		System.out.println("=="+list.size());
		
		for(int a=0;a<list.size();a++){
			System.out.println("=="+list.get(a).getAirCompany());
		}*/
		
		
	String url = "http://www.alhk999.com/cn_interface/service/";
    	
    	HessianProxyFactory factory = new HessianProxyFactory();
    	try {
			ISearchFlightService servier = (ISearchFlightService) factory.create(ISearchFlightService.class, url + ISearchFlightService.class.getSimpleName()) ;
		
			List<FlightInfo> list=servier.SeachFiveoneFlight("PEK", "SHA", "", "2011-12-28","");
			for(int a=0;a<list.size();a++){
				System.out.println("=="+list.get(a).getAirCompany());
			}
    	} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	
		
		
/*
		List list=Server.getInstance().getSearchFiveoneFlightService().SeachFiveoneFlight("PEK", "SHA", "MU", "2011-12-25","");
		System.out.println(list.size());
		
	String url = "http://www.alhk999.com/cn_interface/service/";
    	
    	HessianProxyFactory factory = new HessianProxyFactory();
    	try {
			ISearchFlightService servier = (ISearchFlightService) factory.create(ISearchFlightService.class, url + ISearchFlightService.class.getSimpleName()) ;
		
			List<FlightInfo> list=servier.SeachFiveoneFlight("PEK", "SHA", "", "2011-12-28","");
			for(int a=0;a<list.size();a++){
				System.out.println("=="+list.get(a).getAirCompany());
			}
    	} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
>>>>>>> 1.3*/
	}
}