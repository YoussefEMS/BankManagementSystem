package com.bms.domain.controller;

import com.bms.domain.entity.TransactionContext;
import com.bms.domain.controller.TransactionProcessor;
import com.bms.persistence.AccountDAO;
import com.bms.persistence.PersistenceProvider;
import com.bms.persistence.TransactionDAO;

/**
 * AbstractTransactionProcessor - Template Method Pattern for financial transactions.
 * Orchestrates the standardized transaction lifecycle and delegates specific
 * checks and specific impacts to concrete subclasses.
 *
 * @param <T> The return type of the transaction execution (e.g., Integer for Tx ID, String for Reference Code).
 */
public abstract class AbstractTransactionProcessor<T> {
    protected final AccountDAO accountDAO;
    protected final TransactionDAO transactionDAO;
    protected final TransactionProcessor transactionProcessor;

    protected AbstractTransactionProcessor(PersistenceProvider factory, TransactionProcessor transactionProcessor) {
        this.accountDAO = factory.createAccountDAO();
        this.transactionDAO = factory.createTransactionDAO();
        this.transactionProcessor = transactionProcessor;
    }

    /**
     * The generalized Template Method outlining the transaction sequence.
     * This orchestrates multiple verification and operation steps that are firmly 
     * dictated by the abstract class logic.
     */
    public final T executeTransaction(TransactionContext context, String description) {
        return transactionProcessor.process(context, currentContext -> {
            
            // 1) General base checks of requested numbers
            if (!validateInputs(currentContext)) {
                return getFailureResult(1); 
            }

            // 2) Data-layer existence and state checks
            if (!validateAccountsAndState(currentContext)) {
                return getFailureResult(2);
            }

            // 3) Additional unique validation logic per transaction type
            if (!checkSpecificBusinessRules(currentContext)) {
                return getFailureResult(3);
            }

            // 4) Mathematical impact and persistence
            return executeFinancialImpact(currentContext, description);
        });
    }

    /**
     * Step 1: Validate input fields like amounts
     */
    protected abstract boolean validateInputs(TransactionContext context);

    /**
     * Step 2: Ensure account objects exist and are properly authorized (e.g. status ACTIVE)
     */
    protected abstract boolean validateAccountsAndState(TransactionContext context);

    /**
     * Step 3: Ensure complex state rules (e.g., sufficient balance or external gateways)
     */
    protected abstract boolean checkSpecificBusinessRules(TransactionContext context);

    /**
     * Step 4: Perform the update and insert commands into the Data Access Layer
     */
    protected abstract T executeFinancialImpact(TransactionContext context, String description);

    /**
     * Utility: Determine what to return upon a given step failure.
     */
    protected abstract T getFailureResult(int failureStep);
}
