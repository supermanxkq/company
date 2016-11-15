package testInterHotel;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;
import junit.framework.TestCase;
import org.apache.axis2.databinding.types.UnsignedInt;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.country.Country;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.incity.Incity;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.HotelSortMethod;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.HotelSortOrder;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.RoomTypeEnum;

public class TmportInterHotle extends TestCase {

	public void testHotel() throws Exception {
		String where = "where ID=80 or ID=225 or ID=104 or ID=73 or ID=199 or "
				+ "ID=206 or ID=14 or ID=22 or ID=30 or ID=83 or ID=171 or ID=172 or "
				+ "ID=211 or ID=13 or ID=38 or ID=168 or ID=58 or ID=123 or ID=150 or "
				+ "ID=176";
		// String where = "where 1=1";
		List<Country> countrys = Server.getInstance().getInterHotelService()
				.findAllCountry(where, "", -1, 0);
		for (int i = 0; i < countrys.size(); i++) {
			Country count = countrys.get(i);
			// citysFlight[0]=new Array('AAT','阿勒泰','ALETAI','ALT');
			// System.out.print("\"" + count.getZhname() + "|" +
			// count.getCode3()
			// + "|" + count.getId() + "\",");
			System.out.println("commoncitysFlight[" + i + "]=new Array('"
					+ count.getCode3() + "','" + count.getZhname() + "','"
					+ count.getName() + "','" + count.getCode3() + "','"
					+ count.getId() + "');");
		}
	}

	public void interHotel() {
		try {
			List<Incity> listincity = Server.getInstance()
					.getInterHotelService().findAllIncity(
							" where 1=1 and " + Incity.COL_nr + " IS NOT NULL",
							"ORDER BY ID", -1, 0);
			System.out.println("size:" + listincity.size());
			if (listincity.size() > 0) {
				for (int c = 0; c < listincity.size(); c++) {
					FreeHotelSearchWebServiceStub stub = new FreeHotelSearchWebServiceStub(
							"http://publicwebservices.hotel.de/V2_8/FreeHotelSearchWebService.svc");
					FreeHotelSearchWebServiceStub.GetAvailableHotelsFromLocationNr a = new FreeHotelSearchWebServiceStub.GetAvailableHotelsFromLocationNr();
					FreeHotelSearchWebServiceStub.AvailableHotelsFromLocationNrRequest req = new FreeHotelSearchWebServiceStub.AvailableHotelsFromLocationNrRequest();
					req.setToken("" + System.currentTimeMillis());
					req.setAffiliateNumber(4833757);// 中视
					// req.setAffiliateNumber(6216572);//陈栋
					FreeHotelSearchWebServiceStub.Date sda = new FreeHotelSearchWebServiceStub.Date();
					Calendar cal = Calendar.getInstance();
					sda.setYear(cal.get(Calendar.YEAR));
					sda.setMonth(cal.get(Calendar.MONTH));
					sda.setDay(cal.get(cal.DAY_OF_MONTH));
					req.setArrival(sda);// 开始年月日
					FreeHotelSearchWebServiceStub.Date eda = new FreeHotelSearchWebServiceStub.Date();
					eda.setYear(cal.get(Calendar.YEAR));
					eda.setMonth(cal.get(Calendar.MONTH));
					cal.add(Calendar.DAY_OF_MONTH, 1);
					eda.setDay(cal.get(cal.DAY_OF_MONTH));
					req.setDeparture(eda);// 结束年月日
					req.setLanguage("ZH");

					Incity incity = listincity.get(c);
					// long fValue = Long.parseLong(listincity.get(c).getNr());
					// System.out.println(listincity.get(c).getId()+":NR==" +
					// fValue);
					long fValue = Long.parseLong(incity.getNr());
					UnsignedInt cInt = new UnsignedInt(fValue);
					req.setLocationNr(cInt);
					req.setNumberOfRooms(1);// 间数
					FreeHotelSearchWebServiceStub.RoomTypeEnum roomTypeEnum = RoomTypeEnum.SingleRoom;
					// FreeHotelSearchWebServiceStub.RoomTypeEnum roomTypeEnum =
					// RoomTypeEnum.DoubleRoom;
					req.setRoomType(roomTypeEnum);
					FreeHotelSearchWebServiceStub.HotelSortMethod sortMethod = HotelSortMethod.Default;
					req.setSortMethod(sortMethod);
					FreeHotelSearchWebServiceStub.HotelSortOrder hotelSortOrder = HotelSortOrder.Ascending;
					req.setSortOrder(hotelSortOrder);
					a.setObjRequest(req);
					// 图片
					FreeHotelSearchWebServiceStub.GetAvailableHotelsFromLocationNrResponse res = stub
							.getAvailableHotelsFromLocationNr(a);
					FreeHotelSearchWebServiceStub.ArrayOfAvailabilityListHotel los = res
							.getGetAvailableHotelsFromLocationNrResult()
							.getAvailableHotelList();
					FreeHotelSearchWebServiceStub.AvailabilityListHotel[] lll = los
							.getAvailabilityListHotel();
					if (lll != null) {
						System.out.println(incity.getId() + ":" + lll.length);
						for (FreeHotelSearchWebServiceStub.AvailabilityListHotel availabilityListHotel : lll) {
							// 图片
							Hotel hotel = new Hotel();
							/*
							 * List<Incity> listIncity=
							 * Server.getInstance().getInterHotelService().findAllIncity("
							 * where 1=1 and "+Incity.COL_name+"
							 * ='"+l.getHotelAddress().getCity()+"'", "", -1,
							 * 0); if(listIncity.size()>0){
							 * incity=listIncity.get(0); }
							 */
							// 判断原来数据库中有没有当前查到的酒店
							// List<Hotel> listhotel = Server
							// .getInstance()
							// .getHotelService()
							// .findAllHotel(
							// " where 1=1 and " + Hotel.COL_name
							// + " ='" + hname + "' and "
							// + Hotel.COL_cityid + " ="
							// + incity.getId(), "", -1, 0);
							// if (listhotel.size() > 0) {
							// hotel = listhotel.get(0);
							// }
							int propertyNumber = availabilityListHotel
									.getPropertyNumber();
							hotel.setHotelcode(propertyNumber + "");
							String name = availabilityListHotel.getName();
							hotel.setName(name);
							hotel.setEnname(name);
							// 酒店星级
							String star = availabilityListHotel
									.getRatingHotelDe().split(",")[0];// 酒店星级有3，5这种情况，在网上查结果为3
							hotel.setStar(Integer.parseInt(star));

							if (availabilityListHotel.getMedia() != null
									&& availabilityListHotel.getMedia()
											.getPictureReference() != null) {
								String IMAGE = "";
								FreeHotelSearchWebServiceStub.PictureReference[] aa = availabilityListHotel
										.getMedia().getPictureReference();
								if (aa.length > 0) {
									for (int i = 0; i < aa.length; i++) {
										IMAGE += aa[i].getLink();
										if (aa.length > 1 && i < aa.length - 1) {
											IMAGE += ",";
										}
									}
								}
								hotel.setCheckdesc(IMAGE);// 图片路径
							}

							// 酒店地址
							FreeHotelSearchWebServiceStub.Address address = availabilityListHotel
									.getHotelAddress();
							hotel.setAddress(address.getStreet());
							if (address.getGeographicCoordinates() != null) {
								hotel.setLat(address.getGeographicCoordinates()
										.getLatitude());
								hotel.setLng(address.getGeographicCoordinates()
										.getLongitude());
							}
							if (address.getPostalCode() != null) {
								hotel.setPostcode(address.getPostalCode());// 邮编
							}
							hotel.setCountryid(incity.getCountryid());// 国家ID

							// 酒店基本信息
							hotel.setState(3);
							hotel.setCityid(incity.getId());

							if (availabilityListHotel.getPrice() != null) {
								hotel.setStartprice(Double
										.parseDouble(availabilityListHotel
												.getPrice().getAmountAfterTax()
												+ ""));
								hotel.setPricetype(availabilityListHotel
										.getPrice().getCurrency());
							}
							hotel.setType(2);// 1,国内 2,国际
							Server.getInstance().getHotelService().createHotel(
									hotel);
						}
					}
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
