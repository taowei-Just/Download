package com.tao.mydownloadlibrary.callback;
import com.tao.mydownloadlibrary.info.TaskInfo;

public interface DownloadTaskCAll {
    void onProgress(TaskInfo info);

    void onStart(TaskInfo info);

    void onCompleted(TaskInfo info);
    
    void onError(TaskInfo info);

}
