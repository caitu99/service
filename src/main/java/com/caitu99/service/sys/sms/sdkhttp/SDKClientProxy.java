package com.caitu99.service.sys.sms.sdkhttp;

import java.rmi.RemoteException;

public class SDKClientProxy implements SDKClient {
  private String _endpoint = null;
  private SDKClient sDKClient = null;
  
  public SDKClientProxy() {
    _initSDKClientProxy();
  }
  
  public SDKClientProxy(String endpoint) {
    _endpoint = endpoint;
    _initSDKClientProxy();
  }
  
  private void _initSDKClientProxy() {
    try {
      sDKClient = (new SDKServiceLocator()).getSDKService();
      if (sDKClient != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)sDKClient)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)sDKClient)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (sDKClient != null)
      ((javax.xml.rpc.Stub)sDKClient)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public SDKClient getSDKClient() {
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient;
  }
  
  public String getVersion() throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.getVersion();
  }

  public StatusReport[] getReport(String arg0, String arg1) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.getReport(arg0, arg1);
  }

  public int cancelMOForward(String arg0, String arg1) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.cancelMOForward(arg0, arg1);
  }

  public int chargeUp(String arg0, String arg1, String arg2, String arg3) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.chargeUp(arg0, arg1, arg2, arg3);
  }

  public double getBalance(String arg0, String arg1) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.getBalance(arg0, arg1);
  }

  public double getEachFee(String arg0, String arg1) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.getEachFee(arg0, arg1);
  }

  public Mo[] getMO(String arg0, String arg1) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.getMO(arg0, arg1);
  }

  public int logout(String arg0, String arg1) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.logout(arg0, arg1);
  }

  public int registDetailInfo(String arg0, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6, String arg7, String arg8, String arg9) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.registDetailInfo(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9);
  }

  public int registEx(String arg0, String arg1, String arg2) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.registEx(arg0, arg1, arg2);
  }

  public int sendSMS(String arg0, String arg1, String arg2, String[] arg3, String arg4, String arg5, String arg6, int arg7, long arg8) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.sendSMS(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
  }

  public String sendVoice(String arg0, String arg1, String arg2, String[] arg3, String arg4, String arg5, String arg6, int arg7, long arg8) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.sendVoice(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
  }

  public int serialPwdUpd(String arg0, String arg1, String arg2, String arg3) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.serialPwdUpd(arg0, arg1, arg2, arg3);
  }

  public int setMOForward(String arg0, String arg1, String arg2) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.setMOForward(arg0, arg1, arg2);
  }

  public int setMOForwardEx(String arg0, String arg1, String[] arg2) throws RemoteException{
    if (sDKClient == null)
      _initSDKClientProxy();
    return sDKClient.setMOForwardEx(arg0, arg1, arg2);
  }
  
  
}