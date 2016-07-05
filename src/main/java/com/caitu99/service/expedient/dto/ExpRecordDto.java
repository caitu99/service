/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.expedient.dto;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ExpRecordDto 
 * @author fangjunxiao
 * @date 2016年6月1日 下午3:25:42 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ExpRecordDto {

	
	private Long sumExp;
	

	private Integer countId;
	
	public Long getSumExp() {
		if(null == sumExp){
			return 0L;
		}else{
			return sumExp;
		}
	}

	public void setSumExp(Long sumExp) {
			this.sumExp = sumExp;
	}

	public Integer getCountId() {
		return countId;
	}

	public void setCountId(Integer countId) {
		this.countId = countId;
	}


	
	
}
