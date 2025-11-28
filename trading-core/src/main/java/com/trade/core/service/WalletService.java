package com.trade.core.service;

import com.trade.core.dto.WalletBalanceResponse;
import com.trade.core.repository.WalletBalanceRepository;
import com.trade.pricing.entity.WalletBalance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WalletService {
    
    private final WalletBalanceRepository walletBalanceRepository;

    public WalletService(WalletBalanceRepository walletBalanceRepository) {
        this.walletBalanceRepository = walletBalanceRepository;
    }

    public WalletBalanceResponse getWalletBalance(Long userId) {
        List<WalletBalance> balances = walletBalanceRepository.findByUserId(userId);
        
        Map<String, BigDecimal> balanceMap = new HashMap<>();
        for (WalletBalance balance : balances) {
            balanceMap.put(balance.getCurrency(), balance.getBalance());
        }
        
        return new WalletBalanceResponse(userId, balanceMap);
    }

    @Transactional
    public void updateBalance(Long userId, String currency, BigDecimal amount) {
        WalletBalance wallet = walletBalanceRepository
            .findByUserIdAndCurrency(userId, currency)
            .orElseGet(() -> {
                WalletBalance newWallet = new WalletBalance();
                newWallet.setUserId(userId);
                newWallet.setCurrency(currency);
                newWallet.setBalance(BigDecimal.ZERO);
                return newWallet;
            });
        
        wallet.setBalance(wallet.getBalance().add(amount));
        walletBalanceRepository.save(wallet);
    }

    public BigDecimal getBalance(Long userId, String currency) {
        return walletBalanceRepository
            .findByUserIdAndCurrency(userId, currency)
            .map(WalletBalance::getBalance)
            .orElse(BigDecimal.ZERO);
    }
}
