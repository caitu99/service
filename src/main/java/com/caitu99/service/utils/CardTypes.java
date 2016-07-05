package com.caitu99.service.utils;

public enum CardTypes {
	UNKNOWN(-1, "unknown"),

	CAIFEN(0, "caifen"),

	CREDITCARD(1, "creditCard"),

	SHOPCARD(2, "shopCard"),

	BUSINESS(3, "business");

	private int value;

	private String code;

	private CardTypes(int value, String code) {
		this.value = value;
		this.code = code;
	}

	public int getValue() {
		return this.value;
	}

	public String getCode() {
		return this.code;
	}

	public static CardTypes valueFrom(String code) {
		for (CardTypes status : CardTypes.values()) {
			if (status.getCode().equals(code)) {
				return status;
			}
		}
		return CardTypes.UNKNOWN;
	}

}
