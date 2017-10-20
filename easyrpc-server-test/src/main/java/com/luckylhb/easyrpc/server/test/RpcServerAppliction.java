package com.luckylhb.easyrpc.server.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.luckylhb.easyrpc"})
public class RpcServerAppliction {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RpcServerAppliction.class);

        app.setWebEnvironment(false);
        app.run(args);
    }
}
