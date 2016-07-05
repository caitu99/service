package com.caitu99.service.backstage.domain;

import java.util.Date;

/**
 * Created by chenhl on 2016/2/17.
 *
 * 启动记录
 */
public class StartUpInfo {

    private Long ID;

    private Date startUpTime; //启动时间

    private Date shutDownTime; //关闭时间

    private Long UID;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Date getStartUpTime() {
        return startUpTime;
    }

    public void setStartUpTime(Date startUpTime) {
        this.startUpTime = startUpTime;
    }

    public Date getShutDownTime() {
        return shutDownTime;
    }

    public void setShutDownTime(Date shutDownTime) {
        this.shutDownTime = shutDownTime;
    }

    public Long getUID() {
        return UID;
    }

    public void setUID(Long UID) {
        this.UID = UID;
    }
}
