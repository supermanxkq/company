package com.ccservice.b2b2c.atom.interticket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentHelper;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import sun.misc.BASE64Decoder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.sms.TianXunTongSmsSender;
import com.ccservice.b2b2c.base.aircompany.Aircompany;
import com.ccservice.b2b2c.base.airflight.Airflight;
import com.ccservice.b2b2c.base.cityairport.Cityairport;
import com.ccservice.b2b2c.base.cityairport.CityairportBean;
import com.ccservice.b2b2c.base.fairport.Fairport;
import com.ccservice.b2b2c.base.fflight.AllRouteInfo;
import com.ccservice.b2b2c.base.fflight.InstancePrice;
import com.ccservice.b2b2c.base.fflight.InterAri;
import com.ccservice.b2b2c.base.fflight.InterZefees;
import com.ccservice.b2b2c.base.fflight.Route;
import com.ccservice.b2b2c.base.fflight.RouteDetailInfo;
import com.ccservice.b2b2c.base.fflight.StrokeAri;

public class ATMInternalTicket implements Serializable {
	Log logger = LogFactory.getLog(TianXunTongSmsSender.class);
	//国际机票接口地址
	String interUrl = "http://intf.atm86.com/v2.0/aws_xml.cgi";
	
	String soap_addr = "http://intf.atm86.com/v1.0/inter-soap.cgi";
	String soap_uri = "http://intf.atm86.com/inter";
	String serial_number = "aWawe6qjXJ";// 序列号
	// 接口地址
	// String totalurl="http://192.168.0.5:8091/Default.aspx";
	String totalurl = "http://223.4.238.13:2676/Default.aspx";
	Namespace soap = Namespace.getNamespace("soap",
			"http://schemas.xmlsoap.org/soap/envelope/");
	Namespace ns2 = Namespace.getNamespace("inter_price_owResponse",
			"http://intf.atm86.com/inter");
	Namespace nealnet = Namespace.getNamespace("Array",
			"http://schemas.xmlsoap.org/soap/encoding/");
	Namespace item = Namespace.getNamespace("item",
			"http://intf.atm86.com/inter");

	/**
	 * 查询合同信息@param args 单程
	 */
	public AllRouteInfo searchInterTicketData(String fromcity, String tocity,
			String fromdate, String cabin, String disc, String aircom) {

		
		String methodname = "inter_price_ow";
		
		// 航班全部路线
		AllRouteInfo allrouteinfo = new AllRouteInfo();
		// 接口地址
		// String totalurl="http://localhost:4163/WebClent/Default.aspx";
		String params = "?action=" + methodname + "&fromcity=" + fromcity
				+ "&tocity=" + tocity + "&fromdate=" + fromdate + "&cabin="
				+ cabin + "&disc=" + disc + "&aircom=" + aircom;
		
		List<Route> listRoutes = new ArrayList<Route>();
		try {
			// 添加路线基本信息
			allrouteinfo.setAllRouteID(1);
			allrouteinfo.setRouteStr(fromcity + "-" + tocity);
			org.jdom.Document doc = GetXmlDoc(totalurl + params);
			// 解析xml
			
			Element root = doc.getRootElement();
			Element body = root.getChild("Body", soap);
			Element queryResults = body.getChild("inter_price_owResponse", ns2);
			Element itemarray = queryResults.getChild("Array", nealnet);
			
			List<Element> items = itemarray.getChildren("item", item);
			for (int i = 0; i < items.size(); i++) {
				// 循环有座位航班 Y/N/R（有座/满座/无航线）
				if (items.get(i).getChildText("seats_flag",
						Namespace.getNamespace("seats_flag", soap_uri)) != null
						&& items.get(i).getChildText("seats_flag",
								Namespace.getNamespace("seats_flag", soap_uri))
								.toString().equals("Y")) {

					// 反扣信息
					Element lowfeeitems = items.get(i).getChild("low_fees",
							Namespace.getNamespace("low_fees", soap_uri));
					String Fees = "";
					String Cache = "";
					String currency = "";
					String orig_cash = "";
					String orig_currencylow = "";
					List<Element> loweles = lowfeeitems.getChildren();
					// 币种
					currency = loweles.get(0).getText();
					orig_cash = loweles.get(1).getText();
					String strPolicyId = items.get(i).getChildText("fare_id",
							Namespace.getNamespace("fare_id", soap_uri));

					// 航班线路类-Routes
					Route routeinfo = new Route();// 实例化航班线路
					// 返扣
					routeinfo.setFees(loweles.get(2).getText());
					orig_currencylow = loweles.get(3).getText();
					routeinfo.setFromCity(fromcity);
					routeinfo.setDestCity(tocity);
					// 返现
					routeinfo.setCache(loweles.get(4).getText());
					// 原始币种
					routeinfo.setPolicyMark(items.get(i).getChildText(
							"orig_currency",
							Namespace.getNamespace("orig_currency", soap_uri)));
					// 原始合同价
					routeinfo.setOrig_fare_ad(items.get(i).getChildText(
							"orig_fare_ad",
							Namespace.getNamespace("orig_fare_ad", soap_uri)));
					
					// 纸质合同链接地址
					routeinfo.setContract_url(items.get(i).getChildText(
							"contract_url",
							Namespace.getNamespace("contract_url", soap_uri)));
					// 座位状态 Y/N/R（有座/满座/无航线）
					routeinfo.setSeats_flag(items.get(i).getChildText(
							"seats_flag",
							Namespace.getNamespace("seats_flag", soap_uri)));
					// 运价类型
					routeinfo.setGm_f(items.get(i).getChildText("gm_f",
							Namespace.getNamespace("gm_f", soap_uri)));
					// 合同ID
					routeinfo.setPolicyInfo(strPolicyId);
					
					// 合同航空公司
					routeinfo.setAirCompany(items.get(i).getChildText(
							"airline",
							Namespace.getNamespace("airline", soap_uri)));
					// 合同航空公司返点
					routeinfo.setCommission(items.get(i).getChildText(
							"commission",
							Namespace.getNamespace("commission", soap_uri)));
					// 调整合同价
					routeinfo.setTotalFare(Double
							.parseDouble(items.get(i)
									.getChildText(
											"fare_ad",
											Namespace.getNamespace("fare_ad",
													soap_uri))));
					routeinfo.setDepdateTime(fromdate);
					// 航程信息
					Element segmentelement = items.get(i).getChild("segments",
							Namespace.getNamespace("segments", soap_uri));
					List<Element> segmentdetails = segmentelement.getChildren(
							"item", Namespace.getNamespace("item", soap_uri));
					List<RouteDetailInfo> listroutedetails = new ArrayList<RouteDetailInfo>();
					String strCabin = "";
					for (int d = 0; d < segmentdetails.size(); d++) {
						RouteDetailInfo routedetail = new RouteDetailInfo();
						routedetail.setFromAirport(segmentdetails.get(d)
								.getChildText(
										"depart",
										Namespace.getNamespace("depart",
												soap_uri)));
						routedetail.setToAirport(segmentdetails.get(d)
								.getChildText(
										"arrival",
										Namespace.getNamespace("arrival",
												soap_uri)));
						routedetail.setAirCompany(segmentdetails.get(d)
								.getChildText(
										"carrier",
										Namespace.getNamespace("carrier",
												soap_uri)));
						routedetail.setCabin(segmentdetails.get(d)
								.getChildText(
										"class",
										Namespace.getNamespace("class",
												soap_uri)));
						listroutedetails.add(routedetail);

					}
					routeinfo.setCabin(strCabin);
					routeinfo.setRouteDetailInfos(listroutedetails);
					listRoutes.add(routeinfo);
				}
			}
			allrouteinfo.setRoutes(listRoutes);
			return allrouteinfo;
		} catch (Exception ex) {
			ex.printStackTrace();
			return allrouteinfo;
		}

		

	}

	/**
	 * 查询航班信息 往返 中的去程（OUTBOUND）
	 */
	public AllRouteInfo getOutBound(String fromcity, String tocity,
			String fromdate, String todate, String cabin, String psgtype,
			String aircom, Integer psgcount) {
		AllRouteInfo allrouteinfo = new AllRouteInfo();
		String action = "inter_price_ob";
		String param = "?action=" + action + "&orig_city=" + fromcity
				+ "&dest_city=" + tocity + "&depart_date=" + fromdate
				+ "&return_date=" + todate + "&airline=" + aircom
				+ "&class_type=" + cabin + "&psg_type=" + psgtype
				+ "&psg_count=" + psgcount;
		
		Document doc = GetXmlDoc(totalurl + param);
		//System.out.println("docccccc:"+doc);
		Element root = doc.getRootElement();
		Element body = root.getChild("Body", soap);
		Element queryResults = body.getChild("inter_price_obResponse", ns2);
		Element itemarray = queryResults.getChild("Array", nealnet);
		List<Element> items = itemarray.getChildren("item", item);
		List<Route> listRoutes = new ArrayList<Route>();
		
		for (int i = 0; i < items.size(); i++) {
			// 循环有座位航班 Y/N/R（有座/满座/无航线）
			if (items.get(i).getChildText("seats_flag",
					Namespace.getNamespace("seats_flag", soap_uri)) != null
					&& items.get(i).getChildText("seats_flag",
							Namespace.getNamespace("seats_flag", soap_uri))
							.toString().equals("Y")) {

				// 反扣信息
				Element lowfeeitems = items.get(i).getChild("low_fees",
						Namespace.getNamespace("low_fees", soap_uri));
				String Fees = "";
				String Cache = "";
				String currency = "";
				String orig_cash = "";
				String orig_currencylow = "";
				List<Element> loweles = lowfeeitems.getChildren();
				// 币种
				currency = loweles.get(0).getText();
				orig_cash = loweles.get(1).getText();
				String strPolicyId = items.get(i).getChildText("fare_id",
						Namespace.getNamespace("fare_id", soap_uri));
				String return_low_fare = items.get(i).getChildText(
						"return_low_fare",
						Namespace.getNamespace("return_low_fare", soap_uri));
				// String
				// return_currency=items.get(i).getChildText("return_currency",
				// Namespace.getNamespace("return_currency",soap_uri));
				// String
				// orig_return_low_fare=items.get(i).getChildText("orig_return_low_fare",
				// Namespace.getNamespace("orig_ return_low_fare",soap_uri));
				// tring
				// orig_return_currency=items.get(i).getChildText("orig_return_currency",
				// Namespace.getNamespace("orig_return_currency",soap_uri));

				// 航班线路类-Routes
				Route routeinfo = new Route();// 实例化航班线路
				routeinfo.setReturnTotalFare(Double
						.parseDouble(return_low_fare));
				// routeinfo.setReturncurrencty(return_currency);
				// routeinfo.setReturnorgcurrencty(orig_return_currency);
				// routeinfo.setReturnorigprice(Double.parseDouble(orig_return_low_fare));
				// 返扣
				routeinfo.setFees(loweles.get(2).getText());
				orig_currencylow = loweles.get(3).getText();
				routeinfo.setFromCity(fromcity);
				routeinfo.setDestCity(tocity);
				// 返现
				routeinfo.setCache(loweles.get(4).getText());
				// 原始币种
				routeinfo.setPolicyMark(items.get(i).getChildText(
						"orig_currency",
						Namespace.getNamespace("orig_currency", soap_uri)));
				// 原始合同价
				routeinfo.setOrig_fare_ad(items.get(i).getChildText(
						"orig_fare_ad",
						Namespace.getNamespace("orig_fare_ad", soap_uri)));
				// 纸质合同链接地址
				routeinfo.setContract_url(items.get(i).getChildText(
						"contract_url",
						Namespace.getNamespace("contract_url", soap_uri)));
				// 座位状态 Y/N/R（有座/满座/无航线）
				routeinfo.setSeats_flag(items.get(i).getChildText("seats_flag",
						Namespace.getNamespace("seats_flag", soap_uri)));
				// 运价类型
				routeinfo.setGm_f(items.get(i).getChildText("gm_f",
						Namespace.getNamespace("gm_f", soap_uri)));
				// 合同ID
				routeinfo.setPolicyInfo(strPolicyId);
				
				System.out.println("去程的合同id："+strPolicyId);
				// 合同航空公司
				routeinfo.setAirCompany(items.get(i).getChildText("airline",
						Namespace.getNamespace("airline", soap_uri)));
				// 合同航空公司返点
				routeinfo.setCommission(items.get(i).getChildText("commission",
						Namespace.getNamespace("commission", soap_uri)));
				// 调整合同价
				routeinfo.setTotalFare(Double.parseDouble(items.get(i)
						.getChildText("fare_ad",
								Namespace.getNamespace("fare_ad", soap_uri))));
				routeinfo.setDepdateTime(fromdate);
				// 航程信息
				Element segmentelement = items.get(i).getChild("segments",
						Namespace.getNamespace("segments", soap_uri));
				List<Element> segmentdetails = segmentelement.getChildren(
						"item", Namespace.getNamespace("item", soap_uri));
				List<RouteDetailInfo> listroutedetails = new ArrayList<RouteDetailInfo>();
				String strCabin = "";
				for (int d = 0; d < segmentdetails.size(); d++) {
					RouteDetailInfo routedetail = new RouteDetailInfo();
					routedetail
							.setFromAirport(segmentdetails.get(d).getChildText(
									"depart",
									Namespace.getNamespace("depart", soap_uri)));
					routedetail
							.setToAirport(segmentdetails.get(d)
									.getChildText(
											"arrival",
											Namespace.getNamespace("arrival",
													soap_uri)));
					routedetail
							.setAirCompany(segmentdetails.get(d)
									.getChildText(
											"carrier",
											Namespace.getNamespace("carrier",
													soap_uri)));
					routedetail
							.setCabin(segmentdetails.get(d).getChildText(
									"class",
									Namespace.getNamespace("class", soap_uri)));
					listroutedetails.add(routedetail);

				}
				routeinfo.setCabin(strCabin);
				routeinfo.setRouteDetailInfos(listroutedetails);
				listRoutes.add(routeinfo);
			}
		}
		allrouteinfo.setRoutes(listRoutes);
		return allrouteinfo;
	}

	/**
	 * 查询航班信息 往返 中的回程（inbound）
	 */
	public AllRouteInfo getInBound(String fromcity, String tocity,
			String fromdate, String todate, String cabin, String psgtype,
			String aircom, Integer psgcount, String contractid) {
		
		
		System.out.println("去程的合同id："+contractid);
		
		AllRouteInfo allrouteinfo = new AllRouteInfo();
		String action = "inter_price_ib";
		String param = "?action=" + action + "&orig_city=" + fromcity
				+ "&dest_city=" + tocity + "&depart_date=" + fromdate
				+ "&return_date=" + todate + "&airline=" + aircom
				+ "&class_type=" + cabin + "&psg_type=" + psgtype
				+ "&psg_count=" + psgcount + "&depart_fare_id=" + contractid;
		Document doc = GetXmlDoc(totalurl + param);
		
		Element root = doc.getRootElement();
		Element body = root.getChild("Body", soap);
		Element queryResults = body.getChild("inter_price_ibResponse", ns2);
		Element itemarray = queryResults.getChild("Array", nealnet);
		List<Element> items = itemarray.getChildren("item", item);
		List<Route> listRoutes = new ArrayList<Route>();
		
		
		
		for (int i = 0; i < items.size(); i++) {
			
			// 循环有座位航班 Y/N/R（有座/满座/无航线）
			if (items.get(i).getChildText("seats_flag",
					Namespace.getNamespace("seats_flag", soap_uri)) != null
					&& items.get(i).getChildText("seats_flag",
							Namespace.getNamespace("seats_flag", soap_uri))
							.toString().equals("Y")) {
				System.out.println("有座啊");
				// 反扣信息
				Element lowfeeitems = items.get(i).getChild("low_fees",
						Namespace.getNamespace("low_fees", soap_uri));
				String Fees = "";
				String Cache = "";
				String currency = "";
				String orig_cash = "";
				String orig_currencylow = "";
				List<Element> loweles = lowfeeitems.getChildren();
				// 币种
				currency = loweles.get(0).getText();
				
				orig_cash = loweles.get(1).getText();
				
				String strPolicyId = items.get(i).getChildText("fare_id",
						Namespace.getNamespace("fare_id", soap_uri));
			//	String return_low_fare = items.get(i).getChildText(
			//			"return_low_fare",
			//			Namespace.getNamespace("return_low_fare", soap_uri));
				
			//	System.out.println("reee:"+return_low_fare);
				// String
				// return_currency=items.get(i).getChildText("return_currency",
				// Namespace.getNamespace("return_currency",soap_uri));
				// String
				// orig_return_low_fare=items.get(i).getChildText("orig_return_low_fare",
				// Namespace.getNamespace("orig_ return_low_fare",soap_uri));
				// tring
				// orig_return_currency=items.get(i).getChildText("orig_return_currency",
				// Namespace.getNamespace("orig_return_currency",soap_uri));

				// 航班线路类-Routes
				Route routeinfo = new Route();// 实例化航班线路
			//	routeinfo.setReturnTotalFare(Double
				//		.parseDouble(return_low_fare));
				// routeinfo.setReturncurrencty(return_currency);
				// routeinfo.setReturnorgcurrencty(orig_return_currency);
				// routeinfo.setReturnorigprice(Double.parseDouble(orig_return_low_fare));
				// 返扣
				routeinfo.setFees(loweles.get(2).getText());
				orig_currencylow = loweles.get(3).getText();
				routeinfo.setFromCity(fromcity);
				routeinfo.setDestCity(tocity);
				// 返现
				routeinfo.setCache(loweles.get(4).getText());
				// 原始币种
				routeinfo.setPolicyMark(items.get(i).getChildText(
						"orig_currency",
						Namespace.getNamespace("orig_currency", soap_uri)));
				
				// 原始合同价
				routeinfo.setOrig_fare_ad(items.get(i).getChildText(
						"orig_fare_ad",
						Namespace.getNamespace("orig_fare_ad", soap_uri)));
				// 纸质合同链接地址
				routeinfo.setContract_url(items.get(i).getChildText(
						"contract_url",
						Namespace.getNamespace("contract_url", soap_uri)));
				// 座位状态 Y/N/R（有座/满座/无航线）
				routeinfo.setSeats_flag(items.get(i).getChildText("seats_flag",
						Namespace.getNamespace("seats_flag", soap_uri)));
				// 运价类型
				routeinfo.setGm_f(items.get(i).getChildText("gm_f",
						Namespace.getNamespace("gm_f", soap_uri)));
				// 合同ID
				routeinfo.setPolicyInfo(strPolicyId);
				
				// 合同航空公司
				routeinfo.setAirCompany(items.get(i).getChildText("airline",
						Namespace.getNamespace("airline", soap_uri)));
				// 合同航空公司返点
				routeinfo.setCommission(items.get(i).getChildText("commission",
						Namespace.getNamespace("commission", soap_uri)));
				// 调整合同价
				routeinfo.setTotalFare(Double.parseDouble(items.get(i)
						.getChildText("fare_ad",
								Namespace.getNamespace("fare_ad", soap_uri))));
				routeinfo.setDepdateTime(fromdate);
				// 航程信息
				Element segmentelement = items.get(i).getChild("segments",
						Namespace.getNamespace("segments", soap_uri));
				List<Element> segmentdetails = segmentelement.getChildren(
						"item", Namespace.getNamespace("item", soap_uri));
				List<RouteDetailInfo> listroutedetails = new ArrayList<RouteDetailInfo>();
				
				for (int d = 0; d < segmentdetails.size(); d++) {
					RouteDetailInfo routedetail = new RouteDetailInfo();
					routedetail
							.setFromAirport(segmentdetails.get(d).getChildText(
									"depart",
									Namespace.getNamespace("depart", soap_uri)));
					routedetail
							.setToAirport(segmentdetails.get(d)
									.getChildText(
											"arrival",
											Namespace.getNamespace("arrival",
													soap_uri)));
					routedetail
							.setAirCompany(segmentdetails.get(d)
									.getChildText(
											"carrier",
											Namespace.getNamespace("carrier",
													soap_uri)));
					routedetail
							.setCabin(segmentdetails.get(d).getChildText(
									"class",
									Namespace.getNamespace("class", soap_uri)));
					listroutedetails.add(routedetail);

				}
				
				routeinfo.setRouteDetailInfos(listroutedetails);
				listRoutes.add(routeinfo);
			}else {
				System.out.println("没座啊");
			}
		}
		allrouteinfo.setRoutes(listRoutes);

		return allrouteinfo;
	}

	/**
	 * 根据城市三字码获取城市名称
	 * 
	 * @param aircompany
	 * @return
	 */
	public String getCityNamebyCode(String code) {
		String where = "where " + Fairport.COL_airportcode + "='" + code + "'";
		List<Cityairport> list = com.ccservice.b2b2c.atom.server.Server
				.getInstance().getAirService().findAllCityairport(where,
						"ORDER BY ID", -1, 0);
		return list != null && list.size() > 0 ? list.get(0).getCityname() : "";

	}

	/**
	 * 根据国际航空公司获取航空公司名称
	 * 
	 * @param aircompany
	 * @return
	 */
	public String getAirCompanyNamebyCode(String code) {
		String AirCompanyName = code;
		// 从缓存中得到航空公司信息
		List<Aircompany> listAirCompany = com.ccservice.b2b2c.atom.server.Server
				.getInstance().getTicketSearchService().getAircompanyCache();
		for (Aircompany aircompany : listAirCompany) {
			if (aircompany.getAircomcode().equals(code)) {
				AirCompanyName = aircompany.getAircomjname();
			}
		}
		return AirCompanyName;
	}
	
	public String getAirportNameByCode(String code){

		System.out.println("到这里了：："+code);
		String where=" where C_AIRPORTCODE='"+code+"'";
		 Cityairport airport = (Cityairport) Server.getInstance().getAirService().findAllCityairport(where, "", -1, 0).get(0);
		System.out.println("机场名字："+airport.getAirportname());
		return airport.getAirportname();
	}

	/**
	 * 查询实时价格问题
	 * 
	 * @return
	 */
	public List<InstancePrice> getQTEprice(String trivel_type, String carrier,
			String qinfo, String hinfo) {
		Double d = 0.0;
		if (trivel_type.equals("1")) {
			trivel_type = "OW";
		} else {
			trivel_type = "RT";
		}
		// trivel_type = "OW";
		// carrier = "HO";
		// qinfo = "HO1293,H,2012-09-26,SHA,HVG";
		Document price = GetXmlDocByPara(soap_addr, getinter_detect_price(
				trivel_type, "", qinfo, hinfo));
		Namespace moren = Namespace.getNamespace("http://intf.atm86.com/inter");
		Namespace soapenc = Namespace
				.getNamespace("http://schemas.xmlsoap.org/soap/encoding/");
		Element Envelope = price.getRootElement();
		Element soapd = Envelope.getChild("Body", soap);
		Element inter_detect_priceResponse = soapd.getChild(
				"inter_detect_priceResponse", moren);
		Element s_gensym3 = inter_detect_priceResponse.getChild("s-gensym3",
				moren);
		String status = s_gensym3.getChildText("status", moren);
		List<InstancePrice> instance = new ArrayList<InstancePrice>();
		if (status.equals("100")) {
			String nofarerbd = s_gensym3.getChildText("nofarerbd", moren);
			System.out.println("data:" + nofarerbd);
			if (nofarerbd == null || "".equals(nofarerbd)) {
				System.out.println("QTE获取失败");
			} 
			//else if("1".equals(nofarerbd)){
			//	System.out.println("QTE纸质合同取合同价");
			//}
			else{
				
				Element data = s_gensym3.getChild("data", moren);
				Element ad=data.getChild("ad", moren);
				InstancePrice adprice = new InstancePrice();
				adprice.setPeopleType(1d);
				adprice.setCurrencytype(ad.getChildText("currency", moren));
				adprice.setPrice(Double.parseDouble(ad.getChildText("fare",
						moren)));
				adprice.setQvalue(Double.parseDouble(ad
						.getChildText("q", moren)));
				adprice.setTax(Double
						.parseDouble(ad.getChildText("tax", moren)));
				instance.add(adprice);
				Element in = data.getChild("in", moren);
				InstancePrice inprice = new InstancePrice();
				inprice.setPeopleType(3d);
				inprice.setCurrencytype(in.getChildText("currency", moren));
				inprice.setPrice(Double.parseDouble(in.getChildText("fare",
						moren)));
				inprice.setQvalue(Double.parseDouble(in
						.getChildText("q", moren)));
				inprice.setTax(Double
						.parseDouble(in.getChildText("tax", moren)));
				instance.add(inprice);
				Element ch = data.getChild("ch", moren);
				InstancePrice chprice = new InstancePrice();
				chprice.setPeopleType(2d);
				chprice.setCurrencytype(ch.getChildText("currency", moren));
				chprice.setPrice(Double.parseDouble(ch.getChildText("fare",
						moren)));
				chprice.setQvalue(Double.parseDouble(ch
						.getChildText("q", moren)));
				chprice.setTax(Double
						.parseDouble(ch.getChildText("tax", moren)));
				instance.add(chprice);
			}

			System.out.println("实时价格："+instance.get(0).getPrice());
		} else {
			System.out.println("实时查询价格异常：状态码："+status);
		}
		return instance;
	}

	public static void main(String[] args) {
		
		
		//new ATMInternalTicket().getInBound("BJS", "HKG", "2012-10-17", "2012-10-27", "Y", "ADT", "", 1, "13661871_500659");
//		List[] list = new ATMInternalTicket().getRouteInfo("2", "1", "1", "Y", "SHA", "TYO", "2013-07-12", "2013-07-23");
//		System.out.println(((InterAri)list[0].get(0)).getEquivCurrency());
//		System.out.println(list[1].size());
	}

	/**
	 * 
	 * @param triptype
	 *            旅行类型 OW/RT（单程/来回程）
	 * @param company
	 *            合同航空公司
	 * @param qinfo
	 *            去程信息
	 * @param hinfo
	 *            回程信息
	 * @return
	 */
	public String getQFee(String triptype, String company, String qinfo,
			String hinfo) {
		if (triptype.equals("1")) {
			triptype = "OW";
		} else {
			triptype = "RT";
		}
		Document fee = GetXmlDocByPara(soap_addr, getFaxSoap(triptype, company,
				qinfo, hinfo));
		Namespace moren = Namespace.getNamespace("http://intf.atm86.com/inter");
		Namespace soapenc = Namespace
				.getNamespace("http://schemas.xmlsoap.org/soap/encoding/");
		Element Envelope = fee.getRootElement();
		Element soapd = Envelope.getChild("Body", soap);
		Element inter_ref_taxResponse = soapd.getChild("inter_ref_taxResponse",
				moren);
		Element s_gensym3 = inter_ref_taxResponse.getChild("s-gensym3", moren);
		Element status = s_gensym3.getChild("status", moren);
		if (status.getText().equals("100")) {
			Element data = s_gensym3.getChild("data", moren);
			String childtax = data.getChildText("ch_tax", moren);
			String adulttax = data.getChildText("ad_tax", moren);
			System.out.println("儿童税：" + childtax + ",成人税：" + adulttax);
			return adulttax;
		} else {
			System.out.println("查询税费异常：状态吗" + status);
			return "暂无数据";
		}
	}

	/**
	 * 查询供应商政策
	 * 
	 * @param fare_id_ob
	 *            qu政策id
	 * @param depart_date
	 *            去日期
	 * @param return_date
	 *            回日期
	 * @param fare_id_ib
	 *            回政策id
	 * @return
	 */
	public List<InterZefees> getInterZefees(String fare_id_ob,
			String depart_date, String return_date, String fare_id_ib) {
		List<InterZefees> fee = new ArrayList<InterZefees>();
		String zcparam = "?action=inter_fees&fare_id_ob=" + fare_id_ob
				+ "&depart_date=" + depart_date + "&return_date=" + return_date
				+ "&fare_id_ib=" + fare_id_ib;
		Namespace soap = Namespace
				.getNamespace("http://schemas.xmlsoap.org/soap/envelope/");
		Namespace moren = Namespace.getNamespace("http://intf.atm86.com/inter");
		Namespace soapenc = Namespace
				.getNamespace("http://schemas.xmlsoap.org/soap/encoding/");
		Document feedoc = GetXmlDoc(totalurl + zcparam);
		Element Envelope = feedoc.getRootElement();
		Element soapd = Envelope.getChild("Body", soap);
		Element inter_feesResponse = soapd
				.getChild("inter_feesResponse", moren);
		Element Array = inter_feesResponse.getChild("Array", soapenc);
		List<Element> items = Array.getChildren("item", moren);
		for (int i = 0; i < items.size(); i++) {
			Element item = items.get(i);
			String Fareid = item.getChildText("fees_id", moren);
			Double fees = Double.parseDouble(item.getChildText("fees", moren));
			Double cash = Double.parseDouble(item.getChildText("cash", moren));
			Double commission = Double.parseDouble(item.getChildText(
					"commission", moren));
			String supplier_id = item.getChildText("supplier_id", moren);
			InterZefees zc = new InterZefees();
			zc.setCash(cash);
			zc.setCommission(commission);
			zc.setFare_id(Fareid);
			zc.setSupplier_id(supplier_id);
			zc.setFees(fees);
			fee.add(zc);
		}
		return fee;
	}
	
	public String format_time(String time){
		System.out.println(time);
		String date=time.substring(0, 2)+":"+time.substring(2, 4);
	
		return date;
	}

	/**
	 * 取得航班具体信息
	 * 
	 * @param triptype
	 * @param fromcity
	 * @param tocity
	 * @param fromdate
	 * @param fareid
	 * @return
	 */
	/**
	 * @param triptype
	 * @param fromcity
	 * @param tocity
	 * @param fromdate
	 * @param fareid
	 * @param contractprice
	 * @param gm_f
	 * @param intTravelType
	 * @param backflag
	 * @return
	 */
	public String getAllFlightInfo(String triptype, String fromcity,
			String tocity, String fromdate, String fareid,
			String contractprice, String gm_f,Integer intTravelType,Integer backflag) {
		
		
		String cabin="";
		String strReturn = "<table width='100%' border='0' cellspacing='0' cellpadding='0' class='orter'>";

		strReturn += "<tr>";
		strReturn += "<th width='10%' style='text-align: center;'>航班号</th>";
		strReturn += "<th width='10%' style='text-align: center;'>出发日期</th>";
		strReturn += "<th width='10%' style='text-align: center;'>起飞</th>";
		strReturn += "<th width='10%' style='text-align: center;'>抵达</th>";
		strReturn += "<th width='10%' style='text-align: center;'>起飞时间</th>";
		strReturn += "<th width='10%' style='text-align: center;'>到达时间</th>";
		strReturn += "<th width='10%' style='text-align: center;'>起降航站楼</th>";
		strReturn += "<th width='5%' style='text-align: center;'>机型</th>";
		strReturn += "<th width='10%' style='text-align: center;'>舱位/余票</th>";
		strReturn += "<th width='5%' style='text-align: center;'>飞行时间</th>";
		
			
		if(intTravelType==2&&backflag==1){
			strReturn += "<th width='10%' style='text-align: center;'>查看回程</th>";
		}else{
			strReturn += "<th width='10%' style='text-align: center;'>预订</th>";
		}
		
		strReturn += "</tr>";
		String trip_type="";
		// 根据合同id查询航班信息
		if(intTravelType==1){
			System.out.println("单程航班具体信息查询");
			trip_type = "OW";
		}else if(intTravelType==2&&backflag==1){
			System.out.println("去程航班具体信息查询");
			trip_type = "OB";
		}else{
			System.out.println("回程航班具体信息查询");
			trip_type = "IB";
		}
		
		String flightaction = "inter_flight";
		String flightparams = "?action=" + flightaction
				+ "&trip_type="+trip_type+"&orig_city=" + fromcity + "&dest_city="
				+ tocity + "&depart_date=" + fromdate + "&fare_id=" + fareid;
		// 调用接口，取得返回结果
		org.jdom.Document docflight = GetXmlDoc(totalurl + flightparams);
		// 解析xml
		Namespace soapflight = Namespace.getNamespace("soap",
				"http://schemas.xmlsoap.org/soap/envelope/");
		Namespace ns2flight = Namespace.getNamespace("inter_price_owResponse",
				"http://intf.atm86.com/inter");
		Namespace nealnetflight = Namespace.getNamespace("Array",
				"http://schemas.xmlsoap.org/soap/encoding/");
		Namespace itemflight = Namespace.getNamespace("item",
				"http://intf.atm86.com/inter");
		Element rootflight = docflight.getRootElement();
		Element bodyflight = rootflight.getChild("Body", soap);
		Element flightResults = bodyflight
				.getChild("inter_flightResponse", ns2);
		Element flightarray = flightResults.getChild("Array", nealnet);
		List<Element> flightitems = flightarray.getChildren("item", item);
		StringBuilder sb = null;
		for (int f = 0; f < flightitems.size(); f++) {
			sb = new StringBuilder();
			strReturn += "<tr>";
			strReturn += "<td colspan='11'>";
			strReturn += "<table width='100%' border='0' cellspacing='0' cellpadding='0' style='border-color:#fff'> ";
			strReturn += "<tr>";
			strReturn += "<td width='90%'>";
			strReturn += "<table width='100%' border='0' cellspacing='0' cellpadding='0' style='border-color:#fff'> ";
			List<Element> flightdetails = flightitems.get(f).getChildren(
					"item", item);
			// 航班线路详情类-RouteDetails
			String departName="";
			String depart="";
			String arrival="";
			
			List<RouteDetailInfo> rotinfo = null;
			RouteDetailInfo route=new RouteDetailInfo();
			//JSONObject jso=new JSONObject();

			StringBuffer sbf=new StringBuffer();
			sbf.append("[");
			for (int d = 0; d < flightdetails.size(); d++) {
				rotinfo=new ArrayList<RouteDetailInfo>();
				strReturn += "<tr>";
				strReturn += "<td width='10%'>"+ flightdetails.get(d).getChildText("flight_no",Namespace.getNamespace("flight_no", soap_uri))+ "</td>";
				String flight_no = flightdetails.get(d).getChildText("flight_no",Namespace.getNamespace("flight_no", soap_uri));
				strReturn += "<td width='10%'>"+ flightdetails.get(d).getChildText("depart_date",Namespace.getNamespace("depart_date",soap_uri)) + "</td>";
				
				strReturn += "<td width='10%'>"+ getCityNamebyCode(flightdetails.get(d).getChildText("depart",Namespace.getNamespace("depart", soap_uri)))+ "</td>";
				
				String depart_date=flightdetails.get(d).getChildText("depart_date",Namespace.getNamespace("depart_date", soap_uri));
				System.out.println("起飞日期："+depart_date);
				depart=getCityNamebyCode(flightdetails.get(d).getChildText("depart",Namespace.getNamespace("depart", soap_uri)));
				
				departName=flightdetails.get(d).getChildText("depart",Namespace.getNamespace("depart", soap_uri));
				
				strReturn += "<td width='10%'>"+ getCityNamebyCode(flightdetails.get(d).getChildText("arrival",Namespace.getNamespace("arrival", soap_uri)))+ "</td>";
				
				arrival=getCityNamebyCode(flightdetails.get(d).getChildText("arrival",Namespace.getNamespace("arrival", soap_uri)));
				
				String arrivalName=flightdetails.get(d).getChildText("arrival",Namespace.getNamespace("arrival", soap_uri));
				//起飞时间
				String depart_time= format_time(flightdetails.get(d).getChildText("depart_time",Namespace.getNamespace("depart_time",soap_uri)));
				route.setFromTime(depart_time);
				
				strReturn += "<td width='10%'>"+ depart_time+ "</td>";
				
				
				
				//到达时间
				String arrival_time=format_time( flightdetails.get(d).getChildText("arrival_time",Namespace.getNamespace("arrival_time",soap_uri)));
				
				route.setToTime(arrival_time);
				
				strReturn += "<td width='10%'>"+arrival_time+ "</td>";
			
				
				//起降航站楼
				String depart_terminal= flightdetails.get(d).getChildText("depart_terminal",Namespace.getNamespace("depart_terminal",soap_uri));
				
				String arrival_terminal= flightdetails.get(d).getChildText("arrival_terminal",Namespace.getNamespace("arrival_terminal",soap_uri));
				
				route.setDepart_terminal(depart_terminal);
				
				route.setArrival_terminal(arrival_terminal);
				
				strReturn += "<td width='10%'>"+ depart_terminal+"/"+arrival_terminal+ "</td>";
				
				//机型
				String aircraft=flightdetails.get(d).getChildText("aircraft",Namespace.getNamespace("aircraft", soap_uri));
				
				strReturn += "<td width='5%'>"+ aircraft+ "</td>";
				
				route.setPlane(aircraft);
				
				String strRemainSeat = flightdetails.get(d).getChildText("remain_seats",Namespace.getNamespace("remain_seats", soap_uri));
				
				
				
				if (strRemainSeat.equals("A")) {
					
					strRemainSeat = ">9";
					
				} else {
					
					strRemainSeat = "=" + strRemainSeat;
					
				}
				strReturn += "<td width='10%'>"+ flightdetails.get(d).getChildText("class",Namespace.getNamespace("class", soap_uri))+ "/" + strRemainSeat + "</td>";
				
				String flight_time=flightdetails.get(d).getChildText("flight_time",Namespace.getNamespace("flight_time",soap_uri));
				
				String carrier = getAirCompanyNamebyCode(flightdetails.get(d).getChildText("carrier",Namespace.getNamespace("carrier", soap_uri)));
				
				String carrierName=flightdetails.get(d).getChildText("carrier",Namespace.getNamespace("carrier", soap_uri));
				
				String classes = flightdetails.get(d).getChildText("class",Namespace.getNamespace("class", soap_uri));
				
				route.setFlyTime(flight_time);
				
				sbf.append("{\"depart_time\":\"");
				sbf.append(depart_time);
				sbf.append("\",\"arrival_time\":\"");
				sbf.append(arrival_time);
				sbf.append("\",\"arrival_terminal\":\"");
				sbf.append(arrival_terminal);
				sbf.append("\",\"depart_terminal\":\"");
				sbf.append(depart_terminal);
				sbf.append("\",\"aircraft\":\"");
				sbf.append(aircraft);
				sbf.append("\",\"flight_time\":\"");
				sbf.append(flight_time);
				sbf.append("\",\"depart_date\":\"");
				sbf.append(depart_date);
				sbf.append("\",\"departName\":\"");
				sbf.append(depart);
				sbf.append("\",\"depart\":\"");
				sbf.append(departName);
				sbf.append("\",\"arrivalName\":\"");
				sbf.append(arrival);
				sbf.append("\",\"carrierName\":\"");
				sbf.append(carrier);
				sbf.append("\",\"arrival\":\"");
				sbf.append(arrivalName);
				sbf.append("\",\"carrier\":\"");
				sbf.append(carrierName);
				sbf.append("\",\"classes\":\"");
				sbf.append(classes);
				sbf.append("\",\"strRemainSeat\":\"");
				sbf.append(strRemainSeat);
				sbf.append("\",\"flight_no\":\"");
				sbf.append(flight_no);
				if(flightdetails.size()==1){
					sbf.append("\",\"jsonflag\":\"");
					sbf.append(1);
				}else{
					sbf.append("\",\"jsonflag\":\"");
					sbf.append(2);
				}
				
				if(flightdetails.size()-1==d){
					sbf.append("\"}");
					
				}else{
					sbf.append("\"},");
				}
				
				
				rotinfo.add(route);
				
				String key="routdetail"+d;
				
			//	jso.put(key, rotinfo);
				
				strReturn += "<td width='5%'>"+ flight_time+ "</td>";
				
				strReturn += "</tr>";

				sb.append(flightdetails.get(d).getChildText("carrier",Namespace.getNamespace("carrier", soap_uri)));
				
				sb.append("kk"+ flightdetails.get(d).getChildText("flight_no",Namespace.getNamespace("flight_no", soap_uri)));
				
				sb.append("kk"+ flightdetails.get(d).getChildText("depart_date",Namespace.getNamespace("depart_date",soap_uri)));
				
				sb.append("kk"+ getCityNamebyCode(flightdetails.get(d).getChildText("depart",Namespace.getNamespace("depart", soap_uri))));
				
				sb.append("kk"+ getCityNamebyCode(flightdetails.get(d).getChildText("arrival",Namespace.getNamespace("arrival", soap_uri))));
				
				sb.append("kk"+ flightdetails.get(d).getChildText("depart_time",Namespace.getNamespace("depart_time",soap_uri)));
				
				sb.append("kk"+ flightdetails.get(d).getChildText("arrival_time",Namespace.getNamespace("arrival_time",soap_uri)));
				
				sb.append("kk"+ flightdetails.get(d).getChildText("depart_terminal",Namespace.getNamespace("depart_terminal",soap_uri))+ "/"
						+ flightdetails.get(d).getChildText("arrival_terminal",Namespace.getNamespace("arrival_terminal",soap_uri)));
				
				sb.append("kk"+ flightdetails.get(d).getChildText("aircraft",Namespace.getNamespace("aircraft", soap_uri)));
				
				sb.append("kk"+ flightdetails.get(d).getChildText("class",Namespace.getNamespace("class", soap_uri)));
				
				cabin=flightdetails.get(d).getChildText("class",Namespace.getNamespace("class", soap_uri));
				
				sb.append("kk" + strRemainSeat);
				
				sb.append("kk"+ flightdetails.get(d).getChildText("flight_time",Namespace.getNamespace("flight_time",soap_uri)));
				
				sb.append("kk"+ flightdetails.get(d).getChildText("depart",Namespace.getNamespace("depart", soap_uri)));
				
				sb.append("kk"+ flightdetails.get(d).getChildText("arrival",Namespace.getNamespace("arrival", soap_uri)));
				
				sb.append("kl");
			}
			sbf.append("]");
			
			System.out.println("拼接的json："+sbf);
			
			System.out.println("是否转机"+rotinfo.size());
			strReturn += "</table>";
			strReturn += "</td>";
			if(intTravelType==1){
				strReturn += "<td><input type='button' class='bnt_booking' onclick='qbooking(\""
					+ sb.toString()
					+ "\",\""
					+ fareid
					+ "\",\""
					+ fromdate
					+ "\",\"" + contractprice + "\",\"" + gm_f + "\")'/></td>";
			
			}else if(intTravelType==2&&backflag==2){
				System.out.println("回城预定");
				strReturn += "<td><input type='button' class='bnt_booking' onclick='Doubleqbooking(\""
					+ sb.toString()
					+ "\",\""
					+ fareid
					+ "\",\""
					+ fromdate
					+ "\",\"" + contractprice + "\",\"" + gm_f + "\")'/></td>";
				
			}else{
				//this.getInBound(fromcity, tocity, fromdate, todate, cabin, psgtype, "", psgcount, contractid)
				backflag=1;
				strReturn += "<td><input type='button' class='bnt_back' value='查看回程' onclick='qbackbooking(\""
					+ fareid
					+ "\",\""
					+ depart
					+ "\",\""
					+ arrival
					+ "\","
					+ sbf.toString()
					
					+ ")'/></td>";
				
			}
			
			strReturn += "</tr>";
			strReturn += "</table>";
			strReturn += "</td>";
			strReturn += "</tr>";
			
		}
		
		
		strReturn += "</table>";
		return strReturn;
	}

	// 将 BASE64 编码的字符串 s 进行解码
	public static String getFromBASE64(String s) {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b, "utf-8");
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 根据合同id,查询退改签信息
	 * 
	 * @param fareid
	 * @return
	 */
	public String getInterRules(String fareid) {
		String strreturn = "";
		String methodname = "inter_contract";
		// 接口地址
		String params = "?action=" + methodname + "&serial_number="
				+ serial_number + "&fare_id=" + fareid;
		// 调用接口，取得返回结果
		org.jdom.Document docflight = GetXmlDoc(totalurl + params);
		// 解析xml
		Namespace soapflight = Namespace.getNamespace("soap",
				"http://schemas.xmlsoap.org/soap/envelope/");
		Namespace ns2flight = Namespace.getNamespace("inter_contractResponse",
				"http://intf.atm86.com/inter");
		Element rootflight = docflight.getRootElement();
		Element bodyflight = rootflight.getChild("Body", soap);
		Element flightResults = bodyflight.getChild("inter_contractResponse",
				ns2);
		List<Element> flightitems = flightResults.getChildren("s-gensym3", ns2);
		List<Element> rules = flightitems.get(0).getChildren();

		// 退票规则
		String ref_detail = getFromBASE64(rules.get(0).getText());
		// 最小人数
		String gv_mn = rules.get(1).getText();
		// 最大人数
		String gv_mx = getFromBASE64(rules.get(2).getText());
		// 备注
		String remark = rules.get(3).getText();
		// 提前预售
		String advp = getFromBASE64(rules.get(4).getText());
		// 最大停留
		String sty_mx = getFromBASE64(rules.get(5).getText());
		// 改签规则
		String rer_detail = rules.get(6).getText();
		// 销售起始
		String sale_from = rules.get(7).getText();
		// Noshow规则
		String no_show = getFromBASE64(rules.get(8).getText());
		// 儿童票说明
		String fare_dtl_ch = rules.get(9).getText();
		// 最小停留
		String sty_mn = getFromBASE64(rules.get(10).getText());
		// 销售截至
		String sale_to = rules.get(11).getText();
		// 改期规则
		String reb_detail = "";
		try {
			reb_detail = getFromBASE64(rules.get(12).getText());
		} catch (Exception ex) {
		}
		// 停留规则
		String stay_detail = getFromBASE64(rules.get(13).getText());

		strreturn += "退票规则：" + ref_detail + "<br />";
		strreturn += "改签规则：" + rer_detail + "<br />";
		strreturn += "改期规则：" + reb_detail + "<br />";
		strreturn += "Noshow规则：" + no_show + "<br />";
		strreturn += "儿童票说明：" + fare_dtl_ch + "<br />";
		strreturn += "销售起始：" + sale_from + "<br />";
		strreturn += "销售截止：" + sale_to + "<br />";
		strreturn += "最小人数：" + gv_mn + "<br />";
		strreturn += "最大人数：" + gv_mx + "<br />";
		// strreturn+="最小停留："+sty_mn+"<br />";
		strreturn += "最大停留：" + sty_mx + "<br />";
		strreturn += "提前预售：" + advp + "<br />";
		strreturn += "停留规则：" + stay_detail + "<br />";

		return strreturn;
	}

	/**
	 * 将Base64编码转换成String
	 * 
	 * @param strparam
	 * @return
	 */
	public String getBase64String(String strparam) {
		String strReturn = "";
		String flightparams = "?action=getBase64String&rulesparam=" + strparam;
		// 调用接口，取得返回结果
		String strUrl = totalurl + flightparams;
		try {
			java.net.URL Url = new java.net.URL(strUrl);
			try {
				java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				InputStreamReader isr = null;

				isr = new InputStreamReader(is);
				BufferedReader in = new BufferedReader(isr);
				StringWriter out = new StringWriter();
				int c = -1;

				while ((c = in.read()) != -1) {
					out.write(c);
				}
				strReturn = out.toString();
			} catch (Exception ex) {

			}
		} catch (MalformedURLException e) {
			return "";
		}

		return strReturn;
	}

	public static Document GetXmlDoc(String strUrl) {
		try {
			java.net.URL Url = new java.net.URL(strUrl);
			System.out.println(strUrl);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream();
			InputStreamReader isr = null;
			isr = new InputStreamReader(is);
			BufferedReader in = new BufferedReader(isr);
			org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
			org.jdom.Document doc = build.build(in);
			
			return doc;
		} catch (Exception ex) {
			System.out.println("接口异常:" + ex.getMessage());
			return null;
		}

	}

	public static Document GetXmlDocByPara(String strUrl, String xmlstr) {
		try {
			java.net.URL Url = new java.net.URL(strUrl);
			System.out.println(strUrl);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url
					.openConnection();
			conn.setRequestProperty("Content-Type", "text/xml");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.connect();
			conn.getOutputStream().write(xmlstr.getBytes("utf-8"));// 输入参数
			String inputString = "";
			BufferedReader br = new BufferedReader(new InputStreamReader(conn
					.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				inputString += line;
			}
			br.close();
			org.jdom.input.SAXBuilder build = new org.jdom.input.SAXBuilder();
			org.jdom.Document doc = build.build(new StringReader(inputString));
			System.out.println("xml:" + inputString);
			return doc;
		} catch (Exception ex) {
			System.out.println("接口异常:" + ex.getMessage());
			return null;
		}

	}

	public static String parseSoapXml() {

		return "";
	}

	/**
	 * SOAP XML参数
	 * 
	 * @param methodname
	 *            方法名称
	 * @param argnames
	 *            参数名称
	 * @param args
	 *            参数值
	 * @param _namespace
	 *            命名空间
	 * @return soap xml
	 */
	public static String getSendData(String methodname, String argnames,
			String args, String _namespace) {
		String data = "";
		data = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
		data = data
				+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">";
		data = data + "<soap:Body>";
		data = data + "<" + methodname + " xmlns=\"" + _namespace + "\">";
		if (args != "") {
			String[] arr = args.split(",");
			String[] arr1 = argnames.split(",");
			for (int i = 0; i < arr.length; i++) {
				data = data + "<" + arr1[i] + "><![CDATA[" + arr[i] + "]]></"
						+ arr1[i] + ">";
			}
		}
		data = data + "</" + methodname + ">";
		data = data + "</soap:Body>";
		data = data + "</soap:Envelope>";
		return data;
	}

	/**
	 * 查询税费的soap请求
	 * 
	 * @return
	 */
	public String getFaxSoap(String triptype, String company, String qinfo,
			String hinfo) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb
				.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"  soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\"  xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		sb.append("<soap:Body>");
		sb.append("<inter_ref_tax xmlns=\"http://intf.atm86.com/inter\">");
		sb.append("<c-gensym3 xsi:type=\"xsd:string\">" + serial_number
				+ "</c-gensym3>");
		sb.append("<c-gensym5 xsi:type=\"xsd:string\">" + triptype
				+ "</c-gensym5>");
		sb.append("<c-gensym7 xsi:type=\"xsd:string\">" + company
				+ "</c-gensym7>");
		// MU,K,2012-09-26,BJS,SHA@MU,M,2012-09-26,SHA,HKG
		String[] arys = qinfo.split("@");
		sb.append("<soapenc:Array soapenc:arrayType=\"xsd:anyType["
				+ arys.length + "]\" xsi:type=\"soapenc:Array\">");
		for (int i = 0; i < arys.length; i++) {
			String[] info = arys[i].split(",");
			if (info.length == 5) {
				sb.append("<item>");
				sb.append("<carrier xsi:type=\"xsd:string\">" + info[0]
						+ "</carrier>");
				sb.append("<depart xsi:type=\"xsd:string\">" + info[3]
						+ "</depart>");
				sb.append("<depart_date xsi:type=\"xsd:date\">" + info[2]
						+ "</depart_date>");
				sb.append("<class xsi:type=\"xsd:string\">" + info[1]
						+ "</class>");
				sb.append("<arrival xsi:type=\"xsd:string\">" + info[4]
						+ "</arrival>");
				sb.append("</item>");
			}
		}
		sb.append("</soapenc:Array>");
		if (triptype.equals("RT")) {
			String[] hary = hinfo.split("@");
			sb.append("<soapenc:Array soapenc:arrayType=\"xsd:anyType["
					+ arys.length + "]\" xsi:type=\"soapenc:Array\">");
			for (int i = 0; i < hary.length; i++) {
				String[] info = arys[i].split(",");
				if (info.length == 5) {
					sb.append("<item>");
					sb.append("<carrier xsi:type=\"xsd:string\">" + info[0]
							+ "</carrier>");
					sb.append("<depart xsi:type=\"xsd:string\">" + info[3]
							+ "</depart>");
					sb.append("<depart_date xsi:type=\"xsd:date\">" + info[2]
							+ "</depart_date>");
					sb.append("<class xsi:type=\"xsd:string\">" + info[1]
							+ "</class>");
					sb.append("<arrival xsi:type=\"xsd:string\">" + info[4]
							+ "</arrival>");
					sb.append("</item>");
				}
			}
			sb.append("</soapenc:Array>");
		}
		sb.append("</inter_ref_tax>");
		sb.append("</soap:Body>");
		sb.append("</soap:Envelope>");
		return sb.toString();
	}

	/**
	 * 查询实时票面价
	 * 
	 * @return
	 */
	public String getinter_detect_price(String trivel_type, String carrier,
			String qinfo, String hinfo) {
		StringBuilder sb = new StringBuilder();
		String[] arys = qinfo.split("@");
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb
				.append("<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"  soap:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\"  xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">");
		sb.append("<soap:Body>");
		sb.append("<inter_detect_price xmlns=\"http://intf.atm86.com/inter\">");
		sb.append("<c-gensym3 xsi:type=\"xsd:string\">" + serial_number
				+ "</c-gensym3>");
		sb.append("<c-gensym5 xsi:type=\"xsd:string\">" + trivel_type
				+ "</c-gensym5>");
		sb.append("<c-gensym7 xsi:type=\"xsd:string\">" + arys[0].split(",")[0]
				+ "</c-gensym7>");
		sb.append("<soapenc:Array soapenc:arrayType=\"xsd:anyType["
				+ arys.length + "]\" xsi:type=\"soapenc:Array\">");
		for (int i = 0; i < arys.length; i++) {
			String[] info = arys[i].split(",");
			if (info.length == 6) {
				sb.append("<item>");
				sb.append("<flight_no xsi:type=\"xsd:string\">" + info[1]
						+ "</flight_no>");
				sb.append("<class xsi:type=\"xsd:string\">" + info[2]
						+ "</class>");
				sb.append("<depart_date xsi:type=\"xsd:date\">" + info[3]
						+ "</depart_date>");
				sb.append("<depart_airport xsi:type=\"xsd:string\">" + info[4]
						+ "</depart_airport>");
				sb.append("<arrival_airport xsi:type=\"xsd:string\">" + info[5]
						+ "</arrival_airport>");
				sb.append("</item>");
			}
		}
		sb.append("</soapenc:Array>");
		if (trivel_type.equals("RT")) {
			String[] hary = hinfo.split("@");
			sb.append("<soapenc:Array soapenc:arrayType=\"xsd:anyType["
					+ arys.length + "]\" xsi:type=\"soapenc:Array\">");
			for (int i = 0; i < hary.length; i++) {
				String[] info = arys[i].split(",");
				if (info.length == 5) {
					sb.append("<item>");
					sb.append("<flight_no xsi:type=\"xsd:string\">" + info[0]
							+ "</flight_no>");
					sb.append("<class xsi:type=\"xsd:string\">" + info[1]
							+ "</class>");
					sb.append("<depart_date xsi:type=\"xsd:date\">" + info[2]
							+ "</depart_date>");
					sb.append("<depart_airport xsi:type=\"xsd:string\">"
							+ info[3] + "</depart_airport>");
					sb.append("<arrival_airport xsi:type=\"xsd:string\">"
							+ info[4] + "</arrival_airport>");
					sb.append("</item>");
				}
			}
			sb.append("</soapenc:Array>");
		}
		sb.append("</inter_detect_price>");
		sb.append("</soap:Body>");
		sb.append("</soap:Envelope>");
		return sb.toString();
	}
	/**
	 * 生成PNR
	 */
	public String inter_booking(){
		String pnr="";
		return pnr;
	}
	/**
	 * 查询国际机票信息
	 * @param tripType 旅行类型 1单程 2往返
	 * @param peoNum 人数
	 * @param proType 乘客类型 1成人 2儿童 3婴儿（不占座）
	 * @param cbinType 仓位 F头等舱 C商务舱 Y 经济舱
	 * @param origCity 出发城市
	 * @param destCity 抵达城市
	 * @param srcDate 去程出发日期
	 * @param retDate 返程出发日期
	 * @return
	 */
	public List[] getRouteInfo(String tripType,String peoNum,String proType,String cbinType, String origCity,String destCity,String srcDate ,String retDate){
		if("1".equals(proType)){
			proType = "ADT";
		}else if("2".equals(proType)){
			proType = "CHD";
		}else if("3".equals(proType)){
			proType = "INF";
		}else{
			proType = "ADT";
		}
	//调用的接口的函数名
	String method = "CustomerAirSearch";
	//查询用的xml数据
	String xml = "<CustomerAirSearchRequest><SerialNumber>"+serial_number+"</SerialNumber><AirAvailAry> ";
	xml+="<AirAvail><GenAvail><StartPt>"+origCity+"</StartPt><EndPt>"+destCity+"</EndPt><StartDt>"+srcDate+"</StartDt><Class>"+cbinType+"</Class><NumSeats>"+peoNum+"</NumSeats><TmWndInd/><StartTmWnd/><EndTmWnd/></GenAvail></AirAvail>";		
	if("2".equals(tripType)){
		xml+="<AirAvail><GenAvail><StartPt>"+destCity+"</StartPt><EndPt>"+origCity+"</EndPt><StartDt>"+retDate+"</StartDt><Class>"+cbinType+"</Class><NumSeats>"+peoNum+"</NumSeats><TmWndInd/><StartTmWnd/><EndTmWnd/></GenAvail></AirAvail>";
	}
	xml+="</AirAvailAry><BestSales><PassengerType><PsgrAry> <Psgr> <PTC>"+proType+"</PTC></Psgr></PsgrAry></PassengerType></BestSales></CustomerAirSearchRequest>";		
	
	
	
	//去程的list
	List<InterAri> ariList = new ArrayList<InterAri>();
	//返程的list
	List<InterAri> retAriList = new ArrayList<InterAri>();
	
	try {
		URL url = new URL(interUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");// 提交模式
        conn.setDoOutput(true);// 是否输入参数
        InputStreamReader isr = null;
        StringBuffer params = new StringBuffer();
        // 表单参数与get形式一样
        params.append("method").append("=").append(method).append("&")
              .append("params").append("=").append(xml);
        byte[] bypes = params.toString().getBytes();
        conn.getOutputStream().write(bypes);// 输入参数
        InputStream inStream=conn.getInputStream();
        isr = new InputStreamReader(inStream,"utf-8");//转码
		BufferedReader in = new BufferedReader(isr);
		StringWriter out = new StringWriter();
		int c = -1;
		while ((c = in.read()) != -1) {
			out.write(c);
		}
		
//		Element soapd = Envelope.getChild("Body", soap);
		
		String strReturn = out.toString();
		System.out.println("开始解析");
		org.dom4j.Document document =DocumentHelper.parseText(strReturn);
		org.dom4j.Element root=document.getRootElement();
		org.dom4j.Element book=root.element("body");
		//取得节点
		org.dom4j.Element Customer=book.element("CustomerAirSearchResponse");
		if(Customer==null){
//			System.out.println("查询国际机票信息接口异常，错误信息："+book.getText());
			logger.error("查询国际机票信息接口异常，错误信息："+book.getText());
			return null;
		}
		org.dom4j.Element AirSegs=Customer.element("AirSegs");
		
		 Iterator iterator=AirSegs.elementIterator();
		 int i=0;
		 int index = 0;
		  while(iterator.hasNext()){
		   org.dom4j.Element AirSeg=(org.dom4j.Element)iterator.next();
		   if(AirSeg.getName().equals("AirSeg")){
			   if(i==1){
				   index = 0;
			   }
				   Iterator AirSegItems=AirSeg.elementIterator();
				   //判断上一个航程是否结束 0没有结束 1结束了
				   int isend = 1;
				   InterAri interAri = new InterAri();
				   List<StrokeAri> strokeAriList = new ArrayList<StrokeAri>();
				   StrokeAri strokeAri = null;
				   while(AirSegItems.hasNext()){
					   org.dom4j.Element AirSegItem=(org.dom4j.Element)AirSegItems.next();
					   if(AirSegItem.getName().equals("AirSegItem")){
						   //实例化航程信息
						   strokeAri = new StrokeAri();
						   //Conx  N为直飞,中转则为Y
						   //System.out.println(AirSegItem.element("Conx").getText());
						   if("N".equals(AirSegItem.element("Conx").getText())){
							   isend = 1;
						   }else{
							   isend = 0;
						   }
						   strokeAri.setConx(AirSegItem.element("Conx").getText());
						   //FltTm 飞行时间
						   strokeAri.setFltTm(AirSegItem.element("FltTm").getText());
						 //DayChg 到达日期是 前后几天
						   strokeAri.setDayChg(AirSegItem.element("DayChg").getText());
						 //AirpChg 上一个航班到这一个航班是不是要转换机场（同城，比如SHA到达-PVG出发），第一个Y的航班永远是N，Y只会出现在中转的第二程之后
						   strokeAri.setAirpChg(AirSegItem.element("AirpChg").getText());
						   //FltNum 航班号
						   strokeAri.setFltNum(AirSegItem.element("FltNum").getText());
						 //EndTerminal 到达航站楼
						   strokeAri.setEndTerminal(AirSegItem.element("EndTerminal").getText());
						 //Equip 机型
						   strokeAri.setEquip(AirSegItem.element("Equip").getText());
						   //StartTm 出发时间
						   strokeAri.setStartTm(AirSegItem.element("StartTm").getText());
						   //StartTerminal 出发航站楼
						   strokeAri.setStartTerminal(AirSegItem.element("StartTerminal").getText());
						   //ETktEligibility 是否电子客票
						   strokeAri.setETktEligibility(AirSegItem.element("ETktEligibility").getText());
						   //EndAirp 到达机场3字码
						   strokeAri.setEndAirp(AirSegItem.element("EndAirp").getText());
						   //EndTm 到达时间
						   strokeAri.setEndTm(AirSegItem.element("EndTm").getText());
						   //NumStops 经停次数
						   strokeAri.setNumStops(AirSegItem.element("NumStops").getText());
						   //OpAirVInd 标识是否航空公司跟承运人为同一家
						   strokeAri.setOpAirVInd(AirSegItem.element("OpAirVInd").getText());
						   //StartDt 出发日期
						   strokeAri.setStartDt(AirSegItem.element("StartDt").getText());
						 //AirV //航空公司
						   strokeAri.setAirV(AirSegItem.element("AirV").getText());
						 //OpAirV //实际承运航空公司，为空则为同一家
						   if("".equals(AirSegItem.element("OpAirV").getText())){
							   strokeAri.setOpAirV(AirSegItem.element("AirV").getText());
						   }else{
							   strokeAri.setOpAirV(AirSegItem.element("OpAirV").getText());
						   }
						   //StartAirp 出发机场3字码
						   strokeAri.setStartAirp(AirSegItem.element("StartAirp").getText());
						   //EndDt 到达日期
						   strokeAri.setEndDt(AirSegItem.element("EndDt").getText());
						   strokeAri.setFCRNum(AirSegItem.element("FareItemCrossRef").element("FCRNum").getText());
						   if(AirSegItem.element("FareItemCrossRef").element("FareItemAry").elements().size()!=0){
							   String FINum = "";
							   String FareIndexNum ="";
							   Iterator FareItemAry=AirSegItem.element("FareItemCrossRef").element("FareItemAry").elementIterator();
							   while (FareItemAry.hasNext()) {
								   org.dom4j.Element FareItem=(org.dom4j.Element)FareItemAry.next();
								   if(FareItem.getName()=="FareItem"){
									   FINum+=FareItem.element("FINum").getText()+",";
									   FareIndexNum+=FareItem.element("FareIndexNum").getText()+",";
								   }
							   }
							   strokeAri.setFINum(FINum);
							   strokeAri.setFareIndexNum(FareIndexNum);
						   }
						   strokeAriList.add(strokeAri);
						   if(isend==1){
							   interAri.setStrokeAri(strokeAriList);
							   if(i==0){
								   //去程
								   ariList.add(interAri);
							   }else if(i==1){
								   //回程
								   retAriList.add(interAri);
							   }
							   interAri = new InterAri();
							   strokeAriList = new ArrayList<StrokeAri>();
						   }
						   strokeAri.setIndex(index+"");
						   index++;
					   }
				   }
		   }
		   i++;
		  }
		  List<InterAri> list = null;
		  org.dom4j.Element Fares=Customer.element("Fares");
		  for(int num = 0;num<2;num++){
			  
			  if(num==0){
				  list = ariList;
			  }else{
				  list = retAriList;
			  }
			  if(list!=null && list.size()>0)
			  for(int m=0;m<list.size();m++){
				  InterAri interAri = list.get(m);
				  Iterator faresIte=Fares.elementIterator();
				  List<StrokeAri> strokeAriList= interAri.getStrokeAri();
				  StrokeAri strokeAri = null;
				  int arrayNum = 0;
				  if(strokeAriList!=null && strokeAriList.size()>0){
					  String indexs = "";
					  while(faresIte.hasNext()){
						  org.dom4j.Element Fare=(org.dom4j.Element)faresIte.next();
						  int listsize = strokeAriList.size();
						  int interAriStats = 0;
						  for(int n = 0;n<listsize;n++){
							  strokeAri = strokeAriList.get(n);
							  if(strokeAri.getFINum()==null || "".equals(strokeAri.getFINum())){
								 break; 
							  }else{
							  if(strokeAri!=null && Fare.getName().equals("Fare")){
								  String[] FINums =  strokeAri.getFINum().split(",");
								  for(int x = 0;x<FINums.length;x++){
									int up = 0;
									  if((arrayNum+"").equals(FINums[x])){
										  if(interAri.getEquivFareAmt()==null){
											  //票价被删除小数点的位数
											  interAri.setEquivDecPos(Fare.element("GenQuoteDetails").element("EquivDecPos").getText());
											  //目标货币
											  interAri.setEquivCurrency(Fare.element("GenQuoteDetails").element("EquivCurrency").getText());
											  ////目标币种票价总额
											  interAri.setEquivFareAmt(Fare.element("GenQuoteDetails").element("EquivFareAmt").getText());
											  //目标货币税 -1为无效
											  interAri.setEquivTaxAmt(Fare.element("GenQuoteDetails").element("EquivTaxAmt").getText());
											  //原始货币的小数位数(被删除的)
											  interAri.setBaseDecPos(Fare.element("GenQuoteDetails").element("BaseDecPos").getText());
											  //原始货币类型（跟接口申请类型）
											  interAri.setBaseCurrency(Fare.element("GenQuoteDetails").element("BaseCurrency").getText());
											  //原始货币票价总额
											  interAri.setBaseFareAmt(Fare.element("GenQuoteDetails").element("BaseFareAmt").getText());
											  //原始货币税 -1为无效
											  interAri.setBaseTaxAmt(Fare.element("GenQuoteDetails").element("BaseTaxAmt").getText());
											  //interAriStats++;
											  up=1;
										  }else{
											  //为了获得最小的票面价
											  //
											  String price = Fare.element("GenQuoteDetails").element("EquivFareAmt").getText();
											  String price2 = interAri.getEquivFareAmt();
											  if("0".equals(price2)||(!"0".equals(price) && Integer.parseInt(price)<Integer.parseInt(price2))){
												//票价被删除小数点的位数
												  interAri.setEquivDecPos(Fare.element("GenQuoteDetails").element("EquivDecPos").getText());
												  //目标货币
												  interAri.setEquivCurrency(Fare.element("GenQuoteDetails").element("EquivCurrency").getText());
												  ////目标币种票价总额
												  interAri.setEquivFareAmt(Fare.element("GenQuoteDetails").element("EquivFareAmt").getText());
												  //目标货币税 -1为无效
												  interAri.setEquivTaxAmt(Fare.element("GenQuoteDetails").element("EquivTaxAmt").getText());
												  //原始货币的小数位数(被删除的)
												  interAri.setBaseDecPos(Fare.element("GenQuoteDetails").element("BaseDecPos").getText());
												  //原始货币类型（跟接口申请类型）
												  interAri.setBaseCurrency(Fare.element("GenQuoteDetails").element("BaseCurrency").getText());
												  //原始货币票价总额
												  interAri.setBaseFareAmt(Fare.element("GenQuoteDetails").element("BaseFareAmt").getText());
												  //原始货币税 -1为无效
												  interAri.setBaseTaxAmt(Fare.element("GenQuoteDetails").element("BaseTaxAmt").getText());
												  //interAriStats++;
												  up=1;
											  }
										  }
										  //设置航程信息里面的仓位及剩余票数
										  //航班数组index与下面的仓位信息匹配
//										  String index2 = FareIndexNums[x];
										  String index2 = strokeAri.getIndex();
										  if(num==0){
											  if(up==1){
												//合同id
												  interAri.setCntrId(Fare.element("CntrDetails").element("CntrItem").element("CntrId").getText());
												  ////合同类型 G： M:纸质合同
												  interAri.setCntrType(Fare.element("CntrDetails").element("CntrItem").element("CntrType").getText());
												  //开票航空公司，要是多程不一样，以第一个为准
												  interAri.setCntrAirV(Fare.element("CntrDetails").element("CntrItem").element("CntrAirV").getText());
												  //停留日期说明1
												  interAri.setMinStayDetail(Fare.element("CntrDetails").element("CntrItem").element("MinStayDetail").getText());
												//停留日期说明2
												  interAri.setMaxStayDetail(Fare.element("CntrDetails").element("CntrItem").element("MaxStayDetail").getText());
												//停留说明
												  interAri.setStayDetail(Fare.element("CntrDetails").element("CntrItem").element("StayDetail").getText());
												//提前预售天数
												  interAri.setAdvRsvnTktDetail(Fare.element("CntrDetails").element("CntrItem").element("AdvRsvnTktDetail").getText());
												//合同的url地址 如果为空：N/A
												  interAri.setCntrUrl(Fare.element("CntrDetails").element("CntrItem").element("CntrUrl").getText());
												//GV:1-9 中的9
												  interAri.setMaxGv(Fare.element("CntrDetails").element("CntrItem").element("MaxGv").getText());
												//GV:1-9 中的1
												  interAri.setMinGv(Fare.element("CntrDetails").element("CntrItem").element("MinGv").getText());
												//销售日期：1970-01-01  -  9999-12-31中的1970-01-01部分
												  interAri.setSaleFromDt(Fare.element("CntrDetails").element("CntrItem").element("SaleFromDt").getText());
												//销售日期：1970-01-01  -  9999-12-31中的9999-12-31部分
												  interAri.setSaleToDt(Fare.element("CntrDetails").element("CntrItem").element("SaleToDt").getText());
												//儿童细则(儿童票价详情)
												  interAri.setChildFareDetail(Fare.element("CntrDetails").element("CntrItem").element("ChildFareDetail").getText());
												//退票费细则
												  interAri.setRefDetail(Fare.element("CntrDetails").element("CntrItem").element("RefDetail").getText());
												//改期细则
												  interAri.setRebDetail(Fare.element("CntrDetails").element("CntrItem").element("RebDetail").getText());
												//改签规则
												  interAri.setRerDetail(Fare.element("CntrDetails").element("CntrItem").element("RerDetail").getText());
												//NO_SHOW：N/A
												  interAri.setNoShow(Fare.element("CntrDetails").element("CntrItem").element("NoShow").getText());
												//备注
												  interAri.setRemark(Fare.element("CntrDetails").element("CntrItem").element("Remark").getText());
											  }
											  Iterator FlightItemCrossRefAryItem = Fare.element("FlightItemCrossRefAry").elementIterator();
											  while(FlightItemCrossRefAryItem.hasNext()){
												  org.dom4j.Element FlightItemCrossRef=(org.dom4j.Element)FlightItemCrossRefAryItem.next();
												  
												  if("FlightItemCrossRef".equals(FlightItemCrossRef.getName())){
													  if("0".equals(FlightItemCrossRef.element("ODNum").getText())){
														  org.dom4j.Element FltItemAry = FlightItemCrossRef.element("FltItemAry");
														  Iterator FltItemAryItem=FltItemAry.elementIterator();
														  while(FltItemAryItem.hasNext()){
															  org.dom4j.Element FltItem=(org.dom4j.Element)FltItemAryItem.next();
															  if("FltItem".equals(FltItem.getName())){
																  if(index2.equals(FltItem.element("FltIndexNum").getText())){
																	  strokeAri.setBIC(FltItem.element("BIC").getText());
																	  strokeAri.setStatus(FltItem.element("Status").getText());
																	  strokeAriList.set(n, strokeAri);
																	  break;
																  }
															  }
														  }
													  }
												  }
												  if("2".equals(tripType)){
													  
													  if("1".equals(FlightItemCrossRef.element("ODNum").getText())){
														  org.dom4j.Element FltItemAry = FlightItemCrossRef.element("FltItemAry");
														  Iterator FltItemAryItem=FltItemAry.elementIterator();
														  while(FltItemAryItem.hasNext()){
															  org.dom4j.Element FltItem=(org.dom4j.Element)FltItemAryItem.next();
															  if("FltItem".equals(FltItem.getName())){
																  String FltIndexNum =FltItem.element("FltIndexNum").getText();
																  if("".equals(indexs)){
																	  indexs+=FltIndexNum+",";
																  }else{
																	  String[] str = indexs.split(",");
																	  int temp = 0;
																	  for(String s:str){
																		  if(s.equals(FltIndexNum)){
																			temp=1; 
																			break;
																		  }
																	  }
																	  if(temp==0){
																		  indexs+=FltIndexNum+",";
																	  }
																  }
															  }
														  }
													  }
												  }
											  }
										  }else{
											  //设置返程的退改签极限至
											  int testnum =0;
											  Iterator CntrDetailsItem = Fare.element("CntrDetails").elementIterator();
											  while(CntrDetailsItem.hasNext()){
												  org.dom4j.Element CntrItem=(org.dom4j.Element)CntrDetailsItem.next();
												  if("CntrItem".equals(CntrItem.getName())){
													  if(testnum==1 && up==1){
														//合同id
														  interAri.setCntrId(CntrItem.element("CntrId").getText());
														  ////合同类型 G： M:纸质合同
														  interAri.setCntrType(CntrItem.element("CntrType").getText());
														  //开票航空公司，要是多程不一样，以第一个为准
														  interAri.setCntrAirV(CntrItem.element("CntrAirV").getText());
														  //停留日期说明1
														  interAri.setMinStayDetail(CntrItem.element("MinStayDetail").getText());
														//停留日期说明2
														  interAri.setMaxStayDetail(CntrItem.element("MaxStayDetail").getText());
														//停留说明
														  interAri.setStayDetail(CntrItem.element("StayDetail").getText());
														//提前预售天数
														  interAri.setAdvRsvnTktDetail(CntrItem.element("AdvRsvnTktDetail").getText());
														//合同的url地址 如果为空：N/A
														  interAri.setCntrUrl(CntrItem.element("CntrUrl").getText());
														//GV:1-9 中的9
														  interAri.setMaxGv(CntrItem.element("MaxGv").getText());
														//GV:1-9 中的1
														  interAri.setMinGv(CntrItem.element("MinGv").getText());
														//销售日期：1970-01-01  -  9999-12-31中的1970-01-01部分
														  interAri.setSaleFromDt(CntrItem.element("SaleFromDt").getText());
														//销售日期：1970-01-01  -  9999-12-31中的9999-12-31部分
														  interAri.setSaleToDt(CntrItem.element("SaleToDt").getText());
														//儿童细则(儿童票价详情)
														  interAri.setChildFareDetail(CntrItem.element("ChildFareDetail").getText());
														//退票费细则
														  interAri.setRefDetail(CntrItem.element("RefDetail").getText());
														//改期细则
														  interAri.setRebDetail(CntrItem.element("RebDetail").getText());
														//改签规则
														  interAri.setRerDetail(CntrItem.element("RerDetail").getText());
														//NO_SHOW：N/A
														  interAri.setNoShow(CntrItem.element("NoShow").getText());
														//备注
														  interAri.setRemark(CntrItem.element("Remark").getText());
														  
													  }
												  }
												  testnum++;
											  }
											  //设置返程的仓位信息
											  Iterator FlightItemCrossRefAryItem = Fare.element("FlightItemCrossRefAry").elementIterator();
											  while(FlightItemCrossRefAryItem.hasNext()){
												  org.dom4j.Element FlightItemCrossRef=(org.dom4j.Element)FlightItemCrossRefAryItem.next();
												  if("FlightItemCrossRef".equals(FlightItemCrossRef.getName())){
													  if("1".equals(FlightItemCrossRef.element("ODNum").getText())){
														  org.dom4j.Element FltItemAry = FlightItemCrossRef.element("FltItemAry");
														  Iterator FltItemAryItem=FltItemAry.elementIterator();
														  while(FltItemAryItem.hasNext()){
															  org.dom4j.Element FltItem=(org.dom4j.Element)FltItemAryItem.next();
															  if("FltItem".equals(FltItem.getName())){
																  if(index2.equals(FltItem.element("FltIndexNum").getText())){
																	  strokeAri.setBIC(FltItem.element("BIC").getText());
																	  strokeAri.setStatus(FltItem.element("Status").getText());
																	  strokeAriList.set(n, strokeAri);
																	  break;
																  }
															  }
														  }
													  }
												  }
											  }
											  
											  
										  }
										  
										  interAriStats++;
									  }
								  }
								  
							  }
						  }
							  
						  }
						  arrayNum++;
					  }
					  interAri.setStrokeAri(strokeAriList);
					  interAri.setFltIndexNums(indexs);
				  }
				  list.set(m, interAri);
				  if(num==0){
					  ariList=list;
				  }else{
					  retAriList=list;
				  }
			  }
		  }
		  if(ariList!=null){
			  for(int num=0;num<ariList.size();num++){
				  if(ariList.get(num).getEquivFareAmt()==null ||"".equals(ariList.get(num).getEquivFareAmt())){
					  ariList.remove(num);
					  num--;
					  continue;
				  }
				  
				  if("2".equals(tripType)){
					if(ariList.get(num).getFltIndexNums()==null||"".equals(ariList.get(num).getFltIndexNums())){
						  ariList.remove(num);
						  num--;
					  }
				  }
			  }
			  
			  System.out.println("去程航班个数"+ariList.size());
		  }
		  if(retAriList!=null){
			  for(int num=0;num<retAriList.size();num++){
				  if(retAriList.get(num).getEquivFareAmt()==null ||"".equals(retAriList.get(num).getEquivFareAmt())){
					  retAriList.remove(num);
					  num--;
				  }else{
					  if(retAriList.get(num).getStrokeAri()!=null &&retAriList.get(num).getStrokeAri().get(0)!=null&& retAriList.get(num).getStrokeAri().get(0).getBIC()==null){
						  retAriList.remove(num);
						  num--;
					  }
				  }
				  
			  }
			  
			  System.out.println("返程航班个数"+retAriList.size());
		  }
		
		
		
		
	} catch (Exception ex) {
		ex.printStackTrace();
		// TODO Auto-generated catch block
//		logger.error("查询国际机票信息接口异常，错误信息："+book.getText());
//		System.out.println("查询国际机票信息接口异常，错误信息：" + ex.getMessage());
		logger.error("查询国际机票信息接口异常，错误信息：",  ex.fillInStackTrace());
		return null;
	}
	
	List[] retlist = {ariList,retAriList};
	return retlist;
	
	}
	
	/**
	 * 查询税费 接口版本（air86 v2.0）
	 * @param tripType 是否有多个行程 1 单个行程 2 2个行程
	 * @param peoNum 人数  (返回数据都是成人的,可能以后有变化)
	 * @param proType 乘客类型 0成人 1儿童 2婴儿（不占座）(返回数据都是成人的,可能以后有变化)
	 * @param str1 第一程 数据字符串 数据格式是逗号分隔 数据顺序：航空公司代码，航班号，仓位信息(A,B,C,D..)，出发日期，起飞机场代码，抵达机场代码，出发时间(8000)，抵达时间
	 * @param str2 第二程 数据字符串 同上 
	 * @return
	 */
	public String getTaxQuery(String tripType,String peoNum,String proType,String str1,String str2){
		String taxes = "";
		if("0".equals(proType)){
			proType = "ADT";
		}else if("1".equals(proType)){
			proType = "CHD";
		}else if("2".equals(proType)){
			proType = "INF";
		}else{
			proType = "ADT";
		}
		String xml = "<TaxQueryRequest><SerialNumber>"+serial_number+"</SerialNumber><CntrAirV>FM</CntrAirV><PsgrType>"+proType+"</PsgrType><AirSegSells>" ;
		String[] str = str1.split(",");
		String dtchg = "0";
		if(Integer.parseInt(str[6])>Integer.parseInt(str[7])){
			dtchg = "1";
		}else{
			dtchg = "0";
		}
		xml+="<AirSegSell><AirSegSellItem><AirV>"+str[0]+"</AirV><FltNum>"+str[1]+"</FltNum><OpSuf/><Class>"+str[2]+"</Class><StartDt>"+str[3]+"</StartDt><StartAirp>"+str[4]+"</StartAirp>"
		+"<EndAirp>"+str[5]+"</EndAirp><StartTm>"+str[6]+"</StartTm><EndTm>"+str[7]+"</EndTm><DtChg>"+dtchg+"</DtChg><Status/><NumPsgrs>"+peoNum+"</NumPsgrs></AirSegSellItem></AirSegSell>";
		if("2".equals(tripType) && str2!=null &&!"".equals(str2)){
			str = null;
			str = str2.split(",");
			if(Integer.parseInt(str[6])>Integer.parseInt(str[7])){
				dtchg = "1";
			}else{
				dtchg = "0";
			}
			xml+="<AirSegSell><AirSegSellItem><AirV>"+str[0]+"</AirV><FltNum>"+str[1]+"</FltNum><OpSuf/><Class>"+str[2]+"</Class><StartDt>"+str[3]+"</StartDt><StartAirp>"+str[4]+"</StartAirp>"
			+"<EndAirp>"+str[5]+"</EndAirp><StartTm>"+str[6]+"</StartTm><EndTm>"+str[7]+"</EndTm><DtChg>"+dtchg+"</DtChg><Status/><NumPsgrs>"+peoNum+"</NumPsgrs></AirSegSellItem></AirSegSell>";
		}
		xml+="</AirSegSells></TaxQueryRequest>";
		String method = "TaxQuery";
		
		HttpURLConnection conn;
		try {
			URL url = new URL(interUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");// 提交模式
	        conn.setDoOutput(true);// 是否输入参数
	        InputStreamReader isr = null;
	        StringBuffer params = new StringBuffer();
	        // 表单参数与get形式一样
	        params.append("method").append("=").append(method).append("&")
	              .append("params").append("=").append(xml);
	        byte[] bypes = params.toString().getBytes();
	        conn.getOutputStream().write(bypes);// 输入参数
	        InputStream inStream=conn.getInputStream();
	        isr = new InputStreamReader(inStream,"utf-8");//转码
			BufferedReader in = new BufferedReader(isr);
			StringWriter out = new StringWriter();
			int c = -1;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			System.out.println(out.toString());
			String strReturn = out.toString();
			
			org.dom4j.Document document =DocumentHelper.parseText(strReturn);
			org.dom4j.Element root=document.getRootElement();
			org.dom4j.Element book=root.element("body");
			//取得节点
			org.dom4j.Element TaxQueryResponse=book.element("TaxQueryResponse");
			org.dom4j.Element GenQuoteDetails=TaxQueryResponse.element("GenQuoteDetails");
			org.dom4j.Element EquivTaxAmt=GenQuoteDetails.element("EquivTaxAmt");
			taxes = EquivTaxAmt.getText();
			System.out.println("税费是："+EquivTaxAmt.getText());
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			System.out.println("接口异常:" + e.getMessage());
			logger.error("查询国际税费接口异常，错误信息：",  e.fillInStackTrace());
			return "";
		}
        
		return taxes;
	}
	/**
	 * 实时获取机票真实价格
	 * @param tripType 单程 1 往返 2
	 * @param proNum 人数  (返回数据都是成人的,可能以后有变化)
	 * @param proType 乘客类型 1成人 2儿童 3婴儿（不占座）(返回数据都是成人的,可能以后有变化)
	 * @param str1 第一程 数据字符串(如出现中转用@再拼接一个一样的字符串) 数据格式是逗号分隔 数据顺序：航空公司代码，航班号，仓位信息(A,B,C,D..)，出发日期，起飞机场代码，抵达机场代码，出发时间(8000)，抵达时间
	 * @param str2 第二程 数据字符串 同上 
	 * @return
	 */
	public String getFareVerify(String tripType,String peoNum,String proType,String str1,String str2){
		String price = "";
		if("1".equals(proType)){
			proType = "ADT";
		}else if("2".equals(proType)){
			proType = "CHD";
		}else if("3".equals(proType)){
			proType = "INF";
		}else{
			proType = "ADT";
		}
		String[] temp =  str1.split("@");
		int size = temp.length;
		String[] str = temp[0].split(",");
		String dtchg = "0";
		if(Integer.parseInt(str[6])>Integer.parseInt(str[7])){
			dtchg = "1";
		}else{
			dtchg = "0";
		}
		String method = "FareVerify";
		String xml = "<FareVerifyRequest><SerialNumber>"+serial_number+"</SerialNumber><CntrAirV>"+str[0]+"</CntrAirV><PsgrType>"+proType+"</PsgrType><AirSegSells>";
		xml+="<AirSegSell>" +
		"<AirSegSellItem><AirV>"+str[0]+"</AirV><FltNum>"+str[1]+"</FltNum><OpSuf/><Class>"+str[2]+"</Class>"
		+"<StartDt>"+str[3]+"</StartDt><StartAirp>"+str[4]+"</StartAirp><EndAirp>"+str[5]+"</EndAirp><StartTm>"+str[6]+"</StartTm>"
		+"<EndTm>"+str[7]+"</EndTm><DtChg>"+dtchg+"</DtChg><Status/><NumPsgrs>"+peoNum+"</NumPsgrs></AirSegSellItem>";
		if(size>1){
			str = temp[1].split(",");
			dtchg = "0";
			if(Integer.parseInt(str[6])>Integer.parseInt(str[7])){
				dtchg = "1";
			}else{
				dtchg = "0";
			}
			xml+="<AirSegSellItem><AirV>"+str[0]+"</AirV><FltNum>"+str[1]+"</FltNum><OpSuf/><Class>"+str[2]+"</Class>"
			+"<StartDt>"+str[3]+"</StartDt><StartAirp>"+str[4]+"</StartAirp><EndAirp>"+str[5]+"</EndAirp><StartTm>"+str[6]+"</StartTm>"
			+"<EndTm>"+str[7]+"</EndTm><DtChg>"+dtchg+"</DtChg><Status/><NumPsgrs>"+peoNum+"</NumPsgrs></AirSegSellItem>";
		}
		xml+="</AirSegSell>";
		if("2".equals(tripType) && str2!=null &&!"".equals(str2)){
			str = null;
			temp = str2.split("@");
			size = temp.length;
			str = temp[0].split(",");
			if(Integer.parseInt(str[6])>Integer.parseInt(str[7])){
				dtchg = "1";
			}else{
				dtchg = "0";
			}
			xml+="<AirSegSell>" +
			"<AirSegSellItem><AirV>"+str[0]+"</AirV><FltNum>"+str[1]+"</FltNum><OpSuf/><Class>"+str[2]+"</Class>"
			+"<StartDt>"+str[3]+"</StartDt><StartAirp>"+str[4]+"</StartAirp><EndAirp>"+str[5]+"</EndAirp><StartTm>"+str[6]+"</StartTm>"
			+"<EndTm>"+str[7]+"</EndTm><DtChg>"+dtchg+"</DtChg><Status/><NumPsgrs>"+peoNum+"</NumPsgrs></AirSegSellItem>";
			if(size>1){
				str = temp[1].split(",");
				dtchg = "0";
				if(Integer.parseInt(str[6])>Integer.parseInt(str[7])){
					dtchg = "1";
				}else{
					dtchg = "0";
				}
				xml+="<AirSegSellItem><AirV>"+str[0]+"</AirV><FltNum>"+str[1]+"</FltNum><OpSuf/><Class>"+str[2]+"</Class>"
				+"<StartDt>"+str[3]+"</StartDt><StartAirp>"+str[4]+"</StartAirp><EndAirp>"+str[5]+"</EndAirp><StartTm>"+str[6]+"</StartTm>"
				+"<EndTm>"+str[7]+"</EndTm><DtChg>"+dtchg+"</DtChg><Status/><NumPsgrs>"+peoNum+"</NumPsgrs></AirSegSellItem>";
			}
			
			xml+="</AirSegSell>";
		}
		

		xml+="</AirSegSells></FareVerifyRequest>";
		System.out.println("xml:"+xml);
		try {
			URL url = new URL(interUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setRequestMethod("POST");// 提交模式
	        conn.setDoOutput(true);// 是否输入参数
	        InputStreamReader isr = null;
	        StringBuffer params = new StringBuffer();
	        // 表单参数与get形式一样
	        params.append("method").append("=").append(method).append("&")
	              .append("params").append("=").append(xml);
	        byte[] bypes = params.toString().getBytes();
	        conn.getOutputStream().write(bypes);// 输入参数
	        InputStream inStream=conn.getInputStream();
	        isr = new InputStreamReader(inStream,"utf-8");//转码
			BufferedReader in = new BufferedReader(isr);
			StringWriter out = new StringWriter();
			int c = -1;
			while ((c = in.read()) != -1) {
				out.write(c);
			}
			System.out.println(out.toString());
			String strReturn = out.toString();
			
			org.dom4j.Document document =DocumentHelper.parseText(strReturn);
			org.dom4j.Element root=document.getRootElement();
			org.dom4j.Element book=root.element("body");
			//取得节点
			org.dom4j.Element FareVerifyResponse=book.element("FareVerifyResponse");
			org.dom4j.Element GenQuoteDetails=FareVerifyResponse.element("GenQuoteDetails");
			org.dom4j.Element EquivFareAmt=GenQuoteDetails.element("EquivFareAmt");
			org.dom4j.Element EquivTaxAmt=GenQuoteDetails.element("EquivTaxAmt");
			System.out.println("税费是："+EquivTaxAmt.getText());
			System.out.println("票价是："+EquivFareAmt.getText());
			price = EquivFareAmt.getText()+","+EquivTaxAmt.getText();
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			System.out.println("接口异常:" + e.getMessage());
			logger.error("国际实时获取接口异常，错误信息：",  e.fillInStackTrace());
			return "";
		}
		return price;
	}
}
