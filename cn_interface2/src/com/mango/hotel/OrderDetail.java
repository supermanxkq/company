/**
 * OrderDetail.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class OrderDetail  implements java.io.Serializable {
    private java.lang.String orderStates;

    private java.lang.String sendedHotelFax;

    private java.lang.String hotelConfirmTel;

    private java.lang.String hotelConfirmFax;

    private java.lang.String hotelConfirmFaxReturn;

    private java.lang.String auditStates;

    private com.mango.hotel.Order mgOrder;

    public OrderDetail() {
    }

    public OrderDetail(
           java.lang.String orderStates,
           java.lang.String sendedHotelFax,
           java.lang.String hotelConfirmTel,
           java.lang.String hotelConfirmFax,
           java.lang.String hotelConfirmFaxReturn,
           java.lang.String auditStates,
           com.mango.hotel.Order mgOrder) {
           this.orderStates = orderStates;
           this.sendedHotelFax = sendedHotelFax;
           this.hotelConfirmTel = hotelConfirmTel;
           this.hotelConfirmFax = hotelConfirmFax;
           this.hotelConfirmFaxReturn = hotelConfirmFaxReturn;
           this.auditStates = auditStates;
           this.mgOrder = mgOrder;
    }


    /**
     * Gets the orderStates value for this OrderDetail.
     * 
     * @return orderStates
     */
    public java.lang.String getOrderStates() {
        return orderStates;
    }


    /**
     * Sets the orderStates value for this OrderDetail.
     * 
     * @param orderStates
     */
    public void setOrderStates(java.lang.String orderStates) {
        this.orderStates = orderStates;
    }


    /**
     * Gets the sendedHotelFax value for this OrderDetail.
     * 
     * @return sendedHotelFax
     */
    public java.lang.String getSendedHotelFax() {
        return sendedHotelFax;
    }


    /**
     * Sets the sendedHotelFax value for this OrderDetail.
     * 
     * @param sendedHotelFax
     */
    public void setSendedHotelFax(java.lang.String sendedHotelFax) {
        this.sendedHotelFax = sendedHotelFax;
    }


    /**
     * Gets the hotelConfirmTel value for this OrderDetail.
     * 
     * @return hotelConfirmTel
     */
    public java.lang.String getHotelConfirmTel() {
        return hotelConfirmTel;
    }


    /**
     * Sets the hotelConfirmTel value for this OrderDetail.
     * 
     * @param hotelConfirmTel
     */
    public void setHotelConfirmTel(java.lang.String hotelConfirmTel) {
        this.hotelConfirmTel = hotelConfirmTel;
    }


    /**
     * Gets the hotelConfirmFax value for this OrderDetail.
     * 
     * @return hotelConfirmFax
     */
    public java.lang.String getHotelConfirmFax() {
        return hotelConfirmFax;
    }


    /**
     * Sets the hotelConfirmFax value for this OrderDetail.
     * 
     * @param hotelConfirmFax
     */
    public void setHotelConfirmFax(java.lang.String hotelConfirmFax) {
        this.hotelConfirmFax = hotelConfirmFax;
    }


    /**
     * Gets the hotelConfirmFaxReturn value for this OrderDetail.
     * 
     * @return hotelConfirmFaxReturn
     */
    public java.lang.String getHotelConfirmFaxReturn() {
        return hotelConfirmFaxReturn;
    }


    /**
     * Sets the hotelConfirmFaxReturn value for this OrderDetail.
     * 
     * @param hotelConfirmFaxReturn
     */
    public void setHotelConfirmFaxReturn(java.lang.String hotelConfirmFaxReturn) {
        this.hotelConfirmFaxReturn = hotelConfirmFaxReturn;
    }


    /**
     * Gets the auditStates value for this OrderDetail.
     * 
     * @return auditStates
     */
    public java.lang.String getAuditStates() {
        return auditStates;
    }


    /**
     * Sets the auditStates value for this OrderDetail.
     * 
     * @param auditStates
     */
    public void setAuditStates(java.lang.String auditStates) {
        this.auditStates = auditStates;
    }


    /**
     * Gets the mgOrder value for this OrderDetail.
     * 
     * @return mgOrder
     */
    public com.mango.hotel.Order getMgOrder() {
        return mgOrder;
    }


    /**
     * Sets the mgOrder value for this OrderDetail.
     * 
     * @param mgOrder
     */
    public void setMgOrder(com.mango.hotel.Order mgOrder) {
        this.mgOrder = mgOrder;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof OrderDetail)) return false;
        OrderDetail other = (OrderDetail) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.orderStates==null && other.getOrderStates()==null) || 
             (this.orderStates!=null &&
              this.orderStates.equals(other.getOrderStates()))) &&
            ((this.sendedHotelFax==null && other.getSendedHotelFax()==null) || 
             (this.sendedHotelFax!=null &&
              this.sendedHotelFax.equals(other.getSendedHotelFax()))) &&
            ((this.hotelConfirmTel==null && other.getHotelConfirmTel()==null) || 
             (this.hotelConfirmTel!=null &&
              this.hotelConfirmTel.equals(other.getHotelConfirmTel()))) &&
            ((this.hotelConfirmFax==null && other.getHotelConfirmFax()==null) || 
             (this.hotelConfirmFax!=null &&
              this.hotelConfirmFax.equals(other.getHotelConfirmFax()))) &&
            ((this.hotelConfirmFaxReturn==null && other.getHotelConfirmFaxReturn()==null) || 
             (this.hotelConfirmFaxReturn!=null &&
              this.hotelConfirmFaxReturn.equals(other.getHotelConfirmFaxReturn()))) &&
            ((this.auditStates==null && other.getAuditStates()==null) || 
             (this.auditStates!=null &&
              this.auditStates.equals(other.getAuditStates()))) &&
            ((this.mgOrder==null && other.getMgOrder()==null) || 
             (this.mgOrder!=null &&
              this.mgOrder.equals(other.getMgOrder())));
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
        if (getOrderStates() != null) {
            _hashCode += getOrderStates().hashCode();
        }
        if (getSendedHotelFax() != null) {
            _hashCode += getSendedHotelFax().hashCode();
        }
        if (getHotelConfirmTel() != null) {
            _hashCode += getHotelConfirmTel().hashCode();
        }
        if (getHotelConfirmFax() != null) {
            _hashCode += getHotelConfirmFax().hashCode();
        }
        if (getHotelConfirmFaxReturn() != null) {
            _hashCode += getHotelConfirmFaxReturn().hashCode();
        }
        if (getAuditStates() != null) {
            _hashCode += getAuditStates().hashCode();
        }
        if (getMgOrder() != null) {
            _hashCode += getMgOrder().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(OrderDetail.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "OrderDetail"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("orderStates");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "orderStates"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("sendedHotelFax");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "sendedHotelFax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelConfirmTel");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelConfirmTel"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelConfirmFax");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelConfirmFax"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelConfirmFaxReturn");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelConfirmFaxReturn"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("auditStates");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "auditStates"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("mgOrder");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "mgOrder"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "Order"));
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
