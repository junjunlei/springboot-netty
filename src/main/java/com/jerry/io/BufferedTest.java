package com.jerry.io;

import org.junit.Test;

import java.io.*;

/**
 * @Author jerry
 * 缓冲流
 * <p>
 * 一、流的分类
 * 1.操作数据单位：字节流 、 字符流
 * 2.操作数据流向：输入流（读）输出流（写）
 * 3.流的角色：节点流、处理流
 * <p>
 * 二、流的体系结构
 * 抽象基类                  节点流（或文件流）                                      缓冲流（处理流）
 * InputStream              FileInputStream(read(byte[] buffer))                BufferedInputStream(read(byte[] buffer))
 * OutputStream             FileOutputStream(write(byte[] buffer,0,len))        BufferedOutputStream(write(byte[] buffer,0,len)/flush())
 * Reader                   FileReader(read(char[] buf))                        BufferedReader(read(char[] buf) /readLine())
 * Write                    FileWrite(write(char[] buf,0,len)                   BufferedWriter(write(char[] buf,0,len)/flush())
 * @Date 2021-12-29 13:20
 * @Version 1.0
 **/
public class BufferedTest {


    /**
     * 字节流缓冲区复制
     */
    @Test
    public void bufferedStreamTest() {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            //节点流
            FileInputStream fis = new FileInputStream("background.jpg");
            FileOutputStream fos = new FileOutputStream("background_copy.jpg");
            //缓冲流
            bis = new BufferedInputStream(fis);
            bos = new BufferedOutputStream(fos);

            //复制
            byte[] bytes = new byte[10];
            int len;
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
                //刷新缓冲区
                bos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //要求：先关闭外层的流，再关闭内层的流
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            //说明：关闭外层流的同时，内层流也会自动的进行关闭。关于内层流的关闭，我们可以省略.
        }

    }

    /**
     * 字符流缓冲区复制
     */
    @Test
    public void bufferRwTest() {
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            //节点流
            FileReader fr = new FileReader("hello.txt");
            FileWriter fw = new FileWriter("hello_copy.txt");
            //缓冲流
            br = new BufferedReader(fr);
            bw = new BufferedWriter(fw);

            //复制
//            char[] bytes = new char[1024];
//            int len;
//            while ((len = br.read(bytes)) != -1) {
//                bw.write(bytes, 0, len);
//                //刷新缓冲区
//                bw.flush();
//            }

            String data;
            while ((data = br.readLine()) != null) {
                bw.write(data);
                //换行
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //要求：先关闭外层的流，再关闭内层的流
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            //说明：关闭外层流的同时，内层流也会自动的进行关闭。关于内层流的关闭，我们可以省略.
        }
    }
}
