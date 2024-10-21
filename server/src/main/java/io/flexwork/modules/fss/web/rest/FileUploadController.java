package io.flexwork.modules.fss.web.rest;

import io.flexwork.modules.fss.service.FsObjectService;
import io.flexwork.modules.fss.service.IStorageService;
import io.flexwork.modules.usermanagement.service.dto.UserKey;
import io.flexwork.security.SecurityUtils;
import jakarta.json.Json;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    private final FsObjectService fsObjectService;

    private final IStorageService storageService;

    public FileUploadController(FsObjectService fsObjectService, IStorageService storageService) {
        this.fsObjectService = fsObjectService;
        this.storageService = storageService;
    }

    @PostMapping(value = "/singleUpload")
    public ResponseEntity<String> submit(
            @RequestParam("file") MultipartFile file,
            @RequestParam String type,
            @RequestParam Optional<String> parentPath)
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

        String fileName = URLEncoder.encode(file.getOriginalFilename(), StandardCharsets.UTF_8);
        storageService.uploadFile(prefixPath, fileName, file.getInputStream(), file.getSize());
        String pathRes =
                Json.createObjectBuilder()
                        .add("path", prefixPath + "/" + fileName)
                        .build()
                        .toString();
        return ResponseEntity.ok(pathRes);
    }
}
