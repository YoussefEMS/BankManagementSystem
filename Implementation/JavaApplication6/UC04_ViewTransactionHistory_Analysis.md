# UC-04 View Transaction History — Design Artifacts

## Deliverable 1 — First-Cut Design Class Diagram (textual)
```
«boundary» TransactionHistoryScreen
  - root : VBox
  - historyController : TransactionHistoryController
  - authController : AuthenticationController
  - accountNumberCombo : ComboBox<String>
  - startDatePicker : DatePicker
  - endDatePicker : DatePicker
  - typeFilterCombo : ComboBox<String>
  - searchButton : Button
  - transactionTable : TableView<String[]>
  - noResultLabel : Label
  + refreshAccounts()
  + getRoot() : VBox
  + handleSearch()
  + clearFilters()

«controller» TransactionHistoryController
  - transactionDAO : TransactionDAO
  + viewTransactionHistory(accountNo: String, startDate: LocalDate, endDate: LocalDate, typeFilter: String) : List<Transaction>
  + getTransactionHistoryAsStrings(accountNo: String, startDate: LocalDate, endDate: LocalDate, typeFilter: String) : String[][]

«controller» AuthenticationController   // used to populate account selector
  - customerDAO : CustomerDAO
  - accountDAO : AccountDAO
  - authContext : AuthContext
  + getLoggedInCustomerAccountNumbers() : String[]
  + getLoggedInCustomerId() : int
  + getLoggedInCustomer() : Customer
  + isLoggedIn() : boolean

«entity» Transaction
  - transactionId : int
  - accountNumber : String
  - type : String
  - amount : BigDecimal
  - timestamp : LocalDateTime
  - performedBy : String
  - note : String
  - balanceAfter : BigDecimal
  - referenceCode : String

«persistence» TransactionDAO
  - dataSource : DataSource
  + findByAccountNo(accountNumber: String, startDateTime: LocalDateTime, endDateTime: LocalDateTime, typeFilter: String) : List<Transaction>

«persistence» AccountDAO   // used indirectly via AuthenticationController
  - dataSource : DataSource
  + findByCustomerId(customerId: int) : List<Account>
  + findByAccountNo(accountNumber: String) : Account

«infrastructure» DataSourceFactory
  - dataSource : DataSource
  - PROPERTIES_FILE : String = "application.properties"
  + getDataSource() : DataSource
```

Associations / dependencies (directional):
- TransactionHistoryScreen → TransactionHistoryController (uses for history retrieval)
- TransactionHistoryScreen → AuthenticationController (uses for account list)
- TransactionHistoryController → TransactionDAO (data access)
- AuthenticationController → AccountDAO (fetch accounts for logged-in customer)
- AuthenticationController → AuthContext (session state)
- TransactionDAO → DataSourceFactory → DataSource (connection pool)
- AccountDAO → DataSourceFactory → DataSource
- TransactionDAO ↔ Transaction (row mapping)

## Deliverable 2 — CRC Cards

**TransactionHistoryScreen (boundary)**  
Responsibilities:  
- Capture filters (account, date range, type) and trigger search  
- Render transaction results table and “no results” messaging  
- Populate account list for the logged-in customer and clear filters  
Collaborators:  
- TransactionHistoryController  
- AuthenticationController  

**TransactionHistoryController (controller)**  
Responsibilities:  
- Validate account input and normalize filters  
- Retrieve transactions for account/date/type and return list  
- Format transactions as string rows for presentation  
Collaborators:  
- TransactionDAO  
- Transaction  

**AuthenticationController (controller/support)**  
Responsibilities:  
- Provide logged-in customer account numbers to UI  
- Expose login state/info for presentation needs  
Collaborators:  
- AccountDAO  
- AuthContext  
- CustomerDAO  

**Transaction (entity)**  
Responsibilities:  
- Hold transaction data (id, account, type, amount, timestamp, note, balanceAfter, referenceCode)  
Collaborators:  
- TransactionDAO  
- TransactionHistoryController (read-only)  

**TransactionDAO (persistence)**  
Responsibilities:  
- Build and execute filtered transaction queries by account/date/type  
- Map result rows to Transaction entities  
Collaborators:  
- DataSourceFactory/DataSource  
- Transaction  

**AccountDAO (persistence, supportive)**  
Responsibilities:  
- Fetch accounts for a customer (to supply account numbers)  
- Fetch account by account number (not primary in this UC but present in collaborator)  
Collaborators:  
- DataSourceFactory/DataSource  
- Account  

**DataSourceFactory (infrastructure)**  
Responsibilities:  
- Load DB properties and provide pooled DataSource  
Collaborators:  
- TransactionDAO  
- AccountDAO  

## Deliverable 3 — Refined Design Class Diagram
```
«boundary» TransactionHistoryScreen
  - historyController : TransactionHistoryController
  - authController : AuthenticationController
  - accountNumberCombo : ComboBox<String>
  - startDatePicker : DatePicker
  - endDatePicker : DatePicker
  - typeFilterCombo : ComboBox<String>
  - transactionTable : TableView<String[]>
  - noResultLabel : Label
  + refreshAccounts()
  + handleSearch()
  + clearFilters()
  + getRoot() : VBox

«controller» TransactionHistoryController
  - transactionDAO : TransactionDAO
  + viewTransactionHistory(accountNo: String, startDate: LocalDate, endDate: LocalDate, typeFilter: String) : List<Transaction>
  + getTransactionHistoryAsStrings(accountNo: String, startDate: LocalDate, endDate: LocalDate, typeFilter: String) : String[][]

«controller» AuthenticationController   // limited to account list for this UC
  - accountDAO : AccountDAO
  - authContext : AuthContext
  + getLoggedInCustomerAccountNumbers() : String[]
  + isLoggedIn() : boolean

«entity» Transaction
  - transactionId : int
  - accountNumber : String
  - type : String
  - amount : BigDecimal
  - timestamp : LocalDateTime
  - performedBy : String
  - note : String
  - balanceAfter : BigDecimal
  - referenceCode : String

«persistence» TransactionDAO
  - dataSource : DataSource
  + findByAccountNo(accountNumber: String, startDateTime: LocalDateTime, endDateTime: LocalDateTime, typeFilter: String) : List<Transaction>

«persistence» AccountDAO   // supporting account list
  - dataSource : DataSource
  + findByCustomerId(customerId: int) : List<Account>

«infrastructure» DataSourceFactory
  - dataSource : DataSource
  - PROPERTIES_FILE : String
  + getDataSource() : DataSource
```

Refined dependencies:
- TransactionHistoryScreen → TransactionHistoryController
- TransactionHistoryScreen → AuthenticationController
- AuthenticationController → AccountDAO, AuthContext
- TransactionHistoryController → TransactionDAO
- TransactionDAO → DataSourceFactory → DataSource
- AccountDAO → DataSourceFactory → DataSource
- TransactionDAO ↔ Transaction
