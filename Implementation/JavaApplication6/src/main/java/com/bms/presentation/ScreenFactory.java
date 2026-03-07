package com.bms.presentation;

/**
 * ScreenFactory - Abstract Factory interface for creating presentation screens.
 * Decouples the main application from concrete screen implementations,
 * enabling different UI toolkits or screen variants (e.g., JavaFX, Swing, kiosk
 * mode).
 */
public interface ScreenFactory {

    // Core screens (created once at startup)
    LoginScreen createLoginScreen();

    AccountSelectionScreen createAccountSelectionScreen();

    AccountBalanceScreen createAccountBalanceScreen();

    TransactionHistoryScreen createTransactionHistoryScreen();

    AdminDashboard createAdminDashboard();

    // On-demand admin screens
    CreateCustomerProfileForm createCustomerProfileForm();

    DepositCashForm createDepositForm();

    WithdrawCashForm createWithdrawForm();

    UpdateAccountStatusForm createAccountStatusForm();

    LoanReviewForm createLoanReviewForm();

    MonthlyInterestJob createInterestJobScreen();

    OverdraftAlertView createOverdraftAlertView();

    // On-demand customer screens
    TransferFundsForm createTransferForm();

    ApplyForLoanForm createLoanApplicationForm();

    LoanStatusView createLoanStatusView();
}
