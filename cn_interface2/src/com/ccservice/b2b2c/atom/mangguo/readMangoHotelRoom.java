package com.ccservice.b2b2c.atom.mangguo;
import java.io.File;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;

import com.caucho.hessian.client.HessianProxyFactory;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelprice.Hotelprice;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.b2b2c.base.service.IHotelService;
import com.mango.hotel.MGHotelService;
import com.mango.hotel.MGHotelServiceProxy;
import com.mango.hotel.MangoAuthor;
import com.mango.hotel.RatePlan;
import com.mango.hotel.SingleHotelRequest;
import com.mango.hotel.SingleHotelResponse;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
public class readMangoHotelRoom {    
  
  public static void main(String[] args) throws Exception {   
	  String url = "http://localhost:8080/lthk_service/service/";

		HessianProxyFactory factory = new HessianProxyFactory();
		IHotelService servier = (IHotelService) factory.create(IHotelService.class,
				url + IHotelService.class.getSimpleName());
		File file=new File("d://e.xls");
		WritableWorkbook workbook=Workbook.createWorkbook(file); 
		WritableSheet sheet = workbook.createSheet("e", 0); 
		Label label0 = new Label(0, 0, "id");    
		sheet.addCell(label0); 
		Label label1 = new Label(1, 0, "name");    
		sheet.addCell(label1);
		Label label2 = new Label(2, 0, "e");    
		sheet.addCell(label2);
		int sheetindex=1;
		//13822   20384   14230
		for(int count=13822;count<=20384;count++)
		{
			Hotel hotel=servier.findHotel(count);
			System.out.println("当前酒店ID："+hotel.getId());
			MGHotelService hotelService=new MGHotelServiceProxy();
			SingleHotelRequest hotelRequest=new SingleHotelRequest();
			MangoAuthor author=new MangoAuthor();
			author.setChannel("SXTJ");
			author.setKey("02E882DC62692E9F29D8709BFD0369D7E09C5A4F");
			SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			author.setTimeStamp(dateFormat.format(new Timestamp(System.currentTimeMillis())));
			hotelRequest.setAuthor(author);
			City city=servier.findCity(hotel.getCityid());
			hotelRequest.setCityCode(city.getAreacode());
			hotelRequest.setHotelCode(hotel.getHotelcode());
			hotelRequest.setCheckInDate("2011-06-23");
			hotelRequest.setCheckOutDate("2011-06-28");
			try {
				SingleHotelResponse hotelResponse= hotelService.singleHotel(hotelRequest);
				if(hotelResponse.getResult().getValue()==1)
				{
					com.mango.hotel.Hotel[] list=hotelResponse.getHotelsRes();
					if(list!=null&&list.length>0)
					{
						for(int i=0;i<list.length;i++)
						{
							com.mango.hotel.Hotel hotel2=list[i];
							hotel.setRooms(Integer.parseInt(hotel2.getLayer_count()));
							hotel.setRepaildate(hotel2.getPracice_date());
							hotel.setFax1(hotel2.getWorking_fax());
							if(hotel2.getHotel_star().equals("五星"))
							{
								hotel.setStar(9);
							}else if(hotel2.getHotel_star().equals("准五星"))
							{
								hotel.setStar(8);
							}else if(hotel2.getHotel_star().equals("四星"))
							{
								hotel.setStar(7);
							}else if(hotel2.getHotel_star().equals("准四星"))
							{
								hotel.setStar(6);
							}else if(hotel2.getHotel_star().equals("三星"))
							{
								hotel.setStar(5);
							}else if(hotel2.getHotel_star().equals("准三星"))
							{
								hotel.setStar(4);
							}else if(hotel2.getHotel_star().equals("二星"))
							{
								hotel.setStar(3);
							}else{
								hotel.setStar(1);
							}
							hotel.setDescription(hotel2.getChn_hotel_introduce());
							hotel.setFootitem(hotel2.getMeal_fixtrue());
							hotel.setPlayitem(hotel2.getFree_service());
							hotel.setTrafficinfo(hotel2.getAround_view());
							hotel.setServiceitem(hotel2.getRoom_fixtrue());
							try
							{
							hotel.setLat(Double.parseDouble(hotel2.getLatitude()));
							hotel.setLng(Double.parseDouble(hotel2.getLongitude()));
							}catch(Exception e)
							{
								System.out.println(hotel.getId()+e.toString());
							}
							hotel.setCheckdesc(hotel2.getPictures());
							hotel.setTortell(hotel2.getTelephone());
							try
							{
							servier.updateHotelIgnoreNull(hotel);
							}catch (Exception e) {
								// TODO: handle exception
								//System.out.println("酒店更新失败");
							}
							com.mango.hotel.RoomType[] listroom=hotel2.getRoomTypeList();
							if(listroom!=null)
							{
								for(int ix=0;ix<listroom.length;ix++)
								{
									com.mango.hotel.RoomType roomType2=listroom[ix];
									List<Roomtype> listroomtype=servier.findAllRoomtype(" where "+Roomtype.COL_hotelid+" = "+hotel.getId()+" and "+Roomtype.COL_name+" = '"+roomType2.getRoom_name()+"'", "", -1, 0);
									Roomtype roomtype=new Roomtype();
									if(listroomtype!=null&&listroomtype.size()>0)
									{
										roomtype=listroomtype.get(0);
									}
									roomtype.setName(roomType2.getRoom_name());
									roomtype.setAreadesc(roomType2.getAcreage());
									roomtype.setHotelid(hotel.getId());
									roomtype.setRoomcode(roomType2.getRoomTypeCode());
									roomtype.setState(1);
									if(roomType2.getBed_type().equals("大床,双床,"))
									{
										roomtype.setBed(4);
									}else if(roomType2.getBed_type().equals("大床,"))
									{
										roomtype.setBed(3);
									}else if(roomType2.getBed_type().equals("双床,"))
									{
										roomtype.setBed(2);
									}else if(roomType2.getBed_type().equals("单床,"))
									{
										roomtype.setBed(1);
									}else
									{
										roomtype.setBed(5);
									}
									roomtype.setAreadesc(roomType2.getAcreage());
									if(listroomtype!=null&&listroomtype.size()>0)
									{
										servier.updateRoomtypeIgnoreNull(roomtype);
										//System.out.println("更新的roomtype:"+roomtype);
									}else
									{
										roomtype=servier.createRoomtype(roomtype);
										//System.out.println("创建的roomtype:"+roomtype);
									}
									
									
									Hotelprice hotelprice=new Hotelprice();
									List<Hotelprice> listhotelprice2=servier.findAllHotelprice(" where "+Hotelprice.COL_roomid+" = "+roomtype.getId()+" and "+Hotelprice.COL_datenumber+" = '2011-05'", "", -1, 0);
									if(listhotelprice2!=null&&listhotelprice2.size()>0)
									{
										hotelprice=listhotelprice2.get(0);
									}
									hotelprice.setDatenumber("2011-05");
									hotelprice.setHotelid(hotel.getId());
									hotelprice.setRoomid(roomtype.getId());
									RatePlan[] ratePlan=roomType2.getRatePlanList();
									if(ratePlan!=null&&ratePlan.length>0)
									{
										hotelprice.setDeptprice(ratePlan[0].getMarkert_price());
										for(int iii=0;iii<ratePlan.length;iii++)
										{
											String[] datearray=ratePlan[iii].getAbleSaleDate().split("-");
											String day="";
											if(datearray[2].substring(0,1).equals("0"))
											{
												day=datearray[2].substring(1);
											}else
											{
												day=datearray[2];
											}
													//if(ratePlan[iii].getRateplanName().equals("标准")){
													Hotelprice.class.getMethod("setNo"+day,Double.class).invoke(hotelprice,Double.parseDouble(ratePlan[iii].getSale_price()));
												//}
											//System.out.println(ratePlan[iii].getMarkert_price()+"---"+ratePlan[iii].getSale_price()+"---"+ratePlan[iii].getRateplanCode()+"----"+ratePlan[iii].getRateplanName()+"---"+ratePlan[iii].getAbleSaleDate()+"----"+ratePlan[iii].getNeed_assure()+"---"+ratePlan[iii].getAssure_type()+"---"+ratePlan[iii].getAssure_money()+"---"+ratePlan[iii].getAssure_con());
										}
										hotelprice.setRateplancode(ratePlan[0].getRateplanCode());
										hotelprice.setRateplanname(ratePlan[0].getRateplanName());
										hotelprice.setMoytype(ratePlan[0].getCurrencyType());
									}
									hotelprice.setLanguage(0);
									
									if(listhotelprice2!=null&&listhotelprice2.size()>0)
									{
										servier.updateHotelpriceIgnoreNull(hotelprice);
										//System.out.println("更新了价格："+hotelprice);
									}else
									{
										hotelprice=servier.createHotelprice(hotelprice);
										//System.out.println("创建了价格："+hotelprice);
									}
									
								}
							}
						}
					}
				}else
				{
					System.out.println(hotelResponse.getResult().getMessage());
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				Label labelid = new Label(0, sheetindex, city.getId()+"");    
		        sheet.addCell(labelid); 
		        Label labelcity = new Label(1, sheetindex, city.getName()+"/"+city.getAreacode());    
		        sheet.addCell(labelcity);
		        Label labelpic = new Label(2, sheetindex, e.toString());    
		        sheet.addCell(labelpic); 
		        sheetindex++;
			}
		}
        workbook.write();    
        workbook.close(); 
        
        System.out.println("完了");
  }
}  