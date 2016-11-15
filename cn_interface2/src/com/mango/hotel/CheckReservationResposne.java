/**
 * CheckReservationResposne.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class CheckReservationResposne  implements java.io.Serializable {
    private com.mango.hotel.Result result;

    private com.mango.hotel.RatePlan[] ratePlanList;

    public CheckReservationResposne() {
    }

    public CheckReservationResposne(
           com.mango.hotel.Result result,
           com.mango.hotel.RatePlan[] ratePlanList) {
           this.result = result;
           this.ratePlanList = ratePlanList;
    }


    /**
     * Gets the result value for this CheckReservationResposne.
     * 
     * @return result
     */
    public com.mango.hotel.Result getResult() {
        return result;
    }


    /**
     * Sets the result value for this CheckReservationResposne.
     * 
     * @param result
     */
    public void setResult(com.mango.hotel.Result result) {
        this.result = result;
    }


    /**
     * Gets the ratePlanList value for this CheckReservationResposne.
     * 
     * @return ratePlanList
     */
    public com.mango.hotel.RatePlan[] getRatePlanList() {
        return ratePlanList;
    }


    /**
     * Sets the ratePlanList value for this CheckReservationResposne.
     * 
     * @param ratePlanList
     */
    public void setRatePlanList(com.mango.hotel.RatePlan[] ratePlanList) {
        this.ratePlanList = ratePlanList;
    }

    public com.mango.hotel.RatePlan getRatePlanList(int i) {
        return this.ratePlanList[i];
    }

    public void setRatePlanList(int i, com.mango.hotel.RatePlan _value) {
        this.ratePlanList[i] = _value;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof CheckReservationResposne)) return false;
        CheckReservationResposne other = (CheckReservationResposne) obj;
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
            ((this.ratePlanList==null && other.getRatePlanList()==null) || 
             (this.ratePlanList!=null &&
              java.util.Arrays.equals(this.ratePlanList, other.getRatePlanList())));
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
        if (getRatePlanList() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getRatePlanList());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getRatePlanList(), i);
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
        new org.apache.axis.description.TypeDesc(CheckReservationResposne.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">CheckReservationResposne"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("result");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "result"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "Result"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("ratePlanList");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ratePlanList"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "RatePlan"));
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
