package com.ccservice.b2b2c.base.service;

import java.util.List;

import com.ccservice.b2b2c.base.flightinfo.FlightSearch;

public interface ITicketSearchService {
	/**
	 * 根据航班查询参数类得到航班信息列表
	 * @param FlightSearch
	 * @return FlightInfo 
	 */
 	public List findAllFlightinfo(FlightSearch flightSearch);
 	
 	public List findAllFlightinfo2(FlightSearch flightSearch,String addr,String username,String password);
 	
	public String getRemoteAddr();
	
	public void setRemoteAddr(String addr);
	
	
	
}
