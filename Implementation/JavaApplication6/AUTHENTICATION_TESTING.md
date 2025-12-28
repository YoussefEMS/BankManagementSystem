# Authentication Testing Guide

## 🎯 Quick Start - Test Credentials

The database has been populated with **6 test customers**, each with multiple accounts.

### Test Users (Email / Password)

| Name | Email | Password | Accounts | Tier |
|------|-------|----------|----------|------|
| **John Doe** | john@example.com | password123 | 3 accounts | GOLD |
| **Jane Smith** | jane@example.com | password456 | 3 accounts | PLATINUM |
| **Michael Johnson** | michael@example.com | password789 | 2 accounts | SILVER |
| **Sarah Williams** | sarah@example.com | password101112 | 4 accounts | GOLD |
| **David Brown** | david@example.com | password131415 | 2 accounts | SILVER |
| **Emma Davis** | emma@example.com | password161718 | 4 accounts | PLATINUM |

---

## 📊 Data Overview

### Customers
✅ 6 customers created with unique emails and passwords
- Status: All ACTIVE
- Tier: Mix of GOLD, SILVER, and PLATINUM
- Full contact info: address, phone, national ID

### Accounts
✅ **18 total accounts** distributed across customers:

| Customer | Account Count | Account Types |
|----------|---|---|
| John Doe | 3 | Checking, Savings, Money Market |
| Jane Smith | 3 | Checking, Savings, Business |
| Michael Johnson | 2 | Checking, Savings |
| Sarah Williams | 4 | Checking, Savings, Money Market, IRA |
| David Brown | 2 | Checking, Savings |
| Emma Davis | 4 | Checking, Savings, Business, Money Market |

### Sample Balances
- **Checking Accounts**: $2,100 - $12,500
- **Savings Accounts**: $8,500 - $75,000
- **Money Market**: $50,000 - $150,000
- **Business**: $125,000 - $200,000
- **IRA**: $250,000

### Transactions
✅ Sample transactions created for:
- John Doe's Checking Account (3 transactions)
- Jane Smith's Checking Account (3 transactions)
- Sarah Williams' Savings Account (2 transactions)

---

## 🧪 Testing Steps

### 1. **Login Screen**
- Launch: `mvn javafx:run`
- You should see the **Login Screen** first
- Enter any of the test credentials above

### 2. **Account Selection**
- After successful login, you'll see **Account Selection Screen**
- Shows: Account Number, Type, Balance, Currency
- Example: `ACC-001-CHK` (Checking) with balance $5500.50

### 3. **Account Navigation**
- Click "View Details" on any account
- Navigate to Transaction History to see sample transactions
- Use Menu Bar to switch between screens

### 4. **Logout**
- Click "Logout" from Account Selection Screen
- Returns to Login Screen
- You can immediately log in with a different account

---

## 🔍 Database Verification

### View All Customers
```sql
SELECT customer_id, full_name, email, tier, status 
FROM [Customer] 
ORDER BY customer_id;
```

### View Accounts by Customer
```sql
SELECT c.full_name, a.account_number, a.account_type, 
       CAST(a.balance AS DECIMAL(10,2)) as balance, a.currency, a.status
FROM [Account] a
JOIN [Customer] c ON a.customer_id = c.customer_id
ORDER BY c.customer_id, a.account_number;
```

### View Transactions
```sql
SELECT t.transaction_id, t.account_number, t.type, t.amount, 
       t.timestamp, t.balance_after, t.reference_code
FROM [Transactions] t
ORDER BY t.timestamp DESC;
```

### Account Count Per Customer
```sql
SELECT c.full_name, COUNT(a.account_number) as account_count
FROM [Customer] c
LEFT JOIN [Account] a ON c.customer_id = a.customer_id
GROUP BY c.customer_id, c.full_name
ORDER BY c.customer_id;
```

---

## 🔐 Password Notes

⚠️ **Important:**
- Passwords are stored in plain text in the seed data (for testing only)
- **In production**, use bcrypt or similar hashing
- Current implementation: Simple string comparison
- Example: `john@example.com` → `password123`

---

## 🚀 Application Flow

```
[Login Screen]
     ↓ (Email: john@example.com, Password: password123)
[Account Selection Screen]
     ↓ (Select ACC-001-CHK)
[Account Balance Screen]
     ↓ (View Details)
[Transaction History]
     ↓
[Menu: Logout]
[Back to Login Screen]
```

---

## 📝 Database Schema

**Relationships:**
- 1 Customer → Many Accounts (1:N)
- 1 Account → Many Transactions (1:N)

**Tables:**
1. **Customer** - 6 records
2. **Account** - 18 records
3. **Transactions** - 8 records (sample data)

---

## ✨ Features Implemented

✅ Email/Password authentication
✅ Session management (AuthContext)
✅ Customer-to-Account relationships
✅ Account selection and viewing
✅ Transaction history display
✅ Logout functionality
✅ Menu-based navigation
✅ Professional UI with custom list renderers

---

## 🎓 Example Login Session

1. **Launch app** → See login screen
2. **Enter credentials**:
   - Email: `sarah@example.com`
   - Password: `password101112`
3. **See Sarah's accounts** (4 total):
   - ACC-004-CHK (Checking) - $12,500
   - ACC-004-SAV (Savings) - $45,000
   - ACC-004-MMA (Money Market) - $100,000
   - ACC-004-IRA (IRA) - $250,000
4. **Select any account** to view details
5. **Click Logout** to return to login
6. **Try another user** - e.g., john@example.com / password123

---

## 📌 Notes

- All accounts are in **USD currency**
- All accounts are in **ACTIVE status**
- All customers are in **ACTIVE status**
- Date/timestamps are auto-generated on insert
- Customer IDs are auto-incremented from 1-6
