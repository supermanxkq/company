package com.mango.hotel;

public class MGHotelServiceProxy implements com.mango.hotel.MGHotelService {
  private String _endpoint = null;
  private com.mango.hotel.MGHotelService mGHotelService = null;
  
  public MGHotelServiceProxy() {
    _initMGHotelServiceProxy();
  }
  
  public MGHotelServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initMGHotelServiceProxy();
  }
  
  private void _initMGHotelServiceProxy() {
    try {
      mGHotelService = (new com.mango.hotel.MGHotelWebServiceLocator()).getMGHotelServiceImplPort();
      if (mGHotelService != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)mGHotelService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)mGHotelService)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (mGHotelService != null)
      ((javax.xml.rpc.Stub)mGHotelService)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public com.mango.hotel.MGHotelService getMGHotelService() {
    if (mGHotelService == null)
      _initMGHotelServiceProxy();
    return mGHotelService;
  }
  
  public com.mango.hotel.TestResponse test(com.mango.hotel.TestRequest testRequest) throws java.rmi.RemoteException{
    if (mGHotelService == null)
      _initMGHotelServiceProxy();
    return mGHotelService.test(testRequest);
  }
  
  public com.mango.hotel.DetailOrderResponse detailOrder(com.mango.hotel.DetailOrderRequest detailOrderRequest) throws java.rmi.RemoteException{
    if (mGHotelService == null)
      _initMGHotelServiceProxy();
    return mGHotelService.detailOrder(detailOrderRequest);
  }
  
  public com.mango.hotel.SingleHotelResponse singleHotel(com.mango.hotel.SingleHotelRequest singleHotelRequest) throws java.rmi.RemoteException{
    if (mGHotelService == null)
      _initMGHotelServiceProxy();
    return mGHotelService.singleHotel(singleHotelRequest);
  }
  
  public com.mango.hotel.MutilHotelResponse mutilHotel(com.mango.hotel.MutilHotelRequest mutilHotelRequest) throws java.rmi.RemoteException{
    if (mGHotelService == null)
      _initMGHotelServiceProxy();
    return mGHotelService.mutilHotel(mutilHotelRequest);
  }
  
  public com.mango.hotel.CancelRoomOrderResponse cancelRoomOrder(com.mango.hotel.CancelRoomOrderRequest cancelRoomOrderRequest) throws java.rmi.RemoteException{
    if (mGHotelService == null)
      _initMGHotelServiceProxy();
    return mGHotelService.cancelRoomOrder(cancelRoomOrderRequest);
  }
  
  public com.mango.hotel.CheckReservationResposne checkReservation(com.mango.hotel.CheckReservationRequest checkReservationRequest) throws java.rmi.RemoteException{
    if (mGHotelService == null)
      _initMGHotelServiceProxy();
    return mGHotelService.checkReservation(checkReservationRequest);
  }
  
  public com.mango.hotel.AddRoomOrderResponse addRoomOrder(com.mango.hotel.AddRoomOrderRequest addRoomOrderRequest) throws java.rmi.RemoteException{
    if (mGHotelService == null)
      _initMGHotelServiceProxy();
    return mGHotelService.addRoomOrder(addRoomOrderRequest);
  }
  
  
}