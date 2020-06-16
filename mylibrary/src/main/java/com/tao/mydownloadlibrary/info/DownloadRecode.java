package com.tao.mydownloadlibrary.info;

import java.util.List;

/**
 * 下载历史记录
 */
public class DownloadRecode {

    /**
     * 记录总数量
     */
    int count;
    /**
     * 当前记录的位置
     */
    int index;

    List<DownloadInfo> infoList;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public List<DownloadInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<DownloadInfo> infoList) {
        this.infoList = infoList;
    }
}
