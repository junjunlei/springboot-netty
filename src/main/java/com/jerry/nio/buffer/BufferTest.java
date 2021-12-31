package com.jerry.nio.buffer;

import org.junit.Test;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;

/**
 * @Author jerry
 * @Description nio缓冲区，所有数据都会过缓冲区
 * @Date 2021-12-31 11:30
 * @Version 1.0
 **/
public class BufferTest {

    @Test
    public void test01() throws Exception {
        FileInputStream fis = new FileInputStream("hello.txt");
        FileChannel channel = fis.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(48);
        int read = channel.read(buffer);
        while (read != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.println((char) buffer.get());
            }
            buffer.clear();
            read = channel.read(buffer);
        }
        fis.close();
    }

    @Test
    public void test02() throws Exception {
        IntBuffer buffer = IntBuffer.allocate(8);
        for (int i = 0; i < buffer.capacity(); i++) {
            int j = 2 * (i + 1);
            //将指定整数写入缓冲区当前位置
            buffer.put(j);
        }
        //翻转
        buffer.flip();
        while (buffer.hasRemaining()){
            int j=buffer.get();
            System.out.println("缓冲区中都数据为："+j);
        }
    }
}
