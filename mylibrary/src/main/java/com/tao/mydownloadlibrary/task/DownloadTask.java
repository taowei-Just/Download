package com.tao.mydownloadlibrary.task;

import com.tao.mydownloadlibrary.callback.DownloadTaskCAll;
import com.tao.mydownloadlibrary.info.TaskInfo;
import com.tao.mydownloadlibrary.utils.HttpUtil;
import com.tao.mydownloadlibrary.utils.Lg;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.Request;
import okhttp3.Response;


public class DownloadTask implements Runnable {
    private String tag = getClass().getSimpleName();
    TaskInfo info;
    DownloadTaskCAll downloadCall;

    public DownloadTask(TaskInfo info, DownloadTaskCAll downloadCall) {
        this.info = info;
        this.downloadCall = downloadCall;
    }

    @Override
    public void run() {
        try {
//            Lg.e(tag, "run  " + info);
            if (info.getOffeset() == info.getFileLen()) {
                downloadCall.onCompleted(info);
                return;
            }
            Request.Builder builder = new Request.Builder();
            builder.addHeader("RANGE", "bytes=" + (info.getOffeset() + info.getProgressLen()) + "-" + (info.getOffeset() + info.getThreadLen()));
            builder.url(info.getUrl());
            Response execute = HttpUtil.callGetBuilder(builder);
            long length = execute.body().contentLength();
//            Lg.e(tag, "length  " + length + " thread " + Thread.currentThread());

            InputStream inputStream = execute.body().byteStream();
            File file = new File(info.getCacheFile());
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            } else {
                file.delete();
            }
            RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
//            accessFile.setLength(info.getFileLen());
//            accessFile.seek(info.getOffeset() + info.getProgressLen());    
            accessFile.setLength(info.getThreadLen());
            accessFile.seek(0);
            downloadCall.onStart(info);
            byte[] buff = new byte[1024 * 10];
            int len;
            long time = System.currentTimeMillis();
            int cacheLen = 0;
            while ((len = inputStream.read(buff)) != -1) {
                if (info.getProgressLen() + len > info.getThreadLen()) {
                    len = (int) (info.getThreadLen() - info.getProgressLen());
                }
                if (len <= 0)
                    continue;
                accessFile.write(buff, 0, len);
                cacheLen += len;
                if (System.currentTimeMillis() - time >= 1000) {
                    writeProgress(cacheLen);
                    time = System.currentTimeMillis();
                    cacheLen = 0;
                }
                Thread.sleep(1);
            }
            if (cacheLen > 0) {
                writeProgress(cacheLen);
            }
            downloadCall.onCompleted(info);
            execute.body().close();
        } catch (Exception e) {
            e.printStackTrace();
//            Lg.e(tag, info);
            downloadCall.onError(info);
        }
    }

    private void writeProgress(int len) {
//        Lg.e(tag, "cacheLen" + len + " ProgressLen " + info.getProgressLen() + " threadLean" + info.getThreadLen());
        info.setCurrentLen(len);
        info.setProgressLen(info.getProgressLen() + len);
        downloadCall.onProgress(info);
    }
}
