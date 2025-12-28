# Microsoft SQL Server Migration Guide

This document details the migration from PostgreSQL to Microsoft SQL Server (MSSQL) for the Bank Management System project.

## Migration Summary

✅ **Completed Changes:**
- Removed PostgreSQL JDBC driver (org.postgresql:postgresql:42.7.1)
- Added Microsoft JDBC driver (com.microsoft.sqlserver:mssql-jdbc:13.2.1.jre11)
- Updated `application.properties` for MSSQL connection
- Converted database schema to MSSQL syntax
- Updated all DAO SQL statements for MSSQL compatibility
- Maintained strict 3-layer architecture

## Changes Made

### 1. Maven Dependency (pom.xml)

**Removed:**
```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.7.1</version>
</dependency>
```

**Added:**
```xml
<dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>13.2.1.jre11</version>
</dependency>
```

### 2. Database Configuration (application.properties)

**Old (PostgreSQL):**
```properties
jdbc.url=jdbc:postgresql://localhost:5432/bank_management_system
jdbc.user=postgres
jdbc.password=postgres
jdbc.driver=org.postgresql.Driver
```

**New (MSSQL):**
```properties
jdbc.url=jdbc:sqlserver://localhost:1433;databaseName=bank_management_system;encrypt=true;trustServerCertificate=true
jdbc.user=sa
jdbc.password=YourPassword123!
jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

**Key Configuration Parameters:**
- `localhost:1433` - Default SQL Server port
- `databaseName` - Database name (created separately)
- `encrypt=true` - Enable encrypted connections
- `trustServerCertificate=true` - Allow self-signed certs (for local development)

### 3. Database Schema Changes (schema.sql)

#### Data Type Conversions:
| PostgreSQL | MSSQL |
|-----------|--------|
| `SERIAL` | `INT IDENTITY(1,1)` |
| `VARCHAR(n)` | `NVARCHAR(n)` |
| `VARCHAR(500)` | `NVARCHAR(500)` |
| `TEXT` | `NVARCHAR(MAX)` |
| `TIMESTAMP` | `DATETIME2` |
| `BOOLEAN` | `BIT` |
| `DECIMAL(18,2)` | `DECIMAL(18,2)` (unchanged) |

#### Table Name Changes:
- `transaction` → `[Transactions]` (TRANSACTION is a reserved keyword in MSSQL)
- All table names now use square brackets for consistency: `[Customer]`, `[Account]`, `[Transactions]`

#### Drop Table Syntax:
```sql
-- PostgreSQL
DROP TABLE IF EXISTS transaction CASCADE;

-- MSSQL
IF OBJECT_ID('Transactions', 'U') IS NOT NULL DROP TABLE [Transactions];
```

#### Default Values:
```sql
-- PostgreSQL
date_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP

-- MSSQL
date_created DATETIME2 DEFAULT SYSDATETIME()
```

#### Constraints and Indexes:
- Primary Keys: `INT IDENTITY(1,1) PRIMARY KEY` (MSSQL syntax)
- Foreign Keys: Remain the same, with bracket-quoted table names
- Indexes: Syntax unchanged

### 4. SQL Data Insertion Changes (seed.sql)

#### Date/Time Format:
```sql
-- ISO 8601 format compatible with DATETIME2
'2023-01-15T00:00:00'    -- Format: YYYY-MM-DDTHH:MM:SS
'2024-12-01T10:00:00'
```

#### Table References:
All INSERT statements updated to use bracketed table names:
```sql
INSERT INTO [Customer] (...) VALUES (...)
INSERT INTO [Account] (...) VALUES (...)
INSERT INTO [Transactions] (...) VALUES (...)
```

### 5. DAO Updates

#### AccountDAO.java
**Before:**
```java
"FROM account WHERE account_number = ?"
"UPDATE account SET balance = ? WHERE account_number = ?"
```

**After:**
```java
"FROM [Account] WHERE account_number = ?"
"UPDATE [Account] SET balance = ? WHERE account_number = ?"
```

#### TransactionDAO.java
**Before:**
```java
"FROM transaction WHERE account_number = ?"
```

**After:**
```java
"FROM [Transactions] WHERE account_number = ?"
```

**Note:** JDBC `?` placeholders remain unchanged. All PreparedStatement operations work identically with MSSQL.

## Setup Instructions for MSSQL

### Option 1: Command Line (sqlcmd)

```bash
# Create database
sqlcmd -S localhost -U sa -P YourPassword123! -Q "CREATE DATABASE bank_management_system"

# Run schema
sqlcmd -S localhost -U sa -P YourPassword123! -d bank_management_system -i src\main\resources\db\schema.sql

# Run seed data
sqlcmd -S localhost -U sa -P YourPassword123! -d bank_management_system -i src\main\resources\db\seed.sql
```

### Option 2: SQL Server Management Studio (SSMS)

1. Open SSMS and connect to your SQL Server instance
2. New Query → Execute:
   ```sql
   CREATE DATABASE bank_management_system;
   ```
3. Select the database from the dropdown (or switch with `USE bank_management_system`)
4. Copy entire contents of `schema.sql` and execute
5. Copy entire contents of `seed.sql` and execute

### Option 3: Visual Studio Code with MSSQL Extension

1. Install "mssql" extension in VS Code
2. Connect to your SQL Server instance
3. Open and execute both SQL files

### Verification

After setup, verify the migration with these queries:

```sql
-- Check tables exist
SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'dbo';

-- Check Customer table
SELECT TOP 5 * FROM [Customer];

-- Check Account table
SELECT TOP 5 * FROM [Account];

-- Check Transactions table (should have 30+ records)
SELECT COUNT(*) FROM [Transactions];
SELECT TOP 10 * FROM [Transactions] ORDER BY timestamp DESC;

-- Verify key accounts
SELECT * FROM [Account] WHERE account_number IN ('ACC001', 'ACC002', 'ACC003', 'ACC004');
```

## Running the Application

### 1. Update application.properties

Replace the default MSSQL credentials:
```properties
jdbc.url=jdbc:sqlserver://YOUR_SERVER:1433;databaseName=bank_management_system;encrypt=true;trustServerCertificate=true
jdbc.user=your_username
jdbc.password=your_password
jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

### 2. Open in NetBeans

1. File → Open Project
2. Select project directory
3. NetBeans auto-detects Maven project

### 3. Run the Application

```bash
# Option A: NetBeans (F6) - Executes mvn clean javafx:run
# Option B: Terminal
mvn clean javafx:run
```

## Testing the Use Cases

### UC-02: View Account Balance
1. Run the application
2. Click "Operations" → "View Account Balance"
3. Enter account number: `ACC001`
4. Click "View Balance"
5. Should display: Status = ACTIVE, Balance = 5000.00, Currency = USD

### UC-04: View Transaction History
1. Click "Operations" → "View Transaction History"
2. Enter account number: `ACC001`
3. Leave dates blank or set custom date range
4. Select filter (or "All" for all transactions)
5. Click "Search"
6. Should display 10 transactions for ACC001 (most recent first)

### Testing Not-Found Cases
1. UC-02: Enter invalid account `INVALID` → Shows blank balance (no error)
2. UC-04: Enter invalid account `INVALID` → Shows empty table (no error)

## Architecture Compliance

✅ **3-Layer Architecture Maintained:**
- **View Layer**: AccountBalanceScreen, TransactionHistoryScreen (no SQL)
- **Domain Layer**: AccountBalanceController, TransactionHistoryController (business logic)
- **Persistence Layer**: AccountDAO, TransactionDAO (all JDBC/SQL)

✅ **No Breaking Changes:**
- Entity classes remain unchanged
- Controller logic remains unchanged
- UI behavior remains unchanged
- PreparedStatements still prevent SQL injection
- HikariCP connection pooling still active

## Troubleshooting

### Connection Issues

**Error: "Login failed"**
- Check MSSQL server is running
- Verify username/password in `application.properties`
- Ensure SQL Server TCP/IP protocol is enabled

**Error: "Cannot open database"**
- Database not created
- Run schema.sql to create database and tables
- Check `databaseName` parameter matches created database name

### SQL Execution Issues

**Error: "Incorrect syntax"**
- Verify you used `schema.sql` (MSSQL version, not PostgreSQL)
- Check table names have brackets: `[Transactions]`, not `transaction`

**Error: "Invalid column name"**
- Verify seed.sql was run after schema.sql
- Check data types match between schema and seed inserts

### Application Issues

**Error: "Cannot find database"**
- Update `application.properties` with correct server name/database name
- Ensure connectivity to MSSQL server

**Error: "No tables found"**
- Re-run both schema.sql and seed.sql
- Use SSMS to verify tables exist

## Migration Checklist

- ✅ Replaced PostgreSQL JDBC with MSSQL JDBC in pom.xml
- ✅ Updated JDBC URL format for MSSQL
- ✅ Converted data types (SERIAL→IDENTITY, VARCHAR→NVARCHAR, etc.)
- ✅ Renamed `transaction` table to `[Transactions]`
- ✅ Updated all table/column references with brackets
- ✅ Converted date/time functions (CURRENT_TIMESTAMP→SYSDATETIME)
- ✅ Updated all DAO SQL statements
- ✅ Verified 3-layer architecture maintained
- ✅ Tested UC-02 and UC-04 functionality
- ✅ Updated README documentation
- ✅ Created this migration guide

## Additional Resources

- [Microsoft JDBC Driver for SQL Server Documentation](https://docs.microsoft.com/en-us/sql/connect/jdbc/microsoft-jdbc-driver-for-sql-server)
- [SQL Server Connection String Syntax](https://learn.microsoft.com/en-us/sql/connect/jdbc/building-the-connection-url)
- [MSSQL Data Types](https://docs.microsoft.com/en-us/sql/t-sql/data-types/data-types-transact-sql)
- [IDENTITY Property in MSSQL](https://learn.microsoft.com/en-us/sql/t-sql/statements/create-table-transact-sql-identity-property)

## Support

For issues or questions about the migration:
1. Check the troubleshooting section above
2. Review the original PostgreSQL and MSSQL schema files
3. Verify MSSQL server connectivity and credentials
4. Check application logs (via Logback configuration)

---

**Last Updated**: December 27, 2025
**Status**: Migration Complete ✅
