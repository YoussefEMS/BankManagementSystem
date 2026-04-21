package com.bms.domain.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.bms.domain.entity.Account;
import com.bms.domain.entity.InterestPosting;
import com.bms.domain.entity.Transaction;
import com.bms.domain.entity.TransactionRecordBuilder;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.PersistenceProvider;
import com.bms.persistence.InterestPostingDAO;
import com.bms.persistence.TransactionDAO;

/**
 * Bridge abstraction for posting account interest.
 * The posting workflow is decoupled from the interest calculation algorithm.
 */
public abstract class InterestPostingProcessor {
    protected final InterestCalculator calculator;
    protected final AccountDAO accountDAO;
    protected final TransactionDAO transactionDAO;
    protected final InterestPostingDAO interestPostingDAO;

    protected InterestPostingProcessor(InterestCalculator calculator, PersistenceProvider factory) {
        this.calculator = calculator;
        this.accountDAO = factory.createAccountDAO();
        this.transactionDAO = factory.createTransactionDAO();
        this.interestPostingDAO = factory.createInterestPostingDAO();
    }

    public int postInterest() {
        int processed = 0;
        List<Account> accounts = accountDAO.findAllByAccountType(getAccountType());
        for (Account account : accounts) {
            if (processInterest(account)) {
                processed++;
            }
        }
        return processed;
    }

    protected abstract String getAccountType();

    protected boolean processInterest(Account account) {
        BigDecimal balance = account.getBalance();
        if (balance.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        BigDecimal interest = calculator.calculate(balance);
        double rateUsed = calculator.getRate();
        BigDecimal newBalance = balance.add(interest);

        accountDAO.updateBalance(account.getAccountNumber(), newBalance);

        InterestPosting posting = new InterestPosting();
        posting.setAccountNumber(account.getAccountNumber());
        posting.setAmount(interest.doubleValue());
        posting.setRateUsed(rateUsed);
        posting.setTimestamp(LocalDateTime.now());
        interestPostingDAO.insert(posting);

        Transaction tx = TransactionRecordBuilder.createInterestPosting(
                account.getAccountNumber(), interest, newBalance, rateUsed);
        transactionDAO.insert(tx);

        return true;
    }
}
