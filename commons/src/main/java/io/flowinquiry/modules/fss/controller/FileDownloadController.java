package io.flowinquiry.modules.fss.controller;

import io.flowinquiry.modules.fss.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
public class FileDownloadController {

    private final StorageService storageService;

    public FileDownloadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping(value = "/**")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request) throws Exception {
        String requestUrl = request.getRequestURI();
        int fileIndex = requestUrl.lastIndexOf("/");
        if (fileIndex == -1) {
            throw new IllegalArgumentException("Invalid request. Miss the file container");
        }
        String fileName = requestUrl.substring(fileIndex + 1);
        String container = requestUrl.substring("/api/files".length() + 1, fileIndex);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        storageService.downloadFile(container, fileName, byteArrayOutputStream);

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(
                MediaTypeFactory.getMediaType(fileName).orElse(MediaType.APPLICATION_OCTET_STREAM));
        httpHeaders.setCacheControl("max-age=3600, must-revalidate"); // Cache for 1 hour
        httpHeaders.setExpires(System.currentTimeMillis() + 3600 * 1000); // Set expiry
        httpHeaders.setETag("\"" + byteArrayOutputStream.size() + "\""); // ETag for revalidation

        return new ResponseEntity<>(
                byteArrayOutputStream.toByteArray(), httpHeaders, HttpStatus.OK);
    }
}
