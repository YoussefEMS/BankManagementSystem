package com.bms.presentation;

/**
 * JavaFXScreenFactory - Concrete Abstract Factory for JavaFX screens.
 * Creates the current JavaFX implementation of each presentation screen.
 */
public class JavaFXScreenFactory implements ScreenFactory {

    @Override
    public LoginScreen createLoginScreen() {
        return new LoginScreen();
    }

    @Override
    public AccountSelectionScreen createAccountSelectionScreen() {
        return new AccountSelectionScreen();
    }

    @Override
    public AccountBalanceScreen createAccountBalanceScreen() {
        return new AccountBalanceScreen();
    }

    @Override
    public TransactionHistoryScreen createTransactionHistoryScreen() {
        return new TransactionHistoryScreen();
    }

    @Override
    public AdminDashboard createAdminDashboard() {
        return new AdminDashboard();
    }

    @Override
    public CreateCustomerProfileForm createCustomerProfileForm() {
        return new CreateCustomerProfileForm();
    }

    @Override
    public DepositCashForm createDepositForm() {
        return new DepositCashForm();
    }

    @Override
    public WithdrawCashForm createWithdrawForm() {
        return new WithdrawCashForm();
    }

    @Override
    public UpdateAccountStatusForm createAccountStatusForm() {
        return new UpdateAccountStatusForm();
    }

    @Override
    public LoanReviewForm createLoanReviewForm() {
        return new LoanReviewForm();
    }

    @Override
    public MonthlyInterestJob createInterestJobScreen() {
        return new MonthlyInterestJob();
    }

    @Override
    public OverdraftAlertView createOverdraftAlertView() {
        return new OverdraftAlertView();
    }

    @Override
    public TransferFundsForm createTransferForm() {
        return new TransferFundsForm();
    }

    @Override
    public LoanCatalogComparisonView createLoanCatalogComparisonView() {
        return new LoanCatalogComparisonView();
    }

    @Override
    public ApplyForLoanForm createLoanApplicationForm() {
        return new ApplyForLoanForm();
    }

    @Override
    public LoanStatusView createLoanStatusView() {
        return new LoanStatusView();
    }
}
