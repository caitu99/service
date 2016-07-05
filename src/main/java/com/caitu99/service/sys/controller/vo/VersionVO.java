package com.caitu99.service.sys.controller.vo;


import com.caitu99.service.sys.domain.Version;

public class VersionVO {

	private Long result;
	private Version version;

	public Long getResult() {
		return result;
	}

	public void setResult(Long result) {
		this.result = result;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

}