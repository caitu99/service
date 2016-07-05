package com.caitu99.service;


public class RedisKey {

    public static final String SMS_SEND_KEY = "sms_send_key_%s";
    
    public static final String IS_SEND_TO_MOBILE = "is_send_to_mobile_%s";
    
    public static final String SMS_CL_MSGID = "sms_cl_msgid_%s_%s";
    
    public static final String USER_USERAUTH_BY_USER_ID_KEY = "user_userauth_by_user_id_%d";
    
    public static final String USER_USER_BY_ID_KEY = "user_user_by_id_%d";
    
    public static final String SYS_CONFIG_BY_KEY_KEY = "sys_config_by_key_%s";
    
    public static final String SYS_CONFIG_LIST_KEY = "sys_config_list";
    
    public static final String INTEGRAL_CARD_INTEGRAL_LIST_BY_CARD_ID_KEY = "integral_card_integral_list_by_card_id_%d";
    
    public static final String INTEGRAL_CARD_INTEGRAL_LAST_TIME_LIST_BY_USER_ID_KEY = "integral_card_integral_last_time_list_by_user_id_%d";
	
    public static final String INTEGRAL_CARD_INTEGRAL_OTHER_TIME_LIST_BY_USER_ID_KEY = "integral_card_integral_other_time_list_by_user_id_%d";
    
    public static final String INTEGRAL_CONSUME_LIST_BY_USER_ID_KEY = "integral_consume_list_by_user_id_%d";
    
    public static final String INTEGRAL_EXCHANGE_RULE_LIST_KEY = "integral_exchange_rule_list";
    
    public static final String INTEGRAL_INTEGRAL_EXCHANGE_LIST_BY_USER_ID_KEY = "integral_integral_exchange_list_by_user_id_%d";

    public static final String USER_USER_BY_PRIMARY_KEY = "user_user_by_primary_key_%d";
	
	public static final String USER_USER_CARD_LIST_BY_USER_ID_KEY = "user_user_card_list_by_user_id_%d";


	/**
	 *查询商品首页分类key
	 */
	public static final String GOODS_CLASSIFICATION_GOODS_LIST_KEY = "good_classification_goods_list";
	
	/**
	 * 查询积分账户列表key
	 */
	public static final String INTEGRAL_MANUAL_LIST = "integral_manual_list";
	
	/**
	 * 查询积分账户列表,字母排序key
	 */
	public static final String INTEGRAL_MANUAL_LIST_TO_SORT = "integral_manual_list_sort";

	/**
	 * 天翼每个用户cookie key
	 */
	public static final String TIANYI_COOKIET_USER = "tianyi_cookie_%d";
	
	/**
	 * 天翼每个用户账号 key
	 */
	public static final String TIANYI_LOGINACCOUNT_USER = "tianyi_loginaccount_%d";
	
	/**
	 * 国航每个用户cookie key
	 */
	public static final String AIRCHINA_COOKIET_USER = "airchina_cookie_%d";
	
	/**
	 * 京东每个用户cookie key
	 */
	public static final String JINGDONG_COOKIET_USER = "jingdong_cookie_%d";
	
	/**
	 * 每个积分账户登录页面参数key,1.0版
	 */
	public static final String INTEGRAL_MANUAL_LOGIN_PARAM_1 = "integral_manual_login_param_1_%d";
	
	/**
	 * 每个积分账户登录页面参数key,2.0版
	 */
	public static final String INTEGRAL_MANUAL_LOGIN_PARAM_2 = "integral_manual_login_param_2_%d";
	
	/**
	 * 每个用户相应的积分账户登录记录key,1.0版
	 */
	public static final String INTEGRAL_MANUAL_USER_LOGIN_RECORD_1 = "integral_manual_user_login_record_1_%d_%d";
	
	/**
	 * 每个用户相应的积分账户登录记录key,2.0版
	 */
	public static final String INTEGRAL_MANUAL_USER_LOGIN_RECORD_2 = "integral_manual_user_login_record_2_%d_%d";
	
	/**
	 * 淘宝每个用户账号 key
	 */
	public static final String TAOBAO_MANUAL_LOGINACCOUNT_USER = "taobao_manual_loginaccount_%d";
	/**
	 * 淘宝每个密码 key
	 */
	public static final String TAOBAO_MANUAL_PASSWORD_USER = "taobao_manual_password_%d";
	
	/**
	 * 移动每个用户 key
	 */
	public static final String CMCC_MANUAL_USER = "cmcc_manual_user_%d";
	/**
	 * 移动每个用户账号 key
	 */
	public static final String CMCC_MANUAL_USER_ACCOUNT = "cmcc_manual_user_account_%d";
	/**
	 * 铂涛会每个用户 key
	 */
	public static final String BOTAOHUI_MANUAL_USER = "botaohui_manual_user_%d";
	
	/**
	 * 联通每个用户 key
	 */
	public static final String CU_MANUAL_USER = "cu_manual_user_%d";
	/**
	 * 浦发银行每个用户 key
	 */
	public static final String PUFABANK_MANUAL_USER = "pufabank_manual_user_%d";
	/***
	 * 平安和个用户key
	 */
	public static final String PINGANBANK_MANUAL_USER ="pinganbank_manual_user_%d";
	/**
	 * 微信access_token key
	 */
	public static final String WECHAT_ACCESS_TOKEN = "wechat_access_token";
	
	/**
	 * 活动-刮刮卡 key
	 */
	public static final String ACTIVITIES_CARDS_ID = "activities_cards_id_%d";
	
	/**
	 * 轮播图 key
	 */
	public static final String BANNER_CODE = "banner_code_%d";
	
	/**
	 * 查询在线办理银行卡列表key
	 */
	public static final String MANAGE_BANK_ONLINE_LIST = "manage_bank_online_list";
	
	/**
	 * 查询上门办理银行卡列表key
	 */
	public static final String MANAGE_BANK_DROPIN_LIST = "manage_bank_dropIn_list";
	
	/**
	 * 根据id查询银行key
	 */
	public static final String MANAGE_BANK_PRIMARYKEY_BANKCARDID = "manage_bank_primaryKey_bankCardId_%d";
	
    /**
     * 用户相关卡片的最近积分详情
     */
    public static final String INTEGRAL_CARD_INTEGRAL_LAST_TIME_LIST_NEW_BY_USER_ID_KEY = "integral_card_integral_last_time_list_new_by_user_id_%d_card_id_%d";
	
    public static final String INTEGRAL_CARD_INTEGRAL_OTHER_TIME_LIST_NEW_BY_USER_ID_KEY = "integral_card_integral_other_time_list_new_by_user_id_%d_card_id_%d";
    
    /**
     * 用户登录相应的积分账户是否需要图片验证码
     */
    public static final String MANUAL_QUERY_ISIMAGE_BY_USERID_MANUALID = "manual_query_isImage_by_userId_%d_manualId_%d";
    
    /**
     * 手动查询平台配置key
     */
    public static final String MANUAL_FUTURE_BY_MANUALID_TYPE = "manual_future_by_manualId_%d_type_%d";
    
    /**
     * 卡片类型key
     */
    public static final String CARD_TYPE_BY_ID = "card_type_by_id_%d";
    
    /**
     * 查询自由交易平台集合key
     */
    public static final String FREE_TRADE_PLATFORM_LIST = "free_trade_platform_list";
    
    /**
     * 查询自由交易平台集合key,版本号
     */
    public static final String FREE_TRADE_PLATFORM_LIST_VERSION = "free_trade_platform_list_version_%s";

	/**
	 * 联通商城订单
	 */
	public static final String UNICOM_ORDERNO_USERID_KEY = "unicom_orderno_userid_%s";
	
	/**
	 * 积分变现预约人数key
	 */
	public static final String INTEGRAL_REALIZATION_SUBSCRIBE_BY_CARDTYPEID = "integral_realization_subscribe_by_cardtypeId_%s";

	/**
	 * 建行积分商城用户登录信息key
	 */
	public static final String CCB_AUTO_ACCOUNT_BY_USERID = "ccb_auto_account_by_userId_%s";
	
	/**
	 * 建行积分商城用户银行卡信息key
	 */
	public static final String CCB_AUTO_BANKCARD_BY_USERID = "ccb_auto_bankCard_by_userId_%s";
	
	/***
	 * 建行帐户
	 */
	public static final String CCB_ACCOUNT_KEY = "ccb_account_key_uerId_%s";
	/***
	 * 建行密码
	 */
	public static final String CCB_PASSWORD_KEY = "ccb_password_key_uerId_%s";
	
	
	/***
	 * 中信帐户
	 */
	public static final String CCBI_ACCOUNT_KEY = "ccbi_account_key_uerId_%s";
	/***
	 * 中信密码
	 */
	public static final String CCBI_PASSWORD_KEY = "ccbi_password_key_uerId_%s";
	
	
	/**
	 * 天翼积分商城用户登录信息key
	 */
	public static final String ESURFING_AUTO_ACCOUNT_BY_USERID = "esurfing_auto_account_by_userId_%s";

    /**
     * 积分变现商户key
     */
    public static final String FREE_TRADE_PLATFORM_BY_ID = "free_trade_platform_by_id_%d";

	/**
	 * 用户在线办卡实名认证key
	 */
	public static final String MANAGE_CARD_USER_AUTH_BY_USERID = "manage_card_user_auth_by_userid_%d";

    /**
     * 查询积分变现平台集合key,版本号
     */
    public static final String REALIZATION_PLATFORM_LIST_VERSION = "realization_platform_list_version_version_%s";

    /**
     * 积分变现平台key
     */
    public static final String REALIZATION_PLATFORM_BY_ID = "realization_platform_by_id_%s";

	/**
	 * 交通银行用户信息
	 */
	public static final String COM_ACCOUNT_BY_USERID = "com_account_by_userId_%s";
	
    /**
     * 空白首页卡片类型列表
     */
    public static final String CARD_TYPE_BLANK_LIST = "card_type_blank_list";
    
    /**
     * 充值密码发送验证码
     */
    public static final String SMS_SEND_RESET_PAY_PASSWORD_KEY = "sms_send_reset_pay_password_key_%s";
}
