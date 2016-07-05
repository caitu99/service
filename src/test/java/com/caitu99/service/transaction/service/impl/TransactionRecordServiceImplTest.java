package com.caitu99.service.transaction.service.impl;

import com.caitu99.service.transaction.service.TransactionRecordService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:spring.xml"})
public class TransactionRecordServiceImplTest extends TestCase {

    private static Logger logger = LoggerFactory.getLogger(TransactionRecordServiceImplTest.class);

    @Autowired
    private TransactionRecordService transactionRecordService;

    @Test
    public void testGetLastDayConsumes() throws Exception {
        Integer consumes = transactionRecordService.getLastDayConsumes((long) 1);
        logger.info("{}", consumes);
    }
}