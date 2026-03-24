error id: file:///C:/University/Year%20three%20-%20semester%20two/SWE/BankManagementSystem/Implementation/JavaApplication6/src/main/java/com/bms/domain/controller/DepositHandler.java:_empty_/SqlServerDAOFactory#
file:///C:/University/Year%20three%20-%20semester%20two/SWE/BankManagementSystem/Implementation/JavaApplication6/src/main/java/com/bms/domain/controller/DepositHandler.java
empty definition using pc, found symbol in pc: _empty_/SqlServerDAOFactory#
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 720
uri: file:///C:/University/Year%20three%20-%20semester%20two/SWE/BankManagementSystem/Implementation/JavaApplication6/src/main/java/com/bms/domain/controller/DepositHandler.java
text:
```scala
package com.bms.domain.controller;

import java.math.BigDecimal;

import com.bms.domain.entity.Account;
import com.bms.domain.entity.Transaction;
import com.bms.domain.entity.TransactionFactory;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.AuthContext;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.SqlServerDAOFactory;
import com.bms.persistence.TransactionDAO;

/**
 * DepositHandler - UC-05: Process Deposit
 * Validates amount, looks up account, updates balance, records transaction
 */
public class DepositHandler {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;

    public DepositHandler() {
        this(Sq@@lServerDAOFactory.getInstance());
    }

    public DepositHandler(DAOFactory factory) {
        this.accountDAO = factory.createAccountDAO();
        this.transactionDAO = factory.createTransactionDAO();
    }

    /**
     * Post a deposit to an account
     * 
     * @return the transaction ID (> 0 on success), 0 or negative on failure
     */
    public int postDeposit(String accountNo, double amount, String description) {
        // Validate amount
        if (!validateAmount(amount)) {
            return -1;
        }

        // Look up account
        Account account = getAccount(accountNo);
        if (account == null) {
            return -2;
        }

        if (!"ACTIVE".equals(account.getStatus())) {
            return -3; // Account not active
        }

        // Compute new balance
        BigDecimal depositAmount = BigDecimal.valueOf(amount);
        BigDecimal newBalance = account.getBalance().add(depositAmount);

        // Update balance in DB
        accountDAO.updateBalance(accountNo, newBalance);

        // Record the transaction using Factory Method
        String performedBy = AuthContext.getInstance().isLoggedIn()
                ? AuthContext.getInstance().getLoggedInCustomer().getFullName()
                : "System";

        Transaction tx = TransactionFactory.createDeposit(
                accountNo, depositAmount, newBalance, performedBy, description);

        return transactionDAO.insert(tx);
    }

    private boolean validateAmount(double amount) {
        return amount > 0;
    }

    private Account getAccount(String accountNo) {
        if (accountNo == null || accountNo.trim().isEmpty())
            return null;
        return accountDAO.findByAccountNo(accountNo.trim());
    }
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: _empty_/SqlServerDAOFactory#