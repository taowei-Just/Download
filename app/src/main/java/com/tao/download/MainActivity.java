package com.tao.download;

import android.Manifest;
import android.os.Bundle;
import android.os.Messenger;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.gin.xjh.download.R;
import com.tao.mydownloadlibrary.callback.DownloadCall;
import com.tao.mydownloadlibrary.helper.DownloadHelper;
import com.tao.mydownloadlibrary.info.DownloadInfo;
import com.tao.mydownloadlibrary.info.DownloadRecode;
import com.tao.mydownloadlibrary.info.TaskInfo;
import com.tao.mydownloadlibrary.utils.Lg;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private Messenger mServiceMessenger = null;
    private DownloadCall downloadCall;
    private List<String> mFileList;
    private String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initEvent();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0001);
    }

    private void initEvent() {
        mFileList = new ArrayList<>();
        String  apkurl ="https://eb28948de3ce12e2d6f5dcbfba826b2f.dlied1.cdntips.net/imtt.dd.qq.com/16891/apk/5B69939E818E263742A1EE2E3C93E64C.apk?mkey=5f879ac5b7dac4cd&f=0ef9&fsname=com.tencent.mtt_10.8.5.8430_10858430.apk&csr=1bbd&cip=183.218.226.56&proto=https";
        mFileList.add(apkurl);
//        String String4 = "http://wap.apk.anzhi.com/data5/apk/201905/13/com.dkj.show.muse_50919000.apk";
//        mFileList.add(String4);
//
//        String4 = "http://yapkwww.cdn.anzhi.com/data3/apk/201703/22/com.sec.pcw_03075100.apk";
//        mFileList.add(String4);
//
//        String4 = "http://wap.apk.anzhi.com/data5/apk/202003/09/84975c45d1ddcb9a9d6de9699c643cb5_68778700.apk";
//        mFileList.add(String4);
//
//        String4 = "http://wap.apk.anzhi.com/data5/apk/202004/10/0cb1a83a628ee0df19172f542c9d2fee_42653900.apk";
//        mFileList.add(String4);
//
//        String4 = "http://yapkwww.cdn.anzhi.com/data1/apk/201804/24/com.google.android.inputmethod.pinyin_22939500.apk";
//        mFileList.add(String4);
//        String4 = "http://yapkwww.cdn.anzhi.com/data3/apk/201710/23/com.huawei.hidisk_70602700.apk";
//        mFileList.add(String4);
//        String4 = "http://wap.apk.anzhi.com/data5/apk/202006/11/94385bee070f4aed0405cefabdc33ecb_97451200.apk";
//        mFileList.add(String4);
//        String4 = "http://wap.apk.anzhi.com/data5/apk/202006/08/032eaece8750c4e297bcb6c047ec1324_83453500.apk";
//        mFileList.add(String4);
//        String4 = "http://wap.apk.anzhi.com/data5/apk/202005/14/5bc8969e2fdecf06e98960d33da58a29_87828300.apk";
//        mFileList.add(String4);
//        String4 = "http://wap.apk.anzhi.com/data5/apk/202006/01/692acdc9a9779f872b98d6eb148f59e2_92027500.apk";
//        mFileList.add(String4);

    }

    public void test(View view) {
        downloadCall = new DownloadCall() {
            @Override
            public void onProgress(DownloadInfo downloadInfo) {

                Log.e(tag, "onProgress " + downloadInfo.getFilePath() + "   " + downloadInfo.getProgressPersent() + "%");
            }

            @Override
            public void onProgress(DownloadInfo downloadInfo, TaskInfo info) {

            }

            @Override
            public void onStart(DownloadInfo downloadInfo) {
                Log.e(tag, "onStart " + downloadInfo.getFilePath());

            }

            @Override
            public void onCompleted(DownloadInfo downloadInfo) {
                Log.e(tag, "onCompleted " + downloadInfo.getFilePath());

            }

            @Override
            public void onError(DownloadInfo downloadInfo) {
                Log.e(tag, "onError " + downloadInfo.getFilePath());

            }

            @Override
            public void onPrepare(DownloadInfo downloadInfo) {
                Log.e(tag, "onPrepare " + downloadInfo.getFilePath());

            }
        };
        for (int i = 0; i < mFileList.size(); i++) {

            String String = mFileList.get(i);
            DownloadHelper.getInstance(this).addDownload(String, downloadCall);
//            if (i==3)
//                return;
        }
    }


    public void list(View view) {
        DownloadRecode downloadRecode = DownloadHelper.getInstance(this).listDownloadRecode();

        Lg.e(downloadRecode);
    }
}
