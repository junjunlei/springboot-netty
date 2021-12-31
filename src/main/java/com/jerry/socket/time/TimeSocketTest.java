package com.jerry.socket.time;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * @Author jerry
 * @Description 时间服务
 * @Date 2021-12-30 17:20
 * @Version 1.0
 **/
public class TimeSocketTest {

    @Test
    public void timeServer() {
        ServerSocket server = null;
        Socket socket = null;
        try {
            server = new ServerSocket(7002);
            System.out.println("时间服务器启动于端口: 7002");
            while (true) {
                socket = server.accept();
                //bio一个连接开一个线程，这里可以线程池优化
                TimeServerHandle.start(socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("时间服务已关闭");
        }
    }

    @Test
    public void timeClient() {
        Socket socket=null;
        BufferedReader in=null;
        PrintWriter out=null;
        try {
            socket=new Socket("127.0.0.1",7002);
            in=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out=new PrintWriter(socket.getOutputStream(),true);
            out.println("QUERY ORDER ORDER");
            System.out.println("发送消息成功");
            String message = in.readLine();
            System.out.println("收到到回复为："+message);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out!=null){
                out.close();
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


    private static class TimeServerHandle {
        public static void start(Socket socket) {
            new Thread(() -> {
                BufferedReader in = null;
                PrintWriter out = null;
                try {
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out = new PrintWriter(socket.getOutputStream(), true);
                    String currentTime = null;
                    String body = null;
                    while (true) {
                        body = in.readLine();
                        if (body == null) {
                            break;
                        }
                        System.out.println("时间服务器接收到到参数为：" + body);
                        currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "嘎嘎嘎";
                        out.println(currentTime);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (socket != null) {
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (out != null) {
                        out.close();
                    }
                }
            }).start();
        }
    }
}
