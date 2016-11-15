package com.ccservice.b2b2c.atom.service;

import com.ccservice.b2b2c.base.carorder.Carorder;







public interface IYiHaiCarService {
	
	public String cancelorder(String ordercode) throws Exception;
	
	public String addorder(Carorder carorder) throws Exception;

	public Carorder seachprice(String Stime,String Etime,String Scity,String Ecity,String Sprovince,String Eprovince,String Scarstore,String Ecarstore,String carcode,String gps) throws Exception;	

	public Carorder CreateCarOrder(String Stime,String Etime,String Scity,String Ecity,String Sprovince,String Eprovince,String Scarstore,String Ecarstore,String carcode,String gps,String nuber) throws Exception;	

	public String adduserYiHai(String  mobile,String password,String username,String sex,String email,String nuber,String jtime) throws Exception;

	public String GetYiHaiOrderState(String ordercode)throws Exception;
}
