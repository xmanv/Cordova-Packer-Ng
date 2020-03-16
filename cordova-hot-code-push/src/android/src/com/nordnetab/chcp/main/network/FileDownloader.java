package com.nordnetab.chcp.main.network;

import android.util.Log;

import com.nordnetab.chcp.main.model.ManifestFile;
import com.nordnetab.chcp.main.utils.FilesUtility;
import com.nordnetab.chcp.main.utils.MD5;
import com.nordnetab.chcp.main.utils.Paths;
import com.nordnetab.chcp.main.utils.URLConnectionHelper;
import com.nordnetab.chcp.main.utils.URLUtility;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by Nikolay Demyankov on 22.07.15.
 * <p/>
 * Helper class to download files.
 */
public class FileDownloader {

    private DownloadProgressListener downloadProgressListener;

    public void setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
    }

    /**
     * Download list of files.
     * Full url to the file is constructed from the contentFolderUrl and ManifestFile#hash (relative path).
     * For each downloaded file we perform check of his hash. If it is different from the one, that provided
     * if ManifestFile#hash - exception will be thrown.
     * Download stops on any error.
     *
     * @param downloadFolder   absolute path to the folder, where downloaded files should be placed
     * @param contentFolderUrl root url on the server, where all files are located
     * @param files            list of files to download
     * @throws Exception
     * @see ManifestFile
     */
    public void downloadFiles(String downloadFolder,
                              String contentFolderUrl,
                              List<ManifestFile> files,
                              Map<String, String> requestHeaders) throws Exception {
//        for (ManifestFile file : files) {
//            String fileUrl = URLUtility.construct(contentFolderUrl, file.name);
//            String filePath = Paths.get(downloadFolder, file.name);
//            download(fileUrl, filePath, file.hash, requestHeaders);
//        }
        downloadOneByOne(files, contentFolderUrl, downloadFolder, requestHeaders, 0);
    }

    /**
     * 递归下载文件
     *
     * @param files
     * @param contentFolderUrl
     * @param downloadFolder
     * @param requestHeaders
     * @param index
     */
    public void downloadOneByOne(List<ManifestFile> files,
                                 String contentFolderUrl,
                                 String downloadFolder,
                                 Map<String, String> requestHeaders,
                                 int index) {
        if (index >= 0 && index < files.size()) {
            ManifestFile file = files.get(index);
            String fileUrl = URLUtility.construct(contentFolderUrl, file.name);
            String filePath = Paths.get(downloadFolder, file.name);
            try {
                download(fileUrl, filePath, file.hash, requestHeaders);
                if (null != downloadProgressListener) {
                    index++;
                    float progress = (float) (index) / files.size();
                    downloadProgressListener.onDownloadProgress((float) (Math.round(progress * 100)) / 100, index, files.size());
                    downloadOneByOne(files, contentFolderUrl, downloadFolder, requestHeaders, index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Download file from server, save it on the disk and check his hash.
     *
     * @param urlFrom  url to download from
     * @param filePath where to save file
     * @throws IOException
     */
    public void download(String urlFrom,
                         String filePath,
                         String checkSum,
                         Map<String, String> requestHeaders) throws Exception {
        Log.d("CHCP", "Loading file: " + urlFrom);
        final MD5 md5 = new MD5();

        final File downloadFile = new File(filePath);
        FilesUtility.delete(downloadFile);
        FilesUtility.ensureDirectoryExists(downloadFile.getParentFile());

        // download file
        final URLConnection connection = URLConnectionHelper.createConnectionToURL(urlFrom, requestHeaders);
        final InputStream input = new BufferedInputStream(connection.getInputStream());
        final OutputStream output = new BufferedOutputStream(new FileOutputStream(filePath, false));

        final byte data[] = new byte[1024];
        int count;
        while ((count = input.read(data)) != -1) {
            output.write(data, 0, count);
            md5.write(data, count);
        }

        output.flush();
        output.close();
        input.close();

        final String downloadedFileHash = md5.calculateHash();
        if (!downloadedFileHash.equals(checkSum)) {
            throw new IOException("File is corrupted: checksum " + checkSum + " doesn't match hash " + downloadedFileHash + " of the downloaded file");
        }
    }
}
