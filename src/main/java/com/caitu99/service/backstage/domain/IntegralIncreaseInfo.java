package com.caitu99.service.backstage.domain;

import java.util.Date;

/**
 * Created by chenhl on 2016/2/17.
 *
 * 财币入分记录
 */
public class IntegralIncreaseInfo {

    private Long ID;

    private Long UID;

    private Long integral; //入分数量

    private String type; //  积分/充值/送分

    private String ratio; // 兑换比例

    private Date time;

    private Long total; //总财币

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Long getUID() {
        return UID;
    }

    public void setUID(Long UID) {
        this.UID = UID;
    }

    public Long getIntegral() {
        return integral;
    }

    public void setIntegral(Long integral) {
        this.integral = integral;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRatio() {
        return ratio;
    }

    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
