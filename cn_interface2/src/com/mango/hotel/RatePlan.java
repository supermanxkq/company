/**
 * RatePlan.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class RatePlan  implements java.io.Serializable {
    private java.lang.String rateplanCode;

    private java.lang.String roomTypeCode;

    private java.lang.String rateplanName;

    private java.lang.String payMethod;

    private java.lang.String markert_price;

    private java.lang.String currencyType;

    private java.lang.String sale_price;

    private java.lang.String ableSaleDate;

    private java.lang.String breakType;

    private java.lang.String breakNum;

    private java.lang.String taxChange;

    private java.lang.String need_assure;

    private java.lang.String assure_type;

    private java.lang.String assure_con;

    private java.lang.String assure_money;

    private java.lang.String modify_cancel_msg;

    private java.lang.String pay_to_prepay;

    private java.lang.String latest_bookable_date;

    private java.lang.String latest_bookable_time;

    private java.lang.String must_last_date;

    private java.lang.String must_first_date;

    private java.lang.String continue_day;

    private java.lang.String must_in_date;

    private java.lang.String has_reserv;

    private java.lang.String min_restrict_nights;

    private java.lang.String max_restrict_nights;

    private java.lang.String continue_dates_relation;

    private java.lang.String first_bookable_date;

    private java.lang.String first_bookable_time;

    private java.lang.String assureAlertMessage;

    private int ableSum;

    private int maxBookingSum;

    private java.lang.String roomState;

    public RatePlan() {
    }

    public RatePlan(
           java.lang.String rateplanCode,
           java.lang.String roomTypeCode,
           java.lang.String rateplanName,
           java.lang.String payMethod,
           java.lang.String markert_price,
           java.lang.String currencyType,
           java.lang.String sale_price,
           java.lang.String ableSaleDate,
           java.lang.String breakType,
           java.lang.String breakNum,
           java.lang.String taxChange,
           java.lang.String need_assure,
           java.lang.String assure_type,
           java.lang.String assure_con,
           java.lang.String assure_money,
           java.lang.String modify_cancel_msg,
           java.lang.String pay_to_prepay,
           java.lang.String latest_bookable_date,
           java.lang.String latest_bookable_time,
           java.lang.String must_last_date,
           java.lang.String must_first_date,
           java.lang.String continue_day,
           java.lang.String must_in_date,
           java.lang.String has_reserv,
           java.lang.String min_restrict_nights,
           java.lang.String max_restrict_nights,
           java.lang.String continue_dates_relation,
           java.lang.String first_bookable_date,
           java.lang.String first_bookable_time,
           java.lang.String assureAlertMessage,
           int ableSum,
           int maxBookingSum,
           java.lang.String roomState) {
           this.rateplanCode = rateplanCode;
           this.roomTypeCode = roomTypeCode;
           this.rateplanName = rateplanName;
           this.payMethod = payMethod;
           this.markert_price = markert_price;
           this.currencyType = currencyType;
           this.sale_price = sale_price;
           this.ableSaleDate = ableSaleDate;
           this.breakType = breakType;
           this.breakNum = breakNum;
           this.taxChange = taxChange;
           this.need_assure = need_assure;
           this.assure_type = assure_type;
           this.assure_con = assure_con;
           this.assure_money = assure_money;
           this.modify_cancel_msg = modify_cancel_msg;
           this.pay_to_prepay = pay_to_prepay;
           this.latest_bookable_date = latest_bookable_date;
           this.latest_bookable_time = latest_bookable_time;
           this.must_last_date = must_last_date;
           this.must_first_date = must_first_date;
           this.continue_day = continue_day;
           this.must_in_date = must_in_date;
           this.has_reserv = has_reserv;
           this.min_restrict_nights = min_restrict_nights;
           this.max_restrict_nights = max_restrict_nights;
           this.continue_dates_relation = continue_dates_relation;
           this.first_bookable_date = first_bookable_date;
           this.first_bookable_time = first_bookable_time;
           this.assureAlertMessage = assureAlertMessage;
           this.ableSum = ableSum;
           this.maxBookingSum = maxBookingSum;
           this.roomState = roomState;
    }


    /**
     * Gets the rateplanCode value for this RatePlan.
     * 
     * @return rateplanCode
     */
    public java.lang.String getRateplanCode() {
        return rateplanCode;
    }


    /**
     * Sets the rateplanCode value for this RatePlan.
     * 
     * @param rateplanCode
     */
    public void setRateplanCode(java.lang.String rateplanCode) {
        this.rateplanCode = rateplanCode;
    }


    /**
     * Gets the roomTypeCode value for this RatePlan.
     * 
     * @return roomTypeCode
     */
    public java.lang.String getRoomTypeCode() {
        return roomTypeCode;
    }


    /**
     * Sets the roomTypeCode value for this RatePlan.
     * 
     * @param roomTypeCode
     */
    public void setRoomTypeCode(java.lang.String roomTypeCode) {
        this.roomTypeCode = roomTypeCode;
    }


    /**
     * Gets the rateplanName value for this RatePlan.
     * 
     * @return rateplanName
     */
    public java.lang.String getRateplanName() {
        return rateplanName;
    }


    /**
     * Sets the rateplanName value for this RatePlan.
     * 
     * @param rateplanName
     */
    public void setRateplanName(java.lang.String rateplanName) {
        this.rateplanName = rateplanName;
    }


    /**
     * Gets the payMethod value for this RatePlan.
     * 
     * @return payMethod
     */
    public java.lang.String getPayMethod() {
        return payMethod;
    }


    /**
     * Sets the payMethod value for this RatePlan.
     * 
     * @param payMethod
     */
    public void setPayMethod(java.lang.String payMethod) {
        this.payMethod = payMethod;
    }


    /**
     * Gets the markert_price value for this RatePlan.
     * 
     * @return markert_price
     */
    public java.lang.String getMarkert_price() {
        return markert_price;
    }


    /**
     * Sets the markert_price value for this RatePlan.
     * 
     * @param markert_price
     */
    public void setMarkert_price(java.lang.String markert_price) {
        this.markert_price = markert_price;
    }


    /**
     * Gets the currencyType value for this RatePlan.
     * 
     * @return currencyType
     */
    public java.lang.String getCurrencyType() {
        return currencyType;
    }


    /**
     * Sets the currencyType value for this RatePlan.
     * 
     * @param currencyType
     */
    public void setCurrencyType(java.lang.String currencyType) {
        this.currencyType = currencyType;
    }


    /**
     * Gets the sale_price value for this RatePlan.
     * 
     * @return sale_price
     */
    public java.lang.String getSale_price() {
        return sale_price;
    }


    /**
     * Sets the sale_price value for this RatePlan.
     * 
     * @param sale_price
     */
    public void setSale_price(java.lang.String sale_price) {
        this.sale_price = sale_price;
    }


    /**
     * Gets the ableSaleDate value for this RatePlan.
     * 
     * @return ableSaleDate
     */
    public java.lang.String getAbleSaleDate() {
        return ableSaleDate;
    }


    /**
     * Sets the ableSaleDate value for this RatePlan.
     * 
     * @param ableSaleDate
     */
    public void setAbleSaleDate(java.lang.String ableSaleDate) {
        this.ableSaleDate = ableSaleDate;
    }


    /**
     * Gets the breakType value for this RatePlan.
     * 
     * @return breakType
     */
    public java.lang.String getBreakType() {
        return breakType;
    }


    /**
     * Sets the breakType value for this RatePlan.
     * 
     * @param breakType
     */
    public void setBreakType(java.lang.String breakType) {
        this.breakType = breakType;
    }


    /**
     * Gets the breakNum value for this RatePlan.
     * 
     * @return breakNum
     */
    public java.lang.String getBreakNum() {
        return breakNum;
    }


    /**
     * Sets the breakNum value for this RatePlan.
     * 
     * @param breakNum
     */
    public void setBreakNum(java.lang.String breakNum) {
        this.breakNum = breakNum;
    }


    /**
     * Gets the taxChange value for this RatePlan.
     * 
     * @return taxChange
     */
    public java.lang.String getTaxChange() {
        return taxChange;
    }


    /**
     * Sets the taxChange value for this RatePlan.
     * 
     * @param taxChange
     */
    public void setTaxChange(java.lang.String taxChange) {
        this.taxChange = taxChange;
    }


    /**
     * Gets the need_assure value for this RatePlan.
     * 
     * @return need_assure
     */
    public java.lang.String getNeed_assure() {
        return need_assure;
    }


    /**
     * Sets the need_assure value for this RatePlan.
     * 
     * @param need_assure
     */
    public void setNeed_assure(java.lang.String need_assure) {
        this.need_assure = need_assure;
    }


    /**
     * Gets the assure_type value for this RatePlan.
     * 
     * @return assure_type
     */
    public java.lang.String getAssure_type() {
        return assure_type;
    }


    /**
     * Sets the assure_type value for this RatePlan.
     * 
     * @param assure_type
     */
    public void setAssure_type(java.lang.String assure_type) {
        this.assure_type = assure_type;
    }


    /**
     * Gets the assure_con value for this RatePlan.
     * 
     * @return assure_con
     */
    public java.lang.String getAssure_con() {
        return assure_con;
    }


    /**
     * Sets the assure_con value for this RatePlan.
     * 
     * @param assure_con
     */
    public void setAssure_con(java.lang.String assure_con) {
        this.assure_con = assure_con;
    }


    /**
     * Gets the assure_money value for this RatePlan.
     * 
     * @return assure_money
     */
    public java.lang.String getAssure_money() {
        return assure_money;
    }


    /**
     * Sets the assure_money value for this RatePlan.
     * 
     * @param assure_money
     */
    public void setAssure_money(java.lang.String assure_money) {
        this.assure_money = assure_money;
    }


    /**
     * Gets the modify_cancel_msg value for this RatePlan.
     * 
     * @return modify_cancel_msg
     */
    public java.lang.String getModify_cancel_msg() {
        return modify_cancel_msg;
    }


    /**
     * Sets the modify_cancel_msg value for this RatePlan.
     * 
     * @param modify_cancel_msg
     */
    public void setModify_cancel_msg(java.lang.String modify_cancel_msg) {
        this.modify_cancel_msg = modify_cancel_msg;
    }


    /**
     * Gets the pay_to_prepay value for this RatePlan.
     * 
     * @return pay_to_prepay
     */
    public java.lang.String getPay_to_prepay() {
        return pay_to_prepay;
    }


    /**
     * Sets the pay_to_prepay value for this RatePlan.
     * 
     * @param pay_to_prepay
     */
    public void setPay_to_prepay(java.lang.String pay_to_prepay) {
        this.pay_to_prepay = pay_to_prepay;
    }


    /**
     * Gets the latest_bookable_date value for this RatePlan.
     * 
     * @return latest_bookable_date
     */
    public java.lang.String getLatest_bookable_date() {
        return latest_bookable_date;
    }


    /**
     * Sets the latest_bookable_date value for this RatePlan.
     * 
     * @param latest_bookable_date
     */
    public void setLatest_bookable_date(java.lang.String latest_bookable_date) {
        this.latest_bookable_date = latest_bookable_date;
    }


    /**
     * Gets the latest_bookable_time value for this RatePlan.
     * 
     * @return latest_bookable_time
     */
    public java.lang.String getLatest_bookable_time() {
        return latest_bookable_time;
    }


    /**
     * Sets the latest_bookable_time value for this RatePlan.
     * 
     * @param latest_bookable_time
     */
    public void setLatest_bookable_time(java.lang.String latest_bookable_time) {
        this.latest_bookable_time = latest_bookable_time;
    }


    /**
     * Gets the must_last_date value for this RatePlan.
     * 
     * @return must_last_date
     */
    public java.lang.String getMust_last_date() {
        return must_last_date;
    }


    /**
     * Sets the must_last_date value for this RatePlan.
     * 
     * @param must_last_date
     */
    public void setMust_last_date(java.lang.String must_last_date) {
        this.must_last_date = must_last_date;
    }


    /**
     * Gets the must_first_date value for this RatePlan.
     * 
     * @return must_first_date
     */
    public java.lang.String getMust_first_date() {
        return must_first_date;
    }


    /**
     * Sets the must_first_date value for this RatePlan.
     * 
     * @param must_first_date
     */
    public void setMust_first_date(java.lang.String must_first_date) {
        this.must_first_date = must_first_date;
    }


    /**
     * Gets the continue_day value for this RatePlan.
     * 
     * @return continue_day
     */
    public java.lang.String getContinue_day() {
        return continue_day;
    }


    /**
     * Sets the continue_day value for this RatePlan.
     * 
     * @param continue_day
     */
    public void setContinue_day(java.lang.String continue_day) {
        this.continue_day = continue_day;
    }


    /**
     * Gets the must_in_date value for this RatePlan.
     * 
     * @return must_in_date
     */
    public java.lang.String getMust_in_date() {
        return must_in_date;
    }


    /**
     * Sets the must_in_date value for this RatePlan.
     * 
     * @param must_in_date
     */
    public void setMust_in_date(java.lang.String must_in_date) {
        this.must_in_date = must_in_date;
    }


    /**
     * Gets the has_reserv value for this RatePlan.
     * 
     * @return has_reserv
     */
    public java.lang.String getHas_reserv() {
        return has_reserv;
    }


    /**
     * Sets the has_reserv value for this RatePlan.
     * 
     * @param has_reserv
     */
    public void setHas_reserv(java.lang.String has_reserv) {
        this.has_reserv = has_reserv;
    }


    /**
     * Gets the min_restrict_nights value for this RatePlan.
     * 
     * @return min_restrict_nights
     */
    public java.lang.String getMin_restrict_nights() {
        return min_restrict_nights;
    }


    /**
     * Sets the min_restrict_nights value for this RatePlan.
     * 
     * @param min_restrict_nights
     */
    public void setMin_restrict_nights(java.lang.String min_restrict_nights) {
        this.min_restrict_nights = min_restrict_nights;
    }


    /**
     * Gets the max_restrict_nights value for this RatePlan.
     * 
     * @return max_restrict_nights
     */
    public java.lang.String getMax_restrict_nights() {
        return max_restrict_nights;
    }


    /**
     * Sets the max_restrict_nights value for this RatePlan.
     * 
     * @param max_restrict_nights
     */
    public void setMax_restrict_nights(java.lang.String max_restrict_nights) {
        this.max_restrict_nights = max_restrict_nights;
    }


    /**
     * Gets the continue_dates_relation value for this RatePlan.
     * 
     * @return continue_dates_relation
     */
    public java.lang.String getContinue_dates_relation() {
        return continue_dates_relation;
    }


    /**
     * Sets the continue_dates_relation value for this RatePlan.
     * 
     * @param continue_dates_relation
     */
    public void setContinue_dates_relation(java.lang.String continue_dates_relation) {
        this.continue_dates_relation = continue_dates_relation;
    }


    /**
     * Gets the first_bookable_date value for this RatePlan.
     * 
     * @return first_bookable_date
     */
    public java.lang.String getFirst_bookable_date() {
        return first_bookable_date;
    }


    /**
     * Sets the first_bookable_date value for this RatePlan.
     * 
     * @param first_bookable_date
     */
    public void setFirst_bookable_date(java.lang.String first_bookable_date) {
        this.first_bookable_date = first_bookable_date;
    }


    /**
     * Gets the first_bookable_time value for this RatePlan.
     * 
     * @return first_bookable_time
     */
    public java.lang.String getFirst_bookable_time() {
        return first_bookable_time;
    }


    /**
     * Sets the first_bookable_time value for this RatePlan.
     * 
     * @param first_bookable_time
     */
    public void setFirst_bookable_time(java.lang.String first_bookable_time) {
        this.first_bookable_time = first_bookable_time;
    }


    /**
     * Gets the assureAlertMessage value for this RatePlan.
     * 
     * @return assureAlertMessage
     */
    public java.lang.String getAssureAlertMessage() {
        return assureAlertMessage;
    }


    /**
     * Sets the assureAlertMessage value for this RatePlan.
     * 
     * @param assureAlertMessage
     */
    public void setAssureAlertMessage(java.lang.String assureAlertMessage) {
        this.assureAlertMessage = assureAlertMessage;
    }


    /**
     * Gets the ableSum value for this RatePlan.
     * 
     * @return ableSum
     */
    public int getAbleSum() {
        return ableSum;
    }


    /**
     * Sets the ableSum value for this RatePlan.
     * 
     * @param ableSum
     */
    public void setAbleSum(int ableSum) {
        this.ableSum = ableSum;
    }


    /**
     * Gets the maxBookingSum value for this RatePlan.
     * 
     * @return maxBookingSum
     */
    public int getMaxBookingSum() {
        return maxBookingSum;
    }


    /**
     * Sets the maxBookingSum value for this RatePlan.
     * 
     * @param maxBookingSum
     */
    public void setMaxBookingSum(int maxBookingSum) {
        this.maxBookingSum = maxBookingSum;
    }


    /**
     * Gets the roomState value for this RatePlan.
     * 
     * @return roomState
     */
    public java.lang.String getRoomState() {
        return roomState;
    }


    /**
     * Sets the roomState value for this RatePlan.
     * 
     * @param roomState
     */
    public void setRoomState(java.lang.String roomState) {
        this.roomState = roomState;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof RatePlan)) return false;
        RatePlan other = (RatePlan) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.rateplanCode==null && other.getRateplanCode()==null) || 
             (this.rateplanCode!=null &&
              this.rateplanCode.equals(other.getRateplanCode()))) &&
            ((this.roomTypeCode==null && other.getRoomTypeCode()==null) || 
             (this.roomTypeCode!=null &&
              this.roomTypeCode.equals(other.getRoomTypeCode()))) &&
            ((this.rateplanName==null && other.getRateplanName()==null) || 
             (this.rateplanName!=null &&
              this.rateplanName.equals(other.getRateplanName()))) &&
            ((this.payMethod==null && other.getPayMethod()==null) || 
             (this.payMethod!=null &&
              this.payMethod.equals(other.getPayMethod()))) &&
            ((this.markert_price==null && other.getMarkert_price()==null) || 
             (this.markert_price!=null &&
              this.markert_price.equals(other.getMarkert_price()))) &&
            ((this.currencyType==null && other.getCurrencyType()==null) || 
             (this.currencyType!=null &&
              this.currencyType.equals(other.getCurrencyType()))) &&
            ((this.sale_price==null && other.getSale_price()==null) || 
             (this.sale_price!=null &&
              this.sale_price.equals(other.getSale_price()))) &&
            ((this.ableSaleDate==null && other.getAbleSaleDate()==null) || 
             (this.ableSaleDate!=null &&
              this.ableSaleDate.equals(other.getAbleSaleDate()))) &&
            ((this.breakType==null && other.getBreakType()==null) || 
             (this.breakType!=null &&
              this.breakType.equals(other.getBreakType()))) &&
            ((this.breakNum==null && other.getBreakNum()==null) || 
             (this.breakNum!=null &&
              this.breakNum.equals(other.getBreakNum()))) &&
            ((this.taxChange==null && other.getTaxChange()==null) || 
             (this.taxChange!=null &&
              this.taxChange.equals(other.getTaxChange()))) &&
            ((this.need_assure==null && other.getNeed_assure()==null) || 
             (this.need_assure!=null &&
              this.need_assure.equals(other.getNeed_assure()))) &&
            ((this.assure_type==null && other.getAssure_type()==null) || 
             (this.assure_type!=null &&
              this.assure_type.equals(other.getAssure_type()))) &&
            ((this.assure_con==null && other.getAssure_con()==null) || 
             (this.assure_con!=null &&
              this.assure_con.equals(other.getAssure_con()))) &&
            ((this.assure_money==null && other.getAssure_money()==null) || 
             (this.assure_money!=null &&
              this.assure_money.equals(other.getAssure_money()))) &&
            ((this.modify_cancel_msg==null && other.getModify_cancel_msg()==null) || 
             (this.modify_cancel_msg!=null &&
              this.modify_cancel_msg.equals(other.getModify_cancel_msg()))) &&
            ((this.pay_to_prepay==null && other.getPay_to_prepay()==null) || 
             (this.pay_to_prepay!=null &&
              this.pay_to_prepay.equals(other.getPay_to_prepay()))) &&
            ((this.latest_bookable_date==null && other.getLatest_bookable_date()==null) || 
             (this.latest_bookable_date!=null &&
              this.latest_bookable_date.equals(other.getLatest_bookable_date()))) &&
            ((this.latest_bookable_time==null && other.getLatest_bookable_time()==null) || 
             (this.latest_bookable_time!=null &&
              this.latest_bookable_time.equals(other.getLatest_bookable_time()))) &&
            ((this.must_last_date==null && other.getMust_last_date()==null) || 
             (this.must_last_date!=null &&
              this.must_last_date.equals(other.getMust_last_date()))) &&
            ((this.must_first_date==null && other.getMust_first_date()==null) || 
             (this.must_first_date!=null &&
              this.must_first_date.equals(other.getMust_first_date()))) &&
            ((this.continue_day==null && other.getContinue_day()==null) || 
             (this.continue_day!=null &&
              this.continue_day.equals(other.getContinue_day()))) &&
            ((this.must_in_date==null && other.getMust_in_date()==null) || 
             (this.must_in_date!=null &&
              this.must_in_date.equals(other.getMust_in_date()))) &&
            ((this.has_reserv==null && other.getHas_reserv()==null) || 
             (this.has_reserv!=null &&
              this.has_reserv.equals(other.getHas_reserv()))) &&
            ((this.min_restrict_nights==null && other.getMin_restrict_nights()==null) || 
             (this.min_restrict_nights!=null &&
              this.min_restrict_nights.equals(other.getMin_restrict_nights()))) &&
            ((this.max_restrict_nights==null && other.getMax_restrict_nights()==null) || 
             (this.max_restrict_nights!=null &&
              this.max_restrict_nights.equals(other.getMax_restrict_nights()))) &&
            ((this.continue_dates_relation==null && other.getContinue_dates_relation()==null) || 
             (this.continue_dates_relation!=null &&
              this.continue_dates_relation.equals(other.getContinue_dates_relation()))) &&
            ((this.first_bookable_date==null && other.getFirst_bookable_date()==null) || 
             (this.first_bookable_date!=null &&
              this.first_bookable_date.equals(other.getFirst_bookable_date()))) &&
            ((this.first_bookable_time==null && other.getFirst_bookable_time()==null) || 
             (this.first_bookable_time!=null &&
              this.first_bookable_time.equals(other.getFirst_bookable_time()))) &&
            ((this.assureAlertMessage==null && other.getAssureAlertMessage()==null) || 
             (this.assureAlertMessage!=null &&
              this.assureAlertMessage.equals(other.getAssureAlertMessage()))) &&
            this.ableSum == other.getAbleSum() &&
            this.maxBookingSum == other.getMaxBookingSum() &&
            ((this.roomState==null && other.getRoomState()==null) || 
             (this.roomState!=null &&
              this.roomState.equals(other.getRoomState())));
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
        if (getRateplanCode() != null) {
            _hashCode += getRateplanCode().hashCode();
        }
        if (getRoomTypeCode() != null) {
            _hashCode += getRoomTypeCode().hashCode();
        }
        if (getRateplanName() != null) {
            _hashCode += getRateplanName().hashCode();
        }
        if (getPayMethod() != null) {
            _hashCode += getPayMethod().hashCode();
        }
        if (getMarkert_price() != null) {
            _hashCode += getMarkert_price().hashCode();
        }
        if (getCurrencyType() != null) {
            _hashCode += getCurrencyType().hashCode();
        }
        if (getSale_price() != null) {
            _hashCode += getSale_price().hashCode();
        }
        if (getAbleSaleDate() != null) {
            _hashCode += getAbleSaleDate().hashCode();
        }
        if (getBreakType() != null) {
            _hashCode += getBreakType().hashCode();
        }
        if (getBreakNum() != null) {
            _hashCode += getBreakNum().hashCode();
        }
        if (getTaxChange() != null) {
            _hashCode += getTaxChange().hashCode();
        }
        if (getNeed_assure() != null) {
            _hashCode += getNeed_assure().hashCode();
        }
        if (getAssure_type() != null) {
            _hashCode += getAssure_type().hashCode();
        }
        if (getAssure_con() != null) {
            _hashCode += getAssure_con().hashCode();
        }
        if (getAssure_money() != null) {
            _hashCode += getAssure_money().hashCode();
        }
        if (getModify_cancel_msg() != null) {
            _hashCode += getModify_cancel_msg().hashCode();
        }
        if (getPay_to_prepay() != null) {
            _hashCode += getPay_to_prepay().hashCode();
        }
        if (getLatest_bookable_date() != null) {
            _hashCode += getLatest_bookable_date().hashCode();
        }
        if (getLatest_bookable_time() != null) {
            _hashCode += getLatest_bookable_time().hashCode();
        }
        if (getMust_last_date() != null) {
            _hashCode += getMust_last_date().hashCode();
        }
        if (getMust_first_date() != null) {
            _hashCode += getMust_first_date().hashCode();
        }
        if (getContinue_day() != null) {
            _hashCode += getContinue_day().hashCode();
        }
        if (getMust_in_date() != null) {
            _hashCode += getMust_in_date().hashCode();
        }
        if (getHas_reserv() != null) {
            _hashCode += getHas_reserv().hashCode();
        }
        if (getMin_restrict_nights() != null) {
            _hashCode += getMin_restrict_nights().hashCode();
        }
        if (getMax_restrict_nights() != null) {
            _hashCode += getMax_restrict_nights().hashCode();
        }
        if (getContinue_dates_relation() != null) {
            _hashCode += getContinue_dates_relation().hashCode();
        }
        if (getFirst_bookable_date() != null) {
            _hashCode += getFirst_bookable_date().hashCode();
        }
        if (getFirst_bookable_time() != null) {
            _hashCode += getFirst_bookable_time().hashCode();
        }
        if (getAssureAlertMessage() != null) {
            _hashCode += getAssureAlertMessage().hashCode();
        }
        _hashCode += getAbleSum();
        _hashCode += getMaxBookingSum();
        if (getRoomState() != null) {
            _hashCode += getRoomState().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(RatePlan.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "RatePlan"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rateplanCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "rateplanCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomTypeCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomTypeCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("rateplanName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "rateplanName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "payMethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("markert_price");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "markert_price"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currencyType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "currencyType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sale_price");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "sale_price"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ableSaleDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ableSaleDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("breakType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "breakType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("breakNum");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "breakNum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("taxChange");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "taxChange"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("need_assure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "need_assure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assure_type");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "assure_type"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assure_con");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "assure_con"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assure_money");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "assure_money"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("modify_cancel_msg");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "modify_cancel_msg"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("pay_to_prepay");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "pay_to_prepay"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("latest_bookable_date");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "latest_bookable_date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("latest_bookable_time");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "latest_bookable_time"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("must_last_date");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "must_last_date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("must_first_date");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "must_first_date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("continue_day");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "continue_day"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("must_in_date");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "must_in_date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("has_reserv");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "has_reserv"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("min_restrict_nights");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "min_restrict_nights"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("max_restrict_nights");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "max_restrict_nights"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("continue_dates_relation");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "continue_dates_relation"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("first_bookable_date");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "first_bookable_date"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("first_bookable_time");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "first_bookable_time"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("assureAlertMessage");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "assureAlertMessage"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ableSum");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ableSum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("maxBookingSum");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "maxBookingSum"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomState");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomState"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
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
