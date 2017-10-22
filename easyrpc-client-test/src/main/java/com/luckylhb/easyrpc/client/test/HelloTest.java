package com.luckylhb.easyrpc.client.test;

import com.luckylhb.easyrpc.service.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
@Component
public class HelloTest implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(HelloTest.class);

    @Autowired
    private HelloService helloService;


    @Override
    public void run(String... strings) throws Exception {
//        System.out.println("hello,");//动态代理时会执行toString()方法。。。。
        logger.info("hello,....");
        helloService.hello("world!");
    }
}
