package com.caitu99.service.user.controller.api;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.integral.domain.Bank;
import com.caitu99.service.integral.service.BankService;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.user.controller.vo.IntegralVo;
import com.caitu99.service.user.controller.vo.UserVO;
import com.caitu99.service.user.controller.vo.WeixinFenPKVo;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.user.domain.UserMail;
import com.caitu99.service.user.domain.UserPushInfo;
import com.caitu99.service.user.domain.UserThirdInfo;
import com.caitu99.service.user.domain.WeixinFenPK;
import com.caitu99.service.user.service.UserAuthService;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.user.service.UserMailService;
import com.caitu99.service.user.service.UserPushInfoService;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.user.service.UserThirdInfoService;
import com.caitu99.service.user.service.WeixinFenPKService;
import com.caitu99.service.utils.ApiResultCode;
import com.caitu99.service.utils.LoginType;
import com.caitu99.service.utils.LoginType2EnumUtils;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import com.caitu99.service.utils.result.ApiResultStatus;
import com.caitu99.service.utils.string.StrUtil;
import com.caitu99.service.utils.unionpay.UnionOpens;

@Controller
@RequestMapping("/api/user")
public class UserController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(UserController.class);

	@Autowired
	private RedisOperate redis;

	@Autowired
	private UserService userService;

	@Autowired
	private UserCardService userCardService;

	@Autowired
	private UserMailService userMailService;

	@Autowired
	private UserThirdInfoService userThirdInfoService;

	@Autowired
	private WeixinFenPKService weixinFenPKService;

	@Autowired
	private UserAuthService userAuthService;

	@Autowired
	private AppConfig appConfig;

	@Autowired
	private UserPushInfoService userPushInfoService;
	
	@Autowired
	private BankService bankService;
	
	@Autowired
	private UserCardManualService userCardManualService;

	/**
	 * 用户登陆
	 * 
	 * @param user
	 *            user
	 * @param vcode
	 *            vcode
	 * @param request
	 *            request
	 * @return result
	 */
	@RequestMapping("/login/1.0")
	public String login(User user, String vcode, HttpServletRequest request) {

		ApiResult<UserVO> result = new ApiResult<>();

		String mobile = user.getMobile();

		user.setMobile(mobile);
		if (!user.getMobile().equals("13588888888")
				&& !user.getMobile().equals("13688888888")
				&& !user.getMobile().equals("13788888888")) {

			if (StringUtils.isEmpty(user.getMobile())) {
				result.setCode(2001);
				result.setMessage("手机号不能为空");
				return JSON.toJSONString(result);
			}

			String key = String.format(RedisKey.SMS_SEND_KEY, mobile);
			String sessionCode = redis.getStringByKey(key);
			if (StringUtils.isEmpty(sessionCode)) {
				result.setCode(2002);
				result.setMessage("请先获取验证码");
				logger.debug("get vcode first: {}", mobile);
				return JSON.toJSONString(result);
			}

			if (StringUtils.isEmpty(vcode)) {
				result.setCode(2003);
				result.setMessage("验证码不能为空码");
				logger.debug("vcode can not be empty: {}", mobile);
				return JSON.toJSONString(result);
			}

			if (!sessionCode.equals(vcode)) {
				result.setCode(2004);
				result.setMessage("验证码错误码");
				logger.debug("error vcode: {}", mobile);
				return JSON.toJSONString(result);
			}
		}

		redis.del(String.format(RedisKey.SMS_SEND_KEY, mobile));

		if (null == userService.isExistMobile(user)) {// 如果用户不存在则创建用户
			user.setStatus(1);
			userService.regist(user);
		}

		// 业务实现
		User loginUser = userService.login(user);

		// 结果返回
		if (null == loginUser) {
			result.setCode(2005);
			result.setMessage("登录失败，手机号或密码错误");
			logger.debug("login failure: {}", mobile);
			return JSON.toJSONString(result);
		} else {
			if (loginUser.getPaypass() == null) {
				loginUser.setPaypass("0");
			} else {
				loginUser.setPaypass("1");
			}
			UserVO uservo = new UserVO();
			uservo.setUser(loginUser);
			Long total = userCardService.total(loginUser.getId());
			List<UserMail> usermails = userMailService.selectByUserId(loginUser
					.getId());
			uservo.setMailsize((long) usermails.size());
			uservo.setTotal(total);

			if (StringUtils.isEmpty(loginUser.getImgurl())) {
				UserThirdInfo userThirdInfo = userThirdInfoService
						.selectByUserId(loginUser.getId());
				userService.setUserImageUrl(loginUser, userThirdInfo);
			}

			result.setCode(0);
			result.setMessage("登录成功");
			result.setData(uservo);
			logger.debug("login success: {}", mobile);
			return JSON.toJSONString(result);
		}
	}

	/**
	 * 更新用户信息
	 * 
	 * @param user
	 *            user
	 * @return api result
	 */
	@RequestMapping("/update/1.0")
	@ResponseBody
	public String updateUserInfo(User user) {
		ApiResult<Object> result = new ApiResult<Object>();

		// 数据验证
		if (null == user.getId()) {
			return result.toJSONString(2008, "用户id不能为空");
		}

		// UserThirdInfo userThirdInfo = new UserThirdInfo();
		// encodeUtf8mb4Str(user, null);

		int status = userService.updateUserInfo(user);
		if (status == 1) {
			result.setCode(0);
			result.setMessage("用户信息收集并完善成功");
			logger.debug("update user success: {}", user.getId());
		} else {
			result.setCode(2009);
			result.setMessage("用户信息收集并完善失败");
			logger.debug("update user failure: {}", user.getId());
		}
		return JSON.toJSONString(result);
	}

	/**
	 * 密码重置(该接口没有前台对接功能)
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: resetPassword
	 * @param user
	 * @param userid
	 * @return
	 * @date 2015年11月4日 上午10:12:36
	 * @author ws
	 */
	@RequestMapping("/password/reset/1.0")
	@ResponseBody
	public String resetPassword(User user, Long userid) {
		ApiResult<Object> result = new ApiResult<Object>();

		if (StringUtils.isEmpty(user.getPassword())) {
			result.setCode(2013);
			result.setMessage("密码不能为空");
			logger.debug("empty password: {}", userid);
			return JSON.toJSONString(result);
		}

		if (!user.getPassword().equals(user.getRePassword())) {
			result.setCode(2014);
			result.setMessage("两次密码不一致");
			logger.debug("incompatible password: {}", userid);
			return JSON.toJSONString(result);
		}

		user.setId(userid);
		int status = userService.resetPassword(user);

		if (status == 0) {
			result.setCode(2015);
			result.setMessage("密码重设失败");
			logger.info("reset password failure: {}", userid);
		} else {
			result.setCode(0);
			result.setMessage("密码重设成功");
			logger.debug("reset password success: {}", userid);
		}
		return JSON.toJSONString(result);
	}

	/**
	 *
	 * @param user
	 * @param userid
     * @return
     */
	@RequestMapping("/password/check/1.0")
	@ResponseBody
	public String checkOriginalPassword(String orginPassword, Long userid) {
		ApiResult<Object> result = new ApiResult<Object>();

		if (StringUtils.isEmpty(orginPassword)){
			result.set(2013,"密码不能为空");
			logger.debug("empty password: {}", userid);
			return JSON.toJSONString(result);
		}

		if (orginPassword.length()!=6){
			result.set(ApiResultCode.USER_PASSWORD_LENGTH,"密码长度不正确");
			logger.debug("password length not correct: {}", userid);
			return JSON.toJSONString(result);
		}

		int status = userService.checkOriginalPassword(orginPassword,userid);

		if (status==1)return result.toJSONString(-1,"用户不存在");
		if (status==2)return result.toJSONString(-2,"密码错误");

		result.set(status,"密码检测成功");

		return JSON.toJSONString(result);
	}



	/**
	 * 第三方信息获取并进行字符集转换
	 * 
	 * @Description: (方法职责详细描述,可空)
	 * @Title: encodeUtf8mb4Str
	 * @param user
	 * @param userThirdInfo
	 * @date 2015年11月3日 下午2:54:36
	 * @author ws
	 */
	private void encodeUtf8mb4Str(User user, UserThirdInfo userThirdInfo) {
		if (null != user && null != user.getWechatNick()) {
			user.setWechatNick(encodeStringWithUtf8(user.getWechatNick()));
		}
		if (null != user && null != user.getWeiboNick()) {
			user.setWeiboNick(encodeStringWithUtf8(user.getWeiboNick()));
		}
		if (null != user && null != user.getQqNick()) {
			user.setQqNick(encodeStringWithUtf8(user.getQqNick()));
		}

		if (null != userThirdInfo && null != userThirdInfo.getWeixinNickname()) {
			userThirdInfo.setWeixinNickname(encodeStringWithUtf8(userThirdInfo
					.getWeixinNickname()));
		}
		if (null != userThirdInfo && null != userThirdInfo.getQqScreenName()) {
			userThirdInfo.setQqScreenName(encodeStringWithUtf8(userThirdInfo
					.getQqScreenName()));
		}
		if (null != userThirdInfo && null != userThirdInfo.getWeiboScreenName()) {
			userThirdInfo.setWeiboScreenName(encodeStringWithUtf8(userThirdInfo
					.getWeiboScreenName()));
		}
	}

	private void decodeUtf8mb4Str(User user) {
		if (null != user && null != user.getWechatNick()) {
			user.setWechatNick(decodeStringFromUtf8(user.getWechatNick()));
		}
		if (null != user && null != user.getWeiboNick()) {
			user.setWeiboNick(decodeStringFromUtf8(user.getWeiboNick()));
		}
		if (null != user && null != user.getQqNick()) {
			user.setQqNick(decodeStringFromUtf8(user.getQqNick()));
		}
	}

	public static String encodeStringWithUtf8(String str) {
		byte[] bytes = null;
		try {
			bytes = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new ApiException(-1, e.getMessage());
		}
		String ans = "";
		for (byte tbyte : bytes) {
			ans = ans + ":" + Integer.toString(tbyte & 0xff);
		}
		return ans;
	}

	public static String decodeStringFromUtf8(String ans) {
		String[] out = ans.split(":");
		byte[] result = new byte[100];
		int cnt = 0;
		for (String anOut : out) {
			if (anOut.equals("")) {
				continue;
			}
			result[cnt++] = (byte) Integer.parseInt(anOut);
		}
		try {
			return new String(result, 0, cnt, "utf-8");
		} catch (UnsupportedEncodingException e) {
			throw new ApiException(-1, e.getMessage());
		}
	}

	/**
	 * 第三方登录
	 * 
	 * @param user
	 * @param userThirdInfo
	 * @return result
	 */
	@RequestMapping("/loginThird")
	@ResponseBody
	public String loginThird(User user, UserThirdInfo userThirdInfo) {
		ApiResult<UserVO> result = new ApiResult<UserVO>();
		result.setCode(ApiResultStatus.FAILED.getValue());
		// encodeUtf8mb4Str(user, userThirdInfo);
		// 数据验证
		if (StringUtils.isEmpty(user.getOpenid())
				|| StringUtils.isEmpty(user.getZfbName())
				|| StringUtils.isEmpty(user.getQq())
				|| StringUtils.isEmpty(user.getWeibo())) {
			result.setCode(2035);
			result.setMessage("用户登录号不能为空");
			return JSON.toJSONString(result);
		}
		user = userService.loginThird(user);
		// 更改支付密码
		if (user.getPaypass() == null) {
			user.setPaypass("0");
		} else {
			user.setPaypass("1");
		}
		// 保存第三方登录的信息
		UserThirdInfo userThirdInfoByUserId = userThirdInfoService
				.selectByUserId(user.getId());
		userThirdInfo.setUserId(user.getId());
		if (null != userThirdInfoByUserId) {
			userThirdInfo.setId(userThirdInfoByUserId.getId());
			userThirdInfoService.updateByPrimaryKeySelective(userThirdInfo);
		} else {
			userThirdInfoService.insert(userThirdInfo);
		}
		// 返回登录信息
		if (StringUtils.isEmpty(user.getImgurl())) {
			userThirdInfo = userThirdInfoService.selectByUserId(user.getId());
			setUserImageUrl(user, userThirdInfo);
		}
		UserVO uservo = new UserVO();
		// encodeUtf8mb4Str(user, userThirdInfo);
		uservo.setUser(user);
		Long total = userCardService.total(user.getId());
		List<UserMail> usermails = userMailService.selectByUserId(user.getId());
		uservo.setMailsize((long) usermails.size());
		uservo.setTotal(total);
		result.setCode(0);
		result.setData(uservo);
		result.setMessage("第三方登录成功");

		// 如果用户下载app，首次登陆获取50w身价
		WeixinFenPKVo weixinFenPKVo = weixinFenPKService.selectByUserId(user
				.getId());
		if (null != weixinFenPKVo && null == weixinFenPKVo.getAwardLoginApp()) {
			WeixinFenPK weixinFenPK = new WeixinFenPK();
			weixinFenPK.setId(weixinFenPKVo.getId());
			weixinFenPK.setAwardLoginApp(500000l);
			if (null == weixinFenPKVo.getTotalIntegral()) {
				weixinFenPK.setTotalIntegral(500000l);
			} else {
				weixinFenPK
						.setTotalIntegral(weixinFenPKVo.getTotalIntegral() + 500000);
			}
			weixinFenPKService.updateByPrimaryKeySelective(weixinFenPK);
			if (null != weixinFenPKVo.getInviterid()) {
				weixinFenPK = new WeixinFenPK();
				weixinFenPK.setUserid(weixinFenPKVo.getInviterid());
				weixinFenPK.setTotalIntegral(500000l);
				weixinFenPKService.updateInvitedUserLoginApp(weixinFenPK);
			}
		}
		return JSON.toJSONString(result);
	}

	public void setUserImageUrl(User user, UserThirdInfo userThirdInfo) {
		if (null != userThirdInfo) {
			if (StringUtils.isEmpty(user.getImgurl())) {
				if (!StringUtils.isEmpty(userThirdInfo.getWeixinImgurl())) {
					user.setImgurl(userThirdInfo.getWeixinImgurl());
				} else if (!StringUtils.isEmpty(userThirdInfo
						.getQqProfileImageUrl())) {
					user.setImgurl(userThirdInfo.getQqProfileImageUrl());
				} else if (!StringUtils.isEmpty(userThirdInfo
						.getWeiboProfileImageUrl())) {
					user.setImgurl(userThirdInfo.getWeiboProfileImageUrl());
				}
			}
		}
	}

	/**
	 * @param user
	 * @param request
	 * @return
	 */
	@RequestMapping("/autologin")
	@ResponseBody
	public String autologin(User user, HttpServletRequest request) {

		String mobile = user.getMobile();
		// 初始化
		ApiResult<User> result = new ApiResult<User>();

		if (StringUtils.isEmpty(mobile)) {
			result.setCode(2006);
			result.setMessage("用户手机不能为空");
			return JSON.toJSONString(result);
		}
		user.setMobile(mobile);
		if (null == userService.isExistMobile(user)) {// 如果用户不存在则创建用户
			user.setStatus(1);
			userService.regist(user);
			if (null == user.getId()) {
				result.setCode(2016);
				result.setMessage("注册失败");
			} else {
				result.setCode(0);
				result.setMessage("登录成功");
				result.setData(user);
			}
			return JSON.toJSONString(result);
		}

		// 业务实现
		User loginUser = userService.login(user);

		// 结果返回
		if (null == loginUser) {
			result.setCode(2005);
			result.setMessage("登录失败，手机号或密码错误");
		} else {
			result.setCode(0);
			result.setMessage("登录成功");
			UserThirdInfo userThirdInfo = userThirdInfoService
					.selectByUserId(loginUser.getId());
			setUserImageUrl(loginUser, userThirdInfo);
			result.setData(loginUser);
		}
		return JSON.toJSONString(result);
	}

	/**
	 * 修改手机号码
	 * 
	 * @param mobile
	 * @param vcode
	 * @param sessionid
	 * @param request
	 * @return
	 */
	@RequestMapping("/updateMobile")
	@ResponseBody
	public String updateMobile(User user, String vcode,
			HttpServletRequest request) {

		// 初始化
		String mobile = user.getMobile();
		ApiResult<UserVO> result = new ApiResult<UserVO>();
		String sessionCode = null;
		String key = String.format(RedisKey.SMS_SEND_KEY, mobile);
		String oSessionCode = redis.getStringByKey(key);
		if (null != oSessionCode)
			sessionCode = (String) oSessionCode;

		// 数据验证
		if (StringUtils.isEmpty(mobile)) {
			result.setCode(2017);
			result.setMessage("新的手机号码不能为空");
			return JSON.toJSONString(result);
		}
		if (StrUtil.isEmpty(vcode)) {
			result.setCode(2003);
			result.setMessage("验证码不能为空");
			return JSON.toJSONString(result);
		}
		if (!sessionCode.equals(vcode)) {
			result.setCode(2004);
			result.setMessage("验证码错误");
			return JSON.toJSONString(result);
		}

		User loginUser = userService.selectByPrimaryKey(user.getId());
		User newUser = new User();
		newUser.setMobile(mobile);
		newUser = userService.selectLoginByMobile(newUser);
		int status = 0;
		if (null != newUser) {
			result.setCode(2018);
			result.setMessage("该手机号码已经绑定过了");
			return JSON.toJSONString(result);
		} else if (null != newUser
				&& StringUtils.isEmpty(loginUser.getMobile())) {
			loginUser.setId(newUser.getId());
			loginUser.setStatus(1);
			status = userService.updateUserInfo(loginUser);
		} else {
			newUser = new User();
			newUser.setStatus(1);
			newUser.setId(loginUser.getId());
			newUser.setMobile(mobile);
			status = userService.updateUserInfo(newUser);
		}
		result.setCode(0);
		if (status == 1) {
			result.setMessage("用户手机修改成功");
		} else {
			result.setMessage("用户手机修改失败");
		}
		loginUser.setMobile(mobile);
		loginUser.setStatus(newUser.getStatus());
		UserThirdInfo userThirdInfo = userThirdInfoService
				.selectByUserId(loginUser.getId());
		setUserImageUrl(loginUser, userThirdInfo);
		UserVO uservo = new UserVO();
		uservo.setUser(loginUser);
		Long total = userCardService.total(loginUser.getId());
		List<UserMail> usermails = userMailService.selectByUserId(loginUser
				.getId());
		uservo.setMailsize((long) usermails.size());
		uservo.setTotal(total);
		result.setData(uservo);
		return JSON.toJSONString(result);
	}

	/**
	 * 修改手机号码
	 * 
	 * @param mobile
	 * @param vcode
	 * @param sessionid
	 * @param request
	 * @return
	 */
	@RequestMapping("/mobile/update/1.0")
	@ResponseBody
	public String updateMobileV120(User user, String vcode, Long userid) {
		// 初始化
		ApiResult<UserVO> result = new ApiResult<UserVO>();
		// Object oSessionCode =
		// request.getSession().getAttribute(SessionKey.SESSION_CODE.getKey());
		String mobile = user.getMobile();
		// 数据验证
		if (StringUtils.isEmpty(mobile)) {
			result.setCode(2017);
			result.setMessage("新的手机号码不能为空");
			return JSON.toJSONString(result);
		}
		String key = String.format(RedisKey.SMS_SEND_KEY, mobile);
		String sessionCode = redis.getStringByKey(key);

		if (StringUtils.isEmpty(sessionCode)) {
			result.setCode(2002);
			result.setMessage("请先获取验证码");
			return JSON.toJSONString(result);
		}
		if (StringUtils.isEmpty(vcode)) {
			result.setCode(2003);
			result.setMessage("验证码不能为空");
			return JSON.toJSONString(result);
		}
		if (!sessionCode.equals(vcode)) {
			result.setCode(2004);
			result.setMessage("验证码错误");
			return JSON.toJSONString(result);
		}
		String keys = String.format(RedisKey.SMS_SEND_KEY, mobile);
		redis.del(keys);
		User loginUser = userService.selectByPrimaryKey(userid);
		User newUser = new User();
		newUser.setMobile(mobile);
		newUser = userService.selectLoginByMobile(newUser);
		int status = 0;
		if (null != newUser) {
			result.setCode(2018);
			result.setMessage("该手机号码已经绑定过了");
			return JSON.toJSONString(result);
		} else if (null != newUser
				&& StringUtils.isEmpty(loginUser.getMobile())) {
			loginUser.setId(newUser.getId());
			loginUser.setStatus(1);
			status = userService.updateUserInfo(loginUser);
		} else {
			newUser = new User();
			newUser.setStatus(1);
			newUser.setId(loginUser.getId());
			newUser.setMobile(mobile);
			status = userService.updateUserInfo(newUser);
		}
		result.setCode(0);
		if (status == 1) {
			result.setMessage("用户手机修改成功");
		} else {
			result.setMessage("用户手机修改失败");
		}
		loginUser.setMobile(mobile);
		loginUser.setStatus(newUser.getStatus());
		UserThirdInfo userThirdInfo = userThirdInfoService
				.selectByUserId(loginUser.getId());
		setUserImageUrl(loginUser, userThirdInfo);
		UserVO uservo = new UserVO();
		uservo.setUser(loginUser);
		Long total = userCardService.total(loginUser.getId());
		List<UserMail> usermails = userMailService.selectByUserId(loginUser
				.getId());
		uservo.setMailsize((long) usermails.size());
		uservo.setTotal(total);
		uservo.setPaypass(StringUtils.isBlank(loginUser.getPaypass())?false:true);
		result.setData(uservo);
		return JSON.toJSONString(result);
	}

	/**
	 * 用户注册
	 * 
	 * @param user
	 * @param vcode
	 * @param request
	 * @return
	 */
	@RequestMapping("/regist")
	@ResponseBody
	public String regist(User user, String vcode, HttpServletRequest request) {

		// 初始化
		String mobile = user.getMobile();
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		boolean results = false;
		result.setData(results);
		String key = String.format(RedisKey.SMS_SEND_KEY, mobile);
		String sessionCode = redis.getStringByKey(key);
		// 数据验证
		if (StringUtils.isEmpty(user.getMobile())) {
			result.setCode(2006);
			result.setMessage("手机号不能为空");
			return JSON.toJSONString(result);
		}
		if (null != userService.isExistMobile(user)) {
			result.setCode(2019);
			result.setMessage("该号码已注册");
			return JSON.toJSONString(result);
		}
		if (StringUtils.isEmpty(vcode)) {
			result.setCode(2003);
			result.setMessage("验证码不能为空码");
			return JSON.toJSONString(result);
		}
		if (!vcode.equals(sessionCode)) {
			result.setCode(2004);
			result.setMessage("验证码错误");
			return JSON.toJSONString(result);
		}
		// if (StrUtil.isEmpty(user.getPassword())) {
		// result.setCode(ApiResultStatus.FAILED.getValue());
		// result.setMessage("密码不能为空");
		// return JSON.toJSONString(result);
		// }
		// if (!user.getPassword().equals(user.getRePassword())) {
		// result.setCode(ApiResultStatus.FAILED.getValue());
		// result.setMessage("两次密码不一致");
		// return JSON.toJSONString(result);
		// }

		// 业务实现
		user.setStatus(1);
		userService.regist(user);
		if (null != user.getId()) {
			result.setCode(0);
			result.setMessage("注册成功");
		} else {
			result.setCode(2016);
			result.setMessage("注册失败");
		}
		return JSON.toJSONString(result);
	}

	// 用户注册
	@RequestMapping("/regist/v120")
	@ResponseBody
	public String registV120(User user, String vcode, HttpServletRequest request) {
		// 初始化
		String mobile = user.getMobile();
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		boolean results = false;
		result.setData(results);
		String key = String.format(RedisKey.SMS_SEND_KEY, mobile);
		String sessionCode = redis.getStringByKey(key);
		// 数据验证
		if (StringUtils.isEmpty(user.getMobile())) {
			result.setCode(2006);
			result.setMessage("手机号不能为空");
			return JSON.toJSONString(result);
		}
		if (null != userService.isExistMobile(user)) {
			result.setCode(2019);
			result.setMessage("该号码已注册");
			return JSON.toJSONString(result);
		}
		if (StringUtils.isEmpty(vcode)) {
			result.setCode(2003);
			result.setMessage("验证码不能为空码");
			return JSON.toJSONString(result);
		}
		if (!vcode.equals(sessionCode)) {
			result.setCode(2004);
			result.setMessage("验证码错误");
			return JSON.toJSONString(result);
		}
		// if (StrUtil.isEmpty(user.getPassword())) {
		// result.setCode(ApiResultStatus.FAILED.getValue());
		// result.setMessage("密码不能为空");
		// return JSON.toJSONString(result);
		// }
		// if (!user.getPassword().equals(user.getRePassword())) {
		// result.setCode(ApiResultStatus.FAILED.getValue());
		// result.setMessage("两次密码不一致");
		// return JSON.toJSONString(result);
		// }

		// 业务实现
		user.setStatus(1);
		userService.regist(user);
		if (null != user.getId()) {
			result.setCode(0);
			result.setMessage("注册成功");
		} else {
			result.setCode(2016);
			result.setMessage("注册失败");
		}
		return JSON.toJSONString(result);
	}

	/*	*//**
	 * 用户退出
	 * 
	 * @param sessionid
	 * @return
	 */
	/*
	 * @RequestMapping("/logoff")
	 * 
	 * @ResponseBody public String logoff(String sessionid) { // 初始化
	 * ApiResult<Boolean> result = new ApiResult<Boolean>();
	 * result.setData(false);
	 * 
	 * SessionMap.map.remove(sessionid);
	 * 
	 * result.setCode(0); result.setData(true); result.setMessage("注销成功");
	 * return JSON.toJSONString(result); }
	 */

	/**
	 * 获取用户详细信息
	 * 
	 * @param userid
	 * @return
	 */
	@RequestMapping(value = "/getDetail/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getDetail(Long userid) {
		// 初始化
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		JSONObject jsonObject = new JSONObject();
		// 业务实现
		User loginUser = userService.selectByPrimaryKey(userid);
		List<UserMail> usermails = userMailService.selectByUserId(loginUser
				.getId());
		User users = userService.selectByPrimaryKey(loginUser.getId());
		if (users.getPaypass() == null) {
			users.setPaypass("0");
		} else {
			users.setPaypass("1");
		}
		UserThirdInfo userThirdInfo = new UserThirdInfo();
		// encodeUtf8mb4Str(users, userThirdInfo);
		jsonObject.put("user", users);
		jsonObject.put("mailsize", (long) usermails.size());
		jsonObject.put("total",
				(userMailService.Convertibleintegral(loginUser.getId())));
		// 数据返回
		result.setData(jsonObject);
		result.setCode(0);
		return JSON.toJSONString(result);
	}

	/**
	 * 发送认证短信
	 *
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/sms/sendauth/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String sendAuthSms(Long userid, String mobile) {
		// 初始化
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		result.setCode(ApiResultStatus.FAILED.getValue());
		result.setData(false);

		User loginUser = userService.selectByPrimaryKey(userid);

		UserAuth userAuthTemp = userAuthService.selectByUserId(loginUser
				.getId());
		if (userAuthTemp != null) {
			result.setCode(2020);
			result.setMessage("用户已认证");
			return JSON.toJSONString(result);
		}
		if (StringUtils.isEmpty(mobile)) {
			result.setCode(2006);
			result.setMessage("手机号码不能为空");
			return JSON.toJSONString(result);
		}
		// 业务实现
		UnionOpens unionOpens = new UnionOpens();
		try {
			Map map = unionOpens.sendCode(mobile);
			String retCode = (String) map.get("retCode");
			if (!"0000".equals(retCode)) {
				result.setCode(2007);
				result.setMessage("短信发送失败");
				return JSON.toJSONString(result);
			}
		} catch (Exception e) {
			result.setCode(2007);
			result.setMessage("短信发送失败");
			return JSON.toJSONString(result);
		}

		// 数据返回
		result.setData(true);
		result.setMessage("发送成功");
		result.setCode(0);
		return JSON.toJSONString(result);
	}

	/**
	 * 身份认证
	 * 
	 * @param
	 * @param code
	 * @param userAuth
	 * @return
	 */
	@RequestMapping(value = "/user/auth/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String authUser(Long userid, String code, UserAuth userAuth) {
		// 初始化
		ApiResult<Map<String, String>> result = new ApiResult<Map<String, String>>();
		Map<String, String> resultMap = new HashMap<String, String>();

		// 数据校验

		User loginUser = userService.selectByPrimaryKey(userid);
		UserAuth userAuthTemp = userAuthService.selectByUserId(loginUser
				.getId());
		if (userAuthTemp != null) {
			result.setCode(2020);
			result.setMessage("用户已认证");
			return JSON.toJSONString(result);
		}

		List<UserAuth> listTemp = userAuthService.selectByAccId(userAuth
				.getAccId());
		if (listTemp != null && listTemp.size() >= 2) {
			result.setCode(2021);
			result.setMessage("您的实名信息已绑定，请选用已有账户操作");
			return JSON.toJSONString(result);
		}

		if (StringUtils.isEmpty(userAuth.getMobile())) {
			result.setCode(2003);
			result.setMessage("手机号码不能为空");
			return JSON.toJSONString(result);
		}
		if (StringUtils.isEmpty(userAuth.getAccId())) {
			result.setCode(2022);
			result.setMessage("持卡人身份证号码不能为空");
			return JSON.toJSONString(result);
		}
		if (StringUtils.isEmpty(userAuth.getAccName())) {
			result.setCode(2023);
			result.setMessage("持卡人姓名不能为空");
			return JSON.toJSONString(result);
		}
		if (StringUtils.isEmpty(userAuth.getCardNo())) {
			result.setCode(2024);
			result.setMessage("银行卡号不能为空");
			return JSON.toJSONString(result);
		}
		// 业务实现
		UnionOpens unionOpens = new UnionOpens();
		Map map = null;
		try {
			map = unionOpens.getCardInfo(userAuth.getCardNo());
			String retCode = (String) map.get("retCode");
			if (!"0000".equals(retCode)) {
				result.setCode(2025);
				result.setMessage("获取卡片类型失败");
				return JSON.toJSONString(result);
			}
			String cardType = (String) map.get("cardType");
			if ("贷记卡".equals(cardType)) {
				result.setCode(2026);
				result.setMessage("此卡为信用卡，请绑定储蓄卡");
				return JSON.toJSONString(result);
			}
			resultMap.put("bankName", (String) map.get("bankName"));
			resultMap.put("cardName", (String) map.get("cardName"));
		} catch (Exception e) {
			result.setCode(2027);
			result.setMessage("获取卡片类型失败");
			return JSON.toJSONString(result);
		}

		try {
			userAuth.setUserId(loginUser.getId());
			userAuth.setOrderNo(StrUtil
					.dateToString(new Date(), "yyMMddHHmmss")
					+ ((new Random()).nextInt(8999) + 1000));
			map = unionOpens.bindBankCard(userAuth.getOrderNo(),
					userAuth.getCardNo(), userAuth.getAccName(),
					userAuth.getAccId(), userAuth.getMobile(), code);
			String retCode = (String) map.get("retCode");
			if (!"0000".equals(retCode)) {
				if ("05".equals(retCode)) {
					result.setCode(2028);
					result.setMessage("身份认证失败，请检查身份信息或更换卡片");
					return JSON.toJSONString(result);
				}
				if ("502B".equals(retCode)) {
					result.setCode(2107);
					result.setMessage("短信验证不合法");
					return JSON.toJSONString(result);
				}
				if ("504B".equals(retCode)) {
					result.setCode(2108);
					result.setMessage("短信验证码不存在");
					return JSON.toJSONString(result);
				}
				result.setCode(-1);
				result.setMessage((String) map.get("retDesc"));
				return JSON.toJSONString(result);
			}
		} catch (Exception e) {
			result.setCode(2029);
			result.setMessage("绑定失败");
			return JSON.toJSONString(result);
		}
		userAuth.setBankname(resultMap.get("bankName"));
		userAuth.setBindId((String) map.get("bindId"));
		userAuthService.insert(userAuth);
		if (userAuth.getId() < 0) {
			result.setCode(2029);
			result.setMessage("绑定失败");
			return JSON.toJSONString(result);
		}
		loginUser.setIsauth(1);
		// 数据返回
		resultMap.put("lastNo", userAuth.getCardNo().substring(12));
		result.setData(resultMap);
		result.setMessage("绑定成功");
		result.setCode(0);
		return JSON.toJSONString(result);
	}

	/**
	 * 修改支付密码
	 * 
	 * @param userid
	 * @param newpaypass
	 * @param oldpaypass
	 * @return
	 * @throws CryptoException
	 */
	@RequestMapping(value = "/paypass/update/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String updatePayPass(Long userid, String newpaypass,
			String oldpaypass) throws CryptoException {

		// 初始化
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		result.setData(false);

		if (null != oldpaypass) {
			oldpaypass = AESCryptoUtil.encrypt(oldpaypass);
		}
		if (userid == null) {
			result.setCode(2008);
			result.setMessage("用户id不能为空");
			return JSON.toJSONString(result);
		}
		if (null == newpaypass || newpaypass.length() == 0) {
			result.setCode(2036);
			result.setMessage("新的支付密码不能为空");
			return JSON.toJSONString(result);
		}
		if (!StrUtil.isSixNums(newpaypass)) {
			result.setCode(2039);
			result.setMessage("支付密码必须为六位数字");
			return JSON.toJSONString(result);
		}
		// 业务实现
		User userMsg = userService.selectByPrimaryKey(userid);
		if (userMsg == null) {
			result.setCode(2037);
			result.setMessage("用户不存在");
			return JSON.toJSONString(result);
		}

		// 旧密码不为空时，要校验数据库中的密码
		if (oldpaypass != null) {
			if (!oldpaypass.equals(userMsg.getPaypass())) {
				result.setCode(2030);
				result.setMessage("原支付密码错误");
				return JSON.toJSONString(result);
			}
		} else {
			if (userMsg.getPaypass() != null) {
				result.setCode(2031);
				result.setMessage("你已设置支付密码");
				return JSON.toJSONString(result);
			}
		}

		String newPayPass = AESCryptoUtil.encrypt(newpaypass);
		userMsg.setPaypass(newPayPass);
		userService.updateByPrimaryKeySelective(userMsg);// 更新用户信息 保存支付密码

		result.setCode(0);
		result.setData(true);
		result.setMessage("修改成功");

		return JSON.toJSONString(result);
	}

	/**
	 * 判断支付密码正确
	 * 
	 * @param userid
	 * @param paypass
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/paypass/check/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String booleanPayPass(Long userid, String paypass) throws Exception {
		// paypass加密key=@paypass@

		ApiResult<Boolean> result = new ApiResult<Boolean>();
		result.setData(false);
		// 业务实现
		if (userid == null) {
			result.setCode(2008);
			result.setMessage("用户id不能为空");
			return JSON.toJSONString(result);
		}
		User users = userService.selectByPrimaryKey(userid);
		if (users == null) {
			result.setCode(2037);
			result.setMessage("用户不存在");
			return JSON.toJSONString(result);
		}
		paypass = AESCryptoUtil.encrypt(paypass);
		if (paypass == null || !paypass.equals(users.getPaypass())) {

			result.setMessage("支付密码错误");
			result.setCode(2032);
			return JSON.toJSONString(result);
		}

		result.setCode(0);
		result.setMessage("支付密码正确");
		result.setData(true);
		return JSON.toJSONString(result);
	}

	/**
	 * 第三方登录的情况下绑定其他第三方登录信息或者手机登录的情况下绑定第三方登录信息 绑定第三方,qq,weibo,opeinid(微信)到当前用户
	 * 
	 * @param uid
	 * @param nickname
	 * @param imgurl
	 * @param userid
	 *            当前登录的用户的id 要绑定的第三方信息(第三方登录相关)
	 * @param userThirdInfo
	 *            第三方信息其他信息
	 * @return
	 */
	@RequestMapping(value = "/third/bind/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String Bindingthird(String uid, String nickname, String imgurl,
			String type, UserThirdInfo userThirdInfo, Long userid) {

		ApiResult<User> result = new ApiResult<User>();

		LoginType loginType = LoginType2EnumUtils.getLoginTypeByStr(type);
		if (loginType == null) {
			result.setCode(2046);
			result.setMessage("type未传或者值不对");
			return JSON.toJSONString(result);
		}
		if (uid == null) {
			result.setCode(2047);
			result.setMessage("未传入绑定账号信息");
			return JSON.toJSONString(result);
		}

		User user = new User();

		if (loginType == LoginType.QQ) {
			user.setQq(uid);
			user.setQqNick(nickname);
			userThirdInfo.setQqProfileImageUrl(imgurl);
		} else if (loginType == LoginType.WEBO) {
			user.setWeibo(uid);
			user.setWeiboNick(nickname);
			userThirdInfo.setWeiboProfileImageUrl(imgurl);
		} else {
			user.setOpenid(uid);
			user.setWechatNick(nickname);
			userThirdInfo.setWeixinImgurl(imgurl);
		}

		if (userid == null) {
			result.setCode(2008);
			result.setMessage("用户id不能为空");
			return JSON.toJSONString(result);
		}
		// encodeUtf8mb4Str(user, userThirdInfo); //对用户的第三方昵称以及第三方信息中的昵称进行编码
		// 数据验证
		if (user == null) {
			result.setCode(2040);
			result.setMessage("绑定信息不正确");
			return JSON.toJSONString(result);
		}
		Boolean next = userService.third(user); // 判断用户是否可以绑定
		if (!next) {
			//判断是否已绑定自己
			User wechatUser = userService.getWechatBindingUser(uid);
			if(wechatUser!=null && userid.equals(wechatUser.getId())){
				result.setCode(ApiResultCode.WECHAT_OPENID_BIND_USER);
				result.setMessage("当前用户已绑定过此openid");
				return JSON.toJSONString(result);
			}
		
			result.setCode(2041);
			result.setMessage("该第三方帐号已存在");
			return JSON.toJSONString(result);
		}
		User loginUser = userService.selectByPrimaryKey(userid);// 根据userid查找用户
		if (loginUser == null) {
			result.setCode(2037);
			result.setMessage("用户不存在");
			return JSON.toJSONString(result);
		}
		// 判断当前用户要绑定的信息是否已经存在
		if (user.getQq() != null && loginUser.getQq() != null) // QQ
		{
			result.setCode(2043);
			result.setMessage("当前用户第三方账号：QQ已存在");
			return JSON.toJSONString(result);
		}
		if (user.getWeibo() != null && loginUser.getWeibo() != null) // 微博
		{
			result.setCode(2044);
			result.setMessage("当前用户第三方账号：微博已存在");
			return JSON.toJSONString(result);
		}
		if (user.getOpenid() != null && loginUser.getOpenid() != null) // 微信
		{
			result.setCode(2045);
			result.setMessage("当前用户第三方账号：微信已存在");
			return JSON.toJSONString(result);
		}

		// 过滤不必要的数据，防止数据被覆盖
		// User userBak = new User();
		// userBak.setQq(user.getQq());
		// userBak.setQqNick(user.getQqNick());
		// userBak.setWeibo(user.getWeibo());
		// userBak.setWeiboNick(user.getWeiboNick());
		// userBak.setOpenid(user.getOpenid());
		// userBak.setWechatNick(user.getWechatNick());
		// userBak.setImgurl(user.getImgurl());
		// userBak.setId(null);
		// user = userBak;

		userThirdInfo.setUserId(null);
		userThirdInfo.setId(null);
		userThirdInfo.setQqUid(user.getQq());
		userThirdInfo.setWeiboUid(user.getWeibo());
		userThirdInfo.setWeixinOpenid(user.getOpenid());

		// 业务实现
		user.setId(loginUser.getId());

		try {
			User resultuser = userService.bindingthird(user, userThirdInfo);
			if (resultuser.getPaypass() == null) {
				resultuser.setPaypass("0");
			} else {
				resultuser.setPaypass("1");
			}
			resultuser.setPassword(null);
			// decodeUtf8mb4Str(resultuser);
			// 设置用户头像
			userThirdInfo = userThirdInfoService.selectByUserId(userid);
			setUserImageUrl(resultuser, userThirdInfo);
			result.setData(resultuser);
			result.setMessage("绑定成功");
			result.setCode(0);
		} catch (Exception e) {
			result.setCode(2029);
			result.setData(null);
			result.setMessage("绑定失败");
		}

		return JSON.toJSONString(result);
	}

	// 获得用户绑定的银行卡
	@RequestMapping(value = "/auth/info/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getUserAuth(Long userid) {

		ApiResult<UserAuth> result = new ApiResult<UserAuth>();

		if (userid == null) {
			result.setCode(2008);
			result.setMessage("用户id不能为空");
			return JSON.toJSONString(result);
		}
		// 业务实现
		User loginUser = userService.selectByPrimaryKey(userid);
		if (loginUser == null) {
			result.setCode(2037);
			result.setMessage("用户不存在");
			return JSON.toJSONString(result);
		}
		UserAuth userAuth = userService.getUserAuth(loginUser.getId());
		if (userAuth == null) {
			result.setCode(2033);
			result.setMessage("用户尚未认证");
			return JSON.toJSONString(result);
		}
		userAuth.setCardTypePic(appConfig.staticUrl + userAuth.getCardTypePic());
		result.setData(userAuth);
		result.setCode(0);
		return JSON.toJSONString(result);
	}

	// 根据银行卡号获取银行信息
	@RequestMapping(value = "/bank/info/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getBankInfo(String cardno) {

		ApiResult<Map> result = new ApiResult<Map>();
		// 业务实现
		UnionOpens unionOpens = new UnionOpens();
		Map map = null;
		try {
			map = unionOpens.getCardInfo(cardno);
			String retCode = (String) map.get("retCode");
			if (!"0000".equals(retCode)) {
				result.setCode(2034);
				result.setMessage("获取银行信息失败");
				return JSON.toJSONString(result);
			}
			
			//获取银行图标
			String bankName = (String) map.get("bankName");
			Bank bank = bankService.selectByName(bankName);
			if(bank != null){
				map.put("icon", appConfig.staticUrl + bank.getPicUrl());
			}
		} catch (Exception e) {
			result.setCode(2034);
			result.setMessage("获取银行信息失败");
			return JSON.toJSONString(result);
		}

		// 数据返回
		result.setCode(0);
		result.setData(map);
		return JSON.toJSONString(result);
	}

	/**
	 * 重置支付密码
	 * 
	 * @param userid
	 *            用户id
	 * @param paypass
	 *            新的支付密码
	 * @param repaypass
	 *            重复新的支付密码
	 * @return
	 * @throws CryptoException
	 */
	@RequestMapping(value = "/paypass/modify/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String BackPayPass(Long userid, String paypass, String repaypass)
			throws CryptoException {

		ApiResult<Boolean> result = new ApiResult<Boolean>();
		result.setData(false);

		if (userid == null) {
			result.setCode(2008);
			result.setMessage("用户id不能为空");
			return JSON.toJSONString(result);
		}

		if (StringUtils.isEmpty(paypass)) {
			result.setCode(2036);
			result.setMessage("新的支付密码不能为空");
			return JSON.toJSONString(result);

		}
		if (!paypass.equals(repaypass)) {
			result.setCode(2038);
			result.setMessage("两次支付密码不一致");
			return JSON.toJSONString(result);

		}

		if (!StrUtil.isSixNums(paypass)) {
			result.setCode(2039);
			result.setMessage("支付密码必须为六位数字");
			return JSON.toJSONString(result);
		}

		// paypass加密
		String EncodedPayPass = AESCryptoUtil.encrypt(paypass);
		User users = userService.selectByPrimaryKey(userid);
		users.setPaypass(EncodedPayPass);
		users.setId(userid);
		userService.updateByPrimaryKeySelective(users);
		result.setCode(0);
		result.setData(true);
		result.setMessage("支付密码重设成功");

		return JSON.toJSONString(result);
	}

	// 判断是否有实名认证
	@RequestMapping(value = "/judge/author/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String judgeAuthor(Long userid) {
		// 初始化
		ApiResult<UserAuth> result = new ApiResult<UserAuth>();
		if (userid == null) {
			result.setCode(2008);
			result.setMessage("用户id不能为空");
			return JSON.toJSONString(result);
		}
		UserAuth userAuth = userAuthService.findByUserId(userid);
		if (userAuth != null) {
			result.setData(userAuth);
			result.setCode(0);
			result.setMessage("该用户已经实名认证过了");
		} else {
			/*result.setCode(2048);
			result.setMessage("该用户还没有实名认证");*/
			//mod by chencheng 20160401  暂时关闭实名认证
			result.setCode(0);
			result.setMessage("该用户已经实名认证过了");
		}
		return JSON.toJSONString(result);
	}

	@RequestMapping("init")
	@ResponseBody
	public void init() {
		List<User> users = userService.selectall();
		for (int i = 0; i < users.size(); i++) {
			User user = users.get(i);
			UserThirdInfo userThirdInfo = new UserThirdInfo();
			// encodeUtf8mb4Str(user, userThirdInfo);
			userService.updateByPrimaryKeySelective(user);
		}
	}

	/**
	 * @Description: (更新用户设备信息)
	 * @Title: userPushInfo
	 * @param userId
	 *            用户编号
	 * @param regId
	 *            用户在小米推送服务注册的编号
	 * @param type
	 *            设备类型1.IOS 2.android
	 * @return
	 * @date 2015年12月2日 下午4:33:51
	 * @author Hongbo Peng
	 */
	@RequestMapping("/userPushInfo/1.0")
	@ResponseBody
	public String userPushInfo(Long userId, String regId, Integer type) {
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		if (null == userId) {
			return result.toJSONString(2008, "用户id不能为空");
		}
		if (StringUtils.isEmpty(regId)) {
			return result.toJSONString(2109, "用户regId不能为空");
		}
		if (null == type) {
			return result.toJSONString(2110, "用户设备类型不能为空");
		}
		UserPushInfo userPushInfo = new UserPushInfo();
		userPushInfo.setUserId(userId);
		userPushInfo.setRegId(regId);
		userPushInfo.setType(type);
		userPushInfoService.addOrUpdateUserPushInfo(userPushInfo);
		return result.toJSONString(0, "更新用户设备信息成功");
	}

	/**
	 * 
	 * 
	 * @Description: (是否设置了支付密码)
	 * @Title: isSetPaypass
	 * @param userId
	 * @param regId
	 * @param type
	 * @return
	 * @date 2015年12月4日 下午10:09:31
	 * @author lhj
	 */
	@RequestMapping("/paypass/exist/1.0")
	@ResponseBody
	public String isSetPaypass(Long userid) {
		// 初始化
		ApiResult<Boolean> apiResult = new ApiResult<Boolean>();
		apiResult.setCode(0);
		// 业务实现
		User user = userService.selectByPrimaryKey(userid);
		if (StringUtils.isEmpty(user.getPaypass()))
			apiResult.setData(false);
		else
			apiResult.setData(true);
		// 数据返回
		return JSON.toJSONString(apiResult);
	}
	
	/**
	 * 绑定微信
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: bindingthird 
	 * @param userid
	 * @param phone
	 * @param uid
	 * @param nickname
	 * @param imgurl
	 * @param type
	 * @return
	 * @date 2015年12月7日 下午7:47:43  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/third/bind/2.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String bindingthird(Long userid,String phone,String uid,String nickname,String imgurl,Integer type) {
		ApiResult<JSONObject> apiResult = new ApiResult<JSONObject>();
		if(userid == null){
			return apiResult.toJSONString(-1, "参数userid不能为空");
		}
		if(StringUtils.isBlank(phone)){
			return apiResult.toJSONString(-1, "参数phone不能为空");
		}
		if(StringUtils.isBlank(uid)){
			return apiResult.toJSONString(-1, "参数uid不能为空");
		}
		if(StringUtils.isBlank(nickname)){
			return apiResult.toJSONString(-1, "参数nickname不能为空");
		}
		if(StringUtils.isBlank(imgurl)){
			return apiResult.toJSONString(-1, "参数imgurl不能为空");
		}
		if(type == null){
			return apiResult.toJSONString(-1, "参数type不能为空");
		}
		
		boolean isExit = userService.checkUserExit(phone,uid);
		if(isExit){
			logger.info("该手机号码:" + phone + "已绑定过此openid:" + uid);
			return apiResult.toJSONString(ApiResultCode.WECHAT_OPENID_BIND_USER, "该手机号码已绑定过此openid");
		}
		
		User wechatUser = userService.getWechatBindingUser(uid);
		User user = new User();
		user.setMobile(phone);
		user = userService.selectLoginByMobile(user);
		
		boolean flag = true;
		if(null!=wechatUser && null!=user){
			//使用微信账号登录
			//user =  wechatUser;//用户等同微信用户
			//两个用户都存在,已微信账号登录
			logger.info("该手机号码:" + phone + "已存在,但未绑定过此openid:" + uid);
			return apiResult.toJSONString(ApiResultCode.PHONE_EXIT_NOT_BIND_WECHAT, "该手机号码已存在,但未绑定过此openid");
		}else{
			Date now = new Date();
			Long user_id = null;
			//有一不存在，则绑定
			if(wechatUser != null){
				logger.info("openid:"+uid+"已绑定,但手机号未存在");
				user = wechatUser;
				user.setMobile(phone);
				user.setOpenid(uid);
				user.setWechatNick(nickname);
				user_id = user.getId();
				user.setLoginCount(0);//0次登录
				user.setType(1);//普通用户
			}else if(user != null){
				logger.info("openid:"+uid+"未绑定,但手机号已存在");
				user.setMobile(phone);
				user.setOpenid(uid);
				user.setWechatNick(nickname);
				user_id = user.getId();
			}else{
				logger.info("openid:"+uid+"未绑定,且手机号未存在");
				user = new User();
				user.setMobile(phone);
				user.setOpenid(uid);
				user.setWechatNick(nickname);
				user.setGmtCreate(now);
				user.setLoginCount(0);//0次登录
				user.setType(1);//普通用户
				flag = false;
			}
			user.setGmtModify(now);

			if(flag){
				userService.updateByPrimaryKeySelective(user);
			}else{
				userService.insertSelective(user);
			}
			
			user_id = user.getId();
			
			UserThirdInfo userThirdInfo = null;
			
			if(user_id != null){
				userThirdInfo = userThirdInfoService.selectByUserId(user_id);
			}
			
			if(userThirdInfo != null){
				userThirdInfo.setWeixinImgurl(imgurl);
				userThirdInfo.setWeixinNickname(nickname);
				userThirdInfo.setWeixinOpenid(uid);
				userThirdInfo.setUserId(user_id);
			}else{
				userThirdInfo = new UserThirdInfo();
				userThirdInfo.setWeixinImgurl(imgurl);
				userThirdInfo.setWeixinNickname(nickname);
				userThirdInfo.setWeixinOpenid(uid);
				userThirdInfo.setUserId(user_id);
				userThirdInfo.setGmtCreate(now);
			}
			
			userThirdInfo.setGmtModify(now);
				
			userThirdInfoService.insertOrUpdate(userThirdInfo, user_id);
		}
		
		return apiResult.toJSONString(0, "success");
	}
	
	@RequestMapping(value = "/third/bind/2.1", produces = "application/json;charset=utf-8")
	@ResponseBody
	@Transactional
	public String bindingthird(Long userid,String uid,Integer type) {
		ApiResult<JSONObject> apiResult = new ApiResult<JSONObject>();
		if(userid == null){
			return apiResult.toJSONString(-1, "参数userid不能为空");
		}
		if(StringUtils.isBlank(uid)){
			return apiResult.toJSONString(-1, "参数uid不能为空");
		}
		if(type == null){
			return apiResult.toJSONString(-1, "参数type不能为空");
		}

		User user = userService.getById(userid);
		if(null == user){
			return apiResult.toJSONString(-1, "该用户不存在");
		}
		
		if(uid.equals(user.getOpenidBinding())){
			return apiResult.toJSONString(ApiResultCode.WECHAT_OPENID_BIND_USER, "该手机号码已绑定过此openid");
		}
		
		if(StringUtils.isNotBlank(user.getOpenidBinding())){
			return apiResult.toJSONString(-1, "该手机号码已被绑定");
		}
		
		User wechatUser = userService.getWechatBindingUser(uid);
		if(null != wechatUser){
			return apiResult.toJSONString(-1, "该微信号已被绑定");
		}
		
		user.setOpenidBinding(uid);
		user.setGmtModify(new Date());
		
		userService.updateByPrimaryKeySelective(user);
		
		return apiResult.toJSONString(0, "success");
	}
	
	/**
	 * 获取用户邀请码
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getInvitationCode 
	 * @param userid	用户ID
	 * @return
	 * @date 2015年12月31日 下午12:03:52  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/manage/invitation/get/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getInvitationCode(Long userid) {
		ApiResult<String> apiResult = new ApiResult<String>();
		try {
			String invitationCode = userService.checkInvitationCodeByUser(userid);
			return apiResult.toJSONString(0, "获取邀请码成功", invitationCode);
		} catch (Exception e) {
			logger.error("获取邀请码失败:"+e.getMessage(), e);
			return apiResult.toJSONString(-1, "获取邀请码失败:" + e.getMessage());
		}
	}
	
	/**
	 * 获取用户财币
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getUserAccountIntegral 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年3月16日 下午5:30:28  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/account/integral/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String getUserAccountIntegral(Long userid) {
		ApiResult<JSONObject> apiResult = new ApiResult<JSONObject>();
		try {
			JSONObject json = new JSONObject();
			Double total = 0d;

			//积分价值
			json.put("integral", 0);
			//账户个数
			json.put("size", 0);
			//财币
			json.put("money", 0);
			
			List<IntegralVo> integralList = userCardManualService.queryIntegral(userid,1);
			if(null != integralList && integralList.size() > 0){
				for(IntegralVo integralVo : integralList){
					//积分价值
					Double money = integralVo.getMoney();
					if(integralVo.getType().equals(0)){
						json.put("money", integralVo.getIntegral());
						json.put("size", integralVo.getSize());
					}
					if(null != money){
						total += money;
					}
				}

				json.put("integral", total);
			}
			
			return apiResult.toJSONString(0, "success", json);
		} catch (Exception e) {
			logger.error("获取用户userid:" + userid + ",财币失败:" + e.getMessage(), e);
			return apiResult.toJSONString(-1, "获取用户财币失败:" + e.getMessage());
		}
	}
	
	/**
	 * 判断用户是否实名认证
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: judgeAuthor2 
	 * @param userid	用户ID
	 * @return
	 * @date 2016年6月20日 下午2:31:29  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/judge/author/2.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String judgeAuthor2(Long userid) {
		ApiResult<UserAuth> result = new ApiResult<UserAuth>();
		if (userid == null) {
			result.setCode(2008);
			result.setMessage("用户id不能为空");
			return JSON.toJSONString(result);
		}
		UserAuth userAuth = userAuthService.findByUserId(userid);
		if (userAuth != null) {
			if(StringUtils.isBlank(userAuth.getMobile())){
				User user = userService.selectByPrimaryKey(userid);
				if(!StringUtils.isBlank(user.getMobile())){
					userAuth.setMobile(user.getMobile());
				}
			}
			result.setData(userAuth);
			result.setCode(0);
			result.setMessage("该用户已经实名认证过了");
		} else {
			result.setCode(2048);
			result.setMessage("该用户还没有实名认证");
		}
		return JSON.toJSONString(result);
	}




}
