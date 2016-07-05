package com.caitu99.service.goods.domain;

import com.alibaba.fastjson.annotation.JSONField;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.util.Date;

/**
 * Created by Lion on 2015/11/26 0026.
 */
public class CouponReceiveStock {
    private Long id;
    private Long coupon_marketPrice;   //市场价

    private String coupon_name;  //兑换券名称

    private String coupon_code;      //兑换码

    private String coupon_password;      //密码
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date coupon_effectiveTime;       //有效期
    @JSONField(format="yyyy-MM-dd HH:mm:ss")
    private Date coupon_receive_time;

    private String coupon_wap_url;
    
    /**
     * 礼券类型(0:普通礼券;1:积分变现分享红包)
     */
    private Integer couponType;
    
    /**
     * 礼券状态(只适应积分变现分享红包;1:正常;-1:删除;2:已使用;3:已到账)
     */
    private Integer status;
    /**
     * 礼券类型为1时：1.财币券，2.途币券
     */
    private Integer type;

    public Integer getCouponType() {
		return couponType;
	}

	public void setCouponType(Integer couponType) {
		this.couponType = couponType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getCoupon_wap_url() {
        return coupon_wap_url;
    }

    public void setCoupon_wap_url(String coupon_wap_url) {
        this.coupon_wap_url = coupon_wap_url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCoupon_marketPrice() {
        return coupon_marketPrice;
    }

    public void setCoupon_marketPrice(Long coupon_marketPrice) {
        this.coupon_marketPrice = coupon_marketPrice;
    }

    public String getCoupon_name() {
        return coupon_name;
    }

    public void setCoupon_name(String coupon_name) {
        this.coupon_name = coupon_name;
    }

    public String getCoupon_code() {
        return coupon_code;
    }

    public void setCoupon_code(String coupon_code) {
        this.coupon_code = coupon_code;
    }

    public String getCoupon_password() {
        return coupon_password;
    }

    public void setCoupon_password(String coupon_password) {
        this.coupon_password = coupon_password;
    }

    public Date getCoupon_effectiveTime() {
        return coupon_effectiveTime;
    }

    public void setCoupon_effectiveTime(Date coupon_effectiveTime) {
        this.coupon_effectiveTime = coupon_effectiveTime;
    }

    public Date getCoupon_receive_time() {
        return coupon_receive_time;
    }

    public void setCoupon_receive_time(Date coupon_receive_time) {
        this.coupon_receive_time = coupon_receive_time;
    }

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}
}
