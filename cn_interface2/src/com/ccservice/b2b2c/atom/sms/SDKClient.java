package com.ccservice.b2b2c.atom.sms;

import cn.emay.sdk.client.api.Client;

/**
 * 短信发送平台
 * 
 */
public class SDKClient {

	// 注册号
	private String registationNo;
	// 密码
	private String password;
	// 自定义关键字
	private String key;

	private Client client = null;

	private SDKClient() {
	}

	/**
	 * 获取短信客户端
	 * 
	 * @return
	 */
	public synchronized Client getClient() {
		if (client == null) {
			try {
				System.out.println("=========建立client对象==========");
				client = new Client(registationNo, key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}

	/**
	 * 获取短信客户端
	 * 根据自定义注册号、关键字
	 * 
	 * @param softwareSerialNo
	 *            注册号
	 * @param key
	 *            关键字
	 * @return 短信客户端
	 */
	public synchronized Client getClient(String softwareSerialNo, String key) {
		if (client == null) {
			try {
				client = new Client(softwareSerialNo, key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}

	/*---GETTER SETTER 方法----*/
	public String getRegistationNo() {
		return registationNo;
	}

	public void setRegistationNo(String registationNo) {
		this.registationNo = registationNo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

}
