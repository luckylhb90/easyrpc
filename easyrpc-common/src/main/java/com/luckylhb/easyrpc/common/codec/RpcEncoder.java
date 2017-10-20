package com.luckylhb.easyrpc.common.codec;

import com.luckylhb.easyrpc.common.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Project : easyrpc
 * @Description : RPC编码
 * @Author : lukylhb
 * @Date : 2017/10/1
 */
public class RpcEncoder extends MessageToByteEncoder {


    private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        if(genericClass.isInstance(o)) {
            byte[] data = SerializationUtil.serializer(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }

    }
}
