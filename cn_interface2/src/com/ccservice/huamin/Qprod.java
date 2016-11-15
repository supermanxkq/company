package com.ccservice.huamin;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelproduct.hotelproduct;

/**
 * 查詢產品代碼--接口十一
 * 
 * @author wzc-ccs
 * 
 */
public class Qprod {
	public static void main(String[] args) {
		getProd();
	}

	public static void getProd() {
		List hotelcontracts = Server.getInstance().getSystemService().findMapResultBySql(
				" SELECT DISTINCT C_HOTELID,C_HMCONTRACTID FROM C_HOTELCONTRACT ", null);
		String hmcontract = "";
		String hotelid = "";
		for (int i = 0; i < hotelcontracts.size(); i++) {
			Map map = (Map) hotelcontracts.get(i);
			hmcontract = (map.get("C_HMCONTRACTID").toString());
			hotelid = (map.get("C_HOTELID").toString());
			//String totalurl = "http://123.196.113.28:8034/cn_interface/huaminHotel.jsp?test=qprod&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_contract="
			String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qprod&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_contract="	
					+ hmcontract + "&p_prod=&p_country=&p_city=";
			String str = Util.getStr(totalurl);
			// System.out.println(str);
			parsexml(hotelid, hmcontract, str);
		}
	}

	public static void parsexml(String hotelid, String contractid, String xml) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xml));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			if (result.getChildren().size() > 2) {
				Element contracts = result.getChild("CONTRACTS");
				// String contid = contracts.getChildText("CONTRACT");
				String ver = contracts.getChildText("VER");
				// String hmhotelid = contracts.getChildText("HOTEL");
				// String hotelname = contracts.getChildText("HOTELNAME");
				List<Element> products = contracts.getChildren("PRODUCT");
				for (Element product : products) {
					String prod = product.getChildText("PROD");
					String ratetype = product.getChildText("RATETYPE");
					String nation = product.getChildText("NATION");
					String nationname = product.getChildText("NATIONNAME");
					String min = product.getChildText("MIN");
					String max = product.getChildText("MAX");
					String advance = product.getChildText("ADVANCE");
					String ticket = product.getChildText("TICKET");
					String prombuy = product.getChildText("PROMBUY");
					String promget = product.getChildText("PROMGET");
					String promdisc = product.getChildText("PROMDISC");
					String promqty = product.getChildText("PROMQTY");
					String benefit = product.getChildText("BENEFIT");
					String benefitdesc = product.getChildText("BENEFITDESC");
					String cancel = product.getChildText("CANCEL");
					String calceldesc = product.getChildText("CANCELDESC");

					hotelproduct hotelproduct = new hotelproduct();
					hotelproduct.setHmcontractid(contractid);
					hotelproduct.setHmcontractver(ver);
					hotelproduct.setHotelid(Long.parseLong(hotelid.trim()));
					hotelproduct.setProd(prod);
					hotelproduct.setRatetype(ratetype);
					hotelproduct.setNationid(168l);// 暂时先入库国内数据
					hotelproduct.setNationname(nationname);
					try {
						hotelproduct.setMinday(Long.parseLong(min.trim()));
						hotelproduct.setMaxday(Long.parseLong(max.trim()));
						hotelproduct.setAdvance(Long.parseLong(advance.trim()));
						hotelproduct.setPrombuy(Long.parseLong(prombuy.trim()));
						hotelproduct.setBenefitid(benefit.trim());
						hotelproduct.setPromqty(Long.parseLong(promqty.trim()));
						hotelproduct.setTicket(Long.parseLong(ticket.trim()));
						hotelproduct.setPromget(Long.parseLong(promget.trim()));
					} catch (Exception e) {
						WriteLog.write("酒店产品数据", "转型失败：min" + min + "：合同id：" + contractid + ":max：" + max + ":advance" + advance + ":prombuy："
								+ prombuy + ":benefit：" + benefit + ":promqty" + promqty + ":ticket" + ticket + ":" + promget);
					}
					hotelproduct.setPromdisc(promdisc);
					hotelproduct.setBenifitdesc(benefitdesc);
					hotelproduct.setCancel(cancel.trim());
					hotelproduct.setCanceldesc(calceldesc);
					List<hotelproduct> hotelproductfromtable = Server.getInstance().getHotelService().findAllhotelproduct(
							"WHERE " + hotelproduct.COL_hotelid + "='" + hotelproduct.getHotelid() + "' AND " + hotelproduct.COL_contractid + "='"
									+ hotelproduct.getHmcontractid() + "'", "", -1, 0);
					if (hotelproductfromtable.size() > 0) {
						hotelproduct.setId(hotelproductfromtable.get(0).getId());
						Server.getInstance().getHotelService().updatehotelproductIgnoreNull(hotelproduct);
					} else {
						Server.getInstance().getHotelService().createhotelproduct(hotelproduct);
					}
				}
			} else {
				WriteLog.write("酒店产品prod新增数据", "酒店合同id：" + contractid + "：酒店产品没数据");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
