package com.caitu99.service.file.domain;

import java.util.Date;

public class AttachFileOperation {
	
    private Long id;

    private String path;

    private Integer status;

    private Date dateDelTime;

    private Date fileDelTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path == null ? null : path.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getDateDelTime() {
        return dateDelTime;
    }

    public void setDateDelTime(Date dateDelTime) {
        this.dateDelTime = dateDelTime;
    }

    public Date getFileDelTime() {
        return fileDelTime;
    }

    public void setFileDelTime(Date fileDelTime) {
        this.fileDelTime = fileDelTime;
    }
}