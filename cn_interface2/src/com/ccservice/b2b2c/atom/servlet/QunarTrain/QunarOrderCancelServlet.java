package com.ccservice.b2b2c.atom.servlet.QunarTrain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.hotel.ElongHotelInterfaceUtil;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.servlet.QunarTrain.Thread.QunarCancelFalseThread;

public class QunarOrderCancelServlet extends HttpServlet {

	private final String logname = "qunar去哪_取消订单接口";

	private final String errorlogname = "qunar去哪_取消订单接口_error";

	private final int random = new Random().nextInt();

	private String key = "";

	@Override
	public void init(ServletConfig config) throws ServletException {
		String cancelFalseThreadStart = config
				.getInitParameter("cancelFalseThreadStart");// 是否开启
		if ("1".equals(cancelFalseThreadStart)) {
			QunarCancelFalseThread qunarCancelFalseThread = new QunarCancelFalseThread();
			qunarCancelFalseThread.start();
			System.out.println("qunar cancel false thread start");
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {
		key = "-j0z5n8y0p16cvp4cxkhc7dwi4bph8h8b";
		String result = "";
		JSONObject obj = new JSONObject();
		PrintWriter out = null;
		req.setCharacterEncoding("utf-8");
		res.setCharacterEncoding("UTF-8");
		res.setHeader("content-type", "text/html;charset=UTF-8");
		try {
			out = res.getWriter();
			// POST请求参数
			BufferedReader br = new BufferedReader(new InputStreamReader(
					req.getInputStream(), "UTF-8"));
			String line = "";
			StringBuffer buf = new StringBuffer(1024);
			while ((line = br.readLine()) != null) {
				buf.append(line);
			}
			String param = buf.toString();
			WriteLog.write(logname, random + "-->请求参数:" + param);
			if (ElongHotelInterfaceUtil.StringIsNull(param)) {
				obj.put("ret", false);
				obj.put("errCode", "003");
				obj.put("errMsg", "输入参数错误");
				result = obj.toString();
			} else {
				JSONObject json = JSONObject.parseObject(param);
				String method = json.containsKey("method") ? json
						.getString("method") : "";
				String orderNo = json.containsKey("orderNo") ? json
						.getString("orderNo") : "";
				String hmac = json.containsKey("HMAC") ? json.getString("HMAC")
						: "";
				String sign = ElongHotelInterfaceUtil.MD5(
						key + method + orderNo).toUpperCase();
				WriteLog.write(logname, random + "-->sign:" + sign + "-->hmac:" + hmac);
				if (sign.equals(hmac)) { // 签名是否匹配
					String canceltype = getOrderCancelMsg(orderNo);
					// 104:没有这个订单 105:该订单正在下单
					if (canceltype.contains("104")
							|| canceltype.contains("105")) {
						saveInterfaceOrderNumber(orderNo);
					} else if ("".equals(canceltype)) {
						obj.put("ret", false);
						obj.put("errCode", "006");
						obj.put("errMsg", "订单编号错误");
						result = obj.toString();
						return;
					}
					obj.put("ret", true);
					result = obj.toString();
					WriteLog.write(logname, random + "返回结果:ret = true-->canceltype:" + canceltype);
				} else {
					obj.put("ret", false);
					obj.put("errCode", "002");
					obj.put("errMsg", "安全验证错误，不符合安全校验规则");
					result = obj.toString();
				}
			}
		} catch (Exception e) {
			WriteLog.write(errorlogname, random + "-->" + e);
			obj.put("ret", false);
			obj.put("errCode", "001");
			obj.put("errMsg", "系统错误，未知服务异常");
			result = obj.toString();
		} finally {
			out.print(result);
			out.flush();
			out.close();
		}
	}
	
	/**
	 * 取消订单
	 * @param orderNo
	 * @return
	 */
	public String getOrderCancelMsg(String orderNo) {
		String result = "";
		String sql = "exec [sp_TrainDemandBespeak_Select_Update] @InterfaceOrderNumber='"
				+ orderNo + "'";
		try {
			List list = Server.getInstance().getSystemService()
					.findMapResultBySql(sql, null);
			if (list.size() > 0) {
				Map map = (Map) list.get(0);
				result = map.get("result").toString();
			}
		} catch (Exception e) {
		}
		return result;
	}
	
	/**
	 * 订单不存在和正在下单的存进新表
	 * @param orderNo
	 */
	public void saveInterfaceOrderNumber(String orderNo) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String cancelTime = sdf.format(new Date());
		String sql = "INSERT INTO T_QUNARCANCEL (INTERFACEORDERNUMBER,CANCELTIME) VALUES ('"
				+ orderNo + "','" + cancelTime + "')";
		Server.getInstance().getSystemService().findMapResultBySql(sql, null);

	}
}
