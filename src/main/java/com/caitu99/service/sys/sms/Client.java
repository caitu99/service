package com.caitu99.service.sys.sms;


import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import com.caitu99.service.sys.sms.sdkhttp.Mo;
import com.caitu99.service.sys.sms.sdkhttp.SDKServiceBindingStub;
import com.caitu99.service.sys.sms.sdkhttp.SDKServiceLocator;
import com.caitu99.service.sys.sms.sdkhttp.StatusReport;

public class Client {
	private String softwareSerialNo;
	private String key;
	
	public Client(String sn,String key){
		this.softwareSerialNo=sn;
		this.key=key;
		init();
	}
	
	SDKServiceBindingStub binding;
	
	
	public void init(){
		 try {
            binding = (SDKServiceBindingStub)
                          new SDKServiceLocator().getSDKService();
		 }
        catch (javax.xml.rpc.ServiceException jre) {
            if(jre.getLinkedCause()!=null)
                jre.getLinkedCause().printStackTrace();
        }
	}
	
	public int chargeUp(  String cardNo,String cardPass)
			throws RemoteException {
		int value=-1;
		value=binding.chargeUp(softwareSerialNo, key, cardNo, cardPass);
		return value;
	}

	public double getBalance() throws RemoteException {
		double value=0.0;
		value=binding.getBalance(softwareSerialNo, key);
		return value;
	}

	public double getEachFee( ) throws RemoteException {
		double value=0.0;
		value=binding.getEachFee(softwareSerialNo, key);
		return value;
	}
	public List<Mo> getMO( ) throws RemoteException {
		Mo[] mo=binding.getMO(softwareSerialNo, key);
		
		if(null == mo){
			return null;
		}else{
			List<Mo> molist=Arrays.asList(mo);
		    return molist;
		}
	}
	

	public List<StatusReport> getReport( )
			throws RemoteException {
		StatusReport[] sr=binding.getReport(softwareSerialNo, key);
		if(null!=sr){
			return Arrays.asList(sr);
		}else{
			return null;
		}
	}


	public int logout( ) throws RemoteException {
		int value=-1;
		value=binding.logout(softwareSerialNo, key);
		return value;
	}

	public int registDetailInfo(
			String eName, String linkMan, String phoneNum, String mobile,
			String email, String fax, String address, String postcode
) throws RemoteException {
		int value=-1;
		value=binding.registDetailInfo(softwareSerialNo, key, eName, linkMan, phoneNum, mobile, email, fax, address, postcode);
		return value;
	}

	public int registEx(String password)
			throws RemoteException {
		int value=-1;
		value=binding.registEx(softwareSerialNo, key, password);
		return value;
	}

	public int sendSMS( String[] mobiles, String smsContent, String addSerial,int smsPriority)
			throws RemoteException {
		int value=-1;
		value=binding.sendSMS(softwareSerialNo, key,"", mobiles, smsContent, addSerial, "gbk", smsPriority,0);
		return value;
	}
	public int sendScheduledSMSEx(String[] mobiles, String smsContent,String sendTime,String srcCharset)
	throws RemoteException {
      int value=-1;
      value=binding.sendSMS(softwareSerialNo, key, sendTime, mobiles, smsContent, "", srcCharset, 3,0);
      return value;
	}
	public int sendSMSEx(String[] mobiles, String smsContent, String addSerial,String srcCharset, int smsPriority,long smsID)
	throws RemoteException {
      int value=-1;
      value=binding.sendSMS(softwareSerialNo, key,"", mobiles, smsContent,addSerial, srcCharset, smsPriority,smsID);
      return value;
	}

	public String sendVoice(String[] mobiles, String smsContent, String addSerial,String srcCharset, int smsPriority,long smsID)
			throws RemoteException {
		     String value=null;
		      value=binding.sendVoice(softwareSerialNo, key,"", mobiles, smsContent,addSerial, srcCharset, smsPriority,smsID);
		      return value;
	}
	
	public int serialPwdUpd( String serialPwd, String serialPwdNew)
			throws RemoteException {
		int value=-1;
		value=binding.serialPwdUpd(softwareSerialNo, key, serialPwd, serialPwdNew);
		return value;
	}
}
