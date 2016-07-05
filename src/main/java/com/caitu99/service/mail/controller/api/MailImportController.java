package com.caitu99.service.mail.controller.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.constants.SysConstants;
import com.caitu99.service.integral.dao.BankMapper;
import com.caitu99.service.integral.domain.Bank;
import com.caitu99.service.integral.domain.CardIntegral;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.service.integral.domain.ManualResult;
import com.caitu99.service.integral.service.CardIntegralService;
import com.caitu99.service.integral.service.CardTypeService;
import com.caitu99.service.integral.service.ExchangeRuleService;
import com.caitu99.service.life.service.RechargeService;
import com.caitu99.service.mail.controller.vo.UserCardVo;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.dto.TransactionRecordDto;
import com.caitu99.service.transaction.service.AccountDetailService;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.user.controller.vo.BillResult;
import com.caitu99.service.user.controller.vo.UserCardResult;
import com.caitu99.service.user.domain.UserCard;
import com.caitu99.service.user.domain.UserCardBackup;
import com.caitu99.service.user.domain.UserMail;
import com.caitu99.service.user.service.UserCardBackupService;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.user.service.UserMailService;
import com.caitu99.service.utils.XStringUtil;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.http.HttpClientUtils;

@Controller
@RequestMapping("/api/spider")
public class MailImportController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(MailImportController.class);

	@Autowired
	private CardTypeService cardTypeService;
	@Autowired
	private UserCardService userCardService;
	@Autowired
	private CardIntegralService cardIntegralService;
	@Autowired
	private UserCardBackupService userCardBackupService;
	@Autowired
	private UserMailService userMailService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private RedisOperate redis;
	@Autowired
	private RechargeService rechargeService;
	@Autowired
	private ExchangeRuleService exchangeRuleService;
	@Autowired
	private BankMapper bankMapper;
	@Autowired
	private TransactionRecordService transactionRecordService;
	@Autowired
	private AccountService accountService;
	@Autowired
	private AccountDetailService accountDetailService;
	
	private String USER_MAIL_KEY = "user_%d_mail_%s";

	/**
	 * @param userid
	 * @param account
	 * @param password
	 * @return
	 * @Description: (邮箱登录)
	 * @Title: login
	 * @date 2015年11月2日 下午3:05:15
	 * @author lhj
	 */
	@RequestMapping(value = "/mail/login/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String login(Long userid, String account, String password,
			String passwordalone, String cookie) {

		// 初始化
		ApiResult<String> result = new ApiResult();
		result.setCode(0);
		HttpClient httpCLient = HttpClientBuilder.create().build();
		if (StringUtils.isEmpty(cookie))
			cookie = "";
		// 数据验证
		String mailType = mailType(account);
		if (null == mailType) {
			result.set(2601, "不支持此邮箱");
			return JSON.toJSONString(result);
		}

		// 业务实现
		// 获取邮件上次更新时间
		UserMail userMail = getUserMail(userid, account);
		if (userMail == null) {
			userMail = new UserMail();
			userMail.setUserId(userid);
			userMail.setEmail(account);
		}

		// update password and alone password
		try {
			userMail.setEmailPassword(AESCryptoUtil.encrypt(password));
		} catch (CryptoException e) {
			logger.error("encrypt password error:{}", password);
		}
		if (StringUtils.isNotBlank(passwordalone)) {
			try {
				userMail.setEmailPasswordAlone(AESCryptoUtil
						.encrypt(passwordalone));
			} catch (CryptoException e) {
				logger.error("encrypt password error:{}", password);
			}
		}
		userMail.setGmtLastUpdate(XStringUtil.getLastSeasonDay());

		// 缓存
		String key = String.format(USER_MAIL_KEY, userid, account);
		redis.del(key);
		redis.set(key, JSON.toJSONString(userMail));
		if (!"sina".equals(mailType)) {
			account = account.substring(0, account.indexOf("@"));
		}
		// 创建get请求实例
		StringBuffer url = new StringBuffer(appConfig.spiderUrl);
		// HttpGet httpget = new
		// HttpGet(url.append("/api/mail/").append(mailType)
		// .append("/login/1.0?userid=").append(userid)
		// .append("&account=").append(account).append("&password=")
		// .append(password).append("&cookie=").append(cookie)
		// .append("&date=").append(userMail.getGmtLastUpdate().getTime())
		// .toString());
		try {
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("userid", String.valueOf(userid));
			paramMap.put("account", account);
			paramMap.put("password", password);
			paramMap.put("cookie", cookie);
			if (userMail.getGmtLastUpdate() != null)
				paramMap.put("date",
						String.valueOf(userMail.getGmtLastUpdate().getTime()));

			String postResult = HttpClientUtils
					.getInstances()
					.doPost(url.append("/api/mail/").append(mailType)
							.append("/login/1.0").toString(), "utf-8", paramMap);
			result = JSON.parseObject(postResult, ApiResult.class);
			// if (map.get("code") == null)
			// result.set(-1, "unknown");
			// else
			// result.set(Integer.valueOf(map.get("code").toString()), map
			// .get("message") == null ? "" : map.get("message")
			// .toString());
			// result.setData(map.get("data") == null ? "" : map.get("data")
			// .toString());
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
			result.set(2606, "clientprotocol异常");
		} catch (IOException e) {
			logger.error("IOException", e);
			result.set(2607, "io异常");
		} catch (JSONException e) {
			logger.error("JSONException", e);
			result.set(2608, "json解析错误");
		} catch (Exception e) {
			logger.error("请求lsp服务器失败", e);
			result.set(2611, "请求lsp服务器失败");
		}
		return JSON.toJSONString(result);
	}

	/**
	 * @param userid
	 * @param account
	 * @param vcode
	 * @return
	 * @Description: (校验验证码)
	 * @Title: verify
	 * @date 2015年11月2日 下午4:27:03
	 * @author lhj
	 */
	@RequestMapping(value = "/mail/verify/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String verify(Long userid, String account, String vcode) {
		// 初始化
		ApiResult result = new ApiResult();
		result.setCode(0);
		HttpClient httpCLient = HttpClientBuilder.create().build();

		// 数据验证
		String mailType = mailType(account);
		if (null == mailType) {
			result.set(2601, "不支持此邮箱");
			return JSON.toJSONString(result);
		}

		// 业务实现
		if (!"sina".equals(mailType)) {
			account = account.substring(0, account.indexOf("@"));
		}
		// 创建get请求实例
		StringBuffer url = new StringBuffer(appConfig.spiderUrl);
		// HttpGet httpget = new
		// HttpGet(url.append("/api/mail/").append(mailType)
		// .append("/verify/1.0?userid=").append(userid).append("&vcode=")
		// .append(vcode).toString());
		// HttpResponse response;
		try {
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("userid", String.valueOf(userid));
			paramMap.put("vcode", vcode);
			result = JSON.parseObject(
					HttpClientUtils.getInstances().doPost(
							url.append("/api/mail/").append(mailType)
									.append("/verify/1.0").toString(), "utf-8",
							paramMap), ApiResult.class);
			// if (map.get("code") == null)
			// result.set(-1, "unknown");
			// else
			// result.set(Integer.valueOf(map.get("code").toString()), map
			// .get("message") == null ? "" : map.get("message")
			// .toString());
			// result.setData(map.get("data"));
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
			result.set(2606, "clientprotocol异常");
		} catch (IOException e) {
			logger.error("IOException", e);
			result.set(2607, "io异常");
		} catch (JSONException e) {
			logger.error("JSONException", e);
			result.set(2608, "json解析错误");
		} catch (Exception e) {
			logger.error("请求lsp服务器失败", e);
			result.set(2611, "请求lsp服务器失败");
		}
		return JSON.toJSONString(result);
	}

	/**
	 * @param userid
	 * @param pwdalone
	 * @return
	 * @Description: (验证独立密码)
	 * @Title: pwdalone
	 * @date 2015年11月2日 下午5:09:17
	 * @author lhj
	 */
	@RequestMapping(value = "/mail/pwdalone/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String pwdalone(Long userid, String account, String pwdalone) {
		// 初始化
		ApiResult result = new ApiResult();
		result.setCode(0);
		HttpClient httpCLient = HttpClientBuilder.create().build();

		// 数据验证
		String mailType = mailType(account);
		if (null == mailType) {
			result.set(2601, "不支持此邮箱");
			return JSON.toJSONString(result);
		}

		// 业务实现
		// 获取缓存
		String key = String.format(USER_MAIL_KEY, userid, account);
		String objStr = redis.getStringByKey(key);
		UserMail userMail = null;
		// 如果缓存为空，则从数据库中获取
		if (StringUtils.isEmpty(objStr)) {
			userMail = getUserMail(userid, account);
			if (userMail == null) {
				userMail = new UserMail();
				userMail.setUserId(userid);
				userMail.setEmail(account);
				userMail.setEmailPasswordAlone(pwdalone);
			}
		} else
			userMail = JSON.parseObject(objStr, UserMail.class);
		try {
			userMail.setEmailPasswordAlone(AESCryptoUtil.encrypt(pwdalone));
		} catch (CryptoException e) {
			logger.error("encrypt pwdalone error:{}", pwdalone);
		}
		redis.set(key, JSON.toJSONString(userMail));

		StringBuffer url = new StringBuffer(appConfig.spiderUrl);
		// HttpGet httpget = new HttpGet(url
		// .append("/api/mail/qq/pwdalone/1.0?userid=").append(userid)
		// .append("&pwdalone=").append(pwdalone).toString());
		//
		// HttpResponse response;
		try {
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("userid", String.valueOf(userid));
			paramMap.put("pwdalone", pwdalone);
			result = JSON.parseObject(
					HttpClientUtils.getInstances().doPost(
							url.append("/api/mail/qq").append("/pwdalone/1.0")
									.toString(), "utf-8", paramMap),
					ApiResult.class);
			// if (map.get("code") == null)
			// result.set(-1, "unknown");
			// else
			// result.set(Integer.valueOf(map.get("code").toString()), map
			// .get("message") == null ? "" : map.get("message")
			// .toString());
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
			result.set(2606, "clientprotocol异常");
		} catch (IOException e) {
			logger.error("IOException", e);
			result.set(2607, "io异常");
		} catch (JSONException e) {
			logger.error("JSONException", e);
			result.set(2608, "json解析错误");
		} catch (Exception e) {
			logger.error("请求lsp服务器失败", e);
			result.set(2611, "请求lsp服务器失败");
		}
		return JSON.toJSONString(result);
	}

	/**
	 * @param userid
	 * @return
	 * @Description: (将邮件解析结果保存到数据库)
	 * @Title: checkResult
	 * @date 2015年11月2日 下午5:10:51
	 * @author lhj
	 */
	@RequestMapping(value = "/mail/checkresult/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String checkResult(Long userid, String account, Long type) {
		// 初始化
		ApiResult result = new ApiResult();
		result.setCode(0);
		HttpClient httpCLient = HttpClientBuilder.create().build();

		// 数据验证
		String mailType = mailType(account);
		if (null == mailType) {
			result.set(2601, "不支持此邮箱");
			return JSON.toJSONString(result);
		}

		// 业务实现
		// 获取缓存
		String key = String.format(USER_MAIL_KEY, userid, account);
		String objStr = redis.getStringByKey(key);
		UserMail userMail;
		// 如果缓存为空，则从数据库中获取
		if (StringUtils.isEmpty(objStr)) {
			userMail = getUserMail(userid, account);
			if (userMail == null) {
				userMail = new UserMail();
				userMail.setUserId(userid);
				userMail.setEmail(account);
			}
		} else
			userMail = JSON.parseObject(objStr, UserMail.class);

		redis.set(key, JSON.toJSONString(userMail));

		// 创建get请求实例
		StringBuffer url = new StringBuffer(appConfig.spiderUrl);
		// HttpGet httpget = new HttpGet(url
		// .append("/api/mail/" + mailType + "/checkresult/1.0?userid=")
		// .append(userid).toString());
		//
		// HttpResponse response;
		try {
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("userid", String.valueOf(userid));
			String postResult = HttpClientUtils.getInstances().doPost(
					url.append("/api/mail/").append(mailType)
							.append("/checkresult/1.0").toString(), "utf-8",
					paramMap);
			logger.debug("get result: {}", postResult);

			result = JSON.parseObject(postResult, ApiResult.class);

			// Object oCode = map.get("code");
			// if (oCode == null) {
			// result.set(-1, "unknown");
			// return JSON.toJSONString(result);
			// } else
			// result.set(Integer.valueOf(map.get("code").toString()), map
			// .get("message") == null ? "" : map.get("message")
			// .toString());
			// String code = oCode.toString();
			if (1024 == result.getCode()) {
				// String data = map.get("data").toString();
				// List<UserCardResult> userCardResultList =
				// importIntegral(data,
				// userid);
				List<UserCardResult> userCardResultList = importBill(result
						.getData().toString(),account);
				if (userCardResultList != null) {
					if (userCardResultList.size() == 0)
						result.setMessage("没有新的账单");
					userMail.setGmtLastUpdate(new Date());
					userMail.setFlag(1);
					// 赠送积分
//					try {
//						Map resMap = rechargeService.gift(userid, 1, "1", type);
//						logger.info("用户{}首次导入账单赠送{}财币",userid,resMap.get("giftIntegral"));
//					} catch (Exception e) {
//						logger.info("gift  error: {}", e);
//					}
					if (userMail.getId() == null) {
						userMail.setStatus(1);
						userMailService.insert(userMail);
						redis.set(key, JSON.toJSONString(userMail));
					} else {
						userMail.setStatus(1);
						userMailService.updateByPrimaryKeySelective(userMail);
					}
				}
				result.setData(userCardResultList);
			}
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
			result.set(2606, "clientprotocol异常");
		} catch (IOException e) {
			logger.error("IOException", e);
			result.set(2607, "io异常");
		} catch (JSONException e) {
			logger.error("JSONException", e);
			result.set(2608, "json解析错误");
		} catch (Exception e) {
			logger.error("请求lsp服务器失败", e);
			result.set(2611, "请求lsp服务器失败");
		}
		return JSON.toJSONString(result);
	}

	/**
	 * @param userid
	 * @param account
	 * @return
	 * @Description: (刷新积分)
	 * @Title: refresh
	 * @date 2015年11月4日 下午9:10:37
	 * @author lhj
	 */
	@RequestMapping(value = "/mail/refresh/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String refresh(Long userid, String account, String cookie) {
		// 初始化
		ApiResult result = new ApiResult();
		result.setCode(0);
		HttpClient httpCLient = HttpClientBuilder.create().build();
		String mailType = mailType(account);

		// 业务实现
		String key = String.format(USER_MAIL_KEY, userid, account);
		redis.del(key);
		UserMail userMail = getUserMail(userid, account);
		if (userMail == null) {
			userMail = new UserMail();
			userMail.setUserId(userid);
			userMail.setEmail(account);
		}
		if (!"sina".equals(mailType)) {
			account = account.substring(0, account.indexOf("@"));
		}
		// 登录
		try {
			StringBuffer url = new StringBuffer(appConfig.spiderUrl);
			// HttpGet httpget = new HttpGet(
			// url.append("/api/mail/")
			// .append(mailType)
			// .append("/login/1.0?userid=")
			// .append(userid)
			// .append("&account=")
			// .append(account)
			// .append("&password=")
			// .append(StringUtils.isEmpty(userMail
			// .getEmailPassword()) ? "" : AESCryptoUtil
			// .decrypt(userMail.getEmailPassword()))
			// .append("&pwdalone=")
			// .append(StringUtils.isEmpty(userMail
			// .getEmailPasswordAlone()) ? ""
			// : AESCryptoUtil.decrypt(userMail
			// .getEmailPasswordAlone()))
			// .append("&date=")
			// .append(userMail.getGmtLastUpdate().getTime())
			// .toString());
			// response = httpCLient.execute(httpget);
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("userid", String.valueOf(userid));
			paramMap.put("account", account);
			paramMap.put("cookie", cookie);
			paramMap.put(
					"password",
					StringUtils.isEmpty(userMail.getEmailPassword()) ? ""
							: AESCryptoUtil.decrypt(userMail.getEmailPassword()));
			paramMap.put(
					"pwdalone",
					StringUtils.isEmpty(userMail.getEmailPasswordAlone()) ? ""
							: AESCryptoUtil.decrypt(userMail
									.getEmailPasswordAlone()));
			if (userMail.getGmtLastUpdate() != null)
				paramMap.put("date",
						String.valueOf(userMail.getGmtLastUpdate().getTime()));

			result = JSON.parseObject(
					HttpClientUtils.getInstances().doPost(
							url.append("/api/mail/").append(mailType)
									.append("/login/1.0").toString(), "utf-8",
							paramMap), ApiResult.class);
			// if (map.get("code") == null)
			// result.set(-1, "unknown");
			// else
			// result.set(Integer.valueOf(map.get("code").toString()), map
			// .get("message") == null ? "" : map.get("message")
			// .toString());
			//
			// result.setData(map.get("data") == null ? "" : map.get("data")
			// .toString());
		} catch (CryptoException e) {
			logger.error("decrypt password{" + userMail.getEmailPassword()
					+ "} error", e);
			result.set(2602, "邮箱登录失败");
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
			result.set(2602, "邮箱登录失败");
		} catch (IOException e) {
			logger.error("IOException", e);
			result.set(2602, "邮箱登录失败");
		} catch (JSONException e) {
			logger.error("JSONException", e);
			result.set(2602, "邮箱登录失败");
		} catch (Exception e) {
			logger.error("请求lsp服务器失败", e);
			result.set(2611, "请求lsp服务器失败");
		}
		return JSON.toJSONString(result);
	}

	private List<UserCardResult> importBill(String data,String email) {

		// 初始化
		Map<String, BillResult> map = JSON.parseObject(data,
				new TypeReference<Map<String, BillResult>>() {
				});
		List<UserCardResult> returnList = new ArrayList<UserCardResult>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		// set result
		// Key = "1_中国银行信用卡_李华君"
		// Key = "1_中国银行信用卡_李华君_卡号"
		// Key = "1_艺龙_账号"

		for (Entry<String, BillResult> entry : map.entrySet()) {
			String[] attrs = entry.getKey().split("_");
			BillResult billResult = entry.getValue();

			UserCard userCard = new UserCard();
			// 查询条件
			userCard.setUserId(Long.valueOf(billResult.getUserId()));
			userCard.setName(billResult.getName());
			userCard.setCardTypeId(Long.valueOf(billResult.getCardTypeId()));
			// 其它属性
			calendar.setTime(billResult.getBillMonth());
			userCard.setBillMonth(billResult.getBillMonth());
			userCard.setCardName(billResult.getCardName());
			int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			userCard.setBillDay(dayOfMonth);
			if (attrs.length == 4) {
				userCard.setCardNo(attrs[3]);
			}

			// 从数据库中获取卡片
			List<UserCard> userCardList = userCardService
					.selectByAttrs(userCard);
			UserCard userCardDB = null;
			if (userCardList == null || userCardList.size() == 0) {
				userCard.setCardNo(billResult.getCardNo());

				if (null == billResult.getIntegral()) {
					userCard.setIntegralBalance(-1);
				} else {
					userCard.setIntegralBalance(Integer.valueOf(billResult
							.getIntegral().toString()));
				}
				userCard.setEmail(email);//20160225 add by chencheng 增加邮箱记录
				logger.debug("card no: {}", billResult.getCardNo());
				userCardService.insert(userCard);// 入库
			} else {
				userCardDB = userCardList.get(0);
				if (userCardDB.getBillMonth().getTime() >= userCard
						.getBillMonth().getTime())
					continue;
				if (!billResult.isAlone()) {
					if (StringUtils.isEmpty(userCardDB.getCardNo()))
						userCardDB.setCardNo(billResult.getCardNo());
					else {
						if (!userCardDB.getCardNo().equals(
								billResult.getCardNo())) {
							userCardDB.setCardNo(userCardDB.getCardNo() + ","
									+ billResult.getCardNo());
						}
					}
				}
				if (null == billResult.getIntegral()) {
					userCardDB.setIntegralBalance(-1);
				} else {
					userCardDB.setIntegralBalance(Integer.valueOf(billResult
							.getIntegral().toString()));
				}
				userCardDB.setBillMonth(userCard.getBillMonth());
				userCardDB.setEmail(email);//20160225 add by chencheng 增加邮箱记录
				userCardService.updateByUserCard(userCardDB);
			}
			UserCardResult userCardResult = new UserCardResult();
			CardType cardType = cardTypeService.selectByPrimaryKey(Long
					.valueOf(billResult.getCardTypeId()));
			userCardResult.setBankId(cardType.getId().toString());
			userCardResult.setBankName(cardType.getName());
			userCardResult.setBillDay(format.format(billResult.getBillMonth()));
			userCardResult
					.setCardNo(StringUtils.isEmpty(billResult.getCardNo()) ? ""
							: billResult.getCardNo());
			userCardResult.setName(billResult.getName());
			returnList.add(userCardResult);
		}
		return returnList;

	}

	/**
	 * @param data
	 * @param userid
	 * @return
	 * @Description: (保存积分信息)
	 * @Title: importIntegral
	 * @date 2015年11月2日 下午5:44:18
	 * @author lhj
	 */
	private List<UserCardResult> importIntegral(String data, Long userid) {

		// 初始化
		Map<String, List<UserCardVo>> map = JSON.parseObject(data,
				new TypeReference<Map<String, List<UserCardVo>>>() {
				});
		List<UserCardResult> returnList = new ArrayList<UserCardResult>();

		if (map.isEmpty())
			return returnList;

		List<UserCardVo> aloneList = map.get("ALONE");// 独立积分的卡片
		List<UserCardVo> unionList = map.get("UNION");// 合并积分的卡片

		// 获取所有卡片类型
		Map<String, Long> cardTypeMap = new HashMap<String, Long>();
		List<CardType> cardTypeList = cardTypeService.selectAll();
		for (CardType cardType : cardTypeList) {
			cardTypeMap.put(cardType.getName(), cardType.getId());
		}

		//
		for (UserCardVo userCardVo : aloneList) {
			UserCard userCard = userCardVo.getUserCard();
			userCard.setCardTypeId(cardTypeMap.get(userCard.getCardName()));
			if (userCard.getCardTypeId() == null)
				continue;
			userCard.setUserId(userid);
			List<UserCard> userCards = userCardService
					.selectByConditionsExt(userCard);
			updateDBIntegrals(userCardVo, userCards, returnList);
		}
		//
		for (UserCardVo userCardVo : unionList) {
			UserCard userCard = userCardVo.getUserCard();
			userCard.setCardTypeId(cardTypeMap.get(userCard.getCardName()));
			if (userCard.getCardTypeId() == null)
				continue;
			userCard.setUserId(userid);
			List<UserCard> userCards = userCardService
					.selectByConditions(userCard);
			updateDBIntegrals(userCardVo, userCards, returnList);
		}

		return returnList;
	}

	// 更新数据库中卡片的积分
	private void updateDBIntegrals(UserCardVo userCardVo,
			List<UserCard> userCards, List<UserCardResult> returnList) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		UserCard userCard = userCardVo.getUserCard();
		if (userCards.size() > 0) {
			for (UserCard dbUserCard : userCards) {
				if (null == dbUserCard.getBillMonth()
						|| dbUserCard.getBillMonth().before(
								userCard.getBillMonth())) {
					userCard.setId(dbUserCard.getId());
					// userCardService.updateByUserCard(card);
					userCardService.updateByPrimaryKey(userCard);

					UserCardResult userCardResult = new UserCardResult();
					userCardResult.setBankId(userCard.getCardTypeId()
							.toString());
					userCardResult.setBankName(userCard.getCardName());
					userCardResult.setBillDay(format.format(userCard
							.getBillMonth()));
					userCardResult.setCardNo(userCard.getCardNo());
					userCardResult.setName(userCard.getName());
					returnList.add(userCardResult);
					userCardBackup(userCard);
				} else if (dbUserCard.getBillMonth().equals(
						userCard.getBillMonth())
						|| dbUserCard.getBillMonth().after(
								userCard.getBillMonth())) {
					break;
				} else {
					userCardService.insert(userCard);

					UserCardResult userCardResult = new UserCardResult();
					userCardResult.setBankId(userCard.getCardTypeId()
							.toString());
					userCardResult.setBankName(userCard.getCardName());
					userCardResult.setBillDay(format.format(userCard
							.getBillMonth()));
					userCardResult.setCardNo(userCard.getCardNo());
					userCardResult.setName(userCard.getName());
					returnList.add(userCardResult);
					userCardBackup(userCard);
				}
			}
		} else {
			userCardService.insert(userCard);
			UserCardResult userCardResult = new UserCardResult();
			userCardResult.setBankId(userCard.getCardTypeId().toString());
			userCardResult.setBankName(userCard.getCardName());
			userCardResult.setBillDay(format.format(userCard.getBillMonth()));
			userCardResult.setCardNo(userCard.getCardNo());
			userCardResult.setName(userCard.getName());
			returnList.add(userCardResult);
			userCardBackup(userCard);
		}
		// 6.保存积分到期（中国、中信、浦发、平安）
		if (null != userCardVo.getCardIntegrals()
				&& userCardVo.getCardIntegrals().size() > 0
				&& null != userCard.getId()) {
			saveIntegralExpire(userCard.getId(), userCardVo.getCardIntegrals());
		}
	}

	// 备份userCard数据
	private void userCardBackup(UserCard userCard) {
		UserCardBackup userCardBackup = new UserCardBackup();
		userCardBackup.setCardId(userCard.getId());
		userCardBackup.setIntegral(userCard.getIntegralBalance());
		userCardBackup.setBillDay(userCard.getBillDay());
		userCardBackup.setBillMonth(userCard.getBillMonth());
		userCardBackupService.insert(userCardBackup);
	}

	private void saveIntegralExpire(Long cardId,
			List<CardIntegral> cardIntegrals) {
		// 6.1先删除以前的数据
		cardIntegralService.deleteByCardId(cardId);
		// 6.2在插入到数据库
		for (int j = 0; j < cardIntegrals.size(); j++) {
			CardIntegral cardIntegral = cardIntegrals.get(j);
			cardIntegral.setCardId(cardId);
			cardIntegralService.insert(cardIntegral);
		}
	}

	/**
	 * @param account
	 * @return
	 * @Description: (获取邮箱类型)
	 * @Title: mailType
	 * @date 2015年11月4日 下午4:19:22
	 * @author lhj
	 */
	private String mailType(String account) {

		account = account.toLowerCase();
		switch (account.substring(account.indexOf("@") + 1)) {
		case "qq.com":
			return "qq";
		case "163.com":
			return "163";
		case "126.com":
			return "126";
		case "139.com":
			return "139";
		case "sina.cn":
		case "sina.com":
			return "sina";
		case "hotmail.com":
			return "hotmail";

		default:
			return null;
		}
	}

	/**
	 * @param userid
	 * @param email
	 * @return
	 * @Description: (获取用户邮箱)
	 * @Title: getUserMail
	 * @date 2015年11月4日 下午4:22:13
	 * @author lhj
	 */
	private UserMail getUserMail(Long userid, String email) {
		UserMail userMail = new UserMail();
		userMail.setUserId(userid);
		userMail.setEmail(email);
		return userMailService.selectByUserIdAndMail(userMail);
	}

	public static void main(String[] args) {
		try {
			System.out.println(AESCryptoUtil.encrypt("Password2"));
			System.out.println(AESCryptoUtil.encrypt("caitu123"));
		} catch (CryptoException e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * @param userid
	 * @return
	 * @Description: (将邮件解析结果保存到数据库)
	 * @Title: checkResult
	 * @date 2015年11月2日 下午5:10:51
	 * @author lhj
	 */
	@RequestMapping(value = "/mail/checkresult/2.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String checkResult2(Long userid, String account, Long type) {
		// 初始化
		ApiResult result = new ApiResult();
		result.setCode(0);
		HttpClient httpCLient = HttpClientBuilder.create().build();

		// 数据验证
		String mailType = mailType(account);
		if (null == mailType) {
			result.set(2601, "不支持此邮箱");
			return JSON.toJSONString(result);
		}

		// 业务实现
		// 获取缓存
		String key = String.format(USER_MAIL_KEY, userid, account);
		String objStr = redis.getStringByKey(key);
		UserMail userMail;
		// 如果缓存为空，则从数据库中获取
		if (StringUtils.isEmpty(objStr)) {
			userMail = getUserMail(userid, account);
			if (userMail == null) {
				userMail = new UserMail();
				userMail.setUserId(userid);
				userMail.setEmail(account);
			}
		} else
			userMail = JSON.parseObject(objStr, UserMail.class);

		redis.set(key, JSON.toJSONString(userMail));

		// 创建get请求实例
		StringBuffer url = new StringBuffer(appConfig.spiderUrl);
		// HttpGet httpget = new HttpGet(url
		// .append("/api/mail/" + mailType + "/checkresult/1.0?userid=")
		// .append(userid).toString());
		//
		// HttpResponse response;
		try {
			Map<String, String> paramMap = new HashMap<>();
			paramMap.put("userid", String.valueOf(userid));
			String postResult = HttpClientUtils.getInstances().doPost(
					url.append("/api/mail/").append(mailType)
							.append("/checkresult/1.0").toString(), "utf-8",
					paramMap);
			logger.debug("get result: {}", postResult);

			result = JSON.parseObject(postResult, ApiResult.class);

			// Object oCode = map.get("code");
			// if (oCode == null) {
			// result.set(-1, "unknown");
			// return JSON.toJSONString(result);
			// } else
			// result.set(Integer.valueOf(map.get("code").toString()), map
			// .get("message") == null ? "" : map.get("message")
			// .toString());
			// String code = oCode.toString();
			if (1024 == result.getCode()) {
				// String data = map.get("data").toString();
				// List<UserCardResult> userCardResultList =
				// importIntegral(data,
				// userid);
				List<ManualResult> userCardResultList = importBill2(result
						.getData().toString(),account);
				if (userCardResultList != null) {
					if (userCardResultList.size() == 0)
						result.setMessage("没有新的账单");
					userMail.setGmtLastUpdate(new Date());
					userMail.setFlag(1);
					// 赠送积分
//					try {
//						Map resMap = rechargeService.gift(userid, 1, "1", type);
//						logger.info("用户{}首次导入账单赠送{}财币",userid,resMap.get("giftIntegral"));
//					} catch (Exception e) {
//						logger.info("gift  error: {}", e);
//					}
					if (userMail.getId() == null) {
						userMail.setStatus(1);
						userMailService.insert(userMail);
						redis.set(key, JSON.toJSONString(userMail));
					} else {
						userMail.setStatus(1);
						userMailService.updateByPrimaryKeySelective(userMail);
					}
				}
				result.setCode(0);
				result.setMessage("email");
				result.setData(userCardResultList);
			}
		} catch (ClientProtocolException e) {
			logger.error("ClientProtocolException", e);
			result.set(2606, "clientprotocol异常");
		} catch (IOException e) {
			logger.error("IOException", e);
			result.set(2607, "io异常");
		} catch (JSONException e) {
			logger.error("JSONException", e);
			result.set(2608, "json解析错误");
		} catch (Exception e) {
			logger.error("请求lsp服务器失败", e);
			result.set(2611, "请求lsp服务器失败");
		}
		return JSON.toJSONString(result);
	}

	
	private List<ManualResult> importBill2(String data,String email) {

		// 初始化
		Map<String, BillResult> map = JSON.parseObject(data,
				new TypeReference<Map<String, BillResult>>() {
				});
		List<ManualResult> returnList = new ArrayList<ManualResult>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		// set result
		// Key = "1_中国银行信用卡_李华君"
		// Key = "1_中国银行信用卡_李华君_卡号"
		// Key = "1_艺龙_账号"

		for (Entry<String, BillResult> entry : map.entrySet()) {

			Map<String, Integer> resData = new HashMap<String, Integer>();
			
			String[] attrs = entry.getKey().split("_");
			BillResult billResult = entry.getValue();

			UserCard userCard = new UserCard();
			// 查询条件
			userCard.setUserId(Long.valueOf(billResult.getUserId()));
			userCard.setName(billResult.getName());
			userCard.setCardTypeId(Long.valueOf(billResult.getCardTypeId()));
			// 其它属性
			calendar.setTime(billResult.getBillMonth());
			userCard.setBillMonth(billResult.getBillMonth());
			userCard.setCardName(billResult.getCardName());
			int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			userCard.setBillDay(dayOfMonth);
			if (attrs.length == 4) {
				userCard.setCardNo(attrs[3]);
			}

			// 从数据库中获取卡片
			List<UserCard> userCardList = userCardService
					.selectByAttrs(userCard);
			UserCard userCardDB = null;
			//结果展示
			ManualResult userCardResult = new ManualResult();
			if (userCardList == null || userCardList.size() == 0) {
				
				userCard.setCardNo(billResult.getCardNo());

				if (null == billResult.getIntegral()) {
					userCard.setIntegralBalance(-1);
					userCardResult.setResult(SysConstants.FAILURE);
					userCardResult.setRemark("未获取到积分");
				} else {
					userCard.setIntegralBalance(Integer.valueOf(billResult
							.getIntegral().toString()));
					userCardResult.setResult(SysConstants.SUCCESS);
				}
				userCard.setEmail(email);//20160225 add by chencheng 增加邮箱记录
				logger.debug("card no: {}", billResult.getCardNo());
				userCardService.insert(userCard);// 入库

				logger.info(userCard.getCardTypeId().toString()+",new:"+billResult.getIntegral());
				logger.info(userCard.getCardTypeId().toString()+",old: 0");
				
				if(null != billResult.getIntegral()){
					//计算并赠送财币
					resData = presentTubiDo(Long.valueOf(billResult.getUserId()), userCard.getCardTypeId()
							, 0, billResult.getIntegral().intValue());
				}
			} else {
				userCardDB = userCardList.get(0);
				
				logger.info(userCardDB.getCardTypeId().toString()+",new:"+billResult.getIntegral());
				logger.info(userCardDB.getCardTypeId().toString()+",old:"+userCardDB.getIntegralBalance());

				if(null != billResult.getIntegral()){
					//计算并赠送财币
					resData = presentTubiDo(Long.valueOf(billResult.getUserId()), userCardDB.getCardTypeId()
							, userCardDB.getIntegralBalance(), billResult.getIntegral().intValue());
				}
				
				
				if (userCardDB.getBillMonth().getTime() >= userCard
						.getBillMonth().getTime())
					continue;
				if (!billResult.isAlone()) {
					if (StringUtils.isEmpty(userCardDB.getCardNo()))
						userCardDB.setCardNo(billResult.getCardNo());
					else {
						if (!userCardDB.getCardNo().equals(
								billResult.getCardNo())) {
							userCardDB.setCardNo(userCardDB.getCardNo() + ","
									+ billResult.getCardNo());
						}
					}
				}
				if (null == billResult.getIntegral()) {
					userCardDB.setIntegralBalance(-1);
					userCardResult.setResult(SysConstants.FAILURE);
					userCardResult.setRemark("未获取到积分");
				} else {
					userCardDB.setIntegralBalance(Integer.valueOf(billResult
							.getIntegral().toString()));
					userCardResult.setResult(SysConstants.SUCCESS);
				}
				userCardDB.setBillMonth(userCard.getBillMonth());
				userCardDB.setEmail(email);//20160225 add by chencheng 增加邮箱记录
				userCardService.updateByUserCard(userCardDB);
			}
			CardType cardType = cardTypeService.selectByPrimaryKey(Long
					.valueOf(billResult.getCardTypeId()));
			/*userCardResult.setBankId(cardType.getId().toString());
			userCardResult.setBankName(cardType.getName());
			userCardResult.setBillDay(format.format(billResult.getBillMonth()));
			userCardResult
					.setCardNo(StringUtils.isEmpty(billResult.getCardNo()) ? ""
							: billResult.getCardNo());
			userCardResult.setName(billResult.getName());*/
			
			Bank bank = bankMapper.selectByPrimaryKey(cardType.getId().intValue());
			
			userCardResult.setCardTypeName(cardType.getName());
			userCardResult.setCardNo(StringUtils.isEmpty(billResult.getCardNo()) 
					? "" : billResult.getCardNo());
			userCardResult.setPicUrl(appConfig.staticUrl + bank.getPicUrl());
			userCardResult.setUserName(billResult.getName());
			userCardResult.setAddIntegral(null == resData.get("addIntegral") ? "0" : String.valueOf(resData.get("addIntegral")));
			userCardResult.setAddTubi(null == resData.get("addTubi") ? "0" : String.valueOf(resData.get("addTubi")));
			
			returnList.add(userCardResult);
		}
		return returnList;

	}
	
	
	/**
	 * 
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: presentTubiDo 
	 * @param userid
	 * @param manualId
	 * @param oldIntegral
	 * @param newIntegral
	 * @return	addIntegral	addTubi	
	 * @date 2016年5月13日 下午2:43:07  
	 * @author ws
	 */
	private Map<String,Integer> presentTubiDo(Long userId, Long cardTypeId, Integer oldIntegral, Integer newIntegral){
		
		Integer addIntegral = newIntegral > oldIntegral ? newIntegral - oldIntegral : 0;

		CardType cardType = cardTypeService.selectByPrimaryKey(cardTypeId);
		ExchangeRule exRule = exchangeRuleService.findByCardTypeName(cardType.getName());

		Map<String,Integer> addInfo = new HashMap<String, Integer>();
		addInfo.put("addIntegral", addIntegral);
		if(!appConfig.tubiCardTypeIds.contains(","+cardTypeId+",")){//不支持双倍积分

			//计算财币  向上取整
			Double addTubi = Math.ceil(addIntegral * (null == exRule.getScale() ? 1 : exRule.getScale()));
			addInfo.put("addTubi", addTubi.intValue());
			
			if(0 != addIntegral){
				TransactionRecordDto transactionRecordDto = new TransactionRecordDto();
				transactionRecordDto.setOrderNo("");
				transactionRecordDto.setChannel(4);//赠送
				transactionRecordDto.setComment("双倍积分赠送");
				transactionRecordDto.setInfo("活动");//
				transactionRecordDto.setSource(2);//活动
				transactionRecordDto.setTotal(0L);
				transactionRecordDto.setTransactionNumber(XStringUtil.createSerialNoWithRandom(
						"HD", String.valueOf(userId)));
				transactionRecordDto.setTubi(addTubi.longValue());
				transactionRecordDto.setType(5);//累积
				transactionRecordDto.setUserId(userId);
				// 添加交易记录
				Long recordId = transactionRecordService.saveTransaction(transactionRecordDto);
				// 添加交易明细
				accountDetailService.saveAccountDetailTubi(recordId, transactionRecordDto,
						3);//3  入币
				
				Account account = accountService.selectByUserId(userId);
				// 更新账户
				accountService.updateAccount(account, transactionRecordDto, 1L);
			}else{
				addInfo.put("addTubi", 0);
			}
		}
		

		
		return addInfo;
	}
	

}
