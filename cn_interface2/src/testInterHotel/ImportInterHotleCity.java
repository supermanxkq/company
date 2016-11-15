package testInterHotel;

import java.sql.Timestamp;
import java.util.List;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.country.Country;
import com.ccservice.b2b2c.base.incity.Incity;

import junit.framework.TestCase;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub;

public class ImportInterHotleCity extends TestCase {

	public void testLo() throws Exception {
		// " where 1=1 and "+Country.COL_id+" not in ( SELECT
		// "+Incity.COL_countryid+" FROM "+Incity.TABLE+" ) and
		// "+Country.COL_code3+" is not null AND "+Country.COL_code3+" !='ITA'
		// and "+Country.COL_code3+" !='USA'"
		// String where=" where 1=1 and "+Country.COL_id+" not in ( SELECT
		// distinct "+Incity.COL_countryid+" FROM "+Incity.TABLE+" ) and
		// "+Country.COL_code3+" is not null and "+Country.COL_code3+"
		// !='USA'";// AND "+Country.COL_code3+" !='ITA' and
		// "+Country.COL_code3+" !='USA'", " ORDER BY ID ";
		String where = " where 1=1 and " + Country.COL_code3 + " ='USA'";
		System.out.println(where);
		List<Country> listCountry = Server.getInstance().getInterHotelService()
				.findAllCountry(where, " ORDER BY ID ", -1, 0);
		System.out.println("listCountry=" + listCountry.size());
		FreeHotelSearchWebServiceStub stub = new FreeHotelSearchWebServiceStub(
				"http://publicwebservices.hotel.de/V2_8/FreeHotelSearchWebService.svc");
		FreeHotelSearchWebServiceStub.GetWebservicesVersion gv = new FreeHotelSearchWebServiceStub.GetWebservicesVersion();
		if (listCountry.size() > 0) {
			for (int a = 0; a < listCountry.size(); a++) {
				try {
					FreeHotelSearchWebServiceStub.GetWebservicesVersionResponse resp = stub
							.getWebservicesVersion(gv);
					System.out.println(resp.getGetWebservicesVersionResult());
					FreeHotelSearchWebServiceStub.GetLocationListRequest req = new FreeHotelSearchWebServiceStub.GetLocationListRequest();
					FreeHotelSearchWebServiceStub.GetLocationList lo = new FreeHotelSearchWebServiceStub.GetLocationList();
					req.setAffiliateNumber(4833757);
					req.setToken("" + System.currentTimeMillis());
					System.out
							.println("国家三字码:" + listCountry.get(a).getCode3());
					req.setCountryISOa3(listCountry.get(a).getCode3());
					req.setCampaignNr(0);
					req.setLanguage("ZH");
					req
							.setType(FreeHotelSearchWebServiceStub.LocationType.City);
					lo.setObjRequest(req);
					FreeHotelSearchWebServiceStub.GetLocationListResponseE res = stub
							.getLocationList(lo);
					System.out.println(res.getGetLocationListResult()
							.getError().getErrorDescription());
					FreeHotelSearchWebServiceStub.ArrayOfLoc los = res
							.getGetLocationListResult().getLocationList();
					FreeHotelSearchWebServiceStub.Loc[] lll = los.getLoc();

					if (lll != null) {
						for (FreeHotelSearchWebServiceStub.Loc l : lll) {
							System.out.println("名字:" + l.getName() + ",ISOa3"
									+ l.getISOa3() + ",MRegion"
									+ l.getMRegion() + ",NR" + l.getNr()
									+ ",PCRange" + l.getPCRange());
							Incity incity = new Incity();
							List<Incity> listincity = Server.getInstance()
									.getInterHotelService().findAllIncity(
											" where 1=1 and " + Incity.COL_nr
													+ " ='" + l.getNr() + "'",
											"", -1, 0);
							if (listincity.size() > 0) {
								incity = listincity.get(0);

							}
							incity.setCountryid(listCountry.get(a).getId());
							incity.setCreatetime(new Timestamp(System
									.currentTimeMillis()));
							incity.setDesc("");
							incity.setLatlong(l.getLat() + "," + l.getLong());
							incity.setMregion(l.getMRegion());
							incity.setName(l.getName());
							incity.setNr(l.getNr() + "");
							incity.setType(2l);
							incity.setPcrange(l.getPCRange());
							if (listincity.size() > 0) {
								Server.getInstance().getInterHotelService()
										.updateIncityIgnoreNull(incity);
								System.out.println("修改了==" + incity);
							} else {
								Server.getInstance().getInterHotelService()
										.createIncity(incity);
								System.out.println("创建==" + incity);
							}

						}
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
			System.out.println("完了");
		}
	}

}
