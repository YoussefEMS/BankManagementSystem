package com.bms.service;

import java.math.BigDecimal;

public class CustomerOnboardingWorkflowService {
    private final AccountManagementService accountManagementService;
    private final LoanApplicationService loanApplicationService;

    public record CustomerInput(String fullName, String email, String nationalId,
            String password, String phone, String address) {
    }

    public record AccountInput(String accountType, BigDecimal initialDeposit, String currency) {
    }

    public record LoanInput(BigDecimal amount, Integer termMonths, String loanPurpose, String loanType) {
        public boolean isPresent() {
            return amount != null || termMonths != null || loanPurpose != null || loanType != null;
        }
    }

    public static class OnboardingResult {
        private final boolean success;
        private final String code;
        private final String message;
        private final int customerId;
        private final String accountNumber;
        private final Integer loanId;
        private final String workflowStatus;

        public OnboardingResult(boolean success, String code, String message,
                int customerId, String accountNumber, Integer loanId, String workflowStatus) {
            this.success = success;
            this.code = code;
            this.message = message;
            this.customerId = customerId;
            this.accountNumber = accountNumber;
            this.loanId = loanId;
            this.workflowStatus = workflowStatus;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }

        public int getCustomerId() {
            return customerId;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public Integer getLoanId() {
            return loanId;
        }

        public String getWorkflowStatus() {
            return workflowStatus;
        }
    }

    public CustomerOnboardingWorkflowService() {
        this(new AccountManagementService(), new LoanApplicationService());
    }

    public CustomerOnboardingWorkflowService(AccountManagementService accountManagementService,
            LoanApplicationService loanApplicationService) {
        this.accountManagementService = accountManagementService;
        this.loanApplicationService = loanApplicationService;
    }

    public OnboardingResult onboard(CustomerInput customer, AccountInput account, LoanInput loan) {
        if (customer == null || account == null) {
            return failed("VALIDATION_ERROR", "Customer and account data are required");
        }

        AccountManagementService.CustomerAccountResult accountResult =
                accountManagementService.createCustomerProfile(
                        customer.fullName(),
                        customer.email(),
                        customer.phone(),
                        customer.address(),
                        customer.nationalId(),
                        customer.password(),
                        account.accountType(),
                        account.initialDeposit(),
                        account.currency());

        if (!accountResult.isSuccess()) {
            return failed(accountResult.getCode(), accountResult.getMessage());
        }

        Integer loanId = null;
        if (loan != null && loan.isPresent()) {
            LoanApplicationService.LoanResult loanResult =
                    loanApplicationService.submitLoanApplication(
                            accountResult.getCustomerId(),
                            loan.amount(),
                            loan.loanType(),
                            loan.termMonths() != null ? loan.termMonths() : 0,
                            loan.loanPurpose());

            if (loanResult.getLoanId() <= 0) {
                return new OnboardingResult(false, "LOAN_APPLICATION_FAILED",
                        "Customer and account were created, but starter loan submission failed",
                        accountResult.getCustomerId(), accountResult.getAccountNumber(), null,
                        "PARTIALLY_COMPLETED");
            }
            loanId = loanResult.getLoanId();
        }

        return new OnboardingResult(true, "COMPLETED", "Customer onboarding workflow completed",
                accountResult.getCustomerId(), accountResult.getAccountNumber(), loanId, "COMPLETED");
    }

    private static OnboardingResult failed(String code, String message) {
        return new OnboardingResult(false, code, message, -1, null, null, "FAILED");
    }
}
