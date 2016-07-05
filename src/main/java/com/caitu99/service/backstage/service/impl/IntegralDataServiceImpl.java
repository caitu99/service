package com.caitu99.service.backstage.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.caitu99.service.backstage.domain.IntegralAccountInfo;
import com.caitu99.service.backstage.domain.IntegralChangeInfo;
import com.caitu99.service.backstage.domain.IntegralData;
import com.caitu99.service.backstage.domain.IntegralIncreaseInfo;
import com.caitu99.service.backstage.domain.IntegralReduceInfo;
import com.caitu99.service.backstage.service.IntegralDataService;
import com.caitu99.service.integral.dao.CardTypeMapper;
import com.caitu99.service.integral.dao.ExchangeRuleMapper;
import com.caitu99.service.integral.dao.UserCardManualMapper;
import com.caitu99.service.integral.domain.CardType;
import com.caitu99.service.integral.domain.ExchangeRule;
import com.caitu99.service.transaction.dao.AccountDetailMapper;
import com.caitu99.service.transaction.dao.AccountMapper;
import com.caitu99.service.transaction.dao.TransactionRecordMapper;
import com.caitu99.service.transaction.domain.Account;
import com.caitu99.service.transaction.domain.AccountDetail;
import com.caitu99.service.transaction.domain.TransactionRecord;
import com.caitu99.service.user.dao.UserCardMapper;
import com.caitu99.service.user.domain.UserCard;

/**
 * Created by chenhl on 2016/2/15.
 */
@Service
public class IntegralDataServiceImpl implements IntegralDataService {

    private final static Logger logger = LoggerFactory.getLogger(IntegralDataServiceImpl.class);

    @Autowired
    private TransactionRecordMapper transactionRecordMapper;

    @Autowired
    private AccountDetailMapper accountDetailMapper;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private CardTypeMapper cardTypeMapper;

    @Autowired
    private ExchangeRuleMapper exchangeRuleMapper;

    @Autowired
    private UserCardManualMapper userCardManualMapper;

    @Autowired
    private UserCardMapper userCardMapper;

    @Override
    public IntegralData selectByTime(Date startTime, Date endTime) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();

        Long change2 = 0L;
        IntegralData integralData = new IntegralData();
        Map<String,String> map = new HashMap<String,String>();
        map.put("begin", sdf.format(startTime));
        map.put("end", sdf.format(endTime));
        map.put("memo","测试");

        
      

        List<TransactionRecord> transactionRecordList =  transactionRecordMapper.selectByTime(map);
        List<AccountDetail> accountDetailList = accountDetailMapper.sumIntegerByTime(map);
        Long accountList = accountMapper.selectAllCount();
        
        Date nowTime = cal.getTime();
        map.put("end", sdf.format(nowTime));
        
        Long change1 =  transactionRecordMapper.sumPtTotal(map);
        List<AccountDetail> accountDetailList2 = accountDetailMapper.sumIntegerByTime(map);

        if(null == change1){
        	change1 = 0L;
        }
        
        for( TransactionRecord tr : transactionRecordList ){
            //1:消费 2：兑入3：充值4：提现5：累积6：赠送 8转入
            if( tr.getType() == 2 ||  tr.getType() == 3 ||  tr.getType() == 5 || tr.getType() == 8){  //用户入分
                integralData.setUserIncreaseIntegral(integralData.getUserIncreaseIntegral() + tr.getTotal());
            }else if(tr.getType() == 1 ||  tr.getType() == 4 || tr.getType() == 9){  //用户出分
                integralData.setUserReduceIntegral(integralData.getUserReduceIntegral() + Math.abs(tr.getTotal()));
            }else if( tr.getType() == 6 ){   //送分
                integralData.setGiftIntegral( integralData.getGiftIntegral() + tr.getTotal() );
            }else if( tr.getType() == 10 || tr.getType() == 7 ){
                //忽略
            }else{
                logger.info("TransactionRecord.Type 有新的 类型 ：{}", tr.getType());
            }
        }

        for( AccountDetail ad : accountDetailList ){
            if( ad.getType() == 1 ){ // 1入分；2出分
                integralData.setTestIncreaseIntegral(ad.getIntegralChange() ); //测试入分
            }else if( ad.getType() == 2 ){
                integralData.setTestReduceIntegarl(ad.getIntegralChange()); //测试出分
            }else{
                logger.info("AccountDetail.Type 有新的 类型 ：{}", ad.getType());
            }
        }
 

        for( AccountDetail ad : accountDetailList2 ){
            if( ad.getType() == 1 ){ // 1入分；2出分
            	change2 += ad.getIntegralChange();
            }else if( ad.getType() == 2 ){
            	change2 -= ad.getIntegralChange();
            }else{
                logger.info("AccountDetail.Type 有新的 类型 ：{}", ad.getType());
            }
        }
        
        //入分汇总 = 用户入分 + 送分 + 测试入分
        integralData.setIncreaseIntegralTotal( integralData.getUserIncreaseIntegral()
                                             + integralData.getGiftIntegral()
                                             + integralData.getTestIncreaseIntegral());

        //出分汇总 = 用户出分 + 测试出分
        integralData.setReduceIntegralTotal( integralData.getUserReduceIntegral()
                                             + integralData.getTestReduceIntegarl());

        //财币变动
        integralData.setTotalChange( integralData.getIncreaseIntegralTotal() + integralData.getReduceIntegralTotal() );

        //平台总财币  = 最新总财币 - 变化数
        integralData.setTotalIntegral(accountList - change1 - change2);

        return integralData;
    }

    @Override
    public List<IntegralIncreaseInfo> selectByInIntegral(Long userId, Date startTime, Date endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date nowTime = cal.getTime();
        Map<Object, Object> map = new HashMap<>();
        map.put("begin", sdf.format(startTime));
        map.put("end", sdf.format(endTime));
        map.put("userId", userId);
        map.put("memo", "测试");
        Map<Object, Object> map2 = new HashMap<>();
        map2.put("begin", sdf.format(endTime));
        map2.put("end", sdf.format(nowTime));
        map2.put("userId", userId);
        map2.put("memo", "测试");
        List<TransactionRecord> transactionRecordList = transactionRecordMapper.selectByUserIdAndTime(map);
        Account account = accountMapper.selectByUserId(userId);
        List<AccountDetail> accountDetailList = accountDetailMapper.selectByUserIdAndTime(map2);
        List<IntegralIncreaseInfo> list = new ArrayList<>();
        Long change = 0L;

        //统计用户endTime到nowTime测试积分变动
        for( AccountDetail ad : accountDetailList ){
            if( ad.getType() == 1 ){ // 1入分；2出分
                change = change + ad.getIntegralChange();
            }else if( ad.getType() == 2 ){
                change = change - Math.abs(ad.getIntegralChange());
            }
        }

        for(TransactionRecord tr : transactionRecordList ){
            IntegralIncreaseInfo integralIncreaseInfo = new IntegralIncreaseInfo();
            integralIncreaseInfo.setTime(tr.getCreateTime());
            integralIncreaseInfo.setUID(tr.getUserId());

            //1:消费 2：兑入3：充值4：提现5：累积6：赠送
            if(tr.getType() == 2){
                integralIncreaseInfo.setType("兑入");
                change = change + tr.getTotal();
                integralIncreaseInfo.setIntegral(tr.getTotal());
                if(tr.getSource() == 21 && (tr.getInfo().contains("中信银行"))){
                    CardType cardType = cardTypeMapper.selectByName("中信银行");
                    ExchangeRule exchangeRule = exchangeRuleMapper.findByCardTypeId(cardType.getId());
                    integralIncreaseInfo.setRatio("中信银行（" + exchangeRule.getScaleStr() + ")");
                }else {
                    integralIncreaseInfo.setRatio("--");
                }
                integralIncreaseInfo.setTotal(account.getTotalIntegral() - change);
                list.add(integralIncreaseInfo);
            }else if(tr.getType() == 3){
                integralIncreaseInfo.setType("充值");
                change = change + tr.getTotal();
                integralIncreaseInfo.setIntegral(tr.getTotal());
                integralIncreaseInfo.setRatio("--");
                integralIncreaseInfo.setTotal(account.getTotalIntegral() - change);
                list.add(integralIncreaseInfo);
            }else if(tr.getType() == 5){
                integralIncreaseInfo.setType("累积");
                change = change + tr.getTotal();
                integralIncreaseInfo.setIntegral(tr.getTotal());
                integralIncreaseInfo.setRatio("--");
                integralIncreaseInfo.setTotal(account.getTotalIntegral() - change);
                list.add(integralIncreaseInfo);
            }else if(tr.getType() == 6){
                integralIncreaseInfo.setType("赠送");
                change = change + tr.getTotal();
                integralIncreaseInfo.setIntegral(tr.getTotal());
                integralIncreaseInfo.setRatio("--");
                integralIncreaseInfo.setTotal(account.getTotalIntegral() - change);
                list.add(integralIncreaseInfo);
            }else if(tr.getType() == 8){
                integralIncreaseInfo.setType("转账(转入)");
                change = change + tr.getTotal();
                integralIncreaseInfo.setIntegral(tr.getTotal());
                integralIncreaseInfo.setRatio("--");
                integralIncreaseInfo.setTotal(account.getTotalIntegral() - change);
                list.add(integralIncreaseInfo);
            }else if(tr.getType() == 1 ||  tr.getType() == 4 || tr.getType() == 9){  //用户出分
                change = change - Math.abs(tr.getTotal());
            }

        }
        return list;
    }

    @Override
    public List<IntegralReduceInfo> selectByOutIntegral(Long userId, Date startTime, Date endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date nowTime = cal.getTime();
        Map<Object, Object> map = new HashMap<>();
        map.put("begin", sdf.format(startTime));
        map.put("end", sdf.format(endTime));
        map.put("userId", userId);
        map.put("memo", "测试");
        Map<Object, Object> map2 = new HashMap<>();
        map2.put("begin", sdf.format(endTime));
        map2.put("end", sdf.format(nowTime));
        map2.put("userId", userId);
        map2.put("memo", "测试");
        List<TransactionRecord> transactionRecordList = transactionRecordMapper.selectByUserIdAndTime(map);
        Account account = accountMapper.selectByUserId(userId);
        List<AccountDetail> accountDetailList2 = accountDetailMapper.selectByUserIdAndTime(map2);
        List<IntegralReduceInfo> list = new ArrayList<>();
        Long change = 0L;


        //统计用户endTime到nowTime积分变动
        for( AccountDetail ad : accountDetailList2 ){
            if( ad.getType() == 1 ){ // 1入分；2出分
                change = change + ad.getIntegralChange();
            }else if( ad.getType() == 2 ){
                change = change - Math.abs(ad.getIntegralChange());
            }
        }

        for(TransactionRecord tr : transactionRecordList ){
            IntegralReduceInfo integralReduceInfo = new IntegralReduceInfo();
            integralReduceInfo.setTime(tr.getCreateTime());
            integralReduceInfo.setUID(tr.getUserId());

            //1:消费 2：兑入3：充值4：提现5：累积6：赠送
            if(tr.getType() == 1){
                integralReduceInfo.setType("消费");
                integralReduceInfo.setIntegral(tr.getTotal());
                change = change - Math.abs(tr.getTotal());
                integralReduceInfo.setTotal(account.getTotalIntegral() - change);
                list.add(integralReduceInfo);
            }else if(tr.getType() == 4){
                integralReduceInfo.setIntegral(tr.getTotal());
                integralReduceInfo.setType("提现");
                change = change - Math.abs(tr.getTotal());
                integralReduceInfo.setTotal(account.getTotalIntegral() - change);
                list.add(integralReduceInfo);
            }else if(tr.getType() == 9){
                integralReduceInfo.setIntegral(tr.getTotal());
                integralReduceInfo.setType("转账(转出)");
                change = change - Math.abs(tr.getTotal());
                integralReduceInfo.setTotal(account.getTotalIntegral() - change);
                list.add(integralReduceInfo);
            }else if(tr.getType() == 2 ||  tr.getType() == 3 ||  tr.getType() == 5 || tr.getType() == 6 || tr.getType() == 8){  //用户入分
                change = change + tr.getTotal();
            }

        }
        return list;
    }

    @Override
    public List<IntegralChangeInfo> selectByTotalChange(Long userId, Date startTime, Date endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        Date nowTime = cal.getTime();
        Map<Object, Object> mapStart = new HashMap<>();
        mapStart.put("begin", sdf.format(startTime));
        mapStart.put("end", sdf.format(endTime));
        mapStart.put("userId", userId);
        Map<Object, Object> mapEnd = new HashMap<>();
        mapEnd.put("begin", sdf.format(endTime));
        mapEnd.put("end", sdf.format(nowTime));
        mapEnd.put("userId", userId);

        Account account = accountMapper.selectByUserId(userId);
        List<AccountDetail> accountDetailStartList = accountDetailMapper.selectByUserIdAndTime(mapStart);
        List<AccountDetail> accountDetailEndList = accountDetailMapper.selectByUserIdAndTime(mapEnd);
        List<IntegralChangeInfo> list = new ArrayList<>();
        Long integralChange = 0L;

        //统计用户endTime到nowTime积分变动
        for( AccountDetail ad : accountDetailEndList ){
            if( ad.getType() == 1 ){ // 1入分；2出分
                integralChange = integralChange + ad.getIntegralChange();
            }else if( ad.getType() == 2 ){
                integralChange = integralChange - Math.abs(ad.getIntegralChange());
            }
        }

        for(AccountDetail ad : accountDetailStartList){
            IntegralChangeInfo integralChangeInfo = new IntegralChangeInfo();
            integralChangeInfo.setUID(userId);
            integralChangeInfo.setChangeTime(ad.getGmtCreate());
            if( ad.getType() == 1 ){ // 1入分；2出分
                integralChange = integralChange + ad.getIntegralChange();
                integralChangeInfo.setTotalIntegral(account.getTotalIntegral() - integralChange);
            }else if( ad.getType() == 2 ){
                integralChange = integralChange - Math.abs(ad.getIntegralChange());
                integralChangeInfo.setTotalIntegral(account.getTotalIntegral() - integralChange);
            }
            list.add(integralChangeInfo);
        }

        return list;
    }

    @Override
    public List<IntegralAccountInfo> selectByIntegralAccount(Long userId, Date startTime, Date endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<Object, Object> map = new HashMap<>();
        map.put("begin", sdf.format(startTime));
        map.put("end", sdf.format(endTime));
        map.put("userId", userId);
        //查询用户平台及账户
        List<UserCard> userCardList = userCardMapper.selectByUserIdTime(map);
        List<IntegralAccountInfo> list = new ArrayList<>();

        for(UserCard uc : userCardList){
            IntegralAccountInfo integralAccountInfo = new IntegralAccountInfo();
            integralAccountInfo.setUID(userId);
            integralAccountInfo.setAccount(uc.getName());
            integralAccountInfo.setBelongTo(uc.getTypeName());
            list.add(integralAccountInfo);
        }

        return list;
    }
}
