# Crypto Trading System

A REST API built with Spring Boot for trading cryptocurrencies (BTC/ETH) with real-time price aggregation from Binance and Huobi exchanges. Users can trade, view wallet balances, and track transaction history using an in-memory H2 database.

## Quick Start

### Prerequisites
- Java 17+
- Gradle 7.x+

### Installation & Run

```bash
./gradlew clean build
./gradlew bootRun
```

The application starts on `http://localhost:8080` with H2 console at `http://localhost:8080/h2-console`.

## API Endpoints

### 1. Get Latest Best Aggregated Price
```bash
GET /api/prices/latest?symbol=ETHUSDT
GET /api/prices/latest?symbol=BTCUSDT
```

### 2. Execute Trade
```bash
POST /api/trades
Content-Type: application/json

{
  "userId": 1,
  "symbol": "ETHUSDT",
  "side": "BUY",
  "quantity": 0.5
}
```

### 3. Get Wallet Balance
```bash
GET /api/wallet/{userId}
```

### 4. Get Trading History
```bash
GET /api/trades/history/{userId}
```

## Running Tests

```bash
./gradlew test
```

## Technologies

- **Java 17** - Language runtime
- **Spring Boot 3.x** - Application framework
- **Spring Data JPA** - Database ORM
- **H2 Database** - In-memory database
- **Spring Scheduler** - Price aggregation (10s interval)
- **JUnit 5** - Testing framework

## Design Decisions

### Price Aggregation Strategy
- **10-second com.trade.pricing.scheduler** fetches prices from Binance and Huobi APIs concurrently
- **Best price selection**: Lowest ask price for BUY orders, highest bid price for SELL orders
- **Trade-off**: Real-time accuracy vs API rate limits; 10s interval balances freshness with reliability

### Database Schema
- **Users**: Pre-seeded with 50,000 USDT initial balance
- **Wallets**: Tracks BTC, ETH, and USDT balances per user
- **Prices**: Stores aggregated best prices with timestamp
- **Transactions**: Immutable trade history with price snapshot

### Trade Execution Logic
- Validates sufficient balance before execution (USDT for BUY, crypto for SELL)
- Uses latest aggregated price (not live API calls during trade)
- Atomic wallet updates to prevent race conditions
- **Assumption**: Single-threaded trades per user; for production, add optimistic locking

### Error Handling
- Returns 400 for insufficient funds or invalid symbols
- Returns 404 for non-existent users
- Logs external API failures without blocking com.trade.pricing.scheduler

### Improvements for Production
- Add Redis caching for price data to reduce DB load
- Implement WebSocket for real-time price updates
- Add transaction rollback mechanism for failed trades
- Include rate limiting and authentication (OAuth2/JWT)
- Support more trading pairs and exchanges

## Project Structure

```
src/com.trade.pricing.main/java/
├── com.trade.pricing.controllers/     # REST endpoints
├── com.trade.pricing.entities/        # JPA com.trade.pricing.entities (User, Wallet, Price, Transaction)
├── com.trade.pricing.repositories/    # Data access layer
├── com.trade.pricing.services/        # Business logic
└── com.trade.pricing.scheduler/       # Price aggregation job
```

## Assumptions

1. Users are pre-authenticated (no auth implementation)
2. Initial wallet: 50,000 USDT per user
3. Supported pairs: ETHUSDT, BTCUSDT only
4. Prices stored in database, not fetched during trade execution

## Questions?

Reach out for clarifications or discussions about design choices!
