package com.caitu99.service.user.controller.vo;


import com.caitu99.service.user.domain.User;

public class UserVO {
	private User user;

	private Long mailsize; // 邮箱个数

	private Long total; // 积分数量
	
	private boolean paypass;

	public Long getMailsize() {
		return mailsize;
	}

	public void setMailsize(Long mailsize) {
		this.mailsize = mailsize;
	}

	public Long getTotal() {
		return total == null ? 0 : total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the paypass
	 */
	public boolean isPaypass() {
		return paypass;
	}

	/**
	 * @param paypass the paypass to set
	 */
	public void setPaypass(boolean paypass) {
		this.paypass = paypass;
	}

}
