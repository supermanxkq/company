package com.ccservice.b2b2c.atom.pay;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pay.config.TenpayConfig;

/**
 * 
 * @author wzc
 * 财付通支付商圈签约
 *
 */
public class Tenpaypartner extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String pid = TenpayConfig.getInstance().getPartnerID();
        String url = "https://www.tenpay.com/cgi-bin/trust/showtrust_refund.cgi?spid=" + pid;
        response.sendRedirect(url);
    }
}
