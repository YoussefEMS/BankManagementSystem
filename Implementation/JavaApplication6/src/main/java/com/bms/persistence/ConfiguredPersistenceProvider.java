package com.bms.persistence;

import com.bms.persistence.DatabaseConnectionProvider;
import com.bms.persistence.DatabaseConnectionProviderSelector;

/**
 * Chooses the DAO factory that matches the configured database adapter.
 */
public final class ConfiguredPersistenceProvider implements PersistenceProvider {

    private static final ConfiguredPersistenceProvider INSTANCE = new ConfiguredPersistenceProvider();

    private final PersistenceProvider delegate;
    private final DatabaseConnectionProvider databaseConnectionProvider;

    private ConfiguredPersistenceProvider() {
        this.databaseConnectionProvider = DataSourceProvider.getInstance().getDatabaseConnectionProvider();
        this.delegate = resolveProvider(databaseConnectionProvider);
    }

    public static ConfiguredPersistenceProvider getInstance() {
        return INSTANCE;
    }

    private PersistenceProvider resolveProvider(DatabaseConnectionProvider adapter) {
        DatabaseConnectionProviderSelector.DatabaseType type =
                DatabaseConnectionProviderSelector.DatabaseType.valueOf(adapter.getDatabaseName().toUpperCase());

        switch (type) {
            case SQLSERVER:
                return SqlServerPersistenceProvider.getInstance();
            case MYSQL:
            case ORACLE:
            case POSTGRESQL:
                throw new UnsupportedOperationException(
                        "Configured database adapter " + adapter.getDatabaseName()
                                + " is available, but concrete DAOs are currently implemented only for SQL Server.");
            default:
                throw new IllegalStateException("Unsupported DAO factory for " + adapter.getDatabaseName());
        }
    }

    @Override
    public AccountDAO createAccountDAO() {
        return delegate.createAccountDAO();
    }

    @Override
    public CustomerDAO createCustomerDAO() {
        return delegate.createCustomerDAO();
    }

    @Override
    public TransactionDAO createTransactionDAO() {
        return delegate.createTransactionDAO();
    }

    @Override
    public TransferDAO createTransferDAO() {
        return delegate.createTransferDAO();
    }

    @Override
    public LoanDAO createLoanDAO() {
        return delegate.createLoanDAO();
    }

    @Override
    public InterestPostingDAO createInterestPostingDAO() {
        return delegate.createInterestPostingDAO();
    }

    @Override
    public OverdraftEventDAO createOverdraftEventDAO() {
        return delegate.createOverdraftEventDAO();
    }

    @Override
    public DatabaseConnectionProvider getDatabaseConnectionProvider() {
        return databaseConnectionProvider;
    }
}
