package com.trade.core.repository;

import com.trade.pricing.entity.TradeTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TradeTransactionRepository extends JpaRepository<TradeTransaction, Long> {
    
    Page<TradeTransaction> findByUserIdOrderByExecutedAtDesc(Long userId, Pageable pageable);
}
