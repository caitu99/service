package com.caitu99.service.realization.controller.spider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.goods.domain.GoodProp;
import com.caitu99.service.integral.controller.service.ManualUpdateJobThread;
import com.caitu99.service.integral.domain.ManualLogin;
import com.caitu99.service.integral.service.ManualLoginService;
import com.caitu99.service.realization.domain.RealizeDetail;
import com.caitu99.service.realization.domain.RealizeRecord;
import com.caitu99.service.realization.service.RealizeService;
import com.caitu99.service.sys.domain.Config;
import com.caitu99.service.sys.service.ConfigService;
import com.caitu99.service.transaction.service.OrderService;
import com.caitu99.service.utils.Configuration;
import com.caitu99.service.utils.calculate.CalculateUtils;
import com.caitu99.service.utils.http.HttpClientUtils;
/**
 * @Description: (中国移动积分变现) 
 * @ClassName: CMShopController 
 * @author Hongbo Peng
 * @date 2016年2月25日 下午5:34:53 
 * @Copyright (c) 2015-2020 by caitu99
 */
@Controller
@RequestMapping("/api/realization/cm/")
public class CMShopController extends BaseController{

	private final static Logger logger = LoggerFactory.getLogger(CMShopController.class);
	
	@Autowired
	private AppConfig appConfig;
	
	@Autowired
	private RealizeService realizeService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private ConfigService configService;
	@Autowired
	ManualLoginService manualLoginService;
	
	/**
	 * @Description: (登录)  
	 * @Title: login 
	 * @param userid
	 * @param account
	 * @param password
	 * @param vcode
	 * @param orderno
	 * @return
	 * @date 2016年2月25日 下午5:34:40  
	 * @author Hongbo Peng
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "login/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String login(Long userid, String account, String password, String vcode, Long realizeRecordId) {
        logger.debug("开始登录。。。");
        ApiResult<String> apiResult = new ApiResult<>();
        //业务实现
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("account", account);
        paramMap.put("password", password);
        paramMap.put("vcode", vcode);
        String postResult = null;
        try {
            postResult = HttpClientUtils.getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/cm/login/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            return apiResult.toJSONString(-1, "系统异常，请稍后再试");
        }
        
        logger.info("积分变现移动,登录,用户ID：{},返回数据:{}",userid,postResult);

		try{
			apiResult = JSON.parseObject(postResult, ApiResult.class);
		}catch(com.alibaba.fastjson.JSONException ee){
			ee.printStackTrace();
			return apiResult.toJSONString(-1, "登录失败，移动系统维护中，请稍后再试");
		}

        if (1019 == apiResult.getCode()) {   //登录成功
        	//拿到用户的积分
			JSONObject info_jsonObject = JSON.parseObject(JSON.parseObject(apiResult.getData()).getString("info"));
			Integer integral = info_jsonObject.getInteger("userCurrentIntegral");
			//获取变现方案
			RealizeRecord record = realizeService.selectById(realizeRecordId);
			//验证积分是否足够
			if(integral.intValue() < record.getIntegral().intValue()){
				return apiResult.toJSONString(-2, "您的积分不足");
			}
        	return apiResult.toJSONString(0, "登录成功");
        }else if(1087 == apiResult.getCode()
				|| 1088 == apiResult.getCode()
				|| 1092 == apiResult.getCode()
				|| 1119 == apiResult.getCode()
				|| 1081 == apiResult.getCode()
				|| 1089 == apiResult.getCode()
				|| 1001 == apiResult.getCode()){
			return apiResult.toString();
		}else{
			return apiResult.toJSONString(-1, "登录失败，移动系统维护中，请稍后再试");
		}
    }
	
	/**
	 * @Description: (下单)  
	 * @Title: getSms 
	 * @param userid
	 * @param realizeRecordId
	 * @return
	 * @date 2016年2月29日 下午6:23:45  
	 * @author Hongbo Peng
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "sms/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getSms(Long userid, Long realizeRecordId) {
		ApiResult<String> apiResult = new ApiResult<>();
		//获取变现记录
		RealizeRecord record = realizeService.selectById(realizeRecordId);
		//组装支付参数
		Map<String, Object> propMap = realizeService.getItemPayParams(record.getRealizeDetailId());
		if(null == propMap){
			logger.error("变现方案商品属性不存在");
			return apiResult.toJSONString(-3,"系统异常，请稍后再试");
		}
		List<GoodProp> goodPropList = (List<GoodProp>) propMap.get("propList");
		if(null == goodPropList){
			logger.error("变现方案商品属性不存在");
			return apiResult.toJSONString(-3,"系统异常，请稍后再试");
		}
        Map<String, Object> goodPropMap = this.getGoodProp(goodPropList);
        //万里通账户
        String wanlitongAccount = choiceWLTAccount(record.getIntegral());
        if(StringUtils.isEmpty(wanlitongAccount)){
        	logger.error("所有的万里通账户额度都满了，请补充万里通账户");
        	return apiResult.toJSONString(-3,"当前变现人数过多，请稍后再试");
        }
        JSONObject memoJson = JSONObject.parseObject(record.getMemo());
        memoJson.put("wanlitongAccount", wanlitongAccount);
        RealizeRecord editRecord = new RealizeRecord();
        editRecord.setId(realizeRecordId);
        editRecord.setMemo(memoJson.toJSONString());
        realizeService.updateRealizeRecord(editRecord);
        
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", String.valueOf(userid));
        paramMap.put("wareIds", goodPropMap.get("wareIds").toString());
        paramMap.put("amount", record.getQuantity().toString());
        paramMap.put("wanlitongAccount", wanlitongAccount);
        paramMap.put("price", propMap.get("price").toString());
        //下单发送验证码
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/cm/sms/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            return apiResult.toJSONString(-1, "系统异常，请稍后再试");
        }

        logger.info("积分变现移动,下单,用户ID：{},返回数据:{}",userid,postResult);
        
		try{
			apiResult = JSON.parseObject(postResult, ApiResult.class);
		}catch(com.alibaba.fastjson.JSONException ee){
			ee.printStackTrace();
			return apiResult.toJSONString(-1, "下单失败，移动系统维护中，请稍后再试");
		}
    	return apiResult.toString();
	}
	
	/**
	 * @Description: (支付)  
	 * @Title: pay 
	 * @param userid
	 * @param smscode
	 * @param realizeRecordId
	 * @param account
	 * @param password
	 * @return
	 * @date 2016年2月29日 下午6:24:02  
	 * @author Hongbo Peng
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "pay/1.0", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String pay(Long userid, String smscode, Long realizeRecordId, String account, String password) {
        logger.debug("开始支付。。。");
        ApiResult<String> apiResult = new ApiResult<>();
		//获取变现记录
		RealizeRecord record = realizeService.selectById(realizeRecordId);
		if(null == record){
			logger.info("变现记录不存在realizeRecordId：{}",realizeRecordId);
			return apiResult.toJSONString(-3,"系统异常，请稍后再试");
		}
		if(userid.longValue() != record.getUserId().longValue()){
			logger.info("支付用户和变现记录用户不是一个用户");
			return apiResult.toJSONString(-3,"非法的请求");
		}
		if(3 == record.getStatus().intValue()){
			return apiResult.toJSONString(-3,"变现已完成，请不要重复支付");
		}
		//判断用户的额度
		String userMaxStr = Configuration.getProperty("realize.cm.integral.user.max", null);
		Long userMax = Long.valueOf(userMaxStr);
		Long userSum = realizeService.getUserRealizeSUM(userid,account, null);
		if(null != userSum && CalculateUtils.add(userSum, record.getIntegral()).longValue() > userMax.longValue()){
			return apiResult.toJSONString(-3,"您已超出当月消费限制，请下个月再来");
		}
		
		//确认适合用户的变现方案 
		Long rdid = choiceRealizeDetail(userid, account, record.getRealizeId(), record.getRealizeDetailId(), record.getIntegral());
		if(null == rdid){
			return apiResult.toJSONString(-3,"您已超出当月消费限制，请选择小额兑换或下月再试");
		}
		if(rdid.longValue() != record.getRealizeDetailId()){
			realizeService.updateRealizeRecord(realizeRecordId, rdid);
			record.setRealizeDetailId(rdid);
		}
		
		//组装支付参数
		Map<String, Object> propMap = realizeService.getItemPayParams(record.getRealizeDetailId());
		if(null == propMap){
			logger.error("变现方案商品属性不存在");
			return apiResult.toJSONString(-3,"系统异常，请稍后再试");
		}
		List<GoodProp> goodPropList = (List<GoodProp>) propMap.get("propList");
        Map<String, Object> goodPropMap = this.getGoodProp(goodPropList);
        
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userid", userid.toString());
        paramMap.put("wareIds", goodPropMap.get("wareIds").toString());
        paramMap.put("smsCode", smscode);
        String postResult = null;
        try {
            postResult = HttpClientUtils
                    .getInstances()
                    .doPost(appConfig.spiderUrl + "/api/ishop/cm/order/1.0", "utf-8", paramMap);
        } catch (Exception e) {
            logger.error("请求lsp服务器失败", e);
            return apiResult.toJSONString(-1, "系统异常，请稍后再试");
        }

        logger.info("积分变现移动,支付,用户ID：{},返回数据:{}",userid,postResult);
        
		try{
			apiResult = JSON.parseObject(postResult, ApiResult.class);
		}catch(com.alibaba.fastjson.JSONException ee){
			ee.printStackTrace();
			return apiResult.toJSONString(-1, "支付失败，移动系统维护中，请稍后再试");
		}


        if (1083 == apiResult.getCode()) {
        	
			try {
				List<ManualLogin> manualLoginList = manualLoginService.findByUserIdManualId(userid, 8L);
				ManualUpdateJobThread thread = new ManualUpdateJobThread(manualLoginList);
				thread.run();
			} catch (Exception e) {
				logger.warn("积分变现移动，自动更新失败：{}",e);
			}
        	
            //成功
        	return realizeService.cmPayBusiness(userid,realizeRecordId,account,password);
        } else if(1090 == apiResult.getCode()){
        	return apiResult.toJSONString(-1, "短信验证码输入错误");
        } else if(1120 == apiResult.getCode()){
        	logger.info("支付失败,code:{},message:{}",apiResult.getCode(),apiResult.getMessage());
        	return apiResult.toJSONString(-1, "您的操作过于频繁，请24小时后再试");
        } else {
        	logger.info("支付失败,code:{},message:{}",apiResult.getCode(),apiResult.getMessage());
        	return apiResult.toJSONString(-1,"支付失败，移动系统维护中，请稍后再试");
        }
    }
	
	/**
	 * @Description: (根据用户的额度选择移动变现方案)  
	 * @Title: choiceRealizeDetail 
	 * @param userId
	 * @param realizeId
	 * @param realizeDetailId
	 * @param integral
	 * @return
	 * @date 2016年2月29日 下午6:25:22  
	 * @author Hongbo Peng
	 */
	private Long choiceRealizeDetail(Long userId,String account,Long realizeId,Long realizeDetailId,Long integral){
		Long userSum = realizeService.getUserRealizeSUM(userId, account, realizeDetailId);
		String userMaxStr = Configuration.getProperty("realize.cm.integral.user.max", null);
		Long userMax = Long.valueOf(userMaxStr);
		if(null != userSum && CalculateUtils.add(userSum, integral).longValue() > userMax.longValue()){
			RealizeDetail detail = realizeService.selectByLevel(realizeId, realizeDetailId);
			if(null == detail){
				return null;
			}
			return choiceRealizeDetail(userId,account, realizeId, detail.getId(), integral);
		}else {
			return realizeDetailId;
		}
		
	}
	/**
	 * @Description: (选择万里通帐号)  
	 * @Title: choiceWLTAccount 
	 * @param integral
	 * @return
	 * @date 2016年2月29日 下午6:25:36  
	 * @author Hongbo Peng
	 */
	private String choiceWLTAccount(Long integral){
		String maxStr = Configuration.getProperty("realize.cm.integral.user.max", null);
		Long max = Long.valueOf(maxStr);
		Config config = configService.selectByKey("wanlitong_account");
		if(null == config || StringUtils.isEmpty(config.getValue())){
			logger.error("未读取到配置的万里通帐号");
			return null;
		}
		String[] wanlitongAccounts = config.getValue().split(",");
		for (String string : wanlitongAccounts) {
			Long sum = realizeService.getWLTAccountSUM(string);
			if(null == sum || CalculateUtils.add(sum, integral).longValue() <= max.longValue()){
				return string;
			}
		}
		return null;
	}
	
	private Map<String, Object> getGoodProp(List<GoodProp> goodPropList) {
        Map<String, Object> goodPropMap = new HashMap<>();
        for (GoodProp goodProp : goodPropList) {
            goodPropMap.put(goodProp.getName(), goodProp.getValue());
        }
        return goodPropMap;
    }
	
	
}
