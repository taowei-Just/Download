package com.tao.mydownloadlibrary;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tao.mydownloadlibrary.callback.DownloadCall;
import com.tao.mydownloadlibrary.info.DownloadInfo;
import com.tao.mydownloadlibrary.info.TaskInfo;
import com.tao.mydownloadlibrary.utils.MyGosn;

import java.io.LineNumberReader;
import java.util.ArrayList;

public class GsonTest {

    public static void main(String[] args) throws Exception {

        DownloadInfo downloadInfo = new DownloadInfo("url");
        downloadInfo.setTaskInfos(new ArrayList<TaskInfo>());
        downloadInfo.setDownloadCall(new DownloadCall() {
            @Override
            public void onProgress(DownloadInfo downloadInfo) {

            }

            @Override
            public void onProgress(DownloadInfo downloadInfo, TaskInfo info) {

            }

            @Override
            public void onStart(DownloadInfo downloadInfo) {

            }

            @Override
            public void onCompleted(DownloadInfo downloadInfo) {

            }

            @Override
            public void onError(DownloadInfo downloadInfo) {

            }

            @Override
            public void onPrepare(DownloadInfo downloadInfo) {

            }
        });

        //
 
        //生成json
        String gsonStr = MyGosn.toJson(downloadInfo  );
        System.out.println("===========gsonStr:" + gsonStr);
        DownloadInfo downloadInfo1 = MyGosn.fromJson(gsonStr, DownloadInfo.class);
        System.out.println("===========downloadInfo1:" + downloadInfo1.toString());

 

    }

    static class MyExclusionStrategy implements ExclusionStrategy {
        DownloadInfo downloadInfo;

        public MyExclusionStrategy(DownloadInfo downloadInfo) {
            this.downloadInfo = downloadInfo;
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            if (f.getName().equals("DownloadCall")) {
                return true; //过滤掉name字段
            }
            return false;
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }


}

