package com.tradeshift.Repositories;

import com.tradeshift.entities.Holding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HoldingRepository extends JpaRepository<Holding, UUID> {
    List<Holding> findByUserId(UUID userId);               // needed by PortfolioService
    Optional<Holding> findByUserIdAndSymbol(UUID userId, String symbol); // optional
}