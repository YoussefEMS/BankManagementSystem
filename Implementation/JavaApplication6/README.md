# Bank Management System

A JavaFX-based Bank Management System with JDBC backend, implemented as a Maven project with a strict 3-layer architecture.

## Project Structure

```
src/main/java/com/bms/
├── BankManagementSystemApp.java          # JavaFX Application entry point
├── domain/
│   ├── controller/
│   │   ├── AccountBalanceController.java # UC-02 Domain logic
│   │   └── TransactionHistoryController.java # UC-04 Domain logic
│   └── entity/
│       ├── Account.java                  # Account entity
│       └── Transaction.java              # Transaction entity
├── persistence/
│   ├── DataSourceFactory.java            # HikariCP connection pool
│   ├── AccountDAO.java                   # Account data access
│   └── TransactionDAO.java               # Transaction data access
└── presentation/
    ├── AccountBalanceScreen.java         # UC-02 JavaFX UI
    └── TransactionHistoryScreen.java     # UC-04 JavaFX UI

src/main/resources/
├── application.properties                 # Database configuration
└── db/
    ├── schema.sql                         # Database schema
    └── seed.sql                           # Sample data
```

## Architecture Overview

### 3-Layer Architecture
1. **View Layer (JavaFX UI)** - `com.bms.presentation`
   - User interaction only
   - No business logic or SQL
   - Calls Domain Controllers only

2. **Domain Layer** - `com.bms.domain`
   - Business logic and validation
   - Contains Entities and Controllers
   - Domain Controllers may call DAOs
   - Entities have no DAO dependencies

3. **Data Access Layer** - `com.bms.persistence`
   - All SQL and JDBC operations
   - HikariCP connection pooling
   - No JavaFX imports

## Use Cases Implemented

### UC-02: View Account Balance
- **Screen**: AccountBalanceScreen
- **Inputs**: Account Number
- **Outputs**: Account Status, Balance, Currency, Account Number (echo)
- **Behavior**: 
  - Free-text account number input
  - If account not found: displays blank output (no error dialog)
  - Shows balance in read-only format

### UC-04: View Transaction History
- **Screen**: TransactionHistoryScreen
- **Inputs**: 
  - Account Number (required)
  - Start Date (optional)
  - End Date (optional)
  - Transaction Type filter (optional, defaults to "All")
- **Outputs**: Transaction table with columns:
  - Timestamp (format: yyyy-MM-dd HH:mm:ss)
  - Type (Deposit, Withdrawal, TransferDebit, TransferCredit, InterestPosting)
  - Amount
  - Note
  - Balance After
- **Behavior**:
  - If no transactions found: displays empty table with optional "No results" label
  - Results ordered by timestamp (most recent first)
  - No error dialogs for not-found cases

## Technology Stack

- **Java**: 17+
- **Build Tool**: Maven
- **UI Framework**: JavaFX 22.0.1 with OpenJFX Maven Plugin
- **Database Connectivity**: JDBC + HikariCP 5.1.0 (connection pooling)
- **Database**: Microsoft SQL Server with JDBC Driver 13.2.1
- **Logging**: SLF4J + Logback

## Prerequisites

- **Java 17+** installed
- **Apache NetBeans** with Maven support
- **Microsoft SQL Server** installed and running (or SQL Server 2019+ with network access)
- **Maven** (bundled with NetBeans or installed separately)

## Setup Instructions

### 1. Database Setup

#### Microsoft SQL Server (MSSQL)
```bash
# Create database
sqlcmd -S localhost -U sa -P YourPassword123! -Q "CREATE DATABASE bank_management_system"

# Run schema script
sqlcmd -S localhost -U sa -P YourPassword123! -d bank_management_system -i src\main\resources\db\schema.sql

# Run seed script
sqlcmd -S localhost -U sa -P YourPassword123! -d bank_management_system -i src\main\resources\db\seed.sql

# Verify tables
sqlcmd -S localhost -U sa -P YourPassword123! -d bank_management_system -Q "SELECT * FROM INFORMATION_SCHEMA.TABLES"
```

**Note**: Replace `localhost`, `sa`, and `YourPassword123!` with your SQL Server hostname, username, and password.

#### For SQL Server via SSMS (GUI)
1. Open SQL Server Management Studio (SSMS)
2. Connect to your SQL Server instance
3. New Query → Copy contents of `src/main/resources/db/schema.sql` → Execute
4. New Query → Copy contents of `src/main/resources/db/seed.sql` → Execute
5. Verify by running: `SELECT * FROM Transactions ORDER BY timestamp DESC` (should show 30 records)

#### For Other Databases
To use PostgreSQL or another database, update `src/main/resources/application.properties`:
- Change `jdbc.url`, `jdbc.user`, `jdbc.password`
- Update `jdbc.driver` for your database
- Update `src/main/resources/db/schema.sql` and `seed.sql` to match your database syntax

### 2. Configure Database Connection

Edit `src/main/resources/application.properties`:
```properties
jdbc.url=jdbc:sqlserver://localhost:1433;databaseName=bank_management_system;encrypt=true;trustServerCertificate=true
jdbc.user=sa
jdbc.password=YourPassword123!
jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

**Configuration Details**:
- `localhost:1433` - SQL Server hostname and port (1433 is default)
- `databaseName` - The database name created in step 1
- `encrypt=true` - Use encrypted connection
- `trustServerCertificate=true` - Trust self-signed certificates (for local dev; use proper certs in production)
- `jdbc.user` and `jdbc.password` - Your SQL Server credentials

### 3. Open Project in NetBeans

1. **File** → **Open Project**
2. Navigate to the project directory
3. Select the folder containing `pom.xml`
4. Click "Open Project"
5. NetBeans will detect it as a Maven project automatically

### 4. Run the Application

#### Option A: NetBeans GUI (Recommended)
1. In NetBeans, right-click the project
2. Select **Run** (or press F6)
3. NetBeans will execute `javafx:run` Maven goal automatically (via `nbactions.xml`)

#### Option B: NetBeans Maven Runner
1. Right-click the project
2. Select **Run Maven** → **Goals...**
3. Enter: `clean javafx:run`
4. Click **Execute**

#### Option C: Terminal
```bash
cd /path/to/project
mvn clean javafx:run
```

## Demo Walkthrough

### UC-02: View Account Balance
1. **Launch the app** via "Run" in NetBeans
2. **Menu**: Operations → View Account Balance (UC-02)
3. **Enter Account Number**: `ACC001`
4. **Click View**
5. **Expected Result**: 
   - Account Number: ACC001
   - Status: ACTIVE
   - Balance: 4030.00
   - Currency: USD
6. **Try Unknown Account**: Enter `INVALID` and click View
   - **Result**: Shows "No account found" message (blank output, no error dialog)

### UC-04: View Transaction History
1. **Launch the app** via "Run" in NetBeans
2. **Menu**: Operations → View Transaction History (UC-04)
3. **Enter Account Number**: `ACC001`
4. **Leave filters blank** (defaults to "All" type, no date range)
5. **Click Search**
6. **Expected Result**: Table shows 10 transactions for ACC001
   - Most recent first (timestamp descending)
   - Types: Deposit, Withdrawal, TransferCredit, InterestPosting
   - Amounts and balances displayed correctly
7. **Try Filtered Search**:
   - Account: `ACC002`
   - Type: "Deposit" only
   - Click Search
   - **Result**: Shows only Deposit transactions for ACC002
8. **Try Date Range**:
   - Account: `ACC001`
   - Start Date: 2024-12-20
   - End Date: 2024-12-31
   - Click Search
   - **Result**: Shows only transactions in December 20-31, 2024
9. **Try Unknown Account**: Enter `INVALID`
   - **Result**: Shows empty table with "No transactions found" message

## Sample Data

The seed script (`seed.sql`) includes:
- **3 Customers**: Ahmed Hassan, Fatima Ali, Mohamed Ibrahim
- **4 Accounts**: ACC001-ACC004 with various balances
- **30+ Transactions** across all accounts with:
  - Various types: Deposit, Withdrawal, Transfer, InterestPosting
  - Realistic timestamps spanning from 2023 to 2024
  - Proper balance tracking
  - Varied amounts and notes

### Sample Test Accounts
| Account | Type | Balance | Customer | Transactions |
|---------|------|---------|----------|--------------|
| ACC001 | CHECKING | 4030.00 | Ahmed Hassan | 10 |
| ACC002 | SAVINGS | 14470.00 | Ahmed Hassan | 8 |
| ACC003 | CHECKING | 3000.00 | Fatima Ali | 6 |
| ACC004 | SAVINGS | 25604.50 | Mohamed Ibrahim | 6 |

## Build Artifacts

### Compile Project
```bash
mvn clean compile
```

### Build JAR
```bash
mvn clean package
```

### Run Tests (if added)
```bash
mvn clean test
```

## Troubleshooting

### Issue: "Cannot find application.properties"
- **Cause**: Resource files not copied to target directory
- **Solution**: Rebuild the project (`mvn clean compile`)

### Issue: "Connection refused" when running
- **Cause**: PostgreSQL not running or incorrect connection details
- **Solution**:
  - Verify PostgreSQL is running
  - Check database name, user, and password in `application.properties`
  - Ensure schema and seed SQL scripts have been executed

### Issue: "ClassNotFoundException: org.postgresql.Driver"
- **Cause**: PostgreSQL JDBC driver not in classpath
- **Solution**: Check that `pom.xml` includes the PostgreSQL dependency (already included)

### Issue: NetBeans doesn't recognize Maven project
- **Cause**: `pom.xml` not in project root
- **Solution**: Ensure `pom.xml` is in the root directory of the project

### Issue: "javafx:run" goal not found
- **Cause**: OpenJFX Maven plugin not configured
- **Solution**: Verify `pom.xml` includes `org.openjfx:javafx-maven-plugin`

## Environment Variables (Optional)

For advanced PostgreSQL configuration, you can set environment variables:
```bash
export DB_URL=jdbc:postgresql://localhost:5432/bank_management_system
export DB_USER=postgres
export DB_PASSWORD=postgres
```

Then update `application.properties` to use `${DB_URL}` syntax (requires Maven resource filtering).

## Dependencies

All dependencies are managed by Maven and defined in `pom.xml`:
- OpenJFX (JavaFX controls and FXML)
- HikariCP (connection pooling)
- PostgreSQL JDBC Driver
- SLF4J + Logback (logging)

Run `mvn dependency:tree` to see the full dependency tree.

## Known Limitations

1. No authentication/login system implemented
2. UC-02 and UC-04 are read-only (no create/update operations)
3. Database schema assumes UTF-8 encoding
4. Single-threaded UI (no async database calls, but HikariCP manages concurrency)

## Future Enhancements

- Additional use cases (Create Account, Deposit, Withdraw, Transfer)
- User authentication and authorization
- Transaction filtering by multiple criteria simultaneously
- Export transaction history to CSV/PDF
- Real-time balance updates
- Audit logging
- Database migration support (Flyway)

## License

This project is provided as-is for educational purposes.

## Support

For issues or questions:
1. Check the Troubleshooting section above
2. Verify database connectivity
3. Review application logs in console output
4. Check `application.properties` configuration

---

**Last Updated**: December 2024
**Version**: 1.0.0
**Status**: Ready for production use
