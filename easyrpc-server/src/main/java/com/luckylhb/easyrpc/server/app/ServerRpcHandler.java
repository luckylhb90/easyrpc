package com.luckylhb.easyrpc.server.app;

import com.luckylhb.easyrpc.common.dto.RpcRequest;
import com.luckylhb.easyrpc.common.dto.RpcResponse;
import com.luckylhb.easyrpc.common.exception.ServerException;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @Project : easyrpc
 * @Description : TODO
 * @Author : luckylhb
 * @Date : 2017/10/1
 */
public class ServerRpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(ServerRpcHandler.class);

    private final Map<String, Object> serviceMapping;

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
        RpcResponse response = new RpcResponse();
        if (cause instanceof ServerException) {
            response.setTraceId(((ServerException) cause).getTraceId());
        }
        response.setError(cause);
        ctx.writeAndFlush(response);
    }

    public ServerRpcHandler(Map<String, Object> serviceMapping) {
        this.serviceMapping = serviceMapping;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setTraceId(rpcRequest.getTraceId());

        try {
            logger.info("server handle request:{}", rpcRequest);
            Object result = handle(rpcRequest);
            response.setResult(result);
        } catch (InvocationTargetException e) {
            logger.error("request failure.", e);
            response.setError(e);
        }

        channelHandlerContext.writeAndFlush(response);

    }

    private Object handle(RpcRequest request) throws InvocationTargetException {
        String className = request.getClassName();
        Object serviceBean = serviceMapping.get(className);
        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }
}
