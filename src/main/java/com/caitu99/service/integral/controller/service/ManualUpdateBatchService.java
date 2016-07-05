/*
 * Copyright (c) 2015-2020 by caitu99
 * All rights reserved.
 */
package com.caitu99.service.integral.controller.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CyclicBarrier;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.AppConfig;
import com.caitu99.service.constants.SysConstants;
import com.caitu99.service.integral.controller.auto.AutoUpdateAdapter;
import com.caitu99.service.integral.controller.vo.ManualBatchVo;
import com.caitu99.service.integral.dao.ManualResultMapper;
import com.caitu99.service.integral.domain.ManualResult;
import com.caitu99.service.user.service.UserCardService;

/** 
 * 
 * @Description: (类职责详细描述,可空) 
 * @ClassName: ManualUpdateBatchService 
 * @author ws
 * @date 2016年2月24日 上午10:20:41 
 * @Copyright (c) 2015-2020 by caitu99 
 */
@Service
public class ManualUpdateBatchService {

	private static final Logger logger = LoggerFactory
			.getLogger(ManualUpdateBatchService.class);

	private String MANUAL_UPDATE_BATCH_ERR_MSG = "【批量实时更新失败】：userId:{},manualId:{},loginAccount:{},password:{},errMsg:{}";
	private String MANUAL_UPDATE_BATCH_WARN_MSG = "【批量实时更新失败】：userId:{},manualId:{},loginAccount:{},password:{},msg:{}";
	
	@Autowired
	AutoUpdateAdapter autoUpdateAdapter;
	@Autowired
	UserCardService userCardService;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	ManualResultMapper manualResultMapper;
	
	public void updateManualIntegral(List<ManualBatchVo> manualAccounts,String version){
		for (ManualBatchVo manualAccount : manualAccounts) {
			try {
				String result = autoUpdateAdapter.updateAuto(manualAccount.getUserId()
						, manualAccount.getManualId()
						, manualAccount.getLoginAccount()
						, manualAccount.getPassword(), version);
				
				JSONObject addInfo = null;
				JSONObject data = null;
				if(result.contains("{")){//需要赠送途币
					data = JSON.parseObject(result);
					addInfo = data.getJSONObject(manualAccount.getManualId().toString());
					result = data.getString("message");
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("userId", manualAccount.getUserId());
				map.put("manualId", manualAccount.getManualId());
				map.put("loginAccount", manualAccount.getLoginAccount());
				List<ManualResult> manualInfos = userCardService.getByUserManualInfo(map );
				if(null == manualInfos){
					logger.warn(MANUAL_UPDATE_BATCH_WARN_MSG,manualAccount.getUserId()
							, manualAccount.getManualId()
							, manualAccount.getLoginAccount()
							, manualAccount.getPassword()
							, "卡片信息无效");
					continue;
				}
				for (ManualResult manualInfo : manualInfos) {
					if(StringUtils.isNotBlank(manualInfo.getPicUrl())){
						//补全图片路径
						manualInfo.setPicUrl(appConfig.staticUrl + manualInfo.getPicUrl());
					}
					if(result.equals(SysConstants.SUCCESS)){
						//成功
						manualInfo.setResult(SysConstants.SUCCESS);
						manualInfo.setRemark("");
					}else{
						//失败
						manualInfo.setResult(SysConstants.FAILURE);
						if(result.contains("图片")){
							result = "系统繁忙";
						}
						manualInfo.setRemark(result);
					}
					manualInfo.setUpdateTime(new Date());
					if(null != addInfo){
						manualInfo.setAddIntegral(addInfo.getString("addIntegral"));
						manualInfo.setAddTubi(addInfo.getString("addTubi"));
					}else{
						manualInfo.setAddIntegral("0");
						manualInfo.setAddTubi("0");
					}

					String cardNo = manualInfo.getCardNo();
					if(StringUtils.isNotBlank(cardNo)){
						if(cardNo.length() > 4){
							manualInfo.setCardNo(cardNo.substring(cardNo.length() - 4));
						}
					}
					
					//插入到表中
					manualResultMapper.insert(manualInfo);
				}
			} catch (Exception e) {
				e.getStackTrace();
				logger.error(MANUAL_UPDATE_BATCH_ERR_MSG,manualAccount.getUserId()
						, manualAccount.getManualId()
						, manualAccount.getLoginAccount()
						, manualAccount.getPassword()
						,e);
			}
		}
	}

}
