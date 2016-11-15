package com.ccservice.b2b2c.atom.service;

import java.io.StringReader;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.ccservice.b2b2c.atom.hotel.HTCHotel;
import com.ccservice.b2b2c.base.hotel.BookedRates;

public class HTCHotelService implements IHTCHotelService {

	private static final String USERNAME="ids_207_992823_yytx";
	private static final String PASSWORD="tx992y8y23";
	// 获取酒店列表
	@Override
	public String getproplist(String date) {
		String xml = "<crsmessage PropID=\"\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getproplist\"><PropLimits><date>"
				+ date + "</date></PropLimits></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取酒店基本信息

	public String getProperty(long hotelid) {
		String xml = "<crsmessage PropID=\""
				+ hotelid
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getProperty\"></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取酒店详细信息
	public String getDesc(long id) {
		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getdesc\"></crsmessage>";
		String output = "";

		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 根据类型获取酒店详细信息
	public String getDescByType(String type, long id) {
		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getdesc\"><descriptionmap><descriptionlist><type>"
				+ type
				+ "</type></descriptionlist></descriptionmap></crsmessage>";
		String output = "";

		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取酒店所有房间代码与详细信息
	@Override
	public String getRoomObj(long id) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getroomobj\"></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 根据房间类型代码查询房间详细信息
	@Override
	public String getRoomObjByType(String type, long id) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getroomobj\"><roomobjmap><roomobjdata><roomobjlist><roomtype>"
				+ type
				+ "</roomtype></roomobjlist></roomobjdata></roomobjmap></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取指定酒店所有价格代码的详细信息
	@Override
	public String getRateObj(long id,String date) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getrateobj\"><rateobjmap><rateobjdata><rateobjlist><rateclass/></rateobjlist></rateobjdata></rateobjmap></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取指定酒店指定价格代码详细信息
	@Override
	public String getRateObjByType(String type, long id) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getrateobj\"><rateobjmap><rateobjdata><rateobjlist><rateclass>"
				+ type
				+ "</rateclass></rateobjlist></rateobjdata></rateobjmap></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取酒店所有计划代码的详细信息
	@Override
	public String getPlanObj(long id) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getplanobj\"><planobjmap><planobjdata><planobjlist><planid/></planobjlist></planobjdata></planobjmap></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取酒店指定计划代码的详细信息
	@Override
	public String getPlanObjByPlanid(String planid, long id) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getplanobj\"><planobjmap><planobjdata><planobjlist><planid>"
				+ planid
				+ "</planid></planobjlist></planobjdata></planobjmap></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取指定酒店图片
	@Override
	public String getImage(long id) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getimage\" language=\"\"></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取指定酒店订单服务
	@Override
	public String getPropresv(String channel, String confnum, String iata) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage msgtype=\"getpropresv\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\"><resvuser/><channel>"
				+ channel
				+ "</channel><reservation><confnum>"
				+ confnum
				+ "</confnum><iata>"
				+ iata
				+ "</iata></reservation></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 查询订单审核状态
	@Override
	public String getResvaudit(String cnfnum, String iata) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getresvaudit\" language=\"zh\"><cnfnum>"
				+ cnfnum + "</cnfnum><iata>" + iata + "</iata></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 搜索酒店列表的可用性信息
	@Override
	public String hotelSearch(String date, String nights, String ratestyle,
			long hotelid, String proplv, String city, String district,
			String tradearea, String guestposition, String keywords,
			String pageindex, String pagesize) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"hotelsearch\" language=\"zh\"><options detail=\"proplist\"/><staydetail><date>"
				+ date
				+ "</date><nights>"
				+ nights
				+ "</nights><ratestyle>"
				+ ratestyle
				+ "</ratestyle></staydetail><props><prop>"
				+ hotelid
				+ "</prop></props><search><city>"
				+ city
				+ "</city><proplv>"
				+ proplv
				+ "<proplv/><proplocation><district>"
				+ district
				+ "</district><tradearea>"
				+ tradearea
				+ "</tradearea><guestposition>"
				+ guestposition
				+ "</guestposition></proplocation><keywords>"
				+ keywords
				+ "</keywords></search><page><pageindex>"
				+ pageindex
				+ "</pageindex><pagesize>"
				+ pagesize
				+ "</pagesize></page></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendSerchHotel(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 搜索单个酒店计划价格的可用性信息
	@Override
	public String hotelSerchById(String date, String nights, String ratestyle,
			long hotelid, String proplv, String city, String district,
			String tradearea, String guestposition, String keywords,
			String pageindex, String pagesize) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"hotelsearch\" language=\"zh\"><options detail=\"propprice\"/><staydetail><date>"
				+ date
				+ "</date><nights>"
				+ nights
				+ "</nights><ratestyle>"
				+ ratestyle
				+ "</ratestyle></staydetail><props><prop>"
				+ hotelid
				+ "</prop></props><search><city>"
				+ city
				+ "</city><proplv>"
				+ proplv
				+ "<proplv/><proplocation><district>"
				+ district
				+ "</district><tradearea>"
				+ tradearea
				+ "</tradearea><guestposition>"
				+ guestposition
				+ "</guestposition></proplocation><keywords>"
				+ keywords
				+ "</keywords></search><page><pageindex>"
				+ pageindex
				+ "</pageindex><pagesize>"
				+ pagesize
				+ "</pagesize></page></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendSerchHotel(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 批量搜索酒店信息及计划价格的可用性信息
	@Override
	public String hotelSerchAll(String date, String nights, String ratestyle,
			long hotelid, String proplv, String city, String district,
			String tradearea, String guestposition, String keywords,
			String pageindex, String pagesize) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"hotelsearch\" language=\"zh\"><options detail=\"all\"/><staydetail><date>"
				+ date
				+ "</date><nights>"
				+ nights
				+ "</nights><ratestyle>"
				+ ratestyle
				+ "</ratestyle></staydetail><props><prop>"
				+ hotelid
				+ "</prop></props><search><city>"
				+ city
				+ "</city><proplv>"
				+ proplv
				+ "<proplv/><proplocation><district>"
				+ district
				+ "</district><tradearea>"
				+ tradearea
				+ "</tradearea><guestposition>"
				+ guestposition
				+ "</guestposition></proplocation><keywords>"
				+ keywords
				+ "</keywords></search><page><pageindex>"
				+ pageindex
				+ "</pageindex><pagesize>"
				+ pagesize
				+ "</pagesize></page></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendSerchHotel(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取酒店所有房型所有价格计划的可用性信息
	@Override
	public String getCrateMap(long id, String date,int night) {
		// TODO Auto-generated method stub

		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getcratemap\" nolog=\"1\"><options cascade=\"true\"/><staydetail><date>"
				+ date
				+ "</date><nights>"+night+"</nights><roomtype></roomtype><rateclass></rateclass><rooms>1</rooms><adults>1</adults><children>0</children><filter>0</filter><channel>Website</channel></staydetail></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取酒店指定房型指定价格计划可用性信息
	@Override
	public String getCrateMapByType(long id, String date, String roomtype,
			String rateclass,int night) {
		// TODO Auto-generated method stub

		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getcratemap\" nolog=\"1\"><options cascade=\"true\"/><staydetail><date>"
				+ date
				+ "</date><nights>1</nights><roomtype>"
				+ roomtype
				+ "</roomtype><rateclass>"
				+ rateclass
				+ "</rateclass><rooms>1</rooms><adults>1</adults><children>0</children><filter>0</filter><channel>Website</channel></staydetail></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取酒店所有房型所有价格计划的可用性信息
	@Override
	public String getOnlineRateMap(long id, String date,int night) {
		// TODO Auto-generated method stub

		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getonlineratemap\" nolog=\"1\"><options cascade=\"true\"/><staydetail><date>"
				+ date
				+ "</date><nights>"+night+"</nights><roomtype/><rateclass/><rooms>1</rooms><adults>1</adults><children/><filter>0</filter><channel>Website</channel></staydetail></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 获取酒店指定房型指定价格计划可用性信息
	@Override
	public String getOnlineRateMapByType(long id, String date, String roomtype,
			String rateclass,int night) {
		// TODO Auto-generated method stub

		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"getonlineratemap\" nolog=\"1\"><options cascade=\"true\"/><staydetail><date>"
				+ date
				+ "</date><nights>"+night+"</nights><roomtype>"
				+ roomtype
				+ "</roomtype><rateclass>"
				+ rateclass
				+ "</rateclass><rooms>1</rooms><adults>1</adults><children/><filter>0</filter><channel>Website</channel></staydetail></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	//新增订单，外部
	@Override
	public String newResv(long id, String isassure, String deliverymode,
			String outconfnum, String bookedrate, String date,
			String nights, String roomtype, String rateclass, String rooms,
			String adults, String children, String firstname, String lastname,
			String street1, String holdTime, String phone, String mobile,
			String email, String remark, String IATA) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage PropID=\""
			+ id
			+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"newresv\" language=\"zh\"><reservation><isassure>"
			+ isassure
			+ "</isassure><deliverymode>"
			+ deliverymode
			+ "</deliverymode><outconfnum>"
			+ outconfnum
			+ "</outconfnum>"+bookedrate+"<staydetail><date>"
			+ date
			+ "</date><nights>"
			+ nights
			+ "</nights><roomtype>"
			+ roomtype
			+ "</roomtype><rateclass>"
			+ rateclass
			+ "</rateclass><rooms>"
			+ rooms
			+ "</rooms><adults>"
			+ adults
			+ "</adults><children>"
			+ children
			+ "</children><channel>Website</channel></staydetail><guestinfo><firstname>"
			+ firstname + "</firstname><lastname>" + lastname
			+ "</lastname><street1>" + street1 + "</street1><holdTime>"
			+ holdTime + "</holdTime><phone>" + phone + "</phone><mobile>"
			+ mobile + "</mobile><email>" + email
			+ "</email></guestinfo><remarks>" + remark
			+ "<remark></remark></remarks><miscinfo><IATA>" + IATA
			+ "</IATA></miscinfo></reservation></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}
	
	//新增订单，内部
	@Override
	public String newResvHY(long id, String isassure, String deliverymode,
			String outconfnum, List<BookedRates> bookedrate, String date,
			String nights, String roomtype, String rateclass, String rooms,
			String adults, String children, String firstname, String lastname,
			String street1, String holdTime, String phone, String mobile,
			String email, String remark, String IATA) {
		// TODO Auto-generated method stub
		StringBuffer sb=new StringBuffer();
		for (int i = 0; i < bookedrate.size(); i++) {
			BookedRates br = bookedrate.get(i);
			sb.append("<bookedrate><date>");
			sb.append(br.getDate());
			sb.append("</date><rate>");
			sb.append(br.getRate());
			sb.append("</rate></bookedrate>");
		}
		System.out.println(sb);
		String xml = "<crsmessage PropID=\""
			+ id
			+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"newresv\" language=\"zh\"><reservation><isassure>"
			+ isassure
			+ "</isassure><deliverymode>"
			+ deliverymode
			+ "</deliverymode><outconfnum>"
			+ outconfnum
			+ "</outconfnum><bookedrates>"+sb.toString()+"</bookedrates><staydetail><date>"
			+ date
			+ "</date><nights>"
			+ nights
			+ "</nights><roomtype>"
			+ roomtype
			+ "</roomtype><rateclass>"
			+ rateclass
			+ "</rateclass><rooms>"
			+ rooms
			+ "</rooms><adults>"
			+ adults
			+ "</adults><children>"
			+ children
			+ "</children><channel>Website</channel></staydetail><guestinfo><firstname>"
			+ firstname + "</firstname><lastname>" + lastname
			+ "</lastname><street1>" + street1 + "</street1><holdTime>"
			+ holdTime + "</holdTime><phone>" + phone + "</phone><mobile>"
			+ mobile + "</mobile><email>" + email
			+ "</email></guestinfo><remarks>" + remark
			+ "<remark></remark></remarks><miscinfo><IATA>" + IATA
			+ "</IATA></miscinfo></reservation></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 修改订单
	@Override
	public String modResv(long id, String confnum, String firstname,
			String lastname, String street1, String phone, String email) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"modresv\" language=\"zh\"><reservation><confnum>"
				+ confnum
				+ "</confnum><guestinfo><firstname>"
				+ firstname
				+ "</firstname><lastname>"
				+ lastname
				+ "</lastname><street1>"
				+ street1
				+ "</street1><phone>"
				+ phone
				+ "</phone><email>"
				+ email
				+ "</email></guestinfo><ccinfo><ccname/><cctype/><ccnum/><ccexp/></ccinfo></reservation></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

	// 取消订单
	@Override
	public String cancelresv(long id, String confnum) {
		// TODO Auto-generated method stub
		String xml = "<crsmessage PropID=\""
				+ id
				+ "\" user=\""+USERNAME+"\" pass=\""+PASSWORD+"\" msgtype=\"cancelresv\" nolog=\"1\"><reservation><confnum>"
				+ confnum + "</confnum></reservation></crsmessage>";
		String output = "";
		try {
			output = HTCHotel.postSendHTC(xml);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return output;
	}

}
