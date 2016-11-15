package com.ccservice.b2b2c.atom.servlet;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.apache.axis2.databinding.types.UnsignedInt;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.country.Country;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelimage.Hotelimage;
import com.ccservice.b2b2c.base.incity.Incity;

import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.HotelSortMethod;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.HotelSortOrder;
import de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.RoomTypeEnum;

public class InterHotelJob implements Job{


	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		
		
		   
			 String url = "http://localhost:8080/cn_service/service/";
		    	
		    	
				
					String response="";
					
					
					
					try {
						
						List<Country>listCountry=Server.getInstance().getInterHotelService().findAllCountry(" where 1=1 and "+Country.COL_id+" not  in ( SELECT "+Hotel.COL_contryid+" from "+Hotel.TABLE+"  where 1=1 and "+Hotel.COL_type+" =2 and "+Hotel.COL_contryid+" is not null )", "", -1, 0);
						System.out.println("还有"+listCountry.size()+"国际没导入");
						for(int cc=0;cc<listCountry.size();cc++){
							
						List<Incity> listincity=Server.getInstance().getInterHotelService().findAllIncity(" where 1=1 and "+Incity.COL_nr+" IS NOT NULL  and "+Incity.COL_id+" not in ( SELECT "+Hotel.COL_cityid+" from "+Hotel.TABLE+" where 1=1 and "+Hotel.COL_type+" =2) and "+Incity.COL_countryid+" ="+listCountry.get(cc).getId(), "", -1, 0);
						
						if(listincity.size()>0){
							System.out.println("还有"+listincity.size()+"家城市没导入");
							for(int c=0;c<listincity.size();c++){
						
								if(!listincity.get(c).getNr().equals("98404")){
						FreeHotelSearchWebServiceStub stub = null;
						try {
							stub = new FreeHotelSearchWebServiceStub("http://publicwebservices.hotel.de/V2_8/FreeHotelSearchWebService.svc");
						} catch (AxisFault e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						FreeHotelSearchWebServiceStub.GetAvailableHotelsFromLocationNr a = new FreeHotelSearchWebServiceStub.GetAvailableHotelsFromLocationNr();
						FreeHotelSearchWebServiceStub.AvailableHotelsFromLocationNrRequest req = new FreeHotelSearchWebServiceStub.AvailableHotelsFromLocationNrRequest();
						req.setToken(""+System.currentTimeMillis());
						req.setAffiliateNumber(4833757);
						FreeHotelSearchWebServiceStub.Date sda =new FreeHotelSearchWebServiceStub.Date();
						sda.setYear(2011);
						sda.setMonth(8);
						sda.setDay(24);
						
						req.setArrival(sda);//开始年月日
						
						FreeHotelSearchWebServiceStub.Date eda=new FreeHotelSearchWebServiceStub.Date();
						eda.setYear(2011);
						eda.setMonth(8);
						eda.setDay(26);
						
						req.setDeparture(eda);//结束年月日
						
						
						req.setLanguage("ZH");
						
						long fValue = Long.parseLong(listincity.get(c).getNr());

						System.out.println("NR=="+fValue);
						
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
							Hotel hotel = new Hotel();
							
							
							//
							System.out.println("名字:"+l.getName()+",,价格=="+l.getPrice().getAmountAfterTax()+",币种:"
									+l.getPrice().getCurrency()+",城市:"+l.getHotelAddress().getCity()+",price:"+l.getPrice()
									);
							
						
						Incity incity = new Incity();
						Hotelimage hotelimage = new Hotelimage();
						
						/*List<Incity> listIncity=	Server.getInstance().getInterHotelService().findAllIncity(" where 1=1 and "+Incity.COL_name+" ='"+l.getHotelAddress().getCity()+"'", "", -1, 0);
						if(listIncity.size()>0){
							incity=listIncity.get(0);
						}*/
						String hname=l.getName();
						if(hname.indexOf("'")!=-1){
							hname=hname.replaceAll("'", "''");
						}
						List<Hotel>listhotel=	Server.getInstance().getHotelService().findAllHotel(" where 1=1 and "+Hotel.COL_name+" ='"+hname+"' and "+Hotel.COL_cityid+" ="+listincity.get(c).getId(), "", -1, 0);
							if(listhotel.size()>0){
								hotel=listhotel.get(0);
							}
							
							String IMAGE="";
							
							if(l.getMedia()!=null&&l.getMedia().getPictureReference()!=null){
								
								FreeHotelSearchWebServiceStub.PictureReference[] aa = l.getMedia().getPictureReference();
								if(aa.length>0){
									for(int i=0;i<aa.length;i++){
										IMAGE+=aa[i].getLink()+",";
									}
								}
								hotel.setCheckdesc(IMAGE);//图片路径
							}

							//酒店基本信息
							if(l.getName()!=null){
							hotel.setName(l.getName());
							}
							if(l.getName()!=null){
							hotel.setEnname(l.getName());
							}
							hotel.setCityid(listincity.get(c).getId());
							if(l.getHotelAddress().getStreet()!=null){
							hotel.setAddress(l.getHotelAddress().getStreet());
							}
							if(l.getHotelAddress().getGeographicCoordinates()!=null){
							hotel.setLat(l.getHotelAddress().getGeographicCoordinates().getLatitude());
							hotel.setLng(l.getHotelAddress().getGeographicCoordinates().getLongitude());
							}
							if(l.getPrice().getAmountAfterTax()!=null){
							hotel.setStartprice(Double.parseDouble(l.getPrice().getAmountAfterTax()+""));
							}
							if(l.getPrice().getCurrency()!=null){
							hotel.setPricetype(l.getPrice().getCurrency());
							}
							hotel.setType(2);//1,国内  2,国际
							if(l.getLocation().getPostalCodeRange()!=null){
							hotel.setPostcode(l.getLocation().getPostalCodeRange());//邮编
							}
							hotel.setCountryid(listincity.get(c).getCountryid());
							if(l.getRatingHotelDe()!=null){
							String star=l.getRatingHotelDe();
							hotel.setHotelcode(l.getPropertyNumber()+"");
							if(star.indexOf(",")!=-1){
								
								star=star.split(",")[0];
							}
							hotel.setStar(Integer.parseInt(star));
							}
							hotel.setState(3);
							if(listhotel.size()>0){
								Server.getInstance().getHotelService().updateHotelIgnoreNull(hotel);
								System.out.println("修改了");
							}else{
							Server.getInstance().getHotelService().createHotel(hotel);
							
							System.out.println("增加了");
							}
						
						}
						}
						
			}
							}
						}
						
			}
						System.out.println("完了");
					} catch (RuntimeException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
	}
}