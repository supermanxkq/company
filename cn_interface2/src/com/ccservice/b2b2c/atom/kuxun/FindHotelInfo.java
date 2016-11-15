package com.ccservice.b2b2c.atom.kuxun;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.city.City;
import com.ccservice.b2b2c.base.hotel.Hotel;
import com.ccservice.b2b2c.base.util.PageInfo;

public class FindHotelInfo extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	static final long serialVersionUID = 1L;

	// 分页类的初始化
	PageInfo pageInfo = new PageInfo();

	List<Hotel> hotelList;

	public FindHotelInfo() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// 设置编码方式
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");

		// 获取从页面传过来的数据
		// 当前页
		int pagenum = Integer.parseInt(request.getParameter("page"));

		pageInfo.setPagenum(pagenum);

		// 查询酒店这张表，使用的是存储过程
		List list = Server
				.getInstance()
				.getHotelService()
				.findAllHotelBySP(
						"T_HOTEL",
						"*",
						"ID",
						1,
						"and C_PAYTYPE=2 and C_STATE=3 and id in (select distinct C_HOTELID from T_HMHOTELPRICE)",
						"ID", pageInfo);

		// 用于转换hotel和pageInfo之间的关系
		pageInfo = (PageInfo) list.remove(0);

		hotelList = list;

//		for (Hotel hotel : hotelList) {
//			System.out.println(hotel.toString());
//		}

		PrintWriter out = response.getWriter();
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.println("<result>");
		out.println("<page>" + pageInfo.getPagenum() + "</page>");
		out.println("<pagecount>" + pageInfo.getTotalpage() + "</pagecount>");
		out.println("<hotelcount>" + pageInfo.getTotalrow() + "</hotelcount>");
		out.println("<hotel_list>");
		for (int i = 0; i < hotelList.size(); i++) {
			Hotel hotel = (Hotel) hotelList.get(i);
			out.println("<hotel>");
			// 酒店的id
			out.println("<id>" + hotelList.get(i).getId() + "</id>");
			// 酒店的名称
			if ("".equals(hotel.getName()) || hotel.getName() == null) {
				out.println("<name>" + "" + "</name>");
			} else {
				out.println("<name>" + hotel.getName() + "</name>");
			}

			// 酒店所在的城市
			if ("".equals(hotel.getCityid()) || hotel.getCityid() == null) {
				out.println("<city>" + "" + "</city>");
			} else {
				out.println("<city>"
						+ getCityNameByStr(hotel.getCityid().toString())
						+ "</city>");
			}

			// 酒店的星级
			if ("".equals(hotel.getStar()) || hotel.getStar() == null) {
				out.println("<grade>" + "" + "</grade>");
			} else {
				out.println("<grade>" + hotel.getStar() + "</grade>");
			}
			// 酒店的地址
			if ("".equals(hotel.getAddress()) || hotel.getAddress() == null) {
				out.println("<address>" + "" + "</address>");
			} else {
				out.println("<address>" + hotel.getAddress() + "</address>");
			}
			// 酒店的简介
			if ("".equals(hotel.getDescription())
					|| hotel.getDescription() == null) {
				out.println("<description>" + "" + "</description>");
			} else {
				out.println("<description>" + hotel.getDescription()
						+ "</description>");
			}
			// 酒店的纬度
			if ("".equals(hotel.getLng()) || hotel.getLng() == null) {
				out.println("<longitude>" + "" + "</longitude>");
			} else {
				out.println("<longitude>" + hotel.getLng() + "</longitude>");
			}
			// 酒店的经度
			if ("".equals(hotel.getLat()) || hotel.getLat() == null) {
				out.println("<latitude>" + "" + "</latitude>");
			} else {
				out.println("<latitude>" + hotel.getLat() + "</latitude>");
			}
			// 娱乐设施
			if ("".equals(hotel.getPlayitem()) || hotel.getPlayitem() == null) {
				out.println("<entertainment>" + "" + "</entertainment>");
			} else {
				out.println("<entertainment>" + hotel.getPlayitem()
						+ "</entertainment>");
			}
			// 会议设施
			if ("".equals(hotel.getMeetingitem())
					|| hotel.getMeetingitem() == null) {
				out.println("<meeting>" + "" + "</meeting>");
			} else {
				out
						.println("<meeting>" + hotel.getMeetingitem()
								+ "</meeting>");
			}
			// 餐饮设施
			if ("".equals(hotel.getFootitem()) || hotel.getFootitem() == null) {
				out.println("<catering>" + "" + "</catering>");
			} else {
				out.println("<catering>" + hotel.getFootitem() + "</catering>");
			}
			// 基本设施
			// out.println("<service>" + + "</service>");//基本设施没有
			// 交通状况
			if ("".equals(hotel.getTrafficinfo())
					|| hotel.getTrafficinfo() == null) {
				out.println("<traffic>" + "" + "</traffic>");
			} else {
				out
						.println("<traffic>" + hotel.getTrafficinfo()
								+ "</traffic>");
			}
			// 酒店电话（总机电话）tortell
			if ("".equals(hotel.getTortell()) || hotel.getTortell() == null) {
				out.println("<telephone>" + "" + "</telephone>");
			} else {
				out
						.println("<telephone>" + hotel.getTortell()
								+ "</telephone>");
			}
			// 酒店网址(推荐的网址)

			out
					.println("<url>"
							+ "http://www.yeebooking.com/gx_website/hotel!toHotelInfo.jspx?HotelId="
							+ hotel.getId() + "</url>");

			// 支持的信用卡carttype
			if ("".equals(hotel.getCarttype()) || hotel.getCarttype() == null) {
				out.println("<card>" + "" + "</card>");
			} else {
				out.println("<card>" + hotel.getCarttype() + "</card>");
			}
			// 自定义字段
			out.println("<other>" + "" + "</other>");
			out.println("</hotel>");
		}
		out.println("</hotel_list>");
		out.println("</result>");

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);

	}

	// 根据城市的id获取到name
	public String getCityNameByStr(String cityid) {
		City city = Server.getInstance().getHotelService().findCity(
				Long.parseLong(cityid));
		return city != null && city.getName() != null
				&& !"".equals(city.getName()) ? city.getName() : "";
	}

}