package com.tradeshift.controller;

import com.tradeshift.entities.UserSettings;
import com.tradeshift.service.UserSettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
@CrossOrigin(origins = "http://localhost:5174")
public class UserSettingsController {

    private final UserSettingsService service;

    public UserSettingsController(UserSettingsService service) {
        this.service = service;
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserSettings> updateSettings(
            @PathVariable Long userId,
            @RequestBody UserSettings newSettings) {

        UserSettings updated = service.saveOrUpdateSettings(userId, newSettings);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserSettings> getSettings(@PathVariable Long userId) {
        return service.getSettingsByUserId(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        
        
    }
}
