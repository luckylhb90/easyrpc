package com.luckylhb.easyrpc.client.proxy;

import com.luckylhb.easyrpc.client.app.NettyClient;
import com.luckylhb.easyrpc.common.dto.RpcRequest;
import com.luckylhb.easyrpc.common.dto.RpcResponse;
import org.apache.commons.lang3.tuple.Pair;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Project : easyrpc
 * @Description : 利用代理优化远程调用，生成代理对象，像调用本地方法一样调用远程方法
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
public class RpcProxy implements InvocationHandler {

    private NettyClient nettyClient;

    private Pair<Long, TimeUnit> timeout;

    public RpcProxy() {
    }

    public RpcProxy(NettyClient nettyClient, Pair<Long, TimeUnit> timeout) {
        this.nettyClient = nettyClient;
        this.timeout = timeout;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest();// 创建并初始化 rpc 请求
        request.setTraceId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        RpcResponse response = null;
        if (timeout == null) {
            response = nettyClient.syncSend(request);
        } else {
            response = nettyClient.asyncSend(request, timeout);
        }
        if (response.isError()) {
            throw response.getError();
        } else {
            return response.getResult();
        }
    }
}
