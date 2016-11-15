package com.ccservice.b2b2c.atom.cars;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.axis2.AxisFault;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.ccservice.b2b2c.atom.car.EhicarStub;
import com.ccservice.b2b2c.base.carorder.Carorder;

public class CarBook implements ICarBook {
	private String yihaiusername;
	private String yihaipassword;

	public String getYihaiusername() {
		return yihaiusername;
	}

	public void setYihaiusername(String yihaiusername) {
		this.yihaiusername = yihaiusername;
	}

	public String getYihaipassword() {
		return yihaipassword;
	}

	public void setYihaipassword(String yihaipassword) {
		this.yihaipassword = yihaipassword;
	}

	@Override
	public Carorder seachprice(String Stime, String Etime, String Scity,
			String Ecity, String Sprovince, String Eprovince, String Scarstore,
			String Ecarstore, String carcode, String gps) throws Exception {

		Carorder carorder = new Carorder();

		try {

			EhicarStub stub = new EhicarStub();
			EhicarStub.GetSelfDriveOrderPrice selfDriveOrderPrice = new EhicarStub.GetSelfDriveOrderPrice();

			String orderinfo = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";
			orderinfo += "<order>\r\n";
			orderinfo += "<idcardno></idcardno>\r\n";// 身份证号码
			orderinfo += "<fromdate>" + Stime + "</fromdate>\r\n";// 取车时间（格式2009-01-01
																	// 13:00:00）
			orderinfo += "<todate>" + Etime + "</todate>\r\n";// 还车时间（格式2009-01-01
																// 13:00:00）
			orderinfo += "<fromprovince>" + Sprovince + "</fromprovince>\r\n";// 取车省份编号
			orderinfo += "<fromcity>" + Scity + "</fromcity>\r\n";// 取车城市
			orderinfo += "<fromstore>" + Scarstore + "</fromstore>\r\n";// 取车门店编号(如果是送车上门，请设为0)
			orderinfo += "<fromaddress></fromaddress>\r\n";// 送车上门地址（当fromstore=0时用）
			orderinfo += "<fromdistrict></fromdistrict>\r\n";// 送车上门价格区域（如：内环以内）
			orderinfo += "<toprovince>" + Eprovince + "</toprovince>\r\n";// 还车省份编号
			orderinfo += "<tocity>" + Ecity + "</tocity>\r\n";// 还车城市
			orderinfo += "<tostore>" + Ecarstore + "</tostore>\r\n";// 还车门店编号（如果是上门取车，请设为0）
			orderinfo += "<toaddress></toaddress>\r\n";// 上门取车地址（当tostore=0时用）
			orderinfo += "<todistrict></todistrict>\r\n";// 上门取车价格区域（如：内环以内）
			orderinfo += "<cartype>" + carcode + "</cartype>\r\n";// 车型编号
			orderinfo += "<gps>" + gps + "</gps>\r\n";// 是否需要GPS（Y：需要，N：不需要）
			orderinfo += "<comments></comments>\r\n";// 订单备注
			orderinfo += "</order>\r\n";

			selfDriveOrderPrice.setOrder(orderinfo);

			EhicarStub.CheckSoapE checksoap = new EhicarStub.CheckSoapE();
			EhicarStub.CheckSoap cs = new EhicarStub.CheckSoap();
			cs.setAccount(yihaiusername);
			cs.setPassword(yihaipassword);
			checksoap.setCheckSoap(cs);
			try {
				EhicarStub.GetSelfDriveOrderPriceResponse res = stub
						.getSelfDriveOrderPrice(selfDriveOrderPrice, checksoap);

				String sub = res.getGetSelfDriveOrderPriceResult();
				System.out.println(sub);
				Document document = DocumentHelper.parseText(sub);
				org.dom4j.Element root = document.getRootElement();

				List<org.dom4j.Element> listcode = root.elements("returnvalue");
				if (listcode.get(0).elementText("code") != null
						&& listcode.get(0).elementText("code").equals("ACK")) {// 有数据

					List<org.dom4j.Element> listprice = root.elements("price");

					String name = listprice.get(0).elementText("name");
					String price = listprice.get(0).elementText("rate");
					System.out.println("name==" + name + "--基本价格==" + price);
					carorder.setCarname(listprice.get(0).elementText("name"));
					carorder.setCarcode(listprice.get(0).elementText("code"));
					carorder.setJprice(listprice.get(0).elementText("rate"));
					carorder.setInsurancefee(listprice.get(0).elementText(
							"insurancefee"));
					carorder.setServicefee(listprice.get(0).elementText(
							"servicefee"));
					carorder.setPreauthfee(listprice.get(0).elementText(
							"preauthfee"));
					carorder.setGpsfee(listprice.get(0).elementText("gpsfee"));
					carorder.setGps(gps);
					carorder.setMile(listprice.get(0).elementText("mile"));
					carorder.setPickupservicefee(listprice.get(0).elementText(
							"pickupservicefee"));
					carorder.setDropoffservicefee(listprice.get(0).elementText(
							"dropoffservicefee"));
					carorder.setTicketfee(listprice.get(0).elementText(
							"ticketfee"));
					carorder.setPrice(listprice.get(0)
							.elementText("totalprice"));
					carorder.setPredesc("OK");
					return carorder;

				} else {

					carorder.setPredesc("NO");
					carorder.setSpecreq(listcode.get(0).elementText(
							"description"));
					System.out.println("失败...原因=="
							+ listcode.get(0).elementText("description"));
					return carorder;
				}

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return carorder;
	}

	@Override
	public Carorder CreateCarOrder(String Stime, String Etime, String Scity,
			String Ecity, String Sprovince, String Eprovince, String Scarstore,
			String Ecarstore, String carcode, String gps, String nuber)
			throws Exception {

		Carorder carorder = new Carorder();

		try {

			EhicarStub stub = new EhicarStub();
			EhicarStub.AddSelfDriveOrder addSelfDriveOrder = new EhicarStub.AddSelfDriveOrder();

			String orderinfo = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";
			orderinfo += "<order>\r\n";
			orderinfo += "<idcardno>" + nuber + "</idcardno>\r\n";// 身份证号码
			orderinfo += "<fromdate>" + Stime + "</fromdate>\r\n";// 取车时间（格式2009-01-01
																	// 13:00:00）
			orderinfo += "<todate>" + Etime + "</todate>\r\n";// 还车时间（格式2009-01-01
																// 13:00:00）
			orderinfo += "<fromprovince>" + Sprovince + "</fromprovince>\r\n";// 取车省份编号
			orderinfo += "<fromcity>" + Scity + "</fromcity>\r\n";// 取车城市
			orderinfo += "<fromstore>" + Scarstore + "</fromstore>\r\n";// 取车门店编号(如果是送车上门，请设为0)
			orderinfo += "<fromaddress></fromaddress>\r\n";// 送车上门地址（当fromstore=0时用）
			orderinfo += "<fromdistrict></fromdistrict>\r\n";// 送车上门价格区域（如：内环以内）
			orderinfo += "<toprovince>" + Eprovince + "</toprovince>\r\n";// 还车省份编号
			orderinfo += "<tocity>" + Ecity + "</tocity>\r\n";// 还车城市
			orderinfo += "<tostore>" + Ecarstore + "</tostore>\r\n";// 还车门店编号（如果是上门取车，请设为0）
			orderinfo += "<toaddress></toaddress>\r\n";// 上门取车地址（当tostore=0时用）
			orderinfo += "<todistrict></todistrict>\r\n";// 上门取车价格区域（如：内环以内）
			orderinfo += "<cartype>" + carcode + "</cartype>\r\n";// 车型编号
			orderinfo += "<gps>" + gps + "</gps>\r\n";// 是否需要GPS（Y：需要，N：不需要）
			orderinfo += "<comments></comments>\r\n";// 订单备注
			orderinfo += "</order>\r\n";

			addSelfDriveOrder.setOrder(orderinfo);

			EhicarStub.CheckSoapE checksoap = new EhicarStub.CheckSoapE();
			EhicarStub.CheckSoap cs = new EhicarStub.CheckSoap();
			cs.setAccount(yihaiusername);
			cs.setPassword(yihaipassword);
			checksoap.setCheckSoap(cs);
			try {
				EhicarStub.AddSelfDriveOrderResponse res = stub
						.addSelfDriveOrder(addSelfDriveOrder, checksoap);

				String sub = res.getAddSelfDriveOrderResult();
				System.out.println(sub);
				Document document = DocumentHelper.parseText(sub);
				org.dom4j.Element root = document.getRootElement();

				List<org.dom4j.Element> listcode = root.elements("returnvalue");
				if (listcode.get(0).elementText("code") != null
						&& listcode.get(0).elementText("code").equals("ACK")) {// 有数据

					List<org.dom4j.Element> listordercode = root
							.elements("order");

					String ordercode = listordercode.get(0).elementText(
							"confno");
					System.out.println("订单号为==" + ordercode);
					carorder.setWaicode(ordercode);
					carorder.setPredesc("OK");
					return carorder;

				} else {

					System.out.println("失败...原因=="
							+ listcode.get(0).elementText("description"));
					carorder.setWaicode("NOCODE");
					carorder.setPredesc(listcode.get(0).elementText("code"));
					return carorder;
				}

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return carorder;
	}

	@Override
	public String adduserYiHai(String mobile, String password, String username,
			String sex, String email, String nuber, String jtime)
			throws Exception {

		try {

			EhicarStub stub = new EhicarStub();
			EhicarStub.AddSelfDriveUser addSelfDriveUser = new EhicarStub.AddSelfDriveUser();

			String searchinfo = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n";
			searchinfo += "<user>\r\n";
			searchinfo += "<cellphone>" + mobile + "</cellphone>\r\n";// 必填
			searchinfo += "<password>" + mobile + "</password>\r\n";// 必填--网站登录密码（小于16位）
			searchinfo += "<name>" + username + "</name>\r\n";
			searchinfo += "<homephone></homephone>\r\n";
			searchinfo += "<emailaddress>" + email + "</emailaddress>\r\n";// 必填
			searchinfo += "<gender>" + sex + "</gender>\r\n";// 必填
			searchinfo += "<idcardno>" + nuber + "</idcardno>\r\n";// 必填
			searchinfo += "<drivinglicenceno></drivinglicenceno>\r\n";// 驾照号码
			searchinfo += "<drivingissuedate>" + jtime
					+ "</drivingissuedate>\r\n";// 必填---驾驶证颁发日期格式2009-01-01
												// 13:00:00
			searchinfo += "<drivingfileno></drivingfileno>\r\n";// 驾驶证档案编号
			searchinfo += "<province></province>\r\n";// 用户现在住址省份
			searchinfo += "<city></city>\r\n";// 用户现在住址城市
			searchinfo += "<homeaddress></homeaddress>\r\n";// 家庭地址
			searchinfo += "<contactname></contactname>\r\n";// 紧急联系人姓名
			searchinfo += "<contactgender></contactgender>\r\n";// 紧急联系人性别（男或女）
			searchinfo += "<contactcellphone></contactcellphone>\r\n";// 紧急联系人手机
			searchinfo += "<contacthomeaddress></contacthomeaddress>\r\n";// 紧急联系人家庭地址
			searchinfo += "<contacthomephone></contacthomephone>\r\n";// 紧急联系人家庭电话
			searchinfo += "</user>\r\n";

			addSelfDriveUser.setUser(searchinfo);

			EhicarStub.CheckSoapE checksoap = new EhicarStub.CheckSoapE();
			EhicarStub.CheckSoap cs = new EhicarStub.CheckSoap();
			cs.setAccount(yihaiusername);
			cs.setPassword(yihaipassword);
			checksoap.setCheckSoap(cs);
			try {
				EhicarStub.AddSelfDriveUserResponse res = stub
						.addSelfDriveUser(addSelfDriveUser, checksoap);

				String sub = res.getAddSelfDriveUserResult();

				System.out.println(sub);
				Document document = DocumentHelper.parseText(sub);
				org.dom4j.Element root = document.getRootElement();
				List<org.dom4j.Element> listcode = root.elements("returnvalue");
				if (listcode.get(0).elementText("code") != null
						&& listcode.get(0).elementText("code").equals("ACK")) {// 有数据
					System.out.println("注册成功..号码为==" + root.getTextTrim());
					return "OK";
				} else {
					System.out.println("注册失败...原因=="
							+ listcode.get(0).elementText("description"));
					return listcode.get(0).elementText("code");// EMD,邮箱地址已存在..CPD,手机号码已存在...IDD,身份证号码已存在..ERR,其它错误
				}

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	@Override
	public String cancelorder(String ordercode) throws Exception {

		try {

			EhicarStub stub = new EhicarStub();
			EhicarStub.CancelSelfDriveOrder cancelSelfDriveOrder = new EhicarStub.CancelSelfDriveOrder();

			cancelSelfDriveOrder.setConfNo(ordercode);

			EhicarStub.CheckSoapE checksoap = new EhicarStub.CheckSoapE();
			EhicarStub.CheckSoap cs = new EhicarStub.CheckSoap();
			cs.setAccount(yihaiusername);
			cs.setPassword(yihaipassword);
			checksoap.setCheckSoap(cs);
			try {
				EhicarStub.CancelSelfDriveOrderResponse res = stub
						.cancelSelfDriveOrder(cancelSelfDriveOrder, checksoap);

				String sub = res.getCancelSelfDriveOrderResult();

				System.out.println(sub);
				Document document = DocumentHelper.parseText(sub);
				org.dom4j.Element root = document.getRootElement();
				List<org.dom4j.Element> listcode = root.elements("returnvalue");
				if (listcode.get(0).elementText("code") != null
						&& listcode.get(0).elementText("code").equals("ACK")) {// 有数据
					System.out.println("取消成功");
					return "OK";
				} else {

					System.out.println("取消失败...原因=="
							+ listcode.get(0).elementText("description"));
					return "NOOK";
				}

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	@Override
	public String GetYiHaiOrderState(String ordercode) throws Exception {
		// TODO Auto-generated method stub

		try {

			EhicarStub stub = new EhicarStub();
			EhicarStub.GetSelfDriveOrder getSelfDriveOrder = new EhicarStub.GetSelfDriveOrder();

			getSelfDriveOrder.setConfNo("1110678040");

			EhicarStub.CheckSoapE checksoap = new EhicarStub.CheckSoapE();
			EhicarStub.CheckSoap cs = new EhicarStub.CheckSoap();
			cs.setAccount("108097");
			cs.setPassword("9802E7E8133E2498");
			checksoap.setCheckSoap(cs);
			try {
				EhicarStub.GetSelfDriveOrderResponse res = stub
						.getSelfDriveOrder(getSelfDriveOrder, checksoap);

				String sub = res.getGetSelfDriveOrderResult();

				System.out.println(sub);
				Document document = DocumentHelper.parseText(sub);
				org.dom4j.Element root = document.getRootElement();
				List<org.dom4j.Element> listcode = root.elements("returnvalue");
				if (listcode.get(0).elementText("code") != null
						&& listcode.get(0).elementText("code").equals("ACK")) {// 有数据
					System.out.println("成功");

					List<org.dom4j.Element> listorderlist = root
							.elements("orderlist");
					List<org.dom4j.Element> listorder = listorderlist.get(0)
							.elements("order");

					String state = listorder.get(0).elementText("status");

					System.out.println("订单状态==" + state);
					// 4种：预约中（R）,租赁中（L）,已完成（K）,已取消（F）

					return state;

				} else {
					System.out.println("失败...原因=="
							+ listcode.get(0).elementText("description"));

					return "EARR";
				}

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} catch (AxisFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "EARR";
	}

}
