package com.caitu99.service.utils.kafka;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring.xml"})
@ActiveProfiles("dev")
public class KafkaProducerTest extends TestCase {


    @Test
    public void testSendMessage() throws Exception {
        /*for (int i = 0; i < 10; i++)
            kafkaProducer.sendMessage("aaaa", "event_stream");
        Thread.sleep(10000);*/
    }

    @Test
    public void testKafka() {
        /*Properties props = new Properties();
        props.put("metadata.broker.list", "192.168.25.181:9092");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("request.required.acks", "1");
        ProducerConfig config = new ProducerConfig(props);
        Producer<String, String> producer = new Producer<String, String>(config);
        String msg = "NativeMessage-";
        KeyedMessage<String, String> data = new KeyedMessage<String, String>("event_stream", msg);
        producer.send(data);*/
    }

}