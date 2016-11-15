package com.ccservice.b2b2c.atom.qunar;

public class HotelData implements Comparable<HotelData> {
	private String hotelid;// 酒店id
	private String agentNo;// 代理商id
	private String hotelname;// 酒店名称
	private String roomname;// 房型名称
	private String roomnameBF;// 房型，早餐
	private Integer sealprice;// 销售价格
	private Integer baseprice;// 底价
	private String date;
	private Integer roomstatus;// 1 开房，-1 关房,-2 休息中
	private Integer type;// 0 现付，1 预付
	private Long roomtypeid;// 本地数据库id
	
	private String url;//去哪预订链接
	private String agentName;//去哪加盟商名称
	private Double fanyong;//qunar返佣
	private Integer danbaoflag;// 0不担保，1 担保

	@Override
	public String toString() {
		return "酒店id：" + hotelid + ",本地房型id：" + roomtypeid + ",担保："+getDanboao(danbaoflag)+",代理商编号："+agentNo
				+",代理商名称："+ agentName + ",房型名称：" + roomname + ",套餐属性："+roomnameBF+",qunar销售价格："
				+ sealprice + ",qunar返佣:"+fanyong+",开关房状态：" + getRoom(roomstatus) + ",支付方式："
				+ getxytype(type);
	}
	
	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	
	public Double getFanyong() {
		return fanyong;
	}

	public void setFanyong(Double fanyong) {
		this.fanyong = fanyong;
	}

	/**
	 * 开关房状态 1 开房，-1 关房
	 * 
	 * @param value
	 * @return
	 */
	public String getDanboao(Integer danbao) {
		if (danbao == 1) {
			return "担保";
		} else if (danbao == 0) {
			return "非担保";
		} else {
			return "";
		}
	}
	/**
	 * 开关房状态 1 开房，-1 关房
	 * 
	 * @param value
	 * @return
	 */
	public String getRoom(Integer value) {
		if (value == 1) {
			return "开房";
		} else if (value == -1) {
			return "关房";
		} else {
			return "";
		}
	}
	
	public Integer getDanbaoflag() {
		return danbaoflag;
	}

	public void setDanbaoflag(Integer danbaoflag) {
		this.danbaoflag = danbaoflag;
	}

	/**
	 * 预付，现付 0 现付，1 预付
	 * 
	 * @return
	 */
	public String getxytype(Integer value) {
		if (value == 0) {
			return "现付";
		} else if (value == 1) {
			return "预付";
		} else {
			return "";
		}
	}

	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAgentNo() {
		return agentNo;
	}

	public void setAgentNo(String agentNo) {
		this.agentNo = agentNo;
	}

	public String getRoomname() {
		return roomname;
	}

	public void setRoomname(String roomname) {
		this.roomname = roomname;
	}

	public String getRoomnameBF() {
		return roomnameBF;
	}

	public void setRoomnameBF(String roomnameBF) {
		this.roomnameBF = roomnameBF;
	}

	public Integer getSealprice() {
		return sealprice;
	}

	public void setSealprice(Integer sealprice) {
		this.sealprice = sealprice;
	}

	public Integer getBaseprice() {
		return baseprice;
	}

	public void setBaseprice(Integer baseprice) {
		this.baseprice = baseprice;
	}

	public String getHotelid() {
		return hotelid;
	}

	public void setHotelid(String hotelid) {
		this.hotelid = hotelid;
	}

	public String getHotelname() {
		return hotelname;
	}

	public void setHotelname(String hotelname) {
		this.hotelname = hotelname;
	}

	@Override
	public int compareTo(HotelData temp) {
		return this.getSealprice() - temp.getSealprice();
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getRoomstatus() {
		return roomstatus;
	}

	public void setRoomstatus(Integer roomstatus) {
		this.roomstatus = roomstatus;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getRoomtypeid() {
		return roomtypeid;
	}

	public void setRoomtypeid(Long roomtypeid) {
		this.roomtypeid = roomtypeid;
	}

}
