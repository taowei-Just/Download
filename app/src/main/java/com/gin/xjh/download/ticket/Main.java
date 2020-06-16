package com.gin.xjh.download.ticket;

import com.tao.mydownloadlibrary.utils.HttpUtil;

import okhttp3.OkHttpClient;

public class Main {
    static String url ="http://quotes.money.163.com/service/chddata.html";
    public static void main(String[] args) {
        getTicket( url,"510300" ,"20010101","20200614" );
    }

    private static void getTicket(String url, String code, String start, String end) {
      String  urls = url+"?code=1"+code+"&start="+start+"&end="+end+"&fields=TCLOSE;HIGH;LOW;TOPEN;LCLOSE;CHG;PCHG;VOTURNOVER";
        try {
            String string = HttpUtil.callGet(urls).body().string();
            System.err.println("" +string);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
