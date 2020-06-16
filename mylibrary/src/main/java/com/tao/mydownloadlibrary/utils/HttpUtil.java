package com.tao.mydownloadlibrary.utils;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {

    private static OkHttpClient okHttpClient =new OkHttpClient();

    public static Response callGet(String url) throws Exception {
        return okHttpClient.newCall(new Request.Builder().url(url).get().build()).execute();
    }

    public static Response callGetBuilder(  Request.Builder builder) throws IOException {
        return okHttpClient.newCall(builder.get().build()).execute();
    }
}
