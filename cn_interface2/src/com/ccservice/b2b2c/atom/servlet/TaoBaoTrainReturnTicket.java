package com.ccservice.b2b2c.atom.servlet;

import java.text.SimpleDateFormat;

import com.ccservice.b2b2c.atom.component.PublicComponent;
import com.ccservice.b2b2c.base.train.Trainticket;

public class TaoBaoTrainReturnTicket extends PublicComponent {

	private static final SimpleDateFormat timeFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private static final String datatypeflag = "100";

	private String getTicketType(Trainticket ticket) {
		// 票类型
		int tickettype = ticket.getTickettype();
		// 判断类型
		if (tickettype == 1) {
			return "成人票";
		}
		if (tickettype == 2) {
			return "儿童票";
		}
		if (tickettype == 3) {
			return "学生票";
		}
		if (tickettype == 4) {
			return "残军票";
		}
		return "票类型待定：" + tickettype;
	}
}
