package com.bms;

import com.bms.persistence.AuthContext;
import com.bms.presentation.AccountBalanceScreen;
import com.bms.presentation.AccountSelectionScreen;
import com.bms.presentation.LoginScreen;
import com.bms.presentation.TransactionHistoryScreen;

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
    private LoginScreen loginScreen;
    private AccountSelectionScreen accountSelectionScreen;
    private AccountBalanceScreen accountBalanceScreen;
    private TransactionHistoryScreen transactionHistoryScreen;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        try {
            this.primaryStage = primaryStage;

            // Initialize screens
            loginScreen = new LoginScreen();
            accountSelectionScreen = new AccountSelectionScreen();
            accountBalanceScreen = new AccountBalanceScreen();
            transactionHistoryScreen = new TransactionHistoryScreen();

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
        // Login success -> go to account selection
        loginScreen.setOnLoginSuccess(this::showAccountSelectionScreen);

        // Account selection -> go to account balance
        accountSelectionScreen.setOnAccountSelected(this::showAccountBalanceScreen);

        // Logout from any screen -> back to login
        accountSelectionScreen.setOnLogout(this::showLoginScreen);
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
     * Show account selection screen
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
        // Create menu bar
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
        loginScreen = new LoginScreen();
        accountSelectionScreen = new AccountSelectionScreen();
        accountBalanceScreen = new AccountBalanceScreen();
        transactionHistoryScreen = new TransactionHistoryScreen();
        setupNavigationCallbacks();
    }

    /**
     * Create menu bar with navigation options
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

        MenuItem depositItem = new MenuItem("Deposit");
        depositItem.setOnAction(event -> showAlert("Deposit", "Deposit feature"));

        MenuItem withdrawItem = new MenuItem("Withdraw");
        withdrawItem.setOnAction(event -> showAlert("Withdraw", "Withdraw feature"));

        MenuItem transferItem = new MenuItem("Transfer");
        transferItem.setOnAction(event -> showAlert("Transfer", "Transfer feature"));

        accountMenu.getItems().addAll(depositItem, withdrawItem, transferItem);

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
     * Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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
