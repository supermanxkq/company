package com.ccservice.jl;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.ccservice.b2b2c.atom.qunar.PHUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.hotelall.Hotelall;

/**
 * 
 * @author wzc 根据本地酒店查找去哪酒店id
 * 
 */
public class FindQunarHotelIdMany {
	
	public static void main(String[] args) throws Exception {
		//new FindQunarHotelId().updateQunarHotelId(6);
		new FindQunarHotelIdMany().updateQunarHotelAllId();
	}
	/**
	 * 更新正式酒店qunarid
	 * 
	 * @throws Exception
	 */
	public void updateQunarHotelAllId() throws Exception {
		List<City> citys = Server.getInstance().getHotelService().findAllCity("where C_QUNARCODE IS NOT NULL and id in (select distinct c_cityid from t_hotelall where c_qunarid is null or c_qunarid='')","order by id asc", -1, 0);
		System.out.println(citys.size());
		WritableWorkbook wwb;
		String fos = "D:\\查询酒店002.xls";
		wwb = Workbook.createWorkbook(new File(fos));
		WritableSheet ws = wwb.createSheet("酒店", 11);
		int k=0;
		for (City city : citys) {
			ws.addCell(new Label(0, 0, "酒店id"));
			ws.addCell(new Label(1, 0, "去哪id"));
			ws.addCell(new Label(2, 0, "本地酒店名称"));
			ws.addCell(new Label(3, 0, "去哪酒店名称"));
			ws.addCell(new Label(4, 0, "本地酒店地址"));
			ws.addCell(new Label(5, 0, "去哪酒店地址"));
			List<Hotelall> hotelstemp = Server.getInstance().getHotelService()
					.findAllHotelall("where  c_cityid="+ city.getId()+" and (c_qunarid is null or c_qunarid='')", "ORDER BY ID ASC", -1, 0);
			System.out.println(hotelstemp.size());
			label:for (Hotelall hotel : hotelstemp) {
				String hotelname = hotel.getName().replaceAll("TF", "").trim();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, 1);
				String strUrl = "http://hotel.qunar.com/render/renderAPI.jsp?"
						+ "showAllCondition=1"
						+ "&attrs=L0F4L3C1,ZO1FcGJH,J6TkcChI,HCEm2cI6,08F7hM4i,8dksuR_,YRHLp-jc,pl6clDL0,HFn32cI6,vf_x4Gjt,2XkzJryU,vNfnYBK6,TDoolO-H,pk4QaDyF,x0oSHP6u,z4VVfNJo,5_VrVbqO,VAuXapLv,U1ur4rJN,px3FxFdF,pk4QaDyF,HGYGeXFY,6X7_yoo3,0Ie44fNU,dDjWmcqr,MMObDrW4,ownT_WG6,yYdMIL83,Y0LTFGFh,8F2RFLSO"
						+ "&showBrandInfo=2"
						+ "&showNonPrice=1"
						+ "&showFullRoom=1"
						+ "&showPromotion=1"
						+ "&showTopHotel=1"
						+ "&showGroupShop=1"
						+ "&output=json1.1"
						// +"&v=0.05416518063093645"
						+ "&cityurl=" + city.getQunarcode() + "&q="
						+ URLEncoder.encode(hotelname, "utf-8") + "&fromDate="
						+ sdf.format(new Date()) + "&toDate="
						+ sdf.format(cal.getTime()) + "&requestor=RT_HSLIST"
						// +"&filterid=3a7d82ac-a754-447e-a366-009e0b1984d8_A"
						+ "&requestTime=" + System.currentTimeMillis()
						+ "&needFP=1" + "&__jscallback=XQScript_11";
				System.out.println(strUrl);
				String json = PHUtil.submitPost(strUrl, "").toString();
				String tmp=""; 
				try {
					tmp= json.substring(json.indexOf("{"), json.length());
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				JSONObject datas = JSONObject.fromObject(tmp.subSequence(0, tmp
						.lastIndexOf(")")));
				JSONArray hotels = datas.getJSONArray("hotels");
				for (int i = 0; i < hotels.size(); i++) {
					JSONObject hotelt = hotels.getJSONObject(i);
					JSONObject attrs = hotelt.getJSONObject("attrs");
					JSONObject selected = hotelt.getJSONObject("selected");
					String dis=hotelt.getString("normalDistance");
					String address=attrs.getString("hotelAddress");
					if (selected.containsKey("fts2")) {
						if(dis.equals("0")){
							if (!(hotelt.getString("id")).equals(hotel
									.getQunarId())) {
								System.out.println("------------------------------------------------------------------");
								System.out.println("-----------"+k+"----------------");
								System.out.println("酒店名称："+hotel.getName());
								System.out.println("酒店地址："+hotel.getAddress());
								
								System.out.println("去哪酒店名称："+attrs.getString("hotelName"));
								System.out.println("去哪酒店地址："+address);
								System.out.println("-------------------------------------------------------------------");
								hotel.setQunarId(hotelt.getString("id"));
								ws.addCell(new Label(0, k + 1, hotel.getId() + ""));
								ws.addCell(new Label(1, k + 1, hotel.getQunarId()));
								if(hotel.getName()!=null){
								ws.addCell(new Label(2, k + 1, hotel.getName()));
								}
								ws.addCell(new Label(3, k + 1, attrs.getString("hotelName")));
								if(hotel.getAddress()!=null){
									ws.addCell(new Label(4, k + 1,hotel.getAddress()));
								}
								ws.addCell(new Label(5, k + 1, address));
								
								//Server.getInstance().getHotelService().updateHotelallIgnoreNull(hotel);
								k++;
								continue label;
							}
						}
					}
				}
			}
		}
		wwb.write();
		wwb.close();
		System.out.println("over.......");
	}
	/**
	 * 更新QunarHotelId
	 * 
	 * @throws Exception
	 */
	public void updateQunarHotelId(int sourcetype) throws Exception {
		List<City> citys = Server
				.getInstance().getHotelService().findAllCity("where C_QUNARCODE IS NOT NULL  "," order by id desc ", -1, 0);
		System.out.println(citys.size());
		for (City city : citys) {
			System.out.println(city.getName());
			List<Hotel> hotelstemp = Server.getInstance().getHotelService()
					.findAllHotel("where c_sourcetype="+sourcetype+"  and c_qunarid is null and c_cityid="
									+ city.getId(), "ORDER BY ID ASC", -1, 0);
			System.out.println(hotelstemp.size());
			for (Hotel hotel : hotelstemp) {
				String hotelname = hotel.getName().replaceAll("TF", "").trim();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_MONTH, 1);
				String strUrl = "http://hotel.qunar.com/render/renderAPI.jsp?"
						+ "showAllCondition=1"
						+ "&attrs=L0F4L3C1,ZO1FcGJH,J6TkcChI,HCEm2cI6,08F7hM4i,8dksuR_,YRHLp-jc,pl6clDL0,HFn32cI6,vf_x4Gjt,2XkzJryU,vNfnYBK6,TDoolO-H,pk4QaDyF,x0oSHP6u,z4VVfNJo,5_VrVbqO,VAuXapLv,U1ur4rJN,px3FxFdF,pk4QaDyF,HGYGeXFY,6X7_yoo3,0Ie44fNU,dDjWmcqr,MMObDrW4,ownT_WG6,yYdMIL83,Y0LTFGFh,8F2RFLSO"
						+ "&showBrandInfo=2"
						+ "&showNonPrice=1"
						+ "&showFullRoom=1"
						+ "&showPromotion=1"
						+ "&showTopHotel=1"
						+ "&showGroupShop=1"
						+ "&output=json1.1"
						// +"&v=0.05416518063093645"
						+ "&cityurl=" + city.getQunarcode() + "&q="
						+ URLEncoder.encode(hotelname, "utf-8") + "&fromDate="
						+ sdf.format(new Date()) + "&toDate="
						+ sdf.format(cal.getTime()) + "&requestor=RT_HSLIST"
						// +"&filterid=3a7d82ac-a754-447e-a366-009e0b1984d8_A"
						+ "&requestTime=" + System.currentTimeMillis()
						+ "&needFP=1" + "&__jscallback=XQScript_11";
				System.out.println(strUrl);
				String json = PHUtil.submitPost(strUrl, "").toString();
				String tmp = json.substring(json.indexOf("{"), json.length());
				JSONObject datas = JSONObject.fromObject(tmp.subSequence(0, tmp
						.lastIndexOf(")")));
				JSONArray hotels = datas.getJSONArray("hotels");
				boolean flag = true;
				for (int i = 0; i < hotels.size(); i++) {
					JSONObject hotelt = hotels.getJSONObject(i);
					JSONObject attrs = hotelt.getJSONObject("attrs");
					JSONObject selected = hotelt.getJSONObject("selected");
					if (selected.containsKey("fts2")) {
						if (flag) {
							flag = false;
							if (!(hotelt.getString("id")).equals(hotel
									.getQunarId())) {
								System.out.println(attrs.getString("hotelName")
										+ ":" + hotelt.getString("id") + " ID:"
										+ hotel.getId() + " Sourcetype:"
										+ hotel.getSourcetype() + " 原QunarId:"
										+ hotel.getQunarId() + " 更改后QunarId:"
										+ hotelt.getString("id"));
								hotel.setQunarId(hotelt.getString("id"));
								Server.getInstance().getHotelService()
										.updateHotelIgnoreNull(hotel);
							}

						} else {
							System.out.println(attrs.getString("hotelName")
									+ ":" + hotelt.getString("id") + " ID:"
									+ hotel.getId() + " Sourcetype:"
									+ hotel.getSourcetype()
									+ " fts2出现多次 将更改QunarId为空");
							hotel.setQunarId("");
							Server.getInstance().getHotelService()
									.updateHotelIgnoreNull(hotel);
						}
					}
				}
			}
		}
	}
}
