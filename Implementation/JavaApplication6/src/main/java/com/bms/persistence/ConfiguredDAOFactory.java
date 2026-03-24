package com.bms.persistence;

import com.bms.persistence.adapter.DatabaseAdapter;
import com.bms.persistence.adapter.DatabaseAdapterFactory;

/**
 * Chooses the DAO factory that matches the configured database adapter.
 */
public final class ConfiguredDAOFactory implements DAOFactory {

    private static final ConfiguredDAOFactory INSTANCE = new ConfiguredDAOFactory();

    private final DAOFactory delegate;
    private final DatabaseAdapter databaseAdapter;

    private ConfiguredDAOFactory() {
        this.databaseAdapter = DataSourceFactory.getInstance().getDatabaseAdapter();
        this.delegate = resolveFactory(databaseAdapter);
    }

    public static ConfiguredDAOFactory getInstance() {
        return INSTANCE;
    }

    private DAOFactory resolveFactory(DatabaseAdapter adapter) {
        DatabaseAdapterFactory.DatabaseType type =
                DatabaseAdapterFactory.DatabaseType.valueOf(adapter.getDatabaseName().toUpperCase());

        switch (type) {
            case SQLSERVER:
                return SqlServerDAOFactory.getInstance();
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
    public DatabaseAdapter getDatabaseAdapter() {
        return databaseAdapter;
    }
}
