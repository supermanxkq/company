package com.ccservice.huamin;

import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.atom.service.HMRequestUitl;
import com.ccservice.b2b2c.base.bedtype.Bedtype;

/**
 * 接口七 查询床型
 */
public class Qtype {
	public static void main(String[] args) {
		getType();
	}

	public static void getType() {
		//String totalurl = "http://123.196.113.28:8034/cn_interface/huaminHotel.jsp?test=qtype&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_lang=SIM&p_cat=&p_catname=";
		String totalurl = HMRequestUitl.getHMRequestUrlHeader() + "&api=qtype&p_lang=SIM&p_cat=&p_catname=";
		System.out.println(totalurl);
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
			List<Element> types = result.getChildren("TYPES");
			for (Element type : types) {
				Bedtype bedtype = new Bedtype();
				String typeid = type.getChildText("TYPE");
				String typename = type.getChildText("TYPENAME");
				String maxguest = type.getChildText("MAXGUEST");
				bedtype.setType(typeid);
				bedtype.setTypename(typename);
				bedtype.setMaxguest(Long.parseLong(maxguest.trim()));
				List<Bedtype> bedtypefromtable = Server.getInstance().getHotelService().findAllBedtype(
						" WHERE " + Bedtype.COL_type + "='" + bedtype.getType() + "'", "", -1, 0);
				if(bedtypefromtable.size()>0){
					if(bedtypefromtable.size()>1){
						System.out.println(bedtypefromtable.get(0).getId());
					}
					bedtype.setId(bedtypefromtable.get(0).getId());
					Server.getInstance().getHotelService().updateBedtypeIgnoreNull(bedtype);
					System.out.println("更新床型:"+typename);
				}else{
					System.out.println(typeid);
					Server.getInstance().getHotelService().createBedtype(bedtype);
					System.out.println("插入床型:"+typename);
				}
			}
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			if (errormessage != null || !"".equals(errormessage)) {
				WriteLog.write("床型新增数据", "错误信息:" + resultco + "," + errormessage);
			}
			System.out.println("床型数据插入完成……");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
