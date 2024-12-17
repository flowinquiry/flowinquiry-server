package io.flowinquiry.modules.fss.web.rest;

import io.flowinquiry.modules.fss.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import org.springframework.http.*;
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

        return new ResponseEntity<>(
                byteArrayOutputStream.toByteArray(), httpHeaders, HttpStatus.OK);
    }
}
