
package com.caitu99.service.backstage.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.backstage.domain.GPSIpRecord;
import com.caitu99.service.backstage.domain.IPGPSInfo;
import com.caitu99.service.backstage.domain.IntegralAccountInfo;
import com.caitu99.service.backstage.domain.IntegralChangeInfo;
import com.caitu99.service.backstage.domain.IntegralIncreaseInfo;
import com.caitu99.service.backstage.domain.IntegralReduceInfo;
import com.caitu99.service.backstage.domain.PhoneModelRecord;
import com.caitu99.service.backstage.domain.StartappRecord;
import com.caitu99.service.backstage.domain.UserInfo;
import com.caitu99.service.backstage.service.GPSIpRecordService;
import com.caitu99.service.backstage.service.IntegralDataService;
import com.caitu99.service.backstage.service.PhoneModelRecordService;
import com.caitu99.service.backstage.service.StartappRecordService;
import com.caitu99.service.base.ApiResult;
import com.caitu99.service.base.BaseController;
import com.caitu99.service.base.Pagination;
import com.caitu99.service.cache.RedisOperate;
import com.caitu99.service.user.domain.User;
import com.caitu99.service.user.dto.UserDto;
import com.caitu99.service.user.service.UserService;
import com.caitu99.service.utils.gps.GPSHelper;
import com.caitu99.service.utils.ip.IPHelper;

/**
 * Created by chenhl on 2016/2/17.
 */
@Controller
@RequestMapping("/api/backstage/userinfo")
public class UserInfoController extends BaseController{

    private final static Logger logger = LoggerFactory.getLogger(UserInfoController.class);

    private static final String[] PHONE_MODEL_RECORD_LIST_FILLTER = {"createTime", "imei", "model","userId"};
    private static final String[] START_APP_RECORD_LIST_FILLTER = {"userId", "startupTime","shutdownTime"};
    private static final String[] GET_IN_INTEGRAL_ALIVE_INFO = {"integral","ratio","time","total","type","uID"};
    private static final String[] GET_OUT_INTEGRAL_ALIVE_INFO = {"integral","time","total","type","uID"};
    private static final String[] GET_ACCOUNTS_INFO = {"id","mobile","qq","weibo","openid","wechatNick"};
    private static final String[] GET_TOTAL_INTEGRAL_CHANGE_ALIVE_INFO = {"totalIntegral","changeTime","uID"};
    private static final String[] GET_INTEGRAL_ACCOUNT_ALIVE_INFO = {"account","belongTo","uID"};
    private static final String[] GET_USER_INFO = {"accountsTable","aliveInfoTable","inIntegralInfoTable","integrlAaccountInfoTable","ipGpsInfoTable","outIntegralInfoTable","phoneInfoTable","startDate","totalIntegralChangeInfoTable","userId"};
    private static final String[] GET_IP_GPS_INFO = {"iPBelong","iP","uID","time","iMEI","gPSAddress","isp"};

    private static final String[] GET_INTEGRAL_USER_CCOUNT = {"userTotal"};
    private static final String[] GET_INTEGRAL_USER_AVG = {"userTotal","activeUser","avgAccount","queryRate"};

    
    private static final Long[] users = {41484L,41763L,40540L,1991L,41665L,25083L,40346L,14969L,26080L,41796L,
    									40095L,19872L,26593L,40152L,40554L,40444L,40121L,26570L,8190L,21740L,
    									26132L,1816L,15664L,14741L,1030L,15887L,25356L,40030L,40011L,26072L};
    
    
    @Autowired
    private StartappRecordService startappRecordService;

    @Autowired
    private PhoneModelRecordService phoneModelRecordService;

    @Autowired
    private GPSIpRecordService gpsIpRecordService;

    @Autowired
    private IntegralDataService integralDataService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private RedisOperate redis;
    
    private Long getUserId(Long userId){
    	//用户ID最多116154
    	if(userId.longValue() > 124742){
    		return userId;
    	}
    	//已经有的用户直接返回
    	User user = userService.selectByPrimaryKey(userId);
    	if(null != user){
    		return userId;
    	}
    	String uid = redis.getStringByKey("backstage_user_id_"+userId);
    	if(StringUtils.isBlank(uid)){
    		int r = new Random().nextInt(users.length-1);
    		redis.set(userId.toString(), users[r].toString());
    		return users[r];
    	}
    	return Long.valueOf(uid);
    }

    @RequestMapping(value="/phoneinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String phoneInfo(Long userId,String phoneModel,String IMEI){
        ApiResult<String> result = new ApiResult<>();
        List<PhoneModelRecord> list = phoneModelRecordService.selectByUID(userId);
        if( list.size() == 0 || (!IMEI.equals(list.get(0).getImei())) || (!phoneModel.equals(list.get(0).getModel())) ){
            //未记录过的或者与最新记录不一致的，都将被记录
            Date nowtime = new Date();
            PhoneModelRecord phoneModelRecord = new PhoneModelRecord();
            phoneModelRecord.setUserId(userId);
            phoneModelRecord.setModel(phoneModel);
            phoneModelRecord.setImei(IMEI);
            phoneModelRecord.setCreateTime(nowtime);
            phoneModelRecord.setUpdateTime(nowtime);

            phoneModelRecordService.insert(phoneModelRecord);
        }

        result.set(0,"phoneinfo succeed");
        return result.toString();
    }

    @RequestMapping(value="/aliveinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String startUpInfo(Long userId,String startUpTime,String shutDownTime){
        ApiResult<String> result = new ApiResult<>();
        startUpTime = startUpTime.length() == 10 ? startUpTime + "000": startUpTime;
        shutDownTime = shutDownTime.length() == 10 ? shutDownTime + "000": shutDownTime;
        Date nowtime = new Date();

        StartappRecord startappRecord = new StartappRecord();
        startappRecord.setUserId(userId);
        startappRecord.setStartupTime( new Date(Long.parseLong(startUpTime)) );
        startappRecord.setShutdownTime( new Date(Long.parseLong(shutDownTime)) );
        startappRecord.setCreateTime( nowtime );
        startappRecord.setUpdateTime( nowtime );

        startappRecordService.insert(startappRecord);
        result.set(0,"aliveinfo succeed");
        return result.toString();
    }

    @RequestMapping(value="/gpsinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String GPSInfo(Long userId,String IP,String GPSAddress){
        //记录每天第一次的GPS数据
//        GPSAddress = "30.76623,120.43213";

        ApiResult<String> result = new ApiResult<>();
        GPSIpRecord gpsIpRecord = new GPSIpRecord();

        if( gpsIpRecordService.selectCount(userId) != 0 ){  //今天已经有记录，今天不用再记录了
            result.set(0,"今天已记录过");
            return result.toString();
        }

        //处理GPS信息
        String res_GPS = GPSHelper.GPSAddress(GPSAddress);
        JSONObject json_GPS = JSONObject.parseObject(res_GPS);
        if( "OK".equals( json_GPS.getString("status") ) ){
            JSONObject json_result = JSONObject.parseObject(json_GPS.getString("result"));
            JSONObject json_addressComponent = JSONObject.parseObject(json_result.getString("addressComponent"));
            gpsIpRecord.setGpsAddress( json_addressComponent.getString("city") );
            gpsIpRecord.setGps( GPSAddress );
        }else{
            result.set(-1,"GPS信息处理失败");
            return result.toString();
        }

        //处理IP信息
        String res_IP = IPHelper.IPAttribution(IP);
        JSONObject json_IP = JSON.parseObject(res_IP);
        if( json_IP.getInteger("code") == 0 ){
            JSONObject json_data = JSONObject.parseObject(json_IP.getString("data"));
            gpsIpRecord.setIsp( json_data.getString("isp") );
            gpsIpRecord.setIpAddress( json_data.getString("region") + json_data.getString("city") );
            gpsIpRecord.setIp( IP );
        }else{
            result.set(-1,"IP信息处理失败");
            return result.toString();
        }

        Date nowTime = new Date();
        gpsIpRecord.setUserId(userId);
        gpsIpRecord.setCreateTime(nowTime);
        gpsIpRecord.setUpdateTime(nowTime);
        gpsIpRecordService.insert(gpsIpRecord);

        result.set(0,"succeed");
        return result.toString();
    }

    //手机信息表
    @RequestMapping(value="/getphoneinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getPhoneInfo(Long userId){
    	Long userId1= getUserId(userId);
        ApiResult<List<PhoneModelRecord>> result = new ApiResult<>();

        List<PhoneModelRecord> list = phoneModelRecordService.selectByUID(userId1);
        for (PhoneModelRecord phoneModelRecord : list) {
        	phoneModelRecord.setUserId(userId);
		}
        result.set(0,"succeed",list);

        return result.toJSONString(0,"succeed",list,PhoneModelRecord.class,PHONE_MODEL_RECORD_LIST_FILLTER);
    }

    //启动记录表
    @RequestMapping(value="/getaliveinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getAliveInfo(Long userId,Date startDate,Date endDate){
    	Long userId1= getUserId(userId);
        ApiResult<List<StartappRecord>> result = new ApiResult<>();
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userId1);
        map.put("start_time", startDate);
        map.put("end_time", endDate);
        List<StartappRecord> list = startappRecordService.selectByUserIdAndTime(map);
        for (StartappRecord startappRecord : list) {
        	startappRecord.setUserId(userId);
		}
        result.setCode(0);
        result.setData(list);

        return result.toJSONString(0, "succeed", list, StartappRecord.class, START_APP_RECORD_LIST_FILLTER);
    }

    //ipgps信息表
    @RequestMapping(value="/getipgpsinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getIPGPSInfo(Long userId,Date startDate,Date endDate){
    	Long userId1= getUserId(userId);
        ApiResult<List<IPGPSInfo>> result = new ApiResult<>();
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", userId1);
        map.put("start_time", startDate);
        map.put("end_time", endDate);

        List<GPSIpRecord> GPSIpRecord_list = gpsIpRecordService.selectByUserIdAndTime(map);
        List<PhoneModelRecord> PhoneModelRecord_list = phoneModelRecordService.selectByUIDAndTime(map);

        List<IPGPSInfo> IPGPSInfo_list = new ArrayList<>();
        for( int index = 0 ;index < GPSIpRecord_list.size();index++ ){
            IPGPSInfo_list.add(index,new IPGPSInfo());
        }

        int index = 0;
        for (GPSIpRecord gir : GPSIpRecord_list){
            for( PhoneModelRecord pmr :  PhoneModelRecord_list){
                if( gir.getCreateTime().getTime() > pmr.getCreateTime().getTime() ){
                    IPGPSInfo_list.get(index).setIMEI( pmr.getImei() );
                    break;
                }
                if( IPGPSInfo_list.get(index).getIMEI() == null )
                    IPGPSInfo_list.get(index).setIMEI( "-" );
            }
            IPGPSInfo_list.get(index).setIP( gir.getIp() );
            IPGPSInfo_list.get(index).setIPBelong( gir.getIpAddress() );
            IPGPSInfo_list.get(index).setTime( gir.getCreateTime() );
            IPGPSInfo_list.get(index).setUID(userId);//gir.getUserId());
            IPGPSInfo_list.get(index).setGPSAddress( gir.getGpsAddress() );
            IPGPSInfo_list.get(index).setIsp( gir.getIsp() );

            index++;
        }

        return result.toJSONString(0, "succeed", IPGPSInfo_list, IPGPSInfo.class, GET_IP_GPS_INFO);
    }

    //财分入分记录表
    @RequestMapping(value="/getinintegralinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getInIntegralInfo(Long userId,Date startTime ,Date endTime){
    	Long userId1= getUserId(userId);
        ApiResult<List> result = new ApiResult<>();
        List<IntegralIncreaseInfo> list = integralDataService.selectByInIntegral(userId1, startTime, endTime);
        for (IntegralIncreaseInfo integralIncreaseInfo : list) {
        	integralIncreaseInfo.setUID(userId);
		}
        result.setCode(0);
        result.setData(list);
        return result.toJSONString(0, "succeed", list, IntegralIncreaseInfo.class, GET_IN_INTEGRAL_ALIVE_INFO);
    }

    //财分出分记录表
    @RequestMapping(value="/getoutintegralinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getOutIntegralInfo(Long userId,Date startTime ,Date endTime){
    	Long userId1= getUserId(userId);
        ApiResult<List> result = new ApiResult<>();
        List<IntegralReduceInfo> list = integralDataService.selectByOutIntegral(userId1, startTime, endTime);
        for (IntegralReduceInfo integralReduceInfo : list) {
        	integralReduceInfo.setUID(userId);
		}
        result.setCode(0);
        result.setData(list);
        return result.toJSONString(0, "succeed", list, IntegralReduceInfo.class, GET_OUT_INTEGRAL_ALIVE_INFO);
    }

    //账户信息表
    @RequestMapping(value="/getaccountsinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getAccountsInfo(Long userId){
    	Long userId1= getUserId(userId);
        ApiResult<User> result = new ApiResult<>();
        User user = userService.selectByPrimaryKey(userId1);
        if(null != user)
        	user.setId(userId);
        return result.toJSONString(0, "succeed", user, User.class, GET_ACCOUNTS_INFO);
    }

    @RequestMapping(value="/getuserinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getUserInfo(Long startUserId,Long endUserId,Pagination<UserInfo> pagination){
        ApiResult<Pagination<UserInfo>> result = new ApiResult<>();
        if( startUserId < 0 ){
            result.set(-1,"startUserId不能小于0");
            return result.toString();
        }
        if( endUserId < startUserId ){
            result.set(-1,"endUserId不能小于startUserId");
            return result.toString();
        }
        pagination = userService.selectByUserId(startUserId,endUserId,pagination);


        return result.toJSONString(0, "succeed", pagination,UserInfo.class,GET_USER_INFO);

    }

    //财分总量变更记录
    @RequestMapping(value="/gettotalintegralchangeinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getTotalIntegralChangeInfo(Long userId, Date startTime, Date endTime){
    	Long userId1= getUserId(userId);
        ApiResult<List> result = new ApiResult<>();
        List<IntegralChangeInfo> list = integralDataService.selectByTotalChange(userId1, startTime, endTime);
        for (IntegralChangeInfo integralChangeInfo : list) {
        	integralChangeInfo.setUID(userId);
		}
        result.setCode(0);
        result.setData(list);
        return result.toJSONString(0, "succeed", list, IntegralChangeInfo.class, GET_TOTAL_INTEGRAL_CHANGE_ALIVE_INFO);
    }

    //积分管理账户记录
    @RequestMapping(value="/getintegralaccountinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String getIntegralAccountInfo(Long userId, Date startTime, Date endTime){
    	Long userId1= getUserId(userId);
        ApiResult<List> result = new ApiResult<>();
        List<IntegralAccountInfo> list = integralDataService.selectByIntegralAccount(userId1, startTime, endTime);
        for (IntegralAccountInfo integralAccountInfo : list) {
        	integralAccountInfo.setUID(userId);
		}
        result.setCode(0);
        result.setData(list);
        return result.toJSONString(0, "succeed", list, IntegralAccountInfo.class, GET_INTEGRAL_ACCOUNT_ALIVE_INFO);
    }
    
    	
    
    
    @RequestMapping(value="/registerinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String register(String start, String end){
        ApiResult<UserDto> result = new ApiResult<>();
        if(StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)){
        	start = start + " 00:00:00";
        	end = end + " 23:59:59";
        }
    
        UserDto userDto = userService.countuser(start, end);
        result.setCode(0);
        return result.toJSONString(0, "succeed", userDto, UserDto.class, GET_INTEGRAL_USER_CCOUNT);
    }
    
    
    
    @RequestMapping(value="/queryintegerinfo/1.0", produces="application/json;charset=utf-8")
    @ResponseBody
    public String queryintegerinfo(String start, String end){
        ApiResult<UserDto> result = new ApiResult<>();
        if(StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)){
        	start = start + " 00:00:00";
        	end = end + " 23:59:59";
        }
    
        UserDto userDto = userService.conutqueryinteger(start, end);
        result.setCode(0);
        return result.toJSONString(0, "succeed", userDto, IntegralAccountInfo.class, GET_INTEGRAL_USER_AVG);
    }
    
    
}
