package com.ccservice.b2b2c.atom.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.recharge.Recharge;
import com.ccservice.b2b2c.base.sysconfig.Sysconfig;

/**
 * 北京高阳捷迅信息技术有限公司
 * 话费订单状态通知
 * 
 * @time 2014年11月8日 下午12:35:12
 * @author chendong
 */
public class BillOrderState extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

    static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        String resultStatus = "100";
        response.setContentType("text/plain; charset=utf-8");
        PrintWriter out = response.getWriter();
        try {
            request.setCharacterEncoding("utf-8");
        }
        catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }

        String orderid = request.getParameter("orderid");
        WriteLog.write("19e充值通知", "订单号：" + orderid);
        if (orderid != null && !orderid.trim().equals("")) {
            String status = request.getParameter("status");
            String ordermoney = request.getParameter("ordermoney");
            String ver19e = request.getParameter("verifystring");
            String verifystring = "orderid=" + orderid + "&status=" + status + "&ordermoney=" + ordermoney
                    + "&merchantKey=" + getSystemConfig("phoneMerchantKey");
            String md5verstr = getKeyedDigest(verifystring, "");
            WriteLog.write("19e充值通知", "verifystring:" + verifystring + "---" + "生成MD5:" + md5verstr + "---19eMD5:"
                    + ver19e);
            if (status != null && !status.trim().equals("") && md5verstr.equals(ver19e)) {
                resultStatus = status;//通知接收成功后，改变响应状态值
                List<Recharge> rechargeList = Server
                        .getInstance()
                        .getMemberService()
                        .findAllRecharge("where 1=1 and C_ORDERNUMBER = '" + orderid.trim() + "' and C_STATE=0",
                                "ORDER BY ID DESC", -1, 0);
                if (rechargeList != null && rechargeList.size() > 0) {
                    Recharge recharge = rechargeList.get(0);
                    if (status.equals("2")) {//2充值成功
                        recharge.setState(Recharge.RECHARGESUCCESS);
                    }
                    else if (status.equals("3")) {//3部分成功
                        recharge.setState(Recharge.PARTOFTHESUCCESS);
                    }
                    else if (status.equals("4")) {//4充值失败
                        recharge.setState(Recharge.RECHARGEFAILURE);
                    }
                    if (ordermoney != null && !ordermoney.trim().equals("")) {
                        recharge.setActualrechargeamount(Double.valueOf(ordermoney.trim()));
                    }
                    Server.getInstance().getMemberService().updateRecharge(recharge);

                    //对账户余额进行处理
                    //                    double currentmoney = RechargeUtil.getCurrentAccountInfo(ordermoney);
                    //                    if (currentmoney > 0) {
                    //                        recharge.setCurrentmoney(currentmoney);
                    //                        Server.getInstance().getMemberService().updateRecharge(recharge);
                    //                    }

                    //                    RechargeUtil.smsRemind(currentmoney, this.getInitParameter("phonenumber"));//短信提醒
                }
            }
        }

        out.print(resultStatus);//100表示手机通知接收失败
        out.flush();
        out.close();
    }

    public static String getSystemConfig(String name) {
        List<Sysconfig> configs = Server.getInstance().getSystemService()
                .findAllSysconfig("where c_name='" + name + "'", "", -1, 0);
        if (configs != null && configs.size() == 1) {
            Sysconfig config = configs.get(0);
            return config.getValue();
        }
        return "-1";
    }

    /**
     * 生成验证摘要串
     * 
     * @param strSrc  需要加密的字符串
     * @param key  key请填空字符串
     * @return
     */
    public static String getKeyedDigest(String strSrc, String key) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(strSrc.getBytes("UTF8"));

            String result = "";
            byte[] temp;
            temp = md5.digest(key.getBytes("UTF8"));
            for (int i = 0; i < temp.length; i++) {
                result += Integer.toHexString((0x000000ff & temp[i]) | 0xffffff00).substring(6);
            }

            return result;

        }
        catch (NoSuchAlgorithmException e) {

            e.printStackTrace();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}