package com.trade.core.repository;

import com.trade.pricing.entity.WalletBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletBalanceRepository extends JpaRepository<WalletBalance, Long> {
    
    List<WalletBalance> findByUserId(Long userId);
    
    Optional<WalletBalance> findByUserIdAndCurrency(Long userId, String currency);
}
