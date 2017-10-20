package com.luckylhb.easyrpc.server.app;

import com.google.common.collect.Maps;
import com.luckylhb.easyrpc.common.annotations.ServiceExporter;
import com.luckylhb.easyrpc.common.codec.RpcDecoder;
import com.luckylhb.easyrpc.common.codec.RpcEncoder;
import com.luckylhb.easyrpc.common.dto.RpcRequest;
import com.luckylhb.easyrpc.common.dto.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;


//实现ApplicationContextAware以获得ApplicationContext中的所有bean
@Component
public class NettyServer implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    private Map<String, Object> exportServiceMap = Maps.newHashMap();

    @Value("${rpcServer.host:127.0.0.1}")
    String host;
    @Value("${rpcServer.ioThreadNum:5}")
    int ioThreadNum;

    @Value("${rpcServer.backLog:1024}")
    int backLog;

    @Value("${rpcServer.port:9090}")
    int port;

    /**
     * 启动
     */
    @PostConstruct
    public void start() throws InterruptedException {
        logger.info("start run rpc server.");
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();

        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, backLog)
                // 注意是childOption
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,4 , 0, 4))
                                .addLast("encoder", new LengthFieldPrepender(4, false))
                                .addLast(new RpcDecoder(RpcRequest.class))
                                .addLast(new RpcEncoder(RpcResponse.class))
                                .addLast(new ServerRpcHandler(exportServiceMap));
                    }
                });
        channel = serverBootstrap.bind(host, port).sync().channel();

        logger.info("easyrpc server listening on port {} , and ready for connecttions...", port);
    }

    @PreDestroy
    public void stop() {
        logger.info("easyrpc destroy server resources.");
        if(null == channel) {
            logger.error("easyrpc server channel is null");
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        channel.closeFuture().syncUninterruptibly();
        bossGroup = null;
        workerGroup = null;
        channel = null;
    }

    /**
     * 利用此方法获取spring ioc 接管的所有bean
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String , Object> serviceMap = applicationContext.getBeansWithAnnotation(ServiceExporter.class);
        logger.info("获取到所有的RPC服务：{}", serviceMap);
        if(MapUtils.isNotEmpty(serviceMap)) {
            for(Object serviceBean : serviceMap.values()) {
                String interfaceName= serviceBean.getClass().getAnnotation(ServiceExporter.class).targetInterface().getName();
                logger.info("register service mapping:{}", interfaceName);
                exportServiceMap.put(interfaceName, serviceBean);
            }
        }
    }
}
