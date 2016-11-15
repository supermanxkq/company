package org.tempuri;

public class ServiceSoapProxy implements org.tempuri.ServiceSoap {
  private String _endpoint = null;
  private org.tempuri.ServiceSoap serviceSoap = null;
  
  public ServiceSoapProxy() {
    _initServiceSoapProxy();
  }
  
  public ServiceSoapProxy(String endpoint) {
    _endpoint = endpoint;
    _initServiceSoapProxy();
  }
  
  private void _initServiceSoapProxy() {
    try {
      serviceSoap = (new org.tempuri.ServiceLocator()).getServiceSoap();
      if (serviceSoap != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)serviceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)serviceSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (serviceSoap != null)
      ((javax.xml.rpc.Stub)serviceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public org.tempuri.ServiceSoap getServiceSoap() {
    if (serviceSoap == null)
      _initServiceSoapProxy();
    return serviceSoap;
  }
  
  public java.lang.String test() throws java.rmi.RemoteException{
    if (serviceSoap == null)
      _initServiceSoapProxy();
    return serviceSoap.test();
  }
  
  public java.lang.String login(java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException{
    if (serviceSoap == null)
      _initServiceSoapProxy();
    return serviceSoap.login(userName, password);
  }
  
  public java.lang.String loginEx(java.lang.String username, java.lang.String password, java.lang.String passport, java.lang.String usertype) throws java.rmi.RemoteException{
    if (serviceSoap == null)
      _initServiceSoapProxy();
    return serviceSoap.loginEx(username, password, passport, usertype);
  }
  
  public org.tempuri.Operator getLoginResult(java.lang.String passportID) throws java.rmi.RemoteException{
    if (serviceSoap == null)
      _initServiceSoapProxy();
    return serviceSoap.getLoginResult(passportID);
  }
  
  public void logOffX(java.lang.String passportID) throws java.rmi.RemoteException{
    if (serviceSoap == null)
      _initServiceSoapProxy();
    serviceSoap.logOffX(passportID);
  }
  
  public java.lang.String changePwd(java.lang.String userName, java.lang.String password, java.lang.String newPassword) throws java.rmi.RemoteException{
    if (serviceSoap == null)
      _initServiceSoapProxy();
    return serviceSoap.changePwd(userName, password, newPassword);
  }
  
  public java.lang.String xmlSubmit(java.lang.String identity, java.lang.String request, java.lang.String filter) throws java.rmi.RemoteException{
    if (serviceSoap == null)
      _initServiceSoapProxy();
    return serviceSoap.xmlSubmit(identity, request, filter);
  }
  
  
}