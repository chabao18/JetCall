package com.chaobao18.consumer.bootstrap;


import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestComponent;

import javax.annotation.Resource;

@SpringBootTest
public class ServiceImplTest{
    @Resource
    private ServiceImpl service;

    @Test
    void test1() {
        service.test();
    }

}