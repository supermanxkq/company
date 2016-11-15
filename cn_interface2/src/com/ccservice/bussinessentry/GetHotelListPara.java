package com.ccservice.bussinessentry;

import java.math.BigDecimal;
import java.util.Calendar;

/**
 * 
 * @author wzc 查询价格参数
 */
public class GetHotelListPara {
	private int FCityId;// 城市Id y
	private Calendar FInDate;// 入住日期 y 日期有效，建议入住日期和离店日期不要超过20天
	private Calendar FOutDate;// 离店日期 y 日期有效，返回结果中不包含离店日期的价格信息，离店日期大于入住日期
	private int PageSize;// 每页显示多少条 Int 是 默认为5
	private int PageIndex;// 当前第几页 Int 是 默认1
	private int TotalPage;// 总记录数 Int 是 0

	private int FDHI01RegionId = 0;// 区域Id n
	private String FDHI01Name;// 酒店名称n
	private int FDHI01DHP02Id = 0;// 星级Id n
	private String FDHI01Address;// 酒店地址 n
	private String FRoomName;// 房型名称 n
	private int FOrderByStar = 0;// 星级排序 Int 否 0 按星级排序。1 排序/ 0 不排序，默认为0。
	private int FOrderByRecLevels=0;// 推荐排序 1 排序/ 0 不排序，默认为0
	private int FOrderByPrice=0;// 价格排序 1 排序/ 0 不排序，默认为0。
	private int FPaymentMode=0;// 支付方式 0 全部/ 1 转账（挂账） 2 现付/ 3 全额预付，默认为0。
	private int FUserLevel=0;// 客户等级 0 全部 1 散客 2 同行
	private int FPromotionsRat=0;// 促销等级 0 全部
	private int FSmaillPrice=0;// 最低价格 Int 否 0 价格范围 最小 0 表示 不限制
	private int FBigPrice=0;// 最大价格 Int 否 0 价格范围 最大 0表示 不限制
	private String FLandmark;// 城市地标 String 否 Null 城市的地标名称
	private Double FPonitX=0.0;// 地标纬度坐标 Double 否 0.0 百度坐标 当城市地标不为null时,此参数必须填
	private Double FPonitY=0.0;// 地标经度坐标 Double 否 0.0 百度坐标 当城市地标不为null时,此参数必须填
	private BigDecimal FHotelDistance = new BigDecimal(0);// 公里 Decimal 否 0 //
															// 酒店距离地标的公里数 0 表示
	// 不限制
	// 如果是填1 则表示
	// 距离所填坐标直线距离小于 1公里的酒店。
	private int FShowRoomCount=0;// 显示房型数 Int 否 0 设置每家酒店显示的房型记录数量（最低价房型默认会排在前面）

	// 0 表示 查询全部

	public int getFCityId() {
		return FCityId;
	}

	public void setFCityId(int cityId) {
		FCityId = cityId;
	}

	public Calendar getFInDate() {
		return FInDate;
	}

	public void setFInDate(Calendar inDate) {
		FInDate = inDate;
	}

	public Calendar getFOutDate() {
		return FOutDate;
	}

	public void setFOutDate(Calendar outDate) {
		FOutDate = outDate;
	}

	public int getFDHI01RegionId() {
		return FDHI01RegionId;
	}

	public void setFDHI01RegionId(int regionId) {
		FDHI01RegionId = regionId;
	}

	public String getFDHI01Name() {
		return FDHI01Name;
	}

	public void setFDHI01Name(String name) {
		FDHI01Name = name;
	}

	public int getFDHI01DHP02Id() {
		return FDHI01DHP02Id;
	}

	public void setFDHI01DHP02Id(int id) {
		FDHI01DHP02Id = id;
	}

	public String getFDHI01Address() {
		return FDHI01Address;
	}

	public void setFDHI01Address(String address) {
		FDHI01Address = address;
	}

	public String getFRoomName() {
		return FRoomName;
	}

	public void setFRoomName(String roomName) {
		FRoomName = roomName;
	}

	public int getFOrderByStar() {
		return FOrderByStar;
	}

	public void setFOrderByStar(int orderByStar) {
		FOrderByStar = orderByStar;
	}

	public int getFOrderByRecLevels() {
		return FOrderByRecLevels;
	}

	public void setFOrderByRecLevels(int orderByRecLevels) {
		FOrderByRecLevels = orderByRecLevels;
	}

	public int getFOrderByPrice() {
		return FOrderByPrice;
	}

	public void setFOrderByPrice(int orderByPrice) {
		FOrderByPrice = orderByPrice;
	}

	public int getFPaymentMode() {
		return FPaymentMode;
	}

	public void setFPaymentMode(int paymentMode) {
		FPaymentMode = paymentMode;
	}

	public int getFUserLevel() {
		return FUserLevel;
	}

	public void setFUserLevel(int userLevel) {
		FUserLevel = userLevel;
	}

	public int getFPromotionsRat() {
		return FPromotionsRat;
	}

	public void setFPromotionsRat(int promotionsRat) {
		FPromotionsRat = promotionsRat;
	}

	public int getFSmaillPrice() {
		return FSmaillPrice;
	}

	public void setFSmaillPrice(int smaillPrice) {
		FSmaillPrice = smaillPrice;
	}

	public int getFBigPrice() {
		return FBigPrice;
	}

	public void setFBigPrice(int bigPrice) {
		FBigPrice = bigPrice;
	}

	public String getFLandmark() {
		return FLandmark;
	}

	public void setFLandmark(String landmark) {
		FLandmark = landmark;
	}

	public Double getFPonitX() {
		return FPonitX;
	}

	public void setFPonitX(Double ponitX) {
		FPonitX = ponitX;
	}

	public Double getFPonitY() {
		return FPonitY;
	}

	public void setFPonitY(Double ponitY) {
		FPonitY = ponitY;
	}

	public BigDecimal getFHotelDistance() {
		return FHotelDistance;
	}

	public void setFHotelDistance(BigDecimal hotelDistance) {
		FHotelDistance = hotelDistance;
	}

	public int getFShowRoomCount() {
		return FShowRoomCount;
	}

	public void setFShowRoomCount(int showRoomCount) {
		FShowRoomCount = showRoomCount;
	}

	public int getPageSize() {
		return PageSize;
	}

	public void setPageSize(int pageSize) {
		PageSize = pageSize;
	}

	public int getPageIndex() {
		return PageIndex;
	}

	public void setPageIndex(int pageIndex) {
		PageIndex = pageIndex;
	}

	public int getTotalPage() {
		return TotalPage;
	}

	public void setTotalPage(int totalPage) {
		TotalPage = totalPage;
	}

}
