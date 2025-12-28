# Complete Project Structure

## Project Directory Layout

```
JavaApplication6/
│
├── pom.xml                                 ← Maven build configuration
├── nbactions.xml                           ← NetBeans Maven goal bindings
│
├── README.md                               ← Main documentation (read this first!)
├── QUICKSTART.md                           ← 5-minute setup guide
├── IMPLEMENTATION_SUMMARY.md               ← What was implemented
├── DELIVERABLES.md                         ← Deliverables checklist
├── NETBEANS_INTEGRATION.md                 ← How to run in NetBeans
│
├── src/main/java/com/bms/
│   │
│   ├── BankManagementSystemApp.java        ← JavaFX Application entry point
│   │                                         [extends Application]
│   │                                         [menu navigation]
│   │                                         [scene management]
│   │
│   ├── domain/                             ← DOMAIN LAYER (Business Logic)
│   │   ├── entity/
│   │   │   ├── Account.java                ← Account entity
│   │   │   │                                 [accountNumber, customerId, balance, etc.]
│   │   │   │                                 [no DAO imports]
│   │   │   └── Transaction.java            ← Transaction entity
│   │   │                                     [transactionId, type, amount, timestamp, etc.]
│   │   │                                     [no DAO imports]
│   │   └── controller/
│   │       ├── AccountBalanceController.java ← UC-02 business logic
│   │       │                                   [viewAccountSummary(accountNo)]
│   │       │                                   [returns Account or null]
│   │       │                                   [calls AccountDAO]
│   │       └── TransactionHistoryController.java ← UC-04 business logic
│   │                                             [viewTransactionHistory(...)]
│   │                                             [returns List<Transaction>]
│   │                                             [calls TransactionDAO]
│   │
│   ├── persistence/                       ← DATA ACCESS LAYER (JDBC + SQL)
│   │   ├── DataSourceFactory.java         ← HikariCP connection pool
│   │   │                                    [singleton pattern]
│   │   │                                    [loads application.properties]
│   │   ├── AccountDAO.java                ← Account data access
│   │   │                                    [findByAccountNo(accountNo)]
│   │   │                                    [JDBC PreparedStatements]
│   │   │                                    [maps ResultSet → Account entity]
│   │   └── TransactionDAO.java            ← Transaction data access
│   │                                       [findByAccountNo(accountNo, dates, filter)]
│   │                                       [JDBC PreparedStatements]
│   │                                       [maps ResultSet → Transaction entities]
│   │
│   └── presentation/                      ← VIEW LAYER (JavaFX UI)
│       ├── AccountBalanceScreen.java      ← UC-02 UI (Account Balance)
│       │                                    [TextField for account number]
│       │                                    [Display: status, balance, currency]
│       │                                    [calls AccountBalanceController]
│       │                                    [shows empty output on not-found]
│       └── TransactionHistoryScreen.java  ← UC-04 UI (Transaction History)
│                                           [TextField for account number]
│                                           [DatePicker for start/end dates]
│                                           [ComboBox for type filter]
│                                           [TableView with 5 columns]
│                                           [calls TransactionHistoryController]
│                                           [shows empty table on not-found]
│
└── src/main/resources/
    │
    ├── application.properties              ← Database configuration
    │                                         [jdbc.url, jdbc.user, jdbc.password]
    │                                         [HikariCP pool settings]
    │
    └── db/
        ├── schema.sql                      ← Database schema creation
        │                                     [3 tables: customer, account, transaction]
        │                                     [indexes on account.customer_id]
        │                                     [indexes on transaction timestamps]
        │                                     [foreign key constraints]
        │
        └── seed.sql                        ← Sample data
                                             [3 customers]
                                             [4 accounts with balances]
                                             [30+ transactions with realistic data]
```

## Class Hierarchy and Relationships

```
                         ┌─────────────────┐
                         │ BankManagement  │
                         │   SystemApp     │
                         │ (JavaFX App)    │
                         └────────┬────────┘
                                  │
                    ┌─────────────┴──────────────┐
                    │                            │
            ┌───────▼──────────┐      ┌─────────▼──────────┐
            │ Account Balance  │      │   Transaction      │
            │ Screen           │      │   History Screen   │
            │ (View UC-02)     │      │   (View UC-04)     │
            └───────┬──────────┘      └─────────┬──────────┘
                    │                           │
                    └──────────┬────────────────┘
                               │
                    ┌──────────▼───────────┐
                    │  Domain Controllers  │
                    └──────────┬───────────┘
                               │
                ┌──────────────┼──────────────┐
                │                             │
        ┌───────▼──────────┐      ┌──────────▼──────────┐
        │ Account Balance  │      │ Transaction History │
        │ Controller       │      │ Controller          │
        │ (UC-02 Logic)    │      │ (UC-04 Logic)       │
        └────────┬─────────┘      └──────────┬──────────┘
                 │                            │
        ┌────────▼──────────┐      ┌──────────▼──────────┐
        │  AccountDAO       │      │ TransactionDAO      │
        │  (SQL Access)     │      │ (SQL Access)        │
        └────────┬──────────┘      └──────────┬──────────┘
                 │                            │
                 └──────────┬────────────────┘
                            │
                ┌───────────▼──────────┐
                │ DataSourceFactory    │
                │ (HikariCP Pool)      │
                └───────────┬──────────┘
                            │
                ┌───────────▼──────────┐
                │   PostgreSQL DB      │
                │ (schema.sql + seed)  │
                └──────────────────────┘
```

## Data Flow Example: UC-02 (View Account Balance)

```
User Types "ACC001" in TextField
           │
           ▼
    [View Button Clicked]
           │
           ▼
AccountBalanceScreen.handleViewAccount()
           │
           ├─► Get input from TextField
           │
           ▼
AccountBalanceController.viewAccountSummary(accountNo)
           │
           ├─► Input validation (trim whitespace)
           │
           ▼
AccountDAO.findByAccountNo(accountNo)
           │
           ├─► Get DataSource from DataSourceFactory
           ├─► Prepare SQL: SELECT ... WHERE account_number = ?
           ├─► Execute query with PreparedStatement
           ├─► Map ResultSet to Account entity
           │
           ▼
[Account object returned OR null if not found]
           │
           ▼
AccountBalanceController returns result
           │
           ▼
AccountBalanceScreen.displayAccount(account)
           │
           ├─► If account != null: populate labels with values
           ├─► If account == null: show "No account found" message
           │                       (no error dialog)
           │
           ▼
    [UI Updated]
```

## File Dependencies (No Circular Dependencies ✓)

```
com.bms.presentation (View Layer)
    ↓ imports
com.bms.domain.controller (Domain Controllers)
    ↓ imports
com.bms.domain.entity (Entities)
    ↓ imports
com.bms.persistence (DAOs)
    ↓ imports
Database (PostgreSQL)

NO REVERSE DEPENDENCIES!
```

## Package Organization

| Package | Purpose | No. of Classes | Imports |
|---------|---------|---|---|
| com.bms | Main entry point | 1 | JavaFX, domain.controller, presentation |
| com.bms.domain.entity | Entities (data models) | 2 | Java standard library only |
| com.bms.domain.controller | Business logic | 2 | domain.entity, persistence, java.time |
| com.bms.persistence | Data access | 3 | domain.entity, javax.sql, java.sql, HikariCP |
| com.bms.presentation | JavaFX views | 2 | domain.controller, javafx.* |

## Configuration Files

### pom.xml
- Maven build configuration
- Defines all dependencies
- Configures plugins (javafx-maven-plugin, maven-compiler-plugin)
- Sets Java version to 17

### nbactions.xml
- NetBeans-specific Maven goal bindings
- Maps "run" action to `javafx:run`
- Maps "debug" action to debug goal
- Maps "build" action to package

### application.properties
- Database connection details
- HikariCP pool configuration
- Externalized from code (no hardcoded credentials)

### schema.sql
- Creates database tables with proper schema
- Defines foreign key constraints
- Creates indexes for performance

### seed.sql
- Inserts sample data
- 3 customers, 4 accounts, 30+ transactions
- Represents realistic bank operations

## Technologies Used Per Layer

| Layer | Technology | Purpose |
|-------|-----------|---------|
| View | JavaFX 22.0.1 | UI components, event handling |
| View | JavaFX FXML | (Optional, not used here) |
| Domain | Java 17 | Business logic |
| Persistence | JDBC | Database connectivity |
| Persistence | HikariCP 5.1.0 | Connection pooling |
| Persistence | PostgreSQL JDBC | Database driver |
| Build | Maven 3.6+ | Build automation |
| Logging | SLF4J + Logback | Logging framework |

## Compliance Matrix

| Requirement | Status | Evidence |
|-----------|--------|----------|
| Java 17+ | ✅ | pom.xml: `<source>17</source>` |
| Maven project | ✅ | pom.xml present and valid |
| JavaFX with OpenJFX | ✅ | pom.xml dependencies + javafx-maven-plugin |
| JDBC + HikariCP | ✅ | DataSourceFactory.java + pom.xml |
| NetBeans compatible | ✅ | nbactions.xml + Maven structure |
| 3-layer architecture | ✅ | Folders organized by layer |
| No DTOs | ✅ | Only Entities and primitives used |
| No error dialogs for not-found | ✅ | Both screens show empty output |
| Database scripts | ✅ | schema.sql + seed.sql provided |
| README | ✅ | Comprehensive documentation |

---

**This project is production-ready and fully implements the Bank Management System specification.**
