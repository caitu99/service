package com.caitu99.service.integral.domain;

import java.util.Date;

public class UserCardManual {	
	/** 招商银行  */
	public static final Long CMB = 2L;
	/** 天翼积分  */
	public static final Long ESURFING_INTEGRAL = 29L;
	/** 电信积分  */
	public static final Long CT_INTEGRAL = 30L;
	/** 国航积分  */
	public static final Long AIRCHINA_INTEGRAL = 20L;
	/** 京东积分  */
	public static final Long JINGDONG_INTEGRAL = 34L;
	/** 南航积分  */
	public static final Long CSAIR_INTEGRAL = 3L;
	/** 淘宝积分  */
	public static final Long TAOBAO_INTEGRAL = 31L;
	/** 天猫积分  */
	public static final Long TMALL_INTEGRAL = 32L;
	/** 淘里程  */
	public static final Long TAOLICHENG_INTEGRAL = 33L;
	/** 移动积分  */
	public static final Long CMCC_INTEGRAL = 36L;
	/** 联通积分  */
	public static final Long CU_INTEGRAL = 37L;
	/** 洲际积分  */
	public static final Long IHG_INTEGRAL = 38L;
	/** 花旗银行积分  */
	public static final Long CITYBANK_INTEGRAL = 28L;
	/** 铂涛会积分  */
	public static final Long BOTAOHUI_INTEGRAL = 39L;
	/** 物美积分  */
	public static final Long WUMEI_INTEGRAL = 40L;
	/** 交通银行 */
	public static final Long COMM_INTEGRAL = 16L;
	/**建设银行*/
	public static final Long CCB_INTEGRAL = 17L;
	/**中信银行*/
	public static final Long CCBI_INTEGRAL = 13L;
	/**浦发银行*/
	public static final Long PUFA_INTEGRAL = 45L;
	/**平安银行*/
	public static final Long PINGAN_INTEGRAL = 7L;
	
	
	/** 导入方式:用户手动导入 */
	public static final Integer TYPE_USER_IMPORT = 0;
	/** 导入方式:自动发现导入 */
	public static final Integer TYPE_AUTO_FIND_IMPORT = 1;
	
	
	
	public UserCardManual(){
		
	}
	
    public UserCardManual(Long userId, Long cardTypeId, String userName,Integer integral) {
		this.userId = userId;
		this.cardTypeId = cardTypeId;
		this.userName = userName;
		this.integral = integral;
	}

	public UserCardManual(Integer integral,String cardNo,Integer expirationIntegral,
			Integer nextExpirationIntegral,String userName, Long userId, Long cardTypeId) {
		this.integral = integral;
		this.cardNo = cardNo;
		this.expirationIntegral = expirationIntegral;
		this.nextExpirationIntegral = nextExpirationIntegral;
		this.userName = userName;
		this.userId = userId;
		this.cardTypeId = cardTypeId;
	}

	private Long id;

    private Long userId;

    private Long cardTypeId;
    
    private String userName;
    
    private String cardNo;
    
    private String loginAccount;

	private Integer integral;

    private Integer expirationIntegral;

    private Integer nextExpirationIntegral;

	private Date expirationTime;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModify;
    
    private Integer type;

    public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

    public Long getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(Long cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public String getLoginAccount() {
		return loginAccount;
	}

	public void setLoginAccount(String loginAccount) {
		this.loginAccount = loginAccount;
	}

    public Integer getIntegral() {
        return integral;
    }

    public void setIntegral(Integer integral) {
        this.integral = integral;
    }

    public Integer getExpirationIntegral() {
        return expirationIntegral;
    }

    public void setExpirationIntegral(Integer expirationIntegral) {
        this.expirationIntegral = expirationIntegral;
    }

    public Integer getNextExpirationIntegral() {
		return nextExpirationIntegral;
	}

	public void setNextExpirationIntegral(Integer nextExpirationIntegral) {
		this.nextExpirationIntegral = nextExpirationIntegral;
	}
	
    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

	/**
	 * 修改积分数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: updateIntegral 
	 * @param integral
	 * @param cardNo
	 * @param expirationIntegral
	 * @param nextExpirationIntegral
	 * @param userName
	 * @param gmtModify
	 * @date 2015年11月14日 下午9:07:01  
	 * @author xiongbin
	*/
	public void updateIntegral(Integer integral,String cardNo,Integer expirationIntegral,Integer nextExpirationIntegral,String userName,Date gmtModify) {
		this.setIntegral(integral);
		this.setCardNo(cardNo);
		this.setExpirationIntegral(expirationIntegral);
		this.setNextExpirationIntegral(nextExpirationIntegral);
		this.setUserName(userName);
		this.setGmtModify(gmtModify);
	}
}