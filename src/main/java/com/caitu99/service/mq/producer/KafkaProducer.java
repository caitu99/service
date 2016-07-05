package com.caitu99.service.mq.producer;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.kafka.support.KafkaHeaders;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.caitu99.service.user.dao.UserPushInfoMapper;
import com.caitu99.service.user.domain.UserPushInfo;

@Service("kafkaProducer")
public class KafkaProducer {

	private final static Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
	 
    @Autowired
    @Qualifier("outputToKafka")
    MessageChannel outputToKafka;

    @Autowired
    UserPushInfoMapper userPushInfoMapper;
    
    /**
     * @Description: (给用户推送消息)  
     * @Title: sendMessage 
     * @param title
     * @param description
     * @param payload
     * @param userId
     * @date 2015年12月2日 上午11:16:53  
     * @author Hongbo Peng
     */
    public void sendMessage(String title,String description,String payload,Long userId){
    	try {
			if(null == userId){
				logger.info("用户编号为空");
				return;
			}
			UserPushInfo userPushInfo = userPushInfoMapper.selectByUserId(userId);
			if(null == userPushInfo){
				logger.info("userId: {} 还没有注册regId，暂时不能推送消息",userId);
				return;
			}
			//TODO title description payload
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("pushType", "PUSH_MESSAGE");//推送类型：单推
			jsonObject.put("title", title);
			jsonObject.put("description", description);
			jsonObject.put("payload", payload);
			jsonObject.put("regId", userPushInfo.getRegId());
			jsonObject.put("type", userPushInfo.getType());
			sendMessage(jsonObject.toJSONString(), "app_message_push_topic");
		} catch (Exception e) {
			logger.error("推送消息发生异常，userId:{} title:{} description:{} payload:{} e:{}",userId,title,description,payload,e);
		}
    }
    
    
    public void sendMessage(String message, String topic) {
        outputToKafka.send(
                MessageBuilder.withPayload(message)
                        .setHeader(KafkaHeaders.TOPIC, topic).build());

    }

}
