package com.luckylhb.easyrpc.client.app;

import com.luckylhb.easyrpc.common.dto.RpcRequest;
import com.luckylhb.easyrpc.common.dto.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
@ChannelHandler.Sharable
public class ClientRpcHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(ClientRpcHandler.class);

    //blocking queue 用于阻塞功能，免除自己加锁
    private final ConcurrentHashMap<String, BlockingQueue<RpcResponse>> responseMap = new ConcurrentHashMap<String, BlockingQueue<RpcResponse>>();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse rpcResponse) throws Exception {
        logger.info("receive response : {}", rpcResponse);
        BlockingQueue<RpcResponse> queue = responseMap.get(rpcResponse.getTraceId());
        //高并发下可能为Null
        if (queue == null) {
            queue = new LinkedBlockingQueue<RpcResponse>(1);
            responseMap.putIfAbsent(rpcResponse.getTraceId(), queue);
        }
        queue.add(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.error(cause.getMessage(), cause);
    }

    public RpcResponse send(RpcRequest request, Pair<Long, TimeUnit> timeout) throws InterruptedException {
        responseMap.putIfAbsent(request.getTraceId(), new LinkedBlockingQueue<RpcResponse>(1));
        RpcResponse response = null;
        try {
            BlockingQueue<RpcResponse> queue = responseMap.get(request.getTraceId());
            if (timeout == null) {
                response = queue.take();
            } else {
                response = queue.poll(timeout.getKey(), timeout.getValue());
            }
        } finally {
            responseMap.remove(request.getTraceId());
        }
        return response;
    }
}
