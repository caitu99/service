package com.caitu99.service.user.domain;

public class UserValue implements Comparable<UserValue> {
	private Integer id;

	private Long level;

	private String imgurl;

	private String description;

	public UserValue() {
		super();
	}

	public UserValue(Long level, String imgurl, String description) {
		super();
		this.level = level;
		this.imgurl = imgurl;
		this.description = description;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getLevel() {
		return level;
	}

	public void setLevel(Long level) {
		this.level = level;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl == null ? null : imgurl.trim();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description == null ? null : description.trim();
	}

	@Override
	public int compareTo(UserValue o) {
		Long level = o.getLevel();
		return this.getLevel().compareTo(level);
	}

	@Override
	public String toString() {
		return "UserValue [id=" + id + ", level=" + level + ", imgurl="
				+ imgurl + ", description=" + description + "]";
	}

}