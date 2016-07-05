package com.caitu99.service.manage.controller;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.manage.domain.BankCard;
import com.caitu99.service.manage.domain.ManageCardDropInRecord;
import com.caitu99.service.manage.domain.ManageCardUserAuth;
import com.caitu99.service.manage.service.BankCardService;
import com.caitu99.service.manage.service.ManageCardDropInRecordService;
import com.caitu99.service.manage.service.ManageCardOnLineRecordService;
import com.caitu99.service.manage.service.ManageCardUserAuthService;
import com.caitu99.service.user.domain.UserAuth;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.string.IdCardValidator;
import com.caitu99.service.utils.string.StrUtil;

@Controller
@RequestMapping("/api/manage/bank/")
public class ManageCardController extends BaseController{
	
	private final static Logger logger = LoggerFactory.getLogger(ManageCardController.class);
	
	private static final String[] BANKCARD_LIST_FILLTER = {"id","bankName","icon","url","remark","cornerMark"};
	
	private static final String[] RESERVATION_LIST_FILLTER = {"id","bankCardIcon","bankCardName","gmtCreate","remark","specialManager","specialTel","status"};
	
	private static final String[] MANAGE_CARD_USER_AUTH_FILLTER = {"name","idCard","phone"};
	
	@Autowired
	private BankCardService bankCardService;
	@Autowired
	private ManageCardOnLineRecordService manageCardOnLineRecordService;
	@Autowired
	private ManageCardDropInRecordService manageCardDropInRecordService;
	@Autowired
	private UserService userService;
	@Autowired
	private ManageCardUserAuthService manageCardUserAuthService;
	
	/**
	 * 查询在线办理银行卡
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: onLineList 
	 * @return
	 * @date 2015年12月25日 下午12:02:24  
	 * @author xiongbin
	 */
	@RequestMapping(value="online/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String onLineList() {
		List<BankCard> list = bankCardService.selectOnLinePageList();
		ApiResult<List<BankCard>> result = new ApiResult<List<BankCard>>();
		return result.toJSONString(0,"success",list,BankCard.class,BANKCARD_LIST_FILLTER);
	}
	
	/**
	 * 查询上门办理银行卡
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: dropInList 
	 * @return
	 * @date 2015年12月25日 下午12:02:24  
	 * @author xiongbin
	 */
	@RequestMapping(value="dropIn/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String dropInList() {
		List<BankCard> list = bankCardService.selectDropInPageList();
		ApiResult<List<BankCard>> result = new ApiResult<List<BankCard>>();
		return result.toJSONString(0,"success",list,BankCard.class,BANKCARD_LIST_FILLTER);
	}
	
	/**
	 * 根据id查询银行
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: dropInList 
	 * @return
	 * @date 2015年12月25日 下午12:02:24  
	 * @author xiongbin
	 */
	@RequestMapping(value="select/primaryKey/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String selectByPrimaryKey(Long bankCardId) {
		ApiResult<BankCard> result = new ApiResult<BankCard>();
		
		if(null == bankCardId){
			return result.toJSONString(-1, "参数bankCardId为空");
		}
		
		BankCard bankCard = bankCardService.selectByPrimaryKey(bankCardId);
		return result.toJSONString(0,"success",bankCard,BankCard.class,BANKCARD_LIST_FILLTER);
	}
	
	/**
	 * 保存用户在线办卡数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: dropInList 
	 * @return
	 * @date 2015年12月25日 下午12:02:24  
	 * @author xiongbin
	 */
	@RequestMapping(value="save/online/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String saveOnLine(Long userid,Long bankCardId) {
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		
		if(null == bankCardId){
			return result.toJSONString(-1, "参数bankCardId为空");
		}else if(null == userid){
			return result.toJSONString(-1, "参数userid为空");
		}
		
		UserAuth userAuth = userService.getUserAuth(userid);
		if(null == userAuth){
			ManageCardUserAuth manageCardUserAuth = manageCardUserAuthService.selectByUserId(userid);
			if(null == manageCardUserAuth){
				logger.warn("用户未认证,useId:" + userid);
				return result.toJSONString(2048, "您还未认证,请先认证");
			}
			
			userAuth = new UserAuth();
			userAuth.setAccId(manageCardUserAuth.getIdCard());
			userAuth.setAccName(manageCardUserAuth.getName());
			userAuth.setUserId(manageCardUserAuth.getUserId());
			userAuth.setMobile(manageCardUserAuth.getPhone());
		}
		
		manageCardOnLineRecordService.insert(userAuth, bankCardId);
		return result.toJSONString(0,"success");
	}
	
	/**
	 * 保存用户上门办卡数据
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: saveDropIn 
	 * @param userid
	 * @param manageCardDropInRecord
	 * @return
	 * @date 2015年12月28日 下午9:29:33  
	 * @author xiongbin
	 */
	@RequestMapping(value="save/dropIn/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String saveDropIn(Long userid,ManageCardDropInRecord manageCardDropInRecord) {
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		
		if(null == userid){
			return result.toJSONString(-1, "参数userid为空");
		}
		
		UserAuth userAuth = userService.getUserAuth(userid);
		if(null == userAuth){
			ManageCardUserAuth manageCardUserAuth = manageCardUserAuthService.selectByUserId(userid);
			if(null == manageCardUserAuth){
				logger.warn("用户未认证,useId:" + userid);
				return result.toJSONString(2048, "您还未认证,请先认证");
			}
			
			userAuth = new UserAuth();
			userAuth.setAccId(manageCardUserAuth.getIdCard());
			userAuth.setAccName(manageCardUserAuth.getName());
			userAuth.setUserId(manageCardUserAuth.getUserId());
			userAuth.setMobile(manageCardUserAuth.getPhone());
		}
		
		if(null == manageCardDropInRecord){
			logger.warn("参数manageCardDropInRecord为空,useId:" + userid);
			return result.toJSONString(-1, "您填写的信息不全,请完善您的信息");
		}
		
		if("有本行信用卡".equals(manageCardDropInRecord.getCreditCardInfo())){
			logger.warn("已有本行信用卡,useId:" + userid);
			return result.toJSONString(-1, "已有本行信用卡");
		}
		//银行卡ID
		Long bankCardId = manageCardDropInRecord.getBankCardId();
		if(null == bankCardId){
			logger.warn("参数bankCardId为空,useId:" + userid);
			return result.toJSONString(-1, "参数bankCardId为空");
		}
		BankCard bankCard = bankCardService.selectByPrimaryKey(bankCardId);
		if(null == bankCard){
			logger.warn("未查到bankCardId:" + bankCardId + ",银行信息");
			return result.toJSONString(-1, "非常抱歉,该银行业务已关闭");
		}
		
		manageCardDropInRecord.setBankCardIcon(bankCard.getIcon());
		manageCardDropInRecord.setBankCardName(bankCard.getBankName());
		
		manageCardDropInRecordService.insert(userAuth, manageCardDropInRecord);
		return result.toJSONString(0,"success");
	}
	
	/**
	 * 我的预约分页查询
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: list 
	 * @param item
	 * @param pagination
	 * @return
	 * @date 2015年11月24日 下午8:07:34  
	 * @author xiongbin
	 */
	@RequestMapping(value="reservation/list/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String list(ManageCardDropInRecord manageCardDropInRecord, Pagination<ManageCardDropInRecord> pagination) {
		ApiResult<Pagination<ManageCardDropInRecord>> result = new ApiResult<Pagination<ManageCardDropInRecord>>();
		
		Long userId = manageCardDropInRecord.getUserId();
		if(null == userId){
			return result.toJSONString(-1, "用户ID为空");
		}
		
		pagination = manageCardDropInRecordService.findPageRecord(manageCardDropInRecord, pagination);
		return result.toJSONString(0,"success",pagination,ManageCardDropInRecord.class,RESERVATION_LIST_FILLTER);
	}
	
	/**
	 * 实名认证
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: userAuth 
	 * @param userid		用户ID
	 * @param name			姓名
	 * @param idCard		身份证
	 * @param phone			手机号
	 * @return
	 * @date 2016年2月22日 下午4:32:11  
	 * @author xiongbin
	 */
	@RequestMapping(value="user/auth/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String userAuth(Long userid,String name,String idCard,String phone) {
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		
		try {
			if(null == userid){
				return result.toJSONString(-1, "参数userid为空");
			}else if(StringUtils.isBlank(name)){
				return result.toJSONString(-1, "参数name为空");
			}else if(StringUtils.isBlank(idCard)){
				return result.toJSONString(-1, "参数idCard为空");
			}else if(StringUtils.isBlank(phone)){
				return result.toJSONString(-1, "参数phone为空");
			}
			
			//验证是否认证过
			boolean isAuth = manageCardUserAuthService.isAuth(userid);
			if(isAuth){
				return result.toJSONString(-1, "您已认证过,无效再次认证");
			}
			
			//验证身份证
			boolean isIdCard = IdCardValidator.valideIdCard(idCard);
			if(!isIdCard){
				return result.toJSONString(-1, "请输入正确的身份证");
			}
			
			//验证手机号
			boolean isPhone = StrUtil.isPhone(phone);
			if(!isPhone){
				return result.toJSONString(-1, "请输入正确的手机号");
			}
			
			ManageCardUserAuth manageCardUserAuth = new ManageCardUserAuth();
			manageCardUserAuth.setIdCard(idCard);
			manageCardUserAuth.setName(name);
			manageCardUserAuth.setPhone(phone);
			manageCardUserAuth.setUserId(userid);
			
			manageCardUserAuthService.insert(manageCardUserAuth);
			
			return result.toJSONString(0, "认证成功");
		} catch (Exception e) {
			logger.error("在线办卡实名认证失败:" + e.getMessage(),e);
			return result.toJSONString(-1, "认证失败");
		}
	}
	
	/**
	 * 查询用户在线办卡实名认证信息
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: userJudge 
	 * @param userid		用户ID
	 * @return
	 * @date 2016年2月22日 下午4:42:48  
	 * @author xiongbin
	 */
	@RequestMapping(value="user/judge/1.0", produces="application/json;charset=utf-8")
	@ResponseBody
	public String userJudge(Long userid) {
		ApiResult<ManageCardUserAuth> result = new ApiResult<ManageCardUserAuth>();
		
		try {
			if(null == userid){
				return result.toJSONString(-1, "参数userid为空");
			}
			
			ManageCardUserAuth manageCardUserAuth = manageCardUserAuthService.selectByUserId(userid);
			if(null == manageCardUserAuth){
				return result.toJSONString(2048, "您还未认证过,请先认证");
			}
			
			return result.toJSONString(0, "succeed",manageCardUserAuth,ManageCardUserAuth.class,MANAGE_CARD_USER_AUTH_FILLTER);
		} catch (Exception e) {
			logger.error("查询用户在线办卡实名认证失败:" + e.getMessage(),e);
			return result.toJSONString(-1, "查询用户在线办卡实名认证失败");
		}
	}
}