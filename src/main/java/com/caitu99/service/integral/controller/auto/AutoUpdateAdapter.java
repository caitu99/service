package com.caitu99.service.integral.controller.auto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.caitu99.service.integral.service.AutoFindRecordService;
import com.caitu99.service.integral.service.AutoFindRuleService;

@Component
public class AutoUpdateAdapter {

	private final static Logger logger = LoggerFactory.getILoggerFactory().getLogger("autoAndRefreshFileLogger");
	
	@Autowired
	private AutoFindRuleService autoFindRuleService;
	
	@Autowired
	private AutoFindRecordService autoFindRecordService;
	
	@Autowired
	private AutoFindJingDong autoFindJingDong;
	
	@Autowired
	private AutoFindCMB autoFindCMB;
	
	@Autowired
	private AutoFindAirChina autoFindAirChina;
	
	@Autowired
	private AutoFindCU autoFindCU;
	
	@Autowired
	private AutoFindIHG autoFindIHG;
	
	@Autowired
	private AutoFindCsAir autoFindCsAir;

	@Autowired
	private AutoFindYjf189 autoFindYjf189;
	@Autowired
	private AutoFindYjf189nt autoFindYjf189nt;
	@Autowired
	private AutoFindCMCC autoFindCMCC;
	@Autowired
	private AutoFindCOMM autoFindCOMM;
	@Autowired
	private AutoFindCCB autoFindCCB;
	@Autowired
	private AutoFindPingAn autoFindPingAn;
	
	/**
	 * 登录各平台
	 * 	
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: polling 
	 * @param userId
	 * @param manualId
	 * @param loginAccount
	 * @param password
	 * @return
	 * @date 2015年12月18日 上午11:28:12  
	 * @author ws
	 */
	public String updateAuto(Long userId,Long manualId,String loginAccount,String password, String version){
		String result = null;
		String msg = "";
		switch (manualId.intValue()) {
			//淘宝
			case 1:{
				msg = "请手动更新";
			}
				break;
			//招行
			case 2:{
				result = autoFindCMB.loginForUpdate(userId, loginAccount, password);
				msg = autoFindCMB.saveResult(userId, manualId, loginAccount, password, result, version);
			}
				break;
			/*//京东
			case 3:{
				result = autoFindJingDong.loginForUpdate(userId, loginAccount, password);
				msg = autoFindJingDong.saveResult(userId, manualId, loginAccount, password, result);
			}
				break;*/
			//天翼
			case 4:{
				
				String reg2 = "1(33|53|80|89|81|77)[0-9]{8}";
		        if(loginAccount.matches(reg2)){//电信用户
		        	result = autoFindYjf189.loginForUpdate(userId, loginAccount, password);
					msg = autoFindYjf189.saveResult(userId, manualId, loginAccount, password, result, version);
		        }else{//非电信用户
		        	result = autoFindYjf189nt.loginForUpdate(userId, loginAccount, password);
					msg = autoFindYjf189nt.saveResult(userId, manualId, loginAccount, password, result, version);
		        }
			}
				break;
			//国航
			case 5:{
				result = autoFindAirChina.loginForUpdate(userId, loginAccount, password);
				msg = autoFindAirChina.saveResult(userId, manualId, loginAccount, password, result, version);
			}
				break;
			//联通
			case 6:{
				result = autoFindCU.loginForUpdate(userId, loginAccount, password);
				msg = autoFindCU.saveResult(userId, manualId, loginAccount, password, result, version);
			}
				break;
			//南航
			case 7:{
				result = autoFindCsAir.loginForUpdate(userId, loginAccount, password);
				msg = autoFindCsAir.saveResult(userId, manualId, loginAccount, password, result, version);
			}
				break;
			//移动
			case 8:{
				result = autoFindCMCC.loginForUpdate(userId, loginAccount, password);
				msg = autoFindCMCC.saveResult(userId, manualId, loginAccount, password, result, version);
			}
				break;
			//洲际
			case 9:{
				result = autoFindIHG.loginForUpdate(userId, loginAccount, password);
				msg = autoFindIHG.saveResult(userId, manualId, loginAccount, password, result, version);
			}
				break;
			//花旗银行
			case 10:{
				msg = "请手动更新";
			}
				break;
			//铂涛会
			case 11:{
				msg = "请手动更新";
			}
				break;
			//物美
			case 12:{
				msg = "请手动更新";
			}
				break;
			//交通
			case 13:{
				result = autoFindCOMM.loginForUpdate(userId, loginAccount, password);
				msg = autoFindCOMM.saveResult(userId, manualId, loginAccount, password, result, version);
			}
				break;
			//建行
			case 14:{
				result = autoFindCCB.loginForUpdate(userId, loginAccount, password);
				msg = autoFindCCB.saveResult(userId, manualId, loginAccount, password, result, version);
			}
				break;
			//平安
			case 17:{
				result = autoFindPingAn.loginForUpdate(userId, loginAccount, password);
				msg = autoFindPingAn.saveResult(userId, manualId, loginAccount, password, result, version);
			}
				break;
			
			default:
				logger.info("【手动查询自动更新】:" + "暂不支持此积分账户" + manualId);
				break;
		}
		
		return msg;
	}
}
