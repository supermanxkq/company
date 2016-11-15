package com.ccservice.huamin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.apache.jasper.tagplugins.jstl.core.ForEach;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.hotelcancel.hotelcancel;

/**
 * 接口二十--制作订单功能
 * 
 * @author 师卫林 2012-6-19下午01:51:58
 */
public class Qbooking {
	public static void main(String[] args) {

	}
	/**
	 * 
	 * @param Contract 酒店合同号码
	 * @param Ver 酒店合同版本
	 * @param refno 公司参考编号
	 * @param guarrantee 1-保证入住,0-不保证入住
	 * @param Checkin 入住日期 
	 * @param Checkout 离店日期
	 * @param prod 产品代码
	 * @param cat 房型代码
	 * @param Bedtype 床型代码
	 * @param Serv 服务组合
	 * @param Bf 早餐数量
	 * @param Flightinfo 航班资料
	 * @param Guest 客人姓名
	 * @param Servcode 服务代码 --不是必须的
	 * @param Qty 服务数量 --不是必须的
	 * @param Night 服务晚数 --不是必须的
	 * @param Sr 特别要求代码 --不是必须的
	 * @throws ParseException 
	 */
	public static void booking(String Contract,String Ver,String refno,String Checkin, String Checkout, String prod, String cat,
			String Bedtype, String Serv, int Bf, String Flightinfo, String Guest, String Servcode, int Qty, int Night, String Sr) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yy", Locale.US);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
		String totalurl = "http://123.196.113.28:8034/cn_interface/HMHotel.jsp?api=qbooking&p_company=CN00839&p_id=HTHYAPI&p_pass=HTHYAPI&p_contract=" +
				Contract.trim()+"&p_ver=&" +Ver.trim()+
				"p_refno=" +refno.trim()+
				"&p_guarantee=1&p_checkin=" +sdf.format(sd.parse(Checkin))+
				"&p_checkout=" +sdf.format(sd.parse(Checkout))+
				"&p_prod=" +prod.trim()+
				"&p_cat=" +cat.trim()+
				"&p_type=" +Bedtype.trim()+
				"&p_serv=" +Serv.trim()+
				"&p_bf=" +Bf+
				"&p_flightinfo=" +Flightinfo.trim()+
				"&p_guest=&" +Guest.trim()+
				"p_servcode=&p_qty=&p_night=&p_sr=";
		URL url = null;
		try {
			url = new URL(totalurl);
			try {
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream inputStream = conn.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
				StringBuilder sb = new StringBuilder();
				String str = "";
				while ((str = br.readLine()) != null) {
					sb.append(str);
				}
				String strReturn = sb.toString().trim();
				conn.disconnect();
				if (strReturn.contains("?")) {
					strReturn = strReturn.replaceFirst("\\?", "");
				}
				parsexml(strReturn);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void parsexml(String xmlstr) {
		SAXBuilder build = new SAXBuilder();
		Document document;
		try {
			document = build.build(new StringReader(xmlstr));
			Element root = document.getRootElement();
			Element result = root.getChild("XML_RESULT");
			if (result!=null) {
				Element booking=result.getChild("BOOKING");
				//华闽订单参考号码
				String hmcref=booking.getChildText("HMCREF");
				//公司名称
				String company=booking.getChildText("COMPANY");
				//公司用户名称
				String id=booking.getChildText("ID");
				//酒店合同号码
				String contract=booking.getChildText("CONTRACT");
				//酒店合同版本
				String ver=booking.getChildText("VER");
				//酒店代码
				String hotel=booking.getChildText("HOTEL");
				//N--没有获得分配,Y--获得分配代码
				String cur=booking.getChildText("CUR");
				//酒店合计
				String amount=booking.getChildText("AMOUNT");
				//订单日期
				String bookdate=booking.getChildText("BOOKDATE");
				//订单状态 IC-及时确认,OR-要求中,CFM-已确认,CA-取消中,CAN-已取消,ACK-保证入住
				String bookstatus=booking.getChildText("BOOKSTATUS");
				//保证入住状态
				String gaurantee=booking.getChildText("GAURANTEE");
				List<Element> rooms=booking.getChildren("ROOMS");
				for (Element room : rooms) {
					//房型数量
					String roomno=room.getChildText("ROOMNO");
					//入住日期
					String checkid=room.getChildText("CHECKIN");
					//退房日期
					String checkout=room.getChildText("CHECKOUT");
					//产品代码
					String prod=room.getChildText("PROD");
					//房型代码
					String cat=room.getChildText("CAT");
					//床型代码
					String type=room.getChildText("TYPE");
					//服务组合
					String serv=room.getChildText("SERV");
					//早餐数量
					String bf=room.getChildText("BF");
					//航班资料
					String flight_info=room.getChildText("FLIGHT_INFO");
					//房间状态 IA-内部分配,即时确认 FS-自由销售,即时确认 OR-要求中
					String roomstatus=room.getChildText("ROOMSTATUS");
					List<Element> stays=room.getChildren("STAYS");
					for (Element stay : stays) {
						//停留日数
						String staydate=stay.getChildText("STAYDATE");
						//停留日数价格
						String price=stay.getChildText("PRICE");
					}
					List<Element> guests=room.getChildren("GUESTS");
					for (Element guest : guests) {
						//客人名称
						String guestname=guest.getChildText("GUESTNAME");
					}
					List<Element> specials=room.getChildren("SPECIALS");
					for (Element special : specials) {
						//特别要求代码
						String sr=special.getChildText("SR");
					}
					List<Element> services=room.getChildren("SERVICES");
					for (Element service : services) {
						//服务代码
						String servcode=service.getChildText("SERVCODE");
						//服务数量
						String qty=service.getChildText("QTY");
						//服务晚数
						String nonight=service.getChildText("NONIGHT");
						//服务价格
						String price=service.getChildText("PRICE");
						//服务合计
						String serviceAmount=service.getChildText("AMOUNT");
					}
				}
			} 
			Element resultcode = result.getChild("RETURN_CODE");
			String resultco = resultcode.getText();
			String errormessage = result.getChildText("ERROR_MESSAGE");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
