package com.tao.mydownloadlibrary.utils;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MD5Util {
    public MD5Util() {
    }

    public static String md5FromFile(String path, boolean upper) throws IOException {
        new FileInputStream(path);
        String md5ByFile = getMd5ByFile(new File(path));
        return upper ? md5ByFile.toUpperCase() : md5ByFile.toLowerCase();
    }

 

    public static String getMD5fromBigFile(File inputFile) throws Exception {
        int bufferSize = 262144;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            fileInputStream = new FileInputStream(inputFile);
            digestInputStream = new DigestInputStream(fileInputStream, messageDigest);
            byte[] buffer = new byte[bufferSize];

            while(digestInputStream.read(buffer) > 0) {
            }

            messageDigest = digestInputStream.getMessageDigest();
            byte[] resultByteArray = messageDigest.digest();
            String var7 = SerilbyteUtil.bytes2HexString(resultByteArray);
            return var7;
        } finally {
            try {
                digestInputStream.close();
                fileInputStream.close();
            } catch (Exception var14) {
                var14.printStackTrace();
            }

        }
    }

    public static String getMd5ByFile(File file) throws FileNotFoundException {
        String value = null;
        FileInputStream in = new FileInputStream(file);

        try {
            MappedByteBuffer byteBuffer = in.getChannel().map(MapMode.READ_ONLY, 0L, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            byte[] digest = md5.digest();
            StringBuffer buffer = new StringBuffer();
            byte[] var7 = digest;
            int var8 = digest.length;

            for(int var9 = 0; var9 < var8; ++var9) {
                byte b = var7[var9];
                int number = b & 255;
                String hexString = Integer.toHexString(number);
                if (hexString.length() == 1) {
                    buffer.append("0" + hexString);
                } else {
                    buffer.append(hexString);
                }
            }

            value = buffer.toString().toUpperCase();
        } catch (Exception var21) {
            var21.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException var20) {
                    var20.printStackTrace();
                }
            }

        }

        return value;
    }

    public static String md5(String string) {
        if (string != null && !string.isEmpty()) {
            MessageDigest md5 = null;

            try {
                md5 = MessageDigest.getInstance("MD5");
                byte[] bytes = md5.digest(string.getBytes());
                String result = "";
                byte[] var4 = bytes;
                int var5 = bytes.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    byte b = var4[var6];
                    String temp = Integer.toHexString(b & 255);
                    if (temp.length() == 1) {
                        temp = "0" + temp;
                    }

                    result = result + temp;
                }

                return result;
            } catch (NoSuchAlgorithmException var9) {
                var9.printStackTrace();
                return "";
            }
        } else {
            return "";
        }
    }

   
    public static void main(String[] args) {
        long l = System.currentTimeMillis();

        String md5fromBigFile;
        try {
            md5fromBigFile = getMd5ByFile(new File("C:\\Project\\Android\\Work\\TobaccoQD\\build\\outputs\\apk\\release\\2019年09月23日\\智能烟草机_108000_1.1.0.apk"));
            System.err.println(" 01 " + md5fromBigFile + " " + (System.currentTimeMillis() - l));
        } catch (FileNotFoundException var5) {
            var5.printStackTrace();
        }

        try {
            l = System.currentTimeMillis();
            md5fromBigFile = getMD5fromBigFile(new File("C:\\Project\\Android\\Work\\TobaccoQD\\build\\outputs\\apk\\release\\2019年09月23日\\智能烟草机_108000_1.1.0.apk"));
            System.err.println(" 01 " + md5fromBigFile + " " + (System.currentTimeMillis() - l));
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }
}
