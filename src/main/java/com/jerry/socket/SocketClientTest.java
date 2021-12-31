package com.jerry.socket;

import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * @Author jerry
 * @Description 网络编程测试
 * @Date 2021-12-30 14:14
 * @Version 1.0
 **/
public class SocketClientTest {
    @Test
    public void client01() {
        Socket socket = null;
        OutputStream os = null;
        try {
            InetAddress inet = InetAddress.getByName("127.0.0.1");
            socket = new Socket(inet, 7001);
            os = socket.getOutputStream();
            os.write("你好，我是客户端".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(socket!=null){
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
