package com.bms.mediator.fundtransfer.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bms.mediator.fundtransfer.FundTransferContext;
import com.bms.mediator.fundtransfer.IFundTransferMediator;
import com.bms.mediator.fundtransfer.peers.IAccountStatusHandlerPeer;
import com.bms.mediator.fundtransfer.peers.IBalanceValidatorPeer;
import com.bms.mediator.fundtransfer.peers.IDestinationAccountValidatorPeer;
import com.bms.mediator.fundtransfer.peers.IOverdraftHandlerPeer;
import com.bms.mediator.fundtransfer.peers.ISourceAccountValidatorPeer;
import com.bms.mediator.fundtransfer.peers.ITransactionRecorderPeer;

public class FundTransferMediatorImpl implements IFundTransferMediator {
    private static final Logger logger = Logger.getLogger(FundTransferMediatorImpl.class.getName());

    // Peer references with dependency inversion
    private ISourceAccountValidatorPeer sourceAccountValidator;
    private IDestinationAccountValidatorPeer destinationAccountValidator;
    private IAccountStatusHandlerPeer accountStatusHandler;
    private IOverdraftHandlerPeer overdraftHandler;
    private ITransactionRecorderPeer transactionRecorder;
    private IBalanceValidatorPeer balanceValidator;

    // Dynamic peer registry
    private final Map<String, Object> peerRegistry = new HashMap<>();

    /**
     * Constructor with dependency injection
     */
    public FundTransferMediatorImpl(
            ISourceAccountValidatorPeer sourceAccountValidator,
            IDestinationAccountValidatorPeer destinationAccountValidator,
            IAccountStatusHandlerPeer accountStatusHandler,
            IOverdraftHandlerPeer overdraftHandler,
            ITransactionRecorderPeer transactionRecorder,
            IBalanceValidatorPeer balanceValidator) {
        this.sourceAccountValidator = sourceAccountValidator;
        this.destinationAccountValidator = destinationAccountValidator;
        this.accountStatusHandler = accountStatusHandler;
        this.overdraftHandler = overdraftHandler;
        this.transactionRecorder = transactionRecorder;
        this.balanceValidator = balanceValidator;
    }

    /**
     * Main orchestration method for fund transfer
     * 
     * Validation Sequence:
     * 1. Validate transfer amount
     * 2. Validate source account exists and has funds
     * 3. Validate destination account exists
     * 4. Check both accounts' status
     * 5. Check overdraft implications
     * 6. Create transactions if all validations pass
     */
    @Override
    public FundTransferContext transferFunds(String sourceAccountNo, String destinationAccountNo, 
                                            int customerId, double transferAmount) {
        FundTransferContext context = new FundTransferContext(
                sourceAccountNo, destinationAccountNo, customerId, transferAmount);

        try {
            logger.log(Level.INFO, "Starting fund transfer: {0} -> {1}, Amount: {2}",
                    new Object[]{sourceAccountNo, destinationAccountNo, transferAmount});

            // Generate transfer reference
            String transferRef = generateTransferReference();
            context.setTransferReference(transferRef);

            // VALIDATION SEQUENCE (orchestrated by mediator)
            
            // Step 1: Validate transfer amount
            if (!validateTransferAmount(context)) {
                context.setTransferSuccessful(false);
                return context;
            }

            // Step 2: Validate source account
            if (!validateSourceAccount(context)) {
                context.setTransferSuccessful(false);
                return context;
            }

            // Step 3: Validate destination account
            if (!validateDestinationAccount(context)) {
                context.setTransferSuccessful(false);
                return context;
            }

            // Step 4: Check account status
            if (!checkAccountStatus(context)) {
                context.setTransferSuccessful(false);
                return context;
            }

            // Step 5: Check overdraft implications
            if (!checkOverdraftImpact(context)) {
                context.setTransferSuccessful(false);
                return context;
            }

            // EXECUTION PHASE (all validations passed)
            
            // Step 6: Record transactions
            if (!executeTransfer(context)) {
                context.setTransferSuccessful(false);
                return context;
            }

            context.setTransferSuccessful(true);
            logger.log(Level.INFO, "Fund transfer completed successfully. Reference: {0}", transferRef);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error during fund transfer", e);
            context.setTransferSuccessful(false);
            context.setFailureReason("System error: " + e.getMessage());
        }

        return context;
    }

    /**
     * Step 1: Validate transfer amount
     */
    private boolean validateTransferAmount(FundTransferContext context) {
        logger.info("Step 1: Validating transfer amount");

        if (!balanceValidator.validateTransferAmount(context.getTransferAmount(), null)) {
            context.setFailureReason("Invalid transfer amount: " + context.getTransferAmount());
            logger.warning(context.getFailureReason());
            return false;
        }

        return true;
    }

    /**
     * Step 2: Validate source account
     */
    private boolean validateSourceAccount(FundTransferContext context) {
        logger.info("Step 2: Validating source account");

        if (!sourceAccountValidator.validateSourceAccountExists(
                context.getSourceAccountNo(), null)) {
            context.setFailureReason("Source account does not exist: " + context.getSourceAccountNo());
            logger.warning(context.getFailureReason());
            return false;
        }

        if (!sourceAccountValidator.validateSufficientFunds(
                context.getSourceAccountNo(), context.getTransferAmount(), null)) {
            // Check if overdraft can cover the deficit
            double balance = sourceAccountValidator.getSourceAccountBalance(context.getSourceAccountNo());
            double deficit = context.getTransferAmount() - balance;
            
            if (!overdraftHandler.isWithinOverdraftLimit(
                    context.getSourceAccountNo(), deficit, balance, null)) {
                context.setFailureReason("Insufficient funds and overdraft limit would be exceeded");
                logger.warning(context.getFailureReason());
                return false;
            }
        }

        return true;
    }

    /**
     * Step 3: Validate destination account
     */
    private boolean validateDestinationAccount(FundTransferContext context) {
        logger.info("Step 3: Validating destination account");

        if (!destinationAccountValidator.validateDestinationAccountExists(
                context.getDestinationAccountNo(), null)) {
            context.setFailureReason("Destination account does not exist: " + context.getDestinationAccountNo());
            logger.warning(context.getFailureReason());
            return false;
        }

        if (!destinationAccountValidator.validateDestinationCanReceiveFunds(
                context.getDestinationAccountNo(), null)) {
            context.setFailureReason("Destination account cannot receive funds");
            logger.warning(context.getFailureReason());
            return false;
        }

        return true;
    }

    /**
     * Step 4: Check account status
     */
    private boolean checkAccountStatus(FundTransferContext context) {
        logger.info("Step 4: Checking account status");

        if (!accountStatusHandler.validateAccountsStatus(
                context.getSourceAccountNo(), context.getDestinationAccountNo(), null)) {
            context.setFailureReason("One or both accounts have invalid status for transfer");
            logger.warning(context.getFailureReason());
            return false;
        }

        if (accountStatusHandler.hasTransferRestrictions(context.getSourceAccountNo())) {
            context.setFailureReason("Source account has transfer restrictions");
            logger.warning(context.getFailureReason());
            return false;
        }

        return true;
    }

    /**
     * Step 5: Check overdraft impact
     */
    private boolean checkOverdraftImpact(FundTransferContext context) {
        logger.info("Step 5: Checking overdraft impact");

        double sourceBalance = sourceAccountValidator.getSourceAccountBalance(context.getSourceAccountNo());
        double sourceNewBalance = sourceBalance - context.getTransferAmount();

        if (overdraftHandler.willTriggerOverdraft(context.getSourceAccountNo(), sourceNewBalance)) {
            logger.info("Transfer will trigger overdraft - checking limits");
            
            if (!overdraftHandler.isOverdraftAllowed(context.getSourceAccountNo())) {
                context.setFailureReason("Transfer would cause overdraft and account does not have overdraft facility");
                logger.warning(context.getFailureReason());
                return false;
            }
        }

        context.setSourceNewBalance(sourceNewBalance);
        return true;
    }

    /**
     * Step 6: Execute transfer (record transactions)
     */
    private boolean executeTransfer(FundTransferContext context) {
        logger.info("Step 6: Executing transfer");

        try {
            // Record debit on source account
            int debitTxnId = transactionRecorder.recordDebitTransaction(
                    context.getSourceAccountNo(),
                    context.getTransferAmount(),
                    context.getTransferReference(),
                    null);

            if (debitTxnId <= 0) {
                context.setFailureReason("Failed to record debit transaction");
                return false;
            }

            // Record credit on destination account
            int creditTxnId = transactionRecorder.recordCreditTransaction(
                    context.getDestinationAccountNo(),
                    context.getTransferAmount(),
                    context.getTransferReference(),
                    null);

            if (creditTxnId <= 0) {
                context.setFailureReason("Failed to record credit transaction");
                return false;
            }

            // Link transactions as a pair
            if (!transactionRecorder.linkTransactionPair(debitTxnId, creditTxnId)) {
                context.setFailureReason("Failed to link transaction pair");
                return false;
            }

            context.setTransactionId(debitTxnId);
            context.setDestinationNewBalance(
                    sourceAccountValidator.getSourceAccountBalance(context.getDestinationAccountNo()) +
                    context.getTransferAmount());

            logger.info("Transfer executed successfully. Debit Txn: " + debitTxnId + 
                       ", Credit Txn: " + creditTxnId);

            return true;
        } catch (Exception e) {
            context.setFailureReason("Error executing transfer: " + e.getMessage());
            logger.log(Level.WARNING, context.getFailureReason(), e);
            return false;
        }
    }

    /**
     * Generates unique transfer reference code
     */
    private String generateTransferReference() {
        return "TXN-" + System.currentTimeMillis() + "-" + 
               UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    @Override
    public void registerPeer(String peerName, Object peer) {
        peerRegistry.put(peerName, peer);
        logger.log(Level.INFO, "Peer registered: {0}", peerName);
    }

    @Override
    public void unregisterPeer(String peerName) {
        peerRegistry.remove(peerName);
        logger.log(Level.INFO, "Peer unregistered: {0}", peerName);
    }
}
