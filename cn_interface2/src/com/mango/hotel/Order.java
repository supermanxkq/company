/**
 * Order.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class Order  implements java.io.Serializable {
    private java.lang.Long orderid;

    private int adultnum;

    private java.lang.String arrivaltime;

    private java.lang.String arrivaltraffic;

    private java.lang.Integer bedtype;

    private java.lang.String checkindate;

    private java.lang.String checkoutdate;

    private java.lang.String createdate;

    private java.lang.String creditcardexpires;

    private java.lang.String creditcardname;

    private java.lang.String creditcardno;

    private java.lang.String creditcardtype;

    private java.lang.String currency;

    private java.lang.String customerfax;

    private java.lang.String email;

    private float exchangerate;

    private java.lang.String fellownames;

    private java.lang.String flight;

    private java.lang.String guaranteetype;

    private java.lang.String hotelcode;

    private java.lang.String hotelcodeforchannel;

    private java.lang.Long hotelid;

    private java.lang.String hotelname;

    private java.lang.String hotelnotes;

    private java.lang.String isguarantee;

    private java.lang.String latestarrivaltime;

    private java.lang.String linkman;

    private java.lang.Integer hotelconfirm;

    private java.lang.String hotelconfirmid;

    private float localtotalamout;

    private java.lang.String orderstate;

    private java.lang.String mobile;

    private java.lang.String noshowreason;

    private java.lang.String nosmoking;

    private java.lang.String ordercd;

    private java.lang.String ordercdhotel;

    private java.lang.String paymethod;

    private java.lang.String paysatus;

    private int pricetypecode;

    private java.lang.String rateplancode;

    private int roomquantity;

    private java.lang.Integer roomtypecode;

    private java.lang.String roomtypecodeforchannel;

    private java.lang.Integer roomtypeid;

    private java.lang.String roomtypename;

    private java.lang.String specialrequest;

    private java.lang.String telephone;

    private java.lang.String title;

    private float totalamount;

    private com.mango.hotel.OrderItem[] exOrderItems;

    private java.lang.String specialnote;

    private java.lang.String createtime;

    private java.lang.String modifiedtime;

    private float firstDayPrice;

    public Order() {
    }

    public Order(
           java.lang.Long orderid,
           int adultnum,
           java.lang.String arrivaltime,
           java.lang.String arrivaltraffic,
           java.lang.Integer bedtype,
           java.lang.String checkindate,
           java.lang.String checkoutdate,
           java.lang.String createdate,
           java.lang.String creditcardexpires,
           java.lang.String creditcardname,
           java.lang.String creditcardno,
           java.lang.String creditcardtype,
           java.lang.String currency,
           java.lang.String customerfax,
           java.lang.String email,
           float exchangerate,
           java.lang.String fellownames,
           java.lang.String flight,
           java.lang.String guaranteetype,
           java.lang.String hotelcode,
           java.lang.String hotelcodeforchannel,
           java.lang.Long hotelid,
           java.lang.String hotelname,
           java.lang.String hotelnotes,
           java.lang.String isguarantee,
           java.lang.String latestarrivaltime,
           java.lang.String linkman,
           java.lang.Integer hotelconfirm,
           java.lang.String hotelconfirmid,
           float localtotalamout,
           java.lang.String orderstate,
           java.lang.String mobile,
           java.lang.String noshowreason,
           java.lang.String nosmoking,
           java.lang.String ordercd,
           java.lang.String ordercdhotel,
           java.lang.String paymethod,
           java.lang.String paysatus,
           int pricetypecode,
           java.lang.String rateplancode,
           int roomquantity,
           java.lang.Integer roomtypecode,
           java.lang.String roomtypecodeforchannel,
           java.lang.Integer roomtypeid,
           java.lang.String roomtypename,
           java.lang.String specialrequest,
           java.lang.String telephone,
           java.lang.String title,
           float totalamount,
           com.mango.hotel.OrderItem[] exOrderItems,
           java.lang.String specialnote,
           java.lang.String createtime,
           java.lang.String modifiedtime,
           float firstDayPrice) {
           this.orderid = orderid;
           this.adultnum = adultnum;
           this.arrivaltime = arrivaltime;
           this.arrivaltraffic = arrivaltraffic;
           this.bedtype = bedtype;
           this.checkindate = checkindate;
           this.checkoutdate = checkoutdate;
           this.createdate = createdate;
           this.creditcardexpires = creditcardexpires;
           this.creditcardname = creditcardname;
           this.creditcardno = creditcardno;
           this.creditcardtype = creditcardtype;
           this.currency = currency;
           this.customerfax = customerfax;
           this.email = email;
           this.exchangerate = exchangerate;
           this.fellownames = fellownames;
           this.flight = flight;
           this.guaranteetype = guaranteetype;
           this.hotelcode = hotelcode;
           this.hotelcodeforchannel = hotelcodeforchannel;
           this.hotelid = hotelid;
           this.hotelname = hotelname;
           this.hotelnotes = hotelnotes;
           this.isguarantee = isguarantee;
           this.latestarrivaltime = latestarrivaltime;
           this.linkman = linkman;
           this.hotelconfirm = hotelconfirm;
           this.hotelconfirmid = hotelconfirmid;
           this.localtotalamout = localtotalamout;
           this.orderstate = orderstate;
           this.mobile = mobile;
           this.noshowreason = noshowreason;
           this.nosmoking = nosmoking;
           this.ordercd = ordercd;
           this.ordercdhotel = ordercdhotel;
           this.paymethod = paymethod;
           this.paysatus = paysatus;
           this.pricetypecode = pricetypecode;
           this.rateplancode = rateplancode;
           this.roomquantity = roomquantity;
           this.roomtypecode = roomtypecode;
           this.roomtypecodeforchannel = roomtypecodeforchannel;
           this.roomtypeid = roomtypeid;
           this.roomtypename = roomtypename;
           this.specialrequest = specialrequest;
           this.telephone = telephone;
           this.title = title;
           this.totalamount = totalamount;
           this.exOrderItems = exOrderItems;
           this.specialnote = specialnote;
           this.createtime = createtime;
           this.modifiedtime = modifiedtime;
           this.firstDayPrice = firstDayPrice;
    }


    /**
     * Gets the orderid value for this Order.
     * 
     * @return orderid
     */
    public java.lang.Long getOrderid() {
        return orderid;
    }


    /**
     * Sets the orderid value for this Order.
     * 
     * @param orderid
     */
    public void setOrderid(java.lang.Long orderid) {
        this.orderid = orderid;
    }


    /**
     * Gets the adultnum value for this Order.
     * 
     * @return adultnum
     */
    public int getAdultnum() {
        return adultnum;
    }


    /**
     * Sets the adultnum value for this Order.
     * 
     * @param adultnum
     */
    public void setAdultnum(int adultnum) {
        this.adultnum = adultnum;
    }


    /**
     * Gets the arrivaltime value for this Order.
     * 
     * @return arrivaltime
     */
    public java.lang.String getArrivaltime() {
        return arrivaltime;
    }


    /**
     * Sets the arrivaltime value for this Order.
     * 
     * @param arrivaltime
     */
    public void setArrivaltime(java.lang.String arrivaltime) {
        this.arrivaltime = arrivaltime;
    }


    /**
     * Gets the arrivaltraffic value for this Order.
     * 
     * @return arrivaltraffic
     */
    public java.lang.String getArrivaltraffic() {
        return arrivaltraffic;
    }


    /**
     * Sets the arrivaltraffic value for this Order.
     * 
     * @param arrivaltraffic
     */
    public void setArrivaltraffic(java.lang.String arrivaltraffic) {
        this.arrivaltraffic = arrivaltraffic;
    }


    /**
     * Gets the bedtype value for this Order.
     * 
     * @return bedtype
     */
    public java.lang.Integer getBedtype() {
        return bedtype;
    }


    /**
     * Sets the bedtype value for this Order.
     * 
     * @param bedtype
     */
    public void setBedtype(java.lang.Integer bedtype) {
        this.bedtype = bedtype;
    }


    /**
     * Gets the checkindate value for this Order.
     * 
     * @return checkindate
     */
    public java.lang.String getCheckindate() {
        return checkindate;
    }


    /**
     * Sets the checkindate value for this Order.
     * 
     * @param checkindate
     */
    public void setCheckindate(java.lang.String checkindate) {
        this.checkindate = checkindate;
    }


    /**
     * Gets the checkoutdate value for this Order.
     * 
     * @return checkoutdate
     */
    public java.lang.String getCheckoutdate() {
        return checkoutdate;
    }


    /**
     * Sets the checkoutdate value for this Order.
     * 
     * @param checkoutdate
     */
    public void setCheckoutdate(java.lang.String checkoutdate) {
        this.checkoutdate = checkoutdate;
    }


    /**
     * Gets the createdate value for this Order.
     * 
     * @return createdate
     */
    public java.lang.String getCreatedate() {
        return createdate;
    }


    /**
     * Sets the createdate value for this Order.
     * 
     * @param createdate
     */
    public void setCreatedate(java.lang.String createdate) {
        this.createdate = createdate;
    }


    /**
     * Gets the creditcardexpires value for this Order.
     * 
     * @return creditcardexpires
     */
    public java.lang.String getCreditcardexpires() {
        return creditcardexpires;
    }


    /**
     * Sets the creditcardexpires value for this Order.
     * 
     * @param creditcardexpires
     */
    public void setCreditcardexpires(java.lang.String creditcardexpires) {
        this.creditcardexpires = creditcardexpires;
    }


    /**
     * Gets the creditcardname value for this Order.
     * 
     * @return creditcardname
     */
    public java.lang.String getCreditcardname() {
        return creditcardname;
    }


    /**
     * Sets the creditcardname value for this Order.
     * 
     * @param creditcardname
     */
    public void setCreditcardname(java.lang.String creditcardname) {
        this.creditcardname = creditcardname;
    }


    /**
     * Gets the creditcardno value for this Order.
     * 
     * @return creditcardno
     */
    public java.lang.String getCreditcardno() {
        return creditcardno;
    }


    /**
     * Sets the creditcardno value for this Order.
     * 
     * @param creditcardno
     */
    public void setCreditcardno(java.lang.String creditcardno) {
        this.creditcardno = creditcardno;
    }


    /**
     * Gets the creditcardtype value for this Order.
     * 
     * @return creditcardtype
     */
    public java.lang.String getCreditcardtype() {
        return creditcardtype;
    }


    /**
     * Sets the creditcardtype value for this Order.
     * 
     * @param creditcardtype
     */
    public void setCreditcardtype(java.lang.String creditcardtype) {
        this.creditcardtype = creditcardtype;
    }


    /**
     * Gets the currency value for this Order.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this Order.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the customerfax value for this Order.
     * 
     * @return customerfax
     */
    public java.lang.String getCustomerfax() {
        return customerfax;
    }


    /**
     * Sets the customerfax value for this Order.
     * 
     * @param customerfax
     */
    public void setCustomerfax(java.lang.String customerfax) {
        this.customerfax = customerfax;
    }


    /**
     * Gets the email value for this Order.
     * 
     * @return email
     */
    public java.lang.String getEmail() {
        return email;
    }


    /**
     * Sets the email value for this Order.
     * 
     * @param email
     */
    public void setEmail(java.lang.String email) {
        this.email = email;
    }


    /**
     * Gets the exchangerate value for this Order.
     * 
     * @return exchangerate
     */
    public float getExchangerate() {
        return exchangerate;
    }


    /**
     * Sets the exchangerate value for this Order.
     * 
     * @param exchangerate
     */
    public void setExchangerate(float exchangerate) {
        this.exchangerate = exchangerate;
    }


    /**
     * Gets the fellownames value for this Order.
     * 
     * @return fellownames
     */
    public java.lang.String getFellownames() {
        return fellownames;
    }


    /**
     * Sets the fellownames value for this Order.
     * 
     * @param fellownames
     */
    public void setFellownames(java.lang.String fellownames) {
        this.fellownames = fellownames;
    }


    /**
     * Gets the flight value for this Order.
     * 
     * @return flight
     */
    public java.lang.String getFlight() {
        return flight;
    }


    /**
     * Sets the flight value for this Order.
     * 
     * @param flight
     */
    public void setFlight(java.lang.String flight) {
        this.flight = flight;
    }


    /**
     * Gets the guaranteetype value for this Order.
     * 
     * @return guaranteetype
     */
    public java.lang.String getGuaranteetype() {
        return guaranteetype;
    }


    /**
     * Sets the guaranteetype value for this Order.
     * 
     * @param guaranteetype
     */
    public void setGuaranteetype(java.lang.String guaranteetype) {
        this.guaranteetype = guaranteetype;
    }


    /**
     * Gets the hotelcode value for this Order.
     * 
     * @return hotelcode
     */
    public java.lang.String getHotelcode() {
        return hotelcode;
    }


    /**
     * Sets the hotelcode value for this Order.
     * 
     * @param hotelcode
     */
    public void setHotelcode(java.lang.String hotelcode) {
        this.hotelcode = hotelcode;
    }


    /**
     * Gets the hotelcodeforchannel value for this Order.
     * 
     * @return hotelcodeforchannel
     */
    public java.lang.String getHotelcodeforchannel() {
        return hotelcodeforchannel;
    }


    /**
     * Sets the hotelcodeforchannel value for this Order.
     * 
     * @param hotelcodeforchannel
     */
    public void setHotelcodeforchannel(java.lang.String hotelcodeforchannel) {
        this.hotelcodeforchannel = hotelcodeforchannel;
    }


    /**
     * Gets the hotelid value for this Order.
     * 
     * @return hotelid
     */
    public java.lang.Long getHotelid() {
        return hotelid;
    }


    /**
     * Sets the hotelid value for this Order.
     * 
     * @param hotelid
     */
    public void setHotelid(java.lang.Long hotelid) {
        this.hotelid = hotelid;
    }


    /**
     * Gets the hotelname value for this Order.
     * 
     * @return hotelname
     */
    public java.lang.String getHotelname() {
        return hotelname;
    }


    /**
     * Sets the hotelname value for this Order.
     * 
     * @param hotelname
     */
    public void setHotelname(java.lang.String hotelname) {
        this.hotelname = hotelname;
    }


    /**
     * Gets the hotelnotes value for this Order.
     * 
     * @return hotelnotes
     */
    public java.lang.String getHotelnotes() {
        return hotelnotes;
    }


    /**
     * Sets the hotelnotes value for this Order.
     * 
     * @param hotelnotes
     */
    public void setHotelnotes(java.lang.String hotelnotes) {
        this.hotelnotes = hotelnotes;
    }


    /**
     * Gets the isguarantee value for this Order.
     * 
     * @return isguarantee
     */
    public java.lang.String getIsguarantee() {
        return isguarantee;
    }


    /**
     * Sets the isguarantee value for this Order.
     * 
     * @param isguarantee
     */
    public void setIsguarantee(java.lang.String isguarantee) {
        this.isguarantee = isguarantee;
    }


    /**
     * Gets the latestarrivaltime value for this Order.
     * 
     * @return latestarrivaltime
     */
    public java.lang.String getLatestarrivaltime() {
        return latestarrivaltime;
    }


    /**
     * Sets the latestarrivaltime value for this Order.
     * 
     * @param latestarrivaltime
     */
    public void setLatestarrivaltime(java.lang.String latestarrivaltime) {
        this.latestarrivaltime = latestarrivaltime;
    }


    /**
     * Gets the linkman value for this Order.
     * 
     * @return linkman
     */
    public java.lang.String getLinkman() {
        return linkman;
    }


    /**
     * Sets the linkman value for this Order.
     * 
     * @param linkman
     */
    public void setLinkman(java.lang.String linkman) {
        this.linkman = linkman;
    }


    /**
     * Gets the hotelconfirm value for this Order.
     * 
     * @return hotelconfirm
     */
    public java.lang.Integer getHotelconfirm() {
        return hotelconfirm;
    }


    /**
     * Sets the hotelconfirm value for this Order.
     * 
     * @param hotelconfirm
     */
    public void setHotelconfirm(java.lang.Integer hotelconfirm) {
        this.hotelconfirm = hotelconfirm;
    }


    /**
     * Gets the hotelconfirmid value for this Order.
     * 
     * @return hotelconfirmid
     */
    public java.lang.String getHotelconfirmid() {
        return hotelconfirmid;
    }


    /**
     * Sets the hotelconfirmid value for this Order.
     * 
     * @param hotelconfirmid
     */
    public void setHotelconfirmid(java.lang.String hotelconfirmid) {
        this.hotelconfirmid = hotelconfirmid;
    }


    /**
     * Gets the localtotalamout value for this Order.
     * 
     * @return localtotalamout
     */
    public float getLocaltotalamout() {
        return localtotalamout;
    }


    /**
     * Sets the localtotalamout value for this Order.
     * 
     * @param localtotalamout
     */
    public void setLocaltotalamout(float localtotalamout) {
        this.localtotalamout = localtotalamout;
    }


    /**
     * Gets the orderstate value for this Order.
     * 
     * @return orderstate
     */
    public java.lang.String getOrderstate() {
        return orderstate;
    }


    /**
     * Sets the orderstate value for this Order.
     * 
     * @param orderstate
     */
    public void setOrderstate(java.lang.String orderstate) {
        this.orderstate = orderstate;
    }


    /**
     * Gets the mobile value for this Order.
     * 
     * @return mobile
     */
    public java.lang.String getMobile() {
        return mobile;
    }


    /**
     * Sets the mobile value for this Order.
     * 
     * @param mobile
     */
    public void setMobile(java.lang.String mobile) {
        this.mobile = mobile;
    }


    /**
     * Gets the noshowreason value for this Order.
     * 
     * @return noshowreason
     */
    public java.lang.String getNoshowreason() {
        return noshowreason;
    }


    /**
     * Sets the noshowreason value for this Order.
     * 
     * @param noshowreason
     */
    public void setNoshowreason(java.lang.String noshowreason) {
        this.noshowreason = noshowreason;
    }


    /**
     * Gets the nosmoking value for this Order.
     * 
     * @return nosmoking
     */
    public java.lang.String getNosmoking() {
        return nosmoking;
    }


    /**
     * Sets the nosmoking value for this Order.
     * 
     * @param nosmoking
     */
    public void setNosmoking(java.lang.String nosmoking) {
        this.nosmoking = nosmoking;
    }


    /**
     * Gets the ordercd value for this Order.
     * 
     * @return ordercd
     */
    public java.lang.String getOrdercd() {
        return ordercd;
    }


    /**
     * Sets the ordercd value for this Order.
     * 
     * @param ordercd
     */
    public void setOrdercd(java.lang.String ordercd) {
        this.ordercd = ordercd;
    }


    /**
     * Gets the ordercdhotel value for this Order.
     * 
     * @return ordercdhotel
     */
    public java.lang.String getOrdercdhotel() {
        return ordercdhotel;
    }


    /**
     * Sets the ordercdhotel value for this Order.
     * 
     * @param ordercdhotel
     */
    public void setOrdercdhotel(java.lang.String ordercdhotel) {
        this.ordercdhotel = ordercdhotel;
    }


    /**
     * Gets the paymethod value for this Order.
     * 
     * @return paymethod
     */
    public java.lang.String getPaymethod() {
        return paymethod;
    }


    /**
     * Sets the paymethod value for this Order.
     * 
     * @param paymethod
     */
    public void setPaymethod(java.lang.String paymethod) {
        this.paymethod = paymethod;
    }


    /**
     * Gets the paysatus value for this Order.
     * 
     * @return paysatus
     */
    public java.lang.String getPaysatus() {
        return paysatus;
    }


    /**
     * Sets the paysatus value for this Order.
     * 
     * @param paysatus
     */
    public void setPaysatus(java.lang.String paysatus) {
        this.paysatus = paysatus;
    }


    /**
     * Gets the pricetypecode value for this Order.
     * 
     * @return pricetypecode
     */
    public int getPricetypecode() {
        return pricetypecode;
    }


    /**
     * Sets the pricetypecode value for this Order.
     * 
     * @param pricetypecode
     */
    public void setPricetypecode(int pricetypecode) {
        this.pricetypecode = pricetypecode;
    }


    /**
     * Gets the rateplancode value for this Order.
     * 
     * @return rateplancode
     */
    public java.lang.String getRateplancode() {
        return rateplancode;
    }


    /**
     * Sets the rateplancode value for this Order.
     * 
     * @param rateplancode
     */
    public void setRateplancode(java.lang.String rateplancode) {
        this.rateplancode = rateplancode;
    }


    /**
     * Gets the roomquantity value for this Order.
     * 
     * @return roomquantity
     */
    public int getRoomquantity() {
        return roomquantity;
    }


    /**
     * Sets the roomquantity value for this Order.
     * 
     * @param roomquantity
     */
    public void setRoomquantity(int roomquantity) {
        this.roomquantity = roomquantity;
    }


    /**
     * Gets the roomtypecode value for this Order.
     * 
     * @return roomtypecode
     */
    public java.lang.Integer getRoomtypecode() {
        return roomtypecode;
    }


    /**
     * Sets the roomtypecode value for this Order.
     * 
     * @param roomtypecode
     */
    public void setRoomtypecode(java.lang.Integer roomtypecode) {
        this.roomtypecode = roomtypecode;
    }


    /**
     * Gets the roomtypecodeforchannel value for this Order.
     * 
     * @return roomtypecodeforchannel
     */
    public java.lang.String getRoomtypecodeforchannel() {
        return roomtypecodeforchannel;
    }


    /**
     * Sets the roomtypecodeforchannel value for this Order.
     * 
     * @param roomtypecodeforchannel
     */
    public void setRoomtypecodeforchannel(java.lang.String roomtypecodeforchannel) {
        this.roomtypecodeforchannel = roomtypecodeforchannel;
    }


    /**
     * Gets the roomtypeid value for this Order.
     * 
     * @return roomtypeid
     */
    public java.lang.Integer getRoomtypeid() {
        return roomtypeid;
    }


    /**
     * Sets the roomtypeid value for this Order.
     * 
     * @param roomtypeid
     */
    public void setRoomtypeid(java.lang.Integer roomtypeid) {
        this.roomtypeid = roomtypeid;
    }


    /**
     * Gets the roomtypename value for this Order.
     * 
     * @return roomtypename
     */
    public java.lang.String getRoomtypename() {
        return roomtypename;
    }


    /**
     * Sets the roomtypename value for this Order.
     * 
     * @param roomtypename
     */
    public void setRoomtypename(java.lang.String roomtypename) {
        this.roomtypename = roomtypename;
    }


    /**
     * Gets the specialrequest value for this Order.
     * 
     * @return specialrequest
     */
    public java.lang.String getSpecialrequest() {
        return specialrequest;
    }


    /**
     * Sets the specialrequest value for this Order.
     * 
     * @param specialrequest
     */
    public void setSpecialrequest(java.lang.String specialrequest) {
        this.specialrequest = specialrequest;
    }


    /**
     * Gets the telephone value for this Order.
     * 
     * @return telephone
     */
    public java.lang.String getTelephone() {
        return telephone;
    }


    /**
     * Sets the telephone value for this Order.
     * 
     * @param telephone
     */
    public void setTelephone(java.lang.String telephone) {
        this.telephone = telephone;
    }


    /**
     * Gets the title value for this Order.
     * 
     * @return title
     */
    public java.lang.String getTitle() {
        return title;
    }


    /**
     * Sets the title value for this Order.
     * 
     * @param title
     */
    public void setTitle(java.lang.String title) {
        this.title = title;
    }


    /**
     * Gets the totalamount value for this Order.
     * 
     * @return totalamount
     */
    public float getTotalamount() {
        return totalamount;
    }


    /**
     * Sets the totalamount value for this Order.
     * 
     * @param totalamount
     */
    public void setTotalamount(float totalamount) {
        this.totalamount = totalamount;
    }


    /**
     * Gets the exOrderItems value for this Order.
     * 
     * @return exOrderItems
     */
    public com.mango.hotel.OrderItem[] getExOrderItems() {
        return exOrderItems;
    }


    /**
     * Sets the exOrderItems value for this Order.
     * 
     * @param exOrderItems
     */
    public void setExOrderItems(com.mango.hotel.OrderItem[] exOrderItems) {
        this.exOrderItems = exOrderItems;
    }

    public com.mango.hotel.OrderItem getExOrderItems(int i) {
        return this.exOrderItems[i];
    }

    public void setExOrderItems(int i, com.mango.hotel.OrderItem _value) {
        this.exOrderItems[i] = _value;
    }


    /**
     * Gets the specialnote value for this Order.
     * 
     * @return specialnote
     */
    public java.lang.String getSpecialnote() {
        return specialnote;
    }


    /**
     * Sets the specialnote value for this Order.
     * 
     * @param specialnote
     */
    public void setSpecialnote(java.lang.String specialnote) {
        this.specialnote = specialnote;
    }


    /**
     * Gets the createtime value for this Order.
     * 
     * @return createtime
     */
    public java.lang.String getCreatetime() {
        return createtime;
    }


    /**
     * Sets the createtime value for this Order.
     * 
     * @param createtime
     */
    public void setCreatetime(java.lang.String createtime) {
        this.createtime = createtime;
    }


    /**
     * Gets the modifiedtime value for this Order.
     * 
     * @return modifiedtime
     */
    public java.lang.String getModifiedtime() {
        return modifiedtime;
    }


    /**
     * Sets the modifiedtime value for this Order.
     * 
     * @param modifiedtime
     */
    public void setModifiedtime(java.lang.String modifiedtime) {
        this.modifiedtime = modifiedtime;
    }


    /**
     * Gets the firstDayPrice value for this Order.
     * 
     * @return firstDayPrice
     */
    public float getFirstDayPrice() {
        return firstDayPrice;
    }


    /**
     * Sets the firstDayPrice value for this Order.
     * 
     * @param firstDayPrice
     */
    public void setFirstDayPrice(float firstDayPrice) {
        this.firstDayPrice = firstDayPrice;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof Order)) return false;
        Order other = (Order) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.orderid==null && other.getOrderid()==null) || 
             (this.orderid!=null &&
              this.orderid.equals(other.getOrderid()))) &&
            this.adultnum == other.getAdultnum() &&
            ((this.arrivaltime==null && other.getArrivaltime()==null) || 
             (this.arrivaltime!=null &&
              this.arrivaltime.equals(other.getArrivaltime()))) &&
            ((this.arrivaltraffic==null && other.getArrivaltraffic()==null) || 
             (this.arrivaltraffic!=null &&
              this.arrivaltraffic.equals(other.getArrivaltraffic()))) &&
            ((this.bedtype==null && other.getBedtype()==null) || 
             (this.bedtype!=null &&
              this.bedtype.equals(other.getBedtype()))) &&
            ((this.checkindate==null && other.getCheckindate()==null) || 
             (this.checkindate!=null &&
              this.checkindate.equals(other.getCheckindate()))) &&
            ((this.checkoutdate==null && other.getCheckoutdate()==null) || 
             (this.checkoutdate!=null &&
              this.checkoutdate.equals(other.getCheckoutdate()))) &&
            ((this.createdate==null && other.getCreatedate()==null) || 
             (this.createdate!=null &&
              this.createdate.equals(other.getCreatedate()))) &&
            ((this.creditcardexpires==null && other.getCreditcardexpires()==null) || 
             (this.creditcardexpires!=null &&
              this.creditcardexpires.equals(other.getCreditcardexpires()))) &&
            ((this.creditcardname==null && other.getCreditcardname()==null) || 
             (this.creditcardname!=null &&
              this.creditcardname.equals(other.getCreditcardname()))) &&
            ((this.creditcardno==null && other.getCreditcardno()==null) || 
             (this.creditcardno!=null &&
              this.creditcardno.equals(other.getCreditcardno()))) &&
            ((this.creditcardtype==null && other.getCreditcardtype()==null) || 
             (this.creditcardtype!=null &&
              this.creditcardtype.equals(other.getCreditcardtype()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.customerfax==null && other.getCustomerfax()==null) || 
             (this.customerfax!=null &&
              this.customerfax.equals(other.getCustomerfax()))) &&
            ((this.email==null && other.getEmail()==null) || 
             (this.email!=null &&
              this.email.equals(other.getEmail()))) &&
            this.exchangerate == other.getExchangerate() &&
            ((this.fellownames==null && other.getFellownames()==null) || 
             (this.fellownames!=null &&
              this.fellownames.equals(other.getFellownames()))) &&
            ((this.flight==null && other.getFlight()==null) || 
             (this.flight!=null &&
              this.flight.equals(other.getFlight()))) &&
            ((this.guaranteetype==null && other.getGuaranteetype()==null) || 
             (this.guaranteetype!=null &&
              this.guaranteetype.equals(other.getGuaranteetype()))) &&
            ((this.hotelcode==null && other.getHotelcode()==null) || 
             (this.hotelcode!=null &&
              this.hotelcode.equals(other.getHotelcode()))) &&
            ((this.hotelcodeforchannel==null && other.getHotelcodeforchannel()==null) || 
             (this.hotelcodeforchannel!=null &&
              this.hotelcodeforchannel.equals(other.getHotelcodeforchannel()))) &&
            ((this.hotelid==null && other.getHotelid()==null) || 
             (this.hotelid!=null &&
              this.hotelid.equals(other.getHotelid()))) &&
            ((this.hotelname==null && other.getHotelname()==null) || 
             (this.hotelname!=null &&
              this.hotelname.equals(other.getHotelname()))) &&
            ((this.hotelnotes==null && other.getHotelnotes()==null) || 
             (this.hotelnotes!=null &&
              this.hotelnotes.equals(other.getHotelnotes()))) &&
            ((this.isguarantee==null && other.getIsguarantee()==null) || 
             (this.isguarantee!=null &&
              this.isguarantee.equals(other.getIsguarantee()))) &&
            ((this.latestarrivaltime==null && other.getLatestarrivaltime()==null) || 
             (this.latestarrivaltime!=null &&
              this.latestarrivaltime.equals(other.getLatestarrivaltime()))) &&
            ((this.linkman==null && other.getLinkman()==null) || 
             (this.linkman!=null &&
              this.linkman.equals(other.getLinkman()))) &&
            ((this.hotelconfirm==null && other.getHotelconfirm()==null) || 
             (this.hotelconfirm!=null &&
              this.hotelconfirm.equals(other.getHotelconfirm()))) &&
            ((this.hotelconfirmid==null && other.getHotelconfirmid()==null) || 
             (this.hotelconfirmid!=null &&
              this.hotelconfirmid.equals(other.getHotelconfirmid()))) &&
            this.localtotalamout == other.getLocaltotalamout() &&
            ((this.orderstate==null && other.getOrderstate()==null) || 
             (this.orderstate!=null &&
              this.orderstate.equals(other.getOrderstate()))) &&
            ((this.mobile==null && other.getMobile()==null) || 
             (this.mobile!=null &&
              this.mobile.equals(other.getMobile()))) &&
            ((this.noshowreason==null && other.getNoshowreason()==null) || 
             (this.noshowreason!=null &&
              this.noshowreason.equals(other.getNoshowreason()))) &&
            ((this.nosmoking==null && other.getNosmoking()==null) || 
             (this.nosmoking!=null &&
              this.nosmoking.equals(other.getNosmoking()))) &&
            ((this.ordercd==null && other.getOrdercd()==null) || 
             (this.ordercd!=null &&
              this.ordercd.equals(other.getOrdercd()))) &&
            ((this.ordercdhotel==null && other.getOrdercdhotel()==null) || 
             (this.ordercdhotel!=null &&
              this.ordercdhotel.equals(other.getOrdercdhotel()))) &&
            ((this.paymethod==null && other.getPaymethod()==null) || 
             (this.paymethod!=null &&
              this.paymethod.equals(other.getPaymethod()))) &&
            ((this.paysatus==null && other.getPaysatus()==null) || 
             (this.paysatus!=null &&
              this.paysatus.equals(other.getPaysatus()))) &&
            this.pricetypecode == other.getPricetypecode() &&
            ((this.rateplancode==null && other.getRateplancode()==null) || 
             (this.rateplancode!=null &&
              this.rateplancode.equals(other.getRateplancode()))) &&
            this.roomquantity == other.getRoomquantity() &&
            ((this.roomtypecode==null && other.getRoomtypecode()==null) || 
             (this.roomtypecode!=null &&
              this.roomtypecode.equals(other.getRoomtypecode()))) &&
            ((this.roomtypecodeforchannel==null && other.getRoomtypecodeforchannel()==null) || 
             (this.roomtypecodeforchannel!=null &&
              this.roomtypecodeforchannel.equals(other.getRoomtypecodeforchannel()))) &&
            ((this.roomtypeid==null && other.getRoomtypeid()==null) || 
             (this.roomtypeid!=null &&
              this.roomtypeid.equals(other.getRoomtypeid()))) &&
            ((this.roomtypename==null && other.getRoomtypename()==null) || 
             (this.roomtypename!=null &&
              this.roomtypename.equals(other.getRoomtypename()))) &&
            ((this.specialrequest==null && other.getSpecialrequest()==null) || 
             (this.specialrequest!=null &&
              this.specialrequest.equals(other.getSpecialrequest()))) &&
            ((this.telephone==null && other.getTelephone()==null) || 
             (this.telephone!=null &&
              this.telephone.equals(other.getTelephone()))) &&
            ((this.title==null && other.getTitle()==null) || 
             (this.title!=null &&
              this.title.equals(other.getTitle()))) &&
            this.totalamount == other.getTotalamount() &&
            ((this.exOrderItems==null && other.getExOrderItems()==null) || 
             (this.exOrderItems!=null &&
              java.util.Arrays.equals(this.exOrderItems, other.getExOrderItems()))) &&
            ((this.specialnote==null && other.getSpecialnote()==null) || 
             (this.specialnote!=null &&
              this.specialnote.equals(other.getSpecialnote()))) &&
            ((this.createtime==null && other.getCreatetime()==null) || 
             (this.createtime!=null &&
              this.createtime.equals(other.getCreatetime()))) &&
            ((this.modifiedtime==null && other.getModifiedtime()==null) || 
             (this.modifiedtime!=null &&
              this.modifiedtime.equals(other.getModifiedtime()))) &&
            this.firstDayPrice == other.getFirstDayPrice();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getOrderid() != null) {
            _hashCode += getOrderid().hashCode();
        }
        _hashCode += getAdultnum();
        if (getArrivaltime() != null) {
            _hashCode += getArrivaltime().hashCode();
        }
        if (getArrivaltraffic() != null) {
            _hashCode += getArrivaltraffic().hashCode();
        }
        if (getBedtype() != null) {
            _hashCode += getBedtype().hashCode();
        }
        if (getCheckindate() != null) {
            _hashCode += getCheckindate().hashCode();
        }
        if (getCheckoutdate() != null) {
            _hashCode += getCheckoutdate().hashCode();
        }
        if (getCreatedate() != null) {
            _hashCode += getCreatedate().hashCode();
        }
        if (getCreditcardexpires() != null) {
            _hashCode += getCreditcardexpires().hashCode();
        }
        if (getCreditcardname() != null) {
            _hashCode += getCreditcardname().hashCode();
        }
        if (getCreditcardno() != null) {
            _hashCode += getCreditcardno().hashCode();
        }
        if (getCreditcardtype() != null) {
            _hashCode += getCreditcardtype().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getCustomerfax() != null) {
            _hashCode += getCustomerfax().hashCode();
        }
        if (getEmail() != null) {
            _hashCode += getEmail().hashCode();
        }
        _hashCode += new Float(getExchangerate()).hashCode();
        if (getFellownames() != null) {
            _hashCode += getFellownames().hashCode();
        }
        if (getFlight() != null) {
            _hashCode += getFlight().hashCode();
        }
        if (getGuaranteetype() != null) {
            _hashCode += getGuaranteetype().hashCode();
        }
        if (getHotelcode() != null) {
            _hashCode += getHotelcode().hashCode();
        }
        if (getHotelcodeforchannel() != null) {
            _hashCode += getHotelcodeforchannel().hashCode();
        }
        if (getHotelid() != null) {
            _hashCode += getHotelid().hashCode();
        }
        if (getHotelname() != null) {
            _hashCode += getHotelname().hashCode();
        }
        if (getHotelnotes() != null) {
            _hashCode += getHotelnotes().hashCode();
        }
        if (getIsguarantee() != null) {
            _hashCode += getIsguarantee().hashCode();
        }
        if (getLatestarrivaltime() != null) {
            _hashCode += getLatestarrivaltime().hashCode();
        }
        if (getLinkman() != null) {
            _hashCode += getLinkman().hashCode();
        }
        if (getHotelconfirm() != null) {
            _hashCode += getHotelconfirm().hashCode();
        }
        if (getHotelconfirmid() != null) {
            _hashCode += getHotelconfirmid().hashCode();
        }
        _hashCode += new Float(getLocaltotalamout()).hashCode();
        if (getOrderstate() != null) {
            _hashCode += getOrderstate().hashCode();
        }
        if (getMobile() != null) {
            _hashCode += getMobile().hashCode();
        }
        if (getNoshowreason() != null) {
            _hashCode += getNoshowreason().hashCode();
        }
        if (getNosmoking() != null) {
            _hashCode += getNosmoking().hashCode();
        }
        if (getOrdercd() != null) {
            _hashCode += getOrdercd().hashCode();
        }
        if (getOrdercdhotel() != null) {
            _hashCode += getOrdercdhotel().hashCode();
        }
        if (getPaymethod() != null) {
            _hashCode += getPaymethod().hashCode();
        }
        if (getPaysatus() != null) {
            _hashCode += getPaysatus().hashCode();
        }
        _hashCode += getPricetypecode();
        if (getRateplancode() != null) {
            _hashCode += getRateplancode().hashCode();
        }
        _hashCode += getRoomquantity();
        if (getRoomtypecode() != null) {
            _hashCode += getRoomtypecode().hashCode();
        }
        if (getRoomtypecodeforchannel() != null) {
            _hashCode += getRoomtypecodeforchannel().hashCode();
        }
        if (getRoomtypeid() != null) {
            _hashCode += getRoomtypeid().hashCode();
        }
        if (getRoomtypename() != null) {
            _hashCode += getRoomtypename().hashCode();
        }
        if (getSpecialrequest() != null) {
            _hashCode += getSpecialrequest().hashCode();
        }
        if (getTelephone() != null) {
            _hashCode += getTelephone().hashCode();
        }
        if (getTitle() != null) {
            _hashCode += getTitle().hashCode();
        }
        _hashCode += new Float(getTotalamount()).hashCode();
        if (getExOrderItems() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getExOrderItems());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getExOrderItems(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getSpecialnote() != null) {
            _hashCode += getSpecialnote().hashCode();
        }
        if (getCreatetime() != null) {
            _hashCode += getCreatetime().hashCode();
        }
        if (getModifiedtime() != null) {
            _hashCode += getModifiedtime().hashCode();
        }
        _hashCode += new Float(getFirstDayPrice()).hashCode();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(Order.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "Order"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "orderid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("adultnum");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "adultnum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arrivaltime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "arrivaltime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arrivaltraffic");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "arrivaltraffic"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bedtype");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "bedtype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkindate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "checkindate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkoutdate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "checkoutdate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createdate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "createdate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditcardexpires");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "creditcardexpires"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditcardname");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "creditcardname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditcardno");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "creditcardno"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("creditcardtype");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "creditcardtype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("customerfax");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "customerfax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("email");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "email"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exchangerate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "exchangerate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("fellownames");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "fellownames"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("flight");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "flight"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("guaranteetype");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "guaranteetype"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelcode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelcode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelcodeforchannel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelcodeforchannel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "long"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelname");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelname"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelnotes");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelnotes"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isguarantee");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "isguarantee"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("latestarrivaltime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "latestarrivaltime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("linkman");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "linkman"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelconfirm");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelconfirm"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelconfirmid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelconfirmid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("localtotalamout");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "localtotalamout"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderstate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "orderstate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mobile");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "mobile"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("noshowreason");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "noshowreason"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("nosmoking");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "nosmoking"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ordercd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ordercd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ordercdhotel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ordercdhotel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paymethod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "paymethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("paysatus");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "paysatus"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pricetypecode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "pricetypecode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rateplancode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "rateplancode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomquantity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomquantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomtypecode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomtypecode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomtypecodeforchannel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomtypecodeforchannel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomtypeid");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomtypeid"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomtypename");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomtypename"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("specialrequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "specialrequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("telephone");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "telephone"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("title");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "title"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalamount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "totalamount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("exOrderItems");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "exOrderItems"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "OrderItem"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("specialnote");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "specialnote"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("createtime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "createtime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modifiedtime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "modifiedtime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("firstDayPrice");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "firstDayPrice"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "float"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
