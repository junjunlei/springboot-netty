package com.jerry.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;
import org.junit.Test;

/**
 * @Author jerry
 * @Description netty server端
 * @Date 2022-01-05 11:23
 * @Version 1.0
 **/
public class NettyServer {

    @Test
    public void startServer() {
        //创建bossGroup和workGroup
        //1.bossGroup只处理连接请求、workGroup处理客户端业务
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            //创建服务器启动对象，配置启动参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    //使用NioServerSocketChannel作为服务器的实现通道
                    .channel(NioServerSocketChannel.class)
                    //设置线程对了得到的连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //创建通道测试对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //给工作workGroup对应的管道设置处理器
                            ch.pipeline().addLast(new NettyServerHandle());
                        }
                    });

            System.out.println("服务器已就绪");
            //启动服务器
            ChannelFuture cf = bootstrap.bind(8001).sync();
            //对关闭通道进行监听
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅关闭
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    private class NettyServerHandle extends ChannelInboundHandlerAdapter {

        /**
         * 读取客户端发来的数据
         *
         * @param ctx 上下文 包含管理pipeline channel 等
         * @param msg 客户端发送的消息
         * @throws Exception
         */
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            //super.channelRead(ctx, msg);
            System.out.println("服务器读取线程 " + Thread.currentThread().getName());
            System.out.println("上下文= " + ctx);
//            Channel channel = ctx.channel();
//            ChannelPipeline pipeline = ctx.pipeline();
            ByteBuf buf = (ByteBuf) msg;
            System.out.println("客户端发送来的消息是：" + buf.toString(CharsetUtil.UTF_8));
        }

        /**
         * 读取完成
         *
         * @param ctx 上下文
         * @throws Exception
         */
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            //super.channelReadComplete(ctx);
            //发消息给客户端
            ctx.writeAndFlush(Unpooled.copiedBuffer("客户端你好,哈哈哈", CharsetUtil.UTF_8));
        }

        /**
         * 处理异常(一般是需要关闭通道)
         *
         * @param ctx   上下文
         * @param cause 异常堆栈
         * @throws Exception
         */
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            //super.exceptionCaught(ctx, cause);
            ctx.close();
        }
    }
}
