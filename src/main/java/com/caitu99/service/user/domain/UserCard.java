package com.caitu99.service.user.domain;

import java.util.Date;

public class UserCard {
	
	/**
	 * 导入类型:邮箱导入
	 */
	public static final Integer IMPORT_TYPE_EMAIL = 0;
	/**
	 * 导入类型:手动导入
	 */
	public static final Integer IMPORT_TYPE_MANUAL = 1;
	/**
	 * 导入类型:用户添加
	 */
	public static final Integer IMPORT_TYPE_USER_ADD = 2;
	
	private Long id;

	private Long userId;

	private String name;

	private Long cardTypeId;

	private String cardNo;

	private String email;

	private String emailPassword;

	private Integer billDay;

	private Integer repaymentDay;

	private Integer integralBalance;

	private Long consumeTotal;

	private Integer maxAmount;

	private Integer commonAmount;

	private Integer cashAmount;

	private Date gmtCreate;

	private Date gmtModify;

	private Integer status;

	private Integer typeId;

	private String typeName;

	private Float totalbalance;

	private Float minamount;

	private Float scale;

	private String url;

	private Integer canexchange;

	private Date billMonth;

	private String cardName;
	
	//图片相对地址s
	private String cardTypePic;
	
	//导入类型(0:自动导入;1:手动导入;2:用户添加)
	private Integer importType;
	
	//是否是最新
	private Integer newest;
	
	//是否支持积分变现(0:不支持;1:支持)
	private Integer isRealization;
	
	//是否支持在线办卡(0:不支持;1:支持)
	private Integer isCard;
	
	//是否有精选商城(0:不支持;1:支持)
	private Integer isShop;

	public Integer getIsRealization() {
		return isRealization;
	}

	public void setIsRealization(Integer isRealization) {
		this.isRealization = isRealization;
	}

	public Integer getIsCard() {
		return isCard;
	}

	public void setIsCard(Integer isCard) {
		this.isCard = isCard;
	}

	public Integer getIsShop() {
		return isShop;
	}

	public void setIsShop(Integer isShop) {
		this.isShop = isShop;
	}

	public Integer getNewest() {
		return newest;
	}

	public void setNewest(Integer newest) {
		this.newest = newest;
	}

	public Integer getImportType() {
		return importType;
	}

	public void setImportType(Integer importType) {
		this.importType = importType;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public Date getBillMonth() {
		return billMonth;
	}

	public void setBillMonth(Date billMonth) {
		this.billMonth = billMonth;
	}

	public Integer getCanexchange() {
		return canexchange;
	}

	public void setCanexchange(Integer canexchange) {
		this.canexchange = canexchange;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Float getTotalbalance() {
		return totalbalance;
	}

	public void setTotalbalance(Float totalbalance) {
		this.totalbalance = totalbalance;
	}

	public Float getMinamount() {
		return minamount;
	}

	public void setMinamount(Float minamount) {
		this.minamount = minamount;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public Long getCardTypeId() {
		return cardTypeId;
	}

	public void setCardTypeId(Long cardTypeId) {
		this.cardTypeId = cardTypeId;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo == null ? null : cardNo.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email == null ? null : email.trim();
	}

	public String getEmailPassword() {
		return emailPassword;
	}

	public void setEmailPassword(String emailPassword) {
		this.emailPassword = emailPassword == null ? null : emailPassword
				.trim();
	}

	public Integer getBillDay() {
		return billDay;
	}

	public void setBillDay(Integer billDay) {
		this.billDay = billDay;
	}

	public Integer getRepaymentDay() {
		return repaymentDay;
	}

	public void setRepaymentDay(Integer repaymentDay) {
		this.repaymentDay = repaymentDay;
	}

	public Integer getIntegralBalance() {
		return integralBalance;
	}

	public void setIntegralBalance(Integer integralBalance) {
		this.integralBalance = integralBalance;
	}

	public Long getConsumeTotal() {
		return consumeTotal;
	}

	public void setConsumeTotal(Long consumeTotal) {
		this.consumeTotal = consumeTotal;
	}

	public Integer getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(Integer maxAmount) {
		this.maxAmount = maxAmount;
	}

	public Integer getCommonAmount() {
		return commonAmount;
	}

	public void setCommonAmount(Integer commonAmount) {
		this.commonAmount = commonAmount;
	}

	public Integer getCashAmount() {
		return cashAmount;
	}

	public void setCashAmount(Integer cashAmount) {
		this.cashAmount = cashAmount;
	}

	public Date getGmtCreate() {
		return gmtCreate;
	}

	public void setGmtCreate(Date gmtCreate) {
		this.gmtCreate = gmtCreate;
	}

	public Date getGmtModify() {
		return gmtModify;
	}

	public void setGmtModify(Date gmtModify) {
		this.gmtModify = gmtModify;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getTypeId() {
		return typeId;
	}

	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}

	public Float getScale() {
		return scale;
	}

	public void setScale(Float scale) {
		this.scale = scale;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getCardTypePic() {
		return cardTypePic;
	}

	public void setCardTypePic(String cardTypePic) {
		this.cardTypePic = cardTypePic;
	}
}