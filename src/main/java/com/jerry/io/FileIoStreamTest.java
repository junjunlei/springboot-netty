package com.jerry.io;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
 * @Date 2021-12-28 16:49
 * @Version 1.0
 **/
public class FileIoStreamTest {


    /**
     * 字节流 inputStream 处理文件可能会出现乱码
     */
    @Test
    public void testFileInputStream() {
        FileInputStream fis = null;
        try {
            //1.构造文件
            File file = new File("hello.txt");
            //2.构造流
            fis = new FileInputStream(file);
            //3.读数据
            byte[] buffer = new byte[5];
            //每次读取读字节数
            int len;
            while ((len = fis.read(buffer)) != -1) {
                String str = new String(buffer, 0, len);
                System.out.println(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    //操作的非内存资源，所以需要关闭
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @Test
    public void testFileOutputStream() {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("fileOutput.txt", true);
            fos.write("哈哈哈".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    //操作的非内存资源，所以需要关闭
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
