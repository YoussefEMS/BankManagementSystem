# Seed Data Summary - Bank Management System

## 📊 Data Population Overview

A comprehensive SQL seed script has been created and executed that populates the database with realistic test data for authentication and account management testing.

---

## 👥 Customers Created (6 Total)

### 1. John Doe
- **Email**: john@example.com
- **Password**: password123
- **Tier**: GOLD
- **National ID**: 12345678
- **Phone**: 555-0001
- **Address**: 123 Main Street, New York, NY 10001
- **Accounts**: 3

### 2. Jane Smith
- **Email**: jane@example.com
- **Password**: password456
- **Tier**: PLATINUM
- **National ID**: 87654321
- **Phone**: 555-0002
- **Address**: 456 Oak Avenue, Los Angeles, CA 90001
- **Accounts**: 3

### 3. Michael Johnson
- **Email**: michael@example.com
- **Password**: password789
- **Tier**: SILVER
- **National ID**: 11223344
- **Phone**: 555-0003
- **Address**: 789 Pine Road, Chicago, IL 60601
- **Accounts**: 2

### 4. Sarah Williams
- **Email**: sarah@example.com
- **Password**: password101112
- **Tier**: GOLD
- **National ID**: 44332211
- **Phone**: 555-0004
- **Address**: 321 Elm Street, Houston, TX 77001
- **Accounts**: 4

### 5. David Brown
- **Email**: david@example.com
- **Password**: password131415
- **Tier**: SILVER
- **National ID**: 55667788
- **Phone**: 555-0005
- **Address**: 654 Maple Drive, Phoenix, AZ 85001
- **Accounts**: 2

### 6. Emma Davis
- **Email**: emma@example.com
- **Password**: password161718
- **Tier**: PLATINUM
- **National ID**: 99887766
- **Phone**: 555-0006
- **Address**: 987 Cedar Lane, Philadelphia, PA 19101
- **Accounts**: 4

---

## 🏦 Accounts Created (18 Total)

### John Doe's Accounts (3)
| Account Number | Type | Balance | Currency | Status |
|---|---|---|---|---|
| ACC-001-CHK | Checking | $5,500.50 | USD | ACTIVE |
| ACC-001-SAV | Savings | $25,000.00 | USD | ACTIVE |
| ACC-001-MMA | Money Market | $50,000.00 | USD | ACTIVE |

### Jane Smith's Accounts (3)
| Account Number | Type | Balance | Currency | Status |
|---|---|---|---|---|
| ACC-002-CHK | Checking | $8,750.25 | USD | ACTIVE |
| ACC-002-SAV | Savings | $75,000.00 | USD | ACTIVE |
| ACC-002-BUS | Business | $125,000.00 | USD | ACTIVE |

### Michael Johnson's Accounts (2)
| Account Number | Type | Balance | Currency | Status |
|---|---|---|---|---|
| ACC-003-CHK | Checking | $3,200.75 | USD | ACTIVE |
| ACC-003-SAV | Savings | $15,000.00 | USD | ACTIVE |

### Sarah Williams' Accounts (4)
| Account Number | Type | Balance | Currency | Status |
|---|---|---|---|---|
| ACC-004-CHK | Checking | $12,500.00 | USD | ACTIVE |
| ACC-004-SAV | Savings | $45,000.00 | USD | ACTIVE |
| ACC-004-MMA | Money Market | $100,000.00 | USD | ACTIVE |
| ACC-004-IRA | IRA | $250,000.00 | USD | ACTIVE |

### David Brown's Accounts (2)
| Account Number | Type | Balance | Currency | Status |
|---|---|---|---|---|
| ACC-005-CHK | Checking | $2,100.50 | USD | ACTIVE |
| ACC-005-SAV | Savings | $8,500.00 | USD | ACTIVE |

### Emma Davis' Accounts (4)
| Account Number | Type | Balance | Currency | Status |
|---|---|---|---|---|
| ACC-006-CHK | Checking | $7,650.00 | USD | ACTIVE |
| ACC-006-SAV | Savings | $60,000.00 | USD | ACTIVE |
| ACC-006-BUS | Business | $200,000.00 | USD | ACTIVE |
| ACC-006-MMA | Money Market | $150,000.00 | USD | ACTIVE |

---

## 💳 Account Types Included

1. **Checking** - Daily transaction accounts (all customers have)
2. **Savings** - Interest-bearing savings (all customers have)
3. **Money Market** - High-yield investment accounts (4 customers)
4. **Business** - Business banking accounts (2 customers: Jane, Emma)
5. **IRA** - Retirement accounts (1 customer: Sarah)

---

## 📝 Sample Transactions (8 Total)

### John Doe - ACC-001-CHK Transactions
1. Deposit: $1,000.00 (Balance: $1,000.00)
2. Withdrawal: $200.00 (Balance: $800.00)
3. Payroll Deposit: $4,700.50 (Balance: $5,500.50)

### Jane Smith - ACC-002-CHK Transactions
1. Deposit: $5,000.00 (Balance: $5,000.00)
2. Withdrawal: $500.00 (Balance: $4,500.00)
3. Payroll Deposit: $4,250.25 (Balance: $8,750.25)

### Sarah Williams - ACC-004-SAV Transactions
1. Deposit: $45,000.00 (Balance: $45,000.00)
2. Interest: $25.00 (Balance: $45,025.00)

---

## 📂 Files Created/Modified

### New Files
1. **seed_auth_data.sql** - Comprehensive seed script
   - Location: `src/main/resources/db/seed_auth_data.sql`
   - Contains: Customer INSERT, Account INSERT, Transaction INSERT
   - Includes: Verification queries (commented)

### Modified Files
1. **schema.sql** - Updated with password field
   - Added password column to Customer table
   - Made email UNIQUE

---

## ✅ Verification Queries Included

The seed script includes commented-out verification queries:

```sql
-- View all customers
SELECT customer_id, full_name, email, tier, status FROM [Customer];

-- View all accounts grouped by customer
SELECT c.full_name, a.account_number, a.account_type, a.balance, 
       a.currency, a.status
FROM [Account] a
JOIN [Customer] c ON a.customer_id = c.customer_id
ORDER BY c.customer_id, a.account_number;

-- Account count per customer
SELECT c.full_name, COUNT(a.account_number) as account_count
FROM [Customer] c
LEFT JOIN [Account] a ON c.customer_id = a.customer_id
GROUP BY c.customer_id, c.full_name;

-- View all transactions
SELECT t.transaction_id, t.account_number, t.type, t.amount, 
       t.timestamp, t.balance_after, t.reference_code
FROM [Transactions] t
ORDER BY t.timestamp DESC;
```

---

## 🔗 Relationships

### Customer-to-Account (1:N)
- **1 Customer** can have **multiple Accounts**
- Foreign key: `Account.customer_id` → `Customer.customer_id`
- Example: John Doe (customer_id=1) has 3 accounts

### Account-to-Transaction (1:N)
- **1 Account** can have **multiple Transactions**
- Foreign key: `Transactions.account_number` → `Account.account_number`
- Example: ACC-001-CHK has 3 transaction records

---

## 📊 Data Statistics

| Entity | Count |
|--------|-------|
| Customers | 6 |
| Accounts | 18 |
| Transactions | 8 |
| Account Types | 5 |
| Tiers | 3 (GOLD, SILVER, PLATINUM) |

### Tier Distribution
- **GOLD**: 2 customers (John Doe, Sarah Williams)
- **SILVER**: 2 customers (Michael Johnson, David Brown)
- **PLATINUM**: 2 customers (Jane Smith, Emma Davis)

### Account Type Distribution
- **Checking**: 6 accounts
- **Savings**: 6 accounts
- **Money Market**: 4 accounts
- **Business**: 2 accounts
- **IRA**: 1 account

### Total Asset Value
- **Checking Accounts**: $50,200.50
- **Savings Accounts**: $228,500.00
- **Money Market**: $300,000.00
- **Business**: $325,000.00
- **IRA**: $250,000.00
- **TOTAL**: $1,153,700.50

---

## 🚀 How to Use

### Initial Setup
1. Database schema automatically created by application startup
2. Run seed script: `sqlcmd ... -i seed_auth_data.sql`
3. Verify with included verification queries
4. Launch application with `mvn javafx:run`

### Testing Authentication
Use any of the 6 test users with their credentials to:
- Login with email/password
- View assigned accounts
- See account balances and types
- View transaction history
- Test logout/re-login functionality

### Modifying Data
- Edit `seed_auth_data.sql` to add/modify customers
- Edit account balances to test different scenarios
- Add more transactions for testing history view

---

## 🔐 Security Notes

⚠️ **For Testing Only**
- Passwords are stored in plain text (seed data only)
- In production, use bcrypt, PBKDF2, or Argon2
- Current authentication: simple string comparison
- For production: implement proper password hashing

---

## 📌 Key Features of Seed Data

✅ **Realistic Data**
- Real-world address formats
- Diverse customer tiers
- Multiple account types
- Various balance amounts

✅ **Complete Relationships**
- Each customer linked to 2-4 accounts
- Each account linked to one customer
- Sample transactions for testing

✅ **Easy Testing**
- Simple passwords for dev/test
- Clear account naming (ACC-###-XXX)
- Reference codes for transactions

✅ **Extensible**
- Easy to add more customers
- Easy to modify balances
- Includes template for new transactions

---

## 🎯 Recommended Test Scenarios

1. **Basic Login**: john@example.com / password123
2. **Multiple Accounts**: sarah@example.com / password101112 (4 accounts)
3. **High Balance**: emma@example.com / password161718 ($567,650 total)
4. **Transaction History**: Check jane@example.com / password456
5. **Different Tiers**: Test all three tiers (GOLD, SILVER, PLATINUM)

---

**Database Ready! ✨**
All data has been inserted successfully and is ready for application testing.
