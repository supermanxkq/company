package com.alipay.pos;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.customeruser.Customeruser;
import com.ccservice.b2b2c.base.orderinfo.Orderinfo;
import com.ccservice.b2b2c.base.orderinforc.Orderinforc;
import com.ccservice.b2b2c.base.passenger.Passenger;

/**
 * 
 * @author 分单支付完成结果反馈（MI0015）
 */
public class AlipayPosOrder extends javax.servlet.http.HttpServlet implements
		javax.servlet.Servlet {
	static final long serialVersionUID = 1L;

	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	private static final int DEFAULT_BUFFER_SIZE = 4096;

	private static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDTGmkuheyC/DjpFadnQmFjN42VXIibP9lZI7IfWStKwPohbXPp/Nn0zKbaetWMzYJxRf3yW6RL8q9cJCNsL//xDqeTCR6p5JWDXH8ySH0NSE0fSVOVuaoILim6rNWAOZue4+XhnXcgYJbqsW1AMtPrswoGlC1iPBTs1MAhCXfR4wIDAQAB";

	private static final String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK4jgzvfEnEgiQ+i7prmg3c0a1EELHzXRJpOVJNqnpvsRI8g86LAtJ0INJd3Dff9OXPmlhp51DsYCXL/QBfAU7MuxzeM923gy3Dk1L6mOlJM9bvwWtV9+Sj1pKgASH2zGBY8U5g5doBMBBuNOPMNQ/+aw+pthfHR2sbvGIyYXoNBAgMBAAECgYAgk3wlYyGsEA4T4sMILz9AuYmp6kH4SL0IsMaZnUR2nshkjiGFvM8M4VAVVaxDTBfHWseRxGCzfVenL6Dp0IzXlU3eRCWUIx36xLTIm+xZui8RgXAWtratNAGd6ntQ2sL5TOMF0GFgFFdvkVfnGhgtXJuP/+6LeDakbHm5t2cvAQJBANkIU9YR0gWagkoLjgdS3xfHSgoz5vu3r3+djHqDuBrQBW7ZD0vHWPjYzhul7g8fK8F5Fp5q2wKFaEHEegv5NZkCQQDNZ53DWF1IT/amfRKJleV/qvw0IT+4BBYYbCGIv/D72GSpzquabZP0C7uC9VFKqqTO/+Tbv49G5S8rp6JE4XPpAkBK4qGyyoFSJ6bvD5+ZDVIm7T+x14jKr+2hNeZj25Epxz8oqUKq3gToED7FsXI7y4CYiERysuIQs6Ful/GYsgt5AkBGcNFWq5gZ82pHwD66NlClDrM9AWYDqksVhwedCQ4QoC4tWbXZ2NhTai6dA5okPA8W+gS3I6N0EaayluN8McD5AkEAmRAu8pD34S1KuUL73M1XnKT29Hg0emWQvJZ54cuWxc2kokOUjLbnLlrqxuXnnuyALWwdUBgix83AuaggnhAE4g==";

	private static final String target = "2088801463891028";

	WriteLog writeLog = new WriteLog();

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter out = null;
		try {
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("text/html;charset=UTF-8");
			out = resp.getWriter();
			// 0. 从HTTP请求中抽取报文
			String message = getMessage(req.getInputStream(), "UTF-8");
			// String message = readerFile("d:/request.xml", "UTF-8");
			System.out.println("接收到的报文：" + message);
			if (message.isEmpty()) {
				out.write("error");
				out.flush();
			}
			// 1. 从报文中抽取【Transaction_Header】节点
			String txnHeaderMsg = getXMLMessageByNodeName(message,
					"Transaction_Header");
			// 2. 将【Transaction_Header】节点中的【MAC】节点去除
			String txnHeaderMsgNoMAC = removeXMLNodeByName(txnHeaderMsg, "MAC");
			// 3. 从报文中抽取【Transaction_Body】节点
			String txnBodyMsg = getXMLMessageByNodeName(message,
					"Transaction_Body");
			// 4. 合成最终需要验签的报文
			String signContent = txnHeaderMsgNoMAC + txnBodyMsg;
			// 5. 抽取报文中的数字签名
			String signSignature = getXMLMessageNodeValue(message, "MAC");
			// 6. 报文验签
			boolean result = doCheck(signContent, signSignature, publicKey,
					"UTF-8");
			System.out.println("报文验签结果：" + result);
			// 此处省略实际业务操作。。。

			// start
			// 业务类型

			String transaction_id = getXMLMessageNodeValue(txnHeaderMsg,
					"transaction_id");
			writeLog.write("支付宝POS", getPostName(transaction_id) + ":接收到的报文："
					+ message);
			String resultMessage = "";
			if (true) {
				if (transaction_id.equals("MI0001")) {
					// MI0001 操作员登录
					resultMessage = MI0001(txnHeaderMsg, txnBodyMsg);
				} else if (transaction_id.equals("MI0005")) {
					// MI0005 支付结果反馈
					resultMessage = MI0005(txnHeaderMsg, txnBodyMsg);
				} else if (transaction_id.equals("MI0007")) {
					// MI0007 撤销交易结果反馈
					resultMessage = MI0007(txnHeaderMsg, txnBodyMsg);
				} else if (transaction_id.equals("MI0010")) {
					// MI0010 订单查询
					resultMessage = MI0010(txnHeaderMsg, txnBodyMsg);
				} else if (transaction_id.equals("MI0015")) {
					// MI0015 分单支付完成结果反馈
					resultMessage = MI0015(txnHeaderMsg, txnBodyMsg);
				} else if (transaction_id.equals("MI0016")) {
					// MI0016 分单撤消完成结果反馈
					resultMessage = MI0016(txnHeaderMsg, txnBodyMsg);
				}

				// 0. Mock 业务返回的未加密报文
				String response = resultMessage;
				// String response = readerFile("d:/response.xml", "UTF-8");
				System.out.println("未签名的返回报文：" + response);
				// 1. 从报文中抽取【Transaction_Header】节点
				String txnHeaderMsgRes = getXMLMessageByNodeName(response,
						"Transaction_Header");
				// 2. 从报文中抽取【Transaction_Body】节点
				String txnBodyMsgRes = getXMLMessageByNodeName(response,
						"Transaction_Body");
				// 3. 合成最终需要签名的报文
				String signContentRes = txnHeaderMsgRes + txnBodyMsgRes;

				// 4. 报文签名
				String signature = doSign(signContentRes, privateKey, "UTF-8");
				// 5. 将签名节点【MAC】追加到【Transaction_Header】节点下
				String signResponse = appendXMLNode(response,
						"Transaction_Header", "MAC", signature);
				System.out.println("已签名的返回报文：" + signResponse);
				writeLog.write("支付宝POS", getPostName(transaction_id)
						+ ":未签名的返回报文：" + response);
				writeLog.write("支付宝POS", getPostName(transaction_id)
						+ ":已签名的返回报文：" + signResponse);
				// 6. 报文回复
				out.write(signResponse);
			} else {
				out.write("err");
			}
			out.flush();
		} catch (SignatureException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

	// MI0016
	// 分单撤消完成结果反馈
	private String MI0016(String txnHeaderMsg, String txnBodyMsg) {
		// 用户登录的用户名 (支付宝中这个叫航旅商户员工号)
		// String loginname = getXMLMessageNodeValue(txnHeaderMsg,
		// "delivery_man");
		String orderId = getXMLMessageNodeValue(txnBodyMsg, "order_no");
		float void_order_amt = Float.parseFloat(getXMLMessageNodeValue(
				txnBodyMsg, "void_order_amt"));
		List<Orderinfo> orders = Server.getInstance().getAirService()
				.findAllOrderinfo("where C_ORDERNUMBER = '" + orderId + "'",
						"", -1, 0);
		String response = "<Transaction><Transaction_Header><transaction_id>"
				+ "MI0007</transaction_id><requester>"
				+ "1111111111</requester><target>" + target
				+ "</target><resp_time>"
				+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
				+ "</resp_time><resp_code>";
		if (orders.size() > 0) {
			Orderinfo order = orders.get(0);
			float totalPrice = order.getTotalfuelfee()
					+ order.getTotalairportfee() + order.getTotalticketprice();
			if (order.getOrderstatus() < 3 && totalPrice >= void_order_amt) {
				response += "00</resp_code><resp_msg>成功</resp_msg>";
			} else {
				response += "05</resp_code><resp_msg>不能撤销</resp_msg>";
			}
		}

		response += "</Transaction_Header><Transaction_Body><order_no>"
				+ orderId + "</order_no></Transaction_Body></Transaction>";

		return response;
	}

	// MI0015
	// 分单支付完成结果反馈
	private String MI0015(String txnHeaderMsg, String txnBodyMsg) {
		// 用户登录的用户名 (支付宝中这个叫航旅商户员工号)
		// String loginname = getXMLMessageNodeValue(txnHeaderMsg,
		// "delivery_man");
		// 支付金额
		float order_amt = Float.parseFloat(getXMLMessageNodeValue(txnBodyMsg,
				"order_amt"));
		// 订单号
		String order_no = getXMLMessageNodeValue(txnBodyMsg, "order_no");
		List<Orderinfo> orders = Server.getInstance().getAirService()
				.findAllOrderinfo("where C_ORDERNUMBER = '" + order_no + "'",
						"", -1, 0);
		String response = "<Transaction><Transaction_Header><transaction_id>"
				+ "MI0015</transaction_id><requester>"
				+ "1111111111</requester><target>" + target
				+ "</target><resp_time>"
				+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
				+ "</resp_time><resp_code>";
		if (orders.size() > 0) {
			Orderinfo order = orders.get(0);
			float totalPrice = order.getTotalfuelfee()
					+ order.getTotalairportfee() + order.getTotalticketprice();
			if (totalPrice <= order_amt) {
				order.setPaystatus(1);
				if (order.getOrderstatus() < 3) {
					order.setOrderstatus(2);
				}
				Server.getInstance().getAirService().updateOrderinfo(order);

				response += "00</resp_code><resp_msg>成功</resp_msg>"
						+ "</Transaction_Header><Transaction_Body><order_no>"
						+ order_no
						+ "</order_no></Transaction_Body></Transaction>";
			} else {
				response += "06</resp_code><resp_msg>"
						+ "应收金额不正确</resp_msg></Transaction_Header><Transaction_Body><order_no>"
						+ order_no
						+ "</order_no></Transaction_Body></Transaction>";
			}
		} else {
			response += "03</resp_code><resp_msg>没有检索到数据</resp_msg>"
					+ "</Transaction_Header><Transaction_Body><order_no>"
					+ order_no + "</order_no></Transaction_Body></Transaction>";
		}
		return response;
	}

	// MI0010
	// 订单查询
	private String MI0010(String txnHeaderMsg, String txnBodyMsg) {
		// 用户登录的用户名
		// String loginname = getXMLMessageNodeValue(txnBodyMsg,
		// "delivery_man");
		String orderId = getXMLMessageNodeValue(txnBodyMsg, "order_no");
		List<Orderinfo> orders = Server.getInstance().getAirService()
				.findAllOrderinfo("where C_ORDERNUMBER = '" + orderId + "'",
						"", -1, 0);
		String response = "-1";
		if (orders.size() > 0) {
			Orderinfo order = orders.get(0);
			float totalPrice = order.getTotalfuelfee()
					+ order.getTotalairportfee() + order.getTotalticketprice();
			// 获得乘机人名称
			String passengerNames = getpassengerNames(order.getId());
			response = "<Transaction><Transaction_Header><transaction_id>"
					+ "MI0010</transaction_id><requester>"
					+ "alipay</requester><target>"
					+ target
					+ "</target><resp_time>"
					+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
					+ "</resp_time><resp_code>"
					+ "00</resp_code><ext_attributes><consignee>"
					+ "</consignee><consignee_address>"
					+ order.getContactmobile()
					+ "</consignee_address><consignee_contact>"
					+ "</consignee_contact></ext_attributes></Transaction_Header><Transaction_Body><order_no>"
					+ orderId + "</order_no><amt>" + totalPrice
					+ "</amt><merchant_code></merchant_code><account_keyword>"
					+ "SH</account_keyword><merchant_biz_no>"
					+ "</merchant_biz_no><merchant_biz_type>"
					+ "</merchant_biz_type><desc>" + passengerNames
					+ "</desc></Transaction_Body></Transaction>";
		}
		return response;
	}

	// MI0007
	// 撤销交易结果反馈
	private String MI0007(String txnHeaderMsg, String txnBodyMsg) {
		// 用户登录的用户名 (支付宝中这个叫航旅商户员工号)
		String loginname = getXMLMessageNodeValue(txnHeaderMsg, "delivery_man");
		String orderId = getXMLMessageNodeValue(txnBodyMsg, "order_no");
		List<Orderinfo> orders = Server.getInstance().getAirService()
				.findAllOrderinfo("where C_ORDERNUMBER = '" + orderId + "'",
						"", -1, 0);
		String response = "-1";
		response = "<Transaction><Transaction_Header><transaction_id>"
				+ "MI0007</transaction_id><requester>"
				+ "1111111111</requester><target>" + target
				+ "</target><resp_time>"
				+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
				+ "</resp_time><resp_code>";
		if (orders.size() > 0) {
			Orderinfo order = orders.get(0);
			float totalPrice = order.getTotalfuelfee()
					+ order.getTotalairportfee() + order.getTotalticketprice();
			if (order.getOrderstatus() < 3) {
				response += "00</resp_code><resp_msg>成功</resp_msg>";
			} else {
				response += "05</resp_code><resp_msg>不能撤销</resp_msg>";
			}
		}

		response += "</Transaction_Header><Transaction_Body><order_no>"
				+ orderId + "</order_no></Transaction_Body></Transaction>";

		return response;
	}

	// MI0005
	// 支付结果反馈
	private String MI0005(String txnHeaderMsg, String txnBodyMsg) {
		String loginname = getXMLMessageNodeValue(txnHeaderMsg, "delivery_man");
		String agentId = getXMLMessageNodeValue(txnHeaderMsg,
				"delivery_dept_no");
		String orderId = getXMLMessageNodeValue(txnBodyMsg, "order_no");
		String payPrice = getXMLMessageNodeValue(txnBodyMsg,
				"order_payable_amt");
		String terminal_id = getXMLMessageNodeValue(txnBodyMsg, "terminal_id");
		String order_payable_amt = getXMLMessageNodeValue(txnBodyMsg,
				"order_payable_amt");
		String sql = "SELECT C_MEMBERNAME AS NAME,C_AGENTID AS AGENTID FROM T_CUSTOMERUSER"
				+ " where "
				+ Customeruser.COL_loginname
				+ " = '"
				+ loginname
				+ "'";
		String name = "";
		String sqlAgentid = "";
		List list = Server.getInstance().getSystemService().findMapResultBySql(
				sql, null);
		List<Orderinfo> orders = Server.getInstance().getAirService()
				.findAllOrderinfo("where C_ORDERNUMBER = '" + orderId + "'",
						"", -1, 0);

		Map m = (Map) list.get(0);
		name = m.get("NAME").toString();
		sqlAgentid = m.get("AGENTID").toString();

		String response = "<Transaction><Transaction_Header><transaction_id>MI0005</transaction_id>"
				+ "<requester>1111111111</requester><target>"
				+ target
				+ "</target><resp_time>"
				+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
				+ "</resp_time><resp_code>";
		String acq_type = "-1";
		try {
			acq_type = getXMLMessageNodeValue(txnBodyMsg, "acq_type");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (orders.size() > 0) {
			Orderinfo order = orders.get(0);
			float totalPrice = order.getTotalfuelfee()
					+ order.getTotalairportfee() + order.getTotalticketprice();
			if (!acq_type.equals("-1")) {
				// 分单
				if (acq_type.equals("split")) {
					response += "00</resp_code><resp_msg>"
							+ "成功</resp_msg></Transaction_Header><Transaction_Body><order_no>"
							+ order.getOrdernumber()
							+ "</order_no></Transaction_Body></Transaction>";
					// 合单
				} else if (acq_type.equals("merge")) {
					Orderinforc orderrc = new Orderinforc();
					orderrc.setCreatetime(new Timestamp(new Date().getTime()));
					orderrc.setOrderinfoid(order.getId());
					orderrc.setCustomeruserid(order.getCustomeruserid());
					orderrc.setContent("pos机支付完成,支付人:" + loginname + ",金额:"
							+ order_payable_amt);
					try {
						Server.getInstance().getAirService().createOrderinforc(
								orderrc);
					} catch (SQLException e) {
						e.printStackTrace();
					}
					response += "00</resp_code><resp_msg>"
							+ "成功</resp_msg></Transaction_Header><Transaction_Body><order_no>"
							+ order.getOrdernumber()
							+ "</order_no></Transaction_Body></Transaction>";
				}
			} else {

				if (orders.size() > 0 && list.size() > 0) {
					if (totalPrice <= Float.parseFloat(payPrice)) {
						order.setPaystatus(1);
						if (order.getOrderstatus() < 3) {
							order.setOrderstatus(2);
						}
						Server.getInstance().getAirService().updateOrderinfo(
								order);
						Orderinforc orderrc = new Orderinforc();
						orderrc.setCreatetime(new Timestamp(new Date()
								.getTime()));
						orderrc.setOrderinfoid(order.getId());
						orderrc.setCustomeruserid(order.getCustomeruserid());
						orderrc.setContent("pos机支付完成,支付人:" + loginname + ",金额:"
								+ order_payable_amt);
						try {
							Server.getInstance().getAirService()
									.createOrderinforc(orderrc);
						} catch (SQLException e) {
							e.printStackTrace();
						}
						response += "00</resp_code><resp_msg>"
								+ "成功</resp_msg></Transaction_Header><Transaction_Body><order_no>"
								+ order.getOrdernumber()
								+ "</order_no></Transaction_Body></Transaction>";
					} else {
						response += "06</resp_code><resp_msg>"
								+ "应收金额不正确</resp_msg></Transaction_Header><Transaction_Body><order_no>"
								+ order.getOrdernumber()
								+ "</order_no></Transaction_Body></Transaction>";
					}
				} else {
					response += "08</resp_code><resp_msg>"
							+ "失败</resp_msg></Transaction_Header><Transaction_Body><order_no>"
							+ orderId
							+ "</order_no></Transaction_Body></Transaction>";
				}
			}
		} else {
			if (orderId.indexOf("DP_") == 0) {
				writeLog.write("支付宝充值", orderId + ":金额" + payPrice + ":");
				response += "0</resp_code><resp_msg>"
						+ "成功</resp_msg></Transaction_Header><Transaction_Body><order_no>"
						+ orderId
						+ "</order_no></Transaction_Body></Transaction>";
			} else {
				response += "08</resp_code><resp_msg>"
						+ "失败</resp_msg></Transaction_Header><Transaction_Body><order_no>"
						+ orderId
						+ "</order_no></Transaction_Body></Transaction>";
			}
		}
		return response;
	}

	// MI0001
	// 操作员登录
	private String MI0001(String txnHeaderMsg, String txnBodyMsg) {
		// 用户登录的用户名
		String loginname = getXMLMessageNodeValue(txnBodyMsg, "delivery_man");
		// 用户登录的密码
		String alipayPass = getXMLMessageNodeValue(txnBodyMsg, "password");
		String sql = "SELECT C_LOGPASSWORD AS PASSWORD,C_MEMBERNAME AS NAME,C_AGENTID AS AGENTID FROM T_CUSTOMERUSER"
				+ " where "
				+ Customeruser.COL_loginname
				+ " = '"
				+ loginname
				+ "'";
		// 数据库中的密码
		String loginpass = "";
		String name = "";
		String agentid = "";
		List list = Server.getInstance().getSystemService().findMapResultBySql(
				sql, null);
		if (list.size() > 0) {
			Map m = (Map) list.get(0);
			loginpass = m.get("PASSWORD").toString().toUpperCase();
			loginpass = replace0(loginpass);
			try {
				name = m.get("NAME").toString();
				agentid = m.get("AGENTID").toString();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		String response = "<Transaction><Transaction_Header><transaction_id>"
				+ "MI0001</transaction_id><requester>"
				+ "1111111111</requester><target>" + target
				+ "</target><resp_time>"
				+ new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
				+ "</resp_time><resp_code>";
		System.out.println(alipayPass + ":XML");
		System.out.println(loginpass + ":SQL");
		if (alipayPass.equals(loginpass)) {
			response += "00</resp_code><resp_msg>成功</resp_msg>";
		} else {
			response += "08</resp_code><resp_msg>fail</resp_msg>";
		}

		response += "<ext_attributes><delivery_dept_no>"
				+ agentid
				+ "</delivery_dept_no><delivery_dept>"
				+ agentid
				+ "</delivery_dept></ext_attributes></Transaction_Header><Transaction_Body><delivery_man>"
				+ loginname + "</delivery_man><delivery_name>" + name
				+ "</delivery_name><delivery_zone>"
				+ "BFTD</delivery_zone></Transaction_Body></Transaction>";
		return response;
	}

	public static void main(String[] args) throws SignatureException {
		System.out.println("DP_2012083011408712013".indexOf("DP_") == 0);
	}

	private String getpassengerNames(long id) {
		List<Passenger> passengers = Server.getInstance().getAirService()
				.findAllPassenger("where C_ORDERID = " + id, "", -1, 0);
		String names = "";
		for (int i = 0; i < passengers.size(); i++) {
			names += passengers.get(i).getName() + ",";
		}
		return names;
	}

	/**
	 * 读取本地文件，模拟HTTP请求的报文输入
	 * 
	 * @param fileName
	 *            文件名
	 * @param charset
	 *            编码格式
	 * @return 文件内容
	 */
	private String readerFile(String fileName, String charset) {
		BufferedReader r = null;
		try {
			r = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileName), charset));
			return r.readLine();

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("文件读写错误：" + fileName);
		} finally {
			if (r != null) {
				try {
					r.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("IO错误" + fileName);
				}
			}
		}
	}

	/**
	 * 从报文中截取指定名称的节点内容（包含节点本身）
	 * 
	 * @param message
	 *            报文文本
	 * @param nodeName
	 *            节点名
	 * @return 节点内容
	 */
	private String getXMLMessageByNodeName(String message, String nodeName) {
		String openNode = "<" + nodeName + ">";
		String closeNode = "</" + nodeName + ">";
		int startIdx = message.indexOf(openNode);
		int endIdx = message.indexOf(closeNode) + closeNode.length();
		if (startIdx == -1 || endIdx == -1) {
			throw new RuntimeException("找不到对应的节点名：" + nodeName);
		} else {
			return message.substring(startIdx, endIdx);
		}
	}

	/**
	 * 移除报文中指定节点名的节点
	 * 
	 * @param message
	 *            报文文本
	 * @param nodeName
	 *            节点名
	 * @return 移除节点后的报文
	 */
	private String removeXMLNodeByName(String message, String nodeName) {
		String openNode = "<" + nodeName + ">";
		String closeNode = "</" + nodeName + ">";
		int startIdx = message.indexOf(openNode);
		int endIdx = message.indexOf(closeNode) + closeNode.length();
		if (startIdx == -1 || endIdx == -1) {
			throw new RuntimeException("找不到对应的节点名：" + nodeName);
		} else {
			return message.substring(0, startIdx) + message.substring(endIdx);
		}
	}

	/**
	 * 从报文中截取指定名称的节点值（不包含节点本身）
	 * 
	 * @param message
	 *            报文文本
	 * @param nodeName
	 *            节点名
	 * @return 节点值
	 */
	private String getXMLMessageNodeValue(String message, String nodeName) {
		String nodeXML = getXMLMessageByNodeName(message, nodeName);
		int startIdx = nodeXML.indexOf('>') + 1;
		int endIdx = nodeXML.lastIndexOf('<');
		if (startIdx == -1 || endIdx == -1) {
			throw new RuntimeException("找不到对应的节点名：" + nodeName);
		} else {
			return nodeXML.substring(startIdx, endIdx);
		}
	}

	/**
	 * 在报文中指定名称的节点下追加一个文本节点
	 * 
	 * @param message
	 *            报文文本
	 * @param parentName
	 *            父节点名
	 * @param nodeName
	 *            追加的节点名
	 * @param nodeValue
	 *            追加的节点值
	 * @return 追加节点后的报文
	 */
	private String appendXMLNode(String message, String parentName,
			String nodeName, String nodeValue) {
		String closeNode = "</" + parentName + ">";
		int index = message.indexOf(closeNode);
		if (index == -1) {
			throw new RuntimeException("找不到对应的节点名：" + parentName);
		} else {
			String nodeXML = "<" + nodeName + ">" + nodeValue + "</" + nodeName
					+ ">";
			return message.substring(0, index) + nodeXML
					+ message.substring(index);
		}

	}

	/**
	 * 从输入流中读取报文
	 * 
	 * @param inputStream
	 *            输入流
	 * @param charset
	 *            编码格式
	 * @return 报文
	 */
	private String getMessage(InputStream inputStream, String charset) {
		try {
			BufferedReader breader = new BufferedReader(new InputStreamReader(
					inputStream, charset));
			StringBuilder sbuilder = new StringBuilder();
			String line = "";
			while ((line = breader.readLine()) != null) {
				sbuilder.append(line + "\n");
			}
			return sbuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * <pre>
	 * 对原始数据进行签名
	 * </pre>
	 * 
	 * @param content
	 *            签名内容
	 * @param privateKey
	 *            私钥
	 * @param charset
	 *            字符集
	 * @return
	 * @throws SignatureException
	 */
	public String doSign(String content, String privateKey, String charset)
			throws SignatureException {
		try {
			PrivateKey priKey = this.getPrivateKeyFromPKCS8("RSA",
					new ByteArrayInputStream(privateKey.getBytes()));

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initSign(priKey);
			signature.update(this.getContentBytes(content, charset));
			byte[] signed = signature.sign();
			return new String(Base64.encodeBase64(signed));
		} catch (Exception e) {
			throw new SignatureException("RSA签名[content = " + content
					+ "; charset = " + charset + "]发生异常!", e);
		}
	}

	/**
	 * <pre>
	 * 校验签名
	 * </pre>
	 * 
	 * @param content
	 *            校验签名内容
	 * @param sign
	 *            签名字符串
	 * @param publicKey
	 *            公钥
	 * @param charset
	 *            字符集
	 * @return
	 * @throws SignatureException
	 */
	public boolean doCheck(String content, String sign, String publicKey,
			String charset) throws SignatureException {
		try {
			PublicKey pubKey = this.getPublicKeyFromX509("RSA",
					new ByteArrayInputStream(publicKey.getBytes()));

			java.security.Signature signature = java.security.Signature
					.getInstance(SIGN_ALGORITHMS);

			signature.initVerify(pubKey);
			signature.update(getContentBytes(content, charset));

			return signature.verify(Base64.decodeBase64(sign.getBytes()));
		} catch (Exception e) {
			throw new SignatureException("RSA验证签名[content = " + content
					+ "; charset = " + charset + "; signature = " + sign
					+ "]发生异常!", e);
		}
	}

	/**
	 * 将输入流中的字节码转换成私钥对象
	 * 
	 * @param algorithm
	 *            秘钥生成算法
	 * @param ins
	 *            输入流
	 * @return 私钥对象
	 * @throws NoSuchAlgorithmException
	 *             非法生成算法异常
	 */
	private PrivateKey getPrivateKeyFromPKCS8(String algorithm, InputStream ins)
			throws NoSuchAlgorithmException {
		if (ins == null || StringUtils.isBlank(algorithm)) {
			return null;
		}

		try {
			KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

			byte[] encodedKey = this.readText(ins).getBytes();
			// 先base64解码
			encodedKey = Base64.decodeBase64(encodedKey);
			return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(
					encodedKey));
		} catch (IOException ex) {
			// TODO logger.error("获取私钥时发生异常：", ex);
		} catch (InvalidKeySpecException ex) {
			// TODO logger.error("获取私钥时发生异常：", ex);
		}

		return null;
	}

	/**
	 * 将输入流中的字节码转换成公钥对象
	 * 
	 * @param algorithm
	 *            秘钥生成算法
	 * @param ins
	 *            输入流
	 * @return 公钥对象
	 * @throws NoSuchAlgorithmException
	 *             非法生成算法异常
	 */
	private PublicKey getPublicKeyFromX509(String algorithm, InputStream ins)
			throws NoSuchAlgorithmException {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(algorithm);

			StringWriter writer = new StringWriter();
			io(new InputStreamReader(ins), writer, -1);

			byte[] encodedKey = writer.toString().getBytes();

			// 先base64解码
			encodedKey = Base64.decodeBase64(encodedKey);

			return keyFactory
					.generatePublic(new X509EncodedKeySpec(encodedKey));
		} catch (IOException ex) {
			// TODO logger.error("获取公钥时发生异常：", ex);
		} catch (InvalidKeySpecException ex) {
			// TODO logger.error("获取公钥时发生异常：", ex);
		}

		return null;
	}

	/**
	 * 将字符串转换成字节码
	 * 
	 * @param content
	 *            字符串
	 * @param charset
	 *            编码格式
	 * @return 字节码
	 * @throws UnsupportedEncodingException
	 *             非法编码异常
	 */
	private byte[] getContentBytes(String content, String charset)
			throws UnsupportedEncodingException {
		if (StringUtils.isEmpty(charset)) {
			return content.getBytes();
		}
		return content.getBytes(charset);
	}

	/**
	 * 将指定输入流的所有文本全部读出到一个字符串中.
	 * 
	 * @param in
	 *            要读取的输入流
	 * @return 从输入流中取得的文本
	 * @throws IOException
	 *             输入输出异常
	 */
	private String readText(InputStream in) throws IOException {
		StringWriter writer = new StringWriter();
		this.io(new InputStreamReader(in), writer, DEFAULT_BUFFER_SIZE);
		return writer.toString();
	}

	/**
	 * 从输入流读取内容, 写入到输出流中. 使用指定大小的缓冲区.
	 * 
	 * @param in
	 *            输入流
	 * @param out
	 *            输出流
	 * @param bufferSize
	 *            缓冲区大小(字符数)
	 * @throws IOException
	 *             输入输出异常
	 */
	private void io(Reader in, Writer out, int bufferSize) throws IOException {
		if (bufferSize == -1) {
			bufferSize = DEFAULT_BUFFER_SIZE;
		}
		char[] buffer = new char[bufferSize];
		int amount;
		while ((amount = in.read(buffer)) >= 0) {
			out.write(buffer, 0, amount);
		}
	}

	private String getPostName(String code) {
		String result = "-1";
		if (code.equals("MI0001")) {
			result = "操作员登录";
		}
		if (code.equals("MI0005")) {
			result = "支付结果反馈";
		}
		if (code.equals("MI0007")) {
			result = "撤销交易结果反馈";
		}
		if (code.equals("MI0010")) {
			result = "订单查询";
		}
		if (code.equals("MI0015")) {
			result = "分单支付完成结果反馈";
		}
		if (code.equals("MI0016")) {
			result = "分单撤消完成结果反馈";
		}
		return result + ":";
	}

	/**
	 * 员工登录密码，32位md5摘要（大写）。报文中密码md5格式因为POS机上对密码加密的时候，是采用不对齐的方式，就是将奇数位的0全部去掉。例如：0A2B0C，去0后的结果是A2BC。
	 * @param pwd
	 * @return
	 */
	private String replace0(String pwd) {
		String result = "";
		char[] pwds = new char[pwd.length()];
		for (int i = 0; i < pwd.length(); i++) {
			pwds[i] = pwd.charAt(i);
			if (i % 2 == 0 && pwds[i] == '0') {
				
			} else {
				result += pwds[i];
			}
		}
		return result;
	}
}