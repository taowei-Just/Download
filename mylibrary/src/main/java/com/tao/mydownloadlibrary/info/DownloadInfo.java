package com.tao.mydownloadlibrary.info;


import com.google.gson.annotations.Expose;
import com.tao.mydownloadlibrary.DownloadStatue;
import com.tao.mydownloadlibrary.callback.DownloadCall;
import com.tao.mydownloadlibrary.utils.MD5Util;
import com.tao.mydownloadlibrary.utils.TextUtils;

import java.util.List;


public class DownloadInfo {
    @Expose
    String downloadTag;
    // 下载状态
    @Expose
    DownloadStatue statue;
    // 下载链接
    @Expose
    String url;
    // 线程数量
    @Expose
    int threadCount = 1;
    //文件长度
    @Expose
    long totalLenth;
    // 下载进度
    @Expose
    long progress;
    // 文件名称
    @Expose
    private String fileName;
    // 存储路径
    @Expose
    private String path;
    @Expose(serialize = false, deserialize = false)
    DownloadCall downloadCall;
    @Expose
    String md5;
    @Expose
    List<TaskInfo> taskInfos;
    @Expose
    int progressPersent;
    private String cachePath;


    public DownloadInfo(String url) throws Exception {
        boolean empty = false;
        try {
            empty = TextUtils.isEmpty(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (empty) {
            throw new Exception("url is null exception");
        }

        this.url = url;
        downloadTag = MD5Util.md5(url);
    }

    public String getFilePath() {
        return path + fileName;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getDownloadTag() {
        return downloadTag;
    }

    public List<TaskInfo> getTaskInfos() {
        return taskInfos;
    }

    public void setTaskInfos(List<TaskInfo> taskInfos) {
        this.taskInfos = taskInfos;
    }

    public DownloadCall getDownloadCall() {
        return downloadCall;
    }

    public void setDownloadCall(DownloadCall downloadCall) {
        this.downloadCall = downloadCall;
    }

    public DownloadStatue getStatue() {
        return statue;
    }

    public void setStatue(DownloadStatue statue) {
        this.statue = statue;
    }

    public String getUrl() {
        return url;
    }


    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public long getTotalLenth() {
        return totalLenth;
    }

    public void setTotalLenth(long totalLenth) {
        this.totalLenth = totalLenth;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
        this.progressPersent = (int) ((float) progress / totalLenth * 100);
    }

    public int getProgressPersent() {
        return progressPersent;
    }

    public void setProgressPersent(int progressPersent) {
        this.progressPersent = progressPersent;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return "DownloadInfo{" +
                "downloadTag='" + downloadTag + '\'' +
                ", statue=" + statue +
                ", url='" + url + '\'' +
                ", threadCount=" + threadCount +
                ", totalLenth=" + totalLenth +
                ", progress=" + progress +
                ", fileName='" + fileName + '\'' +
                ", path='" + path + '\'' +
                ", downloadCall=" + downloadCall +
                ", taskInfos=" + taskInfos +
                '}';
    }

    public String getCachePath() {
        return TextUtils.isEmpty(cachePath)? getPath():cachePath;
    }

    public void setCachePath(String cachePath) {
        this.cachePath = cachePath;
    }
}
