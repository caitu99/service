/**
 * SDKClient.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.caitu99.service.sys.sms.sdkhttp;

import java.rmi.RemoteException;

public interface SDKClient extends java.rmi.Remote {
    public String getVersion() throws RemoteException;
    public StatusReport[] getReport(String arg0, String arg1) throws RemoteException;
    public int cancelMOForward(String arg0, String arg1) throws RemoteException;
    public int chargeUp(String arg0, String arg1, String arg2, String arg3) throws RemoteException;
    public double getBalance(String arg0, String arg1) throws RemoteException;
    public double getEachFee(String arg0, String arg1) throws RemoteException;
    public Mo[] getMO(String arg0, String arg1) throws RemoteException;
    public int logout(String arg0, String arg1) throws RemoteException;
    public int registDetailInfo(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9) throws RemoteException;
    public int registEx(String arg0, String arg1, String arg2) throws RemoteException;
    public int sendSMS(String arg0, String arg1, String arg2, String[] arg3, String arg4, String arg5, String arg6, int arg7, long arg8) throws RemoteException;
    public String sendVoice(String arg0, String arg1, String arg2, String[] arg3, String arg4, String arg5, String arg6, int arg7, long arg8) throws RemoteException;
    public int serialPwdUpd(String arg0, String arg1, String arg2, String arg3) throws RemoteException;
    public int setMOForward(String arg0, String arg1, String arg2) throws RemoteException;
    public int setMOForwardEx(String arg0, String arg1, String[] arg2) throws RemoteException;
}
