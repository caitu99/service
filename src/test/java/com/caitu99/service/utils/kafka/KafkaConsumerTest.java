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
public class KafkaConsumerTest extends TestCase {

    /*@Autowired
    private KafkaConsumer consumer;*/

    @Test
    public void testReceiveMessage() throws Exception {
        //consumer.receiveMessage();
    }
}