package testInterHotel;



import junit.framework.TestCase;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.HotelSortMethod;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.HotelSortOrder;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.RoomTypeEnum;


public class InterHotleTestSerchById extends TestCase{
	
	
	public void testHotel()throws Exception{
		FreeHotelSearchWebServiceStub stub = new FreeHotelSearchWebServiceStub("http://publicwebservices.hotel.de/V2_8/FreeHotelSearchWebService.svc");
		
		FreeHotelSearchWebServiceStub.GetMultiAvailability a = new FreeHotelSearchWebServiceStub.GetMultiAvailability();
		FreeHotelSearchWebServiceStub.MultiAvailabilitySelectedPropertiesRequest req = new FreeHotelSearchWebServiceStub.MultiAvailabilitySelectedPropertiesRequest();
	
		
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
		
		//FreeHotelSearchWebServiceStub.ArrayOfInt = new FreeHotelSearchWebServiceStub.ArrayOfInt();
		FreeHotelSearchWebServiceStub.ArrayOfInt arrayOfInt =  new FreeHotelSearchWebServiceStub.ArrayOfInt();
	
		
		int hid= 292100;
		arrayOfInt.set_int(new int[]{hid});
		req.setPropertyNumbers(arrayOfInt);
	
		
		
		req.setNumberOfRooms(2);//间数
		
		FreeHotelSearchWebServiceStub.RoomTypeEnum roomTypeEnum = RoomTypeEnum.SingleRoom;
		req.setRoomType(roomTypeEnum);
		
		
		FreeHotelSearchWebServiceStub.HotelSortMethod sortMethod = HotelSortMethod.Default;
		req.setSortMethod(sortMethod);
		
		FreeHotelSearchWebServiceStub.HotelSortOrder hotelSortOrder =HotelSortOrder.Ascending;
		req.setSortOrder(hotelSortOrder);
		
	
		a.setObjSelectedPropertiesRequest(req);
	
		
		FreeHotelSearchWebServiceStub.GetMultiAvailabilityResponse res = stub.getMultiAvailability(a);
		
		
		FreeHotelSearchWebServiceStub.ArrayOfAvailabilityListHotel los = res.getGetMultiAvailabilityResult().getAvailableHotelList();
		FreeHotelSearchWebServiceStub.AvailabilityListHotel [] lll= los.getAvailabilityListHotel();
		
		for(FreeHotelSearchWebServiceStub.AvailabilityListHotel l:lll){
			
			//图片
			FreeHotelSearchWebServiceStub.PictureReference[] aa = l.getMedia().getPictureReference();
			//
			System.out.println("名字:"+l.getName()+",,价格=="+l.getPrice().getAmountAfterTax()+",币种:"
					+l.getPrice().getCurrency()+",城市:"+l.getHotelAddress().getCity()+",图片:"+aa[0].getLink()
					+",星级:"+l.getRatingHotelDe()+",ID:"+l.getPropertyNumber()+",早餐价:"+l.getBreakfastPrice());
			String image="";
			System.out.println("图片张数="+aa.length);
			if(aa.length>0){
				for(int c=0;c<aa.length;c++){
					
					image+=aa[c].getLink()+",";
				}
				
				
			}
			
			System.out.println("image=="+image);
			
		
		}
	
	}


}
