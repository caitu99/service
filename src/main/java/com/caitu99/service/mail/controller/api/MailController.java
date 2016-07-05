package com.caitu99.service.mail.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.user.controller.vo.UserMailVo;
import com.caitu99.service.user.domain.UserCard;
import com.caitu99.service.user.domain.UserMail;
import com.caitu99.service.user.service.UserCardService;
import com.caitu99.service.user.service.UserMailService;
import com.caitu99.service.utils.crypto.AESCryptoUtil;
import com.caitu99.service.utils.crypto.CryptoException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/api/mail")
public class MailController {

    private static final SimplePropertyPreFilter userMailFilter = new SimplePropertyPreFilter(UserMail.class,
            "email", "emailPassword", "emailPasswordAlone");

    @Autowired
    private UserMailService userMailService;

    @Autowired 
    private UserCardService userCardService;

    @RequestMapping(value="/list/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getMails(Long userId) {

        ApiResult<List<UserMailVo>> result = new ApiResult<List<UserMailVo>>();

        try {
            List<UserMail> userMailList = userMailService.selectByUserId(userId);

            Iterator<UserMail> ite = userMailList.iterator();
            List<UserMailVo> list = new ArrayList<UserMailVo>();
            while (ite.hasNext()) {
                UserMailVo userMailVo = new UserMailVo();
                userMailVo.setNeedFresh(false);
                UserMail userMail = ite.next();
                BeanUtils.copyProperties(userMail, userMailVo);
                Date lastUpdate = userMail.getGmtLastUpdate();
                Date now = new Date();
                UserCard queryCondition = new UserCard();
                queryCondition.setUserId(userId);
                queryCondition.setEmail(userMail.getEmail());
                List<UserCard> userCards = userCardService.queryCardByUserIdAndMail(queryCondition);
                for (UserCard userCard : userCards) {
                    boolean isNeedFresh = isNeedFresh(userCard.getBillDay(), lastUpdate, now);
                    if (isNeedFresh) {
                        userMailVo.setNeedFresh(true);
                        break;
                    }
                }
                list.add(userMailVo);
            }

            result.setCode(0);
            result.setMessage("获取邮箱成功");
            result.setData(list);
        } catch (Exception e) {
            e.printStackTrace();
            result.setCode(2306);
            result.setMessage("获取邮箱列表失败");
            return JSON.toJSONString(result);
        }

        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(UserMailVo.class, "email", "flag", "id",
                "isforward", "needFresh", "status", "userId", "gmtLastUpdate");
        return JSON.toJSONString(result, filter);
    }

    @RequestMapping(value="/del/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String delMail(String mail, Long userId) {

        // 初始化
        ApiResult<Boolean> result = new ApiResult<Boolean>();
        UserMail userMail = new UserMail();
        userMail.setUserId(userId);
        userMail.setEmail(mail);
        userMail.setStatus(0);
        userMailService.updateByPrimaryKeySelective(userMail);

        // 数据返回
        result.setCode(0);
        result.setMessage("删除成功");
        result.setData(true);
        return JSON.toJSONString(result);
    }

    @RequestMapping(value="/get/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getUserMail(String mail, Long userId) throws CryptoException {

        // 初始化
        ApiResult<UserMail> result = new ApiResult<>();
        UserMail userMail = new UserMail();
        userMail.setUserId(userId);
        userMail.setEmail(mail);

        userMail = userMailService.selectByUserIdAndMail(userMail);

        if (userMail != null) {
            String emailPassword = userMail.getEmailPassword();
            if (StringUtils.isNotBlank(emailPassword))
                userMail.setEmailPassword(AESCryptoUtil.decrypt(emailPassword));
            else
                userMail.setEmailPassword("");

            String emailPasswordAlone = userMail.getEmailPasswordAlone();
            if (StringUtils.isNotBlank(emailPasswordAlone))
                userMail.setEmailPasswordAlone(AESCryptoUtil.decrypt(emailPasswordAlone));
            else
                userMail.setEmailPasswordAlone("");

            result.setData(userMail);
        }

        // 数据返回
        result.setCode(0);
        result.setMessage("");
        return JSON.toJSONString(result, userMailFilter);
    }

    private boolean isNeedFresh(Integer billDay, Date lastUpdate, Date now) {
        Calendar billIncomingCal = Calendar.getInstance();
        Calendar lastUpdateCal = Calendar.getInstance();
        Calendar nowCal = Calendar.getInstance();
        lastUpdateCal.setTime(lastUpdate);
        nowCal.setTime(now);
        billIncomingCal.set(Calendar.YEAR, lastUpdateCal.get(Calendar.YEAR));
        billIncomingCal
                .set(Calendar.MONDAY, lastUpdateCal.get(Calendar.MONDAY));
        if (null == billDay) {
            billIncomingCal.set(Calendar.DAY_OF_MONTH, 1);
        } else {
            billIncomingCal.set(Calendar.DAY_OF_MONTH, billDay);
        }
        billIncomingCal.add(Calendar.DAY_OF_MONTH, 3);
        if (billIncomingCal.before(lastUpdateCal)) {
            billIncomingCal.add(Calendar.MONDAY, 1);
        }
        if (billIncomingCal.before(nowCal)) {
            return true;
        }
        return false;
    }

}
