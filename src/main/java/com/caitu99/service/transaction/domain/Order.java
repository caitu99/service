package com.caitu99.service.transaction.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Order {
	
	/** 1.财币 */
	public final static Integer PAY_TYPE_CF = 1;
	/** 2.混合 */
	public final static Integer PAY_TYPE_MIX = 2;
	/**3.积分**/
	public final static Integer PAY_TYPE_F = 3;

	// 2.支付取消，-1.支付失败，0.待支付，1.支付中，2支付成功
	/** 0.待支付 */
	public final static Integer PAY_STATUS_INIT = 0;
	/** 1.支付中 */
	public final static Integer PAY_STATUS_ING = 1;
	/** 2支付成功 */
	public final static Integer PAY_STATUS_FINISH = 2;
	/** -1.支付失败 */
	public final static Integer PAY_STATUS_FAIL = -1;
	/** 2.取消支付 */
	public final static Integer PAY_STATUS_CANCEL = -2;

	// 0.生成/待付款，1.已付款，10，完成交易 20.超时关闭，30.删除
	/** 0.待付款 */
	public final static Integer STATUS_INIT = 0;
	/** 1.已付款 */
	public final static Integer STATUS_PAY = 1;
	/** 2.处理中 */
	public final static Integer STATUS_ING = 2;
	/** 10，完成交易 */
	public final static Integer STATUS_FINISH = 10;
	/** 20.超时关闭 */
	public final static Integer STATUS_TIMEOUT = 20;
	/** 30.删除 */
	public final static Integer STATUS_DELETE = 30;
	

	private String orderNo;

	private Long userId;

	private String name;
	//状态：0.生成/待付款，1.已付款，10，完成交易   20.超时关闭，30.删除
	//变现状态：50.生成 51.未填写券码信息 52.未到账 53.已到账 60.取消资格（券被使用）
	//新手任务状态: 40.支付中,41.完成,42.失败
	//实名认证状态: 70.待支付, 71.支付中,72.完成,73.失败,74.已退款
	private Integer status;
	//-2.支付取消，-1.支付失败，0.待支付，1.支付中，2支付成功
	private Integer payStatus;
	//@付款方式   1：财币2：人民币3：积分 4：财币+人民币  5：财币+途币+人民币
	private Integer payType;
	
	//订单类型  1.商城订单  2.其它商家引流商品，3 移动商城，4.联通商城
	//订单类型   50.变现订单 51.变现话费充值52.里程充值 53.新手任务 54.实名认证
	//订单类型   60 话费充值
	private Integer type;

	private Long price;

	private Long payPrice;

	private Date orderTime;

	private Date payTime;

	private Date timeoutTime;

	private Date deleteTime;

	private Date createTime;

	private Date updateTime;

	private Integer goodsCount;
	//@外来商品总价
	private String outPrice;
	//@外来订单号
	private String outNo;
	//自由交易编号
	private Long freeTradeId;
	//是否展示：1.展示，2不展示
	private Integer display;
	//描述
	private String memo;

	private Long rmb;
	private Long caibi;
	private Long tubi;

	public Long getRmb() {
		return rmb;
	}

	public void setRmb(Long rmb) {
		this.rmb = rmb;
	}

	public Long getCaibi() {
		return caibi;
	}

	public void setCaibi(Long caibi) {
		this.caibi = caibi;
	}

	public Long getTubi() {
		return tubi;
	}

	public void setTubi(Long tubi) {
		this.tubi = tubi;
	}

	public Integer getDisplay() {
		return display;
	}

	public void setDisplay(Integer display) {
		this.display = display;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOutPrice() {
		return outPrice;
	}

	public void setOutPrice(String outPrice) {
		this.outPrice = outPrice;
	}


	public String getOutNo() {
		return outNo;
	}

	public void setOutNo(String outNo) {
		this.outNo = outNo;
	}

	/**
	 * @return the goodsCount
	 */
	public Integer getGoodsCount() {
		return goodsCount;
	}

	/**
	 * @param goodsCount
	 *            the goodsCount to set
	 */
	public void setGoodsCount(Integer goodsCount) {
		this.goodsCount = goodsCount;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo == null ? null : orderNo.trim();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	/**
	 * 状态：0.生成/待付款，1.已付款，10，完成交易   20.超时关闭，30.删除
	 * 变现状态：50.生成 51.未填写券码信息 52.未到账 53.已到账 60.取消资格（券被使用）
	 * 	
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * 状态：0.生成/待付款，1.已付款，10，完成交易   20.超时关闭，30.删除
	 * 变现状态：50.生成 51.未填写券码信息 52.未到账 53.已到账 60.取消资格（券被使用）
	 * 	
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * -2.支付取消，-1.支付失败，0.待支付，1.支付中，2支付成功
	 * 	
	 */
	public Integer getPayStatus() {
		return payStatus;
	}

	/**
	 * -2.支付取消，-1.支付失败，0.待支付，1.支付中，2支付成功
	 * 	
	 */
	public void setPayStatus(Integer payStatus) {
		this.payStatus = payStatus;
	}

	/**
	 * 付款方式 （ 1：财分2：人民币3：积分 4：财分+人民币）
	 * 	
	 */
	public Integer getPayType() {
		return payType;
	}

	/**
	 * 付款方式 （ 1：财分2：人民币3：积分 4：财分+人民币）
	 * 	
	 */
	public void setPayType(Integer payType) {
		this.payType = payType;
	}

	public Long getPrice() {
		return price;
	}

	public void setPrice(Long price) {
		this.price = price;
	}

	public Long getPayPrice() {
		return payPrice;
	}

	public void setPayPrice(Long payPrice) {
		this.payPrice = payPrice;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Date getPayTime() {
		return payTime;
	}

	public void setPayTime(Date payTime) {
		this.payTime = payTime;
	}

	public Date getTimeoutTime() {
		return timeoutTime;
	}

	public void setTimeoutTime(Date timeoutTime) {
		this.timeoutTime = timeoutTime;
	}

	public Date getDeleteTime() {
		return deleteTime;
	}

	public void setDeleteTime(Date deleteTime) {
		this.deleteTime = deleteTime;
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

	/**
	 * 订单号 （22位 =1位识别码+12位时间戳+4位用户ID+5位随机数）
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: getOderNo
	 * @param userId
	 * @return
	 * @date 2015年11月25日 上午11:49:01
	 * @author fangjunxiao
	 */
	public static String getOderNo(Long userId) {
		StringBuffer sb = new StringBuffer();
		String currentDate = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		currentDate = currentDate.substring(2, 14);
		sb.append("F").append(currentDate);
		String us = userId.toString();
		if (4 >= us.length()) {
			for (int i = 0; i < 4 - us.length(); i++) {
				sb.append("0");
			}
			sb.append(us);
		} else {
			us = us.substring(0, 4);
			sb.append(us);
		}
		int t = new Random().nextInt(99999);
		if (t < 10000)
			t += 10000;
		sb.append(t);
		return sb.toString();
	}
	
	
	public static String getOrderNoBySing(Long userId,String sign){
		StringBuffer sb = new StringBuffer();
		String currentDate = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(new Date());
		currentDate = currentDate.substring(2, 14);
		sb.append(sign).append(currentDate);
		String us = userId.toString();
		if (4 >= us.length()) {
			for (int i = 0; i < 4 - us.length(); i++) {
				sb.append("0");
			}
			sb.append(us);
		} else {
			us = us.substring(0, 4);
			sb.append(us);
		}
		int t = new Random().nextInt(99999);
		if (t < 10000)
			t += 10000;
		sb.append(t);
		return sb.toString();
	}
	
	/**
	 * @return the freeTradeId
	 */
	public Long getFreeTradeId() {
		return freeTradeId;
	}

	/**
	 * @param freeTradeId the freeTradeId to set
	 */
	public void setFreeTradeId(Long freeTradeId) {
		this.freeTradeId = freeTradeId;
	}
	
	
	public static String getOrderNoFlag(Integer source){
		switch (source) {
		case 3:
			return "C";
		case 4:
			return "U";
		default:
			return "B";
		}
	}

}
