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

/**
 * 
 * Q币订单状态通知
 *
 */
public class QQOrderState extends javax.servlet.http.HttpServlet implements javax.servlet.Servlet {

    static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    private String gameKey = "KMS1PTr1Oewc1XcvBPxBk1QMt1pvzFUy";//密钥（游戏充值）

    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        WriteLog.write("19eQ币充值通知", "接收到Q币通知");

        String resultStatus = "1";
        response.setContentType("text/plain; charset=utf-8");
        PrintWriter out = response.getWriter();
        try {
            request.setCharacterEncoding("utf-8");
        }
        catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }

        String orderid = request.getParameter("orderid");
        String orderstatus = request.getParameter("orderstatus");
        String finishmoney = request.getParameter("finishmoney");
        String jxorderid = request.getParameter("jxorderid");
        String resultcode = request.getParameter("resultcode");
        String fillmoney = request.getParameter("fillmoney");
        String finishtime = request.getParameter("finishtime");
        String sign = request.getParameter("sign");
        WriteLog.write("19eQ币充值通知", "订单号：" + orderid + "---高阳订单号：" + jxorderid + "---实际充值金额：" + finishmoney
                + "---sign:" + sign);
        if (orderid != null && !orderid.trim().equals("")) {
            String verifystring = "fillmoney=" + fillmoney;
            verifystring += "&finishmoney=" + finishmoney;
            verifystring += "&finishtime=" + finishtime;
            verifystring += "&jxorderid=" + jxorderid;
            verifystring += "&orderid=" + orderid;
            verifystring += "&orderstatus=" + orderstatus;
            verifystring += "&resultcode=" + resultcode;
            String md5verstr = getKeyedDigest(gameKey + getKeyedDigest(verifystring + "&key=" + gameKey, "") + gameKey,
                    "");
            WriteLog.write("19eQ币充值通知", "verifystring：" + verifystring + "---" + "生成MD5:" + md5verstr + "---19eMD5:"
                    + sign);

            if (orderstatus != null && !orderstatus.trim().equals("") && md5verstr.equals(sign)) {
                resultStatus = "0";//通知接收成功后，改变响应状态值
                List<Recharge> rechargeList = Server.getInstance().getMemberService().findAllRecharge(
                        "where 1=1 and C_ORDERNUMBER = '" + orderid.trim() + "' and C_STATE=0", "ORDER BY ID DESC", -1,
                        0);
                if (rechargeList != null && rechargeList.size() > 0) {
                    Recharge recharge = rechargeList.get(0);
                    if (orderstatus.equals("0")) {
                        recharge.setState(Recharge.RECHARGESUCCESS);
                    }
                    else if (orderstatus.equals("1")) {
                        recharge.setState(Recharge.RECHARGEFAILURE);
                    }
                    else if (orderstatus.equals("2")) {
                        recharge.setState(Recharge.PARTOFTHESUCCESS);
                    }
                    if (finishmoney != null && !finishmoney.trim().equals("")) {
                        recharge.setActualrechargeamount(Double.valueOf(finishmoney.trim()));
                    }
                    Server.getInstance().getMemberService().updateRecharge(recharge);

                    //                        double currentmoney = RechargeUtil.getCurrentAccountInfo(finishmoney);
                    //
                    //                        if (currentmoney > 0) {
                    //                            recharge.setCurrentmoney(currentmoney);
                    //                        }

                    //                        RechargeUtil.smsRemind(currentmoney, this.getInitParameter("phonenumber"));//短信提醒
                }
            }
        }

        out.print("<?xml version=\"1.0\" encoding=\"GBK\"?><response><notifyresult>" + resultStatus
                + "</notifyresult></response>");
        out.flush();
        out.close();
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