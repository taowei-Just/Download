package com.tao.mydownloadlibrary.task;

import com.tao.mydownloadlibrary.DownloadStatue;
import com.tao.mydownloadlibrary.callback.DownloadTaskCAll;
import com.tao.mydownloadlibrary.info.TaskInfo;
import com.tao.mydownloadlibrary.utils.HttpUtil;
import com.tao.mydownloadlibrary.utils.Lg;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
        FileOutputStream ops = null;
        RandomAccessFile accessFile = null;
        try {
//            Lg.e(tag, "DownloadTask  run  " + info.getCacheFile());
            if (info.getOffeset() == info.getFileLen() && info.getThreadLen() > 0) {
                downloadCall.onCompleted(info);
                return;
            }
            info.setStatue(DownloadStatue.downloading);

            Request.Builder builder = new Request.Builder();
            if (info.getThreadLen() > 0)
                builder.addHeader("RANGE", "bytes=" + (info.getOffeset() + info.getProgressLen()) + "-" + (info.getEndBound()));
            builder.url(info.getUrl());
            Response execute = HttpUtil.callGetBuilder(builder);
            long length = execute.body().contentLength();

            Lg.e(tag, "length  " + length + " " + info.getThreadLen() + "   " + Thread.currentThread());
            Lg.e(info.toString());
            InputStream inputStream = execute.body().byteStream();
            File file = new File(info.getCacheFile());
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            if (isAccess()) {
                if (file.exists()) {
                    file.delete();
                }
                ops = new FileOutputStream(file);
            } else {
                accessFile = new RandomAccessFile(file, "rwd");
                accessFile.setLength(info.getThreadLen());
                accessFile.seek(info.getOffeset() + info.getProgressLen());
//                accessFile.setLength(info.getThreadLen());
////                accessFile.seek(0);
            }
            downloadCall.onStart(info);
            byte[] buff = new byte[1024 * 1024];
            int len;
            long time = System.currentTimeMillis();
            int cacheLen = 0;
            while ((len = inputStream.read(buff)) != -1) {
                if (info.getProgressLen() + len > info.getThreadLen() && info.getThreadLen() > 0) {
                    len = (int) (info.getThreadLen() - info.getProgressLen());
                }
                if (len <= 0)
                    continue;
                if (isAccess()) {
                    ops.write(buff, 0, len);
                } else {
                    accessFile.write(buff, 0, len);
                }
                cacheLen += len;
                if (System.currentTimeMillis() - time >= 1000) {
                    writeProgress(cacheLen);
                    time = System.currentTimeMillis();
                    cacheLen = 0;
                }
                if (info.getProgressLen() >= info.getThreadLen())
                    break;
                Thread.sleep(1);
            }
            if (cacheLen > 0) {
                writeProgress(cacheLen);
            }
            info.setStatue(DownloadStatue.complete);
            downloadCall.onCompleted(info);
            execute.body().close();
        } catch (Exception e) {
            e.printStackTrace();
            info.setStatue(DownloadStatue.error);
            downloadCall.onError(info);
        } finally {
            try {
                if (accessFile != null)
                    accessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (ops != null) {
                    ops.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isAccess() {
        return info.getThreadLen() > 0;
    }

    private void writeProgress(int len) {
//        Lg.e(tag, "cacheLen" + len + " ProgressLen " + info.getProgressLen() + " threadLean" + info.getThreadLen());
        info.setCurrentLen(len);
        info.setProgressLen(info.getProgressLen() + len);
        downloadCall.onProgress(info);
    }
}
