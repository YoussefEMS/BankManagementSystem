# 🚀 Bank Management System - Quick Start Guide

Get the application running in **5-10 minutes**.

---

## ⚡ Quick Overview

This is a **JavaFX + Maven + Microsoft SQL Server** banking application with two main features:
- **UC-02**: View Account Balance
- **UC-04**: View Transaction History

---

## 📋 Prerequisites

Before you start, ensure you have:
- ✅ Java 17 or higher installed
- ✅ Microsoft SQL Server running and accessible
- ✅ NetBeans IDE (with Maven) or Maven installed separately
- ✅ SSMS (SQL Server Management Studio) for database setup - **OPTIONAL** (can use sqlcmd)

**Check if Java is installed:**
```bash
java -version
```

**Check if Maven is available:**
```bash
mvn -version
```

---

## 🗄️ Step 1: Create Database (2 minutes)

### Current Configuration
Your `application.properties` is set to:
```properties
jdbc.url=jdbc:sqlserver://localhost:1433;databaseName=bank_management_system
jdbc.user=YoussefEriksen
jdbc.password=22681140!
```

### Option A: Using sqlcmd (Command Line)
```bash
sqlcmd -S localhost -U YoussefEriksen -P 22681140! -Q "CREATE DATABASE bank_management_system"
```

### Option B: Using SSMS (GUI)
1. Open **SQL Server Management Studio**
2. Connect to your SQL Server (with your credentials)
3. Right-click **Databases** → **New Database**
4. **Name**: `bank_management_system`
5. Click **OK**

### Option C: If you have a different server name
If your SQL Server is on a different machine or port, update `application.properties`:
```properties
jdbc.url=jdbc:sqlserver://YOUR_SERVER_NAME:1433;databaseName=bank_management_system;encrypt=true;trustServerCertificate=true
```

---

## 📊 Step 2: Load Database Schema (2 minutes)

### Option A: Using sqlcmd
```bash
sqlcmd -S localhost -U YoussefEriksen -P 22681140! -d bank_management_system -i src\main\resources\db\schema.sql
```

### Option B: Using SSMS
1. In SSMS, select the **bank_management_system** database (from dropdown)
2. Click **New Query**
3. Copy entire contents of `src/main/resources/db/schema.sql`
4. Paste into the query window
5. Click **Execute** (or press F5)

**Expected output**: No errors, messages show tables created

---

## 🌱 Step 3: Load Sample Data (1 minute)

### Option A: Using sqlcmd
```bash
sqlcmd -S localhost -U YoussefEriksen -P 22681140! -d bank_management_system -i src\main\resources\db\seed.sql
```

### Option B: Using SSMS
1. Click **New Query** (in the same database)
2. Copy entire contents of `src/main/resources/db/seed.sql`
3. Paste into the query window
4. Click **Execute** (F5)

**Expected output**: No errors, messages show data inserted

---

## ✅ Step 4: Verify Database Setup (1 minute)

Run these queries to verify everything loaded correctly:

```sql
-- In SSMS or sqlcmd, run these:

-- Check database exists
SELECT DB_NAME();

-- Count records in each table
SELECT 'Customer' as TableName, COUNT(*) as RecordCount FROM [Customer]
UNION ALL
SELECT 'Account', COUNT(*) FROM [Account]
UNION ALL
SELECT 'Transactions', COUNT(*) FROM [Transactions];

-- Check test accounts
SELECT * FROM [Account];
```

**Expected results:**
- Customer table: 3 records
- Account table: 4 records  
- Transactions table: 30 records
- Accounts: ACC001, ACC002, ACC003, ACC004

---

## 🔧 Step 5: Verify Configuration (1 minute)

Make sure your `application.properties` has correct credentials:

```properties
jdbc.url=jdbc:sqlserver://localhost:1433;databaseName=bank_management_system;encrypt=true;trustServerCertificate=true
jdbc.user=YoussefEriksen
jdbc.password=22681140!
jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

✅ If this matches your SQL Server setup, you're ready!

---

## 🚀 Step 6: Run the Application (1 minute)

### Option A: NetBeans (Easiest)
1. **File** → **Open Project**
2. Navigate to: `c:\Users\Youssef\Documents\IAcceptDefeat\JavaApplication6`
3. Select the folder and click **Open Project**
4. Right-click the project → **Run** (or press **F6**)
5. The application window opens

### Option B: Command Line
```bash
mvn clean javafx:run
```

Wait for the window to open (~10-15 seconds on first run).

---

## 🧪 Step 7: Test the Features (2 minutes)

### Test UC-02: View Account Balance
1. Click **Operations** menu → **View Account Balance**
2. Enter account number: `ACC001`
3. Click **View Balance**
4. **Expected result:**
   - Status: ACTIVE
   - Balance: 5000.00
   - Currency: USD

### Test UC-04: View Transaction History
1. Click **Operations** menu → **View Transaction History**
2. Enter account number: `ACC001`
3. Leave date fields empty (or set custom range)
4. Select type filter: **All**
5. Click **Search**
6. **Expected result:**
   - Table shows 10 transactions
   - Most recent first (ordered by timestamp)
   - Columns: Timestamp, Type, Amount, Note, Balance After

### Test Invalid Account (No Error Test)
1. In UC-02, enter: `INVALID`
2. Click **View Balance**
3. **Expected result:** Blank output fields (no error dialog)

---

## 🎉 Done! You're Running!

Your Bank Management System is now up and running on Microsoft SQL Server.

---

## 📚 What To Do Next

### Explore the Application
- Try different accounts: ACC002, ACC003, ACC004
- Try different transaction filters (Deposit, Withdrawal, etc.)
- Try date range filters

### Understand the Code
- Source code in: `src/main/java/com/bms/`
- Database scripts in: `src/main/resources/db/`
- Configuration in: `src/main/resources/application.properties`

### Read Documentation
- **Architecture details**: See [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md)
- **Deployment guide**: See [README.md](README.md)
- **Migration details**: See [MSSQL_MIGRATION.md](MSSQL_MIGRATION.md)
- **Troubleshooting**: See [MSSQL_MIGRATION.md#troubleshooting](MSSQL_MIGRATION.md)

---

## 🆘 Troubleshooting

### "Cannot connect to database"
1. Check SQL Server is running
2. Verify credentials in `application.properties`
3. Verify database exists: `sqlcmd -S localhost -U YoussefEriksen -P 22681140! -Q "SELECT DB_NAME() WHERE DB_ID('bank_management_system') IS NOT NULL"`

### "Cannot find tables"
1. Verify schema.sql was executed
2. Check tables exist: 
   ```sql
   SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dbo'
   ```

### "Connection timeout"
1. Increase timeout in `application.properties`:
   ```properties
   hikari.connectionTimeout=60000
   ```
2. Restart application

### "No transactions found for ACC001"
1. Verify seed.sql was executed
2. Check records: `SELECT COUNT(*) FROM [Transactions] WHERE account_number='ACC001'` (should be 10)

### Maven/Java not found
1. Install Java 17+: https://www.oracle.com/java/technologies/downloads/
2. Install Maven: https://maven.apache.org/download.cgi
3. Add to PATH environment variable

---

## 🔑 Sample Test Data

The database comes pre-loaded with test data:

### Test Accounts
| Account | Customer | Balance | Type |
|---------|----------|---------|------|
| ACC001 | Ahmed Hassan | 5,000.00 | CHECKING |
| ACC002 | Ahmed Hassan | 15,000.00 | SAVINGS |
| ACC003 | Fatima Ali | 3,500.00 | CHECKING |
| ACC004 | Mohamed Ibrahim | 25,000.00 | SAVINGS |

### Test Transactions
- **ACC001**: 10 transactions (deposits, withdrawals, transfers, interest)
- **ACC002**: 8 transactions
- **ACC003**: 6 transactions
- **ACC004**: 6 transactions

---

## 💾 Database Credentials

**Current credentials in application.properties:**
- Server: localhost:1433
- Database: bank_management_system
- Username: YoussefEriksen
- Password: 22681140!

> ⚠️ **Security Note**: Update credentials before production deployment

---

## ⏱️ Time Breakdown

| Step | Time |
|------|------|
| Create database | 1 min |
| Load schema | 1 min |
| Load seed data | 1 min |
| Verify setup | 1 min |
| Check config | 1 min |
| Run app | 1 min |
| Test features | 2 min |
| **TOTAL** | **~8 minutes** |

---

## 🛠️ Additional Commands

### Rebuild from scratch
```bash
mvn clean compile
```

### Run tests (if available)
```bash
mvn test
```

### Package application
```bash
mvn clean package
```

### View Maven version
```bash
mvn --version
```

### Check Java version
```bash
java -version
```

---

## 📖 Documentation Map

| Document | Purpose |
|----------|---------|
| [README.md](README.md) | Complete project documentation |
| [MSSQL_MIGRATION.md](MSSQL_MIGRATION.md) | Detailed MSSQL setup and migration info |
| [PROJECT_STRUCTURE.md](PROJECT_STRUCTURE.md) | Architecture and code structure |
| [MIGRATION_COMPLETE.md](MIGRATION_COMPLETE.md) | Migration overview |
| [MIGRATION_INDEX.md](MIGRATION_INDEX.md) | Navigation guide for all docs |

---

## ✨ Key Features

✅ **Two Working Use Cases**
- View Account Balance (UC-02)
- View Transaction History (UC-04)

✅ **Clean Architecture**
- Strict 3-layer separation (View → Domain → Data)
- No hardcoded SQL or credentials
- Connection pooling with HikariCP

✅ **Security**
- All SQL uses PreparedStatements (SQL injection safe)
- Credentials in properties file (not hardcoded)
- Encrypted database connections

✅ **Data**
- 3 test customers
- 4 test accounts
- 30+ test transactions

---

## 🎯 You're All Set!

**Database**: ✅ Created
**Schema**: ✅ Loaded
**Data**: ✅ Loaded
**Config**: ✅ Updated
**Application**: ✅ Ready to run

**Next action**: Press F6 in NetBeans or run `mvn clean javafx:run`

---

**Status**: 🟢 Ready to Start
**Last Updated**: December 27, 2025
**Questions?**: See troubleshooting section above or review [MSSQL_MIGRATION.md](MSSQL_MIGRATION.md)
