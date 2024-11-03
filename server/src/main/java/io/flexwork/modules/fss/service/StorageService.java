package io.flexwork.modules.fss.service;

import java.io.InputStream;
import java.io.OutputStream;

public interface StorageService {

    String uploadFile(String containerName, String blobName, InputStream inputStream)
            throws Exception;

    String uploadImage(String containerName, String blobName, InputStream inputStream)
            throws Exception;

    void downloadFile(String containerName, String blobName, OutputStream outputStream)
            throws Exception;

    void deleteFile(String containerName, String blobName) throws Exception;

    void deleteFile(String objectPath) throws Exception;
}
