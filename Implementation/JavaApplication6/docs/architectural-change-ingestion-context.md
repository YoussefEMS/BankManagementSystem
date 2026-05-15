# Architectural Change Ingestion Context

Last reviewed: 2026-05-14

This document is the briefing artifact to ingest before making architectural changes to this project, especially changes such as adding HTTP APIs, adding another presentation adapter, replacing persistence, introducing authentication infrastructure, or reshaping service boundaries.

It summarizes the current implementation, the live architectural seams, the parts that are still legacy-coupled, and the risks that should be addressed before exposing the system through a new API surface.

## Project Snapshot

The project is a JavaFX desktop Bank Management System for retail banking workflows. It is a coursework implementation, but it already contains a meaningful layered architecture, event infrastructure, DAO persistence, and several pattern-based domain workflows.

Current stack:

- Java 17
- JavaFX 22
- Maven
- JDBC
- HikariCP
- Microsoft SQL Server as the only implemented runtime database backend
- SLF4J and Logback dependencies, with some code still using `java.util.logging` or `System.err`

Current app type:

- Desktop client, not a web service.
- Main class configured in `pom.xml`: `com.bms.view.BankManagementSystemApp`.
- There is no existing REST, HTTP, servlet, Spring Boot, JAX-RS, or embedded server layer.

Current architectural shape:

```text
JavaFX presentation
  -> application services
  -> domain controllers, workflows, policies, entities
  -> DAOs and persistence providers
  -> SQL Server

Application services also publish in-process domain events.
```

If APIs are added, they should become a new adapter layer beside JavaFX, not a replacement for the domain or persistence layers:

```text
JavaFX adapter       REST/API adapter
      |                    |
      +------> application services
                    |
                    v
         domain workflows and entities
                    |
                    v
             persistence DAOs
                    |
                    v
                SQL Server
```

## Files To Read First

Read these before designing or implementing architectural changes:

| File | Why it matters |
| --- | --- |
| `pom.xml` | Defines Java version, JavaFX dependencies, SQL Server JDBC driver, HikariCP, and the actual main class. |
| `README.md` | Good project overview, features, setup, and limitations. Some package references are older than the current source layout. |
| `docs/deliverable4/software-architecture-report.md` | Most complete architectural description of layered, event-driven, and DAO styles. Treat this as the current architectural intent. |
| `docs/design-pattern-mapping.md` | Explains why current package names were flattened into responsibility-oriented packages. Important before renaming or moving classes. |
| `src/main/java/com/bms/view/BankManagementSystemApp.java` | JavaFX composition root and navigation flow. Shows current UI routing and screen creation. |
| `src/main/java/com/bms/service/base/ApplicationService.java` | Base service class, default event dispatcher creation, and event publishing seam. |
| `src/main/java/com/bms/service/*.java` | Current application service surface. Use this layer as the main integration point for new APIs. |
| `src/main/java/com/bms/persistence/*.java` | DAO contracts, SQL queries, datasource setup, SQL Server-specific persistence provider, and database adapter placeholders. |
| `src/main/resources/db/schema.sql` | Source of truth for database tables, columns, constraints, statuses, and relationships. |
| `src/main/resources/application.properties` | Runtime datasource configuration. It currently contains environment-specific credentials; do not copy secrets into new docs, commits, logs, API examples, or generated artifacts. |

Historical/supporting docs:

- `docs/architecture.md` is useful background, but it reads partly as a plan. Cross-check it against the current code and `docs/deliverable4/software-architecture-report.md`.
- `docs/workflow-coordination-legacy-readme.md` preserves older mediator/workflow documentation.
- `docs/mermaid/*` and `docs/deliverable4/uml/*` contain diagram sources.

Generated or non-source directories to avoid treating as architecture sources:

- `target/`
- `build/`
- `dist/`
- `.metals/`
- `chrome-headless-profile/`
- generated reports and PDFs unless the task is documentation-specific

## Layer Responsibilities

### Presentation Layer: `com.bms.view`

Contains JavaFX screens, forms, navigation, screen creation, demos, and application bootstrapping.

Important files:

- `BankManagementSystemApp`
- `ScreenProvider`
- `JavaFxScreenProvider`
- screens/forms such as `LoginScreen`, `AccountSelectionScreen`, `TransferFundsForm`, `LoanReviewForm`, `MonthlyInterestJob`, `OverdraftAlertView`

Current behavior:

- `BankManagementSystemApp` creates core screens through `ScreenProvider`.
- Navigation is callback-driven.
- Login routes to either customer flow or admin dashboard by checking `AuthContext`.
- The UI is still a mix of newer service usage and older direct domain-controller usage.

Do not build API behavior in this layer. An API should not depend on JavaFX, `Stage`, `Scene`, screen callbacks, or JavaFX form validation.

### Application Service Layer: `com.bms.service`

This is the intended integration boundary for external adapters such as a REST API.

Current service classes:

| Service | Main responsibilities | Events emitted |
| --- | --- | --- |
| `CustomerAuthenticationService` | Authenticate, expose logged-in customer/account data, logout, role checks. | `CustomerAuthenticatedEvent` |
| `AccountManagementService` | Create customer profile, update account status. | `CustomerProfileCreatedEvent`, `AccountStatusChangedEvent`, `AccountClosedEvent` |
| `FundTransferService` | Transfer funds through `FundsTransferProcessor`. | `FundTransferCompletedEvent`, `FundTransferFailedEvent` |
| `LoanApplicationService` | Apply for loan, list pending loans, get loan details, approve/reject loans, list customer loans. | `LoanApplicationSubmittedEvent`, `LoanApprovedEvent`, `LoanRejectedEvent` |
| `InterestPostingService` | Post monthly interest. | `InterestPostedEvent` |

Service construction:

- Default constructors use `ConfiguredPersistenceProvider.getInstance()`.
- Services inherit event publication from `ApplicationService`.
- `ApplicationService` creates one default `SynchronousEventDispatcher` and registers default handlers.
- Some constructors accept injected persistence providers or event dispatchers, which is useful for tests and future API composition.

Use this layer for APIs where coverage exists. Where coverage does not exist, create or extend services before adding API endpoints.

### Domain Layer: `com.bms.domain.entity` and `com.bms.domain.controller`

The domain layer contains both pure-ish entities and many workflow/controller classes. The current code is not a strict domain model; some `domain.controller` classes directly use DAOs and `AuthContext`.

Entity package:

- Core state: `Customer`, `Account`, `Loan`, `Transaction`, `Transfer`, `InterestPosting`, `OverdraftEvent`
- Workflow/context payloads: `TransactionContext`, `FundTransferContext`, `MonthlyInterestContext`, `LoanApprovalContext`
- DTO-like payloads: `AccountInfoSnapshot`, `PaymentRequest`, `PaymentResponse`, `PaymentStatus`
- Catalog/pattern data: `LoanProductTemplate`, `LoanComparisonScenario`, `TransactionRecordBuilder`

Controller package:

- Use-case controllers: `AuthenticationController`, `AccountBalanceController`, `TransactionHistoryController`, `CustomerProfileController`, `AccountStatusUpdater`, `LoanStatusController`, `LoanDecisionService`
- Transaction processors: `AbstractTransactionProcessor`, `DepositProcessor`, `WithdrawCashController`, `FundsTransferProcessor`
- Loan workflows: loan application processors, loan interest calculators, loan approval policies, loan catalog controller
- Monthly interest workflows: posting processors, calculators, coordinator, workflow mediator components
- Transfer workflow mediator components
- Decorator examples: account info providers and transaction processors
- Adapter examples: payment gateways and database provider-related clients
- Observer examples: overdraft notification service and listeners

When adding APIs, avoid calling these directly from API controllers unless a service does not yet exist and the architectural change explicitly includes creating that service boundary.

### Event Layer: `com.bms.event`, `com.bms.event.bus`, `com.bms.event.handler`

The application has a synchronous, in-process event bus.

Core types:

- `DomainEvent`: immutable base event with `eventId`, `timestamp`, `aggregateId`, `version`, and `getEventType()`.
- `EventDispatcher`: subscribe and dispatch contract.
- `EventHandler<T extends DomainEvent>`: handler interface.
- `SynchronousEventDispatcher`: in-process dispatcher with handler lookup by assignable event class. Handler runtime failures are logged and do not stop dispatch.
- `EventHandlerRegistry`: registers default handlers.

Default handler registrations:

- `DomainEvent` -> `AuditLogEventHandler`
- `LoanApplicationSubmittedEvent` -> `LoanApprovalNotificationHandler`
- `FundTransferCompletedEvent` -> `TransactionConfirmationHandler`
- `OverdraftDetectedEvent` -> `OverdraftAlertHandler`

Events currently defined:

- `AccountClosedEvent`
- `AccountCreatedEvent`
- `AccountStatusChangedEvent`
- `CustomerAuthenticatedEvent`
- `CustomerProfileCreatedEvent`
- `FundTransferCompletedEvent`
- `FundTransferFailedEvent`
- `InterestPostedEvent`
- `LoanApplicationSubmittedEvent`
- `LoanApprovedEvent`
- `LoanRejectedEvent`
- `OverdraftDetectedEvent`

Important caveat:

- The service-layer event bus and the overdraft observer flow are separate systems.
- `OverdraftDetectedEvent` exists and has a registered handler, but the current overdraft workflow in `OverdraftMonitor` publishes a `com.bms.domain.entity.OverdraftEvent` through `OverdraftNotificationService`, not through `ApplicationService.publish(...)`.
- If an API exposes overdraft operations or turns overdrafts into integration events, reconcile these two event paths deliberately.

### Persistence Layer: `com.bms.persistence`

This layer owns JDBC, DAO classes, connection pooling, and database provider selection.

Important classes:

| Class | Responsibility |
| --- | --- |
| `DataSourceProvider` | Singleton HikariCP datasource. Loads `application.properties` from classpath. |
| `ConfiguredPersistenceProvider` | Singleton provider selector. Delegates to concrete provider based on configured database type. |
| `PersistenceProvider` | Abstract factory for DAOs. |
| `SqlServerPersistenceProvider` | Creates the concrete DAO instances currently used at runtime. |
| `DatabaseConnectionProvider` | Adapter interface for database-specific SQL behavior. |
| `DatabaseConnectionProviderSelector` | Resolves SQL Server, MySQL, Oracle, or PostgreSQL provider based on explicit config, JDBC URL, or driver. |
| `AccountDAO`, `CustomerDAO`, `TransactionDAO`, `TransferDAO`, `LoanDAO`, `InterestPostingDAO`, `OverdraftEventDAO` | Data access objects for database tables and entity mapping. |
| `AuthContext` | Singleton in-memory session for the current desktop user. |

Current database support:

- SQL Server DAOs are implemented.
- MySQL, Oracle, and PostgreSQL connection providers exist as adapter examples, but `ConfiguredPersistenceProvider` throws for those database types because concrete DAOs are currently SQL Server-specific.

Persistence caveats:

- DAOs usually open their own connection per method call.
- Multi-step money movement is not wrapped in a single database transaction at the DAO/provider layer.
- Several DAOs catch `SQLException`, print/log locally, and return sentinel values such as `null`, `false`, `-1`, or empty lists.
- API code should not expose these sentinel values directly as stable API contracts.
- API work that exposes fund transfer, deposit, withdrawal, interest posting, or loan decisions should first define explicit service results/errors and consider transaction boundaries.

## Database Model

Source of truth: `src/main/resources/db/schema.sql`

Tables:

| Table | Purpose |
| --- | --- |
| `Customer` | Stores customers and admins. Uses `role` values `CUSTOMER` and `ADMIN`. Includes plaintext `password` in current coursework implementation. |
| `Account` | Customer bank accounts. Primary key is `account_number`. References `Customer`. Includes `account_type`, `balance`, `currency`, `status`. |
| `Transactions` | Account transaction ledger. References `Account`. Includes transaction type, amount, timestamp, performer, note, balance after, reference code. |
| `Loan` | Loan applications and decisions. References `Customer`. Status is constrained to `PENDING`, `APPROVED`, `REJECTED`. |
| `Transfer` | Transfer records linking source/destination accounts and reference code. |
| `InterestPosting` | Interest posting records for accounts. |
| `OverdraftEvent` | Overdraft records linked to an account and causing transaction. |

Notable constraints and indexes:

- `Customer.email` and `Customer.national_id` are unique.
- `Customer.role` has a check constraint.
- `Loan.status` has a check constraint.
- Common lookup indexes exist for account/customer, transactions by account/timestamp, loans by customer/status, transfers by source/destination, interest postings by account, and overdrafts by account.

Seed data:

- `src/main/resources/db/seed_auth_data.sql` clears and reseeds demo customers, admins, accounts, transactions, transfers, interest postings, overdraft events, and loans.
- Seed passwords are placeholders for local demo use only.

## Runtime Wiring And State

### Desktop startup

`BankManagementSystemApp.start(...)`:

1. Creates `JavaFxScreenProvider`.
2. Creates the login, account selection, account balance, transaction history, and admin dashboard screens.
3. Wires navigation callbacks.
4. Shows login screen.
5. Creates a `Scene` with a `BorderPane` root.
6. Uses menu bars for customer and admin navigation.
7. Logs out on close.

### Session state

`AuthContext` is a process-wide singleton that holds one `Customer` object as the logged-in user.

This works for a single desktop user. It is not suitable as-is for a multi-user HTTP API because:

- All API users would share the same process-wide session.
- Concurrent requests could overwrite or observe the same global login state.
- Some domain controllers read `AuthContext` directly for `performedBy`.

Any API architecture must introduce a request-scoped authentication/authorization model and remove or isolate direct `AuthContext` use from API-driven workflows.

### Datasource state

`DataSourceProvider` is a singleton that initializes a HikariCP pool using classpath `application.properties`.

Implications:

- Configuration is loaded at first access.
- Tests and API startup need to control classpath config or inject alternative providers.
- Shutdown uses `DataSourceProvider.shutdown()`, but current JavaFX close flow logs out only; lifecycle handling may need refinement for API/server mode.

## Current Use-Case Flows

### Login and role routing

Typical desktop flow:

```text
LoginScreen
  -> CustomerAuthenticationService.authenticateCustomer(...)
  -> AuthenticationController.authenticateCustomer(...)
  -> CustomerDAO.authenticate(...)
  -> AuthContext.login(...)
  -> CustomerAuthenticatedEvent
  -> BankManagementSystemApp routes by AuthContext.isAdmin()
```

API implication:

- Do not reuse `AuthContext` as an HTTP session store.
- Expose login through a service or new auth facade that returns a token/session result rather than mutating global desktop state.

### Account balance and history

Current UI:

- `AccountBalanceScreen` uses `AccountBalanceController`, `InterestService`, and `CustomerAuthenticationService`.
- `TransactionHistoryScreen` uses `TransactionHistoryController` and `CustomerAuthenticationService`.

API implication:

- There is no application service facade for account summary or transaction history yet.
- Add an `AccountQueryService` or similar before exposing these as APIs.

### Customer fund transfer

Current service flow:

```text
TransferFundsForm
  -> FundTransferService.transferFunds(...)
  -> FundsTransferProcessor.transferFunds(...)
  -> AbstractTransactionProcessor.executeTransaction(...)
  -> AccountDAO / TransactionDAO / TransferDAO
  -> FundTransferCompletedEvent or FundTransferFailedEvent
```

Important caveats:

- `FundsTransferProcessor` uses `AuthContext` for `performedBy`.
- Debit, credit, transaction inserts, and transfer insert are separate DAO calls.
- The processor returns `null` on failure; the service maps that to `FundTransferFailedEvent`.
- No transaction manager currently guarantees atomic transfer persistence across all affected records.

API implication:

- Transfer APIs should use explicit request-scoped actor information.
- Introduce transactional persistence or a unit-of-work mechanism before treating transfer as production-grade.
- Return structured success/failure objects rather than leaking `null`.

### Deposit and withdrawal

Current UI:

- `DepositCashForm` directly uses `DepositProcessor`.
- `WithdrawCashForm` directly uses `WithdrawCashController`.

Current processor behavior:

- Both extend `AbstractTransactionProcessor`.
- Both use `AuthContext` for `performedBy`.
- Deposit updates balance and inserts a transaction.
- Withdrawal updates balance, inserts a cash withdrawal transaction, optionally inserts a fee transaction, and checks overdraft after the operation.

API implication:

- There is no `CashTransactionService` yet.
- Add a service facade before exposing deposit/withdraw endpoints.
- Define transaction boundaries and structured failure contracts first.

### Loan application, review, status, and catalog

Current service flow:

```text
ApplyForLoanForm
  -> LoanApplicationService.applyForLoan(...)
  -> com.bms.domain.controller.LoanApplicationService.applyForLoan(...)
  -> loan processors / approval policies / LoanDAO
  -> LoanApplicationSubmittedEvent
```

Admin review:

```text
LoanReviewForm
  -> LoanApplicationService.getPendingLoans()
  -> LoanApplicationService.decideLoan(...)
  -> LoanDecisionService / LoanDAO
  -> LoanApprovedEvent or LoanRejectedEvent
```

Loan status:

```text
LoanStatusView
  -> LoanApplicationService.getLoansForCustomer(...)
  -> LoanStatusController / LoanDAO
```

Loan catalog:

- `LoanCatalogComparisonView` directly uses `LoanCatalogController`.

API implication:

- Loan application and review have a usable service boundary.
- Loan catalog comparison needs a service facade if exposed externally.
- Avoid exposing `Loan` entity directly if it contains fields that should be renamed, hidden, or versioned for API clients.

### Monthly interest posting

Current flow:

```text
MonthlyInterestJob
  -> InterestPostingService.postMonthlyInterest()
  -> MonthlyInterestPostingCoordinator.postMonthlyInterest()
  -> SavingsInterestPostingProcessor + MoneyMarketInterestPostingProcessor
  -> AccountDAO / TransactionDAO / InterestPostingDAO
  -> InterestPostedEvent
```

API implication:

- This is an admin/batch operation. It should require explicit admin authorization.
- Idempotency should be considered before exposing it through an API. The current flow can post again if called again.

### Overdraft monitoring

Current flow:

```text
WithdrawCashController
  -> OverdraftMonitor.checkOverdraft(...)
  -> OverdraftNotificationService.publish(domain.entity.OverdraftEvent)
  -> OverdraftEventRecorder / CustomerOverdraftNotifier / OverdraftAuditLogger / UI subscribers
  -> OverdraftEventDAO.insert(...)
```

Current admin view:

- `OverdraftAlertView` directly uses `OverdraftMonitor`, `OverdraftNotificationService`, and listener classes.

API implication:

- There is no application service facade for overdraft queries or events yet.
- There are two event systems to reconcile before adding external notifications.

## Current Architectural Seams For APIs

Use these seams:

- Application services in `com.bms.service`.
- `PersistenceProvider` constructor injection for testability or alternate persistence.
- `EventDispatcher` constructor injection for controlled event testing.
- Domain workflow interfaces such as `PaymentGateway`, `Transfer*` validators, `MonthlyInterest*` components, and `LoanApprovalPolicy`.
- DAO layer only from services or domain controllers, not from API controllers.

Avoid these as API seams:

- JavaFX screens/forms.
- `BankManagementSystemApp` navigation callbacks.
- `AuthContext` as a global login/session source.
- Direct DAO calls from API controllers.
- Exposing domain entities with passwords or internal persistence fields.
- Sentinel return values such as `null`, `-1`, `false`, or empty list as the only error contract.

## Recommended API Addition Strategy

1. Add an API adapter package or module that depends on application services only.

   Example package names:

   - `com.bms.api`
   - `com.bms.api.dto`
   - `com.bms.api.controller`
   - `com.bms.api.mapper`

   Keep API DTOs separate from domain entities.

2. Fill missing service facades before writing endpoints.

   Suggested additions:

   - `AccountQueryService` for balances, account summaries, and transaction history.
   - `CashTransactionService` for deposits and withdrawals.
   - `LoanCatalogService` for loan products and comparisons.
   - `OverdraftService` for overdraft queries and event publication alignment.

3. Replace or isolate `AuthContext` for API flows.

   For APIs, pass actor/user context explicitly to services, or introduce a request-scoped `CurrentUser`/`SecurityContext` abstraction. Do not let API calls mutate the desktop singleton session.

4. Define structured service results.

   Prefer result objects or exceptions that capture:

   - success/failure
   - business error code
   - human-readable message
   - generated IDs/reference codes
   - validation details

   This should happen below the API adapter so JavaFX and APIs can reuse it.

5. Add transaction management for money movement.

   Before exposing transfer, deposit, withdrawal, or interest posting as API operations, decide how to make balance updates and ledger records atomic. Current DAOs do not share one connection/transaction across multi-step operations.

6. Externalize configuration and secrets.

   Current `application.properties` contains local database credentials. API/server work should move secrets out of committed config and support environment-specific configuration.

7. Add tests around service behavior before changing architecture.

   There is no automated test suite currently. Start with service-level tests using fake or test persistence providers where possible.

## Security Context For Future APIs

Current security is coursework-level:

- Passwords are stored and compared as plaintext in `CustomerDAO.authenticate(...)`.
- `AuthContext` stores one global in-memory logged-in customer.
- Role checks are mostly UI routing and service/UI convention, not a hardened authorization layer.
- Admin operations depend on the caller passing/admin context rather than centralized enforcement.

Before exposing external APIs:

- Hash passwords using a real password hashing algorithm.
- Never serialize `Customer.password`.
- Define authentication token/session behavior.
- Enforce role checks in the service or API security layer, not only in UI navigation.
- Pass request actor identity explicitly into state-changing services.
- Audit state-changing operations using either the existing event bus or a dedicated audit persistence mechanism.

## Data And API Contract Notes

The domain currently mixes `double`, `BigDecimal`, and SQL decimal columns:

- Database money values use `DECIMAL`.
- Some DAOs/entities use `BigDecimal`.
- Several service/controller APIs accept `double`.

For external APIs:

- Accept monetary values as decimal strings or JSON numbers mapped to `BigDecimal`.
- Validate scale, sign, and currency.
- Convert to legacy `double` only at existing boundaries until those boundaries are refactored.
- Do not create public contracts that depend on floating-point behavior.

Recommended DTO boundaries:

- Request DTOs should be API-specific.
- Response DTOs should omit passwords and internal-only fields.
- Use stable enum-like values for statuses: account `ACTIVE`, `FROZEN`, `CLOSED`; loan `PENDING`, `APPROVED`, `REJECTED`; roles `CUSTOMER`, `ADMIN`.
- Include reference codes for transfers and transaction IDs for ledger operations.

## Dependency And Build Notes

Build configuration:

- `pom.xml` sets Java source/target to 17.
- JavaFX dependencies use platform classifier `${javafx.platform}`, default `win`.
- JavaFX plugin main class is `com.bms.view.BankManagementSystemApp`.
- Shade plugin also uses `com.bms.view.BankManagementSystemApp`.

Common commands:

```bash
mvn clean package
mvn clean javafx:run
mvn clean javafx:run -Djavafx.platform=linux
mvn clean javafx:run -Djavafx.platform=mac
```

If adding an API runtime:

- Decide whether it is a separate main class or an embedded server started by the desktop app.
- Prefer a separate server entrypoint for clean lifecycle and testability.
- Update Maven plugin/shade configuration intentionally if adding a second executable.
- Avoid forcing JavaFX initialization for API-only runs.

## Known Architectural Gaps

Treat these as design constraints when planning architectural changes:

- No automated tests are present.
- Some UI classes still call domain controllers directly instead of application services.
- Some domain controllers depend on DAOs and `AuthContext`, so the domain layer is not pure.
- `AuthContext` is a desktop singleton and not request-safe for API use.
- DAOs swallow SQL errors and return sentinel values.
- Multi-step financial operations are not transactionally atomic across DAO calls.
- SQL Server is the only implemented concrete persistence backend.
- Database credentials are currently in `application.properties`.
- Password handling is plaintext.
- Event handling is synchronous and in-process only.
- The overdraft observer flow is separate from the service-layer domain event bus.
- Monetary inputs use `double` in several public methods.
- Some docs and README sections contain older package names from before the refactor.

## Architectural Guardrails

When making changes:

- Keep JavaFX, future APIs, and other adapters above the service layer.
- Put orchestration in application services, not in API controllers or JavaFX screens.
- Keep JDBC and SQL in `com.bms.persistence`.
- Avoid adding framework annotations or HTTP-specific types to domain entities/controllers.
- Add service facades for missing use cases instead of making APIs call DAOs directly.
- Make user/actor identity explicit for API-driven operations.
- Preserve existing JavaFX flows unless the task explicitly includes migrating them.
- Add event handlers by subscription instead of modifying services for every side effect.
- Keep SQL Server-specific assumptions visible in persistence, not scattered through services.
- Prefer `BigDecimal` for new money-facing API contracts.

## Pre-Change Checklist

Before implementing an architectural change, answer these:

- What adapter is being added or changed: JavaFX, API, CLI, batch job, or persistence?
- Which application service owns the use case?
- If no service owns it, what service facade should be created first?
- Does the operation mutate balances, loans, account statuses, or customer data?
- Does it need a database transaction boundary?
- What actor identity and role are required?
- What events should be emitted after success or failure?
- Does any existing UI rely on sentinel return values that must remain backward compatible?
- Are any secrets or environment-specific values being copied?
- What tests or manual verification prove the service still works?

## Suggested First Refactors Before Public APIs

Highest leverage changes:

1. Create service facades for balance/history, deposits/withdrawals, loan catalog, and overdraft queries.
2. Replace global `AuthContext` usage in service-driven workflows with explicit actor context.
3. Introduce structured service results and business error codes.
4. Add transaction-scoped persistence support for financial operations.
5. Externalize DB credentials and add sample config.
6. Add service-level tests for login, transfer, deposit, withdrawal, loan decisions, and interest posting.
7. Align overdraft handling with the service-layer domain event bus.

These steps make future API work much smaller and reduce the risk of exposing desktop-only assumptions as server behavior.
