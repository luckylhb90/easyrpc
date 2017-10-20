package com.luckylhb.easyrpc.client.app;

import com.luckylhb.easyrpc.common.dto.RpcRequest;
import com.luckylhb.easyrpc.common.dto.RpcResponse;
import org.apache.commons.lang3.tuple.Pair;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
public interface IClient {

    void connect(InetSocketAddress socketAddress);
    RpcResponse syncSend(RpcRequest request) throws InterruptedException;
    RpcResponse asyncSend(RpcRequest request, Pair<Long, TimeUnit> timeout) throws InterruptedException;
    InetSocketAddress getRemoteAddress();
    void close();
}
