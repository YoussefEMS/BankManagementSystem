package com.bms.domain.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.bms.domain.entity.Account;
import com.bms.domain.entity.InterestPosting;
import com.bms.domain.entity.Transaction;
import com.bms.domain.entity.TransactionFactory;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.DAOFactory;
import com.bms.persistence.InterestPostingDAO;
import com.bms.persistence.SqlServerDAOFactory;
import com.bms.persistence.TransactionDAO;

public class MonthlyInterestHandler {
    private final AccountDAO accountDAO;
    private final TransactionDAO transactionDAO;
    private final InterestPostingDAO interestPostingDAO;

    public MonthlyInterestHandler() {
        this(SqlServerDAOFactory.getInstance());
    }

    public MonthlyInterestHandler(DAOFactory factory) {
        this.accountDAO = factory.createAccountDAO();
        this.transactionDAO = factory.createTransactionDAO();
        this.interestPostingDAO = factory.createInterestPostingDAO();
    }

    public int postMonthlyInterest() {
        int count = 0;

        // Abstraction: Processing different groups of accounts
        count += processGroup("Savings", new SavingsInterest());
        count += processGroup("Money Market", new MoneyMarketInterest());

        return count;
    }

    private int processGroup(String type, InterestCalculator calculator) {
        int processed = 0;
        List<Account> accounts = accountDAO.findAllByAccountType(type);
        for (Account account : accounts) {
            // The Bridge: We pass the Implementation (calculator) to the process
            if (processInterest(account, calculator)) {
                processed++;
            }
        }
        return processed;
    }

    private boolean processInterest(Account account, InterestCalculator calculator) {
        BigDecimal balance = account.getBalance();
        
        // Validation logic
        if (balance.compareTo(BigDecimal.ZERO) <= 0) return false;

        // BRIDGE IN ACTION: 
        // We don't use a switch statement here. We let the 'calculator' object 
        // handle the math regardless of what type of interest it is.
        BigDecimal interest = calculator.calculate(balance);
        double rateUsed = calculator.getRate();
        
        BigDecimal newBalance = balance.add(interest);

        // Update account balance
        accountDAO.updateBalance(account.getAccountNumber(), newBalance);

        // Record interest posting
        InterestPosting posting = new InterestPosting();
        posting.setAccountNumber(account.getAccountNumber());
        posting.setAmount(interest.doubleValue());
        posting.setRateUsed(rateUsed);
        posting.setTimestamp(LocalDateTime.now());
        interestPostingDAO.insert(posting);

        // Record transaction
        Transaction tx = TransactionFactory.createInterestPosting(
                account.getAccountNumber(), interest, newBalance, rateUsed);
        transactionDAO.insert(tx);

        return true;
    }
}