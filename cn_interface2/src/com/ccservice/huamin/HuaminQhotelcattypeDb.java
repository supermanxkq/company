package com.ccservice.huamin;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.HMRequestUitl;
import com.ccservice.b2b2c.base.bedtype.Bedtype;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.province.Province;
import com.ccservice.b2b2c.base.region.Region;
import com.ccservice.b2b2c.base.roomtype.Roomtype;

/**
 * 
 * 
 * @Caless:HuaminQhotelcattypeDb.java
 * @ClassDesc:录入华闽酒店相关的房型床型数据
 * @Author:胡灿
 * @Date:2012-12-15 上午11:07:01
 * @Company: 航天华有(北京)科技有限公司
 * @Copyright: Copyright (c) 2012
 * @version: 1.0
 */
public class HuaminQhotelcattypeDb {
	/**
	 *  查询华闽酒店相关的房型床型数据
	 * @param requestBean
	 */
	private  void getQhotelcattype(HuaminqhotelcattypeRequestBean requestBean) {
		List<Hotel> hotelList = Server.getInstance().getHotelService().findAllHotel("where C_SOURCETYPE=3 and c_cityid=101","", -1, 0);
		int i=hotelList.size();
		for(Hotel hotel:hotelList){
			System.out.println("剩余酒店数量："+i--);
		String url = HMRequestUitl.getHMRequestUrlHeader() + "&api=qhotelcattype&p_lang=SIM";
	
		
		//1  P_COMPANY  公司賬號 为必须 已写死在url中
		//2  P_ID  網頁用戶名稱 为必须 已写死在url中
		//3  P_PASS 網頁密碼 为必须 已写死在url中
		//4  P_LANG ENG-英文  (預設)  CHN-中文繁體  SIM-中文簡體 为必须 已写死在url中
		
		//5  P_COUNTRY 國家代碼 
		if(requestBean.getCountry()!=null&&!"".equals(requestBean.getCountry())){
			url+="&p_country="+requestBean.getCountry();
		}
		
		//6  P_AREA  地區代碼 
		if(requestBean.getArea()!=null&&!"".equals(requestBean.getArea())){
			url+="&p_area="+requestBean.getArea();
		}
		
		//7  P_CITY 城市代碼  
		if(requestBean.getCity()!=null&&!"".equals(requestBean.getCity())){
			url+="&p_city="+requestBean.getCity();
		}
		
		//8  P_CAT  房型代碼 
		if(requestBean.getCat()!=null&&!"".equals(requestBean.getCat())){
			url+="&p_cat="+requestBean.getCat();
		}
		//9  P_CATNAME 房型名稱 
		if(requestBean.getCatname()!=null&&!"".equals(requestBean.getCatname())){
			url+="&p_catname="+requestBean.getCatname();
		}
		//10  P_GRADE 酒店星級  5, 4, 3, 2, 1  
		if(requestBean.getGrade()!=null&&!"".equals(requestBean.getGrade())){
			url+="&p_grade="+requestBean.getGrade();
		}
		//11  P_HOTEL 酒店代碼   TODO
//		if(requestBean.getHotel()!=null&&!"".equals(requestBean.getHotel())){
//			url+="&p_hotel="+requestBean.getHotel();
//		}
		url+="&p_hotel="+hotel.getHotelcode();
		
		//12  P_HOTELNAME 酒店名稱 
		if(requestBean.getHotelname()!=null&&!"".equals(requestBean.getHotelname())){
			url+="&p_hotelname="+requestBean.getHotelname();
		}
		//13  P_CREATED 日-月-年eg.18-Oct-12
		if(requestBean.getCreated()!=null&&!"".equals(requestBean.getCreated())){
			url+="&p_created="+requestBean.getCreated();
		}
		//14  P_MODIFY  日-月-年eg.18-Oct-12 
		if(requestBean.getModify()!=null&&!"".equals(requestBean.getModify())){
			url+="&p_modify="+requestBean.getModify();
		}
			
				System.out.println("[查询华闽酒店相关的房型床型数据]url:" + url);
			String str = Util.getStr(url);

			System.out.println(str);
			parsexml_cancel(str);
		}
	}

	/**
	 *录入华闽酒店相关的房型床型数据
	 * 
	 * @param str
	 */
	private  void parsexml_cancel(String xmlstr) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xmlstr));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			Element hotels = result.getChild("HOTELS");
			if (result.getChildren().size() > 2) {
				List<Element> hotel = hotels.getChildren("HOTEL");
				for (Element hotelbean : hotel) {
					
					String hotelcode = hotelbean.getChildText("HOTEL");// 酒店代碼 不可为空
					String hotelname = hotelbean.getChildText("HOTELNAME");//酒店名稱 
					String area = hotelbean.getChildText("AREA");// 地區代碼
					String city = hotelbean.getChildText("CITY");//城市代碼
					String dist = hotelbean.getChildText("DIST");//區域代碼
					String address = hotelbean.getChildText("ADDRESS");//酒店地址 
					String tel = hotelbean.getChildText("TEL");//酒店電話
					String fax = hotelbean.getChildText("FAX");//酒店傳真
					String grade = hotelbean.getChildText("GRADE");//酒店星級5, 4, 3, 2, 1 
					String latitue = hotelbean.getChildText("LATITUE");//緯度
					String longitude = hotelbean.getChildText("LONGITUDE");//經度
					String hoteldesc = hotelbean.getChildText("HOTELDESC");//酒店簡介
					//根据C_HOTELCODE查找酒店 最多只能找到一条
					List<Hotel> hotelList = Server.getInstance().getHotelService().findAllHotel("where C_HOTELCODE='" +hotelcode+"'","", -1, 0);
						Hotel h;
						boolean falg=true;//true进行创建 false进行修改
					if(hotelList!=null&&hotelList.size()>0){
						h=hotelList.get(0);
						falg=false;
					}else{
						 h=new Hotel();
						h.setId(0L);
						h.setHotelcode(hotelcode);
						h.setCountryid(168L);
					}
					
					if(hotelname!=null&&!"".equals(hotelname)){
						h.setName(hotelname);
					}
					if (area != null && !"".equals(area)) {
						// 查找地区代码 最多只能找到一条
						List<Province> provinceList = Server.getInstance().getHotelService().findAllProvince("where C_HUAMINCODE='" + area+"'", "",-1, 0);
						if (provinceList != null && provinceList.size() > 0) {
							h.setProvinceid(provinceList.get(0).getId());
							System.out.println("[录入华闽酒店相关的房型床型数据]地区C_PROVINCEID"+provinceList.get(0).getId());
							if (city != null && !"".equals(city)) {
								// 根据C_HUAMINCODE Provinceid查找城市代码 最多只能找到一条
								List<City> cityList = Server.getInstance().getHotelService().findAllCity("where C_HUAMINCODE='"+ city+"'"+ "and C_PROVINCEID="+ provinceList.get(0).getId(), "",-1, 0);
								if (provinceList != null&& provinceList.size() > 0) {
									h.setCityid(cityList.get(0).getId());
									System.out.println("[录入华闽酒店相关的房型床型数据]城市C_CITYID"+cityList.get(0).getId());
									if (dist != null && !"".equals(dist)) {
										// 根据C_HUAMINCODE CITYID查找区域代码 最多只能找到一条
										List<Region> regionList = Server.getInstance().getHotelService().findAllRegion("where C_HUAMINCODE='"+ dist+ "' and C_CITYID="+ cityList.get(0).getId(),"", -1, 0);
										if (regionList != null&& regionList.size() > 0) {
											h.setRegionid1(regionList.get(0).getId());
											System.out.println("[录入华闽酒店相关的房型床型数据]区域"+regionList.get(0).getId());
										}

									}

								}
							}
						}
					}
				
					if(address!=null&&!"".equals(address)){
						h.setAddress(address);
					}
					
					if(tel!=null&&!"".equals(tel)){
						h.setTortell(tel);
					}
					
					if(fax!=null&&!"".equals(fax)){
						h.setFaxdesc(fax);
					}
					if(grade!=null&&!"".equals(grade)){
						if (grade.equals("N") || grade.equals("B")) {
							h.setStar(1);
						} else {
						h.setStar(Integer.valueOf(grade));
						}
					}
					
					if(latitue!=null&&!"".equals(latitue)){
						h.setLng(Double.valueOf(latitue));
					}
					if(longitude!=null&&!"".equals(longitude)){
						h.setLat(Double.valueOf(longitude));
					}
					
					if(hoteldesc!=null&&!"".equals(hoteldesc)){
						h.setDescription(hoteldesc);
					}
					String hotelid=h.getId()+"";
					if(falg){
						System.out.println("[录入华闽酒店相关的房型床型数据]向C_HOTEL表添加一条数据");
						Hotel hote = Server.getInstance().getHotelService().createHotel(h);
						System.out.println("[录入华闽酒店相关的房型床型数据]向C_HOTEL表添加一条数据,添加成功,ID:"+hote.getId());
						 hotelid=hote.getId()+"";
					}else{
						System.out.println("[录入华闽酒店相关的房型床型数据]更改C_HOTEL表一条数据");
						Server.getInstance().getHotelService().updateHotelIgnoreNull(h);
						System.out.println("[录入华闽酒店相关的房型床型数据]更改C_HOTEL表一条数据,更改成功");
					}
					
					Element rooms = hotelbean.getChild("ROOMS");
					List<Element> room = rooms.getChildren("ROOM");
					for (Element roombean : room) {
						String cat = roombean.getChildText("CAT");
						String catname = roombean.getChildText("CATNAME");
						String type = roombean.getChildText("TYPE");
						String typename = roombean.getChildText("TYPENAME");
						
						String bedtypeid=null;
						if(type!=null&&!"".equals(type)){
							List<Bedtype> bedtypeList = Server.getInstance().getHotelService().findAllBedtype("where C_TYPE='" +type+"'","", -1, 0);
							Bedtype bedtype;
							boolean falgBedtype=true;
							if(bedtypeList!=null&&bedtypeList.size()>0){
								bedtype=bedtypeList.get(0);
								falgBedtype=false;
								bedtypeid=bedtype.getId()+"";
							}else{
								bedtype=new Bedtype();
								bedtype.setId(0L);
								bedtype.setType(type);
							}
						if(typename!=null&&!"".equals(typename)){
							bedtype.setTypename(typename);
						}
					//	bedtype.setMaxguest(0L);//最多入住人数 默认为0
						if(falgBedtype){
							System.out.println("[录入华闽酒店相关的房型床型数据]向C_BEDTYPE表添加一条数据");
							Bedtype bt = Server.getInstance().getHotelService().createBedtype(bedtype);
							System.out.println("[录入华闽酒店相关的房型床型数据]向C_BEDTYPE表添加一条数据,添加成功,ID:"+bt.getId());
							 bedtypeid=bt.getId()+"";
						}else{
							System.out.println("[录入华闽酒店相关的房型床型数据]更改C_BEDTYPE表一条数据");
							Server.getInstance().getHotelService().updateBedtypeIgnoreNull(bedtype);
							System.out.println("[录入华闽酒店相关的房型床型数据]更改C_BEDTYPE表一条数据,更改成功");
						}
						}
						
						if(cat!=null&&!"".equals(cat)){
							List<Roomtype> roomtypeList = Server.getInstance().getHotelService().findAllRoomtype("where C_ROOMCODE='" +cat+"' and  C_BED='"+bedtypeid+"' and   C_HOTELID="+hotelid,"", -1, 0);
							Roomtype rt;
							boolean falgRoomtype=true;
							if(roomtypeList!=null&&roomtypeList.size()>0){
								rt=roomtypeList.get(0);
								falgRoomtype=false;
							}else{
								rt=new Roomtype();
								rt.setId(0L);
								rt.setRoomcode(cat);
							}
						if(catname!=null&&!"".equals(catname)){
							rt.setName(catname);
						}
						rt.setHotelid(Long.valueOf(hotelid));
						if(bedtypeid!=null){
							rt.setBed(Integer.valueOf(bedtypeid));
						}
						 
						if(falgRoomtype){
							System.out.println("[录入华闽酒店相关的房型床型数据]向C_ROOMTYPE表添加一条数据");
							Roomtype bt = Server.getInstance().getHotelService().createRoomtype(rt);
							System.out.println("[录入华闽酒店相关的房型床型数据]向C_ROOMTYPE表添加一条数据,添加成功,ID:"+bt.getId());
						}else{
							System.out.println("[录入华闽酒店相关的房型床型数据]更改C_ROOMTYPE表一条数据");
							Server.getInstance().getHotelService().updateRoomtypeIgnoreNull(rt);
							System.out.println("[录入华闽酒店相关的房型床型数据]更改C_ROOMTYPE表一条数据,更改成功");
						}
							
						
						}
						System.out.println(cat+"-"+catname+"-"+type+"-"+typename);
					}
				}
			} 

			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("查询华闽酒店相关的房型床型数据", "错误信息:" + resultco + ","+ errormessage);
			}
		} catch (Exception e) {
			WriteLog.write("查询华闽酒店相关的房型床型数据","返回数据格式有问题,错误数据:" +xmlstr);
			e.printStackTrace();
		}

	}
	
	/**
	 * 日期格式转换 如 "2012-11-08"转换后"08-Nov-12"
	 * @param datestr
	 * @return
	 */
	public String getdatestr(String datestr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Locale l = new Locale("en");
		Date date;
		try {
			date = sdf.parse(datestr);
			String day = String.format("%td", date);
			String month = String.format(l, "%tb", date);
			String year = String.format("%ty", date);
			datestr = day + "-" + month + "-" + year;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(datestr);
		return datestr;
	}

	/**
	 * 获取指定日期的前一天
	 * 
	 * @return
	 */
	public static Date CatchYesterday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.roll(Calendar.DATE, -1);
		Date newdate = cal.getTime();
		return newdate;
	}
public static void main(String[] args) {
		HuaminqhotelcattypeRequestBean requestBean=new HuaminqhotelcattypeRequestBean();
		requestBean.setCountry("CHN");
		requestBean.setCity("BJS");
		new HuaminQhotelcattypeDb().getQhotelcattype(requestBean);
}
}
