/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.life.domain;

import java.util.Date;

/** 
 * 兑换记录实体类
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ProductExchangeRecord 
 * @author ws
 * @date 2015年11月5日 下午2:45:42 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class ProductExchangeRecord {
	
	private String product;
	private Long price;
	private String activation;
	private String valid;
	
	/**
	 * 产品名称
	 * @return the product
	 */
	public String getProduct() {
		return product;
	}
	/**
	 * @param product the product to set
	 */
	public void setProduct(String product) {
		this.product = product;
	}
	/**
	 * 价格
	 * @return the price
	 */
	public Long getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(Long price) {
		this.price = price;
	}
	/**
	 * 激活码
	 * @return the activation
	 */
	public String getActivation() {
		return activation;
	}
	/**
	 * @param activation the activation to set
	 */
	public void setActivation(String activation) {
		this.activation = activation;
	}
	/**
	 * 有效日期
	 * @return the valid
	 */
	public String getValid() {
		return valid;
	}
	/**
	 * @param valid the valid to set
	 */
	public void setValid(String valid) {
		this.valid = valid;
	}

	
	
}
