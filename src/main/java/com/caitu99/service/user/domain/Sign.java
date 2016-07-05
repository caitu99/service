package com.caitu99.service.user.domain;

import java.util.Date;

public class Sign {
    private Long id;

    private Long userId;
    
    private Date sign_date;

	private Long count;

	//0表示今天没签到过，1今天表示签到过
	private int signToday;
	
	//签到送的财币
	private Long signGiftIntegral;
	
	//连续签到的次数
	private Long continuous_time;

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Date getSign_date() {
		return sign_date;
	}

	public void setSign_date(Date sign_date) {
		this.sign_date = sign_date;
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

	public Long getContinuous_time() {
		return continuous_time;
	}

	public void setContinuous_time(Long continuous_time) {
		this.continuous_time = continuous_time;
	}

	public Long getSignGiftIntegral() {
		return signGiftIntegral;
	}

	public void setSignGiftIntegral(Long signGiftIntegral) {
		this.signGiftIntegral = signGiftIntegral;
	}

	public int getSignToday() {
		return signToday;
	}

	public void setSignToday(int signToday) {
		this.signToday = signToday;
	}
}