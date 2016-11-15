package com.ccservice.b2b2c.atom.service;

import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;




public class SearchFlightService implements ISearchFlightService {

	@Override
	public List SeachFiveoneFlight(String scitycode, String ecitycode,
		String airlineCode, String depdate, String type) {
		// TODO Auto-generated method stub
		//通过接口访问
		return Server.getInstance().getSearchFiveoneFlightService().SeachFiveoneFlight(scitycode, ecitycode, airlineCode, depdate, type);
		//苍南51book原始接口
		//return Server.getInstance().getRateService().SeachFiveoneFlight(scitycode, ecitycode, airlineCode, depdate, type);
	}

}
