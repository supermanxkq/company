package com.ccservice.b2b2c.atom.kuxun;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hmhotelprice.Hmhotelprice;
import com.ccservice.b2b2c.base.roomtype.Roomtype;
import com.ccservice.huamin.WriteLog;

/**
 * 
 * @author 酷讯的价格信息接口
 * 
 */
public class GetHotelPriceForKuXun extends javax.servlet.http.HttpServlet
		implements javax.servlet.Servlet {
	static final long serialVersionUID = 1L;

	public GetHotelPriceForKuXun() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=utf-8");
		// 获取参数id,checkin,checkout
		String id = request.getParameter("id");
		String checkin = request.getParameter("checkin");
		String checkout = request.getParameter("checkout");
		Writer out = response.getWriter();
		StringBuilder sb = new StringBuilder();
		// 判断传的参数是否为空
		if (id != null && checkin != null && checkout != null) {
			// 根据传过来的酒店id查询房间类型的所有信息
			// select c_roomtypeid from t_hmhotelprice where C_STATEDATE BETWEEN
			// '" + checkin + "' and '" checkout + "'";
			// +
			String strRoomtype = " where C_STATE=1 and C_HOTELID = "
					+ id
					+ "  and id in ( select c_roomtypeid from t_hmhotelprice where  C_STATEDATE >= '"
					+ checkin + "' and C_STATEDATE<'" + checkout + "')";
			System.out.println("strroomtype:" + strRoomtype);
			List<Roomtype> listroomtype = Server.getInstance()
					.getHotelService().findAllRoomtype(strRoomtype, "", -1, 0);
			// 打印输出xml文件
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<hotel fromdate = \"");
			sb.append(checkin + "\"");
			sb.append(" todate = \"");
			sb.append(checkout + "\"");
			sb.append(" >");
			// 遍历上面获取的房间类型信息
			for (int i = 0; i < listroomtype.size(); i++) {
				// 根据房间类型的id和获得的入住时间 checkin 参数、离店时间checkout参数 ，查询预订价格
				String strprice = "where C_ROOMTYPEID = "
						+ listroomtype.get(i).getId() + " and C_HOTELID = "
						+ id + " and C_STATEDATE >= '" + checkin
						+ "' and C_STATEDATE<'" + checkout
						+ "' order by C_STATEDATE";
				System.out.println("strprice:" + strprice);
				List<Hmhotelprice> listhmhotelprice = Server.getInstance()
						.getHotelService().findAllHmhotelprice(strprice, "",
								-1, 0);
				int days = difcount(checkin, checkout);
				if (listhmhotelprice.size() > 0) {
					if (listhmhotelprice.size() == days) {
						// 打印输出xml信息
						String strP = "";
						String strPz = "";
						String strS = "";
						String strT = "";
						sb.append("<room id = \"");
						sb.append(listroomtype.get(i).getId() + "\"");
						sb.append(" n= \"");
						sb.append(listroomtype.get(i).getName()
								+ "("
								+ findbedtypename(listroomtype.get(i).getBed())
								+ ")-预付价("
								+ breakfaststr(listhmhotelprice.get(0).getBf()
										.intValue()) + "早)");
						if (listhmhotelprice.get(0).getAdvancedday() > 0) {
							sb.append("-需提前天"
									+ listhmhotelprice.get(0).getAdvancedday()
									+ "预订");
						}
						sb.append("\"");
						// 遍历获得的预订价格信息
						if (listhmhotelprice != null) {
							for (int j = 0; j < listhmhotelprice.size(); j++) {
								if (j == 0) {
									strP += (listhmhotelprice.get(j).getPriceoffer());
									strPz += 0;
									strS += 1;
									strT += 0;
								} else {
									strP += " | "
											+ (listhmhotelprice.get(j)
													.getPriceoffer());
									strPz += " | " + 0;
									strS += " | " + 1;
									strT += " | " + 0;
								}
							}
						}

						sb.append(" p= \"");
						sb.append(strP + "\"");
						sb.append(" pz= \"");
						sb.append(strPz + "\"");
						sb.append(" s= \"");
						sb.append(strS + " \"");
						sb.append(" c=\"CNY\"");
						sb.append(" t= \"");
						sb.append(strT + "\"");
						sb.append(" >");
						sb.append("</room>");
					} else if (listhmhotelprice.size() > days) {
						List bftypes = Server
								.getInstance()
								.getSystemService()
								.findMapResultBySql(
										"select distinct C_BF from T_HMHOTELPRICE where C_ROOMTYPEID = "
												+ listroomtype.get(i).getId()
												+ " and C_HOTELID = " + id
												+ " and C_STATEDATE >= '"
												+ checkin
												+ "' and C_STATEDATE<'"
												+ checkout + "' order by C_BF ",
										null);
						for (int m = 0; m < bftypes.size(); m++) {
							Map map = (Map) bftypes.get(m);
							String bfcount = map.get("C_BF").toString();
							String bfprice = "where C_ROOMTYPEID = "
									+ listroomtype.get(i).getId()
									+ " and C_BF=" + bfcount
									+ " and C_HOTELID = " + id
									+ " and C_STATEDATE >= '" + checkin
									+ "' and C_STATEDATE<'" + checkout + "'";
							System.out.println(bfprice);
							List<Hmhotelprice> listhmhotelpricetemp = Server
									.getInstance().getHotelService()
									.findAllHmhotelprice(bfprice,
											" order by C_STATEDATE ", -1, 0);
							// 打印输出xml信息
							String strP = "";
							String strPz = "";
							String strS = "";
							String strT = "";
							if (listhmhotelpricetemp.size() == days) {
								sb.append("<room id = \"");
								sb.append(listroomtype.get(i).getId() + "\"");
								sb.append(" n= \"");
								sb.append(listroomtype.get(i).getName()
										+ "("
										+ findbedtypename(listroomtype.get(i)
												.getBed())
										+ ")-预付价("
										+ breakfaststr(Integer
												.parseInt(bfcount)) + "早)");
								if (listhmhotelpricetemp.get(0)
										.getAdvancedday() > 0) {
									sb.append("-需提前天"
											+ listhmhotelpricetemp.get(0)
													.getAdvancedday() + "预订");
								}
								sb.append("\"");
								// 遍历获得的预订价格信息
								if (listhmhotelpricetemp != null) {
									for (int j = 0; j < listhmhotelpricetemp
											.size(); j++) {
										if (j == 0) {
											strP += (listhmhotelpricetemp
													.get(j).getPrice() + 30);
											strPz += 0;
											strS += 1;
											strT += 0;
										} else {
											strP += " | "
													+ (listhmhotelpricetemp
															.get(j).getPrice() + 30);
											strPz += " | " + 0;
											strS += " | " + 1;
											strT += " | " + 0;
										}
									}
								}

								sb.append(" p= \"");
								sb.append(strP + "\"");
								sb.append(" pz= \"");
								sb.append(strPz + "\"");
								sb.append(" s= \"");
								sb.append(strS + " \"");
								sb.append(" c=\"CNY\"");
								sb.append(" t= \"");
								sb.append(strT + "\"");
								sb.append(" >");
								sb.append("</room>");
							}
						}
					} else if (listhmhotelprice.size() < days) {

					}
				}

			}
			sb.append("</hotel>");
			System.out.println(sb.toString());
			out.write(sb.toString());
			out.flush();
			out.close();

		}
	}

	public int difcount(String startDate, String endDate) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date time = df.parse(startDate);
			Date time2 = df.parse(endDate);
			long manyday = (time2.getTime() - time.getTime())
					/ (24 * 3600 * 1000);
			int many = (int) manyday;
			return many;
		} catch (Exception e) {
			WriteLog.write("酷讯日期转换异常", e.getMessage());
		}
		return 0;
	}

	public String breakfaststr(int bf) {
		switch (bf) {
		case 0:
			return "不含";
		case 1:
			return "单";
		case 2:
			return "双";
		case 3:
			return "三";
		default:
			return "";
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	// 根据床型id查找房名
	public String findbedtypename(long bedtypeid) {
		return Server.getInstance().getHotelService().findBedtype(bedtypeid)
				.getTypename();
	}
}