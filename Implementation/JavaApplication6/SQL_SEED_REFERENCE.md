# SQL Seed Data - Complete Query Reference

## Overview
This document contains all the SQL queries used to populate the Bank Management System database with test data for authentication and account management.

---

## Part 1: Customer Insert Statements

### Insert 6 Customers
```sql
INSERT INTO [Customer] (full_name, national_id, email, password, phone, address, tier, status)
VALUES 
    ('John Doe', '12345678', 'john@example.com', 'password123', '555-0001', '123 Main Street, New York, NY 10001', 'GOLD', 'ACTIVE'),
    ('Jane Smith', '87654321', 'jane@example.com', 'password456', '555-0002', '456 Oak Avenue, Los Angeles, CA 90001', 'PLATINUM', 'ACTIVE'),
    ('Michael Johnson', '11223344', 'michael@example.com', 'password789', '555-0003', '789 Pine Road, Chicago, IL 60601', 'SILVER', 'ACTIVE'),
    ('Sarah Williams', '44332211', 'sarah@example.com', 'password101112', '555-0004', '321 Elm Street, Houston, TX 77001', 'GOLD', 'ACTIVE'),
    ('David Brown', '55667788', 'david@example.com', 'password131415', '555-0005', '654 Maple Drive, Phoenix, AZ 85001', 'SILVER', 'ACTIVE'),
    ('Emma Davis', '99887766', 'emma@example.com', 'password161718', '555-0006', '987 Cedar Lane, Philadelphia, PA 19101', 'PLATINUM', 'ACTIVE');
```

**Results**: 6 customers inserted
- Customer IDs auto-generated: 1-6
- All status set to ACTIVE
- Unique emails and national IDs

---

## Part 2: Account Insert Statements

### Customer 1: John Doe (3 accounts)
```sql
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-001-CHK', 1, 'Checking', 5500.50, 'USD', 'ACTIVE'),
    ('ACC-001-SAV', 1, 'Savings', 25000.00, 'USD', 'ACTIVE'),
    ('ACC-001-MMA', 1, 'Money Market', 50000.00, 'USD', 'ACTIVE');
```

### Customer 2: Jane Smith (3 accounts)
```sql
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-002-CHK', 2, 'Checking', 8750.25, 'USD', 'ACTIVE'),
    ('ACC-002-SAV', 2, 'Savings', 75000.00, 'USD', 'ACTIVE'),
    ('ACC-002-BUS', 2, 'Business', 125000.00, 'USD', 'ACTIVE');
```

### Customer 3: Michael Johnson (2 accounts)
```sql
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-003-CHK', 3, 'Checking', 3200.75, 'USD', 'ACTIVE'),
    ('ACC-003-SAV', 3, 'Savings', 15000.00, 'USD', 'ACTIVE');
```

### Customer 4: Sarah Williams (4 accounts)
```sql
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-004-CHK', 4, 'Checking', 12500.00, 'USD', 'ACTIVE'),
    ('ACC-004-SAV', 4, 'Savings', 45000.00, 'USD', 'ACTIVE'),
    ('ACC-004-MMA', 4, 'Money Market', 100000.00, 'USD', 'ACTIVE'),
    ('ACC-004-IRA', 4, 'IRA', 250000.00, 'USD', 'ACTIVE');
```

### Customer 5: David Brown (2 accounts)
```sql
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-005-CHK', 5, 'Checking', 2100.50, 'USD', 'ACTIVE'),
    ('ACC-005-SAV', 5, 'Savings', 8500.00, 'USD', 'ACTIVE');
```

### Customer 6: Emma Davis (4 accounts)
```sql
INSERT INTO [Account] (account_number, customer_id, account_type, balance, currency, status)
VALUES 
    ('ACC-006-CHK', 6, 'Checking', 7650.00, 'USD', 'ACTIVE'),
    ('ACC-006-SAV', 6, 'Savings', 60000.00, 'USD', 'ACTIVE'),
    ('ACC-006-BUS', 6, 'Business', 200000.00, 'USD', 'ACTIVE'),
    ('ACC-006-MMA', 6, 'Money Market', 150000.00, 'USD', 'ACTIVE');
```

**Results**: 18 accounts inserted
- Account numbers follow pattern: ACC-###-XXX (### = customer ID, XXX = type)
- All currency set to USD
- All status set to ACTIVE
- Balances range from $2,100 to $250,000

---

## Part 3: Transaction Insert Statements

### John Doe's Checking Account Transactions (ACC-001-CHK)
```sql
INSERT INTO [Transactions] (account_number, type, amount, performed_by, note, balance_after, reference_code)
VALUES 
    ('ACC-001-CHK', 'Deposit', 1000.00, 'John Doe', 'Initial Deposit', 1000.00, 'TXN-20251220-001'),
    ('ACC-001-CHK', 'Withdrawal', 200.00, 'John Doe', 'ATM Withdrawal', 800.00, 'TXN-20251220-002'),
    ('ACC-001-CHK', 'Deposit', 4700.50, 'Payroll', 'Salary Deposit', 5500.50, 'TXN-20251220-003');
```

### Jane Smith's Checking Account Transactions (ACC-002-CHK)
```sql
INSERT INTO [Transactions] (account_number, type, amount, performed_by, note, balance_after, reference_code)
VALUES 
    ('ACC-002-CHK', 'Deposit', 5000.00, 'Jane Smith', 'Initial Deposit', 5000.00, 'TXN-20251220-004'),
    ('ACC-002-CHK', 'Withdrawal', 500.00, 'Jane Smith', 'Cash Withdrawal', 4500.00, 'TXN-20251220-005'),
    ('ACC-002-CHK', 'Deposit', 4250.25, 'Payroll', 'Salary Deposit', 8750.25, 'TXN-20251220-006');
```

### Sarah Williams' Savings Account Transactions (ACC-004-SAV)
```sql
INSERT INTO [Transactions] (account_number, type, amount, performed_by, note, balance_after, reference_code)
VALUES 
    ('ACC-004-SAV', 'Deposit', 45000.00, 'Sarah Williams', 'Initial Savings', 45000.00, 'TXN-20251220-007'),
    ('ACC-004-SAV', 'Interest', 25.00, 'System', 'Monthly Interest', 45025.00, 'TXN-20251220-008');
```

**Results**: 8 transactions inserted
- Types: Deposit, Withdrawal, Interest
- Reference codes for tracking
- Balance_after field tracks running balance

---

## Verification Queries

### 1. View All Customers
```sql
SELECT customer_id, full_name, email, tier, status 
FROM [Customer] 
ORDER BY customer_id;
```

**Expected Output**: 6 rows with customer details

---

### 2. View All Accounts
```sql
SELECT 
    a.account_number,
    c.full_name AS customer_name,
    a.account_type,
    CAST(a.balance AS DECIMAL(10,2)) AS balance,
    a.currency,
    a.status
FROM [Account] a
JOIN [Customer] c ON a.customer_id = c.customer_id
ORDER BY c.customer_id, a.account_number;
```

**Expected Output**: 18 rows showing all accounts with customer names

---

### 3. Accounts Per Customer Summary
```sql
SELECT 
    c.customer_id,
    c.full_name,
    COUNT(a.account_number) AS account_count,
    SUM(a.balance) AS total_balance
FROM [Customer] c
LEFT JOIN [Account] a ON c.customer_id = a.customer_id
GROUP BY c.customer_id, c.full_name
ORDER BY c.customer_id;
```

**Expected Output**: 
```
customer_id | full_name          | account_count | total_balance
------------|-------------------|---------------|---------------
1           | John Doe          | 3             | 80500.50
2           | Jane Smith        | 3             | 208750.25
3           | Michael Johnson   | 2             | 18200.75
4           | Sarah Williams    | 4             | 407500.00
5           | David Brown       | 2             | 10600.50
6           | Emma Davis        | 4             | 417650.00
```

---

### 4. View All Transactions
```sql
SELECT 
    t.transaction_id,
    t.account_number,
    t.type,
    CAST(t.amount AS DECIMAL(10,2)) AS amount,
    t.timestamp,
    CAST(t.balance_after AS DECIMAL(10,2)) AS balance_after,
    t.reference_code
FROM [Transactions] t
ORDER BY t.timestamp DESC;
```

**Expected Output**: 8 rows with transaction details

---

### 5. Authenticate User
```sql
SELECT customer_id, full_name, email, tier, status
FROM [Customer]
WHERE email = 'john@example.com' AND status = 'ACTIVE';
```

**Expected Output**: 1 row for John Doe (if password matches in application logic)

---

### 6. Get Customer's Accounts
```sql
SELECT 
    a.account_number,
    a.account_type,
    CAST(a.balance AS DECIMAL(10,2)) AS balance,
    a.currency,
    a.status
FROM [Account] a
WHERE a.customer_id = 1
ORDER BY a.date_opened DESC;
```

**Expected Output**: 3 accounts for John Doe (customer_id = 1)

---

### 7. Get Account Transactions
```sql
SELECT 
    t.transaction_id,
    t.type,
    CAST(t.amount AS DECIMAL(10,2)) AS amount,
    t.timestamp,
    t.performed_by,
    t.note,
    t.reference_code
FROM [Transactions] t
WHERE t.account_number = 'ACC-001-CHK'
ORDER BY t.timestamp DESC;
```

**Expected Output**: 3 transactions for John Doe's checking account

---

## Data Integrity Checks

### Foreign Key Validation
```sql
-- Verify all accounts have valid customer_id
SELECT a.account_number, a.customer_id
FROM [Account] a
WHERE a.customer_id NOT IN (SELECT customer_id FROM [Customer]);
```

**Expected Output**: 0 rows (no orphaned accounts)

---

### Unique Constraints
```sql
-- Verify unique emails
SELECT email, COUNT(*) as count
FROM [Customer]
GROUP BY email
HAVING COUNT(*) > 1;
```

**Expected Output**: 0 rows (all emails unique)

---

```sql
-- Verify unique national IDs
SELECT national_id, COUNT(*) as count
FROM [Customer]
GROUP BY national_id
HAVING COUNT(*) > 1;
```

**Expected Output**: 0 rows (all national IDs unique)

---

## Statistics

| Metric | Value |
|--------|-------|
| Total Customers | 6 |
| Total Accounts | 18 |
| Total Transactions | 8 |
| Average Accounts/Customer | 3 |
| Total Assets | $1,153,700.50 |
| Highest Balance | $250,000 (IRA) |
| Lowest Balance | $2,100.50 (Checking) |

---

## Notes

- All timestamps are auto-generated on INSERT (SYSDATETIME())
- Primary keys are auto-generated where specified (IDENTITY)
- All data uses USD currency
- All records set to ACTIVE status
- Passwords are plain text (testing only)
- Account numbers follow naming convention: ACC-###-XXX

---

**File Location**: `src/main/resources/db/seed_auth_data.sql`
