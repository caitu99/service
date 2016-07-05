/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.dto;

import com.caitu99.service.transaction.domain.OrderItem;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: OrderItemDto 
 * @author fangjunxiao
 * @date 2015年11月26日 下午12:38:10 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class OrderItemDto extends OrderItem{
	
	private String imgUrl;
	
	
	

	/**
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}

	/**
	 * @param imgUrl the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	

}
