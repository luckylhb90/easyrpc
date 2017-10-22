package com.luckylhb.easyrpc.server.app;

import com.google.common.collect.Maps;
import com.luckylhb.easyrpc.common.annotations.ServiceExporter;
import com.luckylhb.easyrpc.server.channel.init.RpcServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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

    // 服务映射容器
    private Map<String, Object> exportServiceMap = Maps.newConcurrentMap();

    @Value("${rpcServer.host:127.0.0.1}")
    String host;
    @Value("${rpcServer.ioThreadNum:5}")
    int ioThreadNum;

    @Value("${rpcServer.backLog:1024}")
    int backLog;

    @Value("${rpcServer.port:9090}")
    int port;

    /**
     *
     * acceptor
     * dispatcher
     * handler
     * 启动
     */
    @PostConstruct
    public void start() throws InterruptedException {
        logger.info("start run rpc server.");
        //NioEventLoopGroup是用来处理IO操作的多线程事件循环器
        //boss用来接收进来的连接
        bossGroup = new NioEventLoopGroup();
        //worker 用来处理已经被接收的连接,处理io;
        workerGroup = new NioEventLoopGroup();

        //是一个启动NIO服务端的辅助启动类 //Bootstrap 客户端启动
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        //为bootstrap设置acceptor的EventLoopGroup和client的EventLoopGroup
        //?这里为什么设置两个group呢?
        //设置时间循环对象，前者用来处理accept事件，后者用于处理已经建立的连接的io
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)//用它来建立新accept的连接，用于构造serversocketchannel的工厂类,监听新进来的TCP连接的通道
                .option(ChannelOption.SO_BACKLOG, backLog)//对应的是tcp/ip协议listen函数中的backlog参数，函数listen(int socketfd,int backlog)用来初始化服务端可连接队列
                // 注意是childOption
                .childOption(ChannelOption.SO_KEEPALIVE, true)//参数对应于套接字选项中的SO_KEEPALIVE，该参数用于设置TCP连接，当设置该选项以后，连接会测试链接的状态，这个选项用于可能长时间没有数据交流的连接
                .childOption(ChannelOption.TCP_NODELAY, true)//参数对应于套接字选项中的TCP_NODELAY,该参数禁止使用Nagle算法
                .childHandler(new RpcServerChannelInitializer(exportServiceMap));
        //bind方法会创建一个serverchannel，并且会将当前的channel注册到eventloop上面，
        //会为其绑定本地端口，并对其进行初始化，为其的pipeline加一些默认的handler
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
        channel.closeFuture().syncUninterruptibly();//相当于在这里阻塞，直到serverchannel关闭
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
