package com.caitu99.service.integral.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.BaseController;
import com.caitu99.service.integral.controller.auto.AutoFindAdapter;
import com.caitu99.service.integral.controller.spider.ManualQueryAdapter;

@Controller
@RequestMapping("/api/integral/manual/auto/")
public class ManualQueryAutoFindController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(ManualQueryAdapter.class);

	@Autowired
	private AutoFindAdapter autoFindAdapter;
	
	/**
	 * 用户手动查询成功后,触发自动发现
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: userFind 
	 * @param userid			用户ID
	 * @param manualId			积分账户ID
	 * @param loginAccount		账号
	 * @param password			密码
	 * @date 2015年12月18日 下午5:39:13  
	 * @author xiongbin
	 */
	@RequestMapping(value="find/user/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public void userFind(Long userid,Long manualId,String loginAccount,String password) {
		logger.info("【手动查询自动发现已关闭】");
//		if(null == userid){
//			logger.info("【手动查询自动发现失败】: 参数userid不能为空");
//		}else if(null == manualId){
//			logger.info("【手动查询自动发现失败】: 参数manualId不能为空");
//		}else if(StringUtils.isBlank(loginAccount)){
//			logger.info("【手动查询自动发现失败】: 参数loginAccount不能为空");
//		}else if(StringUtils.isBlank(password)){
//			logger.info("【手动查询自动发现失败】: 参数password不能为空");
//		}
//		
//		try {
//			autoFindAdapter.execute(manualId, userid, loginAccount, password);
//		} catch (Exception e) {
//			logger.error("【手动查询自动发现失败】:" + "userId：" + userid + "," + e.getMessage(),e);
//		}
	}
	
	/**
	 * 手动查询自动发现全部用户数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: userAllFind 
	 * @date 2015年12月22日 上午10:09:10  
	 * @author xiongbin
	 */
	@RequestMapping(value="find/userAll/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public void userAllFind() {
		logger.info("【手动查询自动发现已关闭】");
//		try {
//			autoFindAdapter.executeAll();
//		} catch (Exception e) {
//			logger.error("【手动查询自动发现失败】:" + e.getMessage(),e);
//		}
	}
}