package com.jerry.nio.selector;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author jerry
 * @Description 选择器测试
 * @Date 2021-12-31 14:30
 * @Version 1.0
 **/
public class SelectorTest {

    /**
     * 与Selector一起使用时，Channel必须非阻塞（FileChannel不能与Selector一起使用）
     * 一个通道，并没有一定要支持所有的四种操作。比如服务器通道
     * ServerSocketChannel 支持 Accept 接受操作，而 SocketChannel 客户端通道则
     * 不支持。可以通过通道上的 validOps()方法，来获取特定通道下所有支持的操作集合
     *
     * @throws IOException
     */
    @Test
    public void test01() throws IOException {
        //使用方法

        //1.获取Selector选择器
        Selector selector = Selector.open();
        //2.获取通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //3.设置非阻塞
        serverSocketChannel.configureBlocking(false);
        //4.绑定连接
        serverSocketChannel.bind(new InetSocketAddress(9999));
        //5.将通道注册到选择器
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Test
    public void serverTest() {
        try {
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.socket().bind(new InetSocketAddress("127.0.0.1", 7001));
            serverSocketChannel.configureBlocking(false);
            Selector selector = Selector.open();
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            ByteBuffer readBuffer = ByteBuffer.allocate(1024);
            ByteBuffer writeBuffer = ByteBuffer.allocate(128);

            writeBuffer.put("received".getBytes());
            writeBuffer.flip();

            while (true) {
                int ready = selector.select();
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey next = iterator.next();
                    iterator.remove();
                    if (next.isAcceptable()) {
                        //创建新到连接，并注册到selector上，而且声明只对读感兴趣
                        SocketChannel socketChannel = serverSocketChannel.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    } else if (next.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) next.channel();
                        readBuffer.clear();
                        socketChannel.read(readBuffer);
                        readBuffer.flip();
                        System.out.println("received :" + new String(readBuffer.array()));
                        next.interestOps(SelectionKey.OP_WRITE);
                    } else if (next.isWritable()) {
                        writeBuffer.rewind();
                        SocketChannel socketChannel = (SocketChannel) next.channel();
                        socketChannel.write(writeBuffer);
                        next.interestOps(SelectionKey.OP_READ);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void clientTest(){
        try {
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("127.0.0.1",7001));
            ByteBuffer read = ByteBuffer.allocate(32);
            ByteBuffer write = ByteBuffer.allocate(32);
            write.put("hello".getBytes());
            write.flip();

            while (true){
                write.rewind();
                socketChannel.write(write);
                read.clear();
                socketChannel.read(read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
