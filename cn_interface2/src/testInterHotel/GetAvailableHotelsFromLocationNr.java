package testInterHotel;

import junit.framework.TestCase;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.AvailableHotelsFromLocationNrRequest;

public class GetAvailableHotelsFromLocationNr extends TestCase {
	public void testLo() throws Exception {
		FreeHotelSearchWebServiceStub stub = new FreeHotelSearchWebServiceStub(
				"http://publicwebservices.hotel.de/V2_8/FreeHotelSearchWebService.svc");
		FreeHotelSearchWebServiceStub.GetAvailableHotelsFromLocationNr getAvailableHotelsFromLocationNr10 = new FreeHotelSearchWebServiceStub.GetAvailableHotelsFromLocationNr();
		AvailableHotelsFromLocationNrRequest request = new AvailableHotelsFromLocationNrRequest();
		request.setAffiliateNumber(4833757);
		
		
		
	}

}
