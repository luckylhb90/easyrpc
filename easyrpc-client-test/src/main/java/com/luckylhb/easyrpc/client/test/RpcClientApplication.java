package com.luckylhb.easyrpc.client.test;

import com.luckylhb.easyrpc.client.proxy.RpcProxyFactory;
import com.luckylhb.easyrpc.service.api.HelloService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.luckylhb.easyrpc"})
public class RpcClientApplication {

    @Bean
    public RpcProxyFactory rpcProxyFactory() {
        return new RpcProxyFactory();
    }

    @Bean
    public HelloService buildHelloService(RpcProxyFactory rpcProxyFactory) {
        return rpcProxyFactory.proxyBean(HelloService.class, 0);
    }


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(RpcClientApplication.class);
        app.setWebEnvironment(false);
        app.run(args);
    }
}
