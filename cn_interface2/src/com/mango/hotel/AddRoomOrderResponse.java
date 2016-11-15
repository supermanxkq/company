/**
 * AddRoomOrderResponse.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class AddRoomOrderResponse  implements java.io.Serializable {
    private com.mango.hotel.Result result;

    private java.lang.String ordercode;

    private com.mango.hotel.Hotel[] hotelList;

    public AddRoomOrderResponse() {
    }

    public AddRoomOrderResponse(
           com.mango.hotel.Result result,
           java.lang.String ordercode,
           com.mango.hotel.Hotel[] hotelList) {
           this.result = result;
           this.ordercode = ordercode;
           this.hotelList = hotelList;
    }


    /**
     * Gets the result value for this AddRoomOrderResponse.
     * 
     * @return result
     */
    public com.mango.hotel.Result getResult() {
        return result;
    }


    /**
     * Sets the result value for this AddRoomOrderResponse.
     * 
     * @param result
     */
    public void setResult(com.mango.hotel.Result result) {
        this.result = result;
    }


    /**
     * Gets the ordercode value for this AddRoomOrderResponse.
     * 
     * @return ordercode
     */
    public java.lang.String getOrdercode() {
        return ordercode;
    }


    /**
     * Sets the ordercode value for this AddRoomOrderResponse.
     * 
     * @param ordercode
     */
    public void setOrdercode(java.lang.String ordercode) {
        this.ordercode = ordercode;
    }


    /**
     * Gets the hotelList value for this AddRoomOrderResponse.
     * 
     * @return hotelList
     */
    public com.mango.hotel.Hotel[] getHotelList() {
        return hotelList;
    }


    /**
     * Sets the hotelList value for this AddRoomOrderResponse.
     * 
     * @param hotelList
     */
    public void setHotelList(com.mango.hotel.Hotel[] hotelList) {
        this.hotelList = hotelList;
    }

    public com.mango.hotel.Hotel getHotelList(int i) {
        return this.hotelList[i];
    }

    public void setHotelList(int i, com.mango.hotel.Hotel _value) {
        this.hotelList[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof AddRoomOrderResponse)) return false;
        AddRoomOrderResponse other = (AddRoomOrderResponse) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.result==null && other.getResult()==null) || 
             (this.result!=null &&
              this.result.equals(other.getResult()))) &&
            ((this.ordercode==null && other.getOrdercode()==null) || 
             (this.ordercode!=null &&
              this.ordercode.equals(other.getOrdercode()))) &&
            ((this.hotelList==null && other.getHotelList()==null) || 
             (this.hotelList!=null &&
              java.util.Arrays.equals(this.hotelList, other.getHotelList())));
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
        if (getResult() != null) {
            _hashCode += getResult().hashCode();
        }
        if (getOrdercode() != null) {
            _hashCode += getOrdercode().hashCode();
        }
        if (getHotelList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getHotelList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getHotelList(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(AddRoomOrderResponse.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">AddRoomOrderResponse"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "Result"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ordercode");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ordercode"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("hotelList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "hotelList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "Hotel"));
        elemField.setMinOccurs(0);
        elemField.setNillable(true);
        elemField.setMaxOccursUnbounded(true);
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
