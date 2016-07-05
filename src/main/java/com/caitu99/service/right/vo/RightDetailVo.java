package com.caitu99.service.right.vo;

import com.caitu99.service.right.domain.MyRights;

public class RightDetailVo extends MyRights{
    private Long id;

    private String name;

    private String detail;

    private String rule;

    private String imgUrl;

    private String scopeUrl;

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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail == null ? null : detail.trim();
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule == null ? null : rule.trim();
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }

    public String getScopeUrl() {
        return scopeUrl;
    }

    public void setScopeUrl(String scopeUrl) {
        this.scopeUrl = scopeUrl == null ? null : scopeUrl.trim();
    }
}