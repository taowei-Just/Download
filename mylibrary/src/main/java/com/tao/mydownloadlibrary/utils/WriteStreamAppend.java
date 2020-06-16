package com.tao.mydownloadlibrary.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;

public class WriteStreamAppend {

    public static void method1(String file, String conent) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(conent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void method2(String fileName, String content) {
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件      
            FileWriter writer = new FileWriter(fileName, true);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void method3(String fileName, String content) {
        try {
            // 打开一个随机访问文件流，按读写方式                                    
            RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
            // 文件长度，字节数                                                      
            long fileLength = randomFile.length();
            // 将写文件指针移到文件尾。                                              
            randomFile.seek(fileLength);
            randomFile.writeBytes(content);
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String readFileString(File file) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            StringBuilder builder = new StringBuilder();
            String buff = null;
            while ((buff = bufferedReader.readLine()) != null) {
                builder.append(buff);
                builder.append("\n");
            }

            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }

        return "";
    }

    public static void main(String[] args) {

     
        File file = new File("c:/test.txt");
        if (!file.exists()){
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("start");
        method1(file.getAbsolutePath(), "追加到文件的末尾1\n");
        method1(file.getAbsolutePath(), "---\n");
        method1(file.getAbsolutePath(), "追加到文件的末尾2\n");
        method2(file.getAbsolutePath(), "追加到文件的末尾3\n");
        System.out.println("end");

        System.out.println( 
        readFileString(file));
        
    }
}
