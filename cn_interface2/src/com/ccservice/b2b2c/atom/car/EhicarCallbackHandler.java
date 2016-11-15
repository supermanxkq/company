
/**
 * EhicarCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.2  Built on : Sep 06, 2010 (09:42:01 CEST)
 */

    package com.ccservice.b2b2c.atom.car;

    /**
     *  EhicarCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class EhicarCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public EhicarCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public EhicarCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getSelfDriveOrderPrice method
            * override this method for handling normal response from getSelfDriveOrderPrice operation
            */
           public void receiveResultgetSelfDriveOrderPrice(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveOrderPriceResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveOrderPrice operation
           */
            public void receiveErrorgetSelfDriveOrderPrice(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveProvinces method
            * override this method for handling normal response from getSelfDriveProvinces operation
            */
           public void receiveResultgetSelfDriveProvinces(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveProvincesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveProvinces operation
           */
            public void receiveErrorgetSelfDriveProvinces(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveCarBrand method
            * override this method for handling normal response from getSelfDriveCarBrand operation
            */
           public void receiveResultgetSelfDriveCarBrand(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveCarBrandResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveCarBrand operation
           */
            public void receiveErrorgetSelfDriveCarBrand(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveCarTypeByCity method
            * override this method for handling normal response from getSelfDriveCarTypeByCity operation
            */
           public void receiveResultgetSelfDriveCarTypeByCity(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveCarTypeByCityResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveCarTypeByCity operation
           */
            public void receiveErrorgetSelfDriveCarTypeByCity(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for querySelfDriveOrder method
            * override this method for handling normal response from querySelfDriveOrder operation
            */
           public void receiveResultquerySelfDriveOrder(
                    com.ccservice.b2b2c.atom.car.EhicarStub.QuerySelfDriveOrderResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from querySelfDriveOrder operation
           */
            public void receiveErrorquerySelfDriveOrder(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for updateSelfDriveUser method
            * override this method for handling normal response from updateSelfDriveUser operation
            */
           public void receiveResultupdateSelfDriveUser(
                    com.ccservice.b2b2c.atom.car.EhicarStub.UpdateSelfDriveUserResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from updateSelfDriveUser operation
           */
            public void receiveErrorupdateSelfDriveUser(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveOrder method
            * override this method for handling normal response from getSelfDriveOrder operation
            */
           public void receiveResultgetSelfDriveOrder(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveOrderResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveOrder operation
           */
            public void receiveErrorgetSelfDriveOrder(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for cancelSelfDriveOrder method
            * override this method for handling normal response from cancelSelfDriveOrder operation
            */
           public void receiveResultcancelSelfDriveOrder(
                    com.ccservice.b2b2c.atom.car.EhicarStub.CancelSelfDriveOrderResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from cancelSelfDriveOrder operation
           */
            public void receiveErrorcancelSelfDriveOrder(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for updateSelfDrivePayAmount method
            * override this method for handling normal response from updateSelfDrivePayAmount operation
            */
           public void receiveResultupdateSelfDrivePayAmount(
                    com.ccservice.b2b2c.atom.car.EhicarStub.UpdateSelfDrivePayAmountResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from updateSelfDrivePayAmount operation
           */
            public void receiveErrorupdateSelfDrivePayAmount(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getJYSelfDriveCarTypes method
            * override this method for handling normal response from getJYSelfDriveCarTypes operation
            */
           public void receiveResultgetJYSelfDriveCarTypes(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetJYSelfDriveCarTypesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getJYSelfDriveCarTypes operation
           */
            public void receiveErrorgetJYSelfDriveCarTypes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for updateSelfDriveOrder method
            * override this method for handling normal response from updateSelfDriveOrder operation
            */
           public void receiveResultupdateSelfDriveOrder(
                    com.ccservice.b2b2c.atom.car.EhicarStub.UpdateSelfDriveOrderResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from updateSelfDriveOrder operation
           */
            public void receiveErrorupdateSelfDriveOrder(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveUser method
            * override this method for handling normal response from getSelfDriveUser operation
            */
           public void receiveResultgetSelfDriveUser(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveUserResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveUser operation
           */
            public void receiveErrorgetSelfDriveUser(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriverCarAvailableDate method
            * override this method for handling normal response from getSelfDriverCarAvailableDate operation
            */
           public void receiveResultgetSelfDriverCarAvailableDate(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriverCarAvailableDateResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriverCarAvailableDate operation
           */
            public void receiveErrorgetSelfDriverCarAvailableDate(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveCarMode method
            * override this method for handling normal response from getSelfDriveCarMode operation
            */
           public void receiveResultgetSelfDriveCarMode(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveCarModeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveCarMode operation
           */
            public void receiveErrorgetSelfDriveCarMode(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveCarTypes method
            * override this method for handling normal response from getSelfDriveCarTypes operation
            */
           public void receiveResultgetSelfDriveCarTypes(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveCarTypesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveCarTypes operation
           */
            public void receiveErrorgetSelfDriveCarTypes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveStoresAndCarTypes method
            * override this method for handling normal response from getSelfDriveStoresAndCarTypes operation
            */
           public void receiveResultgetSelfDriveStoresAndCarTypes(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveStoresAndCarTypesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveStoresAndCarTypes operation
           */
            public void receiveErrorgetSelfDriveStoresAndCarTypes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveCarTypesByCity method
            * override this method for handling normal response from getSelfDriveCarTypesByCity operation
            */
           public void receiveResultgetSelfDriveCarTypesByCity(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveCarTypesByCityResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveCarTypesByCity operation
           */
            public void receiveErrorgetSelfDriveCarTypesByCity(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAdditionalServices method
            * override this method for handling normal response from getAdditionalServices operation
            */
           public void receiveResultgetAdditionalServices(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetAdditionalServicesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAdditionalServices operation
           */
            public void receiveErrorgetAdditionalServices(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for addSelfDriveReletOrder method
            * override this method for handling normal response from addSelfDriveReletOrder operation
            */
           public void receiveResultaddSelfDriveReletOrder(
                    com.ccservice.b2b2c.atom.car.EhicarStub.AddSelfDriveReletOrderResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from addSelfDriveReletOrder operation
           */
            public void receiveErroraddSelfDriveReletOrder(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for addSelfDriveOrder method
            * override this method for handling normal response from addSelfDriveOrder operation
            */
           public void receiveResultaddSelfDriveOrder(
                    com.ccservice.b2b2c.atom.car.EhicarStub.AddSelfDriveOrderResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from addSelfDriveOrder operation
           */
            public void receiveErroraddSelfDriveOrder(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for addSelfDriveUser method
            * override this method for handling normal response from addSelfDriveUser operation
            */
           public void receiveResultaddSelfDriveUser(
                    com.ccservice.b2b2c.atom.car.EhicarStub.AddSelfDriveUserResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from addSelfDriveUser operation
           */
            public void receiveErroraddSelfDriveUser(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveCities method
            * override this method for handling normal response from getSelfDriveCities operation
            */
           public void receiveResultgetSelfDriveCities(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveCitiesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveCities operation
           */
            public void receiveErrorgetSelfDriveCities(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for addSelfDriveOrderWithNewUser method
            * override this method for handling normal response from addSelfDriveOrderWithNewUser operation
            */
           public void receiveResultaddSelfDriveOrderWithNewUser(
                    com.ccservice.b2b2c.atom.car.EhicarStub.AddSelfDriveOrderWithNewUserResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from addSelfDriveOrderWithNewUser operation
           */
            public void receiveErroraddSelfDriveOrderWithNewUser(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveStores method
            * override this method for handling normal response from getSelfDriveStores operation
            */
           public void receiveResultgetSelfDriveStores(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveStoresResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveStores operation
           */
            public void receiveErrorgetSelfDriveStores(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveCarServiceArea method
            * override this method for handling normal response from getSelfDriveCarServiceArea operation
            */
           public void receiveResultgetSelfDriveCarServiceArea(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveCarServiceAreaResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveCarServiceArea operation
           */
            public void receiveErrorgetSelfDriveCarServiceArea(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getLowPriceCarTypeByCity method
            * override this method for handling normal response from getLowPriceCarTypeByCity operation
            */
           public void receiveResultgetLowPriceCarTypeByCity(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetLowPriceCarTypeByCityResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getLowPriceCarTypeByCity operation
           */
            public void receiveErrorgetLowPriceCarTypeByCity(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSelfDriveDistrictByCity method
            * override this method for handling normal response from getSelfDriveDistrictByCity operation
            */
           public void receiveResultgetSelfDriveDistrictByCity(
                    com.ccservice.b2b2c.atom.car.EhicarStub.GetSelfDriveDistrictByCityResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSelfDriveDistrictByCity operation
           */
            public void receiveErrorgetSelfDriveDistrictByCity(java.lang.Exception e) {
            }
                


    }
    