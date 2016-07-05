package com.caitu99.service.life.domain;

import java.util.Date;

/**
 * 激活码
 * Modified by Lion
 */
public class Activation {
	
	/**
	 * 1.正常
	 */
	public static final Integer ACTIVATION_STATE_NOMAL = 1;
	
    private Long id;

    //激活码
    private String activation;

    //有效期
    private Date valid;

    //产品激活码类型
    private Integer type;

    //激活码状态  1.正常 2.已使用
    private Integer status;

    //激活码价格
    private Integer price;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActivation() {
        return activation;
    }

    public void setActivation(String activation) {
        this.activation = activation == null ? null : activation.trim();
    }

    public Date getValid() {
        return valid;
    }

    public void setValid(Date valid) {
        this.valid = valid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}