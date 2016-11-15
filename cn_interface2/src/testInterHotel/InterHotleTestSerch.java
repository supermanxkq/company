package testInterHotel;



import junit.framework.TestCase;

import org.apache.axis2.databinding.types.UnsignedInt;

import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.HotelSortMethod;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.HotelSortOrder;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.RoomTypeEnum;


public class InterHotleTestSerch extends TestCase{
	
	
	public void testHotel()throws Exception{
		
		
		
		FreeHotelSearchWebServiceStub stub = new FreeHotelSearchWebServiceStub("http://publicwebservices.hotel.de/V2_8/FreeHotelSearchWebService.svc");
		
		FreeHotelSearchWebServiceStub.GetAvailableHotelsFromLocationNr a = new FreeHotelSearchWebServiceStub.GetAvailableHotelsFromLocationNr();
		FreeHotelSearchWebServiceStub.AvailableHotelsFromLocationNrRequest req = new FreeHotelSearchWebServiceStub.AvailableHotelsFromLocationNrRequest();
		req.setToken(""+System.currentTimeMillis());
		req.setAffiliateNumber(4833757);
		FreeHotelSearchWebServiceStub.Date sda =new FreeHotelSearchWebServiceStub.Date();
		sda.setYear(2011);
		sda.setMonth(8);
		sda.setDay(22);
		
		req.setArrival(sda);//开始年月日
		
		FreeHotelSearchWebServiceStub.Date eda=new FreeHotelSearchWebServiceStub.Date();
		eda.setYear(2011);
		eda.setMonth(8);
		eda.setDay(26);
		
		req.setDeparture(eda);//结束年月日
		
		
		req.setLanguage("EN");
		
		long fValue = 98116l;

		
		
		UnsignedInt cInt = new UnsignedInt(fValue);

		
	
		req.setLocationNr(cInt);
		
		req.setNumberOfRooms(1);//间数
		
		FreeHotelSearchWebServiceStub.RoomTypeEnum roomTypeEnum = RoomTypeEnum.SingleRoom;
		req.setRoomType(roomTypeEnum);
		
		
		FreeHotelSearchWebServiceStub.HotelSortMethod sortMethod = HotelSortMethod.Default;
		req.setSortMethod(sortMethod);
		
		FreeHotelSearchWebServiceStub.HotelSortOrder hotelSortOrder =HotelSortOrder.Ascending;
		req.setSortOrder(hotelSortOrder);
		a.setObjRequest(req);
		
		
		//图片
		FreeHotelSearchWebServiceStub.GetAvailableHotelsFromLocationNrResponse res = stub.getAvailableHotelsFromLocationNr(a);
	
		
		
		FreeHotelSearchWebServiceStub.ArrayOfAvailabilityListHotel los= res.getGetAvailableHotelsFromLocationNrResult().getAvailableHotelList();
		FreeHotelSearchWebServiceStub.AvailabilityListHotel [] lll= los.getAvailabilityListHotel();
		
		if(lll!=null){
		
		for(FreeHotelSearchWebServiceStub.AvailabilityListHotel l:lll){
			
			//图片
			FreeHotelSearchWebServiceStub.PictureReference[] aa = l.getMedia().getPictureReference();
			//
			System.out.println(l.getPrice().getAmountBeforeTax());
			System.out.println(l.getPrice().getEuroExchangeRate());
			System.out.println("名字:"+l.getName()+",,价格=="+l.getPrice().getAmountAfterTax()+",币种:"
					+l.getPrice().getCurrency()+",城市:"+l.getHotelAddress().getCity()+",图片:"+aa[0].getLink()
					+",星级:"+l.getRatingHotelDe()+",ID:"+l.getPropertyNumber()
					);
			String image="";
			System.out.println("aa="+aa.length);
			if(aa.length>0){
				for(int c=0;c<aa.length;c++){
					
					image+=aa[c].getLink()+",";
				}
				
				
			}
			
			System.out.println("image=="+image);
			
			
		/*	Hotel hotel = new Hotel();
			
			//酒店基本信息
			hotel.setName(l.getName());
			hotel.setEnname(l.getName());
			hotel.setCityid(7l);
			hotel.setAddress(l.getHotelAddress().getStreet());
			hotel.setLat(l.getHotelAddress().getGeographicCoordinates().getLatitude());
			hotel.setLng(l.getHotelAddress().getGeographicCoordinates().getLongitude());
			String IMAGE="";
			if(aa.length>0){
				for(int i=0;i<aa.length;i++){
					IMAGE+=aa[i].getLink()+",";
				}
			}
			hotel.setCheckdesc(IMAGE);//图片路径
			hotel.setType(2);//1,国内  2,国际
			hotel.setPostcode(l.getLocation().getPostalCodeRange());//邮编
			hotel.setContryid(2l);
			String star=l.getRatingHotelDe();
			hotel.setHotelcode(l.getPropertyNumber()+"");
			hotel.setStar(Integer.parseInt(star));
			Server.getInstance().getHotelService().createHotel(hotel);*/
			
			
		}
		}
	}


}
