
/**
 * FreeHotelSearchWebServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.5.2  Built on : Sep 06, 2010 (09:42:01 CEST)
 */

    package de.hotel.webservices.v2_8;

    /**
     *  FreeHotelSearchWebServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class FreeHotelSearchWebServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public FreeHotelSearchWebServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public FreeHotelSearchWebServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for determineLocationNumber method
            * override this method for handling normal response from determineLocationNumber operation
            */
           public void receiveResultdetermineLocationNumber(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.DetermineLocationNumberResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from determineLocationNumber operation
           */
            public void receiveErrordetermineLocationNumber(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getWebservicesVersion method
            * override this method for handling normal response from getWebservicesVersion operation
            */
           public void receiveResultgetWebservicesVersion(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetWebservicesVersionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getWebservicesVersion operation
           */
            public void receiveErrorgetWebservicesVersion(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPropertyReviews method
            * override this method for handling normal response from getPropertyReviews operation
            */
           public void receiveResultgetPropertyReviews(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetPropertyReviewsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPropertyReviews operation
           */
            public void receiveErrorgetPropertyReviews(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for determineGeographicCoordinatesFromAddress method
            * override this method for handling normal response from determineGeographicCoordinatesFromAddress operation
            */
           public void receiveResultdetermineGeographicCoordinatesFromAddress(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.DetermineGeographicCoordinatesFromAddressResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from determineGeographicCoordinatesFromAddress operation
           */
            public void receiveErrordetermineGeographicCoordinatesFromAddress(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getSearchableAmenities method
            * override this method for handling normal response from getSearchableAmenities operation
            */
           public void receiveResultgetSearchableAmenities(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetSearchableAmenitiesResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getSearchableAmenities operation
           */
            public void receiveErrorgetSearchableAmenities(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAvailableHotelsFromLocationNr method
            * override this method for handling normal response from getAvailableHotelsFromLocationNr operation
            */
           public void receiveResultgetAvailableHotelsFromLocationNr(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetAvailableHotelsFromLocationNrResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAvailableHotelsFromLocationNr operation
           */
            public void receiveErrorgetAvailableHotelsFromLocationNr(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getChainList method
            * override this method for handling normal response from getChainList operation
            */
           public void receiveResultgetChainList(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetChainListResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getChainList operation
           */
            public void receiveErrorgetChainList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPropertyDescription method
            * override this method for handling normal response from getPropertyDescription operation
            */
           public void receiveResultgetPropertyDescription(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetPropertyDescriptionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPropertyDescription operation
           */
            public void receiveErrorgetPropertyDescription(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getHotelClassifications method
            * override this method for handling normal response from getHotelClassifications operation
            */
           public void receiveResultgetHotelClassifications(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetHotelClassificationsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getHotelClassifications operation
           */
            public void receiveErrorgetHotelClassifications(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getMultiAvailability method
            * override this method for handling normal response from getMultiAvailability operation
            */
           public void receiveResultgetMultiAvailability(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetMultiAvailabilityResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getMultiAvailability operation
           */
            public void receiveErrorgetMultiAvailability(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAvailableHotelsAroundGeographicCoordinates method
            * override this method for handling normal response from getAvailableHotelsAroundGeographicCoordinates operation
            */
           public void receiveResultgetAvailableHotelsAroundGeographicCoordinates(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetAvailableHotelsAroundGeographicCoordinatesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAvailableHotelsAroundGeographicCoordinates operation
           */
            public void receiveErrorgetAvailableHotelsAroundGeographicCoordinates(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getLocationList method
            * override this method for handling normal response from getLocationList operation
            */
           public void receiveResultgetLocationList(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetLocationListResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getLocationList operation
           */
            public void receiveErrorgetLocationList(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getLocations method
            * override this method for handling normal response from getLocations operation
            */
           public void receiveResultgetLocations(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetLocationsResponseE result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getLocations operation
           */
            public void receiveErrorgetLocations(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAvailableHotelsFromDestination method
            * override this method for handling normal response from getAvailableHotelsFromDestination operation
            */
           public void receiveResultgetAvailableHotelsFromDestination(
                    de.hotel.webservices.v2_8.FreeHotelSearchWebServiceStub.GetAvailableHotelsFromDestinationResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAvailableHotelsFromDestination operation
           */
            public void receiveErrorgetAvailableHotelsFromDestination(java.lang.Exception e) {
            }
                


    }
    