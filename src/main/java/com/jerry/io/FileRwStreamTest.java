package com.jerry.io;

import org.junit.Test;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @Author jerry
 * @Description io流原理级分类
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
 * @Date 2021-12-28 19:46
 * @Version 1.0
 **/
public class FileRwStreamTest {

    /**
     * 字符输入流(读)
     */
    @Test
    public void testFileReader() {
        FileReader fr = null;
        try {
            fr = new FileReader("hello.txt");
//            int data;
//            while ((data = fr.read()) != -1) {
//                System.out.println((char) data);
//            }
            char[] chars = new char[15];
            int len;
            while ((len = fr.read(chars)) != -1) {
                System.out.println(new String(chars, 0, len));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 字符输出流（写）
     */
    @Test
    public void testFileWriter() {
        FileWriter fw = null;
        try {
            fw = new FileWriter("hello.txt", true);
            //写
            fw.write("写一下");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fw != null) {
                try {
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
