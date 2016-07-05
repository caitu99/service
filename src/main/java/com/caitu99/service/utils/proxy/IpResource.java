package com.caitu99.service.utils.proxy;

import java.util.HashMap;
import java.util.Map;

public class IpResource {
	private long id;
	private String regon;
	private String ip;
	private String eipId;
	private Map<IPTYPE, Integer> cnt;
	public IpResource(long id, String regon, String ip, String eipId) {
		this.id = id;
		this.ip = ip;
		this.eipId = eipId;
		this.regon = regon;
		cnt = new HashMap<>();
		IPTYPE[] types = IPTYPE.values();
		for(IPTYPE type : types) {
			cnt.put(type, 0);
		}
	}
	public String getIp() {
		return ip;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Map<IPTYPE, Integer> getCnt() {
		return cnt;
	}
	public void setCnt(Map<IPTYPE, Integer> cnt) {
		this.cnt = cnt;
	}
	public String getEipId() {
		return eipId;
	}
	public void setEipId(String eipId) {
		this.eipId = eipId;
	}
	public String getRegon() {
		return regon;
	}
	public void setRegon(String regon) {
		this.regon = regon;
	}
	
}
