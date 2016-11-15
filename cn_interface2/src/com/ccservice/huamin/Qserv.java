package com.ccservice.huamin;

import java.io.StringReader;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelserv.hotelserv;

/**
 * 查询服务包--接口十二
 * 
 * @author wzc-ccs
 * 
 */
public class Qserv {

	public static void main(String[] args) {
		getQserv();
	}

	public static void getQserv() {
		List hotelcontracts = Server.getInstance().getSystemService().findMapResultBySql(
				" SELECT DISTINCT C_HOTELID,C_HMCONTRACTID FROM C_HOTELCONTRACT ", null);
		String hmcontract = "";
		String hotelid = "";
		for (int i = 0; i < hotelcontracts.size(); i++) {
			Map map = (Map) hotelcontracts.get(i);
			hmcontract = (map.get("C_HMCONTRACTID").toString());
			hotelid = (map.get("C_HOTELID").toString());
			//String totalurl = "http://123.196.113.28:8034/cn_interface/huaminHotel.jsp?test=qserv&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_contract="
			String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qserv&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_contract="
					+ hmcontract + "&p_serv=&p_country=&p_city=";
			String str = Util.getStr(totalurl);
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
				// String hmcontractid = contracts.getChildText("CONTRACT");
				String hmcontractver = contracts.getChildText("VER");
				// String hmholtelid = contracts.getChildText("HOTEL");
				// String hmhotelname = contracts.getChildText("HOTELNAME");
				String cur = contracts.getChildText("CUR");
				List<Element> services = contracts.getChildren("SERVICE");
				for (Element service : services) {
					String serverid = service.getChildText("SERV");
					List<Element> items = service.getChildren("ITEM");
					for (Element item : items) {
						String servercode = item.getChildText("SERVCODE");
						String servername = item.getChildText("SERVNAME");
						String price = item.getChildText("PRICE");
						String min = item.getChildText("MIN");
						String max = item.getChildText("MAX");
						hotelserv hotelserv = new hotelserv();
						hotelserv.setHotelid(Long.parseLong(hotelid));
						hotelserv.setContractid(contractid);
						hotelserv.setCur(cur);
						hotelserv.setServ(serverid);
						hotelserv.setServcode(servercode);
						hotelserv.setServname(servername);
						hotelserv.setPrice(price);
						hotelserv.setContractver(hmcontractver);
						try {
							Long maxcount = Long.parseLong(max.trim());
							Long mincount = Long.parseLong(min.trim());
							hotelserv.setMaxcount(maxcount);
							hotelserv.setMincount(mincount);
						} catch (Exception e) {
							WriteLog.write("酒店服务包新增数据", "最大数量或最小数量转换异常：酒店合同id：" + contractid + "：max" + max + ":min" + min);
						}
						System.out.println("添加服务:" + servername);
						List<hotelserv> hotelservfromtable = Server.getInstance().getHotelService().findAllhotelserv(
								"WHERE " + hotelserv.COL_hotelid + "='" + hotelserv.getHotelid() + "' AND " + hotelserv.COL_contractid + "='"
										+ hotelserv.getContractid()+ "'", "", -1, 0);
						if (hotelservfromtable.size() > 0) {
							hotelserv.setId(hotelservfromtable.get(0).getId());
							Server.getInstance().getHotelService().updatehotelservIgnoreNull(hotelserv);
						} else {
							Server.getInstance().getHotelService().createhotelserv(hotelserv);
						}
					}
				}
			} else {
				WriteLog.write("酒店服务包新增数据", "酒店合同id：" + contractid + "：酒店产品没数据");
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("酒店服务包新增数据", "错误信息:" + resultco + "," + errormessage);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
