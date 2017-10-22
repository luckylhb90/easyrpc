package com.luckylhb.easyrpc.server.channel.init;

import com.luckylhb.easyrpc.common.codec.RpcDecoder;
import com.luckylhb.easyrpc.common.codec.RpcEncoder;
import com.luckylhb.easyrpc.common.dto.RpcRequest;
import com.luckylhb.easyrpc.common.dto.RpcResponse;
import com.luckylhb.easyrpc.server.app.ServerRpcHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.util.Map;


/**
 * @Project : easyrpc
 * @Description : //对刚刚接收的channel进行初始化,为accept channel的pipeline预添加的inboundhandler
 * @Author : dearlhb
 * @Date : 2017/10/21
 */
public class RpcServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private final Map<String, Object> serviceMapping;

    public RpcServerChannelInitializer(Map<String, Object> serviceMapping) {
        this.serviceMapping = serviceMapping;
    }

    //ObjectDecoder 底层默认继承半包解码器LengthFieldBasedFrameDecoder处理粘包问题的时候，
    //消息头开始即为长度字段，占据4个字节。这里出于保持兼容的考虑
    final public static int MESSAGE_LENGTH = 4;

    //当新连接accept的时候，这个方法会调用
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                //LengthFieldBasedFrameDecoder是基于长度字段的解码器,解决TCP粘包问题的解码器
                //ObjectDecoder的基类半包解码器LengthFieldBasedFrameDecoder的报文格式保持兼容。因为底层的父类LengthFieldBasedFrameDecoder
                //的初始化参数即为super(maxObjectSize, 0, 4, 0, 4);
                .addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, MESSAGE_LENGTH, 0, MESSAGE_LENGTH))
                //利用LengthFieldPrepender回填补充ObjectDecoder消息报文头
                .addLast("encoder", new LengthFieldPrepender(MESSAGE_LENGTH, false))
                .addLast(new RpcDecoder(RpcRequest.class))
                .addLast(new RpcEncoder(RpcResponse.class))
                .addLast(new ServerRpcHandler(serviceMapping));//为当前的channel的pipeline添加自定义的处理函数
    }
}
