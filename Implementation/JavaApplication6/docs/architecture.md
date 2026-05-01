# Bank Management System — Integrated Architecture Plan

## Executive Summary

The Bank Management System will integrate two complementary architectural patterns:

1. **Layered Architecture** — Organizes code into distinct responsibility tiers with explicit dependencies
2. **Event-Driven Architecture** — Decouples domain workflows through events, enabling async operations and extensibility

These patterns work synergistically: Layered structure provides clarity; event-driven adds flexibility within that structure. Together, they achieve extensibility and decoupling while maintaining clean separation of concerns.

---

## 1. LAYERED ARCHITECTURE

### Refined 4-Tier Structure

```
┌─────────────────────────────────┐
│   PRESENTATION LAYER            │  (JavaFX Views, Controllers, Navigation)
│   com.bms.view.*                │
└──────────────┬──────────────────┘
               │ depends on
┌──────────────▼──────────────────┐
│   APPLICATION/SERVICE LAYER     │  NEW - Domain use-case orchestration
│   com.bms.service.*             │  Coordinates business logic, emits domain events
└──────────────┬──────────────────┘
               │ depends on
┌──────────────▼──────────────────┐
│   DOMAIN LAYER                  │  (Entities, Value Objects, Business Rules)
│   com.bms.domain.*              │  Pure business logic, NO infrastructure deps
└──────────────┬──────────────────┘
               │ depends on
┌──────────────▼──────────────────┐
│   PERSISTENCE LAYER             │  (DAOs, Repository abstractions)
│   com.bms.persistence.*         │  Database abstraction, queries
└──────────────┬──────────────────┘
               │ depends on
┌──────────────▼──────────────────┐
│   DATABASE LAYER                │  (SQL Server, HikariCP pool)
│   External (JDBC/DataSource)    │
└─────────────────────────────────┘
```

### Layers Explained

#### Presentation Layer (`com.bms.view.*`)
- **Responsibility**: Display UI, capture user input, navigate screens
- **Components**: JavaFX FXML, controllers, scene management, CSS
- **Dependencies**: Services from application layer; NO direct domain or persistence access
- **Extensibility**: New screens added without affecting business logic

#### Application/Service Layer (`com.bms.service.*`) — NEW
- **Responsibility**: Orchestrate use-cases; coordinate domain logic; emit domain events
- **Key Classes**:
  - `LoanApplicationService` — Handle loan application workflow
  - `FundTransferService` — Coordinate fund transfer between accounts
  - `InterestPostingService` — Execute monthly interest posting
  - `AccountManagementService` — Create/manage customer accounts
  - `CustomerAuthenticationService` — Login, role-based routing
- **Dependencies**: Domain entities, repositories, event dispatcher
- **NO Infrastructure**: Services contain no JDBC, file I/O, or UI code
- **Event Emission**: Each service emits domain events after completing operations

#### Domain Layer (`com.bms.domain.*`)
- **Responsibility**: Encapsulate pure business logic; define entities and business rules
- **Components**:
  - `com.bms.domain.entity`: Business objects (Customer, Account, Loan, Transaction, etc.)
  - `com.bms.domain.value`: Immutable value objects (Money, AccountNumber, LoanStatus)
  - `com.bms.domain.policy`: Business rules (LoanApprovalPolicy, InterestRatePolicy)
- **NO Infrastructure**: No JDBC, no events, no HTTP, no UI — pure business logic
- **Testability**: Domain layer can be tested independently with no framework

#### Persistence Layer (`com.bms.persistence.*`)
- **Responsibility**: Abstract database access; implement data queries
- **Key Classes**: Repository implementations (AccountRepository, LoanRepository, etc.)
- **Pattern**: Repository + DAO (already in place)
- **Dependencies**: Domain entities (for mapping), database connection pool
- **Isolation**: Only layer that touches JDBC; everything else goes through repositories

#### Database Layer
- **External**: SQL Server instance via HikariCP connection pool
- **Access**: Only via persistence layer through prepared statements

### Why Layered Architecture is Suitable

| Benefit | How It Supports Your System |
|---------|--------------------------|
| **Separation of Concerns** | Each layer has single responsibility; changes in UI don't affect business logic |
| **Testability** | Mock layers below for unit testing (e.g., mock persistence layer when testing services) |
| **Extensibility** | New features fit into existing layers without restructuring (new service for new use-case) |
| **Maintainability** | Clear dependencies and data flow; easy to understand and modify |
| **Supports Current Structure** | Formalizes existing Presentation → Domain → Persistence → Database flow |
| **Team Scalability** | Multiple developers can work on different layers independently |

### Key Refinement — Service Layer

The **crucial addition** is the explicit **Service Layer** between Presentation and Domain:

**Current state**: Presentation calls domain controllers directly → logic mixed with coordination  
**Improved state**: Presentation calls services → Services orchestrate domain → Services emit events

**Benefits**:
- Domain layer remains pure (testable in isolation, framework-independent)
- Services handle complexity of multi-step workflows (loan processing, transfers)
- Clear orchestration point for emitting domain events
- Easy to add cross-cutting concerns (logging, security, performance metrics) at service level

---

## 2. EVENT-DRIVEN ARCHITECTURE

### Event Flow Model

```
┌─ User Action (UI) ──────┐
│  (e.g., "Apply for Loan")
│                         
│  Service Layer         
│  (LoanApplicationService)
│  1. Validate input     
│  2. Execute business logic
│  3. Emit Domain Event  
│                        
└────────┬───────────────┘
         │ publishes
         │
    ┌────▼──────────────────────────────────────┐
    │  EVENT BUS                                 │
    │  (Synchronous dispatcher or async broker) │
    └────┬───────────────────────────────────────┘
         │ routes to all subscribed handlers
         │
    ┌────┴──────────────────────────────────────┐
    │                                           │
┌───▼──────────────────┐  ┌──────────────────┐ │
│ NotificationHandler  │  │ AuditLogHandler  │ │
│ - Send customer      │  │ - Log to DB      │ │
│   notification       │  │ - Compliance     │ │
│ - Async alerts       │  │   record         │ │
└──────────────────────┘  └──────────────────┘ │
                                               │
┌──────────────────────┐  ┌──────────────────┐ │
│ PaymentGateway       │  │ Future: SMS/Email│ │
│ - Process payments   │  │ Notification     │ │
│ - Fund movement      │  │ - Extensible     │ │
└──────────────────────┘  └──────────────────┘ │
                                               │
└───────────────────────────────────────────────┘
```

### Domain Events

Domain events represent important business occurrences. They are immutable, serializable, and named in past tense.

**Loan Processing Events**:
- `LoanApplicationSubmittedEvent` — Customer submits loan application
  - Handler: Notify admin, log audit, send confirmation to customer
- `LoanApprovedEvent` — Admin approves loan application
  - Handler: Update loan status, notify customer, generate documents, log
- `LoanRejectedEvent` — Admin rejects loan application
  - Handler: Notify customer, log, close application

**Account & Transfer Events**:
- `AccountCreatedEvent` — New customer account created
  - Handler: Initialize audit log, send welcome email, setup interest schedule
- `FundTransferInitiatedEvent` — Transfer between accounts starts
  - Handler: Hold funds, create audit record
- `FundTransferCompletedEvent` — Transfer successfully processed
  - Handler: Send receipt to both customers, update balances, log
- `FundTransferFailedEvent` — Transfer failed
  - Handler: Refund held funds, notify customer, log error, alert admin

**Interest & Account Maintenance Events**:
- `InterestPostedEvent` — Monthly interest calculated and posted
  - Handler: Update account balance, send statement, log, track interest income
- `OverdraftDetectedEvent` — Account balance goes negative
  - Handler: Alert admin, apply overdraft fee, send customer alert, log violation
- `AccountClosedEvent` — Account closed
  - Handler: Archive data, notify customer, settle outstanding balances, audit log

### Event Bus Implementation Strategy

**Phase 1 (Immediate) — Synchronous Dispatcher**:
```java
// Simple, testable, no external dependencies
interface EventDispatcher {
  void subscribe(String eventType, EventHandler handler);
  void dispatch(DomainEvent event);
}

// Implementation holds handlers in registry
// dispatch() iterates through handlers and calls handle(event)
// All happens synchronously in same thread
```

**Phase 2 (Future) — Asynchronous with Spring**:
```java
// If adopting Spring Framework
// Use @EventListener on handler methods
// Spring ApplicationEventPublisher for publishing
// Async handlers with @Async annotation
```

**Phase 3 (Enterprise) — Message Queue**:
```java
// If scaling to multi-branch or cloud
// Use RabbitMQ, Kafka, or Azure Service Bus
// Events persisted to queue, handlers consume asynchronously
// Natural event sourcing foundation
```

### Why Event-Driven is Suitable

| Benefit | How It Supports Your System |
|---------|--------------------------|
| **Decoupling** | Services emit events; handlers listen. No direct coupling between services and consumers |
| **Async Operations** | Notifications, logging, audit trails happen without blocking main workflow |
| **Extensibility** | Add new event handler (e.g., SMS notifier) without modifying existing services — just subscribe to event |
| **Scalability Foundation** | Start synchronous; upgrade to message queue without changing service code |
| **Compliance/Audit** | Natural audit trail through events; can persist all events for regulatory compliance |
| **Future Integrations** | Easy to add external systems (payment gateway, email service, reporting tool) as event handlers |
| **Testability** | Mock event bus to verify services emit correct events; test handlers independently |

---

## 3. HOW LAYERED & EVENT-DRIVEN COMPLEMENT EACH OTHER

### Structural vs. Behavioral

| Concern | Layered Architecture | Event-Driven Architecture |
|---------|----------------------|--------------------------|
| **Organizes** | Code into vertical tiers by responsibility | Interactions across tiers horizontally through events |
| **Dependencies** | Strict hierarchical flow (down only) | Cross-layer decoupling through event bus |
| **Example** | Presentation → Service → Domain → Persistence | Service emits event → independent handlers react (in parallel conceptually) |

### Synergy in Practice

**Scenario: Customer applies for loan**

1. **Layered Structure** (organizational clarity):
   - UI (Presentation) captures form data
   - UI calls `LoanApplicationService` (Service layer)
   - Service validates with `LoanApprovalPolicy` (Domain layer)
   - Service saves to `LoanRepository` (Persistence layer)

2. **Event-Driven Behavior** (decoupled reactions):
   - Service publishes `LoanApplicationSubmittedEvent`
   - Event bus routes to handlers:
     - `AdminNotificationHandler` → sends notification to admin dashboard
     - `AuditLogHandler` → logs compliance record to database
     - `EmailHandler` → sends confirmation email to customer (independently, non-blocking)
   - Service returns immediately; handlers execute async

3. **Result**:
   - Service logic remains focused (single responsibility)
   - New notification requirements don't modify service code
   - Adding SMS handler later is just subscribing to same event
   - UI doesn't need to know about notifications, auditing, or email

### Extension Example

**Scenario: System needs SMS notifications**

Without Event-Driven:
- Modify `LoanApplicationService` to add SMS call
- Modify every place that creates loans
- Risk breaking existing functionality

With Event-Driven:
- Create new `SMSNotificationHandler`
- Subscribe to `LoanApplicationSubmittedEvent`
- Redeploy handler; no changes to services

---

## 4. ARCHITECTURE COMPONENTS

### New Packages to Create

#### `com.bms.service`
- `LoanApplicationService` — Loan application workflow
- `FundTransferService` — Transfer between accounts
- `InterestPostingService` — Monthly interest calculation and posting
- `AccountManagementService` — Create/update/close accounts
- `CustomerAuthenticationService` — Login and role routing
- `base.ApplicationService` — Base class for all services (common patterns)

#### `com.bms.event`
- `DomainEvent` — Abstract base class for all events
  - Properties: `eventId`, `timestamp`, `aggregateId`, `version`
- Concrete event classes:
  - `LoanApplicationSubmittedEvent`
  - `LoanApprovedEvent`
  - `FundTransferCompletedEvent`
  - `InterestPostedEvent`
  - `OverdraftDetectedEvent`
  - (etc. for all business events)

#### `com.bms.event.bus`
- `EventDispatcher` interface — Publish/subscribe contract
- `SynchronousEventDispatcher` — Synchronous in-process implementation
- `EventHandler<T extends DomainEvent>` — Handler interface

#### `com.bms.event.handler`
- `LoanApprovalNotificationHandler` — Notify admin of new applications
- `LoanStatusUpdateHandler` — Update UI/database on loan status change
- `AuditLogEventHandler` — Log all events for compliance
- `OverdraftAlertHandler` — Alert admin of overdraft situations
- `TransactionConfirmationHandler` — Send receipts and confirmations
- (etc. for all event handling logic)

### Refactor Existing Packages

#### `com.bms.view`
- **No changes to structure**, but...
- Replace direct domain controller calls with service calls
- Inject services instead of domain controllers

#### `com.bms.domain`
- Move complex workflow logic **out** to services
- Keep pure business logic **in** domain (entities, policies, rules)
- Entities focus on state and validation, not orchestration

#### `com.bms.persistence`
- Formalize as **Repository Pattern**
- One repository per aggregate: `CustomerRepository`, `AccountRepository`, `LoanRepository`, `TransactionRepository`
- Repositories already exist; just formalize and document

---

## 5. DESIGN DECISIONS & JUSTIFICATIONS

### Why NOT Microservices?
- **System Context**: Single-instance desktop application for branch operations
- **Current Scale**: Hundreds of customers max, not millions
- **Deployment**: Desktop executable, not distributed cloud infrastructure
- **Microservices Overhead**: Separate services, API contracts, deployment complexity unwarranted for current scope
- **Decision**: Layered + Event-Driven sufficient. Event architecture provides foundation if future scaling requires decomposition

### Why Service Layer Between Presentation & Domain?
- **Domain Purity**: Keeps domain layer framework-free and highly testable
- **Use-Case Orchestration**: Multi-step workflows (loan processing, transfers) coordinated at service level, not scattered
- **Single Responsibility**: Service focuses on orchestration; domain focuses on rules
- **Event Emission**: Services are natural place to emit events (after persistence success)

### Why Synchronous Event Bus (Phase 1)?
- **Simplicity**: No external dependencies (RabbitMQ, Kafka); fits educational scope
- **Testability**: Synchronous = predictable; easy to verify event emission in tests
- **Gradualism**: Start synchronous; upgrade path to async if needed
- **Sufficient**: For desktop app, thread-blocking not a concern (single user per session)
- **Evolution**: Code structure unchanged when migrating to async bus later

### Why Domain Events vs. Message Events?
- **Purity**: Domain events are business concepts (LoanApproved); message events are infrastructure (MQ message)
- **Language**: Developers think in domain language; easier to find events and handlers
- **Mapping**: Events map naturally to business workflows; reduces mental translation
- **Strategy**: Emit domain events; bus handles transport (sync now, async later)

---

## 6. IMPLEMENTATION ROADMAP

### Phase 1: Establish Service Layer & Event Infrastructure
1. Create `com.bms.service` package with application services
2. Create `com.bms.event` package with base `DomainEvent` class
3. Create `com.bms.event.bus` with `SynchronousEventDispatcher`
4. Refactor domain controllers → move logic to services
5. Services emit events after successful persistence

### Phase 2: Implement Event Handlers
1. Create `com.bms.event.handler` package
2. Implement handlers for critical events (notifications, audit, alerts)
3. Register handlers with event bus at application startup
4. Test event flow end-to-end

### Phase 3: Documentation & Validation
1. Create `docs/architecture.md` with this plan
2. Create `docs/event-catalog.md` listing all events and handlers
3. Add architecture diagrams (Mermaid) to docs
4. Write architecture decision records (ADR) for key choices
5. Verify no circular dependencies; confirm layer isolation

### Phase 4: Testing & Quality
1. Unit test services with mock repositories (persistence abstracted)
2. Unit test domain logic in isolation (no framework dependencies)
3. Integration test event flow (service → event → handler)
4. Verify extensibility by adding new event handler without modifying services

---

## 7. VERIFICATION CHECKLIST

- [ ] **Dependency Flow**: No upward dependencies (presentation never calls persistence directly)
- [ ] **Domain Purity**: Domain layer imports no persistence, UI, or event bus classes
- [ ] **Service Orchestration**: Services coordinate use-cases; domain validates rules
- [ ] **Event Emission**: Every service method that changes state emits an event
- [ ] **Handler Independence**: Handlers don't call each other; only subscribe to events
- [ ] **Async-Ready**: Event bus abstraction allows future swap to async implementation
- [ ] **Testing**: Services testable with mock repositories; handlers testable in isolation
- [ ] **Documentation**: Architecture decisions recorded; event catalog maintained

---

## 8. FURTHER CONSIDERATIONS

### Error Handling in Event-Driven Workflows
- **Challenge**: If a handler fails (e.g., email service down), should the main operation succeed?
- **Strategy**: 
  - Log handler failures; don't fail main operation
  - Implement retry mechanism for critical handlers
  - Consider dead-letter queue for failed events (future)

### Event Versioning
- **Consideration**: If event schema changes, old handlers may break
- **Strategy**: 
  - Version events (`LoanApplicationSubmittedEventV1`, `V2`, etc.)
  - Add versioning to `DomainEvent` base class
  - Maintain backward compatibility or explicit migration

### Event Persistence & Sourcing
- **Future**: Consider event sourcing (persist all events, reconstruct state from events)
- **Current**: Start with transient events; add persistence if audit requirements grow
- **Tool**: Events are already semi-logged through audit handler; full event sourcing is optional

### Cross-Service Communication (Multi-Service Future)
- **Current**: Single service layer, local event bus
- **Future**: If decomposing into microservices, use saga pattern for distributed transactions
- **Foundation**: Event-driven structure is already saga-ready

---

## Summary

**Layered Architecture** provides structural clarity—each tier has a defined role, dependencies flow downward, and layers are independently testable. The **Service Layer** addition is the key refinement, isolating domain logic from orchestration.

**Event-Driven Architecture** adds behavioral flexibility—workflows emit events, handlers react independently, and new functionality integrates without modifying existing code.

Together, they deliver:
- ✅ **Extensibility**: Add features without restructuring
- ✅ **Decoupling**: Services, handlers, and layers are loosely coupled
- ✅ **Testability**: Each layer and handler testable in isolation
- ✅ **Maintainability**: Clear responsibilities and data flow
- ✅ **Scalability Foundation**: Ready to evolve to async, multi-branch, or cloud deployment

The architecture is **production-ready for a desktop banking application** and provides a solid foundation for future growth.
