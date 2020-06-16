package com.tao.mydownloadlibrary.callback;

import com.tao.mydownloadlibrary.info.DownloadInfo;
import com.tao.mydownloadlibrary.info.TaskInfo;

import java.util.List;

public interface PrepareTaskCall {

    void onComplete(DownloadInfo downloadInfo, List<TaskInfo> taskInfoS);

    void onError(DownloadInfo downloadInfo);
 
}
