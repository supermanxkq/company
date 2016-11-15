package testInterHotel;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.axis2.AxisFault;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.country.Country;
import com.ccservice.b2b2c.base.incity.Incity;

import junit.framework.TestCase;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub;

public class InterHotleTestCity extends TestCase {

	public void testLo() {
		// <objRequest> <Language>DE</Language> <Token>TestClient_06.05.2010
		// 10:17:30</Token> <CompanyNumber>0</CompanyNumber>
		// <AffiliateNumber>xxx</AffiliateNumber>
		// <SecureContext>false</SecureContext>
		// <Destination>Nürnberg</Destination> <CountryISOa3>DEU</CountryISOa3>
		// <AirportCode/> </objRequest>
		try {
			FreeHotelSearchWebServiceStub stub = new FreeHotelSearchWebServiceStub(
					"http://publicwebservices.hotel.de/V2_8/FreeHotelSearchWebService.svc");
			List<Country> countrys = Server.getInstance()
					.getInterHotelService().findAllCountry(
							"where 1=1 and ID>44", "ORDER BY ID", -1, 0);
			System.out.println(countrys.size());
			for (int i = 0; i < countrys.size(); i++) {
				FreeHotelSearchWebServiceStub.GetLocationListRequest req = new FreeHotelSearchWebServiceStub.GetLocationListRequest();
				FreeHotelSearchWebServiceStub.GetLocationList lo = new FreeHotelSearchWebServiceStub.GetLocationList();
				String countryCode = countrys.get(i).getCode3();
				// String countryCode = "CHN";
				req.setToken("" + System.currentTimeMillis());
				req.setLanguage("ZH");
				req.setCompanyNumber(0);
				req.setAffiliateNumber(4833757);
				req.setSecureContext(false);
				req.setCampaignNr(0);
				req.setType(FreeHotelSearchWebServiceStub.LocationType.City);
				req.setCountryISOa3(countryCode);
				lo.setObjRequest(req);
				getCity(stub, lo, countrys.get(i).getId());
			}
		} catch (AxisFault e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		try {
			FreeHotelSearchWebServiceStub.GetLocationListRequest req = new FreeHotelSearchWebServiceStub.GetLocationListRequest();
			FreeHotelSearchWebServiceStub.GetLocationList lo = new FreeHotelSearchWebServiceStub.GetLocationList();
			req.setToken("" + System.currentTimeMillis());
			req.setLanguage("EN");
			req.setCompanyNumber(0);
			req.setAffiliateNumber(4833757);
			req.setSecureContext(false);
			req.setCampaignNr(0);
			req.setType(FreeHotelSearchWebServiceStub.LocationType.City);
			req.setCountryISOa3("USA");
			lo.setObjRequest(req);
			getCity(new FreeHotelSearchWebServiceStub(
						"http://publicwebservices.hotel.de/V2_8/FreeHotelSearchWebService.svc"), lo, 840L);
		} catch (AxisFault e) {
			e.printStackTrace();
		}
	}

	private static void getCity(FreeHotelSearchWebServiceStub stub,
			FreeHotelSearchWebServiceStub.GetLocationList lo, long countryid) {
		try {
			FreeHotelSearchWebServiceStub.GetLocationListResponseE res = stub
					.getLocationList(lo);
			FreeHotelSearchWebServiceStub.ArrayOfLoc los = res
					.getGetLocationListResult().getLocationList();
			if (los.getLoc() != null) {
				FreeHotelSearchWebServiceStub.Loc[] lll = los.getLoc();
				System.out.println(lll.length);
				for (int i = 0; i < lll.length; i++) {
					FreeHotelSearchWebServiceStub.Loc loc = lll[i];
					Incity incity = new Incity();
					String countryCode = loc.getISOa3();
					double Latitude = loc.getLat();
					double Longitude = loc.getLong();
					String MRegion = loc.getMRegion();
					String name = loc.getName();
					int nr = loc.getNr();
					String PCRange = loc.getPCRange();

					incity.setCountryid(countryid);
					incity.setCreatetime(new Timestamp(System
							.currentTimeMillis()));
					// incity.setDesc(desc);
					incity.setLatlong(Latitude + "," + Longitude);
					incity.setMregion(MRegion);
					incity.setName(name);
					incity.setNr(nr + "");
					incity.setPcrange(PCRange);
					incity.setType(2L);
					incity.setZhname(name);
//					Server.getInstance().getInterHotelService().createIncity(
//							incity);
					System.out.println(countryid + "国家:" + countryCode + "城市名:"
							+ name + ",NR:" + nr + ",纬度:" + Latitude + ",经度:"
							+ Longitude + ",MRegion:" + MRegion + ",PCRange:"
							+ PCRange);
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
