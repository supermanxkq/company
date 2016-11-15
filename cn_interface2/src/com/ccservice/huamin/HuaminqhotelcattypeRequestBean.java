package com.ccservice.huamin;
/**
 * 
 * 
 * @Caless:HuaminqhotelcattypeRequestBean.java
 * @ClassDesc:查询华闽酒店相关的房型床型数据请求bean
 * @Author:胡灿
 * @Date:2012-12-15 上午11:10:12
 * @Company: 航天华有(北京)科技有限公司
 * @Copyright: Copyright (c) 2012
 * @version: 1.0
 */
public class HuaminqhotelcattypeRequestBean  implements java.io.Serializable{
	 
	 						//	項目  參數  必需(M)/隨意(O)  類型  大小  註解 
	private String company;//1  P_COMPANY  M  Char 10  公司賬號 
	private String id;//2  P_ID  M  Char 15  網頁用戶名稱 
	private String pass;//3  P_PASS  M  Char 15  網頁密碼 
	private String lang;//4  P_LANG  O  Char 3  ENG-英文  (預設)  CHN-中文繁體  SIM-中文簡體 
	private String country;//5  P_COUNTRY  O  Char 3  國家代碼 
	private String area;//6  P_AREA  O  Char 4  地區代碼 
	private String city;//7  P_CITY  O  Char 6  城市代碼 
	private String cat;//8  P_CAT  O  Char 4  房型代碼 
	private String catname;//9  P_CATNAME  O  Char 100  房型名稱 
	private String grade;//10  P_GRADE  O  Char 1  酒店星級  5, 4, 3, 2, 1 
	private String hotel;//11  P_HOTEL  O  Char 10  酒店代碼 
	private String hotelname;//12  P_HOTELNAME  O  Char 70  酒店名稱 
	private String created;//13  P_CREATED  O  Char 9  日-月-年eg.18-Oct-12 
	private String modify;//14  P_MODIFY  O  Char 9  日-月-年eg.18-Oct-12 
	
	
	public HuaminqhotelcattypeRequestBean() {
		super();
	}
	
	public HuaminqhotelcattypeRequestBean(String company, String id,
			String pass, String lang, String country, String area, String city,
			String cat, String catname, String grade, String hotel,
			String hotelname, String created, String modify) {
		super();
		this.company = company;
		this.id = id;
		this.pass = pass;
		this.lang = lang;
		this.country = country;
		this.area = area;
		this.city = city;
		this.cat = cat;
		this.catname = catname;
		this.grade = grade;
		this.hotel = hotel;
		this.hotelname = hotelname;
		this.created = created;
		this.modify = modify;
	}

	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPass() {
		return pass;
	}
	public void setPass(String pass) {
		this.pass = pass;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCat() {
		return cat;
	}
	public void setCat(String cat) {
		this.cat = cat;
	}
	public String getCatname() {
		return catname;
	}
	public void setCatname(String catname) {
		this.catname = catname;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public String getHotel() {
		return hotel;
	}
	public void setHotel(String hotel) {
		this.hotel = hotel;
	}
	public String getHotelname() {
		return hotelname;
	}
	public void setHotelname(String hotelname) {
		this.hotelname = hotelname;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getModify() {
		return modify;
	}
	public void setModify(String modify) {
		this.modify = modify;
	}
	 

}
