package com.trade.core.repository;

import com.trade.pricing.entity.PriceSnapshot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PriceSnapshotRepository extends JpaRepository<PriceSnapshot, Long> {
    
    Optional<PriceSnapshot> findTopBySymbolOrderByCapturedAtDesc(String symbol);
    
    @Query("SELECT ps FROM PriceSnapshot ps WHERE ps.symbol = :symbol ORDER BY ps.capturedAt DESC")
    Page<PriceSnapshot> findBySymbolOrderByCapturedAtDesc(String symbol, Pageable pageable);
}
