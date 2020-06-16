package com.gin.xjh.download.services;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.gin.xjh.download.entities.FileInfo;
import com.tao.mydownloadlibrary.callback.DownloadCall;
import com.tao.mydownloadlibrary.helper.DownloadHelper;
import com.tao.mydownloadlibrary.info.DownloadInfo;
import com.tao.mydownloadlibrary.info.TaskInfo;
import com.tao.mydownloadlibrary.utils.Lg;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class DownloadService extends Service {

    public static final String DOWNLOAD_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/downloads/";
    public static final int MSG_INIT = 0x1;
    public static final int MSG_BIND = 0x2;
    public static final int MSG_START = 0x3;
    public static final int MSG_STOP = 0x4;
    public static final int MSG_FINISH = 0x5;
    public static final int MSG_UPDATE = 0x6;
    public static final int MSG_RESTART = 0x7;
    public static final int MSG_DELETE =0x8;
    private Map<Integer, DownloadTask> mTasks = new LinkedHashMap<>();

    private Messenger mActivityMessenger = null;
    private DownloadHelper downloadHelper;
    private String tag = getClass().getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //创建一个Messenger对象，包含Handler的引用
        Messenger messenger = new Messenger(mHandler);
        downloadHelper = DownloadHelper.getInstance(new DownloadHelper.Build(this).setTaskCount(3).setMissionCount(3));
        return messenger.getBinder();
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            FileInfo fileInfo;
            DownloadTask task;
            switch (msg.what) {
                case MSG_INIT:
                    fileInfo = (FileInfo) msg.obj;
                    //Lg.i("test", "Init:" + fileInfo.getLength());
                    //启动下载任务
                    task = new DownloadTask(DownloadService.this, fileInfo, mActivityMessenger, 3);
                    task.download();
                    mTasks.put(fileInfo.getId(), task);
                    break;
                case MSG_BIND:
                    mActivityMessenger = msg.replyTo;
                    break;
                case MSG_START:
                    fileInfo = (FileInfo) msg.obj;
//                    DownloadTask.sExecutorService.execute(new InitThread(fileInfo));
                    start(fileInfo);
                    break;
                case MSG_RESTART:
                    fileInfo = (FileInfo) msg.obj;
                    reDownload(fileInfo);
                    break;

                case MSG_STOP:
                    fileInfo = (FileInfo) msg.obj;
//                    task = mTasks.get(fileInfo.getId());
//                    if (task != null) {
//                        task.isPause = true;
//                    }
                    stop(fileInfo);
                    break;    case MSG_DELETE:
                    fileInfo = (FileInfo) msg.obj;
//                    task = mTasks.get(fileInfo.getId());
//                    if (task != null) {
//                        task.isPause = true;
//                    }
                    delete(fileInfo);
                    break;
            }
        }
    };

    private void delete(FileInfo fileInfo) {
        downloadHelper.deleteDownload(fileInfo.getUrl());
    }

    private void reDownload(FileInfo fileInfo) {
        fileInfoMap.put(fileInfo.getUrl(), fileInfo);
        downloadHelper.reDownload(fileInfo.getUrl() ,new MyDownloadCall());
    }

    private void stop(FileInfo fileInfo) {
        fileInfoMap.put(fileInfo.getUrl(), fileInfo);
        downloadHelper.stopDownload(fileInfo.getUrl());
    }

    Map<String, FileInfo> fileInfoMap = new HashMap<>();

    private void start(FileInfo fileInfo) {
        fileInfoMap.put(fileInfo.getUrl(), fileInfo);
        downloadHelper.addDownload(fileInfo.getUrl(), downloadHelper.getCachePath(), fileInfo.getFileName(), new MyDownloadCall());
    }

    class MyDownloadCall implements DownloadCall {
        @Override
        public void onProgress(DownloadInfo downloadInfo) {
            sendProgress(downloadInfo);
        }

        @Override
        public void onProgress(DownloadInfo downloadInfo, TaskInfo info) {
            Lg.e(tag, " onProgress " + downloadInfo.getProgressPersent() + "  " + Thread.currentThread());
        }

        @Override
        public void onStart(DownloadInfo downloadInfo) {
            Lg.e(tag, " onStart ");
            sendProgress(downloadInfo);
        }

        @Override
        public void onCompleted(DownloadInfo downloadInfo) {
            Lg.e(tag, " onCompleted ");
            sendProgress(downloadInfo);
        }

        @Override
        public void onError(DownloadInfo downloadInfo) {
            Lg.e(tag, " onError ");
        }

        @Override
        public void onPrepare(DownloadInfo downloadInfo) {
            Lg.e(tag, " onPrepare ");

        }
    }

    private void sendProgress(DownloadInfo downloadInfo) {
        FileInfo fileInfo = fileInfoMap.get(downloadInfo.getUrl());
        Message msg = new Message();
        msg.what = DownloadService.MSG_UPDATE;
        msg.arg1 = downloadInfo.getProgressPersent();
        msg.arg2 = fileInfo.getId();
        try {
            mActivityMessenger.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    class InitThread extends Thread {
        private FileInfo mFileInfo = null;

        public InitThread(FileInfo mFileInfo) {
            this.mFileInfo = mFileInfo;
        }

        @Override
        public void run() {
            HttpURLConnection conn = null;
            RandomAccessFile raf = null;
            try {
                //连接网络文件
                URL url = new URL(mFileInfo.getUrl());
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");
                int length = -1;
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    //获得文件长度
                    length = conn.getContentLength();
                }
                if (length <= 0) {
                    return;
                }
                //在本地创建文件
                File dir = new File(DOWNLOAD_PATH);//验证下载地址
                if (!dir.exists()) {
                    dir.mkdir();
                }
                File file = new File(dir, mFileInfo.getFileName());
                raf = new RandomAccessFile(file, "rwd");//r：读权限，w：写权限，d：删除权限
                //设置文件长度
                raf.setLength(length);
                mFileInfo.setLength(length);
                mHandler.obtainMessage(MSG_INIT, mFileInfo).sendToTarget();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
                try {
                    if (raf != null)
                        raf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
