-- Insert test user
INSERT INTO users (username, created_at) 
VALUES ('testuser', CURRENT_TIMESTAMP);

-- Initialize wallet with starting balances for user_id = 1
INSERT INTO wallet_balance (user_id, currency, balance, updated_at) 
VALUES (1, 'USDT', 50000.00000000, CURRENT_TIMESTAMP);

INSERT INTO wallet_balance (user_id, currency, balance, updated_at) 
VALUES (1, 'BTC', 0.00000000, CURRENT_TIMESTAMP);

INSERT INTO wallet_balance (user_id, currency, balance, updated_at) 
VALUES (1, 'ETH', 0.00000000, CURRENT_TIMESTAMP);
