package com.caitu99.service.goods.domain;

import java.util.Date;

public class GoodProp {
    private Long id;
    //编号
    private String code;
    //商品id
    private Long itemId;
    //属性名称
    private String name;
    //属性值
    private String value;
    //组
    private Integer groupList;
    //0:隐藏属性 1:显示属性
    private Integer useType;
    //排序
    private Integer sort;

    private Date createTime;

    private Date updateTime;

    

	public Integer getUseType() {
		return useType;
	}

	public void setUseType(Integer useType) {
		this.useType = useType;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value == null ? null : value.trim();
    }

    public Integer getGroupList() {
        return groupList;
    }

    public void setGroupList(Integer groupList) {
        this.groupList = groupList;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}