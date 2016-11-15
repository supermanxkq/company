package com.insurance;



import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.tenpay.util.MD5Util;

import client.IcInterFaceStub;

/**
 * 天衢保险接口
 * 
 * @author 贾建磊
 * 
 */
public class TianQuInsurance implements ITianQuInsurance {

	/**
	 * 创建订单
	 * 
	 * @param ainame
	 *            投保人姓名
	 * @param aimobile
	 *            投保人电话
	 * @param aiaddress
	 *            投保人地址 可以为空
	 * @param aiidcard
	 *            证件类型:1 身份证 2 护照 3 军人证 4 港台同胞证 5 其它证件
	 * @param aiidnumber
	 *            证件号
	 * @param aieffectivedate
	 *            生效日期
	 * @param aiemail
	 *            电子邮件
	 * @param aisex
	 *            投保人性别 -1保密 1 男 0 女
	 * @param aiflightnumber
	 *            航班号
	 * @return
	 */
	@Override
	public String createOrder(String ainame, String aimobile, String aiaddress,
			String aiidcard, String aiidnumber, String aieffectivedate,
			String aiemail, String aisex, String aiflightnumber) {
		try {
			String xmldoc = "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
			xmldoc += "<OrderInfo>";
			xmldoc += "<Function>CreateOrder</Function>";
			xmldoc += "<UserInfo>";
			xmldoc += "<UI_Name>j09000006</UI_Name>";
			xmldoc += "</UserInfo>";
			xmldoc += "<ApplicantInfo>";
			xmldoc += "<AI_Name>" + ainame + "</AI_Name>";
			xmldoc += "<AI_Mobile>" + aimobile + "</AI_Mobile>";
			xmldoc += "<AI_Address>" + aiaddress + "</AI_Address>";
			xmldoc += "<AI_IDCard>" + aiidcard + "</AI_IDCard>";
			xmldoc += "<AI_IDNumber>" + aiidnumber + "</AI_IDNumber>";
			xmldoc += "<AI_EffectiveDate>" + aieffectivedate
					+ "</AI_EffectiveDate>";
			xmldoc += "<AI_IcId>1</AI_IcId>";
			xmldoc += "<AI_IccId>35</AI_IccId>";
			xmldoc += "<AI_Email>" + aiemail + "</AI_Email>";
			xmldoc += "<AI_Sex>" + aisex + "</AI_Sex>";
			xmldoc += "<AI_FlightNumber>" + aiflightnumber
					+ "</AI_FlightNumber>";
			xmldoc += "<AI_HeirType>0</AI_HeirType>";
			xmldoc += "<AI_HeirNum>1</AI_HeirNum>";
			xmldoc += "</ApplicantInfo>";
			xmldoc += "</OrderInfo>";
			WriteLog.write("tianquInsurance", "0:CreateOrder");
			WriteLog.write("tianquInsurance", "1:"+xmldoc);
			IcInterFaceStub.IcInterface createOrderIcInterface = new IcInterFaceStub.IcInterface();
			createOrderIcInterface.setXmldoc(xmldoc);
			createOrderIcInterface.setStrFunction("CreateOrder");
			String tempsign = "3l23k23k23k1kf943fd84h5f7" + xmldoc;
			WriteLog.write("tianquInsurance", "2:"+tempsign);
			String sign = MD5Util.MD5Encode(tempsign,"GB2312");
			createOrderIcInterface.setSign(sign);
			WriteLog.write("tianquInsurance", "3:"+sign);

			IcInterFaceStub stub = new IcInterFaceStub();
			IcInterFaceStub.IcInterfaceResponse response = stub
					.icInterface(createOrderIcInterface);
			String result = response.getIcInterfaceResult();
			WriteLog.write("tianquInsurance", "4:"+result);
			Document document = DocumentHelper.parseText(result);
			Element root = document.getRootElement();
			Element resultElement = root.element("Result");
			if (resultElement.getTextTrim() != null
					&& resultElement.getTextTrim().equals("SUCESS")) {
				String ioId = root.element("Ioid").getTextTrim();
				return ioId;
			} else {
				String messageInfo = root.element("MessageInfo").getTextTrim();
				System.out.println("messageInfo==" + messageInfo);
				return "FAIL";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "-1";
	}

	
	
	/**
	 * 查询订单详细
	 * 
	 * @param orderid
	 *            订单号
	 * @return
	 */
	@Override
	public String orderInfo(String orderid) {
		try {
			String xmldoc = "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
			xmldoc += "<OrderInfo>";
			xmldoc += "<Function>OrderInfo</Function>";
			xmldoc += "<UserInfo>";
			xmldoc += "<UI_Name>j09000006</UI_Name>";
			xmldoc += "</UserInfo>";
			xmldoc += "<OrderId>" + orderid + "</OrderId>";
			xmldoc += "</OrderInfo>";

			IcInterFaceStub.IcInterface orderInfoIcInterface = new IcInterFaceStub.IcInterface();
			orderInfoIcInterface.setXmldoc(xmldoc);
			orderInfoIcInterface.setStrFunction("OrderInfo");
			String tempsign = "3l23k23k23k1kf943fd84h5f7" + xmldoc;
			WriteLog.write("tianquInsurance", "2:"+tempsign);
			String sign = MD5Util.MD5Encode(tempsign,"GB2312");
			orderInfoIcInterface.setSign(sign);
			WriteLog.write("tianquInsurance", "3:"+sign);

			IcInterFaceStub stub = new IcInterFaceStub();
			IcInterFaceStub.IcInterfaceResponse response = stub
					.icInterface(orderInfoIcInterface);
			String result = response.getIcInterfaceResult();
			if (result != null) {
				String strJson = "{";
				Document document = DocumentHelper.parseText(result);
				Element root = document.getRootElement();
				Element applicantInfo = root.element("ApplicantInfo");
				strJson += "\"AI_Name\":"
						+ applicantInfo.elementText("AI_Name") + ",";
				strJson += "\"AI_Mobile\":"
						+ applicantInfo.elementText("AI_Mobile") + ",";
				strJson += "\"AI_Address\":"
						+ applicantInfo.elementText("AI_Address") + ",";
				strJson += "\"AI_IDCard\":"
						+ applicantInfo.elementText("AI_IDCard") + ",";
				strJson += "\"AI_IDNumber\":"
						+ applicantInfo.elementText("AI_IDNumber") + ",";
				strJson += "\"AI_EffectiveDate\":"
						+ applicantInfo.elementText("AI_EffectiveDate") + ",";
				strJson += "\"AI_IcId\":"
						+ applicantInfo.elementText("AI_IcId") + ",";
				strJson += "\"AI_IccId\":"
						+ applicantInfo.elementText("AI_IccId") + ",";
				strJson += "\"AI_Email\":"
						+ applicantInfo.elementText("AI_Email") + ",";
				strJson += "\"AI_Sex\":" + applicantInfo.elementText("AI_Sex")
						+ ",";
				strJson += "\"AI_FlightNumber\":"
						+ applicantInfo.elementText("AI_FlightNumber") + ",";
				strJson += "\"AI_HeirType\":"
						+ applicantInfo.elementText("AI_HeirType") + ",";
				strJson += "\"AI_Status\":"
						+ applicantInfo.elementText("AI_Status") + ",";
				strJson += "\"AI_CreateDate\":"
						+ applicantInfo.elementText("AI_CreateDate") + ",";
				strJson += "\"AI_IoNumber\":"
						+ applicantInfo.elementText("AI_IoNumber") + ",";
				strJson += "\"AI_VNumber\":"
						+ applicantInfo.elementText("AI_VNumber") + "";
				strJson += "}";
				return strJson;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 取消订单
	 * 
	 * @param orderid
	 *            订单号
	 * @return
	 */
	@Override
	public String cancleOrder(String orderid) {
		try {
			String xmldoc = "<?xml version=\"1.0\" encoding=\"GB2312\"?>";
			xmldoc += "<OrderInfo>";
			xmldoc += "<Function>CancleOrder</Function>";
			xmldoc += "<UserInfo>";
			xmldoc += "<UI_Name>j09000006</UI_Name>";
			xmldoc += "</UserInfo>";
			xmldoc += "<OrderId>" + orderid + "</OrderId>";
			xmldoc += "</OrderInfo>";

			IcInterFaceStub.IcInterface cancleOrderIcInterface = new IcInterFaceStub.IcInterface();
			cancleOrderIcInterface.setXmldoc(xmldoc);
			cancleOrderIcInterface.setStrFunction("CancleOrder");
			String tempsign = "3l23k23k23k1kf943fd84h5f7" + xmldoc;
			WriteLog.write("tianquInsurance", "2:"+tempsign);
			String sign = MD5Util.MD5Encode(tempsign,"GB2312");
			cancleOrderIcInterface.setSign(sign);
			WriteLog.write("tianquInsurance", "3:"+sign);

			IcInterFaceStub stub = new IcInterFaceStub();
			IcInterFaceStub.IcInterfaceResponse response = stub
					.icInterface(cancleOrderIcInterface);
			String result = response.getIcInterfaceResult();
			Document document = DocumentHelper.parseText(result);
			Element root = document.getRootElement();
			Element resultElement = root.element("Result");
			if (resultElement.getTextTrim() != null
					&& resultElement.getTextTrim().equals("SUCESS")) {
				return resultElement.getTextTrim();
			} else {
				String messageInfo = root.element("MessageInfo").getTextTrim();
				System.out.println("messageInfo==" + messageInfo);
				return "FAIL";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 第一次投保
	 * 
	 * @param orderid
	 *            订单号
	 * @return
	 */
	@Override
	public String payOrder(String orderid) {
		try {
			String xmldoc = "<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n";
			xmldoc += "<OrderInfo>\r\n";
			xmldoc += "<Function>PayOrder</Function>\r\n";
			xmldoc += "<UserInfo>\r\n";
			xmldoc += "<UI_Name>j09000006</UI_Name>\r\n";
			xmldoc += "</UserInfo>\r\n";
			xmldoc += "<OrderId>" + orderid + "</OrderId>\r\n";
			xmldoc += "</OrderInfo>\r\n";

			IcInterFaceStub.IcInterface payOrderIcInterface = new IcInterFaceStub.IcInterface();
			payOrderIcInterface.setXmldoc(xmldoc);
			payOrderIcInterface.setStrFunction("PayOrder");
			String tempsign = "3l23k23k23k1kf943fd84h5f7" + xmldoc;
			WriteLog.write("tianquInsurance", "2:"+tempsign);
			String sign = MD5Util.MD5Encode(tempsign,"GB2312");
			payOrderIcInterface.setSign(sign);
			WriteLog.write("tianquInsurance", "3:"+sign);

			IcInterFaceStub stub = new IcInterFaceStub();
			IcInterFaceStub.IcInterfaceResponse response = stub
					.icInterface(payOrderIcInterface);
			String result = response.getIcInterfaceResult();
			Document document = DocumentHelper.parseText(result);
			Element root = document.getRootElement();
			Element resultElement = root.element("Result");
			if (resultElement.getTextTrim() != null
					&& resultElement.getTextTrim().equals("SUCESS")) {
				String insurancePolicyNO = root.element("MessageInfo")
						.getTextTrim();// 保单号
				return insurancePolicyNO;
			} else {
				String messageInfo = root.element("MessageInfo").getTextTrim();
				System.out.println("messageInfo==" + messageInfo);
				return "FAIL";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "FAIL";
	}

	/**
	 * 退保
	 * 
	 * @param orderNumber
	 *            保单号
	 * @return
	 */
	@Override
	public String backOrder(String orderNumber) {
		try {
			String xmldoc = "<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n";
			xmldoc += "<OrderInfo>\r\n";
			xmldoc += "<Function>BackOrder</Function>\r\n";
			xmldoc += "<UserInfo>\r\n";
			xmldoc += "<UI_Name>j09000006</UI_Name>\r\n";
			xmldoc += "</UserInfo>\r\n";
			xmldoc += "<OrderNumber>" + orderNumber + "</OrderNumber>\r\n";
			xmldoc += "</OrderInfo>\r\n";

			IcInterFaceStub.IcInterface backOrderIcInterface = new IcInterFaceStub.IcInterface();
			backOrderIcInterface.setXmldoc(xmldoc);
			backOrderIcInterface.setStrFunction("BackOrder");
			String tempsign = "3l23k23k23k1kf943fd84h5f7" + xmldoc;
			WriteLog.write("tianquInsurance", "2:"+tempsign);
			String sign = MD5Util.MD5Encode(tempsign,"GB2312");
			backOrderIcInterface.setSign(sign);
			WriteLog.write("tianquInsurance", "3:"+sign);

			IcInterFaceStub stub = new IcInterFaceStub();
			IcInterFaceStub.IcInterfaceResponse response = stub
					.icInterface(backOrderIcInterface);
			String result = response.getIcInterfaceResult();
			WriteLog.write("tianquInsurance", "3:"+result);
			Document document = DocumentHelper.parseText(result);
			Element root = document.getRootElement();
			Element resultElement = root.element("Result");
			if (resultElement.getTextTrim() != null
					&& resultElement.getTextTrim().equals("SUCESS")) {
				return resultElement.getTextTrim();
			} else {
				String messageInfo = root.element("MessageInfo").getTextTrim();
				System.out.println("messageInfo==" + messageInfo);
				return "FAIL";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 投保失败时再次投保
	 * 
	 * @param orderid
	 *            订单号
	 * @param ainame
	 *            投保人姓名
	 * @param aisex
	 *            投保人性别 -1保密 1 男 0 女
	 * @param aiidnumber
	 *            证件号
	 * @param aimobile
	 *            投保人电话
	 * @param aiemail
	 *            电子邮件
	 * @param aiaddress
	 *            投保人地址 可以为空
	 * @param aiflightnumber
	 *            航班号
	 * @return
	 */
	@Override
	public String payOrderAgain(String orderid, String ainame, String aisex,
			String aiidnumber, String aimobile, String aiemail,
			String aiaddress, String aiflightnumber) {
		try {
			String xmldoc = "<?xml version=\"1.0\" encoding=\"GB2312\"?>\r\n";
			xmldoc += "<OrderInfo>\r\n";
			xmldoc += "<Function>PayOrderAgain</Function>\r\n";
			xmldoc += "<UserInfo>\r\n";
			xmldoc += "<UI_Name>j09000006</UI_Name>\r\n";
			xmldoc += "</UserInfo>\r\n";
			xmldoc += "<OrderId>" + orderid + "</OrderId>\r\n";
			xmldoc += "<ApplicantInfo>\r\n";
			xmldoc += "<AI_Name>" + ainame + "</AI_Name>\r\n";
			xmldoc += "<AI_Sex>" + aisex + "</AI_Sex>\r\n";
			xmldoc += "<AI_IDNumber>" + aiidnumber + "</AI_IDNumber>\r\n";
			xmldoc += "<AI_Mobile>" + aimobile + "</AI_Mobile>\r\n";
			xmldoc += "<AI_Email>" + aiemail + "</AI_Email>\r\n";
			xmldoc += "<AI_Address>" + aiaddress + "</AI_Address>\r\n";
			xmldoc += "<AI_FlightNumber>" + aiflightnumber
					+ "</AI_FlightNumber>\r\n";
			xmldoc += "</ApplicantInfo>\r\n";
			xmldoc += "<HeirFristInfo>\r\n";
			xmldoc += "<HFI_Name></HFI_Name>\r\n";
			xmldoc += "<HFI_IDNumber></HFI_IDNumber>\r\n";
			xmldoc += "<HFI_Phone></HFI_Phone>\r\n";
			xmldoc += "<HFI_Relation></HFI_Relation>\r\n";
			xmldoc += "</HeirFristInfo>\r\n";
			xmldoc += "<HeirSecondInfo>\r\n";
			xmldoc += "<HSI_Name></HSI_Name>\r\n";
			xmldoc += "<HSI_IDNumber></HSI_IDNumber>\r\n";
			xmldoc += "<HSI_Phone></HSI_Phone>\r\n";
			xmldoc += "<HSI_Relation></HSI_Relation>\r\n";
			xmldoc += "</HeirSecondInfo>\r\n";
			xmldoc += "</OrderInfo>\r\n";

			IcInterFaceStub.IcInterface payOrderAgainIcInterface = new IcInterFaceStub.IcInterface();
			payOrderAgainIcInterface.setXmldoc(xmldoc);
			payOrderAgainIcInterface.setStrFunction("PayOrderAgain");
			String tempsign = "3l23k23k23k1kf943fd84h5f7" + xmldoc;
			WriteLog.write("tianquInsurance", "2:"+tempsign);
			String sign = MD5Util.MD5Encode(tempsign,"GB2312");
			payOrderAgainIcInterface.setSign(sign);
			WriteLog.write("tianquInsurance", "3:"+sign);

			IcInterFaceStub stub = new IcInterFaceStub();
			IcInterFaceStub.IcInterfaceResponse response = stub
					.icInterface(payOrderAgainIcInterface);
			String result = response.getIcInterfaceResult();
			Document document = DocumentHelper.parseText(result);
			Element root = document.getRootElement();
			Element resultElement = root.element("Result");
			if (resultElement.getTextTrim() != null
					&& resultElement.getTextTrim().equals("SUCESS")) {
				String insurancePolicyNO = root.element("MessageInfo")
						.getTextTrim();// 保单号
				return insurancePolicyNO;
			} else {
				String messageInfo = root.element("MessageInfo").getTextTrim();
				System.out.println("messageInfo==" + messageInfo);
				return "FAIL";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		TianQuInsurance t = new TianQuInsurance();
		t.backOrder("21130341261707898");
	}
}
