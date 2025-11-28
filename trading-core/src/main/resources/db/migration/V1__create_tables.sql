-- Create users table
CREATE TABLE users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_username UNIQUE (username)
);

-- Create wallet_balance table
CREATE TABLE wallet_balance (
    wallet_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    balance DECIMAL(20, 8) NOT NULL DEFAULT 0.00000000,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_user_currency UNIQUE (user_id, currency)
);

-- Create price_snapshot table
CREATE TABLE price_snapshot (
    price_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL,
    bid_price DECIMAL(20, 8) NOT NULL,
    ask_price DECIMAL(20, 8) NOT NULL,
    bid_exchange VARCHAR(10),
    ask_exchange VARCHAR(10),
    captured_at TIMESTAMP NOT NULL
);

-- Create trade_transaction table
CREATE TABLE trade_transaction (
    trade_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    symbol VARCHAR(20) NOT NULL,
    side VARCHAR(10) NOT NULL,
    quantity DECIMAL(20, 8) NOT NULL,
    executed_price DECIMAL(20, 8) NOT NULL,
    total_cost DECIMAL(20, 8) NOT NULL,
    fee_amount DECIMAL(20, 8) NOT NULL DEFAULT 0.00000000,
    net_amount DECIMAL(20, 8) NOT NULL,
    executed_at TIMESTAMP NOT NULL,
    aggregated_from VARCHAR(10)
);

-- Create indexes
CREATE INDEX idx_symbol_captured ON price_snapshot(symbol, captured_at);
CREATE INDEX idx_user_executed ON trade_transaction(user_id, executed_at);
