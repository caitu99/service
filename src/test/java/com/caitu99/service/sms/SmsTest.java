/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.sms;

import java.rmi.RemoteException;

import org.junit.Test;

import com.caitu99.service.AbstractJunit;
import com.caitu99.service.sys.sms.Client;
import com.caitu99.service.sys.sms.SMSSend;
import com.caitu99.service.sys.sms.SingletonClient;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: SmsTest 
 * @author Hongbo Peng
 * @date 2015年12月16日 下午2:29:01 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class SmsTest extends AbstractJunit {

	@Test
	public void test(){
		SMSSend.sendSMS(new String[]{"13325853121"}, "短信验证码为：1234,此验证码只为本人使用切勿告知他人，请在页面中输入以完成验证","3");
	}
	
//	@Test
	public void getBalance(){
		try {
			Client client = SingletonClient.getClient();
			double flag = client.getBalance();
			double flag2 = client.getEachFee();
			System.out.println("-----------------"+flag);
			System.out.println("-----------------"+flag2);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
