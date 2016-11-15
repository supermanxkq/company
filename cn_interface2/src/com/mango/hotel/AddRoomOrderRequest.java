/**
 * AddRoomOrderRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class AddRoomOrderRequest  implements java.io.Serializable {
    private com.mango.hotel.MangoAuthor author;

    private java.lang.String hotelCode;

    private java.lang.String hotelName;

    private java.lang.String roomTypeCode;

    private java.lang.String roomTypeName;

    private java.lang.String ratePlanCode;

    private java.lang.String ratePlanName;

    private java.lang.String checkInDate;

    private java.lang.String checkOutDate;

    private java.lang.String bedType;

    private java.lang.String currency;

    private java.lang.String totalAmount;

    private java.lang.String arriveEarlyTime;

    private java.lang.String lastArriveTime;

    private com.mango.hotel.GuestProfile[] guests;

    private com.mango.hotel.ContactorInfo[] contactors;

    private int roomquantity;

    private int guestCount;

    private java.lang.String specialRequest;

    private java.lang.String hotelNote;

    private java.lang.String payMethod;

    private java.lang.String guarantee;

    private java.lang.String creditcardname;

    private java.lang.String creditcardno;

    private java.lang.String creditcardtype;

    public AddRoomOrderRequest() {
    }

    public AddRoomOrderRequest(
           com.mango.hotel.MangoAuthor author,
           java.lang.String hotelCode,
           java.lang.String hotelName,
           java.lang.String roomTypeCode,
           java.lang.String roomTypeName,
           java.lang.String ratePlanCode,
           java.lang.String ratePlanName,
           java.lang.String checkInDate,
           java.lang.String checkOutDate,
           java.lang.String bedType,
           java.lang.String currency,
           java.lang.String totalAmount,
           java.lang.String arriveEarlyTime,
           java.lang.String lastArriveTime,
           com.mango.hotel.GuestProfile[] guests,
           com.mango.hotel.ContactorInfo[] contactors,
           int roomquantity,
           int guestCount,
           java.lang.String specialRequest,
           java.lang.String hotelNote,
           java.lang.String payMethod,
           java.lang.String guarantee,
           java.lang.String creditcardname,
           java.lang.String creditcardno,
           java.lang.String creditcardtype) {
           this.author = author;
           this.hotelCode = hotelCode;
           this.hotelName = hotelName;
           this.roomTypeCode = roomTypeCode;
           this.roomTypeName = roomTypeName;
           this.ratePlanCode = ratePlanCode;
           this.ratePlanName = ratePlanName;
           this.checkInDate = checkInDate;
           this.checkOutDate = checkOutDate;
           this.bedType = bedType;
           this.currency = currency;
           this.totalAmount = totalAmount;
           this.arriveEarlyTime = arriveEarlyTime;
           this.lastArriveTime = lastArriveTime;
           this.guests = guests;
           this.contactors = contactors;
           this.roomquantity = roomquantity;
           this.guestCount = guestCount;
           this.specialRequest = specialRequest;
           this.hotelNote = hotelNote;
           this.payMethod = payMethod;
           this.guarantee = guarantee;
           this.creditcardname = creditcardname;
           this.creditcardno = creditcardno;
           this.creditcardtype = creditcardtype;
    }


    /**
     * Gets the author value for this AddRoomOrderRequest.
     * 
     * @return author
     */
    public com.mango.hotel.MangoAuthor getAuthor() {
        return author;
    }


    /**
     * Sets the author value for this AddRoomOrderRequest.
     * 
     * @param author
     */
    public void setAuthor(com.mango.hotel.MangoAuthor author) {
        this.author = author;
    }


    /**
     * Gets the hotelCode value for this AddRoomOrderRequest.
     * 
     * @return hotelCode
     */
    public java.lang.String getHotelCode() {
        return hotelCode;
    }


    /**
     * Sets the hotelCode value for this AddRoomOrderRequest.
     * 
     * @param hotelCode
     */
    public void setHotelCode(java.lang.String hotelCode) {
        this.hotelCode = hotelCode;
    }


    /**
     * Gets the hotelName value for this AddRoomOrderRequest.
     * 
     * @return hotelName
     */
    public java.lang.String getHotelName() {
        return hotelName;
    }


    /**
     * Sets the hotelName value for this AddRoomOrderRequest.
     * 
     * @param hotelName
     */
    public void setHotelName(java.lang.String hotelName) {
        this.hotelName = hotelName;
    }


    /**
     * Gets the roomTypeCode value for this AddRoomOrderRequest.
     * 
     * @return roomTypeCode
     */
    public java.lang.String getRoomTypeCode() {
        return roomTypeCode;
    }


    /**
     * Sets the roomTypeCode value for this AddRoomOrderRequest.
     * 
     * @param roomTypeCode
     */
    public void setRoomTypeCode(java.lang.String roomTypeCode) {
        this.roomTypeCode = roomTypeCode;
    }


    /**
     * Gets the roomTypeName value for this AddRoomOrderRequest.
     * 
     * @return roomTypeName
     */
    public java.lang.String getRoomTypeName() {
        return roomTypeName;
    }


    /**
     * Sets the roomTypeName value for this AddRoomOrderRequest.
     * 
     * @param roomTypeName
     */
    public void setRoomTypeName(java.lang.String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }


    /**
     * Gets the ratePlanCode value for this AddRoomOrderRequest.
     * 
     * @return ratePlanCode
     */
    public java.lang.String getRatePlanCode() {
        return ratePlanCode;
    }


    /**
     * Sets the ratePlanCode value for this AddRoomOrderRequest.
     * 
     * @param ratePlanCode
     */
    public void setRatePlanCode(java.lang.String ratePlanCode) {
        this.ratePlanCode = ratePlanCode;
    }


    /**
     * Gets the ratePlanName value for this AddRoomOrderRequest.
     * 
     * @return ratePlanName
     */
    public java.lang.String getRatePlanName() {
        return ratePlanName;
    }


    /**
     * Sets the ratePlanName value for this AddRoomOrderRequest.
     * 
     * @param ratePlanName
     */
    public void setRatePlanName(java.lang.String ratePlanName) {
        this.ratePlanName = ratePlanName;
    }


    /**
     * Gets the checkInDate value for this AddRoomOrderRequest.
     * 
     * @return checkInDate
     */
    public java.lang.String getCheckInDate() {
        return checkInDate;
    }


    /**
     * Sets the checkInDate value for this AddRoomOrderRequest.
     * 
     * @param checkInDate
     */
    public void setCheckInDate(java.lang.String checkInDate) {
        this.checkInDate = checkInDate;
    }


    /**
     * Gets the checkOutDate value for this AddRoomOrderRequest.
     * 
     * @return checkOutDate
     */
    public java.lang.String getCheckOutDate() {
        return checkOutDate;
    }


    /**
     * Sets the checkOutDate value for this AddRoomOrderRequest.
     * 
     * @param checkOutDate
     */
    public void setCheckOutDate(java.lang.String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }


    /**
     * Gets the bedType value for this AddRoomOrderRequest.
     * 
     * @return bedType
     */
    public java.lang.String getBedType() {
        return bedType;
    }


    /**
     * Sets the bedType value for this AddRoomOrderRequest.
     * 
     * @param bedType
     */
    public void setBedType(java.lang.String bedType) {
        this.bedType = bedType;
    }


    /**
     * Gets the currency value for this AddRoomOrderRequest.
     * 
     * @return currency
     */
    public java.lang.String getCurrency() {
        return currency;
    }


    /**
     * Sets the currency value for this AddRoomOrderRequest.
     * 
     * @param currency
     */
    public void setCurrency(java.lang.String currency) {
        this.currency = currency;
    }


    /**
     * Gets the totalAmount value for this AddRoomOrderRequest.
     * 
     * @return totalAmount
     */
    public java.lang.String getTotalAmount() {
        return totalAmount;
    }


    /**
     * Sets the totalAmount value for this AddRoomOrderRequest.
     * 
     * @param totalAmount
     */
    public void setTotalAmount(java.lang.String totalAmount) {
        this.totalAmount = totalAmount;
    }


    /**
     * Gets the arriveEarlyTime value for this AddRoomOrderRequest.
     * 
     * @return arriveEarlyTime
     */
    public java.lang.String getArriveEarlyTime() {
        return arriveEarlyTime;
    }


    /**
     * Sets the arriveEarlyTime value for this AddRoomOrderRequest.
     * 
     * @param arriveEarlyTime
     */
    public void setArriveEarlyTime(java.lang.String arriveEarlyTime) {
        this.arriveEarlyTime = arriveEarlyTime;
    }


    /**
     * Gets the lastArriveTime value for this AddRoomOrderRequest.
     * 
     * @return lastArriveTime
     */
    public java.lang.String getLastArriveTime() {
        return lastArriveTime;
    }


    /**
     * Sets the lastArriveTime value for this AddRoomOrderRequest.
     * 
     * @param lastArriveTime
     */
    public void setLastArriveTime(java.lang.String lastArriveTime) {
        this.lastArriveTime = lastArriveTime;
    }


    /**
     * Gets the guests value for this AddRoomOrderRequest.
     * 
     * @return guests
     */
    public com.mango.hotel.GuestProfile[] getGuests() {
        return guests;
    }


    /**
     * Sets the guests value for this AddRoomOrderRequest.
     * 
     * @param guests
     */
    public void setGuests(com.mango.hotel.GuestProfile[] guests) {
        this.guests = guests;
    }

    public com.mango.hotel.GuestProfile getGuests(int i) {
        return this.guests[i];
    }

    public void setGuests(int i, com.mango.hotel.GuestProfile _value) {
        this.guests[i] = _value;
    }


    /**
     * Gets the contactors value for this AddRoomOrderRequest.
     * 
     * @return contactors
     */
    public com.mango.hotel.ContactorInfo[] getContactors() {
        return contactors;
    }


    /**
     * Sets the contactors value for this AddRoomOrderRequest.
     * 
     * @param contactors
     */
    public void setContactors(com.mango.hotel.ContactorInfo[] contactors) {
        this.contactors = contactors;
    }

    public com.mango.hotel.ContactorInfo getContactors(int i) {
        return this.contactors[i];
    }

    public void setContactors(int i, com.mango.hotel.ContactorInfo _value) {
        this.contactors[i] = _value;
    }


    /**
     * Gets the roomquantity value for this AddRoomOrderRequest.
     * 
     * @return roomquantity
     */
    public int getRoomquantity() {
        return roomquantity;
    }


    /**
     * Sets the roomquantity value for this AddRoomOrderRequest.
     * 
     * @param roomquantity
     */
    public void setRoomquantity(int roomquantity) {
        this.roomquantity = roomquantity;
    }


    /**
     * Gets the guestCount value for this AddRoomOrderRequest.
     * 
     * @return guestCount
     */
    public int getGuestCount() {
        return guestCount;
    }


    /**
     * Sets the guestCount value for this AddRoomOrderRequest.
     * 
     * @param guestCount
     */
    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }


    /**
     * Gets the specialRequest value for this AddRoomOrderRequest.
     * 
     * @return specialRequest
     */
    public java.lang.String getSpecialRequest() {
        return specialRequest;
    }


    /**
     * Sets the specialRequest value for this AddRoomOrderRequest.
     * 
     * @param specialRequest
     */
    public void setSpecialRequest(java.lang.String specialRequest) {
        this.specialRequest = specialRequest;
    }


    /**
     * Gets the hotelNote value for this AddRoomOrderRequest.
     * 
     * @return hotelNote
     */
    public java.lang.String getHotelNote() {
        return hotelNote;
    }


    /**
     * Sets the hotelNote value for this AddRoomOrderRequest.
     * 
     * @param hotelNote
     */
    public void setHotelNote(java.lang.String hotelNote) {
        this.hotelNote = hotelNote;
    }


    /**
     * Gets the payMethod value for this AddRoomOrderRequest.
     * 
     * @return payMethod
     */
    public java.lang.String getPayMethod() {
        return payMethod;
    }


    /**
     * Sets the payMethod value for this AddRoomOrderRequest.
     * 
     * @param payMethod
     */
    public void setPayMethod(java.lang.String payMethod) {
        this.payMethod = payMethod;
    }


    /**
     * Gets the guarantee value for this AddRoomOrderRequest.
     * 
     * @return guarantee
     */
    public java.lang.String getGuarantee() {
        return guarantee;
    }


    /**
     * Sets the guarantee value for this AddRoomOrderRequest.
     * 
     * @param guarantee
     */
    public void setGuarantee(java.lang.String guarantee) {
        this.guarantee = guarantee;
    }


    /**
     * Gets the creditcardname value for this AddRoomOrderRequest.
     * 
     * @return creditcardname
     */
    public java.lang.String getCreditcardname() {
        return creditcardname;
    }


    /**
     * Sets the creditcardname value for this AddRoomOrderRequest.
     * 
     * @param creditcardname
     */
    public void setCreditcardname(java.lang.String creditcardname) {
        this.creditcardname = creditcardname;
    }


    /**
     * Gets the creditcardno value for this AddRoomOrderRequest.
     * 
     * @return creditcardno
     */
    public java.lang.String getCreditcardno() {
        return creditcardno;
    }


    /**
     * Sets the creditcardno value for this AddRoomOrderRequest.
     * 
     * @param creditcardno
     */
    public void setCreditcardno(java.lang.String creditcardno) {
        this.creditcardno = creditcardno;
    }


    /**
     * Gets the creditcardtype value for this AddRoomOrderRequest.
     * 
     * @return creditcardtype
     */
    public java.lang.String getCreditcardtype() {
        return creditcardtype;
    }


    /**
     * Sets the creditcardtype value for this AddRoomOrderRequest.
     * 
     * @param creditcardtype
     */
    public void setCreditcardtype(java.lang.String creditcardtype) {
        this.creditcardtype = creditcardtype;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AddRoomOrderRequest)) return false;
        AddRoomOrderRequest other = (AddRoomOrderRequest) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.author==null && other.getAuthor()==null) || 
             (this.author!=null &&
              this.author.equals(other.getAuthor()))) &&
            ((this.hotelCode==null && other.getHotelCode()==null) || 
             (this.hotelCode!=null &&
              this.hotelCode.equals(other.getHotelCode()))) &&
            ((this.hotelName==null && other.getHotelName()==null) || 
             (this.hotelName!=null &&
              this.hotelName.equals(other.getHotelName()))) &&
            ((this.roomTypeCode==null && other.getRoomTypeCode()==null) || 
             (this.roomTypeCode!=null &&
              this.roomTypeCode.equals(other.getRoomTypeCode()))) &&
            ((this.roomTypeName==null && other.getRoomTypeName()==null) || 
             (this.roomTypeName!=null &&
              this.roomTypeName.equals(other.getRoomTypeName()))) &&
            ((this.ratePlanCode==null && other.getRatePlanCode()==null) || 
             (this.ratePlanCode!=null &&
              this.ratePlanCode.equals(other.getRatePlanCode()))) &&
            ((this.ratePlanName==null && other.getRatePlanName()==null) || 
             (this.ratePlanName!=null &&
              this.ratePlanName.equals(other.getRatePlanName()))) &&
            ((this.checkInDate==null && other.getCheckInDate()==null) || 
             (this.checkInDate!=null &&
              this.checkInDate.equals(other.getCheckInDate()))) &&
            ((this.checkOutDate==null && other.getCheckOutDate()==null) || 
             (this.checkOutDate!=null &&
              this.checkOutDate.equals(other.getCheckOutDate()))) &&
            ((this.bedType==null && other.getBedType()==null) || 
             (this.bedType!=null &&
              this.bedType.equals(other.getBedType()))) &&
            ((this.currency==null && other.getCurrency()==null) || 
             (this.currency!=null &&
              this.currency.equals(other.getCurrency()))) &&
            ((this.totalAmount==null && other.getTotalAmount()==null) || 
             (this.totalAmount!=null &&
              this.totalAmount.equals(other.getTotalAmount()))) &&
            ((this.arriveEarlyTime==null && other.getArriveEarlyTime()==null) || 
             (this.arriveEarlyTime!=null &&
              this.arriveEarlyTime.equals(other.getArriveEarlyTime()))) &&
            ((this.lastArriveTime==null && other.getLastArriveTime()==null) || 
             (this.lastArriveTime!=null &&
              this.lastArriveTime.equals(other.getLastArriveTime()))) &&
            ((this.guests==null && other.getGuests()==null) || 
             (this.guests!=null &&
              java.util.Arrays.equals(this.guests, other.getGuests()))) &&
            ((this.contactors==null && other.getContactors()==null) || 
             (this.contactors!=null &&
              java.util.Arrays.equals(this.contactors, other.getContactors()))) &&
            this.roomquantity == other.getRoomquantity() &&
            this.guestCount == other.getGuestCount() &&
            ((this.specialRequest==null && other.getSpecialRequest()==null) || 
             (this.specialRequest!=null &&
              this.specialRequest.equals(other.getSpecialRequest()))) &&
            ((this.hotelNote==null && other.getHotelNote()==null) || 
             (this.hotelNote!=null &&
              this.hotelNote.equals(other.getHotelNote()))) &&
            ((this.payMethod==null && other.getPayMethod()==null) || 
             (this.payMethod!=null &&
              this.payMethod.equals(other.getPayMethod()))) &&
            ((this.guarantee==null && other.getGuarantee()==null) || 
             (this.guarantee!=null &&
              this.guarantee.equals(other.getGuarantee()))) &&
            ((this.creditcardname==null && other.getCreditcardname()==null) || 
             (this.creditcardname!=null &&
              this.creditcardname.equals(other.getCreditcardname()))) &&
            ((this.creditcardno==null && other.getCreditcardno()==null) || 
             (this.creditcardno!=null &&
              this.creditcardno.equals(other.getCreditcardno()))) &&
            ((this.creditcardtype==null && other.getCreditcardtype()==null) || 
             (this.creditcardtype!=null &&
              this.creditcardtype.equals(other.getCreditcardtype())));
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
        if (getAuthor() != null) {
            _hashCode += getAuthor().hashCode();
        }
        if (getHotelCode() != null) {
            _hashCode += getHotelCode().hashCode();
        }
        if (getHotelName() != null) {
            _hashCode += getHotelName().hashCode();
        }
        if (getRoomTypeCode() != null) {
            _hashCode += getRoomTypeCode().hashCode();
        }
        if (getRoomTypeName() != null) {
            _hashCode += getRoomTypeName().hashCode();
        }
        if (getRatePlanCode() != null) {
            _hashCode += getRatePlanCode().hashCode();
        }
        if (getRatePlanName() != null) {
            _hashCode += getRatePlanName().hashCode();
        }
        if (getCheckInDate() != null) {
            _hashCode += getCheckInDate().hashCode();
        }
        if (getCheckOutDate() != null) {
            _hashCode += getCheckOutDate().hashCode();
        }
        if (getBedType() != null) {
            _hashCode += getBedType().hashCode();
        }
        if (getCurrency() != null) {
            _hashCode += getCurrency().hashCode();
        }
        if (getTotalAmount() != null) {
            _hashCode += getTotalAmount().hashCode();
        }
        if (getArriveEarlyTime() != null) {
            _hashCode += getArriveEarlyTime().hashCode();
        }
        if (getLastArriveTime() != null) {
            _hashCode += getLastArriveTime().hashCode();
        }
        if (getGuests() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getGuests());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getGuests(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getContactors() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getContactors());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getContactors(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        _hashCode += getRoomquantity();
        _hashCode += getGuestCount();
        if (getSpecialRequest() != null) {
            _hashCode += getSpecialRequest().hashCode();
        }
        if (getHotelNote() != null) {
            _hashCode += getHotelNote().hashCode();
        }
        if (getPayMethod() != null) {
            _hashCode += getPayMethod().hashCode();
        }
        if (getGuarantee() != null) {
            _hashCode += getGuarantee().hashCode();
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
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AddRoomOrderRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">AddRoomOrderRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("author");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "author"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "MangoAuthor"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomTypeCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomTypeCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomTypeName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomTypeName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ratePlanCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ratePlanCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ratePlanName");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ratePlanName"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkInDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "checkInDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("checkOutDate");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "checkOutDate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("bedType");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "bedType"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("currency");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "currency"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("totalAmount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "totalAmount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("arriveEarlyTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "arriveEarlyTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("lastArriveTime");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "lastArriveTime"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("guests");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "guests"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "GuestProfile"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("contactors");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "contactors"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ContactorInfo"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("roomquantity");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomquantity"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("guestCount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "guestCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("specialRequest");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "specialRequest"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelNote");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelNote"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "payMethod"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("guarantee");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "Guarantee"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
