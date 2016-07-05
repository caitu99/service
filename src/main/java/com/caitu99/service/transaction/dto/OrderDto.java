/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.dto;

import java.util.List;

import com.caitu99.service.transaction.domain.Order;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: OrderDto 
 * @author fangjunxiao
 * @date 2015年11月26日 上午10:39:56 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class OrderDto extends Order{

	private List<String> imgUrlList;
	private Long itemPrice;
	private Integer quantity;
	
	private String outTimeString;
	
	private String payTimeString;
	
	private String orderTimeString;
	
	private String typeString;
	
	
	
	
	public String getOrderTimeString() {
		return orderTimeString;
	}
	public void setOrderTimeString(String orderTimeString) {
		this.orderTimeString = orderTimeString;
	}
	public String getTypeString() {
		return typeString;
	}
	public void setTypeString(String typeString) {
		this.typeString = typeString;
	}
	public String getPayTimeString() {
		return payTimeString;
	}
	public void setPayTimeString(String payTimeString) {
		this.payTimeString = payTimeString;
	}
	/**
	 * @return the outTimeString
	 */
	public String getOutTimeString() {
		return outTimeString;
	}
	/**
	 * @param outTimeString the outTimeString to set
	 */
	public void setOutTimeString(String outTimeString) {
		this.outTimeString = outTimeString;
	}
		
	
	/**
	 * @return the imgUrlList
	 */
	public List<String> getImgUrlList() {
		return imgUrlList;
	}
	/**
	 * @param imgUrlList the imgUrlList to set
	 */
	public void setImgUrlList(List<String> imgUrlList) {
		this.imgUrlList = imgUrlList;
	}
	/**
	 * @return the quantity
	 */
	public Integer getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	/**
	 * @return the itemPrice
	 */
	public Long getItemPrice() {
		return itemPrice;
	}
	/**
	 * @param itemPrice the itemPrice to set
	 */
	public void setItemPrice(Long itemPrice) {
		this.itemPrice = itemPrice;
	}
	
	
}
