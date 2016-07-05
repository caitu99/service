package com.caitu99.service.user.domain;

import com.caitu99.service.user.domain.WeixinFenPK;

public class WeixinFenPKVo extends WeixinFenPK {
	private String weixinNickname;
	private String weixinImgurl;
	public String getWeixinNickname() {
		return weixinNickname;
	}
	public void setWeixinNickname(String weixinNickname) {
		this.weixinNickname = weixinNickname;
	}
	public String getWeixinImgurl() {
		return weixinImgurl;
	}
	public void setWeixinImgurl(String weixinImgurl) {
		this.weixinImgurl = weixinImgurl;
	}
}
