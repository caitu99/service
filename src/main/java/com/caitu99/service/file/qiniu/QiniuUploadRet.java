package com.caitu99.service.file.qiniu;

public class QiniuUploadRet {

	private Long fsize;
	private String key;
	private String hash;
	private Integer width;
	private Integer height;
	public Long getFsize() {
		return fsize;
	}
	public void setFsize(Long fsize) {
		this.fsize = fsize;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getHash() {
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public Integer getWidth() {
		return width;
	}
	public void setWidth(Integer width) {
		this.width = width;
	}
	public Integer getHeight() {
		return height;
	}
	public void setHeight(Integer height) {
		this.height = height;
	}
    
    
}
