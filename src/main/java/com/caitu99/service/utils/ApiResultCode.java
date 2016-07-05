package com.caitu99.service.utils;


public class ApiResultCode {

	/** 成功 */
	public final static Integer SUCCEED = 0;
	
	/** 需要输入验证码 */
	public final static Integer NEED_INOUT_IMAGECODE = 1001;
	
	/** 验证京东账户成功 */
	public final static Integer JINGDONG_CHECH_LOGINACCOUNT_SUCCEED = 1037;
	
	/** 移动发送验证码成功 */
	public final static Integer CMCC_SEND_PHONECODE_SUCCEED = 1051;
	
	/** 联通登录成功 */
	public final static Integer CU_LOGIN_SUCCEED = 1070;
	
	
	/** 用户id不能为空 */
	public final static Integer USER_ID_NOT_NULL = 2008;

	/** 用户密码长度不正确*/
	public final static Integer USER_PASSWORD_LENGTH=2409;
	/** 积分账户id不能为空 */
	public final static Integer MANUAL_ID_NOT_NULL = 2410;
	/** 用户名不能为空 */
	public final static Integer LOGINACCOUNT_NOT_NULL = 2411;
	/** 密码不能为空 */
	public final static Integer PASSWORD_NOT_NULL = 2412;
	/** 图片验证码不能为空 */
	public final static Integer IMAGE_CODE_NOT_NULL = 2413;
	/** 手机验证码不能为空 */
	public final static Integer PHONE_CODE_NOT_NULL = 2414;
	/** 银行卡号不能为空 */
	public final static Integer BANKCARD_NOT_NULL = 2415;
	/** 身份证不能为空 */
	public final static Integer IDENTITYCARD_NOT_NULL = 2416;
	/** 手机不能为空 */
	public final static Integer PHONE_NOT_NULL = 2417;
	/** 查询积分账户列表失败 */
	public final static Integer QUERY_MANUAL_LIST_ERROR = 2418;
	/** 查询积分账户列表,字母排序失败 */
	public final static Integer QUERY_MANUAL_LIST_SORE_ERROR = 2419;
	/** 查询用户登录记录失败 */
	public final static Integer QUERY_MANUAL_LOGIN_ERROR = 2420;
	/** 查询手动查询登录配置失败 */
	public final static Integer QUERY_FUTURE_ERROR = 2421;
	/** 生成手动查询登录配置失败 */
	public final static Integer GENERATE_LOGIN_CONFIG_ERROR = 2422;
	/** 获取天翼图片验证码失败 */
	public final static Integer GET_TIANYI_IMAGE_CODE_ERROR = 2423;
	
	/** 验证天翼图片验证码失败 */
	public final static Integer CHECH_TIANYI_IMAGE_CODE_ERROR = 2602;
	/** 天翼短信验证码发送失败 */
	public final static Integer TIANYI_IMAGE_SEND_NOTE_CODE_ERROR = 2007;
	/** 天翼图片验证码不通过,请重新获取验证码 */
	public final static Integer TIANYI_IMAGE_CODE_NOT_PASS = 2004;
	/** 登录天翼失败 */
	public final static Integer TIANYI_LOGIN_ERROR = 2005;
	/** 获取天翼积分失败 */
	public final static Integer TIANYI_GET_INTEGRAL_ERROR = 2049;
	
	/** 新增用户手动查询积分数据失败 */
	public final static Integer INSERT_USER_CARD_MANUAL_ERROR = 2424;
	/** 查询用户手动查询积分数据失败 */
	public final static Integer QUERY_USER_CARD_MANUAL_ERROR = 2425;
	/** 修改用户手动查询积分数据失败 */
	public final static Integer UPDATE_USER_CARD_MANUAL_ERROR = 2426;
	
	/** 新增手动账户登录记录失败 */
	public final static Integer INSERT_MANUAL_LOGIN_ERROR = 2427;
	/**验证成功,有短信验证 */
	public final static Integer FOLLOWUP_VERIFY_PHONE = 2428;

	/** 获取国航图片验证码失败 */
	public final static Integer GET_AIRCHINA_IMAGE_CODE_ERROR = 2429;
	/** 修改手动账户登录记录失败 */
	public final static Integer UPDATE_MANUAL_LOGIN_ERROR = 2430;
	/** 登录国航失败  */
	public final static Integer AIRCHINA_LOGIN_ERROR = 2431;
	
	/** 暂不支持此积分账户 */
	public final static Integer NONSUPPORT_MANUAL = 2432;
	
	/** 获取京东图片验证码失败 */
	public final static Integer JINGDONG_GET_IMAGECODE_ERROR = 2433;
	/** 登录京东账户失败 */
	public final static Integer JINGDONG_LOGIN_ERROR = 2434;
	
	/** 获取招商银行图片验证码失败 */
	public final static Integer CMB_GET_IMAGECODE_ERROR = 2435;
	/** 登录招商银行失败 */
	public final static Integer CMB_LOGIN_ERROR = 2436;
	
	/** 新增或修改用户手动查询积分数据失败 */
	public final static Integer INSERT_UPDATE_USER_CARD_MANUAL_ERROR = 2437;
	
	/** 新增或修改手动账户登录记录失败 */
	public final static Integer INSERT_UPDATE_MANUAL_LOGIN_ERROR = 2438;
	
	/**验证成功,有图片验证 */
	public final static Integer FOLLOWUP_VERIFY_IMAGE = 2439;
	
	/** 获取南航图片验证码失败 */
	public final static Integer CSAIR_GET_IMAGECODE_ERROR = 2440;
	/** 登录南航失败 */
	public final static Integer CSAIR_LOGIN_ERROR = 2441;
	
	/** 获取淘宝图片验证码失败 */
	public final static Integer TAOBAO_GET_IMAGECODE_ERROR = 2442;
	/** 登录淘宝失败 */
	public final static Integer TAOBAO_LOGIN_ERROR = 2443;
	/** 验证淘宝短信验证码失败 */
	public final static Integer TAOBAO_CHECK_PHONECODE_ERROR = 2444;
	
	/** 获取移动图片验证码失败 */
	public final static Integer CMCC_GET_IMAGECODE_ERROR = 2445;
	/** 获取移动短信验证码失败 */
	public final static Integer CMCC_GET_PHONECODE_ERROR = 2446;
	/** 登录移动失败 */
	public final static Integer CMCC_LOGIN_ERROR = 2447;
	
	/** 系统繁忙 */
	public final static Integer SYSTEM_BUSY = 2448;
	/** 账号错误 */
	public final static Integer LOGINACCOUNT_ERROR = 2449;
	/** 密码错误 */
	public final static Integer PASSWORD_ERROR = 2450;
	/** 账号或密码错误 */
	public final static Integer LOGINACCOUNT_PASSWORD_ERROR = 2451;
	/** 图片验证码不正确 */
	public final static Integer IMAGECODE_ERROR = 2452;
	/** 账号验证已过期 */
	public final static Integer LOGINACCOUNT_VERIFY_EXPIRE = 2453;
	/** 验证码必须为4位 */
	public final static Integer CODE_MUST_FOUR = 2454;
	/** 发送短信验证码失败 */
	public final static Integer SEND_CODE_ERROR = 2455;
	/** 短信验证码不正确 */
	public final static Integer PHONE_CODE_ERROR = 2456;
	/** 账户名不存在 */
	public final static Integer LOGINACCOUNT_NO_EXIST = 2457;
	/** 账户被锁定 */
	public final static Integer ACCOUNT_LOCK = 2458;
	/** 获取联通图片验证码失败 */
	public final static Integer CU_GET_IMAGECODE_ERROR = 2459;
	/** 自动识别图片验证码失败 */
	public final static Integer AUTO_DISCERN_IMAGECODE_ERROR = 2460;
	/** 无过期积分 */
	public final static Integer NOT_EXPIRE_INTEGRAL = 2461;
	/** 登录已过期或无登录 */
	public final static Integer NOT_LOGIN_STATUS = 2462;

	/** 平安银行下单成功,输入支付密码 */
	public final static Integer PAB_ORDER_PASSWORD = 2463;
	/** 平安银行下单成功,输入短信验证码 */
	public final static Integer PAB_ORDER_PHONECODE = 2464;
	/** 平安银行下单成功,输入支付密码和短信验证码 */
	public final static Integer PAB_ORDER_PASSWORD_AND_PHONECODE = 2465;
	
	
	/** 分页查询商品失败 */
	public final static Integer QUERY_PAGE_ITEM_ERROR = 2801;

	/** 关闭超时订单失败*/
	public final static Integer CLOSE_ORDER_ERROR = 2802;
	
	/** 商品不存在 */
	public final static Integer GOODS_ITEM_NOT_EXIST = 2803;

	/** 该手机号码已绑定过此openid */
	public final static Integer WECHAT_OPENID_BIND_USER = 2804;

	/** 该手机号码已存在,但未绑定过此openid */
	public final static Integer PHONE_EXIT_NOT_BIND_WECHAT = 2805;
	
	/**生成订单失败*/
	public final static Integer SC_ORDER_ERROR = 2901;
	
	/**修改订单状态失败*/
	public final static Integer ORDER_STATUS_FAILE = 2902;
	
	/**删除订单失败*/
	public final static Integer ORDER_DELETE_FAILE = 2903;
	
	/**订单分页查询失败*/
	public final static Integer ORDER_PAGE_FAILE = 2904;
	
	/**找不到该订单*/
	public final static Integer ORDER_QUERY_FAILE = 2910; 
	
	/**用户验证失败*/
	public final static Integer ORDER_USERID_FAILE = 2911; 
	
	/**商品明细为空*/
	public final static Integer ORDER_SKU_NONE = 2912;
	
	/**取消支付*/
	public final static Integer ORDER_CANCELPAY_FAILE = 2920;
	
	/**库存不足*/
	public final static Integer ORDER_INVENTORY_NONE = 2921;
	
	/**查询库存失败*/
	public final static Integer ORDER_INVENTORY_FAILE = 2922;
	
	
	/**查询抽奖次数失败*/
	public final static Integer ACTIVITIES_TIMES_ERROR = 3201;
	
	/**抽奖失败*/
	public final static Integer ACTIVITIES_GETPROOF_ERROR = 3202;
	
	/**领奖失败*/
	public final static Integer ACTIVITIES_WINNING_ERROR = 3203;
	
	/**财币扣取失败*/
	public final static Integer ACTIVITIES_DEDUCT_INTEGRAL_ERROR = 3204;
	
	/**库存不足*/
	public final static Integer ACTIVITIES_NO_STOCK_ERROR = 3205;
	
	/**财币领取失败*/
	public final static Integer ACTIVITIES_GET_INTEGRAL_ERROR = 3206;
	
	/**验证用户财币失败*/
	public final static Integer ACTIVITIES_CHECK_INTEGRAL_ERROR = 3207;
	
	/**放弃领奖失败*/
	public final static Integer ACTIVITIES_NOT_WINNING_ERROR = 3208;
	
	/**查询中奖信息失败*/
	public final static Integer ACTIVITIES_GET_INRECORD_ERROR = 3209;
	
	/**移动短信验证码过期或错误*/
	public final static Integer CMCC_SMS_ERROR = 2511;
	/**非移动用户请注册互联网用户登录*/
	public final static Integer CMCC_NOT_CMCC_USER = 2512;
	/**移动号码有误*/
	public final static Integer CMCC_PHONE_ERROR = 2513;
	
	/**洲际账号或密码错误*/
	public final static Integer IHG_ACCOUNT_PWD_ERROR = 2514;
	/**数据不完整*/
	public final static Integer DATA_LOST = 2515;
	/**花旗不可重复登录*/
	public final static Integer CITY_BANK_LOGIN_TWICE = 2516;
	
	/**获取铂涛会验证码错误*/
	public final static Integer BOTAOHUI_GET_IMAGECODE_ERROR = 2517;
	/**获取铂涛会验证码成功*/
	public final static Integer BOTAOHUI_GET_IMAGECODE_SUCCEED = 2520;
	/**获取铂涛会短信验证码成功*/
	public final static Integer BOTAOHUI_PHONE_CODE_SUCCEED = 2518;
	/**获取铂涛会短信验证码失败*/
	public final static Integer BOTAOHUI_GET_PHONE_CODE_ERROR = 2519;
	/**获取铂涛会登陆失败*/
	public final static Integer BOTAOHUI_LOGIN_ERROR = 2523;
	/**获取铂涛会短信过于频繁，稍后再试*/
	public final static Integer BOTAOHUI_PHONECODE_TWICE_ERROR = 2521;
	
	/**移动号码有误*/
	public final static Integer CMCC_GET_PHONE_CODE_ERROR = 2522;
	
	/**获取物美图片验证码失败*/
	public final static Integer WUMEI_GET_IMAGECODE_ERROR = 2530;
	/**获取物美图片验证码成功*/
	public final static Integer WUMEI_GET_IMAGECODE_SUCCEED = 2530;
	/**登录物美失败*/
	public final static Integer WUMEI_LOGIN_ERROR = 2531;
	/**登录物美，省份不能为空*/
	public final static Integer WUMEI_PROVINCE_NOT_NULL = 2532;
	/**图片验证码错误*/
	public final static Integer WUMEI_IMGCODE_ERROR = 2533;
	/**请输入图片验证码*/
	public final static Integer WUMEI_IMGCODE_INPUT = 2534;
	/**手机号码格式错误*/
	public final static Integer PHONE_NO_ERROR = 2535;
	/**请输入短信验证码*/
	public final static Integer WUMEI_PHONECODE_INPUT = 2536;
	/**密码修改成功*/
	public final static Integer WUMEI_PWD_MOD_SUCCESS = 2537;

	/** 自动发现失败 */
	public final static Integer AUTO_FIND_ERROR = 3501;
	
	/**自由交易市场记录为空**/
	public final static Integer FREE_TRADE_NULL_ERROR = 4001;
	
	/**查询商品属性失败**/
	public final static Integer FREE_TRADE_PROP_ERROR = 4002;
	
	/**参数为空*/
	public final static Integer REALIZE_ORDER_NULL = 5001;
	/**变现订单生成失败*/
	public final static Integer REALIZE_ORDER_ERROR = 5002;
	/**变现订单完成失败*/
	public final static Integer REALIZE_FINISH_ERROR = 5003;

	/**获取交通积分成功*/
	public final static Integer COMM_INTEGRAL = 2111;
	/** 查询交通银行积分失败 */
	public final static Integer COMM_INTEGRAL_ERROR = 5004;
	/** 移动积分解析失败 */
	public final static Integer CMCC_GET_INTEGRAL_ERROR = 5005;
	/** 交通银行登录失败 */
	public final static Integer COMM_LOGIN_ERROR = 5006;
	/** 交通银行积分解析失败 */
	public final static Integer COMM_GET_INTEGRAL_ERROR = 5007;
	/** 交通银行登录账号密码不匹配 */
	public final static Integer COMM_ACCOUNT_PASSWORD_ERROR = 5008;
	/**	卡片查询密码被锁 */
	public final static Integer COMM_ACCOUNT_PASSWORD_LOCKED = 5009;
	/**	登录密码输入已超过限制，请24小时后再试 */
	public final static Integer COMM_ACCOUNT_PASSWORD_LIMITTED= 5010;
	
	/**新手任务订单生成失败*/
	public final static Integer NEWBIE_ACTIVITY_ORDER_ERROR = 5011;
	/**参数为空*/
	public final static Integer NEWBIE_ACTIVITY_ORDER_NULL = 5012;
	/**新手任务订单完成失败*/
	public final static Integer NEWBIE_ACTIVITY_FINISH_ERROR = 5013;
	
	public final static Integer IMG_GET_ERROR = 6000;
	
	/** 登录失败 */
	public final static Integer LOGIN_ERROR = 6001;
	/** 请输入短信验证码 */
	public final static Integer PHONE_CODE_INPUT = 6002;


}
