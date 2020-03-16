package com.nordnetab.chcp.main.network;

/**
 * 下载进度
 * Created by zliu on 2017/12/28.
 */

public interface DownloadProgressListener {

    /**
     * 下载回调
     * @param progress   进度 0.0
     * @param currentNum 正在下载第几个
     * @param totalNum   总共需要下载文件数量
     */
    void onDownloadProgress(float progress, int currentNum, int totalNum);

}
