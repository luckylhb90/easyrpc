package com.luckylhb.easyrpc.server.test.service.impl;

import com.luckylhb.easyrpc.common.annotations.ServiceExporter;
import com.luckylhb.easyrpc.service.api.HelloService;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
@ServiceExporter(value = "demoSvr", targetInterface = HelloService.class, debugAddress = "127.0.0.1:9090")
public class HelloServiceImpl implements HelloService {
    @Override
    public void hello(String data) {
        System.out.println(data);
    }
}
