package com.jerry.nio.chat;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @Author jerry
 * @Description 聊天客户端
 * @Date 2021-12-31 17:28
 * @Version 1.0
 **/
public class ChatClient {

    public static void startClient(String name) throws IOException {
        //1.连接服务器
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8000));
        //2.接收服务器响应数据
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        //创建线程
        ClientHandle.start(selector);
        //向服务器发送数据
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String message = scanner.nextLine();
            if (message.length() > 0) {
                socketChannel.write(Charset.forName("UTF-8").encode(name + ": " + message));
            }
        }
    }

    private static class ClientHandle {
        public static void start(Selector selector) throws IOException {
            new Thread(() -> {
                try {
                    for (; ; ) {
                        int read = selector.select();
                        if (read == 0) {
                            continue;
                        }
                        Set<SelectionKey> selectionKeys = selector.selectedKeys();
                        Iterator<SelectionKey> iterator = selectionKeys.iterator();
                        while (iterator.hasNext()) {
                            SelectionKey key = iterator.next();
                            //移除当前key
                            iterator.remove();
                            //可读状态
                            if (key.isReadable()) {
                                readOperator(selector, key);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * 处理可读状态
     *
     * @param selector
     * @param key
     */
    private static void readOperator(Selector selector, SelectionKey key) throws IOException {
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
        //输出服务端返回的消息
        if (message.length() > 0) {
            System.out.println(message);
        }
    }

    @Test
    public void client01() throws IOException {
        ChatClient.startClient("用户xxx");
    }

    @Test
    public void client02() throws IOException {
        ChatClient.startClient("用户yyy");
    }
}

