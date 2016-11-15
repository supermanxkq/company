/**
 * MGHotelService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.mango.hotel;

public interface MGHotelService extends java.rmi.Remote {
    public com.mango.hotel.TestResponse test(com.mango.hotel.TestRequest testRequest) throws java.rmi.RemoteException;
    public com.mango.hotel.DetailOrderResponse detailOrder(com.mango.hotel.DetailOrderRequest detailOrderRequest) throws java.rmi.RemoteException;
    public com.mango.hotel.SingleHotelResponse singleHotel(com.mango.hotel.SingleHotelRequest singleHotelRequest) throws java.rmi.RemoteException;
    public com.mango.hotel.MutilHotelResponse mutilHotel(com.mango.hotel.MutilHotelRequest mutilHotelRequest) throws java.rmi.RemoteException;
    public com.mango.hotel.CancelRoomOrderResponse cancelRoomOrder(com.mango.hotel.CancelRoomOrderRequest cancelRoomOrderRequest) throws java.rmi.RemoteException;
    public com.mango.hotel.CheckReservationResposne checkReservation(com.mango.hotel.CheckReservationRequest checkReservationRequest) throws java.rmi.RemoteException;
    public com.mango.hotel.AddRoomOrderResponse addRoomOrder(com.mango.hotel.AddRoomOrderRequest addRoomOrderRequest) throws java.rmi.RemoteException;
}
