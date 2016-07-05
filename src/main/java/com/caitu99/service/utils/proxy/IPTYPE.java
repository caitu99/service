package com.caitu99.service.utils.proxy;

public enum IPTYPE {
	MAILQQ("QQ", 5),
	MAIL163("163", 5),
	MAIL126("126", 5),
	MAIL139("139", 5);
	private String name;
	private Integer hits;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getHits() {
		return hits;
	}
	public void setHits(Integer hits) {
		this.hits = hits;
	}
	private IPTYPE(String name, Integer hits) {
		this.name = name;
		this.hits = hits;
	}
	
	
}
