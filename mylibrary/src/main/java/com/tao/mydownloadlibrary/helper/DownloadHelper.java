package com.tao.mydownloadlibrary.helper;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.tao.mydownloadlibrary.DownloadStatue;
import com.tao.mydownloadlibrary.callback.DownloadCall;
import com.tao.mydownloadlibrary.callback.DownloadTaskCAll;
import com.tao.mydownloadlibrary.callback.PrepareTaskCall;
import com.tao.mydownloadlibrary.info.DownloadInfo;
import com.tao.mydownloadlibrary.info.DownloadRecode;
import com.tao.mydownloadlibrary.info.TaskInfo;
import com.tao.mydownloadlibrary.task.DownloadTask;
import com.tao.mydownloadlibrary.task.PrepareTask;
import com.tao.mydownloadlibrary.utils.Lg;
import com.tao.mydownloadlibrary.utils.MD5Util;
import com.tao.mydownloadlibrary.utils.MyGosn;
import com.tao.mydownloadlibrary.utils.SharedUtlis;
import com.tao.mydownloadlibrary.utils.WriteStreamAppend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DownloadHelper implements IDownloader {
    private Map<String, DownloadInfo> downloadInfoMap = new HashMap<>();
    private Map<String, List<TaskInfo>> taskInfoMap = new HashMap<>();
    private Map<String, List<Future>> futureMap = new HashMap<>();
    private static DownloadHelper downloadHelper;
    private final ExecutorService loadInfoPool;
    private ExecutorService downloadPool;
    private ExecutorService perparePool;
    private int defaultCount = 2048;
    private long lastProgressTime;
    private Build build;
    private String tag = getClass().getSimpleName();

    private DownloadHelper() {
        loadInfoPool = Executors.newFixedThreadPool(10);
    }


    public void init(Build build) {
        if (null == build)
            return;
        this.build = build;
        initPool(build);
    }

    private void initPool(Build build) {

        for (String s : downloadInfoMap.keySet()) {
            stopDownload(downloadInfoMap.get(s).getUrl());
        }

        if (perparePool != null)
            perparePool.shutdownNow();
        perparePool = Executors.newFixedThreadPool(build.missionCount);

        if (downloadPool != null)
            downloadPool.shutdownNow();

        int nThreads = build.missionCount * build.taskCount;

        downloadPool = Executors.newFixedThreadPool(nThreads);

        File file = new File(build.configPath);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        file = new File(build.rootPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        file = new File(build.cachePath);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    public void init(Context context) {
        if (null == context)
            return;
        this.build = this.build == null ? createDefaultBuild(context) : this.build;
        initPool(build);
    }

    public Build createDefaultBuild(Context context) {
        return new Build(context);
    }


    public static synchronized DownloadHelper getInstance() {
        if (downloadHelper == null) {
            synchronized (DownloadHelper.class) {
                if (downloadHelper == null) {
                    downloadHelper = new DownloadHelper();
                }
            }
        }
        return downloadHelper;
    }

//    public void deleteDownloadInfoByUrl(String url) {
//        deleteDownloadLine(url);
//    }


    public void deleteDownloadLine(final String url) {
        String tag = MD5Util.md5(url);
        deleteDownloadLineByTag(tag);
    }

    private void deleteDownloadLineByTag(String tag) {
        BufferedReader bufferedReader = null;

        File file1 = new File(build.configPath);
        File file2 = new File(build.configPath + ".cache");

        if (!file2.exists()) {
            file2.getParentFile().mkdirs();
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1)));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
//                Lg.e(line);
                if (line.contains(tag)) {
                    String[] split = line.split(":");
                    if (split.length < 2) {
                        continue;
                    }
                    File file = new File(split[1]);
                    if (!file.exists()) {
                        continue;
                    }
                    file.delete();
                    continue;
                }
                WriteStreamAppend.method1(file2.getAbsolutePath(), line + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            file1.delete();
            file2.renameTo(file1);
        }
    }

    public void reDownload(final String url, final DownloadCall downloadCall) {
        loadInfoPool.execute(new Runnable() {
            @Override
            public void run() {
                DownloadInfo downloadRecode = deleteDownloadInfo(url, false);

                if (recodeVaild(downloadRecode)) {
                    addDownload(url, downloadRecode.getPath(), downloadRecode.getFileName(), downloadCall);
                } else {
                    deleteDownloadLine(url);
                    addDownload(url, downloadCall);
                }
            }
        });

    }

    private DownloadInfo deleteDownloadInfo(String url, boolean retain) {
        stopDownload(url);
        // 删除配置文件重新下载
        DownloadInfo downloadRecode = checkDownloadRecode(url);
        deleteDownloadInfo(downloadRecode, retain);
        return downloadRecode;
    }

    private void deleteDownloadInfo(DownloadInfo downloadRecode, boolean retain) {
        if (downloadRecode == null)
            return;
        File file2 = new File(downloadRecode.getPath() + File.separator + "downloadInfos" + File.separator + downloadRecode.getDownloadTag() + ".info");
        if (file2.exists()) {
            file2.delete();
        }

        List<TaskInfo> taskInfos = downloadRecode.getTaskInfos();
        if (taskInfos == null)
            return;
        for (TaskInfo taskInfo : taskInfos) {
            if (taskInfo == null)
                continue;
            File file1 = new File(taskInfo.getCacheFile());
            if (file1.exists())
                file1.delete();
        }

        if (retain)
            return;

        File file = new File(downloadRecode.getPath(), downloadRecode.getFileName());
        if (file.exists())
            file.delete();
    }

    public void addDownload(String url, DownloadCall downloadCall) {
        try {
            addDownload(url, build.rootPath, downloadCall);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDownload(String url, String path, DownloadCall downloadCall) {
        addDownload(url, path, "", downloadCall);
    }

    public void addDownload(final String url, final String path, final String fileName, final DownloadCall downloadCall) {

        loadInfoPool.execute(new Runnable() {
            @Override
            public void run() {
                DownloadInfo downloadInfo = downloadInfoMap.get(MD5Util.md5(url));
                if (downloadInfo != null && downloadInfo.getStatue() != DownloadStatue.error && downloadInfo.getStatue() != DownloadStatue.complete) {
//            Lg.e("addDownload", downloadInfo);
                    return;
                }
                long l = System.currentTimeMillis();
                DownloadInfo downloadRecode = checkDownloadRecode(url);
//                Lg.e("耗时：" + (System.currentTimeMillis() - l));
                if (null == downloadRecode) {
                    try {
                        prepareDownload(prepareDownloadInfo(url, path, fileName, downloadCall));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    downloadRecode.setDownloadCall(downloadCall);
                    // 有下载记录
                    //2.查找之前下载的文件还在不在
                    //3.尝试恢复下载
                    if (recodeVaild(downloadRecode) && build.reuseFile) {
                        recoverRecode(downloadRecode, path, fileName);
                    } else {
                        try {
                            prepareDownload(prepareDownloadInfo(url, path, fileName, downloadCall));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    private void recoverRecode(DownloadInfo downloadRecode, String path, String fileName) {


        try {
            if (null != downloadRecode.getDownloadCall()) {
                downloadRecode.getDownloadCall().onPrepare(downloadRecode);
            }

            File inputFile = new File(downloadRecode.getPath(), downloadRecode.getFileName());
            if (inputFile.exists()) {
                if (!path.endsWith(File.separator)) {
                    path = path + File.separator;
                }

                String md5fromBigFile = MD5Util.getMD5fromBigFile(inputFile);
                if (md5fromBigFile.equals(downloadRecode.getMd5())) {
                    if (TextUtils.isEmpty(path)) {
                        path = downloadRecode.getPath();
                    }
                    if (TextUtils.isEmpty(fileName)) {
                        fileName = downloadRecode.getFileName();
                    }

                    if (!downloadRecode.getFilePath().equals(path + fileName)) {
                        // 复制文件到指定下载路径
                        if (copyFile(downloadRecode.getFilePath(), path + fileName)) {
                            downloadRecode.setPath(path);
                            downloadRecode.setFileName(fileName);
                        }
                    }
                    if (downloadRecode.getDownloadCall() != null)
                        downloadRecode.getDownloadCall().onCompleted(downloadRecode);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (downloadRecode.getDownloadCall() != null)
                downloadRecode.getDownloadCall().onError(downloadRecode);
        } finally {
            saveDwnloadInfo2File(downloadRecode);
        }

//        Lg.e(" recoverRecode", downloadRecode);
        excuteDownloadTask(downloadRecode);
    }

    private boolean copyFile(String filePath, String newFile) {
        File file = new File(filePath);
        File newF = new File(newFile);
        if (!file.exists())
            return false;
        if (!newF.exists()) {
            newF.getParentFile().mkdirs();
            try {
                newF.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        FileInputStream fileInputStream = null;
        FileOutputStream newFileOutput = null;
        try {
            fileInputStream = new FileInputStream(file);
            newFileOutput = new FileOutputStream(newFile);

            // 缓存2M
            byte[] buff = new byte[2 * 1024 * 1024];
            int len = 0;
            while ((len = fileInputStream.read(buff)) > -1) {
                newFileOutput.write(buff, 0, len);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            if (newFileOutput != null) {
                try {
                    newFileOutput.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }


    }

    private boolean recodeVaild(DownloadInfo downloadRecode) {
//        Lg.e("recodeVaild", downloadRecode);
        if (downloadRecode == null
                || downloadRecode.getStatue() == DownloadStatue.error
                || downloadRecode.getTaskInfos() == null
                || downloadRecode.getTotalLenth() <= 0
                || downloadRecode.getTaskInfos().size() == 0
                || TextUtils.isEmpty(downloadRecode.getUrl())
        )
            return false;

        File file1 = new File(downloadRecode.getPath(), downloadRecode.getFileName());
        if (file1.exists()) {
            try {
                String md5fromBigFile = MD5Util.getMD5fromBigFile(file1);
                if (md5fromBigFile.equals(downloadRecode.getMd5())) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<TaskInfo> taskInfos = downloadRecode.getTaskInfos();
        if (taskInfos == null)
            return false;
        for (TaskInfo taskInfo : taskInfos) {
            if (taskInfo == null)
                return false;
            File file = new File(taskInfo.getCacheFile());
            if (!file.exists())
                return false;
            long length = file.length();
            if (length != taskInfo.getThreadLen())
                return false;
        }
        return true;
    }

    private DownloadInfo checkDownloadRecode(String url) {
        String tag = MD5Util.md5(url);
        BufferedReader bufferedReader = null;
        DownloadInfo downloadInfo = null;
        File file1 = new File(build.configPath);
//        File file2 = new File(build.configPath + ".cache");
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1)));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
//                Lg.e(line);
                if (line.contains(tag)) {
                    String[] split = line.split(":");
                    if (split.length < 2) {
                        continue;
                    }
                    File file = new File(split[1]);
                    if (!file.exists()) {
                        continue;
                    }
                    if (downloadInfo == null || downloadInfo.getStatue() == DownloadStatue.error) {
                        String string = WriteStreamAppend.readFileString(file);
                        try {
                            DownloadInfo downloadInfo1 = MyGosn.fromJson(string, DownloadInfo.class);
                            if (downloadInfo1 == null)
                                continue;
                            if (tag.equals(downloadInfo1.getDownloadTag())) {
                                if (!recodeVaild(downloadInfo1)) {
                                    continue;
                                }
                                downloadInfo = downloadInfo1;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            continue;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }

        }
        return downloadInfo;
    }


    /**
     * 指定下载文件夹，以及指定文件名称
     *
     * @param url
     * @param path
     * @param fileName
     * @return
     * @throws
     */
    private DownloadInfo prepareDownloadInfo(String url, String path, String fileName, DownloadCall downloadCall) throws Exception {
        DownloadInfo downloadInfo = new DownloadInfo(url);
        downloadInfo.setStatue(DownloadStatue.prepare);
        downloadInfo.setThreadCount(build.taskCount);
        downloadInfo.setPath(path);
        downloadInfo.setCachePath(build.cachePath);
        downloadInfo.setFileName(fileName);
        downloadInfo.setDownloadCall(downloadCall);
        return downloadInfo;
    }

    /**
     * 使用指定路径 自动命名文件
     *
     * @param url
     * @param path
     * @return
     * @throws
     */
    private DownloadInfo prepareDownloadInfo(String url, String path, DownloadCall downloadCall) throws Exception {
        return prepareDownloadInfo(url, path, "", downloadCall);
    }

    /**
     * 使用默认下载路径 以及自动命名文件
     *
     * @param url
     * @return
     * @throws
     */
    private DownloadInfo prepareDownloadInfo(String url, DownloadCall downloadCall) throws Exception {
        return prepareDownloadInfo(url, build.rootPath, downloadCall);
    }

    private void prepareDownload(DownloadInfo downloadInfo) {
//        Lg.e("prepareDownload", downloadInfo);
        addDownloadInfoRecode(downloadInfo);
        saveDwnloadInfo2File(downloadInfo);
        perparePool.submit(new PrepareTask(downloadInfo, new MyPerpare()));
        downloadInfoMap.put(downloadInfo.getDownloadTag(), downloadInfo);
        if (null != downloadInfo.getDownloadCall()) {
            downloadInfo.getDownloadCall().onPrepare(downloadInfo);
        }
    }


    private void excuteDownloadTask(DownloadInfo downloadInfo) {
        List<Future> futureList = new ArrayList<>();
        List<TaskInfo> taskInfoS = downloadInfo.getTaskInfos();
        for (int i = 0; i < taskInfoS.size(); i++) {
            Future<?> future = downloadPool.submit(new DownloadTask(taskInfoS.get(i), new MyDownloadCall()));
            futureList.add(future);
//            Log.e(tag, "submit DownloadTask "  + taskInfoS.get(i).getCacheFile());
        }
        downloadInfo.setStatue(DownloadStatue.downloading);
        saveDwnloadInfo(downloadInfo);
        downloadInfoMap.put(downloadInfo.getDownloadTag(), downloadInfo);
        taskInfoMap.put(downloadInfo.getDownloadTag(), taskInfoS);
        futureMap.put(downloadInfo.getDownloadTag(), futureList);
    }

    public String getCachePath() {
        return build.rootPath;
    }

    public void stopDownload(String url) {
        if (url == null)
            return;
        String s = MD5Util.md5(url);
        if (futureMap.containsKey(s)) {
            cancelTask(s);
            futureMap.remove(s);
            downloadInfoMap.remove(s);
            taskInfoMap.remove(s);
        }
    }

    public void deleteDownload(final String url) {
        loadInfoPool.execute(new Runnable() {
            @Override
            public void run() {
                stopDownload(url);
                deleteDownloadInfo(url, false);
                deleteDownloadLine(url);

            }
        });

    }

    public void deleteDownloadOnlyInfo(final String url) {
        loadInfoPool.execute(new Runnable() {
            @Override
            public void run() {
                stopDownload(url);
//                DownloadInfo downloadInfo = checkDownloadRecode(url);
//                deleteDownloadInfo(downloadInfo);
                deleteDownloadInfo(url, true);
                deleteDownloadLine(url);

            }
        });

    }

    @Override
    public synchronized DownloadRecode listDownloadRecode(int index, int maxCount) {
        int count = 0;
        int index01 = 0;
        BufferedReader bufferedReader = null;
        File file1 = new File(build.configPath);
        DownloadRecode downloadRecode = new DownloadRecode();
        List<DownloadInfo> downloadInfos = new ArrayList<>();
        downloadRecode.setIndex(index);
        downloadRecode.setInfoList(downloadInfos);
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file1)));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
//                Lg.e(line);
                String[] split = line.split(":");
                if (split.length < 2) {
                    continue;
                }
                File file = new File(split[1]);
                if (!file.exists()) {
                    continue;
                }
                String string = WriteStreamAppend.readFileString(file);
                try {
                    DownloadInfo downloadInfo1 = MyGosn.fromJson(string, DownloadInfo.class);
                    if (downloadInfo1 == null)
                        continue;

                    if (index01 < index) {
                        continue;
                    }
                    index01++;
                    count++;
                    if (count < maxCount) {
                        downloadInfos.add(downloadInfo1);
                        downloadRecode.setCount(count);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return downloadRecode;
    }

    @Override
    public DownloadRecode listDownloadRecode(int index) {
        return listDownloadRecode(index, defaultCount);
    }

    @Override
    public DownloadRecode listDownloadRecode() {
        return listDownloadRecode(0, Integer.MAX_VALUE);
    }

    class MyDownloadCall implements DownloadTaskCAll {
        @Override
        public void onProgress(TaskInfo info) {
            callProgress(info);
        }

        @Override
        public void onStart(TaskInfo info) {
//            Lg.e("MyDownloadCall onStart ", info.getUrl());
            saveDwnloadInfo2File(downloadInfoMap.get(info.getDownloadTag()));
        }

        @Override
        public void onCompleted(TaskInfo info) {
//            Lg.ii("MyDownloadCall onCompleted ", info.getUrl());

            callCompleted(info);
        }

        @Override
        public void onError(TaskInfo info) {
//            Lg.d("MyDownloadCall onError ", info.getUrl());
            callError(info);
        }
    }

    private void callError(TaskInfo info) {
        DownloadInfo downloadInfo = downloadInfoMap.get(info.getDownloadTag());
        saveDwnloadInfo2File(downloadInfo);

        cancelTask(info.getDownloadTag());
        downloadInfoMap.remove(info.getDownloadTag());
        taskInfoMap.remove(info.getDownloadTag());
        futureMap.remove(info.getDownloadTag());

        if (downloadInfo == null || null == downloadInfo.getDownloadCall())
            return;
        downloadInfo.getDownloadCall().onError(downloadInfo);
    }

    private void cancelTask(String downloadTag) {
        List<Future> futureList = futureMap.get(downloadTag);
        if (futureList == null)
            return;
        for (Future future : futureList) {
            if (future == null || future.isCancelled() || future.isDone())
                return;
            future.cancel(true);
        }
    }

    private synchronized void callCompleted(TaskInfo info) {
        DownloadInfo downloadInfo = downloadInfoMap.get(info.getDownloadTag());
        List<TaskInfo> taskInfos = taskInfoMap.get(info.getDownloadTag());
        if (downloadInfo == null || taskInfos == null) {
            return;
        }
        downloadInfo.setStatue(DownloadStatue.complete);
        downloadInfo.setProgress(loadTaskProgress(taskInfos));
        saveDwnloadInfo2File(downloadInfo);

        downloadInfoMap.remove(info.getDownloadTag());
        taskInfoMap.remove(info.getDownloadTag());
        futureMap.remove(info.getDownloadTag());

        if (downloadInfo.getProgress() >= downloadInfo.getTotalLenth() || (downloadInfo.getTotalLenth() == -1 && completeTask(downloadInfo))) {

            try {
                if (downloadInfo.getTotalLenth() == -1) {
                    for (TaskInfo taskInfo : downloadInfo.getTaskInfos()) {
                        if (taskInfo == null)
                            continue;
                        downloadInfo.setTotalLenth(downloadInfo.getTotalLenth()+taskInfo.getProgressLen());

                    }
                    downloadInfo.setTotalLenth(downloadInfo.getTotalLenth()+1);
                }
                mergeFiles(downloadInfo, taskInfos);
                saveDwnloadInfo2File(downloadInfo);
            } catch (Exception e) {
                e.printStackTrace();
                if (null != downloadInfo.getDownloadCall())
                    downloadInfo.getDownloadCall().onError(downloadInfo);
                return;
            }

            if (null != downloadInfo.getDownloadCall())
                downloadInfo.getDownloadCall().onCompleted(downloadInfo);
        } else if (downloadInfo.getTotalLenth() == Integer.MAX_VALUE && downloadInfo.getProgress() > 0) {


            if (null != downloadInfo.getDownloadCall())
                downloadInfo.getDownloadCall().onCompleted(downloadInfo);

        } else {

            if (null != downloadInfo.getDownloadCall())
                downloadInfo.getDownloadCall().onError(downloadInfo);
        }
    }

    private boolean completeTask(DownloadInfo downloadInfo) {
        for (TaskInfo taskInfo : downloadInfo.getTaskInfos()) {
            if (taskInfo != null && taskInfo.getStatue() != DownloadStatue.complete) {
                return false;
            }
        }
        return true;
    }

    private void mergeFiles(DownloadInfo downloadInfo, List<TaskInfo> taskInfos) throws Exception {
        File file = new File(downloadInfo.getPath() + File.separator + downloadInfo.getFileName());
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            if (file.isDirectory())
                file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception("file create error");
            }
        } else {
            file.delete();
        }

        for (TaskInfo taskInfo : taskInfos) {
            RandomAccessFile accessFile = new RandomAccessFile(file, "rwd");
            accessFile.setLength(downloadInfo.getTotalLenth());
//            Lg.e(taskInfo);
            long offeset = taskInfo.getOffeset();
            long threadLen = taskInfo.getThreadLen();
            accessFile.seek(offeset);
            File cacheFile = new File(taskInfo.getCacheFile());
            FileInputStream inputStream = new FileInputStream(cacheFile);

            byte[] buff = new byte[1024 * 1024 * 3];

            int len = 0;
            while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
                accessFile.write(buff, 0, len);
            }
            cacheFile.delete();
            try {
                inputStream.close();
                accessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (taskInfos.size() > 0) {
            try {
                new File(taskInfos.get(0).getCacheFile()).getParentFile().delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        downloadInfo.setMd5(MD5Util.getMD5fromBigFile(file));

    }

    private void callProgress(TaskInfo info) {
        DownloadInfo downloadInfo = downloadInfoMap.get(info.getDownloadTag());
        saveDwnloadInfo2File(downloadInfo);

        if (downloadInfo == null)
            return;
        downloadInfo.setProgress(loadTaskProgress(downloadInfo.getTaskInfos()));


//        Lg.e("MyDownloadCall onProgress ", downloadInfo);

        if (null == downloadInfo.getDownloadCall())
            return;
        if (lastProgressTime == 0 || (System.currentTimeMillis() - lastProgressTime >= 1 * 1000)) {
            downloadInfo.getDownloadCall().onProgress(downloadInfo);
            downloadInfo.getDownloadCall().onProgress(downloadInfo, info);
            lastProgressTime = System.currentTimeMillis();
        }

    }

    private synchronized long loadTaskProgress(List<TaskInfo> taskInfos) {
        long progress = 0;
        for (TaskInfo taskInfo : taskInfos) {
            progress += taskInfo.getProgressLen();
        }
        return progress;
    }

    class MyPerpare implements PrepareTaskCall {
        @Override
        public void onComplete(DownloadInfo downloadInfo, List<TaskInfo> taskInfoS) {
            if (downloadInfo.getTotalLenth() <= 0) {
//                onError(downloadInfo);
                downloadInfo.setThreadCount(1);
//                downloadInfo.setTotalLenth(Integer.MAX_VALUE);

                List<TaskInfo> taskInfos = downloadInfo.getTaskInfos();
                TaskInfo taskInfo = taskInfos.get(0);
                taskInfo.setThreadCount(1);
                taskInfo.setFileLen(-2);
                taskInfo.setThreadLen(-2);
                taskInfo.setOffeset(0);

                taskInfoS.clear();
                taskInfoS.add(taskInfo);
            }
            saveDwnloadInfo2File(downloadInfo);
            excuteDownloadTask(downloadInfo);
        }

        @Override
        public void onError(DownloadInfo downloadInfo) {
            downloadInfo.setStatue(DownloadStatue.error);
            saveDwnloadInfo2File(downloadInfo);

            if (null != downloadInfo.getDownloadCall()) {
                downloadInfo.getDownloadCall().onError(downloadInfo);
            }
        }
    }

    public static void saveDwnloadInfo(DownloadInfo downloadInfo) {
        downloadHelper.saveDwnloadInfo2File(downloadInfo);
    }

    private synchronized void addDownloadInfoRecode(DownloadInfo downloadInfo) {
        if (downloadInfo == null)
            return;
        BufferedWriter writer = null;
        try {
            File file = new File(build.configPath);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            String str = downloadInfo.getDownloadTag() + ":" + new File(build.context.getExternalCacheDir() + File.separator + "download" + File.separator + "downloadInfos" + File.separator + downloadInfo.getDownloadTag() + ".info").getAbsolutePath();
//            Lg.e(str);
            WriteStreamAppend.method1(file.getAbsolutePath(), str + "\n");
//            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file ,true)));
//           
//            writer.write(str);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDwnloadInfo2File(DownloadInfo downloadInfo) {
        if (downloadInfo == null)
            return;
        String s = MyGosn.toJson(downloadInfo, "DownloadCall");
//        Lg.e(s);
        FileOutputStream outputStream = null;
        try {
            File file = new File(build.context.getExternalCacheDir() + File.separator + "download" + File.separator + "downloadInfos" + File.separator + downloadInfo.getDownloadTag() + ".info");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class Build {
        public String cachePath;
        Context context;
        // 文件默认下载路径
        String rootPath;
        // 同时最多下载任务数s
        int missionCount = 3;
        // 默认每个下载线程数
        int taskCount = 1;
        String configPath;
        // 当发现连接对应的文件已存在的时候是否复用原先已经下载的文件
        boolean reuseFile = true;

        public Build(Context context) {
            this.context = context.getApplicationContext();
            cachePath = context.getExternalCacheDir() + File.separator + "download" + File.separator + "caches" + File.separator;
            rootPath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + "download" + File.separator;
            configPath = context.getFilesDir() + File.separator + "download" + File.separator + "download.config";
        }

        public Build setContext(Context context) {
            this.context = context;
            return this;
        }

        public Build setRootPath(String rootPath) {
            this.rootPath = rootPath;
            return this;
        }

        public Build setMissionCount(int missionCount) {
            this.missionCount = missionCount;
            return this;
        }

        public Build setTaskCount(int taskCount) {
            this.taskCount = taskCount;
            return this;
        }
    }


}
