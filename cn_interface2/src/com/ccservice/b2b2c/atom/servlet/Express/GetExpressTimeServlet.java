package com.ccservice.b2b2c.atom.servlet.Express;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.tenpay.util.MD5Util;

/**
 * jd sf 两个时效比较 选优
 */
@WebServlet("/GetExpressTimeServlet")
public class GetExpressTimeServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public GetExpressTimeServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain; charset=utf-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		PrintWriter out = null;
		JSONObject responsejson = new JSONObject();
		int random = new Random().nextInt(100000);
		try {
			out = response.getWriter();
			String reqtime = request.getParameter("reqtime");
			String partnerid = request.getParameter("partnerid");
			String sign = request.getParameter("sign");
			String address = request.getParameter("address");
//			address = new String(address.getBytes("ISO8859-1"), "UTF-8");  
			String sendProvince = request.getParameter("sendProvince");
//			sendProvince = new String(sendProvince.getBytes("ISO8859-1"), "UTF-8");  
			String sendCity = request.getParameter("sendCity");
//			sendCity = new String(sendCity.getBytes("ISO8859-1"), "UTF-8");  
			WriteLog.write("线下快递时效接口", random + "reqtime:" + reqtime
					+ ",partnerid:" + partnerid + ";sign:" + sign + ";address:"
					+ address + ";sendProvince:" + sendProvince + ";sendCity:"
					+ sendCity);
			if (!ElongHotelInterfaceUtil.StringIsNull(reqtime)
					&& !ElongHotelInterfaceUtil.StringIsNull(partnerid)
					&& !ElongHotelInterfaceUtil.StringIsNull(sign)
					&& !ElongHotelInterfaceUtil.StringIsNull(address)
					&& !ElongHotelInterfaceUtil.StringIsNull(sendProvince)
					&& !ElongHotelInterfaceUtil.StringIsNull(sendCity)) {
				String sql = "SELECT keys from TrainOfflineAgentKey where partnerName='"
						+ partnerid + "'";
				List list = Server.getInstance().getSystemService()
						.findMapResultBySql(sql, null);
				String key = "";
				String code = "";
				String msg = "";
				boolean success = false;
				JSONObject deliverDescription = new JSONObject();
				if (list.size() > 0) {
					Map map = (Map) list.get(0);
					key = map.get("keys").toString();

					String md5s = partnerid + reqtime
							+ MD5Util.MD5Encode(key, "utf-8").toUpperCase();
					md5s = MD5Util.MD5Encode(md5s, "utf-8").toUpperCase();
					if (sign.equals(md5s)) {// 验证通过
						code = "4";
						try {
							// SF
							String sf = ExpressPublicMethod.getDelieveStr(
									address, sendProvince, sendCity, random);
							// JD
							String jd = ExpressPublicMethod.getJDDelieveStr(
									sendProvince, sendCity, address, random);
							deliverDescription.put("sf", sf);
							deliverDescription.put("jd", jd);
							WriteLog.write("线下快递时效接口", random + "--(JD)--:"
									+ jd);
							WriteLog.write("线下快递时效接口", random + "--(SF)--:"
									+ sf);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						msg = "获取配送时效,价格成功";
						success = true;
					} else {
						code = "1";
						msg = "加密错误！";
					}
				} else {
					code = "1";
					msg = "账号核验失败！";
				}
				responsejson.put("code", code);
				responsejson.put("success", success);
				responsejson.put("msg", msg);
				responsejson.put("data", deliverDescription);

			} else {
				responsejson.put("code", "1");
				responsejson.put("success", false);
				responsejson.put("msg", "参数错误！");
				responsejson.put("data", "");
			}
		} catch (Exception e) {
			responsejson.put("code", 1);
			responsejson.put("success", false);
			responsejson.put("msg", "系统错误");
			responsejson.put("data", "");
		} finally {
			WriteLog.write("线下快递时效接口",
					random + "--result:" + responsejson.toString());
			out.print(responsejson.toString());
			out.flush();
			out.close();
		}
	}

}
