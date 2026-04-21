# Design Pattern Mapping

This document records the structural refactor from pattern-named packages into responsibility-oriented architecture.

## Architecture Layers

| Layer | Responsibility |
| --- | --- |
| `persistence` | Database access, data source setup, DAO creation, and vendor connection providers. |
| `domain/entity` | Business entities, state payloads, request/response values, and domain snapshots. |
| `domain/controller` | Use-case controllers, workflow coordinators, policies, validators, calculators, processors, notifications, and gateway clients. |
| `view` | JavaFX screens, UI bootstrapping, screen provisioning, and console/demo views. |

## Persistence

| File path(s) | New name | Old name | Layer | Pattern | Why it applies | Filename exposes pattern? |
| --- | --- | --- | --- | --- | --- | --- |
| `src/main/java/com/bms/persistence/AccountDAO.java`, `CustomerDAO.java`, `TransactionDAO.java`, `TransferDAO.java`, `LoanDAO.java`, `InterestPostingDAO.java`, `OverdraftEventDAO.java` | DAO classes | Same | persistence | DAO | These classes isolate SQL/JDBC access for domain entities. | Yes, but retained because DAO names describe data-access responsibility. |
| `src/main/java/com/bms/persistence/PersistenceProvider.java` | `PersistenceProvider` | `DAOFactory` | persistence | Abstract Factory | It defines creation methods for DAO families without binding controllers to a concrete persistence implementation. | No, renamed to system-oriented provider. |
| `src/main/java/com/bms/persistence/SqlServerPersistenceProvider.java` | `SqlServerPersistenceProvider` | `SqlServerDAOFactory` | persistence | Abstract Factory, Singleton | It supplies SQL Server DAO instances and has a single shared instance. | No, renamed to persistence provider. |
| `src/main/java/com/bms/persistence/ConfiguredPersistenceProvider.java` | `ConfiguredPersistenceProvider` | `ConfiguredDAOFactory` | persistence | Abstract Factory, Singleton | It selects the configured persistence provider while exposing the same DAO creation contract. | No, renamed to configured provider. |
| `src/main/java/com/bms/persistence/DataSourceProvider.java` | `DataSourceProvider` | `DataSourceFactory` | persistence | Singleton | It owns one configured Hikari data source instance for the application. | No, renamed to provider. |
| `src/main/java/com/bms/persistence/DatabaseConnectionProvider.java` | `DatabaseConnectionProvider` | `DatabaseAdapter` | persistence | Adapter | It standardizes vendor-specific JDBC details behind one interface. | No, renamed to connection provider. |
| `src/main/java/com/bms/persistence/SqlServerConnectionProvider.java`, `MySQLConnectionProvider.java`, `OracleConnectionProvider.java`, `PostgreSQLConnectionProvider.java` | Vendor connection providers | `*Adapter` | persistence | Adapter | Each class translates vendor-specific JDBC behavior into `DatabaseConnectionProvider`. | No, renamed to connection provider/client role. |
| `src/main/java/com/bms/persistence/DatabaseConnectionProviderSelector.java` | `DatabaseConnectionProviderSelector` | `DatabaseAdapterFactory` | persistence | Factory Method / Simple Factory | It selects a concrete database connection provider from configuration. | No, renamed to selector. |
| `src/main/java/com/bms/persistence/AuthContext.java` | `AuthContext` | Same | persistence | None | Holds authenticated user/session context used by persistence-backed flows. | No. |

## Domain Entities

| File path(s) | New name | Old name | Layer | Pattern | Why it applies | Filename exposes pattern? |
| --- | --- | --- | --- | --- | --- | --- |
| `src/main/java/com/bms/domain/entity/Account.java`, `Customer.java`, `Loan.java`, `Transaction.java`, `Transfer.java`, `InterestPosting.java`, `OverdraftEvent.java` | Core entities | Same | domain/entity | None | Persistent business state holders. | No. |
| `src/main/java/com/bms/domain/entity/TransactionRecordBuilder.java` | `TransactionRecordBuilder` | `TransactionFactory` | domain/entity | Factory Method | Centralizes transaction object creation for deposit, withdrawal, and transfer records. | No, renamed to builder. |
| `src/main/java/com/bms/domain/entity/LoanApprovalContext.java` | `LoanApprovalContext` | Same package-moved from strategy package | domain/entity | Strategy participant | Holds the selected loan approval policy and delegates approval. | No. |
| `src/main/java/com/bms/domain/entity/TransactionContext.java` | `TransactionContext` | Same package-moved from decorator package | domain/entity | Decorator participant | Carries mutable transaction processing state through chained processors. | No. |
| `src/main/java/com/bms/domain/entity/AccountInfoSnapshot.java` | `AccountInfoSnapshot` | `AccountInfoView` | domain/entity | Decorator output DTO | Carries account display data assembled by account info providers. | No, renamed to snapshot. |
| `src/main/java/com/bms/domain/entity/LoanProductTemplate.java` | `LoanProductTemplate` | `LoanProductFlyweight` | domain/entity | Flyweight | Stores shared loan product intrinsic state reused across comparison scenarios. | No, renamed to product template. |
| `src/main/java/com/bms/domain/entity/LoanComparisonScenario.java` | `LoanComparisonScenario` | Same package-moved from flyweight package | domain/entity | Flyweight client state | Combines shared loan product data with extrinsic comparison values. | No. |
| `src/main/java/com/bms/domain/entity/FundTransferContext.java`, `MonthlyInterestContext.java` | Workflow contexts | Same package-moved from mediator packages | domain/entity | Mediator participant | Carry workflow state through transfer and monthly-interest coordination. | No. |
| `src/main/java/com/bms/domain/entity/PaymentRequest.java`, `PaymentResponse.java`, `PaymentStatus.java` | Payment payloads | Same package-moved from payment package | domain/entity | Adapter DTO | Uniform request/response state used by payment gateway clients. | No. |

## Domain Controllers and Services

| File path(s) | New name | Old name | Layer | Pattern | Why it applies | Filename exposes pattern? |
| --- | --- | --- | --- | --- | --- | --- |
| `AuthenticationController.java`, `AccountBalanceController.java`, `CustomerProfileController.java`, `TransactionHistoryController.java`, `LoanCatalogController.java`, `LoanStatusController.java`, `WithdrawCashController.java` | Use-case controllers | Same or package-updated | domain/controller | MVC/Application Controller | They coordinate use-case input, persistence calls, and domain objects. | Some retain `Controller`, which reflects app-layer responsibility. |
| `src/main/java/com/bms/domain/controller/DepositProcessor.java` | `DepositProcessor` | `DepositHandler` | domain/controller | Template Method participant | Performs deposit workflow through shared transaction processing steps. | No, renamed to processor. |
| `src/main/java/com/bms/domain/controller/FundsTransferProcessor.java` | `FundsTransferProcessor` | `TransferHandler` | domain/controller | Template Method participant, Adapter client | Performs transfer workflow and optionally invokes a payment gateway. | No, renamed to processor. |
| `src/main/java/com/bms/domain/controller/AccountStatusUpdater.java` | `AccountStatusUpdater` | `AccountStatusHandler` | domain/controller | None | Updates account status as a use case. | No, renamed to updater. |
| `src/main/java/com/bms/domain/controller/OverdraftMonitor.java` | `OverdraftMonitor` | `OverdraftHandler` | domain/controller | Observer publisher client | Detects overdraft conditions and publishes events. | No, renamed to monitor. |
| `src/main/java/com/bms/domain/controller/MonthlyInterestPostingCoordinator.java` | `MonthlyInterestPostingCoordinator` | `MonthlyInterestHandler` | domain/controller | Template Method coordinator | Coordinates savings and money-market interest posting processors. | No, renamed to coordinator. |
| `src/main/java/com/bms/domain/controller/LoanApplicationService.java` | `LoanApplicationService` | `LoanApplicationHandler` | domain/controller | Bridge client | Selects loan processors while preserving loan application workflow behavior. | No, renamed to service. |
| `src/main/java/com/bms/domain/controller/LoanDecisionService.java` | `LoanDecisionService` | `LoanDecisionHandler` | domain/controller | None | Reviews and updates loan decisions. | No, renamed to service. |
| `src/main/java/com/bms/domain/controller/AbstractTransactionProcessor.java` | `AbstractTransactionProcessor` | `AbstractTransactionTemplate` | domain/controller | Template Method | Defines transaction workflow skeleton with subclass-specific validation and impact steps. | No, renamed to processor. |
| `src/main/java/com/bms/domain/controller/InterestPostingProcessor.java`, `SavingsInterestPostingProcessor.java`, `MoneyMarketInterestPostingProcessor.java` | Interest posting processors | `AbstractInterestPostingHandler`, `SavingsInterestPostingHandler`, `MoneyMarketInterestPostingHandler` | domain/controller | Template Method | Base class defines posting algorithm; subclasses supply account type/rate rules. | No, renamed to processors. |
| `AbstractLoanApplicationProcessor.java`, `DefaultLoanApplicationProcessor.java`, `HomeLoanApplicationProcessor.java`, `AutoLoanApplicationProcessor.java`, `PersonalLoanApplicationProcessor.java` | Loan application processors | Same | domain/controller | Template Method, Bridge | Common loan workflow is in the abstract processor; concrete processors vary loan type and calculator. | No. |
| `AbstractLoanInterestCalculator.java`, `DefaultLoanInterestCalculator.java`, `HomeLoanInterestCalculator.java`, `AutoLoanInterestCalculator.java`, `PersonalLoanInterestCalculator.java`, `LoanInterestCalculator.java` | Loan interest calculators | Same | domain/controller | Strategy / Bridge implementation side | The application processor depends on the calculator abstraction and concrete calculators vary by loan type. | No. |
| `LoanApprovalPolicy.java`, `StandardLoanApprovalPolicy.java`, `PremiumLoanApprovalPolicy.java`, `BusinessLoanApprovalPolicy.java`, `LoanApprovalPolicySelector.java` | Loan approval policies | `LoanApprovalStrategy`, `*LoanApprovalStrategy`, `LoanApprovalStrategyFactory` | domain/controller | Strategy, Factory Method | Policies encapsulate approval rules; selector chooses a policy by customer tier. | No, renamed to policy/selector. |
| `InterestRatePolicy.java`, `StandardInterestRatePolicy.java`, `PremiumInterestRatePolicy.java`, `BusinessInterestRatePolicy.java`, `AccountInterestCalculator.java`, `InterestService.java` | Interest rate policies | `InterestCalculationStrategy`, `*InterestStrategy`, strategy `InterestCalculator` | domain/controller | Strategy | Account interest calculation delegates rate behavior to interchangeable policies. | No, renamed to policy/calculator. |
| `AccountInfoProvider.java`, `BaseAccountInfoProvider.java`, `AccountInfoEnhancer.java`, `CurrencyFormattedAccountInfoProvider.java`, `OverdraftWarningAccountInfoProvider.java`, `RewardPointsAccountInfoProvider.java` | Account info providers/enhancers | `AccountInfoService`, `BasicAccountInfoService`, `AccountInfoDecorator`, `*Decorator` | domain/controller | Decorator | Enhancers wrap another provider to add formatted balance, warnings, and rewards. | No, renamed to provider/enhancer. |
| `TransactionProcessor.java`, `BasicTransactionProcessor.java`, `TransactionProcessorChain.java`, `AuditedTransactionProcessor.java`, `FeeChargingTransactionProcessor.java`, `NotifyingTransactionProcessor.java`, `TransactionOperation.java` | Transaction processors | `TransactionProcessorDecorator`, `AuditDecorator`, `FeeDecorator`, `NotificationDecorator` | domain/controller | Decorator | Processors wrap the base processor to add audit, fee, and notification behavior. | No, renamed to processor roles. |
| `TransactionCollection.java`, `TransactionIterator.java`, `AccountTransactionHistory.java`, `FilteredTransactionIterator.java` | Transaction iteration classes | Same | domain/controller | Iterator | Collection returns iterator abstractions for transaction history traversal/filtering. | No. |
| `LoanProductCatalog.java`, `LoanCatalogController.java` | Product catalog | `LoanProductFlyweightFactory` | domain/controller | Flyweight | Catalog reuses shared loan product templates instead of recreating intrinsic product state. | No, renamed to catalog. |
| `OverdraftAlertListener.java`, `OverdraftNotificationService.java`, `OverdraftEventRecorder.java`, `CustomerOverdraftNotifier.java`, `AdminOverdraftAlertDispatcher.java`, `OverdraftAuditLogger.java` | Overdraft notification components | `OverdraftObserver`, `DatabaseOverdraftObserver`, `CustomerNotificationObserver`, `AdminAlertObserver`, `AuditLogObserver` | domain/controller | Observer | Service publishes overdraft events to independent listeners for DB recording, UI refresh, notification, and audit logging. | No, renamed to listener/recorder/notifier/dispatcher/logger. |
| `PaymentGateway.java`, `StripePaymentClient.java`, `PayPalPaymentClient.java`, `SquarePaymentClient.java`, `PaymentGatewayProvider.java`, `PaymentGatewayException.java` | Payment gateway clients | `StripeAdapter`, `PayPalAdapter`, `SquareAdapter`, `PaymentGatewayFactory` | domain/controller | Adapter, Factory Method | Gateway clients adapt vendor-specific payment APIs to one payment gateway interface; provider creates clients. | No, renamed to clients/provider. |
| `MonthlyInterestWorkflow.java`, `MonthlyInterestWorkflowCoordinator.java`, `MonthlyInterestAccounts.java`, `DatabaseMonthlyInterestAccounts.java`, `MonthlyInterestCalculator.java`, `AccountTypeMonthlyInterestCalculator.java`, `InterestTransactionBuilder.java`, `InterestPostingTransactionBuilder.java`, `InterestBalanceUpdater.java`, `AccountInterestBalanceUpdater.java`, `InterestPostingAuditLogger.java`, `ConsoleInterestPostingAuditLogger.java`, `InterestNotificationSender.java`, `ConsoleInterestNotificationSender.java` | Monthly interest workflow components | `IMonthlyInterestMediator`, `MonthlyInterestMediatorImpl`, `*Peer`, `*PeerImpl` | domain/controller | Mediator | Workflow coordinator centralizes interactions among account source, calculator, transaction builder, updater, logger, and notifier. | No, renamed to workflow/coordinator/component roles. |
| `FundTransferWorkflow.java`, `FundTransferWorkflowCoordinator.java`, `TransferTransactionRecorder.java`, `DatabaseTransferTransactionRecorder.java`, `SourceAccountValidator.java`, `SourceAccountAvailabilityValidator.java`, `DestinationAccountValidator.java`, `DestinationAccountAvailabilityValidator.java`, `TransferAccountStatusChecker.java`, `ActiveTransferAccountStatusChecker.java`, `TransferOverdraftPolicy.java`, `AccountLimitOverdraftPolicy.java`, `TransferBalanceValidator.java`, `AvailableFundsTransferValidator.java` | Fund transfer workflow components | `IFundTransferMediator`, `FundTransferMediatorImpl`, `I*Peer`, `*Impl` | domain/controller | Mediator | Workflow coordinator controls validation and recording order through role interfaces. | No, renamed to workflow/coordinator/component roles. |
| `SavingsInterest.java`, `MoneyMarketInterest.java`, `InterestCalculator.java` | Account interest calculators | Same | domain/controller | Strategy | Account-type interest algorithms implement a shared calculator interface. | No. |

## View

| File path(s) | New name | Old name | Layer | Pattern | Why it applies | Filename exposes pattern? |
| --- | --- | --- | --- | --- | --- | --- |
| `src/main/java/com/bms/view/BankManagementSystemApp.java` | `BankManagementSystemApp` | Moved from `com.bms` root | view | Application Controller / UI composition root | Boots JavaFX and wires screens. | No. |
| `src/main/java/com/bms/view/ScreenProvider.java` | `ScreenProvider` | `ScreenFactory` | view | Abstract Factory | Defines creation of JavaFX screens behind one UI provisioning interface. | No, renamed to provider. |
| `src/main/java/com/bms/view/JavaFxScreenProvider.java` | `JavaFxScreenProvider` | `JavaFXScreenFactory` | view | Abstract Factory | Concrete JavaFX implementation creates the screen family. | No, renamed to provider. |
| `AccountBalanceScreen.java`, `AccountSelectionScreen.java`, `AdminDashboard.java`, `ApplyForLoanForm.java`, `CreateCustomerProfileForm.java`, `DepositCashForm.java`, `LoanCatalogComparisonView.java`, `LoanReviewForm.java`, `LoanStatusView.java`, `LoginScreen.java`, `MonthlyInterestJob.java`, `OverdraftAlertView.java`, `TransactionHistoryScreen.java`, `TransferFundsForm.java`, `UpdateAccountStatusForm.java`, `WithdrawCashForm.java` | UI screens/forms | Moved from `presentation` | view | MVC View | These classes render JavaFX UI and delegate business work to domain controllers. | No. |
| `src/main/java/com/bms/view/MonthlyInterestDemo.java`, `FundTransferDemo.java`, `WorkflowCoordinationDocumentation.java` | Demo/documentation views | `com.bms.mediator.demo.*`, `MediatorPatternDocumentation` | view | Demo clients | Console/demo classes display workflow behavior and are not core domain logic. | `WorkflowCoordinationDocumentation` does not expose a pattern in the filename; content documents pattern history. |

## Build And Source Layout Notes

| File path | Change | Reason |
| --- | --- | --- |
| `pom.xml` | Main class changed to `com.bms.view.BankManagementSystemApp`. | Application bootstrapping moved to the `view` layer. |
| `dependency-reduced-pom.xml` | Main class changed to `com.bms.view.BankManagementSystemApp`. | Keeps the generated reduced POM aligned with the main POM. |
| `docs/workflow-coordination-legacy-readme.md` | Moved from `src/main/java/com/bms/mediator/README.md`. | Removed pattern-specific documentation from the Java source tree while preserving the content. |
| `src/main/java/com/bms/DAO/.LCKTransactionDAO.java~` | Removed. | Stale NetBeans lock file, not Java source. |

## Final Package Constraint Justification

The final Java package structure under `com.bms` is limited to:

- `persistence`
- `domain.entity`
- `domain.controller`
- `view`

No extra Java subpackages remain. Persistence connector classes were flattened into `persistence`, and former pattern packages were folded into `domain.controller`, `domain.entity`, or `view` according to primary responsibility.
