package com.tao.mydownloadlibrary.helper;

import com.tao.mydownloadlibrary.callback.DownloadCall;
import com.tao.mydownloadlibrary.info.DownloadRecode;

public interface IDownloader {

    /**
     * 添加下载任务
     * @param url
     * @param downloadCall
     */
      void addDownload(String url, DownloadCall downloadCall);

    /**
     * 停止下载任务
     * @param url
     */
      void stopDownload(String url) ;
      
    /**
     * 
     * 重行下载任务
     */
    void reDownload(String url, DownloadCall downloadCall);
    /**
     * 删除下载任务
     */
    void deleteDownload(String url);

    /**
     * 列出下载记录
     * @param index  起始位置
     * @param maxCount 返回最大数据量
     * @return
     */
    DownloadRecode listDownloadRecode(int index,int maxCount);
     /**
     * 列出下载记录
     * @param index  起始位置
     * @param   
     * @return
     */
    DownloadRecode listDownloadRecode(int index);
    /**
     * 列出下载记录

     */
    DownloadRecode listDownloadRecode();
    
    
}
