package com.tradeshift.service;

import com.tradeshift.Repositories.UserSettingsRepository;
import com.tradeshift.entities.UserSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserSettingsService {

    private final UserSettingsRepository repository;

    public UserSettingsService(UserSettingsRepository repository) {
        this.repository = repository;
    }

    public Optional<UserSettings> getSettingsByUserId(Long userId) {
        return repository.findByUserId(userId);
    }

    @Transactional 
    public UserSettings saveOrUpdateSettings(Long userId, UserSettings newSettings) {
        return repository.findByUserId(userId)
                .map(existing -> {
                   
                    existing.setFirstName(newSettings.getFirstName());
                    existing.setLastName(newSettings.getLastName());
                    existing.setEmail(newSettings.getEmail());
                    existing.setPhone(newSettings.getPhone());
                    existing.setBio(newSettings.getBio());
                    existing.setNotificationsEnabled(newSettings.isNotificationsEnabled());
                    existing.setTheme(newSettings.getTheme());

                    
                    return existing;
                })
                .orElseGet(() -> {
                    
                    newSettings.setUserId(userId);
                    return newSettings;
                });
    }
}
