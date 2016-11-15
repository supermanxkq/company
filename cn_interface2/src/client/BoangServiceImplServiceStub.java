/**
 * BoangServiceImplServiceStub.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.2  Built on : Sep 06, 2010 (09:42:01 CEST)
 */
package client;

/*
*  BoangServiceImplServiceStub java implementation
*/

public class BoangServiceImplServiceStub extends org.apache.axis2.client.Stub {
    protected org.apache.axis2.description.AxisOperation[] _operations;

    //hashmaps to keep the fault mapping
    private java.util.HashMap faultExceptionNameMap = new java.util.HashMap();

    private java.util.HashMap faultExceptionClassNameMap = new java.util.HashMap();

    private java.util.HashMap faultMessageMap = new java.util.HashMap();

    private static int counter = 0;

    private static synchronized java.lang.String getUniqueSuffix() {
        // reset the counter if it is greater than 99999
        if (counter > 99999) {
            counter = 0;
        }
        counter = counter + 1;
        return java.lang.Long.toString(System.currentTimeMillis()) + "_" + counter;
    }

    private void populateAxisService() throws org.apache.axis2.AxisFault {

        //creating the Service with a unique name
        _service = new org.apache.axis2.description.AxisService("BoangServiceImplService" + getUniqueSuffix());
        addAnonymousOperations();

        //creating the operations
        org.apache.axis2.description.AxisOperation __operation;

        _operations = new org.apache.axis2.description.AxisOperation[2];

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("http://boangService.service.web.insurance.boang.com/",
                "submitInsureReq"));
        _service.addOperation(__operation);

        _operations[0] = __operation;

        __operation = new org.apache.axis2.description.OutInAxisOperation();

        __operation.setName(new javax.xml.namespace.QName("http://boangService.service.web.insurance.boang.com/",
                "cancelInsurance"));
        _service.addOperation(__operation);

        _operations[1] = __operation;

    }

    //populates the faults
    private void populateFaults() {

    }

    /**
      *Constructor that takes in a configContext
      */

    public BoangServiceImplServiceStub(org.apache.axis2.context.ConfigurationContext configurationContext,
            java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(configurationContext, targetEndpoint, false);
    }

    /**
      * Constructor that takes in a configContext  and useseperate listner
      */
    public BoangServiceImplServiceStub(org.apache.axis2.context.ConfigurationContext configurationContext,
            java.lang.String targetEndpoint, boolean useSeparateListener) throws org.apache.axis2.AxisFault {
        //To populate AxisService
        populateAxisService();
        populateFaults();

        _serviceClient = new org.apache.axis2.client.ServiceClient(configurationContext, _service);

        _serviceClient.getOptions().setTo(new org.apache.axis2.addressing.EndpointReference(targetEndpoint));
        _serviceClient.getOptions().setUseSeparateListener(useSeparateListener);

    }

    /**
     * Default Constructor
     */
    public BoangServiceImplServiceStub(org.apache.axis2.context.ConfigurationContext configurationContext)
            throws org.apache.axis2.AxisFault {

        this(configurationContext, "http://121.41.114.170:8080/InsuranceService/boangService/boangService");

    }

    /**
     * Default Constructor
     */
    public BoangServiceImplServiceStub() throws org.apache.axis2.AxisFault {

        this("http://121.41.114.170:8080/InsuranceService/boangService/boangService");

    }

    /**
     * Constructor taking the target endpoint
     */
    public BoangServiceImplServiceStub(java.lang.String targetEndpoint) throws org.apache.axis2.AxisFault {
        this(null, targetEndpoint);
    }

    /**
     * Auto generated method signature
     * 
     * @see client.BoangServiceImplService#submitInsureReq
     * @param submitInsureReq
    
     */

    public client.BoangServiceImplServiceStub.SubmitInsureReqResponseE submitInsureReq(

    client.BoangServiceImplServiceStub.SubmitInsureReqE submitInsureReq)

    throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[0]
                    .getName());
            _operationClient.getOptions().setAction(
                    "http://boangService.service.web.insurance.boang.com/IBoangService/submitInsureReq");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), submitInsureReq,
                    optimizeContent(new javax.xml.namespace.QName(
                            "http://boangService.service.web.insurance.boang.com/", "submitInsureReq")));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
                    .getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    client.BoangServiceImplServiceStub.SubmitInsureReqResponseE.class,
                    getEnvelopeNamespaces(_returnEnv));

            return (client.BoangServiceImplServiceStub.SubmitInsureReqResponseE) object;

        }
        catch (org.apache.axis2.AxisFault f) {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
                                .get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                new java.lang.Class[] { messageClass });
                        m.invoke(ex, new java.lang.Object[] { messageObject });

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }
                    catch (java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }
                else {
                    throw f;
                }
            }
            else {
                throw f;
            }
        }
        finally {
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        }
    }

    /**
     * Auto generated method signature
     * 
     * @see client.BoangServiceImplService#cancelInsurance
     * @param cancelInsurance
    
     */

    public client.BoangServiceImplServiceStub.CancelInsuranceResponseE cancelInsurance(

    client.BoangServiceImplServiceStub.CancelInsuranceE cancelInsurance)

    throws java.rmi.RemoteException

    {
        org.apache.axis2.context.MessageContext _messageContext = null;
        try {
            org.apache.axis2.client.OperationClient _operationClient = _serviceClient.createClient(_operations[1]
                    .getName());
            _operationClient.getOptions().setAction(
                    "http://boangService.service.web.insurance.boang.com/IBoangService/cancelInsurance");
            _operationClient.getOptions().setExceptionToBeThrownOnSOAPFault(true);

            addPropertyToOperationClient(_operationClient,
                    org.apache.axis2.description.WSDL2Constants.ATTR_WHTTP_QUERY_PARAMETER_SEPARATOR, "&");

            // create a message context
            _messageContext = new org.apache.axis2.context.MessageContext();

            // create SOAP envelope with that payload
            org.apache.axiom.soap.SOAPEnvelope env = null;

            env = toEnvelope(getFactory(_operationClient.getOptions().getSoapVersionURI()), cancelInsurance,
                    optimizeContent(new javax.xml.namespace.QName(
                            "http://boangService.service.web.insurance.boang.com/", "cancelInsurance")));

            //adding SOAP soap_headers
            _serviceClient.addHeadersToEnvelope(env);
            // set the message context with that soap envelope
            _messageContext.setEnvelope(env);

            // add the message contxt to the operation client
            _operationClient.addMessageContext(_messageContext);

            //execute the operation client
            _operationClient.execute(true);

            org.apache.axis2.context.MessageContext _returnMessageContext = _operationClient
                    .getMessageContext(org.apache.axis2.wsdl.WSDLConstants.MESSAGE_LABEL_IN_VALUE);
            org.apache.axiom.soap.SOAPEnvelope _returnEnv = _returnMessageContext.getEnvelope();

            java.lang.Object object = fromOM(_returnEnv.getBody().getFirstElement(),
                    client.BoangServiceImplServiceStub.CancelInsuranceResponseE.class,
                    getEnvelopeNamespaces(_returnEnv));

            return (client.BoangServiceImplServiceStub.CancelInsuranceResponseE) object;

        }
        catch (org.apache.axis2.AxisFault f) {

            org.apache.axiom.om.OMElement faultElt = f.getDetail();
            if (faultElt != null) {
                if (faultExceptionNameMap.containsKey(faultElt.getQName())) {
                    //make the fault by reflection
                    try {
                        java.lang.String exceptionClassName = (java.lang.String) faultExceptionClassNameMap
                                .get(faultElt.getQName());
                        java.lang.Class exceptionClass = java.lang.Class.forName(exceptionClassName);
                        java.lang.Exception ex = (java.lang.Exception) exceptionClass.newInstance();
                        //message class
                        java.lang.String messageClassName = (java.lang.String) faultMessageMap.get(faultElt.getQName());
                        java.lang.Class messageClass = java.lang.Class.forName(messageClassName);
                        java.lang.Object messageObject = fromOM(faultElt, messageClass, null);
                        java.lang.reflect.Method m = exceptionClass.getMethod("setFaultMessage",
                                new java.lang.Class[] { messageClass });
                        m.invoke(ex, new java.lang.Object[] { messageObject });

                        throw new java.rmi.RemoteException(ex.getMessage(), ex);
                    }
                    catch (java.lang.ClassCastException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (java.lang.ClassNotFoundException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (java.lang.NoSuchMethodException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (java.lang.reflect.InvocationTargetException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (java.lang.IllegalAccessException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                    catch (java.lang.InstantiationException e) {
                        // we cannot intantiate the class - throw the original Axis fault
                        throw f;
                    }
                }
                else {
                    throw f;
                }
            }
            else {
                throw f;
            }
        }
        finally {
            _messageContext.getTransportOut().getSender().cleanup(_messageContext);
        }
    }

    /**
     *  A utility method that copies the namepaces from the SOAPEnvelope
     */
    private java.util.Map getEnvelopeNamespaces(org.apache.axiom.soap.SOAPEnvelope env) {
        java.util.Map returnMap = new java.util.HashMap();
        java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
        while (namespaceIterator.hasNext()) {
            org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
            returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
        }
        return returnMap;
    }

    private javax.xml.namespace.QName[] opNameArray = null;

    private boolean optimizeContent(javax.xml.namespace.QName opName) {

        if (opNameArray == null) {
            return false;
        }
        for (int i = 0; i < opNameArray.length; i++) {
            if (opName.equals(opNameArray[i])) {
                return true;
            }
        }
        return false;
    }

    //http://121.41.114.170:8080/InsuranceService/boangService/boangService
    public static class CancelInsuranceResponseE implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://boangService.service.web.insurance.boang.com/", "cancelInsuranceResponse", "ns1");

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for CancelInsuranceResponse
        */

        protected CancelInsuranceResponse localCancelInsuranceResponse;

        /**
        * Auto generated getter method
        * @return CancelInsuranceResponse
        */
        public CancelInsuranceResponse getCancelInsuranceResponse() {
            return localCancelInsuranceResponse;
        }

        /**
           * Auto generated setter method
           * @param param CancelInsuranceResponse
           */
        public void setCancelInsuranceResponse(CancelInsuranceResponse param) {

            this.localCancelInsuranceResponse = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    CancelInsuranceResponseE.this.serialize(MY_QNAME, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            //We can safely assume an element has only one type associated with it

            if (localCancelInsuranceResponse == null) {
                throw new org.apache.axis2.databinding.ADBException("Property cannot be null!");
            }
            localCancelInsuranceResponse.serialize(MY_QNAME, factory, xmlWriter);

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            //We can safely assume an element has only one type associated with it
            return localCancelInsuranceResponse.getPullParser(MY_QNAME);

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static CancelInsuranceResponseE parse(javax.xml.stream.XMLStreamReader reader)
                    throws java.lang.Exception {
                CancelInsuranceResponseE object = new CancelInsuranceResponseE();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    while (!reader.isEndElement()) {
                        if (reader.isStartElement()) {

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName(
                                            "http://boangService.service.web.insurance.boang.com/",
                                            "cancelInsuranceResponse").equals(reader.getName())) {

                                object.setCancelInsuranceResponse(CancelInsuranceResponse.Factory.parse(reader));

                            } // End of if for expected property start element

                            else {
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                        + reader.getLocalName());
                            }

                        }
                        else {
                            reader.next();
                        }
                    } // end of while loop

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class CallBackInfoBean implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = callBackInfoBean
                Namespace URI = http://boangService.service.web.insurance.boang.com/
                Namespace Prefix = ns1
                */

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for Callback
        */

        protected java.lang.String localCallback;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localCallbackTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getCallback() {
            return localCallback;
        }

        /**
           * Auto generated setter method
           * @param param Callback
           */
        public void setCallback(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localCallbackTracker = true;
            }
            else {
                localCallbackTracker = false;

            }

            this.localCallback = param;

        }

        /**
        * field for ClientType
        */

        protected java.lang.String localClientType;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localClientTypeTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getClientType() {
            return localClientType;
        }

        /**
           * Auto generated setter method
           * @param param ClientType
           */
        public void setClientType(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localClientTypeTracker = true;
            }
            else {
                localClientTypeTracker = false;

            }

            this.localClientType = param;

        }

        /**
        * field for Merchantsign
        */

        protected java.lang.String localMerchantsign;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localMerchantsignTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getMerchantsign() {
            return localMerchantsign;
        }

        /**
           * Auto generated setter method
           * @param param Merchantsign
           */
        public void setMerchantsign(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localMerchantsignTracker = true;
            }
            else {
                localMerchantsignTracker = false;

            }

            this.localMerchantsign = param;

        }

        /**
        * field for UserId
        */

        protected java.lang.String localUserId;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localUserIdTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getUserId() {
            return localUserId;
        }

        /**
           * Auto generated setter method
           * @param param UserId
           */
        public void setUserId(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localUserIdTracker = true;
            }
            else {
                localUserIdTracker = false;

            }

            this.localUserId = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                    parentQName) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    CallBackInfoBean.this.serialize(parentQName, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            java.lang.String prefix = null;
            java.lang.String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                if (writerPrefix != null) {
                    xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                }
                else {
                    if (prefix == null) {
                        prefix = generatePrefix(namespace);
                    }

                    xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }
            }
            else {
                xmlWriter.writeStartElement(parentQName.getLocalPart());
            }

            if (serializeType) {

                java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                        "http://boangService.service.web.insurance.boang.com/");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
                            + ":callBackInfoBean", xmlWriter);
                }
                else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "callBackInfoBean",
                            xmlWriter);
                }

            }
            if (localCallbackTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "callback", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "callback");
                    }

                }
                else {
                    xmlWriter.writeStartElement("callback");
                }

                if (localCallback == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("callback cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localCallback);

                }

                xmlWriter.writeEndElement();
            }
            if (localClientTypeTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "clientType", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "clientType");
                    }

                }
                else {
                    xmlWriter.writeStartElement("clientType");
                }

                if (localClientType == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("clientType cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localClientType);

                }

                xmlWriter.writeEndElement();
            }
            if (localMerchantsignTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "merchantsign", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "merchantsign");
                    }

                }
                else {
                    xmlWriter.writeStartElement("merchantsign");
                }

                if (localMerchantsign == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("merchantsign cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localMerchantsign);

                }

                xmlWriter.writeEndElement();
            }
            if (localUserIdTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "userId", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "userId");
                    }

                }
                else {
                    xmlWriter.writeStartElement("userId");
                }

                if (localUserId == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("userId cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localUserId);

                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localCallbackTracker) {
                elementList.add(new javax.xml.namespace.QName("", "callback"));

                if (localCallback != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCallback));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("callback cannot be null!!");
                }
            }
            if (localClientTypeTracker) {
                elementList.add(new javax.xml.namespace.QName("", "clientType"));

                if (localClientType != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localClientType));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("clientType cannot be null!!");
                }
            }
            if (localMerchantsignTracker) {
                elementList.add(new javax.xml.namespace.QName("", "merchantsign"));

                if (localMerchantsign != null) {
                    elementList
                            .add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localMerchantsign));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("merchantsign cannot be null!!");
                }
            }
            if (localUserIdTracker) {
                elementList.add(new javax.xml.namespace.QName("", "userId"));

                if (localUserId != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUserId));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("userId cannot be null!!");
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static CallBackInfoBean parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
                CallBackInfoBean object = new CallBackInfoBean();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        java.lang.String fullTypeName = reader.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"callBackInfoBean".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (CallBackInfoBean) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "callback").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setCallback(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "clientType").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setClientType(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "merchantsign").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setMerchantsign(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("", "userId").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setUserId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class SendInfoBean implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = sendInfoBean
                Namespace URI = http://boangService.service.web.insurance.boang.com/
                Namespace Prefix = ns1
                */

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for CallBackInfoBean
        */

        protected CallBackInfoBean localCallBackInfoBean;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localCallBackInfoBeanTracker = false;

        /**
        * Auto generated getter method
        * @return CallBackInfoBean
        */
        public CallBackInfoBean getCallBackInfoBean() {
            return localCallBackInfoBean;
        }

        /**
           * Auto generated setter method
           * @param param CallBackInfoBean
           */
        public void setCallBackInfoBean(CallBackInfoBean param) {

            if (param != null) {
                //update the setting tracker
                localCallBackInfoBeanTracker = true;
            }
            else {
                localCallBackInfoBeanTracker = false;

            }

            this.localCallBackInfoBean = param;

        }

        /**
        * field for InsuranceInfoBean
        */

        protected InsuranceInfoBean localInsuranceInfoBean;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localInsuranceInfoBeanTracker = false;

        /**
        * Auto generated getter method
        * @return InsuranceInfoBean
        */
        public InsuranceInfoBean getInsuranceInfoBean() {
            return localInsuranceInfoBean;
        }

        /**
           * Auto generated setter method
           * @param param InsuranceInfoBean
           */
        public void setInsuranceInfoBean(InsuranceInfoBean param) {

            if (param != null) {
                //update the setting tracker
                localInsuranceInfoBeanTracker = true;
            }
            else {
                localInsuranceInfoBeanTracker = false;

            }

            this.localInsuranceInfoBean = param;

        }

        /**
        * field for OrderInfoBean
        */

        protected OrderInfoBean localOrderInfoBean;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localOrderInfoBeanTracker = false;

        /**
        * Auto generated getter method
        * @return OrderInfoBean
        */
        public OrderInfoBean getOrderInfoBean() {
            return localOrderInfoBean;
        }

        /**
           * Auto generated setter method
           * @param param OrderInfoBean
           */
        public void setOrderInfoBean(OrderInfoBean param) {

            if (param != null) {
                //update the setting tracker
                localOrderInfoBeanTracker = true;
            }
            else {
                localOrderInfoBeanTracker = false;

            }

            this.localOrderInfoBean = param;

        }

        /**
        * field for PassengerInfoList
        * This was an Array!
        */

        protected PassengerInfoBean[] localPassengerInfoList;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localPassengerInfoListTracker = false;

        /**
        * Auto generated getter method
        * @return PassengerInfoBean[]
        */
        public PassengerInfoBean[] getPassengerInfoList() {
            return localPassengerInfoList;
        }

        /**
         * validate the array for PassengerInfoList
         */
        protected void validatePassengerInfoList(PassengerInfoBean[] param) {

        }

        /**
         * Auto generated setter method
         * @param param PassengerInfoList
         */
        public void setPassengerInfoList(PassengerInfoBean[] param) {

            validatePassengerInfoList(param);

            if (param != null) {
                //update the setting tracker
                localPassengerInfoListTracker = true;
            }
            else {
                localPassengerInfoListTracker = true;

            }

            this.localPassengerInfoList = param;
        }

        /**
        * Auto generated add method for the array for convenience
        * @param param PassengerInfoBean
        */
        public void addPassengerInfoList(PassengerInfoBean param) {
            if (localPassengerInfoList == null) {
                localPassengerInfoList = new PassengerInfoBean[] {};
            }

            //update the setting tracker
            localPassengerInfoListTracker = true;

            java.util.List list = org.apache.axis2.databinding.utils.ConverterUtil.toList(localPassengerInfoList);
            list.add(param);
            this.localPassengerInfoList = (PassengerInfoBean[]) list.toArray(new PassengerInfoBean[list.size()]);

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                    parentQName) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    SendInfoBean.this.serialize(parentQName, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            java.lang.String prefix = null;
            java.lang.String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                if (writerPrefix != null) {
                    xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                }
                else {
                    if (prefix == null) {
                        prefix = generatePrefix(namespace);
                    }

                    xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }
            }
            else {
                xmlWriter.writeStartElement(parentQName.getLocalPart());
            }

            if (serializeType) {

                java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                        "http://boangService.service.web.insurance.boang.com/");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
                            + ":sendInfoBean", xmlWriter);
                }
                else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "sendInfoBean",
                            xmlWriter);
                }

            }
            if (localCallBackInfoBeanTracker) {
                if (localCallBackInfoBean == null) {
                    throw new org.apache.axis2.databinding.ADBException("callBackInfoBean cannot be null!!");
                }
                localCallBackInfoBean.serialize(new javax.xml.namespace.QName("", "callBackInfoBean"), factory,
                        xmlWriter);
            }
            if (localInsuranceInfoBeanTracker) {
                if (localInsuranceInfoBean == null) {
                    throw new org.apache.axis2.databinding.ADBException("insuranceInfoBean cannot be null!!");
                }
                localInsuranceInfoBean.serialize(new javax.xml.namespace.QName("", "insuranceInfoBean"), factory,
                        xmlWriter);
            }
            if (localOrderInfoBeanTracker) {
                if (localOrderInfoBean == null) {
                    throw new org.apache.axis2.databinding.ADBException("orderInfoBean cannot be null!!");
                }
                localOrderInfoBean.serialize(new javax.xml.namespace.QName("", "orderInfoBean"), factory, xmlWriter);
            }
            if (localPassengerInfoListTracker) {
                if (localPassengerInfoList != null) {
                    for (int i = 0; i < localPassengerInfoList.length; i++) {
                        if (localPassengerInfoList[i] != null) {
                            localPassengerInfoList[i].serialize(new javax.xml.namespace.QName("", "passengerInfoList"),
                                    factory, xmlWriter);
                        }
                        else {

                            // write null attribute
                            java.lang.String namespace2 = "";
                            if (!namespace2.equals("")) {
                                java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                                if (prefix2 == null) {
                                    prefix2 = generatePrefix(namespace2);

                                    xmlWriter.writeStartElement(prefix2, "passengerInfoList", namespace2);
                                    xmlWriter.writeNamespace(prefix2, namespace2);
                                    xmlWriter.setPrefix(prefix2, namespace2);

                                }
                                else {
                                    xmlWriter.writeStartElement(namespace2, "passengerInfoList");
                                }

                            }
                            else {
                                xmlWriter.writeStartElement("passengerInfoList");
                            }

                            // write the nil attribute
                            writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                            xmlWriter.writeEndElement();

                        }

                    }
                }
                else {

                    // write null attribute
                    java.lang.String namespace2 = "";
                    if (!namespace2.equals("")) {
                        java.lang.String prefix2 = xmlWriter.getPrefix(namespace2);

                        if (prefix2 == null) {
                            prefix2 = generatePrefix(namespace2);

                            xmlWriter.writeStartElement(prefix2, "passengerInfoList", namespace2);
                            xmlWriter.writeNamespace(prefix2, namespace2);
                            xmlWriter.setPrefix(prefix2, namespace2);

                        }
                        else {
                            xmlWriter.writeStartElement(namespace2, "passengerInfoList");
                        }

                    }
                    else {
                        xmlWriter.writeStartElement("passengerInfoList");
                    }

                    // write the nil attribute
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "nil", "1", xmlWriter);
                    xmlWriter.writeEndElement();

                }
            }
            xmlWriter.writeEndElement();

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localCallBackInfoBeanTracker) {
                elementList.add(new javax.xml.namespace.QName("", "callBackInfoBean"));

                if (localCallBackInfoBean == null) {
                    throw new org.apache.axis2.databinding.ADBException("callBackInfoBean cannot be null!!");
                }
                elementList.add(localCallBackInfoBean);
            }
            if (localInsuranceInfoBeanTracker) {
                elementList.add(new javax.xml.namespace.QName("", "insuranceInfoBean"));

                if (localInsuranceInfoBean == null) {
                    throw new org.apache.axis2.databinding.ADBException("insuranceInfoBean cannot be null!!");
                }
                elementList.add(localInsuranceInfoBean);
            }
            if (localOrderInfoBeanTracker) {
                elementList.add(new javax.xml.namespace.QName("", "orderInfoBean"));

                if (localOrderInfoBean == null) {
                    throw new org.apache.axis2.databinding.ADBException("orderInfoBean cannot be null!!");
                }
                elementList.add(localOrderInfoBean);
            }
            if (localPassengerInfoListTracker) {
                if (localPassengerInfoList != null) {
                    for (int i = 0; i < localPassengerInfoList.length; i++) {

                        if (localPassengerInfoList[i] != null) {
                            elementList.add(new javax.xml.namespace.QName("", "passengerInfoList"));
                            elementList.add(localPassengerInfoList[i]);
                        }
                        else {

                            elementList.add(new javax.xml.namespace.QName("", "passengerInfoList"));
                            elementList.add(null);

                        }

                    }
                }
                else {

                    elementList.add(new javax.xml.namespace.QName("", "passengerInfoList"));
                    elementList.add(localPassengerInfoList);

                }

            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static SendInfoBean parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
                SendInfoBean object = new SendInfoBean();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        java.lang.String fullTypeName = reader.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"sendInfoBean".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SendInfoBean) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    java.util.ArrayList list4 = new java.util.ArrayList();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "callBackInfoBean").equals(reader.getName())) {

                        object.setCallBackInfoBean(CallBackInfoBean.Factory.parse(reader));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "insuranceInfoBean").equals(reader.getName())) {

                        object.setInsuranceInfoBean(InsuranceInfoBean.Factory.parse(reader));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "orderInfoBean").equals(reader.getName())) {

                        object.setOrderInfoBean(OrderInfoBean.Factory.parse(reader));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "passengerInfoList").equals(reader.getName())) {

                        // Process the array and step past its final element's end.

                        nillableValue = reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "nil");
                        if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                            list4.add(null);
                            reader.next();
                        }
                        else {
                            list4.add(PassengerInfoBean.Factory.parse(reader));
                        }
                        //loop until we find a start element that is not part of this array
                        boolean loopDone4 = false;
                        while (!loopDone4) {
                            // We should be at the end element, but make sure
                            while (!reader.isEndElement())
                                reader.next();
                            // Step out of this element
                            reader.next();
                            // Step to next element event.
                            while (!reader.isStartElement() && !reader.isEndElement())
                                reader.next();
                            if (reader.isEndElement()) {
                                //two continuous end elements means we are exiting the xml structure
                                loopDone4 = true;
                            }
                            else {
                                if (new javax.xml.namespace.QName("", "passengerInfoList").equals(reader.getName())) {

                                    nillableValue = reader.getAttributeValue(
                                            "http://www.w3.org/2001/XMLSchema-instance", "nil");
                                    if ("true".equals(nillableValue) || "1".equals(nillableValue)) {
                                        list4.add(null);
                                        reader.next();
                                    }
                                    else {
                                        list4.add(PassengerInfoBean.Factory.parse(reader));
                                    }
                                }
                                else {
                                    loopDone4 = true;
                                }
                            }
                        }
                        // call the converter utility  to convert and set the array

                        object.setPassengerInfoList((PassengerInfoBean[]) org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToArray(PassengerInfoBean.class, list4));

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class SubmitInsureReqResponseE implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://boangService.service.web.insurance.boang.com/", "submitInsureReqResponse", "ns1");

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for SubmitInsureReqResponse
        */

        protected SubmitInsureReqResponse localSubmitInsureReqResponse;

        /**
        * Auto generated getter method
        * @return SubmitInsureReqResponse
        */
        public SubmitInsureReqResponse getSubmitInsureReqResponse() {
            return localSubmitInsureReqResponse;
        }

        /**
           * Auto generated setter method
           * @param param SubmitInsureReqResponse
           */
        public void setSubmitInsureReqResponse(SubmitInsureReqResponse param) {

            this.localSubmitInsureReqResponse = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    SubmitInsureReqResponseE.this.serialize(MY_QNAME, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            //We can safely assume an element has only one type associated with it

            if (localSubmitInsureReqResponse == null) {
                throw new org.apache.axis2.databinding.ADBException("Property cannot be null!");
            }
            localSubmitInsureReqResponse.serialize(MY_QNAME, factory, xmlWriter);

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            //We can safely assume an element has only one type associated with it
            return localSubmitInsureReqResponse.getPullParser(MY_QNAME);

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static SubmitInsureReqResponseE parse(javax.xml.stream.XMLStreamReader reader)
                    throws java.lang.Exception {
                SubmitInsureReqResponseE object = new SubmitInsureReqResponseE();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    while (!reader.isEndElement()) {
                        if (reader.isStartElement()) {

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName(
                                            "http://boangService.service.web.insurance.boang.com/",
                                            "submitInsureReqResponse").equals(reader.getName())) {

                                object.setSubmitInsureReqResponse(SubmitInsureReqResponse.Factory.parse(reader));

                            } // End of if for expected property start element

                            else {
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                        + reader.getLocalName());
                            }

                        }
                        else {
                            reader.next();
                        }
                    } // end of while loop

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class SyncResponse implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = syncResponse
                Namespace URI = http://boangService.service.web.insurance.boang.com/
                Namespace Prefix = ns1
                */

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for Code
        */

        protected java.lang.String localCode;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localCodeTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getCode() {
            return localCode;
        }

        /**
           * Auto generated setter method
           * @param param Code
           */
        public void setCode(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localCodeTracker = true;
            }
            else {
                localCodeTracker = false;

            }

            this.localCode = param;

        }

        /**
        * field for Desc
        */

        protected java.lang.String localDesc;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localDescTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getDesc() {
            return localDesc;
        }

        /**
           * Auto generated setter method
           * @param param Desc
           */
        public void setDesc(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localDescTracker = true;
            }
            else {
                localDescTracker = false;

            }

            this.localDesc = param;

        }

        /**
        * field for Success
        */

        protected boolean localSuccess;

        /**
        * Auto generated getter method
        * @return boolean
        */
        public boolean getSuccess() {
            return localSuccess;
        }

        /**
           * Auto generated setter method
           * @param param Success
           */
        public void setSuccess(boolean param) {

            this.localSuccess = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                    parentQName) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    SyncResponse.this.serialize(parentQName, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            java.lang.String prefix = null;
            java.lang.String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                if (writerPrefix != null) {
                    xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                }
                else {
                    if (prefix == null) {
                        prefix = generatePrefix(namespace);
                    }

                    xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }
            }
            else {
                xmlWriter.writeStartElement(parentQName.getLocalPart());
            }

            if (serializeType) {

                java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                        "http://boangService.service.web.insurance.boang.com/");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
                            + ":syncResponse", xmlWriter);
                }
                else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "syncResponse",
                            xmlWriter);
                }

            }
            if (localCodeTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "code", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "code");
                    }

                }
                else {
                    xmlWriter.writeStartElement("code");
                }

                if (localCode == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("code cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localCode);

                }

                xmlWriter.writeEndElement();
            }
            if (localDescTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "desc", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "desc");
                    }

                }
                else {
                    xmlWriter.writeStartElement("desc");
                }

                if (localDesc == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("desc cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localDesc);

                }

                xmlWriter.writeEndElement();
            }
            namespace = "";
            if (!namespace.equals("")) {
                prefix = xmlWriter.getPrefix(namespace);

                if (prefix == null) {
                    prefix = generatePrefix(namespace);

                    xmlWriter.writeStartElement(prefix, "success", namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);

                }
                else {
                    xmlWriter.writeStartElement(namespace, "success");
                }

            }
            else {
                xmlWriter.writeStartElement("success");
            }

            if (false) {

                throw new org.apache.axis2.databinding.ADBException("success cannot be null!!");

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil
                        .convertToString(localSuccess));
            }

            xmlWriter.writeEndElement();

            xmlWriter.writeEndElement();

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localCodeTracker) {
                elementList.add(new javax.xml.namespace.QName("", "code"));

                if (localCode != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCode));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("code cannot be null!!");
                }
            }
            if (localDescTracker) {
                elementList.add(new javax.xml.namespace.QName("", "desc"));

                if (localDesc != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDesc));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("desc cannot be null!!");
                }
            }
            elementList.add(new javax.xml.namespace.QName("", "success"));

            elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSuccess));

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static SyncResponse parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
                SyncResponse object = new SyncResponse();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        java.lang.String fullTypeName = reader.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"syncResponse".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SyncResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("", "code").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setCode(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("", "desc").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setDesc(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "success").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setSuccess(org.apache.axis2.databinding.utils.ConverterUtil.convertToBoolean(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {
                        // A start element we are not expecting indicates an invalid parameter was passed
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());
                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class CancelInfoBean implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = cancelInfoBean
                Namespace URI = http://boangService.service.web.insurance.boang.com/
                Namespace Prefix = ns1
                */

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for CancelReason
        */

        protected java.lang.String localCancelReason;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localCancelReasonTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getCancelReason() {
            return localCancelReason;
        }

        /**
           * Auto generated setter method
           * @param param CancelReason
           */
        public void setCancelReason(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localCancelReasonTracker = true;
            }
            else {
                localCancelReasonTracker = false;

            }

            this.localCancelReason = param;

        }

        /**
        * field for ClientType
        */

        protected java.lang.String localClientType;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localClientTypeTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getClientType() {
            return localClientType;
        }

        /**
           * Auto generated setter method
           * @param param ClientType
           */
        public void setClientType(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localClientTypeTracker = true;
            }
            else {
                localClientTypeTracker = false;

            }

            this.localClientType = param;

        }

        /**
        * field for PolicyId
        */

        protected java.lang.String localPolicyId;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localPolicyIdTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getPolicyId() {
            return localPolicyId;
        }

        /**
           * Auto generated setter method
           * @param param PolicyId
           */
        public void setPolicyId(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localPolicyIdTracker = true;
            }
            else {
                localPolicyIdTracker = false;

            }

            this.localPolicyId = param;

        }

        /**
        * field for Policyno
        */

        protected java.lang.String localPolicyno;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localPolicynoTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getPolicyno() {
            return localPolicyno;
        }

        /**
           * Auto generated setter method
           * @param param Policyno
           */
        public void setPolicyno(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localPolicynoTracker = true;
            }
            else {
                localPolicynoTracker = false;

            }

            this.localPolicyno = param;

        }

        /**
        * field for UserId
        */

        protected java.lang.String localUserId;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localUserIdTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getUserId() {
            return localUserId;
        }

        /**
           * Auto generated setter method
           * @param param UserId
           */
        public void setUserId(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localUserIdTracker = true;
            }
            else {
                localUserIdTracker = false;

            }

            this.localUserId = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                    parentQName) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    CancelInfoBean.this.serialize(parentQName, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            java.lang.String prefix = null;
            java.lang.String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                if (writerPrefix != null) {
                    xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                }
                else {
                    if (prefix == null) {
                        prefix = generatePrefix(namespace);
                    }

                    xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }
            }
            else {
                xmlWriter.writeStartElement(parentQName.getLocalPart());
            }

            if (serializeType) {

                java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                        "http://boangService.service.web.insurance.boang.com/");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
                            + ":cancelInfoBean", xmlWriter);
                }
                else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "cancelInfoBean",
                            xmlWriter);
                }

            }
            if (localCancelReasonTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "cancelReason", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "cancelReason");
                    }

                }
                else {
                    xmlWriter.writeStartElement("cancelReason");
                }

                if (localCancelReason == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("cancelReason cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localCancelReason);

                }

                xmlWriter.writeEndElement();
            }
            if (localClientTypeTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "clientType", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "clientType");
                    }

                }
                else {
                    xmlWriter.writeStartElement("clientType");
                }

                if (localClientType == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("clientType cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localClientType);

                }

                xmlWriter.writeEndElement();
            }
            if (localPolicyIdTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "policyId", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "policyId");
                    }

                }
                else {
                    xmlWriter.writeStartElement("policyId");
                }

                if (localPolicyId == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("policyId cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localPolicyId);

                }

                xmlWriter.writeEndElement();
            }
            if (localPolicynoTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "policyno", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "policyno");
                    }

                }
                else {
                    xmlWriter.writeStartElement("policyno");
                }

                if (localPolicyno == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("policyno cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localPolicyno);

                }

                xmlWriter.writeEndElement();
            }
            if (localUserIdTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "userId", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "userId");
                    }

                }
                else {
                    xmlWriter.writeStartElement("userId");
                }

                if (localUserId == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("userId cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localUserId);

                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localCancelReasonTracker) {
                elementList.add(new javax.xml.namespace.QName("", "cancelReason"));

                if (localCancelReason != null) {
                    elementList
                            .add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCancelReason));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("cancelReason cannot be null!!");
                }
            }
            if (localClientTypeTracker) {
                elementList.add(new javax.xml.namespace.QName("", "clientType"));

                if (localClientType != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localClientType));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("clientType cannot be null!!");
                }
            }
            if (localPolicyIdTracker) {
                elementList.add(new javax.xml.namespace.QName("", "policyId"));

                if (localPolicyId != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPolicyId));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("policyId cannot be null!!");
                }
            }
            if (localPolicynoTracker) {
                elementList.add(new javax.xml.namespace.QName("", "policyno"));

                if (localPolicyno != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localPolicyno));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("policyno cannot be null!!");
                }
            }
            if (localUserIdTracker) {
                elementList.add(new javax.xml.namespace.QName("", "userId"));

                if (localUserId != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localUserId));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("userId cannot be null!!");
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static CancelInfoBean parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
                CancelInfoBean object = new CancelInfoBean();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        java.lang.String fullTypeName = reader.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"cancelInfoBean".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (CancelInfoBean) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "cancelReason").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setCancelReason(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "clientType").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setClientType(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "policyId").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setPolicyId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "policyno").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setPolicyno(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("", "userId").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setUserId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class CancelInsurance implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = cancelInsurance
                Namespace URI = http://boangService.service.web.insurance.boang.com/
                Namespace Prefix = ns1
                */

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for Arg0
        */

        protected CancelInfoBean localArg0;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localArg0Tracker = false;

        /**
        * Auto generated getter method
        * @return CancelInfoBean
        */
        public CancelInfoBean getArg0() {
            return localArg0;
        }

        /**
           * Auto generated setter method
           * @param param Arg0
           */
        public void setArg0(CancelInfoBean param) {

            if (param != null) {
                //update the setting tracker
                localArg0Tracker = true;
            }
            else {
                localArg0Tracker = false;

            }

            this.localArg0 = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                    parentQName) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    CancelInsurance.this.serialize(parentQName, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            java.lang.String prefix = null;
            java.lang.String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                if (writerPrefix != null) {
                    xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                }
                else {
                    if (prefix == null) {
                        prefix = generatePrefix(namespace);
                    }

                    xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }
            }
            else {
                xmlWriter.writeStartElement(parentQName.getLocalPart());
            }

            if (serializeType) {

                java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                        "http://boangService.service.web.insurance.boang.com/");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
                            + ":cancelInsurance", xmlWriter);
                }
                else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "cancelInsurance",
                            xmlWriter);
                }

            }
            if (localArg0Tracker) {
                if (localArg0 == null) {
                    throw new org.apache.axis2.databinding.ADBException("arg0 cannot be null!!");
                }
                localArg0.serialize(new javax.xml.namespace.QName("", "arg0"), factory, xmlWriter);
            }
            xmlWriter.writeEndElement();

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localArg0Tracker) {
                elementList.add(new javax.xml.namespace.QName("", "arg0"));

                if (localArg0 == null) {
                    throw new org.apache.axis2.databinding.ADBException("arg0 cannot be null!!");
                }
                elementList.add(localArg0);
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static CancelInsurance parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
                CancelInsurance object = new CancelInsurance();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        java.lang.String fullTypeName = reader.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"cancelInsurance".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (CancelInsurance) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("", "arg0").equals(reader.getName())) {

                        object.setArg0(CancelInfoBean.Factory.parse(reader));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class SubmitInsureReqResponse implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = submitInsureReqResponse
                Namespace URI = http://boangService.service.web.insurance.boang.com/
                Namespace Prefix = ns1
                */

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for _return
        */

        protected SyncResponse local_return;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean local_returnTracker = false;

        /**
        * Auto generated getter method
        * @return SyncResponse
        */
        public SyncResponse get_return() {
            return local_return;
        }

        /**
           * Auto generated setter method
           * @param param _return
           */
        public void set_return(SyncResponse param) {

            if (param != null) {
                //update the setting tracker
                local_returnTracker = true;
            }
            else {
                local_returnTracker = false;

            }

            this.local_return = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                    parentQName) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    SubmitInsureReqResponse.this.serialize(parentQName, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            java.lang.String prefix = null;
            java.lang.String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                if (writerPrefix != null) {
                    xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                }
                else {
                    if (prefix == null) {
                        prefix = generatePrefix(namespace);
                    }

                    xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }
            }
            else {
                xmlWriter.writeStartElement(parentQName.getLocalPart());
            }

            if (serializeType) {

                java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                        "http://boangService.service.web.insurance.boang.com/");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
                            + ":submitInsureReqResponse", xmlWriter);
                }
                else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            "submitInsureReqResponse", xmlWriter);
                }

            }
            if (local_returnTracker) {
                if (local_return == null) {
                    throw new org.apache.axis2.databinding.ADBException("return cannot be null!!");
                }
                local_return.serialize(new javax.xml.namespace.QName("", "return"), factory, xmlWriter);
            }
            xmlWriter.writeEndElement();

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (local_returnTracker) {
                elementList.add(new javax.xml.namespace.QName("", "return"));

                if (local_return == null) {
                    throw new org.apache.axis2.databinding.ADBException("return cannot be null!!");
                }
                elementList.add(local_return);
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static SubmitInsureReqResponse parse(javax.xml.stream.XMLStreamReader reader)
                    throws java.lang.Exception {
                SubmitInsureReqResponse object = new SubmitInsureReqResponse();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        java.lang.String fullTypeName = reader.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"submitInsureReqResponse".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SubmitInsureReqResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("", "return").equals(reader.getName())) {

                        object.set_return(SyncResponse.Factory.parse(reader));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class CancelInsuranceE implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://boangService.service.web.insurance.boang.com/", "cancelInsurance", "ns1");

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for CancelInsurance
        */

        protected CancelInsurance localCancelInsurance;

        /**
        * Auto generated getter method
        * @return CancelInsurance
        */
        public CancelInsurance getCancelInsurance() {
            return localCancelInsurance;
        }

        /**
           * Auto generated setter method
           * @param param CancelInsurance
           */
        public void setCancelInsurance(CancelInsurance param) {

            this.localCancelInsurance = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    CancelInsuranceE.this.serialize(MY_QNAME, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            //We can safely assume an element has only one type associated with it

            if (localCancelInsurance == null) {
                throw new org.apache.axis2.databinding.ADBException("Property cannot be null!");
            }
            localCancelInsurance.serialize(MY_QNAME, factory, xmlWriter);

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            //We can safely assume an element has only one type associated with it
            return localCancelInsurance.getPullParser(MY_QNAME);

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static CancelInsuranceE parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
                CancelInsuranceE object = new CancelInsuranceE();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    while (!reader.isEndElement()) {
                        if (reader.isStartElement()) {

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName(
                                            "http://boangService.service.web.insurance.boang.com/", "cancelInsurance")
                                            .equals(reader.getName())) {

                                object.setCancelInsurance(CancelInsurance.Factory.parse(reader));

                            } // End of if for expected property start element

                            else {
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                        + reader.getLocalName());
                            }

                        }
                        else {
                            reader.next();
                        }
                    } // end of while loop

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class InsuranceInfoBean implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = insuranceInfoBean
                Namespace URI = http://boangService.service.web.insurance.boang.com/
                Namespace Prefix = ns1
                */

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for Productcode
        */

        protected java.lang.String localProductcode;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localProductcodeTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getProductcode() {
            return localProductcode;
        }

        /**
           * Auto generated setter method
           * @param param Productcode
           */
        public void setProductcode(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localProductcodeTracker = true;
            }
            else {
                localProductcodeTracker = false;

            }

            this.localProductcode = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                    parentQName) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    InsuranceInfoBean.this.serialize(parentQName, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            java.lang.String prefix = null;
            java.lang.String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                if (writerPrefix != null) {
                    xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                }
                else {
                    if (prefix == null) {
                        prefix = generatePrefix(namespace);
                    }

                    xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }
            }
            else {
                xmlWriter.writeStartElement(parentQName.getLocalPart());
            }

            if (serializeType) {

                java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                        "http://boangService.service.web.insurance.boang.com/");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
                            + ":insuranceInfoBean", xmlWriter);
                }
                else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "insuranceInfoBean",
                            xmlWriter);
                }

            }
            if (localProductcodeTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "productcode", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "productcode");
                    }

                }
                else {
                    xmlWriter.writeStartElement("productcode");
                }

                if (localProductcode == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("productcode cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localProductcode);

                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localProductcodeTracker) {
                elementList.add(new javax.xml.namespace.QName("", "productcode"));

                if (localProductcode != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localProductcode));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("productcode cannot be null!!");
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static InsuranceInfoBean parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
                InsuranceInfoBean object = new InsuranceInfoBean();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        java.lang.String fullTypeName = reader.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"insuranceInfoBean".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (InsuranceInfoBean) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "productcode").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setProductcode(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class ExtensionMapper {

        public static java.lang.Object getTypeObject(java.lang.String namespaceURI, java.lang.String typeName,
                javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {

            if ("http://boangService.service.web.insurance.boang.com/".equals(namespaceURI)
                    && "callBackInfoBean".equals(typeName)) {

                return CallBackInfoBean.Factory.parse(reader);

            }

            if ("http://boangService.service.web.insurance.boang.com/".equals(namespaceURI)
                    && "submitInsureReqResponse".equals(typeName)) {

                return SubmitInsureReqResponse.Factory.parse(reader);

            }

            if ("http://boangService.service.web.insurance.boang.com/".equals(namespaceURI)
                    && "submitInsureReq".equals(typeName)) {

                return SubmitInsureReq.Factory.parse(reader);

            }

            if ("http://boangService.service.web.insurance.boang.com/".equals(namespaceURI)
                    && "syncResponse".equals(typeName)) {

                return SyncResponse.Factory.parse(reader);

            }

            if ("http://boangService.service.web.insurance.boang.com/".equals(namespaceURI)
                    && "sendInfoBean".equals(typeName)) {

                return SendInfoBean.Factory.parse(reader);

            }

            if ("http://boangService.service.web.insurance.boang.com/".equals(namespaceURI)
                    && "orderInfoBean".equals(typeName)) {

                return OrderInfoBean.Factory.parse(reader);

            }

            if ("http://boangService.service.web.insurance.boang.com/".equals(namespaceURI)
                    && "insuranceInfoBean".equals(typeName)) {

                return InsuranceInfoBean.Factory.parse(reader);

            }

            if ("http://boangService.service.web.insurance.boang.com/".equals(namespaceURI)
                    && "cancelInsuranceResponse".equals(typeName)) {

                return CancelInsuranceResponse.Factory.parse(reader);

            }

            if ("http://boangService.service.web.insurance.boang.com/".equals(namespaceURI)
                    && "passengerInfoBean".equals(typeName)) {

                return PassengerInfoBean.Factory.parse(reader);

            }

            if ("http://boangService.service.web.insurance.boang.com/".equals(namespaceURI)
                    && "cancelInsurance".equals(typeName)) {

                return CancelInsurance.Factory.parse(reader);

            }

            if ("http://boangService.service.web.insurance.boang.com/".equals(namespaceURI)
                    && "cancelInfoBean".equals(typeName)) {

                return CancelInfoBean.Factory.parse(reader);

            }

            throw new org.apache.axis2.databinding.ADBException("Unsupported type " + namespaceURI + " " + typeName);
        }

    }

    public static class SubmitInsureReq implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = submitInsureReq
                Namespace URI = http://boangService.service.web.insurance.boang.com/
                Namespace Prefix = ns1
                */

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for Arg0
        */

        protected SendInfoBean localArg0;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localArg0Tracker = false;

        /**
        * Auto generated getter method
        * @return SendInfoBean
        */
        public SendInfoBean getArg0() {
            return localArg0;
        }

        /**
           * Auto generated setter method
           * @param param Arg0
           */
        public void setArg0(SendInfoBean param) {

            if (param != null) {
                //update the setting tracker
                localArg0Tracker = true;
            }
            else {
                localArg0Tracker = false;

            }

            this.localArg0 = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                    parentQName) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    SubmitInsureReq.this.serialize(parentQName, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            java.lang.String prefix = null;
            java.lang.String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                if (writerPrefix != null) {
                    xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                }
                else {
                    if (prefix == null) {
                        prefix = generatePrefix(namespace);
                    }

                    xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }
            }
            else {
                xmlWriter.writeStartElement(parentQName.getLocalPart());
            }

            if (serializeType) {

                java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                        "http://boangService.service.web.insurance.boang.com/");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
                            + ":submitInsureReq", xmlWriter);
                }
                else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "submitInsureReq",
                            xmlWriter);
                }

            }
            if (localArg0Tracker) {
                if (localArg0 == null) {
                    throw new org.apache.axis2.databinding.ADBException("arg0 cannot be null!!");
                }
                localArg0.serialize(new javax.xml.namespace.QName("", "arg0"), factory, xmlWriter);
            }
            xmlWriter.writeEndElement();

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localArg0Tracker) {
                elementList.add(new javax.xml.namespace.QName("", "arg0"));

                if (localArg0 == null) {
                    throw new org.apache.axis2.databinding.ADBException("arg0 cannot be null!!");
                }
                elementList.add(localArg0);
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static SubmitInsureReq parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
                SubmitInsureReq object = new SubmitInsureReq();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        java.lang.String fullTypeName = reader.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"submitInsureReq".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (SubmitInsureReq) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("", "arg0").equals(reader.getName())) {

                        object.setArg0(SendInfoBean.Factory.parse(reader));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class OrderInfoBean implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = orderInfoBean
                Namespace URI = http://boangService.service.web.insurance.boang.com/
                Namespace Prefix = ns1
                */

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for Aircode
        */

        protected java.lang.String localAircode;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localAircodeTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getAircode() {
            return localAircode;
        }

        /**
           * Auto generated setter method
           * @param param Aircode
           */
        public void setAircode(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localAircodeTracker = true;
            }
            else {
                localAircodeTracker = false;

            }

            this.localAircode = param;

        }

        /**
        * field for Cabincode
        */

        protected java.lang.String localCabincode;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localCabincodeTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getCabincode() {
            return localCabincode;
        }

        /**
           * Auto generated setter method
           * @param param Cabincode
           */
        public void setCabincode(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localCabincodeTracker = true;
            }
            else {
                localCabincodeTracker = false;

            }

            this.localCabincode = param;

        }

        /**
        * field for Destination
        */

        protected java.lang.String localDestination;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localDestinationTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getDestination() {
            return localDestination;
        }

        /**
           * Auto generated setter method
           * @param param Destination
           */
        public void setDestination(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localDestinationTracker = true;
            }
            else {
                localDestinationTracker = false;

            }

            this.localDestination = param;

        }

        /**
        * field for OrderId
        */

        protected java.lang.String localOrderId;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localOrderIdTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getOrderId() {
            return localOrderId;
        }

        /**
           * Auto generated setter method
           * @param param OrderId
           */
        public void setOrderId(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localOrderIdTracker = true;
            }
            else {
                localOrderIdTracker = false;

            }

            this.localOrderId = param;

        }

        /**
        * field for Origin
        */

        protected java.lang.String localOrigin;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localOriginTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getOrigin() {
            return localOrigin;
        }

        /**
           * Auto generated setter method
           * @param param Origin
           */
        public void setOrigin(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localOriginTracker = true;
            }
            else {
                localOriginTracker = false;

            }

            this.localOrigin = param;

        }

        /**
        * field for StartDate
        */

        protected java.lang.String localStartDate;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localStartDateTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getStartDate() {
            return localStartDate;
        }

        /**
           * Auto generated setter method
           * @param param StartDate
           */
        public void setStartDate(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localStartDateTracker = true;
            }
            else {
                localStartDateTracker = false;

            }

            this.localStartDate = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                    parentQName) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    OrderInfoBean.this.serialize(parentQName, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            java.lang.String prefix = null;
            java.lang.String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                if (writerPrefix != null) {
                    xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                }
                else {
                    if (prefix == null) {
                        prefix = generatePrefix(namespace);
                    }

                    xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }
            }
            else {
                xmlWriter.writeStartElement(parentQName.getLocalPart());
            }

            if (serializeType) {

                java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                        "http://boangService.service.web.insurance.boang.com/");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
                            + ":orderInfoBean", xmlWriter);
                }
                else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "orderInfoBean",
                            xmlWriter);
                }

            }
            if (localAircodeTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "aircode", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "aircode");
                    }

                }
                else {
                    xmlWriter.writeStartElement("aircode");
                }

                if (localAircode == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("aircode cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localAircode);

                }

                xmlWriter.writeEndElement();
            }
            if (localCabincodeTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "cabincode", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "cabincode");
                    }

                }
                else {
                    xmlWriter.writeStartElement("cabincode");
                }

                if (localCabincode == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("cabincode cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localCabincode);

                }

                xmlWriter.writeEndElement();
            }
            if (localDestinationTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "destination", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "destination");
                    }

                }
                else {
                    xmlWriter.writeStartElement("destination");
                }

                if (localDestination == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("destination cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localDestination);

                }

                xmlWriter.writeEndElement();
            }
            if (localOrderIdTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "orderId", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "orderId");
                    }

                }
                else {
                    xmlWriter.writeStartElement("orderId");
                }

                if (localOrderId == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("orderId cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localOrderId);

                }

                xmlWriter.writeEndElement();
            }
            if (localOriginTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "origin", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "origin");
                    }

                }
                else {
                    xmlWriter.writeStartElement("origin");
                }

                if (localOrigin == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("origin cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localOrigin);

                }

                xmlWriter.writeEndElement();
            }
            if (localStartDateTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "startDate", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "startDate");
                    }

                }
                else {
                    xmlWriter.writeStartElement("startDate");
                }

                if (localStartDate == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("startDate cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localStartDate);

                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localAircodeTracker) {
                elementList.add(new javax.xml.namespace.QName("", "aircode"));

                if (localAircode != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localAircode));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("aircode cannot be null!!");
                }
            }
            if (localCabincodeTracker) {
                elementList.add(new javax.xml.namespace.QName("", "cabincode"));

                if (localCabincode != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localCabincode));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("cabincode cannot be null!!");
                }
            }
            if (localDestinationTracker) {
                elementList.add(new javax.xml.namespace.QName("", "destination"));

                if (localDestination != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localDestination));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("destination cannot be null!!");
                }
            }
            if (localOrderIdTracker) {
                elementList.add(new javax.xml.namespace.QName("", "orderId"));

                if (localOrderId != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOrderId));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("orderId cannot be null!!");
                }
            }
            if (localOriginTracker) {
                elementList.add(new javax.xml.namespace.QName("", "origin"));

                if (localOrigin != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localOrigin));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("origin cannot be null!!");
                }
            }
            if (localStartDateTracker) {
                elementList.add(new javax.xml.namespace.QName("", "startDate"));

                if (localStartDate != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localStartDate));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("startDate cannot be null!!");
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static OrderInfoBean parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
                OrderInfoBean object = new OrderInfoBean();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        java.lang.String fullTypeName = reader.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"orderInfoBean".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (OrderInfoBean) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "aircode").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setAircode(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "cabincode").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setCabincode(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "destination").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setDestination(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "orderId").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setOrderId(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("", "origin").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setOrigin(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "startDate").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setStartDate(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class CancelInsuranceResponse implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = cancelInsuranceResponse
                Namespace URI = http://boangService.service.web.insurance.boang.com/
                Namespace Prefix = ns1
                */

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for _return
        */

        protected SyncResponse local_return;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean local_returnTracker = false;

        /**
        * Auto generated getter method
        * @return SyncResponse
        */
        public SyncResponse get_return() {
            return local_return;
        }

        /**
           * Auto generated setter method
           * @param param _return
           */
        public void set_return(SyncResponse param) {

            if (param != null) {
                //update the setting tracker
                local_returnTracker = true;
            }
            else {
                local_returnTracker = false;

            }

            this.local_return = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                    parentQName) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    CancelInsuranceResponse.this.serialize(parentQName, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            java.lang.String prefix = null;
            java.lang.String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                if (writerPrefix != null) {
                    xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                }
                else {
                    if (prefix == null) {
                        prefix = generatePrefix(namespace);
                    }

                    xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }
            }
            else {
                xmlWriter.writeStartElement(parentQName.getLocalPart());
            }

            if (serializeType) {

                java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                        "http://boangService.service.web.insurance.boang.com/");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
                            + ":cancelInsuranceResponse", xmlWriter);
                }
                else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type",
                            "cancelInsuranceResponse", xmlWriter);
                }

            }
            if (local_returnTracker) {
                if (local_return == null) {
                    throw new org.apache.axis2.databinding.ADBException("return cannot be null!!");
                }
                local_return.serialize(new javax.xml.namespace.QName("", "return"), factory, xmlWriter);
            }
            xmlWriter.writeEndElement();

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (local_returnTracker) {
                elementList.add(new javax.xml.namespace.QName("", "return"));

                if (local_return == null) {
                    throw new org.apache.axis2.databinding.ADBException("return cannot be null!!");
                }
                elementList.add(local_return);
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static CancelInsuranceResponse parse(javax.xml.stream.XMLStreamReader reader)
                    throws java.lang.Exception {
                CancelInsuranceResponse object = new CancelInsuranceResponse();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        java.lang.String fullTypeName = reader.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"cancelInsuranceResponse".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (CancelInsuranceResponse) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("", "return").equals(reader.getName())) {

                        object.set_return(SyncResponse.Factory.parse(reader));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class SubmitInsureReqE implements org.apache.axis2.databinding.ADBBean {

        public static final javax.xml.namespace.QName MY_QNAME = new javax.xml.namespace.QName(
                "http://boangService.service.web.insurance.boang.com/", "submitInsureReq", "ns1");

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for SubmitInsureReq
        */

        protected SubmitInsureReq localSubmitInsureReq;

        /**
        * Auto generated getter method
        * @return SubmitInsureReq
        */
        public SubmitInsureReq getSubmitInsureReq() {
            return localSubmitInsureReq;
        }

        /**
           * Auto generated setter method
           * @param param SubmitInsureReq
           */
        public void setSubmitInsureReq(SubmitInsureReq param) {

            this.localSubmitInsureReq = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this, MY_QNAME) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    SubmitInsureReqE.this.serialize(MY_QNAME, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(MY_QNAME, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            //We can safely assume an element has only one type associated with it

            if (localSubmitInsureReq == null) {
                throw new org.apache.axis2.databinding.ADBException("Property cannot be null!");
            }
            localSubmitInsureReq.serialize(MY_QNAME, factory, xmlWriter);

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            //We can safely assume an element has only one type associated with it
            return localSubmitInsureReq.getPullParser(MY_QNAME);

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static SubmitInsureReqE parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
                SubmitInsureReqE object = new SubmitInsureReqE();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    while (!reader.isEndElement()) {
                        if (reader.isStartElement()) {

                            if (reader.isStartElement()
                                    && new javax.xml.namespace.QName(
                                            "http://boangService.service.web.insurance.boang.com/", "submitInsureReq")
                                            .equals(reader.getName())) {

                                object.setSubmitInsureReq(SubmitInsureReq.Factory.parse(reader));

                            } // End of if for expected property start element

                            else {
                                // A start element we are not expecting indicates an invalid parameter was passed
                                throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                        + reader.getLocalName());
                            }

                        }
                        else {
                            reader.next();
                        }
                    } // end of while loop

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    public static class PassengerInfoBean implements org.apache.axis2.databinding.ADBBean {
        /* This type was generated from the piece of schema that had
                name = passengerInfoBean
                Namespace URI = http://boangService.service.web.insurance.boang.com/
                Namespace Prefix = ns1
                */

        private static java.lang.String generatePrefix(java.lang.String namespace) {
            if (namespace.equals("http://boangService.service.web.insurance.boang.com/")) {
                return "ns1";
            }
            return org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
        }

        /**
        * field for InsuredEmail
        */

        protected java.lang.String localInsuredEmail;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localInsuredEmailTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getInsuredEmail() {
            return localInsuredEmail;
        }

        /**
           * Auto generated setter method
           * @param param InsuredEmail
           */
        public void setInsuredEmail(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localInsuredEmailTracker = true;
            }
            else {
                localInsuredEmailTracker = false;

            }

            this.localInsuredEmail = param;

        }

        /**
        * field for Insuredbirthday
        */

        protected java.lang.String localInsuredbirthday;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localInsuredbirthdayTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getInsuredbirthday() {
            return localInsuredbirthday;
        }

        /**
           * Auto generated setter method
           * @param param Insuredbirthday
           */
        public void setInsuredbirthday(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localInsuredbirthdayTracker = true;
            }
            else {
                localInsuredbirthdayTracker = false;

            }

            this.localInsuredbirthday = param;

        }

        /**
        * field for Insuredidno
        */

        protected java.lang.String localInsuredidno;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localInsuredidnoTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getInsuredidno() {
            return localInsuredidno;
        }

        /**
           * Auto generated setter method
           * @param param Insuredidno
           */
        public void setInsuredidno(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localInsuredidnoTracker = true;
            }
            else {
                localInsuredidnoTracker = false;

            }

            this.localInsuredidno = param;

        }

        /**
        * field for Insuredidtype
        */

        protected java.lang.String localInsuredidtype;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localInsuredidtypeTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getInsuredidtype() {
            return localInsuredidtype;
        }

        /**
           * Auto generated setter method
           * @param param Insuredidtype
           */
        public void setInsuredidtype(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localInsuredidtypeTracker = true;
            }
            else {
                localInsuredidtypeTracker = false;

            }

            this.localInsuredidtype = param;

        }

        /**
        * field for Insuredmobile
        */

        protected java.lang.String localInsuredmobile;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localInsuredmobileTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getInsuredmobile() {
            return localInsuredmobile;
        }

        /**
           * Auto generated setter method
           * @param param Insuredmobile
           */
        public void setInsuredmobile(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localInsuredmobileTracker = true;
            }
            else {
                localInsuredmobileTracker = false;

            }

            this.localInsuredmobile = param;

        }

        /**
        * field for Insuredname
        */

        protected java.lang.String localInsuredname;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localInsurednameTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getInsuredname() {
            return localInsuredname;
        }

        /**
           * Auto generated setter method
           * @param param Insuredname
           */
        public void setInsuredname(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localInsurednameTracker = true;
            }
            else {
                localInsurednameTracker = false;

            }

            this.localInsuredname = param;

        }

        /**
        * field for Insurednum
        */

        protected java.lang.String localInsurednum;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localInsurednumTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getInsurednum() {
            return localInsurednum;
        }

        /**
           * Auto generated setter method
           * @param param Insurednum
           */
        public void setInsurednum(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localInsurednumTracker = true;
            }
            else {
                localInsurednumTracker = false;

            }

            this.localInsurednum = param;

        }

        /**
        * field for Sex
        */

        protected java.lang.String localSex;

        /*  This tracker boolean wil be used to detect whether the user called the set method
        *   for this attribute. It will be used to determine whether to include this field
        *   in the serialized XML
        */
        protected boolean localSexTracker = false;

        /**
        * Auto generated getter method
        * @return java.lang.String
        */
        public java.lang.String getSex() {
            return localSex;
        }

        /**
           * Auto generated setter method
           * @param param Sex
           */
        public void setSex(java.lang.String param) {

            if (param != null) {
                //update the setting tracker
                localSexTracker = true;
            }
            else {
                localSexTracker = false;

            }

            this.localSex = param;

        }

        /**
        * isReaderMTOMAware
        * @return true if the reader supports MTOM
        */
        public static boolean isReaderMTOMAware(javax.xml.stream.XMLStreamReader reader) {
            boolean isReaderMTOMAware = false;

            try {
                isReaderMTOMAware = java.lang.Boolean.TRUE.equals(reader
                        .getProperty(org.apache.axiom.om.OMConstants.IS_DATA_HANDLERS_AWARE));
            }
            catch (java.lang.IllegalArgumentException e) {
                isReaderMTOMAware = false;
            }
            return isReaderMTOMAware;
        }

        /**
        *
        * @param parentQName
        * @param factory
        * @return org.apache.axiom.om.OMElement
        */
        public org.apache.axiom.om.OMElement getOMElement(final javax.xml.namespace.QName parentQName,
                final org.apache.axiom.om.OMFactory factory) throws org.apache.axis2.databinding.ADBException {

            org.apache.axiom.om.OMDataSource dataSource = new org.apache.axis2.databinding.ADBDataSource(this,
                    parentQName) {

                public void serialize(org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                        throws javax.xml.stream.XMLStreamException {
                    PassengerInfoBean.this.serialize(parentQName, factory, xmlWriter);
                }
            };
            return new org.apache.axiom.om.impl.llom.OMSourcedElementImpl(parentQName, factory, dataSource);

        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {
            serialize(parentQName, factory, xmlWriter, false);
        }

        public void serialize(final javax.xml.namespace.QName parentQName, final org.apache.axiom.om.OMFactory factory,
                org.apache.axis2.databinding.utils.writer.MTOMAwareXMLStreamWriter xmlWriter, boolean serializeType)
                throws javax.xml.stream.XMLStreamException, org.apache.axis2.databinding.ADBException {

            java.lang.String prefix = null;
            java.lang.String namespace = null;

            prefix = parentQName.getPrefix();
            namespace = parentQName.getNamespaceURI();

            if ((namespace != null) && (namespace.trim().length() > 0)) {
                java.lang.String writerPrefix = xmlWriter.getPrefix(namespace);
                if (writerPrefix != null) {
                    xmlWriter.writeStartElement(namespace, parentQName.getLocalPart());
                }
                else {
                    if (prefix == null) {
                        prefix = generatePrefix(namespace);
                    }

                    xmlWriter.writeStartElement(prefix, parentQName.getLocalPart(), namespace);
                    xmlWriter.writeNamespace(prefix, namespace);
                    xmlWriter.setPrefix(prefix, namespace);
                }
            }
            else {
                xmlWriter.writeStartElement(parentQName.getLocalPart());
            }

            if (serializeType) {

                java.lang.String namespacePrefix = registerPrefix(xmlWriter,
                        "http://boangService.service.web.insurance.boang.com/");
                if ((namespacePrefix != null) && (namespacePrefix.trim().length() > 0)) {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", namespacePrefix
                            + ":passengerInfoBean", xmlWriter);
                }
                else {
                    writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "type", "passengerInfoBean",
                            xmlWriter);
                }

            }
            if (localInsuredEmailTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "insuredEmail", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "insuredEmail");
                    }

                }
                else {
                    xmlWriter.writeStartElement("insuredEmail");
                }

                if (localInsuredEmail == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("insuredEmail cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localInsuredEmail);

                }

                xmlWriter.writeEndElement();
            }
            if (localInsuredbirthdayTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "insuredbirthday", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "insuredbirthday");
                    }

                }
                else {
                    xmlWriter.writeStartElement("insuredbirthday");
                }

                if (localInsuredbirthday == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("insuredbirthday cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localInsuredbirthday);

                }

                xmlWriter.writeEndElement();
            }
            if (localInsuredidnoTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "insuredidno", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "insuredidno");
                    }

                }
                else {
                    xmlWriter.writeStartElement("insuredidno");
                }

                if (localInsuredidno == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("insuredidno cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localInsuredidno);

                }

                xmlWriter.writeEndElement();
            }
            if (localInsuredidtypeTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "insuredidtype", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "insuredidtype");
                    }

                }
                else {
                    xmlWriter.writeStartElement("insuredidtype");
                }

                if (localInsuredidtype == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("insuredidtype cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localInsuredidtype);

                }

                xmlWriter.writeEndElement();
            }
            if (localInsuredmobileTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "insuredmobile", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "insuredmobile");
                    }

                }
                else {
                    xmlWriter.writeStartElement("insuredmobile");
                }

                if (localInsuredmobile == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("insuredmobile cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localInsuredmobile);

                }

                xmlWriter.writeEndElement();
            }
            if (localInsurednameTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "insuredname", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "insuredname");
                    }

                }
                else {
                    xmlWriter.writeStartElement("insuredname");
                }

                if (localInsuredname == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("insuredname cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localInsuredname);

                }

                xmlWriter.writeEndElement();
            }
            if (localInsurednumTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "insurednum", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "insurednum");
                    }

                }
                else {
                    xmlWriter.writeStartElement("insurednum");
                }

                if (localInsurednum == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("insurednum cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localInsurednum);

                }

                xmlWriter.writeEndElement();
            }
            if (localSexTracker) {
                namespace = "";
                if (!namespace.equals("")) {
                    prefix = xmlWriter.getPrefix(namespace);

                    if (prefix == null) {
                        prefix = generatePrefix(namespace);

                        xmlWriter.writeStartElement(prefix, "sex", namespace);
                        xmlWriter.writeNamespace(prefix, namespace);
                        xmlWriter.setPrefix(prefix, namespace);

                    }
                    else {
                        xmlWriter.writeStartElement(namespace, "sex");
                    }

                }
                else {
                    xmlWriter.writeStartElement("sex");
                }

                if (localSex == null) {
                    // write the nil attribute

                    throw new org.apache.axis2.databinding.ADBException("sex cannot be null!!");

                }
                else {

                    xmlWriter.writeCharacters(localSex);

                }

                xmlWriter.writeEndElement();
            }
            xmlWriter.writeEndElement();

        }

        /**
         * Util method to write an attribute with the ns prefix
         */
        private void writeAttribute(java.lang.String prefix, java.lang.String namespace, java.lang.String attName,
                java.lang.String attValue, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            if (xmlWriter.getPrefix(namespace) == null) {
                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);

            }

            xmlWriter.writeAttribute(namespace, attName, attValue);

        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeAttribute(java.lang.String namespace, java.lang.String attName, java.lang.String attValue,
                javax.xml.stream.XMLStreamWriter xmlWriter) throws javax.xml.stream.XMLStreamException {
            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attValue);
            }
        }

        /**
          * Util method to write an attribute without the ns prefix
          */
        private void writeQNameAttribute(java.lang.String namespace, java.lang.String attName,
                javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            java.lang.String attributeNamespace = qname.getNamespaceURI();
            java.lang.String attributePrefix = xmlWriter.getPrefix(attributeNamespace);
            if (attributePrefix == null) {
                attributePrefix = registerPrefix(xmlWriter, attributeNamespace);
            }
            java.lang.String attributeValue;
            if (attributePrefix.trim().length() > 0) {
                attributeValue = attributePrefix + ":" + qname.getLocalPart();
            }
            else {
                attributeValue = qname.getLocalPart();
            }

            if (namespace.equals("")) {
                xmlWriter.writeAttribute(attName, attributeValue);
            }
            else {
                registerPrefix(xmlWriter, namespace);
                xmlWriter.writeAttribute(namespace, attName, attributeValue);
            }
        }

        /**
         *  method to handle Qnames
         */

        private void writeQName(javax.xml.namespace.QName qname, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String namespaceURI = qname.getNamespaceURI();
            if (namespaceURI != null) {
                java.lang.String prefix = xmlWriter.getPrefix(namespaceURI);
                if (prefix == null) {
                    prefix = generatePrefix(namespaceURI);
                    xmlWriter.writeNamespace(prefix, namespaceURI);
                    xmlWriter.setPrefix(prefix, namespaceURI);
                }

                if (prefix.trim().length() > 0) {
                    xmlWriter.writeCharacters(prefix + ":"
                            + org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }
                else {
                    // i.e this is the default namespace
                    xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
                }

            }
            else {
                xmlWriter.writeCharacters(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qname));
            }
        }

        private void writeQNames(javax.xml.namespace.QName[] qnames, javax.xml.stream.XMLStreamWriter xmlWriter)
                throws javax.xml.stream.XMLStreamException {

            if (qnames != null) {
                // we have to store this data until last moment since it is not possible to write any
                // namespace data after writing the charactor data
                java.lang.StringBuffer stringToWrite = new java.lang.StringBuffer();
                java.lang.String namespaceURI = null;
                java.lang.String prefix = null;

                for (int i = 0; i < qnames.length; i++) {
                    if (i > 0) {
                        stringToWrite.append(" ");
                    }
                    namespaceURI = qnames[i].getNamespaceURI();
                    if (namespaceURI != null) {
                        prefix = xmlWriter.getPrefix(namespaceURI);
                        if ((prefix == null) || (prefix.length() == 0)) {
                            prefix = generatePrefix(namespaceURI);
                            xmlWriter.writeNamespace(prefix, namespaceURI);
                            xmlWriter.setPrefix(prefix, namespaceURI);
                        }

                        if (prefix.trim().length() > 0) {
                            stringToWrite
                                    .append(prefix)
                                    .append(":")
                                    .append(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(qnames[i]));
                        }
                        else {
                            stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                    .convertToString(qnames[i]));
                        }
                    }
                    else {
                        stringToWrite.append(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(qnames[i]));
                    }
                }
                xmlWriter.writeCharacters(stringToWrite.toString());
            }

        }

        /**
        * Register a namespace prefix
        */
        private java.lang.String registerPrefix(javax.xml.stream.XMLStreamWriter xmlWriter, java.lang.String namespace)
                throws javax.xml.stream.XMLStreamException {
            java.lang.String prefix = xmlWriter.getPrefix(namespace);

            if (prefix == null) {
                prefix = generatePrefix(namespace);

                while (xmlWriter.getNamespaceContext().getNamespaceURI(prefix) != null) {
                    prefix = org.apache.axis2.databinding.utils.BeanUtil.getUniquePrefix();
                }

                xmlWriter.writeNamespace(prefix, namespace);
                xmlWriter.setPrefix(prefix, namespace);
            }

            return prefix;
        }

        /**
        * databinding method to get an XML representation of this object
        *
        */
        public javax.xml.stream.XMLStreamReader getPullParser(javax.xml.namespace.QName qName)
                throws org.apache.axis2.databinding.ADBException {

            java.util.ArrayList elementList = new java.util.ArrayList();
            java.util.ArrayList attribList = new java.util.ArrayList();

            if (localInsuredEmailTracker) {
                elementList.add(new javax.xml.namespace.QName("", "insuredEmail"));

                if (localInsuredEmail != null) {
                    elementList
                            .add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInsuredEmail));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("insuredEmail cannot be null!!");
                }
            }
            if (localInsuredbirthdayTracker) {
                elementList.add(new javax.xml.namespace.QName("", "insuredbirthday"));

                if (localInsuredbirthday != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
                            .convertToString(localInsuredbirthday));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("insuredbirthday cannot be null!!");
                }
            }
            if (localInsuredidnoTracker) {
                elementList.add(new javax.xml.namespace.QName("", "insuredidno"));

                if (localInsuredidno != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInsuredidno));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("insuredidno cannot be null!!");
                }
            }
            if (localInsuredidtypeTracker) {
                elementList.add(new javax.xml.namespace.QName("", "insuredidtype"));

                if (localInsuredidtype != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
                            .convertToString(localInsuredidtype));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("insuredidtype cannot be null!!");
                }
            }
            if (localInsuredmobileTracker) {
                elementList.add(new javax.xml.namespace.QName("", "insuredmobile"));

                if (localInsuredmobile != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil
                            .convertToString(localInsuredmobile));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("insuredmobile cannot be null!!");
                }
            }
            if (localInsurednameTracker) {
                elementList.add(new javax.xml.namespace.QName("", "insuredname"));

                if (localInsuredname != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInsuredname));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("insuredname cannot be null!!");
                }
            }
            if (localInsurednumTracker) {
                elementList.add(new javax.xml.namespace.QName("", "insurednum"));

                if (localInsurednum != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localInsurednum));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("insurednum cannot be null!!");
                }
            }
            if (localSexTracker) {
                elementList.add(new javax.xml.namespace.QName("", "sex"));

                if (localSex != null) {
                    elementList.add(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(localSex));
                }
                else {
                    throw new org.apache.axis2.databinding.ADBException("sex cannot be null!!");
                }
            }

            return new org.apache.axis2.databinding.utils.reader.ADBXMLStreamReaderImpl(qName, elementList.toArray(),
                    attribList.toArray());

        }

        /**
         *  Factory class that keeps the parse method
         */
        public static class Factory {

            /**
            * static method to create the object
            * Precondition:  If this object is an element, the current or next start element starts this object and any intervening reader events are ignorable
            *                If this object is not an element, it is a complex type and the reader is at the event just after the outer start element
            * Postcondition: If this object is an element, the reader is positioned at its end element
            *                If this object is a complex type, the reader is positioned at the end element of its outer element
            */
            public static PassengerInfoBean parse(javax.xml.stream.XMLStreamReader reader) throws java.lang.Exception {
                PassengerInfoBean object = new PassengerInfoBean();

                int event;
                java.lang.String nillableValue = null;
                java.lang.String prefix = "";
                java.lang.String namespaceuri = "";
                try {

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.getAttributeValue("http://www.w3.org/2001/XMLSchema-instance", "type") != null) {
                        java.lang.String fullTypeName = reader.getAttributeValue(
                                "http://www.w3.org/2001/XMLSchema-instance", "type");
                        if (fullTypeName != null) {
                            java.lang.String nsPrefix = null;
                            if (fullTypeName.indexOf(":") > -1) {
                                nsPrefix = fullTypeName.substring(0, fullTypeName.indexOf(":"));
                            }
                            nsPrefix = nsPrefix == null ? "" : nsPrefix;

                            java.lang.String type = fullTypeName.substring(fullTypeName.indexOf(":") + 1);

                            if (!"passengerInfoBean".equals(type)) {
                                //find namespace for the prefix
                                java.lang.String nsUri = reader.getNamespaceContext().getNamespaceURI(nsPrefix);
                                return (PassengerInfoBean) ExtensionMapper.getTypeObject(nsUri, type, reader);
                            }

                        }

                    }

                    // Note all attributes that were handled. Used to differ normal attributes
                    // from anyAttributes.
                    java.util.Vector handledAttributes = new java.util.Vector();

                    reader.next();

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "insuredEmail").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setInsuredEmail(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "insuredbirthday").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setInsuredbirthday(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "insuredidno").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setInsuredidno(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "insuredidtype").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setInsuredidtype(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "insuredmobile").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setInsuredmobile(org.apache.axis2.databinding.utils.ConverterUtil
                                .convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "insuredname").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setInsuredname(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement()
                            && new javax.xml.namespace.QName("", "insurednum").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setInsurednum(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement() && new javax.xml.namespace.QName("", "sex").equals(reader.getName())) {

                        java.lang.String content = reader.getElementText();

                        object.setSex(org.apache.axis2.databinding.utils.ConverterUtil.convertToString(content));

                        reader.next();

                    } // End of if for expected property start element

                    else {

                    }

                    while (!reader.isStartElement() && !reader.isEndElement())
                        reader.next();

                    if (reader.isStartElement())
                        // A start element we are not expecting indicates a trailing invalid property
                        throw new org.apache.axis2.databinding.ADBException("Unexpected subelement "
                                + reader.getLocalName());

                }
                catch (javax.xml.stream.XMLStreamException e) {
                    throw new java.lang.Exception(e);
                }

                return object;
            }

        }//end of factory class

    }

    private org.apache.axiom.om.OMElement toOM(client.BoangServiceImplServiceStub.SubmitInsureReqE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(client.BoangServiceImplServiceStub.SubmitInsureReqE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(client.BoangServiceImplServiceStub.SubmitInsureReqResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(client.BoangServiceImplServiceStub.SubmitInsureReqResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(client.BoangServiceImplServiceStub.CancelInsuranceE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(client.BoangServiceImplServiceStub.CancelInsuranceE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.om.OMElement toOM(client.BoangServiceImplServiceStub.CancelInsuranceResponseE param,
            boolean optimizeContent) throws org.apache.axis2.AxisFault {

        try {
            return param.getOMElement(client.BoangServiceImplServiceStub.CancelInsuranceResponseE.MY_QNAME,
                    org.apache.axiom.om.OMAbstractFactory.getOMFactory());
        }
        catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
            client.BoangServiceImplServiceStub.SubmitInsureReqE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {

            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(client.BoangServiceImplServiceStub.SubmitInsureReqE.MY_QNAME, factory));
            return emptyEnvelope;
        }
        catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    /* methods to provide back word compatibility */

    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory,
            client.BoangServiceImplServiceStub.CancelInsuranceE param, boolean optimizeContent)
            throws org.apache.axis2.AxisFault {

        try {

            org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();
            emptyEnvelope.getBody().addChild(
                    param.getOMElement(client.BoangServiceImplServiceStub.CancelInsuranceE.MY_QNAME, factory));
            return emptyEnvelope;
        }
        catch (org.apache.axis2.databinding.ADBException e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }

    }

    /* methods to provide back word compatibility */

    /**
    *  get the default envelope
    */
    private org.apache.axiom.soap.SOAPEnvelope toEnvelope(org.apache.axiom.soap.SOAPFactory factory) {
        return factory.getDefaultEnvelope();
    }

    private java.lang.Object fromOM(org.apache.axiom.om.OMElement param, java.lang.Class type,
            java.util.Map extraNamespaces) throws org.apache.axis2.AxisFault {

        try {

            if (client.BoangServiceImplServiceStub.SubmitInsureReqE.class.equals(type)) {

                return client.BoangServiceImplServiceStub.SubmitInsureReqE.Factory.parse(param
                        .getXMLStreamReaderWithoutCaching());

            }

            if (client.BoangServiceImplServiceStub.SubmitInsureReqResponseE.class.equals(type)) {

                return client.BoangServiceImplServiceStub.SubmitInsureReqResponseE.Factory.parse(param
                        .getXMLStreamReaderWithoutCaching());

            }

            if (client.BoangServiceImplServiceStub.CancelInsuranceE.class.equals(type)) {

                return client.BoangServiceImplServiceStub.CancelInsuranceE.Factory.parse(param
                        .getXMLStreamReaderWithoutCaching());

            }

            if (client.BoangServiceImplServiceStub.CancelInsuranceResponseE.class.equals(type)) {

                return client.BoangServiceImplServiceStub.CancelInsuranceResponseE.Factory.parse(param
                        .getXMLStreamReaderWithoutCaching());

            }

        }
        catch (java.lang.Exception e) {
            throw org.apache.axis2.AxisFault.makeFault(e);
        }
        return null;
    }

}
