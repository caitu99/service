package com.caitu99.service.free.domain;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class FreeTradePlatform {
	@JSONField(name="manualId")
    private Long id;
	//名称
    private String name;
    //商户类型(0:银行;1:商旅;2:购物)
    private Integer type;
    //图标
    private String icon;
    //状态(1:正常;-1:删除)
    private Integer status;
    //登录地址(废弃)
    private String url;
    //排序字母
    private String sort;
    //创建时间
    private Date gmtCreate;
    //修改时间
    private Date gmtModify;
    //版本
    private Integer version;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon == null ? null : icon.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort == null ? null : sort.trim();
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(Date gmtModify) {
        this.gmtModify = gmtModify;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}