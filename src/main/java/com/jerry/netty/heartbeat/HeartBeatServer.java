package com.jerry.netty.heartbeat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @Author jerry
 * @Description 心跳检测服务器
 * @Date 2022-01-06 16:43
 * @Version 1.0
 **/
public class HeartBeatServer {

    @Test
    public void startServer() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            /**
                             * IdleStateHandler是netty提供的处理空闲状态的处理器
                             * readerIdleTime：表示多长时间没有读，就会发送一个心跳检测包检测是否连接
                             * writerIdleTime：表示多长时间没有写，就会发送一个心跳检测包检测是否连接
                             * allIdleTime：表示多长时间没有读写，就会发送一个心跳检测包检测是否连接
                             *
                             * 当IdleStateEvent事件触发后，就会传递给管道的下一个handle取出来，通过调用下一个handle的userEventTriggered,在该方法中
                             * 取出来IdleStateEvent（读空闲、写空闲、读写空袭）
                             */
                            pipeline.addLast(new IdleStateHandler(13, 5, 2, TimeUnit.SECONDS));
                            pipeline.addLast(new ServerHandle());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(7001).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class ServerHandle extends ChannelInboundHandlerAdapter {
        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            //super.userEventTriggered(ctx, evt);
            if (evt instanceof IdleStateEvent) {
                IdleStateEvent event= (IdleStateEvent) evt;
                String eventType="";
                switch (event.state()){
                    case READER_IDLE:
                        eventType="读空闲";
                        break;
                    case WRITER_IDLE:
                        eventType="写空闲";
                        break;
                    case ALL_IDLE:
                        eventType="读写空闲";
                        break;
                }
                System.out.println(ctx.channel().remoteAddress()+"--超时时间--"+eventType);
                System.out.println("服务器做响应处理");
//                if(eventType!=""){
//                    ctx.channel().close();
//                }
            }
        }
    }
}
