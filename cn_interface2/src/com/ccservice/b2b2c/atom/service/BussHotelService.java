package com.ccservice.b2b2c.atom.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.bedtype.Bedtype;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hmhotelprice.Hmhotelprice;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.province.Province;
import com.ccservice.b2b2c.base.region.Region;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.bussinessentry.GetHotelListPara;
import com.ccservice.bussinessman.DDS2Stub;
import com.ccservice.bussinessman.DDS2Stub.Build区域By城市;
import com.ccservice.bussinessman.DDS2Stub.Build区域By城市Response;
import com.ccservice.bussinessman.DDS2Stub.Build城市;
import com.ccservice.bussinessman.DDS2Stub.Build城市Response;
import com.ccservice.bussinessman.DDS2Stub.Build省份By国家;
import com.ccservice.bussinessman.DDS2Stub.Build省份By国家Response;
import com.ccservice.bussinessman.DDS2Stub.GetHotelList;
import com.ccservice.bussinessman.DDS2Stub.GetHotelListResponse;
import com.ccservice.bussinessman.DDS2Stub.GetSingleHotel;
import com.ccservice.bussinessman.DDS2Stub.GetSingleHotelResponse;
import com.ccservice.bussinessmancode.BussinessLogin;
import com.ccservice.huamin.WriteLog;

public class BussHotelService implements IBussHotelService {

	@SuppressWarnings("unchecked")
	public void getCityAndRegion() throws Exception {
		DDS2Stub stub = new DDS2Stub();
		//本地省份
		List<Province> localProvinces = Server.getInstance().getHotelService().findAllProvince("WHERE ID < 135", "", -1, 0);
		Build省份By国家 province = new Build省份By国家();
		province.setLoginToken(BussinessLogin.getLoginToken());
		province.setNationId(1);
		Build省份By国家Response pres = stub.build省份By国家(province);
		JSONObject pobj = JSONObject.fromObject(pres.getBuild省份By国家Result());
		JSONArray pary = pobj.getJSONArray("城市");
		Map<String,Long> pmap = new HashMap<String, Long>();
		Map<String,String> pmapnames = new HashMap<String, String>();
		for(int i = 0 ; i < pary.size() ; i++){
			JSONObject obj = pary.getJSONObject(i);
			String pname = obj.getString("DHU01Name");
			String pid = obj.getString("DHU01Level1");
			if("内蒙古自治区".equals(pname))pname = "内蒙古";
			if("广西壮族自治区".equals(pname))pname = "广西";
			if("西藏自治区".equals(pname))pname = "西藏";
			if("宁夏回族自治区".equals(pname))pname = "宁夏";
			if("新疆维吾尔自治区".equals(pname))pname = "新疆";
			if("香港特别行政区".equals(pname))pname = "香港";
			if("澳门特别行政区".equals(pname))pname = "澳门";
			for(Province p:localProvinces){
				if(pname.equals(p.getName().trim()) || (pname).equals(p.getName().trim()+"省")){
					pmap.put(pid, p.getId());
					pmapnames.put(pid, pname);
					if(p.getBuscode()==null || !p.getBuscode().equals(obj.getString("DHU01Id"))){
						p.setBuscode(obj.getString("DHU01Id"));
						Server.getInstance().getHotelService().updateProvinceIgnoreNull(p);
						System.out.println("更新省份---"+p.getName());
					}
					break;
				}
			}
		}
		//本地城市
		List<City> localCitys = Server.getInstance().getHotelService().findAllCity("WHERE C_TYPE IS NULL or C_TYPE = 1", "ORDER BY ID", -1, 0);
		//生意人城市
		Build城市 city = new Build城市();
		city.setLoginToken(BussinessLogin.getLoginToken());
		Build城市Response cres = stub.build城市(city);
		String result = cres.getBuild城市Result();
		JSONObject obj = JSONObject.fromObject(result);
		JSONArray ary = obj.getJSONArray("城市");
		int count = 0;
		for(int i = 0 ; i < ary.size() ; i++){
			JSONObject cobj = ary.getJSONObject(i);
			String cityId = cobj.getString("DHU01Id"); //生意人城市ID
			String cityName = cobj.getString("DHU01Name");//城市名称
			String cityPinyin = cobj.getString("DHU01Pinyin");//城市拼音
			String cityProvince = cobj.getString("DHU01Level1");//省份
			String DHU01Level2 = cobj.getString("DHU01Level2");//城市
			if(cityId==null||"".equals(cityId.trim())||cityProvince==null||"".equals(cityProvince)){
				continue;
			}
			if("平遥县".equals(cityName))cityName = "平遥";
			if("延边朝鲜族自治州".equals(cityName))cityName="延边";
			if("浦江县".equals(cityName))cityName="浦江";
			if("恩施土家族苗族自治州".equals(cityName))cityName="恩施";
			if("湘西土家族苗族自治州".equals(cityName))cityName="湘西";
			if("陵水黎族自治县".equals(cityName))cityName="陵水";
			if("九龙城区".equals(cityName))cityName="九龙";
			if("伊犁哈萨克自治州".equals(cityName))cityName="伊犁";
			if("海西蒙古族藏族自治州".equals(cityName))cityName="海西";
			if("博尔塔拉蒙古自治州".equals(cityName))cityName="博尔塔拉";
			if("楚雄彝族自治州".equals(cityName))cityName="楚雄";
			if("红河哈尼族彝族自治州".equals(cityName))cityName="红河州";
			if("文山壮族苗族自治州".equals(cityName))cityName="文山";
			if("德宏傣族景颇族自治州".equals(cityName))cityName="德宏州";
			if("怒江傈僳族自治州".equals(cityName))cityName="怒江";
			if("甘南藏族自治州".equals(cityName))cityName="甘南";
			if("甘孜藏族自治州".equals(cityName))cityName="甘孜";
			if("海北藏族自治州".equals(cityName))cityName="海北州";
			if("海南藏族自治州".equals(cityName))cityName="海南州";
			if("玉树藏族自治州".equals(cityName))cityName="玉树";
			if("海西".equals(cityName))cityName="海西州";
			if("海东地区".equals(cityName))cityName="海东州";
			if("甘孜".equals(cityName))cityName="甘孜州";
			if("思茅市".equals(cityName))cityName="普洱思茅";
			if("凉山彝族自治州".equals(cityName))cityName="凉山州";
			if("阿坝藏族羌族自治州".equals(cityName))cityName="阿坝州";
			if("荷泽市".equals(cityName))cityName="菏泽";
			if("西双版纳傣族自治州".equals(cityName))cityName="西双版纳";
			if("大理白族自治州".equals(cityName))cityName="大理";
			int repeat = 0 ;
			City localCity = new City();
			for(City c:localCitys){
				if(cityId.equals(c.getBusscode())){
					localCity = c;
					repeat++;
					break;
				}
				if(pmap.get(cityProvince).equals(c.getProvinceid())){
					if(cityName.equals(c.getName()) || cityName.equals(c.getName()+"市") || cityPinyin.equals(c.getEnname())){
						repeat++;
						if(repeat==1){
							localCity = c;
						}else{
							break;
						}
					}
				}
			}
			if(repeat==0){
				//广西崇左市
				if("451400".equals(cityId)){
					City newcity = new City();
					newcity.setName("崇左");
					newcity.setEnname("CHONGZUO");
					newcity.setSname("CHONGZUO");
					newcity.setProvinceid(121l);
					newcity.setLanguage(0);
					newcity.setCountryid(168l);
					newcity.setType(1l);
					newcity.setBusscode(cityId);
					newcity = Server.getServer().getHotelService().createCity(newcity);
					localCity = newcity;
				}else{
					System.out.println("未匹配上："+pmapnames.get(cityProvince)+"---"+pmap.get(cityProvince)+"---"+cityId+"---"+cityName+"---"+cityPinyin+"---"+(++count));
				}
			}else if(repeat==1){
				if(localCity.getId()>0 && localCity.getBusscode()==null){
					localCity.setBusscode(cityId);
					Server.getInstance().getHotelService().updateCityIgnoreNull(localCity);
					System.out.println("更新："+cityId+"---"+cityName+"---"+cityPinyin+"---"+(++count)+"---"+localCity.getName()+"---"+localCity.getEnname());
				}
			}else{
				System.out.println("可能是重复城市："+pmapnames.get(cityProvince)+"---"+pmap.get(cityProvince)+"---"+cityId+"---"+cityName+"---"+cityPinyin+"---"+(++count)+"---"+localCity.getName()+"---"+localCity.getEnname());
			}
			//区域
			if(localCity.getId()>0){
				//本地区域
				List<Region> localRegions = Server.getInstance().getHotelService().findAllRegion("WHERE C_CITYID="+localCity.getId(), "", -1, 0);
				if(DHU01Level2!=null && !"".equals(DHU01Level2.trim())){
					Build区域By城市 regionreq = new Build区域By城市();
					regionreq.setLoginToken(BussinessLogin.getLoginToken());
					regionreq.setPId(Integer.parseInt(cityProvince));//省份
					regionreq.setCityId(Integer.parseInt(DHU01Level2));//城市
					Build区域By城市Response regionResponse = stub.build区域By城市(regionreq);
					String regionResult = regionResponse.getBuild区域By城市Result();
					if(regionResult!=null && regionResult.contains("DHU01Id")){
						JSONObject robj = JSONObject.fromObject(regionResult);
						JSONArray rary = robj.getJSONArray("城市");
						for(int m = 0 ; m < rary.size() ; m++){
							JSONObject objr = rary.getJSONObject(m);
							String regionid = objr.getString("DHU01Id");
							String regionname = objr.getString("DHU01Name");
							if(regionid==null || "".equals(regionid.trim()) ||
									regionname==null || "".equals(regionname.trim())){
								continue;
							}else{
								Region region = new Region();
								region.setName(regionname);
								region.setCityid(localCity.getId());
								region.setLanguage(0);
								region.setCountryid(localCity.getCountryid());
								region.setBusscode(regionid);
								for(Region r:localRegions){
									if(regionid.equals(r.getBusscode())){
										region.setId(r.getId());
										System.out.println("匹配区域："+localCity.getName()+"---"+r.getName());
										break;
									}
									if(regionname.equals(r.getName())){
										region.setId(r.getId());
										if(r.getType()==null || "".equals(r.getType()))region.setType("2");//行政区
										Server.getInstance().getHotelService().updateRegionIgnoreNull(region);
										System.out.println("更新区域："+localCity.getName()+"---"+r.getName());
										break;
									}
								}
								if(region.getId()==0){
									region.setType("2");//行政区
									Server.getInstance().getHotelService().createRegion(region);
									System.out.println("新增区域："+localCity.getName()+"---"+region.getName());
								}
							}
						}
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void getHotelRoomPrice() throws Exception{

		long starttime = System.currentTimeMillis();
		
		List<City> citys = Server.getInstance().getHotelService().findAllCity("where C_BUSSCODE is not null", "ORDER BY ID", -1, 0);
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = sdf.parse(sdf.format(cal1.getTime()));
		cal1.setTime(date1);
		cal1.add(Calendar.DAY_OF_MONTH, 0);
		Date date2 = sdf.parse(sdf.format(cal2.getTime()));
		cal2.setTime(date2);
		cal2.add(Calendar.DAY_OF_MONTH, 30);
		
		int pagesize = 30; //每页返回30家酒店
		int count = citys.size();
		
		for(City c:citys){
			System.out.println("城市：" + c.getName()+"---"+(count--));
			try {
				GetHotelListPara para = new GetHotelListPara();
				para.setFCityId(Integer.parseInt(c.getBusscode()));
				para.setPageIndex(1);
				para.setPageSize(pagesize);
				para.setFInDate(cal1);
				para.setFOutDate(cal2);
				para.setFPaymentMode(1);//转帐
				para.setFUserLevel(2);//同行
				int total = GetHotelList(para);
				// 总页数
				int pageNum = total % pagesize == 0 ? total / pagesize : ((total / pagesize) + 1);
				if (pageNum > 0) {
					System.out.println("总页数：" + pageNum);
					for (int j = 2; j <= pageNum; j++) {
						GetHotelListPara parat = new GetHotelListPara();
						parat.setFCityId(Integer.parseInt(c.getBusscode()));
						parat.setPageIndex(j);
						parat.setPageSize(pagesize);
						parat.setFInDate(cal1);
						parat.setFOutDate(cal2);
						GetHotelList(parat);
						Thread.sleep(3000);
					}
				}
			} catch (Exception e) {
				System.out.println("更新生意人酒店信息出现异常，异常信息为："+e.getMessage());
			}
		}
		
		long endtime = System.currentTimeMillis();
		System.out.println("更新生意人酒店信息完成，消耗时间："+(endtime-starttime)/1000/60 + "分");
	}
	
	@SuppressWarnings("unchecked")
	private int GetHotelList(GetHotelListPara para) throws Exception {
		DDS2Stub stub = new DDS2Stub();
		GetHotelListResponse response = new GetHotelListResponse();
		GetHotelList getHotelList = new GetHotelList();

		getHotelList.setLoginToken(BussinessLogin.getLoginToken());
		getHotelList.setFCityId(para.getFCityId());
		if (para.getFDHI01RegionId() > 0) {
			getHotelList.setFDHI01DHP02Id(para.getFDHI01RegionId());
		}
		if (para.getFDHI01Name() != null) {
			getHotelList.setFDHI01Name(para.getFDHI01Name());
		}
		if (para.getFDHI01DHP02Id() > 0) {
			getHotelList.setFDHI01DHP02Id(para.getFDHI01DHP02Id());
		}
		if (para.getFDHI01Address() != null) {
			getHotelList.setFDHI01Address(para.getFDHI01Address());
		}
		if (para.getFRoomName() != null) {
			getHotelList.setFRoomName(para.getFRoomName());
		}
		getHotelList.setFOrderByStar(para.getFOrderByStar());
		getHotelList.setFOrderByRecLevels(para.getFOrderByRecLevels());
		getHotelList.setFOrderByPrice(para.getFOrderByPrice());
		getHotelList.setFInDate(para.getFInDate());
		getHotelList.setFOutDate(para.getFOutDate());
		getHotelList.setFPaymentMode(para.getFPaymentMode());
		getHotelList.setFUserLevel(para.getFUserLevel());
		getHotelList.setFPromotionsRat(para.getFPromotionsRat());
		getHotelList.setFSmaillPrice(para.getFSmaillPrice());
		getHotelList.setFBigPrice(para.getFBigPrice());
		if (para.getFLandmark() != null) {
			getHotelList.setFLandmark(para.getFLandmark());
			if (para.getFPonitX() != null) {
				getHotelList.setFPonitX(para.getFPonitX());
			}
			if (para.getFPonitY() != null) {
				getHotelList.setFPonitY(para.getFPonitY());
			}
		}
		getHotelList.setFHotelDistance(para.getFHotelDistance());
		getHotelList.setFShowRoomCount(para.getFShowRoomCount());
		getHotelList.setPageIndex(para.getPageIndex());
		getHotelList.setPageSize(para.getPageSize());
		response = stub.getHotelList(getHotelList);
		int totalpage = response.getTotalPage();
		String jsonresult = response.getGetHotelListResult();
		//解析
		JSONObject obj=null;
		try {
			obj = JSONObject.fromObject(jsonresult.replaceAll("\n", "").replaceAll("\\[\\{\\{", "\\[\\{").replaceAll("\\}\\}\\]", "\\}\\]"));
			//酒店信息
			Map<String,Long> hotelmap = new HashMap<String, Long>();//<生意人酒店编码,本地酒店编码>，用于房型取本地酒店编码
			Map<String,Long> roommap = new HashMap<String,Long>();//<生意人房型编码,本地房型编码>，用于价格获取本地房型编码
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat timeformat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String startDate = sdf.format(para.getFInDate().getTime());
			String endDate = sdf.format(para.getFOutDate().getTime());
			JSONArray Tables = obj.getJSONArray("Table");
			for (int i = 0; i < Tables.size(); i++) {
				JSONObject Table = Tables.getJSONObject(i);
				Hotel hotel = new Hotel();
				hotel.setState(3);
				hotel.setType(1);//国内
				hotel.setPaytype(2l);//预付
				String hotelid = Table.getString("DHI01Id");// 酒店Id
				if(hotelid==null || "".equals(hotelid.trim())){
					continue;
				}
				hotel.setHotelcode(hotelid);
				String hotelname = Table.getString("DHI01Name");// 酒店名称 String
				if(hotelname==null || "".equals(hotelname.trim())){
					continue;
				}
				hotel.setName(hotelname);
				String englishname = Table.getString("DHI01EnglishName");// 酒店英文名称
				hotel.setEnname(englishname);
				String DHI01PinyinCode = Table.getString("DHI01PinyinCode");// 酒店拼音简写
				hotel.setJpname(DHI01PinyinCode);
				String DHI01Nation = Table.getString("DHI01Nation");// 国家Id Int
				if("1".equals(DHI01Nation)){
					hotel.setCountryid(168l);//中国
				}else{
					continue;
				}
				String DHI01CityId = Table.getString("DHI01CityId");// 城市Id
				if(DHI01CityId==null || "".equals(DHI01CityId)){
					continue;
				}
				List<City> cityes = 
					Server.getInstance().getHotelService().findAllCity("where C_BUSSCODE='"+DHI01CityId+"'", "", -1, 0);
				if (cityes!=null && cityes.size() > 0) {
					hotel.setCityid(cityes.get(0).getId());
					hotel.setProvinceid(cityes.get(0).getProvinceid());
					hotel.setCountryid(cityes.get(0).getCountryid());
				}
				String DHI01RegionId = Table.getString("DHI01RegionId");// 区域Id
				if(DHI01RegionId!=null && !"".equals(DHI01RegionId.trim()) && hotel.getCityid()!=null && hotel.getCityid()>0){
					List<Region> regions = 
						Server.getInstance().getHotelService().findAllRegion(
								"where C_BUSSCODE='"+DHI01RegionId+"' and C_CITYID="+hotel.getCityid(), "", -1, 0);
					if (regions!=null && regions.size() > 0) {
						hotel.setRegionid1(regions.get(0).getId());
					}
				}
				/**
				 * 1 五星级 2 四星级 3 三星级 4 二星级 5 经济型 6 准五星 7 准四星 8 准三星
				 */
				int DHI01DHP02Id = Table.getInt("DHI01DHP02Id");// 星级Id Int
				if (DHI01DHP02Id == 1) {
					hotel.setStar(5);
				} else if (DHI01DHP02Id == 2) {
					hotel.setStar(4);
				} else if (DHI01DHP02Id == 3) {
					hotel.setStar(3);
				} else if (DHI01DHP02Id == 4) {
					hotel.setStar(2);
				} else if (DHI01DHP02Id == 5) {
					hotel.setStar(1);
				} else if (DHI01DHP02Id == 6) {
					hotel.setStar(16);
				} else if (DHI01DHP02Id == 7) {
					hotel.setStar(13);
				} else if (DHI01DHP02Id == 8) {
					hotel.setStar(10);
				}
				String DHI01Traffic = Table.getString("DHI01Traffic");// 交通 String
				hotel.setTrafficinfo(DHI01Traffic);
				String DHI01Around = Table.getString("DHI01Around");// 周围环境 String
				String DHI01Server = Table.getString("DHI01Server");// 宾馆服务项目 String
				hotel.setServiceitem(DHI01Server);
				String DHI01Entertainments = Table.getString("DHI01Entertainments");// 宾馆餐饮娱乐与健身设施
				hotel.setFootitem(DHI01Entertainments);
				String DHI01Introduce = Table.getString("DHI01Introduce");// 酒店介绍
				hotel.setDescription(DHI01Introduce);
				String DHI01Explains = Table.getString("DHI01Explains");// 酒店特别提示
				hotel.setAvailPolicy(DHI01Explains);
				String DHI01StartDatetemp = Table.getString("DHI01StartDate");
				String DHI01TrimDatetemp = Table.getString("DHI01TrimDate");
				String DHI01StartDate = "";
				String DHI01TrimDate = "";
				try {
					if (!"".equals(DHI01StartDatetemp)) {
						DHI01StartDate = sdf.format(sdf.parse(DHI01StartDatetemp));// 开业时间
						Date date = sdf.parse(DHI01StartDate);
						hotel.setOpendate(new java.sql.Date(date.getTime()));
					}
	
					if (!"".equals(DHI01TrimDatetemp)) {
						DHI01TrimDate = sdf.format(sdf.parse(DHI01TrimDatetemp));// 装修时间
						hotel.setRepaildate(DHI01TrimDate);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
	
				String DHI01SiteUrl = Table.getString("DHI01SiteUrl");// 酒店网址
				String DHI01Telephone = Table.getString("DHI01Telephone");// 总机电话号码
				hotel.setTortell(DHI01Telephone);
				String DHI01Address = Table.getString("DHI01Address");// 酒店地址
				hotel.setAddress(DHI01Address);
				hotel.setSourcetype(5l);
				String DHI01IsAirportServer = Table.getString("DHI01IsAirportServer");// 是否有接机服务
				hotel.setAirportservice(DHI01IsAirportServer);
				// Int
				// 0 否
				// / 1
				// 是
				String DHI01DHP04Id = Table.getString("DHI01DHP04Id");// 推荐等级
				// Int
				// 0 否
				// / 1
				// 是
				String DHI01IsShow = Table.getString("DHI01IsShow");// 前台是否显示 Int 0 否 /
	
				String DHI10WarrantyInfo = Table.getString("DHI10WarrantyInfo");// 担保信息
				hotel.setWarrantyInfo(DHI10WarrantyInfo);
				String DHI01IsAcceptForeign = Table.getString("DHI01IsAcceptForeign");// 是否接受外宾
				if(DHI01IsAcceptForeign!=null && !"".equals(DHI01IsAcceptForeign.trim())){
					hotel.setAcceptForeign(Integer.parseInt(DHI01IsAcceptForeign));
				}
				// 1 是
				String DHI01PointX = Table.getString("DHI01PointX");// 纬度 Float 百度坐标
				String DHI01PointY = Table.getString("DHI01PointY");// 经度 Float 百度坐标
				if (DHI01PointX != null && !"".equals(DHI01PointX)) {
					hotel.setLng(Double.parseDouble(DHI01PointX));
				}
				if (DHI01PointY != null && !"".equals(DHI01PointY)) {
					hotel.setLat(Double.parseDouble(DHI01PointY));
				}
	
				String DHU02Name = Table.getString("DHU02Name");// 国家名称
				String PDHU01Name = Table.getString("PDHU01Name");// PDHU01Name省份名称
				String PDHU01EnglishName = Table.getString("PDHU01EnglishName");// 省份英文名称
				String PDHU01Pinyin = Table.getString("PDHU01Pinyin");// PDHU01Pinyin省份拼音
				String CDHU01Name = Table.getString("CDHU01Name");// 城市名称
				String CDHU01EnglishName = Table.getString("CDHU01EnglishName");// 城市英文名称
				String CDHU01Pinyin = Table.getString("CDHU01Pinyin");// 城市拼音
				String RDHU01Name = Table.getString("RDHU01Name");// 区域名称
				String RDHU01EnglishName = Table.getString("RDHU01EnglishName");// 区域英文名称
				String RDHU01Pinyin = Table.getString("RDHU01Pinyin");// 区域拼音
				String DHP02Name = Table.getString("DHP02Name");// 星级名称
				String DHP04Name = Table.getString("DHP04Name");// 推荐等级名称
				String DHI01Distances = Table.getString("DHI01Distances");// 公里数
				String DHI01LowestPrices = Table.getString("DHI01LowestPrices");// 最低价格
				if(DHI01LowestPrices!=null && !"".equals(DHI01LowestPrices.trim())){
					hotel.setStartprice(Double.parseDouble(DHI01LowestPrices));
				}
				List<Hotel> hotels = Server.getInstance().getHotelService()
						.findAllHotel("where C_HOTELCODE='" + hotel.getHotelcode() + "' AND C_SOURCETYPE = 5", "", -1,0);
				if (hotels!=null && hotels.size() > 0) {
					hotel.setId(hotels.get(0).getId());
					System.out.println("更新：" + hotel.getName());
					Server.getInstance().getHotelService().updateHotelIgnoreNull(hotel);
				} else {
					System.out.println("添加：" + hotel.getName());
					hotel = Server.getInstance().getHotelService().createHotel(hotel);
				}
				hotelmap.put(hotel.getHotelcode(), hotel.getId());
			}
			//房型信息
			JSONArray Table1s = obj.getJSONArray("Table1");
			for (int i = 0; i < Table1s.size(); i++) {
				JSONObject Table1 = Table1s.getJSONObject(i);
				Roomtype room = new Roomtype();
				//生意人房型编码
				room.setRoomcode(Table1.getInt("DHI03Id")+"");
				//通过生意人酒店编码获取本地酒店编码
				Long hotelid = hotelmap.get(Table1.getInt("DHI03DHI01Id")+"");
				if(hotelid!=null && hotelid>0){
					room.setHotelid(hotelid);
				}else{
					continue;
				}
				String roomname = Table1.getString("DHI11Name");
				int paytype = Table1.getInt("DHI08DOP02Id");//支付方式 : 1 转账 / 2 现付 / 3 全额预付
				int usertype = Table1.getInt("DHI10DUP02Id");//客户等级 ：1 散客 / 2同行
				if(roomname==null || "".equals(roomname.trim()) || paytype != 1 || usertype!=2){
					continue;
				}
				room.setName(roomname);
				room.setAreadesc(Table1.getString("DHI03Dimension"));//面积描述
				room.setRoomdesc(Table1.getString("DHI03Info"));//房型描述
				String DHI03DHP05Id = "BUS_" + Table1.getInt("DHI03DHP05Id");//增加生意人标识，床型ID
				String DHP05Name = Table1.getString("DHP05Name");//床型名称
				if(!"BUS_".equals(DHI03DHP05Id)){
					Bedtype bed = new Bedtype();
					bed.setType(DHI03DHP05Id);
					bed.setTypename(DHP05Name);
					List<Bedtype> localbeds = 
						Server.getInstance().getHotelService().
							findAllBedtype("where C_TYPE='"+DHI03DHP05Id+"'", "", -1, 0);
					if(localbeds==null||localbeds.size()==0){
						if(DHP05Name!=null && !"".equals(DHP05Name.trim())){
							bed = Server.getInstance().getHotelService().createBedtype(bed); 
							System.out.println("[录入生意人酒店相关的房型床型数据]向C_BEDTYPE表添加一条数据,添加成功,ID:"+ bed.getId());
						}else{
							continue;
						}
					}else{
						Bedtype localbed = localbeds.get(0);
						bed.setId(localbed.getId());
						if(localbed.getTypename()==null || !localbed.getTypename().equals(DHP05Name)){
							Server.getInstance().getHotelService().updateBedtypeIgnoreNull(bed);
							System.out.println("[录入生意人酒店相关的房型床型数据]更改C_BEDTYPE表一条数据,更改成功");
						}else{
							System.out.println("[录入生意人酒店相关的房型床型数据]匹配C_BEDTYPE表一条数据,匹配成功,ID:"+ bed.getId());
						}
					}
					room.setBed(Integer.parseInt(bed.getId()+""));
				}
				room.setState(1);//房型状态 - 1:可用
				room.setLayer(Table1.getString("DHI03Storey"));//楼层
				String bf = Table1.getString("DHI10BreakfastDesc");//早餐
				if(bf==null || "80001".equals(bf)){
					room.setBreakfast(0);//无早
				}else if("81000".equals(bf) || "81001".equals(bf) || "81101".equals(bf) || "81201".equals(bf) || "81311".equals(bf)){
					room.setBreakfast(1);//单早
				}else if("82001".equals(bf) || "82101".equals(bf) || "82201".equals(bf) || "82311".equals(bf)){
					room.setBreakfast(2);//双早
				}else if("83001".equals(bf)){
					room.setBreakfast(3);//三早
				}else if("84001".equals(bf)){
					room.setBreakfast(4);//四早
				}else if("86001".equals(bf)){
					room.setBreakfast(6);//六早
				}else{
					room.setBreakfast(0);//无早
				}
				String wideband = Table1.getString("DHI03IsOnLine");//宽带
				room.setWideband(0);
				if(wideband!=null && "1".equals(wideband)){
					String DHI03OnLineRemark = Table1.getString("DHI03OnLineRemark");
					if("宽带免费".equals(DHI03OnLineRemark) || "免费有线".equals(DHI03OnLineRemark)){
						room.setWideband(1);
						room.setWidedesc("免费");
					}
					if("收费有线".equals(DHI03OnLineRemark)){
						room.setWideband(2);
						room.setWidedesc("收费");
					}
				}
				room.setLanguage(0);
				room.setLastupdatetime(timeformat.format(new Date()));
				List<Roomtype> roomtypeList = 
					Server.getInstance().getHotelService().findAllRoomtype(
						"where C_ROOMCODE='" + room.getRoomcode()+ "' and  C_BED='"+ room.getBed()+ "' and C_HOTELID="+ room.getHotelid(), "", -1, 0);
				if (roomtypeList != null && roomtypeList.size() > 0) {
					room.setId(roomtypeList.get(0).getId());
					Server.getInstance().getHotelService().updateRoomtypeIgnoreNull(room);
					System.out.println("[录入生意人酒店相关的房型床型数据]更改C_ROOMTYPE表一条数据,更改成功");
				}else{
					room = Server.getInstance().getHotelService().createRoomtype(room);
					System.out.println("[录入生意人酒店相关的房型床型数据]向C_ROOMTYPE表添加一条数据,添加成功,ID:" + room.getId());
				}
				roommap.put(room.getRoomcode(), room.getId());
			}
			//价格信息
			Map<Long,String> deleteflag = new HashMap<Long,String>();
			JSONArray Table2s = obj.getJSONArray("Table2");
			for (int i = 0; i < Table2s.size(); i++) {
				JSONObject Table2 = Table2s.getJSONObject(i);
				//通过生意人酒店编码获取本地酒店编码
				Long hotelid = hotelmap.get(Table2.getInt("DHI03DHI01Id")+"");
				Long roomid = roommap.get(Table2.getInt("DHI03Id")+"");
				int paytype = Table2.getInt("DHI08DOP02Id");//支付方式 : 1 转账 / 2 现付 / 3 全额预付
				int DHI10DUP02Id = Table2.getInt("DHI10DUP02Id");//客户等级 1 散客 / 2 同行
				if(hotelid!=null && hotelid>0 && paytype==1 && DHI10DUP02Id==2){
					//删除价格
					if(deleteflag.get(hotelid)==null || "".equals(deleteflag.get(hotelid).trim())){
						Server.getInstance().getSystemService().findMapResultBySql(
								" delete from t_hmhotelprice where c_hotelid=" + hotelid +
								" and C_STATEDATE<'"+endDate+"' and C_STATEDATE>='"+startDate+"'",null); 
						deleteflag.put(hotelid, "Delete");
					}
					if(roomid!=null && roomid>0){
						Hmhotelprice price = new Hmhotelprice();
						price.setHotelid(hotelid);
						price.setRoomtypeid(roomid);
						//提前预订天数
						String DHP09Days = Table2.getString("DHP09Days");
						if(DHP09Days==null||"".equals(DHP09Days.trim())){
							price.setAdvancedday(0l);
						}else{
							price.setAdvancedday(Long.parseLong(DHP09Days));
						}
						//早餐
						String bf = Table2.getString("DHI10BreakfastDesc");
						if(bf==null || "80001".equals(bf)){
							price.setBf(0l);//无早
						}else if("81000".equals(bf) || "81001".equals(bf) || "81101".equals(bf) || "81201".equals(bf) || "81311".equals(bf)){
							price.setBf(1l);//单早
						}else if("82001".equals(bf) || "82101".equals(bf) || "82201".equals(bf) || "82311".equals(bf)){
							price.setBf(2l);//双早
						}else if("83001".equals(bf)){
							price.setBf(3l);//三早
						}else if("84001".equals(bf)){
							price.setBf(4l);//四早
						}else if("86001".equals(bf)){
							price.setBf(6l);//六早
						}else{
							price.setBf(0l);//无早
						}
						price.setStatedate(sdf.format(sdf.parse(Table2.getString("DHI09Date"))));//日期
						price.setPrice(Table2.getDouble("DHI10Pay"));//价格
						if(price.getPrice().doubleValue()<=12){//生意人12块以下不卖，接口数据没作处理
							continue;
						}
						String DHP08Amount = Table2.getString("DHP08Amount");//最低预定量
						if(DHP08Amount==null || "".equals(DHP08Amount.trim())){
							price.setMinday(1l);//最少停留晚数
						}else{
							price.setMinday(Long.parseLong(DHP08Amount));//最少停留晚数
						}
						price.setPriceoffer(price.getPrice()+20);
						price.setAbleornot(1);
						price.setUpdatetime(timeformat.format(new Date()));
						String DHI18SurplusInventory = Table2.getString("DHI18SurplusInventory");// 房型剩余库存
						if(DHI18SurplusInventory!=null && !"".equals(DHI18SurplusInventory.trim())){
							price.setYuliuNum(Long.parseLong(DHI18SurplusInventory));
						}else{
							price.setYuliuNum(0l);
						}
						//房态
						String DHI18RoomState = Table2.getString("DHI18RoomState");
						if(DHI18RoomState==null || "".equals(DHI18RoomState.trim()) || "3".equals(DHI18RoomState)){
							price.setRoomstatus(0l);//申请
							price.setYuliuNum(0l);
							price.setIsallot("N");
						}else if("1".equals(DHI18RoomState)){
							price.setRoomstatus(0l);//即时确认
							price.setIsallot("Y");
						}else if("4".equals(DHI18RoomState)){
							price.setRoomstatus(0l);//紧张
							price.setIsallot("N");
						}else{
							price.setRoomstatus(1l);//满房
							price.setIsallot("C");
						}
						price.setCityid(Server.getInstance().getHotelService().findHotel(hotelid).getCityid()+"");
						price.setSourcetype("5");//生意人
						String DHI10DHP07Id = Table2.getString("DHI10DHP07Id");//促销等级
						String DHP07Name = Table2.getString("DHP07Name");//促销等级名称
						if(DHI10DHP07Id==null || "".equals(DHI10DHP07Id.trim()) ||
								DHP07Name==null || "".equals(DHP07Name.trim()) ||
									"1".equals(DHI10DHP07Id.trim())){
							price.setProd("1");//无
							price.setRatetype("");//价格类型名称
						}else{
							price.setProd(DHI10DHP07Id.trim());//4：限时房；5：特价房；6：限量房
							price.setRatetype(DHP07Name);//价格类型名称
						}
						price.setContractid("");
						price.setContractver("");
						price.setCur("RMB");
						price.setTicket("0");
						//取消规则
						price.setCanceldesc("");
						String DHI10DHP11Id = Table2.getString("DHI10DHP11Id");
						if(DHI10DHP11Id!=null && !"".equals(DHI10DHP11Id.trim())){
							String canceldesc = "";
							JSONArray Table4s = obj.getJSONArray("Table4");
							for (int j = 0; j < Table4s.size(); j++) {
								JSONObject Table4 = Table4s.getJSONObject(j);
								int DHP11Id = Table4.getInt("DHP11Id");
								if(DHP11Id==Integer.parseInt(DHI10DHP11Id)){
									int DHP12DayCount = Table4.getInt("DHP12DayCount") - 1;//提前取消天数
									int DHP12Fee = Table4.getInt("DHP12Fee");//扣款类型
									if(DHP12DayCount<0){
										canceldesc += "取消收取";
									}else if(DHP12DayCount==0){
										canceldesc += "入住当天取消收取";
									}else{
										canceldesc += "入住前"+DHP12DayCount+"天取消收取";
									}
									if(DHP12Fee==1){
										canceldesc += "全额房费；";
									}else if(DHP12Fee==2){
										canceldesc += "首晚房费；";
									}else if(DHP12Fee==3){
										canceldesc += "最后一晚房费；";
									}else if(DHP12Fee==4){
										canceldesc += "最高一晚房费；";
									}else if(DHP12Fee==5){//扣款类型为“无”
										canceldesc += "全额房费；";
									}else if(DHP12Fee==6){
										canceldesc += "50%全额房费；";
									}else{
										canceldesc += "全额房费；";
									}
								}
							}
							if(canceldesc.endsWith("；")){//入住当天取消扣除首晚房费；
								canceldesc = canceldesc.substring(0, canceldesc.length()-1);
							}
							price.setCanceldesc(canceldesc);
						}
						price = Server.getInstance().getHotelService().createHmhotelprice(price);
						System.out.println("[录入生意人酒店相关的价格]向T_HMHOTELPRICE表添加一条数据,添加成功,ID:" + price.getId());
					}
				}
			}	
		} catch (Exception ex) {
			if(!"JSONObject[\"DHI01Id\"] not found.".equals(ex.getMessage())){
				ex.printStackTrace();
				WriteLog.write("json解析问题", "json字符串："+jsonresult+"\n"+ex.getMessage());
			}
		}
		return totalpage;
	}
	
	public String getSingleHotel(String hotelCode,String roomCode,String checkInDate,String checkOutDate)throws Exception{
		if(hotelCode==null || "".equals(hotelCode.trim()) || 
				checkInDate==null || "".equals(checkInDate.trim()) || 
					checkOutDate==null || "".equals(checkOutDate.trim())){
			throw new Exception("获取酒店信息，缺少必要参数.");
		}
		DDS2Stub stub = new DDS2Stub();
		//封装请求
		GetSingleHotel req = new GetSingleHotel();
		req.setLoginToken(BussinessLogin.getLoginToken());
		req.setFHotelId(Integer.parseInt(hotelCode));
		if(roomCode!=null && !"".equals(roomCode.trim())){
			req.setFRoomId(Integer.parseInt(roomCode));
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar incal = Calendar.getInstance();
		incal.setTime(sdf.parse(checkInDate));
		req.setFInDate(incal);
		
		Calendar outcal = Calendar.getInstance();
		outcal.setTime(sdf.parse(checkOutDate));
		req.setFOutDate(outcal);
		
		req.setFPaymentMode(1);//转帐
		req.setFUserLevel(2);//同行
		//请求、回复
		GetSingleHotelResponse res = stub.getSingleHotel(req);
		//回复的JSON数据
		return res.getGetSingleHotelResult();
	}
}
























