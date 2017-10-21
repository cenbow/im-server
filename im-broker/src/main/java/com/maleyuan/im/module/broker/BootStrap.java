package com.maleyuan.im.module.broker;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.util.concurrent.AbstractIdleService;
import com.maleyuan.im.module.broker.handle.MqttDispathHandle;

/**
 * Created by wwj on 16.1.26.
 */
@Component
public class BootStrap extends AbstractIdleService {
    private Logger logger = LoggerFactory.getLogger(BootStrap.class);
    @Autowired
    private MqttDispathHandle dispathHandle;
    private int port = 1873;
    
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workerGroup = new NioEventLoopGroup();
	@Override
	protected void shutDown() throws Exception {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
	}
	@Override
	protected void startUp() throws Exception {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("idle", new IdleStateHandler(180, 200, 200));
                        pipeline.addLast("encode", MqttEncoder.INSTANCE);
                        pipeline.addLast("decode", new MqttDecoder());
                        pipeline.addLast(dispathHandle);
                    }
                });
        ChannelFuture channelFuture = bootstrap.bind(port).sync();
        logger.info("server start port:" + port);
        channelFuture.channel().closeFuture().sync();
    }

}
