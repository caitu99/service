package com.caitu99.service.merchant.controller;

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
import com.caitu99.service.merchant.domain.PersonnelManage;
import com.caitu99.service.merchant.service.PersonnelManageService;

@Controller
@RequestMapping("/api/merchant/manage")
public class PersonnelManageController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(PersonnelManageController.class);

	@Autowired
	private PersonnelManageService personnelManageService;

	/**
	 * 人员管理列表
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: list 
	 * @param userid
	 * @param personnelManage
	 * @param pagination
	 * @return
	 * @date 2016年6月20日 下午4:36:50  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/list/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String list(Long userid,PersonnelManage personnelManage, Pagination<PersonnelManage> pagination) {
		ApiResult<Pagination<PersonnelManage>> result = new ApiResult<Pagination<PersonnelManage>>();
		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}
		
		personnelManage.setAdminUserId(userid);
		
		pagination = personnelManageService.findPageItem(personnelManage, pagination);

		String[] LIST_FILLTER = {"id","name", "type","rate"};
		
		return result.toJSONString(0, "", pagination, PersonnelManage.class, LIST_FILLTER);
	}
	
	/**
	 * 人员详情
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: detail 
	 * @param id	人员管理ID
	 * @return
	 * @date 2016年6月20日 下午4:40:13  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/detail/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String detail(Long id) {
		ApiResult<PersonnelManage> result = new ApiResult<PersonnelManage>();
		if(null == id){
			return result.toJSONString(-1, "参数id不能为空");
		}
		
		PersonnelManage personnelManage = personnelManageService.selectByPrimaryKey(id);

		String[] LIST_FILLTER = {"id","name", "type","rate","contacts","loginAccount","province","city"};
		
		return result.toJSONString(0, "", personnelManage, PersonnelManage.class, LIST_FILLTER);
	}
	
	/**
	 * 新增
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: insert 
	 * @param userid		管理员用户ID
	 * @param personnelManage
	 * @return
	 * @date 2016年6月20日 下午4:45:05  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/insert/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String insert(Long userid,PersonnelManage personnelManage) {
		ApiResult<String> result = new ApiResult<String>();
		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}else if(null == personnelManage){
			return result.toJSONString(-1, "参数personnelManage不能为空");
		}else if(null == personnelManage.getType()){
			return result.toJSONString(-1, "参数type不能为空");
		}else if(!personnelManage.getType().equals(1) && !personnelManage.getType().equals(2)){
			return result.toJSONString(-1, "参数type传参错误");
		}else if(StringUtils.isBlank(personnelManage.getName())){
			return result.toJSONString(-1, "参数name不能为空");
		}else if(StringUtils.isBlank(personnelManage.getContacts())){
			return result.toJSONString(-1, "参数contacts不能为空");
		}else if(StringUtils.isBlank(personnelManage.getProvince())){
			return result.toJSONString(-1, "参数province不能为空");
		}else if(StringUtils.isBlank(personnelManage.getCity())){
			return result.toJSONString(-1, "参数city不能为空");
		}else if(null == personnelManage.getRate()){
			return result.toJSONString(-1, "参数rate不能为空");
		}else if(StringUtils.isBlank(personnelManage.getLoginAccount())){
			return result.toJSONString(-1, "参数loginAccount不能为空");
		}
		
		try {
			Integer rate = Integer.parseInt(personnelManage.getRate());
			if(rate > 100 || rate < 0){
				return result.toJSONString(-1, "费率比例错误");
			}
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(),e);
			return result.toJSONString(-1, "费率错误");
		}

		personnelManage.setAdminUserId(userid);
		return personnelManageService.create(personnelManage);
	}
	
	/**
	 * 修改
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: update 
	 * @param userid
	 * @param personnelManage
	 * @return
	 * @date 2016年6月20日 下午6:19:29  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/update/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String update(Long userid,PersonnelManage personnelManage) {
		ApiResult<String> result = new ApiResult<String>();
		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}else if(null == personnelManage){
			return result.toJSONString(-1, "参数personnelManage不能为空");
		}else if(null == personnelManage.getType()){
			return result.toJSONString(-1, "参数type不能为空");
		}else if(!personnelManage.getType().equals(1) && !personnelManage.getType().equals(2)){
			return result.toJSONString(-1, "参数type传参错误");
		}else if(StringUtils.isBlank(personnelManage.getName())){
			return result.toJSONString(-1, "参数name不能为空");
		}else if(StringUtils.isBlank(personnelManage.getContacts())){
			return result.toJSONString(-1, "参数contacts不能为空");
		}else if(StringUtils.isBlank(personnelManage.getProvince())){
			return result.toJSONString(-1, "参数province不能为空");
		}else if(StringUtils.isBlank(personnelManage.getCity())){
			return result.toJSONString(-1, "参数city不能为空");
		}else if(null == personnelManage.getRate()){
			return result.toJSONString(-1, "参数rate不能为空");
		}else if(StringUtils.isBlank(personnelManage.getLoginAccount())){
			return result.toJSONString(-1, "参数loginAccount不能为空");
		}
		
		try {
			Integer rate = Integer.parseInt(personnelManage.getRate());
			if(rate > 100 || rate < 0){
				return result.toJSONString(-1, "费率比例错误");
			}
		} catch (NumberFormatException e) {
			logger.error(e.getMessage(),e);
			return result.toJSONString(-1, "费率错误");
		}
		
		personnelManage.setAdminUserId(userid);
		return personnelManageService.modify(personnelManage);
	}
	
	/**
	 * 删除
	 * @Description: (方法职责详细描述,可空)  
	 * @Title: detele 
	 * @param userid	用户ID
	 * @param id
	 * @return
	 * @date 2016年6月22日 下午4:17:06  
	 * @author xiongbin
	 */
	@RequestMapping(value = "/detele/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String detele(Long userid,Long id) {
		ApiResult<String> result = new ApiResult<String>();
		if(null == userid){
			return result.toJSONString(-1, "参数userid不能为空");
		}else if(null == id){
			return result.toJSONString(-1, "参数id不能为空");
		}
		
		PersonnelManage personnelManage = new PersonnelManage();
		personnelManage.setAdminUserId(userid);
		personnelManage.setId(id);
		return personnelManageService.detele(personnelManage);
	}
}
