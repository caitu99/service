package com.caitu99.service.sys.sms.cl;

import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.http.HttpClientUtils;



/**
 * @author Beyond
 */
public class ClClient {
	private String account;
	private String pswd;
	private String url;
	private static ClClient clClient = null;
	
	private ClClient(){
		account = Configuration.getProperty("cl.account", null);
		pswd = Configuration.getProperty("cl.pswd", null);
		url =  Configuration.getProperty("cl.url", null);
	}
	
	public synchronized static ClClient getClClient(){
		if(null == clClient){
			clClient = new ClClient();
		}
		return clClient;
	}

	/**
	 * @Description: (发送短信)  
	 * @Title: send 
	 * @param mobile
	 * @param msg
	 * @return
	 * @throws Exception
	 * @date 2015年12月16日 上午11:26:38  
	 * @author Hongbo Peng
	 */
	public String send(String[] mobiles, String msg) throws Exception {
		StringBuilder mobile = new StringBuilder();
		for (String m : mobiles) {
			mobile.append(m).append(",");
		}
		StringBuilder builder = new StringBuilder(url);
		builder.append("account=").append(account)
				.append("&pswd=").append(pswd)
				.append("&mobile=").append(mobile)
				.append("&msg=").append(msg)
				.append("&needstatus=true");
		String flag = HttpClientUtils.getInstances().doGet(builder.toString(), "utf-8");
		return flag;
	}
}
