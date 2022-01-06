package com.jerry.netty.chat;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author jerry
 * @Description netty 群聊 服务器（telnet测试就好了）
 * @Date 2022-01-06 14:13
 * @Version 1.0
 **/
public class GroupChatServer {

    @Test
    public void startServer() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            //加入解码器
                            pipeline.addLast("decoder", new StringDecoder());
                            //加入编码器
                            pipeline.addLast("encoder", new StringEncoder());
                            pipeline.addLast(new GroupChatServerHandler());
                        }
                    });
            System.out.println("群聊服务器启动～");
            ChannelFuture bind = bootstrap.bind("127.0.0.1", 8888).sync();
            bind.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {

        //定义一个channel组，管理所有channel
        private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        /**
         * 建立连接，第一个执行
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            /**
             * 该方法会遍历channelGroup中所有的channel,并发消息
             */
            channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "[" + channel.id() + "]" + "加入聊天" + simpleDateFormat.format(new Date()) + "\n");
            //加入这个组
            channelGroup.add(channel);
        }

        /**
         * 断开连接执行
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() + "[" + channel.id() + "]" + "离开了" + simpleDateFormat.format(new Date()) + "\n");
            System.out.println("剩余在线人数" + channelGroup.size());
        }

        /**
         * 通道活跃，提示上线
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            System.out.println("[客户端]" + channel.remoteAddress() + "[" + channel.id() + "]" + "上线了" + simpleDateFormat.format(new Date()) + "\n");
        }

        /**
         * 不活动状态，离线了
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            Channel channel = ctx.channel();
            System.out.println("[客户端]" + channel.remoteAddress() + "[" + channel.id() + "]" + "离线了" + simpleDateFormat.format(new Date()) + "\n");
        }

        /**
         * 读数据
         *
         * @param ctx
         * @param msg
         * @throws Exception
         */
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            Channel channel = ctx.channel();
            channelGroup.stream().forEach(k -> {
                if (k != channel) {
                    //不是自己,转发消息就好了
                    k.writeAndFlush("[客户端]" + channel.remoteAddress() + "[" + channel.id() + "]" + ":" + msg + "\n");
                } else {
                    //是自己,回显消息就好了
                    k.writeAndFlush("[我]" + channel.remoteAddress() + "[" + channel.id() + "]" + ":" + msg + "\n");
                }
            });
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }
    }
}
