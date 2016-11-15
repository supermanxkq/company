package com.ccservice.b2b2c.atom.pay;

/**
 * @author hanmh
 *支付接口
 */
public interface Pay {

    /**
     * @param factorage 手续费
     * @throws Exception
     */
    public void pay(float factorage) throws Exception;

    /**
     * @param agentid 代理商agentid
     */
    public void pay(long agentid) throws Exception;
}
