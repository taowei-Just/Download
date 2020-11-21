package com.tao.mydownloadlibrary.info;

import com.google.gson.annotations.Expose;
import com.tao.mydownloadlibrary.DownloadStatue;

public class TaskInfo {
    @Expose
    int id;
    // 下载id
    @Expose
    String downloadTag;
    @Expose
    int taskId;
    // 下载链接地址
    @Expose
    String url;
    //保存的文件名称
    @Expose
    String fileName;
    // 文件总长度
    @Expose
    long fileLen;
    // 下载线程数
    @Expose
    int threadCount = 1;
    // 分配给每个线程的长度
    @Expose
    long threadLen = -1;
    // 每个线程下载的进度
    @Expose
    long progressLen;
    // 缓存文件地址
    @Expose
    String cacheFile;
    // 偏移
    @Expose
    private long offeset = 0;
    @Expose
    long currentLen;
    @Expose

    DownloadStatue statue =DownloadStatue.prepare;   // 状态 0 未启动 ， 1.准备中 ，2.下载中 ，3 异常 ，4 完成 

    public TaskInfo(String downloadId, int taskId, String url, String fileName) {
        this.downloadTag = downloadId;
        this.fileName = fileName;
        this.taskId = taskId;
        this.url = url;
        this.cacheFile = fileName + ".cache";
    }


    public DownloadStatue getStatue() {
        return statue;
    }

    public void setStatue(DownloadStatue statue) {
        this.statue = statue;
    }

    public int getTaskId() {
        return taskId;

    }

    public String getDownloadTag() {
        return downloadTag;
    }

    public void setDownloadTag(String downloadTag) {
        this.downloadTag = downloadTag;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    @Override
    public String toString() {

        return "TaskInfo{" +
                "id=" + id +
                ", downloadTag=" + downloadTag +
                ", taskId=" + taskId +
                ", url='" + url + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileLen=" + fileLen +
                ", threadCount=" + threadCount +
                ", threadLen=" + threadLen +
                ", progressLen=" + progressLen +
                ", cacheFile='" + cacheFile + '\'' +
                ", offeset=" + offeset +
                ", currentLen=" + currentLen +
                '}';
    }

    public long getCurrentLen() {
        return currentLen;
    }

    public void setCurrentLen(long currentLen) {
        this.currentLen = currentLen;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileLen() {
        return fileLen;
    }

    public void setFileLen(long fileLen) {
        this.fileLen = fileLen;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public long getThreadLen() {
        return threadLen;
    }

    public void setThreadLen(long threadLen) {
        this.threadLen = threadLen;
    }

    public long getProgressLen() {
        return progressLen;
    }

    public void setProgressLen(long progressLen) {
        this.progressLen = progressLen;
    }

    public String getCacheFile() {
        return cacheFile;
    }

    public void setCacheFile(String cacheFile) {
        this.cacheFile = cacheFile;
    }

    public long getOffeset() {
        return offeset;
    }

    public void setOffeset(long offeset) {
        this.offeset = offeset;
    }
}
