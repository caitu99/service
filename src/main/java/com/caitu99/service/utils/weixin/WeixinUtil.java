package com.caitu99.service.utils.weixin;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.RedisKey;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.exception.ApiException;
import com.caitu99.service.utils.encryption.DigestUtils;
import com.caitu99.service.utils.exception.AssertUtil;
import com.caitu99.service.utils.http.HttpClientUtils;


/**
 * 公众平台通用接口工具类 
 *
 * @author liuyq
 * @date 2013-08-09
 */
@Component
public class WeixinUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(WeixinUtil.class);
	
	@Autowired
	private RedisOperate redis;
	
	@Autowired
	private AppConfig appConfig;
	
	/**
	 * 获取微信access_token url
	 */
	private static final String ACCESSTOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
	/**
	 * 设置微信自定义菜单 url
	 */
	private static final String SETMENU_URL = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
	/**
	 * 获取微信自定义菜单 url
	 */
	private static final String GETMENU_URL = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=ACCESS_TOKEN";
	/**
	 * 根据openid获取微信用户信息
	 */
	private static final String USERINFO_URL = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
	
	/**
	 * 获取微信access_token
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getAccessToken 
	 * @return
	 * @date 2015年11月30日 下午9:36:25  
	 * @author xiongbin
	 * @throws Exception 
	 */
	public String getAccessToken(){
		try {
			String key = RedisKey.WECHAT_ACCESS_TOKEN;
			String accessToken = redis.getStringByKey(key);
			
			if(StringUtils.isNotBlank(accessToken)){
				return accessToken;
			}
			
			String appId = appConfig.appID;
			String appsecret = appConfig.appsecret;
			
			String url = ACCESSTOKEN_URL.replace("APPID", appId).replace("APPSECRET", appsecret);
			
			String result = HttpClientUtils.getInstances().doGet(url, "UTF-8");
			JSONObject json = JSON.parseObject(result);
			accessToken = json.getString("access_token");
			
			AssertUtil.hasLength(accessToken, "获取微信access_token失败:" + result);
			
			if(StringUtils.isNotBlank(accessToken)){
				redis.set(key, accessToken, 7000);
			}
			
			return accessToken;
		} catch (Exception e) {
			logger.error("获取微信access_token失败:" + e.getMessage(),e);
			throw new ApiException(-1,"获取微信access_token失败:" + e.getMessage());
		}
	}
	
    /**
     * 分享到朋友圈
     * @Title: getSignature
     * @Description: (这里用一句话描述这个方法的作用)
     * @param:@param token
     * @param:@param noncestr
     * @param:@param timestamp
     * @param:@param url
     * @param:@return
     * @return String
     * @author phb
     * @date 2015年2月11日 上午10:38:23
     */
    public String getSignature(String token,String noncestr,String timestamp,String url){
    	String jsapi_ticket = "";
		try {
			String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
			String requestUrl = JSAPI_TICKET_URL.replace("ACCESS_TOKEN", token);  
			String resultString = HttpClientUtils.getInstances().doGet(requestUrl, "UTF-8");
			JSONObject jsonObject = JSONObject.parseObject(resultString);
	        String ticket = jsonObject.getString("ticket");
	        String JSAPI_TICKET="jsapi_ticket=JSAPI_TICKET&noncestr=NONCESTR&timestamp=TIMESTAMP&url=URL";
	        jsapi_ticket=JSAPI_TICKET.replace("JSAPI_TICKET", ticket).replace("NONCESTR", noncestr).replace("TIMESTAMP", timestamp).replace("URL", url);   	
	        jsapi_ticket = DigestUtils.jzShaEncrypt(jsapi_ticket);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsapi_ticket;
    }
    
	/**
	 * 微信授权链接
	 * @Title: _getWeChatUrl 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param url
	 * @return
	 * @date 2015年2月6日 下午1:11:18  
	 * @author liujia
	 */
	public static String getWeChatUrl(String appid, String urlOrIp ,String url){
		StringBuffer weChatUrl = new StringBuffer("https://open.weixin.qq.com/connect/oauth2/authorize?appid=");
		weChatUrl.append(appid);
		weChatUrl.append("&redirect_uri=");
		weChatUrl.append(WeixinUtil.urlEnodeUTF8(urlOrIp));
		weChatUrl.append("&response_type=code&scope=snsapi_base&state=");
		weChatUrl.append(WeixinUtil.urlEnodeUTF8(url));
		weChatUrl.append("#wechat_redirect");
		System.out.println("授权地址:"+weChatUrl.toString());
		return weChatUrl.toString();
	}
	
	/**
	 * 微信重定向url判定
	 * @Title: getWeChatUrl 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param appId
	 * @param urlOrIp
	 * @param openId
	 * @param getWeChatUrl
	 * @param returnUrl
	 * @param ext
	 * @return
	 * @date 2015年2月6日 下午3:55:16  
	 * @author liujia
	 */

	public String getWeChatUrl(String appId,String urlOrIp, String openId, String masterId,String getWeChatUrl,String returnUrl, String ext) {
		if((null==masterId) || (null == openId)){	
			return "redirect:"+getWeChatUrl(appId, urlOrIp, getWeChatUrl);
		}else{
			
			return returnUrl+masterId+ext;
		}
	}

	/**
	 * 把网址里的特殊字符转换，如http://转换成http:%3A%2F%2F
	 * @Title: urlEnodeUTF8 
	 * @Description: (这里用一句话描述这个方法的作用) 
	 * @param str
	 * @return
	 * @date 2015年2月3日 下午4:17:46  
	 * @author liujia
	 */
    public static String urlEnodeUTF8(String str){
        String result = str;
        try {
            result = URLEncoder.encode(str,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 判断访问是否来自微信浏览器
     * @Title: isWechatBrowser 
     * @Description: (这里用一句话描述这个方法的作用) 
     * @param httpRequest
     * @return
     * @date 2015年5月7日 上午10:07:16  
     * @author ah
     */
    public static boolean isWechatBrowser(HttpServletRequest httpRequest) {
    	String ua = httpRequest.getHeader("user-agent").toLowerCase();
    	return ua.contains("micromessenger");
    }

	/**
	 * @Title: _getWeChatCodeUrl
	 * @Description: (组装获取codeUrl)
	 * @param appId
	 * @param urlOrIp
	 * @param isBaseInfo true：全部信息 false：基本信息
	 * @param state
	 * @return
	 * @return String 返回类型
	 * @throws
	 * @author Baojiang Yang
	 * @date 2015年9月22日 下午9:57:15
	 */
	public static String getCodeUrl(String appId, String urlOrIp, boolean isBaseInfo, String state) {
		String getCodeRequest = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
		getCodeRequest = getCodeRequest.replace("APPID", urlEnodeUTF8(appId));
		getCodeRequest = getCodeRequest.replace("REDIRECT_URI", urlEnodeUTF8(urlOrIp));
		getCodeRequest = getCodeRequest.replace("SCOPE", isBaseInfo ? "snsapi_userinfo" : "snsapi_base");
		getCodeRequest = getCodeRequest.replace("STATE", state);
		return getCodeRequest;
	}

	/**
	 * @Title: _getWeChatAccessToken
	 * @Description: (组装获取AccessTokenUrl)
	 * @param appid
	 * @param secret
	 * @param code
	 * @return
	 * @return String 返回类型
	 * @throws
	 * @author Baojiang Yang
	 * @date 2015年9月22日 下午9:58:39
	 */
	public static String getAccessToken(String appid, String secret, String code) {
		StringBuilder sb = new StringBuilder("https://api.weixin.qq.com/sns/oauth2/access_token");
		sb.append("?appid=").append(appid);
		sb.append("&secret=").append(secret);
		sb.append("&code=").append(code);
		sb.append("&grant_type=authorization_code");
		return sb.toString();
	}

	public static boolean isNotEmptyCode(HttpServletRequest request) {
		if (request != null) {
			// 微信返回
			String code = request.getParameter("code");
			// 请求标志位
			String state = request.getParameter("state");
			return StringUtils.isNotBlank(code) && StringUtils.isNotBlank(state);
		}
		return false;
	}
	
	public static void setIndustry(String accessToken){
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/template/api_set_industry?access_token=ACCESS_TOKEN";
			url = url.replace("ACCESS_TOKEN", accessToken);
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("industry_id1","1");
			paramMap.put("industry_id2","1");
			String reslt = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			System.out.println(reslt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getTemplateId(String accessToken,String templateIdShort,Integer repeCount){
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/template/api_add_template?access_token=ACCESS_TOKEN";
			url = url.replace("ACCESS_TOKEN", accessToken);
			Map<String,String> paramMap = new HashMap<String,String>();
			paramMap.put("template_id_short",templateIdShort);
			String reslt = HttpClientUtils.getInstances().doPost(url, "UTF-8", paramMap);
			
			JSONObject json = JSON.parseObject(reslt);
			String templateId = json.getString("template_id");
			if(StringUtils.isBlank(templateId)){
				AssertUtil.isTrue(false, "获取微信模板ID出错:" + json.getString("errmsg"));
			}
			
			return templateId;
		} catch (Exception e) {
			repeCount--;
			logger.error("获取微信模板ID出错:" + e.getMessage(),e);
			if(repeCount>0){
				return getTemplateId(accessToken,templateIdShort,repeCount);
			}else{
				return null;
			}
		}
	}
	
	/**
	 * 发送微信模板消息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: sendTemplateMessage 
	 * @param accessToken
	 * @param templateId
	 * @param openid
	 * @param integral
	 * @param caifen
	 * @date 2015年12月2日 下午3:35:41  
	 * @author xiongbin
	 */
	public void sendTemplateMessage(String accessToken,String templateId,String jsonText){
		try {
			String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
			url = url.replace("ACCESS_TOKEN", accessToken);
			
//			String jsonText="{\"touser\":\"OPENID\",\"template_id\":\"TEMPLATEID\",\"url\":\"http://www.caitu99.com\",\"topcolor\":\"#FF0000\","
//					+ "\"data\":{"
//					+ "\"first\": {\"value\":\"firstData\",\"color\":\"#173177\"},"
//					+ "\"keyword1\": {\"value\":\"keyword1DATA\",\"color\":\"#173177\"},"
//					+ "\"keyword2\": {\"value\":\"keyword2DATA\",\"color\":\"#173177\"},"
//					+ "\"remark\": {\"value\":\"remarkData\",\"color\":\"#173177\"}}}";
//			
//			jsonText = jsonText.replace("OPENID", openid).replace("TEMPLATEID", templateId).replace("firstData", "您目前管理的所有")
//												.replace("keyword1DATA", "积分总价值:INTEGRAL元").replace("keyword2DATA", "总财币:CAIFEN财币")
//												.replace("remarkData", "下载APP查看积分详情")
//												.replace("INTEGRAL", integral.toString()).replace("CAIFEN", caifen.toString());
			
			logger.info("发生微信模板消息:" + jsonText);
			
			String reslt = HttpClientUtils.getInstances().doPost(url, "UTF-8", new String(jsonText.getBytes("UTF-8"), "ISO8859_1"));
			
			JSONObject json = JSON.parseObject(reslt);
			String errcode = json.getString("errcode");
			if(!"0".equals(errcode)){
				AssertUtil.isTrue(false, "发生微信模板消息出错:" + json.getString("errmsg"));
			}
		} catch (Exception e) {
			logger.error("发生微信模板消息出错:" + e.getMessage(),e);
		}
	}
	
	/**
	 * 发送自动回复消息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: sendMessage 
	 * @param openid
	 * @param fromUserName
	 * @param content
	 * @return
	 * @date 2015年12月2日 下午7:52:05  
	 * @author xiongbin
	 */
	public String sendMessage(String openid,String fromUserName,String content){
		String result = "<xml><ToUserName><![CDATA[OPENID]]></ToUserName><FromUserName><![CDATA[FROMUSERNAME]]></FromUserName>"
						+ "<CreateTime>12345678</CreateTime><MsgType><![CDATA[text]]></MsgType>"
						+ "<Content><![CDATA[CONTENT]]></Content></xml>";
		
		result = result.replace("OPENID", openid).replace("FROMUSERNAME", fromUserName).replace("CONTENT", content);
		
		return result;
	}
	
	/**
	 * 设置自定义菜单
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: setMenu 
	 * @param accessToken
	 * @throws Exception
	 * @date 2015年12月3日 下午5:41:24  
	 * @author xiongbin
	 */
	public void setMenu(String accessToken) {
		try {
			String url = SETMENU_URL.replace("ACCESS_TOKEN", accessToken);
			String jsonText = "{\"button\":[{\"name\":\"我的积分\",\"sub_button\":[{\"type\":\"click\","
					+ "\"name\":\"我的积分\",\"key\":\"my_integral_click\"},"
					+ "{\"type\":\"view\","
					+ "\"name\":\"应用下载\",\"url\":\"http://www.caitu99.com/app/download.html\"},"

					+ "{\"type\":\"view\","
					+ "\"name\":\"业务员\",\"url\":\""
					+ appConfig.caituUrl
					+ "/clerk_background/login.html\"}"

					+ "]},"
					+ "{\"name\":\"领红包\",\"sub_button\":["
					+ "{\"type\":\"view\","
					+ "\"name\":\"刮刮卡\",\"url\":\""
					+ appConfig.caituUrl
					+ "/public/wechat/redirect/openid/1.0?type=1\"},"

					+ "{\"type\":\"view\","
					+ "\"name\":\"刷爆卡\",\"url\":\""
					+ appConfig.caituUrl
					+ "/activity_card/activity_card_index.html\"},"

					+ "{\"type\":\"view\","
					+ "\"name\":\"财运签\",\"url\":\""
					+ appConfig.caituUrl
					+ "/activity_random/activity_random_index.html\"}"

					+ "]},{\"type\":\"view\","
					+ "\"name\":\"花积分\",\"url\":\""
					+ appConfig.caituUrl
					+ "/public/wechat/redirect/openid/1.0?type=0"
					+ "\"}]}";
			System.out.println(jsonText);
			String reslut = HttpClientUtils.getInstances().doPost(url, "UTF-8",new String(jsonText.getBytes("UTF-8"), "ISO8859_1"));
			System.out.println(reslut);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void getMenu(String accessToken) throws Exception{
		String url = GETMENU_URL.replace("ACCESS_TOKEN", accessToken);
		String reslut = HttpClientUtils.getInstances().doGet(url, "UTF-8");
		System.out.println(new String(reslut.getBytes("ISO8859_1"),"UTF-8"));
	}

	/**
	 * 获取微信用户信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: getUserInfo 
	 * @param accessToken
	 * @param openid
	 * @return
	 * @date 2015年12月3日 下午5:57:34  
	 * @author xiongbin
	 */
    public String getUserInfo(String accessToken,String openid){
    	String reslut = "";
    	try {
			String url = USERINFO_URL.replace("ACCESS_TOKEN", accessToken).replace("OPENID", openid);
			reslut = HttpClientUtils.getInstances().doGet(url, "UTF-8");
			
			if(StringUtils.isBlank(reslut)){
				logger.error("获取微信用户信息失败:返回数据为空");
				return "";
			}
			
			JSONObject json = JSON.parseObject(reslut);
			String errmsg = json.getString("errmsg");
			if(!StringUtils.isBlank(errmsg)){
				AssertUtil.isTrue(false, errmsg);
			}
	    	return new String(reslut.getBytes("ISO8859_1"),"UTF-8");
		} catch (Exception e) {
			logger.error("获取微信用户信息失败:" + e.getMessage());
			return "";
		}
    }
	
    public static void main(String[] args) throws Exception {
//    	String JSAPI_TICKET_URL = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
//		String resultString = "";
//		try {
//			resultString = HttpClientUtils.getInstances().doGet(JSAPI_TICKET_URL, "UTF-8");
//			System.out.println(resultString);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		JSONObject jsonObject = JSONObject.fromObject(resultString);
    	
//    	getAccessToken();
    	
    	
//    	String accessToken = "TNy6LfjFYwpNvJXbu0Q7anin7guGHTfUlH3EWWWLirkGg6vdYKUSsIm5foSFsZDI54HgA4_woE8cEBheCNDn5LwxJ0kH9eWOw22ccPVqFL4SXQhAAAGLE";
//    	String templateIdShort = "TM00005";
//    	String openid = "og2tiw36tYCHD03SYTC8haAbYeQk";
//    	
//    	String templateId = "brCyjlsP04ftw_0Qkw7NoKjkfUxCXnfYVmD3rVJHLEw";//getTemplateId(accessToken,templateIdShort,15);
//    	if(StringUtils.isBlank(templateId)){
//    		
//    	}else{
////    		sendMessage(accessToken,templateId,openid);
//    	}
    	
//    	setMenu(accessToken);
//    	getMenu(accessToken);
    	
    	String jsonText = "{\"button\":[{\"name\":\"我的积分\",\"sub_button\":[{\"type\":\"click\","
				+ "\"name\":\"我的积分\",\"key\":\"my_integral_click\"},{\"type\":\"view\","

				+ "\"name\":\"应用下载\",\"url\":\"http://www.caitu99.com/app/download.html\"}]},"
				+ "{\"name\":\"领红包\",\"sub_button\":["
				+ "{\"type\":\"view\","
				+ "\"name\":\"刮刮卡\",\"url\":\""
				+ "https://p.weixin.caitu99.com"
				+ "/public/wechat/redirect/openid/1.0?type=1\"},"

				+ "{\"type\":\"view\","
				+ "\"name\":\"刷爆卡\",\"url\":\""
				+ "https://p.weixin.caitu99.com"
				+ "/activity_card/activity_card_index.html\"},"

				+ "{\"type\":\"view\","
				+ "\"name\":\"财运签\",\"url\":\""
				+ "https://p.weixin.caitu99.com"
				+ "/activity_random/activity_random_index.html\"}"

				+ "]},{\"type\":\"view\","
				+ "\"name\":\"花积分\",\"url\":\""
				+ "https://p.weixin.caitu99.com"
				+ "/public/wechat/redirect/openid/1.0?type=0"
				+ "\"}]}";
		System.out.println(jsonText);
	}
} 