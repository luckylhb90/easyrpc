package com.luckylhb.easyrpc.client.test;

import com.luckylhb.easyrpc.service.api.HelloService;
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

    @Autowired
    private HelloService helloService;


    @Override
    public void run(String... strings) throws Exception {
        System.out.println("....");
        helloService.hello("world!");
    }
}
