package com.bms.persistence;

import com.bms.persistence.DatabaseConnectionProvider;

/**
 * PersistenceProvider - Abstract Factory interface for creating Data Access Objects.
 * Decouples the controller layer from concrete DAO implementations,
 * enabling different persistence backends (e.g., SQL Server, in-memory for
 * testing).
 */
public interface PersistenceProvider {

    AccountDAO createAccountDAO();

    CustomerDAO createCustomerDAO();

    TransactionDAO createTransactionDAO();

    TransferDAO createTransferDAO();

    LoanDAO createLoanDAO();

    InterestPostingDAO createInterestPostingDAO();

    OverdraftEventDAO createOverdraftEventDAO();

    DatabaseConnectionProvider getDatabaseConnectionProvider();
}
