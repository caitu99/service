package com.caitu99.service;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        Date date = new Date(2015, 12, 1);
        System.out.println(date.getTime());
        date = new Date(2015, 12, 31);
        System.out.print(date.getTime());
    }
}