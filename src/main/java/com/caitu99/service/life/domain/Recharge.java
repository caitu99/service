package com.caitu99.service.life.domain;

import java.util.Date;
/**
 * 财币充值记录表
 * @Description: (类职责详细描述,可空) 
 * @ClassName: Recharge 
 * @author lawrence
 * @date 2015年11月10日 下午3:07:21 
 * @Copyright (c) 2015-2020 by caitu99
 */
public class Recharge {
	
	/** 购买 */
	public static final Integer RECHARGE_TYPE_BUY = 1;
	public static final Integer RECHARGE_TYPE_EXCHANGE = 2;
	public static final Integer RECHARGE_TYPE_GIFT = 3;
	
	public static final Integer GIFT_WAY_NOT = 0;
	public static final Integer GIFT_WAY_IMMEDIATELY = 1;
	
	public static final Integer GIFT_TYPE_NOT = 0;
	public static final Integer GIFT_TYPE_IMPORT_MAIL = 1;
	public static final Integer GIFT_TYPE_MANUAL_QUERY = 2;
	
	//主键，自动增长
    private Long id;
	//用户ID    
    private Long userId;
    //充值类型(1.购买 2 兑换 3 营销赠送)
    private Integer rechargeType;
    //赠送方式（0 非赠送模型 1.及时到帐）
    private Integer giftWay;
    //赠送类型（0.非赠送模型 1.首次邮箱导入 2 首次手动查询）
    private Integer giftType;
    //充值财币/赠送财币（单位：分）
    private Long totalAmount;
    //充值时间/兑换时间/赠送时间
    private Date rechargeTime;
    //创建时间
    private Date createTime;
    //更新时间
    private Date updateTime;
    //状态
    private Integer status;

    /**
     * 默认构造
     * @author lawrence
     */
    public Recharge(){
    	
    }
    
    /**
     * 构造赠送模型	
     * @Description: (方法职责详细描述,可空)  
     * @Title: setGift 
     * @param userId
     * @date 2015年11月10日 下午3:36:06  
     * @author lawrence
     */
    public Recharge(Long userId,Integer giftType,Long totalAmount,Date date,Integer status){
    	if(null == date) date = new Date();
    	this.setUserId(userId);
    	this.setGiftType(giftType);
    	this.setTotalAmount(totalAmount);
    	this.setGiftWay(GIFT_WAY_IMMEDIATELY);
    	this.setRechargeType(RECHARGE_TYPE_GIFT);
    	this.setRechargeTime(date);
    	this.setCreateTime(date);
    	this.setUpdateTime(date);
        this.setStatus(status);
    }
    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRechargeType() {
        return rechargeType;
    }

    public void setRechargeType(Integer rechargeType) {
        this.rechargeType = rechargeType;
    }

    public Integer getGiftWay() {
        return giftWay;
    }

    public void setGiftWay(Integer giftWay) {
        this.giftWay = giftWay;
    }

    public Integer getGiftType() {
        return giftType;
    }

    public void setGiftType(Integer giftType) {
        this.giftType = giftType;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getRechargeTime() {
        return rechargeTime;
    }

    public void setRechargeTime(Date rechargeTime) {
        this.rechargeTime = rechargeTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }


}