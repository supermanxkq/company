package com.ccservice.b2b2c.atom.pay.handle;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import chinapnr.SecureLink;

import com.ccservice.b2b2c.ben.Paymentmethod;

/**
 * @author Administrator
 * 汇付支付通知接口
 * hanmh
 *
 */
@SuppressWarnings("serial")
public class ChinapnrNotifyHandle extends NotifyHandleSupport {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Logger logger = Logger.getLogger(this.getClass().getSimpleName());

        PrintWriter out = response.getWriter();
        request.setCharacterEncoding("gbk");
        String CmdId = request.getParameter("CmdId"); //消息类型
        String MerId = request.getParameter("MerId"); //商户号
        String RespCode = request.getParameter("RespCode"); //应答返回码
        String TrxId = request.getParameter("TrxId"); //钱管家交易唯一标识
        String OrdAmt = request.getParameter("OrdAmt"); //金额
        String CurCode = request.getParameter("CurCode"); //币种
        String Pid = request.getParameter("Pid"); //商品编号
        String OrdId = request.getParameter("OrdId"); //订单号
        String MerPriv = request.getParameter("MerPriv"); //商户私有域
        String RetType = request.getParameter("RetType"); //返回类型
        String DivDetails = request.getParameter("DivDetails"); //分账明细
        String GateId = request.getParameter("GateId"); //银行ID
        String ChkValue = request.getParameter("ChkValue"); //签名信息 	
        logger.info("××汇付天下支付成功通知：支付成功.订单号：" + OrdId);
        try {
            //验签
            String MerKeyFile = request.getSession().getServletContext().getRealPath("/") + "/PgPubk.key";
            String MerData = CmdId + MerId + RespCode + TrxId + OrdAmt + CurCode + Pid + OrdId + MerPriv + RetType
                    + DivDetails + GateId; //参数顺序不能错
            SecureLink sl = new SecureLink();
            int ret = sl.VeriSignMsg(MerKeyFile, MerData, ChkValue);

            if (ret != 0) {
                out.println("签名验证失败[" + MerData + "]");
            }
            else {
                if (RespCode.equals("000000")) {
                    //交易成功
                    //根据订单号 进行相应业务操作
                    //在些插入代码
                    //	out.println("交易成功");
                    out.write("RECV_ORD_ID_" + OrdId);
                    float pay = Float.valueOf(OrdAmt);
                    super.orderHandle(MerPriv, "HF" + TrxId, pay, Paymentmethod.CHINAPNR, "");

                }
                else {
                    //交易失败
                    //根据订单号 进行相应业务操作
                    //在些插入代码
                    out.println("交易失败");
                }

            }
        }
        catch (Exception e) {
            out.println("签名验证异常");
        }
        out.flush();
        out.close();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        this.doGet(request, response);
    }

    //public abstract void orderhandle(String ordernumber);

}
