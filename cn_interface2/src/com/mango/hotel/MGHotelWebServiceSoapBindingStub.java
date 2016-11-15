/**
 * MGHotelWebServiceSoapBindingStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public class MGHotelWebServiceSoapBindingStub extends org.apache.axis.client.Stub implements com.mango.hotel.MGHotelService {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        _operations = new org.apache.axis.description.OperationDesc[7];
        _initOperationDesc1();
    }

    private static void _initOperationDesc1(){
        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("Test");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "TestRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">TestRequest"), com.mango.hotel.TestRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">TestResponse"));
        oper.setReturnClass(com.mango.hotel.TestResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "TestResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DetailOrder");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "DetailOrderRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">DetailOrderRequest"), com.mango.hotel.DetailOrderRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">DetailOrderResponse"));
        oper.setReturnClass(com.mango.hotel.DetailOrderResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "DetailOrderResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("SingleHotel");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "SingleHotelRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">SingleHotelRequest"), com.mango.hotel.SingleHotelRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">SingleHotelResponse"));
        oper.setReturnClass(com.mango.hotel.SingleHotelResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "SingleHotelResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("MutilHotel");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "MutilHotelRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">MutilHotelRequest"), com.mango.hotel.MutilHotelRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">MutilHotelResponse"));
        oper.setReturnClass(com.mango.hotel.MutilHotelResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "MutilHotelResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CancelRoomOrder");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "CancelRoomOrderRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">CancelRoomOrderRequest"), com.mango.hotel.CancelRoomOrderRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">CancelRoomOrderResponse"));
        oper.setReturnClass(com.mango.hotel.CancelRoomOrderResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "CancelRoomOrderResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CheckReservation");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "CheckReservationRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">CheckReservationRequest"), com.mango.hotel.CheckReservationRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">CheckReservationResposne"));
        oper.setReturnClass(com.mango.hotel.CheckReservationResposne.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "CheckReservationResposne"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("AddRoomOrder");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "AddRoomOrderRequest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">AddRoomOrderRequest"), com.mango.hotel.AddRoomOrderRequest.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">AddRoomOrderResponse"));
        oper.setReturnClass(com.mango.hotel.AddRoomOrderResponse.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "AddRoomOrderResponse"));
        oper.setStyle(org.apache.axis.constants.Style.DOCUMENT);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

    }

    public MGHotelWebServiceSoapBindingStub() throws org.apache.axis.AxisFault {
         this(null);
    }

    public MGHotelWebServiceSoapBindingStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
         super.cachedEndpoint = endpointURL;
    }

    public MGHotelWebServiceSoapBindingStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        if (service == null) {
            super.service = new org.apache.axis.client.Service();
        } else {
            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">AddRoomOrderRequest");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.AddRoomOrderRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">AddRoomOrderResponse");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.AddRoomOrderResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">CancelRoomOrderRequest");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.CancelRoomOrderRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">CancelRoomOrderResponse");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.CancelRoomOrderResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">CheckReservationRequest");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.CheckReservationRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">CheckReservationResposne");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.CheckReservationResposne.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">DetailOrderRequest");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.DetailOrderRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">DetailOrderResponse");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.DetailOrderResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">MutilHotelRequest");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.MutilHotelRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">MutilHotelResponse");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.MutilHotelResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">SingleHotelRequest");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.SingleHotelRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">SingleHotelResponse");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.SingleHotelResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">TestRequest");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.TestRequest.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", ">TestResponse");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.TestResponse.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "ContactorInfo");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.ContactorInfo.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "GuestProfile");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.GuestProfile.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "Hotel");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.Hotel.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "HotelSummary");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.HotelSummary.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "MangoAuthor");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.MangoAuthor.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "Order");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.Order.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "OrderDetail");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.OrderDetail.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "OrderItem");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.OrderItem.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "RatePlan");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.RatePlan.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "Result");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.Result.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mangocity.com/hdl/ex/dto", "RoomType");
            cachedSerQNames.add(qName);
            cls = com.mango.hotel.RoomType.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

    }

    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {
        try {
            org.apache.axis.client.Call _call = super._createCall();
            if (super.maintainSessionSet) {
                _call.setMaintainSession(super.maintainSession);
            }
            if (super.cachedUsername != null) {
                _call.setUsername(super.cachedUsername);
            }
            if (super.cachedPassword != null) {
                _call.setPassword(super.cachedPassword);
            }
            if (super.cachedEndpoint != null) {
                _call.setTargetEndpointAddress(super.cachedEndpoint);
            }
            if (super.cachedTimeout != null) {
                _call.setTimeout(super.cachedTimeout);
            }
            if (super.cachedPortName != null) {
                _call.setPortName(super.cachedPortName);
            }
            java.util.Enumeration keys = super.cachedProperties.keys();
            while (keys.hasMoreElements()) {
                java.lang.String key = (java.lang.String) keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                if (firstCall()) {
                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        java.lang.Class cls = (java.lang.Class) cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            java.lang.Class sf = (java.lang.Class)
                                 cachedSerFactories.get(i);
                            java.lang.Class df = (java.lang.Class)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            org.apache.axis.encoding.SerializerFactory sf = (org.apache.axis.encoding.SerializerFactory)
                                 cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = (org.apache.axis.encoding.DeserializerFactory)
                                 cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public com.mango.hotel.TestResponse test(com.mango.hotel.TestRequest testRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "Test"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {testRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.mango.hotel.TestResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.mango.hotel.TestResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.mango.hotel.TestResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.mango.hotel.DetailOrderResponse detailOrder(com.mango.hotel.DetailOrderRequest detailOrderRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "DetailOrder"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {detailOrderRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.mango.hotel.DetailOrderResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.mango.hotel.DetailOrderResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.mango.hotel.DetailOrderResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.mango.hotel.SingleHotelResponse singleHotel(com.mango.hotel.SingleHotelRequest singleHotelRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "SingleHotel"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {singleHotelRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.mango.hotel.SingleHotelResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.mango.hotel.SingleHotelResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.mango.hotel.SingleHotelResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.mango.hotel.MutilHotelResponse mutilHotel(com.mango.hotel.MutilHotelRequest mutilHotelRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "MutilHotel"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {mutilHotelRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.mango.hotel.MutilHotelResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.mango.hotel.MutilHotelResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.mango.hotel.MutilHotelResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.mango.hotel.CancelRoomOrderResponse cancelRoomOrder(com.mango.hotel.CancelRoomOrderRequest cancelRoomOrderRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "CancelRoomOrder"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {cancelRoomOrderRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.mango.hotel.CancelRoomOrderResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.mango.hotel.CancelRoomOrderResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.mango.hotel.CancelRoomOrderResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.mango.hotel.CheckReservationResposne checkReservation(com.mango.hotel.CheckReservationRequest checkReservationRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "CheckReservation"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {checkReservationRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.mango.hotel.CheckReservationResposne) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.mango.hotel.CheckReservationResposne) org.apache.axis.utils.JavaUtils.convert(_resp, com.mango.hotel.CheckReservationResposne.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

    public com.mango.hotel.AddRoomOrderResponse addRoomOrder(com.mango.hotel.AddRoomOrderRequest addRoomOrderRequest) throws java.rmi.RemoteException {
        if (super.cachedEndpoint == null) {
            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("", "AddRoomOrder"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {        java.lang.Object _resp = _call.invoke(new java.lang.Object[] {addRoomOrderRequest});

        if (_resp instanceof java.rmi.RemoteException) {
            throw (java.rmi.RemoteException)_resp;
        }
        else {
            extractAttachments(_call);
            try {
                return (com.mango.hotel.AddRoomOrderResponse) _resp;
            } catch (java.lang.Exception _exception) {
                return (com.mango.hotel.AddRoomOrderResponse) org.apache.axis.utils.JavaUtils.convert(_resp, com.mango.hotel.AddRoomOrderResponse.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  throw axisFaultException;
}
    }

}
