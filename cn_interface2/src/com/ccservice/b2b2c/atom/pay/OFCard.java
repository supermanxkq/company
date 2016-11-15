package com.ccservice.b2b2c.atom.pay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.ccservice.b2b2c.base.recharge.OFRechargeinfo;
import com.ccservice.b2b2c.base.util.Util;

/**
 * @author Administrator 欧飞手机充值
 * 
 */
public class OFCard {
	Log log=LogFactory.getLog(OFCard.class);
	private String userid;// 帐号
	private String password;// 密码
	private String ipAddress;// 服务器地址
	private String ipAddressspare;// 备用服务器地址
	private String mobileunicom = "";// 移动联通cardid
	private String telecom = "";// 电信cardid
	private String ketstr = "";// KetStr
	private String qcardid;// Q币Cardid
	private String onlinrechargestr;
	private String querycardstr;// 具体商品查询路径
	private String version;
	private DateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getMobileunicom() {
		return mobileunicom;
	}

	public void setMobileunicom(String mobileunicom) {
		this.mobileunicom = mobileunicom;
	}

	public String getTelecom() {
		return telecom;
	}

	public void setTelecom(String telecom) {
		this.telecom = telecom;
	}

	public DateFormat getDateformat() {
		return dateformat;
	}

	public void setDateformat(DateFormat dateformat) {
		this.dateformat = dateformat;
	}
	
	/**
	 * 根据手机号和面值查询商品信息（telquery.do）
	 * 此接口用于查询手机号是否能充值，如果能充值返回商品信息，不能充返回运营商维护
	 * http://api2.ofpay.com/telquery.do?userid=A850584&userpws=5463bd907398fa013db3c5ddae199a11&phoneno=18625427511&pervalue=30&version=6.0
	 * @param phone
	 * @param money
	 * @return
	 */
	public OFRechargeinfo getPhonerechargeinfo(String phone,float money){
		String ulr="http://api2.ofpay.com/telquery.do?userid="+this.userid+"&userpws="+this.password+"&phoneno="+phone+"&pervalue="+money+"&version="+this.version;
		OFRechargeinfo recharg=new OFRechargeinfo();
		try {
			URL urlc=new URL(ulr);
			HttpURLConnection connection=(HttpURLConnection)urlc.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", "gb2312");
			connection.connect();
			SAXReader reader=new SAXReader();
			Document document=reader.read(connection.getInputStream());
			Element root=document.getRootElement();
			String retcode=root.elementTextTrim("retcode");
			String err_msg=root.elementTextTrim("err_msg");
			
			recharg.setMessage(err_msg);
			if(retcode.equals("1")){//返回充值信息
				recharg.setState(true);
				String inprice=root.elementTextTrim("inprice");
				recharg.setPrice(Float.valueOf(inprice));
				String  game_area=root.elementTextTrim("game_area");
				recharg.setMessage(game_area);
			}else{
				recharg.setState(false);
			}
		} catch (Exception e) {
			
		}
		return recharg;
	  
	}

	/**
	 * 手机充值
	 * @param cardtype
	 *            充值类型：移动联通电信
	 * @param cardnum
	 *            充值金额对应值 0.2 10，0.4 20
	 * @param ordernumber
	 *            商户订单号
	 * @param mobilenumber
	 *            充值手机号
	 */
	public String mobileRecharge(int cardtype, String cardnum,
			String ordernumber, String mobilenumber) {
		log.error(mobilenumber+"：手机话费充值。");
		String retvalue = "";
		try {

			// userid+userpws+cardid+cardnum+sporder_id+sporder_time+
			// game_userid
			String time = dateformat.format(new Timestamp(System
					.currentTimeMillis()));
			String md5str = userid + password  +"140101"+ cardnum + ordernumber
					+ time + mobilenumber + ketstr;
			md5str = Util.MD5(md5str).toUpperCase();
			String url = ipAddress + onlinrechargestr + "userid=" + userid
					+ "&userpws=" + password + "&cardid=140101&cardnum="
					+ cardnum + "&sporder_id=" + ordernumber + "&sporder_time="
					+ time + "&game_userid=" + mobilenumber + "&md5_str="
					+ md5str + "&version=" + version;
			log.error(mobilenumber+":URL>"+url);
			java.net.URL Url = new java.net.URL(url);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url
					.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Accept-Charset", "gb2312");
			conn.connect();
			SAXReader reader = new SAXReader();
			Document document = reader.read(conn.getInputStream());
			org.dom4j.Element root = document.getRootElement();
			String err_msg = root.elementTextTrim("err_msg");
			log.error(mobilenumber+":err_msg>"+err_msg);
			if (err_msg != null && err_msg.length() > 0) {
				return err_msg;
			}
			//备用信息
			String orderid = root.elementTextTrim("orderid");
			String cardid = root.elementTextTrim("cardid");
			String tcardnum = root.elementTextTrim("cardnum");
			String ordercash = root.elementTextTrim("ordercash");
			String cardname = root.elementTextTrim("cardname");
			String sporder_id = root.elementTextTrim("sporder_id");
			String game_userid = root.elementTextTrim("game_userid");
			//-------
			String game_state = root.elementTextTrim("game_state");
			retvalue = game_state;
			System.out.println(err_msg);
			conn.disconnect();
			log.error(mobilenumber+":retvalue>"+retvalue);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (retvalue.length() > 0) {
			return retvalue;
		}
		return "";

	}

	/**
	 * 获取商户信息
	 * @return
	 */
	public Map<String, String> getUserInof() {
		String ulr = ipAddress+"queryuserinfo.do?userid="
				+ userid + "&userpws=" + this.password + "&version="
				+ this.version;
		Map<String, String> map = new HashMap<String, String>();
		try {
			URL neturl = new URL(ulr);
			HttpURLConnection connection = (HttpURLConnection) neturl
					.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestProperty("Accept-Charset", "gb2312");
			SAXReader reader = new SAXReader();
			Document document = reader.read(connection.getInputStream());
			org.dom4j.Element root = document.getRootElement();
			String err_msg = root.elementTextTrim("err_msg");
			if(err_msg!=null&&err_msg.trim().length()>0){
				map.put("err_msg", err_msg);
				return map;
			}
			String ret_leftcredit=root.elementTextTrim("ret_leftcredit");
			map.put("ret_leftcredit", ret_leftcredit);

		} catch (IOException e) {
			map.put("err_msg", "服务器端网络异常");
		}
        catch (DocumentException e) {
            e.printStackTrace();
        }

		return map;
	}

	/**
	 * 获取充值状态
	 * 
	 * @param ordernumber
	 * @return
	 * @throws IOException
	 */
	public String getPaystate(String ordernumber) throws IOException {
		String url = this.ipAddressspare+"api/query.do?userid="
				+ userid + "&spbillid=" + ordernumber;
		
		java.net.URL Url = new java.net.URL(url);
		java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url
				.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept-Charset", "gb2312");
		conn.connect();
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn
				.getInputStream()));

		String message = "";
		String m = "";
		while ((message = reader.readLine()) != null) {
			m += message;
		}
		return m.trim();

	}

	public static void main(String[] args) throws DocumentException {
		  OFCard d=new OFCard();
		d.ipAddress  ="http://api2.ofpay.com/";
		d.querycardstr="querycardinfo.do?";
		d.userid="A748518";
		d.password="2f9480f40aa8bac0b9cf8cb0b54ef263";
		d.getQQmoney("141202");
		// http://esales1.ofcard.com:8088/querycardinfo.do?userid=A676877&userpws=dfa1b63cbb7d09eb5902a05ceb5fb2f9&cardid=2206&version=4.0
		// String url=ipAddress+"userid="+userid+"&userpws="+
		// password+"&cardid="+this.qcardid+"&version=4.0";
		// URL theurl=new URL(url);
		// HttpURLConnection conn=(HttpURLConnection)theurl.openConnection();
		// conn.setDoInput(true);
		// conn.setDoOutput(true);
		// conn.setRequestProperty("Accept-Charset", "gb2312");
		// conn.connect();
		// //InputStream stream=(InputStream) conn.getInputStream();
		// BufferedReader readers=new BufferedReader(new
		// InputStreamReader(conn.getInputStream()));
		// String s="";
		// while((s=readers.readLine())!=null){
		// System.out.println(s);
		// }

		SAXReader reader = new SAXReader();
		// Document document=reader.read(conn.getInputStream());
		Document document = reader.read(OFCard.class
				.getResourceAsStream("qq.xml"));
		Element root = document.getRootElement();
		String errmsg = root.elementTextTrim("err_msg");
		if (errmsg.trim().equals("")) {
			String retcode = root.elementTextTrim("retcode");
			Element retelement = root.element("ret_cardinfos");
			Iterator<Element> elementlist = retelement.elementIterator("card");
			List<Map<String, String>> qlist = new ArrayList<Map<String, String>>();
			while (elementlist.hasNext()) {
				Map<String, String> emap = new HashMap<String, String>();
				Element element = elementlist.next();
				String cardid = element.elementTextTrim("cardid");
				emap.put("cardid", cardid);
				String pervalue = element.elementTextTrim("pervalue");
				emap.put("pervalue", pervalue);
				String inprice = element.elementTextTrim("inprice");
				emap.put("inprice", inprice);
				String sysddprice = element.elementTextTrim("sysddprice");
				emap.put("sysddprice", sysddprice);
				String sysdd1price = element.elementTextTrim("sysdd1price");
				emap.put("sysdd1price", sysdd1price);
				String sysdd2price = element.elementTextTrim("sysdd2price");
				emap.put("sysdd2price", sysdd2price);
				String agentprice = element.elementTextTrim("agentprice");
				emap.put("agentprice", agentprice);
				String agentprice1 = element.elementTextTrim("agentprice1");
				emap.put("agentprice1", agentprice1);
				String memberprice = element.elementTextTrim("memberprice");
				emap.put("memberprice", memberprice);
				String innum = element.elementTextTrim("innum");
				emap.put("innum", innum);
				String cardname = element.elementTextTrim("cardname");
				emap.put("cardname", cardname);
				String howeasy = element.elementTextTrim("howeasy");
				emap.put("howeasy", howeasy);
				String amounts = element.elementTextTrim("amounts");
				emap.put("amounts", amounts);
				String subclassid = element.elementTextTrim("subclassid");
				emap.put("subclassid", subclassid);
				String classtype = element.elementTextTrim("classtype");
				emap.put("classtype", classtype);
				String fullcostsite = element.elementTextTrim("fullcostsite");
				emap.put("fullcostsite", fullcostsite);
				String gamearea = element.elementTextTrim("gamearea");
				emap.put("gamearea", gamearea);
				String gamesrv = element.elementTextTrim("gamesrv");
				emap.put("gamesrv", gamesrv);
				String lastreftime = element.elementTextTrim("lastreftime");
				emap.put("lastreftime", lastreftime);
				qlist.add(emap);

			}
		}

	}

	/**
	 * @return 获取Q币信息
	 */
	public Map<String, String> getQQmoney(String cardid) {
		// http://esales1.ofcard.com:8088/querycardinfo.do?userid=A676877&userpws=dfa1b63cbb7d09eb5902a05ceb5fb2f9&cardid=220612&version=4.0
		if (cardid == null) {
			cardid = this.qcardid;
		}
		String url = ipAddress + this.querycardstr + "userid=" + userid
				+ "&userpws=" + password + "&cardid=" + cardid + "&version=4.0";
		Map<String, String> emap = new HashMap<String, String>();
		System.out.println("*****************************"+url);
		try {
			URL theurl;
			theurl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) theurl
					.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Accept-Charset", "gb2312");
			conn.connect();
			SAXReader reader = new SAXReader();
			Document document = reader.read(conn.getInputStream());
			Element root = document.getRootElement();
			String errmsg = root.elementTextTrim("err_msg");
			if (errmsg.trim().equals("")) {
				String retcode = root.elementTextTrim("retcode");
				Element retelement = root.element("ret_cardinfos");
				Element cardelement = retelement.element("card");
				String tcardid = cardelement.elementTextTrim("cardid");
				emap.put("cardid", tcardid);
				String pervalue = cardelement.elementTextTrim("pervalue");
				emap.put("pervalue", pervalue);
				String inprice = cardelement.elementTextTrim("inprice");
				emap.put("inprice", inprice);
				String sysddprice = cardelement.elementTextTrim("sysddprice");
				emap.put("sysddprice", sysddprice);
				String sysdd1price = cardelement.elementTextTrim("sysdd1price");
				emap.put("sysdd1price", sysdd1price);
				String sysdd2price = cardelement.elementTextTrim("sysdd2price");
				emap.put("sysdd2price", sysdd2price);
				String agentprice = cardelement.elementTextTrim("agentprice");
				emap.put("agentprice", agentprice);
				String agentprice1 = cardelement.elementTextTrim("agentprice1");
				emap.put("agentprice1", agentprice1);
				String memberprice = cardelement.elementTextTrim("memberprice");
				emap.put("memberprice", memberprice);
				String innum = cardelement.elementTextTrim("innum");
				emap.put("innum", innum);
				String cardname = cardelement.elementTextTrim("cardname");
				emap.put("cardname", cardname);
				String othername = cardelement.elementTextTrim("othername");
				emap.put("othername", othername);
				String howeasy = cardelement.elementTextTrim("howeasy");
				emap.put("howeasy", howeasy);
				String amounts = cardelement.elementTextTrim("amounts");
				emap.put("amounts", amounts);
				String subclassid = cardelement.elementTextTrim("subclassid");
				emap.put("subclassid", subclassid);
				String classtype = cardelement.elementTextTrim("classtype");
				emap.put("classtype", classtype);
				String fullcostsite = cardelement
						.elementTextTrim("fullcostsite");
				emap.put("fullcostsite", fullcostsite);
				String gamearea = cardelement.elementTextTrim("gamearea");
				emap.put("gamearea", gamearea);
				String gamesrv = cardelement.elementTextTrim("gamesrv");
				emap.put("gamesrv", gamesrv);
				String lastreftime = cardelement.elementTextTrim("lastreftime");
				emap.put("lastreftime", lastreftime);

			} else {
				emap.put("err_msg", errmsg);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        catch (DocumentException e) {
            e.printStackTrace();
        }

		return emap;
	}

	public List<Map<String, String>> getQmoneyInfo() throws IOException {
		// http://esales1.ofcard.com:8088/querycardinfo.do?userid=A676877&userpws=dfa1b63cbb7d09eb5902a05ceb5fb2f9&cardid=2206&version=4.0
		// String url=ipAddress+"userid="+userid+"&userpws="+
		// password+"&cardid="+this.qcardid+"&version=4.0";
		// URL theurl=new URL(url);
		// HttpURLConnection conn=(HttpURLConnection)theurl.openConnection();
		// conn.setDoInput(true);
		// conn.setDoOutput(true);
		// conn.setRequestProperty("Accept-Charset", "gb2312");
		// conn.connect();
		// //InputStream stream=(InputStream) conn.getInputStream();
		// BufferedReader readers=new BufferedReader(new
		// InputStreamReader(conn.getInputStream()));
		// String s="";
		// while((s=readers.readLine())!=null){
		// System.out.println(s);
		// }
		SAXReader reader = new SAXReader();
		// Document document=reader.read(conn.getInputStream());
        Document document = null;
        try {
            document = reader.read(this.getClass().getResourceAsStream("qq.xml"));
        }
        catch (DocumentException e) {
            e.printStackTrace();
        }
		Element root = document.getRootElement();
		String errmsg = root.elementTextTrim("err_msg");
		String retcode = root.elementTextTrim("retcode");
		Element retelement = root.element("ret_cardinfos");
		Iterator<Element> elementlist = retelement.elementIterator("card");
		List<Map<String, String>> qlist = new ArrayList<Map<String, String>>();
		while (elementlist.hasNext()) {
			Map<String, String> emap = new HashMap<String, String>();
			Element element = elementlist.next();
			String cardid = element.elementTextTrim("cardid");
			emap.put("cardid", cardid);
			String pervalue = element.elementTextTrim("pervalue");
			emap.put("pervalue", pervalue);
			String inprice = element.elementTextTrim("inprice");
			emap.put("inprice", inprice);
			String sysddprice = element.elementTextTrim("sysddprice");
			emap.put("sysddprice", sysddprice);
			String sysdd1price = element.elementTextTrim("sysdd1price");
			emap.put("sysdd1price", sysdd1price);
			String sysdd2price = element.elementTextTrim("sysdd2price");
			emap.put("sysdd2price", sysdd2price);
			String agentprice = element.elementTextTrim("agentprice");
			emap.put("agentprice", agentprice);
			String agentprice1 = element.elementTextTrim("agentprice1");
			emap.put("agentprice1", agentprice1);
			String memberprice = element.elementTextTrim("memberprice");
			emap.put("memberprice", memberprice);
			String innum = element.elementTextTrim("innum");
			emap.put("innum", innum);
			String cardname = element.elementTextTrim("cardname");
			emap.put("cardname", cardname);
			String othername = element.elementTextTrim("othername");
			emap.put("othername", othername);
			String howeasy = element.elementTextTrim("howeasy");
			emap.put("howeasy", howeasy);
			String amounts = element.elementTextTrim("amounts");
			emap.put("amounts", amounts);
			String subclassid = element.elementTextTrim("subclassid");
			emap.put("subclassid", subclassid);
			String classtype = element.elementTextTrim("classtype");
			emap.put("classtype", classtype);
			String fullcostsite = element.elementTextTrim("fullcostsite");
			emap.put("fullcostsite", fullcostsite);
			String gamearea = element.elementTextTrim("gamearea");
			emap.put("gamearea", gamearea);
			String gamesrv = element.elementTextTrim("gamesrv");
			emap.put("gamesrv", gamesrv);
			String lastreftime = element.elementTextTrim("lastreftime");
			emap.put("lastreftime", lastreftime);
			qlist.add(emap);

		}
		return qlist;
	}

	/**
	 * Q币充值
	 * 
	 * @param ordernumber
	 *            商家订单号
	 * @param cardid
	 *            所需提货商品的编码
	 * @param buynum
	 *            所需提货商品的数量
	 * @param qqnumber
	 *            QQ号
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public String qmoneyRecharge(String ordernumber, String cardid, int buynum,
			String qqnumber) {
		log.error(qqnumber+"：QQ币充值。");
		String retvalue = "";
		try {
			// userid+userpws+cardid+cardnum+sporder_id+sporder_time+
			// game_userid+ game_area+ game_srv
			String time = dateformat.format(new Timestamp(System.currentTimeMillis()));
			String md5str = userid + this.password + cardid + buynum
					+ ordernumber + time + qqnumber + ketstr;
			md5str = Util.MD5(md5str).toUpperCase();
			// http://esales1.ofcard.com:8088/onlineorder.do?userid=A00002&userpws=xxxxxxx&cardid=360101&cardnum=1&sporder_id=xxxxxxxxx&sporder_time=xxxxxxxx&game_userid=xxxxx&game_area=xxxxxx&game_srv=xxxxx&md5_str=xxxxxxxxxxxxx&version
			String url = this.ipAddress + this.onlinrechargestr + "userid="
					+ userid + "&userpws=" + this.password + "&cardid="
					+ cardid + "&cardnum=" + buynum + "&sporder_id="
					+ ordernumber + "&sporder_time=" + time + "&game_userid="
					+ qqnumber + "&md5_str=" + md5str + "&version=" + version;
			log.error(qqnumber+"：URL>"+url);
			java.net.URL Url = new java.net.URL(url);
			java.net.HttpURLConnection conn = (java.net.HttpURLConnection) Url
					.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Accept-Charset", "gb2312");
			conn.connect();
			SAXReader reader = new SAXReader();
			Document document = reader.read(conn.getInputStream());
			org.dom4j.Element root = document.getRootElement();
			String err_msg = root.elementTextTrim("err_msg");
			String orderid = root.elementTextTrim("orderid");
			String qcardid = root.elementTextTrim("cardid");
			String qcardnum = root.elementTextTrim("cardnum");
			String ordercash = root.elementTextTrim("ordercash");
			String cardname = root.elementTextTrim("cardname");
			String sporder_id = root.elementTextTrim("sporder_id");
			String game_userid = root.elementTextTrim("game_userid");
			String game_area = root.elementTextTrim("game_area");
			String game_srv = root.elementTextTrim("game_srv");
			String game_state = root.elementTextTrim("game_state");
			retvalue = game_state;
			System.out.println(err_msg);
			conn.disconnect();
			log.error(qqnumber+"：retvalue>"+retvalue);
			log.error(qqnumber+"：err_msg>"+err_msg);
			if (err_msg != null && err_msg.length() > 0) {
				return err_msg;
			}
		} catch (Exception e) {
			System.out.println("Q币充值出现以下异常：");
			e.printStackTrace();
		}
		return retvalue;
	}

	/**
	 * public String qmoneyRecharge(){ String retvalue=""; try {
	 * 
	 * //包体=userid+userpws+cardid+cardnum+sporder_id+sporder_time+ game_userid+
	 * game_area+ game_srv String time=dateformat.format(new
	 * Timestamp(System.currentTimeMillis())); String
	 * md5str=userid+password+type+cardnum+ordernumber+time+mobilenumber+ketstr;
	 * md5str=Util.MD5(md5str).toUpperCase(); String
	 * url=ipAddress+"userid="+userid+"&userpws="+
	 * password+"&cardid="+type+"&cardnum="+cardnum+"&sporder_id="+ordernumber+
	 * "&sporder_time="+time+
	 * "&game_userid="+mobilenumber+"&md5_str="+md5str+"&version=4.0";
	 * System.out.println("*************手机充值URL:************");
	 * System.out.println(url); java.net.URL Url = new java.net.URL(url);
	 * java.net.HttpURLConnection conn = (java.net.HttpURLConnection)
	 * Url.openConnection(); conn.setDoInput(true); conn.setDoOutput(true);
	 * conn.setRequestProperty("Accept-Charset", "gb2312"); conn.connect();
	 * SAXReader reader= new SAXReader(); Document
	 * document=reader.read(conn.getInputStream()); org.dom4j.Element
	 * root=document.getRootElement(); String err_msg=
	 * root.elementTextTrim("err_msg"); String orderid=
	 * root.elementTextTrim("orderid"); String cardid=
	 * root.elementTextTrim("cardid"); String tcardnum=
	 * root.elementTextTrim("cardnum"); String ordercash=
	 * root.elementTextTrim("ordercash"); String cardname=
	 * root.elementTextTrim("cardname"); String sporder_id=
	 * root.elementTextTrim("sporder_id"); String game_userid=
	 * root.elementTextTrim("game_userid"); String game_state=
	 * root.elementTextTrim("game_state"); retvalue=game_state;
	 * System.out.println(err_msg); conn.disconnect();
	 * if(err_msg!=null&&err_msg.length()>0){ return err_msg; } } catch
	 * (Exception e) { e.printStackTrace(); } if(retvalue.length()>0){ return
	 * retvalue; } return ""; }
	 */
	public String getKetstr() {
		return ketstr;
	}

	public void setKetstr(String ketstr) {
		this.ketstr = ketstr;
	}

	public String getQcardid() {
		return qcardid;
	}

	public void setQcardid(String qcardid) {
		this.qcardid = qcardid;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getQuerycardstr() {
		return querycardstr;
	}

	public void setQuerycardstr(String querycardstr) {
		this.querycardstr = querycardstr;
	}

	public String getOnlinrechargestr() {
		return onlinrechargestr;
	}

	public void setOnlinrechargestr(String onlinrechargestr) {
		this.onlinrechargestr = onlinrechargestr;
	}

	public String getIpAddressspare() {
		return ipAddressspare;
	}

	public void setIpAddressspare(String ipAddressspare) {
		this.ipAddressspare = ipAddressspare;
	}

}
