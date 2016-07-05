/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.lottery.dto;

import com.caitu99.service.lottery.domain.LotteryOrder;
import com.caitu99.service.utils.date.DateUtil;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: LotteryPageDto 
 * @author fangjunxiao
 * @date 2016年5月14日 上午11:23:57 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class LotteryPageDto extends LotteryOrder{

	
	private String statusString;
	
	private String createTimeString;
	
	private Long total;
	
	private Long tubi;
	
	

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getTubi() {
		return tubi;
	}

	public void setTubi(Long tubi) {
		this.tubi = tubi;
	}

	public String getStatusString() {
		if(null == statusString){
			switch (super.getStatus()) {
			case 0:
				statusString = "投注中";
				break;
			case 1:
				statusString = "投注成功";
				break;
			case 2:
				statusString = "投注失败";
				break;

			default:
				break;
			}
		}
		return statusString;
	}

	public void setStatusString(String statusString) {
		this.statusString = statusString;
	}

	public String getCreateTimeString() {
		if(null == createTimeString){
			this.createTimeString = DateUtil.DateToString(super.getCreateTime());
		}
		return  createTimeString;
	}

	public void setCreateTimeString(String createTimeString) {
		this.createTimeString = createTimeString;
	}
	
}
