package com.tao.mydownloadlibrary.callback;

import com.tao.mydownloadlibrary.info.DownloadInfo;
import com.tao.mydownloadlibrary.info.TaskInfo;

public interface DownloadCall {

    void onProgress(DownloadInfo downloadInfo);

    void onProgress(DownloadInfo downloadInfo, TaskInfo info);

    void onStart(DownloadInfo downloadInfo);

    void onCompleted(DownloadInfo downloadInfo);

    void onError(DownloadInfo downloadInfo);

    void onPrepare(DownloadInfo downloadInfo);
}
