package testInterHotel;



import junit.framework.TestCase;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub;


public class InterHotleCityTest extends TestCase{
	
	public void testLo()throws Exception{
		
		FreeHotelSearchWebServiceStub stub = new FreeHotelSearchWebServiceStub("http://publicwebservices.hotel.de/V2_8/FreeHotelSearchWebService.svc");
		FreeHotelSearchWebServiceStub.GetWebservicesVersion gv = new FreeHotelSearchWebServiceStub.GetWebservicesVersion();
	
		FreeHotelSearchWebServiceStub.GetLocationListRequest req = new FreeHotelSearchWebServiceStub.GetLocationListRequest();
		FreeHotelSearchWebServiceStub.GetLocationList lo = new FreeHotelSearchWebServiceStub.GetLocationList();
		req.setAffiliateNumber(4833757);
		req.setToken(""+System.currentTimeMillis());
	
		req.setCountryISOa3("CHN");
		req.setCampaignNr(0);
		req.setLanguage("EN");
		req.setType(FreeHotelSearchWebServiceStub.LocationType.City);
		lo.setObjRequest(req);
		FreeHotelSearchWebServiceStub.GetLocationListResponseE res = stub.getLocationList(lo);
		
		System.out.println(res.getGetLocationListResult().getError().getErrorDescription());
		FreeHotelSearchWebServiceStub.ArrayOfLoc los= res.getGetLocationListResult().getLocationList();
		FreeHotelSearchWebServiceStub.Loc [] lll= los.getLoc();
		for(FreeHotelSearchWebServiceStub.Loc l:lll){
			System.out.println("名字:"+l.getName()+","+l.getISOa3()+","+l.getMRegion()+","+l.getNr()+","+l.getPCRange());
		}
	}

}
