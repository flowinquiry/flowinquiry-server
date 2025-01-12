package io.flowinquiry.modules.fss.controller;

import io.flowinquiry.modules.fss.service.StorageService;
import io.flowinquiry.modules.usermanagement.service.dto.UserKey;
import io.flowinquiry.security.SecurityUtils;
import jakarta.json.Json;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
public class FileUploadController {

    private static final Logger LOG = LoggerFactory.getLogger(FileUploadController.class);

    private static final String AVATAR_TYPE = "avatar";

    private final Map<String, String> typeRelativePaths =
            new HashMap<>() {
                {
                    put(AVATAR_TYPE, AVATAR_TYPE);
                }
            };

    private final StorageService storageService;

    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping(value = "/singleUpload")
    public ResponseEntity<String> submit(
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") String type,
            @RequestParam("parentPath") Optional<String> parentPath)
            throws Exception {
        String currentUser = SecurityUtils.getCurrentUserLogin().map(UserKey::getEmail).orElse("");
        LOG.debug(
                "User {} saves file {} into the storage with options {}",
                currentUser,
                file.getOriginalFilename(),
                type);

        if (!typeRelativePaths.containsKey(type))
            return ResponseEntity.badRequest().body("Not support upload with type " + type);
        String prefixPath = typeRelativePaths.get(type);

        String path =
                storageService.uploadFile(
                        prefixPath, file.getOriginalFilename(), file.getInputStream());
        return ResponseEntity.ok(Json.createObjectBuilder().add("path", path).build().toString());
    }
}
