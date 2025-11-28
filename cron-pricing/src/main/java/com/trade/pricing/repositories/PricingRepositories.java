package com.trade.pricing.repositories;

import com.trade.pricing.entity.BestPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BestPriceRepository extends JpaRepository<BestPrice, Long> {
    
    Optional<BestPrice> findTopBySymbolOrderByCreatedAtDesc(String symbol);
    
    @Query("SELECT bp FROM BestPrice bp WHERE bp.symbol = :symbol ORDER BY bp.createdAt DESC LIMIT 1")
    Optional<BestPrice> findLatestBySymbol(String symbol);
}
