# Bank Management System

A JavaFX desktop banking application built with Java 17, Maven, JDBC, and Microsoft SQL Server.

This repository contains a course project that models common retail banking workflows for both customers and administrators, including account access, transactions, loan processing, interest posting, and overdraft monitoring. The codebase also demonstrates several object-oriented design patterns in a working application.

## Overview

The application is split into three main areas:

- `presentation`: JavaFX screens and navigation
- `domain`: business logic, controllers, entities, and pattern-based workflow components
- `persistence`: DAO abstractions, SQL Server integration, datasource setup, and database-adapter infrastructure

The main entry point is `com.bms.BankManagementSystemApp`.

## Implemented Features

### Customer workflows

- Login and role-based routing
- Account selection
- Account balance viewing
- Transaction history viewing
- Transfer between accounts
- Loan application submission
- Loan status viewing
- Loan catalog comparison

### Admin workflows

- Admin dashboard
- Customer profile creation
- Cash deposit
- Cash withdrawal
- Account status updates
- Loan review and decision handling
- Monthly interest posting
- Overdraft alert viewing

## Design Patterns Used

The project is not only a banking application, but also a design-pattern implementation exercise. The codebase currently includes:

- Abstract Factory for screen creation and DAO creation
- Factory Method for transaction creation
- Singleton for shared runtime infrastructure
- Bridge for interest posting and loan-application processing
- Adapter for database dialect abstraction and payment gateway integration
- Decorator for account-info and transaction-processing enhancements
- Flyweight for loan product catalog sharing

Supporting documentation for the structural patterns is included in:

- [structural_design_patterns_report.md](/c:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/structural_design_patterns_report.md)
- [structural_design_patterns_report.html](/c:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/structural_design_patterns_report.html)

## Tech Stack

- Java 17
- JavaFX 22
- Maven
- JDBC
- Microsoft SQL Server
- HikariCP
- SLF4J + Logback

## Repository Layout

```text
src/main/java/com/bms
├── BankManagementSystemApp.java
├── domain
│   ├── controller
│   ├── decorator
│   ├── entity
│   └── flyweight
├── payment
├── persistence
│   └── adapter
└── presentation

src/main/resources
├── application.properties
└── db
    ├── schema.sql
    └── seed_auth_data.sql
```

## Prerequisites

Before running the project, make sure you have:

- Java 17 or newer
- Maven 3.8+ (recommended)
- Microsoft SQL Server running locally or remotely
- A database named `bank_management_system`, or an updated JDBC URL that points to your chosen database

## Database Setup

The project is currently configured for SQL Server.

1. Create a SQL Server database.
2. Run [schema.sql](/c:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/resources/db/schema.sql).
3. Run [seed_auth_data.sql](/c:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/resources/db/seed_auth_data.sql).
4. Update [application.properties](/c:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/resources/application.properties) with your local connection details.

The seed script inserts demo customers, admin users, accounts, transactions, transfers, overdraft events, and loans.

## Configuration

Database settings live in [application.properties](/c:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/resources/application.properties).

Expected settings:

```properties
jdbc.url=jdbc:sqlserver://HOST:PORT;databaseName=bank_management_system;encrypt=true;trustServerCertificate=true
jdbc.user=your_username
jdbc.password=your_password
jdbc.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
```

The repository currently contains environment-specific values in `application.properties`. Replace them before running the project on your machine, and do not reuse them in a public or shared environment.

## Running the Application

From the project root:

```bash
mvn clean javafx:run
```

The `pom.xml` defaults the JavaFX platform classifier to `win`. If you are building on another platform, override it explicitly:

```bash
mvn clean javafx:run -Djavafx.platform=linux
```

or:

```bash
mvn clean javafx:run -Djavafx.platform=mac
```

## Building

To build the project:

```bash
mvn clean package
```

The Maven build uses the Shade plugin to produce a runnable packaged JAR in `target/`.

## Current Backend Support

The persistence layer includes adapter infrastructure for:

- SQL Server
- MySQL
- Oracle
- PostgreSQL

At the moment, SQL Server is the actual supported runtime backend. The other adapters exist as part of the architecture and design-pattern implementation, not as complete production-ready backends.

## Example Application Flow

Typical runtime flow:

1. The application starts at the login screen.
2. After authentication, the user is routed by role.
3. Customers move through account selection, balance/history views, transfers, and loan features.
4. Admin users access operational screens such as deposits, withdrawals, loan review, interest posting, and overdraft alerts.

## Notable Packages

- `com.bms.presentation`: JavaFX UI components and screen factories
- `com.bms.domain.controller`: use-case orchestration and workflow logic
- `com.bms.domain.entity`: domain models such as `Account`, `Loan`, and `Transaction`
- `com.bms.domain.decorator`: decorator-based enhancements
- `com.bms.domain.flyweight`: flyweight-based loan catalog modeling
- `com.bms.persistence`: DAO contracts, factories, auth context, and datasource configuration
- `com.bms.payment`: payment gateway abstraction and adapter examples

## Project Status

This is a coursework project and should be treated as an educational implementation rather than a production banking system.

Known limitations:

- No automated test suite is currently included
- Persistence is effectively SQL Server-specific at runtime
- Security is coursework-level and not production-grade
- Configuration is environment-local rather than profile-driven
- Some infrastructure exists primarily to support design-pattern coverage

## Suggested First Steps for a New Viewer

If you are opening the repository for the first time, this is the fastest path to understanding it:

1. Read [pom.xml](/c:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/pom.xml) for the runtime stack and build configuration.
2. Open [BankManagementSystemApp.java](/c:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/java/com/bms/BankManagementSystemApp.java) to see the navigation flow.
3. Review the `presentation`, `domain`, and `persistence` packages in that order.
4. Check the database schema in [schema.sql](/c:/Users/Youssef/Documents/Uni/Software/Semester%201/Project/Implementation/JavaApplication6/src/main/resources/db/schema.sql).
5. Read the structural design pattern report for the design rationale.
