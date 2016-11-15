/**
 * CheckReservationRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class CheckReservationRequest  implements java.io.Serializable {
    private com.mango.hotel.MangoAuthor author;

    private java.lang.String hotelCode;

    private java.lang.String roomTypeCode;

    private java.lang.String ratePlanCode;

    private java.lang.String checkInDate;

    private java.lang.String checkOutDate;

    private int roomCount;

    private java.lang.String payMethod;

    public CheckReservationRequest() {
    }

    public CheckReservationRequest(
           com.mango.hotel.MangoAuthor author,
           java.lang.String hotelCode,
           java.lang.String roomTypeCode,
           java.lang.String ratePlanCode,
           java.lang.String checkInDate,
           java.lang.String checkOutDate,
           int roomCount,
           java.lang.String payMethod) {
           this.author = author;
           this.hotelCode = hotelCode;
           this.roomTypeCode = roomTypeCode;
           this.ratePlanCode = ratePlanCode;
           this.checkInDate = checkInDate;
           this.checkOutDate = checkOutDate;
           this.roomCount = roomCount;
           this.payMethod = payMethod;
    }


    /**
     * Gets the author value for this CheckReservationRequest.
     * 
     * @return author
     */
    public com.mango.hotel.MangoAuthor getAuthor() {
        return author;
    }


    /**
     * Sets the author value for this CheckReservationRequest.
     * 
     * @param author
     */
    public void setAuthor(com.mango.hotel.MangoAuthor author) {
        this.author = author;
    }


    /**
     * Gets the hotelCode value for this CheckReservationRequest.
     * 
     * @return hotelCode
     */
    public java.lang.String getHotelCode() {
        return hotelCode;
    }


    /**
     * Sets the hotelCode value for this CheckReservationRequest.
     * 
     * @param hotelCode
     */
    public void setHotelCode(java.lang.String hotelCode) {
        this.hotelCode = hotelCode;
    }


    /**
     * Gets the roomTypeCode value for this CheckReservationRequest.
     * 
     * @return roomTypeCode
     */
    public java.lang.String getRoomTypeCode() {
        return roomTypeCode;
    }


    /**
     * Sets the roomTypeCode value for this CheckReservationRequest.
     * 
     * @param roomTypeCode
     */
    public void setRoomTypeCode(java.lang.String roomTypeCode) {
        this.roomTypeCode = roomTypeCode;
    }


    /**
     * Gets the ratePlanCode value for this CheckReservationRequest.
     * 
     * @return ratePlanCode
     */
    public java.lang.String getRatePlanCode() {
        return ratePlanCode;
    }


    /**
     * Sets the ratePlanCode value for this CheckReservationRequest.
     * 
     * @param ratePlanCode
     */
    public void setRatePlanCode(java.lang.String ratePlanCode) {
        this.ratePlanCode = ratePlanCode;
    }


    /**
     * Gets the checkInDate value for this CheckReservationRequest.
     * 
     * @return checkInDate
     */
    public java.lang.String getCheckInDate() {
        return checkInDate;
    }


    /**
     * Sets the checkInDate value for this CheckReservationRequest.
     * 
     * @param checkInDate
     */
    public void setCheckInDate(java.lang.String checkInDate) {
        this.checkInDate = checkInDate;
    }


    /**
     * Gets the checkOutDate value for this CheckReservationRequest.
     * 
     * @return checkOutDate
     */
    public java.lang.String getCheckOutDate() {
        return checkOutDate;
    }


    /**
     * Sets the checkOutDate value for this CheckReservationRequest.
     * 
     * @param checkOutDate
     */
    public void setCheckOutDate(java.lang.String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }


    /**
     * Gets the roomCount value for this CheckReservationRequest.
     * 
     * @return roomCount
     */
    public int getRoomCount() {
        return roomCount;
    }


    /**
     * Sets the roomCount value for this CheckReservationRequest.
     * 
     * @param roomCount
     */
    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }


    /**
     * Gets the payMethod value for this CheckReservationRequest.
     * 
     * @return payMethod
     */
    public java.lang.String getPayMethod() {
        return payMethod;
    }


    /**
     * Sets the payMethod value for this CheckReservationRequest.
     * 
     * @param payMethod
     */
    public void setPayMethod(java.lang.String payMethod) {
        this.payMethod = payMethod;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CheckReservationRequest)) return false;
        CheckReservationRequest other = (CheckReservationRequest) obj;
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
            ((this.roomTypeCode==null && other.getRoomTypeCode()==null) || 
             (this.roomTypeCode!=null &&
              this.roomTypeCode.equals(other.getRoomTypeCode()))) &&
            ((this.ratePlanCode==null && other.getRatePlanCode()==null) || 
             (this.ratePlanCode!=null &&
              this.ratePlanCode.equals(other.getRatePlanCode()))) &&
            ((this.checkInDate==null && other.getCheckInDate()==null) || 
             (this.checkInDate!=null &&
              this.checkInDate.equals(other.getCheckInDate()))) &&
            ((this.checkOutDate==null && other.getCheckOutDate()==null) || 
             (this.checkOutDate!=null &&
              this.checkOutDate.equals(other.getCheckOutDate()))) &&
            this.roomCount == other.getRoomCount() &&
            ((this.payMethod==null && other.getPayMethod()==null) || 
             (this.payMethod!=null &&
              this.payMethod.equals(other.getPayMethod())));
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
        if (getRoomTypeCode() != null) {
            _hashCode += getRoomTypeCode().hashCode();
        }
        if (getRatePlanCode() != null) {
            _hashCode += getRatePlanCode().hashCode();
        }
        if (getCheckInDate() != null) {
            _hashCode += getCheckInDate().hashCode();
        }
        if (getCheckOutDate() != null) {
            _hashCode += getCheckOutDate().hashCode();
        }
        _hashCode += getRoomCount();
        if (getPayMethod() != null) {
            _hashCode += getPayMethod().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CheckReservationRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">CheckReservationRequest"));
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
        elemField.setFieldName("roomTypeCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomTypeCode"));
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
        elemField.setFieldName("roomCount");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "roomCount"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("payMethod");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "payMethod"));
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
