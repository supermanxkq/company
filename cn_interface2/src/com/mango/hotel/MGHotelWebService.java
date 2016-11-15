/**
 * MGHotelWebService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public interface MGHotelWebService extends javax.xml.rpc.Service {
    public java.lang.String getMGHotelServiceImplPortAddress();

    public com.mango.hotel.MGHotelService getMGHotelServiceImplPort() throws javax.xml.rpc.ServiceException;

    public com.mango.hotel.MGHotelService getMGHotelServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}
