/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.transaction.dto;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: EnterpriseDto 
 * @author fangjunxiao
 * @date 2016年5月27日 下午4:03:12 
 * @Copyright (c) 2015-2020 by caitu99 
 */
public class EnterpriseDto {
	
	private Long countId;
	
	private Long sumTotal;
	
	//总数据
	private Long countA;
	private Long sumA;
	//成功数据
	private Long countB;
	private Long sumB;
	//失败数据
	private Long countC;
	private Long sumC;
	//处理中数据
	private Long countD;
	private Long sumD;

	public Long getCountId() {
		return countId;
	}

	public void setCountId(Long countId) {
		this.countId = countId;
	}

	public Long getSumTotal() {
		if(null == sumTotal){
			return 0L;
		}else{
			return sumTotal;
		}
	}

	public void setSumTotal(Long sumTotal) {
			this.sumTotal = sumTotal;
	}

	public Long getCountA() {
		return countA;
	}

	public void setCountA(Long countA) {
		this.countA = countA;
	}

	public Long getSumA() {
		return sumA;
	}

	public void setSumA(Long sumA) {
		this.sumA = sumA;
	}

	public Long getCountB() {
		return countB;
	}

	public void setCountB(Long countB) {
		this.countB = countB;
	}

	public Long getSumB() {
		return sumB;
	}

	public void setSumB(Long sumB) {
		this.sumB = sumB;
	}

	public Long getCountC() {
		return countC;
	}

	public void setCountC(Long countC) {
		this.countC = countC;
	}

	public Long getSumC() {
		return sumC;
	}

	public void setSumC(Long sumC) {
		this.sumC = sumC;
	}

	public Long getCountD() {
		return countD;
	}

	public void setCountD(Long countD) {
		this.countD = countD;
	}

	public Long getSumD() {
		return sumD;
	}

	public void setSumD(Long sumD) {
		this.sumD = sumD;
	}
	
	

}
