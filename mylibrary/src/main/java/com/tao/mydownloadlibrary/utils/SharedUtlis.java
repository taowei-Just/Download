package com.tao.mydownloadlibrary.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedUtlis {
    Context context;
    String name;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEdit;

    public SharedUtlis(Context context, String name) {
        this.context = context;
        this.name = name;
        this.mPreferences = context.getSharedPreferences(name, Context.MODE_MULTI_PROCESS);
        this.mEdit = this.mPreferences.edit();
    }

    public void putString(String key, String value) {
        this.mEdit.putString(key, value);
        this.mEdit.commit();
    }

    public void putBoolean(String key, boolean value) {
        this.mEdit.putBoolean(key, value);
        this.mEdit.commit();
    }

    public boolean getBoolean(String key, boolean def) {
        return this.mPreferences.getBoolean(key, def);
    }

    public String getString(String key, String def) {
        return this.mPreferences.getString(key, def);
    }

    public int getInt(String key, int defo) {
        return this.mPreferences.getInt(key, defo);
    }

    public void putInt(String key, int value) {
        this.mEdit.putInt(key, value);
        this.mEdit.commit();
    }

    public long getLong(String key, long defo) {
        return this.mPreferences.getLong(key, defo);
    }

    public void putLong(String key, long value) {
        this.mEdit.putLong(key, value);
        this.mEdit.commit();
    }

    public String getString(String url) {
        return  getString(url, "");
    }
}
