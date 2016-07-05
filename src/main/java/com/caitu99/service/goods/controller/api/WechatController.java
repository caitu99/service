package com.caitu99.service.goods.controller.api;

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.integral.service.UserCardManualService;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.service.AccountService;
import com.caitu99.service.user.controller.vo.UserCardManualVo;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.http.HttpClientUtils;
import com.caitu99.service.utils.weixin.MessageUtil;
import com.caitu99.service.utils.weixin.SignUtil;
import com.caitu99.service.utils.weixin.WeixinUtil;

@Controller
@RequestMapping("/public/wechat/")
public class WechatController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(WechatController.class);
	
//	@Autowired
//	private RedisOperate redis;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserCardManualService userCardManualService;
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private WeixinUtil weixinUtil;
	
	@Autowired
	private AccountService accountService;
	
	/** 微信自定义菜单,我的积分,点击key */
	private static final String MY_INTEGRAL_CLICK_KEY = "my_integral_click";
	/** 微信自定义菜单,事件点击事件 */
	private static final String EVENT_TYPE_CLICK = "click";
	/** 微信自定义菜单,事件视图事件 */
//	private static final String EVENT_TYPE_VIEW = "view";
	/** 微信自定义菜单,订阅公众事件 */
	private static final String EVENT_TYPE_SUBSCRIBE = "subscribe";
	/** 微信自定义菜单,花积分,url */
//	private static final String SPEND_INTEGRAL_URL = "/list_page/Purchase_History.html";
	private static final String SPEND_INTEGRAL_URL = "/pages/goods-list.html?from=2";
	/** 微信自定义菜单,登录url */
	private static final String LOGIN_URL = "/Integral_page/banding.html";
	/** 微信自定义菜单,首页url */
	private static final String HOME_PAGE_URL = "/Integral_page/Financial.html";
	/** 微信自定义菜单,热门活动,url */
	private static final String HOT_ACTIVITY_URL = "/activity_page/activity_page.html?source=2";
	
	/** 微信自定义菜单,花积分 */
	private static final String SPEND_INTEGRAL = "0";
	/** 微信自定义菜单,热门活动 */
	private static final String HOT_ACTIVITY = "1";

//	/** 微信已绑定用户发送模板 */
//	private static final String ISBAND_WECHAT_TEMPLATE = "{\"touser\":\"OPENID\",\"template_id\":\"TEMPLATEID\","
//														+ "\"url\":\"http://www.caitu99.com\",\"topcolor\":\"#FF0000\","
//														+ "\"data\":{"
//														+ "\"first\": {\"value\":\"firstData\",\"color\":\"#173177\"},"
//														+ "\"keyword1\": {\"value\":\"keyword1DATA\",\"color\":\"#173177\"},"
//														+ "\"keyword2\": {\"value\":\"keyword2DATA\",\"color\":\"#173177\"},"
//														+ "\"remark\": {\"value\":\"remarkData\",\"color\":\"#173177\"}}}";
//	
//	/** 微信未绑定用户发送模板 */
//	private static final String ISBAND_NOT_WECHAT_TEMPLATE = "{\"touser\":\"OPENID\",\"template_id\":\"TEMPLATEID\","
//														+ "\"url\":\"http://www.caitu99.com\",\"topcolor\":\"#FF0000\","
//														+ "\"data\":{"
//														+ "\"first\": {\"value\":\"firstData\",\"color\":\"#173177\"},"
//														+ "\"keyword1\": {\"value\":\"keyword1DATA\",\"color\":\"#173177\"},"
//														+ "\"keyword2\": {\"value\":\"keyword2DATA\",\"color\":\"#173177\"},"
//														+ "\"remark\": {\"value\":\"remarkData\",\"color\":\"#173177\"}}}";
//	
	
	
	
	/**
	 * 	微信分享
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: share 
	 * @param url
	 * @param jsonpCallback
	 * @return
	 * @date 2015年12月22日 下午3:22:12  
	 * @author fangjunxiao
	 */
	@RequestMapping(value="activity/share/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String share(String url,String jsonpCallback) {
		ApiResult<Map<String,String>> result = new ApiResult<Map<String,String>>();
		if(StringUtils.isBlank(url)){
			logger.error("获取微信openid失败:" + "获取微信code失败");
			return result.toJSONString(-1, "获取微信code失败");
		}
		String appId = appConfig.appID;
		String caituUrl = appConfig.caituUrl;
		
		String token = weixinUtil.getAccessToken();
		String timestamp = "1450865347432";
		String noncestr = "71cb99fe-ac50-4f73-bdd6-e4530ea69f38";
		String jsapi_ticket = weixinUtil.getSignature(token, noncestr, timestamp, url);
		Map<String,String> map = new HashMap<String, String>();
		map.put("signature", jsapi_ticket);
		map.put("appId", appId);
		map.put("caituUrl", caituUrl);
		
		String resultString = result.toJSONString(1, "success",map);
		return super.retrunResult(resultString, jsonpCallback);
	}
	
	
	
	/**
	 * 向微信发起获取微信openid请求
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: redirectOpenid 
	 * @return
	 * @date 2015年12月1日 上午9:58:43  
	 * @author xiongbin
	 */
	@RequestMapping(value="redirect/openid/1.0", produces="application/json;charset=utf-8")
	public String redirectOpenid(HttpServletRequest request) {
		String appId = appConfig.appID;
		String caituUrl = appConfig.caituUrl;
		String type = request.getParameter("type");
		
		String url = WeixinUtil.getWeChatUrl(appId, caituUrl + "/public/wechat/callback/openid/1.0", type);
		return "redirect:" + url;
	}
	
	/**
	 * 获取微信openid
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getOpenid 
	 * @param code
	 * @param state
	 * @return
	 * @date 2015年12月1日 上午10:09:54  
	 * @author xiongbin
	 */
	@RequestMapping(value="callback/openid/1.0", produces="application/json;charset=utf-8")
	public String getOpenid(String code, String state,HttpServletResponse response) {
		ApiResult<String> result = new ApiResult<String>();
		
		String appId = appConfig.appID;
		String appsecret = appConfig.appsecret;
		
		if(StringUtils.isBlank(code)){
			logger.error("获取微信openid失败:" + "获取微信code失败");
			return result.toJSONString(-1, "获取微信code失败");
		}
		
		String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
		url = url.replace("APPID", appId).replace("SECRET", appsecret).replace("CODE", code);
		
		try {
			String wechatResult = HttpClientUtils.getInstances().doGet(url, "UTF-8");
			JSONObject json = JSON.parseObject(wechatResult);
			
			if(StringUtils.isBlank(wechatResult)){
				logger.error("获取微信openid失败:" + "微信返回数据为空");
				return "redirect:" + appConfig.caituUrl + LOGIN_URL;
			}
			
			String openid = json.getString("openid");
			if(StringUtils.isBlank(openid)){
				logger.error("获取微信openid失败:" + json.getString("errmsg"));
			}else{
//				String thirdLoginReslut = thirdLogin(openid);
//				JSONObject thirdLoginReslutJson = JSON.parseObject(thirdLoginReslut);
//				if(thirdLoginReslutJson.getInteger("code").equals(0)){
//					String accessToken = thirdLoginReslutJson.getString("data");
//					return "redirect:" + SPEND_INTEGRAL_URL + "?accessToken=" + accessToken;
//				}
				
				boolean remember = false;
				String userResult = getUserIntegral(openid);
				if(StringUtils.isNotBlank(userResult)){
					//用户已绑定过,可以自动登录
					remember = true;
				}
				
				if(SPEND_INTEGRAL.equals(state)){
					return "redirect:" + appConfig.caituUrl + SPEND_INTEGRAL_URL + "&openid=" + openid + "&remember=" + remember;
				}else if(HOT_ACTIVITY.equals(state)){
					return "redirect:" + appConfig.caituUrl + HOT_ACTIVITY_URL + "&openid=" + openid + "&remember=" + remember;
				}
			}
			return "redirect:" + appConfig.caituUrl + LOGIN_URL;
		} catch (Exception e) {
			logger.error("获取微信openid失败:" + e.getMessage());
			return "redirect:" + appConfig.caituUrl + LOGIN_URL;
		}
	}
	
//	/**
//	 * 将用户的微信openid放入cookie中
//	 * @Description: (方法职责详细描述,可空)  
//	 * @Title: openidSetCookie 
//	 * @param openid
//	 * @param response
//	 * @date 2015年12月5日 上午10:45:56  
//	 * @author xiongbin
//	 */
//	private void setCookie(String cookieName,String cookieValue,HttpServletResponse response){
//		Cookie cookie = new Cookie(cookieName,cookieValue);
//		cookie.setDomain(appConfig.caituUrl.replace("http://", ""));
//		cookie.setMaxAge(1000*60*60*24*30*12);
//		cookie.setPath("/");
//		response.addCookie(cookie);
//	}
//	
//	/**
//	 * 查询用户积分信息
//	 * @Description: (方法职责详细描述,可空)  
//	 * @Title: queryUserIntegral 
//	 * @param userid
//	 * @return
//	 * @date 2015年12月1日 下午2:31:06  
//	 * @author xiongbin
//	 */
//	@RequestMapping(value="user/integral", produces="application/json;charset=utf-8")
//	@ResponseBody
//	public String queryUserIntegral(Long userid) {
//		User user = userService.getById(userid);
//		if(null == user){
//			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
//			return result.toJSONString(-1, "没有该用户");
//		}
//		
//		return getUserIntegral(user);
//	}
//	
//	/**
//	 * 判断微信用户是否绑定,并返回积分信息
//	 * @Description: (方法职责详细描述,可空)  
//	 * @Title: isBinding 
//	 * @param openid
//	 * @return
//	 * @date 2015年12月1日 上午10:51:20  
//	 * @author xiongbin
//	 */
//	private String isBinding(String openid) {
//		User user = userService.getWechatBindingUser(openid);
//		if(null == user){
//			ApiResult<JSONObject> result = new ApiResult<JSONObject>();
//			return result.toJSONString(1, "未绑定账户");
//		}
//		
//		return getUserIntegral(user);
//	}
//	
//	/**
//	 * 查询用户财币与价值
//	 * @Description: (方法职责详细描述,可空)  
//	 * @Title: getUserIntegral 
//	 * @param user
//	 * @return
//	 * @date 2015年12月1日 下午2:28:51  
//	 * @author xiongbin
//	 */
//	private String getUserIntegral(User user){
//		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
//		JSONObject json = new JSONObject();
//		
//		//用户财币
//		Integer caifen = user.getIntegral();
//		//兑换比率
//		double exchangeScale = appConfig.exchangeScale;
//		//总价值
//		double total = caifen * exchangeScale;
//		
//		//获取用户手动查询所拥有的卡片
//		List<UserCardManualVo> list = userCardManualService.queryIntegralRemoveRepetition(user.getId());
//		for(UserCardManualVo userCardManualVo : list){
//			Integer integral = userCardManualVo.getIntegral();
//			float scale = userCardManualVo.getScale();
//			
//			if(null!=integral && !integral.equals(-1)){
//				total += exchangeScale*integral*scale;
//			}
//		}
//		
//		//保留两位小数点
//		DecimalFormat df = new DecimalFormat("#.00");  
//		
//		json.put("caifen", caifen);
//		json.put("total", df.format(total));
//		
//		return result.toJSONString(0, "",json);
//	}
	
	/**
	 * 根据用户的openid获取积分信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getUserIntegral 
	 * @param openid		微信openid
	 * @return
	 * @date 2015年12月2日 下午7:07:57  
	 * @author xiongbin
	 */
	private String getUserIntegral(String openid){
		User user = userService.getWechatBindingUser(openid);
		if(null == user){
			return null;
		}
		
		//未绑定手机号码
//		if(StringUtils.isBlank(user.getMobile())){
//			return "-1";
//		}
		
		JSONObject json = new JSONObject();
		
		//用户财币
//		Integer caifen = user.getIntegral();
		Long caifen = 0L;
		Account account = accountService.selectByUserId(user.getId());
		if(account != null){
			caifen = account.getAvailableIntegral();
		}
		
		//兑换比率
		double exchangeScale = appConfig.exchangeScale;
		//总价值
		double total = caifen * exchangeScale;
		
		//获取用户手动查询所拥有的卡片
		List<UserCardManualVo> list = userCardManualService.queryIntegralRemoveRepetition(user.getId(),1);
		for(UserCardManualVo userCardManualVo : list){
			Integer integral = userCardManualVo.getIntegral();
			float scale = userCardManualVo.getScale();
			
			if(null!=integral && !integral.equals(-1)){
				total += exchangeScale*integral*scale;
			}
		}
		
		//保留两位小数点
		DecimalFormat df = new DecimalFormat("#.00");  
		
		json.put("caifen", caifen);
		json.put("total", total==0?0:df.format(total));
		
		return json.toJSONString();
	}
	
//	/**
//	 * 发送用户积分的微信消息
//	 * @Description: (方法职责详细描述,可空)  
//	 * @Title: sendMessage 
//	 * @param openid		微信openid
//	 * @param caifen		用户财币
//	 * @param integral		用户积分价值
//	 * @return
//	 * @date 2015年12月2日 下午7:08:23  
//	 * @author xiongbin
//	 */
//	private void sendMessage(String openid,String caifen,String integral) {
//		String accessToken = weixinUtil.getAccessToken();
//		String templateId = appConfig.bandTemplate;
//		
//		String jsonText="{\"touser\":\"OPENID\",\"template_id\":\"TEMPLATEID\",\"url\":\"http://www.caitu99.com\",\"topcolor\":\"#FF0000\","
//				+ "\"data\":{"
//				+ "\"first\": {\"value\":\"firstData\",\"color\":\"#173177\"},"
//				+ "\"keyword1\": {\"value\":\"keyword1DATA\",\"color\":\"#173177\"},"
//				+ "\"keyword2\": {\"value\":\"keyword2DATA\",\"color\":\"#173177\"},"
//				+ "\"remark\": {\"value\":\"remarkData\",\"color\":\"#173177\"}}}";
//		
//		jsonText = jsonText.replace("OPENID", openid).replace("TEMPLATEID", templateId).replace("firstData", "您目前管理的所有")
//											.replace("keyword1DATA", "积分总价值:INTEGRAL元").replace("keyword2DATA", "总财币:CAIFEN财币")
//											.replace("remarkData", "下载APP查看积分详情")
//											.replace("INTEGRAL", integral.toString()).replace("CAIFEN", caifen.toString());
//		
//		weixinUtil.sendMessage(accessToken, templateId, jsonText);
//	}
	
	/**
	 * 请求校验（确认请求来自微信服务器）
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: wechatEventMessage 
	 * @param signature
	 * @param timestamp
	 * @param nonce
	 * @param echostr
	 * @param response
	 * @throws Exception
	 * @date 2015年12月2日 下午7:09:27  
	 * @author xiongbin
	 */
	@RequestMapping(value="event/message", method=RequestMethod.GET)
	public void wechatEventMessage(String signature, String timestamp, String nonce, String echostr, HttpServletResponse response) throws Exception {
		if(StringUtils.isBlank(signature) || StringUtils.isBlank(timestamp) || StringUtils.isBlank(nonce) || StringUtils.isBlank(echostr)){
			return;
		}
		
		PrintWriter out = response.getWriter();
		// 通过检验signature对请求进行校验，若校验成功则原样返回echostr，表示接入成功，否则接入失败
		if (SignUtil.checkSignature(signature, timestamp, nonce)) {
			out.print(echostr);
		}
		out.close();
		out = null;
	}
	
	/**
	 * 处理微信服务器发了的消息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: wechatEventMessage 
	 * @param request
	 * @param response
	 * @throws Exception
	 * @date 2015年12月2日 下午7:09:40  
	 * @author xiongbin
	 */
	@RequestMapping(value="event/message", method=RequestMethod.POST)
	public void wechatEventMessage(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter out = null;
		try {
			Map<String, String> requestMap = MessageUtil.parseXml(request);
			
			//事件类型
			String event = requestMap.get("Event");
			//事件key
			String key = requestMap.get("EventKey");
			String openid = requestMap.get("FromUserName");
			// 公众帐号
			String fromUserName = requestMap.get("ToUserName");
			
			if(StringUtils.isBlank(event) || StringUtils.isBlank(key) || StringUtils.isBlank(openid) || StringUtils.isBlank(fromUserName)){
				return;
			}
			
			if(MY_INTEGRAL_CLICK_KEY.equals(key.toLowerCase()) && EVENT_TYPE_CLICK.equals(event.toLowerCase())){
				String result = getUserIntegral(openid);
				out = response.getWriter();
				
				StringBuffer content = new StringBuffer();
//				content.append("非常抱歉,系统暂时出现异常,请稍后再试!");
				
				if(StringUtils.isBlank(result)){
					content.append("您好\n")
							.append("请先开通/绑定您的财途账户\n")
							.append("即可查询积分\n")
							.append("点击<a href='")
							.append(appConfig.caituUrl)
							.append(LOGIN_URL)
							.append("?openid=")
							.append(openid)
							.append("'>一键开通/绑定财途账户</a>");
					String message = weixinUtil.sendMessage(openid, fromUserName, content.toString());
					
					out.print(message);
//				}else if("-1".equals(result)){
//					content.append("您好\n")
//							.append("请先开通/绑定您的财途账户\n")
//							.append("即可查询积分\n")
//							.append("点击<a href='")
//							.append(appConfig.caituUrl)
//							.append(LOGIN_URL)
//							.append("?openid=")
//							.append(openid)
//							.append("&type=update")
//							.append("'>一键开通/绑定财途账户</a>");
//					String message = weixinUtil.sendMessage(openid, fromUserName, content.toString());
//					
//		
//					//将用户的微信openid放入cookie中
//					openidSetCookie(openid,response);
//					
//					out.print(message);
				}else{
					JSONObject json = JSON.parseObject(result);
					content.append("您目前管理的所有\n")
							.append("积分总价值" + json.getString("total") + "元\n")
							.append("您拥有" + json.getString("caifen") + "财币\n")
							.append("下载APP查看积分详情\n")
							.append("去看看有什么宝贝可以兑换吧\n");
					
//					String thirdLoginReslut = thirdLogin(openid);
//					JSONObject thirdLoginReslutJson = JSON.parseObject(thirdLoginReslut);
//					Integer code = thirdLoginReslutJson.getInteger("code");
//					if(code.equals(0)){
//						String accessToken = thirdLoginReslutJson.getString("data");
//						System.out.println(accessToken);
//						content.append("<a href='")
//						.append(appConfig.caituUrl)
//						.append(HOME_PAGE_URL)
//						.append("?accessToken=")
//						.append(accessToken).append("'>立即兑换</a>");
//					}else{
//						content.append("<a href='")
//								.append(appConfig.caituUrl)
//								.append(HOME_PAGE_URL)
//								.append("'>立即兑换</a>");
//					}
					
					content.append("<a href='")
							.append(appConfig.caituUrl)
							.append(HOME_PAGE_URL)
							.append("?openid=")
							.append(openid)
							.append("&remember=true")
							.append("'>立即兑换</a>");
					
					String message = weixinUtil.sendMessage(openid, fromUserName, content.toString());
					out.print(message);
				}
				
				out.close();
				out = null;
			}else if(EVENT_TYPE_SUBSCRIBE.equals(event.toLowerCase())){
				out = response.getWriter();
				StringBuffer content = new StringBuffer();
				content.append("终于等到你，欢迎关注财途积分生活！财小喵已经准备好了一大波福利，就等你来领啦，更多详情请下载财途积分生活APP哦，喵~");
				
				String message = weixinUtil.sendMessage(openid, fromUserName, content.toString());
				out.print(message);
				
				out.close();
				out = null;
			}
		} catch (Exception e) {
			logger.error("处理微信服务器推送时间的消息出错:" + e.getMessage(),e);
			if(out != null){
				out.close();
				out = null;
			}
		}
	}
	
	/**
	 * 获取微信用户信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getWechatUserInfo 
	 * @param openid
	 * @return
	 * @date 2015年12月3日 下午6:23:04  
	 * @author xiongbin
	 */
	@RequestMapping(value="userInfo/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String getWechatUserInfo(String openid) {
		ApiResult<JSON> result = new ApiResult<JSON>();
		
		if(StringUtils.isBlank(openid)){
			return result.toJSONString(-1, "用户微信openid不能为空");
		}
		
		String wechatResult = wechatUserInfo(openid);
		JSONObject json = JSON.parseObject(wechatResult);
		
		Integer code = json.getInteger("code");
		
		if(code == null){
			String nickname = json.getString("nickname");
			String headimgurl = json.getString("headimgurl");
			
			JSONObject reultJson = new JSONObject();
			reultJson.put("nickname", nickname);
			reultJson.put("headimgurl", headimgurl);
			return result.toJSONString(0, "",reultJson);
		}
		
		logger.error("用户未关注公众,无法获取用户信息");
		return result.toJSONString(-1, "用户未关注公众,无法获取用户信息");
	}
	
	/**
	 * 获取微信用户信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: wechatUserInfo 
	 * @param openid
	 * @return
	 * @date 2015年12月4日 下午4:47:52  
	 * @author xiongbin
	 */
	private String wechatUserInfo(String openid){
		ApiResult<String> result = new ApiResult<String>();
		
		String accessToken = weixinUtil.getAccessToken();
		String wechatResult = weixinUtil.getUserInfo(accessToken, openid);
		
		if(StringUtils.isBlank(wechatResult)){
			return result.toJSONString(-1, "用户未关注公众,无法获取用户信息");
		}
		
		JSONObject json = JSON.parseObject(wechatResult);
//		System.out.println(json.toJSONString());
		//是否关注公众号(0代表此用户没有关注该公众号)
		Integer subscribe = json.getInteger("subscribe"); 
		if(!subscribe.equals(0)){
			return wechatResult;
		}

		return result.toJSONString(-1, "用户未关注公众,无法获取用户信息");
	}
	
//	/**
//	 * 第三方登录
//	 * @Description: (方法职责详细描述,可空)  
//	 * @Title: thirdLogin 
//	 * @param openid		微信openid
//	 * @return	
//	 * @date 2015年12月4日 下午4:15:50  
//	 * @author xiongbin
//	 */
//	private String thirdLogin(String openid){
//		ApiResult<String> resultJSON = new ApiResult<String>();
//		try {
//			String wechatResult = wechatUserInfo(openid);
//			JSONObject json = JSON.parseObject(wechatResult);
//			
//			Integer code = json.getInteger("code");
//			
//			if(code == null){
//				String nickname = json.getString("nickname");
//				String headimgurl = json.getString("headimgurl");
//				
//				String url = "http://gateway.caitu99.com/oauth/token";
//				Map<String,String> paramMap = new HashMap<String,String>();
//				paramMap.put("grant_type","password");
//				paramMap.put("client_secret", "832103d85bad45c495c2a82e5d1928f9");
//				paramMap.put("uid", openid);
//				paramMap.put("nickname", nickname);
//				paramMap.put("profileimg", headimgurl);
//				paramMap.put("type", "3");
//				paramMap.put("client_id", "1");
//				
//				String gatewayResult = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
//				JSONObject gatewayJson = JSON.parseObject(gatewayResult);
//				
//				String accessToken = gatewayJson.getString("access_token");
//				if(StringUtils.isNotBlank(accessToken)){
//					return resultJSON.toJSONString(0, "", accessToken);
//				}
//
//				logger.debug("第三方登录失败:" + gatewayJson.getString("error_description"));
//				return resultJSON.toJSONString(-1, "第三方登录失败:" + gatewayJson.getString("error_description"));
//			}
//			
//			logger.debug("第三方登录失败:" + json.getString("message"));
//			return resultJSON.toJSONString(-1, "第三方登录失败:" + json.getString("message"));
//		} catch (Exception e) {
//			logger.error("第三方登录失败:" + e.getMessage(), e);
//			return resultJSON.toJSONString(-1, "第三方登录失败:" + e.getMessage());
//		}
//	}
}
