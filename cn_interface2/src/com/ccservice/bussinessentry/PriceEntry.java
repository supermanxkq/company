package com.ccservice.bussinessentry;

import java.io.Serializable;

public class PriceEntry implements Serializable {
	private static final long serialVersionUID = 1L;
	private Integer hotelid;// 酒店id
	private Integer roomtypeid;// 房型id
	private String roomtypename;//房型名称
	private String datestr;// 对应的日期
	private String priceoffer;// 卖价
	/**
	 * 1 即时确认 2 满房 3 申请 4 紧张 5无值
	 */
	private Integer roomstatus;// 房态
	private String roomleave;// 剩余房数
	private String BreakfastDescId;// 早餐类型id
	private String BreakfastName;// 早餐类型名称
	private Integer minAmount;// 最低预定量
	private Integer beforeDay;// 提前预订天数
	private String minPayed;// 最低消费额
	private String cancelname;// 取消条款名称

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "酒店id：" + hotelid + ",房型id：" + roomtypeid + ",日期：" + datestr
				+ ",卖价：" + priceoffer + ",房态：" + getroomtypestauts(roomstatus)
				+ ",剩余房数：" + roomleave + ",早餐类型名称：" + BreakfastName + "，最低预定量："
				+ minAmount + ",提前预定天数：" + beforeDay + ",最低消费额：" + minPayed
				+ ",取消条款名称：" + cancelname;
	}

	public String getroomtypestauts(Integer value) {
		if (value == 1) {
			return "即时确认";
		} else if (value == 2) {
			return "满房";
		} else if (value == 3) {
			return "申请";
		} else if (value == 4) {
			return "紧张";
		} else if (value == 5) {
			return "无值";
		}
		return "";
	}

	public Integer getHotelid() {
		return hotelid;
	}

	public void setHotelid(Integer hotelid) {
		this.hotelid = hotelid;
	}

	public Integer getRoomtypeid() {
		return roomtypeid;
	}

	public void setRoomtypeid(Integer roomtypeid) {
		this.roomtypeid = roomtypeid;
	}

	public String getDatestr() {
		return datestr;
	}

	public void setDatestr(String datestr) {
		this.datestr = datestr;
	}

	public String getPriceoffer() {
		return priceoffer;
	}

	public void setPriceoffer(String priceoffer) {
		this.priceoffer = priceoffer;
	}

	public Integer getRoomstatus() {
		return roomstatus;
	}

	public void setRoomstatus(Integer roomstatus) {
		this.roomstatus = roomstatus;
	}

	public String getRoomleave() {
		return roomleave;
	}

	public void setRoomleave(String roomleave) {
		this.roomleave = roomleave;
	}

	public String getBreakfastDescId() {
		return BreakfastDescId;
	}

	public void setBreakfastDescId(String breakfastDescId) {
		BreakfastDescId = breakfastDescId;
	}

	public String getBreakfastName() {
		return BreakfastName;
	}

	public void setBreakfastName(String breakfastName) {
		BreakfastName = breakfastName;
	}

	public Integer getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(Integer minAmount) {
		this.minAmount = minAmount;
	}

	public Integer getBeforeDay() {
		return beforeDay;
	}

	public void setBeforeDay(Integer beforeDay) {
		this.beforeDay = beforeDay;
	}

	public String getMinPayed() {
		return minPayed;
	}

	public void setMinPayed(String minPayed) {
		this.minPayed = minPayed;
	}

	public String getCancelname() {
		return cancelname;
	}

	public void setCancelname(String cancelname) {
		this.cancelname = cancelname;
	}

	public String getRoomtypename() {
		return roomtypename;
	}

	public void setRoomtypename(String roomtypename) {
		this.roomtypename = roomtypename;
	}

}
