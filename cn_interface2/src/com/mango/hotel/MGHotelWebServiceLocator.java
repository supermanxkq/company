/**
 * MGHotelWebServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class MGHotelWebServiceLocator extends org.apache.axis.client.Service implements com.mango.hotel.MGHotelWebService {

    public MGHotelWebServiceLocator() {
    }


    public MGHotelWebServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public MGHotelWebServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for MGHotelServiceImplPort
    private java.lang.String MGHotelServiceImplPort_address = "http://www.mangocity.com/HDL/service/MZExHotel";

    public java.lang.String getMGHotelServiceImplPortAddress() {
        return MGHotelServiceImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String MGHotelServiceImplPortWSDDServiceName = "MGHotelServiceImplPort";

    public java.lang.String getMGHotelServiceImplPortWSDDServiceName() {
        return MGHotelServiceImplPortWSDDServiceName;
    }

    public void setMGHotelServiceImplPortWSDDServiceName(java.lang.String name) {
        MGHotelServiceImplPortWSDDServiceName = name;
    }

    public com.mango.hotel.MGHotelService getMGHotelServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(MGHotelServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getMGHotelServiceImplPort(endpoint);
    }

    public com.mango.hotel.MGHotelService getMGHotelServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            com.mango.hotel.MGHotelWebServiceSoapBindingStub _stub = new com.mango.hotel.MGHotelWebServiceSoapBindingStub(portAddress, this);
            _stub.setPortName(getMGHotelServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setMGHotelServiceImplPortEndpointAddress(java.lang.String address) {
        MGHotelServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (com.mango.hotel.MGHotelService.class.isAssignableFrom(serviceEndpointInterface)) {
                com.mango.hotel.MGHotelWebServiceSoapBindingStub _stub = new com.mango.hotel.MGHotelWebServiceSoapBindingStub(new java.net.URL(MGHotelServiceImplPort_address), this);
                _stub.setPortName(getMGHotelServiceImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("MGHotelServiceImplPort".equals(inputPortName)) {
            return getMGHotelServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "MGHotelWebService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "MGHotelServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("MGHotelServiceImplPort".equals(portName)) {
            setMGHotelServiceImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
