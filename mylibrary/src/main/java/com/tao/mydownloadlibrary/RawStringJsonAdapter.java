package com.tao.mydownloadlibrary;


import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

//将部分json内容保留为字符串形式，不进行解析
public   class RawStringJsonAdapter extends TypeAdapter<String> {
    @Override
    public void write(JsonWriter out, String value) throws IOException {
        out.value(value);
    }
    
    @Override
    public String read(JsonReader in) throws IOException {
        return new JsonParser().parse(in).toString();
    }
}

 