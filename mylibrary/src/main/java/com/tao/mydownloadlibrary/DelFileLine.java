package com.tao.mydownloadlibrary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class DelFileLine {
    public DelFileLine() {
    }

    private static String filePath = "c:/test.txt";

    /**
     * 删除文件指定行 从1开始      * @param indexLine
     */
    public static String deleteLine(int indexLine) {
        int counter = 1;
        FileWriter writer = null;
        BufferedReader buffReader = null;
        StringBuffer tempTxt = new StringBuffer();
        try {
            File file = new File(filePath);
            FileReader freader = new FileReader(file);
            buffReader = new BufferedReader(freader);
            while (buffReader.ready()) {
                if (counter != indexLine) {
                    tempTxt.append(buffReader.readLine() + "\n");
                } else {
                    buffReader.readLine();
                }
                counter++;
            }
            buffReader.close();
            writer = new FileWriter(file);
            writer.write(tempTxt.toString());
            writer.close();
        } catch (Exception e) {
            return "fail :" + e.getCause();
        }
        return "success!";
    }

    public static void main(String[] args) {
        System.out.println(DelFileLine.deleteLine(9));
    }
} 
