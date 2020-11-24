package com.tao.download;

import com.tao.mydownloadlibrary.callback.DownloadCall;
import com.tao.mydownloadlibrary.helper.DownloadHelper;
import com.tao.mydownloadlibrary.info.DownloadInfo;
import com.tao.mydownloadlibrary.info.TaskInfo;
import com.tao.mydownloadlibrary.utils.Lg;

import java.io.File;

public class DwonloadTest {

    public static void main(String[] args) {

//        new File("E:\\新建文件夹\\\\c70ea4b7d50614c21fb69671d0f61d39\\2345haozip_6.2.0.11032_setup.exe").delete();
        DownloadHelper.Build build = new DownloadHelper.Build(null);
        build.setMissionCount(3)
                .setTaskCount(3)
                .setRootPath("E:\\新建文件夹\\")
        ;
        DownloadHelper.getInstance().init(build);
        DownloadCall downloadCall = new Myd();
        DownloadHelper.getInstance().addDownload("https://pm.myapp.com/invc/xfspeed/qqpcmgr/download/QQPCDownload110023.exe", downloadCall);
        DownloadHelper.getInstance().addDownload("http://wap.apk.anzhi.com/data5/apk/202011/02/fe31a83fa694652d2220911487dd6a22_16286800.apk", downloadCall);
        DownloadHelper.getInstance().addDownload("https://forspeed.rbread05.cn/down/newdown/10/28/BactchRename.rar", downloadCall);
        
        
    }

    private static class Myd implements DownloadCall {
        private String tag = "";

        @Override
        public void onProgress(DownloadInfo downloadInfo) {

            Lg.e(tag, "onProgress " + downloadInfo.getFilePath() + "   " + downloadInfo.getProgressPersent() + "%" + " " + Thread.currentThread().getName());
        }

        @Override
        public void onProgress(DownloadInfo downloadInfo, TaskInfo info) {

        }

        @Override
        public void onStart(DownloadInfo downloadInfo) {
            Lg.e(tag, "onStart " + downloadInfo.getFilePath());

        }

        @Override
        public void onCompleted(DownloadInfo downloadInfo) {
            Lg.e(tag, "onCompleted " + downloadInfo.toString());
         
        }

        @Override
        public void onError(DownloadInfo downloadInfo) {
            Lg.e(tag, "onError " + downloadInfo.getFilePath());

        }

        @Override
        public void onPrepare(DownloadInfo downloadInfo) {
            Lg.e(tag, "onPrepare " + downloadInfo.getFilePath() + downloadInfo.getFileName());

        }
    }
}
