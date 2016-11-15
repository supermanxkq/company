package com.ccservice.b2b2c.atom.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hmhotelprice.AllotResult;
import com.ccservice.b2b2c.base.hmhotelprice.Allotment;
import com.ccservice.b2b2c.base.hmhotelprice.Hmhotelprice;
import com.ccservice.b2b2c.base.hmhotelprice.StayDate;

public class UpdateAlot {
	public static void main(String[] args) {
		Calendar cal = Calendar.getInstance();
		Date checkin = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, 28);
		Date checkout = cal.getTime();
		AllotResult result = null;
		try {
			//result = Server.getInstance().getIHMHotelService().getQallot(
				//	contract, "", "", "", checkin, checkout);
		} catch (Exception e) {
			System.out.println("加3天试试……");
			Calendar calt = Calendar.getInstance();
			calt.add(Calendar.DAY_OF_MONTH, 3);
			Date check = calt.getTime();
			calt.add(Calendar.DAY_OF_MONTH, 28);
			Date check0 = calt.getTime();
			//result = Server.getInstance().getIHMHotelService().getQallot(
				//	contract, "", "", "", check, check0);
		}
		List<Allotment> alloments = result.getAllotments();
		if (alloments != null && alloments.size() > 0) {
			for (Allotment allotment : alloments) {
				String sql = "where C_CONTRACTID='" + result.getContract()
						+ "' and C_CONTRACTVER='" + result.getHotelvar()
						+ "' and C_ALLOT='" + allotment.getAllot() + "' ";
				System.out.println(sql);
				List<Hmhotelprice> hmhotelprices = Server.getInstance()
						.getHotelService().findAllHmhotelprice(sql,
								"order by C_STATEDATE asc", -1, 0);
				List<StayDate> stads = allotment.getStaydates();
				if (stads.size() > 0 && hmhotelprices.size() > 0) {
					for (StayDate stayDate : stads) {
						for (Hmhotelprice hmprice : hmhotelprices) {
							if (stayDate.getDatestr().equals(
									hmprice.getStatedate())) {
								if (hmprice.getIsallot().equals(
										stayDate.getAllot())) {
								} else {
									hmprice.setIsallot(stayDate.getAllot());
									Server.getInstance().getHotelService()
											.updateHmhotelpriceIgnoreNull(hmprice.getId(),hmprice.getHotelid(),"",hmprice);
								}
							}
						}
					}
				}
			}

		}
	}
}
