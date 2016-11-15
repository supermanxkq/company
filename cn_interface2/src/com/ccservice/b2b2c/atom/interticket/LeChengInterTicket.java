package com.ccservice.b2b2c.atom.interticket;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Element;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeragent.Customeragent;
import com.ccservice.b2b2c.base.fflight.AllFlight;
import com.ccservice.b2b2c.base.fflight.AllRouteInfo;
import com.ccservice.b2b2c.base.fflight.Route;
import com.ccservice.b2b2c.base.fflight.RouteDetailInfo;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;

public class LeChengInterTicket {

	/**
	 * 乐程国际机票接口
	 */
	private String username;
	private String password;
	private String ipAddress;
	private String userid;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 乐程网国际机票查询接口
	 * 
	 * @param strFromCity
	 *            出发城市三字码
	 * @param strToCity
	 *            到达城市三字码
	 * @param strFromDate
	 *            出发时间
	 * @param strseatType
	 *            舱位级别
	 * @return 国际航班列表
	 */
	public static AllRouteInfo interTicketSearch(String strFromCity,
			String strToCity, String strFromDate, String strReturnDate,
			String strseatType) {
		// 航班全部路线
		AllRouteInfo allrouteinfo = new AllRouteInfo();

		java.io.InputStream in = null;
		String totalurl = "";
		totalurl = "http://www.lcair.com/api/price.php" + "?";
		// totalurl+="Uid="+userid+"&Username="+username+"&Userpwd="+password;
		// //乐程不需要用户名密码
		// fromCity=BJS&toCity=NYC&fromDate=2011-08-19&returnDate=&adultCount=2&seatType=C
		totalurl += "fromCity=" + strFromCity;
		totalurl += "&toCity=" + strToCity;
		totalurl += "&fromDate=" + strFromDate;
		if (strReturnDate != null && !strReturnDate.equals("")) {
			totalurl += "&returnDate=" + strReturnDate;
		}
		totalurl += "&seatType=" + strseatType;

		try {
			java.net.URL Url = new java.net.URL(totalurl);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			in = conn.getInputStream();
			org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
			org.jdom.Document doc = build.build(in);
			org.jdom.Element data = doc.getRootElement();
			List<Element> routes = data.getChildren();
			// 查询到航班信息
			if (routes.size() > 0) {
				List<Route> listroute = new ArrayList<Route>();
				int indexroute = 0;
				allrouteinfo.setAllRouteID(1);
				allrouteinfo.setRouteStr(strFromCity + "-" + strToCity);
				for (int i = 0; i < routes.size(); i++) {
					List<RouteDetailInfo> listroutedetailinfo = new ArrayList<RouteDetailInfo>();
					List<AllFlight> listallfight = new ArrayList<AllFlight>();
					Element ed = (Element) routes.get(i);
					Route routeinfo = new Route();
					indexroute++;
					// ////////////////----得到航班线路类 开始
					// 出发城市三字码
					routeinfo.setFromCity(ed.getChildText("F"));
					// 到达城市三字码
					routeinfo.setDestCity(ed.getChildText("T"));
					// 航空公司两字码
					routeinfo.setAirCompany(ed.getChildText("A"));
					// 成人价格
					if (ed.getChildText("M") != null
							&& !ed.getChildText("M").toString().equals("")) {
						Double dadultprice = Double.parseDouble(ed
								.getChildText("M").toString()
								+ "");
						dadultprice = dadultprice * (1 + 0.03);
						routeinfo.setTotalFare(Double.parseDouble(dadultprice
								.intValue()
								+ ""));
					} else {
						routeinfo.setTotalFare(0);
					}

					// 儿童价格
					if (ed.getChildText("CM") != null
							&& !ed.getChildText("CM").toString().equals("")) {
						Double dchlidprice = Double.parseDouble(ed
								.getChildText("CM").toString()
								+ "");
						dchlidprice = dchlidprice * (1 + 0.03);
						routeinfo.setTotalChlidFare(Double
								.parseDouble(dchlidprice.intValue() + ""));
					} else {
						routeinfo.setTotalChlidFare(0);
					}

					// 参考税费
					if (ed.getChildText("X") != null
							&& !ed.getChildText("X").toString().equals("")) {
						Double dtax = Double.parseDouble(ed.getChildText("X")
								.toString()
								+ "");
						dtax = dtax * (1 + 0.03);
						routeinfo.setTotalTax(Double.parseDouble(dtax
								.intValue()
								+ ""));
					} else {
						routeinfo.setTotalTax(0);
					}

					// 是否转机
					if (ed.getChildText("Z") != null
							&& !ed.getChildText("Z").toString().equals("")) {
						routeinfo.setIsChangeFlight(Integer.parseInt(ed
								.getChildText("Z").toString()
								+ ""));
					} else {
						routeinfo.setIsChangeFlight(0);
					}

					// 政策
					try {
						routeinfo
								.setPolicyInfo(ed.getChildText("L").toString());
					} catch (Exception ex) {
						routeinfo.setPolicyInfo("");
					}
					// 航程线路ID
					routeinfo.setID(indexroute);
					// 航程线路段
					routeinfo.setRouteStr(strFromCity + "-" + strToCity);
					// //////////////----得到航班线路类 结束
					// /////////////-----得到航班线路详情 开始
					org.jdom.Element eleroutedetail = ed.getChild("SS");
					List<Element> eleroutedetailinfo = eleroutedetail
							.getChildren("S");

					for (Element routedetailinfoxml : eleroutedetailinfo) {
						RouteDetailInfo routedetailinfo = new RouteDetailInfo();
						// 出发城市
						routedetailinfo.setFromCity(routedetailinfoxml
								.getChildText("F"));
						// 到达城市
						routedetailinfo.setDestCity(routedetailinfoxml
								.getChildText("T"));
						// 航空公司
						routedetailinfo.setAirCompany(routedetailinfoxml
								.getChildText("A"));
						// 舱位
						routedetailinfo.setCabin(routedetailinfoxml
								.getChildText("ST"));
						// 出发机场
						routedetailinfo.setFromAirport(routedetailinfoxml
								.getChildText("FA"));
						// 到达机场
						routedetailinfo.setToAirport(routedetailinfoxml
								.getChildText("TA"));
						// 出发时间
						routedetailinfo.setFromDate(routedetailinfoxml
								.getChildText("FD")
								+ "," + routedetailinfoxml.getChildText("FT"));
						// 到达时间
						routedetailinfo.setToDate(routedetailinfoxml
								.getChildText("TD")
								+ "," + routedetailinfoxml.getChildText("TT"));
						// 航班号
						routedetailinfo.setFlightNumber(routedetailinfoxml
								.getChildText("No"));

						// 航班线路详情
						org.jdom.Element eleallfight = routedetailinfoxml
								.getChild("FS");
						List<Element> listeleallflight = eleallfight
								.getChildren("F");
						int indexflight = 0;
						// 总航班数量
						routedetailinfo.setTotalFlightNo(listeleallflight
								.size() - 1);
						for (int w = 0; w < listeleallflight.size(); w++) {
							// 去掉和RouteDtail中重复的航班
							boolean boolflag = false;
							for (Element routedetailinfoxml1 : eleroutedetailinfo) {
								if (routedetailinfoxml1.getChildText("No")
										.equals(
												listeleallflight.get(w)
														.getChildText("No"))) {
									boolflag = true;
								}
							}
							if (!boolflag) {
								AllFlight allflight = new AllFlight();
								indexflight++;
								allflight.setNo(indexflight);
								allflight.setFromCity(listeleallflight.get(w)
										.getChildText("FA"));
								allflight.setDestCity(listeleallflight.get(w)
										.getChildText("TA"));
								allflight.setAirCompany(listeleallflight.get(w)
										.getChildText("A"));
								allflight.setFromDate(listeleallflight.get(w)
										.getChildText("FD")
										+ ","
										+ listeleallflight.get(w).getChildText(
												"FT"));
								allflight.setToDate(listeleallflight.get(w)
										.getChildText("TD")
										+ ","
										+ listeleallflight.get(w).getChildText(
												"TT"));
								allflight.setFlightNumber(listeleallflight.get(
										w).getChildText("No"));
								listallfight.add(allflight);
							}

						}
						// 航班线路详情
						routedetailinfo.setFlightInfos(listallfight);
						listroutedetailinfo.add(routedetailinfo);
					}
					routeinfo.setRouteDetailInfos(listroutedetailinfo);
					// /////////////-----得到航班线路详情 开始
					listroute.add(routeinfo);
				}

				allrouteinfo.setRoutes(listroute);
			}
			in.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("国际机票查询完毕....");
		return allrouteinfo;
	}

	/**
	 * 转换null
	 * 
	 * @param <T>
	 * @param t
	 * @param v
	 * @return
	 */
	public <T> T converNull(T t, T v) {
		if (t != null && !t.equals("")) {
			return t;
		}
		return v;
	}

	public String formatMoney_B2BBack(String s) {

		// String s = "123.456 ";
		float money = Float.valueOf(s).floatValue();

		DecimalFormat format = null;
		format = (DecimalFormat) NumberFormat.getInstance();
		format.applyPattern("###0.00");
		try {
			String result = format.format(money);
			return result;
		} catch (Exception e) {
			return Float.toString(money);
		}
	}

	/**
	 * 停用
	 * @param order
	 * @param pnum
	 * @returnFloat[] 0:利润，1：返点
	 */
	public Map<Long, Float> getAgentro(Orderinfo order, int pnum) {

		Map<Long, Float> agentmap = new HashMap<Long, Float>();
		if (order.getInternal() != null && order.getInternal() == 1) {// 国际机票分润计算
			/**
			 * float money = 0;// 总利润 for (Orderinfo orderinfo : orderinfos) {
			 * money += orderinfo.getRebatemoney(); } long orderagentid =
			 * orderinfos.get(0).getBuyagentid(); String where = " WHERE ID=" +
			 * orderagentid + " OR
			 * CHARINDEX(','+CONVERT(NVARCHAR,ID)+',',(SELECT
			 * ','+C_PARENTSTR+',' FROM T_CUSTOMERAGENT WHERE ID=" +
			 * orderagentid + "))>0"; IMemberService service =
			 * Server.getInstance().getMemberService(); List<Customeragent>
			 * customeragents = service.findAllCustomeragent( where, "", -1, 0);
			 * String rulewhere = " WHERE " + Rebaterule.COL_ruletypeid + "=" +
			 * 1;// 国际机票 List<Rebaterule> listrule =
			 * service.findAllRebaterule(rulewhere, "", -1, 0);
			 * 
			 * if (money > 0) { float othermoney = 0f; Customeragent orderagent =
			 * null; for (Customeragent agent : customeragents) { int agentlevel =
			 * agent.getAgentjibie(); for (Rebaterule rule : listrule) { long
			 * agenttype = rule.getAgenttypeid(); if (agenttype == agentlevel) {
			 * if (agent.getId() != orderagentid) { float rebatmoney =
			 * rule.getRebatvalue() * money; agentmap.put(agent, rebatmoney);
			 * othermoney += rebatmoney; } else { orderagent = agent; } } } }
			 * 
			 * float orderagentmoney = money - othermoney;
			 * agentmap.put(orderagent, orderagentmoney); }
			 * 
			 * return agentmap;
			 */
			return null;

		} else {// 国内机票分润计算
			Map<Long, float[]> map = Server.getInstance()
					.getB2BAirticketService().getAgentlevelrebate(
							order.getId(), pnum, 0);
			Iterator<Map.Entry<Long, float[]>> iterator = map.entrySet()
					.iterator();
			for (; iterator.hasNext();) {
				Map.Entry<Long, float[]> entry = iterator.next();
				long id = entry.getKey();
				float rebate = entry.getValue()[0];
				
			
				if(id==order.getBuyagentid()||rebate==0f){// 采购者，或利润为0 则不参与分润。
					continue;
				}
				agentmap.put(id, rebate);
			}
		// // 46 加盟商id ,3.3：自己留点,1850.0 ：价格：,3.3：总返点@：
		// String fdstr = order.getBackpointinfo();
		// if (fdstr != null) {
		// String[] agentstrs = fdstr.split("@");
		// String sql = "SELECT ID id, C_CODE AS code,ISNULL(C_ALIPAYACCOUNT,'')
		// as alipayaccount,C_KUAIBILLACCOUNT AS
		// kuaibillaccount,ISNULL(C_CHINAPNRCOUNT,'') chinapnrcount "
		// + ",ISNULL(C_ISPARTNER,0) ispartner FROM T_CUSTOMERAGENT WHERE ID IN
		// ";
		// StringBuilder agentidsb = new StringBuilder("0");
		// // agentstrs[agentstrs.length-1]="";
		// int length = agentstrs.length - 1;// 去掉最后一项 保险。
		// if (order.getOrdertype() == 1) {
		// length -= 1;// 最后一项为网站散客 返点，不予计算。
		// }
		// for (int i = 0; i < length; i++) {
		// String agentstr = agentstrs[i];
		// String[] infos = agentstr.split(",");
		// String agentid = infos[0];// 加盟商ID
		// if (order.getOrdertype() == 2
		// && agentid.equals(order.getBuyagentid())) {
		// continue;
		// }
		// agentidsb.append("," + agentid);
		//
		// }
		// sql += "(" + agentidsb.toString() + ") ";
		// List agentlist = Server.getInstance().getSystemService()
		// .findMapResultBySql(sql, null);
		// for (int i = 0; i < orderinfos.size(); i++) {
		// Orderinfo orderinfo = orderinfos.get(i);
		// for (int j = 0; j < length; j++) {
		// String agentstr = agentstrs[j];
		// String[] infos = agentstr.split(",");
		// String agentid = infos[0];// 加盟商ID
		// if (orderinfo.getOrdertype() == 2
		// && agentid.equals(orderinfo.getBuyagentid()
		// + "")) {
		// continue;
		// }
		// String fd = infos[1];// 自己留点
		// String ordermoney = infos[2];// 订单总额
		//
		// float totalmoney = Float.valueOf(ordermoney) * pnum;
		// float agentfd = Float.valueOf(fd);
		// Customeragent agent = null;
		// try {
		// for (Object obj : agentlist) {
		// Map map = (Map) obj;
		// String id = map.get("id").toString();
		// if (id.equals(agentid)) {
		// agent = this.setFiledfrommap(
		// Customeragent.class, map);
		// break;
		// }
		// }
		// float agentmoney = (float) Math.floor(totalmoney
		// * agentfd / 100);
		// if (agent.getId() == 46) {//运营商分润时加上平台手续费
		// Sysconfig sysconfig = Server.getInstance()
		// .getMemberService().findSysconfig(
		// 10022L);
		// if (sysconfig != null)
		// agentmoney += Float.parseFloat(sysconfig
		// .getValue());
		// }
		// if (agentmoney == 0) {
		// continue;
		// }
		// if (i == 0) {
		// agentmap.put(agent, formatMoney(agentmoney));
		// } else {
		// float money = agentmap.get(agent);
		// money += agentmoney;
		// agentmap.put(agent, money);
		// }
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// }
		// return agentmap;
		// } else {
		// return null;
		// }
		}
		return agentmap;
	}

// /**
// * @param train
// * 创建火车票分润记录
// */
// public void createTrainrebate(List<Orderinfo> orderinfos) {
// IMemberService service = Server.getInstance().getMemberService();
// for (Orderinfo orderinfo : orderinfos) {
// List<Orderinfo> neworders = new ArrayList<Orderinfo>();
// neworders.add(orderinfo);
// Rebaterecord record = new Rebaterecord();
// record.setOrdernumber(String.valueOf(orderinfo.getId()));
// record.setRebatetime(new Timestamp(System.currentTimeMillis()));
// record.setYewutype(1);
// record.setCustomerid(orderinfo.getCustomeruserid());
// Iterator<Map.Entry<Customeragent, Float>> agentiterator = getAgentroyalty(
// neworders, 1).entrySet().iterator();
// for (Map.Entry<Customeragent, Float> entery = agentiterator.next();
// agentiterator
// .hasNext();) {
// Customeragent agent = entery.getKey();
// record.setRebateagentid(agent.getId());
// record.setRebatemoney(entery.getValue());
// try {
// service.createRebaterecord(record);
// } catch (SQLException e) {
// e.printStackTrace();
// }
//
// }
// }
// }

	DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();

	public float formatMoney(Float money) {
		format.applyPattern("###0.0");
		try {
			String result = format.format(money);
			return Float.valueOf(result);
		} catch (Exception e) {
			if (money != null) {
				return money;
			} else {
				return 0;
			}
		}
	}

	/**
	 * 从map转换为对象
	 * 
	 * @param <T>
	 * @param t
	 * @param map
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws InstantiationException
	 * @throws NoSuchFieldException
	 */
	public <T> T setFiledfrommap(Class t, Map map) throws SecurityException,
			NoSuchMethodException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			InstantiationException, NoSuchFieldException {
		Iterator<Map.Entry<String, String>> iterator = map.entrySet()
				.iterator();
		T tt = (T) t.newInstance();
		System.out.println(map.size());
		for (Map.Entry<String, String> entry = null; iterator.hasNext();) {
			entry = iterator.next();
			String paraname = entry.getKey();
			Object val = entry.getValue();
			paraname = paraname.substring(0, 1).toUpperCase()
					+ paraname.substring(1);
			Method getm = t.getMethod("get" + paraname, null);
			String type = getm.getReturnType().getSimpleName();
			if (type.equals("Integer") || type.equals("int")) {
				val = Integer.valueOf(val.toString());
			}
			if (type.equals("Long") || type.equals("long")) {
				val = Long.valueOf(converNull(val, '0').toString());
			}
			if (type.equals("Float") || type.equals("float")) {
				val = Float.valueOf(val.toString());
			}
			if (type.equals("Byte") || type.equals("byte")) {
				val = Byte.valueOf(val.toString());
			}
			Method method = t.getMethod("set" + paraname, getm.getReturnType());

			method.invoke(tt, val);
		}
		return tt;
	}

	public static void main(String[] args) throws MalformedURLException {

		// AllRouteInfo
		// listroute=interTicketSearch("BJS","NYC","2011-08-19","","C");
		// System.out.println(listroute.toString());
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
}
