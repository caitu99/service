package com.caitu99.service.lottery.domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class LotteryOrder {
    private String orderNo;

    private Long userId;
    //0:生成  1:出票  2:出票失败  -1:删除 
    private Integer status;

    private String name;

    private String outOrderId;

    private Long points;

    private Long marketPrice;

    private Long realPrice;

    private Date finishTime;

    private Date createTime;

    private Date updateTime;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getOutOrderId() {
        return outOrderId;
    }

    public void setOutOrderId(String outOrderId) {
        this.outOrderId = outOrderId == null ? null : outOrderId.trim();
    }

    public Long getPoints() {
        return points;
    }

    public void setPoints(Long points) {
        this.points = points;
    }

    public Long getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Long marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Long getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(Long realPrice) {
        this.realPrice = realPrice;
    }

    
    public Date getFinishTime() {
		return finishTime;
	}

	public void setFinishTime(Date finishTime) {
		this.finishTime = finishTime;
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
	public static String generateNo(Long userId) {
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
    
}