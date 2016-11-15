package com.ccservice.b2b2c.atom.pay;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.ccservice.b2b2c.atom.refund.helper.Refundhelper;
import com.ccservice.b2b2c.atom.refund.helper.Refundinfo;
import com.ccservice.b2b2c.ben.Refundtrade;
import com.pay.config.Chinapnrconfig;

/**
 * @author Administrator 汇付天下退款。 汇付天下仅支持单笔退款.
 * 
 */
public class Chinapnrrefund extends RefundSupport implements Refund {
	public static DateFormat dateformat = new SimpleDateFormat("yyyyMMddhhmmss");
	static final long serialVersionUID = 1L;

	private static final String gateurl = "http://mas.chinapnr.com/gao/entry.do?";
	private static final String Version = "10";// 版本号
	private static final String MerId = Chinapnrconfig.getInstance()
			.getPartnerID();// 商户号
	private static final String CmdId = "Refund";// 普通退款 Refund 供应商退款给平台
													// Refund1
	// 平台退款给分销商 Refund2
	static DecimalFormat format = (DecimalFormat) NumberFormat.getInstance();

	Refundhelper refundhelper = null;
	static Logger logger = Logger.getLogger(Chinapnrrefund.class
			.getSimpleName());

	public Chinapnrrefund(HttpServletRequest request,
			HttpServletResponse response, Refundhelper refundhelper) {
		super(request, response, refundhelper);
		format.applyPattern("###0.00");
	}

	@Override
	public void refund() {
		Refundinfo refundinfo = refundhelper.getRefundinfos().get(0);// 汇付天下仅支持单笔退款。
		String DivDetails = getRoyalty_parameters(refundinfo);// /;//分账明细
																					// 可选
		Refundtrade refundtrade = this.createRefundtrade(DivDetails,
				refundhelper.getRefundinfos());
		Date date = new Date(System.currentTimeMillis());
		String OrdId = dateformat.format(date) + refundtrade.getId() + "";// 订单号
		String RefAmt = format.format(refundinfo.getRefundprice());// 订单退款的总金额，应小于或等于各分账退款金额总和
		String OldOrdId = refundhelper.getOldOrdId();// 原始订单号需要操作（订单退款，分账变更）的原支付成功的订单号
		String BgRetUrl = "http://" + request.getServerName() + ":"
				+ request.getServerPort()
				+ "/cn_interface/ChinapnrrefundHandle";// 订单支付时，商户后台应答地址 可选
		String BenDetails = "";// 可选 退款入账分润明细串
		// 格式为：Oper_Id1:ref_amt1;oper_Id2:ref_amt2;例如一笔100元的工行退款有入账分润串:usr1:2.00;
		// usr2:3.00;代表给买家退款入账95元，给usr1分润入账2元，给usr2分润入账3元。
		String IsCrCover = "";// 可选 是否需要退款垫资 ‘Y’表示该笔退款卖家和商户都垫资。
		// ‘S’表示该笔退款仅卖家退款垫资。‘M’表示该笔退款仅商户退款垫资。
		// ‘N’表示该笔退款不需要退款垫资。
		String ChkValue = "";// 签名
		// 签名
		String MerKeyFile = "";
		try {
			MerKeyFile = URLDecoder.decode(Chinapnrrefund.class
					.getClassLoader().getResource("/").getPath(), "UTF-8")
					+ "MerPrK" + MerId + ".key";
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // 商户私钥文件路径 请将MerPrK510010.key改为你的私钥文件名称
		System.out.println("MerKeyFile:" + MerKeyFile);
		String MerData = Version + CmdId + MerId + DivDetails + RefAmt + OrdId
				+ OldOrdId + BgRetUrl;
		System.out.println(MerData);
		SecureLink sl = new SecureLink();
		int ret = sl.SignMsg(MerId, MerKeyFile, MerData);
		System.out.println("ret：" + ret);
		if (ret != 0) {
			logger
					.error(this.refundhelper.getOrdernumber() + "签名错误 ret="
							+ ret);
			return;
		}
		ChkValue = sl.getChkValue();
		System.out.println("ChkValue:" + ChkValue);
		Map<String, String> map = new HashMap<String, String>();
		map.put("Version", Version);
		map.put("CmdId", CmdId);
		map.put("MerId", MerId);
		map.put("OrdId", OrdId);
		map.put("DivDetails", DivDetails);
		map.put("RefAmt", RefAmt);
		map.put("OldOrdId", OldOrdId);
		map.put("BgRetUrl", BgRetUrl);
		map.put("BenDetails", BenDetails);
		map.put("IsCrCover", IsCrCover);
		map.put("ChkValue", ChkValue);
		String param = createLinkString(map);
		try {
			URL neturl = new URL(gateurl + param);
			HttpURLConnection connection = (HttpURLConnection) neturl
					.openConnection();
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.connect();
			BufferedInputStream buffer = new BufferedInputStream(connection
					.getInputStream());
			byte[] bytes = new byte[2048];
			ByteArrayOutputStream byteout = new ByteArrayOutputStream();
			int i = 0;
			while ((i = buffer.read(bytes)) > 0) {
				byteout.write(bytes, 0, i);
			}
			buffer.close();
			connection.disconnect();
			String str = new String(bytes, "GBK");
			logger
					.error(this.refundhelper.getOrdernumber() + "申请退款返回结果："
							+ str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// return "";
	}

	private static String createLinkString(Map<String, String> params) {
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String prestr = "";

		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (value == null || value.length() == 0) {
				continue;
			}
			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}
		System.out.print("拼接后的字符串：" + prestr);
		return prestr;
	}

	/**
	 * 
	 * 退款明细 对于退款：填写退款的分账信息，其格式和支付时填写的一样。对于CmdId=Refund，为可选
	 * 
	 * @param agentroya
	 *            各代理分账明细
	 * @return 分润退款字符串
	 */
	public static String getRoyalty_parameters(Refundinfo refundinfo) {
		String royaltysb = "";
		if (refundinfo.getRoyalty_parameters() != null) {
			Iterator<Map.Entry<String, Float>> royiterator = refundinfo
					.getRoyalty_parameters().entrySet().iterator();
			while (royiterator.hasNext()) {
				Map.Entry<String, Float> entery = royiterator.next();

				String account = entery.getKey();
				float money = entery.getValue();
				if (royiterator.hasNext()) {
					royaltysb = ";" + "Agent:" + account + ":"
							+ format.format(money) + royaltysb;
				} else {
					royaltysb = "Agent:" + account + ":" + format.format(money)
							+ royaltysb;
				}

			}
		}
		return royaltysb.toString();
		// return null;getre
	}

	public static boolean isNotnullorEpt(String str) {
		if (str == null || str.length() == 0) {
			return false;
		} else {
			return true;
		}
	}
}