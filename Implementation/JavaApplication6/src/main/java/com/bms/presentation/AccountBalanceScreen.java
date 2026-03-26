package com.bms.presentation;

import com.bms.domain.controller.AccountBalanceController;
import com.bms.domain.controller.AuthenticationController;
import com.bms.domain.decorator.accountinfo.AccountInfoView;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * AccountBalanceScreen - UC-02 View Account Balance
 * Displays account balance and details
 * If account not found, shows blank/empty output (no error dialogs)
 */
public class AccountBalanceScreen {
    private VBox root;
    private AccountBalanceController balanceController;
    private AuthenticationController authController;

    // UI Components
    private ComboBox<String> accountNumberCombo;
    private Button viewButton;
    private Label statusLabel;
    private Label balanceLabel;
    private Label currencyLabel;
    private Label accountNumberDisplayLabel;
    private Label rewardPointsLabel;
    private Label warningLabel;
    private Label noResultLabel;

    public AccountBalanceScreen() {
        this.balanceController = new AccountBalanceController();
        this.authController = new AuthenticationController();
        this.root = createUI();
        loadUserAccounts();
    }

    /**
     * Create the UI for account balance view
     */
    private VBox createUI() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 12;");

        // Title
        Label titleLabel = new Label("View Account Balance (UC-02)");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Input section
        VBox inputSection = createInputSection();

        // Results section
        VBox resultsSection = createResultsSection();

        container.getChildren().addAll(
            titleLabel,
            new Separator(),
            inputSection,
            new Separator(),
            resultsSection
        );

        return container;
    }

    /**
     * Create input section with account number dropdown and view button
     */
    private VBox createInputSection() {
        VBox section = new VBox(10);

        Label label = new Label("Select Account:");
        label.setStyle("-fx-font-weight: bold;");

        HBox inputBox = new HBox(10);
        inputBox.setAlignment(Pos.CENTER_LEFT);

        accountNumberCombo = new ComboBox<>();
        accountNumberCombo.setPromptText("Select an account");
        accountNumberCombo.setPrefWidth(300);
        accountNumberCombo.setStyle("-fx-font-size: 12;");

        viewButton = new Button("View");
        viewButton.setPrefWidth(100);
        viewButton.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        viewButton.setOnAction(e -> handleViewAccount());

        // Allow Enter key to trigger search
        accountNumberCombo.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                handleViewAccount();
            }
        });

        inputBox.getChildren().addAll(accountNumberCombo, viewButton);
        section.getChildren().addAll(label, inputBox);

        return section;
    }

    /**
     * Load user's accounts into the combo box
     */
    private void loadUserAccounts() {
        String[] accountNumbers = authController.getLoggedInCustomerAccountNumbers();
        accountNumberCombo.setItems(FXCollections.observableArrayList(accountNumbers));
    }

    /**
     * Refresh the accounts list (call this when screen becomes visible after login)
     */
    public void refreshAccounts() {
        loadUserAccounts();
    }

    /**
     * Create results display section
     */
    private VBox createResultsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10, 0, 0, 0));

        Label resultsLabel = new Label("Account Details:");
        resultsLabel.setStyle("-fx-font-weight: bold;");

        // No results label (initially hidden)
        noResultLabel = new Label("No account found. Please enter a valid account number.");
        noResultLabel.setStyle("-fx-text-fill: #666666; -fx-font-style: italic;");
        noResultLabel.setVisible(false);

        // Account details grid
        VBox detailsBox = new VBox(8);
        detailsBox.setPadding(new Insets(10));
        detailsBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");

        // Account Number
        HBox accountNoBox = new HBox(20);
        Label accountNoLabelText = new Label("Account Number:");
        accountNoLabelText.setPrefWidth(150);
        accountNoLabelText.setStyle("-fx-font-weight: bold;");
        accountNumberDisplayLabel = new Label("");
        accountNoBox.getChildren().addAll(accountNoLabelText, accountNumberDisplayLabel);

        // Status
        HBox statusBox = new HBox(20);
        Label statusLabelText = new Label("Status:");
        statusLabelText.setPrefWidth(150);
        statusLabelText.setStyle("-fx-font-weight: bold;");
        statusLabel = new Label("");
        statusBox.getChildren().addAll(statusLabelText, statusLabel);

        // Balance
        HBox balanceBox = new HBox(20);
        Label balanceLabelText = new Label("Balance:");
        balanceLabelText.setPrefWidth(150);
        balanceLabelText.setStyle("-fx-font-weight: bold;");
        balanceLabel = new Label("");
        balanceLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: #2196F3;");
        balanceBox.getChildren().addAll(balanceLabelText, balanceLabel);

        // Currency
        HBox currencyBox = new HBox(20);
        Label currencyLabelText = new Label("Currency:");
        currencyLabelText.setPrefWidth(150);
        currencyLabelText.setStyle("-fx-font-weight: bold;");
        currencyLabel = new Label("");
        currencyBox.getChildren().addAll(currencyLabelText, currencyLabel);

        // Reward points
        HBox rewardPointsBox = new HBox(20);
        Label rewardPointsLabelText = new Label("Reward Points:");
        rewardPointsLabelText.setPrefWidth(150);
        rewardPointsLabelText.setStyle("-fx-font-weight: bold;");
        rewardPointsLabel = new Label("");
        rewardPointsBox.getChildren().addAll(rewardPointsLabelText, rewardPointsLabel);

        warningLabel = new Label("");
        warningLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        warningLabel.setWrapText(true);

        detailsBox.getChildren().addAll(
            accountNoBox,
            statusBox,
            balanceBox,
            currencyBox,
            rewardPointsBox,
            warningLabel
        );

        section.getChildren().addAll(resultsLabel, noResultLabel, detailsBox);
        return section;
    }

    /**
     * Handle view account button click
     */
    private void handleViewAccount() {
        String accountNo = accountNumberCombo.getValue();

        if (accountNo == null || accountNo.trim().isEmpty()) {
            clearResults();
            noResultLabel.setText("Please select an account.");
            noResultLabel.setVisible(true);
            return;
        }

        try {
            // Call controller to get account details as strings
            AccountInfoView accountDetails = balanceController.getAccountDetails(accountNo);

            if (accountDetails != null) {
                displayAccount(accountDetails);
                noResultLabel.setVisible(false);
            } else {
                // No account found - show blank/empty output
                clearResults();
                noResultLabel.setText("No account found. Please select a valid account.");
                noResultLabel.setVisible(true);
            }
        } catch (Exception e) {
            clearResults();
            noResultLabel.setText("Error retrieving account. Please try again.");
            noResultLabel.setVisible(true);
            e.printStackTrace();
        }
    }

    /**
     * Display account details on the screen
     */
    private void displayAccount(AccountInfoView accountInfoView) {
        accountNumberDisplayLabel.setText(accountInfoView.getAccountNumber());
        statusLabel.setText(accountInfoView.getStatus());
        balanceLabel.setText(accountInfoView.getBalance());
        currencyLabel.setText(accountInfoView.getCurrency());
        rewardPointsLabel.setText(accountInfoView.getRewardPoints());
        warningLabel.setText(accountInfoView.getWarningMessage());
    }

    /**
     * Clear all result displays
     */
    private void clearResults() {
        accountNumberDisplayLabel.setText("");
        statusLabel.setText("");
        balanceLabel.setText("");
        currencyLabel.setText("");
        rewardPointsLabel.setText("");
        warningLabel.setText("");
    }

    /**
     * Get the root VBox for this screen
     */
    public VBox getRoot() {
        return root;
    }
}
