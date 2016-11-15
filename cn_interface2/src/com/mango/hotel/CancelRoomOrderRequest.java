/**
 * CancelRoomOrderRequest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class CancelRoomOrderRequest  implements java.io.Serializable {
    private com.mango.hotel.MangoAuthor author;

    private java.lang.String channelCode;

    private java.lang.String ordercd;

    private java.lang.String cancelReason;

    public CancelRoomOrderRequest() {
    }

    public CancelRoomOrderRequest(
           com.mango.hotel.MangoAuthor author,
           java.lang.String channelCode,
           java.lang.String ordercd,
           java.lang.String cancelReason) {
           this.author = author;
           this.channelCode = channelCode;
           this.ordercd = ordercd;
           this.cancelReason = cancelReason;
    }


    /**
     * Gets the author value for this CancelRoomOrderRequest.
     * 
     * @return author
     */
    public com.mango.hotel.MangoAuthor getAuthor() {
        return author;
    }


    /**
     * Sets the author value for this CancelRoomOrderRequest.
     * 
     * @param author
     */
    public void setAuthor(com.mango.hotel.MangoAuthor author) {
        this.author = author;
    }


    /**
     * Gets the channelCode value for this CancelRoomOrderRequest.
     * 
     * @return channelCode
     */
    public java.lang.String getChannelCode() {
        return channelCode;
    }


    /**
     * Sets the channelCode value for this CancelRoomOrderRequest.
     * 
     * @param channelCode
     */
    public void setChannelCode(java.lang.String channelCode) {
        this.channelCode = channelCode;
    }


    /**
     * Gets the ordercd value for this CancelRoomOrderRequest.
     * 
     * @return ordercd
     */
    public java.lang.String getOrdercd() {
        return ordercd;
    }


    /**
     * Sets the ordercd value for this CancelRoomOrderRequest.
     * 
     * @param ordercd
     */
    public void setOrdercd(java.lang.String ordercd) {
        this.ordercd = ordercd;
    }


    /**
     * Gets the cancelReason value for this CancelRoomOrderRequest.
     * 
     * @return cancelReason
     */
    public java.lang.String getCancelReason() {
        return cancelReason;
    }


    /**
     * Sets the cancelReason value for this CancelRoomOrderRequest.
     * 
     * @param cancelReason
     */
    public void setCancelReason(java.lang.String cancelReason) {
        this.cancelReason = cancelReason;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CancelRoomOrderRequest)) return false;
        CancelRoomOrderRequest other = (CancelRoomOrderRequest) obj;
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
            ((this.channelCode==null && other.getChannelCode()==null) || 
             (this.channelCode!=null &&
              this.channelCode.equals(other.getChannelCode()))) &&
            ((this.ordercd==null && other.getOrdercd()==null) || 
             (this.ordercd!=null &&
              this.ordercd.equals(other.getOrdercd()))) &&
            ((this.cancelReason==null && other.getCancelReason()==null) || 
             (this.cancelReason!=null &&
              this.cancelReason.equals(other.getCancelReason())));
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
        if (getChannelCode() != null) {
            _hashCode += getChannelCode().hashCode();
        }
        if (getOrdercd() != null) {
            _hashCode += getOrdercd().hashCode();
        }
        if (getCancelReason() != null) {
            _hashCode += getCancelReason().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(CancelRoomOrderRequest.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">CancelRoomOrderRequest"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("author");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "author"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "MangoAuthor"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("channelCode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "channelCode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ordercd");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ordercd"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("cancelReason");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "cancelReason"));
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
