package com.jerry.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.junit.Test;

import java.net.URI;

/**
 * @Author jerry
 * @Description http服务端
 * @Date 2022-01-06 13:31
 * @Version 1.0
 **/
public class NettyHttpServer {

    @Test
    public void startServer() {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //http服务说明 是netty 提供的处理http的编码解码器
                            ch.pipeline().addLast("MyHttpServerCode", new HttpServerCodec());
                            //自定义handle
                            ch.pipeline().addLast("MyHttpServerHandle", new HttpServerHandle());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(7002).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private class HttpServerHandle extends SimpleChannelInboundHandler<HttpObject> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {
            if (msg instanceof HttpRequest) {
                System.out.println("msg 类型=" + msg.getClass());
                System.out.println("客户端地址：" + ctx.channel().remoteAddress());
                HttpRequest httpRequest = (HttpRequest) msg;
                URI uri = new URI(httpRequest.uri());
                if ("/favicon".equals(uri.getPath())) {
                    System.out.println("请求了 favicon ， 不响应");
                    return;
                }
                //其他请求，回消息
                ByteBuf byteBuf = Unpooled.copiedBuffer("你好，我是服务器", CharsetUtil.UTF_8);
                //构造http response
                FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain;charset=UTF-8");
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, byteBuf.readableBytes());
                //返回
                ctx.writeAndFlush(response);
            }
        }
    }
}
