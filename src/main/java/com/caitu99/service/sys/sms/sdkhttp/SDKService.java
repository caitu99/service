/**
 * SDKService.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.caitu99.service.sys.sms.sdkhttp;

import javax.xml.rpc.ServiceException;
import java.net.URL;

public interface SDKService extends javax.xml.rpc.Service {
    public String getSDKServiceAddress();

    public SDKClient getSDKService() throws ServiceException;

    public SDKClient getSDKService(URL portAddress) throws ServiceException;
}
