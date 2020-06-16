package com.tao.mydownloadlibrary.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.GsonBuilder;

public class MyGosn {

    public static String toJson(Object object, String... feildName) {
        return new GsonBuilder().addSerializationExclusionStrategy(new MyExclusionStrategy(feildName)).create().toJson(object);
    }
    public static <T> T fromJson(String str ,Class<T> t){
        return new  GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().fromJson(str ,t);
    }


    static class MyExclusionStrategy implements ExclusionStrategy {
        String [] feildNames;

        public MyExclusionStrategy(String[] feildNames) {
            this.feildNames = feildNames;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            for (String feildName : feildNames) {
                if (f.getName().equals(feildName)) {
                    return true; //过滤掉name字段
                }
            }
           
            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

}
