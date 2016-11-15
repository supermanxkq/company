package com.ccservice.component.sms;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ccservice.b2b2c.atom.server.Server;
import com.ccservice.b2b2c.base.dnsmaintenance.Dnsmaintenance;
import com.ccservice.b2b2c.base.templet.Templet;

public class SMSTemplet implements Serializable {
	static Log log = LogFactory.getLog(SMSTemplet.class);

	// 通过方法去调用
	public  String getSMSTemplett(SMSType smstype, Dnsmaintenance dns) {
		Templet templet = Server.getInstance().getMemberService()
				.findTempletbyagentid(smstype.getType(), dns.getAgentid());
		if (templet != null) {
			return templet.getTempletmess();
		}
		return "";
	}

	public static void main(String[] args) {

	}
}
