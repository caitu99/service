package com.caitu99.service.utils.proxy;

import java.util.List;

public class Node {
	private String hostId;
	private String cityCode;
	private List<IpResource> ipResources;
	private boolean needFresh;
	public String getHostId() {
		return hostId;
	}
	public void setHostId(String hostId) {
		this.hostId = hostId;
	}
	public List<IpResource> getIpResources() {
		return ipResources;
	}
	public void setIpResources(List<IpResource> ipResources) {
		this.ipResources = ipResources;
	}
	public String getCityCode() {
		return cityCode;
	}
	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}
	public boolean isNeedFresh() {
		return needFresh;
	}
	public void setNeedFresh(boolean needFresh) {
		this.needFresh = needFresh;
	}
	
}
