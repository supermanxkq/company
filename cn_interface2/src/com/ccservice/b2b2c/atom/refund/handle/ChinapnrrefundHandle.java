package com.ccservice.b2b2c.atom.refund.handle;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.ccservice.b2b2c.atom.pay.Chinapnrrefund;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.ben.Refundtrade;

import chinapnr.SecureLink;

/**
 * @author Administrator 汇付天下退款通知接口
 * 
 */
@SuppressWarnings("serial")
public class ChinapnrrefundHandle extends HttpServlet {

	static Logger logger = Logger.getLogger(Chinapnrrefund.class
			.getSimpleName());

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		try {
			request.setCharacterEncoding("GBK");
		} catch (UnsupportedEncodingException e2) {
		}
		String CmdId = request.getParameter("CmdId").trim(); // 消息类型
		String OrdId = request.getParameter("OrdId").trim(); // 退款订单号
		logger.error("汇付天下退款通知：退款订单号" + OrdId);
		String OldOrdId = request.getParameter("OldOrdId").trim(); // 原交易订单号
		String RespCode = request.getParameter("RespCode").trim(); // 商户号
		String ErrMsg = request.getParameter("ErrMsg").trim(); // 应答错误描述
		String ChkValue = request.getParameter("ChkValue").trim(); // 签名
		PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e1) {
		}
		try {
			// 验签
			String MerKeyFile = request.getSession().getServletContext()
					.getRealPath("/")
					+ "/PgPubk.key";
			String MerData = CmdId + OrdId + OldOrdId + RespCode + ErrMsg; // 参数顺序不能错
			SecureLink sl = new SecureLink();
			int ret = sl.VeriSignMsg(MerKeyFile, MerData, ChkValue);

			if (ret != 0) {
				logger.error("退款订单号" + OrdId + "签名验证失败[" + MerData + "]");
				out.println("签名验证失败[" + MerData + "]");
			} else {
				logger.error("退款订单号" + OrdId + "退款成功,处理订单状态");
				String ordernumber=OrdId.substring(14);
				Refundtrade fefundtrade=Server.getInstance().getMemberService().findRefundtrade(Long.valueOf(ordernumber));
				String handleclass=fefundtrade.getHandleclass();
				boolean success=false;
				if (RespCode.equals("000000")) {	
					success=true;
					// 退款成功
					// 根据退款订单号 进行相应业务操作
					// 在些插入代码
					out.println("退款成功");
				} else {
					// 退款失败
					// 根据退款订单号 进行相应业务操作
					// 在些插入代码
					out.println("退款失败");
				}
				try {
					RefundHandle refundhandle=	(RefundHandle) Class.forName(
							RefundHandle.class.getPackage().getName() + "." + handleclass)
							.newInstance();
					refundhandle.refundedHandle(success,fefundtrade.getOrderid(),OrdId);
				} catch(Exception e){
				}
				out.println("RECV_ORD_ID_" + OldOrdId);
			}
		} catch (Exception e) {
			logger.error("退款订单号" + OrdId + "签名验证异常");
			out.println("签名验证异常");
		}
		out.flush();
		out.close();
	}

}