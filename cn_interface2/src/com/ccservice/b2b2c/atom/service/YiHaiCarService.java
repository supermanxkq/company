package com.ccservice.b2b2c.atom.service;

import com.ccservice.b2b2c.atom.cars.ICarBook;
import com.ccservice.b2b2c.base.carorder.Carorder;

public class YiHaiCarService implements IYiHaiCarService {
	private ICarBook carBook;

	public ICarBook getCarBook() {
		return carBook;
	}

	public void setCarBook(ICarBook carBook) {
		this.carBook = carBook;
	}

	@Override
	public String addorder(Carorder carorder) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cancelorder(String ordercode) throws Exception {
		// TODO Auto-generated method stub
		return carBook.cancelorder(ordercode);
	}


	public Carorder seachprice(String Stime, String Etime, String Scity,
			String Ecity, String Sprovince, String Eprovince, String Scarstore,
			String Ecarstore, String carcode, String gps) throws Exception {
		
		return carBook.seachprice(Stime, Etime, Scity, Ecity, Sprovince, Eprovince, Scarstore, Ecarstore, carcode, gps);
	}

	@Override
	public Carorder CreateCarOrder(String Stime, String Etime, String Scity,
			String Ecity, String Sprovince, String Eprovince, String Scarstore,
			String Ecarstore, String carcode, String gps, String nuber)
			throws Exception {
		
		return carBook.CreateCarOrder(Stime, Etime, Scity, Ecity, Sprovince, Eprovince, Scarstore, Ecarstore, carcode, gps, nuber);
	}

	@Override
	public String adduserYiHai(String mobile, String password, String username,
			String sex, String email, String nuber, String jtime)
			throws Exception {
		// TODO Auto-generated method stub
		return carBook.adduserYiHai(mobile, password, username, sex, email, nuber, jtime);
	}

	@Override
	public String GetYiHaiOrderState(String ordercode) throws Exception {
		// TODO Auto-generated method stub
		return carBook.GetYiHaiOrderState(ordercode);
	}
	
	

}
