package com.tao.mydownloadlibrary.task;

import android.text.TextUtils;
import com.tao.mydownloadlibrary.callback.PrepareTaskCall;
import com.tao.mydownloadlibrary.info.DownloadInfo;
import com.tao.mydownloadlibrary.info.TaskInfo;
import com.tao.mydownloadlibrary.utils.HttpUtil;
import com.tao.mydownloadlibrary.utils.Lg;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Headers;
import okhttp3.Response;

public class PrepareTask implements Runnable {
    private String tag = getClass().getSimpleName();
    DownloadInfo downloadInfo;
    PrepareTaskCall prepareTaskCall;

    public PrepareTask(DownloadInfo downloadInfo, PrepareTaskCall prepareTaskCall) {
        this.downloadInfo = downloadInfo;
        this.prepareTaskCall = prepareTaskCall;
    }
    @Override
    public void run() {
        try {
            Response response = HttpUtil.callGet(downloadInfo.getUrl());
            if (response.code() != 200) {
                prepareTaskCall.onError(downloadInfo);
            } else {
                long length = response.body().contentLength();
                downloadInfo.setTotalLenth(length);
                Headers headers = response.headers();
//                Lg.e(tag, " headers " + headers);
                readFileName(headers);
                usedRanges(headers);
                List<TaskInfo> taskInfoS = new ArrayList<>();
                for (int j = 0; j < downloadInfo.getThreadCount(); j++) {
                    TaskInfo info = new TaskInfo(downloadInfo.getDownloadTag(), j, downloadInfo.getUrl(), downloadInfo.getFileName());
                    info.setTaskId(j);
                    info.setThreadCount(downloadInfo.getThreadCount());
                    info.setFileLen(length);
                    info.setProgressLen(0);
                    info.setThreadLen(j == downloadInfo.getThreadCount() - 1 ? length / downloadInfo.getThreadCount() + length % downloadInfo.getThreadCount() : length / downloadInfo.getThreadCount());
                    info.setCacheFile(downloadInfo.getCachePath() + File.separator + downloadInfo.getDownloadTag() + File.separator + downloadInfo.getFileName() + "(" + j + ").cache");
                    info.setOffeset(j * (length / downloadInfo.getThreadCount()));
                    taskInfoS.add(info);
//                    Lg.e(tag, info.toString());
                }
                downloadInfo.setTaskInfos(taskInfoS);
                Thread.sleep(1);
                prepareTaskCall.onComplete(downloadInfo, taskInfoS);
            }
        } catch (Exception e) {
            e.printStackTrace();
            prepareTaskCall.onError(downloadInfo);
        }
    }

    private void usedRanges(Headers headers) {
        boolean ranges = !rangS(headers, "Ranges");
        boolean bytes = headers.values("Accept-Ranges").contains("bytes");
        boolean bytes1 = headers.values("Content-Ranges").contains("bytes");
        if (ranges || !(bytes || bytes1)) {
            downloadInfo.setThreadCount(1);
        }
    }

    private void readFileName(Headers headers) {
        if (rangS(headers, "content-disposition")) {
            List<String> values = headers.values("content-disposition");
            for (String s : values) {
                if (s.contains("filename=")) {
                    String[] split = s.replace("\"", "").split("=");
                    if (TextUtils.isEmpty(downloadInfo.getFileName()))
                        downloadInfo.setFileName(split[split.length - 1]);
                    break;
                }
            }
        } else {
            if (TextUtils.isEmpty(downloadInfo.getFileName()))
                downloadInfo.setFileName(getfileName(downloadInfo.getUrl()));
        }
    }

    public boolean rangS(Headers headers, String head) {
        Set<String> names = headers.names();
        Iterator<String> iterator = names.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().toLowerCase().contains(head.toLowerCase()))
                return true;
        }
        return false;
    }

    public String getfileName(String url) {
        String substring = url.substring(url.lastIndexOf("/") + 1);
        // 清除掉所有特殊字符 
//        String regEx="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile("[`~☆★!@#$%^&*()+=|{}':;,\\[\\]》·<>/?~！@#￥%……（）——+|{}【】‘；：”“’。，、？]");//去除特殊字符
        Matcher m = p.matcher(substring);

        substring = m.replaceAll("").trim();
        if (substring.toLowerCase().contains("name=")) {
            String[] split = substring.split("name=");
            substring = split[split.length - 1];
        }
        return substring;
    }

}
