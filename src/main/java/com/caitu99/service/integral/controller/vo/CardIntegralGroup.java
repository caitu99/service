package com.caitu99.service.integral.controller.vo;

import java.util.List;

public class CardIntegralGroup {
	private List<CardIntegralLastTime> cardIntegral;
	private Integer type;
	public List<CardIntegralLastTime> getCardIntegral() {
		return cardIntegral;
	}
	public void setCardIntegral(List<CardIntegralLastTime> cardIntegral) {
		this.cardIntegral = cardIntegral;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
}
