/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.goods.dto;

import java.util.List;

import com.caitu99.service.goods.domain.Group;
import com.caitu99.service.goods.domain.Template;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: TemplateDto 
 * @author fangjunxiao
 * @date 2016年1月6日 下午6:27:32 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class TemplateDto extends Template{

	private List<Group> goupList;
	
	private String versionString;
	
	
	public String getVersionString() {
		return versionString;
	}

	public void setVersionString(String versionString) {
		this.versionString = versionString;
		super.setVersion(getVersionLong(versionString));
	}

	public List<Group> getGoupList() {
		return goupList;
	}

	public void setGoupList(List<Group> goupList) {
		this.goupList = goupList;
	}
	
	private Long getVersionLong(String version){
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
