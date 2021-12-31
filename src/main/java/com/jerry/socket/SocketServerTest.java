package com.jerry.socket;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @Author jerry
 * @Description 网络编程测试
 * @Date 2021-12-30 14:14
 * @Version 1.0
 **/
public class SocketServerTest {

    @Test
    public void server01() {
        ServerSocket serverSocket = null;
        Socket socket = null;
        InputStream is = null;
        OutputStream os = null;

        try {
            serverSocket = new ServerSocket(7001);
            socket = serverSocket.accept();
            is = socket.getInputStream();
            os = new ByteArrayOutputStream();
            byte[] bytes = new byte[5];
            int len;
            while ((len = is.read(bytes)) != -1) {
                os.write(bytes, 0, len);
            }
            System.out.println("收到了来自于"+socket.getInetAddress().getHostAddress()+"的数据："+os);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is!=null){
                try {
                    is.close();
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
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
