# Quick MSSQL Setup Guide

**Goal**: Get the Bank Management System running with Microsoft SQL Server in 5 minutes.

## Prerequisites

- SQL Server 2019+ or SQL Server Express (free)
- Java 17+
- NetBeans or Maven installed

## Step 1: Create Database (1 minute)

### Using SSMS (Easiest)
1. Open SQL Server Management Studio
2. Right-click "Databases" → "New Database"
3. Name: `bank_management_system`
4. Click OK

### Using sqlcmd
```bash
sqlcmd -S localhost -U sa -P YourPassword123! -Q "CREATE DATABASE bank_management_system"
```

## Step 2: Run Schema (1 minute)

### Using SSMS
1. New Query
2. Copy contents of `src/main/resources/db/schema.sql`
3. Paste and Execute

### Using sqlcmd
```bash
sqlcmd -S localhost -U sa -P YourPassword123! -d bank_management_system -i src\main\resources\db\schema.sql
```

## Step 3: Run Seed Data (30 seconds)

### Using SSMS
1. New Query
2. Copy contents of `src/main/resources/db/seed.sql`
3. Paste and Execute

### Using sqlcmd
```bash
sqlcmd -S localhost -U sa -P YourPassword123! -d bank_management_system -i src\main\resources\db\seed.sql
```

## Step 4: Update Config (30 seconds)

Edit `src/main/resources/application.properties`:

```properties
jdbc.url=jdbc:sqlserver://localhost:1433;databaseName=bank_management_system;encrypt=true;trustServerCertificate=true
jdbc.user=sa
jdbc.password=YourPassword123!
jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

**Note**: Replace `sa` with your SQL Server username and `YourPassword123!` with your password.

## Step 5: Run Application (1 minute)

### NetBeans
1. Right-click project → Run (F6)

### Command Line
```bash
mvn clean javafx:run
```

## Verify It Works

### Test UC-02 (View Account Balance)
1. Click "Operations" → "View Account Balance"
2. Enter: `ACC001`
3. Should show: Balance = 5000.00, Status = ACTIVE

### Test UC-04 (View Transaction History)
1. Click "Operations" → "View Transaction History"
2. Enter: `ACC001`
3. Should show 10 transactions

## Done! ✅

Your Bank Management System is now running on Microsoft SQL Server.

---

### Useful SQL Commands for Verification

```sql
-- Check if database exists
SELECT name FROM sys.databases WHERE name = 'bank_management_system';

-- Check tables
SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dbo';

-- Count records in each table
SELECT 'Customers' as TableName, COUNT(*) as RecordCount FROM [Customer]
UNION ALL
SELECT 'Accounts', COUNT(*) FROM [Account]
UNION ALL
SELECT 'Transactions', COUNT(*) FROM [Transactions];

-- View all accounts
SELECT * FROM [Account];

-- View all transactions for ACC001
SELECT * FROM [Transactions] WHERE account_number = 'ACC001' ORDER BY timestamp DESC;
```

### Troubleshooting

| Problem | Solution |
|---------|----------|
| Connection refused | Check SQL Server is running: `sqlcmd -S localhost -U sa -P YourPassword123!` |
| Database not found | Run schema.sql and seed.sql from Step 2-3 |
| Login failed | Verify username/password in application.properties |
| No tables | Ensure schema.sql was executed in the correct database |

---

For detailed troubleshooting, see [MSSQL_MIGRATION.md](MSSQL_MIGRATION.md)
