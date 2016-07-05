package com.caitu99.service.activities.controller.api;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.activities.domain.UserOtherPlatform;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.mongodb.dao.MongoDao;
import com.caitu99.service.utils.string.StrUtil;

@Controller
@RequestMapping("/api/activities/other/platform")
public class UserOtherPlatformController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(UserOtherPlatformController.class);

	@Autowired
	private MongoDao mongoBaseDao;
	
	/**
	 * 第三方平台注册
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: register 
	 * @param phone			手机号码
	 * @param source		第三方平台注册平台来源
	 * @return
	 * @date 2016年1月6日 下午2:46:07  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/register/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String register(String phone,String source) {
		ApiResult<String> apiResult = new ApiResult<String>();
		
		if(StringUtils.isBlank(phone)){
			return apiResult.toJSONString(-1, "手机号码不能为空");
		}else if(StringUtils.isBlank(source)){
			return apiResult.toJSONString(-1, "注册平台来源不能为空");
		}else if(!StrUtil.isPhone(phone)){
			return apiResult.toJSONString(-1, "请输入正确的手机号码");
		}
		
		try {
			UserOtherPlatform user = new UserOtherPlatform();
			user.setPhone(phone);
			user.setSource(source);
			user.setGmtCreate(new Date());
			
			mongoBaseDao.add(user);
			return apiResult.toJSONString(0, "注册成功");
		} catch (Exception e) {
			logger.error("第三方平台注册失败:" + e.getMessage(),e);
			return apiResult.toJSONString(-1, "系统繁忙,请稍后再试");
		}
	}
}
