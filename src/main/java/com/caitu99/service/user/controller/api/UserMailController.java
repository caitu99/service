package com.caitu99.service.user.controller.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.user.domain.UserMail;
import com.caitu99.service.user.service.MailDetailService;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.user.service.UserMailService;

@Controller
@RequestMapping("/api/user/mail")
public class UserMailController extends BaseController {

	// private static final Logger LOG = Logger
	// .getLogger(UserMailController.class);

	@Autowired
	private UserMailService userMailService;
	@Autowired
	private UserCardService userCardService;

	@Autowired
	private MailDetailService mailDetailService;

	/*
	 * // 删除用户邮箱
	 * 
	 * @RequestMapping("/test/mail/lists/1.0")
	 * 
	 * @ResponseBody public String testOMailLists() { // 初始化 ApiResult<Boolean>
	 * result = new ApiResult<Boolean>(); result.setCode(0);
	 * 
	 * List<MailDetail> mailDetails = mailDetailService.list(); List<MailSrc>
	 * mailSrcs = new ArrayList<MailSrc>(); // mainFuction(String mail, Long
	 * userId, List<MailSrc> mailList) for (int i = 0; i < mailDetails.size();
	 * i++) { MailSrc mailSrc = new MailSrc(); MailDetail mailDetail =
	 * mailDetails.get(i); mailSrc.setTitle(mailDetail.getMailTitle());
	 * mailSrc.setBody(mailDetail.getMailBody()); mailSrcs.add(mailSrc); } try {
	 * List<UserCard> userCards = mainFuction("328342832@qq.com", 2l, mailSrcs);
	 * } catch (Exception e) { e.printStackTrace(); }
	 * 
	 * return JSON.toJSONString(result); }
	 */
	// 删除用户邮箱
	@RequestMapping(value = "/del/1.0", produces = "application/json;charset=utf-8")
	@ResponseBody
	public String delMail(UserMail userMail) {
		// 初始化
		ApiResult<Boolean> result = new ApiResult<Boolean>();
		userMail.setStatus(0);
		int num = userMailService.updateByUserIdAndMail(userMail);
		// 数据返回
		if (num > 0) {
			result.setCode(0);
			result.setMessage("删除成功");
			result.setData(true);
		} else {
			result.setCode(2610);
			result.setMessage("删除失败");
			result.setData(false);
		}
		return JSON.toJSONString(result);
	}

	/*
	 * // 获取用户邮箱列表
	 * 
	 * @RequestMapping(value="/mail/list/1.0",
	 * produces="application/json;charset=utf-8")
	 * 
	 * @ResponseBody public String getMails(Long userId) {
	 * 
	 * ApiResult<List<UserMailVo>> result = new ApiResult<List<UserMailVo>>();
	 * 
	 * try { List<UserMail> userMailList = userMailService
	 * .selectByUserId(userId);
	 * 
	 * Iterator<UserMail> ite = userMailList.iterator(); List<UserMailVo> list =
	 * new ArrayList<UserMailVo>(); while (ite.hasNext()) { UserMailVo
	 * userMailVo = new UserMailVo(); userMailVo.setNeedFresh(false); UserMail
	 * userMail = ite.next(); BeanUtils.copyProperties(userMail, userMailVo);
	 * Date lastUpdate = userMail.getGmtLastUpdate(); Date now = new Date();
	 * UserCard queryCondition = new UserCard();
	 * queryCondition.setUserId(userId);
	 * queryCondition.setEmail(userMail.getEmail()); List<UserCard> usercards =
	 * userCardService .queryCardByUserIdAndMail(queryCondition);
	 * Iterator<UserCard> ites = usercards.iterator(); while (ites.hasNext()) {
	 * UserCard userCard = ites.next(); boolean isNeedFresh =
	 * isNeedFresh(userCard.getBillDay(), lastUpdate, now); if (isNeedFresh) {
	 * userMailVo.setNeedFresh(true); break; } } list.add(userMailVo); }
	 * 
	 * // 数据返回 result.setCode(0); result.setMessage("获取邮箱成功");
	 * result.setData(list); } catch (Exception e) { e.printStackTrace();
	 * result.setCode(2306); result.setMessage("获取邮箱列表失败"); return
	 * JSON.toJSONString(result); } return JSON.toJSONString(result); }
	 * 
	 * private boolean isNeedFresh(Integer billDay, Date lastUpdate, Date now) {
	 * Calendar billIncomingCal = Calendar.getInstance(); Calendar lastUpdateCal
	 * = Calendar.getInstance(); Calendar nowCal = Calendar.getInstance();
	 * lastUpdateCal.setTime(lastUpdate); nowCal.setTime(now);
	 * billIncomingCal.set(Calendar.YEAR, lastUpdateCal.get(Calendar.YEAR));
	 * billIncomingCal .set(Calendar.MONDAY,
	 * lastUpdateCal.get(Calendar.MONDAY)); if (null == billDay) {
	 * billIncomingCal.set(Calendar.DAY_OF_MONTH, 1); } else {
	 * billIncomingCal.set(Calendar.DAY_OF_MONTH, billDay); }
	 * billIncomingCal.add(Calendar.DAY_OF_MONTH, 3); if
	 * (billIncomingCal.before(lastUpdateCal)) {
	 * billIncomingCal.add(Calendar.MONDAY, 1); } if
	 * (billIncomingCal.before(nowCal)) { return true; } return false; }
	 */
}
