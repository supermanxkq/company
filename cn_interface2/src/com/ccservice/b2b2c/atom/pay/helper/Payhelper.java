package com.ccservice.b2b2c.atom.pay.helper;

/**
 * 订单辅助接口：用于获在Alipay中获取订单相关信息。 相关不同订单实现此接口。 所有已实现的类：Trainpayhelper.
 * @author hanmh 
 * 
 */
public interface Payhelper {

    public String getHandleName();

    /**
     * 订单号
     * 
     * @return
     */
    public String getOrdernumber();

    /**
     * 交易价格
     * 
     * @return
     */
    public double getOrderprice();

    /**
     * 订单描述
     * 
     * @return
     */
    public String getOrderDescription();

    /**
     * @return 订单名称
     */
    public String getOrdername();

    // getReturnurl()和getNotifyurl()不可同时返回空
    public String getReturnurl();

    //	// getReturnurl()和getNotifyurl()不可同时返回空
    //	public String getNotifyurl();

    /**
     * 商品展示路径。可为空
     * 
     * @return
     */
    public String getShwourl();

    /**
     * 获得交易类型：产生交易记录时使用。不可重复。
     * 1'机票,2'酒店,3'火车票,4'手机充值,5'Q币充值,6'虚拟账户充值 ,7'保险购买,8'短信充值,9'租车,10'旅游,11'签证
     * @return
     */
    public int getTradetype();

    /**支付交易号。分润时使用
     * @return
     */
    public String getTradeno();

}
