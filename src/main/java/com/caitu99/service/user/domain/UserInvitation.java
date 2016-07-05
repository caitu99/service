package com.caitu99.service.user.domain;

import java.util.Date;

public class UserInvitation {

	/**
	 * 状态:上架
	 */
	public final static Integer STATUS_NORMAL = 1;
	/**
	 * 状态:删除
	 */
	public final static Integer STATUS_DELETE = -1;
	
    private Long id;

    private Long invitationUserId;

    private Long invitedUserId;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getInvitationUserId() {
        return invitationUserId;
    }

    public void setInvitationUserId(Long invitationUserId) {
        this.invitationUserId = invitationUserId;
    }

    public Long getInvitedUserId() {
        return invitedUserId;
    }

    public void setInvitedUserId(Long invitedUserId) {
        this.invitedUserId = invitedUserId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}