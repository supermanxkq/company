package com.ccservice.b2b2c.atom.pay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ccservice.b2b2c.atom.pay.helper.Payhelper;

/**
 * @author hanmh
 * 支付父类
 *
 */
public abstract class PaySupport {
    HttpServletRequest request;

    HttpServletResponse response;

    Payhelper payhelper;

    /**
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param payhelper 支付辅助类
     */
    public PaySupport(HttpServletRequest request, HttpServletResponse response, Payhelper payhelper) {
        this.request = request;
        this.response = response;
        this.payhelper = payhelper;
    }

    /**
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param payhelper 支付辅助类
     */
    public PaySupport(Payhelper payhelper) {
        this.payhelper = payhelper;
    }
}
