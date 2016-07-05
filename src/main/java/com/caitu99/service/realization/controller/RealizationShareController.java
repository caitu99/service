package com.caitu99.service.realization.controller;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.realization.service.RealizeShareService;
import com.caitu99.service.utils.json.JsonResult;
import com.caitu99.service.utils.string.StrUtil;

@Controller
@RequestMapping("/api/realization/share/")
public class RealizationShareController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(RealizationShareController.class);

	@Autowired
	private RealizeShareService realizeShareService;
	@Autowired
	private AppConfig appConfig;
	
	/**
	 * 生成分享数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: userlist 
	 * @param userid				用户ID
	 * @param realizeRecordId		积分变现记录ID
	 * @return
	 * @date 2016年4月11日 下午7:19:46  
	 * @author xiongbin
	 */
	@RequestMapping(value="generate/record/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String generate(Long userid,Long realizeRecordId) {
		ApiResult<Long> result = new ApiResult<Long>();
		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}else if(null == realizeRecordId){
			return result.toJSONString(-1, "参数realizeRecordId不能为空");
		}
		
		Long realizeShareId = realizeShareService.generate(userid, realizeRecordId);
		if(null == realizeShareId){
			return result.toJSONString(-1,"分享失败");
		}else if(realizeShareId.equals(-1L)){
			return result.toJSONString(-1,"您已经分享过了");
		}
		
		return result.toJSONString(0,"success",realizeShareId);
	}
	
	/**
	 * 领取分享红包
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: receive 
	 * @param userid				用户ID
	 * @param realizeShareId		积分变现分享ID
	 * @param platform				分享平台
	 * @return
	 * @date 2016年4月12日 下午12:16:23  
	 * @author xiongbin
	 */
	@RequestMapping(value="receive/record/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String receive(String phone,Long realizeShareId,Integer platform) {
		ApiResult<Long> result = new ApiResult<Long>();
		if(StringUtils.isBlank(phone)){
			return result.toJSONString(-1, "参数phone不能为空");
		}else if(null == realizeShareId){
			return result.toJSONString(-1, "参数realizeShareId不能为空");
		}else if(null == platform){
			return result.toJSONString(-1, "参数platform不能为空");
		}else if(!platform.equals(1) && !platform.equals(2)){
			//目前只支持微信分享,微博分享
			return result.toJSONString(-1, "参数platform错误");
		}else if(!StrUtil.isPhone(phone)){
			return result.toJSONString(-1, "请输入正确的手机号码");
		}
		
		//领取的分享金额(途币)
		Long money = realizeShareService.receive(phone, realizeShareId, platform);
		if(null == money){
			return result.toJSONString(-1,"领取分享失败");
		}
		
		return result.toJSONString(0,"success",money);
	}
	
	/**
	 * 获取微信分享数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: weixinShare 
	 * @param userid				用户ID
	 * @param realizeRecordId		积分变现记录ID
	 * @return
	 * @date 2016年4月14日 上午10:24:01  
	 * @author xiongbin
	 */
	@RequestMapping(value="weixin/share/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String weixinShare(Long userid,Long realizeRecordId) {
		ApiResult<JSONObject> result = new ApiResult<JSONObject>();
		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}else if(null == realizeRecordId){
			return result.toJSONString(-1, "参数realizeRecordId不能为空");
		}
		
		//生成分享数据
		String reslut = this.generate(userid, realizeRecordId);
		boolean flag = JsonResult.checkResult(reslut);
		if(flag){
			Long realizeShareId = Long.parseLong(JsonResult.getResult(reslut, "data"));
			//分享标题
			String title = "来就送5元！积分变现，就找财途积分钱包！";
			//分享描述
			String desc = "财途积分钱包喊你来领钱啦！";
			//分享链接
			String link = appConfig.caituUrl + "/realize_share/share.html?realizeShareId=" + realizeShareId;
			//分享图标
			String imgUrl = appConfig.caituUrl + "/realize_share/images/active.png";
			
			JSONObject json = new JSONObject();
			json.put("title", title);
			json.put("desc", desc);
			json.put("link", link);
			json.put("imgUrl", imgUrl);
			
			return result.toJSONString(0, "成功",json);
		}
		
		return reslut;
	}
}
