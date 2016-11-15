package com.ccservice.huamin;

import java.io.StringReader;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelcancel.hotelcancel;
/**
 * 查询取消陈述--接口十四
 * @author 
 * 2012-6-13上午11:30:53
 */
public class Qcancel {
	public static void main(String[] args) {
		getcancel();
	}

	public static void getcancel() {
		List hotelcontracts = Server.getInstance().getSystemService().findMapResultBySql(
				" SELECT DISTINCT C_HOTELID,C_HMCONTRACTID FROM C_HOTELCONTRACT ", null);
		String hmcontract = "";
		String hotelid = "";
		for (int i = 0; i < hotelcontracts.size(); i++) {
			Map map = (Map) hotelcontracts.get(i);
			hmcontract = (map.get("C_HMCONTRACTID").toString());
			hotelid = (map.get("C_HOTELID").toString());
			String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qcancel&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_contract="	
					+ hmcontract + "&p_cancel=&p_country=&p_city=";
			String str = Util.getStr(totalurl);
			parsexml(hotelid, hmcontract, str);
		}
	}

	public static void parsexml(String hotelid,String hmcontract, String xmlstr) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xmlstr));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			if (result.getChildren().size() > 2) {
				Element contracts = result.getChild("CONTRACTS");
				//String hmcontractid = contracts.getChildText("CONTRACT");
				String hmcontractver = contracts.getChildText("VER");
				//String hmholtelid = contracts.getChildText("HOTEL");
				//String hmhotelname = contracts.getChildText("HOTELNAME");
				List<Element> cancellation = contracts
						.getChildren("CANCELLATION");
				for (Element cancel : cancellation) {
					hotelcancel hotelcancel= new hotelcancel();
					String calcelcode = cancel.getChildText("CANCEL");
					String canceldesc = cancel.getChildText("CANCELDESC");
					hotelcancel.setConractid(hmcontract);
					hotelcancel.setConractver(hmcontractver);
					if (hotelid .equals("0")) {
						WriteLog.write("酒店取消陈述数据", "没有找到对应的酒店id，华敏酒店id代码："
								+ hotelid);
					}
					hotelcancel.setHotelid(Long.parseLong(hotelid));
					hotelcancel.setCancelid(calcelcode);
					hotelcancel.setCanceldesc(canceldesc);
					List<hotelcancel> hotelcancelfromtable = Server.getInstance().getHotelService().findAllhotelcancel(
							"WHERE " + hotelcancel.COL_hotelid + "='" + hotelcancel.getHotelid() + "' AND " + hotelcancel.COL_conractid + "='"
									+ hotelcancel.getConractid() + "' AND "+hotelcancel.COL_cancelid+"='"+hotelcancel.getCancelid()+"'", "", -1, 0);
					if (hotelcancelfromtable.size() > 0) {
						hotelcancel.setId(hotelcancelfromtable.get(0).getId());
						Server.getInstance().getHotelService().updatehotelcancelIgnoreNull(hotelcancel);
					} else {
						Server.getInstance().getHotelService().createhotelcancel(hotelcancel);
					}
				}
			} else {
				WriteLog.write("酒店取消陈述数据", "暂无信息:合同id" + hmcontract);
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("酒店取消陈述数据", "错误信息:" + resultco + ","
						+ errormessage);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
