package com.ccservice.huamin;

import java.io.StringReader;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.servercode.Servercode;

/**
 * 接口九
 * @author wzc-ccs 功能是查詢服务代码
 */
public class Qservcode {

	public static void main(String[] args) {
		getQservcode("SIM");
	}

	public static void getQservcode(String countrycode) {
		//String totalurl = "http://123.196.113.28:8034/cn_interface/huaminHotel.jsp?test=qservcode&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_servcode=&p_servname=";
		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qservcode&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=" +
		countrycode+"&p_servcode=&p_servname=";
		String str = Util.getStr(totalurl);
		parsexml(str);
	}

	public static void parsexml(String xml) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xml));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			List<Element> services = result.getChildren("SERVICES");
			for (Element service : services) {
				String servcode = service.getChildText("SERVCODE");
				String servname = service.getChildText("SERVNAME");
				System.out.println("名字:"+servname);
				if(!servname.equals("")){
					Servercode servercode = new Servercode();
					servercode.setServcode(servcode);
					servercode.setServname(servname);
					List<Servercode> servercodefromtable = Server.getInstance().getHotelService().findAllServercode(
							" WHERE " + Servercode.COL_servcode+ "='" + servercode.getServcode()+ "'", "", -1, 0);
					if(servercodefromtable.size()>0){
						servercode.setId(servercodefromtable.get(0).getId());
						Server.getInstance().getHotelService().updateServercodeIgnoreNull(servercode);
					}else{
						Server.getInstance().getHotelService().createServercode(servercode);
					}
				}
			}
			System.out.println("服务代码完成插入……");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
