package com.caitu99.service.sys.sms;


import com.caitu99.service.AppConfig;
import com.caitu99.service.utils.SpringContext;

import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;


public class SingletonClient {

    private static AppConfig appConfig = SpringContext.getBean(AppConfig.class);

	private static Client client=null;
	private SingletonClient(){
	}
	public synchronized static Client getClient(String softwareSerialNo,String key){
		if(client==null){
			try {
				client=new Client(softwareSerialNo,key);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}
	public synchronized static Client getClient(){
		if(client==null){
			try {
				client=new Client(appConfig.smsSoftwareSerialNo, appConfig.smsKey);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return client;
	}
	
	
}
