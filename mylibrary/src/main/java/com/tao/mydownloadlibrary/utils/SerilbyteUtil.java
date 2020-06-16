package com.tao.mydownloadlibrary.utils;

public class SerilbyteUtil {
    public SerilbyteUtil() {
    }

    public static String bytes2hex03(byte[] bytes) {
        String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        byte[] var3 = bytes;
        int var4 = bytes.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            sb.append("0123456789abcdef".charAt(b >> 4 & 15));
            sb.append("0123456789abcdef".charAt(b & 15));
        }

        return sb.toString();
    }

    public static String bytes2hex02(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        byte[] var3 = bytes;
        int var4 = bytes.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            byte b = var3[var5];
            tmp = Integer.toHexString(255 & b);
            if (tmp.length() == 1) {
                tmp = "0" + tmp;
            }

            sb.append(tmp);
        }

        return sb.toString();
    }

    public static byte[] hexStringToByte(String hex) {
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();

        for(int i = 0; i < len; ++i) {
            int pos = i * 2;
            result[i] = (byte)(toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }

        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte)"0123456789ABCDEF".indexOf(c);
        return b;
    }

    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);

        for(int i = 0; i < bArray.length; ++i) {
            String sTemp = Integer.toHexString(255 & bArray[i]);
            if (sTemp.length() < 2) {
                sb.append(0);
            }

            sb.append(sTemp.toUpperCase());
        }

        return sb.toString();
    }

    public static String string2HexString(String s) throws Exception {
        String r = bytes2HexString(string2Bytes(s));
        return r;
    }

    public static byte[] string2Bytes(String s) {
        byte[] r = s.getBytes();
        return r;
    }

    public static String bytes2HexString(byte[] b) {
        String r = "";

        for(int i = 0; i < b.length; ++i) {
            String hex = Integer.toHexString(b[i] & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            r = r + hex.toUpperCase();
        }

        return r;
    }

    public static byte[] hex2byte(String hex) {
        String digital = "0123456789ABCDEF";
        String hex1 = hex.replace(" ", "");
        char[] hex2char = hex1.toCharArray();
        byte[] bytes = new byte[hex1.length() / 2];

        for(int p = 0; p < bytes.length; ++p) {
            byte temp = (byte)(digital.indexOf(hex2char[2 * p]) * 16);
            temp = (byte)(temp + digital.indexOf(hex2char[2 * p + 1]));
            bytes[p] = (byte)(temp & 255);
        }

        return bytes;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString != null && !hexString.equals("")) {
            hexString = hexString.toUpperCase();
            int length = hexString.length() / 2;
            char[] hexChars = hexString.toCharArray();
            byte[] d = new byte[length];

            for(int i = 0; i < length; ++i) {
                int pos = i * 2;
                d[i] = (byte)(charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
            }

            return d;
        } else {
            return null;
        }
    }

    private static byte charToByte(char c) {
        return (byte)"0123456789ABCDEF".indexOf(c);
    }

    public static void printHexString(byte[] b) {
        for(int i = 0; i < b.length; ++i) {
            String hex = Integer.toHexString(b[i] & 255);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }

            System.out.print(hex.toUpperCase());
            Lg.e("printHexString  ", hex.toUpperCase());
        }

    }

    public static int byte2Int(byte b) {
        return b;
    }

    public static byte int2Byte(int i) {
        byte r = (byte)i;
        return r;
    }

    public static String bytes2String(byte[] b) throws Exception {
        String r = new String(b, "UTF-8");
        return r;
    }

    public static String hex2String(String hex) throws Exception {
        String r = bytes2String(hexString2Bytes(hex));
        return r;
    }

    public static byte[] hexString2Bytes(String hex) {
        if (hex != null && !hex.equals("")) {
            if (hex.length() % 2 != 0) {
                return null;
            } else {
                hex = hex.toUpperCase();
                int len = hex.length() / 2;
                byte[] b = new byte[len];
                char[] hc = hex.toCharArray();

                for(int i = 0; i < len; ++i) {
                    int p = 2 * i;
                    b[i] = (byte)(charToByte(hc[p]) << 4 | charToByte(hc[p + 1]));
                }

                return b;
            }
        } else {
            return null;
        }
    }
}
