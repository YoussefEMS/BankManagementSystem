package com.bms.persistence;

/**
 * SqlServerDAOFactory - Concrete Abstract Factory for SQL Server persistence.
 * Creates DAO instances that use SQL Server via the HikariCP connection pool
 * managed by DataSourceFactory.
 *
 * Implemented as a Singleton so that controllers can default to it
 * without requiring an explicit factory parameter.
 */
public class SqlServerDAOFactory implements DAOFactory {

    private static final SqlServerDAOFactory INSTANCE = new SqlServerDAOFactory();

    private SqlServerDAOFactory() {
        // Private constructor - use getInstance()
    }

    public static SqlServerDAOFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public AccountDAO createAccountDAO() {
        return new AccountDAO();
    }

    @Override
    public CustomerDAO createCustomerDAO() {
        return new CustomerDAO();
    }

    @Override
    public TransactionDAO createTransactionDAO() {
        return new TransactionDAO();
    }

    @Override
    public TransferDAO createTransferDAO() {
        return new TransferDAO();
    }

    @Override
    public LoanDAO createLoanDAO() {
        return new LoanDAO();
    }

    @Override
    public InterestPostingDAO createInterestPostingDAO() {
        return new InterestPostingDAO();
    }

    @Override
    public OverdraftEventDAO createOverdraftEventDAO() {
        return new OverdraftEventDAO();
    }
}
