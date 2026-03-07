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

/**
 * MonthlyInterestHandler - UC-12: Post Monthly Interest
 * Iterates through eligible accounts, calculates interest, updates balances
 */
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

    /**
     * Post monthly interest to all eligible accounts
     * 
     * @return the number of accounts that received interest
     */
    public int postMonthlyInterest() {
        int count = 0;

        // Process Savings accounts
        List<Account> savingsAccounts = accountDAO.findAllByAccountType("Savings");
        for (Account account : savingsAccounts) {
            if (processInterest(account)) {
                count++;
            }
        }

        // Process Money Market accounts
        List<Account> moneyMarketAccounts = accountDAO.findAllByAccountType("Money Market");
        for (Account account : moneyMarketAccounts) {
            if (processInterest(account)) {
                count++;
            }
        }

        return count;
    }

    private boolean processInterest(Account account) {
        double monthlyRate = getMonthlyRate(account.getAccountType());
        if (monthlyRate <= 0)
            return false;

        BigDecimal balance = account.getBalance();
        if (balance.compareTo(BigDecimal.ZERO) <= 0)
            return false;

        // Calculate interest
        BigDecimal interest = balance.multiply(BigDecimal.valueOf(monthlyRate));
        interest = interest.setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal newBalance = balance.add(interest);

        // Update account balance
        accountDAO.updateBalance(account.getAccountNumber(), newBalance);

        // Record interest posting
        InterestPosting posting = new InterestPosting();
        posting.setAccountNumber(account.getAccountNumber());
        posting.setAmount(interest.doubleValue());
        posting.setRateUsed(monthlyRate);
        posting.setTimestamp(LocalDateTime.now());
        interestPostingDAO.insert(posting);

        // Record transaction using Factory Method
        Transaction tx = TransactionFactory.createInterestPosting(
                account.getAccountNumber(), interest, newBalance, monthlyRate);
        transactionDAO.insert(tx);

        return true;
    }

    /**
     * Get monthly interest rate by account type
     * Returns the rate as a decimal (e.g. 0.005 for 0.5% monthly)
     */
    private double getMonthlyRate(String accountType) {
        if (accountType == null)
            return 0.0;
        return switch (accountType) {
            case "Savings" -> 0.005; // 0.5% monthly (~6% annual)
            case "Money Market" -> 0.0075; // 0.75% monthly (~9% annual)
            default -> 0.0;
        };
    }
}