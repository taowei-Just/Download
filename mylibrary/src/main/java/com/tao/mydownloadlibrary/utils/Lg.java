package com.tao.mydownloadlibrary.utils;

import android.util.Log;

import com.google.gson.Gson;

public class Lg {
    private static String tag = "log";

    public static void e(Object s) {
        if (s == null) {
            e("null");
            return;
        }
        e(new Gson().toJson(s));

    }

    public static void e(String s) {
        e(tag, s);
    }

    public static void e(String tag, Object s) {
        Log.e(tag, new Gson().toJson(s));
    }

    public static void e(String tag, String s) {
        Log.e(tag, s);
    }
}
