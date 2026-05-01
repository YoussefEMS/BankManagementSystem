package com.bms.view;

import com.bms.service.CustomerAuthenticationService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * AdminDashboard - Landing screen for ADMIN role users (tellers/managers)
 * Provides navigation to admin-only operations
 */
public class AdminDashboard {
    private final VBox root;
    private final CustomerAuthenticationService authController;

    // Navigation callbacks
    private Runnable onCreateCustomer;
    private Runnable onDeposit;
    private Runnable onWithdraw;
    private Runnable onUpdateAccountStatus;
    private Runnable onReviewLoans;
    private Runnable onPostInterest;
    private Runnable onViewOverdrafts;
    private Runnable onViewBalance;
    private Runnable onViewTransactionHistory;

    public AdminDashboard() {
        this.authController = new CustomerAuthenticationService();
        this.root = createLayout();
    }

    private VBox createLayout() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);

        // Welcome header
        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

        Label welcomeLabel = new Label("Welcome, " + authController.getLoggedInCustomerName());
        welcomeLabel.setFont(Font.font("System", 16));

        Label roleLabel = new Label("Role: Administrator / Teller");
        roleLabel.setFont(Font.font("System", 14));
        roleLabel.setStyle("-fx-text-fill: #666;");

        // Operations grid
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        // --- Customer Management ---
        Label customerHeader = new Label("Customer Management");
        customerHeader.setFont(Font.font("System", FontWeight.BOLD, 14));
        grid.add(customerHeader, 0, 0, 2, 1);

        Button createCustomerBtn = createNavButton("Create Customer Profile",
                "Register a new customer in the system");
        createCustomerBtn.setOnAction(e -> {
            if (onCreateCustomer != null)
                onCreateCustomer.run();
        });
        grid.add(createCustomerBtn, 0, 1);

        // --- Teller Operations ---
        Label tellerHeader = new Label("Teller Operations");
        tellerHeader.setFont(Font.font("System", FontWeight.BOLD, 14));
        grid.add(tellerHeader, 0, 2, 2, 1);

        Button depositBtn = createNavButton("Process Deposit",
                "Deposit cash into a customer's account");
        depositBtn.setOnAction(e -> {
            if (onDeposit != null)
                onDeposit.run();
        });
        grid.add(depositBtn, 0, 3);

        Button withdrawBtn = createNavButton("Process Withdrawal",
                "Withdraw cash from a customer's account");
        withdrawBtn.setOnAction(e -> {
            if (onWithdraw != null)
                onWithdraw.run();
        });
        grid.add(withdrawBtn, 1, 3);

        // --- Account Management ---
        Label accountHeader = new Label("Account Management");
        accountHeader.setFont(Font.font("System", FontWeight.BOLD, 14));
        grid.add(accountHeader, 0, 4, 2, 1);

        Button viewBalanceBtn = createNavButton("View Account Balance",
                "Look up any account's balance");
        viewBalanceBtn.setOnAction(e -> {
            if (onViewBalance != null)
                onViewBalance.run();
        });
        grid.add(viewBalanceBtn, 0, 5);

        Button viewHistoryBtn = createNavButton("Transaction History",
                "View transaction history for any account");
        viewHistoryBtn.setOnAction(e -> {
            if (onViewTransactionHistory != null)
                onViewTransactionHistory.run();
        });
        grid.add(viewHistoryBtn, 1, 5);

        Button updateStatusBtn = createNavButton("Update Account Status",
                "Freeze, activate, or close an account");
        updateStatusBtn.setOnAction(e -> {
            if (onUpdateAccountStatus != null)
                onUpdateAccountStatus.run();
        });
        grid.add(updateStatusBtn, 0, 6);

        // --- Loan Management ---
        Label loanHeader = new Label("Loan Management");
        loanHeader.setFont(Font.font("System", FontWeight.BOLD, 14));
        grid.add(loanHeader, 0, 7, 2, 1);

        Button reviewLoansBtn = createNavButton("Review Loan Applications",
                "Approve or reject pending loan requests");
        reviewLoansBtn.setOnAction(e -> {
            if (onReviewLoans != null)
                onReviewLoans.run();
        });
        grid.add(reviewLoansBtn, 0, 8);

        // --- System Operations ---
        Label systemHeader = new Label("System Operations");
        systemHeader.setFont(Font.font("System", FontWeight.BOLD, 14));
        grid.add(systemHeader, 0, 9, 2, 1);

        Button postInterestBtn = createNavButton("Post Monthly Interest",
                "Run monthly interest posting for savings accounts");
        postInterestBtn.setOnAction(e -> {
            if (onPostInterest != null)
                onPostInterest.run();
        });
        grid.add(postInterestBtn, 0, 10);

        Button overdraftBtn = createNavButton("View Overdraft Alerts",
                "View accounts with overdraft events");
        overdraftBtn.setOnAction(e -> {
            if (onViewOverdrafts != null)
                onViewOverdrafts.run();
        });
        grid.add(overdraftBtn, 1, 10);

        layout.getChildren().addAll(titleLabel, welcomeLabel, roleLabel, grid);
        return layout;
    }

    private Button createNavButton(String title, String tooltip) {
        Button btn = new Button(title);
        btn.setPrefWidth(220);
        btn.setPrefHeight(40);
        btn.setStyle("-fx-font-size: 13px;");
        if (tooltip != null) {
            btn.setTooltip(new javafx.scene.control.Tooltip(tooltip));
        }
        return btn;
    }

    /**
     * Refresh the dashboard (e.g. after navigation back)
     */
    public void refresh() {
        // Rebuild layout to update welcome message
        root.getChildren().clear();
        VBox newLayout = createLayout();
        root.getChildren().addAll(newLayout.getChildren());
    }

    public VBox getRoot() {
        return root;
    }

    // --- Navigation callback setters ---

    public void setOnCreateCustomer(Runnable callback) {
        this.onCreateCustomer = callback;
    }

    public void setOnDeposit(Runnable callback) {
        this.onDeposit = callback;
    }

    public void setOnWithdraw(Runnable callback) {
        this.onWithdraw = callback;
    }

    public void setOnUpdateAccountStatus(Runnable callback) {
        this.onUpdateAccountStatus = callback;
    }

    public void setOnReviewLoans(Runnable callback) {
        this.onReviewLoans = callback;
    }

    public void setOnPostInterest(Runnable callback) {
        this.onPostInterest = callback;
    }

    public void setOnViewOverdrafts(Runnable callback) {
        this.onViewOverdrafts = callback;
    }

    public void setOnViewBalance(Runnable callback) {
        this.onViewBalance = callback;
    }

    public void setOnViewTransactionHistory(Runnable callback) {
        this.onViewTransactionHistory = callback;
    }
}
