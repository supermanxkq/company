package com.ccservice.b2b2c.atom.pay.handle;

/**
 * @author hanmh
 * 支付成功订单处理接口
 *
 */
public interface PayHandle {

    /**
     * 支付成功后的后续订单处理
     * @param ordernumber 订单号
     * @param tradeno     交易号
     * @param payprice    交易金额
     * @param paytype     支付方式
     */
    public void orderHandle(String ordernumber, String tradeno, double payprice, int paytype, String selleremail);
    //    public void orderHandle(String ordernumber, String tradeno, float payprice, int paytype);
}
