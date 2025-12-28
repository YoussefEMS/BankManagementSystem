-- Bank Management System - Seed Data with Authentication
-- Insert test customers and their linked accounts

-- ============================================================================
-- 1. INSERT CUSTOMERS
-- ============================================================================

INSERT INTO [Customer] (full_name, national_id, email, password, phone, address, tier, status)
VALUES 
    ('John Doe', '12345678', 'john@example.com', 'password123', '555-0001', '123 Main Street, New York, NY 10001', 'GOLD', 'ACTIVE'),
    ('Jane Smith', '87654321', 'jane@example.com', 'password456', '555-0002', '456 Oak Avenue, Los Angeles, CA 90001', 'PLATINUM', 'ACTIVE'),
    ('Michael Johnson', '11223344', 'michael@example.com', 'password789', '555-0003', '789 Pine Road, Chicago, IL 60601', 'SILVER', 'ACTIVE'),
    ('Sarah Williams', '44332211', 'sarah@example.com', 'password101112', '555-0004', '321 Elm Street, Houston, TX 77001', 'GOLD', 'ACTIVE'),
    ('David Brown', '55667788', 'david@example.com', 'password131415', '555-0005', '654 Maple Drive, Phoenix, AZ 85001', 'SILVER', 'ACTIVE'),
    ('Emma Davis', '99887766', 'emma@example.com', 'password161718', '555-0006', '987 Cedar Lane, Philadelphia, PA 19101', 'PLATINUM', 'ACTIVE');

-- ============================================================================
-- 2. INSERT ACCOUNTS FOR EACH CUSTOMER
-- ============================================================================

-- Customer 1: John Doe (customer_id = 1)
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-001-CHK', 1, 'Checking', 5500.50, 'USD', 'ACTIVE'),
    ('ACC-001-SAV', 1, 'Savings', 25000.00, 'USD', 'ACTIVE'),
    ('ACC-001-MMA', 1, 'Money Market', 50000.00, 'USD', 'ACTIVE');

-- Customer 2: Jane Smith (customer_id = 2)
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-002-CHK', 2, 'Checking', 8750.25, 'USD', 'ACTIVE'),
    ('ACC-002-SAV', 2, 'Savings', 75000.00, 'USD', 'ACTIVE'),
    ('ACC-002-BUS', 2, 'Business', 125000.00, 'USD', 'ACTIVE');

-- Customer 3: Michael Johnson (customer_id = 3)
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-003-CHK', 3, 'Checking', 3200.75, 'USD', 'ACTIVE'),
    ('ACC-003-SAV', 3, 'Savings', 15000.00, 'USD', 'ACTIVE');

-- Customer 4: Sarah Williams (customer_id = 4)
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-004-CHK', 4, 'Checking', 12500.00, 'USD', 'ACTIVE'),
    ('ACC-004-SAV', 4, 'Savings', 45000.00, 'USD', 'ACTIVE'),
    ('ACC-004-MMA', 4, 'Money Market', 100000.00, 'USD', 'ACTIVE'),
    ('ACC-004-IRA', 4, 'IRA', 250000.00, 'USD', 'ACTIVE');

-- Customer 5: David Brown (customer_id = 5)
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-005-CHK', 5, 'Checking', 2100.50, 'USD', 'ACTIVE'),
    ('ACC-005-SAV', 5, 'Savings', 8500.00, 'USD', 'ACTIVE');

-- Customer 6: Emma Davis (customer_id = 6)
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-006-CHK', 6, 'Checking', 7650.00, 'USD', 'ACTIVE'),
    ('ACC-006-SAV', 6, 'Savings', 60000.00, 'USD', 'ACTIVE'),
    ('ACC-006-BUS', 6, 'Business', 200000.00, 'USD', 'ACTIVE'),
    ('ACC-006-MMA', 6, 'Money Market', 150000.00, 'USD', 'ACTIVE');

-- ============================================================================
-- 3. INSERT SAMPLE TRANSACTIONS (Optional)
-- ============================================================================

-- Transactions for John Doe's Checking Account
INSERT INTO [Transactions] (account_number, type, amount, performed_by, note, balance_after, reference_code)
VALUES 
    ('ACC-001-CHK', 'Deposit', 1000.00, 'John Doe', 'Initial Deposit', 1000.00, 'TXN-20251220-001'),
    ('ACC-001-CHK', 'Withdrawal', 200.00, 'John Doe', 'ATM Withdrawal', 800.00, 'TXN-20251220-002'),
    ('ACC-001-CHK', 'Deposit', 4700.50, 'Payroll', 'Salary Deposit', 5500.50, 'TXN-20251220-003');

-- Transactions for Jane Smith's Checking Account
INSERT INTO [Transactions] (account_number, type, amount, performed_by, note, balance_after, reference_code)
VALUES 
    ('ACC-002-CHK', 'Deposit', 5000.00, 'Jane Smith', 'Initial Deposit', 5000.00, 'TXN-20251220-004'),
    ('ACC-002-CHK', 'Withdrawal', 500.00, 'Jane Smith', 'Cash Withdrawal', 4500.00, 'TXN-20251220-005'),
    ('ACC-002-CHK', 'Deposit', 4250.25, 'Payroll', 'Salary Deposit', 8750.25, 'TXN-20251220-006');

-- Transactions for Sarah Williams' Savings Account
INSERT INTO [Transactions] (account_number, type, amount, performed_by, note, balance_after, reference_code)
VALUES 
    ('ACC-004-SAV', 'Deposit', 45000.00, 'Sarah Williams', 'Initial Savings', 45000.00, 'TXN-20251220-007'),
    ('ACC-004-SAV', 'Interest', 25.00, 'System', 'Monthly Interest', 45025.00, 'TXN-20251220-008');

-- ============================================================================
-- 4. VERIFICATION QUERIES (Run separately to verify)
-- ============================================================================

-- Show all customers
-- SELECT customer_id, full_name, email, tier, status FROM [Customer];

-- Show all accounts grouped by customer
-- SELECT c.full_name, a.account_number, a.account_type, a.balance, a.currency, a.status
-- FROM [Account] a
-- JOIN [Customer] c ON a.customer_id = c.customer_id
-- ORDER BY c.customer_id, a.account_number;

-- Show account count per customer
-- SELECT c.full_name, COUNT(a.account_number) as account_count
-- FROM [Customer] c
-- LEFT JOIN [Account] a ON c.customer_id = a.customer_id
-- GROUP BY c.customer_id, c.full_name;

-- Show all transactions
-- SELECT t.transaction_id, t.account_number, t.type, t.amount, t.timestamp, t.balance_after, t.reference_code
-- FROM [Transactions] t
-- ORDER BY t.timestamp DESC;
