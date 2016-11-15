package com.ccservice.huamin;

import java.io.StringReader;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.specialreq.Specialreq;

/**
 * 查詢特別要求 接口八
 * 
 * @author wzc-ccs
 * 
 */
public class Qsr {

	public static void main(String[] args) {
		getQsr();
	}

	public static void getQsr() {
		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qsr&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_sr=&p_srname=";
		String str = Util.getStr(totalurl);
		System.out.println(str);
		parsexml(str);
	}

	public static void parsexml(String xml) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xml));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			List<Element> specialrequest = result.getChildren("SPECIALREQUESTS");
			for (Element spr : specialrequest) {
				Specialreq specialreq = new Specialreq();
				String srcode = spr.getChildText("SR");
				String srname = spr.getChildText("SRNAME");
				specialreq.setReqid(srcode);
				specialreq.setReqname(srname);
				List<Specialreq> specialreqfromtable = Server.getInstance().getHotelService().findAllSpecialreq(
						" WHERE " + Specialreq.COL_reqid + "='" + specialreq.getReqid() + "'", "", -1, 0);
				if(specialreqfromtable.size()>0){
					specialreq.setId(specialreqfromtable.get(0).getId());
					Server.getInstance().getHotelService().updateSpecialreqIgnoreNull(specialreq);
				}else{
					Server.getInstance().getHotelService().createSpecialreq(specialreq);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
