package com.ccservice.b2b2c.atom.sms;

import com.ccservice.b2b2c.ben.Dnsbarends;

public interface SmsSender {

    public boolean sendSMS(String[] mobiles, String content, long ordercode, long sendagentid, Dnsbarends dns);

    /**
     * 
     * 此方法是企信通短信剩余条数查询接口，有实际意义的实现类是：QiXinTongShortMsgSmsSender   对于其他实现类无实际意义
     * 
     */
    public String smsBalanceInquiry();

    //
    //	// SDK客户端
    //	private SDKClient sdkClient;
    //	// 发送优先级别(1~5)，数字越高，级别越高
    //	private int smsPriority;
    //	// 短信编码
    //	private String charset;
    //
    //	/**
    //	 * 发送短信
    //	 * 
    //	 * @param mobiles
    //	 *            手机号码组
    //	 * @param content
    //	 *            短信内容
    //	 * 
    //	 * @return 返回发送结果
    //	 */
    //	public int sendSMS(String[] mobiles, String content,String ordercode) {
    //		int i = 0;
    //		try {
    //			i = this.sdkClient.getClient().sendSMSEx(mobiles, content, charset,
    //					smsPriority);
    //		} catch (Exception e) {
    //			e.printStackTrace();
    //		} finally {
    //			this.getSdkClient().getClient().closeChannel();
    //		}
    //		return i;
    //	}
    //
    //	public SDKClient getSdkClient() {
    //		return sdkClient;
    //	}
    //
    //	public void setSdkClient(SDKClient sdkClient) {
    //		this.sdkClient = sdkClient;
    //	}
    //
    //	public int getSmsPriority() {
    //		return smsPriority;
    //	}
    //
    //	public void setSmsPriority(int smsPriority) {
    //		this.smsPriority = smsPriority;
    //	}
    //
    //	public String getCharset() {
    //		return charset;
    //	}
    //
    //	public void setCharset(String charset) {
    //		this.charset = charset;
    //	}

}
