package com.jerry.nio.chat;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author jerry
 * @Description 聊天服务器
 * @Date 2021-12-31 16:06
 * @Version 1.0
 **/
public class ChatServer {


    @Test
    public void startServer() throws IOException {
        //1.创建选择器
        Selector selector = Selector.open();
        //2.创建服务端通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //3.为通道创建端口
        serverSocketChannel.socket().bind(new InetSocketAddress(8000));
        //非阻塞
        serverSocketChannel.configureBlocking(false);
        //4.把channel注册到选择器
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("聊天服务器启动成功");
        //5.等待接入
        for (; ; ) {
            int readCounts = selector.select();
            if (readCounts == 0) {
                continue;
            }
            //获取通道集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //移除当前key
                iterator.remove();
                //连接状态
                if (key.isAcceptable()) {
                    acceptOperator(serverSocketChannel, selector);
                }
                //可读状态
                if (key.isReadable()) {
                    readOperator(selector, key);
                }

            }
        }
    }

    /**
     * 处理可读状态
     *
     * @param selector
     * @param key
     */
    private void readOperator(Selector selector, SelectionKey key) throws IOException {
        //1.获取已经就绪的通道
        SocketChannel socketChannel = (SocketChannel) key.channel();
        //2.创建缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //3.读数据
        int read = socketChannel.read(buffer);
        StringBuilder message = new StringBuilder();
        if (read != -1) {
            //翻转
            buffer.flip();
            //读取内容
            message.append(Charset.forName("UTF-8").decode(buffer));
        }
        //4.将通道再次注册到选择器，监听可读状态
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        //5.消息发送到各个客户端
        if (message.length() > 0) {
            System.out.println("收到到信息为：" + message);
            broadcastToOtherClient(message, selector, socketChannel);
        }
    }

    /**
     * 广播到其他客户端
     *
     * @param message
     * @param selector
     * @param socketChannel
     */
    private void broadcastToOtherClient(StringBuilder message, Selector selector, SocketChannel socketChannel) throws IOException {
        //1.获取所有可用通道（已接入）
        Set<SelectionKey> selectionKeys = selector.keys();
        for (SelectionKey selectionKey : selectionKeys) {
            SelectableChannel channel = selectionKey.channel();
            //不需要给自己发
            if (channel instanceof SocketChannel && channel != socketChannel) {
                ((SocketChannel) channel).write(Charset.forName("UTF-8").encode(message.toString()));
            }
        }
    }

    /**
     * 处理连接状态
     *
     * @param serverSocketChannel 服务端通道
     * @param selector            选择器
     */
    private void acceptOperator(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        //1.创建SocketChannel
        SocketChannel socketChannel = serverSocketChannel.accept();
        //2.非阻塞（一定要在注册之前设置非阻塞）
        socketChannel.configureBlocking(false);
        //3.注册到selector,监听可读状态
        socketChannel.register(selector, SelectionKey.OP_READ);
        //4.回复客户端连接成功
        socketChannel.write(Charset.forName("UTF-8").encode("欢迎进入聊天室,哈哈哈"));
    }
}
