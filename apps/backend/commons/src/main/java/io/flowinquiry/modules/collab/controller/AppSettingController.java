package io.flowinquiry.modules.collab.controller;

import io.flowinquiry.modules.collab.service.AppSettingService;
import io.flowinquiry.modules.collab.service.dto.AppSettingDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class AppSettingController {

    private final AppSettingService appSettingService;

    public AppSettingController(AppSettingService appSettingService) {
        this.appSettingService = appSettingService;
    }

    @GetMapping("/{key}")
    public ResponseEntity<AppSettingDTO> getSetting(@PathVariable String key) {
        Optional<String> value = appSettingService.getValue(key);
        return value.map(v -> ResponseEntity.ok(new AppSettingDTO(key, v, null, null, null)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<AppSettingDTO>> getAllSettings() {
        return ResponseEntity.ok(appSettingService.getAllSettingDTOs());
    }

    @PutMapping
    public ResponseEntity<Void> updateSettings(@RequestBody List<AppSettingDTO> settings) {
        appSettingService.updateSettings(settings);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{key}")
    public ResponseEntity<Void> updateSetting(
            @PathVariable String key, @RequestBody AppSettingDTO dto) {
        if (!key.equals(dto.getKey())) {
            return ResponseEntity.badRequest().build();
        }
        appSettingService.updateValue(dto.getKey(), dto.getValue());
        return ResponseEntity.ok().build();
    }
}
