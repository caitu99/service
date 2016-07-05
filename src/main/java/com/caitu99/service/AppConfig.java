package com.caitu99.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AppConfig {

	@Value("${env}")
	public byte env;

	@Value("${sms.uri}")
	public String smsUrl;

	@Value("${sms.softwareSerialNo}")
	public String smsSoftwareSerialNo;

	@Value("${sms.key}")
	public String smsKey;

	/** 话费充 部分 */
	@Value("${recharge.phone.switch}")
	public String rechargePhoneSwitch;

	@Value("${recharge.phone.key}")
	public String rechargePhoneKey;

	@Value("${recharge.phone.openid}")
	public String rechargePhoneOpenId;

	@Value("${recharge.phone.checked.url}")
	public String rechargePhoneCheckedUrl;

	@Value("${recharge.phone.order.url}")
	public String rechargePhoneOrderUrl;

	@Value("${exchange.scale}")
	public double exchangeScale;

	@Value("${upload.path}")
	public String uploadPath;

	@Value("${spider.url}")
	public String spiderUrl;
	
	@Value("${static.url}")
	public String staticUrl;
	
	@Value("${file.url}")
	public String fileUrl;
	
	@Value("${image.pic.big}")
	public String imagePicBig;
	
	@Value("${image.file.big}")
	public String imageFileBig;

	@Value("${job.topic}")
	public String jobTopic;

	@Value("${wechat.appID}")
	public String appID;
	
	@Value("${wechat.appsecret}")
	public String appsecret;
	
	@Value("${wechat.caituUrl}")
	public String caituUrl;
	
	@Value("${wechat.band.template}")
	public String bandTemplate;
	
	/**付费抽奖*/
	@Value("${activities.item.integral}")
	public Long activitiesItemIntegral;
	/**放弃奖品返还财币*/
	@Value("${activities.add.integral}")
	public Long activitiesAddIntegeral;
	/**活动Id*/
	@Value("${activities.id}")
	public long activitiesId;
	/**没中奖提示*/
	@Value("${activities.winning.title}")
	public String activitiesWinningTitle;

	@Value("${emails.exclude}")
	public String emailExclude;

    /**每天最大消费总额*/
    @Value("${integral.max.consumes}")
	public Integer maxConsumesPerDay;

	/**
	 * 企业财币预警值
	 */
	@Value("${company.integral.threshold}")
	public Long companyIntegralThreshold;
	

	@Value("${mode.union.pay}")
	public String modeUnionPay;

	@Value("${max.query.limit}")
	public Integer maxQueryLimit;

	@Value("${phone.recharge.scale}")
	public Integer phoneRechargeScale;
	
	/***/
	@Value("${timer.free.tarde.integral}")
	public String freeTardeTntegeral;
	
	/** 积分变现天翼爱奇艺购买商品ID  */
	@Value("${realize.esurfing.limit.iQIYI.itemId}")
	public String realizeEsurfingLimitIQIYIItemId;
	
	/** 积分变现天翼爱奇艺购买积分限制  */
	@Value("${realize.esurfing.limit.iQIYI.integral}")
	public String realizeEsurfingLimitIQIYIIntegral;
	
	/** 积分变现天翼中石化购买商品ID  */
	@Value("${realize.esurfing.limit.CNPC.itemId}")
	public String realizeEsurfingLimitCNPCItemId;
	
	/** 积分变现天翼中石油购买积分限制  */
	@Value("${realize.esurfing.limit.CNPC.integral}")
	public String realizeEsurfingLimitICNPCIntegral;
	
	@Value("${phone.recharge.amount.limit}")
	public Long phoneRechargeAmountLimit;
	
	@Value("${is.dev.mode}")
	public boolean isDevMode;
	
	@Value("${is.test.mode}")
	public boolean isTestMode;
	
	/** 积分变现分享红包过期时间(单位:秒) */
	@Value("${realize.share.expires.time}")
	public Long realizeShareExpiresTime;
	
	/** 我的权益失效时间(单位:天) */
	@Value("${my.rights.expires.time}")
	public Integer myRightsExpiresTime;
	
	
	/**彩票*/
	@Value("${lottery.app.key}")
	public String lotteryAppKey;
	
	@Value("${lottery.app.secret}")
	public String lotteryAppSecret;
	
	@Value("${lottery.app.url}")
	public String lotteryAppUrl;

	
	@Value("${push.lottery.pay.success}")
	public String pushLotteryPaySuccess;
	
	@Value("${push.lottery.pay.fail}")
	public String pushLotteryPayFail;
	
	@Value("${push.lottery.title.success}")
	public String pushLotteryTitleSuccess;
	
	@Value("${push.lottery.title.fail}")
	public String pushLotteryTitleFail;
	
	@Value("${tubi.cardtypeids}")
	public String tubiCardTypeIds;
	
	
	
	@Value("${tubi.integer.rate}")
	public double tubiIntegerRate;
	
	
	
	/**等级经验*/
	@Value("${exp.record.exp}")
	public Long expRecordExp;
	
	@Value("${exp.sig.evenyday}")
	public Long expSigEvenyday;
	
	@Value("${exp.sig.goon}")
	public Long expSigGoon;
	
	@Value("${exp.realization.cash}")
	public double expRealizationCash;
	
	@Value("${exp.realization.top}")
	public Long expRealizationTop;
	
	@Value("${exp.ishop.inegral}")
	public double expIshopInegral;

	@Value("${exp.ishop.top}")
	public Long expIshopTop;
	
	@Value("${exp.ishop.time}")
	public Long expIshopTime;
	
	@Value("${exp.ishop.time.top}")
	public Long expIshopTimeTop;

	@Value("${exp.share.exp}")
	public Long expShareExp;
	
	@Value("${exp.share.top}")
	public Long expShareTop;

	
}
