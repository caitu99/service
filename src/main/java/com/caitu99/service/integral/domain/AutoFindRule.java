package com.caitu99.service.integral.domain;

import java.util.Date;

public class AutoFindRule {
	
	/** 状态:正常 */
	public final static Integer STATUS_NORMAL = 1;
	
	/** 类型:安全账户 */
	public final static Integer TYPE_ACCOUNT_SECURITY = 1;
	/** 类型:普通账户 */
	public final static Integer TYPE_ACCOUNT_ORDINARY = 2;
	/** 类型:银行 */
	public final static Integer TYPE_BANK = 3;
	
    private Long id;

    private Integer type;

    private Long manualId;

    private Integer errorCount;

    private Date gmtCreate;

    private Date gmtModify;

    private Integer isPassword;

    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Long getManualId() {
        return manualId;
    }

    public void setManualId(Long manualId) {
        this.manualId = manualId;
    }

    public Integer getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
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

    public Integer getIsPassword() {
        return isPassword;
    }

    public void setIsPassword(Integer isPassword) {
        this.isPassword = isPassword;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}