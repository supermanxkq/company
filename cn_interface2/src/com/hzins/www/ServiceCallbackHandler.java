
/**
 * ServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.2  Built on : Sep 06, 2010 (09:42:01 CEST)
 */

    package com.hzins.www;

    /**
     *  ServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class ServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public ServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public ServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for policyReprint method
            * override this method for handling normal response from policyReprint operation
            */
           public void receiveResultpolicyReprint(
                    com.hzins.www.ServiceStub.PolicyReprintResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from policyReprint operation
           */
            public void receiveErrorpolicyReprint(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for orderApply method
            * override this method for handling normal response from orderApply operation
            */
           public void receiveResultorderApply(
                    com.hzins.www.ServiceStub.OrderApplyResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from orderApply operation
           */
            public void receiveErrororderApply(java.lang.Exception e) {
            }
                


    }
    