package com.caitu99.service.utils;

import javax.servlet.http.HttpServletRequest;


public class VersionUtil {
	
	/**
	 * 获取APP版本号
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getAppVersion 
	 * @param httpRequest
	 * @return
	 * @date 2016年2月19日 上午11:15:03  
	 * @author xiongbin
	 */
    public static String getAppVersion(HttpServletRequest httpRequest) {
    	return httpRequest.getHeader("version");
    }
    
    /**
     * 转换版本号
     * @Description: (方法职责详细描述,可空)  
     * @Title: getVersionLong 
     * @param version
     * @return
     * @date 2016年2月19日 下午2:47:30  
     * @author xiongbin
     */
    public static Long getVersionLong(String version){
		String[] versionArray = version.split("\\.");
		StringBuffer versionStr = new StringBuffer("");
		boolean isFirst = true;
		for (String ver : versionArray) {
			if(isFirst){
				versionStr.append(ver);
				isFirst = false;
				continue;
			}
			
			if(ver.length()==1){
				versionStr.append("00").append(ver);
			}else if(ver.length()==2){
				versionStr.append("0").append(ver);
			}else{
				versionStr.append(ver);
			}
		}
		
		Long versionLong = Long.parseLong(versionStr.toString());
		return versionLong;
	}
} 