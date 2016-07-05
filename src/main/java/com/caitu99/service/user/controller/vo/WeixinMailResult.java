package com.caitu99.service.user.controller.vo;

public class WeixinMailResult {
	private String code;
	private String imgUrl;
	private WeixinFenPKWithOrderVo weixinFenPKWithOrderVo;
	public WeixinFenPKWithOrderVo getWeixinFenPKWithOrderVo() {
		return weixinFenPKWithOrderVo;
	}
	public void setWeixinFenPKWithOrderVo(WeixinFenPKWithOrderVo weixinFenPKWithOrderVo) {
		this.weixinFenPKWithOrderVo = weixinFenPKWithOrderVo;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
