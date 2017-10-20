package com.luckylhb.easyrpc.client.proxy;

import com.luckylhb.easyrpc.client.app.NettyClient;
import com.luckylhb.easyrpc.client.app.NettyClientFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.concurrent.TimeUnit;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
@Component
public class RpcProxyFactory {

    public <T> T proxyBean(Class<?> targetInterface, long timeoutInMillis) {
        NettyClient client = NettyClientFactory.get(targetInterface);
        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{targetInterface}, new RpcProxy(client, Pair.of(timeoutInMillis, TimeUnit.MILLISECONDS)));
    }

}
