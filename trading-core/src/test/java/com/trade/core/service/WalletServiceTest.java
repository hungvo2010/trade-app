package com.trade.core.service;

import com.trade.core.dto.WalletBalanceResponse;
import com.trade.core.repository.WalletBalanceRepository;
import com.trade.pricing.entity.WalletBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletBalanceRepository walletBalanceRepository;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getWalletBalance_Success() {
        List<WalletBalance> balances = Arrays.asList(
            createWalletBalance(1L, 1L, "USDT", new BigDecimal("50000")),
            createWalletBalance(2L, 1L, "BTC", new BigDecimal("0.5")),
            createWalletBalance(3L, 1L, "ETH", new BigDecimal("10"))
        );

        when(walletBalanceRepository.findByUserId(1L)).thenReturn(balances);

        WalletBalanceResponse response = walletService.getWalletBalance(1L);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertEquals(3, response.getBalances().size());
        assertEquals(new BigDecimal("50000"), response.getBalances().get("USDT"));
        assertEquals(new BigDecimal("0.5"), response.getBalances().get("BTC"));
        assertEquals(new BigDecimal("10"), response.getBalances().get("ETH"));
    }

    @Test
    void getWalletBalance_EmptyWallet() {
        when(walletBalanceRepository.findByUserId(1L)).thenReturn(Arrays.asList());

        WalletBalanceResponse response = walletService.getWalletBalance(1L);

        assertNotNull(response);
        assertEquals(1L, response.getUserId());
        assertTrue(response.getBalances().isEmpty());
    }

    @Test
    void updateBalance_ExistingWallet() {
        WalletBalance existing = createWalletBalance(1L, 1L, "USDT", new BigDecimal("50000"));
        
        when(walletBalanceRepository.findByUserIdAndCurrency(1L, "USDT"))
            .thenReturn(Optional.of(existing));
        when(walletBalanceRepository.save(any(WalletBalance.class)))
            .thenReturn(existing);

        walletService.updateBalance(1L, "USDT", new BigDecimal("-1000"));

        verify(walletBalanceRepository).save(argThat(wallet -> 
            wallet.getBalance().compareTo(new BigDecimal("49000")) == 0
        ));
    }

    @Test
    void updateBalance_NewWallet() {
        when(walletBalanceRepository.findByUserIdAndCurrency(1L, "BTC"))
            .thenReturn(Optional.empty());
        
        WalletBalance newWallet = createWalletBalance(null, 1L, "BTC", new BigDecimal("0.5"));
        when(walletBalanceRepository.save(any(WalletBalance.class)))
            .thenReturn(newWallet);

        walletService.updateBalance(1L, "BTC", new BigDecimal("0.5"));

        verify(walletBalanceRepository).save(argThat(wallet -> 
            wallet.getUserId().equals(1L) &&
            wallet.getCurrency().equals("BTC") &&
            wallet.getBalance().compareTo(new BigDecimal("0.5")) == 0
        ));
    }

    @Test
    void getBalance_ExistingCurrency() {
        WalletBalance wallet = createWalletBalance(1L, 1L, "USDT", new BigDecimal("50000"));
        
        when(walletBalanceRepository.findByUserIdAndCurrency(1L, "USDT"))
            .thenReturn(Optional.of(wallet));

        BigDecimal balance = walletService.getBalance(1L, "USDT");

        assertEquals(new BigDecimal("50000"), balance);
    }

    @Test
    void getBalance_NonExistingCurrency() {
        when(walletBalanceRepository.findByUserIdAndCurrency(1L, "ETH"))
            .thenReturn(Optional.empty());

        BigDecimal balance = walletService.getBalance(1L, "ETH");

        assertEquals(BigDecimal.ZERO, balance);
    }

    private WalletBalance createWalletBalance(Long id, Long userId, String currency, BigDecimal balance) {
        WalletBalance wallet = new WalletBalance();
        wallet.setWalletId(id);
        wallet.setUserId(userId);
        wallet.setCurrency(currency);
        wallet.setBalance(balance);
        return wallet;
    }
}
