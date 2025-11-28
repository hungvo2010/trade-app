package pricing.services;

import org.springframework.stereotype.Service;

@Service
public interface PriceService {
    void getLatestPrices();
}
