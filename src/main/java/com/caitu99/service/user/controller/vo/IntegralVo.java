package com.caitu99.service.user.controller.vo;

public class IntegralVo {

	private Integer type;// 类型

	private Integer integral;// 分数
	
	private Double money;
	
	private Integer size; //数量
	
	

	public Integer getSize() {
		return size==null?0:size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public Integer getIntegral() {
		return integral;
	}

	public void setIntegral(Integer integral) {
		this.integral = integral;
	}
}
