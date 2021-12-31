package com.jerry.nio.channel;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @Author jerry
 * @Description 文件通道
 * @Date 2021-12-30 14:56
 * @Version 1.0
 **/
public class FileChannelTest {

    /**
     * file channel read test
     *
     * @throws Exception
     */
    @Test
    public void fileChannelReadTest() throws Exception {
        FileInputStream fis = new FileInputStream("hello.txt");
        FileChannel channel = fis.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(48);
        int read = channel.read(buffer);
        while (read != -1) {
            System.out.println("读取：" + read);
            //反转（读变写）
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.println((char) buffer.get());
            }
            buffer.clear();
            read = channel.read(buffer);
        }
        fis.close();
        System.out.println("操作结束");
    }


    /**
     * file channel write
     *
     * @throws Exception
     */
    @Test
    public void fileChannelWriteTest() throws Exception {
        FileOutputStream fos = new FileOutputStream("hello.txt", true);
        FileChannel channel = fos.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate(48);
        String data = "new String to write to file";
        buffer.clear();
        buffer.put(data.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }
        channel.close();
        fos.close();
        System.out.println("操作结束");
    }

    /**
     * 将通道1复制到通道2
     *
     * @throws Exception
     */
    @Test
    public void testTransferFrom() throws Exception {
        FileInputStream fis1 = new FileInputStream("hello.txt");
        FileChannel channel1 = fis1.getChannel();
        FileOutputStream fis2 = new FileOutputStream("hello_copy.txt");
        FileChannel channel2 = fis2.getChannel();
        long size = channel1.size();
        channel2.transferFrom(channel1, 0, size);
        fis1.close();
        fis2.close();
        System.out.println("传输完成");
    }

    /**
     * 将通道1复制到通道2
     * @throws Exception
     */
    @Test
    public void testTransferTo() throws Exception {
        FileInputStream fis1 = new FileInputStream("hello.txt");
        FileChannel channel1 = fis1.getChannel();
        FileOutputStream fis2 = new FileOutputStream("hello_copy.txt");
        FileChannel channel2 = fis2.getChannel();
        channel1.transferTo(0, channel1.size(), channel2);
        fis1.close();
        fis2.close();
        System.out.println("传输完成");
    }
}
