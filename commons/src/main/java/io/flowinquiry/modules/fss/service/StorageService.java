package io.flowinquiry.modules.fss.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public interface StorageService {

    String AVATAR_TYPE = "avatar";

    String ATTACHMENTS = "attachments";

    Map<String, String> typeRelativePaths =
            new HashMap<>() {
                {
                    put(AVATAR_TYPE, AVATAR_TYPE);
                }
            };

    default String getRelativePathByType(String type) {
        return typeRelativePaths.get(type);
    }

    String uploadFile(String containerName, String blobName, InputStream inputStream)
            throws Exception;

    String uploadImage(String containerName, String blobName, InputStream inputStream)
            throws Exception;

    void downloadFile(String containerName, String blobName, OutputStream outputStream)
            throws Exception;

    void deleteFile(String containerName, String blobName) throws Exception;

    void deleteFile(String objectPath) throws Exception;
}
