/**
 * SystemUserTypes.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public class SystemUserTypes implements java.io.Serializable {
    private java.lang.String _value_;
    private static java.util.HashMap _table_ = new java.util.HashMap();

    // Constructor
    protected SystemUserTypes(java.lang.String value) {
        _value_ = value;
        _table_.put(_value_,this);
    }

    public static final java.lang.String _Platform = "Platform";
    public static final java.lang.String _Distributor = "Distributor";
    public static final java.lang.String _Agent = "Agent";
    public static final java.lang.String _Supplier = "Supplier";
    public static final java.lang.String _Member = "Member";
    public static final SystemUserTypes Platform = new SystemUserTypes(_Platform);
    public static final SystemUserTypes Distributor = new SystemUserTypes(_Distributor);
    public static final SystemUserTypes Agent = new SystemUserTypes(_Agent);
    public static final SystemUserTypes Supplier = new SystemUserTypes(_Supplier);
    public static final SystemUserTypes Member = new SystemUserTypes(_Member);
    public java.lang.String getValue() { return _value_;}
    public static SystemUserTypes fromValue(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        SystemUserTypes enumeration = (SystemUserTypes)
            _table_.get(value);
        if (enumeration==null) throw new java.lang.IllegalArgumentException();
        return enumeration;
    }
    public static SystemUserTypes fromString(java.lang.String value)
          throws java.lang.IllegalArgumentException {
        return fromValue(value);
    }
    public boolean equals(java.lang.Object obj) {return (obj == this);}
    public int hashCode() { return toString().hashCode();}
    public java.lang.String toString() { return _value_;}
    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumSerializer(
            _javaType, _xmlType);
    }
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new org.apache.axis.encoding.ser.EnumDeserializer(
            _javaType, _xmlType);
    }
    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SystemUserTypes.class);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://tempuri.org/", "SystemUserTypes"));
    }
    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

}
