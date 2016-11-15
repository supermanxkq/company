package com.ccservice.b2b2c.atom.hthy;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.service.HTCHotelService;

/**
 * @author wzc 汇通客对外接口
 */
public class HTHYInterfaceOffer extends HttpServlet {
	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml");
		PrintWriter out = response.getWriter();
		String method = request.getParameter("method");
		String str="";
		try {
			HTCHotelService htcservice = new HTCHotelService();
			if (method != null) {
				if (method.equals("getproplist")) {
					// 获取酒店列表
					String date = request.getParameter("date");
					str = htcservice.getproplist(date);
				} else if (method.equals("getProperty")) {
					// 获取酒店基本信息
					String hotelid = request.getParameter("hotelid");
					str = htcservice.getProperty(Long.valueOf(hotelid));
				} else if (method.equals("getDesc")) {
					// 获取酒店详细信息
					String hotelid = request.getParameter("hotelid");
					str = htcservice.getDesc(Long.valueOf(hotelid));
				} else if (method.equals("getDescByType")) {
					// 根据类型获取酒店详细信息
					String type = request.getParameter("type");
					String hotelid = request.getParameter("hotelid");
					str = htcservice.getDescByType(type, Long.valueOf(hotelid));
				} else if (method.equals("getRoomObj")) {
					// 获取酒店所有房间代码与详细信息
					String hotelid = request.getParameter("hotelid");
					str = htcservice.getRoomObj(Long.valueOf(hotelid));
				} else if (method.equals("getRoomObjByType")) {
					// 根据房间类型代码查询房间详细信息
					String type = request.getParameter("type");
					String hotelid = request.getParameter("hotelid");
					str = htcservice.getRoomObjByType(type, Long.valueOf(hotelid));
				} else if (method.equals("getRateObj")) {
					// 获取指定酒店所有价格代码的详细信息
					String hotelid = request.getParameter("hotelid");
					String date = request.getParameter("date");
					date="";
					str = htcservice.getRateObj(Long.valueOf(hotelid),date);
				} else if (method.equals("getRateObjByType")) {
					// 获取指定酒店指定价格代码详细信息
					String type = request.getParameter("type");
					String hotelid = request.getParameter("hotelid");
					str = htcservice.getRateObjByType(type,Long.valueOf(hotelid));
				} else if (method.equals("getPlanObj")) {
					// 获取酒店所有计划代码的详细信息
					String hotelid = request.getParameter("hotelid");
					str = htcservice.getPlanObj(Long.valueOf(hotelid));
				} else if (method.equals("getPlanObjByPlanid")) {
					// 获取酒店指定计划代码的详细信息
					String planid = request.getParameter("planid");
					String hotelid = request.getParameter("hotelid");
					str = htcservice.getPlanObjByPlanid(planid, Long.valueOf(hotelid));
				} else if (method.equals("getImage")) {
					// 获取指定酒店图片
				//	String hotelid = request.getParameter("hotelid");
				//	str = htcservice.getImage(Long.valueOf(hotelid));
				} else if (method.equals("getPropresv")) {
					// 获取指定酒店订单服务
					String channel = request.getParameter("channel");
					String confnum = request.getParameter("confnum");
					String iata = request.getParameter("iata");
					str = htcservice.getPropresv(channel, confnum, iata);
				} else if (method.equals("getResvaudit")) {
					// 查询订单审核状态
					String cnfnum = request.getParameter("cnfnum");
					String iata = request.getParameter("iata");
					str = htcservice.getResvaudit(cnfnum, iata);
				} else if (method.equals("hotelSearch")) {
					// 搜索酒店列表的可用性信息
					String date = request.getParameter("date");
					String nights = request.getParameter("nights");
					String ratestyle = request.getParameter("ratestyle");
					Long  hotelid = Long.valueOf(request.getParameter("prop"));
					String proplv = request.getParameter("proplv");
					String city = request.getParameter("city");
					String district = request.getParameter("district");
					String tradearea = request.getParameter("tradearea");
					String guestposition = request.getParameter("guestposition");
					String keywords = request.getParameter("keywords");
					String pageindex = request.getParameter("pageindex"); 
					String pagesize = request.getParameter("pagesize");
					str = htcservice.hotelSearch(date, nights, ratestyle, hotelid,
							proplv, city, district, tradearea, guestposition,
							keywords, pageindex, pagesize);
				} else if (method.equals("hotelSerchById")) {
					// 搜索单个酒店计划价格的可用性信息
					String date = request.getParameter("date");
					String nights = request.getParameter("nights");
					String ratestyle = request.getParameter("ratestyle");
					long hotelid = Long.valueOf(request.getParameter("prop"));
					String proplv = request.getParameter("proplv");
					String city = request.getParameter("city");
					String district = request.getParameter("district");
					String tradearea = request.getParameter("tradearea");
					String guestposition = request.getParameter("guestposition");
					String keywords = request.getParameter("keywords");
					String pageindex = request.getParameter("pageindex");
					String pagesize = request.getParameter("pagesize");
					str = htcservice.hotelSerchById(date, nights, ratestyle, hotelid,
							proplv, city, district, tradearea, guestposition,
							keywords, pageindex, pagesize);
				} else if (method.equals("hotelSerchAll")) {
					// 批量搜索酒店信息及计划价格的可用性信息
					String date = request.getParameter("date");
					String nights = request.getParameter("nights");
					String ratestyle = request.getParameter("ratestyle");
					long hotelid = Long.valueOf(request.getParameter("prop"));
					String proplv = request.getParameter("proplv");
					String city = request.getParameter("city");
					String district = request.getParameter("district");
					String tradearea = request.getParameter("tradearea");
					String guestposition = request.getParameter("guestposition");
					String keywords = request.getParameter("keywords");
					String pageindex = request.getParameter("pageindex");
					String pagesize = request.getParameter("pagesize");
					str = htcservice.hotelSerchAll(date, nights, ratestyle, hotelid,
							proplv, city, district, tradearea, guestposition,
							keywords, pageindex, pagesize);
				} else if (method.equals("getCrateMap")) {
					// 获取酒店所有房型所有价格计划的可用性信息
					String hotelid = request.getParameter("hotelid");
					String date = request.getParameter("date");
					int night=Integer.parseInt(request.getParameter("night"));
					str = htcservice.getCrateMap(Long.valueOf(hotelid),date,night);
				} else if (method.equals("getCrateMapByType")) {
					// 获取酒店指定房型指定价格计划可用性信息
					String hotelid = request.getParameter("hotelid");
					String date = request.getParameter("date");
					String roomtype = request.getParameter("roomtype");
					String rateclass = request.getParameter("rateclass");
					int night=Integer.parseInt(request.getParameter("night"));
					str = htcservice.getCrateMapByType(Long.valueOf(hotelid),date,roomtype,rateclass,night);
				} else if (method.equals("getOnlineRateMap")) {
					// 获取酒店所有房型所有价格计划的可用性信息
					String hotelid = request.getParameter("hotelid");
					String date = request.getParameter("date");
					int night=Integer.parseInt(request.getParameter("night"));
					str = htcservice.getOnlineRateMap(Long.valueOf(hotelid),
							date,night);
				} else if (method.equals("getOnlineRateMapByType")) {
					// 获取酒店指定房型指定价格计划可用性信息
					String hotelid = request.getParameter("hotelid");
					String date = request.getParameter("date");
					String roomtype = request.getParameter("roomtype");
					String rateclass = request.getParameter("rateclass");
					int night=Integer.parseInt(request.getParameter("night"));
					str = htcservice.getOnlineRateMapByType(Long.valueOf(hotelid), date, roomtype, rateclass,night);
				} else {
					str="<error>系统错误</error>";
				}
			} else {
				str="<error>系统错误</error>";
			}
		} catch (Exception e) {
			e.printStackTrace();
			str="<error>系统错误</error>";
		}
		System.out.println(str);
		out.write(str);
		out.flush();
		out.close();
	}
}
