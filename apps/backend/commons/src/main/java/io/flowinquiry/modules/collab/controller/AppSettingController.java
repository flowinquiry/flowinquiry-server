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
import org.springframework.web.bind.annotation.RequestParam;
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
    public List<AppSettingDTO> getAllSettings(
            @RequestParam(value = "group", required = false) String group) {
        if (group != null) {
            return appSettingService.getSettingsByGroup(group);
        }
        return appSettingService.getAllSettingDTOs();
    }

    @PutMapping
    public void updateSettings(@RequestBody List<AppSettingDTO> settings) {
        appSettingService.updateSettings(settings);
    }

    @PutMapping("/{key}")
    public void updateSetting(@PathVariable String key, @RequestBody AppSettingDTO dto) {
        if (!key.equals(dto.getKey())) {
            return;
        }
        appSettingService.updateValue(dto.getKey(), dto.getValue());
    }
}
