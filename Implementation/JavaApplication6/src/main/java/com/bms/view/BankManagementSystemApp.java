package com.bms.view;

import com.bms.persistence.AuthContext;
import com.bms.view.AccountBalanceScreen;
import com.bms.view.AccountSelectionScreen;
import com.bms.view.AdminDashboard;
import com.bms.view.ApplyForLoanForm;
import com.bms.view.CreateCustomerProfileForm;
import com.bms.view.DepositCashForm;
import com.bms.view.JavaFxScreenProvider;
import com.bms.view.LoanCatalogComparisonView;
import com.bms.view.LoanReviewForm;
import com.bms.view.LoanStatusView;
import com.bms.view.LoginScreen;
import com.bms.view.MonthlyInterestJob;
import com.bms.view.OverdraftAlertView;
import com.bms.view.ScreenProvider;
import com.bms.view.TransactionHistoryScreen;
import com.bms.view.TransferFundsForm;
import com.bms.view.UpdateAccountStatusForm;
import com.bms.view.WithdrawCashForm;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * BankManagementSystemApp - Main JavaFX application entry point
 * Manages authentication flow and scene navigation
 */
public class BankManagementSystemApp extends Application {
    private BorderPane root;
    private final ScreenProvider screenProvider;
    private LoginScreen loginScreen;
    private AccountSelectionScreen accountSelectionScreen;
    private AccountBalanceScreen accountBalanceScreen;
    private TransactionHistoryScreen transactionHistoryScreen;
    private AdminDashboard adminDashboard;
    private Stage primaryStage;

    public BankManagementSystemApp() {
        this.screenProvider = new JavaFxScreenProvider();
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;

            // Initialize core screens using Abstract Factory
            loginScreen = screenProvider.createLoginScreen();
            accountSelectionScreen = screenProvider.createAccountSelectionScreen();
            accountBalanceScreen = screenProvider.createAccountBalanceScreen();
            transactionHistoryScreen = screenProvider.createTransactionHistoryScreen();
            adminDashboard = screenProvider.createAdminDashboard();

            // Setup navigation callbacks
            setupNavigationCallbacks();

            // Create root layout
            root = new BorderPane();

            // Show login screen first
            showLoginScreen();

            // Create main scene
            Scene scene = new Scene(root, 1000, 700);

            // Set stage
            primaryStage.setTitle("Bank Management System");
            primaryStage.setScene(scene);
            primaryStage.show();

            // Handle stage close event
            primaryStage.setOnCloseRequest(event -> onApplicationClose());

        } catch (Exception e) {
            e.printStackTrace();
            showError("Initialization Error", "Failed to start application: " + e.getMessage());
        }
    }

    /**
     * Setup navigation callbacks between screens
     */
    private void setupNavigationCallbacks() {
        // Login success -> role-based routing
        loginScreen.setOnLoginSuccess(() -> {
            if (AuthContext.getInstance().isAdmin()) {
                showAdminDashboard();
            } else {
                showAccountSelectionScreen();
            }
        });

        // Account selection -> go to account balance
        accountSelectionScreen.setOnAccountSelected(this::showAccountBalanceScreen);

        // Logout from any screen -> back to login
        accountSelectionScreen.setOnLogout(this::showLoginScreen);

        // --- Admin Dashboard callbacks ---
        adminDashboard.setOnCreateCustomer(() -> {
            CreateCustomerProfileForm form = screenProvider.createCustomerProfileForm();
            form.setOnBack(this::showAdminDashboard);
            root.setCenter(form.getRoot());
        });

        adminDashboard.setOnDeposit(() -> {
            DepositCashForm form = screenProvider.createDepositForm();
            form.setOnBack(this::showAdminDashboard);
            root.setCenter(form.getRoot());
        });

        adminDashboard.setOnWithdraw(() -> {
            WithdrawCashForm form = screenProvider.createWithdrawForm();
            form.setOnBack(this::showAdminDashboard);
            root.setCenter(form.getRoot());
        });

        adminDashboard.setOnUpdateAccountStatus(() -> {
            UpdateAccountStatusForm form = screenProvider.createAccountStatusForm();
            form.setOnBack(this::showAdminDashboard);
            root.setCenter(form.getRoot());
        });

        adminDashboard.setOnReviewLoans(() -> {
            LoanReviewForm form = screenProvider.createLoanReviewForm();
            form.setOnBack(this::showAdminDashboard);
            root.setCenter(form.getRoot());
        });

        adminDashboard.setOnPostInterest(() -> {
            MonthlyInterestJob form = screenProvider.createInterestJobScreen();
            form.setOnBack(this::showAdminDashboard);
            root.setCenter(form.getRoot());
        });

        adminDashboard.setOnViewOverdrafts(() -> {
            OverdraftAlertView form = screenProvider.createOverdraftAlertView();
            form.setOnBack(this::showAdminDashboard);
            root.setCenter(form.getRoot());
        });

        adminDashboard.setOnViewBalance(() -> {
            accountBalanceScreen.refreshAccounts();
            root.setCenter(accountBalanceScreen.getRoot());
            root.setTop(createMenuBar());
        });

        adminDashboard.setOnViewTransactionHistory(() -> {
            transactionHistoryScreen.refreshAccounts();
            root.setCenter(transactionHistoryScreen.getRoot());
            root.setTop(createMenuBar());
        });
    }

    /**
     * Show login screen
     */
    private void showLoginScreen() {
        root.setCenter(loginScreen.getRoot());
        root.setTop(null);
        root.setBottom(null);
    }

    /**
     * Show admin dashboard
     */
    private void showAdminDashboard() {
        adminDashboard.refresh();
        root.setCenter(adminDashboard.getRoot());
        root.setTop(createAdminMenuBar());
    }

    /**
     * Show account selection screen (customer flow)
     */
    private void showAccountSelectionScreen() {
        accountSelectionScreen.refreshAccounts();
        root.setCenter(accountSelectionScreen.getRoot());
        root.setTop(null);
        root.setBottom(null);
    }

    /**
     * Show account balance screen
     */
    private void showAccountBalanceScreen() {
        accountBalanceScreen.refreshAccounts();
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);
        root.setCenter(accountBalanceScreen.getRoot());
    }

    /**
     * Show transaction history screen
     */
    private void showTransactionHistoryScreen() {
        transactionHistoryScreen.refreshAccounts();
        MenuBar menuBar = createMenuBar();
        root.setTop(menuBar);
        root.setCenter(transactionHistoryScreen.getRoot());
    }

    /**
     * Reset all screens to clean state
     */
    private void resetAll() {
        loginScreen = screenProvider.createLoginScreen();
        accountSelectionScreen = screenProvider.createAccountSelectionScreen();
        accountBalanceScreen = screenProvider.createAccountBalanceScreen();
        transactionHistoryScreen = screenProvider.createTransactionHistoryScreen();
        adminDashboard = screenProvider.createAdminDashboard();
        setupNavigationCallbacks();
    }

    /**
     * Create menu bar for customer screens
     */
    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // Navigation menu
        Menu navMenu = new Menu("Navigation");

        MenuItem accountsItem = new MenuItem("View Accounts");
        accountsItem.setOnAction(event -> showAccountSelectionScreen());

        MenuItem balanceItem = new MenuItem("Account Balance");
        balanceItem.setOnAction(event -> showAccountBalanceScreen());

        MenuItem historyItem = new MenuItem("Transaction History");
        historyItem.setOnAction(event -> showTransactionHistoryScreen());

        navMenu.getItems().addAll(accountsItem, balanceItem, historyItem);

        // Account menu
        Menu accountMenu = new Menu("Account");

        MenuItem transferItem = new MenuItem("Transfer Funds");
        transferItem.setOnAction(event -> {
            TransferFundsForm form = screenProvider.createTransferForm();
            form.setOnBack(this::showAccountSelectionScreen);
            root.setCenter(form.getRoot());
        });

        MenuItem loanStatusItem = new MenuItem("My Loans");
        loanStatusItem.setOnAction(event -> {
            LoanStatusView view = screenProvider.createLoanStatusView();
            view.setOnBack(this::showAccountSelectionScreen);
            root.setCenter(view.getRoot());
        });

        MenuItem loanCatalogItem = new MenuItem("Loan Catalog");
        loanCatalogItem.setOnAction(event -> {
            LoanCatalogComparisonView view = screenProvider.createLoanCatalogComparisonView();
            view.setOnBack(this::showAccountSelectionScreen);
            root.setCenter(view.getRoot());
        });

        MenuItem applyLoanItem = new MenuItem("Apply for Loan");
        applyLoanItem.setOnAction(event -> {
            ApplyForLoanForm form = screenProvider.createLoanApplicationForm();
            form.setOnBack(this::showAccountSelectionScreen);
            root.setCenter(form.getRoot());
        });

        accountMenu.getItems().addAll(transferItem, loanStatusItem, loanCatalogItem, applyLoanItem);

        // User menu
        Menu userMenu = new Menu("User");

        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(event -> {
            AuthContext.getInstance().logout();
            resetAll();
            showLoginScreen();
        });

        userMenu.getItems().add(logoutItem);

        menuBar.getMenus().addAll(navMenu, accountMenu, userMenu);
        return menuBar;
    }

    /**
     * Create menu bar for admin screens
     */
    private MenuBar createAdminMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu navMenu = new Menu("Navigation");

        MenuItem dashboardItem = new MenuItem("Dashboard");
        dashboardItem.setOnAction(event -> showAdminDashboard());

        navMenu.getItems().add(dashboardItem);

        // User menu
        Menu userMenu = new Menu("User");

        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(event -> {
            AuthContext.getInstance().logout();
            resetAll();
            showLoginScreen();
        });

        userMenu.getItems().add(logoutItem);

        menuBar.getMenus().addAll(navMenu, userMenu);
        return menuBar;
    }

    /**
     * Show error dialog
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Handle application close
     */
    private void onApplicationClose() {
        AuthContext.getInstance().logout();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
