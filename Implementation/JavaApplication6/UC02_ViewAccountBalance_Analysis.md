# UC-02 View Account Balance — Analysis

## First-Cut Class Diagram (textual)
```
Boundary
  AccountBalanceScreen
    - accountNumberCombo : ComboBox<String>
    - viewButton : Button
    - statusLabel, balanceLabel, currencyLabel : Label
    - balanceController : AccountBalanceController
    - authController : AuthenticationController

Controller
  AccountBalanceController
    - accountDAO : AccountDAO

  AuthenticationController (only to populate account list)
    - authContext : AuthContext
    - customerDAO : CustomerDAO

Entity
  Account
    - accountNumber : String
    - customerId : int
    - accountType : String
    - balance : BigDecimal
    - currency : String
    - status : String
    - dateOpened : LocalDateTime

Persistence
  AccountDAO
    - dataSource : DataSource
    + findByAccountNo(accountNo) : Account
    + findByCustomerId(customerId) : List<Account>

  DataSourceFactory (singleton/static)
    - dataSource : DataSource
    - PROPERTIES_FILE = "application.properties"
    + getDataSource() : DataSource
```

Associations:
- AccountBalanceScreen → AccountBalanceController (uses)
- AccountBalanceScreen → AuthenticationController (populate account combo)
- AccountBalanceController → AccountDAO (uses)
- AuthenticationController → CustomerDAO, AuthContext (uses)
- AccountDAO → DataSourceFactory → DataSource (uses)
- AccountDAO ↔ Account (maps rows to entities)

## CRC Cards

- **AccountBalanceScreen** (Boundary)  
  Responsibilities: capture selected account; invoke balance lookup; render details or no-result message.  
  Collaborators: AccountBalanceController, AuthenticationController.

- **AccountBalanceController** (Controller)  
  Responsibilities: validate account number; retrieve Account; format details for UI.  
  Collaborators: AccountDAO, Account.

- **AuthenticationController** (Controller/support)  
  Responsibilities (for this UC): provide logged-in customer account numbers.  
  Collaborators: AuthContext, CustomerDAO, AccountDAO.

- **Account** (Entity)  
  Responsibilities: hold account state (number, balance, currency, status, type, customerId, dateOpened).  
  Collaborators: AccountDAO, AccountBalanceController, AccountBalanceScreen (read-only).

- **AccountDAO** (Persistence)  
  Responsibilities: query account by account number; map ResultSet → Account; optionally update balance.  
  Collaborators: DataSourceFactory/DataSource, Account.

- **DataSourceFactory** (Persistence infra)  
  Responsibilities: load `application.properties`; configure and provide pooled DataSource.  
  Collaborators: AccountDAO, AuthenticationController/CustomerDAO (indirect).

## Updated Class Diagram (refined)
```
[AccountBalanceScreen] «boundary»
  + handleViewAccount()
  + displayAccount(...)

    uses
        |
        v
[AccountBalanceController] «controller»
  - accountDAO : AccountDAO
  + viewAccountSummary(accountNo)
  + getAccountDetails(accountNo)

    uses                          uses
        |                             |
        v                             v
 [AccountDAO] «persistence»     [AuthenticationController] «controller»
  - dataSource : DataSource       - authContext : AuthContext
  + findByAccountNo(...)          - customerDAO : CustomerDAO
                                  + getLoggedInCustomerAccountNumbers()

        | maps to
        v
    [Account] «entity»
    - accountNumber : String
    - balance : BigDecimal
    - currency : String
    - status : String
    - accountType : String
    - customerId : int
    - dateOpened : LocalDateTime

[DataSourceFactory] «persistence infra»
  + getDataSource()
  (provides DataSource to AccountDAO/CustomerDAO)
```
