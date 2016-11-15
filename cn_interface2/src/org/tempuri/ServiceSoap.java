/**
 * ServiceSoap.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.tempuri;

public interface ServiceSoap extends java.rmi.Remote {
    public java.lang.String test() throws java.rmi.RemoteException;

    /**
     * 登录企业航旅通系统
     */
    public java.lang.String login(java.lang.String userName, java.lang.String password) throws java.rmi.RemoteException;

    /**
     * 登录企业航旅通系统
     */
    public java.lang.String loginEx(java.lang.String username, java.lang.String password, java.lang.String passport, java.lang.String usertype) throws java.rmi.RemoteException;

    /**
     * 获取当前的登录结果
     */
    public org.tempuri.Operator getLoginResult(java.lang.String passportID) throws java.rmi.RemoteException;

    /**
     * 断开登录
     */
    public void logOffX(java.lang.String passportID) throws java.rmi.RemoteException;

    /**
     * 修改企业航旅通登录密码
     */
    public java.lang.String changePwd(java.lang.String userName, java.lang.String password, java.lang.String newPassword) throws java.rmi.RemoteException;
    public java.lang.String xmlSubmit(java.lang.String identity, java.lang.String request, java.lang.String filter) throws java.rmi.RemoteException;
}
