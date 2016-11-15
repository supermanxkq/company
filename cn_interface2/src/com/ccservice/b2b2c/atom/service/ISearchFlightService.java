package com.ccservice.b2b2c.atom.service;

import java.util.List;

public interface ISearchFlightService {
	
	public List SeachFiveoneFlight(String scitycode,String ecitycode,String airlineCode,String depdate,String type);
}
