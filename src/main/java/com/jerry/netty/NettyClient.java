package com.jerry.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import org.junit.Test;

/**
 * @Author jerry
 * @Description netty client
 * @Date 2022-01-05 19:44
 * @Version 1.0
 **/
public class NettyClient {

    @Test
    public void startClient() {
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyClientHandle());
                        }
                    });
            System.out.println("客户端准备完成");
            //启动客户端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8001).sync();
            //关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private class NettyClientHandle extends ChannelInboundHandlerAdapter {

        /**
         * 通道就绪会触发
         *
         * @param ctx
         * @throws Exception
         */
        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            //super.channelActive(ctx);
            ctx.writeAndFlush(Unpooled.copiedBuffer("你好服务器",CharsetUtil.UTF_8));
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ByteBuf bug = (ByteBuf) msg;
            System.out.println("服务器回复对消息: " + bug.toString(CharsetUtil.UTF_8));
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            //super.exceptionCaught(ctx, cause);
            cause.printStackTrace();
            ctx.close();
        }
    }
}
