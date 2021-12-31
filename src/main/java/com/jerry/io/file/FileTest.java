package com.jerry.io.file;

import java.io.File;
import java.io.IOException;

/**
 * @Author jerry
 * @Description File类的使用
 * @Date 2021-12-28 14:49
 * @Version 1.0
 **/
public class FileTest {
    public static void main(String[] args) throws IOException {
        //默认当前项目目录
        File file = new File("hello.txt");
        if (!file.exists()) {
            file.createNewFile();
        }
    }
}
