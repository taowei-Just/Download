package com.gin.xjh.download;

import android.Manifest;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.gin.xjh.download.entities.FileInfo;
import com.gin.xjh.download.services.DownloadService;
import com.tao.mydownloadlibrary.callback.DownloadCall;
import com.tao.mydownloadlibrary.helper.DownloadHelper;
import com.tao.mydownloadlibrary.info.DownloadInfo;
import com.tao.mydownloadlibrary.info.DownloadRecode;
import com.tao.mydownloadlibrary.info.TaskInfo;
import com.tao.mydownloadlibrary.utils.Lg;

import junit.framework.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ListView mLvFile;
    private List<FileInfo> mFileList;
    private FileListAdapter mAdapter;
    private Messenger mServiceMessenger = null;
    private DownloadCall downloadCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0001);
    }
    private void initView() {
        mLvFile = findViewById(R.id.lvFile);
    }

    private void initEvent() {
        mFileList = new ArrayList<>();
        FileInfo fileInfo0 = new FileInfo(0, "http://music.163.com/" +
                "song/media/outer/url?id=557581647.mp3", "一眼一生"+".mp3", 4806156, 0);//l
        FileInfo fileInfo1 = new FileInfo(1, "http://music.163.com/" +
                "song/media/outer/url?id=1313897867.mp3", "不负时代"+".mp3", 2975495, 0);
        FileInfo fileInfo2 = new FileInfo(2, "http://music.163.com/" +
                "song/media/outer/url?id=1306386464.mp3", "万王归来"+".mp3", 5232475, 0);
        FileInfo fileInfo3 = new FileInfo(3, "http://music.163.com/" +
                "song/media/outer/url?id=531295350.mp3", "越清醒越孤独"+".mp3", 5004687, 0);
  FileInfo fileInfo4 = new FileInfo(4, "http://wap.apk.anzhi.com/data5/apk/201905/13/com.dkj.show.muse_50919000.apk", "", 5004687, 0);
        mFileList.add(fileInfo4);

        fileInfo4 = new FileInfo(5, "http://yapkwww.cdn.anzhi.com/data3/apk/201703/22/com.sec.pcw_03075100.apk", "三星分享.apk", 5004687, 0);
        mFileList.add(fileInfo4);

        fileInfo4 = new FileInfo(6, "http://wap.apk.anzhi.com/data5/apk/202003/09/84975c45d1ddcb9a9d6de9699c643cb5_68778700.apk", "天翼账号中心.apk", 5004687, 0);
        mFileList.add(fileInfo4);

        fileInfo4 = new FileInfo(7, "http://wap.apk.anzhi.com/data5/apk/202004/10/0cb1a83a628ee0df19172f542c9d2fee_42653900.apk", "360清理大师.apk", 5004687, 0);
        mFileList.add(fileInfo4);

        fileInfo4 = new FileInfo(8, "http://yapkwww.cdn.anzhi.com/data1/apk/201804/24/com.google.android.inputmethod.pinyin_22939500.apk", "谷歌拼音.apk", 5004687, 0);
        mFileList.add(fileInfo4);
        fileInfo4 = new FileInfo(9, "http://yapkwww.cdn.anzhi.com/data3/apk/201710/23/com.huawei.hidisk_70602700.apk", "华为文件管理.apk", 5004687, 0);
        mFileList.add(fileInfo4);
  fileInfo4 = new FileInfo(9, "http://wap.apk.anzhi.com/data5/apk/202006/11/94385bee070f4aed0405cefabdc33ecb_97451200.apk", "吃鸡战场.apk", 5004687, 0);
        mFileList.add(fileInfo4);
  fileInfo4 = new FileInfo(9, "http://wap.apk.anzhi.com/data5/apk/202006/08/032eaece8750c4e297bcb6c047ec1324_83453500.apk", "优酷视频.apk", 5004687, 0);
        mFileList.add(fileInfo4);
  fileInfo4 = new FileInfo(9, "http://wap.apk.anzhi.com/data5/apk/202005/14/5bc8969e2fdecf06e98960d33da58a29_87828300.apk", "足球.apk", 5004687, 0);
        mFileList.add(fileInfo4);
  fileInfo4 = new FileInfo(9, "http://wap.apk.anzhi.com/data5/apk/202006/01/692acdc9a9779f872b98d6eb148f59e2_92027500.apk", "搜狐视频.apk", 5004687, 0);
        mFileList.add(fileInfo4);

        mFileList.add(fileInfo0);
        mFileList.add(fileInfo1);
        mFileList.add(fileInfo2);
        mFileList.add(fileInfo3);

        for (int i = 0; i < 1000; i++) {
            fileInfo4 = new FileInfo(9, "http://wap.apk.anzhi.com/data5/apk/202006/01/692acdc9a9779f872b98d6eb148f59e2_92027500.apk"+i, "搜狐视频.apk", 5004687, 0);
            mFileList.add(fileInfo4);   
        }
        
        for (int i = 0; i < mFileList.size(); i++) {
            mFileList.get(i).setId(i);
        }

        mAdapter = new FileListAdapter(this, mFileList);
        mLvFile.setAdapter(mAdapter);

        //绑定Service
        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, mConnection, Service.BIND_AUTO_CREATE);
        
    }

    private void test() {

        downloadCall = new DownloadCall() {
            @Override
            public void onProgress(DownloadInfo downloadInfo) {

            }

            @Override
            public void onProgress(DownloadInfo downloadInfo, TaskInfo info) {

            }

            @Override
            public void onStart(DownloadInfo downloadInfo) {

            }

            @Override
            public void onCompleted(DownloadInfo downloadInfo) {

            }

            @Override
            public void onError(DownloadInfo downloadInfo) {

            }

            @Override
            public void onPrepare(DownloadInfo downloadInfo) {

            }
        };
        
        for (FileInfo fileInfo : mFileList) {
            DownloadHelper.getInstance(this).addDownload(fileInfo.getUrl(), downloadCall);
        }

    }

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //获得Service中的Messenger
            mServiceMessenger = new Messenger(iBinder);
            //设置适配器中的Messenger
            mAdapter.setmMessenger(mServiceMessenger);
            //创建Activity中的Messenger
            Messenger messenger = new Messenger(mHandler);
            //创建消息
            Message msg = new Message();
            msg.what = DownloadService.MSG_BIND;
            msg.replyTo = messenger;
            //使用Service的Messenger发送Activity中的Messenger
            try {
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            
//            Executors.newSingleThreadExecutor().execute(new Runnable() {
//                @Override
//                public void run() {
//                    test();
//                }
//            }); 
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DownloadService.MSG_UPDATE:
                    int finished = msg.arg1;
                    int id = msg.arg2;
                    mAdapter.updateProgress(mLvFile, id, finished);
                    break;
                case DownloadService.MSG_FINISH:
                    FileInfo fileInfo = (FileInfo) msg.obj;
                    //更新进度为0
                    mAdapter.updateProgress(mLvFile,fileInfo.getId(), fileInfo.getLength());
                    Toast.makeText(MainActivity.this, mFileList.get(fileInfo.getId()).getFileName() + "下载完毕", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void recodes(View view) {
        long l = System.currentTimeMillis();
        DownloadRecode downloadRecode = DownloadHelper.getInstance(this).listDownloadRecode();
        Lg.e(downloadRecode);
        Lg.e("耗时："+(System.currentTimeMillis()-l));
    }
}
