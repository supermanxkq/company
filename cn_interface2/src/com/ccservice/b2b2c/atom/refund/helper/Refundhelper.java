package com.ccservice.b2b2c.atom.refund.helper;

import java.util.List;

public interface Refundhelper {

    /**
     * 订单号
     * @return
     */
    public long getOrderid();

    /**
     * 订单支付时所传递的orderid;此方法主要针对汇付天下退款。
     * @return
     */
    public String getOldOrdId();

    /**
     * 退款信息集合
     * @return
     */
    public List<Refundinfo> getRefundinfos();

    /**
     * @return
     * 
     */
    @SuppressWarnings("unchecked")
    public Class getProfitHandle();

    /**]
     * 请与支付时所传订单号保持一致。即payhelper中的一致。
     * @return
     */
    public String getOrdernumber();

    // 获得交易类型：产生交易记录时使用。不可重复。
    // 暂定：机票：1，酒店2，火车票3，4.手机充值，5，Q币充值，6，虚拟账户充值,7保险购买，8短信充值。若有其他，在此声明，切勿重复。
    public int getTradetype();

    /**
     * 需要退款的代理商ID
     * @author wzc
     * @date 2016年2月17日 下午3:17:12
     */
    public long getAgentId();

}
