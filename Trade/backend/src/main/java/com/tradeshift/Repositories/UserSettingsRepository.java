package com.tradeshift.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradeshift.entities.UserSettings;

import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUserId(Long userId);
}
