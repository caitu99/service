package com.caitu99.service.activities.domain;

import java.util.Date;

public class ActivitiesStatistics {
    private Long id;

    private String activitiesCode;

    private String pointCode;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivitiesCode() {
        return activitiesCode;
    }

    public void setActivitiesCode(String activitiesCode) {
        this.activitiesCode = activitiesCode == null ? null : activitiesCode.trim();
    }

    public String getPointCode() {
        return pointCode;
    }

    public void setPointCode(String pointCode) {
        this.pointCode = pointCode == null ? null : pointCode.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}