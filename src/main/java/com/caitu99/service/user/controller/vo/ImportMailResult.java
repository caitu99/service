package com.caitu99.service.user.controller.vo;

import java.util.List;

public class ImportMailResult {
	private String code;
	private String imgUrl;
	private List<UserCardResult> userCardResult;
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public List<UserCardResult> getUserCardResult() {
		return userCardResult;
	}
	public void setUserCardResult(List<UserCardResult> userCardResult) {
		this.userCardResult = userCardResult;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}
