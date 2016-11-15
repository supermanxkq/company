/**
 * SingleHotelRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class SingleHotelRequest  implements java.io.Serializable {
    private com.mango.hotel.MangoAuthor author;

    private java.lang.String hotelCode;

    private java.lang.String cityCode;

    private java.lang.String checkInDate;

    private java.lang.String checkOutDate;

    public SingleHotelRequest() {
    }

    public SingleHotelRequest(
           com.mango.hotel.MangoAuthor author,
           java.lang.String hotelCode,
           java.lang.String cityCode,
           java.lang.String checkInDate,
           java.lang.String checkOutDate) {
           this.author = author;
           this.hotelCode = hotelCode;
           this.cityCode = cityCode;
           this.checkInDate = checkInDate;
           this.checkOutDate = checkOutDate;
    }


    /**
     * Gets the author value for this SingleHotelRequest.
     * 
     * @return author
     */
    public com.mango.hotel.MangoAuthor getAuthor() {
        return author;
    }


    /**
     * Sets the author value for this SingleHotelRequest.
     * 
     * @param author
     */
    public void setAuthor(com.mango.hotel.MangoAuthor author) {
        this.author = author;
    }


    /**
     * Gets the hotelCode value for this SingleHotelRequest.
     * 
     * @return hotelCode
     */
    public java.lang.String getHotelCode() {
        return hotelCode;
    }


    /**
     * Sets the hotelCode value for this SingleHotelRequest.
     * 
     * @param hotelCode
     */
    public void setHotelCode(java.lang.String hotelCode) {
        this.hotelCode = hotelCode;
    }


    /**
     * Gets the cityCode value for this SingleHotelRequest.
     * 
     * @return cityCode
     */
    public java.lang.String getCityCode() {
        return cityCode;
    }


    /**
     * Sets the cityCode value for this SingleHotelRequest.
     * 
     * @param cityCode
     */
    public void setCityCode(java.lang.String cityCode) {
        this.cityCode = cityCode;
    }


    /**
     * Gets the checkInDate value for this SingleHotelRequest.
     * 
     * @return checkInDate
     */
    public java.lang.String getCheckInDate() {
        return checkInDate;
    }


    /**
     * Sets the checkInDate value for this SingleHotelRequest.
     * 
     * @param checkInDate
     */
    public void setCheckInDate(java.lang.String checkInDate) {
        this.checkInDate = checkInDate;
    }


    /**
     * Gets the checkOutDate value for this SingleHotelRequest.
     * 
     * @return checkOutDate
     */
    public java.lang.String getCheckOutDate() {
        return checkOutDate;
    }


    /**
     * Sets the checkOutDate value for this SingleHotelRequest.
     * 
     * @param checkOutDate
     */
    public void setCheckOutDate(java.lang.String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SingleHotelRequest)) return false;
        SingleHotelRequest other = (SingleHotelRequest) obj;
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
            ((this.cityCode==null && other.getCityCode()==null) || 
             (this.cityCode!=null &&
              this.cityCode.equals(other.getCityCode()))) &&
            ((this.checkInDate==null && other.getCheckInDate()==null) || 
             (this.checkInDate!=null &&
              this.checkInDate.equals(other.getCheckInDate()))) &&
            ((this.checkOutDate==null && other.getCheckOutDate()==null) || 
             (this.checkOutDate!=null &&
              this.checkOutDate.equals(other.getCheckOutDate())));
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
        if (getCityCode() != null) {
            _hashCode += getCityCode().hashCode();
        }
        if (getCheckInDate() != null) {
            _hashCode += getCheckInDate().hashCode();
        }
        if (getCheckOutDate() != null) {
            _hashCode += getCheckOutDate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SingleHotelRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">SingleHotelRequest"));
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
        elemField.setFieldName("cityCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "cityCode"));
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
