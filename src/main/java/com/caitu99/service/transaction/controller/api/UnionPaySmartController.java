package com.caitu99.service.transaction.controller.api;

import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.transaction.controller.vo.AccountResult;
import com.caitu99.service.transaction.service.TransactionRecordService;
import com.caitu99.service.transaction.service.UnionPaySmartService;
import com.caitu99.service.user.api.IUserServiceApi;
import com.caitu99.service.user.domain.User;

@Controller
@RequestMapping("/api/transaction/unionpaysmart")
public class UnionPaySmartController extends BaseController {
	
	private final static Logger logger = LoggerFactory
			.getLogger(UnionPaySmartController.class);
	
	@Autowired
	private UnionPaySmartService unionPaySmartService;
	
	@Autowired
	private IUserServiceApi userServiceApi;
	
	@Autowired
	private TransactionRecordService transactionRecordService;
	
	@RequestMapping(value = "/recharge/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String recharge(Long clientId, String serialNo, Long userid, String mobile, Long point,
					Integer model, String userAccName, String userAccNo) {
		logger.info("clientId:{},serialNo:{},userid:{},mobile:{},point:{},model:{},userAccName:{},userAccNo:{}",
				clientId,serialNo,userid,mobile,point,model,userAccName,userAccNo);
		ApiResult<String> result = new ApiResult<>();
		User user = userServiceApi.addUser(mobile);
		if (serialNo.length() > 64) {
            return result.toJSONString(2071, "serialNo长度不符", "");
        }
		if (clientId != 1001) {
            return result.toJSONString(2074, "非法客户端", "");
        }
		
		if(30 != model.intValue() && 31 != model.intValue()){
			return result.toJSONString(2075, "参数错误", "");
		}
		if(31 == model.intValue() && (StringUtils.isBlank(userAccName) || StringUtils.isBlank(userAccNo))){
			return result.toJSONString(2075, "代付请传递银行卡姓名卡号", "");
		}
		Random random = new Random();
		String tNo = "union"+random.nextInt(10)+System.currentTimeMillis()
				+random.nextInt(10)+user.getId();
		try {
			AccountResult accountResult = unionPaySmartService.rechargeDirect(user.getId(), userid, point, serialNo, tNo, clientId, userAccName, userAccNo, model);
			if(0 == accountResult.getCode()){
				return result.toJSONString(0, "处理中", tNo);
			}
			if (accountResult.getCode() != 3101) {
                return result.toJSONString(accountResult.getCode(), accountResult.getResult(), "");
            }
		} catch (Exception e) {
			//修改状态为失败
			logger.error("银联智慧代充代付失败:{}",e);
			return result.toJSONString(-1, "系统异常", "");
		}
		return result.toJSONString(0, "充值成功", tNo);
	}
	

	@RequestMapping(value = "/job/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String jobquery(String transactionNumber) {
		return unionPaySmartService.queryAndHandle(transactionNumber);
	}
}
