package com.ccservice.b2b2c.atom.pay;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.component.WriteLog;
import com.ccservice.b2b2c.atom.pay.helper.Payhelper;

@SuppressWarnings("serial")
public class Payframework extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String payname = request.getParameter("payname");
            String helpername = request.getParameter("helpername");
            long orderid = Long.parseLong(new String(request.getParameter("orderid").getBytes("ISO8859-1"), "UTF-8"));
            String factoragestr = request.getParameter("factorage");
            float factorage = 0f;
            if (factoragestr != null && factoragestr.length() > 0) {
                try {
                    factorage = Float.parseFloat(new String(request.getParameter("factorage").getBytes("ISO8859-1"),
                            "UTF-8"));
                }
                catch (Exception e) {
                }
            }
            WriteLog.write("Payframework", orderid + ":" + payname + ":" + helpername + ":" + factoragestr + ":"
                    + factorage);
            String payHelperName = Payhelper.class.getPackage().getName() + "." + helpername;
            Payhelper payhelper = (Payhelper) Class.forName(payHelperName).getConstructor(long.class)
                    .newInstance(orderid);
            String className1 = Pay.class.getPackage().getName() + "." + payname;
            Pay pay = (Pay) Class.forName(className1)
                    .getConstructor(HttpServletRequest.class, HttpServletResponse.class, Payhelper.class)
                    .newInstance(request, response, payhelper);
            pay.pay(factorage);
        }
        catch (Exception e) {
            WriteLog.write("Payframework", "" + e.getMessage());
        }

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.doGet(request, response);
    }

}
